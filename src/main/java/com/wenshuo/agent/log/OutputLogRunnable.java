/*
 * @(#)OutputLogRunnable.java	2015-7-28 下午03:27:20
 * javaagent
 * Copyright 2015 wenshuo, Inc. All rights reserved.
 * wenshuo PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.wenshuo.agent.log;

import com.wenshuo.agent.applog.AppLogFactory;
import com.wenshuo.agent.applog.IAppLog;

/**
 * OutputLogRunnable
 *
 * @author dingjsh
 * @time 2015-7-28下午03:27:20
 */
public class OutputLogRunnable implements Runnable {

    private static IAppLog log = AppLogFactory.getAppLog(OutputLogRunnable.class);

    @Override
    public void run() {
        try {
            ExecuteLogUtils.outputCounterLog();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
