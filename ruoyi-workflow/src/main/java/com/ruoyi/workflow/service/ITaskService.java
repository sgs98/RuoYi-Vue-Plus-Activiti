package com.ruoyi.workflow.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.domain.bo.NextNodeREQ;
import com.ruoyi.workflow.domain.bo.TaskCompleteREQ;
import com.ruoyi.workflow.domain.bo.TaskREQ;
import com.ruoyi.workflow.domain.bo.TransmitREQ;
import com.ruoyi.workflow.domain.vo.BackProcessVo;
import com.ruoyi.workflow.domain.vo.ProcessNode;
import com.ruoyi.workflow.domain.vo.TaskFinishVo;
import com.ruoyi.workflow.domain.vo.TaskWaitingVo;

import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务接口
 * @author: gssong
 * @created: 2021/10/17 14:57
 */
public interface ITaskService {
    /**
     * 查询当前用户的待办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskWaitingVo> getTaskWaitByPage(TaskREQ req);

    /**
     * 完成任务
     * @param req
     * @return
     */
    Boolean completeTask(TaskCompleteREQ req);

    /**
     * 查询当前用户的已办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskFinishVo> getTaskFinishByPage(TaskREQ req);

    /**
     * 获取目标节点（下一个节点）
     * @param req
     * @return
     */
    List<ProcessNode> getNextNodeInfo(NextNodeREQ req);

    /**
     * 查询所有用户的已办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskFinishVo> getAllTaskFinishByPage(TaskREQ req);

    /**
     * 查询所有用户的待办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskWaitingVo> getAllTaskWaitByPage(TaskREQ req);

    /**
     * 驳回审批
     * @param: backProcessVo
     * @return
     */
    String backProcess(BackProcessVo backProcessVo);

    /**
     * 获取历史任务节点，用于驳回功能
     * @param processInstId
     * @return
     */
    List<ActTaskNode> getBackNodes(String processInstId);

    /**
     * 委托
     * @param taskREQ
     * @return
     */
    Boolean delegateTask(TaskREQ taskREQ);

    /**
     * 转办任务
     * @param transmitREQ
     * @return
     */
    R<Boolean> transmitTask(TransmitREQ transmitREQ);
}
