package com.ruoyi.workflow.controller;

import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;

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
import com.ruoyi.workflow.domain.vo.ActProcessDefSettingVo;
import com.ruoyi.workflow.domain.bo.ActProcessDefSettingBo;
import com.ruoyi.workflow.service.IActProcessDefSetting;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 流程定义设置Controller
 *
 * @author gssong
 * @date 2022-08-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/processDefSetting")
public class ActProcessDefSettingController extends BaseController {

    private final IActProcessDefSetting iActProcessDefSetting;

    /**
     * 查询流程定义设置列表
     */
    @GetMapping("/list")
    public TableDataInfo<ActProcessDefSettingVo> list(ActProcessDefSettingBo bo, PageQuery pageQuery) {
        return iActProcessDefSetting.queryPageList(bo, pageQuery);
    }

    /**
     * 导出流程定义设置列表
     */
    @Log(title = "流程定义设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ActProcessDefSettingBo bo, HttpServletResponse response) {
        List<ActProcessDefSettingVo> list = iActProcessDefSetting.queryList(bo);
        ExcelUtil.exportExcel(list, "流程定义设置", ActProcessDefSettingVo.class, response);
    }

    /**
     * 获取流程定义设置详细信息
     */
    @GetMapping("/{id}")
    public R<ActProcessDefSettingVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(iActProcessDefSetting.queryById(id));
    }

    /**
     * 按流程定义id查询流程定义设置详细
     */
    @GetMapping("/getProcessDefSettingByDefId/{defId}")
    public R<ActProcessDefSettingVo> getProcessDefSettingByDefId(@NotNull(message = "流程定义id不能为空") @PathVariable("defId") String defId) {
        return R.ok(iActProcessDefSetting.getProcessDefSettingByDefId(defId));
    }

    /**
     * 校验表单是否关联
     */
    @GetMapping("/checkProcessDefSetting/{defId}/{param}/{businessType}")
    public R<List<Long>> checkProcessDefSetting(@NotNull(message = "流程定义id不能为空") @PathVariable("defId") String defId,
                                                                @NotNull(message = "参数不能为空") @PathVariable("param") String param,
                                                                @NotNull(message = "业务类型") @PathVariable("businessType") Integer businessType) {
        return iActProcessDefSetting.checkProcessDefSetting(defId, param, businessType);
    }

    /**
     * 新增流程定义设置
     */
    @Log(title = "流程定义设置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ActProcessDefSettingBo bo) {
        return toAjax(iActProcessDefSetting.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改流程定义设置
     */
    @Log(title = "流程定义设置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ActProcessDefSettingBo bo) {
        return toAjax(iActProcessDefSetting.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除流程定义设置
     */
    @Log(title = "流程定义设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iActProcessDefSetting.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }
}
