package com.ruoyi.system.workflow;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.user.UserException;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.mapper.SysUserMapper;
/**
 * 业务规则选人
 *
 * @author  gssong
 */
public class WorkflowRuleUserService {

    /**
     * 按用户id查询
     * @param userId
     * @return
     */
    public Long queryUserById(Long userId){
        SysUserMapper userMapper = SpringUtils.getBean(SysUserMapper.class);
        SysUser sysUser = userMapper.selectUserById(userId);
        if(ObjectUtil.isNull(sysUser)){
          throw new UserException("未找到审批人员");
        }
        return sysUser.getUserId();
    }
}
