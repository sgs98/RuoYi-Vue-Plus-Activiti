package com.ruoyi.workflow.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.domain.bo.DefinitionBo;
import com.ruoyi.workflow.domain.vo.ActProcessNodeVo;
import com.ruoyi.workflow.domain.vo.ProcessDefinitionVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author gssong
 */
public interface IProcessDefinitionService {
    /**
     * 查询流程定义列表
     * @param defReq
     * @return
     */
    TableDataInfo<ProcessDefinitionVo> getByPage(DefinitionBo defReq);

    /**
     * 删除流程定义
     * @param deploymentId
     * @param definitionId
     * @return
     */
    R<Void> deleteDeployment(String deploymentId,String definitionId);

    /**
     * 通过zip或xml部署流程定义
     * @param file
     * @return
     */
    R<Void> deployByFile(MultipartFile file);

    /**
     * 导出流程定义文件（xml,png)
     * @param type
     * @param definitionId
     * @param response
     */
    void exportFile(String type, String definitionId, HttpServletResponse response);

    /**
     * 查看xml文件
     * @param definitionId
     * @return
     */
    String getXml(String definitionId);

    /**
     * 查询历史流程定义列表
     * @param defReq
     * @return
     */
    List<ProcessDefinitionVo> getHisByPage(DefinitionBo defReq);

    /**
     * 激活或者挂起流程定义
     * @param data
     * @return
     */
    Boolean updateProcDefState(Map<String,Object> data);

    /**
     * 查询流程环节
     * @param processDefinitionId
     * @return
     */
    R<List<ActProcessNodeVo>> setting(String processDefinitionId);
}
