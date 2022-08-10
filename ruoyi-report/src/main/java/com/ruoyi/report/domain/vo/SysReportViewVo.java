package com.ruoyi.report.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;



/**
 * 报表查看视图对象 sys_report_view
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@Data
@ApiModel("报表查看视图对象")
@ExcelIgnoreUnannotated
public class SysReportViewVo {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    @ApiModelProperty("id")
    private Long id;

    /**
     * 报表id
     */
    @ExcelProperty(value = "报表id")
    @ApiModelProperty("报表id")
    private String reportId;

    /**
     * 报表名称
     */
    @ExcelProperty(value = "报表名称")
    @ApiModelProperty("报表名称")
    private String reportName;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    @ApiModelProperty("排序")
    private Integer orderNo;

}
