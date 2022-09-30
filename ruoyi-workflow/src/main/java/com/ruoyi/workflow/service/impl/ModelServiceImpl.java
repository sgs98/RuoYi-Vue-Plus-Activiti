package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.activiti.factory.WorkflowService;
import com.ruoyi.workflow.domain.bo.ModelBo;
import com.ruoyi.workflow.service.IModelService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.repository.*;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * @program: ruoyi-vue-plus
 * @description: 模型业务层
 * @author: gssong
 * @created: 2021/10/17 15:59
 */
@Service
@Slf4j
public class ModelServiceImpl extends WorkflowService implements IModelService {

    /**
     * @description: 保存模型
     * @param: data
     * @return: java.lang.Boolean
     * @author: gssong
     * @date: 2022/5/22 13:51
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveModelXml(ModelBo data) {
        try {
            String xml = data.getXml();
            String svg = data.getSvg();
            String modelId = data.getModelId();
            String key = data.getProcess().getId();
            String name = data.getProcess().getName();
            String category = data.getProcess().getCategory();
            JSONObject jsonObject = JSONUtil.xmlToJson(xml);
            JSONObject definitions = (JSONObject) jsonObject.get("definitions");
            String description = "";
            if(ObjectUtil.isNotEmpty(definitions)){
                JSONObject process = (JSONObject) definitions.get("process");
                if(process.containsKey("documentation")){
                    description = process.get("documentation").toString();
                }
            }
            Model model = repositoryService.getModel(modelId);
            List<Model> list = repositoryService.createModelQuery().list();
            List<Model> modelList = list.stream().filter(e -> !e.getId().equals(model.getId())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(modelList)){
                List<Model> models = modelList.stream().filter(e -> e.getKey().equals(key)).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(models)){
                    throw new ServiceException("模型KEY已存在！");
                }
            }
            model.setKey(key);
            model.setName(name);
            model.setVersion(model.getVersion()+1);
            model.setCategory(category);
            //封装模型json对象
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            objectNode.put(ModelDataJsonConstants.MODEL_REVISION, model.getVersion()+1);
            objectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            model.setMetaInfo(objectNode.toString());
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), StrUtil.utf8Bytes(xml));
            InputStream svgStream = new ByteArrayInputStream(StrUtil.utf8Bytes(svg));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            return true;
        } catch (Exception e) {
            throw new ActivitiException("Error saving model", e);
        }
    }

    /**
     * @description: 查询模型信息
     * @param: modelId
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @author: gssong
     * @date: 2022/5/22 13:54
     */
    @Override
    public Map<String,Object> getEditorXml(String modelId) {
        Map<String, Object> map = new HashMap<>(16);
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
                map.put("xml",StrUtil.utf8Str(modelEditorSource));
                map.put("model",model);
                return map;
            } catch (Exception e) {
                throw new ActivitiException(e.getMessage());
            }
        }
        return map;
    }

    /**
     * @description: 查询模型列表
     * @param: modelBo 请求参数
     * @return: com.ruoyi.common.core.page.TableDataInfo<org.activiti.engine.repository.Model>
     * @author: gssong
     * @date: 2021/10/3
     */
    @Override
    public TableDataInfo<Model> getByPage(ModelBo modelBo) {
        ModelQuery query = repositoryService.createModelQuery();
        if (StringUtils.isNotEmpty(modelBo.getName())) {
            query.modelNameLike("%" + modelBo.getName() + "%");
        }
        if (StringUtils.isNotEmpty(modelBo.getKey())) {
            query.modelKey(modelBo.getKey());
        }
        query.orderByLastUpdateTime().desc();
        //创建时间降序排列
        query.orderByCreateTime().desc();
        // 分页查询
        List<Model> modelList = query.listPage(modelBo.getFirstResult(), modelBo.getPageSize());
        if (CollectionUtil.isNotEmpty(modelList)) {
            modelList.forEach(e -> {
                boolean isNull = JSONUtil.isNull(JSONUtil.parseObj(e.getMetaInfo()).get(ModelDataJsonConstants.MODEL_DESCRIPTION));
                if (!isNull) {
                    e.setMetaInfo((String) JSONUtil.parseObj(e.getMetaInfo()).get(ModelDataJsonConstants.MODEL_DESCRIPTION));
                } else {
                    e.setMetaInfo("");
                }
            });
        }
        // 总记录数
        long total = query.count();
        return new TableDataInfo<>(modelList, total);
    }

    /**
     * @description: 新建模型
     * @param: modelAdd
     * @return: java.lang.Boolean
     * @author: gssong
     * @date: 2021/10/3
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(ModelBo data){
        try {
            String xml = data.getXml();
            String svg = data.getSvg();
            String key = data.getProcess().getId();
            String name = data.getProcess().getName();
            String category = data.getProcess().getCategory();
            if(StringUtils.isBlank(category)){
                throw new ServiceException("请选择流程分类！");
            }
            JSONObject jsonObject = JSONUtil.xmlToJson(xml);
            JSONObject definitions = (JSONObject) jsonObject.get("definitions");
            String description = "";
            if(ObjectUtil.isNotEmpty(definitions)){
                JSONObject process = (JSONObject) definitions.get("process");
                if(process.containsKey("documentation")){
                    description = process.get("documentation").toString();
                }
            }
            int version = 0;
            Model checkModel = repositoryService.createModelQuery().modelKey(key).singleResult();
            if(ObjectUtil.isNotNull(checkModel)){
                throw new ServiceException("模型KEY已存在！");
            }
            //初始空的模型
            Model model = repositoryService.newModel();
            model.setKey(key);
            model.setName(name);
            model.setVersion(version);
            model.setCategory(category);
            //封装模型json对象
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            objectNode.put(ModelDataJsonConstants.MODEL_REVISION, version);
            objectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            model.setMetaInfo(objectNode.toString());
            //保存初始化的模型基本信息数据
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), StrUtil.utf8Bytes(xml));
            InputStream svgStream = new ByteArrayInputStream(StrUtil.utf8Bytes(svg));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            return true;
        }catch (Exception e){
            throw new ActivitiException("Error saving model", e);
        }
    }

    /**
     * @description: 通过流程定义模型id部署流程定义
     * @param: id 模型id
     * @return: java.lang.Boolean
     * @author: gssong
     * @date: 2021/10/3
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deploy(String id){
        try {
            //1.查询流程定义模型xml
            byte[] xmlBytes = repositoryService.getModelEditorSource(id);
            if (xmlBytes == null) {
                throw new ServiceException("模型数据为空，请先设计流程定义模型，再进行部署！");
            }
            //2. 查询流程定义模型的图片
            byte[] pngBytes = repositoryService.getModelEditorSourceExtra(id);

            // 查询模型的基本信息
            Model model = repositoryService.getModel(id);
            // xml资源的名称 ，对应act_ge_bytearray表中的name_字段
            String processName = model.getName() + ".bpmn20.xml";
            // 图片资源名称，对应act_ge_bytearray表中的name_字段
            String pngName = model.getName() + "." + model.getKey() + ".png";

            //3. 调用部署相关的api方法进行部署流程定义
            Deployment deployment = repositoryService.createDeployment()
                // 部署名称
                .name(model.getName())
                // 部署标识key
                .key(model.getKey())
                // bpmn20.xml资源
                .addBytes(processName, xmlBytes)
                // png资源
                .addBytes(pngName, pngBytes)
                .deploy();

            // 更新 部署id 到流程定义模型数据表中
            model.setDeploymentId(deployment.getId());
            repositoryService.saveModel(model);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @description: 导出流程定义模型zip压缩包
     * @param: modelId
     * @param: response
     * @return: void
     * @author: gssong
     * @date: 2021/10/7
     */
    @Override
    public void exportZip(String modelId, HttpServletResponse response) {
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(response.getOutputStream());
            // 压缩包文件名
            String zipName = "模型不存在";
            //1.查询模型基本信息
            Model model = repositoryService.getModel(modelId);
            if (ObjectUtil.isNotNull(model)) {
                // 2. 查询流程定义模型的json字节码
                byte[] xmlBytes = repositoryService.getModelEditorSource(modelId);
                // 2.1 将json字节码转换为xml字节码
                if (xmlBytes == null) {
                    zipName = "模型数据为空-请先设计流程定义模型，再导出";
                } else {
                    // 压缩包文件名
                    zipName = model.getName() + "." + model.getKey() + ".zip";
                    // 将xml添加到压缩包中(指定xml文件名：请假流程.bpmn20.xml
                    zipos.putNextEntry(new ZipEntry(model.getName() + ".bpmn20.xml"));
                    zipos.write(xmlBytes);
                    //3.查询流程定义模型图片字节码
                    byte[] pngBytes = repositoryService.getModelEditorSourceExtra(modelId);
                    if (pngBytes != null) {
                        zipos.putNextEntry(new ZipEntry(model.getName() + "." + model.getKey() + ".png"));
                        zipos.write(pngBytes);
                    }
                }
            }
            response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(zipName, ActConstant.UTF_8) + ".zip");
            // 刷出响应流
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipos != null) {
                try {
                    zipos.closeEntry();
                    zipos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @description: 将流程定义转换为模型
     * @param: processDefinitionId 流程定义id
     * @return: java.lang.Boolean
     * @author: gssong
     * @date: 2021/11/6
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean convertToModel(String processDefinitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(processDefinitionId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
        Model model = repositoryService.createModelQuery().modelKey(pd.getKey()).singleResult();
            try {
                XMLInputFactory xif = XMLInputFactory.newInstance();
                InputStreamReader in = new InputStreamReader(bpmnStream, StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(in);
                BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
                BpmnXMLConverter converter = new BpmnXMLConverter();
                byte[] xmlBytes = converter.convertToXML(bpmnModel);
                if(ObjectUtil.isNotNull(model)){
                    repositoryService.addModelEditorSource(model.getId(), xmlBytes);
                    InputStream inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getDiagramResourceName());
                    if(inputStream!=null){
                        repositoryService.addModelEditorSourceExtra(model.getId(),IOUtils.toByteArray(inputStream));
                    }
                }else{
                    Model modelData = repositoryService.newModel();
                    modelData.setKey(pd.getKey());
                    modelData.setName(pd.getName());

                    ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
                    modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, pd.getName());
                    modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
                    modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, pd.getDescription());
                    modelData.setMetaInfo(modelObjectNode.toString());
                    repositoryService.saveModel(modelData);
                    repositoryService.addModelEditorSource(modelData.getId(), xmlBytes);
                    InputStream inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getDiagramResourceName());
                    if(inputStream!=null){
                        repositoryService.addModelEditorSourceExtra(modelData.getId(),IOUtils.toByteArray(inputStream));
                    }
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                throw new ServiceException(e.getMessage());
            }
    }
}
