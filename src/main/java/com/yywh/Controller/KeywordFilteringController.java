package com.yywh.Controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

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

import com.yywh.domain.bean.KeywordFiltering;
import com.yywh.domain.bean.OperateLog;
import com.yywh.domain.bean.User;
import com.yywh.service.KeywordFilteringService;
import com.yywh.service.OperateLogService;
import com.yywh.service.UserService;
import com.yywh.util.IpUtil;
import com.yywh.vo.ResponseStatus;

@RequestMapping("/keywordFiltering")
@Controller
public class KeywordFilteringController {

	private Logger logs = LoggerFactory.getLogger(KeywordFilteringController.class);
	
	@Autowired
	private KeywordFilteringService keywordFilteringService;
	@Autowired
	private OperateLogService operateLogService;
	@Autowired
	private UserService userService;
	
	@RequestMapping("/addKeyword")
	@ResponseBody
	public ResponseStatus<KeywordFiltering> addKeyword(@RequestBody KeywordFiltering entity, @CookieValue(value="accessToken", required=false) String token, HttpServletRequest request){
		ResponseStatus<KeywordFiltering> responseStatus = new ResponseStatus<KeywordFiltering>(0, "");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			entity.setOperId(operUser.getId());
			entity.setOperName(operUser.getName());
			responseStatus = keywordFilteringService.addKeyword(entity);
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), operUser.getId(), operUser.getName(), "添加关键字“"+entity.getKeyword()+"”，结果："+responseStatus.getMessage()));
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	};

	@RequestMapping("/updKeyword")
	@ResponseBody
	public ResponseStatus<KeywordFiltering> updKeyword(@RequestBody KeywordFiltering entity, @CookieValue(value="accessToken") String token, HttpServletRequest request){
		ResponseStatus<KeywordFiltering> responseStatus = new ResponseStatus<KeywordFiltering>(0, "");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			entity.setOperId(operUser.getId());
			entity.setOperName(operUser.getName());
			ResponseStatus<KeywordFiltering> tmpResponseStatus = keywordFilteringService.findOne(entity.getId());
			if(tmpResponseStatus == null || tmpResponseStatus.getData() == null) {
				responseStatus.setMessage("关键词不存在！");
				return responseStatus;
			}
			KeywordFiltering tmpKeywordFiltering = tmpResponseStatus.getData();
			String msg = "修改关键字“" + tmpKeywordFiltering.getKeyword() + "”为“" + entity.getKeyword();
			tmpKeywordFiltering.setKeyword(entity.getKeyword());
			responseStatus = keywordFilteringService.updKeyword(entity);
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), operUser.getId(), operUser.getName(), msg + "”，结果："+responseStatus.getMessage()));
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	};

	@RequestMapping("/delKeyword/{id}")
	@ResponseBody
	public ResponseStatus<KeywordFiltering> delKeyword(@PathVariable("id") Long id, @CookieValue(value="accessToken") String token, HttpServletRequest request){
		ResponseStatus<KeywordFiltering> responseStatus = new ResponseStatus<KeywordFiltering>(0, "");
		User operUser = userService.findByToken(token);
		if(operUser != null) {
			responseStatus = keywordFilteringService.delKeyword(id);
			KeywordFiltering oldBean = responseStatus.getData();
			String msg = "删除关键字";
			if(oldBean != null) {
				msg += "“"+oldBean.getKeyword()+"”";
			}
			operateLogService.addLog(new OperateLog(IpUtil.getIP(request), operUser.getId(), operUser.getName(), msg + "，结果："+responseStatus.getMessage()));
		}else {
			responseStatus.setMessage("用户token失效，请重新登录！");
		}
		return responseStatus;
	};


	@RequestMapping("/queryPage")
	@ResponseBody
	public Page<KeywordFiltering> queryPage(@RequestParam(defaultValue="1",value="page")Integer page,
			@RequestParam(defaultValue="10",value="pageSize")Integer size,
			@RequestParam(defaultValue="",value="name")String keyword,
			@RequestParam(value="createTime",required=false)String[] createTime) throws ParseException{
		PageRequest pageRequest = new PageRequest(--page, size);
		Page<KeywordFiltering> responsePage = keywordFilteringService.queryPage(keyword,createTime, pageRequest);
		return responsePage;
	};
	
	@RequestMapping("/queryOne/{id}")
	@ResponseBody
	public ResponseStatus<KeywordFiltering> queryOne(@PathVariable("id") Long id){
    	return keywordFilteringService.findOne(id);
	}

}
