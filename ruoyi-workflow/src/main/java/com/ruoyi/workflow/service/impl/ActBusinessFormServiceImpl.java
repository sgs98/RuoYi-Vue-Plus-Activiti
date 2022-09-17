package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.BeanCopyUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.domain.ActProcessDefSetting;
import com.ruoyi.workflow.service.IActProcessDefSetting;
import com.ruoyi.workflow.service.IProcessInstanceService;
import com.ruoyi.workflow.utils.WorkFlowUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.domain.bo.ActBusinessFormBo;
import com.ruoyi.workflow.domain.vo.ActBusinessFormVo;
import com.ruoyi.workflow.domain.ActBusinessForm;
import com.ruoyi.workflow.mapper.ActBusinessFormMapper;
import com.ruoyi.workflow.service.IActBusinessFormService;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 业务表单Service业务层处理
 *
 * @author gssong
 * @date 2022-08-19
 */
@RequiredArgsConstructor
@Service
public class ActBusinessFormServiceImpl implements IActBusinessFormService {

    private final ActBusinessFormMapper baseMapper;

    private final IActProcessDefSetting iActProcessDefSetting;

    private final IProcessInstanceService iProcessInstanceService;

    /**
     * 查询业务表单
     */
    @Override
    public ActBusinessFormVo queryById(Long id) {
        ActBusinessFormVo vo = baseMapper.selectVoById(id);
        WorkFlowUtils.setStatusFileValue(vo, String.valueOf(vo.getId()));
        return vo;
    }

    /**
     * 查询业务表单列表
     */
    @Override
    public TableDataInfo<ActBusinessFormVo> queryPageList(ActBusinessFormBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ActBusinessForm> lqw = buildQueryWrapper(bo);
        Page<ActBusinessFormVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<ActBusinessFormVo> records = result.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            List<String> collectIds = records.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.toList());
            WorkFlowUtils.setStatusListFileValue(records, collectIds, "id");
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询业务表单列表
     */
    @Override
    public List<ActBusinessFormVo> queryList(ActBusinessFormBo bo) {
        LambdaQueryWrapper<ActBusinessForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ActBusinessForm> buildQueryWrapper(ActBusinessFormBo bo) {
        LambdaQueryWrapper<ActBusinessForm> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getFormKey()), ActBusinessForm::getFormKey, bo.getFormKey());
        lqw.like(StringUtils.isNotBlank(bo.getApplyCode()), ActBusinessForm::getApplyCode, bo.getApplyCode());
        lqw.like(StringUtils.isNotBlank(bo.getFormName()), ActBusinessForm::getFormName, bo.getFormName());
        return lqw;
    }

    /**
     * 新增业务表单
     */
    @Override
    public ActBusinessFormVo insertByBo(ActBusinessFormBo bo) {
        ActBusinessForm add = BeanUtil.toBean(bo, ActBusinessForm.class);
        ActProcessDefSetting actProcessDefSetting = iActProcessDefSetting.queryByFormId(add.getFormId());
        if (BusinessStatusEnum.WAITING.getStatus().equals(bo.getStatus()) && actProcessDefSetting == null) {
            throw new ServiceException("未绑定流程");
        }
        String date = DateUtils.dateTime();
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<>());
        add.setApplyCode("ACT" + date + (count + 1));
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        ActBusinessFormVo actBusinessFormVo = new ActBusinessFormVo();
        BeanCopyUtils.copy(add, actBusinessFormVo);
        actBusinessFormVo.setActProcessDefSetting(actProcessDefSetting);
        Map<String, Object> variableMap = new HashMap<>(16);
        if (ObjectUtil.isNotEmpty(actProcessDefSetting) && StringUtils.isNotBlank(actProcessDefSetting.getFormVariable())) {
            String formValue = actBusinessFormVo.getFormValue();
            JSONObject jsonObject = JSONUtil.parseObj(formValue);
            String[] split = actProcessDefSetting.getFormVariable().split(",");
            for (String variableKey : split) {
                if (jsonObject.containsKey(variableKey)) {
                    Object value = jsonObject.get(variableKey);
                    variableMap.put(variableKey, value);
                }
            }
        }
        variableMap.put("businessKey", actBusinessFormVo.getId());
        actBusinessFormVo.setVariableMap(variableMap);
        return actBusinessFormVo;
    }


    /**
     * 修改业务表单
     */
    @Override
    public ActBusinessFormVo updateByBo(ActBusinessFormBo bo) {
        ActBusinessForm update = BeanUtil.toBean(bo, ActBusinessForm.class);
        ActProcessDefSetting actProcessDefSetting = iActProcessDefSetting.queryByFormId(update.getFormId());
        if (BusinessStatusEnum.WAITING.getStatus().equals(bo.getStatus()) && actProcessDefSetting == null) {
            throw new ServiceException("未绑定流程");
        }
        baseMapper.updateById(update);
        ActBusinessFormVo actBusinessFormVo = new ActBusinessFormVo();
        BeanCopyUtils.copy(update, actBusinessFormVo);
        actBusinessFormVo.setActProcessDefSetting(actProcessDefSetting);
        Map<String, Object> variableMap = new HashMap<>(16);
        if (ObjectUtil.isNotEmpty(actProcessDefSetting) && StringUtils.isNotBlank(actProcessDefSetting.getFormVariable())) {
            String formValue = actBusinessFormVo.getFormValue();
            JSONObject jsonObject = JSONUtil.parseObj(formValue);
            String[] split = actProcessDefSetting.getFormVariable().split(",");
            for (String variableKey : split) {
                if (jsonObject.containsKey(variableKey)) {
                    Object value = jsonObject.get(variableKey);
                    variableMap.put(variableKey, JsonUtils.toJsonString(value));
                }
            }
        }
        variableMap.put("businessKey", actBusinessFormVo.getId());
        actBusinessFormVo.setVariableMap(variableMap);
        return actBusinessFormVo;
    }

    /**
     * 批量删除业务表单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        for (Long id : ids) {
            String processInstanceId = iProcessInstanceService.getProcessInstanceId(id.toString());
            if (StringUtils.isNotBlank(processInstanceId)) {
                iProcessInstanceService.deleteRuntimeProcessAndHisInst(processInstanceId);
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
