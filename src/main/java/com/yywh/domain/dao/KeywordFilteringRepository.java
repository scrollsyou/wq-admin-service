package com.yywh.domain.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.yywh.domain.bean.KeywordFiltering;

public interface KeywordFilteringRepository extends Repository<KeywordFiltering, Long> {

	KeywordFiltering save(KeywordFiltering entity); 
    
    Page<KeywordFiltering> findByKeywordLikeOrderByOperTimeDesc(String keyword, Pageable pageable);

    KeywordFiltering findOne(Long id);
    
    void delete(KeywordFiltering entity);
}
