package com.ruoyi.report.controller;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.report.domain.JimuReportDb;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.report.service.IJimuReportDbService;

/**
 * 报表数据Controller
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/report/reportDb")
public class JimuReportDbController extends BaseController {

    private final IJimuReportDbService iJimuReportDbService;

    /**
     * 查询报表数据列表
     */
    @GetMapping("/list")
    public TableDataInfo<JimuReportDb> list(JimuReportDb bo, PageQuery pageQuery) {
        return iJimuReportDbService.queryPageList(bo, pageQuery);
    }

}
