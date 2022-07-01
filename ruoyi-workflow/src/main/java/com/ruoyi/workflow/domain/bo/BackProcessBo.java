package com.ruoyi.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 驳回请求
 * @author: gssong
 * @created: 2021/11/06 22:22
 */
@Data
@ApiModel("驳回请求")
public class BackProcessBo implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "任务id",required = true)
    private String taskId;

    @ApiModelProperty(value = "驳回的目标节点id",required = true)
    private String targetActivityId;

    @ApiModelProperty(value = "审批意见")
    private String comment;

    @ApiModelProperty("消息对象")
    private SendMessage sendMessage;
}
