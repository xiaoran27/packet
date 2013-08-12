/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-9
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-29
* + encodecMoFsm(String csvfile, String msuFile)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-12
* + //支持rate的设置 //分散的msu文件
\*************************** END OF CHANGE REPORT HISTORY ********************/
package com.lj.ss7;

import com.lj.utils.ISOUtil;

import com.xiaoran27.tools.HexFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Arrays;
import java.util.Date;


/**
 * SS7的MAP层的编解码
 *
 * @author $author$
 * @version $Revision$
  */
public class MapCodec {
  /**
   * @param args
   */
  public static void main(String[] args) {
    //	  String str = "8613800210500";
    //	  System.out.println(ISOUtil.hexdump(ISOUtil.str2bigbcd(str+(str.length()%2==0?"":"F"), false)));

    //	byte[] msu = encodecMoFsm(0x010203,0x030201,"8613800210500","8613743168","8613901988686","8613788992292","中文");
    //	System.out.println(ISOUtil.hexdump(msu));
    MapCodec mapCodec = new MapCodec();
    mapCodec.usage();

    if (args.length > 1) {
        mapCodec.encodecMoMtFsm(args[0], args[1]);
    }else if (args.length > 0) {
      mapCodec.encodecMoMtFsm(args[0], args[0] + ".msu");
    }  
  }

  /**
   * USAGE
   */
  public void usage() {
    System.out.println("Usage: java -cp packet.jar com.lj.ss7.MapCodec srcfile [dstfile]");
    System.out.println("\tsrcfile -  CSV file ");
    System.out.println("\tdstfile -  OPTION, MSU file.  def: ${srcfile}.msu");
    System.out.println();
    System.out.println("\tNotes:");
    System.out.println("\t1. CSV file format:");
    System.out.println("\t\ttitle: momt,dpc,opc,cdSmsc,cgGt,sender,receiver,content,rate");
    System.out.println("\t\tdemo1: mo,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"SMS content\",5/30s");
    System.out.println("\t\tdemo2: mt,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"短信内容\",50/10m");
    System.out.println("\t\tdemo3: mo,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"SMS content|短信内容\",500/1h");
    System.out.println("\t2. MSU file format: hex by space, a MTP3's MSU per line.");
    System.out.println("\t\tdemo1: 83 64 fe 0b 01 fe 0b 02 09 81 03 0e 1a 0b 12 08 00 12 04 68 ...");
    System.out.println();
  }
  
  /**
   * 将excel转为csv文件
   *
   * @param srcfile  指定格式的CSV文件
   * @param dstfile  每行是一个MTP3的MSU
   * @return  个数
   */
  public int excel2csv(String src, String dst) {
	  int count = 0;
	  //TODO
	  
	  return count;
  }

