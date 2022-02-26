package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 流程执行历史对象 act_hi_actinst
 *
 * @author gssong
 * @date 2022-01-23
 */
@Data
@Accessors(chain = true)
@TableName("act_hi_actinst")
public class ActHiActInst {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "ID_")
    private String id;
    /**
     * 流程定义id
     */
    private String procDefId;
    /**
     * 流程实例id
     */
    private String procInstId;
    /**
     * 流程执行id
     */
    private String executionId;
    /**
     * 流程节点id
     */
    @TableField(value = "ACT_ID_")
    private String actId;
    /**
     * 流程任务id
     */
    private String taskId;
    /**
     * 
     */
    private String callProcInstId;
    /**
     * 流程节点名称
     */
    private String actName;
    /**
     * 
     */
    private String actType;
    /**
     * 
     */
    private String assignee;
    /**
     * 
     */
    private Date startTime;
    /**
     * 
     */
    private Date endTime;
    /**
     * 
     */
    private Long duration;
    /**
     * 
     */
    private String deleteReason;
    /**
     * 
     */
    private String tenantId;

}
