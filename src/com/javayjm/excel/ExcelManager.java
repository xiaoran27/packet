/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-22
* + //poi
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.javayjm.excel;

import com.javayjm.excel.config.ExcelConfigFactory;
import com.javayjm.excel.config.ExcelConfigManager;
import com.javayjm.excel.file.ExcelToModel;
import com.javayjm.excel.file.impl.ExcelToModelImpl;
import com.javayjm.excel.file.impl.ExcelToModelImplPoi;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExcelManager {

	private File excelFile = null;
    private String modelName = "";
    private Map valueMap = null;

    public ExcelManager(String fileName, String modelName, Map valueMap) {
        this(new File(fileName), modelName, valueMap);
    }

    public ExcelManager(File fileName, String modelName, Map valueMap) {
        this.excelFile = fileName;
        this.modelName = modelName;
        this.valueMap = valueMap;
    }
    

	
    public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Map getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map valueMap) {
		this.valueMap = valueMap;
	}

    public List getModelList() {
        ExcelConfigManager configManager = ExcelConfigFactory.createExcelConfigManger();
//        ExcelToModel etm = new ExcelToModelImpl(this.excelFile,configManager.getModel(modelName, ""), this.valueMap);  //jxl
        ExcelToModel etm = new ExcelToModelImplPoi(this.excelFile,configManager.getModel(modelName, ""), this.valueMap);  //poi
        List modelList = etm.getModelList();

        return modelList;
    }
    
    

    public static void main(String[] args) {
    	{
	        Map<String, String> map = new HashMap<String, String>();
	        //map.put("deptNo", "1");
	        map.put("bgqx永久", "Y");
	
	        String file = "G:/workspace/packet/docs/test.xls"; //test.xls
	        
	        System.out.println("========================================");
	        System.out.println(file);
	        System.out.println(map);
	        
	        ExcelManager test = new ExcelManager(file, "deptModel", map);
	        List modelList = test.getModelList();
	
	        for (int i = 0; i < modelList.size(); i++) {
	            Object obj = modelList.get(i);
	            System.out.println(ToStringBuilder.reflectionToString(obj));
	        }
    	}
        
    	{
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("deptNo", "1");
	        map.put("bgqx永久", "Y");
	
	        String file = "G:/workspace/packet/docs/test.csv"; //test.xls
	        
	        System.out.println("========================================");
	        System.out.println(file);
	        System.out.println(map);
	        
	        ExcelManager test = new ExcelManager(file, "deptModel", map);
	        List modelList = test.getModelList();
	
	        for (int i = 0; i < modelList.size(); i++) {
	            Object obj = modelList.get(i);
	            System.out.println(ToStringBuilder.reflectionToString(obj));
	        }
    	}
    	
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this)
				.append("excelFile", this.excelFile)
				.append("modelName", this.modelName)
				.append("valueMap", this.valueMap)
				.toString();
	}
    
    



    /**
     * 配制文件加强,一是可以传一个固定值到所有Bena中. 配制一个固定值.把固定值在RuturnConfig 单设成一个Map ,配制文件中,对固定值的配制必须有默认值,
     * ExcelToModelImpl 实现时,根据Excel列,属性设置完成后,对Map循环,设置其值.首先从传参Map中取值,没有取默认值设置.
     * 二是可以配制转换对应的码表值.如:excel中传的值为"长期" 可以配制成 "bgqx长期"做为键值 "C" 做为 Bean 设置的值.
     */
}
