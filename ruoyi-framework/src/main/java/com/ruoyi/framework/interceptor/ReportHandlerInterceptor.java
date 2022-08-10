package com.ruoyi.framework.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
/**
 * report 拦截器
 *
 * @author gssong
 */
public class ReportHandlerInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        Map<String, String[]> parameterMap = request.getParameterMap();
        String loginId = null;
        if(CollectionUtil.isNotEmpty(parameterMap) && parameterMap.containsKey("token")){
            String[] tokens = parameterMap.get("token");
            System.out.println(tokens[0]);
            loginId = StpUtil.stpLogic.getLoginIdNotHandle(tokens[0]);
        }
        if(StringUtils.isBlank(loginId)){
            throw new RuntimeException("认证失败，无法访问系统资源");
        }
        return true;
    }
}
