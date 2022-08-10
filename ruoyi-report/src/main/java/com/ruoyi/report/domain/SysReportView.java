package com.ruoyi.report.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表查看对象 sys_report_view
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_report_view")
public class SysReportView extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 报表id
     */
    private String reportId;
    /**
     * 报表名称
     */
    private String reportName;
    /**
     * 排序
     */
    private Integer orderNo;

}
