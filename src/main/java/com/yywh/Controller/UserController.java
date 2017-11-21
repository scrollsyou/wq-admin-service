package com.yywh.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yywh.annotation.NotLogin;
import com.yywh.domain.bean.OperateLog;
import com.yywh.domain.bean.User;
import com.yywh.service.OperateLogService;
import com.yywh.service.UserService;
import com.yywh.util.CodeUtil;
import com.yywh.util.DateUtil;
import com.yywh.util.IpUtil;
import com.yywh.vo.Menu;
import com.yywh.vo.ResponseStatus;

@RequestMapping("/user")
@Controller
public class UserController {

	private Logger logs = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private OperateLogService operateLogService;
	
	@RequestMapping("/addUser")
	@ResponseBody
	public ResponseStatus<User> addUser(@RequestBody User entity, @CookieValue(value="accessToken") String token, HttpServletRequest request){
		ResponseStatus<User> responseStatus = new ResponseStatus<User>(0, "");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			entity.setOperId(operUser.getId());
			entity.setOperName(operUser.getName());
			responseStatus = userService.addUser(entity);
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), operUser.getId(), operUser.getName(), "添加用户帐号“"+entity.getAccount()+"”，结果："+responseStatus.getMessage()));
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	};
	
	@RequestMapping("/updUser")
	@ResponseBody
	public ResponseStatus<User> updUser(@RequestBody User entity, @CookieValue(value="accessToken") String token, HttpServletRequest request){
		ResponseStatus<User> responseStatus = new ResponseStatus<User>(0, "");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			entity.setOperId(operUser.getId());
			entity.setOperName(operUser.getName());
			responseStatus = userService.updUser(entity);
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), operUser.getId(), operUser.getName(), "修改用户“" + entity.getAccount() + "”的信息，结果："+responseStatus.getMessage()));
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	};
	
	@RequestMapping("/queryPage")
	@ResponseBody
	public Page<User> queryPage(@RequestParam(defaultValue="1",value="page")Integer page,
			@RequestParam(defaultValue="10",value="pageSize")Integer size,
			@RequestParam(defaultValue="",value="name")String name,
			@RequestParam(value="createTime",required=false)String[] createTime) throws ParseException{
		PageRequest pageRequest = new PageRequest(--page, size);
		Page<User> responsePage = userService.queryPage(name,createTime, pageRequest);
		return responsePage;
	};
	
	@RequestMapping("/queryOne/{id}")
	@ResponseBody
	public ResponseStatus<User> queryOne(@PathVariable("id") Long id){
    	return userService.findOne(id);
	}

	@RequestMapping("/queryOne")
	@ResponseBody
	public ResponseStatus<User> queryOne(@CookieValue(value="accessToken") String token){
		ResponseStatus<User> responseStatus = new ResponseStatus<User>(0, "");
		User bean = userService.findByToken(token);
		if(bean != null) {
			responseStatus.setStatus(1);
			responseStatus.setData(bean);
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	}
	
	@RequestMapping("/delUser/{id}")
	@ResponseBody
	public ResponseStatus<User> delUser(@PathVariable("id") Long id, @CookieValue(value="accessToken") String token, HttpServletRequest request){
		ResponseStatus<User> responseStatus = new ResponseStatus<User>(0, "");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			responseStatus = userService.delUser(id);
			User oldBean = responseStatus.getData();
			String msg = "删除用户帐号";
			if(oldBean != null) {
				msg += "“"+oldBean.getName()+"”";
			}
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), operUser.getId(), operUser.getName(), msg + "，结果："+responseStatus.getMessage()));
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	}

	@NotLogin
	@RequestMapping("/login")
	@ResponseBody
	public ResponseStatus<User> login(@RequestParam(defaultValue="",value="account")String account, @RequestParam(defaultValue="",value="password")String password, HttpServletResponse response, HttpServletRequest request){
		ResponseStatus<User> responseStatus = new ResponseStatus<User>(0, "用户登录失败！");
		response.setStatus(400);
		if(account == null || "".equals(account.trim()) || password == null || "".equals(password.trim()) ) {
			responseStatus.setMessage("请输入帐号和密码！");
			return responseStatus;
		}
		User userBean = userService.findByAccount(account);
		if(userBean == null) {
			responseStatus.setMessage("该用户帐号不存在！");
			return responseStatus;
		}
//		String passwordMd5 = CodeUtil.enCodeMd5_UTF8(password);
		logs.info("login user account={},password={},queryUserPassword={}",account,password,userBean.getPassword());
		if(!userBean.getPassword().equals(password)) {
			responseStatus.setMessage("用户密码不正确！");
			return responseStatus;
		}
		String accessToken = account+CodeUtil.getRandomCode32();
		//保存token
		userBean.setToken(accessToken);
		userBean = userService.updUserToken(userBean);
		if(userBean != null) {
			//set cookie accessToken
			Cookie cookie = new Cookie("accessToken", accessToken);
			cookie.setHttpOnly(true);
			cookie.setMaxAge(900000);
			cookie.setPath("/");
			response.addCookie(cookie);
			//set cookie token
			String token = "{\"id\":" + userBean.getId() + ",\"deadline\":" + (System.currentTimeMillis()+(1000*60*60)) + "}";
			try {
				token = URLEncoder.encode(token, "utf-8");
			} catch (UnsupportedEncodingException e) {
				logs.error(e.getMessage());
			}
			cookie = new Cookie("token", token);
			cookie.setHttpOnly(true);
			cookie.setMaxAge(900000);
			cookie.setPath("/");
			response.addCookie(cookie);
			
			//set responseStatus
			responseStatus.setData(new User(userBean.getAccount(), userBean.getName(), userBean.getPhone(), userBean.getToken(), userBean.getOperTime()));
			responseStatus.setStatus(1);
			responseStatus.setMessage("用户登录成功！");
			response.setStatus(200);
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), userBean.getId(), userBean.getName(), "用户登录成功，登录时间：" + DateUtil.getNowStr()));
			return responseStatus;
		}
		return responseStatus;
	}

	@NotLogin
	@RequestMapping("/logout")
	@ResponseBody
	public ResponseStatus<User> loginOut(@CookieValue(value="accessToken") String token, HttpServletRequest request, HttpServletResponse response) {
		ResponseStatus<User> responseStatus = new ResponseStatus<User>(1, "退出登录成功！");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			operUser.setToken("");
			operUser = userService.updUserToken(operUser);
			if(operUser == null) {
				responseStatus.setStatus(0);
				responseStatus.setMessage("退出登录失败！");
				return responseStatus;
			}
		}
		//注销cookies
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0) {
			for(Cookie cookie : cookies) {
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
			}
		}
		return responseStatus;
	}
	
	@RequestMapping("/menus")
	@ResponseBody
	public ResponseStatus<List<Menu>> getMenu() {
		List<Menu> list = new ArrayList<Menu>();
		list.add(new Menu("1", "laptop", "首页", "/dashboard", "", ""));
		list.add(new Menu("2", "user", "用户管理", "/user", "1", ""));
		list.add(new Menu("21", "", "用户详细", "/user/:id", "2", "-1"));
		list.add(new Menu("3", "database", "访问日志", "/accesslog", "1", ""));
		list.add(new Menu("31", "", "日志详细", "/accesslog/:id", "3", "-1"));
		list.add(new Menu("4", "database", "操作日志", "/editlog", "1", ""));
		list.add(new Menu("41", "", "日志详细", "/editlog/:id", "4", "-1"));
		list.add(new Menu("5", "database", "关键词管理", "/sensitive", "1", ""));
		list.add(new Menu("51", "", "关键词详细", "/sensitive/:id", "5", "-1"));
		return new ResponseStatus<List<Menu>>(1, "", list);
	}
}
