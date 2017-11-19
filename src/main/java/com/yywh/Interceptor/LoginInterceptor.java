package com.yywh.Interceptor;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

	public LoginInterceptor(){}
	public LoginInterceptor(UserService userService){
		this.userService = userService;
	}
	public LoginInterceptor(UserService userService,boolean debug){
		this.userService = userService;
		this.debug = debug;
	}
	private static Logger logs = LoggerFactory.getLogger(LoginInterceptor.class);
	
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
			HttpServletResponse response, Object handler, Model model) throws Exception {
		request.setAttribute("accessIp", this.getIP(request));
		logs.info("preHandle = accessIp="+this.getIP(request));
		logs.info(request.getHeader("X-Forwarded-For"));
		if("OPTIONS".equalsIgnoreCase(request.getMethod())){//平台不处理OPTIONS请求
			return true;
		}
		if(debug){
			Map<String, String[]> requestMap = request.getParameterMap();
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
			response.setStatus(401);
			
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

	/**
	 * 获取客户端ip
	 * @param request
	 * @return
	 */
	private String getIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		logs.info("ip="+ip);
		if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return request.getRemoteAddr();
		}
	}
}
