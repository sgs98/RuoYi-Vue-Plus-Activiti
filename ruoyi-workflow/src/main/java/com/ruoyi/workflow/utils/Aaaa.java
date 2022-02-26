package com.ruoyi.workflow.utils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.util.Arrays;

public class Aaaa implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        //String assigneeList = String.valueOf(execution.getVariable("assigneeList"));
        String assigneeList = "admin,ry";
        if(assigneeList != null){
            // 根据逗号分割并以数组形式重新设置进去
            execution.setVariableLocal("assigneeList", Arrays.asList(assigneeList.split(",")));
        }
    }
}
