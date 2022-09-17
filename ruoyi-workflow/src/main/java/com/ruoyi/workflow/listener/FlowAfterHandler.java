package com.ruoyi.workflow.listener;

/**
 * 任务结束后执行
 *
 * @author gssong
 * @date 2022 06-26
 */
public interface FlowAfterHandler {
    /**
     * 任务结束后执行
     * @param processInstanceId 流程实例id
     */
    void handleProcess(String processInstanceId);
}
