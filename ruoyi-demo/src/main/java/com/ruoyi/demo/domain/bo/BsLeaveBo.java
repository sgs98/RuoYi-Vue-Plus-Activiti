package com.ruoyi.demo.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("请假业务业务对象")
public class BsLeaveBo extends BaseEntity {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID", required = true)
    @NotBlank(message = "主键ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String id;

    /**
     * 申请人用户名
     */
    @ApiModelProperty(value = "申请人用户名", required = true)
    @NotBlank(message = "申请人用户名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String username;

    /**
     * 请假时长，单位：天
     */
    @ApiModelProperty(value = "请假时长，单位：天", required = true)
    @NotNull(message = "请假时长，单位：天不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal duration;

    /**
     * 工作委托人
     */
    @ApiModelProperty(value = "工作委托人", required = true)
    @NotBlank(message = "工作委托人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String principal;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", required = true)
    @NotBlank(message = "联系电话不能为空", groups = { AddGroup.class, EditGroup.class })
    private String contactPhone;

    /**
     * 请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他
     */
    @ApiModelProperty(value = "请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他", required = true)
    @NotNull(message = "请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer leaveType;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", required = true)
    @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因", required = true)
    @NotBlank(message = "请假原因不能为空", groups = { AddGroup.class, EditGroup.class })
    private String leaveReason;

    /**
     * 请假开始时间
     */
    @ApiModelProperty(value = "请假开始时间", required = true)
    @NotNull(message = "请假开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date startDate;

    /**
     * 请假开始时间
     */
    @ApiModelProperty(value = "请假开始时间", required = true)
    @NotNull(message = "请假开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date endDate;


}
