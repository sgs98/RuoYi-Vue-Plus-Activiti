package com.ruoyi.workflow.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.domain.ActProcessDefSetting;
import com.ruoyi.workflow.domain.vo.ActProcessDefSettingVo;
import com.ruoyi.workflow.domain.bo.ActProcessDefSettingBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 流程定义设置Service接口
 *
 * @author gssong
 * @date 2022-08-28
 */
public interface IActProcessDefSetting {

    /**
     * 查询流程定义设置
     */
    ActProcessDefSettingVo queryById(Long id);

    /**
     * 按流程定义id查询流程定义设置详细
     */
    ActProcessDefSettingVo getProcessDefSettingByDefId(String defId);

    /**
     * 按流程定义id集合查询流程定义设置详细集合
     */
    List<ActProcessDefSettingVo> getProcessDefSettingByDefIds(List<String> defIds);

    /**
     * 校验表单是否关联
     */
    R<List<Long>> checkProcessDefSetting(String defId, String param, Integer businessType);

    /**
     * 查询流程定义设置列表
     */
    TableDataInfo<ActProcessDefSettingVo> queryPageList(ActProcessDefSettingBo bo, PageQuery pageQuery);

    /**
     * 查询流程定义设置列表
     */
    List<ActProcessDefSettingVo> queryList(ActProcessDefSettingBo bo);

    /**
     * 修改流程定义设置
     */
    Boolean insertByBo(ActProcessDefSettingBo bo);

    /**
     * 修改流程定义设置
     */
    Boolean updateByBo(ActProcessDefSettingBo bo);

    /**
     * 校验并批量删除流程定义设置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 按照formId查询
     */
    ActProcessDefSetting queryByFormId(Long formId);
}
