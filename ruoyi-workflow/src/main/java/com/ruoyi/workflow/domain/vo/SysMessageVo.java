package com.ruoyi.workflow.domain.vo;

import java.util.Date;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
 * 消息通知视图对象 sys_message
 *
 * @author gssong
 * @date 2022-06-17
 */
@Data
@ApiModel("消息通知视图对象")
@ExcelIgnoreUnannotated
public class SysMessageVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 消息发送者id
     */
    @ExcelProperty(value = "消息发送者id")
    @ApiModelProperty("消息发送者id")
    private Long sendId;

    /**
     * 消息接收者
     */
    @ExcelProperty(value = "消息发送者")
    @ApiModelProperty("消息发送者")
    private String sendName;

    /**
     * 消息接收者id
     */
    @ExcelProperty(value = "消息接收者id")
    @ApiModelProperty("消息接收者id")
    private Long recordId;

    /**
     * 消息接收者
     */
    @ExcelProperty(value = "消息接收者")
    @ApiModelProperty("消息接收者")
    private String recordName;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    @ApiModelProperty("标题")
    private String title;

    /**
     * 消息类型1：站内信，2：邮件，3：短信
     */
    @ExcelProperty(value = "消息类型1：站内信，2：邮件，3：短信", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message")
    @ApiModelProperty("消息类型1：站内信，2：邮件，3：短信")
    private Integer type;

    /**
     * 阅读状态0：未读，1：已读
     */
    @ExcelProperty(value = "阅读状态0：未读，1：已读",converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "read_status")
    @ApiModelProperty("阅读状态0：未读，1：已读")
    private Integer status;

    /**
     * 消息内容
     */
    @ExcelProperty(value = "消息内容")
    @ApiModelProperty("消息内容")
    private String messageContent;

    /**
     * 阅读时间
     */
    @ExcelProperty(value = "阅读时间")
    @ApiModelProperty("阅读时间")
    private Date readTime;


}
