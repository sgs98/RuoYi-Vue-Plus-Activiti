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
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.workflow.domain.vo.SysMessageVo;
import com.ruoyi.workflow.domain.bo.SysMessageBo;
import com.ruoyi.workflow.service.ISysMessageService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 消息通知Controller
 *
 * @author gssong
 * @date 2022-06-17
 */
@Validated
@Api(value = "消息通知控制器", tags = {"消息通知管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/message")
public class SysMessageController extends BaseController {

    private final ISysMessageService iSysMessageService;

    /**
     * 查询消息通知列表
     */
    @ApiOperation("查询消息通知列表")
    @SaCheckPermission("workflow:message:list")
    @GetMapping("/list")
    public TableDataInfo<SysMessageVo> list(@Validated(QueryGroup.class) SysMessageBo bo, PageQuery pageQuery) {
        return iSysMessageService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出消息通知列表
     */
    @ApiOperation("导出消息通知列表")
    @SaCheckPermission("workflow:message:export")
    @Log(title = "消息通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated SysMessageBo bo, HttpServletResponse response) {
        List<SysMessageVo> list = iSysMessageService.queryList(bo);
        ExcelUtil.exportExcel(list, "消息通知", SysMessageVo.class, response);
    }

    /**
     * 获取消息通知详细信息
     */
    @ApiOperation("获取消息通知详细信息")
    @SaCheckPermission("workflow:message:query")
    @GetMapping("/{id}")
    public R<SysMessageVo> getInfo(@ApiParam("主键")
                                                  @NotNull(message = "主键不能为空")
                                                  @PathVariable("id") Long id) {
        return R.ok(iSysMessageService.queryById(id));
    }

    /**
     * 新增消息通知
     */
    @ApiOperation("新增消息通知")
    @SaCheckPermission("workflow:message:add")
    @Log(title = "消息通知", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysMessageBo bo) {
        return toAjax(iSysMessageService.sendMessage(bo) ? 1 : 0);
    }

    /**
     * 修改消息通知
     */
    @ApiOperation("修改消息通知")
    @SaCheckPermission("workflow:message:edit")
    @Log(title = "消息通知", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysMessageBo bo) {
        return toAjax(iSysMessageService.updateMessage(bo) ? 1 : 0);
    }

    /**
     * 删除消息通知
     */
    @ApiOperation("删除消息通知")
    @SaCheckPermission("workflow:message:remove")
    @Log(title = "消息通知", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Long[] ids) {
        return toAjax(iSysMessageService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
    /**
     * @Description:  阅读消息
     * @param: id
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2022/6/19 17:08
     */
    @ApiOperation("阅读消息")
    @Log(title = "消息通知", businessType = BusinessType.INSERT)
    @GetMapping("/readMessage/{id}")
    public R<Void> readMessage(@PathVariable Long id) {
        return toAjax(iSysMessageService.readMessage(id) ? 1 : 0);
    }

    /**
     * @Description:  批量阅读消息
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2022/6/19 17:08
     */
    @ApiOperation("批量阅读消息")
    @Log(title = "消息通知", businessType = BusinessType.INSERT)
    @GetMapping("/batchReadMessage")
    public R<Void> batchReadMessage() {
        return toAjax(iSysMessageService.batchReadMessage() ? 1 : 0);
    }
}
