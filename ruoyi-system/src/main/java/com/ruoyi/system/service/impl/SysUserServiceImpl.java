package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysUserPost;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.domain.bo.SysUserBo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.common.constant.Constants.*;

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper baseMapper;
    private final SysRoleMapper roleMapper;
    private final SysPostMapper postMapper;
    private final SysDeptMapper deptMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysUserPostMapper userPostMapper;

    @Override
    public TableDataInfo<SysUser> selectPageUserList(SysUser user, PageQuery pageQuery) {
        Page<SysUser> page = baseMapper.selectPageUserList(pageQuery.build(), user);
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return baseMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUser> selectAllocatedList(SysUser user, PageQuery pageQuery) {
        Page<SysUser> page = baseMapper.selectAllocatedList(pageQuery.build(), user);
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUser> selectUnallocatedList(SysUser user, PageQuery pageQuery) {
        Page<SysUser> page = baseMapper.selectUnallocatedList(pageQuery.build(), user);
        return TableDataInfo.build(page);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return baseMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, userName));
        if (exist) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getPhonenumber, user.getPhonenumber())
            .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId()));
        if (exist) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getEmail, user.getEmail())
            .ne(ObjectUtil.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId()));
        if (exist) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (ObjectUtil.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (!LoginHelper.isAdmin()) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (CollUtil.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = baseMapper.insert(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        user.setCreateBy(user.getUserName());
        user.setUpdateBy(user.getUserName());
        return baseMapper.insert(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, userId));
        // 新增用户与岗位管理
        insertUserPost(user);
        return baseMapper.updateById(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
            .eq(SysUserRole::getUserId, userId));
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return baseMapper.updateById(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return baseMapper.updateById(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getAvatar, avatar)
                .eq(SysUser::getUserName, userName)) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return baseMapper.updateById(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return baseMapper.update(null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getPassword, password)
                .eq(SysUser::getUserName, userName));
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();
        if (ObjectUtil.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.insertBatch(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (ObjectUtil.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
                userPostMapper.insertBatch(list);
            }
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (ObjectUtil.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.insertBatch(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        // 删除用户与岗位表
        userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, userId));
        return baseMapper.deleteById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        List<Long> ids = Arrays.asList(userIds);
        // 删除用户与角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, ids));
        // 删除用户与岗位表
        userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().in(SysUserPost::getUserId, ids));
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<SysUser> selectListUserByIds(List<Long> userIds) {
        List<SysUser> userList = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, userIds));
        return userList;
    }

    @Override
    public List<SysUser> selectUserListByUserName(List<String> userName) {
        List<SysUser> userList = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserName, userName));
        return userList;
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
                Page<SysUser> userPage = baseMapper.selectPage(page, queryWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysUser> list = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
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
                    Page<SysUser> userPage = baseMapper.selectPage(page, queryWrapper);
                    if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                        List<SysUser> list = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
                        map.put("list",list);
                    }
                    map.put("page",TableDataInfo.build(recordPage(userPage)));
                    return map;
                }
                //按部门id查询用户
            } else if (WORKFLOW_DEPT.equals(sysUserBo.getType())) {
                queryWrapper.in(SysUser::getDeptId, paramList);
                Page<SysUser> userPage = baseMapper.selectPage(page, queryWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysUser> list = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
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

                Page<SysUser> userPage = baseMapper.selectPage(page, userWrapper);
                if(CollectionUtil.isNotEmpty(sysUserBo.getIds())){
                    List<SysUser> list = baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getUserId, sysUserBo.getIds()));
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
