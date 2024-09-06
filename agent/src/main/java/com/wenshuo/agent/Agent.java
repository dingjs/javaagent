/*
 * @(#)Agent.java	2015-7-24 上午09:49:34
 * javaagent
 * Copyright 2015 wenshuo, Inc. All rights reserved.
 * wenshuo PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.wenshuo.agent;

import java.lang.instrument.Instrumentation;

import com.wenshuo.agent.applog.AppLogFactory;
import com.wenshuo.agent.applog.IAppLog;
import com.wenshuo.agent.log.ExecuteLogUtils;
import com.wenshuo.agent.transformer.AgentLogClassFileTransformer;

/**
 * Agent
 *
 * @author dingjsh
 */
public class Agent {

    private static IAppLog log = AppLogFactory.getAppLog(Agent.class);

    public static void premain(String agentArs, Instrumentation inst) {
        // 初始化配置
        ConfigUtils.initProperties(agentArs);
        log.info("javaagent启动成功，将自动记录方法的执行次数和时间,日志文件路径：" + ConfigUtils.getLogFileName());
        ExecuteLogUtils.init();
        inst.addTransformer(new AgentLogClassFileTransformer());
    }

}
