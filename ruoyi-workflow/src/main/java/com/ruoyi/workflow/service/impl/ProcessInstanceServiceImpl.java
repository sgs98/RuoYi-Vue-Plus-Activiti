package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.workflow.activiti.config.CustomProcessDiagramGenerator;
import com.ruoyi.workflow.activiti.config.ICustomProcessDiagramGenerator;
import com.ruoyi.workflow.activiti.config.WorkflowConstants;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.domain.bo.ProcessInstFinishREQ;
import com.ruoyi.workflow.domain.bo.ProcessInstRunningREQ;
import com.ruoyi.workflow.domain.bo.StartREQ;
import com.ruoyi.workflow.domain.vo.ActHistoryInfoVo;
import com.ruoyi.workflow.domain.vo.ProcessInstFinishVo;
import com.ruoyi.workflow.domain.vo.ProcessInstRunningVo;
import com.ruoyi.workflow.factory.WorkflowService;
import com.ruoyi.workflow.service.IActBusinessStatusService;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import com.ruoyi.workflow.service.IActTaskNodeService;
import com.ruoyi.workflow.service.IProcessInstanceService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程实例业务层
 * @author: gssong
 * @created: 2021/10/10 18:38
 */
@Service
public class ProcessInstanceServiceImpl extends WorkflowService implements IProcessInstanceService {

    @Autowired
    IActBusinessStatusService iActBusinessStatusService;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private IActTaskNodeService iActTaskNodeService;


    @Autowired
    private ProcessEngine processEngine;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> startWorkFlow(StartREQ startReq) {
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(startReq.getBusinessKey())) {
            throw new ServiceException("启动工作流时必须包含业务ID");
        }
        // 判断当前业务是否启动过流程
        List<HistoricProcessInstance> instanceList = historyService.createHistoricProcessInstanceQuery()
            .processInstanceBusinessKey(startReq.getBusinessKey()).list();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> taskResult = taskQuery.processInstanceBusinessKey(startReq.getBusinessKey()).list();
        if (CollectionUtil.isNotEmpty(instanceList)) {
            ActBusinessStatus info = iActBusinessStatusService.getInfoByBusinessKey(startReq.getBusinessKey());
            if(ObjectUtil.isNotEmpty(info)){
                BusinessStatusEnum.checkStatus(info.getStatus());
            }
            map.put("processInstanceId",taskResult.get(0).getProcessInstanceId());
            map.put("taskId",taskResult.get(0).getId());
            return map;
        }
        // 设置启动人
        Authentication.setAuthenticatedUserId(LoginHelper.getUserId().toString());
        // 启动流程实例（提交申请）
        Map<String, Object> variables = startReq.getVariables();
        variables.put("status",BusinessStatusEnum.DRAFT.getStatus());
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(startReq.getProcessKey(), startReq.getBusinessKey(),variables);
        // 将流程定义名称 作为 流程实例名称
        runtimeService.setProcessInstanceName(pi.getProcessInstanceId(), pi.getProcessDefinitionName());
        // 申请人执行流程
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        for (Task task : taskList) {
            taskService.setAssignee(task.getId(),LoginHelper.getUserId().toString());
        }

