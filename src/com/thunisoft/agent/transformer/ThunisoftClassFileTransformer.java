/*
 * @(#)ThunisoftClassFileTransformer.java 2015-7-24 上午09:53:44 javaagent
 * Copyright 2015 Thuisoft, Inc. All rights reserved. THUNISOFT
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thunisoft.agent.transformer;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import com.thunisoft.agent.javassist.CannotCompileException;
import com.thunisoft.agent.javassist.ClassPool;
import com.thunisoft.agent.javassist.CtClass;
import com.thunisoft.agent.javassist.CtMethod;
import com.thunisoft.agent.javassist.LoaderClassPath;
import com.thunisoft.agent.javassist.NotFoundException;

import com.thunisoft.agent.ConfigUtils;
import com.thunisoft.agent.PojoDetector;

/**
 * ThunisoftClassFileTransformer
 * 
 * @author dingjsh
 * @time 2015-7-24上午09:53:44
 */
public class ThunisoftClassFileTransformer implements ClassFileTransformer {

    private static final String LOG_UTILS = "com.thunisoft.agent.log.ExecuteLogUtils";

    private static final String AGENT_PACKAGE_NAME = "com.thunisoft.agent";

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
        // loadLogUtilsClass(loader);
        byteCode = aopLog(loader, className, byteCode);
        return byteCode;
    }

    private byte[] aopLog(ClassLoader loader, String className, byte[] byteCode) {
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = null;
            try {
                cc = cp.get(className);
            } catch (NotFoundException e) {
                cp.insertClassPath(new LoaderClassPath(loader));
                cc = cp.get(className);
            }
            byteCode = aopLog(cc, className, byteCode);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return byteCode;
    }

    private byte[] aopLog(CtClass cc, String className, byte[] byteCode) throws CannotCompileException, IOException {
        if (null == cc) {
            return byteCode;
        }
        if (!cc.isInterface()) {
            CtMethod[] methods = cc.getDeclaredMethods();
            if (null != methods && methods.length > 0) {
                boolean isOpenPojoMonitor = ConfigUtils.isOpenPojoMonitor();
                Set<String> getSetMethods = Collections.emptySet();
                if (!isOpenPojoMonitor) {
                    getSetMethods = PojoDetector.getPojoMethodNames(methods);
                }
                for (CtMethod m : methods) {
                    if (isOpenPojoMonitor
                            || !getSetMethods.contains(m.getName())) {
                        aopLog(className, m);
                    }
                }
                byteCode = cc.toBytecode();
            }
        }
        cc.detach();
        return byteCode;
    }

    private void aopLog(String className, CtMethod m) throws CannotCompileException {
        if (null == m || m.isEmpty()) {
            return;
        }
        //避免变量名重复
        m.addLocalVariable("dingjsh_javaagent_elapsedTime", CtClass.longType);
        m.insertBefore("dingjsh_javaagent_elapsedTime = java.lang.System.currentTimeMillis();");
        m.insertAfter("dingjsh_javaagent_elapsedTime = java.lang.System.currentTimeMillis() - dingjsh_javaagent_elapsedTime;"
                + LOG_UTILS
                + ".log(\""
                + className
                + "\",\""
                + m.getName()
                + "\",java.lang.System.currentTimeMillis(),(int)dingjsh_javaagent_elapsedTime"
                + ");");
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
        //do not transform the agent class,prevent deadlock 
        if (className.startsWith(AGENT_PACKAGE_NAME)) {
            return false;
        }
        Set<String> includes = ConfigUtils.getIncludePackages();
        if (null == includes || includes.isEmpty()) {
            return false;
        }
        
        boolean isNeeded = false;
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
        return isNeeded;
    }
}
