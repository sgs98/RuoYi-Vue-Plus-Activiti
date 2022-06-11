package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 方法参数对象 act_full_class_param
 *
 * @author gssong
 * @date 2021-12-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_full_class_param")
@ApiModel("方法参数对象")
public class ActFullClassParam extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 参数类型
     */
    @NotBlank(message = "参数类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String paramType;
    /**
     * 参数
     */
    @NotBlank(message = "参数不能为空", groups = { AddGroup.class, EditGroup.class })
    private String param;
    /**
     * 备注
     */
    private String remark;
    /**
     * 业务规则id
     */
    @NotNull(message = "业务规则id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long fullClassId;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer orderNo;

}
