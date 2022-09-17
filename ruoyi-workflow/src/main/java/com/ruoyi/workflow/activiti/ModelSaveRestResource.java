/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ruoyi.workflow.activiti;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.utils.WorkFlowUtils;
import lombok.RequiredArgsConstructor;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Tijs Rademakers
 */
@RestController
@Anonymous
@RequiredArgsConstructor
public class ModelSaveRestResource extends BaseController implements ModelDataJsonConstants {

  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

  private final RepositoryService repositoryService;

  private final ObjectMapper objectMapper;

    @RepeatSubmit
    @RequestMapping(value="/model/newModel", method = RequestMethod.POST)
    public R<Model> add(@RequestBody Map<String ,Object> modelData) {
        try {
            int version = 0;
            String key = modelData.get("key").toString();
            String name = modelData.get("name").toString();
            String description = "";
            if(modelData.containsKey("description")){
                description = modelData.get("description").toString();
            }

            Model checkModel = repositoryService.createModelQuery().modelKey(key).singleResult();
            if(ObjectUtil.isNotNull(checkModel)){
                return R.fail("模型key已存在",null);
            }
            // 1. 初始空的模型
            Model model = repositoryService.newModel();
            model.setKey(key);
            model.setName(name);
            model.setVersion(version);

            // 封装模型json对象
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            objectNode.put(ModelDataJsonConstants.MODEL_REVISION, version);
            objectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            model.setMetaInfo(objectNode.toString());
            // 保存初始化的模型基本信息数据
            repositoryService.saveModel(model);

            // 封装模型对象基础数据json串
            // {"id":"canvas","resourceId":"canvas","stencilset":{"namespace":"http://b3mn.org/stencilset/bpmn2.0#"},"properties":{"process_id":"未定义"}}
            ObjectNode editorNode = objectMapper.createObjectNode();
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", ActConstant.NAMESPACE);
            editorNode.replace("stencilset", stencilSetNode);
            // 标识key
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            propertiesNode.put("process_id", key);
            propertiesNode.put("name", name);
            editorNode.replace("properties", propertiesNode);
            repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
        return R.ok();
    }

  /**
   * @Description: 编辑模型
   * @param: modelId
   * @param: values
   * @return: void
   * @author: gssong
   * @Date: 2022/8/8
   */
  @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.PUT)
  @ResponseStatus(value = HttpStatus.OK)
  public void saveModel(@PathVariable String modelId, @RequestParam MultiValueMap<String, String> values) {
    try {

      Model model = repositoryService.getModel(modelId);

      ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

      modelJson.put(MODEL_NAME, values.getFirst("name"));
      modelJson.put(MODEL_DESCRIPTION, values.getFirst("description"));
      model.setMetaInfo(modelJson.toString());
      model.setName(values.getFirst("name"));
      model.setVersion(model.getVersion() + 1 ); // 每次保存把版本更新+1
      // 获取唯一标识key
      String key = objectMapper.readTree(values.getFirst("json_xml")).get("properties").get("process_id").textValue();
      model.setKey(key);


      repositoryService.saveModel(model);
      byte[] xmlBytes = WorkFlowUtils.bpmnJsonToXmlBytes(values.getFirst("json_xml").getBytes(StandardCharsets.UTF_8));

      repositoryService.addModelEditorSource(model.getId(), xmlBytes);

      InputStream svgStream = new ByteArrayInputStream(values.getFirst("svg_xml").getBytes(StandardCharsets.UTF_8));
      TranscoderInput input = new TranscoderInput(svgStream);

      PNGTranscoder transcoder = new PNGTranscoder();
      // Setup output
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      TranscoderOutput output = new TranscoderOutput(outStream);

      // Do the transformation
      transcoder.transcode(input, output);
      final byte[] result = outStream.toByteArray();
      repositoryService.addModelEditorSourceExtra(model.getId(), result);
      outStream.close();

    } catch (Exception e) {
      LOGGER.error("Error saving model", e);
      throw new ActivitiException("Error saving model", e);
    }
  }
}