  /**
   * 据CSV的格式进行MO编码生成对应的MTP3码流.
   *
   * @param srcfile  指定格式的CSV文件
   * @param dstfile  每行是一个MTP3的MSU
   * @return  个数
   */
  public int encodecMoMtFsm(String csvfile, String msuFile) {
	String[] msuArray = new String[60*60];  //每秒一个  ,共1h。
	
    int count = 0;
    long old = System.currentTimeMillis();
    System.out.println(count + " lines is converted from "+csvfile+" to "+msuFile+"; established MS=" + (System.currentTimeMillis() - old) + "; START AT " + new Date());

    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(csvfile)));
      BufferedWriter bw = new BufferedWriter(new FileWriter(new File(msuFile)));

      String line = br.readLine();
      if (line.startsWith("momt,")){  //skip title
    	  line = br.readLine();
      }
      while (null != line) {
        if ((line.trim().length() < 1) || line.startsWith("--") || line.startsWith("#")) { //--，#作为注释行
          line = br.readLine();

          continue;
        }

        String[] data = line.split(",");
        if (data.length >= 8) {  //momt,dpc,opc,cdSmsc,cgGt,sender,receiver,content,rate
        	int dpc=Integer.parseInt(data[1].substring(2),16);
        	int opc=Integer.parseInt(data[2].substring(2),16);
        	
        	byte[] msu = null;
        	if ("mo".equalsIgnoreCase(data[0])){
	          msu = encodecMoFsm(dpc,opc,data[3],data[4],data[5],data[6],data[7]);
        	}else{
        	  msu = encodecMtFsm(dpc,opc,data[3],data[4],data[5],data[6],data[7]);
        	}
        	if (null!=msu){
        		//计算设置的发送频率
	        	String rate = data[8];  //支持rate的设置
	        	String[] rates = (rate==null?"1/1h":rate).split("/");
	        	char rflag = rates[1].charAt(rates[1].length()-1);
	        	int msuCntPerHour = msuArray.length/3600;
	        	if ('h'==rflag || 'H'==rflag){
	        		msuCntPerHour = Integer.parseInt(rates[0]) * msuArray.length/Integer.parseInt(rates[1].substring(0,rates[1].length()-1))/3600;
	        	}else if ('m'==rflag || 'M'==rflag){
	        		msuCntPerHour = Integer.parseInt(rates[0]) * msuArray.length/Integer.parseInt(rates[1].substring(0,rates[1].length()-1))/60;
	        	}else {
	        		msuCntPerHour = Integer.parseInt(rates[0]) * msuArray.length/Integer.parseInt(rates[1].substring(0,rates[1].length()-1));
	        	} 
	        	int step = 3600/msuCntPerHour;
	        	
	        	String sMsu = HexFormat.bytes2str(msu, true);
	        	//save to  array
	        	for (int i=0; i<msuCntPerHour; i++){
		        	bw.write(sMsu);
		        	bw.write("\r\n");
		        	count ++;
		        	
		        	for (int j=i*step; j<(i*step+step); j++){
		        		if (null==msuArray[j]){
		        			msuArray[j] = sMsu;
		        			break;
		        		}
		        	}
	        	}
        	}
        }
        
        line = br.readLine();
      } //end while (null!=line)

      // 关闭流  
      br.close();
      bw.close();
      
      //分散的msu文件
      BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(msuFile.substring(0, msuFile.length()-3)+"avg.msu")));
      final String FILL_MSU = msuArray[0].substring(0,7*3)+"03 11 40 31 32 33 34";  //81 16 0f 32 24 04 fd 03 11 40 31 32 33 34
      for (int i=0; i<msuArray.length; i++){
    	  if (null==msuArray[i]){
    		  bw2.write(FILL_MSU);
    	  }else{
    		  bw2.write(msuArray[i]);
    	  }
    	  bw2.write("\r\n");
      }
      bw2.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(count + " lines is converted from "+csvfile+" to "+msuFile+"; established MS=" + (System.currentTimeMillis() - old) + "; FINISHED AT " + new Date());

    return count;
  }
  
  /**
   * 构造MT消息.
   *
  * @param dpc
  * @param opc
  * @param cd  - sccp called
  * @param cg  - sccp calling
  * @param sender - calling
  * @param receiver - called
  * @param content - short message content
  * @return MSU码流
  */
  public byte[] encodecMtFsm(int dpc, int opc, String cd, String cg, String sender, String receiver, String content) {
    byte[] msu = new byte[276];
    //TODO
    
    System.out.println("unsupport MT now.");
    return null;
  }

  /**
   * 构造MO消息.
   *
  * @param dpc
  * @param opc
  * @param cd  - sccp called
  * @param cg  - sccp calling
  * @param sender - calling
  * @param receiver - called
  * @param content - short message content
  * @return MSU码流
  */
  public byte[] encodecMoFsm(int dpc, int opc, String cd, String cg, String sender, String receiver, String content) {
    byte[] msu = new byte[276];
    int pos = 0;

    //fill SIO
    msu[pos++] = (byte) 0x83;

    //fill DPC and OPC
    msu[pos++] = (byte) (dpc & 0xFF);
    msu[pos++] = (byte) ((dpc >> 8) & 0xFF);
    msu[pos++] = (byte) ((dpc >> 16) & 0xFF);

    msu[pos++] = (byte) (opc & 0xFF);
    msu[pos++] = (byte) ((opc >> 8) & 0xFF);
    msu[pos++] = (byte) ((opc >> 16) & 0xFF);

    //fill SLS,MSGID,PROTOCAL_CLASS
    msu[pos++] = (byte) 0x00; //0-15
    msu[pos++] = (byte) 0x09;
    msu[pos++] = (byte) 0x80; //80,81,01

    //fill 3 pointers  
    msu[pos++] = (byte) 0x03;

    int zq_pos1_cg = pos; //keep the positon of cg pointer
    msu[pos++] = (byte) 0x0e; //cg len: temp value  0d,0e,0f
    msu[pos++] = (byte) 0x19; //content len: temp value  19,17

    //fill sccp cd and sg
    byte[] zq_sms_sccpCd = cd.getBytes(); //{ '8','6','1','3','8','0','0','2','1','0','5','0','0'}; //sccp cd(sms address: 8613800210500)
    byte[] zq_rnm_sccpCg = cg.getBytes(); //{ '8','6','1','3','7','4','3','1','6','8'}; //sccp cg(SIMM_I address)  
    byte[] zq_send = sender.getBytes(); //{ '8','6','1','3','9','0','1','9','8','8','6','8','6'};
    int zq_tid = 0x8080;

    byte[] ZQ_DIALOG_MO_LIST_V2 = {
        0x6B, 0x1A, 0x28, 0x18, 0x06, 0x07, 0x00, 0x11, (byte) 0x86, 0x05, 0x01, 0x01, 0x01, (byte) 0xA0, 0x0D, 0x60, 0x0B, (byte) 0xA1, 0x09, 0x06, 0x07, 0x04,
        0x00, 0x00, 0x01, 0x00, 0x15, 0x02
      };

    //short message content
    byte[] _content = content.getBytes();
    byte[] ZQ_UI = new byte[3 + _content.length];
    ZQ_UI[0] = 0x00;
    ZQ_UI[1] = (byte) (ISOUtil.isChinese(content) ? 0x08 : 0x00); //0x00-7bit, 0x08-UCS2(16bit)
    ZQ_UI[2] = (byte) _content.length; //SMS's len
    System.arraycopy(_content, 0, ZQ_UI, 3, _content.length);

    //sccp cd(sms address: 8613800210500)
    boolean zq_even = (cd.length() % 2) == 0;
    byte[] cdBcd = ISOUtil.str2bigbcd(cd + (zq_even ? "" : "F"), false);
    int cdBcdLen = (cdBcd.length * 2) - (zq_even ? 0 : 1);
    msu[pos++] = (byte) (5 + cdBcd.length); //Fill length
    msu[pos++] = (byte) 0x12; //routing on GT  12,52
    msu[pos++] = (byte) 0x08; //SMSC  
    msu[pos++] = (byte) 0x00; //00

    if (zq_even) {
      msu[pos++] = (byte) 0x12; //E164,EVEN digit 
    } else {
      msu[pos++] = (byte) 0x11; //E164,ODD digit 
    }

    msu[pos++] = (byte) 0x04; //International number
    System.arraycopy(cdBcd, 0, msu, pos, cdBcd.length); //Fill digits
    pos = pos + cdBcd.length;

    int zq_pos2_cg = pos; //keep the offset of cg part

    //put cg to MSU
    zq_even = (cg.length() % 2) == 0;

    byte[] cgBcd = ISOUtil.str2bigbcd(cg + (zq_even ? "" : "F"), false);
    int cgBcdLen = (cgBcd.length * 2) - (zq_even ? 0 : 1);
    msu[pos++] = (byte) (5 + cgBcd.length); //Fill length 
    msu[pos++] = (byte) 0x12; //routing on GT  12,52
    msu[pos++] = (byte) 0x08; //MSC  
    msu[pos++] = (byte) 0x00; //00

    if (zq_even) {
      msu[pos++] = (byte) 0x12; //E164,EVEN digit 
    } else {
      msu[pos++] = (byte) 0x11; //E164,ODD digit 
    }

    msu[pos++] = (byte) 0x04; //International number
    System.arraycopy(cgBcd, 0, msu, pos, cgBcd.length); //Fill digits
    pos = pos + cgBcd.length;

    int zq_pos3_content = pos; //keep the offset of content part

    //Adjust cg pointer and content ptr 
    msu[zq_pos1_cg] = (byte) (zq_pos2_cg - zq_pos1_cg); //cg len 
    msu[zq_pos1_cg + 1] = (byte) (zq_pos3_content - zq_pos1_cg - 1); //content len 

    //convert sender
    zq_even = (sender.length() % 2) == 0;

    byte[] senderBcd = ISOUtil.str2bigbcd(sender + (zq_even ? "" : "F"), false);
    int senderBcdLen = (senderBcd.length * 2) - (zq_even ? 0 : 1);

    //convert receiver
    zq_even = (receiver.length() % 2) == 0;

    byte[] receiverBcd = ISOUtil.str2bigbcd(receiver + (zq_even ? "" : "F"), false);
    int receiverBcdLen = (receiverBcd.length * 2) - (zq_even ? 0 : 1);

    //fill sccp part
    //fill sccp's length
    int zq_pos_sccp = pos; //keep sccp length position     

    //short format
    msu[pos++] = (byte) (8 + ZQ_DIALOG_MO_LIST_V2.length + 12 + 3 + cdBcd.length + 3 + senderBcd.length + 6 + receiverBcd.length + ZQ_UI.length);
    //long format   
    //	  msu[pos++] = (byte)(9+ZQ_DIALOG_MO_LIST_V2.length+12+3+cdBcd.length+3+senderBcd.length+6+receiverBcd.length+ZQ_UI.length); 
    msu[pos++] = (byte) 0x62;

    //short format
    msu[pos++] = (byte) (6 + ZQ_DIALOG_MO_LIST_V2.length + 12 + 3 + cdBcd.length + 3 + senderBcd.length + 6 + receiverBcd.length + ZQ_UI.length);

    msu[pos++] = (byte) 0x48;
    msu[pos++] = (byte) 0x04;

    //OTID related to last    
    msu[pos++] = (byte) ((zq_tid >> 24) & 0xFF);
    msu[pos++] = (byte) ((zq_tid >> 16) & 0xFF);
    msu[pos++] = (byte) ((zq_tid >> 8) & 0xFF);
    msu[pos++] = (byte) ((zq_tid) & 0xFF);

    //copy DIALOG part
    System.arraycopy(ZQ_DIALOG_MO_LIST_V2, 0, msu, pos, ZQ_DIALOG_MO_LIST_V2.length);
    pos = pos + ZQ_DIALOG_MO_LIST_V2.length;

    //copy COMPONENT part
    msu[pos++] = (byte) 0x6c;

    //not used temperily
    msu[pos++] = (byte) (10 + 3 + cdBcd.length + 3 + senderBcd.length + 6 + receiverBcd.length + ZQ_UI.length);
    msu[pos++] = (byte) 0xa1;

    //not used temperily
    msu[pos++] = (byte) (8 + 3 + cdBcd.length + 3 + senderBcd.length + 6 + receiverBcd.length + ZQ_UI.length);

    //fill invoke id
    msu[pos++] = (byte) 0x02;
    msu[pos++] = (byte) 0x01;
    msu[pos++] = (byte) 0x01;

    //fill op code
    msu[pos++] = (byte) 0x02;
    msu[pos++] = (byte) 0x01;
    msu[pos++] = (byte) 0x2e; //P46_MO

    //fill sequence
    msu[pos++] = (byte) 0x30;

    //not used temperily
    msu[pos++] = (byte) (3 + cdBcd.length + 3 + senderBcd.length + 6 + receiverBcd.length + ZQ_UI.length);

    //fill RPDA:sms  
    msu[pos++] = (byte) 0x84;
    msu[pos++] = (byte) (cdBcd.length + 1);
    msu[pos++] = (byte) 0x91;
    System.arraycopy(cdBcd, 0, msu, pos, cdBcd.length);
    pos = pos + cdBcd.length;

    //fill RPOA:sender  
    msu[pos++] = (byte) 0x82;
    msu[pos++] = (byte) (senderBcd.length + 1);
    msu[pos++] = (byte) 0x91;
    System.arraycopy(senderBcd, 0, msu, pos, senderBcd.length);
    pos = pos + senderBcd.length;

    //fill RP_DATA  
    msu[pos++] = (byte) 0x04;
    //not used temperily  
    msu[pos++] = (byte) (4 + receiverBcd.length + ZQ_UI.length);
    //not used temperily 
    msu[pos++] = (byte) 0x01;
    msu[pos++] = (byte) 0x6D;
    msu[pos++] = (byte) (receiverBcdLen);
    msu[pos++] = (byte) 0x91;
    System.arraycopy(receiverBcd, 0, msu, pos, receiverBcd.length);
    pos = pos + receiverBcd.length;

    //not used temperily   
    //fill UI
    System.arraycopy(ZQ_UI, 0, msu, pos, ZQ_UI.length);
    pos = pos + ZQ_UI.length;

    //return real msu
    byte[] _msu = new byte[pos];
    System.arraycopy(msu, 0, _msu, 0, _msu.length);

    return _msu;
  }
}
