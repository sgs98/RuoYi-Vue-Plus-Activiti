package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;


import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务表单业务对象 act_business_form
 *
 * @author gssong
 * @date 2022-08-19
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ActBusinessFormBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 表单id
     */
    @NotNull(message = "表单id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long formId;

    /**
     * 表单key
     */
    @NotBlank(message = "表单key不能为空", groups = { AddGroup.class, EditGroup.class })
    private String formKey;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单内容
     */
    private String formText;

    /**
     * 表单数据
     */
    private String formValue;

    /**
     *单号
     */
    private String applyCode;

    /**
     *状态
     */
    private String status;


}
