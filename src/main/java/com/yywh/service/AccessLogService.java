package com.yywh.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yywh.domain.bean.AccessLog;
import com.yywh.domain.bean.AccessLogVo;
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
		return accessLogRepository.findByTitleLikeOrderByOperTimeDesc("%"+title+"%", pageRequest);
	}

	public List<LinkedHashMap<String, Integer>> statisticsDailyVisits() {
		List<LinkedHashMap<String, Integer>> listMap = new ArrayList<LinkedHashMap<String, Integer>>();
		for(int i=0;i<30;i++) {
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
			map.put("name", (i+1));
			map.put("日访问量", 0);
			listMap.add(map);
		}
		List<Object[]> resultList = accessLogRepository.statisticsDailyVisits();
		if(resultList != null && resultList.size() > 0) {
			for(Object[] result : resultList) {
				LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
				Integer dayNum = Integer.valueOf(result[0].toString());
				map.put("name", dayNum);
				map.put("日访问量", Integer.valueOf(result[1].toString()));
				listMap.add(dayNum-1, map);
			}
		}
		Gson gson = new Gson();
		gson.toJson(listMap);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("sales", gson.toString());
		return listMap;
	}
}
