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
 * @description: 加签参数请求
 * @author: gssong
 * @created: 2022年4月15日13:01:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("加签参数请求")
public class AddMultiREQ implements Serializable {

    private static final long serialVersionUID=1L;

    @NotBlank(message = "任务ID不能为空",groups = AddGroup.class)
    @ApiModelProperty("任务id")
    private String taskId;

    @NotEmpty(message = "加签人员不能为空",groups = AddGroup.class)
    @ApiModelProperty("人员id")
    private List<Long> assignees;

    @NotEmpty(message = "加签人员不能为空",groups = AddGroup.class)
    @ApiModelProperty("人员名称")
    private List<String> assigneeNames;
}
