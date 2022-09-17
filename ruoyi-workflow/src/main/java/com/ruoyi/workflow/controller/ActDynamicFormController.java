package com.ruoyi.workflow.controller;

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
import com.ruoyi.workflow.domain.vo.ActDynamicFormVo;
import com.ruoyi.workflow.domain.bo.ActDynamicFormBo;
import com.ruoyi.workflow.service.IActDynamicFormService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 动态表单Controller
 *
 * @author gssong
 * @date 2022-08-11
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/dynamicForm")
public class ActDynamicFormController extends BaseController {

    private final IActDynamicFormService iActDynamicFormService;

    /**
     * 查询动态表单列表
     */
    @SaCheckPermission("workflow:dynamicForm:list")
    @GetMapping("/list")
    public TableDataInfo<ActDynamicFormVo> list(ActDynamicFormBo bo, PageQuery pageQuery) {
        return iActDynamicFormService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询启用动态表单列表
     */
    @GetMapping("/enableList")
    public TableDataInfo<ActDynamicFormVo> enableList(ActDynamicFormBo bo, PageQuery pageQuery) {
        return iActDynamicFormService.queryPageEnableList(bo, pageQuery);
    }

    /**
     * 导出动态表单列表
     */
    @SaCheckPermission("workflow:dynamicForm:export")
    @Log(title = "动态表单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ActDynamicFormBo bo, HttpServletResponse response) {
        List<ActDynamicFormVo> list = iActDynamicFormService.queryList(bo);
        ExcelUtil.exportExcel(list, "动态表单", ActDynamicFormVo.class, response);
    }

    /**
     * 获取动态表单详细信息
     */
    @SaCheckPermission("workflow:dynamicForm:query")
    @GetMapping("/{id}")
    public R<ActDynamicFormVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(iActDynamicFormService.queryById(id));
    }

    /**
     * 新增动态表单
     */
    @SaCheckPermission("workflow:dynamicForm:add")
    @Log(title = "动态表单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActDynamicFormBo bo) {
        return toAjax(iActDynamicFormService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改动态表单
     */
    @SaCheckPermission("workflow:dynamicForm:edit")
    @Log(title = "动态表单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActDynamicFormBo bo) {
        return toAjax(iActDynamicFormService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 修改动态表单
     */
    @SaCheckPermission("workflow:dynamicForm:edit")
    @Log(title = "动态表单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/editForm")
    public R<Void> editForm(@RequestBody ActDynamicFormBo bo) {
        return toAjax(iActDynamicFormService.editForm(bo) ? 1 : 0);
    }

    /**
     * 删除动态表单
     */
    @SaCheckPermission("workflow:dynamicForm:remove")
    @Log(title = "动态表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iActDynamicFormService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
