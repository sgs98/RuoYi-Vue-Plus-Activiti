package com.ruoyi.system.domain.bo;

import lombok.Data;

/**
 * @program: ruoyi-vue-plus
 * @description: 用户查询
 * @author: gssong
 * @created: 2021/11/28 18:22
 */
@Data
public class SysUserBo {
    private String type;
    private String params;
    private String userName;
    private String phonenumber;
    private String deptId;
    private Integer pageNum;
    private Integer pageSize;
}
