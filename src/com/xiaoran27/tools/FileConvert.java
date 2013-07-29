
/*

SMSS dat format:
typedef struct _MsgFileHeader {  //16byte=4+4+4+4
     INT32 efl;
     INT32 wfl;
     INT32 dwl;
     INT32 spare;
} MsgFileHeader
typedef struct {  //20byte=4+4+4+4+4
     INT32     header;  // 4D3C2B1A | 1A2B3C4D
     INT32      ipAddr;
     INT32     type;
     INT32     len;
     INT32     reserved;  //时间秒
} TMsgHeader;
typedef  struct
{
     TMsgHeader   msgh;
     INT8 data[MAX_MSG_LEN];
}TMsg;

typedef struct
{            
     UINT32 code;  
     UINT8  linkId;                   
     UINT8  portType;                           
     UINT8  reserved;                           
     UINT8  repeatNum;               
     UINT32 msgLength;                       
     UINT8  msg[MAX_MSU_LEN - 12];   // 320
} TMsu;
const INT32 MAX_MSU_LEN = 320;
const INT32 MAX_MSG_LEN =1500;


ZCXC dat format:
BYTE: 4*0XF+8+[4B+4B+4B+FLAG(8B:01+ALL 00)+data(MTP1<3B>+MTP2<3B>+。。。)]
00000000h: 5A 43 58 43 34 2E 33 30 06 06 06 06 06 06 06 06 ; ZCXC4.30........
00000010h: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ; ................
00000020h: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ; ................
00000030h: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ; ................
00000040h: 00 00 00 00 00 00 00 00 53 00 03 00 00 03 00 CA ; ........S......?
00000050h: 69 DB 51 2D 01 00 00 00 00 00 00 00 92 02 AE 87 ; i跶-........?畤
00000060h: 3B 00 83 24 04 FD 16 0F 32 0E 09 00 03 10 1D 0D ; ;.?.?.2.......
00000070h: 52 EE 00 61 04 64 00 03 39 14 00 02 00 0D 12 08 ; R?a.d..9.......
00000080h: 00 61 04 64 00 03 39 14 10 01 00 11 E4 0F C7 04 ; .a.d..9.....??
00000090h: 07 B9 12 13 E8 07 EA 05 CF 01 01 F2 00 53 00 53 ; .?.???.?S.S
000000a0h: 00 03 00 00 03 00 CA 69 DB 51 2F 01 00 00 00 00 ; ......蔵跶/.....
000000b0h: 00 00 00 92 02 AF 87 3B 00 83 24 04 FD 16 0F 32 ; ...?瘒;.?.?.2
000000c0h: 06 09 00 03 10 1D 0D 52 EE 00 61 04 64 00 03 39 ; .......R?a.d..9
000000d0h: 14 00 02 00 0D 12 08 00 61 04 64 00 03 39 14 10 ; ........a.d..9..
000000e0h: 01 00 11 E4 0F C7 04 08 15 1D 11 E8 07 EA 05 CF ; ...??....???
000000f0h: 01 01 F2 00 53 00 29 01 03 00 00 03 40 CA 69 DB ; ..?S.).....@蔵?
00000100h: 51 36 01 00 00 00 00 00 00 00                   ; Q6........

PCAP format:
pcap头24B+[packet头16B+packet数据]
Pcap文件头24B各字段说明：
Magic：4B：0x1A 2B 3C 4D:用来标示文件的开始
Major：2B，0x02 00:当前文件主要的版本号
Minor：2B，0x04 00当前文件次要的版本号
ThisZone：4B当地的标准时间；全零
SigFigs：4B时间戳的精度；全零
SnapLen：4B最大的存储长度
LinkType：4B链路类型

Packet 包头和Packet数据组成
Packet 字段说明：
Timestamp：4B时间戳高位，精确到seconds
Timestamp：4B时间戳低位，精确到microseconds
Caplen：4B当前数据区的长度，即抓取到的数据帧长度，由此可以得到下一个数据帧的位置。
Len：4B离线数据长度：网络中实际数据帧的长度，一般不大于caplen，多数情况下和Caplen数值相等。
00000000h: D4 C3 B2 A1 02 00 04 00 00 00 00 00 00 00 00 00 ; 悦病............
00000010h: 00 90 01 00 8D 00 00 00 07 56 E7 51 00 00 00 00 ; .?.?...V鏠....
00000020h: 9C 00 00 00 9C 00 00 00 83 79 FF 1D 04 FF 1D 08 ; ?..?..儁....
00000030h: 09 81 03 0D 19 0A 12 08 00 12 04 68 31 44 90 95 ; .?........h1D悤
00000040h: 0C 12 08 00 11 04 68 31 08 90 15 05 03 76 62 74 ; ......h1.?..vbt
00000050h: 48 04 00 49 1C 00 6B 1A 28 18 06 07 00 11 86 05 ; H..I..k.(.....?
00000060h: 01 01 01 A0 0D 60 0B A1 09 06 07 04 00 00 01 00 ; ...?`.?.......
00000070h: 19 02 6C 50 A1 4E 02 01 00 02 01 2E 30 46 80 08 ; ..lP......0F€.
00000080h: 64 00 02 90 95 27 58 F7 84 08 91 68 31 08 90 15 ; d..悤'X鲃.慼1.?
00000090h: 05 F0 04 30 24 0D 91 68 31 07 59 54 86 F1 00 08 ; .?0$.慼1.YT嗰..
000000a0h: 11 01 71 51 10 93 23 1C 4F 60 76 84 8B C1 66 0E ; ..qQ.?.O`v剫羏.
000000b0h: 54 8C 67 68 67 F3 76 84 4E 00 8D 77 5B C4 8D 70 ; T実hg髒凬.峸[膷p
000000c0h: 4E 86 30 02 07 56 E7 51 01 00 00 00 8E 00 00 00 ; N?..V鏠....?..
000000d0h: 8E 00 00 00 83 03 02 01 01 02 03 00 09 80 03 0F ; ?..?.......€..
000000e0h: 19 0C 12 08 00 11 04 68 31 08 20 01 05 F0 0A 12 ; .......h1. ..?.
000000f0h: 08 00 12 04 68 31 47 13 86 64 62 62 48 04 00 00 ; ....h1G.哾bbH...
00000100h: 80 80 6B 1A 28 18 06 07 00 11 86 05 01 01 01 A0 ; €€k.(.....?...?
00000110h: 0D 60 0B A1 09 06 07 04 00 00 01 00 15 02 6C 3E ; .`.?.........l>
00000120h: A1 3C 02 01 01 02 01 2E 30 34 84 08 91 68 31 08 ; ?......04?慼1.
00000130h: 20 01 05 F0 82 08 91 68 31 09 91 88 86 F6 04 1E ;  ..饌.慼1.憟嗹..
00000140h: 01 6D 0D 91 68 31 87 98 29 92 F2 00 08 14 30 31 ; .m.慼1嚇)掤...01
00000150h: 32 33 34 35 36 37 38 39 61 62 63 64 65 66 D6 D0 ; 23456789abcdef中
00000160h: CE C4                                           ; 文
*/



