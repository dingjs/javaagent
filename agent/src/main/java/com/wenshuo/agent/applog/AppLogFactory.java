/**
 * @projectName javaagent
 * @package com.wenshuo.agent.applog
 * @className com.wenshuo.agent.applog.IAppLogFactory
 * @copyright Copyright 2023 Thunisoft, Inc All rights reserved.
 */

package com.wenshuo.agent.applog;

/**
 * AppLogFactory
 *
 * @author dingjsh
 * @description javaagent日志工厂类
 * @date 2023-10-16 11:49
 * @since 2.1.3
 */
public class AppLogFactory {

    private static final String SLF4J_FACTORY_CLASS_NAME = "org.slf4j.LoggerFactory";

    private static final String COMMONS_LOGGING_FACTORY_CLASS_NAME = "org.apache.commons.logging.LogFactory";

    /**
     * 如果工程中有slf4j,则采用slf4j输出日志，如果有commons-logging,则采用commons-logging,否则用console
     * @param clazz
     * @return javaagent日志对象
     */
    public static IAppLog getAppLog(Class<?> clazz) {
        if (isClassPresent(SLF4J_FACTORY_CLASS_NAME, Thread.currentThread().getContextClassLoader())) {
            return new Slf4jAppLog(clazz);
        } else if (isClassPresent(COMMONS_LOGGING_FACTORY_CLASS_NAME, Thread.currentThread().getContextClassLoader())) {
            return new CommonsLoggingAppLog(clazz);
        } else {
            return new ConsoleAppLog();
        }
    }

    protected static boolean isClassPresent(String className, ClassLoader classLoader) {
        try {
            resolve(className, classLoader);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    protected static Class<?> resolve(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader != null ? Class.forName(className, false, classLoader) : Class.forName(className);
    }

}