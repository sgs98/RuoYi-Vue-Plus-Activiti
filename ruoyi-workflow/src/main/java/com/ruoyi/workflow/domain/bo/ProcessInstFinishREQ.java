package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 已完成流程查询
 * @author: gssong
 * @created: 2021/10/16 19:42
 */
@Data
@ApiModel("运行中流程查询")
public class ProcessInstFinishREQ extends PageEntity implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty("流程名称")
    private String name;

    @ApiModelProperty("任务发起人")
    private String startUserId;
}
