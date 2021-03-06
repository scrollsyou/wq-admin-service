package com.yywh.Interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yywh.annotation.NotLogin;
import com.yywh.domain.bean.User;
import com.yywh.service.UserService;
import com.yywh.vo.ResponseStatus;

/**
 * 
 * 登陆拦截
 * @author you 2016年3月25日
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

	private static Logger logs = LoggerFactory.getLogger(LoginInterceptor.class);
	public LoginInterceptor(){}
	public LoginInterceptor(UserService userService){
		super();
		this.userService = userService;
	}
	public LoginInterceptor(UserService userService,boolean debug){
		super();
		logs.info("拦截器初始化！。。。。。。。。。。。。。。。。。。。。。");
		this.userService = userService;
		this.debug = debug;
	}
	/**
	 * 配置是否为debug模式
	 */
	private boolean debug = true;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private UserService userService;

	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView mv) throws Exception {
	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if("OPTIONS".equalsIgnoreCase(request.getMethod())){//平台不处理OPTIONS请求
			return true;
		}
		if(debug){
			logs.info("请求类型为:"+request.getMethod());
			logs.info("请求地址为:"+request.getRequestURI()
					.toString());
//			logs.info("发送请求数据为:"+JSON.toJSONString(requestMap));
//			if (request.getRequestURI().contains("/v2/")||request.getRequestURI().contains("/swagger-resources"))
//				return true;
		}
		logs.info("handler类为："+handler.getClass());
		if(handler instanceof HandlerMethod){
			HandlerMethod method = (HandlerMethod) handler;
			NotLogin notLogin = method.getMethodAnnotation(NotLogin.class);
			if(notLogin != null){
				logs.info("该接口为免登陆接口!"+request.getRequestURI().toString());
				return true;
			}
			
			logs.info("getRequestURI:"+request.getRequestURI());
//			if (request.getRequestURI().contains("/facecheck/"))
//				return true;
			//从cookie中获取token
			Cookie[] cookies = request.getCookies();
			String token = null;
			if(cookies != null && cookies.length > 0) {
				for(Cookie cookie : cookies) {
					if("accessToken".equals(cookie.getName())){
						token = cookie.getValue();
						break;
					}
				}
			}
			logs.info("获取token="+token);
			String stringer = "{\"status\":-1,\"message\":\"用户token为空!\"}";
			if(token!=null && !"".equals(token)){
				ResponseStatus<User>  userRS = userService.hasLogin(token);
				if(userRS.getStatus()==1){
					request.setAttribute("user", userRS.getData());
					return true;
				}
				logs.warn(userRS.getMessage()+"！");
				stringer = "{\"status\":-1,\"message\":\""+userRS.getMessage()+"\"}";
			}

			//注销cookies
			if(cookies != null && cookies.length > 0) {
				for(Cookie cookie : cookies) {
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
			response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
		    response.setContentType("text/json; charset=UTF-8");
		    response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		    return false;
		} else {
			// 返回json
			logs.warn("当前controller类非正常的Mvc类！！！！！！！！！");
			String stringer = "{\"status\":-1,\"message\":\"请重新登陆!\"}";
			response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  
		    response.setContentType("text/json; charset=UTF-8");
		    response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		    return false;
		}
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
