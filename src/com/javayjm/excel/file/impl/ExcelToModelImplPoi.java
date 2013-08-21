/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-20
*  create 
*  poi 实现xlsx的读取
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.javayjm.excel.file.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.javayjm.excel.config.RuturnConfig;
import com.javayjm.excel.config.RuturnPropertyParam;
import com.javayjm.excel.file.ExcelToModel;


public class ExcelToModelImplPoi implements ExcelToModel {
    private File excelFile = null;
    private RuturnConfig excelConfig = null;
    private Map valueMap = null;
    private List fixityList = null;

    public ExcelToModelImplPoi(File excelFile, RuturnConfig excelConfig,
        Map valueMap) {
        this.excelConfig = excelConfig;
        this.excelFile = excelFile;
        this.valueMap = valueMap;
    }

    /*
     * 对于配置文件中,指定取固定值的属性,先取到List中.无则为空
     */
    private void setFixity() {
        List fixityList = new ArrayList();
        Map pmap = this.excelConfig.getPropertyMap();

        for (Iterator it = pmap.values().iterator(); it.hasNext();) {
            RuturnPropertyParam propertyBean = (RuturnPropertyParam) it.next();

            if (propertyBean.getFixity().toUpperCase().trim().equals("YES")) {
                fixityList.add(propertyBean);
            }
        }

        this.fixityList = fixityList;
    }

