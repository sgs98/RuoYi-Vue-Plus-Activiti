package com.ruoyi.workflow.domain.vo;

import lombok.Data;

@Data
public class TaskListenerVo {

    /**
     * 事件前后  after,before
     */
    private String eventType;

    /**
     * bean名称
     */
    private String beanName;

}
