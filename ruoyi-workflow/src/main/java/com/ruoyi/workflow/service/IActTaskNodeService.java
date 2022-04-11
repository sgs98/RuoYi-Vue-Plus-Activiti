package com.ruoyi.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.workflow.domain.ActTaskNode;

import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务节点接口
 * @author: gssong
 * @created: 2021/11/06 16:27
 */
public interface IActTaskNodeService extends IService<ActTaskNode> {
    /**
     * 按流程实例id查询
     * @param processInstanceId
     * @return
     */
    List<ActTaskNode> getListByInstanceId(String processInstanceId);

    /**
     * 按流程实例id与节点id查询
     * @param processInstanceId
     * @param nodeId
     * @return
     */
    ActTaskNode getListByInstanceIdAndNodeId(String processInstanceId,String nodeId);

    /**
     * 删除驳回后的节点
     * @param processInstanceId
     * @param targetActivityId
     */
    Boolean deleteBackTaskNode(String processInstanceId, String targetActivityId);

    /**
     * 按流程实例id删除
     * @param processInstanceId
     * @return
     */
    Boolean deleteByInstanceId(String processInstanceId);

    /**
     * 保存完成的节点
     * @param actTaskNode
     * @return
     */
    void saveTaskNode(ActTaskNode actTaskNode);
}
