package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流程模型查询")
public class ModelREQ extends PageEntity implements Serializable {

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
}
