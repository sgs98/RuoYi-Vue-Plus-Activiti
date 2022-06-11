package com.ruoyi.workflow.domain.vo;

import com.alibaba.excel.util.DateUtils;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: ruoyi-vue-plus
 * @description: 查询正在运行中的实例
 * @author: gssong
 * @created: 2021/10/16 19:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("查询正在运行中的实例")
public class ProcessInstRunningVo implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 流程实例id
     */
    @ApiModelProperty("流程实例id")
    private String processInstanceId;
    /**
     * 流程定义名称
     */
    @ApiModelProperty("流程定义名称")
    private String name;
    /**
     * 流程定义key
     */
    @ApiModelProperty("流程定义key")
    private String processDefinitionKey;
    /**
     * 流程定义版本
     */
    @ApiModelProperty("流程定义版本")
    private Integer processDefinitionVersion;
    /**
     * 流程发起人ID
     */
    @ApiModelProperty("流程发起人ID")
    private String startUserId;
    /**
     * 流程发起人
     */
    @ApiModelProperty("流程发起人")
    private String startUserNickName;
    /**
     * 流程状态 挂起或激活
     */
    @ApiModelProperty("流程状态")
    private String isSuspended;
    /**
     * 流程关联的业务id
     */
    @ApiModelProperty("流程关联的业务id")
    private String businessKey;
    /**
     * 流程启动时间
     */
    @ApiModelProperty("流程启动时间")
    private Date startTime;

    /**
     * 当前办理人
     */
    @ApiModelProperty("当前办理人")
    private String currTaskInfo;


    /**
     * 当前办理人ID
     */
    @ApiModelProperty("当前办理人ID")
    private String currTaskInfoId;

    /**
     * 流程状态
     */
    @ApiModelProperty("流程状态")
    private ActBusinessStatus actBusinessStatus;

}
