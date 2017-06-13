/*
 * @(#)ExecuteLogUtils.java 2015-7-27 下午05:57:01
 * javaagent
 * Copyright 2015 Thuisoft, Inc. All rights reserved.
 * THUNISOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thunisoft.agent.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.thunisoft.agent.AgentUtils;
import com.thunisoft.agent.ConfigUtils;
import com.thunisoft.agent.NamedThreadFactory;

/**
 * ExecuteLogUtils 这个class要加载到webapp的classloader中，不要增加任何第三方类的引用
 * 
 * @author dingjsh
 * @time 2015-7-27下午05:57:01
 */
public class ExecuteLogUtils {

    private static BufferedWriter counterLogWriter = null;

    private static long nextDayStartTimeMillis;

    private static String logFileName = null;

    private static long startTimemillis;

    private static ScheduledThreadPoolExecutor counterLogExecutor;

    private static boolean isOutputingLog; // 是否在输出日志，为避免阻塞，第一版处理方案是输出日志时舍弃新的输入

    private static Map<String, Map<String, int[]>> exeuteCounterMap = new ConcurrentHashMap<String, Map<String, int[]>>();

    private static final Object executeCounterLock = new Object();

    private static final int BUFFER_SIZE = 256 * 1024;

    private static final String ENCODING = "UTF-8";

    private static long intervalInMillis;

    private static boolean logAvgExecuteTime = false;
    
    private static boolean inited = false;

    /**
     * 初使化
     *
     * @author dingjsh
     * @time 2015-7-30上午10:47:58
     */
    public static synchronized void init() {
        if(inited){
            return;
        }
        String logFileName = ConfigUtils.getLogFileName();
        int interval = ConfigUtils.getLogInterval();
        boolean logAvgExecuteTime = ConfigUtils.isLogAvgExecuteTime();
        ExecuteLogUtils.logFileName = logFileName;
        ExecuteLogUtils.intervalInMillis = (long) interval * 1000;
        ExecuteLogUtils.logAvgExecuteTime = logAvgExecuteTime;
        if (AgentUtils.isBlank(logFileName)) {
            System.err.println("日志文件名为空");
            throw new RuntimeException("日志文件名为空");
        }
        setNextDateStartTimeMillis();
        initWriter();
        startTimemillis = System.currentTimeMillis();
        counterLogExecutor = new ScheduledThreadPoolExecutor(1,new NamedThreadFactory("pool-thread-agent-log", true));
        counterLogExecutor.scheduleWithFixedDelay(new OutputLogRunnable(), interval,
                interval, TimeUnit.SECONDS);
        inited = true;
    }
    
    public static void log(String className, String methodName,
            long currentTimemillis, int executeTime) {
        if (isOutputingLog) {
            return;
        }
        logExecuteCounter(className, methodName, executeTime);
    }