/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-18
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-19
*   implements X0,X1
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-29
*  + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen = (10+4+8+6)
*  + int FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH = 0
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.xiaoran27.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.lj.utils.ISOUtil;

/**
 * SS7的采集文件格式互转
 * 
 * @author xiaoran27
 *
 */
public class FileConvert {
	
	private static int BUFFER_SIZE = 8*1024;
	
	//SMSS
	final static private int FILEHEAD_SMSS_LENGTH = 4*4;  //efl,wfl,dwl,spare 
	private byte[] FILEHEAD_SMSS = new byte[FILEHEAD_SMSS_LENGTH];
	final static private int FILEHEAD_SMSS_MSG_LENGTH = 4*5;  //header,ipAddr,type,len,reserved
	private byte[] FILEHEAD_SMSS_MSG = new byte[FILEHEAD_SMSS_MSG_LENGTH];
	final static private byte[] FILEHEAD_SMSS_MSG_FLAG0 = {0X1A,0X2B,0X3C,0X4D};
	final static private byte[] FILEHEAD_SMSS_MSG_FLAG1 = {0X4D,0X3C,0X2B,0X1A};
	private byte[] FILEHEAD_SMSS_MSG_FLAG = new byte[4];
	final static private int FILEHEAD_SMSS_PACKET_HEAD_LENGTH = 4+1*4+4;  //code + linkId,portType,reserved,repeatNum + msgLength
	private byte[] FILEHEAD_SMSS_PACKET_HEAD = new byte[FILEHEAD_SMSS_PACKET_HEAD_LENGTH];
	
	//ZCXC
	final static private int FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH = 0;
	final static private byte[] FILEHEAD_ZCXC_FLAG = {0X5A, 0X43, 0X58, 0X43, 0X34, 0X2E, 0X33, 0X30};
	final static private int FILEHEAD_ZCXC_LENGTH = 4*16+4+(12-FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH);  
	private byte[] FILEHEAD_ZCXC = new byte[FILEHEAD_ZCXC_LENGTH];
	final static private int FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen = (FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH+4+8+6);  //skip UNKNOWN,TIMESTAMP,PACKET_HEAD_FLAG,MTP2
	final static private int FILEHEAD_ZCXC_PACKET_HEAD_LENGTH = 8;  
	final static private byte[] FILEHEAD_ZCXC_PACKET_HEAD_FLAG = new byte[FILEHEAD_ZCXC_PACKET_HEAD_LENGTH];//0,7字节可能不是0
	private byte[] FILEHEAD_ZCXC_PACKET_HEAD = new byte[FILEHEAD_ZCXC_PACKET_HEAD_LENGTH];
	
	//PCAP
	final static private byte[] FILEHEAD_PCAP_FLAG = {(byte) 0XD4, (byte) 0XC3, (byte) 0XB2, (byte) 0XA1, 0X02, 0X00, 0X04, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00
		, 0X00, (byte) 0X90, 0X01, 0X00, (byte) 0X8D, 0X00, 0X00, 0X00};
	final static private int FILEHEAD_PCAP_LENGTH = 4+2*2+4+4+4+4; //Magic + Major,Minor + ThisZone + SigFigs + SnapLen + LinkType
	final static private byte[] FILEHEAD_PCAP = FILEHEAD_PCAP_FLAG;
	final static private int FILEHEAD_PCAP_PACKET_HEAD_LENGTH = 4*4; //timestampSec,timestampMs,caplen,len
	final static private byte[] FILEHEAD_PCAP_PACKET_HEAD = new byte[FILEHEAD_PCAP_PACKET_HEAD_LENGTH];

