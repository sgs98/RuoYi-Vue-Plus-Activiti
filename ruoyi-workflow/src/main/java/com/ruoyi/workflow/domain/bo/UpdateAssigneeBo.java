package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 修改办理人
 * @author: gssong
 * @created: 2022/04/10 14:50
 */
@Data
public class UpdateAssigneeBo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 任务id
     */
    @NotNull(message = "任务id不能为空",groups = {AddGroup.class })
    private List<String> taskIdList;

    /**
     * 用户id
     */
    @NotBlank(message = "用户id不能为空",groups = {AddGroup.class })
    private String userId;

}
