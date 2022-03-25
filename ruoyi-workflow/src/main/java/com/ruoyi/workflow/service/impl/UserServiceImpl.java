package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.*;
import com.ruoyi.workflow.domain.bo.SysUserBo;
import com.ruoyi.workflow.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.workflow.common.constant.ActConstant.*;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;
    private final SysUserRoleMapper userRoleMapper;
    @Override
    public List<SysUser> selectListUserByIds(List<Long> userIds) {
        List<SysUser> userList = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, userIds));
        return userList;
    }

    @Override
    public SysUser selectUserById(Long userId) {
        SysUser sysUser = userMapper.selectUserById(userId);
        return sysUser;
    }

    @Override
    public Map<String,Object> getWorkflowUserListByPage(SysUserBo sysUserBo) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isNotEmpty(sysUserBo.getParams())){
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            //检索条件
            queryWrapper.eq(StringUtils.isNotEmpty(sysUserBo.getDeptId()),SysUser::getDeptId,sysUserBo.getDeptId());
            queryWrapper.eq(SysUser::getStatus,UserStatus.OK.getCode());
            queryWrapper.like(StringUtils.isNotEmpty(sysUserBo.getUserName()),SysUser::getUserName,sysUserBo.getUserName());
            queryWrapper.like(StringUtils.isNotEmpty(sysUserBo.getPhonenumber()),SysUser::getPhonenumber,sysUserBo.getPhonenumber());
            Page<SysUser> page = new Page<>(sysUserBo.getPageNum(), sysUserBo.getPageSize());
            // 按用户id查询
            String[] split = sysUserBo.getParams().split(",");
            List<Long> paramList = new ArrayList<>();
            for (String userId : split) {
                paramList.add(Long.valueOf(userId));
            }
            if (WORKFLOW_PERSON.equals(sysUserBo.getType())||WORKFLOW_RULE.equals(sysUserBo.getType())) {
                queryWrapper.in(SysUser::getUserId, paramList);
                Page<SysUser> userPage = userMapper.selectPage(page, queryWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysUser> list = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
                    map.put("list",list);
                }
                map.put("page",TableDataInfo.build(recordPage(userPage)));
                return map;
                //按角色id查询用户
            } else if (WORKFLOW_ROLE.equals(sysUserBo.getType())) {
                List<SysRole> sysRoles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, paramList));
                if (CollectionUtil.isNotEmpty(sysRoles)) {
                    List<Long> collectRoleId = sysRoles.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
                    List<SysUserRole> sysUserRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, collectRoleId));
                    queryWrapper.in(SysUser::getUserId, sysUserRoles.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
                    Page<SysUser> userPage = userMapper.selectPage(page, queryWrapper);
                    if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                        List<SysUser> list = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
                        map.put("list",list);
                    }
                    map.put("page",TableDataInfo.build(recordPage(userPage)));
                    return map;
                }
                //按部门id查询用户
            } else if (WORKFLOW_DEPT.equals(sysUserBo.getType())) {
                queryWrapper.in(SysUser::getDeptId, paramList);
                Page<SysUser> userPage = userMapper.selectPage(page, queryWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysUser> list = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
                    map.put("list",list);
                }
                map.put("page",TableDataInfo.build(recordPage(userPage)));
                return map;
            }
        }else{
            if(WORKFLOW_ROLE.equals(sysUserBo.getType())){
                LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
                Page<SysRole> rolePage = new Page<>(sysUserBo.getPageNum(), sysUserBo.getPageSize());

                //检索条件
                roleWrapper.like(StringUtils.isNotEmpty(sysUserBo.getRoleName()),SysRole::getRoleName,sysUserBo.getRoleName());
                roleWrapper.like(StringUtils.isNotEmpty(sysUserBo.getRoleKey()),SysRole::getRoleKey,sysUserBo.getRoleKey());
                roleWrapper.eq(SysRole::getStatus,UserStatus.OK.getCode());
                Page<SysRole> roleListPage = roleMapper.selectPage(rolePage, roleWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysRole> list = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, sysUserBo.getIds()));
                    map.put("list",list);
                }
                map.put("page",TableDataInfo.build(roleListPage));
            }else if(WORKFLOW_PERSON.equals(sysUserBo.getType())){
                LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
                Page<SysUser> page = new Page<>(sysUserBo.getPageNum(), sysUserBo.getPageSize());

                //检索条件
                userWrapper.eq(StringUtils.isNotEmpty(sysUserBo.getDeptId()),SysUser::getDeptId,sysUserBo.getDeptId());
                userWrapper.eq(SysUser::getStatus,UserStatus.OK.getCode());
                userWrapper.like(StringUtils.isNotEmpty(sysUserBo.getUserName()),SysUser::getUserName,sysUserBo.getUserName());
                userWrapper.like(StringUtils.isNotEmpty(sysUserBo.getPhonenumber()),SysUser::getPhonenumber,sysUserBo.getPhonenumber());

                Page<SysUser> userPage = userMapper.selectPage(page, userWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysUser> list = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
                    map.put("list",list);
                }
                map.put("page",TableDataInfo.build(recordPage(userPage)));
                return map;
            }else if(WORKFLOW_DEPT.equals(sysUserBo.getType())){
                LambdaQueryWrapper<SysDept> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.eq(SysDept::getStatus, UserStatus.OK.getCode());
                List<SysDept> deptList = deptMapper.selectList(roleWrapper);
                map.put("list",deptList);
                return map;
            }

        }
        return map;
    }

    /**
     * 翻译部门
     * @param page
     * @return
     */
    private Page<SysUser> recordPage(Page<SysUser> page){
        List<SysUser> records = page.getRecords();
        if(CollectionUtil.isEmpty(records)){
            return page;
        }
        List<Long> collectDeptId = records.stream().filter(e -> e.getDeptId()!=null).map(SysUser::getDeptId).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(collectDeptId)){
            return page;
        }
        List<SysDept> sysDepts = deptMapper.selectBatchIds(collectDeptId);
        records.forEach(e->{
            SysDept sysDept = sysDepts.stream().filter(d -> d.getDeptId().equals(e.getDeptId())).findFirst().orElse(null);
            if(ObjectUtil.isNotNull(sysDept)){
                e.setDept(sysDept);
            }
        });
        page.setRecords(records);
        return page;
    }
}
