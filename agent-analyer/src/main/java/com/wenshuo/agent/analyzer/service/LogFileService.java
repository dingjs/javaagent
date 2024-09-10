package com.wenshuo.agent.analyzer.service;

import com.alibaba.fastjson.JSONObject;
import com.wenshuo.agent.analyzer.bean.AgentLogDO;
import com.wenshuo.agent.analyzer.bean.ClassBean;
import com.wenshuo.agent.analyzer.bean.MethodBean;
import com.wenshuo.agent.analyzer.dao.LogFileDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @date 2024/09/06 14:25
 **/
@Service
public class LogFileService {
    private static Log log = LogFactory.getLog(LogFileService.class);

    private static final String PATTERNS = "yyyy-MM-dd hh:mm:ss";

    @Autowired
    private LogFileDao logFileDao;

    public boolean saveFileInfoToDatabase(MultipartFile file, JSONObject json) throws IOException, ParseException {
        long start = System.currentTimeMillis();
        boolean success = validateAgentLogFile(file, json);
        if (!success)
            return false;
        String fileName = file.getOriginalFilename();
        String agentLogStr = readAgentLog(file);
        List<ClassBean> agentLogArray = JSONObject.parseArray(agentLogStr, ClassBean.class);
        long parseArrayEnd = System.currentTimeMillis();
        log.info("转换【" + fileName + "】成ClassBean成功，耗时【" + (parseArrayEnd - start) + "】毫秒，共有【" + agentLogArray.size() + "】条");
        List<AgentLogDO> agentLogDOList = new ArrayList<>(agentLogArray.size() * 10);
        for (ClassBean classBean : agentLogArray) {
            List<MethodBean> methodList = JSONObject.parseArray(classBean.getMethods(), MethodBean.class);
            if (!CollectionUtils.isEmpty(methodList))
                for (MethodBean methodBean : methodList) {
                    AgentLogDO agentLogDo = buildAgentLogDO(fileName, classBean, methodBean);
                    agentLogDOList.add(agentLogDo);
                }
        }
        long convertToBeanEnd = System.currentTimeMillis();
        log.info("转换【" + fileName + "】成功，耗时【" + (convertToBeanEnd - start) + "】毫秒");
        if (!CollectionUtils.isEmpty(agentLogDOList)) {
            this.logFileDao.saveAll(agentLogDOList);
            this.logFileDao.flush();
        }
        long end = System.currentTimeMillis();
        log.info("文件【" + fileName + "】对应的数据保存到数据库成功，耗时【" + (end - convertToBeanEnd) + "】毫秒");
        return true;
    }

    private AgentLogDO buildAgentLogDO(String fileName, ClassBean classBean, MethodBean methodBean) throws ParseException {
        AgentLogDO agentLogDo = new AgentLogDO();
        agentLogDo.setMethodName(methodBean.getMethodName());
        agentLogDo.setCounter(methodBean.getCounter());
        agentLogDo.setTime(methodBean.getTime());
        if (methodBean.getAvg() != null) {
            agentLogDo.setAvg(methodBean.getAvg());
        } else {
            agentLogDo.setAvg(Long.valueOf(methodBean.getTime().longValue() / methodBean.getCounter().longValue()));
        }
        agentLogDo.setMin(methodBean.getMin());
        agentLogDo.setMedian(methodBean.getMedian());
        agentLogDo.setMax(methodBean.getMax());
        agentLogDo.setTh90Pct(methodBean.getTh90Pct());
        agentLogDo.setTh95Pct(methodBean.getTh95Pct());
        agentLogDo.setTh99Pct(methodBean.getTh99Pct());
        agentLogDo.setClassName(classBean.getClassName());
        agentLogDo.setDay(classBean.getStart().substring(0, 10));
        agentLogDo.setStartAndEnd(classBean.getStart() + " 至 " + classBean.getEnd());
        agentLogDo.setStartTime(DateUtils.parseDate(classBean.getStart(), new String[] { "yyyy-MM-dd hh:mm:ss" }));
        agentLogDo.setTimeCut(classBean.getStart().substring(0, 13));
        agentLogDo.setEndTime(DateUtils.parseDate(classBean.getEnd(), new String[] { "yyyy-MM-dd hh:mm:ss" }));
        agentLogDo.setFileName(fileName);
        return agentLogDo;
    }

    private String readAgentLog(MultipartFile file) throws IOException {
        try (InputStream input = file.getInputStream()) {
            return IOUtils.toString(input, "UTF-8");
        }
    }

    private boolean validateAgentLogFile(MultipartFile file, JSONObject json) {
        if (file.getSize() == 0L) {
            json.put("code", Integer.valueOf(1));
            json.put("message", "上传的文档不存在");
            return false;
        }
        String fileName = file.getOriginalFilename();
        AgentLogDO agentLogDoForQuery = new AgentLogDO();
        agentLogDoForQuery.setFileName(fileName);
        Example<AgentLogDO> example = Example.of(agentLogDoForQuery);
        if (this.logFileDao.exists(example)) {
            String message = "此文档已存在，文档名为" + fileName;
            log.info(message);
            json.put("message", message);
            return false;
        }
        return true;
    }

    public List<AgentLogDO> getDataInfo(String methodName, String className) {
        AgentLogDO agentLogDo = new AgentLogDO();
        agentLogDo.setClassName(className);
        if (StringUtils.isNotEmpty(methodName))
            agentLogDo.setMethodName(methodName);
        Example<AgentLogDO> logFileExample = Example.of(agentLogDo);
        String sortString = "time";
        Sort sort = Sort.by(Sort.Direction.DESC, new String[] { sortString });
        return this.logFileDao.findAll(logFileExample, sort);
    }

    public Pageable getPageInfo(Integer page, String type, String order, Map<String, Object> result, String sortString) {
        PageRequest pageRequest;
        if (type != null)
            sortString = type;
        Sort sort = Sort.by(Sort.Direction.DESC, new String[] { sortString });
        if (order != null && order.contains("asc"))
            sort = Sort.by(Sort.Direction.ASC, new String[] { sortString });
        Pageable pageable = null;
        if (page != null && page.intValue() > 1) {
            pageRequest = PageRequest.of(page.intValue() - 1, 100, sort);
            result.put("pages", page);
        } else {
            pageRequest = PageRequest.of(0, 100, sort);
            result.put("pages", Integer.valueOf(1));
        }
        return (Pageable)pageRequest;
    }
}
