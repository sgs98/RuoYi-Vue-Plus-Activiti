package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 减签参数请求
 * @author: gssong
 * @created: 2022年7月3日16:01:36
 */
@Data
@ApiModel("流程模型对象")
public class ModeBo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "模型id不能为空",groups = {EditGroup.class })
    @ApiModelProperty("模型id")
    private String modelId;

    @NotBlank(message = "模型xml不能为空",groups = { AddGroup.class, EditGroup.class })
    @ApiModelProperty("模型xml")
    private String xml;

    @NotBlank(message = "模型图片不能为空",groups = { AddGroup.class, EditGroup.class })
    @ApiModelProperty("模型svg")
    private String svg;

    @ApiModelProperty("流程模型标识")
    private Process process;

    @Data
    @ApiModel("流程模型标识")
    public class Process{

        @NotBlank(message = "模型标识key不能为空",groups = { AddGroup.class, EditGroup.class })
        @ApiModelProperty("模型标识key")
        private String id;

        @NotBlank(message = "模型名称不能为空",groups = { AddGroup.class, EditGroup.class })
        @ApiModelProperty("模型名称")
        private String name;

        @NotBlank(message = "流程分类不能为空",groups = { AddGroup.class, EditGroup.class })
        @ApiModelProperty("流程分类")
        private String category;

    }

}