    /**
     * 输出方法执行记数日志
     * 
     * @author dingjsh
     * @time 2015-7-28下午01:35:25
     */
    public static void outputCounterLog() {
        isOutputingLog = true;
        // 如果下次日志记录时间是在第二天，则清空目前exeuteCounterMap的数据
        boolean needClearLog = System.currentTimeMillis() + intervalInMillis >= nextDayStartTimeMillis;

        writeLog("-----------------------");// 分隔线
        writeLog("startTime:{" + foramteTimeMillis(startTimemillis) + "}");
        Iterator<Map.Entry<String, Map<String, int[]>>> ite = exeuteCounterMap
                .entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, Map<String, int[]>> entry = ite.next();
            String className = entry.getKey();
            Map<String, int[]> method2ExecuteMap = entry.getValue();
            writeLog("{");
            writeLog("className:{" + className + "}");
            Iterator<Map.Entry<String, int[]>> method2ExecuteIte = method2ExecuteMap
                    .entrySet().iterator();
            while (method2ExecuteIte.hasNext()) {
                Map.Entry<String, int[]> methodEntry = method2ExecuteIte.next();
                String methodName = methodEntry.getKey();
                int[] executeCounter = methodEntry.getValue();
                String logInfo = "methodName:{" + methodName + "},counter:{"
                        + executeCounter[0] + "},time:{" + executeCounter[1]
                        + "}";
                if (logAvgExecuteTime && executeCounter[0] > 0) {
                    logInfo += ",avg:{"
                            + (executeCounter[1] / executeCounter[0]) + "}";
                }
                writeLog(logInfo);
            }
            writeLog("}");
        }
        writeLog("endTime:{" + foramteTimeMillis(System.currentTimeMillis())
                + "}");
        flushLog();
        if (needClearLog) {
            exeuteCounterMap.clear();
            startTimemillis = System.currentTimeMillis();
        }
        isOutputingLog = false;
    }

    private static void logExecuteCounter(String className, String methodName,
            int executeTime) {
        Map<String, int[]> methodCounterMap = exeuteCounterMap.get(className);
        if (null == methodCounterMap) {
            synchronized (executeCounterLock) {
                methodCounterMap = exeuteCounterMap.get(className);
                if (null == methodCounterMap) {
                    methodCounterMap = new ConcurrentHashMap<String, int[]>();
                    exeuteCounterMap.put(className, methodCounterMap);
                }
            }
        }
        // 不采用统一的锁，而用methodCounterMap做锁，这样不同class对应的Map可以并发执行，大大提高并发能力
        // dingjsh commented in 20150729
        synchronized (methodCounterMap) {
            int[] oldCounter = methodCounterMap.get(methodName);
            if (null == oldCounter) {
                methodCounterMap.put(methodName, new int[] { 1, executeTime });
            } else {
                oldCounter[0] += 1;
                oldCounter[1] += executeTime;
            }
        }
    }

    private static void writeLog(String logValue) {
        writeLog(logValue, true);
    }

    private static void writeLog(String logValue, boolean newLine) {
        ensureLogFileUpToDate();
        try {
            counterLogWriter.write(logValue);
            if (newLine) {
                counterLogWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private static void flushLog() {
        try {
            counterLogWriter.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * 确保日志文件没过时,日志文件都会加上日期后缀,如果当前日志文件
     */
    private static void ensureLogFileUpToDate() {
        long currTimeMillis = System.currentTimeMillis();
        if (currTimeMillis >= nextDayStartTimeMillis) {
            try {
                counterLogWriter.flush();
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                AgentUtils.closeQuietly(counterLogWriter);
            }
            initWriter();
            setNextDateStartTimeMillis();
        }
    }

    private static void initWriter() {
        try {
            File logFile = getCounterLogFile(logFileName, true);
            counterLogWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(logFile, true), ENCODING), BUFFER_SIZE);
        } catch (IOException e) {
            System.err.println(e);
            throw new RuntimeException("无法初始化【"+logFileName+"】,建议您检查磁盘空间，或者手动删除该日志文件");
        }
    }

    private static File getCounterLogFile(String logFileName, boolean appendDate)
            throws IOException {

        String logFileNameWithDate = getCurrDateString();
        int lastIndexOfDot = logFileName.lastIndexOf('.');
        if (lastIndexOfDot > 0) {
            logFileName = logFileName.substring(0, lastIndexOfDot) + "."
                    + logFileNameWithDate
                    + logFileName.substring(lastIndexOfDot);
        } else {
            logFileName = logFileName + "." + logFileNameWithDate + ".log";
        }
        File logFile = getLogFile(logFileName);
        return logFile;
    }

    private static File getLogFile(String logFileName) throws IOException {
        File logFile = new File(logFileName);
        if (logFile.isDirectory()) {
            System.err.println("【" + logFileName + "】是文件夹");
            throw new RuntimeException("【" + logFileName + "】是文件夹");
        } else if (!logFile.exists()) {
            // dingjs modified in 20140513
            File dirFile = logFile.getParentFile();
            AgentUtils.forceMkdir(dirFile);
            boolean created = logFile.createNewFile();
            if (!created) {
                System.err.println("【" + logFileName + "】创建失败");
                throw new RuntimeException("【" + logFileName + "】创建失败");
            }
        }
        return logFile;
    }

    private static String getCurrDateString() {
        Date today = new Date();
        return new SimpleDateFormat("yyyy-MM-dd").format(today);
    }

    /**
     * 由开始日期转换为开始时间，一般在以时间为条件的查询中使用
     */
    private static Date date2StartTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        Date startTime = null;
        try {
            startTime = sdf.parse(dateStr + " 00:00:00");
        } catch (ParseException e) {
            System.err.println(e);
        }
        return startTime;
    }

    /**
     * 设置下一天的开始时间
     */
    private static void setNextDateStartTimeMillis() {
        Date currDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currDate);
        cal.add(Calendar.DATE, 1);
        Date startTime = date2StartTime(cal.getTime());
        nextDayStartTimeMillis = startTime.getTime();
    }

    private static String foramteTimeMillis(long timeMillis) {
        Date date = new Date(timeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
