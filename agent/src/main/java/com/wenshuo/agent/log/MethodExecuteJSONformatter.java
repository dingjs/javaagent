package com.wenshuo.agent.log;

import com.tdunning.math.stats.MergingDigest;

import java.util.Map;


/**
 * MethodExecuteJSONformatter
 *
 * @author dingjsh
 * @version 2.0.0
 * @description 2.0.0
 * @date 2018年8月22日 下午8:07:01
 */
class MethodExecuteJSONformatter {

    /**
     * MethodExecuteJSONformatter
     *
     * @param className         类名
     * @param method2ExecuteMap Map<方法名, Object[]> 数组第一个是执行次数，第二个是执行时间，第三个是方法执行时间统计（MergingDigest, 根据配置可选）
     * @param startTime         统计周期的开始时间，格式为 yyyy-MM-dd HH:mm:ss
     * @param endTime           统计周期的结束时间，格式为 yyyy-MM-dd HH:mm:ss
     * @return 格式化后的json string
     * @description 将方法执行监控数据格式化为json string
     * @author dingjsh
     * @date 2018年8月22日 下午8:49:18
     * @version 2.0.0
     */
    static String getMethodExecuteJSON(String className, Map<String, Object[]> method2ExecuteMap,
                                       String startTime, String endTime, boolean isUsingNanoTime, boolean logAvgExecuteTime,
                                       boolean logStatExecuteTime, double[] logStatExecuteTimePct) {
        StringBuilder json = new StringBuilder("{");
        appendString(json, "class", className).append(",");
        appendString(json, "start", startTime).append(",");
        appendString(json, "end", endTime).append(",");
        appendKey(json, "methods").append("[");
        for (Map.Entry<String, Object[]> methodEntry : method2ExecuteMap.entrySet()) {
            String methodName = methodEntry.getKey();
            Object[] executeCounter = methodEntry.getValue();
            Long counter = (Long) executeCounter[0];
            Long totalTime = (Long) executeCounter[1];
            long timeInMillis
                    = isUsingNanoTime ? totalTime / 1000000 : totalTime;
            json.append("{");
            appendString(json, "name", methodName).append(",");
            appendLong(json, "counter", counter).append(",");
            appendLong(json, "time", timeInMillis);
            if (logAvgExecuteTime && counter > 0) {
                json.append(",");
                appendLong(json, "avg", timeInMillis / counter);
            }
            if (logStatExecuteTime && counter > 0) {
                MergingDigest md = (MergingDigest)executeCounter[2];
                json.append(",");
                appendLong(json, "min", (long) md.getMin());

                for (int i = 0; i < logStatExecuteTimePct.length; i++) {
                    double pct = logStatExecuteTimePct[i];
                    json.append(",");
                    appendLong(json, "th" + (int)(pct * 100), (long) md.quantile(pct));
                }

                json.append(",");
                appendLong(json, "max", (long) md.getMax());
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
