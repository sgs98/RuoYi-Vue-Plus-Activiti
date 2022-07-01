package com.ruoyi.demo.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 测试单表视图对象 test_demo
 *
 * @author Lion Li
 * @date 2021-07-26
 */
@Data
@ApiModel("测试单表视图对象")
@ExcelIgnoreUnannotated
public class TestDemoVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 部门id
     */
    @ExcelProperty(value = "部门id")
    @ApiModelProperty("部门id")
    private Long deptId;

    /**
     * 用户id
     */
    @ExcelProperty(value = "用户id")
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    @ApiModelProperty("排序号")
    private Integer orderNum;

    /**
     * key键
     */
    @ExcelProperty(value = "key键")
    @ApiModelProperty("key键")
    private String testKey;

    /**
     * 值
     */
    @ExcelProperty(value = "值")
    @ApiModelProperty("值")
    private String value;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 创建人
     */
    @ExcelProperty(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 更新人
     */
    @ExcelProperty(value = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;


}
