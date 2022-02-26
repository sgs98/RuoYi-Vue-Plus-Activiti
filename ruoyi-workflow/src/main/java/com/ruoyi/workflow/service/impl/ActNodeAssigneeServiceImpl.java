package com.ruoyi.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.mapper.ActNodeAssigneeMapper;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义设置业务层
 * @author: gssong
 * @created: 2021/11/21
 */
@Service
public class ActNodeAssigneeServiceImpl extends ServiceImpl<ActNodeAssigneeMapper, ActNodeAssignee> implements IActNodeAssigneeService {

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
}
