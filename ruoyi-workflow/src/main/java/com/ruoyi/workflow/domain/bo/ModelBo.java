package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.workflow.common.PageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 模型请求对象
 * @author: gssong
 * @created: 2022年7月3日16:01:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelBo extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 模型id
     */
    @NotBlank(message = "模型id不能为空",groups = {EditGroup.class })
    private String modelId;

    /**
     * 模型xml
     */
    @NotBlank(message = "模型xml不能为空",groups = { AddGroup.class, EditGroup.class })
    private String xml;

    /**
     * 模型图片
     */
    @NotBlank(message = "模型图片不能为空",groups = { AddGroup.class, EditGroup.class })
    private String svg;

    /**
     * 流程模型标识
     */
    private Process process;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型标识key
     */
    private String key;

    @Data
    public static class Process{

        /**
         * 模型标识key
         */
        @NotBlank(message = "模型标识key不能为空",groups = { AddGroup.class, EditGroup.class })
        private String id;

        /**
         * 模型名称
         */
        @NotBlank(message = "模型名称不能为空",groups = { AddGroup.class, EditGroup.class })
        private String name;

        /**
         * 流程分类
         */
        @NotBlank(message = "流程分类不能为空",groups = { AddGroup.class, EditGroup.class })
        private String category;

    }

}
