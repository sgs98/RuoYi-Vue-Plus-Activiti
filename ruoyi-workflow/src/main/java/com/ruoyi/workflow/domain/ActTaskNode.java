package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @program: ruoyi-vue-plus
 * @description: 审批历史任务节点
 * @author: gssong
 * @created: 2021/11/06 16:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_task_Node")
public class ActTaskNode extends BaseEntity{

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 流程实例id
     */
    private String instanceId;

    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 是否可退回
     */
    private Boolean isBack;

}
