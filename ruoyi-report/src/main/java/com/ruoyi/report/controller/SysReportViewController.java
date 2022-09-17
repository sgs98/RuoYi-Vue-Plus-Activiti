package com.ruoyi.report.controller;

import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.report.domain.vo.SysReportViewVo;
import com.ruoyi.report.domain.bo.SysReportViewBo;
import com.ruoyi.report.service.ISysReportViewService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报表查看Controller
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/report/reportView")
public class SysReportViewController extends BaseController {

    private final ISysReportViewService iSysReportViewService;

    /**
     * 查询报表查看列表
     */
    @SaCheckPermission("report:reportView:list")
    @GetMapping("/list")
    public TableDataInfo<SysReportViewVo> list(SysReportViewBo bo, PageQuery pageQuery) {
        return iSysReportViewService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出报表查看列表
     */
    @SaCheckPermission("report:reportView:export")
    @Log(title = "报表查看", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysReportViewBo bo, HttpServletResponse response) {
        List<SysReportViewVo> list = iSysReportViewService.queryList(bo);
        ExcelUtil.exportExcel(list, "报表查看", SysReportViewVo.class, response);
    }

    /**
     * 获取报表查看详细信息
     */
    @SaCheckPermission("report:reportView:query")
    @GetMapping("/{id}")
    public R<SysReportViewVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(iSysReportViewService.queryById(id));
    }

    /**
     * 新增报表查看
     */
    @SaCheckPermission("report:reportView:add")
    @Log(title = "报表查看", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysReportViewBo bo) {
        return toAjax(iSysReportViewService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改报表查看
     */
    @SaCheckPermission("report:reportView:edit")
    @Log(title = "报表查看", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysReportViewBo bo) {
        return toAjax(iSysReportViewService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除报表查看
     */
    @SaCheckPermission("report:reportView:remove")
    @Log(title = "报表查看", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iSysReportViewService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
