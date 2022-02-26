package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ValidatorUtils;
import com.ruoyi.workflow.domain.ActFullClassParam;
import com.ruoyi.workflow.service.IActFullClassParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.workflow.domain.bo.ActFullClassBo;
import com.ruoyi.workflow.domain.vo.ActFullClassVo;
import com.ruoyi.workflow.domain.ActFullClass;
import com.ruoyi.workflow.mapper.ActFullClassMapper;
import com.ruoyi.workflow.service.IActFullClassService;
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
public class ActFullClassServiceImpl implements IActFullClassService {

    private final ActFullClassMapper baseMapper;
    @Autowired
    private IActFullClassParamService iActFullClassParamService;

    @Override
    public ActFullClassVo queryById(Long id){
        List<ActFullClassParam> list = iActFullClassParamService.queryListByFullClassId(id);
        ActFullClassVo vo = baseMapper.selectVoById(id);
        vo.setFullClassParam(list);
        return vo;
    }

    @Override
    public TableDataInfo<ActFullClassVo> queryPageList(ActFullClassBo bo,PageQuery pageQuery) {
        Page<ActFullClassVo> result = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(result);
    }

    @Override
    public List<ActFullClassVo> queryList(ActFullClassBo bo) {
        return baseMapper.selectVoList(buildQueryWrapper(bo));
    }

    private LambdaQueryWrapper<ActFullClass> buildQueryWrapper(ActFullClassBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ActFullClass> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFullClass()), ActFullClass::getFullClass, bo.getFullClass());
        lqw.like(StringUtils.isNotBlank(bo.getMethod()), ActFullClass::getMethod, bo.getMethod());
        return lqw;
    }

    @Override
    public Boolean insertByBo(ActFullClassBo bo) {
        ActFullClass add = BeanUtil.toBean(bo, ActFullClass.class);
        validEntityBeforeSave(add);
        int flag = baseMapper.insert(add);
        if (flag>0) {
            bo.setId(add.getId());
        }
        List<ActFullClassParam> actFullClassParams = bo.getFullClassParam();
        if(CollectionUtil.isNotEmpty(actFullClassParams)){
            actFullClassParams.forEach(e->{
                e.setFullClassId(add.getId());
                ValidatorUtils.validate(e, AddGroup.class);
            });
            iActFullClassParamService.saveBatch(actFullClassParams);
        }
        return flag>0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(ActFullClassBo bo) {
        ActFullClass update = BeanUtil.toBean(bo, ActFullClass.class);
        validEntityBeforeSave(update);
        List<ActFullClassParam> actFullClassParams = bo.getFullClassParam();
        iActFullClassParamService.remove(new LambdaQueryWrapper<ActFullClassParam>().eq(ActFullClassParam::getFullClassId,update.getId()));
        if(CollectionUtil.isNotEmpty(actFullClassParams)){
            actFullClassParams.forEach(e->{
                e.setFullClassId(update.getId());
                ValidatorUtils.validate(e, EditGroup.class);
            });
            iActFullClassParamService.saveBatch(actFullClassParams);
        }
        return baseMapper.updateById(update)>0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(ActFullClass entity){
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            iActFullClassParamService.remove(new LambdaQueryWrapper<ActFullClassParam>().in(ActFullClassParam::getFullClassId,ids));
        }
        return baseMapper.deleteBatchIds(ids)>0;
    }
}
