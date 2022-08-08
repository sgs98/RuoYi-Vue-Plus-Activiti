package com.ruoyi.workflow.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.domain.bo.*;
import com.ruoyi.workflow.domain.bo.BackProcessBo;
import com.ruoyi.workflow.domain.vo.TaskFinishVo;
import com.ruoyi.workflow.domain.vo.TaskWaitingVo;
import com.ruoyi.workflow.domain.vo.VariableVo;

import java.util.List;
import java.util.Map;

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
    Map<String,Object> getNextNodeInfo(NextNodeREQ req);

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
     * @param: backProcessBo
     * @return
     */
    String backProcess(BackProcessBo backProcessBo);

    /**
     * 获取历史任务节点，用于驳回功能
     * @param processInstId
     * @return
     */
    List<ActTaskNode> getBackNodes(String processInstId);

    /**
     * 委托
     * @param delegateREQ
     * @return
     */
    Boolean delegateTask(DelegateREQ delegateREQ);

    /**
     * 转办任务
     * @param transmitREQ
     * @return
     */
    R<Boolean> transmitTask(TransmitREQ transmitREQ);

    /**
     * 会签任务加签
     * @param addMultiREQ
     * @return
     */
    R<Boolean> addMultiInstanceExecution(AddMultiREQ addMultiREQ);

    /**
     * 会签任务减签
     * @param deleteMultiREQ
     * @return
     */
    R<Boolean> deleteMultiInstanceExecution(DeleteMultiREQ deleteMultiREQ);

    /**
     * 修改办理人
     * @param updateAssigneeBo
     * @return
     */
    R<Void> updateAssignee(UpdateAssigneeBo updateAssigneeBo);


    /**
     * 查询流程变量
     * @param taskId
     * @return
     */
    R<List<VariableVo>> getProcessInstVariable(String taskId);

    /**
     * 修改审批意见
     * @param commentId
     * @param comment
     * @return
     */
    R<Void> editComment(String commentId,String comment);
}
