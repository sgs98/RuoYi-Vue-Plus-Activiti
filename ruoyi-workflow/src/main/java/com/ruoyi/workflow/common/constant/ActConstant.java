package com.ruoyi.workflow.common.constant;


public interface ActConstant {

     String PNG = "png";

     String XML = "xml";

     String UTF_8 = "UTF-8";

     String ZIP = "ZIP";

    /**
     * bpmn2.0命名空间
     */
     String NAMESPACE = "http://b3mn.org/stencilset/bpmn2.0#";

    /**
     * 并行网关
     */
     String PARALLEL_GATEWAY = "parallelGateway";

    /**
     *排它网关
     */
     String EXCLUSIVE_GATEWAY = "exclusiveGateway";

    /**
     *包含网关
     */
    String INCLUSIVE_GATEWAY = "InclusiveGateway";

    /**
     *结束节点
     */
    String END_EVENT = "EndEvent";

    /**
     *连线
     */
    String SEQUENCE_FLOW = "sequenceFlow";

    /**
     * 用户任务
     */
     String USER_TASK = "userTask";

    /**
     *  会签任务总数
     */
    String NUMBER_OF_INSTANCES = "nrOfInstances";

    /**
     *  正在执行的会签总数
     */
    String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";

    /**
     *  已完成的会签任务总数
     */
    String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";

    /**
     *  循环的索引值，可以使用elementIndexVariable属性修改loopCounter的变量名
     */
    String LOOP_COUNTER = "loopCounter";

    /**
     * 流程自定义人员
     */
    String WORKFLOW_ASSIGNEE = "workflowAssignee";

    /**
     * 角色
     */
     String WORKFLOW_ROLE = "role";

    /**
     * 部门
     */
     String WORKFLOW_DEPT = "dept";

    /**
     * 人员id
     */
     String WORKFLOW_PERSON = "person";

    /**
     * 业务规则
     */
    String WORKFLOW_RULE= "rule";

    /**
     * 流程状态对象
     */
    String ACT_BUSINESSS_TATUS = "actBusinessStatus";

    /**
     * 流程
     */
    String PROCESS_INSTANCE_ID = "processInstanceId";

    /**
     * 流程委派标识
     */
    String PENDING = "PENDING";

    /**
     * 候选标识
     */
    String CANDIDATE = "candidate";

    /**
     * 任务执行前
     */
    String HANDLE_BEFORE = "before";

    /**
     * 任务执行后
     */
    String HANDLE_AFTER = "after";

    /**
     * 任务执行方法
     */
    String HANDLE_PROCESS = "handleProcess";

    String PARAM_BYTE = "Byte";

    String PARAM_SHORT = "Short";

    String PARAM_INTEGER = "Integer";

    String PARAM_LONG = "Long";

    String PARAM_FLOAT = "Float";

    String PARAM_DOUBLE = "Double";

    String PARAM_BOOLEAN = "Boolean";

}
