package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * 业务状态实体对象 act_business_status
 *
 * @author gssong
 * @date 2021-10-10
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("act_business_status")
public class ActBusinessStatus implements Serializable {

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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
