package com.wenshuo.agent.analyzer.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2024/09/06 14:10
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodBean {

    @JSONField(name = "name")
    private String methodName;

    @JSONField(name = "time")
    private Long time;

    @JSONField(name = "avg")
    private Long avg;

    @JSONField(name = "counter")
    private Long counter;

    @JSONField(name = "min")
    private Long min;

    @JSONField(name = "max")
    private Long max;

    @JSONField(name = "th50")
    private Long median;

    @JSONField(name = "th90")
    private Long th90Pct;

    @JSONField(name = "th95")
    private Long th95Pct;

    @JSONField(name = "th99")
    private Long th99Pct;
}
