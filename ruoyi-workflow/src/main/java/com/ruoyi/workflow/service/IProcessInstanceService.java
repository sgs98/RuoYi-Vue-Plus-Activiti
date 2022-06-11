package com.ruoyi.workflow.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.domain.bo.ProcessInstFinishREQ;
import com.ruoyi.workflow.domain.bo.ProcessInstRunningREQ;
import com.ruoyi.workflow.domain.bo.StartREQ;
import com.ruoyi.workflow.domain.vo.ActHistoryInfoVo;
import com.ruoyi.workflow.domain.vo.ProcessInstFinishVo;
import com.ruoyi.workflow.domain.vo.ProcessInstRunningVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程实例接口
 * @author: gssong
 * @created: 2021/10/10 18:38
 */
public interface IProcessInstanceService {
    /**
     * 提交申请，启动流程实例
     * @param startReq
     * @return
     */
    Map<String,Object> startWorkFlow(StartREQ startReq);

    /**
     * 通过流程实例id查询流程审批记录
     * @param processInstanceId
     * @return
     */
    List<ActHistoryInfoVo> getHistoryInfoList(String processInstanceId);

    /**
     * 通过流程实例id获取历史流程图
     * @param processInstanceId
     * @param response
     */
    void getHistoryProcessImage(String processInstanceId, HttpServletResponse response);

    /**
     * 查询正在运行的流程实例
     * @param req
     * @return
     */
    TableDataInfo<ProcessInstRunningVo> getProcessInstRunningByPage(ProcessInstRunningREQ req);

    /**
     * 挂起或激活流程实例
     * @param data
     */
    void updateProcInstState(Map<String,Object> data);

    /**
     * 作废流程实例，不会删除历史记录
     * @param processInstId
     * @return
     */
    boolean deleteRuntimeProcessInst(String processInstId);

    /**
     * 删除运行中的实例，删除历史记录，删除业务与流程关联信息
     * @param processInstId
     * @return
     */
    boolean deleteRuntimeProcessAndHisInst(String processInstId);


    /**
     * 查询已结束的流程实例
     * @param req
     * @return
     */
    TableDataInfo<ProcessInstFinishVo> getProcessInstFinishByPage(ProcessInstFinishREQ req);

    /**
     * 删除已完成的实例，删除历史记录，删除业务与流程关联信息
     * @param processInstId
     * @return
     */
    boolean deleteFinishProcessAndHisInst(String processInstId);

    /**
     * 获取流程实例id
     * @param businessKey
     * @return
     */
    String getProcessInstanceId(String businessKey);

    /**
     * 撤销申请
     * @param processInstId
     * @return
     */
    boolean cancelProcessApply(String processInstId);

}