	//MTP2 FLAG
	final private byte MTP3FLAG = (byte)0x83;
	
	//TYPE
	final static private int TYPE_PCAP = 0;
	final static private int TYPE_MSU = 1;
	final static private int TYPE_DAT_ZCXC = 2;
	final static private int TYPE_DAT_SMSS = 3;
	
	final static Map<Integer,String> ctypeMap = new HashMap<Integer,String>();
	static {
		ctypeMap.put(00, "CONVERT PCAP TO PCAP");
		ctypeMap.put(10, "CONVERT MSU TO PCAP");
		ctypeMap.put(20, "CONVERT ZCXC-DAT TO PCAP");
		ctypeMap.put(30, "CONVERT SMSS-DAT TO PCAP");
		
		ctypeMap.put(01, "CONVERT PCAP TO MSU");
		ctypeMap.put(11, "CONVERT MSU TO MSU");
		ctypeMap.put(21, "CONVERT ZCXC-DAT TO MSU");
		ctypeMap.put(31, "CONVERT SMSS-DAT TO MSU");
		
		ctypeMap.put(02, "CONVERT PCAP TO ZCXC-DAT");
		ctypeMap.put(12, "CONVERT MSU TO ZCXC-DAT");
		ctypeMap.put(22, "CONVERT ZCXC-DAT TO ZCXC-DAT");
		ctypeMap.put(32, "CONVERT SMSS-DAT TO MZCXC-DATSU");
		
		ctypeMap.put(03, "CONVERT PCAP TO SMSS-DAT");
		ctypeMap.put(13, "CONVERT MSU TO SMSS-DAT");
		ctypeMap.put(23, "CONVERT ZCXC-DAT TO SMSS-DAT");
		ctypeMap.put(33, "CONVERT SMSS-DAT TO SMSS-DAT");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long old = System.currentTimeMillis();
		usage();
		
		String ctype = System.getProperty("ctype",args.length>0?args[0]:"10");
		String src = System.getProperty("srcfile",args.length>1?args[1]:"ss7mtp3.msu");
		String dst = System.getProperty("dstfile",args.length>2?args[2]:src+".pcap");
		
		String[] srcfiles={"G://workspace//packet//data//ss7mtp.msu",
				"G://workspace//packet//data//zcxc.dat",
				"G://workspace//packet//data//smss.dat"};
		
		int fi = 2;
		ctype=fi+"0";
		src = srcfiles[fi-1];
		dst = srcfiles[fi-1]+".pcap";
		
//		ctype = "20";
//		src = "G://workspace//packet//data//bjcdmazcxc//解失败文件//139PPS_0712_194958.dat";
//		dst = src+".pcap";
		
		FileConvert fc = new FileConvert();
		
		int count = -1;
		int type = Integer.parseInt(ctype);
		if (10==type){
			count = fc.msu2pcap(src,dst);
		}else if (20==type){
			count = fc.zcxcDat2pcapDirect(src,dst);
		}else if (30==type){
			count = fc.smssDat2pcapDirect(src,dst);
		}else if (21==type){
			count = fc.zcxcDat2msu(src,dst);
		}else if (31==type){
			count = fc.smssDat2msu(src,dst);
		}else{
			System.out.println("UNSUPPORT "+ctypeMap.get(type));
		}
		
		if (count != -1){
			System.out.println(count+" lines is converted from "+src+" to "+dst+", established MS = "+(System.currentTimeMillis() - old));
		}
		
	}
	
	static public void usage(){
		System.out.println("Usage: java -cp packet.jar com.xiaoran27.tools.FileConvert ctype srcfile [dstfile]");
		System.out.println("\tctype -   any assemble: 0-pcap,1-msu,2-zcxc,3-smss 。 Sample: 10,20,30,21,31");
		System.out.println("\tsrcfile -  ZCXC dat file or SMSS dat file or MSU file ");
		System.out.println("\tdstfile -  OPTION, pcap file.  def: ${srcfile}.pcap");
		System.out.println();
	}
	

	/**
	 * SMSS数据文件转为pcap文件.
	 * 
	 * @param smssDatFile - SMSS数据文件
	 * @param pcapFile - pcap文件
	 * @return 转换的包数
	 */
	public int smssDat2pcap(final String smssDatFile, final String pcapFile) {
		int count = smssDat2msu(smssDatFile,smssDatFile+".msu");
		if (count>0){
			count = msu2pcap(smssDatFile+".msu", pcapFile);
		}
		return count;
	}
	
	/**
	 * SMSS数据文件转为msu文件.
	 * 
	 * @param smssDatFile - SMSS数据文件
	 * @param msuFile - msu文件
	 * @return 转换的包数
	 */
	public int smssDat2msu(final String smssDatFile, final String msuFile) {
		int count = 0;
		
		long old = System.currentTimeMillis();
		System.out.println(count+" smssDat2msu - established MS="+(System.currentTimeMillis()-old)+"; START AT "+new Date());
		try {  
            // 源文件  
            FileInputStream fileInputStream = new FileInputStream(new File(smssDatFile));  
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(msuFile)));
  
            //read head 
            Arrays.fill(FILEHEAD_SMSS, (byte)0);
            fileInputStream.read(FILEHEAD_SMSS);
            
