package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程定义设置对象 act_node_assignee
 *
 * @author gssong
 * @date 2021-11-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_node_assignee")
@ApiModel("流程定义设置对象")
public class ActNodeAssignee extends BaseEntity{

    private static final long serialVersionUID=1L;


    /**
     * id
     */
    @TableId(value = "id")
    private String id;

    /**
     * 流程定义id
     */
    @NotBlank(message = "流程定义id不能为空", groups = { AddGroup.class})
    private String processDefinitionId;

    /**
     * 选择方式  role按角色选人  dept按部门选人  person自定义选人
     */
    private String chooseWay;

    /**
     * 流程节点id
     */
    @NotBlank(message = "流程节点id不能为空", groups = { AddGroup.class})
    private String nodeId;

    /**
     * 流程节点名称
     */
    @NotBlank(message = "流程节点id不能为空", groups = { AddGroup.class})
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
    @NotNull(message = "是否弹出选人不能为空", groups = { AddGroup.class})
    private Boolean isShow;

    /**
     * 是否会签
     */
    @NotNull(message = "是否会签不能为空", groups = { AddGroup.class})
    private Boolean multiple;

    /**
     * 会签保存人员KEY值
     */
    private String multipleColumn;

    /**
     * 是否可退回
     */
    @NotNull(message = "是否能退回不能为空", groups = { AddGroup.class})
    private Boolean isBack;

    /**
     * 是否可委托
     */
    @NotNull(message = "是否能委托不能为空", groups = { AddGroup.class})
    private Boolean isDelegate;

    /**
     * 是否可转办
     */
    @NotNull(message = "是否能转办不能为空", groups = { AddGroup.class})
    private Boolean isTransmit;

    /**
     * 是否可抄送
     */
    @NotNull(message = "是否能抄送不能为空", groups = { AddGroup.class})
    private Boolean isCopy;

    /**
     * 是否可加签
     */
    private Boolean addMultiInstance;

    /**
     * 是否可减签
     */
    private Boolean deleteMultiInstance;

    /**
     * 下标排序
     */
    @TableField(exist = false)
    private Integer index;

}
