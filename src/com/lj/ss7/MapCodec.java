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
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-13
* + //2h内10/s分散处理
* + //上一组已存满
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-15
* + //防止越界
* + final int MSU_xxx
* + int nullPosForLine = 0;  //每个设置中首个可存放msu的位置
* + int nvlPosForStep = nullPosForLine; //每个msu可存放的位置
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-16
* + implement encodecMtFsm(...)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-20
* + //计算文件的行数，作为发送速率
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-9-25/27/30
* M //防止step<1
* +M encodecMxFsm(...)  //支持mo-deliver 
* M //仅data[7]有QUOTE
* M //data[8]不存在
* M //SMS's len: isChinese?字节数:字符数
\*************************** END OF CHANGE REPORT HISTORY ********************/
package com.lj.ss7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

import org.ajwcc.pduUtils.gsm3040.PduUtils;

import com.lj.utils.ISOUtil;
import com.xiaoran27.tools.HexFormat;


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
	  
    MapCodec mapCodec = new MapCodec();
    mapCodec.usage();
	    
    //	  String str = "8613800210500";
    //	  System.out.println(ISOUtil.hexdump(ISOUtil.str2bigbcd(str+(str.length()%2==0?"":"F"), false)));

//	byte[] msu = mapCodec.encodecMoFsm(0x010203,0x030201,"8613800210500","8613743168","8613788992292","8613901988686","中文");
//	System.out.println(ISOUtil.hexdump(msu));
//	  
//	msu = mapCodec.encodecMtFsm(0x010203,0x030201,"8615644141","8613010455500","460012510636057","13851667199","01234567890123456789");
//	System.out.println(ISOUtil.hexdump(msu));
	  

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
    System.out.println("Usage: java -DmoDeliver=1 -DmsuSpeed=10 -DmsuHours=2 -DmsuOverload=2 -cp packet.jar com.lj.ss7.MapCodec srcfile [dstfile]");
    System.out.println("\t-DmoDeliver={MO_DELIVER} OPTION, MO FOR DELIVER(1) OR SUBMIT(0), def: 1");
    System.out.println("\t-DmsuSpeed={MSU_SPEED} OPTION, speed(=lines) , def: 10");
	System.out.println("\t-DmsuHours={MSU_HOURS} OPTION, time(=max time): hour, def: 2");
	System.out.println("\t-DmsuOverload={MSU_OVERLOAD} OPTION, more msu, def: 2");
    System.out.println("\tsrcfile -  CSV file ");
    System.out.println("\tdstfile -  OPTION, MSU file.  def: ${srcfile}.msu");
    System.out.println();
    System.out.println("\tNotes:");
    System.out.println("\t1. CSV file format:");
    System.out.println("\t\ttitle: momt,dpc,opc,cd,cg,sender,receiver,content,rate");
    System.out.println("\t\tdemo1: mo,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"SMS content\",5/30s");
    System.out.println("\t\tdemo2: mt,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"短信内容\",50/10m");
    System.out.println("\t\tdemo3: mo,0x010203,0x030201,8613800210500,8613743168,8613901988686,8613788992292,\"SMS content|短信内容\",500/1h");
    System.out.println("\t2. MSU file format: hex by space, a MTP3's MSU per line.");
    System.out.println("\t\tdemo1: 83 64 fe 0b 01 fe 0b 02 09 81 03 0e 1a 0b 12 08 00 12 04 68 ...");
    System.out.println();
  }
  

  /**
   * 据CSV的格式进行MO编码生成对应的MTP3码流.
   *
   * @param srcfile  指定格式的CSV文件
   * @param dstfile  每行是一个MTP3的MSU
   * @return  个数
   */
  public int encodecMoMtFsm(String csvfile, String msuFile) {
	  
	  //计算文件的行数，作为发送速率
	  int lineCnt = 0;
	  try {
	        BufferedReader br = new BufferedReader(new FileReader(new File(csvfile)));
	        while (null != br.readLine()) {
	      	  lineCnt ++;
	        }
	        br.close();
	    }catch (Exception e){}
	
	  
	final int MO_DELIVER = Integer.parseInt(System.getProperty("moDeliver", "1"));  //1-DELIVER,0-SUBMIT
	final int MSU_OVERLOAD = Integer.parseInt(System.getProperty("msuOverload", "2"));  //多发MSU的个数
	final int MSU_SPEED = Math.max((lineCnt+9)/10*10,Integer.parseInt(System.getProperty("msuSpeed", "10")));  //一般是文件中的行数
	final int MSU_STORE_HOURS = Integer.parseInt(System.getProperty("msuHours", "2"));  //2h，一般是rate的最大时长
	final int MSU_MAX_SIZE = MSU_SPEED*60*60*MSU_STORE_HOURS;  //speed*hours
	
	
	String[] msuArray = new String[MSU_MAX_SIZE];  //每秒一个。
	int nullPosForLine = 0;  //每个设置中首个可存放msu的位置
	
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

        String[] data = line.split(",");//momt,dpc,opc,cdSmsc,cgGt,sender,receiver,content,rate
        if (data.length >= 8) { //content(=data[8])里面可能有','且用'"'做QUOTE
        	
        	if (data[7].startsWith("\"")) {
        		data[7] = data[7].substring(1); 
        	}
        	
        	boolean rateIsLastest = data[data.length-1].matches("[0-9]+/[0-9]+[sShHmM]");
        	int contentEndPos = data.length - ( rateIsLastest ? 2 : 1);
        	StringBuffer sb = new StringBuffer();
        	for (int i=8; i <= contentEndPos; i++){
        		sb.append(',');
        		if ( i != contentEndPos ){
        			sb.append(data[i]);
        		}else{
        			if (data[i].endsWith("\"")){
        				sb.append(data[i].substring(0,data[i].length()-1)); 
        			}
        		}
        	}
        	
        	if (contentEndPos <= 7  && data[7].endsWith("\"")) {  //仅data[7]有QUOTE
        		data[7] = data[7].substring(0,data[7].length()-1); 
        	}
        	
        	data[7] = data[7] + sb.toString();  //content
        	if (data.length>8){ //data[8]不存在
        		data[8] = rateIsLastest ? data[data.length-1] : null; //rate
        	}
        }
        if (data.length >= 8) {  
        	int dpc=Integer.parseInt(data[1].substring(2),16);
        	int opc=Integer.parseInt(data[2].substring(2),16);
        	
        	byte[] msu = null;
        	if ("mo".equalsIgnoreCase(data[0])){
        	  if (MO_DELIVER == 1){
        		  msu = encodecMoFsm(dpc,opc,data[3],data[4],"8613800210500", data[6],data[5],data[7].length()<1?"no content":data[7]);
        	  }else{
        		  msu = encodecMoFsm(dpc,opc,data[3],data[4],data[6],data[5],data[7].length()<1?"no content":data[7]);
        	  }
        	}else{
        	  msu = encodecMtFsm(dpc,opc,data[3],data[4],data[6],data[5],data[7].length()<1?"no content":data[7]);
        	}
        	if (null!=msu){
	        	String sMsu = HexFormat.bytes2str(msu, true);
	        	bw.write(sMsu);
	        	bw.write("\r\n");
	        	count ++;
	        	
        		//计算设置的发送频率
	        	String rate = data.length>8 ? data[8] : null;  //支持rate的设置,//data[8]不存在
	        	String[] rates = (rate==null?"1/1h":rate).split("/");
	        	char rflag = rates[1].charAt(rates[1].length()-1);
	        	int _msuCnt = Integer.parseInt(rates[0])+MSU_OVERLOAD;
	        	int _msuTime = 3600;
	        	if ('h'==rflag || 'H'==rflag){
	        		_msuTime = Integer.parseInt(rates[1].substring(0,rates[1].length()-1)) * 3600;
	        	}else if ('m'==rflag || 'M'==rflag){
	        		_msuTime = Integer.parseInt(rates[1].substring(0,rates[1].length()-1)) * 60;
	        	}else if ('s'==rflag || 'S'==rflag){
	        		_msuTime = Integer.parseInt(rates[1].substring(0,rates[1].length()-1)) * 1;
	        	}else {
	        		_msuTime = Integer.parseInt(rates[1].substring(0,rates[1].length())) * 1;
	        	} 
	        	int step = MSU_SPEED * (_msuTime/_msuCnt);
	        	step = step <1 ? 1 : step;  //防止step<1
	        	
	        	//save to  array
	        	int nvlPosForStep = nullPosForLine; //每个msu可存放的位置
	        	for (int i=0; i<_msuCnt; i++){
	        		int j=nvlPosForStep;  
		        	for (; j<nvlPosForStep+(i+1)*step; j++){
		        		if (j >= MSU_MAX_SIZE){  //防止越界
		        			System.out.println(MSU_MAX_SIZE+" line="+line);  
		        			System.out.println(count+" lines<DISCARD> j="+j+"; nvlPosForStep="+nvlPosForStep+"; i="+i+";step="+step);
		        			break;
		        		}
		        		
		        		if (null==msuArray[j]){
		        			msuArray[j] = sMsu;
		        			nvlPosForStep = nvlPosForStep+step;
		        			if (i==0) nullPosForLine = j+1;
		        			break;
		        		}
		        	}
        			if (j == nvlPosForStep+(i+1)*step){  
        				System.out.println(MSU_MAX_SIZE+" line="+line);  
        				System.out.println("CONFLICT(NO NULL POS): from "+nvlPosForStep+" to "+j);  
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
      bw2.write("-- send msu speed is "+MSU_SPEED);
      bw2.write("\r\n");
      
      final String FILL_MSU = " 81"+msuArray[0].substring(3,6*3)+" 03 11 40 31 32 33 34";  //test msu:81 16 0f 32 24 04 fd 03 11 40 31 32 33 34
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

    if ((count+9)/10*10 != MSU_SPEED){
    	System.out.println("Please set jvm -DmsuSpeed="+(count+10)/10*10+" if you see 'CONFLICT(NO NULL POS)... '. current msuSpeed="+MSU_SPEED); 
    }else{
    	System.out.println("current msuSpeed="+MSU_SPEED);
    }
    return count;
  }
  
  public byte[] encodecMtFsm(int dpc, int opc, String receiverRpdaImsi, String senderTpoa, String content) {
	  return encodecMtFsm(dpc, opc, "8613749233", "8613743168", "8613800210500", receiverRpdaImsi, senderTpoa, content);
  }
  
  public byte[] encodecMtFsm(int dpc, int opc, String receiverRpdaImsi, String rpoaSmsc, String senderTpoa, String content) {
	  return encodecMtFsm(dpc, opc, "8613749233", "8613743168", rpoaSmsc, receiverRpdaImsi, senderTpoa, content);
  }
  
  public byte[] encodecMtFsm(int dpc, int opc, String cd, String cg, String receiver, String sender, String content) {
	  return encodecMtFsm(dpc, opc, cd, cg, "8613800210500", receiver, sender, content);
  }
  
  /**
   * 构造MT消息(sms-deliver).
   */
  public byte[] encodecMtFsm(int dpc, int opc, String cd, String cg, String smsc, String receiver, String sender, String content) {
	    return encodecMxFsm(true, dpc,  opc,  cd,  cg,  smsc,  receiver,  sender,  content);
  }
  
  /**
   * 构造MO消息(sms-deliver).
   */
  public byte[] encodecMoFsm(int dpc, int opc, String cd, String cg, String smsc, String receiver, String sender, String content) {
	    return encodecMxFsm(false, dpc,  opc,  cd,  cg,  smsc,  receiver,  sender,  content);
  }
  
  /**
   * 构造Mx消息(sms-deliver).
   *
  * @param dpc
  * @param opc
  * @param cd  - sccp called
  * @param cg  - sccp calling
  * @param smsc - SMSC
  * @param sender - calling
  * @param receiver - called
  * @param content - short message content
  * @return MSU码流
  */
  public byte[] encodecMxFsm(boolean mt, int dpc, int opc, String cd, String cg, String smsc, String receiver, String sender, String content) {
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
	    byte[] zq_sms_sccpCd = cd.getBytes(); 
	    byte[] zq_rnm_sccpCg = cg.getBytes(); 
	    byte[] zq_send = sender.getBytes(); 
	    int zq_tid = 0x8080;

	    byte[] ZQ_DIALOG_MT_LIST_V2 = {
	        0x6b, 0x1E, //FALG,LEN1
	        0x28,0x1C,//FALG,LEN=LEN1-2
	        0x06,0x07,//FALG,LEN
	        0x00,0x11,(byte) 0x86,0x05, 0x01,0x01,0x01,
	        (byte) 0xA0,0x11,//FALG,LEN
	        0x60,0x0F,//FALG,LEN
	        (byte) 0x80,0x02,0X07,(byte)0X80,
	        (byte) 0xA1,0x09,//FALG,LEN
	        0x06,0x07,//FALG,LEN
	        0x04,0x00,0x00,0x01,0x00,0x19,0x03  
	        //0.4.0.0.1.0.25.x : x=[2,3]在wireshark中的info显示: invoke forwardSM(2)/mt-forwardSM(3)
	    };

	    //sccp cd(msc address)
	    boolean zq_even = (cd.length() % 2) == 0;
	    byte[] cdBcd = ISOUtil.str2bigbcd(cd + (zq_even ? "" : "F"), false);
	    int cdBcdLen = (cdBcd.length * 2) - (zq_even ? 0 : 1);
	    msu[pos++] = (byte) (5 + cdBcd.length); //Fill length
	    msu[pos++] = (byte) 0x12; //routing on GT  12,52
	    msu[pos++] = (byte) 0x08; //MSC  
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
	    msu[pos++] = (byte) 0x08; //SMSC  
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

	    //convert smsc
	    zq_even = (smsc.length() % 2) == 0;
	    byte[] smscBcd = ISOUtil.str2bigbcd(smsc + (zq_even ? "" : "F"), false);
	    int smscBcdLen = (smscBcd.length * 2) - (zq_even ? 0 : 1);
	    
	    //convert receiver
	    zq_even = (receiver.length() % 2) == 0;
	    byte[] receiverBcd = ISOUtil.str2bigbcd(receiver + (zq_even ? "" : "F"), false);
	    int receiverBcdLen = (receiverBcd.length * 2) - (zq_even ? 0 : 1);
	    
	    //convert sender
	    zq_even = (sender.length() % 2) == 0;
	    byte[] senderBcd = ISOUtil.str2bigbcd(sender + (zq_even ? "" : "F"), false);
	    int senderBcdLen = (senderBcd.length * 2) - (zq_even ? 0 : 1);

	    //fill sccp part
	    //fill sccp's length
	    int zq_pos_sccp = pos; //keep sccp length position   
	    
	    //short message content
	    boolean isChinese = ISOUtil.isChinese(content);
	    byte[] _content = isChinese ? PduUtils.encodeUcs2UserData(content) : PduUtils.unencodedSeptetsToEncodedSeptets(PduUtils.stringToUnencodedSeptets(content)) ;
	    byte[] ZQ_UI = new byte[10 + _content.length];
	    ZQ_UI[0] = 0x00;
	    ZQ_UI[1] = (byte) ( isChinese ? 0x08 : 0x00); //0x00-7bit, 0x04-8bit, 0x08-UCS2(16bit)
	    ZQ_UI[2] = 0x21;//YY-12
	    ZQ_UI[3] = (byte) 0x80;//MM-08
	    ZQ_UI[4] = 0x61;//DD-16
	    ZQ_UI[5] = 0x41;//HH-14
	    ZQ_UI[6] = 0x10;//MM-01
	    ZQ_UI[7] = 0x30;//SS-30
	    ZQ_UI[8] = 0x23;//ZONE GTM+8
	    ZQ_UI[9] = (byte) (isChinese ? _content.length : content.length()); //SMS's len: isChinese?字节数:字符数
	    System.arraycopy(_content, 0, ZQ_UI, 10, _content.length);

	    //short format
	    msu[pos++] = (byte) (8 + ZQ_DIALOG_MT_LIST_V2.length + 12 + 2 + receiverBcd.length +2 + 3 + smscBcd.length + 3 + senderBcd.length + ZQ_UI.length);
	    msu[pos++] = (byte) 0x62;

	    //short format
	    msu[pos++] = (byte) (6 + ZQ_DIALOG_MT_LIST_V2.length + 12 + 2 + receiverBcd.length +2 + 3 + smscBcd.length + 3 + senderBcd.length + ZQ_UI.length);

	    msu[pos++] = (byte) 0x48;
	    msu[pos++] = (byte) 0x04;

	    //OTID related to last    
	    msu[pos++] = (byte) ((zq_tid >> 24) & 0xFF);
	    msu[pos++] = (byte) ((zq_tid >> 16) & 0xFF);
	    msu[pos++] = (byte) ((zq_tid >> 8) & 0xFF);
	    msu[pos++] = (byte) ((zq_tid) & 0xFF);

	    //copy DIALOG part
	    System.arraycopy(ZQ_DIALOG_MT_LIST_V2, 0, msu, pos, ZQ_DIALOG_MT_LIST_V2.length);
	    pos = pos + ZQ_DIALOG_MT_LIST_V2.length;

	    //copy COMPONENT part
	    msu[pos++] = (byte) 0x6c;

	    //not used temperily
	    msu[pos++] = (byte) (2 + 8 + 2 + receiverBcd.length + 3 + smscBcd.length +2 + 3 + senderBcd.length + ZQ_UI.length);
	    msu[pos++] = (byte) 0xa1;

	    //not used temperily
	    msu[pos++] = (byte) (8 + 2 + receiverBcd.length + 3 + smscBcd.length +2 + 3 + senderBcd.length + ZQ_UI.length);

	    //fill invoke id
	    msu[pos++] = (byte) 0x02;
	    msu[pos++] = (byte) 0x01;
	    msu[pos++] = (byte) 0x01;

	    //fill op code
	    msu[pos++] = (byte) 0x02;
	    msu[pos++] = (byte) 0x01;
	    msu[pos++] = mt?(byte) 0x2c : (byte) 0x2e; //P44_MT(0x2c),P46_MO(0x2e)

	    //fill sequence
	    msu[pos++] = (byte) 0x30;

	    //not used temperily
	    msu[pos++] = (byte) (2 + receiverBcd.length + 3 + smscBcd.length +2 + 3 + senderBcd.length + ZQ_UI.length);

	    //fill RPDA:Xmsi  
	    msu[pos++] = (byte) 0x80;  //80-IMSI,81-LMSI
	    msu[pos++] = (byte) receiverBcd.length; //(cdBcd.length +1);
//	    msu[pos++] = (byte) 0x91;
	    System.arraycopy(receiverBcd, 0, msu, pos, receiverBcd.length);
	    pos = pos + receiverBcd.length;

	    //fill RPOA:smsc  
	    msu[pos++] = (byte) 0x84;  //
	    msu[pos++] = (byte) (smscBcd.length + 1);
	    msu[pos++] = (byte) 0x91;
	    System.arraycopy(smscBcd, 0, msu, pos, smscBcd.length);
	    pos = pos + smscBcd.length;

	    //fill RP_DATA  
	    msu[pos++] = (byte) 0x04;
	    
	    //TP_OA:sender
	    msu[pos++] = (byte) (3 + senderBcd.length + ZQ_UI.length);
	    msu[pos++] = (byte) 0x24;
	    msu[pos++] = (byte) (senderBcdLen);
	    msu[pos++] = (byte) 0xa1;
	    System.arraycopy(senderBcd, 0, msu, pos, senderBcd.length);
	    pos = pos + senderBcd.length;
	    
//	    msu[pos++] = (byte) (ZQ_UI.length);
	    //fill UI
	    System.arraycopy(ZQ_UI, 0, msu, pos, ZQ_UI.length);
	    pos = pos + ZQ_UI.length;

	    //return real msu
	    byte[] _msu = new byte[pos];
	    System.arraycopy(msu, 0, _msu, 0, _msu.length);

	    return _msu;
	  }

  /**
   * 构造MO消息(SMS-SUBMIT).
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
  public byte[] encodecMoFsm(int dpc, int opc, String cd, String cg, String receiver, String sender, String content) {
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
    int zq_tid = 0x8080;

    byte[] ZQ_DIALOG_MO_LIST_V2 = {
        0x6B, 0x1A, 0x28, 0x18, 0x06, 0x07, 0x00, 0x11, (byte) 0x86, 0x05, 0x01, 0x01, 0x01, (byte) 0xA0, 0x0D, 0x60, 0x0B, (byte) 0xA1, 0x09, 0x06, 0x07, 0x04,
        0x00, 0x00, 0x01, 0x00, 0x15, 0x02
      };

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

    //short message content
    byte[] _content = content.getBytes();
    byte[] ZQ_UI = new byte[3 + _content.length];
    ZQ_UI[0] = 0x00;
    ZQ_UI[1] = (byte) (ISOUtil.isChinese(content) ? 0x08 : 0x00); //0x00-7bit, 0x08-UCS2(16bit)
    ZQ_UI[2] = (byte) _content.length; //SMS's len
    System.arraycopy(_content, 0, ZQ_UI, 3, _content.length);
    
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

    //fill RPDA:smsc  
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
    //TP_DA: receiver
    msu[pos++] = (byte) (4 + receiverBcd.length + ZQ_UI.length);
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
