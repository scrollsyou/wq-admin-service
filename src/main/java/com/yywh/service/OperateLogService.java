package com.yywh.service;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yywh.domain.bean.OperateLog;
import com.yywh.domain.dao.OperateLogRepository;
import com.yywh.util.DateUtil;
import com.yywh.vo.ResponseStatus;

@Service
public class OperateLogService {
	private Logger logs = LoggerFactory.getLogger(OperateLogService.class);
	@Autowired
	private OperateLogRepository operateLogRepository;

    public ResponseStatus<OperateLog> addLog(OperateLog entity) {
    	ResponseStatus<OperateLog> responseStatus = new ResponseStatus<OperateLog>();
    	entity.setId(null);
    	entity = operateLogRepository.save(entity);
    	if(entity != null) {
    		responseStatus.setStatus(1);
    		responseStatus.setMessage("添加操作日志成功！");
        	return responseStatus;
    	}
		responseStatus.setStatus(0);
		responseStatus.setMessage("添加操作日志失败！");
    	return responseStatus;
    }

    public Page<OperateLog> queryPage(String remark, String[] createTime, PageRequest pageRequest) throws ParseException{
		Date beginDate = new Date();
		Date endDate = new Date();
		if(createTime!=null&&createTime.length>1) {
			beginDate = DateUtil.stringToDate(createTime[0]);
			endDate = DateUtil.stringToDate(createTime[0]);
			logs.info("begindate={},endDate={}",createTime[0],createTime[1]);
		}
		return operateLogRepository.findByRemarkLike("%"+remark+"%", pageRequest);
	}
}
