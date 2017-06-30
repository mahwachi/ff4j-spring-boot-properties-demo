package com.example.demo.ff4j.springcloud;

import com.example.demo.ff4j.springboot.FF4jConfigurationProperties;
import com.example.demo.ff4j.springboot.SpringBootConfigurationPropertyStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Similar to {@link SpringBootConfigurationPropertyStore} but uses @RefreshScope
 * to allows runtime refresh with Spring Cloud Config
 * 
 * @author Pierre Besson
 */
@Configuration
@RefreshScope
@EnableConfigurationProperties(FF4jConfigurationProperties.class)
public class SpringCloudConfigurationPropertyStore extends SpringBootConfigurationPropertyStore {
}
