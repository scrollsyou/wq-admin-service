package com.yywh.domain.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.yywh.domain.bean.AccessLog;

public interface AccessLogRepository extends Repository<AccessLog, Long> {

	AccessLog save(AccessLog entity); 
    
    Page<AccessLog> findByTitleLike(String title, Pageable pageable);
    
}
