package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 动态表单对象 act_dynamic_form
 *
 * @author gssong
 * @date 2022-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_dynamic_form")
public class ActDynamicForm extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 表单key
     */
    private String formKey;
    /**
     * 表单名称
     */
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
