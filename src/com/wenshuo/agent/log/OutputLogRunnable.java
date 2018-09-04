/*
 * @(#)OutputLogRunnable.java	2015-7-28 下午03:27:20
 * javaagent
 * Copyright 2015 Thuisoft, Inc. All rights reserved.
 * wenshuo PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wenshuo.agent.log;

/**
 * OutputLogRunnable
 * 
 * @author dingjsh
 * @time 2015-7-28下午03:27:20
 */
public class OutputLogRunnable implements Runnable {

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try{
            ExecuteLogUtils.outputCounterLog();
        }catch(Exception e){
            System.err.println(e);
        }
    }

}
