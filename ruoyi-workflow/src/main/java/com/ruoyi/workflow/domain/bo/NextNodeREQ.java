package com.ruoyi.workflow.domain.bo;

import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
public class NextNodeREQ implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private String taskId;

    /**
     * 流程变量
     */
    @ApiModelProperty("流程变量")
    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables == null ? new HashMap<>() : variables;
    }
}