        //查询下一个任务
        List<Task> nextList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        for (Task task : nextList) {
            // 更新业务状态
            iActBusinessStatusService.updateState(startReq.getBusinessKey(), BusinessStatusEnum.DRAFT, task.getProcessInstanceId(), startReq.getClassFullName());
        }
        map.put("processInstanceId",pi.getProcessInstanceId());
        map.put("taskId",nextList.get(0).getId());
        return map;
    }


    @Override
    public List<ActHistoryInfoVo> getHistoryInfoList(String processInstanceId) {
        //查询任务办理记录
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
            .orderByHistoricTaskInstanceEndTime().desc().list();
        list.stream().sorted(Comparator.comparing(l -> l.getEndTime(), Comparator.nullsFirst(Date::compareTo))).collect(Collectors.toList());
        List<ActHistoryInfoVo> actHistoryInfoVoList = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            ActHistoryInfoVo actHistoryInfoVo = new ActHistoryInfoVo();
            BeanUtils.copyProperties(historicTaskInstance, actHistoryInfoVo);
            actHistoryInfoVo.setStatus(actHistoryInfoVo.getEndTime() == null ? "待处理" : "已处理");
            String message = null;
            if(StringUtils.isEmpty(message)) {
                List<Comment> taskComments = taskService.getTaskComments(historicTaskInstance.getId());
                message = taskComments.stream()
                    .map(m -> m.getFullMessage()).collect(Collectors.joining("。"));
            }
            actHistoryInfoVo.setComment(message);
            actHistoryInfoVoList.add(actHistoryInfoVo);
        }
        //翻译人员名称
        if(CollectionUtil.isNotEmpty(actHistoryInfoVoList)){
            List<String> collect = actHistoryInfoVoList.stream().filter(e->StringUtils.isNotBlank(e.getAssignee())).map(ActHistoryInfoVo::getAssignee).collect(Collectors.toList());
            List<Long> userIds = new ArrayList<>();
            for (String userId : collect) {
                userIds.add(Long.valueOf(userId));
            }
            List<SysUser> sysUsers = iSysUserService.selectListUserByIds(userIds);
            for (ActHistoryInfoVo actHistoryInfoVo : actHistoryInfoVoList) {
                SysUser sysUser = sysUsers.stream().filter(e -> e.getUserId().toString().equals(actHistoryInfoVo.getAssignee())).findFirst().orElse(null);
                if(ObjectUtil.isNotEmpty(sysUser)){
                    actHistoryInfoVo.setNickName(sysUser.getNickName());
                }
            }

        }
        List<ActHistoryInfoVo> collect = new ArrayList<>();
        //待办理
        List<ActHistoryInfoVo> waitingTask = actHistoryInfoVoList.stream().filter(e -> e.getEndTime() == null).collect(Collectors.toList());
        //已办理
        List<ActHistoryInfoVo> finishTask = actHistoryInfoVoList.stream().filter(e -> e.getEndTime() != null).collect(Collectors.toList());
        collect.addAll(waitingTask);
        collect.addAll(finishTask);
        return collect;
    }

    @Override
    public void getHistoryProcessImage(String processInstanceId, HttpServletResponse response) {
        // 设置页面不缓存
        response.setHeader( "Pragma", "no-cache" );
        response.addHeader( "Cache-Control", "must-revalidate" );
        response.addHeader( "Cache-Control", "no-cache" );
        response.addHeader( "Cache-Control", "no-store" );
        response.setDateHeader("Expires", 0);
        InputStream inputStream = null;
        try {
            // 1. 查询流程实例历史数据
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
            // 2. 查询流程中已执行的节点，按时开始时间降序排列
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .desc().list();

            // 3. 单独的提取高亮节点id ( 绿色）
            List<String> histExecutedActivityIdList = new ArrayList<>();
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                histExecutedActivityIdList.add(activityInstance.getActivityId());
            }
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 实例化流程图生成器
            CustomProcessDiagramGenerator generator = new CustomProcessDiagramGenerator();
            // 获取高亮连线id
            List<String> highLightedFlows = generator.getHighLightedFlows(bpmnModel, historicActivityInstanceList);
            // 4. 正在执行的节点 （红色）
            Set<String> executedActivityIdList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list()
                .stream().map(e->e.getActivityId()).collect(Collectors.toSet());

            ICustomProcessDiagramGenerator diagramGenerator = (ICustomProcessDiagramGenerator) processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", histExecutedActivityIdList,
                highLightedFlows, "宋体", "宋体", "宋体", null, 1.0, new Color[] { WorkflowConstants.COLOR_NORMAL, WorkflowConstants.COLOR_CURRENT }, executedActivityIdList);

            // 响应相关图片
            response.setContentType("image/png");

            byte[] bytes = IOUtils.toByteArray(inputStream);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查询正在运行的流程实例
     * @param req
     * @return
     */
    @Override
    public TableDataInfo<ProcessInstRunningVo> getProcessInstRunningByPage(ProcessInstRunningREQ req) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StringUtils.isNotBlank(req.getName())) {
            query.processInstanceNameLikeIgnoreCase(req.getName());
        }
        if (StringUtils.isNotBlank(req.getStartUserId())) {
            query.startedBy(req.getStartUserId());
        }
        List<ProcessInstance> processInstances = query.listPage(req.getFirstResult(), req.getPageSize());
        List<ProcessInstRunningVo> processInstRunningVoList = new ArrayList<>();
        long total = query.count();
        for (ProcessInstance pi : processInstances) {
            ProcessInstRunningVo processInstRunningVo = new ProcessInstRunningVo();
            BeanUtils.copyProperties(pi, processInstRunningVo);
            SysUser sysUser = iSysUserService.selectUserById(Long.valueOf(pi.getStartUserId()));
            if (ObjectUtil.isNotEmpty(sysUser)) {
                processInstRunningVo.setStartUserNickName(sysUser.getNickName());
            }
            processInstRunningVo.setIsSuspended(pi.isSuspended() == true ? "挂起" : "激活");
            // 查询当前实例的当前任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
            //办理人
            String currTaskInfo = "";
            //办理人id
            String currTaskInfoId = "";
            //办理人集合
            List<String> nickNameList = null;
            for (Task task : taskList) {
                if (StringUtils.isNotBlank(task.getAssignee())) {
                    String[] split = task.getAssignee().split(",");
                    List<Long> userIds = new ArrayList<>();
                    for (String userId : split) {
                        userIds.add(Long.valueOf(userId));
                    }
                    //办理人
                    List<SysUser> sysUsers = iSysUserService.selectListUserByIds(userIds);
                    if (CollectionUtil.isNotEmpty(sysUsers)) {
                        nickNameList = sysUsers.stream().map(SysUser::getNickName).collect(Collectors.toList());
                    }
                }


                currTaskInfo += "任务名【" + task.getName() + "】，办理人【" + StringUtils.join(nickNameList, ",") + "】";
                currTaskInfoId += task.getAssignee();
            }
            processInstRunningVo.setCurrTaskInfo(currTaskInfo);
            processInstRunningVo.setCurrTaskInfoId(currTaskInfoId);
            processInstRunningVoList.add(processInstRunningVo);
        }
        List<ProcessInstRunningVo> list = null;
        if (CollectionUtil.isNotEmpty(processInstRunningVoList)) {
            list = processInstRunningVoList.stream().sorted(Comparator.comparing(ProcessInstRunningVo::getStartTime).reversed()).collect(Collectors.toList());
        }
        return new TableDataInfo(list, total);
    }

    /**
     * 挂起或激活流程实例
     * @param processInstId
     */
    @Override
    public void updateProcInstState(String processInstId) {
        // 1. 查询指定流程实例的数据
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstId)
            .singleResult();

        // 2. 判断当前流程实例的状态
        if (processInstance.isSuspended()) {
            // 如果是已挂起，则更新为激活状态
            runtimeService.activateProcessInstanceById(processInstId);
        } else {
            // 如果是已激活，则更新为挂起状态
            runtimeService.suspendProcessInstanceById(processInstId);
        }
    }

    /**
     * 作废流程实例，不会删除历史记录
     * @param processInstId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRuntimeProcessInst(String processInstId) {
        //1.查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstId).singleResult();
        //2.删除流程实例
        runtimeService.deleteProcessInstance(processInstId, LoginHelper.getUserId() + "作废了当前流程申请");
        //3. 更新业务状态
        return iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.INVALID);
    }

    /**
     * 删除程实例，删除历史记录，删除业务与流程关联信息
     * @param processInstId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRuntimeProcessAndHisInst(String processInstId) {
        //1.查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstId).singleResult();
        //2.删除流程实例
        runtimeService.deleteProcessInstance(processInstId, LoginHelper.getUserId() + "删除了当前流程申请");
        //3.删除历史记录
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstId).singleResult();
        if(ObjectUtil.isNotEmpty(historicProcessInstance)){
            historyService.deleteHistoricProcessInstance(processInstId);
        }
        //4.删除业务状态
        iActBusinessStatusService.deleteState(processInstance.getBusinessKey());
        //5.删除保存的任务节点
        return iActTaskNodeService.deleteByInstanceId(processInstId);
    }

    /**
     * 删除已完成的实例，删除历史记录，删除业务与流程关联信息
     * @param processInstId
     * @return
     */
    @Override
    public boolean deleteFinishProcessAndHisInst(String processInstId) {
        //1.查询流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstId).singleResult();
        //2.删除历史记录
        historyService.deleteHistoricProcessInstance(processInstId);
        //3.删除业务状态
        iActBusinessStatusService.deleteState(historicProcessInstance.getBusinessKey());
        //4.删除保存的任务节点
        return iActTaskNodeService.deleteByInstanceId(processInstId);
    }

    /**
     * 查询已结束的流程实例
     * @param req
     * @return
     */
    @Override
    public TableDataInfo<ProcessInstFinishVo> getProcessInstFinishByPage(ProcessInstFinishREQ req) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
            .finished() // 已结束的
            .orderByProcessInstanceEndTime().desc();
        ;
        if (StringUtils.isNotEmpty(req.getName())) {
            query.processInstanceNameLikeIgnoreCase(req.getName());
        }
        if (StringUtils.isNotEmpty(req.getStartUserId())) {
            query.startedBy(req.getStartUserId());
        }
        List<HistoricProcessInstance> list = query.listPage(req.getFirstResult(), req.getPageSize());
        long total = query.count();
        List<ProcessInstFinishVo> processInstFinishVoList = new ArrayList<>();
        for (HistoricProcessInstance hpi : list) {
            ProcessInstFinishVo processInstFinishVo = new ProcessInstFinishVo();
            BeanUtils.copyProperties(hpi, processInstFinishVo);
            SysUser sysUser = iSysUserService.selectUserById(Long.valueOf(hpi.getStartUserId()));
            if (ObjectUtil.isNotEmpty(sysUser)) {
                processInstFinishVo.setStartUserNickName(sysUser.getNickName());
            }
            //业务状态
            ActBusinessStatus businessKey = iActBusinessStatusService.getInfoByBusinessKey(hpi.getBusinessKey());
            if (ObjectUtil.isNotNull(businessKey)) {
                processInstFinishVo.setStatus(BusinessStatusEnum.getEumByStatus(businessKey.getStatus()).getDesc());
            }
            processInstFinishVoList.add(processInstFinishVo);
        }
        return new TableDataInfo(processInstFinishVoList, total);
    }

    @Override
    public String getProcessInstanceId(String businessKey) {
        String processInstanceId = null;
        ActBusinessStatus infoByBusinessKey = iActBusinessStatusService.getInfoByBusinessKey(businessKey);
        if(ObjectUtil.isNotEmpty(infoByBusinessKey)&&infoByBusinessKey.getStatus().equals(BusinessStatusEnum.FINISH.getStatus())){
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            processInstanceId = ObjectUtil.isNotEmpty(historicProcessInstance)?historicProcessInstance.getId():"";
        }else{
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            processInstanceId = ObjectUtil.isNotEmpty(processInstance)?processInstance.getProcessInstanceId():"";
        }
        return processInstanceId;
    }

    /**
     * 撤销申请
     * @param processInstId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelProcessApply(String processInstId) {
        /*ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstId).startedBy(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isNull(processInstance)){
            throw new ServiceException("流程不是该审批人提交,撤销失败!");
        }
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstId).list();
        for (Task task : list) {
            if (task.isSuspended()) {
                throw new ServiceException("当前任务已被挂起");
            }
            List<Task> newlist = taskService.createTaskQuery().processInstanceId(processInstId).list();
            if(CollectionUtil.isNotEmpty(newlist)&&newlist.size()==1){
                // 1. 获取流程模型实例 BpmnModel
                BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
                // 2.当前节点信息
                FlowNode curFlowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
                // 3.获取当前节点原出口连线
                List<SequenceFlow> sequenceFlowList = curFlowNode.getOutgoingFlows();
                // 4. 临时存储当前节点的原出口连线
                List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
                oriSequenceFlows.addAll(sequenceFlowList);
                // 5. 将当前节点的原出口清空
                sequenceFlowList.clear();
                // 6. 获取目标节点信息
                List<ActTaskNode> listActTaskNode = iActTaskNodeService.getListByInstanceId(processInstId);
                if(CollectionUtil.isEmpty(listActTaskNode)){
                    throw new ServiceException("未查询到撤回节点信息");
                }
                ActTaskNode actTaskNode = listActTaskNode.stream().filter(e -> e.getOrderNo().equals(0)).findFirst().orElse(null);
                if(ObjectUtil.isNull(actTaskNode)){
                    throw new ServiceException("未查询到撤回节点信息");
                }
                FlowNode targetFlowNode = (FlowNode) bpmnModel.getFlowElement(actTaskNode.getNodeId());
                // 7. 获取目标节点的入口连线
                List<SequenceFlow> incomingFlows = targetFlowNode.getIncomingFlows();
                // 8. 存储所有目标出口
                List<SequenceFlow> targetSequenceFlow = new ArrayList<>();
                for (SequenceFlow incomingFlow : incomingFlows) {
                    // 找到入口连线的源头（获取目标节点的父节点）
                    FlowNode source = (FlowNode) incomingFlow.getSourceFlowElement();
                    List<SequenceFlow> sequenceFlows;
                    if (source instanceof ParallelGateway) {
                        // 并行网关: 获取目标节点的父节点（并行网关）的所有出口，
                        sequenceFlows = source.getOutgoingFlows();
                    } else {
                        // 其他类型父节点, 则获取目标节点的入口连续
                        sequenceFlows = targetFlowNode.getIncomingFlows();
                    }
                    targetSequenceFlow.addAll(sequenceFlows);
                }
                // 9. 将当前节点的出口设置为新节点
                curFlowNode.setOutgoingFlows(targetSequenceFlow);
                // 10. 完成当前任务，流程就会流向目标节点创建新目标任务
                // 当前任务，完成当前任务
                taskService.addComment(task.getId(), task.getProcessInstanceId(),"撤销申请");
                // 完成任务，就会进行驳回到目标节点，产生目标节点的任务数据
                Map<String, Object> variables =new HashMap<>();
                variables.put("status",BusinessStatusEnum.CANCEL.getStatus());
                taskService.setVariables(task.getId(),variables);
                taskService.complete(task.getId());
                // 11. 完成驳回功能后，将当前节点的原出口方向进行恢复
                curFlowNode.setOutgoingFlows(oriSequenceFlows);
                // 12. 查询目标任务节点历史办理人
                List<Task> newTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
                for (Task newTask : newTaskList) {
                    taskService.setAssignee(newTask.getId(), LoginHelper.getUserId().toString());
                }
            }else{
                taskService.complete(task.getId());
                historyService.deleteHistoricTaskInstance(task.getId());
            }

        }
        // 13. 删除驳回后的流程节点
        iActBusinessStatusService.updateState(processInstance.getBusinessKey(),BusinessStatusEnum.CANCEL);
        Boolean taskNode = iActTaskNodeService.deleteByInstanceId(processInstId);*/
        boolean b = this.deleteRuntimeProcessAndHisInst(processInstId);
        return b;
    }
}