            while (fileInputStream.available()>0){
	            //read MSG head 
	            int msgPos = 0;
	            Arrays.fill(FILEHEAD_SMSS_MSG, (byte)0);
	            fileInputStream.read(FILEHEAD_SMSS_MSG);
	            Arrays.fill(FILEHEAD_SMSS_MSG_FLAG, (byte)0);
	            System.arraycopy(FILEHEAD_SMSS_MSG, 0, FILEHEAD_SMSS_MSG_FLAG, 0, FILEHEAD_SMSS_MSG_FLAG.length);
	            if (Arrays.equals(FILEHEAD_SMSS_MSG_FLAG, FILEHEAD_SMSS_MSG_FLAG0) 
	            		|| Arrays.equals(FILEHEAD_SMSS_MSG_FLAG, FILEHEAD_SMSS_MSG_FLAG1)){
	            }else{
	            	fileInputStream.close(); 
	            	System.out.println(smssDatFile+" isn't SMSS format msg.(ERR:-100)");  
	            	return -100;
	            }
	            int msglen = bigbytes2int(FILEHEAD_SMSS_MSG,12);
	            int ms = bytes2int(FILEHEAD_SMSS_MSG,16);
            
	            int pos = 0;  
	            byte[] buf = new byte[msglen];
	            int remainDataLen = 0;
	            byte[] packet = null;
	            int packetLen = 0;
	            int packetStartPos = pos;
            
	            //read data (至少有一个包,应该是多个完整的MSU)
	            remainDataLen = fileInputStream.read(buf);
            
	            do{  
	            	
		    		//read packet head 
		            Arrays.fill(FILEHEAD_SMSS_PACKET_HEAD, (byte)0);
		            System.arraycopy(buf, pos, FILEHEAD_SMSS_PACKET_HEAD, 0, FILEHEAD_SMSS_PACKET_HEAD_LENGTH);
		            pos = pos + FILEHEAD_SMSS_PACKET_HEAD_LENGTH;
		            packetLen=bigbytes2int(FILEHEAD_SMSS_PACKET_HEAD,8);  //4+1*4+4: code + linkId,portType,reserved,repeatNum + msgLength
            
		            //read a packet
            		packet = new byte[packetLen - 3];  //MTP3
            		pos = pos + 3;
            		System.arraycopy(buf, pos, packet, 0, packet.length);
            		pos = pos + packet.length;
            		
            		bw.write(bytes2str(packet,true));
            		bw.write("\r\n");
            		count ++;
            		packetStartPos = pos;
            		            		
            	}  while(pos < remainDataLen - FILEHEAD_SMSS_PACKET_HEAD_LENGTH );
            	
            }  //end while (fileInputStream.available()>0)
            	
            // 关闭流  
            fileInputStream.close();  
            bw.close();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		System.out.println(count+" smssDat2msu - established MS="+(System.currentTimeMillis()-old)+"; FINISHED AT "+new Date()); 
		
