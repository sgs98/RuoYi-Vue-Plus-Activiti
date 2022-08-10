package com.ruoyi.report.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;


import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表查看业务对象 sys_report_view
 *
 * @author ruoyi
 * @date 2022-08-07
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("报表查看业务对象")
public class SysReportViewBo extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 报表id
     */
    @ApiModelProperty(value = "报表id", required = true)
    @NotNull(message = "报表id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reportId;

    /**
     * 报表名称
     */
    @ApiModelProperty(value = "报表名称", required = true)
    @NotBlank(message = "报表名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reportName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderNo;


}
