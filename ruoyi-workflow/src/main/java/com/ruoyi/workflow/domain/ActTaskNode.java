package com.ruoyi.workflow.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务节点
 * @author: gssong
 * @created: 2021/11/06 16:12
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("act_task_Node")
public class ActTaskNode {

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

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
