package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.domain.bo.SysMessageBo;
import com.ruoyi.workflow.domain.vo.SysMessageVo;
import com.ruoyi.workflow.domain.SysMessage;
import com.ruoyi.workflow.mapper.SysMessageMapper;
import com.ruoyi.workflow.service.ISysMessageService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 消息通知Service业务层处理
 *
 * @author gssong
 * @date 2022-06-17
 */
@RequiredArgsConstructor
@Service
public class SysMessageServiceImpl implements ISysMessageService {

    private final SysMessageMapper baseMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 查询消息通知
     *
     * @param id 消息通知主键
     * @return 消息通知
     */
    @Override
    public SysMessageVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询消息通知列表
     *
     * @param bo 消息通知
     * @return 消息通知
     */
    @Override
    public TableDataInfo<SysMessageVo> queryPageList(SysMessageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysMessage> lqw = buildQueryWrapper(bo);
        Page<SysMessageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        if(CollectionUtil.isNotEmpty(result.getRecords())){
            List<Long> userIds= result.getRecords().stream().map(SysMessageVo::getSendId).collect(Collectors.toList());
            List<Long> recordIds = result.getRecords().stream().map(SysMessageVo::getRecordId).collect(Collectors.toList());
            userIds.addAll(recordIds);
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SysUser::getUserId,userIds);
            List<SysUser> sysUsers = sysUserMapper.selectList(wrapper);
            result.getRecords().forEach(e->{
                SysUser recordUser = sysUsers.stream().filter(t -> t.getUserId().compareTo(e.getRecordId()) == 0).findFirst().orElse(null);
                SysUser sendUser = sysUsers.stream().filter(t -> t.getUserId().compareTo(e.getSendId()) == 0).findFirst().orElse(null);
                if(ObjectUtil.isNotEmpty(recordUser)){
                    e.setRecordName(recordUser.getNickName());
                }
                if(ObjectUtil.isNotEmpty(sendUser)){
                    e.setSendName(sendUser.getNickName());
                }
            });
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询消息通知列表
     *
     * @return 消息通知
     */
    @Override
    public TableDataInfo<SysMessageVo> queryPage(PageQuery pageQuery,String userName) {
        SysUser sysUser = sysUserMapper.selectUserByUserName(userName);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getStatus,false);
        wrapper.eq(SysMessage::getRecordId, sysUser.getUserId());
        Page<SysMessageVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询消息通知列表
     *
     * @param bo 消息通知
     * @return 消息通知
     */
    @Override
    public List<SysMessageVo> queryList(SysMessageBo bo) {
        LambdaQueryWrapper<SysMessage> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysMessage> buildQueryWrapper(SysMessageBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysMessage> lqw = Wrappers.lambdaQuery();
        if(!LoginHelper.isAdmin()){
            lqw.eq(SysMessage::getRecordId,LoginHelper.getUserId());
        }
        lqw.eq(bo.getSendId() != null, SysMessage::getSendId, bo.getSendId());
        lqw.eq(bo.getRecordId() != null, SysMessage::getRecordId, bo.getRecordId());
        lqw.eq(bo.getType() != null, SysMessage::getType, bo.getType());
        lqw.eq(bo.getStatus() != null, SysMessage::getStatus, bo.getStatus());
        lqw.like(StringUtils.isNotBlank(bo.getMessageContent()), SysMessage::getMessageContent, bo.getMessageContent());
        lqw.between(params.get("beginReadTime") != null && params.get("endReadTime") != null,
            SysMessage::getReadTime ,params.get("beginReadTime"), params.get("endReadTime"));
        return lqw;
    }

    /**
     * 发送消息通知
     *
     * @param bo 消息通知
     * @return 结果
     */
    @Override
    public Boolean sendMessage(SysMessageBo bo) {
        SysMessage add = BeanUtil.toBean(bo, SysMessage.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量发送消息通知
     *
     * @param messageList 消息通知
     * @return 结果
     */
    @Override
    public Boolean sendBatchMessage(List<SysMessage> messageList) {
        return baseMapper.insertBatch(messageList);
    }

    /**
     * 修改消息通知
     *
     * @param bo 消息通知
     * @return 结果
     */
    @Override
    public Boolean updateMessage(SysMessageBo bo) {
        SysMessage update = BeanUtil.toBean(bo, SysMessage.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量修改消息通知
     *
     * @param messageList 消息通知
     * @return 结果
     */
    @Override
    public Boolean updateBatchMessage(List<SysMessage> messageList) {
        return baseMapper.updateBatchById(messageList);
    }

    /**
     * 批量删除消息通知
     *
     * @param ids 需要删除的消息通知主键
     * @return 结果
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * @Description: 阅读消息
     * @param: id
     * @return: java.lang.Boolean
     * @author: gssong
     * @Date: 2022/6/19 17:11
     */
    @Override
    public Boolean readMessage(Long id) {
        SysMessage sysMessage = baseMapper.selectById(id);
        sysMessage.setStatus(1);
        sysMessage.setReadTime(new Date());
        return baseMapper.updateById(sysMessage)> 0;
    }

    /**
     * @Description:  批量阅读消息
     * @return: boolean
     * @author: gssong
     * @Date: 2022/6/19 17:16
     */
    @Override
    public boolean batchReadMessage() {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getRecordId, LoginHelper.getUserId());
        List<SysMessage> messageList = baseMapper.selectList(wrapper);
            messageList.forEach(e->{
                e.setStatus(1);
                e.setReadTime(new Date());
            });
        return baseMapper.updateBatchById(messageList);
    }
}
