package com.ruoyi.workflow.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 用户加签查询
 * @author: gssong
 * @created: 2021/11/28 18:22
 */
@Data
public class SysUserMultiBo {

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
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页几条
     */
    private Integer pageSize;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 用于查询人员回显的id回显
     */
    private List<Long> ids;
}
