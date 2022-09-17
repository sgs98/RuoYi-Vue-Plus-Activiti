package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @program: ruoyi-vue-plus
 * @description: 委托请求对象
 * @author: gssong
 * @created: 2022-06-18
 */
@Data
public class DelegateBo {

    @NotBlank(message = "任务id不能为空",groups = AddGroup.class)
    private String taskId;

    @NotBlank(message = "委派人id不能为空",groups = AddGroup.class)
    private String delegateUserId;

    @NotBlank(message = "委派人不能为空",groups = AddGroup.class)
    private String delegateUserName;

    private String comment;

    private SendMessage sendMessage;
}
