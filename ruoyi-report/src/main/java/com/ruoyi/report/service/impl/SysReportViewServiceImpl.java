package com.ruoyi.report.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.report.domain.bo.SysReportViewBo;
import com.ruoyi.report.domain.vo.SysReportViewVo;
import com.ruoyi.report.domain.SysReportView;
import com.ruoyi.report.mapper.SysReportViewMapper;
import com.ruoyi.report.service.ISysReportViewService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 报表查看Service业务层处理
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@RequiredArgsConstructor
@Service
public class SysReportViewServiceImpl implements ISysReportViewService {

    private final SysReportViewMapper baseMapper;

    /**
     * 查询报表查看
     */
    @Override
    public SysReportViewVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询报表查看列表
     */
    @Override
    public TableDataInfo<SysReportViewVo> queryPageList(SysReportViewBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysReportView> lqw = buildQueryWrapper(bo);
        Page<SysReportViewVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询报表查看列表
     */
    @Override
    public List<SysReportViewVo> queryList(SysReportViewBo bo) {
        LambdaQueryWrapper<SysReportView> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysReportView> buildQueryWrapper(SysReportViewBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysReportView> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getReportId() != null, SysReportView::getReportId, bo.getReportId());
        lqw.like(StringUtils.isNotBlank(bo.getReportName()), SysReportView::getReportName, bo.getReportName());
        lqw.orderByAsc(SysReportView::getOrderNo);
        return lqw;
    }

    /**
     * 新增报表查看
     */
    @Override
    public Boolean insertByBo(SysReportViewBo bo) {
        SysReportView add = BeanUtil.toBean(bo, SysReportView.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改报表查看
     */
    @Override
    public Boolean updateByBo(SysReportViewBo bo) {
        SysReportView update = BeanUtil.toBean(bo, SysReportView.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysReportView entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除报表查看
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
