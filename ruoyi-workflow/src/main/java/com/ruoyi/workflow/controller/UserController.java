package com.ruoyi.workflow.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.domain.bo.SysUserBo;
import com.ruoyi.workflow.domain.bo.SysUserMultiBo;
import com.ruoyi.workflow.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
/**
 * @program: ruoyi-vue-plus
 * @description: 工作流用户信息控制器
 * @author: gssong
 * @created: 2022-03-25
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/user")
public class UserController {

    private final IUserService iUserService;

    /**
     * @Description: 分页查询工作流选人,角色，部门等
     * @param: sysUserBo
     * @return: com.ruoyi.common.core.domain.R<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: gssong
     * @Date: 2021/12/10
     */
    @PostMapping("/getWorkflowUserListByPage")
    public R<Map<String,Object>> getWorkflowUserListByPage(@RequestBody SysUserBo sysUserBo){
        Map<String,Object> map = iUserService.getWorkflowUserListByPage(sysUserBo);
        return R.ok(map);
    }

    /**
     * @Description: 分页查询工作流选择加签人员
     * @param: sysUserMultiBo
     * @return: com.ruoyi.common.core.domain.R<java.util.Map<java.lang.String,java.lang.Object>>
     * @author: gssong
     * @Date: 2022/4/22 21:17
     */
    @PostMapping("/getWorkflowAddMultiListByPage")
    public R<Map<String,Object>> getWorkflowAddMultiListByPage(@RequestBody SysUserMultiBo sysUserMultiBo){
        Map<String,Object> map = iUserService.getWorkflowAddMultiListByPage(sysUserMultiBo);
        return R.ok(map);
    }
}
