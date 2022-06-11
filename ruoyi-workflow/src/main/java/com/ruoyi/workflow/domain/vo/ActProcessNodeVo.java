package com.ruoyi.workflow.domain.vo;


import lombok.Data;

@Data
public class ActProcessNodeVo {

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
