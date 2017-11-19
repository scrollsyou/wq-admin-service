package com.yywh.domain.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.yywh.domain.bean.OperateLog;

public interface OperateLogRepository extends Repository<OperateLog, Long> {

	OperateLog save(OperateLog entity); 
    
    Page<OperateLog> findByRemarkLike(String remark, Pageable pageable);
    
}
