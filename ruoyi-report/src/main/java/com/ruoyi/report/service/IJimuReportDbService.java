package com.ruoyi.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.report.domain.JimuReportDb;
import com.ruoyi.common.core.domain.PageQuery;
/**
 * 报表数据Service接口
 *
 * @author ruoyi
 * @date 2022-08-07
 */
public interface IJimuReportDbService extends IService<JimuReportDb> {

    /**
     * 查询报表数据列表
     */
    TableDataInfo<JimuReportDb> queryPageList(JimuReportDb bo, PageQuery pageQuery);
}
