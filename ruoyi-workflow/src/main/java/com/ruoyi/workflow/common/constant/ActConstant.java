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
     String PARALLELGATEWAY = "parallelGateway";

    /**
     *排它网关
     */
     String EXCLUSIVEGATEWAY = "exclusiveGateway";

    /**
     *结束
     */
    String ENDTASK = "endTask";

    /**
     * 用户任务
     */
     String USER_TASK = "userTask";

     String JSON = "json";

     Boolean TRUE=true;

     Boolean FALSE=false;

     String STRING = "string";

     String INTEGER = "integer";

     String SHORT = "short";

     String LONG = "long";

     String DOUBLE = "double";

     String DATE = "date";

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

    String PARAM_BYTE = "Byte";

    String PARAM_SHORT = "Short";

    String PARAM_INTEGER = "Integer";

    String PARAM_LONG = "Long";

    String PARAM_FLOAT = "Float";

    String PARAM_DOUBLE = "Double";

    String PARAM_BOOLEAN = "Boolean";

    String PARAM_CHARACTER = "Character";






}
