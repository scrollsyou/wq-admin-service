package com.yywh.Controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yywh.annotation.NotLogin;
import com.yywh.domain.bean.AccessLog;
import com.yywh.service.AccessLogService;
import com.yywh.util.IpUtil;
import com.yywh.vo.ResponseStatus;

@RequestMapping("/accessLog")
@Controller
public class AccessLogController {

	private Logger logs = LoggerFactory.getLogger(AccessLogController.class);
	
	@Autowired
	private AccessLogService accessLogService;
	
	@NotLogin
	@RequestMapping("/addLog")
	@ResponseBody
	public ResponseStatus<AccessLog> addLog(AccessLog entity, HttpServletRequest request){
		String ip = IpUtil.getIP(request);
		entity.setIp(ip);
		return accessLogService.addLog(entity);
	};

	@RequestMapping("/queryPage")
	@ResponseBody
	public Page<AccessLog> queryPage(@RequestParam(defaultValue="1",value="page")Integer page,
			@RequestParam(defaultValue="10",value="pageSize")Integer size,
			@RequestParam(defaultValue="",value="name")String title,
			@RequestParam(value="createTime",required=false)String[] createTime) throws ParseException{
		PageRequest pageRequest = new PageRequest(--page, size);
		Page<AccessLog> responsePage = accessLogService.queryPage(title,createTime, pageRequest);
		return responsePage;
	};
	
	@RequestMapping("/sales")
	@ResponseBody
	public ResponseStatus statisticsDailyVisits() {
		ResponseStatus responseStatus = new ResponseStatus(1, "");
		responseStatus.setData(accessLogService.statisticsDailyVisits());
		return responseStatus;
	}
}
