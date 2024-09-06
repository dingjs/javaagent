/*
 * @(#)ConfigConsts.java	2015-7-27 下午06:06:23
 * javaagent
 * Copyright 2015 wenshuo, Inc. All rights reserved.
 * wenshuo PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wenshuo.agent;

/**
 * ConfigConsts
 * 
 * @author dingjsh
 * @time 2015-7-27下午06:06:23
 */
public interface ConfigConsts {

    String EXCLUDE_PACKAGE_DEFAULT = "agent.exclude.package.default";

    String EXCLUDE_PACKAGE = "agent.exclude.package";

    String INCLUDE_PACKAGE = "agent.include.package";

    String LOG_FILE = "agent.log.file";

    String LOG_INTERVAL_SECONDS = "agent.log.interval.seconds";

    String EXCLUDE_CLASS_REGEX = "agent.exclude.class.regex";

    String EXCLUDE_CLASS_REGEX_DEFAULT = "agent.exclude.class.regex.default";

    String LOG_AVG_EXECUTE_TIME = "agent.log.avg.execute.time";
    
    String POJO_MONITOR_OPEN = "agent.pojo.monitor.open";
    
    String LOG_TIME_NANO = "agent.log.nano";

    /** 是否统计方法执行时间百分比，同JMeter性能测试百分比计算方式，如果开启默认会统计最大值、最小值*/
    String LOG_STAT_EXECUTE_TIME = "agent.log.stat.execute.time";

    /** 方法执行时间统计百分比（agent.log.stat.execute.time=true时有效），多选范围[0, 1]，例如：0.5,0.9,0.95,0.99 */
    String LOG_STAT_EXECUTE_TIME_PCT = "agent.log.stat.execute.time.pct";

    int DEFAULT_LOG_INTERVAL = 600;

}
