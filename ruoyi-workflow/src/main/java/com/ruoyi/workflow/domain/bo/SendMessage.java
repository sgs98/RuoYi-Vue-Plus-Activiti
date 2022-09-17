package com.ruoyi.workflow.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @program: ruoyi-vue-plus
 * @description: 接收消息对象
 * @author: gssong
 * @created: 2022-06-23
 */
@Data
public class SendMessage implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息类型1：站内信，2：邮件，3：短信
     */
    private List<Integer> type;

    /**
     * 消息通知内容
     */
    private String messageContent;

}
