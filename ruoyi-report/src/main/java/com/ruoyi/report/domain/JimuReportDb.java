package com.ruoyi.report.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报表数据对象 jimu_report_db
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jimu_report_db")
public class JimuReportDb extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id")
    private String id;
    /**
     * 主键字段
     */
    private String jimuReportId;
    /**
     * 数据集编码
     */
    private String dbCode;
    /**
     * 数据集名字
     */
    private String dbChName;
    /**
     * 数据源类型
     */
    private String dbType;
    /**
     * 数据库表名
     */
    private String dbTableName;
    /**
     * 动态查询SQL
     */
    private String dbDynSql;
    /**
     * 数据源KEY
     */
    private String dbKey;
    /**
     * 填报数据源
     */
    private String tbDbKey;
    /**
     * 填报数据表
     */
    private String tbDbTableName;
    /**
     * java类数据集  类型（spring:springkey,class:java类名）
     */
    private String javaType;
    /**
     * java类数据源  数值（bean key/java类名）
     */
    private String javaValue;
    /**
     * 请求地址
     */
    private String apiUrl;
    /**
     * 请求方法0-get,1-post
     */
    private String apiMethod;
    /**
     * 是否是列表0否1是 默认0
     */
    private String isList;
    /**
     * 是否作为分页,0:不分页，1:分页
     */
    private String isPage;
    /**
     * 数据源
     */
    private String dbSource;
    /**
     * 数据库类型 MYSQL ORACLE SQLSERVER
     */
    private String dbSourceType;
    /**
     * json数据，直接解析json内容
     */
    private String jsonData;
    /**
     * api转换器
     */
    private String apiConvert;

}
