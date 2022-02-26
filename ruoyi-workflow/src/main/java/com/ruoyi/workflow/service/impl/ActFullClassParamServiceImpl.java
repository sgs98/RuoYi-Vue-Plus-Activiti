package com.ruoyi.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.domain.ActFullClassParam;
import com.ruoyi.workflow.mapper.ActFullClassParamMapper;
import com.ruoyi.workflow.service.IActFullClassParamService;

import java.util.List;

/**
 * 方法参数Service业务层处理
 *
 * @author gssong
 * @date 2021-12-17
 */
@Service
public class ActFullClassParamServiceImpl extends ServiceImpl<ActFullClassParamMapper, ActFullClassParam> implements IActFullClassParamService {


    @Override
    public List<ActFullClassParam> queryListByFullClassId(Long fullClassId) {
        LambdaQueryWrapper<ActFullClassParam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActFullClassParam::getFullClassId,fullClassId);
        queryWrapper.orderByAsc(ActFullClassParam::getOrderNo);
        List<ActFullClassParam> list = baseMapper.selectList(queryWrapper);
        return list;
    }
}
