package com.yywh.domain.bean;

import java.io.Serializable;

/**
 * 访问日志
 * @author you
 */
public class AccessLogVo implements Serializable {

	private Integer cunt;
    
    private Integer dayNum;

	public Integer getCunt() {
		return cunt;
	}

	public void setCunt(Integer cunt) {
		this.cunt = cunt;
	}

	public Integer getDayNum() {
		return dayNum;
	}

	public void setDayNum(Integer dayNum) {
		this.dayNum = dayNum;
	}
	
}
