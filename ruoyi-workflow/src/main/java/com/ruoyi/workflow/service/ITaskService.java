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
    TableDataInfo<TaskWaitingVo> getTaskWaitByPage(TaskBo req);

    /**
     * 完成任务
     * @param req
     * @return
     */
    Boolean completeTask(TaskCompleteBo req);

    /**
     * 查询当前用户的已办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskFinishVo> getTaskFinishByPage(TaskBo req);

    /**
     * 获取目标节点（下一个节点）
     * @param req
     * @return
     */
    Map<String,Object> getNextNodeInfo(NextNodeBo req);

    /**
     * 查询所有用户的已办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskFinishVo> getAllTaskFinishByPage(TaskBo req);

    /**
     * 查询所有用户的待办任务
     * @param req
     * @return
     */
    TableDataInfo<TaskWaitingVo> getAllTaskWaitByPage(TaskBo req);

    /**
     * 驳回审批
     * @param backProcessBo
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
     * @param delegateBo
     * @return
     */
    Boolean delegateTask(DelegateBo delegateBo);

    /**
     * 转办任务
     * @param transmitBo
     * @return
     */
    R<Boolean> transmitTask(TransmitBo transmitBo);

    /**
     * 会签任务加签
     * @param addMultiBo
     * @return
     */
    R<Boolean> addMultiInstanceExecution(AddMultiBo addMultiBo);

    /**
     * 会签任务减签
     * @param deleteMultiBo
     * @return
     */
    R<Boolean> deleteMultiInstanceExecution(DeleteMultiBo deleteMultiBo);

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
