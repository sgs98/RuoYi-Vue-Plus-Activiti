package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.service.IActNodeAssigneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotBlank;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程节点人员设置控制层
 * @author: gssong
 * @created: 2021/11/21 13:48
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/actNodeAssignee")
public class ActNodeAssigneeController extends BaseController {

    private final IActNodeAssigneeService iActNodeAssigneeService;

    /**
     * @Description: 保存流程节点人员设置
     * @param: actNodeAssignee
     * @return: com.ruoyi.common.core.domain.R<com.ruoyi.workflow.domain.ActNodeAssignee>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @Log(title = "流程节点人员设置管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<ActNodeAssignee> add(@Validated(AddGroup.class) @RequestBody ActNodeAssignee actNodeAssignee){
        return R.ok(iActNodeAssigneeService.add(actNodeAssignee));
    }

    /**
     * @Description: 按照流程定义id和流程节点id查询流程节点人员设置
     * @param: actNodeAssignee
     * @return: com.ruoyi.common.core.domain.R<com.ruoyi.workflow.domain.ActNodeAssignee>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @GetMapping("/{processDefinitionId}/{nodeId}")
    public R<ActNodeAssignee> getInfoSetting(@NotBlank(message = "流程定义id不能为空") @PathVariable String processDefinitionId,
                                             @NotBlank(message = "流程节点id不能为空") @PathVariable String nodeId){
        ActNodeAssignee nodeAssignee = iActNodeAssigneeService.getInfoSetting(processDefinitionId,nodeId);
        return R.ok(nodeAssignee);
    }

    /**
     * @Description: 删除流程节点人员设置
     * @param: id
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/11/21
     */
    @Log(title = "流程节点人员设置管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> del(@NotBlank(message = "主键不能为空") @PathVariable String id){
        return toAjax(iActNodeAssigneeService.del(id) ? 1 : 0);
    }

    /**
     * @Description: 复制给最新流程节点人员设置
     * @param: id 流程定义id
     * @param: key 流程定义key
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2022/03/26
     */
    @Log(title = "流程节点人员设置管理", businessType = BusinessType.INSERT)
    @PostMapping("/copy/{id}/{key}")
    public R<Void> copy(@NotBlank(message = "id不能为空") @PathVariable("id")  String id,
                        @NotBlank(message = "流程Key不能为空") @PathVariable("key") String key){
        Boolean copy = iActNodeAssigneeService.copy(id, key);
        if(copy){
            return R.ok();
        }
        return R.fail("当前流程未设置人员");
    }
}
