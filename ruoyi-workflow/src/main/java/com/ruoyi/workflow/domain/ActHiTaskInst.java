package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史任务信息对象 act_hi_taskinst
 *
 * @author gssong
 * @date 2022-03-06
 */
@Data
@TableName("act_hi_taskinst")
@ApiModel("历史任务信息对象")
public class ActHiTaskInst implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 流程任务id
     */
    @TableId(value = "ID_")
    private String id;

    /**
     * 了流程定义id
     */
    @TableField(value = "PROC_DEF_ID_")
    private String procDefId;

    /**
     * 节点id
     */
    @TableField(value = "TASK_DEF_KEY_")
    private String taskDefKey;

    /**
     * 流程实例id
     */
    @TableField(value = "PROC_INST_ID_")
    private String procInstId;

    /**
     * 流程执行id
     */
    @TableField(value = "EXECUTION_ID_")
    private String executionId;

    /**
     * 流程名称
     */
    @TableField(value = "NAME_")
    private String name;

    /**
     * 流程任务父级id
     */
    @TableField(value = "PARENT_TASK_ID_")
    private String parentTaskId;

    /**
     * 描述
     */
    @TableField(value = "DESCRIPTION_")
    private String description;

    /**
     * 委托人
     */
    @TableField(value = "OWNER_")
    private String owner;

    /**
     * 办理人
     */
    @TableField(value = "ASSIGNEE_")
    private String assignee;

    /**
     * 流程开始时间
     */
    @TableField(value = "START_TIME_")
    private Date startTime;

    /**
     * 认领时间
     */
    @TableField(value = "CLAIM_TIME_")
    private Date claimTime;

    /**
     * 流程结束时间
     */
    @TableField(value = "END_TIME_")
    private Date endTime;

    /**
     * 流程流转时长
     */
    @TableField(value = "DURATION_")
    private Long duration;

    /**
     * 流程删除原因
     */
    @TableField(value = "DELETE_REASON_")
    private String deleteReason;

    /**
     * 优先级
     */
    @TableField(value = "PRIORITY_")
    private Long priority;

    /**
     * 到期时间
     */
    @TableField(value = "DUE_DATE_")
    private Date dueDate;

    /**
     * 表单key
     */
    @TableField(value = "FORM_KEY_")
    private String formKey;

    /**
     * 类别
     */
    @TableField(value = "CATEGORY_")
    private String category;

    /**
     * 租户id
     */
    @TableField(value = "TENANT_ID_")
    private String tenantId;

}
