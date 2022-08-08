package com.ruoyi.workflow.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程变量
 * @author: gssong
 * @created: 2022/07/23 14:54
 */
@Data
@ApiModel("任务视图")
public class VariableVo implements Serializable {

    private static final long serialVersionUID=1L;

    private String key;

    private String value;
}
