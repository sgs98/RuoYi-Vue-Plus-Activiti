package com.ruoyi.workflow.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.domain.bo.ModeBo;
import com.ruoyi.workflow.domain.bo.ModelREQ;
import org.activiti.engine.repository.Model;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface IModelService {


    /**
     * 保存模型
     * @param data
     * @return
     */
    R<Void> saveModelXml(ModeBo data);

    /**
     * 查询模型信息
     * @param modelId
     * @return
     */
    R<Map<String,Object>> getEditorXml(String modelId);

    /**
     * 分页查询模型
     * @param modelReq
     * @return
     */
    TableDataInfo<Model> getByPage(ModelREQ modelReq);

    /**
     * 新增模型对象
     * @param data
     * @return
     */
    R<Model> add(ModeBo data);

    /**
     * 通过流程定义模型id部署流程定义
     * @param id
     * @return
     */
    R<Void> deploy(String id);

    /**
     * 导出流程定义模型zip压缩包
     * @param modelId
     * @param response
     */
    void exportZip(String modelId, HttpServletResponse response);

    /**
     * 将流程定义转换为模型
     * @param processDefinitionId
     * @return
     */
    Boolean convertToModel(String processDefinitionId);
}
