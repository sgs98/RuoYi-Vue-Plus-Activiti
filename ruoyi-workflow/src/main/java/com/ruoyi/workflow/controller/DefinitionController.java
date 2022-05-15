package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workflow.domain.bo.DefREQ;
import com.ruoyi.workflow.domain.vo.ActProcessNodeVo;
import com.ruoyi.workflow.domain.vo.ProcessDefinitionVo;
import com.ruoyi.workflow.service.IDefinitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义
 * @author: gssong
 * @created: 2021/10/07 11:12
 */
@Validated
@Api(value = "流程定义控制器", tags = {"流程定义控制器"})
@RestController
@RequestMapping("/workflow/definition")
public class DefinitionController extends BaseController {

    @Autowired
    private IDefinitionService iDefinitionService;

    /**
     * @Description: 查询流程定义列表
     * @param: defReq
     * @return: com.ruoyi.common.core.domain.R<java.util.List<com.ruoyi.workflow.domain.vo.ProcessDefinitionVo>>
     * @Author: gssong
     * @Date: 2021/10/7
     */
    @ApiOperation("查询流程定义列表")
    @GetMapping("/list")
    public TableDataInfo<ProcessDefinitionVo> getByPage(DefREQ defReq){
        return iDefinitionService.getByPage(defReq);
    }

    /**
     * @Description: 查询历史流程定义列表
     * @param: defReq
     * @return: com.ruoyi.common.core.domain.R<java.util.List<com.ruoyi.workflow.domain.vo.ProcessDefinitionVo>>
     * @Author: gssong
     * @Date: 2021/10/7
     */
    @ApiOperation("查询历史流程定义列表")
    @GetMapping("/hisList")
    public R<List<ProcessDefinitionVo>> getHisByPage(DefREQ defReq){
        List<ProcessDefinitionVo> definitionVoList= iDefinitionService.getHisByPage(defReq);
        return R.ok(definitionVoList);
    }


    /**
     * @Description: 删除流程定义
     * @param: deploymentId
     * @param: definitionId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/7
     */
    @ApiOperation("删除流程定义")
    @DeleteMapping("/{deploymentId}/{definitionId}")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @RepeatSubmit
    public R<Void> deleteDeployment(@PathVariable String deploymentId,@PathVariable String definitionId) {
        return iDefinitionService.deleteDeployment(deploymentId,definitionId);
    }


    /**
     * @Description: 通过zip或xml部署流程定义
     * @param: file
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2022/4/12 13:32
     */
    @ApiOperation("通过zip或xml部署流程定义")
    @PostMapping("/deployByFile")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @RepeatSubmit
    public R<Void> deployByFile(@RequestParam("file") MultipartFile file) {
        return iDefinitionService.deployByFile(file);
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
    @ApiOperation("导出流程定义文件（xml,png)")
    @GetMapping("/export/{type}/{definitionId}")
    public void exportFile(@PathVariable String type,
                           @PathVariable String definitionId,
                           HttpServletResponse response) {
        iDefinitionService.exportFile(type,definitionId,response);
    }

    /**
     * @Description: 查看xml文件
     * @param: definitionId
     * @return: com.ruoyi.common.core.domain.R<java.lang.String>
     * @author: gssong
     * @Date: 2022/5/3 19:25
     */
    @ApiOperation("查看xml文件")
    @GetMapping("/getXml/{definitionId}")
    public R<String> getXml(@PathVariable String definitionId) {
        String  xmlStr = iDefinitionService.getXml(definitionId);
        return R.ok("操作成功",xmlStr);
    }

    /**
     * @Description: 激活或者挂起流程定义
     * @param: definitionId 流程定义id
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @Author: gssong
     * @Date: 2021/10/10
     */
    @ApiOperation("激活或者挂起流程定义")
    @PutMapping("/updateProcDefState/{definitionId}")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    public R<Boolean> updateProcDefState(@PathVariable String definitionId){
        return R.ok(iDefinitionService.updateProcDefState(definitionId));
    }

    /**
     * @Description: 查询流程环节
     * @param: processDefinitionId
     * @return: com.ruoyi.common.core.domain.R<java.util.List<com.ruoyi.workflow.domain.vo.ActProcessNodeVo>>
     * @author: gssong
     * @Date: 2021/11/19
     */
    @ApiOperation("查询流程环节")
    @GetMapping("/setting/{processDefinitionId}")
    public R<List<ActProcessNodeVo>> setting(@PathVariable String processDefinitionId){
        return iDefinitionService.setting(processDefinitionId);
    }

}
