package com.yywh.domain.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 操作日志
 * @author you
 * id,ip,oper_id,oper_name,oper_time,remark
 */
@Entity
public class OperateLog implements Serializable  {
	@Id
	@GenericGenerator(name="generator", strategy="identity")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private Long operId;
	
    private String operName;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date operTime = new Date();

    private String ip;

	@Column(nullable=false)
    private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOperId() {
		return operId;
	}

	public void setOperId(Long operId) {
		this.operId = operId;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public Date getOperTime() {
		return operTime;
	}

	public void setOperTime(Date operTime) {
		this.operTime = operTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public OperateLog() {}
	
	public OperateLog(String ip, Long operId, String operName, String remark) {
		this.ip = ip;
		this.operId = operId;
		this.operName = operName;
		this.operTime = new Date();
		this.remark = remark;
	}
}
