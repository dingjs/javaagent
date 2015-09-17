/*
 * @(#)Agent.java	2015-7-24 上午09:49:34
 * javaagent
 * Copyright 2015 Thuisoft, Inc. All rights reserved.
 * THUNISOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thunisoft.agent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;

import com.thunisoft.agent.log.ExecuteLogUtils;
import com.thunisoft.agent.log.OutputLogRunnable;
import com.thunisoft.agent.transformer.ThunisoftClassFileTransformer;

/**
 * Agent
 * 
 * @author dingjsh
 */
public class Agent {

    public static void premain(String agentArs, Instrumentation inst)
            throws IOException {
        // 初始化配置
        ConfigUtils.initProperties(agentArs);

        setExecuteLogUtilsBytes();

        inst.addTransformer(new ThunisoftClassFileTransformer());
    }

    private static void setExecuteLogUtilsBytes() throws IOException {
        byte[] logUtilsBytes = getClassBytes(ExecuteLogUtils.class);
        byte[] outputRunnableBytes = getClassBytes(OutputLogRunnable.class);
        ThunisoftClassFileTransformer.setLogUtilsClassBytes(logUtilsBytes,
                outputRunnableBytes);
    }

    @SuppressWarnings("rawtypes")
    private static byte[] getClassBytes(Class c) throws IOException {
        String className = c.getName();
        String classAsPath = className.replace('.', '/') + ".class";
        InputStream input = c.getClassLoader().getResourceAsStream(classAsPath);
        byte[] logUtilsBytes = AgentUtils.toByteArray(input);
        AgentUtils.closeQuietly(input);
        return logUtilsBytes;
    }

}
