package com.ruoyi.framework.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xycq
 * @version 1.0.0
 * @ClassName JmreportConfig.java
 * @Description 积木报表配置
 * @createTime 2022年06月18日 17:21:00
 */
@Configuration
@ComponentScan(basePackages = {"org.jeecg.modules.jmreport"})
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class JmReportConfig {
}

