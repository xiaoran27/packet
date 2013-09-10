package com.javayjm.excel.config.impl;

import com.javayjm.excel.config.ConfigConstant;
import com.javayjm.excel.config.ExcelConfigManager;
import com.javayjm.excel.config.RuturnConfig;
import com.javayjm.excel.config.RuturnPropertyParam;

import org.dom4j.Document;
import org.dom4j.Element;

import org.dom4j.io.SAXReader;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ExcelConfigManagerImpl implements ExcelConfigManager {
    private String configName = "./ImportExcelToModel.xml";
    private SAXReader saxReader;
    private Document doc;
    private Element root;

    public ExcelConfigManagerImpl() {
//        String filename = this.getClass().getResource(configName).getPath();
        if (!new File(configName).exists()){
        	configName = "./conf/ImportExcelToModel.xml";  //÷ß≥÷confœ¬≈‰÷√
        }
        String filename = configName;

        saxReader = new SAXReader();
        try {
            doc = saxReader.read(new File(filename));
            //doc = saxReader.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        root = doc.getRootElement();
    }

    public Element getModelElement(String modelName) {

        List list = root.elements("model");//.elements();
        Element model = null;
        Element returnModel = null;

        for (Iterator it = list.iterator(); it.hasNext();) {
            model = (Element) it.next();

            if (model.attributeValue("id").equals(modelName)) {
                returnModel = model;

                break;
            }
        }

        return returnModel;
    }

    public RuturnConfig getModel(String modelName, String flag) {
        Element model = this.getModelElement(modelName);
        RuturnConfig result = new RuturnConfig();

        if (model != null) {
        	//∂¡sheetNo≈‰÷√
        	String sheetNo = model.attributeValue(ConfigConstant.MODEL_SHEETNO);
        	if (sheetNo!=null && sheetNo.matches("[0-9]+")){
        		result.setSheetNo(Integer.parseInt(sheetNo));
        	}
        	
        	//∂¡startRow≈‰÷√
        	String startrow = model.attributeValue(ConfigConstant.MODEL_STARTROW);
        	if (startrow!=null && startrow.matches("[0-9]+")){
        		result.setStartRow(Integer.parseInt(startrow));
        	}
        	
            result.setClassName(model.attributeValue(ConfigConstant.MODEL_CLASS));
            result.setPropertyMap(this.getPropertyMap(model));
        }

        return result;
    }

    private Map<String, RuturnPropertyParam> getPropertyMap(Element model) {
        Map<String, RuturnPropertyParam> propertyMap = new HashMap<String, RuturnPropertyParam>();
        List list = model.elements("property");
        Element property = null;

        for (Iterator it = list.iterator(); it.hasNext();) {
            property = (Element) it.next();

            RuturnPropertyParam modelProperty = new RuturnPropertyParam();
            modelProperty.setName(property.attributeValue(
                    ConfigConstant.PROPERTY_NAME));
            modelProperty.setColumn(property.attributeValue(
                    ConfigConstant.PROPERTY_COLUMN));
            modelProperty.setExcelTitleName(property.attributeValue(
                    ConfigConstant.PROPERTY_EXCEL_TITLE_NAME));
            modelProperty.setDataType(property.attributeValue(
                    ConfigConstant.PROPERTY_DATA_TYPE));
            modelProperty.setMaxLength(property.attributeValue(
                    ConfigConstant.PROPERTY_MAX_LENGTH));

            modelProperty.setFixity(property.attributeValue(
                    ConfigConstant.PROPERTY_FIXITY));
            modelProperty.setCodeTableName(property.attributeValue(
                    ConfigConstant.PROPERTY_CODE_TABLE_NAME));
            modelProperty.setDefaultValue(property.attributeValue(
                    ConfigConstant.PROPERTY_DEFAULT));

            if (modelProperty.getExcelTitleName().trim().length()>0){
            	propertyMap.put(modelProperty.getExcelTitleName(), modelProperty);
            }
            if (modelProperty.getColumn().trim().length()>0){
            	propertyMap.put(model.attributeValue(
                    ConfigConstant.MODEL_CLASS)+"#"+modelProperty.getColumn(), modelProperty);
            }
            
        }

        return propertyMap;
    }

    public static void main(String[] args) {
        ExcelConfigManagerImpl test = new ExcelConfigManagerImpl();
        
        String modelName = "deptModel";
        test.getModel(modelName, "");

        System.out.println(test.getModelElement(modelName));
    }
}
