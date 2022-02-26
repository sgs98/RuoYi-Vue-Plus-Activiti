package com.ruoyi.demo.controller;

import java.util.List;
import java.util.Arrays;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.demo.domain.BsLeave;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.demo.domain.vo.BsLeaveVo;
import com.ruoyi.demo.domain.bo.BsLeaveBo;
import com.ruoyi.demo.service.IBsLeaveService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 请假业务Controller
 *
 * @author gssong
 * @date 2022-01-11
 */
@Validated
@Api(value = "请假业务控制器", tags = {"请假业务管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/demo/leave")
public class BsLeaveController extends BaseController {

    private final IBsLeaveService iBsLeaveService;

    /**
     * 查询请假业务列表
     */
    @ApiOperation("查询请假业务列表")
    @SaCheckPermission("demo:leave:list")
    @GetMapping("/list")
    public TableDataInfo<BsLeaveVo> list(@Validated(QueryGroup.class) BsLeaveBo bo, PageQuery pageQuery) {
        return iBsLeaveService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出请假业务列表
     */
    @ApiOperation("导出请假业务列表")
    @SaCheckPermission("demo:leave:export")
    @Log(title = "请假业务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated BsLeaveBo bo, HttpServletResponse response) {
        List<BsLeaveVo> list = iBsLeaveService.queryList(bo);
        ExcelUtil.exportExcel(list, "请假业务", BsLeaveVo.class, response);
    }

    /**
     * 获取请假业务详细信息
     */
    @ApiOperation("获取请假业务详细信息")
    @SaCheckPermission("demo:leave:query")
    @GetMapping("/{id}")
    public R<BsLeaveVo> getInfo(@ApiParam("主键")
                                         @NotNull(message = "主键不能为空")
                                         @PathVariable("id") String id) {
        return R.ok(iBsLeaveService.queryById(id));
    }

    /**
     * 新增请假业务
     */
    @ApiOperation("新增请假业务")
    @SaCheckPermission("demo:leave:add")
    @Log(title = "请假业务", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<BsLeave> add(@Validated(AddGroup.class) @RequestBody BsLeaveBo bo) {
        return R.ok(iBsLeaveService.insertByBo(bo));
    }

    /**
     * 修改请假业务
     */
    @ApiOperation("修改请假业务")
    @SaCheckPermission("demo:leave:edit")
    @Log(title = "请假业务", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<BsLeave> edit(@Validated(EditGroup.class) @RequestBody BsLeaveBo bo) {
        return R.ok(iBsLeaveService.updateByBo(bo));
    }

    /**
     * 删除请假业务
     */
    @ApiOperation("删除请假业务")
    @SaCheckPermission("demo:leave:remove")
    @Log(title = "请假业务" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                   @NotEmpty(message = "主键不能为空")
                                   @PathVariable String[] ids) {
        return toAjax(iBsLeaveService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
