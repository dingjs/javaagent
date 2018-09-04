package com.wenshuo.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 获取配置信息<br>
 * ConfigUtils
 * 
 * @author dingjsh
 * @time 2015-7-27下午09:16:18
 */
public class ConfigUtils {

    private static Properties props;

    private static Set<String> excludePackages;

    private static Set<String> includePackages;

    private static Set<String> excludeClassRegexs;

    private ConfigUtils() {
        super();
    }

    public static void initProperties(String propertiesFileName) {
        props = getProperties(propertiesFileName);
        initExcluePackages();
        initIncludePackages();
        initExcludeClassRegexs();
    }

    public static String getProperty(String key) {
        if (null != props && AgentUtils.isNotBlank(key)) {
            return props.getProperty(key);
        }
        return null;
    }

    private static Properties getProperties(String propertiesFileName) {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = ConfigUtils.class.getClassLoader().getResourceAsStream("props/agent.properties");
            properties.load(input);
        } catch (Exception e) {
            System.err.println("未找到默认配置");
        } finally {
            AgentUtils.closeQuietly(input);
        }
        if (AgentUtils.isNotBlank(propertiesFileName)) {
            try {
                input = new FileInputStream(new File(propertiesFileName));
                properties.load(input);
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                AgentUtils.closeQuietly(input);
            }
        }
        return properties;
    }

    public static Set<String> getExcludePackages() {
        return excludePackages;
    }

    private static void initExcluePackages() {
        if (null == excludePackages) {
            String excludeDefault = getProperty(ConfigConsts.EXCLUDE_PACKAGE_DEFAULT);
            String excludes = getProperty(ConfigConsts.EXCLUDE_PACKAGE);
            excludePackages = new HashSet<String>();
            if (null != excludeDefault && AgentUtils.isNotBlank(excludeDefault)) {
                excludePackages.addAll(Arrays.asList(excludeDefault.split(";")));
            }
            if (null != excludes && AgentUtils.isNotBlank(excludes)) {
                excludePackages.addAll(Arrays.asList(excludes.split(";")));
            }
        }
    }

    public static Set<String> getIncludePackages() {
        return includePackages;
    }

    private static void initIncludePackages() {
        if (null == includePackages) {
            String includes = getProperty(ConfigConsts.INCLUDE_PACKAGE);
            includePackages = new HashSet<String>();
            if (null != includes && AgentUtils.isNotBlank(includes)) {
                includePackages.addAll(Arrays.asList(includes.split(";")));
            }
        }
    }

    public static String getLogFileName() {
        return getProperty(ConfigConsts.LOG_FILE);
    }

    public static int getLogInterval() {
        String intervalStr = getProperty(ConfigConsts.LOG_INTERVAL_SECONDS);
        if (AgentUtils.isBlank(intervalStr)) {
            return ConfigConsts.DEFAULT_LOG_INTERVAL;
        }
        return Integer.parseInt(intervalStr);
    }

    public static Set<String> getExcludeClassRegexs() {
        return excludeClassRegexs;
    }

    private static void initExcludeClassRegexs() {
        if (null == excludeClassRegexs) {
            Set<String> excludeClassRegexsTemp = new HashSet<String>();
            String defaultRegex = getProperty(ConfigConsts.EXCLUDE_CLASS_REGEX_DEFAULT);
            if (null != defaultRegex && AgentUtils.isNotBlank(defaultRegex)) {
                excludeClassRegexsTemp.addAll(Arrays.asList(defaultRegex.split(";")));
            }
            String excludeRegexStr = getProperty(ConfigConsts.EXCLUDE_CLASS_REGEX);
            if (null != excludeRegexStr && AgentUtils.isNotBlank(excludeRegexStr)) {
                excludeClassRegexsTemp.addAll(Arrays.asList(excludeRegexStr.split(";")));
            }
            excludeClassRegexs = excludeClassRegexsTemp;
        }
    }

    public static boolean isLogAvgExecuteTime() {
        String value = getProperty(ConfigConsts.LOG_AVG_EXECUTE_TIME);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * 是否开启pojo的监控
     * 
     * @return
     * @author dingjsh
     */
    public static boolean isOpenPojoMonitor() {
        String value = getProperty(ConfigConsts.POJO_MONITOR_OPEN);
        return "true".equalsIgnoreCase(value);
    }
    
    /**
     * ConfigUtils
     * @description 是否采用nanoTime来记录方法的执行时间
     * @return
     * @author dingjsh
     * @date 2018年5月25日 下午2:08:33
     * @version 1.2.0
     */
    public static boolean isUsingNanoTime(){
        String  value = getProperty(ConfigConsts.LOG_TIME_NANO);
        return "true".equalsIgnoreCase(value);
    }
}
