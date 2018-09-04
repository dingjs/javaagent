package com.wenshuo.agent.transformer;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import com.wenshuo.agent.javassist.CannotCompileException;
import com.wenshuo.agent.javassist.ClassPool;
import com.wenshuo.agent.javassist.CtClass;
import com.wenshuo.agent.javassist.CtMethod;
import com.wenshuo.agent.javassist.LoaderClassPath;
import com.wenshuo.agent.javassist.Modifier;
import com.wenshuo.agent.javassist.NotFoundException;

import com.wenshuo.agent.ConfigUtils;
import com.wenshuo.agent.PojoDetector;

/**
 * AgentLogClassFileTransformer
 * 
 * @author dingjsh
 * @time 2015-7-24上午09:53:44
 */
public class AgentLogClassFileTransformer implements ClassFileTransformer {

    private static final String LOG_UTILS = "com.wenshuo.agent.log.ExecuteLogUtils";

    private static final String AGENT_PACKAGE_NAME = "com.wenshuo.agent";

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader
     * , java.lang.String, java.lang.Class, java.security.ProtectionDomain,
     * byte[])
     */
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;
        className = className.replace('/', '.');
        if (!isNeedLogExecuteInfo(className)) {
            return byteCode;
        }
        if (null == loader) {
            loader = Thread.currentThread().getContextClassLoader();
        }
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
                    if (isOpenPojoMonitor || !getSetMethods.contains(m.getName())) {
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
        boolean isMethodStatic = Modifier.isStatic(m.getModifiers());
        String aopClassName = isMethodStatic ? "\"" + className + "\"" : "this.getClass().getName()";
        final String timeMethodStr
            = ConfigUtils.isUsingNanoTime() ? "java.lang.System.nanoTime()" : "java.lang.System.currentTimeMillis()";

        // 避免变量名重复
        m.addLocalVariable("dingjsh_javaagent_elapsedTime", CtClass.longType);
        m.insertBefore("dingjsh_javaagent_elapsedTime = "+timeMethodStr+";");
        m.insertAfter(
            "dingjsh_javaagent_elapsedTime = "+timeMethodStr+" - dingjsh_javaagent_elapsedTime;");
        m.insertAfter(LOG_UTILS + ".log(" + aopClassName + ",\"" + m.getName()
            + "\",java.lang.System.currentTimeMillis(),(long)dingjsh_javaagent_elapsedTime" + ");");
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
        // do not transform the agent class,prevent deadlock
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
            Set<String> excludeClassRegexs = ConfigUtils.getExcludeClassRegexs();
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
