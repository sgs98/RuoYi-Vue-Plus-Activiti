package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 流程执行对象 act_ru_execution
 *
 * @author gssong
 * @date 2022-02-08
 */
@Data
@Accessors(chain = true)
@TableName("act_ru_execution")
public class ActRuExecution{

    private static final long serialVersionUID=1L;

    /**
     * 
     */
    @TableId(value = "ID_")
    private String id;
    /**
     * 
     */
    @TableField(value = "REV_")
    private Long rev;
    /**
     * 
     */
    @TableField(value = "PROC_INST_ID_")
    private String procInstId;
    /**
     * 
     */
    @TableField(value = "BUSINESS_KEY_")
    private String businessKey;
    /**
     * 
     */
    @TableField(value = "PARENT_ID_")
    private String parentId;
    /**
     * 
     */
    @TableField(value = "PROC_DEF_ID_")
    private String procDefId;
    /**
     * 
     */
    @TableField(value = "SUPER_EXEC_")
    private String superExec;
    /**
     * 
     */
    @TableField(value = "ROOT_PROC_INST_ID_")
    private String rootProcInstId;
    /**
     * 
     */
    @TableField(value = "ACT_ID_")
    private String actId;
    /**
     * 
     */
    @TableField(value = "IS_ACTIVE_")
    private Integer isActive;
    /**
     * 
     */
    @TableField(value = "IS_CONCURRENT_")
    private Integer isConcurrent;
    /**
     * 
     */
    @TableField(value = "IS_SCOPE_")
    private Integer isScope;
    /**
     * 
     */
    @TableField(value = "IS_EVENT_SCOPE_")
    private Integer isEventScope;
    /**
     * 
     */
    @TableField(value = "IS_MI_ROOT_")
    private Integer isMiRoot;
    /**
     * 
     */
    @TableField(value = "SUSPENSION_STATE_")
    private Long suspensionState;
    /**
     * 
     */
    @TableField(value = "CACHED_ENT_STATE_")
    private Long cachedEntState;
    /**
     * 
     */
    @TableField(value = "TENANT_ID_")
    private String tenantId;
    /**
     * 
     */
    @TableField(value = "NAME_")
    private String name;
    /**
     * 
     */
    @TableField(value = "START_TIME_")
    private Date startTime;
    /**
     * 
     */
    @TableField(value = "START_USER_ID_")
    private String startUserId;
    /**
     * 
     */
    @TableField(value = "LOCK_TIME_")
    private Date lockTime;
    /**
     * 
     */
    @TableField(value = "IS_COUNT_ENABLED_")
    private Integer isCountEnabled;
    /**
     * 
     */
    @TableField(value = "EVT_SUBSCR_COUNT_")
    private Long evtSubscrCount;
    /**
     * 
     */
    @TableField(value = "TASK_COUNT_")
    private Long taskCount;
    /**
     * 
     */
    @TableField(value = "JOB_COUNT_")
    private Long jobCount;
    /**
     * 
     */
    @TableField(value = "TIMER_JOB_COUNT_")
    private Long timerJobCount;
    /**
     * 
     */
    @TableField(value = "SUSP_JOB_COUNT_")
    private Long suspJobCount;
    /**
     * 
     */
    @TableField(value = "DEADLETTER_JOB_COUNT_")
    private Long deadletterJobCount;
    /**
     * 
     */
    @TableField(value = "VAR_COUNT_")
    private Long varCount;
    /**
     * 
     */
    @TableField(value = "ID_LINK_COUNT_")
    private Long idLinkCount;

}
