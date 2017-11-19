package com.yywh.service;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yywh.domain.bean.AccessLog;
import com.yywh.domain.dao.AccessLogRepository;
import com.yywh.util.DateUtil;
import com.yywh.vo.ResponseStatus;

@Service
public class AccessLogService {
	private Logger logs = LoggerFactory.getLogger(AccessLogService.class);
	@Autowired
	private AccessLogRepository accessLogRepository;

    public ResponseStatus<AccessLog> addLog(AccessLog entity) {
    	ResponseStatus<AccessLog> responseStatus = new ResponseStatus<AccessLog>();
    	entity.setId(null);
    	AccessLog bean = accessLogRepository.save(entity);
    	if(bean != null) {
    		responseStatus.setStatus(1);
    		responseStatus.setMessage("添加访问日志成功！");
        	return responseStatus;
    	}
		responseStatus.setStatus(0);
		responseStatus.setMessage("添加访问日志失败！");
    	return responseStatus;
    }

    public Page<AccessLog> queryPage(String title, String[] createTime, PageRequest pageRequest) throws ParseException{
		Date beginDate = new Date();
		Date endDate = new Date();
		if(createTime!=null&&createTime.length>1) {
			beginDate = DateUtil.stringToDate(createTime[0]);
			endDate = DateUtil.stringToDate(createTime[0]);
			logs.info("begindate={},endDate={}",createTime[0],createTime[1]);
		}
		//Predicate predicate = criteriaQuery.equalsIgnoreCase("jing2").and(entity.getAccount().startsWith("12"));
		/*return userRepository.findByNameLikeAndOperTimeBetween("%"+name+"%",beginDate,endDate,pageRequest);*/
//		List<User> list = userRepository.findByOperTimeBetween(beginDate,endDate);
//		if(list != null) {
//			logs.info("list size={}", list.size());
//		}
		return accessLogRepository.findByTitleLike("%"+title+"%", pageRequest);
	}
}
