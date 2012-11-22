package com.javayjm.excel.config;

import java.util.HashMap;
import java.util.Map;

public class RuturnConfig {

	private int startRow = 1;  //数据开始行
	
	private String className = null;

	private Map propertyMap = new HashMap();
	
	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}
	
	
}
