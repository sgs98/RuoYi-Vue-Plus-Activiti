package com.ruoyi.workflow.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: ruoyi-vue-plus
 * @description: 流程定义视图
 * @author: gssong
 * @created: 2021/10/07 11:25
 */
@Data
public class ProcessDefinitionVo implements Serializable {

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
     * 流程定义标识key
     */
    private String key;

    /**
     * 流程定义版本
     */
    private int version;

    /**
     * 流程定义挂起或激活 1激活 2挂起
     */
    private int suspensionState;

    /**
     * 流程xml名称
     */
    private String resourceName;

    /**
     * 流程图片名称
     */
    private String diagramResourceName;

    /**
     * 流程部署id
     */
    private String deploymentId;

    /**
     * 流程部署时间
     */
    private Date deploymentTime;

    /**
     * 挂起或激活原因
     */
    private String description;

    /**
     * 流程定义设置
     */
    private ActProcessDefSettingVo actProcessDefSettingVo;

}
