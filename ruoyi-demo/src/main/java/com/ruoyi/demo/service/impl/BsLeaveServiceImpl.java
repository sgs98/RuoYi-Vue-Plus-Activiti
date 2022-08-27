package com.ruoyi.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.demo.domain.BsLeave;
import com.ruoyi.demo.domain.bo.BsLeaveBo;
import com.ruoyi.demo.domain.vo.BsLeaveVo;
import com.ruoyi.demo.mapper.BsLeaveMapper;
import com.ruoyi.demo.service.IBsLeaveService;
import com.ruoyi.workflow.service.IProcessInstanceService;
import com.ruoyi.workflow.utils.WorkFlowUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请假业务Service业务层处理
 *
 * @author gssong
 * @date 2021-10-10
 */
@RequiredArgsConstructor
@Service
public class BsLeaveServiceImpl implements IBsLeaveService {

    private final BsLeaveMapper baseMapper;

    private final IProcessInstanceService iProcessInstanceService;
    @Override
    public BsLeaveVo queryById(String id){
        BsLeaveVo vo = baseMapper.selectVoById(id);
        vo.setProcessInstanceId(iProcessInstanceService.getProcessInstanceId(id));
        WorkFlowUtils.setStatusFileValue(vo, Arrays.asList(vo.getId()),vo.getId());
        return vo;
    }

    @Override
    public TableDataInfo<BsLeaveVo> queryPageList(BsLeaveBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BsLeave> lqw = buildQueryWrapper(bo);
        Page<BsLeaveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<BsLeaveVo> records = result.getRecords();
        if(CollectionUtil.isNotEmpty(records)){
            List<String> collectIds = records.stream().map(BsLeaveVo::getId).collect(Collectors.toList());
            for (BsLeaveVo record : records) {
                WorkFlowUtils.setStatusFileValue(record,collectIds,record.getId());
                WorkFlowUtils.setProcessInstIdFileValue(record,collectIds,record.getId());
            }
        }
        result.setRecords(records);
        return TableDataInfo.build(result);
    }
    @Override
    public List<BsLeaveVo> queryList(BsLeaveBo bo) {
        LambdaQueryWrapper<BsLeave> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BsLeave> buildQueryWrapper(BsLeaveBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BsLeave> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getUsername()), BsLeave::getUsername, bo.getUsername());
        lqw.eq(bo.getDuration() != null, BsLeave::getDuration, bo.getDuration());
        lqw.eq(StringUtils.isNotBlank(bo.getPrincipal()), BsLeave::getPrincipal, bo.getPrincipal());
        lqw.like(StringUtils.isNotBlank(bo.getContactPhone()), BsLeave::getContactPhone, bo.getContactPhone());
        lqw.eq(bo.getLeaveType() != null, BsLeave::getLeaveType, bo.getLeaveType());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), BsLeave::getTitle, bo.getTitle());
        return lqw;
    }

    @Override
    public BsLeave insertByBo(BsLeaveBo bo) {
        BsLeave add = BeanUtil.toBean(bo, BsLeave.class);
        validEntityBeforeSave(add);
        baseMapper.insert(add);
        return add;
    }

    @Override
    public BsLeave updateByBo(BsLeaveBo bo) {
        BsLeave update = BeanUtil.toBean(bo, BsLeave.class);
        validEntityBeforeSave(update);
        baseMapper.updateById(update);
        return update;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(BsLeave entity){
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        int i = baseMapper.deleteBatchIds(ids);
        for (String id : ids) {
            String processInstanceId = iProcessInstanceService.getProcessInstanceId(id);
            if(StringUtils.isNotBlank(processInstanceId)){
                iProcessInstanceService.deleteRuntimeProcessAndHisInst(processInstanceId);
            }
        }
        return i>0;
    }
}
