package com.example.demo;

import com.example.demo.ff4j.springcloud.SpringCloudConfigurationFeatureStore;
import com.example.demo.ff4j.springcloud.SpringCloudConfigurationPropertyStore;
import org.ff4j.FF4j;
import org.ff4j.web.ApiConfig;
import org.ff4j.web.FF4jDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FF4jConfiguration {
	@Autowired
	SpringCloudConfigurationFeatureStore springConfigurationFeatureStore;

	@Autowired
	SpringCloudConfigurationPropertyStore springConfigurationPropertyStore;

	@Autowired
	private FF4jDispatcherServlet ff4jServlet;

	@Bean
	public FF4j getFF4j() {
		FF4j ff4j = new FF4j();
		ff4j.setFeatureStore(springConfigurationFeatureStore);
		ff4j.setPropertiesStore(springConfigurationPropertyStore);
		return ff4j;
	}

	@Bean
	public FF4jDispatcherServlet getFF4JServlet() {
		FF4jDispatcherServlet ds = new FF4jDispatcherServlet();
		ds.setFf4j(getFF4j());
		return ds;
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
		return new ServletRegistrationBean(ff4jServlet, "/ff4j-console/*");
	}

}