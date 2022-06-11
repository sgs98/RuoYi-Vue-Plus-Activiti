package com.ruoyi.workflow.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;

import java.util.List;

/**
 * 业务状态实体Service接口
 *
 * @author gssong
 * @date 2021-10-10
 */
public interface IActBusinessStatusService extends IService<ActBusinessStatus> {
    /**
     * 更新业务状态
     * @param businessKey 业务id
     * @param statusEnum 状态值
     * @param procInstId 流程实例id
     * @param classFullName 全类名
     * @return
     */
    boolean updateState(String businessKey, BusinessStatusEnum statusEnum, String procInstId,String classFullName);

    /**
     * 更新业务状态
     * @param businessKey 业务id
     * @param statusEnum 状态值
     * @param processInstanceId 流程实例id
     * @return
     */
    boolean updateState(String businessKey, BusinessStatusEnum statusEnum, String processInstanceId);

    /**
     * 更新业务状态
     * @param businessKey 业务id
     * @param statusEnum 状态值
     * @return
     */
    boolean updateState(String businessKey, BusinessStatusEnum statusEnum);

    /**
     * 根据业务id查询流程实例
     * @param businessKey
     * @return
     */
    ActBusinessStatus getInfoByBusinessKey(String businessKey);

    /**
     * 根据业务id查询流程实例
     * @param businessKeys
     * @return
     */
    List<ActBusinessStatus> getListInfoByBusinessKey(List<String> businessKeys);

    /**
     * 删除业务状态
     * @param businessKey 业务id
     * @return
     */
    boolean deleteState(String businessKey);

    /**
     * 根据流程实例id查询流程实例
     * @param processInstanceId 流程实例id
     * @return
     */
    ActBusinessStatus getInfoByProcessInstId(String processInstanceId);

    /**
     * 根据流程实例ids查询流程实例
     * @param processInstanceIds 流程实例id
     * @return
     */
    List<ActBusinessStatus> getInfoByProcessInstIds(List<String> processInstanceIds);
}
