package com.ruoyi.workflow.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * 统一管理activity提供的服务接口
 */
public class WorkflowService {
    @Resource
    public ObjectMapper objectMapper;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    public RepositoryService repositoryService;

    /**
     * 查询运行信息
     */
    @Autowired
    public RuntimeService runtimeService;

    /**
     * 查询任务信息
     */
    @Autowired
    public TaskService taskService;

    /**
     * 查询历史信息
     */
    @Autowired
    public HistoryService historyService;

}
