/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-9-13
* + //去space并换DPC,OPC(仅8X的码流)
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.lj.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaoran27.tools.FileConvert;
import com.xiaoran27.tools.HexFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Task {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	@Setter @Getter private int taskId = 0; //	16-bit 无符号整数	范围：1-0x0fff
	@Setter @Getter private int linkId = 0; //	8-bit 无符号整数	范围：0-0xf
	@Setter @Getter private String startTime = null; //	16个字节的ASCIIZ字符串，格式为yyyymmdd_hhmmss	任务启动时间>=当前时间,一般在整分钟。
	@Setter @Getter private int sendMode = 1; //	8-bit 无符号整数	1: 循环发送, 2：按总数发送, 3: 按时长发送
	@Setter @Getter private int sendModeValue = 0; //	32-bit 无符号整数	SendMode=[2,3]: 个数或秒数
	@Setter @Getter private int sendRate = 100; //	16-bit 无符号整数	每秒多少个
	@Setter @Getter private String filename = null; //	<=255个字节的ASCIIZ字符串	必须是dat,msu,pcap后缀的绝对路径文件名
	@Setter @Getter private String finishedTime = null; //	16个字节的ASCIIZ字符串，格式为yyyymmdd_hhmmss	可为null
	@Setter @Getter private int status = 1; //	8-bit 无符号整数	0-完成，1-等待，2-运行，3-取消
	
	@Setter @Getter private String dpc = null; //DPC,OPC format: 0X000003
	@Setter @Getter private String opc = null; 
	
	@Setter @Getter private int protocol = 0; 
	
	@Setter @Getter private  List<byte[]> msuList = new ArrayList<byte[]>(); 
	
	//存放所有文件对应的msu码流
	public static Map<String,List<byte[]>> fileMsuMap = new HashMap<String,List<byte[]>>();
	
	/*protocol note
	 * MSUD_CSP(0): 
		format: type<1B>+length<4B>+msuLen<4B>+msuData<83...>
		type+length+msuLen: 1B+4B+4B
	*/
	final static public int PTOTOCOL_MSUD_CSP = 0; 
	
	/*SEND MODE
	 * 1: 循环发送, 2：按总数发送, 3: 按时长发送
	 * */
	final static public int SEND_MODE_LOOP = 1;
	final static public int SEND_MODE_TOTAL = 2;
	final static public int SEND_MODE_TIME = 3;
	
	
	/**时间格式为yyyyMMdd_HHmmss的串转为时间毫秒数.
	 * 
	 * @param yyyyMMdd_HHmmss  format is "yyyyMMdd_HHmmss"
	 * @return  Ms
	 * @throws ParseException
	 */
	public long getMsFrom4y2M2d_2H2m2s(String yyyyMMdd_HHmmss) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = sdf.parse(yyyyMMdd_HHmmss);
		
		return date.getTime();
	}
	
	public int genMsu() throws Exception {
		
		if (fileMsuMap.get(filename)!=null)  return msuList.size(); //该文件已经处理
		
		int count =0;
		
		File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //支持读取目录
        	datafiles = file.list();
        	Arrays.sort(datafiles);
        	filepath = file.getPath();
        }
        
        for (String _file : datafiles){
        	String f = filepath+File.separator+_file;
        	File datafile = new File(f);
        	if (datafile.isDirectory()){
        		System.out.println(f+" is Directory, ignore it." + new Date());
        		continue;
            }
        	
        	int pos = f.indexOf(".", f.length()-5);
        	String ext = f.substring(pos+1);
        	if ("msu".equalsIgnoreCase(ext) || "txt".equalsIgnoreCase(ext)){
        		count = count + getMsuFromMsuOrTxt(f);
        	}else if ("pcap".equalsIgnoreCase(ext)){
        		count = count + getMsuFromPcap(f);
        	}else{
        		System.out.println(f+" is unsupport, ignore it." + new Date());
        		continue;
        	}
        	
        }
        
        fileMsuMap.put(filename, msuList);
		
		return count;
	}
	
	
	//读取msu或txt文件的msu
	public int getMsuFromMsuOrTxt(String filename) throws Exception {
		
		int count = 0;
		
        File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //支持读取目录
        	datafiles = file.list();
        	Arrays.sort(datafiles);
        	filepath = file.getPath();
        }
        
        for (String f : datafiles){
        	if (new File(f).isDirectory()){
        		System.out.println(f+" is Directory, ignore it." + new Date());
        		continue;
            }
        	
        	int _count = 0;
        	System.out.println(_count+"; file="+f+" start at " + new Date());
        	
	        BufferedReader br = new BufferedReader(new FileReader(new File(filepath+File.separator+f)));
	        String line = br.readLine();
	        
		    while (null!=line){
		    	if (line.trim().length()<1 || line.startsWith("--")|| line.startsWith("#")) {  //--，#作为注释行
		    		//"-- send msu speed is NUM"
		    		if ( line.indexOf("speed")>0){
		    			String[] _values = line.split("[ ,;=]");
		    			sendRate = Integer.parseInt(_values[_values.length-1]);
		    		}
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	//去space并换DPC,OPC(仅8X的码流); 可能会有误判,暂忽略
		    	line = line.replaceAll(" ", "");
		    	int _8pos = -1;
		    	if (line.charAt(0)=='8'){ //8X
		    		_8pos = 0;
		    	}else if (line.charAt(6)=='8'){//0123458X
		    		_8pos = 6;
		    	}else if (line.charAt(12)=='8'){//0123456789018X
		    		_8pos = 12;
		    	}
		    	if (_8pos >= 0){
		    		line = line.substring(0,_8pos+2)+dpc.substring(6,8)+dpc.substring(4,6)+dpc.substring(2,4)+opc.substring(6,8)+opc.substring(4,6)+opc.substring(2,4)+line.substring(_8pos+14);
		    	}
		    	
		    	byte[] msu = HexFormat.str2bytes(line);
		    	//MSU超长丢弃
		    	if (_8pos >= 0 && msu.length > FileConvert.MSU_MAX_LENGTH){
		    		System.out.println(_count+"; file="+f+" start at " + new Date());
		    		System.out.println("IGNORE MSU because too long ="+msu.length);
		    		System.out.println("MSU start with "+HexFormat.bytes2str(msu, 0, 10, true));
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	//format: type<1B>+length<4B>+msuLen<4B>+msuData<83...>
	    		//type+length+msuLen: 1B+4B+4B
	    		ByteBuffer bb = ByteBuffer.allocate(9+msu.length);
	    		if (protocol == PTOTOCOL_MSUD_CSP){
	    			bb.put((byte)0x06);  //06-msu
		    		bb.putInt(msu.length+4);
		    		bb.putInt(msu.length);
		    	}
	    		bb.put(msu);
	    		bb.flip();
	    		byte[] _msu = bb.array();
	    		msuList.add(_msu);
	    		_count ++;
		    	
		        if (_count%1000==0){
		        	System.out.println(_count+"; file="+f+" is reading at " + new Date());
		        }
		    	
		        line = br.readLine();
		    } //end  while (null!=line)
		    br.close();
		    count = count + _count;
		    
		    System.out.println(count+"; file="+f+" is finished at " + new Date());
        }
        
        System.out.println("Total: "+count+"at " + new Date());
        System.out.println("files: "+Arrays.toString(datafiles));
		
		return count;
	}
	

	//读取pcap文件的msu
	public int getMsuFromPcap(String filename) throws Exception {
		
		int count = 0;
		
        File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //支持读取目录
        	datafiles = file.list();
        	Arrays.sort(datafiles);
        	filepath = file.getPath();
        }
        
        for (String f : datafiles){
        	if (new File(f).isDirectory()){
        		System.out.println(f+" is Directory, ignore it." + new Date());
        		continue;
            }
        	
        	int _count = 0;
        	System.out.println(_count+"; file="+f+" start at " + new Date());
        	
        	FileInputStream fileInputStream = new FileInputStream(new File(filepath+File.separator+f));  
        	byte[] pcapHead = new byte[FileConvert.FILEHEAD_PCAP_LENGTH];
        	fileInputStream.read(pcapHead);
        	
        	byte[] packetHead = new byte[FileConvert.FILEHEAD_PCAP_PACKET_HEAD_LENGTH];
		    while (fileInputStream.available()>0){
		    	
		    	//read packet head
		    	fileInputStream.read(packetHead);
		    	
		    	//calc msu len
		    	int msuLen = HexFormat.bigbytes2int(packetHead, 12);
		    	
		    	//read packet 
		    	byte[] msu = new byte[msuLen];
		    	fileInputStream.read(msu);
		    	
		    	//去space并换DPC,OPC(仅8X的码流); 可能会有误判,暂忽略
		    	int _8pos = -1;
		    	if ( (msu[0]&0xf0) == 0x80){ //8X
		    		_8pos = 0;
		    	}else if ((msu[3]&0xf0) == 0x80){//0123458X
		    		_8pos = 3;
		    	}else if ((msu[6]&0xf0) == 0x80){//0123456789018X
		    		_8pos = 6;
		    	}
		    	if (_8pos >= 0){
		    		byte[] dpcopc = HexFormat.str2bytes(dpc.substring(6,8)+dpc.substring(4,6)+dpc.substring(2,4)+opc.substring(6,8)+opc.substring(4,6)+opc.substring(2,4));
		    		for(int i=0; i<dpcopc.length; i++){
		    			msu[i+_8pos+1] = dpcopc[i];
		    		}
		    	}
		    	
		    	//MSU超长丢弃
		    	if ( _8pos >= 0 && msuLen > FileConvert.MSU_MAX_LENGTH){
		    		System.out.println(_count+"; file="+f+" start at " + new Date());
		    		System.out.println("IGNORE MSU because too long ="+msuLen);
		    		System.out.println("MSU start with "+HexFormat.bytes2str(msu, 0, 10, true));
		    		continue;
		    	}
		    	
		    	ByteBuffer bb = ByteBuffer.allocate(9+msu.length);
	    		if (protocol == PTOTOCOL_MSUD_CSP){
	    			bb.put((byte)0x06);  //06-msu
		    		bb.putInt(msu.length+4);
		    		bb.putInt(msu.length);
		    	}
	    		bb.put(msu);
	    		bb.flip();
	    		byte[] _msu = bb.array();
	    		msuList.add(_msu);
		        _count ++;
		        
		        if (_count%1000==0){
		        	System.out.println(_count+"; file="+f+" is reading at " + new Date());
		        }
		    	
		    } //end  while (null!=line)
		    fileInputStream.close();
		    count = count + _count;
		    
		    System.out.println(_count+"; file="+f+" is finished at " + new Date());
        }
        
        System.out.println("count=" + count + "; files: "+Arrays.toString(datafiles));
		
		return count;
	}
		
	
}
