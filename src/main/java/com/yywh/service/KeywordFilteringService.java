package com.yywh.service;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yywh.Controller.UserController;
import com.yywh.domain.bean.KeywordFiltering;
import com.yywh.domain.bean.User;
import com.yywh.domain.dao.KeywordFilteringRepository;
import com.yywh.util.DateUtil;
import com.yywh.vo.ResponseStatus;

@Service
public class KeywordFilteringService {
	private Logger logs = LoggerFactory.getLogger(KeywordFilteringService.class);
	@Autowired
	private KeywordFilteringRepository keywordFilteringRepository;

    public ResponseStatus<KeywordFiltering> addKeyword(KeywordFiltering entity) {
    	ResponseStatus<KeywordFiltering> responseStatus = new ResponseStatus<KeywordFiltering>();
    	entity.setId(null);
    	KeywordFiltering bean = keywordFilteringRepository.save(entity);
    	if(bean != null) {
    		responseStatus.setStatus(1);
    		responseStatus.setMessage("添加关键词成功！");
        	return responseStatus;
    	}
		responseStatus.setStatus(0);
		responseStatus.setMessage("添加关键词失败！");
    	return responseStatus;
    }

    public ResponseStatus<KeywordFiltering> updKeyword(KeywordFiltering entity) {
    	ResponseStatus<KeywordFiltering> responseStatus = new ResponseStatus<KeywordFiltering>();
    	KeywordFiltering bean = keywordFilteringRepository.save(entity);
    	if(bean != null) {
    		responseStatus.setStatus(1);
    		responseStatus.setMessage("修改关键词成功！");
        	return responseStatus;
    	}
		responseStatus.setStatus(0);
		responseStatus.setMessage("修改关键词失败！");
    	return responseStatus;
    }

    public Page<KeywordFiltering> queryPage(String keyword, String[] createTime, PageRequest pageRequest) throws ParseException{
		Date beginDate = new Date();
		Date endDate = new Date();
		if(createTime!=null&&createTime.length>1) {
			beginDate = DateUtil.stringToDate(createTime[0]);
			endDate = DateUtil.stringToDate(createTime[0]);
			logs.info("begindate={},endDate={}",createTime[0],createTime[1]);
		}
		return keywordFilteringRepository.findByKeywordLikeOrderByOperTimeDesc("%"+keyword+"%", pageRequest);
	}
    
    /**
     * 删除用户
     * @param entity
     * @return
     */
	public ResponseStatus<KeywordFiltering> delKeyword(Long id) {
		KeywordFiltering entity = keywordFilteringRepository.findOne(id);
		if(entity == null) {
    		return new ResponseStatus<KeywordFiltering>(0, "关键字不存在！", entity);
		} else {
	    	keywordFilteringRepository.delete(entity);
    		return new ResponseStatus<KeywordFiltering>(1, "删除关键字成功！", entity);
		}
    }

    public ResponseStatus<KeywordFiltering> findOne(Long id){
    	ResponseStatus<KeywordFiltering> responseStatus = new ResponseStatus<KeywordFiltering>();
    	responseStatus.setStatus(1);
    	responseStatus.setData(keywordFilteringRepository.findOne(id));
    	return responseStatus;
    }

}
