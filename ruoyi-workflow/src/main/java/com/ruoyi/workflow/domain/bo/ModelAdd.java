package com.ruoyi.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 模型新增对象
 * @author: gssong
 * @created: 2021/10/03 18:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("模型新增对象")
public class ModelAdd  implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     * 模型名称
     */
    @ApiModelProperty("模型名称")
    private String name;

    /**
     * 模型标识key
     */
    @ApiModelProperty("模型标识key")
    private String key;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;
}
