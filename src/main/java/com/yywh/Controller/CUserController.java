package com.yywh.Controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yywh.annotation.NotLogin;
import com.yywh.domain.bean.CUser;
import com.yywh.domain.dao.CUserRepository;
import com.yywh.util.CodeUtil;
import com.yywh.vo.ResponseStatus;

@RequestMapping("/cuser")
@Controller
public class CUserController {

	
	@Autowired
	private CUserRepository cUserRepository;
	

	@NotLogin
	@RequestMapping("/login")
	@ResponseBody
	public ResponseStatus<CUser> login(@RequestParam(defaultValue="",value="account")String account, @RequestParam(defaultValue="",value="password")String password, @RequestParam(defaultValue="",value="name")String name, HttpServletResponse response, HttpServletRequest request){
		ResponseStatus<CUser> responseStatus = new ResponseStatus<CUser>(0, "用户登录失败！");
		CUser cUser = cUserRepository.findByAccountAndPassword(account, password);
		if(cUser != null) {
			responseStatus.setStatus(1);
			responseStatus.setData(cUser);
			String accessToken = account+CodeUtil.getRandomCode32();
			//保存token
			cUser.setToken(accessToken);
			cUserRepository.save(cUser);
		}else if(!StringUtils.isEmpty(name)) {
			cUser = new CUser();
			responseStatus.setStatus(1);
			responseStatus.setData(cUser);
			cUser.setAccount(account);
			cUser.setPassword(password);
			cUser.setName(name);
			String accessToken = account+CodeUtil.getRandomCode32();
			//保存token
			cUser.setToken(accessToken);
			cUserRepository.save(cUser);
		}
		return responseStatus;
	}

	@NotLogin
	@RequestMapping("/isLogin")
	@ResponseBody
	public ResponseStatus<CUser> isLogin(@RequestParam(value="token") String token, HttpServletRequest request, HttpServletResponse response) {
		ResponseStatus<CUser> responseStatus = new ResponseStatus<CUser>(0, "未登录成功！");
		CUser operUser = cUserRepository.findByToken(token);
		if(operUser != null) {
			responseStatus.setData(operUser);
			responseStatus.setStatus(1);
			responseStatus.setMessage("已登录成功!");
		}
		return responseStatus;
	}
	
}
