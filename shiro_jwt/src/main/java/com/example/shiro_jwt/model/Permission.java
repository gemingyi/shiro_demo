package com.example.shiro_jwt.model;


import com.example.shiro_jwt.utils.PaginationEntity;

import java.io.Serializable;

public class Permission  extends PaginationEntity implements Serializable {
	private Integer pid;
	private String permissionName;
	private  String desc;
	
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
