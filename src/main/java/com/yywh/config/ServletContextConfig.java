package com.yywh.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.yywh.Interceptor.LoginInterceptor;
import com.yywh.service.UserService;

/**
 * springmvc自定义
 * 配置。
 * @author you
 *
 */
@Configuration
public class ServletContextConfig extends WebMvcConfigurationSupport {

	private boolean debug = true;

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor(this.getApplicationContext().getBean(UserService.class),debug)).addPathPatterns("/**");
		super.addInterceptors(registry);
	}

	@Override
	protected void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*");
		super.addCorsMappings(registry);
	}

	
}
