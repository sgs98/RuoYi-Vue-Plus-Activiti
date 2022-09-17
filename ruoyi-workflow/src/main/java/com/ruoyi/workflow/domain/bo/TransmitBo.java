package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * @program: ruoyi-vue-plus
 * @description: 转办请求
 * @author: gssong
 * @created: 2022/04/10 14:50
 */
@Data
public class TransmitBo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 任务id
     */
    @NotBlank(message = "任务id为空",groups = AddGroup.class)
    private String taskId;

    /**
     * 转办人id
     */
    @NotBlank(message = "转办人不能为空",groups = AddGroup.class)
    private String transmitUserId;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 消息对象
     */
    private SendMessage sendMessage;
}
