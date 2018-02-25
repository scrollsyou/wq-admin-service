package com.yywh.domain.dao;

import org.springframework.data.repository.Repository;

import com.yywh.domain.bean.CUser;

public interface CUserRepository extends Repository<CUser, Long> {

	CUser save(CUser entity); 
    
    CUser findByAccountAndPassword(String account,String password);
    
    CUser findByToken(String token);
}
