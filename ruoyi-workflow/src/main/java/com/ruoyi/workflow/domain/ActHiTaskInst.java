package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 历史任务信息对象 act_hi_taskinst
 *
 * @author gssong
 * @date 2022-03-06
 */
@Data
@TableName("act_hi_taskinst")
public class ActHiTaskInst {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "ID_")
    private String id;
    /**
     *
     */
    @TableField(value = "PROC_DEF_ID_")
    private String procDefId;
    /**
     *
     */
    @TableField(value = "TASK_DEF_KEY_")
    private String taskDefKey;
    /**
     *
     */
    @TableField(value = "PROC_INST_ID_")
    private String procInstId;
    /**
     *
     */
    @TableField(value = "EXECUTION_ID_")
    private String executionId;
    /**
     *
     */
    @TableField(value = "NAME_")
    private String name;
    /**
     *
     */
    @TableField(value = "PARENT_TASK_ID_")
    private String parentTaskId;
    /**
     *
     */
    @TableField(value = "DESCRIPTION_")
    private String description;
    /**
     *
     */
    @TableField(value = "OWNER_")
    private String owner;
    /**
     *
     */
    @TableField(value = "ASSIGNEE_")
    private String assignee;
    /**
     *
     */
    @TableField(value = "START_TIME_")
    private Date startTime;
    /**
     *
     */
    @TableField(value = "CLAIM_TIME_")
    private Date claimTime;
    /**
     *
     */
    @TableField(value = "END_TIME_")
    private Date endTime;
    /**
     *
     */
    @TableField(value = "DURATION_")
    private Long duration;
    /**
     *
     */
    @TableField(value = "DELETE_REASON_")
    private String deleteReason;
    /**
     *
     */
    @TableField(value = "PRIORITY_")
    private Long priority;
    /**
     *
     */
    @TableField(value = "DUE_DATE_")
    private Date dueDate;
    /**
     *
     */
    @TableField(value = "FORM_KEY_")
    private String formKey;
    /**
     *
     */
    @TableField(value = "CATEGORY_")
    private String category;
    /**
     *
     */
    @TableField(value = "TENANT_ID_")
    private String tenantId;

}
