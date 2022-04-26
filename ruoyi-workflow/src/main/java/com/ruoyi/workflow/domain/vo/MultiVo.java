package com.ruoyi.workflow.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @program: ruoyi-vue-plus
 * @description: MultiVo
 * @author: gssong
 * @created: 2022/04/20 21:22
 */
@Data
@ApiModel("会签类型")
public class MultiVo {
    /**
     * 会签类型（串行，并行）
     */
    private Object type;

    /**
     * 会签人员KEY
     */
    private String assignee;

    /**
     * 会签人员集合KEY
     */
    private String assigneeList;
}
