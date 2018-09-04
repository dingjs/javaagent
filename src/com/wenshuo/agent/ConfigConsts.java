/*
 * @(#)ConfigConsts.java	2015-7-27 下午06:06:23
 * javaagent
 * Copyright 2015 Thuisoft, Inc. All rights reserved.
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

    int DEFAULT_LOG_INTERVAL = 600;

}
