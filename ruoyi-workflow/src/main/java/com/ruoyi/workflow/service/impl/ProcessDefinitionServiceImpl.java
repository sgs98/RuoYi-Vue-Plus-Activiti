package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.domain.bo.DefREQ;
import com.ruoyi.workflow.domain.vo.ActProcessNodeVo;
import com.ruoyi.workflow.domain.vo.ProcessDefinitionVo;
import com.ruoyi.workflow.activiti.factory.WorkflowService;
import com.ruoyi.workflow.mapper.ProcessDefinitionMapper;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import com.ruoyi.workflow.service.IProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
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
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义服务层
 * @author: gssong
 * @created: 2021/10/07 11:14
 */
@Service
@Slf4j
public class ProcessDefinitionServiceImpl extends WorkflowService implements IProcessDefinitionService {

    @Autowired
    private IActNodeAssigneeService iActNodeAssigneeService;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * @Description: 查询流程定义列表
     * @param: defReq
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.ProcessDefinitionVo>
     * @author: gssong
     * @Date: 2021/10/7
     */
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

    /**
     * @Description: 查询历史流程定义列表
     * @param: defReq
     * @return: java.util.List<com.ruoyi.workflow.domain.vo.ProcessDefinitionVo>
     * @author: gssong
     * @Date: 2021/10/7
     */
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
        return CollectionUtil.reverse(processDefinitionVoList);
    }

    /**
     * @Description: 删除流程定义
     * @param: deploymentId
     * @param: definitionId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2021/10/7
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deleteDeployment(String deploymentId,String definitionId) {
        try {
            repositoryService.deleteDeployment(deploymentId);
            iActNodeAssigneeService.delByDefinitionId(definitionId);
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.fail();
        }
    }

    /**
     * @Description: 通过zip或xml部署流程定义
     * @param: file
     * @return: com.ruoyi.common.core.domain.R
     * @author: gssong
     * @Date: 2022/4/12 13:32
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
            throw new ServiceException("部署失败"+e.getMessage());
        }
    }

    /**
     * @Description: 导出流程定义文件（xml,png)
     * @param: type 类型 xml 或 png
     * @param: definitionId 流程定义id
     * @param: response
     * @return: void
     * @Author: gssong
     * @Date: 2021/10/7
     */
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

    /**
     * @Description: 查看xml文件
     * @param: definitionId
     * @return: java.lang.String
     * @author: gssong
     * @Date: 2022/5/3 19:34
     */
    @Override
    public String getXml(String definitionId) {
        StringBuilder xml = new StringBuilder();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(definitionId);
        InputStream inputStream = null;
        try {
            inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
            xml.append(IOUtils.toString(inputStream,ActConstant.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xml.toString();
    }

    /**
     * @Description: 激活或者挂起流程定义
     * @param: data 参数
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @Author: gssong
     * @Date: 2021/10/10
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProcDefState(Map<String,Object> data) {
        try {
            String definitionId = data.get("definitionId").toString();
            String description = data.get("description").toString();
            ProcessDefinitionMapper processDefinitionMapper = sqlSessionTemplate.getMapper(ProcessDefinitionMapper.class);
            //更新原因
            processDefinitionMapper.updateDescriptionById(definitionId,description);
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
            throw new ServiceException("操作失败");
        }
    }

    /**
     * @Description: 查询流程环节
     * @param: processDefinitionId
     * @return: com.ruoyi.common.core.domain.R<java.util.List<com.ruoyi.workflow.domain.vo.ActProcessNodeVo>>
     * @author: gssong
     * @Date: 2021/11/19
     */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public R<List<ActProcessNodeVo>> setting(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes = bpmnModel.getProcesses();
        List<ActProcessNodeVo> processNodeVoList = new ArrayList<>();
        Collection<FlowElement> elements = processes.get(0).getFlowElements();
        //获取开始节点后第一个节点
        ActProcessNodeVo firstNode = new ActProcessNodeVo();
        for (FlowElement element : elements) {
            if(element instanceof StartEvent){
                List<SequenceFlow> outgoingFlows = ((StartEvent) element).getOutgoingFlows();
                for (SequenceFlow outgoingFlow : outgoingFlows) {
                    FlowElement flowElement = outgoingFlow.getTargetFlowElement();
                    if(flowElement instanceof UserTask){
                        firstNode.setNodeId(flowElement.getId());
                        firstNode.setNodeName(flowElement.getName());
                        firstNode.setProcessDefinitionId(processDefinitionId);
                        firstNode.setIndex(0);
                    }
                }
            }
        }
        processNodeVoList.add(firstNode);
        for (FlowElement element : elements) {
            ActProcessNodeVo actProcessNodeVo = new ActProcessNodeVo();
            if (element instanceof UserTask && !firstNode.getNodeId().equals(element.getId())) {
                actProcessNodeVo.setNodeId(element.getId());
                actProcessNodeVo.setNodeName(element.getName());
                actProcessNodeVo.setProcessDefinitionId(processDefinitionId);
                actProcessNodeVo.setIndex(1);
                processNodeVoList.add(actProcessNodeVo);
            }else if(element instanceof SubProcess){
                Collection<FlowElement> flowElements = ((SubProcess) element).getFlowElements();
                for (FlowElement flowElement : flowElements) {
                    ActProcessNodeVo actProcessNode= new ActProcessNodeVo();
                    if (flowElement instanceof UserTask && !firstNode.getNodeId().equals(flowElement.getId())) {
                        actProcessNode.setNodeId(flowElement.getId());
                        actProcessNode.setNodeName(flowElement.getName());
                        actProcessNode.setProcessDefinitionId(processDefinitionId);
                        actProcessNode.setIndex(1);
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
        actNodeAssignee.setIndex(0);
        iActNodeAssigneeService.delByDefinitionIdAndNodeId(actProcessNodeVo.getProcessDefinitionId(),actProcessNodeVo.getNodeId());
        iActNodeAssigneeService.add(actNodeAssignee);
        return R.ok(processNodeVoList);

    }
}
