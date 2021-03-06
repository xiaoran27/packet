
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-15
*   create
*   支持excel保存为csv
*   M usage()
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-22
* M cdSmsc->cd cgGT->cg
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.javayjm.model;

import com.javayjm.excel.ExcelManager;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 短信自动化测试配置相关
 *
 * @author xiaoran27 $author$
 * @version $Revision$
  */
/**
 * @author 0078
 *
 */
public class Sms {
	

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      Sms sms = new Sms();
      sms.usage();
      
      if (args.length > 1) {
    	  sms.saveCsvFile(args[0], args[1]);
    	  System.out.println("RESULT: see "+args[1]);
      }else if (args.length > 0) {
    	  sms.saveCsvFile(args[0], args[0] + ".csv");
    	  System.out.println("RESULT: see "+args[0] + ".csv");
      }else{
    	  System.out.println("ERR: no param，see Usage");
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  

  /**
   * USAGE
   */
  public void usage() {
//    System.out.println("Usage: java -cp commons-logging.jar"+File.pathSeparator+"spring.jar"+File.pathSeparator+"dom4j.jar"+File.pathSeparator+"jxl.jar"+File.pathSeparator+"packet.jar com.javayjm.model.Sms srcfile [dstfile]");
    System.out.println("Usage: java -cp commons-logging.jar"+File.pathSeparator+"spring.jar"+File.pathSeparator+"dom4j.jar"+File.pathSeparator+"poi.jar"+File.pathSeparator+"poi-ooxml.jar"+File.pathSeparator+"poi-ooxml-schemas.jar"+File.pathSeparator+"xmlbeans.jar"+File.pathSeparator+"packet.jar com.javayjm.model.Sms srcfile [dstfile]");

    System.out.println("\tsrcfile -  excel(xls for 97-2003) file ");
    System.out.println("\tdstfile -  OPTION, CSV file.  def: ${srcfile}.csv");
    System.out.println();
    System.out.println("\tNotes:");
    System.out.println("\t1. xls file format(see ImportExcelToModel.xml<id=smsModel>):");
    System.out.println("\t\ttitle: ...,momt=11,dpc=x,opc=x,cd=x,cg=x,sender=12,receiver=13,content=14,rate=10");
    System.out.println();
    System.out.println("\t2. CSV file format:");
    System.out.println("\t\ttitle: momt,dpc,opc,cd,cg,sender,receiver,content,rate");
    System.out.println("\t\tdemo1: mo,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"SMS content\",5/30s");
    System.out.println("\t\tdemo2: mt,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"短信内容\",50/10m");
    System.out.println("\t\tdemo3: mo,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"SMS content|短信内容\",500/1h");
    System.out.println();
  }  
	  
  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String momt = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String dpc = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String opc = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String cd = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String cg = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String sender = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String receiver = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String content = null;

  /**
   * DOCUMENT ME!
   */
  @Getter
  @Setter
  private String rate = null;

  /**
   * 读写excel或csv文件的配置数据
   *
   * @param filename excel或csv文件
   *
   * @return 短信数据列表
   */
  public List getModelList(String filename) {
    Map<String, String> map = new HashMap<String, String>();

    ExcelManager test = new ExcelManager(filename, "smsModel", map);

    return test.getModelList();
  }

  /**
   * 将短信数据列表保存为csv文件
   *
   * @param modelList 短信数据列表
   * @param cvsfile csv文件
   *
   * @return 行数
   *
   * @throws Exception 异常
   */
  public int saveCsvFile(List modelList, String cvsfile)
    throws Exception {
    int count = 0;
    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(cvsfile)));
    bw.write(getCsvTitle());
    bw.write("\r\n");

    for (int i = 0; i < modelList.size(); i++) {
      Sms sms = (Sms) modelList.get(i);

      bw.write(sms.toCsvString());
      bw.write("\r\n");
    }

    bw.close();

    return count;
  }

  /**
   * @see saveCsvFile(String src, String dst)
   */
  public int saveCsvFile(String src) throws Exception {
    return saveCsvFile(src, src + ".csv");
  }


  /**
   * 读写excel或csv文件的配置数据保存为csv文件
 * @param src excel或csv文件
 * @param dst csv文件
 * @return 行数
 * @throws Exception
 */
  public int saveCsvFile(String src, String dst) throws Exception {
    List modelList = getModelList(src);
    int count = saveCsvFile(modelList, dst);

    return count;
  }

  /**
   * @return CsvTitle
   */
  public String getCsvTitle() {
    return "momt,dpc,opc,cd,cg,sender,receiver,content,rate";
  }

  /**
   * 转为","分隔符的csv串
   *
   * @return csv串
   */
  public String toCsvString() {
    return toCsvString(",");
  }

  /**
   * 转为指定分隔符的csv串
   *
   * @param split 分隔符
   *
   * @return csv串
   */
  public String toCsvString(String split) {
    //momt,dpc,opc,cdSmsc,cgGt,sender,receiver,content,rate
    StringBuilder sb = new StringBuilder();
    sb.append(momt).append(split);
    sb.append(dpc).append(split);
    sb.append(opc).append(split);
    sb.append(cd).append(split);
    sb.append(cg).append(split);
    sb.append(sender).append(split);
    sb.append(receiver).append(split);
    sb.append("\"").append(content).append("\"").append(split);
    sb.append(rate);

    return sb.toString();
  }

}
