package com.ruoyi.workflow.domain.vo;

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
public class ProcessInstFinishVo implements Serializable {

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
     * 流程结束时间
     */
    @ApiModelProperty("流程结束时间")
    private Date endTime;

    /**
     * 业务状态
     */
    @ApiModelProperty("业务状态")
    private String status;

}
