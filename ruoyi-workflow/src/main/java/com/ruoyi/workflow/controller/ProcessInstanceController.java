package com.ruoyi.workflow.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.workflow.domain.bo.ProcessInstFinishBo;
import com.ruoyi.workflow.domain.bo.ProcessInstRunningBo;
import com.ruoyi.workflow.domain.bo.StartProcessBo;
import com.ruoyi.workflow.domain.vo.ActHistoryInfoVo;
import com.ruoyi.workflow.domain.vo.ProcessInstFinishVo;
import com.ruoyi.workflow.domain.vo.ProcessInstRunningVo;
import com.ruoyi.workflow.service.IProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 流程实例
 *
 * @author gssong
 * @date 2021/10/10 18:36
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/processInstance")
public class ProcessInstanceController extends BaseController {

    private final IProcessInstanceService processInstanceService;

    /**
     * 启动流程实例
     * @param: startProcessBo
     * @return: com.ruoyi.common.core.domain.R<java.util.Map<java.lang.String,java.lang.Object>>
     * @author: gssong
     * @date: 2021/10/10
     */
    @Log(title = "流程实例管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/startWorkFlow")
    public R<Map<String,Object>> startWorkFlow(@RequestBody StartProcessBo startProcessBo){
        Map<String,Object> map = processInstanceService.startWorkFlow(startProcessBo);
        return R.ok("提交成功",map);
    }



    /**
     * 通过流程实例id查询流程审批记录
     * @param: processInstanceId
     * @return: com.ruoyi.common.core.domain.R<java.util.List<com.ruoyi.workflow.domain.vo.ActHistoryInfoVo>>
     * @author: gssong
     * @date: 2021/10/16
     */
    @GetMapping("/getHistoryInfoList/{processInstanceId}")
    public R<List<ActHistoryInfoVo>> getHistoryInfoList(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstanceId){
        List<ActHistoryInfoVo> historyInfoList = processInstanceService.getHistoryInfoList(processInstanceId);
        return R.ok(historyInfoList);
    }

    /**
     * 通过流程实例id获取历史流程图
     * @param: processInstId
     * @param: response
     * @return: void
     * @author: gssong
     * @date: 2021/10/16
     */
    @Anonymous
    @GetMapping("/getHistoryProcessImage")
    public void getHistoryProcessImage(@NotBlank(message = "流程实例id不能为空") @RequestParam String processInstanceId,
                                              HttpServletResponse response) {
         processInstanceService.getHistoryProcessImage(processInstanceId, response);
    }

    /**
     * 查询正在运行的流程实例
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.ProcessInstRunningVo>
     * @author: gssong
     * @date: 2021/10/16
     */
    @GetMapping("/getProcessInstRunningByPage")
    public TableDataInfo<ProcessInstRunningVo> getProcessInstRunningByPage(ProcessInstRunningBo req){
        return processInstanceService.getProcessInstRunningByPage(req);
    }

    /**
     * 挂起或激活流程实例
     * @param: data
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @date: 2021/10/16
     */
    @Log(title = "流程实例管理", businessType = BusinessType.UPDATE)
    @PutMapping("/state")
    public R<Void> updateProcInstState(@RequestBody Map<String,Object> data){
        return toAjax(processInstanceService.updateProcInstState(data));
    }

    /**
     * 作废流程实例，不会删除历史记录
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @date: 2021/10/16
     */
    @Log(title = "流程实例管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteRuntimeProcessInst/{processInstId}")
    public R<Void> deleteRuntimeProcessInst(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        return toAjax(processInstanceService.deleteRuntimeProcessInst(processInstId));
    }

    /**
     * 运行中的实例 删除程实例，删除历史记录，删除业务与流程关联信息
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @date: 2021/10/16
     */
    @Log(title = "流程实例管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteRuntimeProcessAndHisInst/{processInstId}")
    public R<Void> deleteRuntimeProcessAndHisInst(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        return toAjax(processInstanceService.deleteRuntimeProcessAndHisInst(processInstId));
    }

    /**
     * 已完成的实例 删除程实例，删除历史记录，删除业务与流程关联信息
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @date: 2021/10/16
     */
    @Log(title = "流程实例管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteFinishProcessAndHisInst/{processInstId}")
    public R<Void> deleteFinishProcessAndHisInst(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        return toAjax(processInstanceService.deleteFinishProcessAndHisInst(processInstId));
    }

    /**
     * 撤销申请
     * @param: processInstId
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @date: 2022/1/21
     */
    @Log(title = "流程实例管理", businessType = BusinessType.INSERT)
    @GetMapping("/cancelProcessApply/{processInstId}")
    public R<Void> cancelProcessApply(@NotBlank(message = "流程实例id不能为空") @PathVariable String processInstId){
        return toAjax(processInstanceService.cancelProcessApply(processInstId));
    }

    /**
     * 查询已结束的流程实例
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.ProcessInstFinishVo>
     * @author: gssong
     * @date: 2021/10/23
     */
    @GetMapping("/getProcessInstFinishByPage")
    public TableDataInfo<ProcessInstFinishVo> getProcessInstFinishByPage(ProcessInstFinishBo req) {
        return processInstanceService.getProcessInstFinishByPage(req);
    }

}
