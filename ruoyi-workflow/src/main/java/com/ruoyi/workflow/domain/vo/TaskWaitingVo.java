package com.ruoyi.workflow.domain.vo;

import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.domain.ActProcessDefSetting;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务视图
 * @author: gssong
 * @created: 2021/10/17 14:54
 */
@Data
public class TaskWaitingVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 流程任务id
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 流程状态
     */
    private String processStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 节点id
     */
    private String taskDefinitionKey;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程执行id
     */
    private String executionId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 办理人或候选人 （ 任务办理人: 如果是候选人则没有值，办理人才有）
     */
    private String assignee;

    /**
     * 办理人或候选人 （ 任务办理人: 如果是候选人则没有值，办理人才有）
     */
    private Long assigneeId;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 流程定义版本
     */
    private Integer processDefinitionVersion;

    /**
     * 流程启动人
     */
    private String startUserNickName;

    /**
     * 流程启动人ID
     */
    private String startUserId;

    /**
     * 业务id
     */
    private String businessKey;

    /**
     * 父级任务id
     */
    private String parentTaskId;

    /**
     * 认领或归还 ture已认领，false未认领 ，空没有候选人
     */
    private Boolean isClaim;

    /**
     * 是否会签
     */
    private Boolean multiInstance;

    /**
     * 是否可减签人员
     */
    private List<TaskVo> taskVoList;

    /**
     * 业务状态
     */
    private ActBusinessStatus actBusinessStatus;

    /**
     * 流程定义设置
     */
    private ActProcessDefSettingVo actProcessDefSetting;
}
