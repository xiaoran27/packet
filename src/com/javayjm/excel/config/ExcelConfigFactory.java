package com.javayjm.excel.config;

import com.javayjm.excel.config.impl.ExcelConfigManagerImpl;

public class ExcelConfigFactory {
	private static ExcelConfigManager instance = new ExcelConfigManagerImpl();
	public static ExcelConfigManager createExcelConfigManger(){
		return instance;
	}
}
