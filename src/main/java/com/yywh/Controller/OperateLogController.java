package com.yywh.Controller;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yywh.domain.bean.OperateLog;
import com.yywh.service.OperateLogService;

@RequestMapping("/operateLog")
@Controller
public class OperateLogController {

	private Logger logs = LoggerFactory.getLogger(OperateLogController.class);
	
	@Autowired
	private OperateLogService operateLogService;
	
//	@RequestMapping("/addLog")
//	@ResponseBody
//	public ResponseStatus<OperateLog> addLog(OperateLog entity){
//		return operateLogService.addLog(entity);
//	};

	@RequestMapping("/queryPage")
	@ResponseBody
	public Page<OperateLog> queryPage(@RequestParam(defaultValue="1",value="page")Integer page,
			@RequestParam(defaultValue="10",value="pageSize")Integer size,
			@RequestParam(defaultValue="",value="name")String remark,
			@RequestParam(value="createTime",required=false)String[] createTime) throws ParseException{
		PageRequest pageRequest = new PageRequest(--page, size);
		Page<OperateLog> responsePage = operateLogService.queryPage(remark,createTime, pageRequest);
		return responsePage;
	};
}
