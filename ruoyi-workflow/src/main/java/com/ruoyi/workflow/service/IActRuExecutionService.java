package com.ruoyi.workflow.service;

import com.ruoyi.workflow.domain.ActRuExecution;
import com.ruoyi.workflow.domain.vo.ActRuExecutionVo;
import com.ruoyi.workflow.domain.bo.ActRuExecutionBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 流程执行Service接口
 *
 * @author gssong
 * @date 2022-02-08
 */
public interface IActRuExecutionService{
	/**
	 * 查询单个
	 * @return
	 */
	ActRuExecutionVo queryById(String id);

	/**
	 * 查询列表
	 */
    TableDataInfo<ActRuExecutionVo> queryPageList(ActRuExecutionBo bo, PageQuery pageQuery);

	/**
	 * 查询列表
	 */
	List<ActRuExecutionVo> queryList(ActRuExecutionBo bo);

	/**
	 * 根据新增业务对象插入流程执行
	 * @param bo 流程执行新增业务对象
	 * @return
	 */
	Boolean insertByBo(ActRuExecutionBo bo);

	/**
	 * 根据编辑业务对象修改流程执行
	 * @param bo 流程执行编辑业务对象
	 * @return
	 */
	Boolean updateByBo(ActRuExecutionBo bo);

	/**
	 * 校验并删除数据
	 * @param ids 主键集合
	 * @param isValid 是否校验,true-删除前校验,false-不校验
	 * @return
	 */
	Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

	List<ActRuExecution> selectRuExecutionByProcInstId(String procInstId);
}
