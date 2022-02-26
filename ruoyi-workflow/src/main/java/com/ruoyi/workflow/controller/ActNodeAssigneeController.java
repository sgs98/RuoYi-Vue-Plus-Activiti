package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义设置控制层
 * @author: gssong
 * @created: 2021/11/21 13:48
 */
@Validated
@Api(value = "流程定义设置控制层", tags = {"流程定义设置控制层"})
@RestController
@RequestMapping("/workflow/actNodeAssignee")
public class ActNodeAssigneeController extends BaseController {

    @Autowired
    private IActNodeAssigneeService iActNodeAssigneeService;

    /**
     * @Description: 保存流程定义设置
     * @param: actNodeAssignee
     * @return: com.ruoyi.common.core.domain.R<com.ruoyi.workflow.domain.ActNodeAssignee>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @PostMapping
    @ApiOperation("保存流程定义设置")
    @Log(title = "保存流程定义设置", businessType = BusinessType.INSERT)
    public R<ActNodeAssignee> add(@Validated(AddGroup.class) @RequestBody ActNodeAssignee actNodeAssignee){
        return R.ok(iActNodeAssigneeService.add(actNodeAssignee));
    }

    /**
     * @Description: 修改流程定义设置
     * @param: actNodeAssignee
     * @return: com.ruoyi.common.core.domain.R<com.ruoyi.workflow.domain.ActNodeAssignee>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @PutMapping
    @ApiOperation("修改流程定义设置")
    @Log(title = "修改流程定义设置", businessType = BusinessType.UPDATE)
    public R<ActNodeAssignee> edit(@Validated(EditGroup.class)  @RequestBody ActNodeAssignee actNodeAssignee){
        return R.ok(iActNodeAssigneeService.edit(actNodeAssignee));
    }

    /**
     * @Description: 按照流程定义id和流程节点id查询流程定义设置
     * @param: actNodeAssignee
     * @return: com.ruoyi.common.core.domain.R<com.ruoyi.workflow.domain.ActNodeAssignee>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @GetMapping("/{processDefinitionId}/{nodeId}")
    @ApiOperation("按照流程定义id和流程节点id查询流程定义设置")
    public R<ActNodeAssignee> getInfo(@PathVariable String processDefinitionId,@PathVariable String nodeId){
        ActNodeAssignee nodeAssignee = iActNodeAssigneeService.getInfo(processDefinitionId,nodeId);
        return R.ok(nodeAssignee);
    }

    /**
     * @Description: 删除
     * @param: actNodeAssignee
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @DeleteMapping("{id}")
    @ApiOperation("删除")
    @Log(title = "删除", businessType = BusinessType.DELETE)
    public R<Void> del(@PathVariable String id){
        return toAjax(iActNodeAssigneeService.del(id) ? 1 : 0);
    }
}
