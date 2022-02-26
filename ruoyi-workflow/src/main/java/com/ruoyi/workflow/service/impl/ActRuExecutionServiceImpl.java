package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.workflow.domain.ActRuExecution;
import com.ruoyi.workflow.domain.bo.ActRuExecutionBo;
import com.ruoyi.workflow.domain.vo.ActRuExecutionVo;
import com.ruoyi.workflow.mapper.ActRuExecutionMapper;
import com.ruoyi.workflow.service.IActRuExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 流程执行Service业务层处理
 *
 * @author gssong
 * @date 2022-02-08
 */
@RequiredArgsConstructor
@Service
public class ActRuExecutionServiceImpl implements IActRuExecutionService {

    private final ActRuExecutionMapper baseMapper;
    @Override
    public ActRuExecutionVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<ActRuExecutionVo> queryPageList(ActRuExecutionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ActRuExecution> lqw = buildQueryWrapper(bo);
        Page<ActRuExecutionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<ActRuExecutionVo> queryList(ActRuExecutionBo bo) {
        LambdaQueryWrapper<ActRuExecution> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ActRuExecution> buildQueryWrapper(ActRuExecutionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ActRuExecution> lqw = Wrappers.lambdaQuery();
        return lqw;
    }

    @Override
    public Boolean insertByBo(ActRuExecutionBo bo) {
        ActRuExecution add = BeanUtil.toBean(bo, ActRuExecution.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add)>0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(ActRuExecutionBo bo) {
        ActRuExecution update = BeanUtil.toBean(bo, ActRuExecution.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update)>0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(ActRuExecution entity){
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids)>0;
    }

    @Override
    public List<ActRuExecution> selectRuExecutionByProcInstId(String procInstId) {
        LambdaQueryWrapper<ActRuExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActRuExecution::getProcInstId,procInstId);
        return baseMapper.selectList(queryWrapper);
    }
}
