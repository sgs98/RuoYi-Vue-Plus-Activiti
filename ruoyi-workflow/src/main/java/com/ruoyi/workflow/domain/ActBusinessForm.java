package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务表单对象 act_business_form
 *
 * @author gssong
 * @date 2022-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_business_form")
public class ActBusinessForm extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
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

}
