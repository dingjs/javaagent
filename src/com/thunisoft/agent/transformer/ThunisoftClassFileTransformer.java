/*
 * @(#)ThunisoftClassFileTransformer.java	2015-7-24 上午09:53:44
 * javaagent
 * Copyright 2015 Thuisoft, Inc. All rights reserved.
 * THUNISOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thunisoft.agent.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import com.thunisoft.agent.ConfigUtils;

/**
 * ThunisoftClassFileTransformer
 * 
 * @author dingjsh
 * @time 2015-7-24上午09:53:44
 */
public class ThunisoftClassFileTransformer implements ClassFileTransformer {

    private static final String LOG_UTILS = "com.thunisoft.agent.log.ExecuteLogUtils";

    private static final String LOG_UTILS_OUTPUT_RUNNABLE = "com.thunisoft.agent.log.OutputLogRunnable";

    private static byte[] logUtilsClassBytes = null;

    private static byte[] logUtilsOutputRunnableClassBytes = null;

    // private Set<ClassLoader> loadedClassLoaders = new HashSet<ClassLoader>();
    private WeakHashMap<ClassLoader, Object> loadedClassLoaders = new WeakHashMap<ClassLoader, Object>();

    private static final Object EXISTS = new Object();

    // private boolean classLoaderInited;

    public static void setLogUtilsClassBytes(byte[] logUtilsClassBytes,
            byte[] runnableBytes) {
        ThunisoftClassFileTransformer.logUtilsClassBytes = logUtilsClassBytes;
        ThunisoftClassFileTransformer.logUtilsOutputRunnableClassBytes = runnableBytes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader
     * , java.lang.String, java.lang.Class, java.security.ProtectionDomain,
     * byte[])
     */
    public byte[] transform(ClassLoader loader, String className,
            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;
        className = className.replace('/', '.');
        if (!isNeedLogExecuteInfo(className)) {
            return byteCode;
        }
        if (null == loader) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        loadLogUtilsClass(loader);
        byteCode = aopLog(className, byteCode);
        return byteCode;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void loadLogUtilsClass(ClassLoader loader) {
        if (!loadedClassLoaders.containsKey(loader)) {
            loadedClassLoaders.put(loader, EXISTS);
            ClassPool cp = ClassPool.getDefault();
            cp.insertClassPath(new LoaderClassPath(loader));
            try {
                Class highestClassLoaderClass = loader.getClass();
                while (null != highestClassLoaderClass.getSuperclass()
                        && !ClassLoader.class.equals(highestClassLoaderClass)) {
                    highestClassLoaderClass = highestClassLoaderClass
                            .getSuperclass();
                }
                highestClassLoaderClass.getDeclaredMethods();
                Method defineClassMethod = highestClassLoaderClass
                        .getDeclaredMethod("defineClass", String.class,
                                byte[].class, int.class, int.class);
                defineClassMethod.setAccessible(true);
                defineClassMethod.invoke(loader, LOG_UTILS, logUtilsClassBytes,
                        0, logUtilsClassBytes.length);

                defineClassMethod.invoke(loader, LOG_UTILS_OUTPUT_RUNNABLE,
                        logUtilsOutputRunnableClassBytes, 0,
                        logUtilsOutputRunnableClassBytes.length);

                Class logUtils = loader.loadClass(LOG_UTILS);
                Method initMethod = logUtils.getDeclaredMethod("init",
                        String.class, int.class,boolean.class);
                initMethod.invoke(logUtils, ConfigUtils.getLogFileName(),
                        ConfigUtils.getLogInterval(),ConfigUtils.isLogAvgExecuteTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] aopLog(String className, byte[] byteCode) {
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(className);
            if (null != cc) {
                if (!cc.isInterface()) {
                    CtMethod[] methods = cc.getDeclaredMethods();
                    if (null != methods && methods.length > 0) {
                        for (CtMethod m : methods) {
                            aopLog(className, m);
                        }
                        byteCode = cc.toBytecode();
                    }
                }
                cc.detach();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return byteCode;
    }

    private void aopLog(String className, CtMethod m)
            throws CannotCompileException {
        if (null == m || m.isEmpty()) {
            return;
        }
        m.addLocalVariable("elapsedTime", CtClass.longType);
        m.insertBefore("elapsedTime = System.currentTimeMillis();");
        m.insertAfter("elapsedTime = System.currentTimeMillis() - elapsedTime;"
                + LOG_UTILS + ".log(\"" + className + "\",\"" + m.getName()
                + "\",System.currentTimeMillis(),(int)elapsedTime" + ");");
    }

    /**
     * 是否需要记录执行信息
     * 
     * @param className
     * @return
     * @author dingjsh
     * @time 2015-7-27下午06:11:02
     */
    private boolean isNeedLogExecuteInfo(String className) {
        boolean isNeeded = false;

        Set<String> includes = ConfigUtils.getIncludePackages();
        if (null != includes && !includes.isEmpty()) {
            // 可用责任链，但是还是算了
            // include package
            for (String packageName : includes) {
                if (className.startsWith(packageName)) {
                    isNeeded = true;
                    break;
                }
            }
            // exclude package
            if (isNeeded) {
                Set<String> excludes = ConfigUtils.getExcludePackages();
                if (null != excludes && !excludes.isEmpty()) {
                    for (String packageName : excludes) {
                        if (className.startsWith(packageName)) {
                            isNeeded = false;
                            break;
                        }
                    }
                }
            }

            if (isNeeded) {
                Set<String> excludeClassRegexs = ConfigUtils
                        .getExcludeClassRegexs();
                if (null != excludeClassRegexs && !excludeClassRegexs.isEmpty()) {
                    for (String regex : excludeClassRegexs) {
                        isNeeded = !Pattern.matches(regex, className);
                        if (!isNeeded) {
                            break;
                        }
                    }
                }
            }
        }
        return isNeeded;
    }
}
