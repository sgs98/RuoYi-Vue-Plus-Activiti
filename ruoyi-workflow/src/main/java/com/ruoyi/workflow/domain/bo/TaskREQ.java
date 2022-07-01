package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务请求
 * @author: gssong
 * @created: 2021/10/17 14:50
 */
@Data
@Validated
@EqualsAndHashCode(callSuper = true)
@ApiModel("任务请求")
public class TaskREQ extends PageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("流程定义id")
    private String processInstId;
}
