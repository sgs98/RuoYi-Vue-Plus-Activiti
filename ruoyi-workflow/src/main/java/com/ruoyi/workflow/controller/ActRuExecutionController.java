package com.ruoyi.workflow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.workflow.domain.bo.ActRuExecutionBo;
import com.ruoyi.workflow.domain.vo.ActRuExecutionVo;
import com.ruoyi.workflow.service.IActRuExecutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 流程执行Controller
 *
 * @author gssong
 * @date 2022-02-08
 */
@Validated
@Api(value = "流程执行控制器", tags = {"流程执行管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/workflow/ruExecution")
public class ActRuExecutionController extends BaseController {

    private final IActRuExecutionService iActRuExecutionService;

    /**
     * 查询流程执行列表
     */
    @ApiOperation("查询流程执行列表")
    @SaCheckPermission("workflow:ruExecution:list")
    @GetMapping("/list")
    public TableDataInfo<ActRuExecutionVo> list(@Validated(QueryGroup.class) ActRuExecutionBo bo, PageQuery pageQuery) {
        return iActRuExecutionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出流程执行列表
     */
    @ApiOperation("导出流程执行列表")
    @SaCheckPermission("workflow:ruExecution:export")
    @Log(title = "流程执行", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated ActRuExecutionBo bo, HttpServletResponse response) {
        List<ActRuExecutionVo> list = iActRuExecutionService.queryList(bo);
        ExcelUtil.exportExcel(list, "流程执行", ActRuExecutionVo.class, response);
    }

    /**
     * 获取流程执行详细信息
     */
    @ApiOperation("获取流程执行详细信息")
    @SaCheckPermission("workflow:ruExecution:query")
    @GetMapping("/{id}")
    public R<ActRuExecutionVo> getInfo(@ApiParam("主键")
                                                  @NotNull(message = "主键不能为空")
                                                  @PathVariable("id") String id) {
        return R.ok(iActRuExecutionService.queryById(id));
    }

    /**
     * 新增流程执行
     */
    @ApiOperation("新增流程执行")
    @SaCheckPermission("workflow:ruExecution:add")
    @Log(title = "流程执行", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActRuExecutionBo bo) {
        return toAjax(iActRuExecutionService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改流程执行
     */
    @ApiOperation("修改流程执行")
    @SaCheckPermission("workflow:ruExecution:edit")
    @Log(title = "流程执行", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActRuExecutionBo bo) {
        return toAjax(iActRuExecutionService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除流程执行
     */
    @ApiOperation("删除流程执行")
    @SaCheckPermission("workflow:ruExecution:remove")
    @Log(title = "流程执行" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable String[] ids) {
        return toAjax(iActRuExecutionService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
