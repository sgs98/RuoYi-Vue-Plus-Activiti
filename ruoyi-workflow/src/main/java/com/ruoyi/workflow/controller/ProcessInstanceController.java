package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Log;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程实例
 * @author: gssong
 * @created: 2021/10/10 18:36
 */
@Validated
@Api(value = "流程实例控制器", tags = {"流程实例控制器"})
@RestController
@RequestMapping("/workflow/processInstance")
public class ProcessInstanceController {

    @Autowired
    private IProcessInstanceService processInstanceService;

    /**
     * @Description: 提交申请，启动流程实例
     * @param: startReq
     * @return: com.ruoyi.common.core.domain.R<java.util.Map<java.lang.String,java.lang.Object>>
     * @author: gssong
     * @Date: 2021/10/10
     */
    @ApiOperation("提交申请，启动流程实例")
    @PostMapping("/startWorkFlow")
    @Log(title = "流程实例", businessType = BusinessType.INSERT)
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
    @GetMapping("/getHistoryInfoList/{processInstanceId}")
    public R<List<ActHistoryInfoVo>> getHistoryInfoList(@PathVariable String processInstanceId){
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
    @GetMapping("/getHistoryProcessImage")
    public void getHistoryProcessImage(@RequestParam String processInstanceId,
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
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @Author: gssong
     * @Date: 2021/10/16
     */
    @ApiOperation("挂起或激活流程实例")
    @PutMapping("/state/{processInstId}")
    @Log(title = "流程实例", businessType = BusinessType.UPDATE)
    public R<Void> updateProcInstState(@PathVariable String processInstId){
        try {
            processInstanceService.updateProcInstState(processInstId);
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
    @DeleteMapping("/deleteRuntimeProcessInst/{processInstId}")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    public R<Boolean> deleteRuntimeProcessInst(@PathVariable String processInstId){
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
    @DeleteMapping("/deleteRuntimeProcessAndHisInst/{processInstId}")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    public R<Void> deleteRuntimeProcessAndHisInst(@PathVariable String processInstId){
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
    @DeleteMapping("/deleteFinishProcessAndHisInst/{processInstId}")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    public R<Void> deleteFinishProcessAndHisInst(@PathVariable String processInstId){
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
    @GetMapping("/cancelProcessApply/{processInstId}")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    public R<Void> cancelProcessApply(@PathVariable String processInstId){
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
