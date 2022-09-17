package com.ruoyi.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.workflow.domain.ActNodeAssignee;

import java.util.List;


/**
 * @program: ruoyi-vue-plus
 * @description: 流程节点人员设置接口
 * @author: gssong
 * @created: 2021/11/21
 */
public interface IActNodeAssigneeService extends IService<ActNodeAssignee> {

    /**
     * 保存流程节点人员设置
     * @param actNodeAssignee
     * @return
     */
    ActNodeAssignee add(ActNodeAssignee actNodeAssignee);

    /**
     * 按照流程定义id和流程节点id查询流程节点人员设置
     * @param processDefinitionId
     * @param nodeId
     * @return
     */
    ActNodeAssignee getInfo(String processDefinitionId, String nodeId);

    /**
     * 按照流程定义id和流程节点id查询流程节点人员设置
     * @param processDefinitionId
     * @param nodeId
     * @return
     */
    ActNodeAssignee getInfoSetting(String processDefinitionId, String nodeId);

    /**
     * 按照流程定义id查询
     * @param processDefinitionId
     * @return
     */
    List<ActNodeAssignee> getInfoByProcessDefinitionId(String processDefinitionId);

    /**
     * 删除流程节点人员设置
     * @param id
     * @return
     */
    Boolean del(String id);

    /**
     * 按照流程定义删除
     * @param definitionId
     * @return
     */
    Boolean delByDefinitionId(String definitionId);

    /**
     * 按照流程定义与节点删除删除
     * @param definitionId
     * @param nodeId
     * @return
     */
    Boolean delByDefinitionIdAndNodeId(String definitionId,String nodeId);

    /**
     * 复制给最新流程节点人员设置
     * @param id
     * @param key
     * @return
     */
    Boolean copy(String id,String key);
}
