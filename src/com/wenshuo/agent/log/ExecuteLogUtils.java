/*
 * @(#)ExecuteLogUtils.java 2015-7-27 下午05:57:01 javaagent Copyright 2015 wenshuo, Inc. All rights reserved. wenshuo
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wenshuo.agent.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.wenshuo.agent.AgentUtils;
import com.wenshuo.agent.ConfigUtils;
import com.wenshuo.agent.NamedThreadFactory;

/**
 * ExecuteLogUtils
 *
 * @author dingjsh
 * @time 2015-7-27下午05:57:01
 */
public class ExecuteLogUtils {

    private static BufferedWriter counterLogWriter = null;

    private static long nextDayStartTimeMillis;

    private static String logFileName = null;

    private static long startTimeMillis;

    private static ScheduledThreadPoolExecutor counterLogExecutor;

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, long[]>> executeCounterMap;

    private static final Object executeCounterLock = new Object();

    private static final int BUFFER_SIZE = 256 * 1024;

    private static final String ENCODING = "UTF-8";

    private static boolean isUsingNanoTime = false;

    private static boolean logAvgExecuteTime = false;

    private static volatile boolean isInitialized = false;

    private ExecuteLogUtils() {
        super();
    }

    /**
     * 初使化
     *
     * @author dingjsh
     * @time 2015-7-30上午10:47:58
     */
    public static synchronized void init() {
        if (isInitialized) {
            return;
        }
        logFileName = ConfigUtils.getLogFileName();
        int interval = ConfigUtils.getLogInterval();
        logAvgExecuteTime = ConfigUtils.isLogAvgExecuteTime();
        isUsingNanoTime = ConfigUtils.isUsingNanoTime();
        if (AgentUtils.isBlank(logFileName)) {
            System.err.println("日志文件名为空");
            throw new RuntimeException("日志文件名为空");
        }
        setNextDateStartTimeMillis();
        initWriter();
        executeCounterMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, long[]>>();
        startTimeMillis = System.currentTimeMillis();
        counterLogExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("pool-thread-agent-log", true));
        counterLogExecutor.scheduleWithFixedDelay(new OutputLogRunnable(), interval, interval, TimeUnit.SECONDS);
        isInitialized = true;
    }

    public static void log(String className, String methodName, long executeTime) {
        logExecuteCounter(className, methodName, executeTime);
    }

    /**
     * 输出方法执行记数日志
     *
     * @author dingjsh
     * @time 2015-7-28下午01:35:25
     */
    synchronized static void outputCounterLog() throws IOException {
        ConcurrentHashMap<String, ConcurrentHashMap<String, long[]>> executeCounterMapInner = executeCounterMap;
        executeCounterMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, long[]>>();
        String startTime = formatTimeMillis(startTimeMillis);
        String endTime = formatTimeMillis(System.currentTimeMillis());
        long byteLength = removeJSONArrayEndBracket();
        if (0 == byteLength) {
            writeLog("[", true, startTimeMillis);
        }

        Set<Map.Entry<String, ConcurrentHashMap<String, long[]>>> entrySet = executeCounterMapInner.entrySet();
        Iterator<Map.Entry<String, ConcurrentHashMap<String, long[]>>> ite = entrySet.iterator();
        int length = entrySet.size();
        if (length > 0 && byteLength > 10) { // 说明文件不只是[],json数组中已经有内容
            writeLog(",", startTimeMillis);
        }
        for (int index = 0; ite.hasNext(); index++) {
            Map.Entry<String, ConcurrentHashMap<String, long[]>> entry = ite.next();
            String className = entry.getKey();
            Map<String, long[]> method2ExecuteMap = entry.getValue();
            String methodExecuteJson = MethodExecuteJSONformatter.getMethodExecuteJSON(className, method2ExecuteMap,
                    startTime, endTime, isUsingNanoTime, logAvgExecuteTime);
            writeLog(methodExecuteJson, startTimeMillis);
            if (index < length - 1) {
                writeLog(",", true, startTimeMillis);
            }
        }
        writeLog("]", true, startTimeMillis);
        flushLogAndClose();
        startTimeMillis = System.currentTimeMillis();
    }

    private static void logExecuteCounter(String className, String methodName, long executeTime) {
        // dingjs modfied in 20210825 原来是用锁和Atomiclong来避免线程安全问题，现在去掉这些实现。原因是此处线程安全问题顶多导致
        // 数据有一些误差，这个误差对于agent数据监控并没有什么影响，牺牲一定的准确性带来性能的提升是有必要的
        ConcurrentHashMap<String, long[]> methodCounterMap = getOrCreateClassExecutesMapping(className);
        long[] counter = methodCounterMap.get(methodName);
        if (null == counter) {
            methodCounterMap.put(methodName, new long[]{1, executeTime});
        } else {
            counter[0]++;
            counter[1] = executeTime + counter[1];
        }
    }

    /**
     * ExecuteLogUtils
     *
     * @param className 类名
     * @return class和它的调用次数映射关系
     * @description 获得class和它的调用次数映射关系，如果exeuteCounterMap中没有，则创建一个并放入
     * @author dingjsh
     * @date 2018年5月25日 下午4:40:33
     * @version 1.2.0
     */
    private static ConcurrentHashMap<String, long[]> getOrCreateClassExecutesMapping(String className) {
        ConcurrentHashMap<String, long[]> methodCounterMap = executeCounterMap.get(className);
        if (null == methodCounterMap) {
            synchronized (executeCounterLock) {
                methodCounterMap = executeCounterMap.get(className);
                if (null == methodCounterMap) {
                    methodCounterMap = new ConcurrentHashMap<String, long[]>();
                    executeCounterMap.put(className, methodCounterMap);
                }
            }
        }
        return methodCounterMap;
    }

    private static void writeLog(String logValue, long currTimeMillis) {
        writeLog(logValue, false, currTimeMillis);
    }

    private static void writeLog(String logValue, boolean newLine, long currTimeMillis) {
        ensureLogFileUpToDate(currTimeMillis);
        try {
            counterLogWriter.write(logValue);
            if (newLine) {
                counterLogWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private static void flushLogAndClose() {
        try {
            counterLogWriter.flush();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            AgentUtils.closeQuietly(counterLogWriter);
            counterLogWriter = null;
        }
    }

    /**
     * 确保日志文件没过时,日志文件都会加上日期后缀,如果当前日志文件
     */
    private static void ensureLogFileUpToDate(long currTimeMillis) {
        if (currTimeMillis >= nextDayStartTimeMillis) {
            try {
                if (null != counterLogWriter) {
                    counterLogWriter.flush();
                }
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                AgentUtils.closeQuietly(counterLogWriter);
            }
            initWriter();
            setNextDateStartTimeMillis();
        }
        if (null == counterLogWriter) {
            initWriter();
        }

    }

    private static void initWriter() {
        try {
            File logFile = getCounterLogFile(logFileName);
            counterLogWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), ENCODING),
                    BUFFER_SIZE);
        } catch (IOException e) {
            System.err.println(e);
            throw new RuntimeException("无法初始化【" + logFileName + "】,建议您检查磁盘空间，或者手动删除该日志文件");
        }
    }

    private static File getCounterLogFile(String logFileName) throws IOException {

        String logFileNameWithDate = getCurrDateString();
        int lastIndexOfDot = logFileName.lastIndexOf('.');
        if (lastIndexOfDot > 0) {
            logFileName = logFileName.substring(0, lastIndexOfDot) + "." + logFileNameWithDate
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

    private static String formatTimeMillis(long timeMillis) {
        Date date = new Date(timeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    private static long removeJSONArrayEndBracket() throws IOException {
        File logFile = getCounterLogFile(logFileName);
        RandomAccessFile f = new RandomAccessFile(logFile.getAbsolutePath(), "rw");
        long length = f.length();
        byte b = -1;
        while (b != 93 && length > 0) {
            length -= 1;
            f.seek(length);
            b = f.readByte();
        }
        f.setLength(length);
        f.close();
        return length;
    }
}