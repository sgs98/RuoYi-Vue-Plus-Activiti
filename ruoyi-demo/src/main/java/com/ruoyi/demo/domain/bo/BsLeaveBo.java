package com.ruoyi.demo.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 请假业务业务对象 bs_leave
 *
 * @author gssong
 * @date 2022-10-10
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class BsLeaveBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotBlank(message = "主键ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 申请人用户名
     */
    @NotBlank(message = "申请人用户名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String username;

    /**
     * 请假时长，单位：天
     */
    @NotNull(message = "请假时长，单位：天不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal duration;

    /**
     * 工作委托人
     */
    @NotBlank(message = "工作委托人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String principal;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空", groups = { AddGroup.class, EditGroup.class })
    private String contactPhone;

    /**
     * 请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他
     */
    @NotNull(message = "请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer leaveType;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 请假原因
     */
    @NotBlank(message = "请假原因不能为空", groups = { AddGroup.class, EditGroup.class })
    private String leaveReason;

    /**
     * 请假开始时间
     */
    @NotNull(message = "请假开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date startDate;

    /**
     * 请假开始时间
     */
    @NotNull(message = "请假开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date endDate;


}
