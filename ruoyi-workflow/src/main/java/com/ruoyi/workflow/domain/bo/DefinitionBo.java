package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义查询
 * @author: gssong
 * @created: 2021/10/07 11:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DefinitionBo extends PageEntity implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 流程定义id
     */
    private String id;
    /**
     * 流程定义名称
     */
    private String name;

    /**
     * 模型标识key
     */
    private String key;
}
