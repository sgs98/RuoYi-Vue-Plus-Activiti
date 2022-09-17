package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;


import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 流程定义设置业务对象 act_process_def_Setting
 *
 * @author gssong
 * @date 2022-08-28
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ActProcessDefSettingBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 流程定义id
     */
    @NotBlank(message = "流程定义id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String processDefinitionId;

    /**
     * 流程定义key
     */
    @NotBlank(message = "流程定义key不能为空", groups = { AddGroup.class, EditGroup.class })
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    @NotBlank(message = "流程定义名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String processDefinitionName;

    /**
     * 业务类型，0动态表单，1业务单据
     */
    @NotNull(message = "业务类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer businessType;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 表单id
     */
    private Long formId;

    /**
     * 表单key
     */
    private String formKey;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 动态表单中参数id,多个用英文逗号隔开
     */
    private String formVariable;

    /**
     * 备注
     */
    private String remark;

    /**
     * 主键集合
     */
    private List<Long> ids;



}
