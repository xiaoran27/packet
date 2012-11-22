
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-20
*   create
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.biff.FontRecord;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {

    private String filename = null;
    private WritableWorkbook writableWorkbook = null;
    private int rowCount = 0;

    public ExcelUtil(String filename) {
        this.filename = filename;
    }


    public int getRowCount() {
        return rowCount;
    }



    /**
     * 新建一个指定名称的sheet.
     *
     * @param sheetname
     * @return
     * @throws IOException 当文件异常或sheetname存在.
     */
    public WritableSheet createWritableSheet(String sheetname) throws IOException {
        if (null==writableWorkbook){
            writableWorkbook = Workbook.createWorkbook(new File(filename));
        }

        WritableSheet sheet = writableWorkbook.getSheet(sheetname);
        if (null!=sheet){
            throw new IOException(sheetname + " exists in " + filename);
        }
        sheet = writableWorkbook.createSheet(sheetname, 0);

        return sheet;
    }

    /**
     * 从指定行(start)开始写入数据(rowData)到excel(sheet)中
     *
     * @param sheet WritableSheet excel的工作薄
     * @param start int 第一个要写入数据的行
     * @param rowData List<List> 数据
     *
     * @return 成功写入的数据行数
     *
     * @throws WriteException 格式或数据错误
     */
    public int writeHeader2sheet(WritableSheet sheet, List<String> headerData)
      throws WriteException,IOException {
      int count = 0;

        List<String> data = headerData;

        for (int j = 0; j < data.size(); j++) {
          //format
          jxl.write.WritableCellFormat wcf = new jxl.write.WritableCellFormat();
          wcf.setWrap(true);
          wcf.setAlignment(jxl.format.Alignment.CENTRE);
          wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
          jxl.write.WritableFont wf = new jxl.write.WritableFont(WritableFont.TIMES,
                  10, WritableFont.BOLD, true);
          wcf.setFont(wf);

          //Label(列号,行号 ,内容 )
          jxl.write.Label colData = new jxl.write.Label(j, 0,data.get(j),wcf);  //TODO: 会不会有乱码和格式问题
          sheet.addCell(colData);
        }

        count++;

      rowCount = rowCount + count;

      return count;
    }

    /**
     * 从指定行(start)开始写入数据(rowData)到excel(sheet)中
     *
     * @param sheet WritableSheet excel的工作薄
     * @param start int 第一个要写入数据的行
     * @param rowData List<List> 数据
     *
     * @return 成功写入的数据行数
     *
     * @throws WriteException 格式或数据错误
     */
    public int writeData2sheet(WritableSheet sheet, int start, List<List<String>> rowData)
      throws WriteException,IOException {
      int count = 0;

      for (int i = 0; i < rowData.size(); i++) {
        List<String> data = rowData.get(i);

        //row height
        sheet.setRowView(rowCount+i, 500*4);

        for (int j = 0; j < data.size(); j++) {
          //format
          jxl.write.WritableCellFormat wcf = new jxl.write.WritableCellFormat();
          wcf.setWrap(true);
          wcf.setAlignment(jxl.format.Alignment.LEFT);
          wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

          //column width
          sheet.setColumnView(j, j==0?10:50);

          //Label(列号,行号 ,内容 )
          jxl.write.Label colData = new jxl.write.Label(j, rowCount + start + i,data.get(j),wcf);  //TODO: 会不会有乱码和格式问题
          sheet.addCell(colData);
        }

        count++;
      }

      rowCount = rowCount + count;

      return count;
    }

    public void close() throws IOException,WriteException {
        if (null!=writableWorkbook){
            writableWorkbook.write();  //必须一次写,不可分开写
            writableWorkbook.close();
            writableWorkbook = null;
        }
    }

    /**
     * 写入数据(rowData)到excel(sheet)中
     *
     * @param sheet WritableSheet excel的工作薄
     * @param rowData List<List> 数据
     *
     * @return 成功写入的数据行数
     *
     * @throws Exception 格式或数据错误
     */
    public int writeData2sheet(WritableSheet sheet, List<List<String>> rowData)
    throws Exception {
        return writeData2sheet(sheet, 0, rowData);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {


        try {
            //data convert
            List<List<String>> allData = new ArrayList<List<String>>();
            List<String> data = new ArrayList<String>();
            int start = (int)System.currentTimeMillis()&0xffff;
            for (int i = start; i < start+10; i++) {
                data.add(String.valueOf(i));
            }
            allData.add(data);

            //write data to excel
            ExcelUtil excelUtil = new ExcelUtil("test.toexcel.xls");
            WritableSheet writableSheet = excelUtil.createWritableSheet(TextFormat.getDateTime(TextFormat.YYYYMMDD+TextFormat.HHMMSS));

            int count = excelUtil.writeHeader2sheet(writableSheet, data);
            System.out.println(excelUtil.getRowCount());

            count = excelUtil.writeData2sheet(writableSheet, allData);
            System.out.println(excelUtil.getRowCount());

            count = excelUtil.writeData2sheet(writableSheet, allData);
            System.out.println(excelUtil.getRowCount());

            excelUtil.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
