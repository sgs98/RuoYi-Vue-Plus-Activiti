package com.ruoyi.workflow.service;

import com.ruoyi.workflow.domain.SysMessage;
import com.ruoyi.workflow.domain.vo.SysMessageVo;
import com.ruoyi.workflow.domain.bo.SysMessageBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 消息通知Service接口
 *
 * @author gssong
 * @date 2022-06-17
 */
public interface ISysMessageService {

    /**
     * 查询消息通知
     *
     * @param id 消息通知主键
     * @return 消息通知
     */
    SysMessageVo queryById(Long id);

    /**
     * 查询消息通知列表
     *
     * @param bo 消息通知
     * @return 消息通知集合
     */
    TableDataInfo<SysMessageVo> queryPageList(SysMessageBo bo, PageQuery pageQuery);

    /**
     * 查询消息通知列表
     *
     * @return 消息通知集合
     */
    TableDataInfo<SysMessageVo> queryPage();

    /**
     * 查询消息通知列表
     *
     * @param bo 消息通知
     * @return 消息通知集合
     */
    List<SysMessageVo> queryList(SysMessageBo bo);

    /**
     * 发送消息通知
     *
     * @param bo 消息通知
     * @return 结果
     */
    Boolean sendMessage(SysMessageBo bo);

    /**
     * 批量发送消息通知
     *
     * @param messageList 消息通知
     * @return 结果
     */
    Boolean sendBatchMessage(List<SysMessage> messageList);

    /**
     * 修改消息通知
     *
     * @param bo 消息通知
     * @return 结果
     */
    Boolean updateMessage(SysMessageBo bo);

    /**
     * 修改消息通知
     *
     * @param messageList 消息通知
     * @return 结果
     */
    Boolean updateBatchMessage(List<SysMessage> messageList);

    /**
     * 校验并批量删除消息通知信息
     *
     * @param ids 需要删除的消息通知主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     * @return 结果
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 阅读消息
     * @param id
     * @return
     */
    Boolean readMessage(Long id);

    /**
     * 批量阅读消息
     * batchReadMessage
     * @return
     */
    boolean batchReadMessage();
}
