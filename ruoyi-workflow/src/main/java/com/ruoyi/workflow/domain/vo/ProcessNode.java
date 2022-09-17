package com.ruoyi.workflow.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程节点信息
 * @author: gssong
 * @created: 2021/10/16 19:35
 */
@Data
public class ProcessNode  implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 节点id
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String nodeName;


    /**
     * 网关或者用户任务
     */
    private String nodeType;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 表达式是否成立 true 成立 false 不成立
     */
    private Boolean expression;

    /**
     * 选择方式  role按角色选人  dept按部门选人  person自定义选人
     */
    private String chooseWay;

    /**
     * 审批人员
     */
    private String assignee;

    /**
     * 审批参数ID
     */
    private String assigneeId;

    /**
     * 业务规则id
     */
    private Long businessRuleId;

    /**
     * 是否弹窗选人
     */
    private Boolean isShow;

    /**
     * 是否会签
     */
    private Boolean multiple;

    /**
     * 会签保存人员KEY值
     */
    private String multipleColumn;
}
