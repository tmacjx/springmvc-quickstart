package com.bokecc.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 **/
@Configuration
public class PropertiesObtainConfig implements EnvironmentAware {

    public static Environment env;

    @Override
    public void setEnvironment(Environment environment) {

        env = environment;
    }
}
