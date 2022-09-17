package com.ruoyi.demo.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;



/**
 * 请假业务视图对象 bs_leave
 *
 * @author gssong
 * @date 2022-01-11
 */
@Data
@ExcelIgnoreUnannotated
public class BsLeaveVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private String id;

    /**
     * 申请人用户名
     */
    @ExcelProperty(value = "申请人用户名")
    private String username;

    /**
     * 请假时长，单位：天
     */
    @ExcelProperty(value = "请假时长，单位：天")
    private BigDecimal duration;

    /**
     * 工作委托人
     */
    @ExcelProperty(value = "工作委托人")
    private String principal;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他
     */
    @ExcelProperty(value = "请假类型：1病假，2事假，3年假，4婚假，5产假，6丧假，7探亲，8调休，9其他")
    private Integer leaveType;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 请假原因
     */
    @ExcelProperty(value = "请假原因")
    private String leaveReason;

    /**
     * 请假开始时间
     */
    @ExcelProperty(value = "请假开始时间")
    private Date startDate;

    /**
     * 请假开始时间
     */
    @ExcelProperty(value = "请假开始时间")
    private Date endDate;

    /**
     * 流程状态
     */
    @ExcelProperty(value = "流程状态")
    private ActBusinessStatus actBusinessStatus;

}
