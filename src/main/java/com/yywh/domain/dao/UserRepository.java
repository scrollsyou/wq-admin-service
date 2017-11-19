package com.yywh.domain.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.yywh.domain.bean.User;

public interface UserRepository extends Repository<User, Long> {

    User save(User entity); 
    
    Page<User> findByNameLikeAndOperTimeBetween(String name,Date min,Date max,Pageable pageable);
    
    List<User> findByOperTimeBetween(Date min,Date max);
    
    Page<User> findByNameLike(String name,Pageable pageable);
    
    User findOne(Long id);
    
    void delete(User entity);
    
    Integer countByAccountAndIdNot(String account, Long id);
    
    Integer countByAccount(String account);
    
    User findByAccount(String account);
    
    User findByToken(String token);
}
