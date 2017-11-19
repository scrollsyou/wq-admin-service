package com.yywh.vo;

import java.io.Serializable;

/**
 * 菜单
 * @author you
 * id,icon,name,route,bpid,mpid
 */
public class Menu implements Serializable  {
	
	private String id;

	private String icon;
	
	private String name;
	
	private String route;
	
	private String bpid;
	
	private String mpid;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getBpid() {
		return bpid;
	}

	public void setBpid(String bpid) {
		this.bpid = bpid;
	}

	public String getMpid() {
		return mpid;
	}

	public void setMpid(String mpid) {
		this.mpid = mpid;
	}

	public Menu() {}
	
	public Menu(String id, String icon, String name, String route, String bpid, String mpid) {
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.route = route;
		this.bpid = bpid;
		this.mpid = mpid;
	}
}
