package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义查询
 * @author: gssong
 * @created: 2021/10/07 11:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流程定义查询")
public class DefREQ extends PageEntity implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 流程定义id
     */
    @ApiModelProperty("流程定义id")
    private String id;
    /**
     * 流程定义名称
     */
    @ApiModelProperty("流程定义名称")
    private String name;

    /**
     * 模型标识key
     */
    @ApiModelProperty("流程定义标识key")
    private String key;
}
