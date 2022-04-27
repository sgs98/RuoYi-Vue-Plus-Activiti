package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.domain.bo.DefREQ;
import com.ruoyi.workflow.domain.vo.ActProcessNodeVo;
import com.ruoyi.workflow.domain.vo.ProcessDefinitionVo;
import com.ruoyi.workflow.factory.WorkflowService;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import com.ruoyi.workflow.service.IDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义服务层
 * @author: gssong
 * @created: 2021/10/07 11:14
 */
@Service
@Slf4j
public class DefinitionServiceImpl extends WorkflowService implements IDefinitionService {

    @Autowired
    private IActNodeAssigneeService iActNodeAssigneeService;

    @Override
    public TableDataInfo<ProcessDefinitionVo> getByPage(DefREQ defReq) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotEmpty(defReq.getKey())) {
            query.processDefinitionKeyLike("%" + defReq.getKey() + "%");
        }
        if (StringUtils.isNotEmpty(defReq.getName())) {
            query.processDefinitionNameLike("%" + defReq.getName() + "%");
        }
        // 分页查询
        List<ProcessDefinitionVo> processDefinitionVoList = new ArrayList<>();
        List<ProcessDefinition> definitionList = query.latestVersion().listPage(defReq.getFirstResult(), defReq.getPageSize());
        for (ProcessDefinition processDefinition : definitionList) {
            // 部署时间
            Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(processDefinition.getDeploymentId()).singleResult();
            ProcessDefinitionVo processDefinitionVo = BeanUtil.toBean(processDefinition, ProcessDefinitionVo.class);
            processDefinitionVo.setDeploymentTime(deployment.getDeploymentTime());
            processDefinitionVoList.add(processDefinitionVo);
        }
        // 总记录数
        long total = query.count();

        return new TableDataInfo(processDefinitionVoList, total);
    }

    @Override
    public List<ProcessDefinitionVo> getHisByPage(DefREQ defReq) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotBlank(defReq.getKey())) {
            query.processDefinitionKey(defReq.getKey());
        }

        // 分页查询
        List<ProcessDefinitionVo> processDefinitionVoList = new ArrayList<>();
        List<ProcessDefinition> definitionList = query.list();
        for (ProcessDefinition processDefinition : definitionList) {
            if (!processDefinition.getId().equals(defReq.getId())) {
                // 部署时间
                Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(processDefinition.getDeploymentId()).singleResult();
                ProcessDefinitionVo processDefinitionVo = BeanUtil.toBean(processDefinition, ProcessDefinitionVo.class);
                processDefinitionVo.setDeploymentTime(deployment.getDeploymentTime());
                processDefinitionVoList.add(processDefinitionVo);
            }
        }
        return processDefinitionVoList;
    }

    @Override
    @Transactional
    public R<Void> deleteDeployment(String deploymentId,String definitionId) {
        //1.删除部署的流程定义
        repositoryService.deleteDeployment(deploymentId);
        iActNodeAssigneeService.delByDefinitionId(definitionId);
        return R.ok();
    }

    @Override
    public R deployByFile(MultipartFile file) {
        try {
            // 文件名+后缀名
            String filename = file.getOriginalFilename();
            // 文件后缀名
            String suffix = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            InputStream inputStream = file.getInputStream();
            DeploymentBuilder deployment = repositoryService.createDeployment();
            if (ActConstant.ZIP.equals(suffix)) {
                // zip
                deployment.addZipInputStream(new ZipInputStream(inputStream));
            } else {
                // xml 或 bpmn
                deployment.addInputStream(filename, inputStream);
            }
            // 部署名称
            deployment.name(filename.substring(0, filename.lastIndexOf(".")));

            // 开始部署
            deployment.deploy();

            return R.ok();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("部署失败：" + e.getMessage());
            return R.fail("部署失败");
        }
    }

    @Override
    public void exportFile(String type, String definitionId, HttpServletResponse response) {
        try {
            ProcessDefinition processDefinition = repositoryService.getProcessDefinition(definitionId);
            String resourceName = "文件不存在";

            if (ActConstant.XML.equals(type)) {
                //xml名称
                resourceName = processDefinition.getResourceName();
            } else if (ActConstant.PNG.equals(type)) {
                // 获取 png 图片资源名
                resourceName = processDefinition.getDiagramResourceName();
            }
            InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
            // 创建输出流
            response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(resourceName, ActConstant.UTF_8));
            // 流的拷贝放到设置请求头下面，不然文件大于10k可能无法导出
            IOUtils.copy(inputStream, response.getOutputStream());

            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出文件失败：{}", e.getMessage());
        }
    }

    @Override
    public Boolean updateProcDefState(String definitionId) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId).singleResult();
            if (processDefinition.isSuspended()) {
                // 将当前为挂起状态更新为激活状态
                // 参数说明：参数1：流程定义id,参数2：是否激活（true是否级联对应流程实例，激活了则对应流程实例都可以审批），
                // 参数3：什么时候激活，如果为null则立即激活，如果为具体时间则到达此时间后激活
                repositoryService.activateProcessDefinitionById(definitionId, true, null);
                return true;
            } else {
                // 将当前为激活状态更新为挂起状态
                // 参数说明：参数1：流程定义id,参数2：是否挂起（true是否级联对应流程实例，挂起了则对应流程实例都不可以审批），
                // 参数3：什么时候挂起，如果为null则立即挂起，如果为具体时间则到达此时间后挂起
                repositoryService.suspendProcessDefinitionById(definitionId, true, null);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("操作失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
	@Transactional(rollbackFor = Exception.class)
    public R<List<ActProcessNodeVo>> setting(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes = bpmnModel.getProcesses();
        List<ActProcessNodeVo> processNodeVoList = new ArrayList<>();
        Collection<FlowElement> elements = processes.get(0).getFlowElements();
        for (FlowElement element : elements) {
            ActProcessNodeVo actProcessNodeVo = new ActProcessNodeVo();
            if (element instanceof UserTask) {
                actProcessNodeVo.setNodeId(element.getId());
                actProcessNodeVo.setNodeName(element.getName());
                actProcessNodeVo.setProcessDefinitionId(processDefinitionId);
                processNodeVoList.add(actProcessNodeVo);
            }else if(element instanceof SubProcess){
                Collection<FlowElement> flowElements = ((SubProcess) element).getFlowElements();
                for (FlowElement flowElement : flowElements) {
                    ActProcessNodeVo actProcessNode= new ActProcessNodeVo();
                    if (flowElement instanceof UserTask) {
                        actProcessNode.setNodeId(flowElement.getId());
                        actProcessNode.setNodeName(flowElement.getName());
                        actProcessNode.setProcessDefinitionId(processDefinitionId);
                        processNodeVoList.add(actProcessNode);
                    }
                }
            }
        }
        ActProcessNodeVo actProcessNodeVo = processNodeVoList.get(0);
        ActNodeAssignee actNodeAssignee = new ActNodeAssignee();
        actNodeAssignee.setProcessDefinitionId(actProcessNodeVo.getProcessDefinitionId());
        actNodeAssignee.setNodeId(actProcessNodeVo.getNodeId());
        actNodeAssignee.setNodeName(actProcessNodeVo.getNodeName());
        actNodeAssignee.setIsShow(false);
        actNodeAssignee.setIsBack(true);
        actNodeAssignee.setMultiple(false);
        iActNodeAssigneeService.delByDefinitionIdAndNodeId(actProcessNodeVo.getProcessDefinitionId(),actProcessNodeVo.getNodeId());
        iActNodeAssigneeService.add(actNodeAssignee);
        processNodeVoList.remove(0);
        return R.ok(processNodeVoList);

    }
}
