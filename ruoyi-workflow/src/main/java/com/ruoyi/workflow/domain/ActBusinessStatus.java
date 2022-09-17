package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务状态实体对象 act_business_status
 *
 * @author gssong
 * @date 2021-10-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_business_status")
public class ActBusinessStatus  extends BaseEntity{

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 业务ID
     */
    private String businessKey;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 状态
     */
    private String status;

    /**
     * 全类名
     */
    private String classFullName;

    /**
     * 挂起流程原因
     */
    private String suspendedReason;

}
