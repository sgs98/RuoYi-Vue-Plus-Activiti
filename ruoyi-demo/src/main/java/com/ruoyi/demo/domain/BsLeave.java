package com.ruoyi.demo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 请假业务对象 bs_leave
 *
 * @author gssong
 * @date 2022-10-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bs_leave")
public class BsLeave extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private String id;
    /**
     * 申请人用户名
     */
    private String username;
    /**
     * 请假时长，单位：天
     */
    private BigDecimal duration;
    /**
     * 工作委托人
     */
    private String principal;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他
     */
    private Integer leaveType;
    /**
     * 标题
     */
    private String title;
    /**
     * 请假原因
     */
    private String leaveReason;
    /**
     * 请假开始时间
     */
    private Date startDate;
    /**
     * 请假开始时间
     */
    private Date endDate;

}
