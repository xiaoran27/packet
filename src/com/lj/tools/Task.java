/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-9-13
* + //ȥspace����DPC,OPC(��8X������)
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
	
	@Setter @Getter private int taskId = 0; //	16-bit �޷�������	��Χ��1-0x0fff
	@Setter @Getter private int linkId = 0; //	8-bit �޷�������	��Χ��0-0xf
	@Setter @Getter private String startTime = null; //	16���ֽڵ�ASCIIZ�ַ�������ʽΪyyyymmdd_hhmmss	��������ʱ��>=��ǰʱ��,һ���������ӡ�
	@Setter @Getter private int sendMode = 1; //	8-bit �޷�������	1: ѭ������, 2������������, 3: ��ʱ������
	@Setter @Getter private int sendModeValue = 0; //	32-bit �޷�������	SendMode=[2,3]: ����������
	@Setter @Getter private int sendRate = 100; //	16-bit �޷�������	ÿ����ٸ�
	@Setter @Getter private String filename = null; //	<=255���ֽڵ�ASCIIZ�ַ���	������dat,msu,pcap��׺�ľ���·���ļ���
	@Setter @Getter private String finishedTime = null; //	16���ֽڵ�ASCIIZ�ַ�������ʽΪyyyymmdd_hhmmss	��Ϊnull
	@Setter @Getter private int status = 1; //	8-bit �޷�������	0-��ɣ�1-�ȴ���2-���У�3-ȡ��
	
	@Setter @Getter private String dpc = null; //DPC,OPC format: 0X000003
	@Setter @Getter private String opc = null; 
	
	@Setter @Getter private int protocol = 0; 
	
	@Setter @Getter private  List<byte[]> msuList = new ArrayList<byte[]>(); 
	
	//��������ļ���Ӧ��msu����
	public static Map<String,List<byte[]>> fileMsuMap = new HashMap<String,List<byte[]>>();
	
	/*protocol note
	 * MSUD_CSP(0): 
		format: type<1B>+length<4B>+msuLen<4B>+msuData<83...>
		type+length+msuLen: 1B+4B+4B
	*/
	final static public int PTOTOCOL_MSUD_CSP = 0; 
	
	/*SEND MODE
	 * 1: ѭ������, 2������������, 3: ��ʱ������
	 * */
	final static public int SEND_MODE_LOOP = 1;
	final static public int SEND_MODE_TOTAL = 2;
	final static public int SEND_MODE_TIME = 3;
	
	
	/**ʱ���ʽΪyyyyMMdd_HHmmss�Ĵ�תΪʱ�������.
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
		
		if (fileMsuMap.get(filename)!=null)  return msuList.size(); //���ļ��Ѿ�����
		
		int count =0;
		
		File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //֧�ֶ�ȡĿ¼
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
	
	
	//��ȡmsu��txt�ļ���msu
	public int getMsuFromMsuOrTxt(String filename) throws Exception {
		
		int count = 0;
		
        File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //֧�ֶ�ȡĿ¼
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
		    	if (line.trim().length()<1 || line.startsWith("--")|| line.startsWith("#")) {  //--��#��Ϊע����
		    		//"-- send msu speed is NUM"
		    		if ( line.indexOf("speed")>0){
		    			String[] _values = line.split("[ ,;=]");
		    			sendRate = Integer.parseInt(_values[_values.length-1]);
		    		}
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	//ȥspace����DPC,OPC(��8X������); ���ܻ�������,�ݺ���
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
		    	//MSU��������
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
	

	//��ȡpcap�ļ���msu
	public int getMsuFromPcap(String filename) throws Exception {
		
		int count = 0;
		
        File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //֧�ֶ�ȡĿ¼
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
		    	
		    	//ȥspace����DPC,OPC(��8X������); ���ܻ�������,�ݺ���
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
		    	
		    	//MSU��������
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
