package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务请求
 * @author: gssong
 * @created: 2021/10/17 14:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskBo extends PageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 流程定义id
     */
    private String processInstId;
}
