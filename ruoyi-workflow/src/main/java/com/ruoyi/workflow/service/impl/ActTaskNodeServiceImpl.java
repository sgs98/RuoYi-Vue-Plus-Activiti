package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.mapper.ActTaskNodeMapper;
import com.ruoyi.workflow.service.IActTaskNodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: ruoyi-vue-plus
 * @description: 节点名称业务层
 * @author: gssong
 * @created: 2021/11/06 16:29
 */
@Service
public class ActTaskNodeServiceImpl extends ServiceImpl<ActTaskNodeMapper, ActTaskNode> implements IActTaskNodeService {
    @Override
    public List<ActTaskNode> getListByInstanceId(String processInstanceId) {
        LambdaQueryWrapper<ActTaskNode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActTaskNode::getInstanceId, processInstanceId);
        queryWrapper.orderByDesc(ActTaskNode::getOrderNo);
        List<ActTaskNode> list = this.baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public ActTaskNode getListByInstanceIdAndNodeId(String processInstanceId, String nodeId) {
        LambdaQueryWrapper<ActTaskNode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActTaskNode::getInstanceId, processInstanceId);
        queryWrapper.eq(ActTaskNode::getNodeId, nodeId);
        ActTaskNode actTaskNode = this.baseMapper.selectOne(queryWrapper);
        return actTaskNode;
    }

    @Override
    public Boolean deleteBackTaskNode(String processInstanceId, String targetActivityId) {
        try {
            LambdaQueryWrapper<ActTaskNode> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ActTaskNode::getInstanceId, processInstanceId);
            queryWrapper.eq(ActTaskNode::getNodeId, targetActivityId);
            ActTaskNode actTaskNode = this.baseMapper.selectOne(queryWrapper);
            Integer orderNo = actTaskNode.getOrderNo();
            List<ActTaskNode> taskNodeList = getListByInstanceId(processInstanceId);

            List<String> ids = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(taskNodeList)) {
                for (ActTaskNode taskNode : taskNodeList) {
                    if (taskNode.getOrderNo() >= orderNo) {
                        ids.add(taskNode.getId());
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(ids)) {
                this.baseMapper.deleteBatchIds(ids);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByInstanceId(String processInstanceId) {
        LambdaQueryWrapper<ActTaskNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActTaskNode::getInstanceId,processInstanceId);
        List<ActTaskNode> list = baseMapper.selectList(wrapper);
        int delete = baseMapper.delete(wrapper);
        if(list.size()!=delete){
            throw new ServiceException("删除失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskNode(ActTaskNode actTaskNode) {
        List<ActTaskNode> list = getListByInstanceId(actTaskNode.getInstanceId());
        if(list.size()>0){
            ActTaskNode taskNode = list.stream().filter(e -> e.getNodeId().equals(actTaskNode.getNodeId()) && e.getOrderNo() == 0).findFirst().orElse(null);
            if(ObjectUtil.isEmpty(taskNode)){
                LambdaQueryWrapper<ActTaskNode> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ActTaskNode::getInstanceId, actTaskNode.getInstanceId());
                queryWrapper.eq(ActTaskNode::getNodeId, actTaskNode.getNodeId());
                baseMapper.delete(queryWrapper);
                List<ActTaskNode> nodeList = getListByInstanceId(actTaskNode.getInstanceId());
                actTaskNode.setOrderNo(nodeList.get(0).getOrderNo()+1);
                save(actTaskNode);
            }
        }
    }
}
