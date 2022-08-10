package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.workflow.domain.ActBusinessRuleParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务规则业务对象 act_business_rule
 *
 * @author gssong
 * @date 2021-12-16
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("业务规则业务对象")
public class ActBusinessRuleBo extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空", groups = {EditGroup.class })
    private Long id;

    /**
     * 全类名
     */
    @ApiModelProperty(value = "bean名称", required = true)
    @NotBlank(message = "bean名称为空", groups = { AddGroup.class, EditGroup.class })
    private String beanName;

    /**
     * 方法名
     */
    @ApiModelProperty(value = "方法名", required = true)
    @NotBlank(message = "方法名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String method;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数", required = true)
    private String param;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private List<ActBusinessRuleParam> businessRuleParams;

}
