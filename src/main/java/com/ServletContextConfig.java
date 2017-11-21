package com;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
	Logger log = LoggerFactory.getLogger(ServletContextConfig.class);

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		log.info("开始注册web上下文 ！");
		registry.addInterceptor(new LoginInterceptor(this.getApplicationContext().getBean(UserService.class),debug)).addPathPatterns("/**");
		super.addInterceptors(registry);
	}

	@Override
	protected void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*");
		super.addCorsMappings(registry);
	}

	
}
