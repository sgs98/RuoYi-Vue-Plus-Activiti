package com.ruoyi.workflow.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.workflow.common.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * @program: ruoyi-vue-plus
 * @description: 转办请求
 * @author: gssong
 * @created: 2022/04/10 14:50
 */
@Data
@Validated
@ApiModel("转办请求")
public class TransmitREQ implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("任务id")
    private String taskId;

    @NotBlank(message = "请选择转办人",groups = AddGroup.class)
    @ApiModelProperty(value = "转办人id")
    private String transmitUserId;

    @ApiModelProperty("审批意见")
    private String comment;
}
