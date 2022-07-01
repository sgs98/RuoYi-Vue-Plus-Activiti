package com.ruoyi.workflow.listener;

/**
 * 任务结束前执行
 *
 * @author gssong
 * @date 2022 06-26
 */
public interface FlowBeforeHandler {
    void handleProcess(String processInstanceId,String taskId);
}
