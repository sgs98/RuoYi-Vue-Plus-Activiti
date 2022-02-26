package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 流程执行业务对象 act_ru_execution
 *
 * @author gssong
 * @date 2022-02-08
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("流程执行业务对象")
public class ActRuExecutionBo extends BaseEntity {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String id;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long rev;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String procInstId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String businessKey;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String parentId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String procDefId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String superExec;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String rootProcInstId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String actId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Integer isActive;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Integer isConcurrent;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Integer isScope;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Integer isEventScope;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Integer isMiRoot;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long suspensionState;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long cachedEntState;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String tenantId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String name;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Date startTime;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private String startUserId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Date lockTime;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Integer isCountEnabled;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long evtSubscrCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long taskCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long jobCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long timerJobCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long suspJobCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long deadletterJobCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long varCount;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private Long idLinkCount;


}
