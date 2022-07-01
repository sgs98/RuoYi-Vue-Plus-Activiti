package com.ruoyi.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("接收消息对象")
public class SendMessage implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "标题",required = true)
    private String title;

    @ApiModelProperty(value = "消息类型1：站内信，2：邮件，3：短信",required = true)
    private List<Integer> type;

    @ApiModelProperty(value = "消息通知内容",required = true)
    private String messageContent;

}
