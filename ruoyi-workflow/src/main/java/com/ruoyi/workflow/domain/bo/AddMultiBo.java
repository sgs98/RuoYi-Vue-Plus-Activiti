package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import lombok.Data;

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
public class AddMultiBo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空",groups = AddGroup.class)
    private String taskId;

    /**
     * 加签人员id
     */
    @NotEmpty(message = "加签人员不能为空",groups = AddGroup.class)
    private List<Long> assignees;

    /**
     * 加签人员名称
     */
    @NotEmpty(message = "加签人员不能为空",groups = AddGroup.class)
    private List<String> assigneeNames;
}
