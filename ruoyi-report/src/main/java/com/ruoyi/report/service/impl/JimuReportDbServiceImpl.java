package com.ruoyi.report.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import com.ruoyi.report.domain.JimuReportDb;
import com.ruoyi.report.mapper.JimuReportDbMapper;
import com.ruoyi.report.service.IJimuReportDbService;


/**
 * 报表数据Service业务层处理
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@Service
public class JimuReportDbServiceImpl extends ServiceImpl<JimuReportDbMapper, JimuReportDb> implements IJimuReportDbService{

    @Override
    public TableDataInfo<JimuReportDb> queryPageList(JimuReportDb bo, PageQuery pageQuery) {
        LambdaQueryWrapper<JimuReportDb> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getJimuReportId()), JimuReportDb::getJimuReportId, bo.getJimuReportId());
        lqw.eq(StringUtils.isNotBlank(bo.getDbCode()), JimuReportDb::getDbCode, bo.getDbCode());
        lqw.like(StringUtils.isNotBlank(bo.getDbChName()), JimuReportDb::getDbChName, bo.getDbChName());
        Page<JimuReportDb> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }
}
