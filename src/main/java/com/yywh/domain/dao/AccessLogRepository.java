package com.yywh.domain.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.yywh.domain.bean.AccessLog;
import com.yywh.domain.bean.AccessLogVo;

public interface AccessLogRepository extends Repository<AccessLog, Long> {

	AccessLog save(AccessLog entity); 
    
    Page<AccessLog> findByTitleLikeOrderByOperTimeDesc(String title, Pageable pageable);
    
    @Query(value = "select count(A.oper_time) cunt,A.day_num from (select al.oper_time,date_format(al.oper_time, '%d') day_num from access_log al where date_format(al.oper_time, '%Y%m') = '201711') A group by A.day_num", 
    		nativeQuery = true)
    List<Object[]> statisticsDailyVisits();
}
