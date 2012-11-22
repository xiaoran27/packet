package com.javayjm.model;

import java.util.Date;

public class Dept {
	private String deptName;
	private String deptCode;
	private String sendFileName;
	private String deptNo;
	private Date date;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getSendFileName() {
		return sendFileName;
	}
	public void setSendFileName(String sendFileName) {
		this.sendFileName = sendFileName;
	}
	public String getDeptNo() {
		return deptNo;
	}
	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}
	
	
}
