package com.wenshuo.agent;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.wenshuo.agent.applog.AppLogFactory;
import com.wenshuo.agent.applog.IAppLog;

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

    private static IAppLog log = AppLogFactory.getAppLog(ConfigUtils.class);

    private ConfigUtils() {
        super();
    }

    static void initProperties(String propertiesFileName) {
        props = getProperties(propertiesFileName);
        initExcludePackages();
        initIncludePackages();
        initExcludeClassRegexs();
    }

    private static String getProperty(String key) {
        if (null != props && AgentUtils.isNotBlank(key)) {
            return props.getProperty(key);
        }
        return null;
    }

    private static Properties getProperties(String propertiesFileName) {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = ConfigUtils.class.getClassLoader().getResourceAsStream("agent.properties");
            properties.load(input);
        } catch (Exception e) {
            log.warn("未找到默认配置");
        } finally {
            AgentUtils.closeQuietly(input);
        }
        if (AgentUtils.isNotBlank(propertiesFileName)) {
            try {
                input = new FileInputStream(propertiesFileName);
                properties.load(input);
            } catch (Exception e) {
                log.error("解析配置文件出错：" + propertiesFileName, e);
            } finally {
                AgentUtils.closeQuietly(input);
            }
        }
        return properties;
    }

    public static Set<String> getExcludePackages() {
        return excludePackages;
    }

    private static void initExcludePackages() {
        if (null == excludePackages) {
            String excludeDefault = getProperty(ConfigConsts.EXCLUDE_PACKAGE_DEFAULT);
            String excludes = getProperty(ConfigConsts.EXCLUDE_PACKAGE);
            excludePackages = new HashSet<String>();
            if (AgentUtils.isNotBlank(excludeDefault)) {
                excludePackages.addAll(Arrays.asList(excludeDefault.split(";")));
            }
            if (AgentUtils.isNotBlank(excludes)) {
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
            if (AgentUtils.isNotBlank(includes)) {
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
            if (AgentUtils.isNotBlank(defaultRegex)) {
                excludeClassRegexsTemp.addAll(Arrays.asList(defaultRegex.split(";")));
            }
            String excludeRegexStr = getProperty(ConfigConsts.EXCLUDE_CLASS_REGEX);
            if (AgentUtils.isNotBlank(excludeRegexStr)) {
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
     * @return 是否开启pojo的监控
     * @author dingjsh
     */
    public static boolean isOpenPojoMonitor() {
        String value = getProperty(ConfigConsts.POJO_MONITOR_OPEN);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * ConfigUtils
     *
     * @return 是否采用nanoTime来记录方法的执行时间
     * @description 是否采用nanoTime来记录方法的执行时间
     * @author dingjsh
     * @date 2018年5月25日 下午2:08:33
     * @version 1.2.0
     */
    public static boolean isUsingNanoTime() {
        String value = getProperty(ConfigConsts.LOG_TIME_NANO);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * ConfigUtils
     *
     * @author fanzhongwei
     * @return boolean 是否统计方法执行时间百分比，同JMeter性能测试百分比计算方式
     * @date 2024/9/5 下午7:12
     **/
    public static boolean isLogStatExecuteTime() {
        String value = getProperty(ConfigConsts.LOG_STAT_EXECUTE_TIME);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * ConfigUtils
     *
     * @author fanzhongwei
     * @return boolean 方法执行时间统计百分比（agent.log.stat.execute.time=true时有效），多选范围[0, 1]，例如：0.5,0.9,0.95,0.99
     * @date 2024/9/5 下午7:13
     **/
    public static String getLogStatExecuteTimePct() {
        String value = getProperty(ConfigConsts.LOG_STAT_EXECUTE_TIME_PCT);
        return value;
    }

}
