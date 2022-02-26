package com.ruoyi.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.workflow.domain.ActHiActInst;
import com.ruoyi.workflow.mapper.ActHiActInstMapper;
import com.ruoyi.workflow.service.IActHiActInstService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程执行历史Service业务层处理
 *
 * @author gssong
 * @date 2022-01-23
 */
@Service
public class ActHiActInstServiceImpl extends ServiceImpl<ActHiActInstMapper, ActHiActInst> implements IActHiActInstService {

    @Override
    public void deleteActHiActInstByActId(String nodeId) {
        LambdaQueryWrapper<ActHiActInst> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActHiActInst::getActId,nodeId);
        baseMapper.delete(wrapper);
    }

    @Override
    public void deleteActHiActInstByActIds(List<String> nodeIds) {
        LambdaQueryWrapper<ActHiActInst> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ActHiActInst::getActId,nodeIds);
        baseMapper.delete(wrapper);
    }
}
