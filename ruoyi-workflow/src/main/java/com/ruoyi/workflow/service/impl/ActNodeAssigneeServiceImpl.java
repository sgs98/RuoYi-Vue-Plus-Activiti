package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.domain.vo.ActProcessNodeVo;
import com.ruoyi.workflow.mapper.ActNodeAssigneeMapper;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import lombok.RequiredArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义设置业务层
 * @author: gssong
 * @created: 2021/11/21
 */
@Service
@RequiredArgsConstructor
public class ActNodeAssigneeServiceImpl extends ServiceImpl<ActNodeAssigneeMapper, ActNodeAssignee> implements IActNodeAssigneeService {

    private final RepositoryService repositoryService;

    @Override
    public ActNodeAssignee add(ActNodeAssignee actNodeAssignee) {
        baseMapper.insert(actNodeAssignee);
        return actNodeAssignee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActNodeAssignee edit(ActNodeAssignee actNodeAssignee) {
        try {
            baseMapper.insert (actNodeAssignee);
        }catch (Exception e){
            throw  new ServiceException("操作失败");
        }
        return  actNodeAssignee;
    }

    @Override
    public ActNodeAssignee getInfo(String processDefinitionId, String nodeId) {
        LambdaQueryWrapper<ActNodeAssignee> wrapper = new LambdaQueryWrapper();
        wrapper.eq(ActNodeAssignee::getProcessDefinitionId,processDefinitionId);
        wrapper.eq(ActNodeAssignee::getNodeId,nodeId);
        ActNodeAssignee nodeAssignee = baseMapper.selectOne(wrapper);
        return nodeAssignee;
    }

    @Override
    public List<ActNodeAssignee> getInfoByProcessDefinitionId(String processDefinitionId) {
        LambdaQueryWrapper<ActNodeAssignee> wrapper = new LambdaQueryWrapper();
        wrapper.eq(ActNodeAssignee::getProcessDefinitionId,processDefinitionId);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Boolean del(String id) {
        int i = baseMapper.deleteById(id);
        return i==1?true:false;
    }

    @Override
    public Boolean delByDefinitionId(String definitionId) {
        LambdaQueryWrapper<ActNodeAssignee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActNodeAssignee::getProcessDefinitionId,definitionId);
        List<ActNodeAssignee> list = baseMapper.selectList(queryWrapper);
        int i = baseMapper.delete(queryWrapper);
        if(list.size()!=i){
            throw new ServiceException("删除失败");
        }
        return true;
    }

    @Override
    public Boolean delByDefinitionIdAndNodeId(String definitionId, String nodeId) {
        LambdaQueryWrapper<ActNodeAssignee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActNodeAssignee::getProcessDefinitionId,definitionId);
        queryWrapper.eq(ActNodeAssignee::getNodeId,nodeId);
        List<ActNodeAssignee> list = baseMapper.selectList(queryWrapper);
        int i = baseMapper.delete(queryWrapper);
        if(list.size()!=i){
            throw new ServiceException("删除失败");
        }
        return true;
    }

    @Override
    public Boolean copy(String id,String key) {
        LambdaQueryWrapper<ActNodeAssignee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActNodeAssignee::getProcessDefinitionId,id);
        List<ActNodeAssignee> oldNodeAssigneeList = baseMapper.selectList(wrapper);

        if(CollectionUtil.isNotEmpty(oldNodeAssigneeList)){
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
            if(ObjectUtil.isEmpty(processDefinition)){
                throw new ServiceException("流程定义不存在");
            }
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            List<Process> processes = bpmnModel.getProcesses();
            List<ActProcessNodeVo> processNodeVoList = new ArrayList<>();
            Collection<FlowElement> elements = processes.get(0).getFlowElements();
            for (FlowElement element : elements) {
                ActProcessNodeVo actProcessNodeVo = new ActProcessNodeVo();
                if (element instanceof UserTask) {
                    actProcessNodeVo.setNodeId(element.getId());
                    actProcessNodeVo.setNodeName(element.getName());
                    actProcessNodeVo.setProcessDefinitionId(processDefinition.getId());
                    processNodeVoList.add(actProcessNodeVo);
                }
            }
            delByDefinitionId(processDefinition.getId());
            List<ActNodeAssignee> actNodeAssigneeList = new ArrayList<>();
            for (ActNodeAssignee oldNodeAssignee : oldNodeAssigneeList) {
                ActProcessNodeVo actProcessNodeVo = processNodeVoList.stream().filter(e -> e.getNodeId().equals(oldNodeAssignee.getNodeId())).findFirst().orElse(null);
                if(ObjectUtil.isNotEmpty(actProcessNodeVo)){

                    ActNodeAssignee actNodeAssignee = new ActNodeAssignee();
                    BeanUtils.copyProperties(oldNodeAssignee,actNodeAssignee);
                    actNodeAssignee.setId("");
                    actNodeAssignee.setProcessDefinitionId(processDefinition.getId());
                    actNodeAssigneeList.add(actNodeAssignee);
                }
            }
            if(CollectionUtil.isNotEmpty(actNodeAssigneeList)){
                saveBatch(actNodeAssigneeList);
            }
            return true;
        }else{
            return false;
        }
    }
}
