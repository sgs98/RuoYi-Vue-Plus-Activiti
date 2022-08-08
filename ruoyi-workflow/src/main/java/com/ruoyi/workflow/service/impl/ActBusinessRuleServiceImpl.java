package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ValidatorUtils;
import com.ruoyi.workflow.domain.ActBusinessRuleParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.workflow.domain.bo.ActBusinessRuleBo;
import com.ruoyi.workflow.domain.vo.ActBusinessRuleVo;
import com.ruoyi.workflow.domain.ActBusinessRule;
import com.ruoyi.workflow.mapper.ActBusinessRuleMapper;
import com.ruoyi.workflow.service.IActBusinessRuleService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 业务规则Service业务层处理
 *
 * @author gssong
 * @date 2021-12-16
 */
@RequiredArgsConstructor
@Service
public class ActBusinessRuleServiceImpl implements IActBusinessRuleService {

    private final ActBusinessRuleMapper baseMapper;

    @Override
    public ActBusinessRuleVo queryById(Long id){
        ActBusinessRuleVo vo = baseMapper.selectVoById(id);
        if(StringUtils.isNotBlank(vo.getParam())){
            List<ActBusinessRuleParam> params = JsonUtils.parseArray(vo.getParam(), ActBusinessRuleParam.class);
            vo.setBusinessRuleParams(params);
        }
        return vo;
    }

    @Override
    public TableDataInfo<ActBusinessRuleVo> queryPageList(ActBusinessRuleBo bo, PageQuery pageQuery) {
        Page<ActBusinessRuleVo> result = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(result);
    }

    @Override
    public List<ActBusinessRuleVo> queryList(ActBusinessRuleBo bo) {
        return baseMapper.selectVoList(buildQueryWrapper(bo));
    }

    private LambdaQueryWrapper<ActBusinessRule> buildQueryWrapper(ActBusinessRuleBo bo) {
        LambdaQueryWrapper<ActBusinessRule> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBeanName()), ActBusinessRule::getBeanName, bo.getBeanName());
        lqw.like(StringUtils.isNotBlank(bo.getMethod()), ActBusinessRule::getMethod, bo.getMethod());
        return lqw;
    }

    @Override
    public Boolean insertByBo(ActBusinessRuleBo bo) {
        List<ActBusinessRuleParam> businessRuleParams = bo.getBusinessRuleParams();
        ActBusinessRule add = BeanUtil.toBean(bo, ActBusinessRule.class);
        if(CollectionUtil.isNotEmpty(businessRuleParams)){
            add.setParam(JsonUtils.toJsonString(businessRuleParams));
        }
        int flag = baseMapper.insert(add);
        if (flag>0) {
            bo.setId(add.getId());
        }
        return flag>0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(ActBusinessRuleBo bo) {
        List<ActBusinessRuleParam> businessRuleParams = bo.getBusinessRuleParams();
        ActBusinessRule update = BeanUtil.toBean(bo, ActBusinessRule.class);
        if(CollectionUtil.isNotEmpty(businessRuleParams)){
            update.setParam(JsonUtils.toJsonString(businessRuleParams));
        }
        return baseMapper.updateById(update)>0;
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids)>0;
    }
}
