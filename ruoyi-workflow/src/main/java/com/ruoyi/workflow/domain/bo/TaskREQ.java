package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 待办任务查询
 * @author: gssong
 * @created: 2021/10/17 14:50
 */
@Data
@Validated
@EqualsAndHashCode(callSuper = true)
@ApiModel("任务查询")
public class TaskREQ extends PageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("流程定义id")
    private String processInstId;

    @ApiModelProperty(value = "委派人id")
    @NotBlank(message = "请选择委派人",groups = AddGroup.class)
    private String delegateUserId;

    @ApiModelProperty("委派人")
    private String delegateUserName;

    @ApiModelProperty(value = "择转办人id")
    private String transmitUserId;

    @ApiModelProperty("审批意见")
    private String comment;
}
