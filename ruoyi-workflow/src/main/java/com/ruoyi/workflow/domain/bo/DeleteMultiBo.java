package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import lombok.Data;

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
public class DeleteMultiBo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空",groups = AddGroup.class)
    private String taskId;

    /**
     * 减签人员
     */
    @NotEmpty(message = "减签人员不能为空",groups = AddGroup.class)
    private List<String> taskIds;

    /**
     * 执行id
     */
    @NotEmpty(message = "减签人员不能为空",groups = AddGroup.class)
    private List<String> executionIds;

    /**
     * 人员id
     */
    private List<Long> assigneeIds;

    /**
     * 人员名称
     */
    @NotEmpty(message = "减签人员不能为空",groups = AddGroup.class)
    private List<String> assigneeNames;
}
