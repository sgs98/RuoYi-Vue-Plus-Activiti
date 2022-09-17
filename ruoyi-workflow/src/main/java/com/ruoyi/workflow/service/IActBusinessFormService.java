package com.ruoyi.workflow.service;

import com.ruoyi.workflow.domain.ActBusinessForm;
import com.ruoyi.workflow.domain.vo.ActBusinessFormVo;
import com.ruoyi.workflow.domain.bo.ActBusinessFormBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 业务表单Service接口
 *
 * @author gssong
 * @date 2022-08-19
 */
public interface IActBusinessFormService {

    /**
     * 查询业务表单
     */
    ActBusinessFormVo queryById(Long id);

    /**
     * 查询业务表单列表
     */
    TableDataInfo<ActBusinessFormVo> queryPageList(ActBusinessFormBo bo, PageQuery pageQuery);

    /**
     * 查询业务表单列表
     */
    List<ActBusinessFormVo> queryList(ActBusinessFormBo bo);

    /**
     * 修改业务表单
     */
    ActBusinessFormVo insertByBo(ActBusinessFormBo bo);

    /**
     * 修改业务表单
     */
    ActBusinessFormVo updateByBo(ActBusinessFormBo bo);

    /**
     * 校验并批量删除业务表单信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);
}
