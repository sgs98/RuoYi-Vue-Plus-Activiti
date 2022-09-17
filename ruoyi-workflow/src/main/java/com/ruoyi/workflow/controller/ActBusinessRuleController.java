package com.ruoyi.workflow.controller;

import java.util.List;
import java.util.Arrays;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.PageQuery;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.workflow.domain.vo.ActBusinessRuleVo;
import com.ruoyi.workflow.domain.bo.ActBusinessRuleBo;
import com.ruoyi.workflow.service.IActBusinessRuleService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 业务规则Controller
 *
 * @author gssong
 * @date 2021-12-16
 */
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/workflow/businessRule")
public class ActBusinessRuleController extends BaseController {

    private final IActBusinessRuleService iActBusinessRuleService;

    /**
     * 查询业务规则列表
     */
    @SaCheckPermission("workflow:businessRule:list")
    @GetMapping("/list")
    public TableDataInfo<ActBusinessRuleVo> list(@Validated(QueryGroup.class) ActBusinessRuleBo bo, PageQuery pageQuery) {
        return iActBusinessRuleService.queryPageList(bo,pageQuery);
    }

    /**
     * 导出业务规则列表
     */
    @SaCheckPermission("workflow:businessRule:export")
    @Log(title = "业务规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated ActBusinessRuleBo bo, HttpServletResponse response) {
        List<ActBusinessRuleVo> list = iActBusinessRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "业务规则", ActBusinessRuleVo.class, response);
    }

    /**
     * 获取业务规则详细信息
     */
    @SaCheckPermission("workflow:businessRule:query")
    @GetMapping("/{id}")
    public R<ActBusinessRuleVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(iActBusinessRuleService.queryById(id));
    }

    /**
     * 新增业务规则
     */
    @SaCheckPermission("workflow:businessRule:add")
    @Log(title = "业务规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActBusinessRuleBo bo) {
        return toAjax(iActBusinessRuleService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改业务规则
     */
    @SaCheckPermission("workflow:businessRule:edit")
    @Log(title = "业务规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActBusinessRuleBo bo) {
        return toAjax(iActBusinessRuleService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除业务规则
     */
    @SaCheckPermission("workflow:businessRule:remove")
    @Log(title = "业务规则" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iActBusinessRuleService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
