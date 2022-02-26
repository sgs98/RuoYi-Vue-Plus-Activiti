package com.ruoyi.workflow.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.service.IActBusinessStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程与业务关联控制器
 * @author: gssong
 * @created: 2021/10/16 16:18
 */
@Validated
@Api(value = "流程与业务关联控制器", tags = {"流程与业务关联控制器"})
@RestController
@RequestMapping("/workflow/actBusiness")
public class ActBusinessStatusController {

    @Autowired
    private IActBusinessStatusService iActBusinessStatusService;

    /**
     * @Description: 根据业务id查询流程实例
     * @param: businessKey
     * @return: com.ruoyi.common.core.domain.R<com.ruoyi.workflow.domain.ActBusinessStatus>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("根据业务id查询流程实例")
    @GetMapping("/getInfoByBusinessKey/{businessKey}")
    public R<ActBusinessStatus> getInfoByBusinessId(@PathVariable String  businessKey){
        ActBusinessStatus actBusinessStatus = iActBusinessStatusService.getInfoByBusinessKey(businessKey);
        return R.ok(actBusinessStatus);
    }

}
