package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务规则对象 act_full_class
 *
 * @author ruoyi
 * @date 2021-12-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_full_class")
@ApiModel("业务规则对象")
public class ActFullClass extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 全类名
     */
    private String fullClass;
    /**
     * 方法名
     */
    private String method;
    /**
     * 备注
     */
    private String remark;

}