		return  count;
	}
	

	/**
	 * SMSS数据文件直接转为pcap文件.
	 * 
	 * @param smssDatFile - SMSS数据文件
	 * @param pcapFile - pcap文件
	 * @return 转换的包数
	 */
	public int smssDat2pcapDirect(final String smssDatFile, final String pcapFile) {
		int count = 0;
		
		long old = System.currentTimeMillis();
		System.out.println(count+" smssDat2msu - established MS="+(System.currentTimeMillis()-old)+"; START AT "+new Date());
		try {  
            // 源文件  
            FileInputStream fileInputStream = new FileInputStream(new File(smssDatFile));  
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pcapFile));  
            fileOutputStream.write(FILEHEAD_PCAP_FLAG);
  
            //read head 
            Arrays.fill(FILEHEAD_SMSS, (byte)0);
            fileInputStream.read(FILEHEAD_SMSS);
            
            while (fileInputStream.available()>0){
	            //read MSG head 
	            int msgPos = 0;
	            Arrays.fill(FILEHEAD_SMSS_MSG, (byte)0);
	            fileInputStream.read(FILEHEAD_SMSS_MSG);
	            Arrays.fill(FILEHEAD_SMSS_MSG_FLAG, (byte)0);
	            System.arraycopy(FILEHEAD_SMSS_MSG, 0, FILEHEAD_SMSS_MSG_FLAG, 0, FILEHEAD_SMSS_MSG_FLAG.length);
	            if (Arrays.equals(FILEHEAD_SMSS_MSG_FLAG, FILEHEAD_SMSS_MSG_FLAG0) 
	            		|| Arrays.equals(FILEHEAD_SMSS_MSG_FLAG, FILEHEAD_SMSS_MSG_FLAG1)){
	            }else{
	            	fileInputStream.close(); 
	            	System.out.println(smssDatFile+" isn't SMSS format msg.(ERR:-100)");  
	            	return -100;
	            }
	            int msglen = bigbytes2int(FILEHEAD_SMSS_MSG,12);
	            int ms = bytes2int(FILEHEAD_SMSS_MSG,16);
            
	            int pos = 0;  
	            byte[] buf = new byte[msglen];
	            int remainDataLen = 0;
	            byte[] packet = null;
	            int packetLen = 0;
	            int packetStartPos = pos;
            
	            //read data (至少有一个包,应该是多个完整的MSU)
	            remainDataLen = fileInputStream.read(buf);
            
	            do{  
	            	
		    		//read packet head 
		            Arrays.fill(FILEHEAD_SMSS_PACKET_HEAD, (byte)0);
		            System.arraycopy(buf, pos, FILEHEAD_SMSS_PACKET_HEAD, 0, FILEHEAD_SMSS_PACKET_HEAD_LENGTH);
		            pos = pos + FILEHEAD_SMSS_PACKET_HEAD_LENGTH;
		            packetLen=bigbytes2int(FILEHEAD_SMSS_PACKET_HEAD,8);  //4+1*4+4: code + linkId,portType,reserved,repeatNum + msgLength
            
		            //read a packet
            		packet = new byte[packetLen - 3];  //MTP3
            		pos = pos + 3;
            		System.arraycopy(buf, pos, packet, 0, packet.length);
            		pos = pos + packet.length;
            		
            		byte[] pcapPacket = mtp3msu2pcap(packet, ms);
                	fileOutputStream.write(pcapPacket);
            		count ++;
            		packetStartPos = pos;
            		            		
            	}  while(pos < remainDataLen - FILEHEAD_SMSS_PACKET_HEAD_LENGTH );
            	
            }  //end while (fileInputStream.available()>0)
            	
            // 关闭流  
            fileInputStream.close();  
            fileOutputStream.close();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		System.out.println(count+" smssDat2msu - established MS="+(System.currentTimeMillis()-old)+"; FINISHED AT "+new Date()); 
		
		return  count;
	}
	
	
	
	/**
	 * hex的MSU数据文件(每行一个MTP3MSU<0x83开始>)转为pcap文件.
	 * 
	 * @param msuFile - hex的MSU文件(每行一个MTP3MSU<0x83开始>)
	 * @param pcapFile - pcap文件
	 * @return 转换的包数
	 */
	public int msu2pcap(final String msuFile, final String pcapFile) {
		int count = 0;
		
		long old = System.currentTimeMillis();
		System.out.println(count+" msu2pcap - established MS="+(System.currentTimeMillis()-old)+"; START AT "+new Date());
		try {  
            // 源文件  
			BufferedReader br = new BufferedReader(new FileReader(new File(msuFile)));
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pcapFile));  
            fileOutputStream.write(FILEHEAD_PCAP_FLAG);
			
		    String line = br.readLine();
		    while (null!=line){
		    	if (line.trim().length()<1 || line.startsWith("--")|| line.startsWith("#")) {  //--，#作为注释行
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	byte[] packet = str2bytes(line);
		    	byte[] pcapPacket = mtp3msu2pcap(packet, count);
            	fileOutputStream.write(pcapPacket);
            	count ++;
		    	
		    	line = br.readLine();
		    }
		    br.close(); 
            	
            // 关闭流  
            fileOutputStream.close();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		System.out.println(count+" msu2pcap - established MS="+(System.currentTimeMillis()-old)+"; FINISHED AT "+new Date()); 
		
		return  count;
	}
	
	//hexStr="01 23 45 67 89 ab cd ef"
	private byte[] str2bytes(String hexStr){
		
		String hex = hexStr.replaceAll(" ", "");
		byte[] b = new byte[hex.length()/2];
		for (int i=0; i<b.length; i++){
			b[i]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
		}
		
		return b;
	}
	
	//{0,a,a0,f,f0,ff} -> "00 0a a0 0f f0 ff"
	private String bytes2str(byte[] bytes, boolean space){
		
		StringBuilder toHex = new StringBuilder();

	    for (int i = 0; (null != bytes) && (i < bytes.length); i++) {
	      if (space) {
	          toHex.append(' ');
	      }

	      char hi = Character.forDigit((bytes[i] >> 4) & 0x0F, 16);
	      char lo = Character.forDigit(bytes[i] & 0x0F, 16);
	      toHex.append(Character.toUpperCase(hi));
	      toHex.append(Character.toUpperCase(lo));

	    }

	    return toHex.toString();
	}
	
	/**
	 * 中创仪表数据文件转为pcap文件.
	 * 
	 * @param zcxcDatFile - 中创仪表数据文件
	 * @param pcapFile - pcap文件
	 * @return 转换的包数
	 */
	public int zcxcDat2pcap(final String zcxcDatFile, final String pcapFile) {
		int count = smssDat2msu(zcxcDatFile,zcxcDatFile+".msu");
		if (count>0){
			count = msu2pcap(zcxcDatFile+".msu", pcapFile);
		}
		return count;
	}
	
	/**
	 * 中创仪表数据文件转为msu文件.
	 * 
	 * @param zcxcDatFile - 中创仪表数据文件
	 * @param msuFile - pcap文件
	 * @return 转换的包数
	 */
	public int zcxcDat2msu(final String zcxcDatFile, final String msuFile) {
		int count = 0;
		
		long old = System.currentTimeMillis();
		System.out.println(count+" zcxcDat2msu - established MS="+(System.currentTimeMillis()-old)+"; START AT "+new Date());
		try {  
            // 源文件  
            FileInputStream fileInputStream = new FileInputStream(new File(zcxcDatFile));  
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(msuFile)));
  
            //read head and CHECK HEAD FLAG
            Arrays.fill(FILEHEAD_ZCXC, (byte)0);
            fileInputStream.read(FILEHEAD_ZCXC);
            byte[] _FILEHEAD_ZCXC_FLAG = new byte[FILEHEAD_ZCXC_FLAG.length];
            Arrays.fill(_FILEHEAD_ZCXC_FLAG, (byte)0);
            System.arraycopy(FILEHEAD_ZCXC, 0, _FILEHEAD_ZCXC_FLAG, 0, FILEHEAD_ZCXC_FLAG.length);
            if (!Arrays.equals(_FILEHEAD_ZCXC_FLAG, FILEHEAD_ZCXC_FLAG)){
            	fileInputStream.close(); 
            	System.out.println(zcxcDatFile+" isn't ZCXC format file.(ERR:-100)");  
            	return -100;
            }
            
            
            int pos = 0;  
            byte[] buf = new byte[BUFFER_SIZE];
            int remainDataLen = 0;
            byte[] packet = null;
            int packetStartPos = pos;
            
            //read data (至少有一个包)
            remainDataLen = pos + fileInputStream.read(buf,pos,Math.min(fileInputStream.available(),buf.length-pos));
            pos = pos + FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH;//skip UNKNOWN
