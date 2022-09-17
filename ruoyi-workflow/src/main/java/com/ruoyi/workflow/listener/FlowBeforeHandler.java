package com.ruoyi.workflow.listener;

/**
 * 任务结束前执行
 *
 * @author gssong
 * @date 2022 06-26
 */
public interface FlowBeforeHandler {
    /**
     * 任务结束前执行
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     */
    void handleProcess(String processInstanceId,String taskId);
}
