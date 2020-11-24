package com.bokecc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 服务自定义配置
 * @author: Faizel Lannister(chufuying)
 * @date: 2019/9/6
 * @version: 0.0.1
 * @jdk: 1.8
 * @email: chufuying@163.com
 **/

@Data
@Configuration
@ConfigurationProperties(prefix = "app-config")
public class ConfigProperties {

    private String dingUrl;

}
