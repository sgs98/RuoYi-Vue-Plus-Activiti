package com.ruoyi.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ruoyi-vue-plus
 * @description: 启动流程
 * @author: gssong
 * @created: 2021/10/10 18:43
 */
@Data
@ApiModel("启动流程实例请求类（提交申请)")
public class StartREQ implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty("业务唯一值id")
    private String businessKey;

    @ApiModelProperty("流程执行key")
    private String processKey;

    @ApiModelProperty("节点任务办理人一位或多位")
    private List<String> assignees;

    @ApiModelProperty("全类名")
    private String classFullName;

    @ApiModelProperty("流程变量，前端会提交一个元素{'entity': {业务详情数据对象}}")
    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables == null ? new HashMap<>() : variables;
    }
}
