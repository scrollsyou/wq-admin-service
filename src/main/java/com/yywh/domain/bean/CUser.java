package com.yywh.domain.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * c端用户注册表
 * @author you
 *
 */
@Entity
public class CUser implements Serializable {

	private static final long serialVersionUID = -7865277301021806294L;

	@Id
	@GenericGenerator(name="generator", strategy="identity")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(nullable=false,unique=true)
	private String account;
	
	@Column(nullable=false)
	private String name;

	@Column(nullable=false)
	private String password;
	
	private String token;//登录token

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
