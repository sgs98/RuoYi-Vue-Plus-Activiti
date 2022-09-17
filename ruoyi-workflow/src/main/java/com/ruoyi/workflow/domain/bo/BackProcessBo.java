package com.ruoyi.workflow.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 驳回请求
 * @author: gssong
 * @created: 2021/11/06 22:22
 */
@Data
public class BackProcessBo implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务id
     */
    private String targetActivityId;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 消息对象
     */
    private SendMessage sendMessage;
}
