package com.yywh.service;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yywh.domain.bean.User;
import com.yywh.domain.dao.OperateLogRepository;
import com.yywh.domain.dao.UserRepository;
import com.yywh.util.CodeUtil;
import com.yywh.util.DateUtil;
import com.yywh.vo.ResponseStatus;

@Service
public class UserService {
	private Logger logs = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserRepository userRepository;

    public ResponseStatus<User> addUser(User entity) {
    	ResponseStatus<User> responseStatus = new ResponseStatus<User>();
    	//设置id为空
		entity.setId(null);
		entity.setOperTime(new Date());
		//md5加密
//		entity.setPassword(CodeUtil.enCodeMd5_UTF8(entity.getPassword()));
    	//判断帐号是否存在
		Integer existsCount = userRepository.countByAccount(entity.getAccount());
		logs.info("判断帐号是否存在:account={},count={}", entity.getAccount(), existsCount);
		if(existsCount != null && existsCount > 0) {
    		responseStatus.setStatus(0);
    		responseStatus.setMessage("用户帐号已存在！");
        	return responseStatus;
		}
    	//添加用户
    	User bean = userRepository.save(entity);
    	if(bean != null) {
    		responseStatus.setStatus(1);
    		responseStatus.setMessage("添加用户成功！");
        	return responseStatus;
    	}
		responseStatus.setStatus(0);
		responseStatus.setMessage("添加用户失败！");
    	return responseStatus;
    }

    /**
     * 修改用户信息
     * @param entity
     * @return
     */
    public ResponseStatus<User> updUser(User entity) {
    	ResponseStatus<User> responseStatus = new ResponseStatus<User>();
		User bean = userRepository.findOne(entity.getId());
		if(bean == null) {
    		responseStatus.setStatus(0);
    		responseStatus.setMessage("用户不存在！");
        	return responseStatus;
		} else {
	    	//判断帐号是否存在
			bean.setName(entity.getName());
			bean.setAccount(entity.getAccount());
			bean.setPhone(entity.getPhone());
			bean.setPassword(entity.getPassword());
			Integer existsCount = userRepository.countByAccountAndIdNot(bean.getAccount(), bean.getId());
			logs.info("判断帐号是否存在:account={},count={}", entity.getAccount(), existsCount);
			if(existsCount != null && existsCount > 0) {
	    		responseStatus.setStatus(0);
	    		responseStatus.setMessage("用户帐号已存在！");
	        	return responseStatus;
			}
			bean = userRepository.save(bean);
			if(bean != null) {
	    		responseStatus.setStatus(1);
	    		responseStatus.setMessage("修改用户成功！");
	        	return responseStatus;
			}
		}
		responseStatus.setStatus(0);
		responseStatus.setMessage("修改用户失败！");
    	return responseStatus;
    }
    
	public Page<User> queryPage(String name,String[] createTime, PageRequest pageRequest) throws ParseException{
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
		return userRepository.findByNameLikeOrderByOperTimeDesc("%"+name+"%", pageRequest);
	}
    
    public ResponseStatus<User> findOne(Long id){
    	ResponseStatus<User> responseStatus = new ResponseStatus<User>();
    	responseStatus.setStatus(1);
    	responseStatus.setData(userRepository.findOne(id));
    	return responseStatus;
    }

    /**
     * 删除用户
     * @param entity
     * @return
     */
	public ResponseStatus<User> delUser(Long id) {
		User entity = userRepository.findOne(id);
		if(entity == null) {
    		return new ResponseStatus<User>(0, "用户不存在！", entity);
		} else {
	    	userRepository.delete(entity);
    		return new ResponseStatus<User>(1, "删除用户成功！", entity);
		}
    }
	
	/**
	 * 根据帐号查询用户
	 * @param account
	 * @return
	 */
	public User findByAccount(String account) {
		return userRepository.findByAccount(account);
	}

	/**
	 * 根据用户token查询用户
	 * @param account
	 * @return
	 */
	public User findByToken(String token) {
		return userRepository.findByToken(token);
	}
	
	/**
	 * 判断用户登录
	 * @param token
	 * @return
	 */
	public ResponseStatus hasLogin(String token) {
		ResponseStatus<User> responseStatus = new ResponseStatus<User>();
		User user = this.findByToken(token);
		if(user==null){
			responseStatus.setStatus(0);
			responseStatus.setMessage("用户token已失效,请重新登陆!");
		}else{
			responseStatus.setStatus(1);
			responseStatus.setData(user);
		}
		return responseStatus;
	}
	
	/**
	 * 修改登录用户token信息
	 * @param token
	 * @param account
	 * @return
	 */
	public User updUserToken(User entity) {
		return userRepository.save(entity);
	}
}
