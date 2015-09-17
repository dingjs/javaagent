package com.thunisoft.agent;

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

    private static Boolean logAvgExecuteTime;

    public static void initProperties(String propertiesFileName) {
        props = getProperties(propertiesFileName);
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
            input = ConfigUtils.class.getClassLoader().getResourceAsStream(
                    "props/thunisoft-agent.properties");
            properties.load(input);
        } catch (Exception e) {
            System.err.println("未找到默认配置");
        } finally {
            AgentUtils.closeQuietly(input);
        }
        if (AgentUtils.isNotBlank(propertiesFileName)) {
            try {
                // dingjs added in 20131021 在加密工具同一个文件夹内
                // 如果有相同的配置,文件夹的配置覆盖jar包内的配置
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
        if (null == excludePackages) {
            synchronized (ConfigUtils.class) {
                if (null == excludePackages) {
                    String excludeDefault = getProperty(ConfigConsts.EXCLUDE_PACKAGE_DEFAULT);
                    String excludes = getProperty(ConfigConsts.EXCLUDE_PACKAGE);
                    Set<String> excludePackagesTmp = excludePackages = new HashSet<String>();
                    if (AgentUtils.isNotBlank(excludeDefault)) {
                        excludePackagesTmp.addAll(Arrays.asList(excludeDefault
                                .split(";")));
                    }
                    if (AgentUtils.isNotBlank(excludes)) {
                        excludePackagesTmp.addAll(Arrays.asList(excludes
                                .split(";")));
                    }
                    excludePackages = excludePackagesTmp;
                }
            }
        }
        return excludePackages;
    }

    public static Set<String> getIncludePackages() {
        if (null == includePackages) {
            synchronized (ConfigUtils.class) {
                if (null == includePackages) {
                    String includes = getProperty(ConfigConsts.INCLUDE_PACKAGE);
                    Set<String> includePackagesTmp = new HashSet<String>();
                    if (AgentUtils.isNotBlank(includes)) {
                        includePackagesTmp.addAll(Arrays.asList(includes
                                .split(";")));
                    }
                    includePackages = includePackagesTmp;
                }
            }
        }
        return includePackages;
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
        if (null == excludeClassRegexs) {
            synchronized (ConfigUtils.class) {
                if (null == excludeClassRegexs) {
                    excludeClassRegexs = new HashSet<String>();
                    String defaultRegex = getProperty(ConfigConsts.EXCLUDE_CLASS_REGEX_DEFAULT);
                    if (AgentUtils.isNotBlank(defaultRegex)) {
                        excludeClassRegexs.addAll(Arrays.asList(defaultRegex
                                .split(";")));
                    }
                    String excludeRegexStr = getProperty(ConfigConsts.EXCLUDE_CLASS_REGEX);
                    if (AgentUtils.isNotBlank(excludeRegexStr)) {
                        excludeClassRegexs.addAll(Arrays.asList(excludeRegexStr
                                .split(";")));
                    }
                }
            }
        }
        return excludeClassRegexs;
    }

    public static boolean isLogAvgExecuteTime() {
        if (null == logAvgExecuteTime) {
            synchronized (ConfigUtils.class) {
                if (null == logAvgExecuteTime) {
                    String value = getProperty(ConfigConsts.LOG_AVG_EXECUTE_TIME);
                    logAvgExecuteTime = "true".equalsIgnoreCase(value);
                }
            }
        }
        return logAvgExecuteTime.booleanValue();
    }
}
