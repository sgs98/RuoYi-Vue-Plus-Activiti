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
 * @description: 流程定义视图
 * @author: gssong
 * @created: 2021/10/07 11:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流程定义视图")
public class ProcessDefinitionVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 流程定义id
     */
    @ApiModelProperty("流程定义id")
    private String id;

    /**
     * 流程定义名称
     */
    @ApiModelProperty("流程定义名称")
    private String name;

    /**
     * 流程定义标识key
     */
    @ApiModelProperty("流程定义标识key")
    private String key;

    /**
     * 流程定义版本
     */
    @ApiModelProperty("流程定义版本")
    private int version;

    /**
     * 流程定义挂起或激活 1激活 2挂起
     */
    @ApiModelProperty("流程定义挂起或激活")
    private int suspensionState;

    /**
     * 流程xml名称
     */
    @ApiModelProperty("流程xml名称")
    private String resourceName;

    /**
     * 流程图片名称
     */
    @ApiModelProperty("流程图片名称")
    private String diagramResourceName;

    /**
     * 流程部署id
     */
    @ApiModelProperty("流程部署id")
    private String deploymentId;

    /**
     * 流程部署时间
     */
    @ApiModelProperty("流程部署时间")
    private Date deploymentTime;

    /**
     * 挂起或激活原因
     */
    @ApiModelProperty("挂起或激活原因")
    private String description;

}
