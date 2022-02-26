package com.ruoyi.workflow.domain.vo;

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
public class BackProcessVo implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("驳回的目标节点id")
    private String targetActivityId;

    @ApiModelProperty("审批意见")
    private String comment;
}
