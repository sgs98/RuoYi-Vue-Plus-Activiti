package com.ruoyi.workflow.controller;

import java.util.List;
import java.util.Arrays;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.PageQuery;
import io.swagger.annotations.*;
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
import com.ruoyi.workflow.domain.vo.ActFullClassVo;
import com.ruoyi.workflow.domain.bo.ActFullClassBo;
import com.ruoyi.workflow.service.IActFullClassService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 业务规则Controller
 *
 * @author gssong
 * @date 2021-12-16
 */
@Validated
@Api(value = "业务规则控制器", tags = {"业务规则管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/workflow/fullClass")
public class ActFullClassController extends BaseController {

    private final IActFullClassService iActFullClassService;

    /**
     * 查询业务规则列表
     */
    @ApiOperation("查询业务规则列表")
    @SaCheckPermission("workflow:fullClass:list")
    @GetMapping("/list")
    public TableDataInfo<ActFullClassVo> list(@Validated(QueryGroup.class) ActFullClassBo bo, PageQuery pageQuery) {
        return iActFullClassService.queryPageList(bo,pageQuery);
    }

    /**
     * 导出业务规则列表
     */
    @ApiOperation("导出业务规则列表")
    @SaCheckPermission("workflow:fullClass:export")
    @Log(title = "业务规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated ActFullClassBo bo, HttpServletResponse response) {
        List<ActFullClassVo> list = iActFullClassService.queryList(bo);
        ExcelUtil.exportExcel(list, "业务规则", ActFullClassVo.class, response);
    }

    /**
     * 获取业务规则详细信息
     */
    @ApiOperation("获取业务规则详细信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id",value = "主键",required = true,dataTypeClass = Long.class)
    })
    @SaCheckPermission("workflow:fullClass:query")
    @GetMapping("/{id}")
    public R<ActFullClassVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(iActFullClassService.queryById(id));
    }

    /**
     * 新增业务规则
     */
    @ApiOperation("新增业务规则")
    @SaCheckPermission("workflow:fullClass:add")
    @Log(title = "业务规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActFullClassBo bo) {
        return toAjax(iActFullClassService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改业务规则
     */
    @ApiOperation("修改业务规则")
    @SaCheckPermission("workflow:fullClass:edit")
    @Log(title = "业务规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActFullClassBo bo) {
        return toAjax(iActFullClassService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除业务规则
     */
    @ApiOperation("删除业务规则")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids",value = "主键串",required = true,dataTypeClass = Long.class,allowMultiple = true)
    })
    @SaCheckPermission("workflow:fullClass:remove")
    @Log(title = "业务规则" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iActFullClassService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
