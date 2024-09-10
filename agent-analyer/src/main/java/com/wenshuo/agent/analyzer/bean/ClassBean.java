package com.wenshuo.agent.analyzer.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @date 2024/09/06 14:09
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassBean {

    private String bh;

    private Date startTime;

    private String start;

    private Date endTime;

    private String end;

    @JSONField(name = "class")
    private String className;

    @JSONField(name = "methods")
    private String methods;

    private String version;

    private String day;

    private String startAndEnd;
}
