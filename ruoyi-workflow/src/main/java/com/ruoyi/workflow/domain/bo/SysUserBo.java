package com.ruoyi.workflow.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 用户查询
 * @author: gssong
 * @created: 2021/11/28 18:22
 */
@Data
public class SysUserBo {

    /**
     * 要查询的类型  person：根据人员id查询人员，role：根据角色id查询人员
     * dept：根据部门id查询人员，rule 根据业务规则查询人员
     */
    private String type;

    /**
     * 如果为空则查询全部可用的用户或者角色,部门等
     * 默认是根据流程配置中选择的人员id或者角色id 部门id等
     */
    private String params;

    /**
     * 人员名称
     */
    private String userName;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识符
     */
    private String roleKey;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页几条
     */
    private Integer pageSize;

    /**
     * 用于查询人员或者角色的id回显
     */
    private List<Long> ids;
}
