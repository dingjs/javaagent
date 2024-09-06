package com.wenshuo.agent.analyzer.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @date 2024/09/06 14:05
 **/
@Entity
@Table(name = "T_AGENT_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentLogDO {

    @Id
    @GeneratedValue
    private UUID id;

    private Date startTime;

    private Date endTime;

    @JSONField(name = "class")
    private String className;

    private String methodName;

    private Long counter;

    private Long time;

    private Long avg;

    private Long min;

    private Long max;

    private Long median;

    private Long th90Pct;

    private Long th95Pct;

    private Long th99Pct;

    private String version;

    private String day;

    private String startAndEnd;

    private String fileName;

    private String timeCut;
}
