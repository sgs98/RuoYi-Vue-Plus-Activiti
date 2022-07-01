package com.ruoyi.workflow.listener;

import com.ruoyi.common.utils.spring.SpringUtils;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestBean implements  FlowBeforeHandler{
    private static Logger logger = LoggerFactory.getLogger(TestBean.class);

    @Override
    public void handleProcess(String processInstanceId, String taskId) {
        TaskService taskService = SpringUtils.getBean(TaskService.class);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        logger.info("taskName:"+task.getName());
    }
}
