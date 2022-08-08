package com.ruoyi.workflow.rule;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.user.UserException;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

/**
 * 业务规则选人 返回人员id，多个id用英文逗号隔开
 *
 * @author  gssong
 */
@Component
public class WorkflowRuleUserComponent {

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