    public List getModelList() {
    	
    	if (excelFile.getName().toLowerCase().indexOf(".csv")>=0){
    		return getModelListByCsv();
    	}
    	
        this.setFixity();

        List modelList = new ArrayList();

        try {
            Workbook book = null;
            if(excelFile.getPath().endsWith(".xls")) {
            	book = new HSSFWorkbook(new FileInputStream(this.excelFile));
            } else {
            	book = new XSSFWorkbook(new FileInputStream(this.excelFile));
            }

            Sheet sheet = book.getSheetAt(excelConfig.getSheetNo());  //支持指定sheet
            Row titleRow = sheet.getRow(0);

            for (int i = excelConfig.getStartRow() - 1; i < sheet.getLastRowNum(); i++) { //从指定数据行开始
                Object obj = this.getModelClass(excelConfig.getClassName());
                BeanWrapper bw = new BeanWrapperImpl(obj);
                boolean isTitle = true;  //标题行跳过

                // 对excel每一列的值进行取值.
                Row row = sheet.getRow(i);
                for (int j = 0; j < row.getLastCellNum(); j++) {
                	
                    // 取得Excel表头
                    String excelTitleName = String.valueOf(getCellValue(titleRow.getCell(j)));

                    // 取得Excel值
                    Object _obj = getCellValue(row.getCell(j));
                    String value =  (_obj==null || "null".equals(_obj.toString()))?null:String.valueOf(_obj).trim();
                    
                    isTitle = isTitle && excelTitleName.equals(value);
                    if (isTitle){
                    	continue;
                    }
                    
                    // 取得配置属性
                    RuturnPropertyParam propertyBean = (RuturnPropertyParam) excelConfig.getPropertyMap().get(excelTitleName);
                    if (propertyBean == null) {
                    	propertyBean = (RuturnPropertyParam) excelConfig.getPropertyMap()
                        	.get(excelConfig.getClassName()+"#"+(j+1));
                    }
                    
                    //System.out.println("i = " + i + "  j =" + j + " value ="+ value + " title = " + excelTitleName);
                    if (propertyBean != null) {

                        // //做出判断,是否需要 Text/Value 值转换.
                        if (propertyBean.getCodeTableName().length() > 1) {
                            String valueKey = propertyBean.getCodeTableName()
                                                          .trim() + value;

                            Object obj1 = this.valueMap.get(valueKey);

                            if (obj1 == null) {
                                value = "";
                            } else {
                                value = obj1.toString();
                            }
                        }

                        if ((value == null) || (value.length() < 1)) {
                            value = propertyBean.getDefaultValue();
                        }
                        if (propertyBean.getDataType().equals("java.util.Date") 
                        		|| propertyBean.getDataType().equals("Date")
                        		|| propertyBean.getDataType().equals("java.sql.Date")
                        		|| propertyBean.getDataType().equals("java.sql.Timestamp") 
                        		|| propertyBean.getDataType().equals("Timestamp")){
                        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        	Date date = null;
                        	if (value.length()>0){
                        		try {
									date = sdf.parse(value);
								} catch (Exception e) {
									try {
										sdf = new SimpleDateFormat("yyyy/MM/dd");
										date = sdf.parse(value);
									} catch (Exception e2) {
										e2.printStackTrace();
									}
								}
                        	}
                        	bw.setPropertyValue(propertyBean.getName(), date);
                        }else if (propertyBean.getDataType().equals("int") 
                        		|| propertyBean.getDataType().equals("Integer")
                        		|| propertyBean.getDataType().equals("BigInteger")
                        		|| propertyBean.getDataType().equals("long")
                        		|| propertyBean.getDataType().equals("Long") 
                        		|| propertyBean.getDataType().equals("short")
                        		|| propertyBean.getDataType().equals("Short") ){
                        	//BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short 
                        	NumberFormat nf = new DecimalFormat("#####0");
                        	nf.setParseIntegerOnly(true);
                        	Number n = 0;
                        	if (value.length()>0){
                        		try {
									n = nf.parse(value);
								} catch (Exception e) {
								}
                        	}
                        	bw.setPropertyValue(propertyBean.getName(), n);
                        }else if (propertyBean.getDataType().equals("float") 
                        		|| propertyBean.getDataType().equals("Float")
                        		|| propertyBean.getDataType().equals("double")
                        		|| propertyBean.getDataType().equals("Double") 
                        		|| propertyBean.getDataType().equals("BigDecimal") ){
                        	NumberFormat nf = new DecimalFormat("#####0.0000") ;
                        	Number n = 0;
                        	if (value.length()>0){
                        		try {
									n = nf.parse(value);
								} catch (Exception e) {
								}
                        	}
                        	bw.setPropertyValue(propertyBean.getName(), n);
                        }else{
                        	bw.setPropertyValue(propertyBean.getName(), value);
                        }
                    }
                }

                /*
                 * 设置属性中的固定值.
                 */
                for (Iterator it = this.fixityList.iterator(); it.hasNext();) {
                    RuturnPropertyParam propertyBean = (RuturnPropertyParam) it.next();
                    Object value = this.valueMap.get(propertyBean.getName());

                    if ((value == null) || (value.toString().length() < 1)) {
                        value = propertyBean.getDefaultValue();
                    }

                    bw.setPropertyValue(propertyBean.getName(), value);
                }

                if (!isTitle){
                	modelList.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return modelList;
    }
    
    private Object getCellValue(Cell cell){
    	if (null == cell) return null;
    	
    	try{
		    switch (cell.getCellType()) {
		    case Cell.CELL_TYPE_NUMERIC:
		    	if (HSSFDateUtil.isCellDateFormatted(cell)) {
		    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    		return sdf.format(cell.getDateCellValue());
		    	}else{
		    		return cell.getNumericCellValue();
		    	}
		    case Cell.CELL_TYPE_STRING:
		     return cell.getStringCellValue();
		    case Cell.CELL_TYPE_FORMULA:
		     return cell.getCellFormula();
		    case Cell.CELL_TYPE_BLANK:
		     return "";
		    case Cell.CELL_TYPE_BOOLEAN:
		     return cell.getBooleanCellValue();
		    case Cell.CELL_TYPE_ERROR:
		     return cell.getErrorCellValue();
		    }
    	}catch(Exception e){
    		System.out.println("ERR( return null): ROW="+cell.getRowIndex()+",COL="+cell.getColumnIndex());
    		return null;
    	}
	    
	    return "";
	    
	}

    public List getModelListByCsv() {
        this.setFixity();

        List modelList = new ArrayList();
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(this.excelFile));

            String[] titleNames = null;
            String[] values = null;
            String line = in.readLine();
            
            //从指定数据行开始，
            for(int i=1; i<(excelConfig.getStartRow()>1?excelConfig.getStartRow()-1:1); i++){
            	line = in.readLine();
            }
            boolean first = true;
            while ((null != line) && (line.trim().length() > 0)) {
                values = line.split("[, \t]");  
                
                if(first){
                	titleNames = values;
                	first = false;
                }

                Object obj = this.getModelClass(excelConfig.getClassName());
                BeanWrapper bw = new BeanWrapperImpl(obj);
                boolean isTitle = true; //标题行跳过

                // 对每一列的值进行取值.
                for (int j = 0; j < titleNames.length; j++) {
                    // 取得表头
                    String titleName = titleNames[j].trim();

                    // 取得值
                    String value = j<values.length?values[j].trim():null;
                    
                    isTitle = isTitle && titleName.equals(value);
                    
                    //					
                    // 取得配置属性
                    RuturnPropertyParam propertyBean = (RuturnPropertyParam) excelConfig.getPropertyMap()
                                                                                        .get(titleName);
                    if (propertyBean == null) {
                    	propertyBean = (RuturnPropertyParam) excelConfig.getPropertyMap()
                        	.get(excelConfig.getClassName()+"#"+(j+1));
                    }
                    if (propertyBean != null) {
                        // //做出判断,是否需要 Text/Value 值转换.
                        if (propertyBean.getCodeTableName().length() > 1) {
                            String valueKey = propertyBean.getCodeTableName()
                                                          .trim() + value;
                            Object obj1 = this.valueMap.get(valueKey);

                            if (obj1 == null) {
                                value = "";
                            } else {
                                value = obj1.toString();
                            }
                        }

                        if ((value == null) || (value.length() < 1)) {
                            value = propertyBean.getDefaultValue();
                        }

                        if (propertyBean.getDataType().equals("java.util.Date") 
                        		|| propertyBean.getDataType().equals("Date")
                        		|| propertyBean.getDataType().equals("java.sql.Date")
                        		|| propertyBean.getDataType().equals("java.sql.Timestamp") 
                        		|| propertyBean.getDataType().equals("Timestamp")){
                        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        	Date date = null;
                        	if (value.length()>0){
                        		try {
									date = sdf.parse(value);
								} catch (Exception e) {
									try {
										sdf = new SimpleDateFormat("yyyy/MM/dd");
										date = sdf.parse(value);
									} catch (Exception e2) {
										e2.printStackTrace();
									}
								}
                        	}
                        	bw.setPropertyValue(propertyBean.getName(), date);
                        }else if (propertyBean.getDataType().equals("int") 
                        		|| propertyBean.getDataType().equals("Integer")
                        		|| propertyBean.getDataType().equals("BigInteger")
                        		|| propertyBean.getDataType().equals("long")
                        		|| propertyBean.getDataType().equals("Long") 
                        		|| propertyBean.getDataType().equals("short")
                        		|| propertyBean.getDataType().equals("Short") ){
                        	//BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short 
                        	NumberFormat nf = new DecimalFormat("#####0");
                        	nf.setParseIntegerOnly(true);
                        	Number n = 0;
                        	if (value.length()>0){
                        		try {
									n = nf.parse(value);
								} catch (Exception e) {
								}
                        	}
                        	bw.setPropertyValue(propertyBean.getName(), n);
                        }else if (propertyBean.getDataType().equals("float") 
                        		|| propertyBean.getDataType().equals("Float")
                        		|| propertyBean.getDataType().equals("double")
                        		|| propertyBean.getDataType().equals("Double") 
                        		|| propertyBean.getDataType().equals("BigDecimal") ){
                        	NumberFormat nf = new DecimalFormat("#####0.0000") ;
                        	Number n = 0;
                        	if (value.length()>0){
                        		try {
									n = nf.parse(value);
								} catch (Exception e) {
								}
                        	}
                        	bw.setPropertyValue(propertyBean.getName(), n);
                        }else{
                        	bw.setPropertyValue(propertyBean.getName(), value);
                        }
                    }
                }

                /*
                 * 设置属性中的固定值.
                 */
                for (Iterator it = this.fixityList.iterator(); it.hasNext();) {
                    RuturnPropertyParam propertyBean = (RuturnPropertyParam) it.next();
                    Object value = this.valueMap.get(propertyBean.getName());

                    if ((value == null) || (value.toString().length() < 1)) {
                        value = propertyBean.getDefaultValue();
                    }

                    bw.setPropertyValue(propertyBean.getName(), value);
                }

                if (!isTitle){
                	modelList.add(obj);
                }

                /*read next*/
                line = in.readLine();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return modelList;
    }

    @SuppressWarnings("unused")
    private Object getModelClass(String className) {
        Object obj = null;

        try {
            obj = Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
