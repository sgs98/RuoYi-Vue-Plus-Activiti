package com.ruoyi.system.domain.bo;

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
    private String type;
    /**
     * 默认是根据流程配置中选择的人员id或者角色id 部门id等
     * 如果为空则查询全部用户
     */
    private String params;
    private String userName;
    private String phonenumber;
    private String deptId;
    private Integer pageNum;
    private Integer pageSize;
    //用于查询人员回显
    private List<Long> ids;
}
