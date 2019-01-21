package com.example.shiro_jwt.model;


import com.example.shiro_jwt.utils.PaginationEntity;

import java.io.Serializable;
import java.util.List;

public class User extends PaginationEntity implements Serializable{
	private Integer uid ;
	private String userName ;
	private String password ;
	private String realName;
	private Integer sflag;
	private Integer lock;
	private String createDate;
	private List<Role> roles;

	private String sToken;
	private String textStr;

	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public Integer getSflag() {
		return sflag;
	}
	public void setSflag(Integer sflag) {
		this.sflag = sflag;
	}
	public Integer getLock() {
		return lock;
	}
	public void setLock(Integer lock) {
		this.lock = lock;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getsToken() {
		return sToken;
	}
	public void setsToken(String sToken) {
		this.sToken = sToken;
	}
	public String getTextStr() {
		return textStr;
	}
	public void setTextStr(String textStr) {
		this.textStr = textStr;
	}
}
