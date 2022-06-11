package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workflow.domain.bo.ModelREQ;
import com.ruoyi.workflow.service.IModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Validated
@Api(value = "模型控制器", tags = {"模型控制器"})
@RestController
@RequestMapping("/workflow/model")
public class ModelController extends BaseController {


    @Autowired
    private IModelService iModelService;

    @Autowired
    private RepositoryService repositoryService;


    /**
     * @Description:  保存模型
     * @param: data
     * @return: void
     * @author: gssong
     * @Date: 2022/5/22 13:47
     */
    @PutMapping
    @ApiOperation("保存模型")
    @Log(title = "保存模型", businessType = BusinessType.INSERT)
    @RepeatSubmit
    public R<Void> saveModelXml(@RequestBody Map<String,String> data) {
        return iModelService.saveModelXml(data);
    }

    /**
     * @Description: 查询模型信息
     * @param: modelId 模型id
     * @return: com.ruoyi.common.core.domain.R<java.lang.String>
     * @author: gssong
     * @Date: 2022/5/22 13:42
     */
    @GetMapping("/getInfo/{modelId}/xml")
    public R<Map<String,Object>> getEditorXml(@PathVariable String modelId) {
        return iModelService.getEditorXml(modelId);
    }

    /**
     * @Description: 查询模型列表
     * @param: modelReq 请求参数
     * @return: com.ruoyi.common.core.page.TableDataInfo<org.activiti.engine.repository.Model>
     * @Author: gssong
     * @Date: 2021/10/3
     */
    @ApiOperation("查询模型列表")
    @GetMapping("/list")
    public TableDataInfo<Model> getByPage(ModelREQ modelReq) {
        return iModelService.getByPage(modelReq);
    }


    /**
     * @Description: 新建模型
     * @param: data
     * @return: com.ruoyi.common.core.domain.R<org.activiti.engine.repository.Model>
     * @Author: gssong
     * @Date: 2021/10/3
     */
    @ApiOperation("新建模型")
    @Log(title = "模型管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Model> add(@RequestBody Map<String,String> data) {
        return iModelService.add(data);
    }

    /**
     * @Description: 通过流程定义模型id部署流程定义
     * @param: id 模型id
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/3
     */
    @ApiOperation("通过流程定义模型id部署流程定义")
    @Log(title = "模型管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/deploy/{id}")
    public R<Void> deploy(@PathVariable("id") String id) {
        return iModelService.deploy(id);
    }

    /**
     * @Description: 删除流程定义模型
     * @param: id 模型id
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/3
     */
    @ApiOperation("删除流程定义模型")
    @Log(title = "模型管理", businessType = BusinessType.DELETE)
    @RepeatSubmit
    @DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> add(@NotEmpty(message = "主键不能为空") @PathVariable String[] ids) {
        for (String id : ids) {
            repositoryService.deleteModel(id);
        }
        return R.ok();
    }

    /**
     * @Description: 导出流程定义模型zip压缩包
     * @param: modelId
     * @param: response
     * @return: void
     * @Author: gssong
     * @Date: 2021/10/7
     */
    @ApiOperation("导出流程定义模型zip压缩包")
    @GetMapping("/export/zip/{modelId}")
    public void exportZip(@PathVariable String modelId,
                          HttpServletResponse response) {
        iModelService.exportZip(modelId, response);
    }

    /**
     * @Description: 将流程定义转换为模型
     * @param: processDefinitionId 流程定义id
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/11/6
     */
    @ApiOperation("将流程定义转换为模型")
    @GetMapping("/convertToModel/{processDefinitionId}")
    public R<Void> convertToModel(@PathVariable String processDefinitionId){
        Boolean convertToModel = iModelService.convertToModel(processDefinitionId);
        return convertToModel==true?R.ok():R.fail();
    }

}
