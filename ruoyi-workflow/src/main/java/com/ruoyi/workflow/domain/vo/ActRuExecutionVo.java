package com.ruoyi.workflow.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;



/**
 * 流程执行视图对象 act_ru_execution
 *
 * @author gssong
 * @date 2022-02-08
 */
@Data
@ApiModel("流程执行视图对象")
@ExcelIgnoreUnannotated
public class ActRuExecutionVo {

	private static final long serialVersionUID = 1L;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String id;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long rev;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String procInstId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String businessKey;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String parentId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String procDefId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String superExec;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String rootProcInstId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String actId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Integer isActive;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Integer isConcurrent;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Integer isScope;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Integer isEventScope;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Integer isMiRoot;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long suspensionState;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long cachedEntState;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String tenantId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String name;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Date startTime;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private String startUserId;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Date lockTime;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Integer isCountEnabled;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long evtSubscrCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long taskCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long jobCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long timerJobCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long suspJobCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long deadletterJobCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long varCount;

    /**
     * 
     */
	@ExcelProperty(value = "")
	@ApiModelProperty("")
	private Long idLinkCount;


}
