package com.wenshuo.agent.analyzer.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.wenshuo.agent.analyzer.bean.AgentLogDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LogFileDao extends JpaRepository<AgentLogDO, UUID> {
    @Query("select t from  AgentLogDO t where t.className  = ?1 and t.methodName  = ?2 and t.startTime = ?3 and t.endTime =?4")
    List<AgentLogDO> findByClassName(String paramString1, String paramString2, Date paramDate1, Date paramDate2);

    @Query("select startAndEnd from AgentLogDO t where t.fileName = ?1 group by startAndEnd order by startAndEnd")
    List<String> findTimeList(String paramString);

    @Query("select timeCut from AgentLogDO t where t.fileName = ?1 group by timeCut order by timeCut")
    List<String> findTimeCutList(String paramString);

    @Query("SELECT fileName FROM AgentLogDO group by fileName")
    List<String> findFileList();

    @Modifying
    @Query("delete from  AgentLogDO t where t.fileName  = ?1")
    void deleteFiles(String paramString);

    @Query("select className from  AgentLogDO t where t.fileName  = ?1 and t.startAndEnd  = ?2 ")
    List<String> findClassNames(String paramString1, String paramString2);

    @Query("select sum(counter) from  AgentLogDO t where t.fileName  = ?1  and t.timeCut  = ?2 ")
    Long selectCountInfo(String paramString1, String paramString2);

    @Query("select sum(time) from  AgentLogDO t where t.fileName  = ?1  and t.timeCut  = ?2 ")
    Long selectTimeInfo(String paramString1, String paramString2);

    @Query("select t.timeCut as timeCut,sum(time) as totalTime,sum(counter) as totalCounter from  AgentLogDO t where t.fileName  = ?1  group by timeCut order by timeCut ")
    List<Map> stasticByFile(String paramString);
}
