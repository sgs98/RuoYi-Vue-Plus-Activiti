package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 流程定义设置对象 act_node_assignee
 *
 * @author gssong
 * @date 2021-11-21
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("act_node_assignee")
public class ActNodeAssignee extends BaseEntity implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * id
     */
    @TableId(value = "id")
    private String id;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 选择方式  role按角色选人  dept按部门选人  person自定义选人
     */
    private String chooseWay;

    /**
     * 流程节点id
     */
    private String nodeId;

    /**
     * 流程节点名称
     */
    private String nodeName;

    /**
     * 审批人员
     */
    private String assignee;

    /**
     * 审批人员id
     */
    private String assigneeId;

    /**
     * 业务规则id
     */
    private Long fullClassId;

    /**
     * 是否弹出选人
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

    /**
     * 是否可退回
     */
    private Boolean isBack;


}
