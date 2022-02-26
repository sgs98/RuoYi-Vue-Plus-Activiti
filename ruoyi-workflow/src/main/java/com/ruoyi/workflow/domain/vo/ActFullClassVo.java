package com.ruoyi.workflow.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import com.ruoyi.workflow.domain.ActFullClassParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.util.List;


/**
 * 业务规则视图对象 act_full_class
 *
 * @author gssong
 * @date 2021-12-16
 */
@Data
@ApiModel("业务规则视图对象")
@ExcelIgnoreUnannotated
public class ActFullClassVo {

	private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 全类名
     */
	@ExcelProperty(value = "全类名")
	@ApiModelProperty("全类名")
	private String fullClass;

    /**
     * 方法名
     */
	@ExcelProperty(value = "方法名")
	@ApiModelProperty("方法名")
	private String method;

    /**
     * 备注
     */
	@ExcelProperty(value = "备注")
	@ApiModelProperty("备注")
	private String remark;

    /**
     * 创建时间
     */
	@ExcelProperty(value = "创建时间")
	@ApiModelProperty("创建时间")
	private Date createTime;

    /**
     * 更新时间
     */
	@ExcelProperty(value = "更新时间")
	@ApiModelProperty("更新时间")
	private Date updateTime;

    /**
     * 创建人
     */
	@ExcelProperty(value = "创建人")
	@ApiModelProperty("创建人")
	private String createBy;

    /**
     * 更新人
     */
	@ExcelProperty(value = "更新人")
	@ApiModelProperty("更新人")
	private String updateBy;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private List<ActFullClassParam> fullClassParam;


}
