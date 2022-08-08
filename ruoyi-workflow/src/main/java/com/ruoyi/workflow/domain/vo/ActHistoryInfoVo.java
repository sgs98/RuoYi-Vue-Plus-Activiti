package com.ruoyi.workflow.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程审批记录视图
 * @author: gssong
 * @created: 2021/10/16 15:36
 */
@Data
@ApiModel("流程审批记录视图")
public class ActHistoryInfoVo implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private String id;
    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String name;
    /**
     * 流程实例id
     */
    @ApiModelProperty("流程实例id")
    private String processInstanceId;
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private Date startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private Date endTime;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;
    /**
     * 办理人id
     */
    @ApiModelProperty("办理人id")
    private String assignee;

    /**
     * 办理人名称
     */
    @ApiModelProperty("办理人名称")
    private String nickName;

    /**
     * 办理人id
     */
    @ApiModelProperty("办理人id")
    private String owner;

    /**
     * 审批信息id
     */
    @ApiModelProperty("审批信息id")
    private String commentId;

    /**
     * 审批信息
     */
    @ApiModelProperty("审批信息")
    private String comment;
}
