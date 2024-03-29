package com.ruoyi.workflow.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务视图
 * @author: gssong
 * @created: 2021/10/17 14:54
 */
@Data
public class TaskFinishVo implements Serializable {

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
     * 启动时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 流程执行id
     */
    private String executionId;
    /**
     * 流程定义id
     */
    private String processDefinitionId;
    /**
     * 办理人或候选人 （ 任务办理人: 如果是候选人则没有值，办理人才有）
     */
    private String assignee;

    /**
     * 办理人或候选人 （ 任务办理人: 如果是候选人则没有值，办理人才有）
     */
    private Long assigneeId;
    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义版本
     */
    private Integer version;

    /**
     * 父级任务id
     */
    private String parentTaskId;
}
