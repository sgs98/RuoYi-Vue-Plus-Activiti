package com.ruoyi.workflow.activiti.factory;

import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 统一管理activity提供的服务接口
 */
public class WorkflowService {


    /**
     * 流程引擎
     */
    @Autowired
    public  ProcessEngine processEngine;

    /**
     * 流程仓库服务类
     */
    @Autowired
    public  RepositoryService repositoryService;

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
