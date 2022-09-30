package com.ruoyi.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.domain.bo.ActDynamicFormBo;
import com.ruoyi.workflow.domain.vo.ActDynamicFormVo;
import com.ruoyi.workflow.domain.ActDynamicForm;
import com.ruoyi.workflow.mapper.ActDynamicFormMapper;
import com.ruoyi.workflow.service.IActDynamicFormService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.ruoyi.common.constant.UserConstants.TYPE_BUTTON;

/**
 * 流程单Service业务层处理
 *
 * @author gssong
 * @date 2022-08-11
 */
@RequiredArgsConstructor
@Service
public class ActDynamicFormServiceImpl implements IActDynamicFormService {

    private final ActDynamicFormMapper baseMapper;
    private final ISysMenuService iSysMenuService;
    private final SysMenuMapper sysMenuMapper;

    /**
     * 查询动态表单
     */
    @Override
    public ActDynamicFormVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询动态表单列表
     */
    @Override
    public TableDataInfo<ActDynamicFormVo> queryPageList(ActDynamicFormBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ActDynamicForm> lqw = buildQueryWrapper(bo);
        Page<ActDynamicFormVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询启用动态表单列表
     */
    @Override
    public TableDataInfo<ActDynamicFormVo> queryPageEnableList(ActDynamicFormBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ActDynamicForm> lqw = buildQueryWrapper(bo);
        List<SysMenu> sysMenus = iSysMenuService.selectMenuList(LoginHelper.getUserId());
        if(CollectionUtil.isNotEmpty(sysMenus)){
            List<Long> menuIds = sysMenus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
            lqw.in(ActDynamicForm::getMenuId,menuIds);
        }
        Page<ActDynamicFormVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        if(CollectionUtil.isNotEmpty(result.getRecords())){
            List<Long> menuIds = result.getRecords().stream().map(ActDynamicFormVo::getMenuId).collect(Collectors.toList());
            List<SysMenu> menuList = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getMenuId, menuIds));
            if(CollectionUtil.isNotEmpty(menuList)){
                result.getRecords().forEach(e-> menuList.stream().filter(m->ObjectUtil.equals(m.getMenuId(),e.getMenuId())).findFirst().ifPresent(m-> e.setPerms(m.getPerms())));
            }
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询动态表单列表
     */
    @Override
    public List<ActDynamicFormVo> queryList(ActDynamicFormBo bo) {
        LambdaQueryWrapper<ActDynamicForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ActDynamicForm> buildQueryWrapper(ActDynamicFormBo bo) {
        LambdaQueryWrapper<ActDynamicForm> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFormKey()), ActDynamicForm::getFormKey, bo.getFormKey());
        lqw.like(StringUtils.isNotBlank(bo.getFormName()), ActDynamicForm::getFormName, bo.getFormName());
        lqw.orderByAsc(ActDynamicForm::getOrderNo);
        return lqw;
    }

    /**
     * 新增动态表单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(ActDynamicFormBo bo) {
        ActDynamicForm add = BeanUtil.toBean(bo, ActDynamicForm.class);
        //保存前的数据校验
        validEntityBeforeSave(add);
        //保存表单权限
        saveMenu(add);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 修改动态表单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(ActDynamicFormBo bo) {
        ActDynamicForm update = BeanUtil.toBean(bo, ActDynamicForm.class);
        //保存前的数据校验
        validEntityBeforeSave(update);
        //保存表单权限
        saveMenu(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存表单权限
     */
    private void saveMenu(ActDynamicForm entity){
        //权限菜单
        if(ObjectUtil.isNotEmpty(entity.getMenuId())){
            sysMenuMapper.deleteById(entity.getMenuId());
        }
        SysMenu sysMenu = new SysMenu();
        sysMenu.setParentId(1560570936626032642L);
        sysMenu.setMenuName(entity.getFormName());
        sysMenu.setMenuType(TYPE_BUTTON);
        sysMenu.setPerms("dynamicForm:"+entity.getFormKey()+":add");
        sysMenu.setOrderNum(1);
        sysMenu.setIsFrame("1");
        sysMenu.setIsCache("0");
        sysMenu.setVisible("0");
        sysMenu.setStatus("0");
        boolean flag = sysMenuMapper.insert(sysMenu)>0;
        if(flag){
            entity.setMenuId(sysMenu.getMenuId());
        }
    }

    @Override
    public Boolean editForm(ActDynamicFormBo bo) {
        ActDynamicForm update = BeanUtil.toBean(bo, ActDynamicForm.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ActDynamicForm entity) {
        LambdaQueryWrapper<ActDynamicForm> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ActDynamicForm::getFormKey, entity.getFormKey());
        if (entity.getId() != null) {
            wrapper.ne(ActDynamicForm::getId, entity.getId());
            List<ActDynamicForm> actDynamicForms = baseMapper.selectList(wrapper);
            validException(actDynamicForms);
        } else {
            List<ActDynamicForm> actDynamicForms = baseMapper.selectList(wrapper);
            validException(actDynamicForms);
        }
    }

    private void validException(List<ActDynamicForm> actDynamicForms) {

        if (CollectionUtil.isNotEmpty(actDynamicForms)) {
            throw new ServiceException("表单key已存在");
        }

        if (CollectionUtil.isNotEmpty(actDynamicForms) && actDynamicForms.size() > 1) {
            throw new ServiceException(actDynamicForms.get(0).getFormKey() + "表单key存在" + actDynamicForms.size() + "个,请检查数据！");
        }
    }

    /**
     * 批量删除动态表单
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
