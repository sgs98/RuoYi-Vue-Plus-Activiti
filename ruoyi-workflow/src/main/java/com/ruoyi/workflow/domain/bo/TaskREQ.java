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
 * @description: 待办任务查询
 * @author: gssong
 * @created: 2021/10/17 14:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("任务查询")
public class TaskREQ extends PageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("任务名称")
    private String taskName;
}