//            packetStartPos = pos;
            
    		//4b (timestamp), byte[] to int
            int ms = bytes2int(buf,packetStartPos+FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH);
            pos = pos + 4;
    		
            //packet head flag
            Arrays.fill(FILEHEAD_ZCXC_PACKET_HEAD, (byte)0);
    		System.arraycopy(buf, pos, FILEHEAD_ZCXC_PACKET_HEAD, 0, FILEHEAD_ZCXC_PACKET_HEAD_LENGTH);
        	pos = pos + FILEHEAD_ZCXC_PACKET_HEAD_LENGTH;
        	FILEHEAD_ZCXC_PACKET_HEAD[0]=0;
        	FILEHEAD_ZCXC_PACKET_HEAD[7]=0;
            if (!Arrays.equals(FILEHEAD_ZCXC_PACKET_HEAD, FILEHEAD_ZCXC_PACKET_HEAD_FLAG)){
            	fileInputStream.close(); 
            	System.out.println(zcxcDatFile+" NOT FOUND PACKET HEAD FLAG[x,0,0,0,0,0,0,x].(ERR:-101)");  
            	return -101;
            }
            
            while (true) {  
            	
            	while(pos < remainDataLen ){
            		
            		if (buf[pos]==MTP3FLAG && pos > packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen ){
            			//found next packet's pos
            			Arrays.fill(FILEHEAD_ZCXC_PACKET_HEAD, (byte)0);
            			System.arraycopy(buf, pos-6-8, FILEHEAD_ZCXC_PACKET_HEAD, 0, FILEHEAD_ZCXC_PACKET_HEAD_FLAG.length);
            			FILEHEAD_ZCXC_PACKET_HEAD[0]=0;
            			FILEHEAD_ZCXC_PACKET_HEAD[7]=0;
                        if (Arrays.equals(FILEHEAD_ZCXC_PACKET_HEAD, FILEHEAD_ZCXC_PACKET_HEAD_FLAG)){
                        	if ((pos - FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen) < (packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen) ){  //异常数据包
                        		System.out.println("=====Negative: pos="+pos+"; packetStartPos="+packetStartPos);
                        		byte[] errBytes = new byte[pos - packetStartPos];
                        		System.arraycopy(buf, packetStartPos, errBytes, 0, errBytes.length);
                        		System.out.println(ISOUtil.hexdump(errBytes));
                        		
                        		pos ++;
                        		continue;
                        	}
                        	pos = pos - FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen; //前移到下个包时间的位置,要减一个包头的首字节
                        	
                        	ms = bytes2int(buf,packetStartPos+FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH);
                        	
                        	//get a packet and write pcapfile
                        	packetStartPos = packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen;  
                        	packet = new byte[pos - packetStartPos];
                        	System.arraycopy(buf, packetStartPos, packet, 0, packet.length);
                        	if (packet[0]==MTP3FLAG){
                        		bw.write(bytes2str(packet,true));
                        		bw.write("\r\n");
                            	count ++;
                        	}else{
                        		System.out.println(" NOT MTP3 PACKET.(ERR:-102)");
                        		System.out.println(ISOUtil.hexdump(packet));
                        		
                        		fileInputStream.close();  
                                bw.close(); 
                            	return -102;
                        	}
                        	
                        	packetStartPos = pos;
                        	pos = pos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen;
                        }
            		}
            		
            		//next byte
                    pos ++;
            	}  //end while(pos < remainDataLen )
            	
            	if (fileInputStream.available()>0){
            		System.out.println(count+" zcxcDat2msu - established MS="+(System.currentTimeMillis()-old)+"; converting AT "+new Date()); 
            		
            		//不是一个完整包
                	packet = new byte[remainDataLen - packetStartPos];
                	System.arraycopy(buf, packetStartPos, packet, 0, packet.length);
                	System.arraycopy(packet, 0, buf, 0, packet.length);
                	packetStartPos = 0;
                	pos = packet.length - (remainDataLen - pos);
                	
	            	//read data (至少有一个包)
                	Arrays.fill(buf,packet.length,buf.length, (byte)0);
	                remainDataLen = packet.length + fileInputStream.read(buf,packet.length,Math.min(fileInputStream.available(),buf.length-packet.length));
            	}else{
            		ms = bytes2int(buf,packetStartPos+10);
                	//最后一个完整包
            		//get a packet and write pcapfile
                	packetStartPos = packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen;  
                	packet = new byte[remainDataLen - packetStartPos];
                	System.arraycopy(buf, packetStartPos, packet, 0, packet.length);
                	
                	bw.write(bytes2str(packet,true));
            		bw.write("\r\n");
                	count ++;
                	
                	break;
            	}
                
            }  //end while (true)
            	
            // 关闭流  
            fileInputStream.close();  
            bw.close();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
		System.out.println(count+" zcxcDat2msu - established MS="+(System.currentTimeMillis()-old)+"; FINISHED AT "+new Date()); 
		
		return  count;
	}
	
	/**
	 * 中创仪表数据文件直接转为pcap文件.
	 * 
	 * @param zcxcDatFile - 中创仪表数据文件
	 * @param pcapFile - pcap文件
	 * @return 转换的包数
	 */
	public int zcxcDat2pcapDirect(final String zcxcDatFile, final String pcapFile) {
		int count = 0;
		
		long old = System.currentTimeMillis();
		System.out.println(count+" established MS="+(System.currentTimeMillis()-old)+"; START AT "+new Date());
		try {  
            // 源文件  
            FileInputStream fileInputStream = new FileInputStream(new File(zcxcDatFile));  
  
            //read head and CHECK HEAD FLAG
            Arrays.fill(FILEHEAD_ZCXC, (byte)0);
            fileInputStream.read(FILEHEAD_ZCXC);
            byte[] _FILEHEAD_ZCXC_FLAG = new byte[FILEHEAD_ZCXC_FLAG.length];
            Arrays.fill(_FILEHEAD_ZCXC_FLAG, (byte)0);
            System.arraycopy(FILEHEAD_ZCXC, 0, _FILEHEAD_ZCXC_FLAG, 0, FILEHEAD_ZCXC_FLAG.length);
            if (!Arrays.equals(_FILEHEAD_ZCXC_FLAG, FILEHEAD_ZCXC_FLAG)){
            	fileInputStream.close(); 
            	System.out.println(zcxcDatFile+" isn't ZCXC format file.(ERR:-100)");  
            	return -100;
            }
            
            
            int pos = 0;  
            byte[] buf = new byte[BUFFER_SIZE];
            int remainDataLen = 0;
            byte[] packet = null;
            int packetStartPos = pos;
            
            //read data (至少有一个包)
            remainDataLen = pos + fileInputStream.read(buf,pos,Math.min(fileInputStream.available(),buf.length-pos));
            pos = pos + FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH;//skip UNKNOWN
//            packetStartPos = pos;
            
    		//4b (timestamp), byte[] to int
            int ms = bytes2int(buf,packetStartPos+FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH);
            pos = pos + 4;
    		
            //packet head flag
            Arrays.fill(FILEHEAD_ZCXC_PACKET_HEAD, (byte)0);
    		System.arraycopy(buf, pos, FILEHEAD_ZCXC_PACKET_HEAD, 0, FILEHEAD_ZCXC_PACKET_HEAD_LENGTH);
        	pos = pos + FILEHEAD_ZCXC_PACKET_HEAD_LENGTH;
        	FILEHEAD_ZCXC_PACKET_HEAD[0]=0;
        	FILEHEAD_ZCXC_PACKET_HEAD[7]=0;
            if (!Arrays.equals(FILEHEAD_ZCXC_PACKET_HEAD, FILEHEAD_ZCXC_PACKET_HEAD_FLAG)){
            	fileInputStream.close(); 
            	System.out.println(zcxcDatFile+" NOT FOUND PACKET HEAD FLAG[x,0,0,0,0,0,0,x].(ERR:-101)");  
            	return -101;
            }
            
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pcapFile));  
            fileOutputStream.write(FILEHEAD_PCAP_FLAG);
            
           
            while (true) {  
            	
            	while(pos < remainDataLen ){
            		
            		if (buf[pos]==MTP3FLAG && pos > packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen ){
            			//found next packet's pos
            			Arrays.fill(FILEHEAD_ZCXC_PACKET_HEAD, (byte)0);
            			System.arraycopy(buf, pos-6-8, FILEHEAD_ZCXC_PACKET_HEAD, 0, FILEHEAD_ZCXC_PACKET_HEAD_FLAG.length);
            			FILEHEAD_ZCXC_PACKET_HEAD[0]=0;
            			FILEHEAD_ZCXC_PACKET_HEAD[7]=0;
                        if (Arrays.equals(FILEHEAD_ZCXC_PACKET_HEAD, FILEHEAD_ZCXC_PACKET_HEAD_FLAG)){
                        	if ((pos - FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen) < (packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen) ){  //异常数据包
                        		System.out.println("=====Negative: pos="+pos+"; packetStartPos="+packetStartPos);
                        		byte[] errBytes = new byte[pos - packetStartPos];
                        		System.arraycopy(buf, packetStartPos, errBytes, 0, errBytes.length);
                        		System.out.println(ISOUtil.hexdump(errBytes));
                        		
                        		pos ++;
                        		continue;
                        	}
                        	pos = pos - FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen; //packet 开头  //前移到下个包时间的位置,要减一个包头的首字节
                        	
                        	ms = bytes2int(buf,packetStartPos+FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH);
                        	
                        	//get a packet and write pcapfile
                        	packetStartPos = packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen;  
                        	packet = new byte[pos - packetStartPos];
                        	System.arraycopy(buf, packetStartPos, packet, 0, packet.length);
                        	if (packet[0]==MTP3FLAG){
                        		byte[] pcapPacket = mtp3msu2pcap(packet, ms);
                            	fileOutputStream.write(pcapPacket);
                            	count ++;
                        	}else{
                        		System.out.println(" NOT MTP3 PACKET.(ERR:-102)");
                        		System.out.println(ISOUtil.hexdump(packet));
                        		
                        		fileInputStream.close();  
                                fileOutputStream.close(); 
                            	return -102;
                        	}
                        	
                        	packetStartPos = pos;
                        	pos = pos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen;
                        }
            		}
            		
            		//next byte
                    pos ++;
            	}  //end while(pos < remainDataLen )
            	
            	if (fileInputStream.available()>0){
            		System.out.println(count+" established MS="+(System.currentTimeMillis()-old)+"; converting AT "+new Date()); 
            		
            		//不是一个完整包
                	packet = new byte[remainDataLen - packetStartPos];
                	System.arraycopy(buf, packetStartPos, packet, 0, packet.length);
                	System.arraycopy(packet, 0, buf, 0, packet.length);
                	packetStartPos = 0;
                	pos = packet.length - (remainDataLen - pos);
                	
	            	//read data (至少有一个包)
                	Arrays.fill(buf,packet.length,buf.length, (byte)0);
	                remainDataLen = packet.length + fileInputStream.read(buf,packet.length,Math.min(fileInputStream.available(),buf.length-packet.length));
            	}else{
            		ms = bytes2int(buf,packetStartPos+FILEHEAD_ZCXC_PACKET_HEAD_UNKNOWN_LENGTH);
                	//最后一个完整包
            		//get a packet and write pcapfile
                	packetStartPos = packetStartPos + FILEHEAD_ZCXC_PACKET_HEAD_skip2MTP3DiffLen;  
                	packet = new byte[remainDataLen - packetStartPos];
                	System.arraycopy(buf, packetStartPos, packet, 0, packet.length);
                	
                	byte[] pcapPacket = mtp3msu2pcap(packet, ms);
                	fileOutputStream.write(pcapPacket);
                	count ++;
                	
                	break;
            	}
                
            }  //end while (true)
            	
            // 关闭流  
            fileInputStream.close();  
            fileOutputStream.close();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		System.out.println(count+" established MS="+(System.currentTimeMillis()-old)+"; FINISHED AT "+new Date()); 
		
		return  count;
	}
	

	
	/**
	 * 字节数组转为int.
	 * 
	 * @param bytes - 字节数组(length>=4)
	 * @param pos - 开始位置
	 * @return 数字
	 */
	private int bytes2int(byte[] bytes, int pos){
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(bytes,pos,4);
		bb.flip();
		
		return  bb.getInt();
	}
	
	/**
	 * 字节数组转为int.
	 * 
	 * @param bytes - 字节数组(length>=4)
	 * @param pos - 开始位置
	 * @return 数字
	 */
	private int bigbytes2int(byte[] bytes, int pos){
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(bytes[pos+3]);
		bb.put(bytes[pos+2]);
		bb.put(bytes[pos+1]);
		bb.put(bytes[pos+0]);
		bb.flip();
		
		return  bb.getInt();
	}
	
	/**
	 * 将MTP3(83开始的码流)转换为pcap码流.
	 * 
	 * @param mtp3msu - MTP3的码流(0x83开头)
	 * @param ms - 时间戳
	 * @return - pcap码流
	 */
	public byte[] mtp3msu2pcap(final byte[] mtp3msu, int ms ) {
		ByteBuffer bb = ByteBuffer.allocate(mtp3msu.length+8+4+4);
		
		bb.putLong(uint2long(ms));  //4b+4B: timestamp
		
		bb.put((byte)(mtp3msu.length));
		bb.put((byte)(mtp3msu.length>>8));
		bb.put((byte)(mtp3msu.length>>16));
		bb.put((byte)(mtp3msu.length>>24));  //caplen
		bb.put((byte)(mtp3msu.length));
		bb.put((byte)(mtp3msu.length>>8));
		bb.put((byte)(mtp3msu.length>>16));
		bb.put((byte)(mtp3msu.length>>24));  //len
		bb.put(mtp3msu);
		
		bb.flip();
		byte[] pcapbytes = new byte[bb.limit()];
		bb.get(pcapbytes);
		
		return pcapbytes;
	}
	
	private long uint2long(int ms){
		//uint2long
		ByteBuffer bbLong = ByteBuffer.allocate(8);
		
		int sec = (ms/1000);
		int mms = ms%1000;
		bbLong.put((byte)(sec));
		bbLong.put((byte)(sec>>8));
		bbLong.put((byte)(sec>>16));
		bbLong.put((byte)(sec>>24)); 
		bbLong.put((byte)(mms));
		bbLong.put((byte)(mms>>8));
		bbLong.put((byte)(mms>>16));
		bbLong.put((byte)(mms>>24)); 

		bbLong.flip();
		
		return bbLong.getLong();
	}

}
