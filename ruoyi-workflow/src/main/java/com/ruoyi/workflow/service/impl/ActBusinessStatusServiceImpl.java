package com.ruoyi.workflow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.mapper.ActBusinessStatusMapper;
import com.ruoyi.workflow.service.IActBusinessStatusService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 业务状态实体Service业务层处理
 *
 * @author gssong
 * @date 2021-10-10
 */
@Service
public class ActBusinessStatusServiceImpl extends ServiceImpl<ActBusinessStatusMapper, ActBusinessStatus> implements IActBusinessStatusService {


    /**
     * @Description: 修改业务状态
     * @param: businessKey 业务id
     * @param: statusEnum 业务状态
     * @param: procInstId 流程实例id
     * @param: classFullName 全类名
     * @return: boolean
     * @author: gssong
     * @Date: 2021/10/21
     */
    @Override
    public boolean updateState(String businessKey, BusinessStatusEnum statusEnum, String procInstId,String classFullName) {
        try {
            // 1. 查询当前数据
            ActBusinessStatus bs = baseMapper.selectOne(new LambdaQueryWrapper<ActBusinessStatus>().eq(ActBusinessStatus::getBusinessKey,businessKey));
            // 2. 新增操作
            if(ObjectUtil.isNull(bs)){
                ActBusinessStatus actBusinessStatus = new ActBusinessStatus();
                // 设置状态值
                actBusinessStatus.setStatus(statusEnum.getStatus());
                actBusinessStatus.setBusinessKey(businessKey);
                actBusinessStatus.setClassFullName(classFullName);
                // 只要判断不为null,就更新
                if(procInstId != null) {
                    actBusinessStatus.setProcessInstanceId(procInstId);
                }
                return baseMapper.insert(actBusinessStatus) == 1;
            }else{
                // 设置状态值
                bs.setStatus(statusEnum.getStatus());
                bs.setBusinessKey(businessKey);
                // 只要判断不为null,就更新
                if(procInstId != null) {
                    bs.setProcessInstanceId(procInstId);
                }
                // 3. 更新操作
                return baseMapper.updateById(bs) == 1;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException("更新失败:"+e.getMessage());
        }
    }

    @Override
    public boolean updateState(String businessKey, BusinessStatusEnum statusEnum, String procInstId) {
        return updateState(businessKey, statusEnum,procInstId,null);
    }

    @Override
    public boolean updateState(String businessKey, BusinessStatusEnum statusEnum) {
        return updateState(businessKey, statusEnum, null,null);
    }

    @Override
    public ActBusinessStatus getInfoByBusinessKey(String businessKey) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ActBusinessStatus>().eq(ActBusinessStatus::getBusinessKey, businessKey));
    }

    @Override
    public List<ActBusinessStatus> getListInfoByBusinessKey(List<String> businessKeys) {
        return baseMapper.selectList(new LambdaQueryWrapper<ActBusinessStatus>().in(ActBusinessStatus::getBusinessKey, businessKeys));
    }

    @Override
    public boolean deleteState(String businessKey) {
        int delete = baseMapper.delete(new LambdaQueryWrapper<ActBusinessStatus>().eq(ActBusinessStatus::getBusinessKey, businessKey));
        return delete == 1;
    }

    @Override
    public ActBusinessStatus getInfoByProcessInstId(String processInstanceId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ActBusinessStatus>().eq(ActBusinessStatus::getProcessInstanceId, processInstanceId));
    }

    @Override
    public List<ActBusinessStatus> getInfoByProcessInstIds(List<String> processInstanceIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<ActBusinessStatus>().in(ActBusinessStatus::getProcessInstanceId, processInstanceIds));
    }
}
