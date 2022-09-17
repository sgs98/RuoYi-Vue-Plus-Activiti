package com.ruoyi.workflow.domain.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程节点视图
 * @author: gssong
 * @created: 2022-02-26
 */
@Data
public class ActProcessNodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 索引下标
     */
    private Integer index;
}
