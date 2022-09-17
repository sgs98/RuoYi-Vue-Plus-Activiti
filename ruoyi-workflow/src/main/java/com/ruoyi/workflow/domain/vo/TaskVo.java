package com.ruoyi.workflow.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务视图
 * @author: gssong
 * @created: 2022/4/16 17:46
 */
@Data
public class TaskVo implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 流程任务id
     */
    private String id;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 流程执行id
     */
    private String executionId;

    /**
     * 办理人或候选人 （ 任务办理人: 如果是候选人则没有值，办理人才有）
     */
    private String assignee;

    /**
     * 办理人或候选人Id
     */
    private String assigneeId;
}
