package com.ruoyi.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
@ApiModel("完成任务请求类")
public class TaskCompleteREQ {
    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("是否抄送")
    private Boolean isCopy = true;

    @ApiModelProperty("抄送人员id")
    private String assigneeIds;

    @ApiModelProperty("抄送人员名称")
    private String assigneeNames;

    @ApiModelProperty("审批意见")
    private String message;

    @ApiModelProperty("下一个节点审批，key: 节点id, vallue：审批人集合,多个人使用英文逗号分隔")
    private Map<String, String> assigneeMap;

    public String getMessage() {
        return StringUtils.isEmpty(message) ? "同意": message;
    }

    @ApiModelProperty("流程变量，前端会提交一个元素{'entity': {业务详情数据对象}}")
    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables == null ? new HashMap<>() : variables;
    }

    /**
     * 通过节点id获取审批人集合
     * @param key
     * @return
     */
    public List<Long> getAssignees(String key) {
        if(assigneeMap == null) {
            return null;
        }
        if(StringUtils.isNotBlank(assigneeMap.get(key))){
            List<Long> userIds = new ArrayList<>();
            String[] split = assigneeMap.get(key).split(",");
            for (String userId : split) {
                userIds.add(Long.valueOf(userId));
            }
            return userIds;
        }
        return null;
    }
}
