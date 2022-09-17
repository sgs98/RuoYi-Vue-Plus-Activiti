package com.ruoyi.workflow.common;

import lombok.Data;

/**
 * @program: ruoyi-vue-plus
 * @description: 分页参数
 * @author: gssong
 * @created: 2022-02-26
 */
@Data
public class PageEntity {
    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 页码
     * @return
     */
    public Integer getFirstResult() {
        return (pageNum - 1) * pageSize;
    }
}
