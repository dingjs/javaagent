package com.thunisoft.agent.log;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * MethodExecuteJSONformatter
 * 
 * @description 2.0.0
 * @author dingjsh
 * @date 2018年8月22日 下午8:07:01
 * @version 2.0.0
 */
class MethodExecuteJSONformatter {

    /**
     * 
     * MethodExecuteJSONformatter
     * 
     * @description 将方法执行监控数据格式化为json string
     * @param className 类名
     * @param method2ExecuteMap Map<方法名, AtomicLong[]> 数组第一个是执行次数，第二个是执行时间
     * @param startTime 统计周期的开始时间，格式为 yyyy-MM-dd HH:mm:ss
     * @param endTime 统计周期的结束时间，格式为 yyyy-MM-dd HH:mm:ss
     * @return 格式化后的json string
     * @author dingjsh
     * @date 2018年8月22日 下午8:49:18
     * @version 2.0.0
     */
    public static String getMethodExecuteJSON(String className, Map<String, AtomicLong[]> method2ExecuteMap,
        String startTime, String endTime, boolean isUsingNanoTime, boolean logAvgExecuteTime) {
        StringBuilder json = new StringBuilder("{");
        appendString(json, "class", className).append(",");
        appendString(json, "start", startTime).append(",");
        appendString(json, "end", endTime).append(",");
        appendKey(json, "methods").append("[");
        Iterator<Map.Entry<String, AtomicLong[]>> method2ExecuteIte = method2ExecuteMap.entrySet().iterator();
        while (method2ExecuteIte.hasNext()) {
            Map.Entry<String, AtomicLong[]> methodEntry = method2ExecuteIte.next();
            String methodName = methodEntry.getKey();
            AtomicLong[] executeCounter = methodEntry.getValue();
            long counter = executeCounter[0].longValue();
            long timeInMillis
                = isUsingNanoTime ? executeCounter[1].longValue() / 1000000 : executeCounter[1].longValue();
            json.append("{");
            appendString(json, "name", methodName).append(",");
            appendLong(json, "counter", counter).append(",");
            appendLong(json, "time", timeInMillis);
            if (logAvgExecuteTime && counter > 0) {
                json.append(",");
                appendLong(json, "avg", timeInMillis / counter);
            }
            json.append("},");
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("]}");
        return json.toString();
    }

    private static StringBuilder appendString(StringBuilder jsonBuilder, String key, String value) {
        return appendKey(jsonBuilder, key).append("\"").append(value).append("\"");
    }

    private static StringBuilder appendLong(StringBuilder jsonBuilder, String key, long value) {
        return appendKey(jsonBuilder, key).append(value);
    }

    private static StringBuilder appendKey(StringBuilder jsonBuilder, String key) {
        return jsonBuilder.append("\"").append(key).append("\":");
    }
}
