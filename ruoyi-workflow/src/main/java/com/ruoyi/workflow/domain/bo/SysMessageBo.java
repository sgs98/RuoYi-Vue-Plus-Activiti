package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息通知业务对象 sys_message
 *
 * @author gssong
 * @date 2022-06-17
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("消息通知业务对象")
public class SysMessageBo extends BaseEntity {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", required = true)
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 消息发送者id
     */
    @ApiModelProperty(value = "消息发送者id", required = true)
    @NotNull(message = "消息发送者id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sendId;

    /**
     * 消息接收者id
     */
    @ApiModelProperty(value = "消息接收者id", required = true)
    @NotNull(message = "消息接收者id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long recordId;

    /**
     * 消息类型1：站内信，2：邮件，3：短信
     */
    @ApiModelProperty(value = "消息类型1：站内信，2：邮件，3：短信", required = true)
    @NotNull(message = "消息类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer type;

    /**
     * 阅读状态0：未读，1：已读
     */
    @ApiModelProperty(value = "阅读状态false：未读，true：已读", required = true)
    @NotNull(message = "阅读状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String messageContent;

    /**
     * 阅读时间
     */
    @ApiModelProperty(value = "阅读时间")
    private Date readTime;


}
