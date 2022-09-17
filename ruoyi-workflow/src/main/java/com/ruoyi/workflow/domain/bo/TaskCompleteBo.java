package com.ruoyi.workflow.domain.bo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @program: ruoyi-vue-plus
 * @description: 完成任务请求对象
 * @author: gssong
 * @created: 2022-02-26
 */
@Data
public class TaskCompleteBo {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 是否抄送
     */
    private Boolean isCopy = true;

    /**
     * 抄送人员id
     */
    private String assigneeIds;

    /**
     * 抄送人员名称
     */
    private String assigneeNames;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 消息对象
     */
    private SendMessage sendMessage;

    /**
     * 下一个节点审批，key: 节点id, value：审批人集合,多个人使用英文逗号分隔
     */
    private Map<String, String> assigneeMap;

    public String getMessage() {
        return StringUtils.isEmpty(message) ? "同意": message;
    }

    /**
     * 流程变量，前端会提交一个元素{'entity': {业务详情数据对象}}
     */
    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables == null ? new HashMap<>(16) : variables;
    }

    /**
     * 通过节点id获取审批人集合
     * @param key
     * @return
     */
    public List<Long> getAssignees(String key) {
        if(assigneeMap == null) {
            return Collections.emptyList();
        }
        if(StringUtils.isNotBlank(assigneeMap.get(key))){
            List<Long> userIds = new ArrayList<>();
            String[] split = assigneeMap.get(key).split(",");
            for (String userId : split) {
                userIds.add(Long.valueOf(userId));
            }
            return userIds;
        }
        return Collections.emptyList();
    }
}
