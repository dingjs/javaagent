package com.wenshuo.agent.analyzer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.wenshuo.agent.analyzer.bean.AgentLogDO;
import com.wenshuo.agent.analyzer.dao.LogFileDao;
import com.wenshuo.agent.analyzer.service.LogFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping({"/logFileAnalysis"})
public class CheckLogFileController {
    @Autowired
    private LogFileDao logFileDao;

    @Autowired
    private LogFileService logFileService;

    @RequestMapping({"/upload"})
    @ResponseBody
    public JSONObject uploadAndAnalysis(MultipartFile[] file) throws ParseException, IOException {
        JSONObject json = new JSONObject();
        boolean success = this.logFileService.saveFileInfoToDatabase(file[0], json);
        if (success) {
            json.put("code", Integer.valueOf(0));
        } else {
            json.put("code", Integer.valueOf(1));
        }
        return json;
    }

    @RequestMapping({"deleteFiles"})
    @ResponseBody
    public Object deleteFiles(String files) {
        JSONObject json = new JSONObject();
        try {
            this.logFileDao.deleteFiles(files);
            json.put("code", Integer.valueOf(0));
        } catch (Exception e) {
            json.put("code", Integer.valueOf(1));
        }
        return json;
    }

    @RequestMapping({"getDays"})
    @ResponseBody
    public String getDays() {
        List<String> list = this.logFileDao.findFileList();
        return JSONObject.toJSONString(list);
    }

    @RequestMapping({"toResultPage"})
    public String toResultPage(Model model, Integer page, String type, String order, String methodName, String className) {
        model.addAttribute("methodName", methodName);
        model.addAttribute("className", className);
        return "html/result";
    }

    @RequestMapping({"toClassResultPage"})
    public String toClassResultPage(Model model, Integer page, String type, String order, String className) {
        List<AgentLogDO> findOne = this.logFileService.getDataInfo(null, className);
        model.addAttribute("data", JSONObject.toJSONString(findOne));
        model.addAttribute("className", className);
        return "html/result";
    }

    @RequestMapping({"getTimes"})
    @ResponseBody
    public String getTimes(String files) {
        List<String> list = this.logFileDao.findTimeList(files);
        return JSONObject.toJSONString(list);
    }

    @RequestMapping({"getClassName"})
    @ResponseBody
    public String getClassName(String files, String startAndEndTime) {
        List<String> list = this.logFileDao.findClassNames(files, startAndEndTime);
        return JSONObject.toJSONString(list);
    }

    @RequestMapping({"getDetail"})
    @ResponseBody
    public String getDetail(String methodName, String className, Long value, Integer type, String days, String startAndEndTime) throws ParseException {
        AgentLogDO agentLogDo = new AgentLogDO();
        agentLogDo.setMethodName(methodName);
        agentLogDo.setClassName(className);
        agentLogDo.setDay(days);
        agentLogDo.setStartAndEnd(startAndEndTime);
        if (type.intValue() == 1) {
            agentLogDo.setTime(value);
        } else if (type.intValue() == 2) {
            agentLogDo.setCounter(value);
        } else if (type.intValue() == 3) {
            agentLogDo.setAvg(value);
        }
        Example<AgentLogDO> logFileExample = Example.of(agentLogDo);
        Optional<AgentLogDO> findOne = this.logFileDao.findOne(logFileExample);
        return JSONObject.toJSONString(findOne.get());
    }

    @RequestMapping({"getEchartResult"})
    @ResponseBody
    public String getEchartResult(String files) {
        Map<String, Object> result = new HashMap<>();
        List<String> timePointList = new ArrayList<>();
        List<String> y1Data = new ArrayList<>();
        List<String> y2Data = new ArrayList<>();
        List<String> y3Data = new ArrayList<>();
        List<Map> results = this.logFileDao.stasticByFile(files);
        for (Map statics : results) {
            String timePoint = statics.get("timeCut").toString().substring(10);
            Long count = (Long)statics.get("totalCounter");
            y1Data.add(count.toString());
            Long time = (Long)statics.get("totalTime");
            y2Data.add(time.toString());
            if (count.longValue() != 0L) {
                Long avg = Long.valueOf(time.longValue() / count.longValue());
                y3Data.add(avg.toString());
            } else {
                y3Data.add("-");
            }
            timePointList.add(timePoint);
        }
        result.put("xData", JSONObject.toJSONString(timePointList));
        result.put("y1Data", JSONObject.toJSONString(y1Data));
        result.put("y2Data", JSONObject.toJSONString(y2Data));
        result.put("y3Data", JSONObject.toJSONString(y3Data));
        return JSONObject.toJSONString(result);
    }

    @RequestMapping({"getDetailTableResult"})
    @ResponseBody
    public String getDetailTableResult(Integer page, String type, String order, String className, String methodName) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        AgentLogDO agentLogDo = new AgentLogDO();
        if (StringUtils.isNotEmpty(methodName))
            agentLogDo.setMethodName(methodName);
        if (StringUtils.isNotEmpty(className))
            agentLogDo.setClassName(className);
        Example<AgentLogDO> logFileExample = Example.of(agentLogDo);
        String sortString = "time";
        Pageable pageable = this.logFileService.getPageInfo(page, type, order, result, sortString);
        Page<AgentLogDO> pageInfo = this.logFileDao.findAll(logFileExample, pageable);
        result.put("code", Integer.valueOf(1));
        result.put("count", Long.valueOf(pageInfo.getTotalElements()));
        result.put("data", JSON.toJSON(pageInfo.getContent()));
        return JSONObject.toJSONString(result);
    }

    @RequestMapping({"getTableResult"})
    @ResponseBody
    public String getTableResult(String files, String startAndEndTime, Integer page, String type, String order, String className, String methodName) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        AgentLogDO agentLogDo = new AgentLogDO();
        agentLogDo.setFileName(files);
        if (!startAndEndTime.equals("null"))
            agentLogDo.setStartAndEnd(startAndEndTime);
        if (StringUtils.isNotEmpty(methodName))
            agentLogDo.setMethodName(methodName);
        if (StringUtils.isNotEmpty(className))
            agentLogDo.setClassName(className);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("className", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<AgentLogDO> logFileExample = Example.of(agentLogDo, matcher);
        String sortString = "avg";
        Pageable pageable = this.logFileService.getPageInfo(page, type, order, result, sortString);
        Page<AgentLogDO> pageInfo = this.logFileDao.findAll(logFileExample, pageable);
        result.put("code", Integer.valueOf(1));
        result.put("count", Long.valueOf(pageInfo.getTotalElements()));
        result.put("data", JSON.toJSON(pageInfo.getContent()));
        return JSONObject.toJSONString(result);
    }
}
