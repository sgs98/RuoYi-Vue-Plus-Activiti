package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;


import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 动态表单业务对象 act_dynamic_form
 *
 * @author gssong
 * @date 2022-08-11
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ActDynamicFormBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 表单key
     */
    @NotBlank(message = "表单key不能为空", groups = { AddGroup.class, EditGroup.class })
    private String formKey;

    /**
     * 表单名称
     */
    @NotBlank(message = "表单名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String formName;

    /**
     * 表单数据
     */
    private String formDesignerText;

    /**
     * 表单备注
     */
    private String formRemark;

    /**
     * 表单状态
     */
    private Boolean status;

    /**
     * 排序
     */
    private Integer orderNo;
}
