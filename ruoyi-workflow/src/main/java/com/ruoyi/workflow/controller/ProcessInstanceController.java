package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workflow.domain.bo.ProcessInstFinishREQ;
import com.ruoyi.workflow.domain.bo.ProcessInstRunningREQ;
import com.ruoyi.workflow.domain.bo.StartREQ;
import com.ruoyi.workflow.domain.vo.ActHistoryInfoVo;
import com.ruoyi.workflow.domain.vo.ProcessInstFinishVo;
import com.ruoyi.workflow.domain.vo.ProcessInstRunningVo;
import com.ruoyi.workflow.service.IProcessInstanceService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程实例
 * @author: gssong
 * @created: 2021/10/10 18:36
 */
@Validated
@Api(value = "流程实例控制器", tags = {"流程实例管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/processInstance")
public class ProcessInstanceController {

    private final IProcessInstanceService processInstanceService;

    /**
     * @Description: 提交申请，启动流程实例
     * @param: startReq
     * @return: com.ruoyi.common.core.domain.R<java.util.Map<java.lang.String,java.lang.Object>>
     * @author: gssong
     * @Date: 2021/10/10
     */
    @ApiOperation("提交申请，启动流程实例")
    @Log(title = "流程实例管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/startWorkFlow")
    public R<Map<String,Object>> startWorkFlow(@RequestBody StartREQ startReq){
        Map<String,Object> map = processInstanceService.startWorkFlow(startReq);
        return R.ok("提交成功",map);
    }



    /**
     * @Description: 通过流程实例id查询流程审批记录
     * @param: processInstanceId
     * @return: com.ruoyi.common.core.domain.R<java.util.List<com.ruoyi.workflow.domain.vo.ActHistoryInfoVo>>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("通过流程实例id查询流程审批记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstanceId",value = "流程实例id",required = true,dataTypeClass = String.class)
    })
    @GetMapping("/getHistoryInfoList/{processInstanceId}")
    public R<List<ActHistoryInfoVo>> getHistoryInfoList(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstanceId){
        List<ActHistoryInfoVo> historyInfoList = processInstanceService.getHistoryInfoList(processInstanceId);
        return R.ok(historyInfoList);
    }

    /**
     * @Description: 通过流程实例id获取历史流程图
     * @param: processInstId
     * @param: response
     * @return: void
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("通过流程实例id获取历史流程图")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstanceId",value = "流程实例id",required = true,dataTypeClass = String.class)
    })
    @Anonymous
    @GetMapping("/getHistoryProcessImage")
    public void getHistoryProcessImage(@NotBlank(message = "流程实例id不能为空") @RequestParam String processInstanceId,
                                              HttpServletResponse response) {
         processInstanceService.getHistoryProcessImage(processInstanceId, response);
    }

    /**
     * @Description: 查询正在运行的流程实例
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.ProcessInstRunningVo>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("查询正在运行的流程实例")
    @GetMapping("/getProcessInstRunningByPage")
    public TableDataInfo<ProcessInstRunningVo> getProcessInstRunningByPage(ProcessInstRunningREQ req){
        return processInstanceService.getProcessInstRunningByPage(req);
    }

    /**
     * @Description: 挂起或激活流程实例
     * @param: data
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("挂起或激活流程实例")
    @Log(title = "流程实例管理", businessType = BusinessType.UPDATE)
    @PutMapping("/state")
    public R<Void> updateProcInstState(@RequestBody Map<String,Object> data){
        try {
            processInstanceService.updateProcInstState(data);
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.fail();
        }
    }

    /**
     * @Description: 作废流程实例，不会删除历史记录
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("作废流程实例，不会删除历史记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstId",value = "流程实例id",required = true,dataTypeClass = String.class)
    })
    @Log(title = "流程实例管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteRuntimeProcessInst/{processInstId}")
    public R<Boolean> deleteRuntimeProcessInst(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        boolean b = processInstanceService.deleteRuntimeProcessInst(processInstId);
        if(b){
            return R.ok();
        }else{
            return R.fail();
        }
    }

    /**
     * @Description: 运行中的实例 删除程实例，删除历史记录，删除业务与流程关联信息
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("删除运行中的实例，删除历史记录，删除业务与流程关联信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstId",value = "流程实例id",required = true,dataTypeClass = String.class)
    })
    @Log(title = "流程实例管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteRuntimeProcessAndHisInst/{processInstId}")
    public R<Void> deleteRuntimeProcessAndHisInst(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        boolean b = processInstanceService.deleteRuntimeProcessAndHisInst(processInstId);
        if(b){
            return R.ok();
        }else{
            return R.fail();
        }
    }

    /**
     * @Description: 已完成的实例 删除程实例，删除历史记录，删除业务与流程关联信息
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("删除已完成的实例，删除历史记录，删除业务与流程关联信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstId",value = "流程实例id",required = true,dataTypeClass = String.class)
    })
    @Log(title = "流程实例管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteFinishProcessAndHisInst/{processInstId}")
    public R<Void> deleteFinishProcessAndHisInst(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        boolean b = processInstanceService.deleteFinishProcessAndHisInst(processInstId);
        if(b){
            return R.ok();
        }else{
            return R.fail();
        }
    }

    /**
     * @Description: 撤销申请
     * @param processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2022/1/21
     */
    @ApiOperation("撤销申请")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstId",value = "流程实例id",required = true,dataTypeClass = String.class)
    })
    @Log(title = "流程实例管理", businessType = BusinessType.INSERT)
    @GetMapping("/cancelProcessApply/{processInstId}")
    public R<Void> cancelProcessApply(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        boolean b = processInstanceService.cancelProcessApply(processInstId);
        if(b){
            return R.ok();
        }else{
            return R.fail();
        }
    }

    /**
     * @Description: 查询已结束的流程实例
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.ProcessInstFinishVo>
     * @Author: gssong
     * @Date: 2021/10/23
     */
    @ApiOperation("查询已结束的流程实例")
    @GetMapping("/getProcessInstFinishByPage")
    public TableDataInfo<ProcessInstFinishVo> getProcessInstFinishByPage(ProcessInstFinishREQ req) {
        return processInstanceService.getProcessInstFinishByPage(req);
    }

}
