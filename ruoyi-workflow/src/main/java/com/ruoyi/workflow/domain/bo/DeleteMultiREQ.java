package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 减签参数请求
 * @author: gssong
 * @created: 2022年4月16日16:01:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("减签参数请求")
public class DeleteMultiREQ implements Serializable {

    private static final long serialVersionUID=1L;

    @NotBlank(message = "任务ID不能为空",groups = AddGroup.class)
    @ApiModelProperty("当前任务id")
    private String taskId;

    @NotEmpty(message = "减签人员不能为空",groups = AddGroup.class)
    @ApiModelProperty("减签任务ID")
    private List<String> taskIds;

    @NotEmpty(message = "减签人员不能为空",groups = AddGroup.class)
    @ApiModelProperty("减签执行ID")
    private List<String> executionIds;

    @ApiModelProperty("人员id")
    private List<Long> assigneeIds;
}
