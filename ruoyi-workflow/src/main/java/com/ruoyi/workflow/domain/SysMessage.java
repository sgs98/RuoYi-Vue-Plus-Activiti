package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 消息通知对象 sys_message
 *
 * @author gssong
 * @date 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class SysMessage extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 消息发送者id
     */
    private Long sendId;
    /**
     * 消息接收者id
     */
    private Long recordId;
    /**
     * 消息类型1：站内信，2：邮件，3：短信
     */
    private Integer type;
    /**
     * 标题
     */
    private String title;
    /**
     * 阅读状态0：未读，1：已读
     */
    private Integer status;
    /**
     * 消息内容
     */
    private String messageContent;
    /**
     * 阅读时间
     */
    private Date readTime;

}
