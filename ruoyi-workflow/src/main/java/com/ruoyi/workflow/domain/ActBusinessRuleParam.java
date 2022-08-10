package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 方法参数对象 act_business_rule_param
 *
 * @author gssong
 * @date 2021-12-17
 */
@Data
@TableName("act_business_rule_param")
@ApiModel("方法参数对象")
public class ActBusinessRuleParam{

    private static final long serialVersionUID=1L;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 参数
     */
    private String param;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    private Integer orderNo;

}
