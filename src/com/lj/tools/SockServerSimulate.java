/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-30
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-1
*  + //支持读取目录
*  + //read RECV, but ignore them //deplay ms
*  + //MSU超长丢弃
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-15
*  + //可能有81,85等msu
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-19
*  + //生成msu后启动独立线程发送
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-20
*  +//仅需延时或每发送100个msg后读一次
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-22
*  +//每秒发送条数完成且耗时不到一秒需sleep
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-9-9
*  M //key is IP
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.lj.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.xiaoran27.tools.FileConvert;
import com.xiaoran27.tools.HexFormat;

@ToString
public class SockServerSimulate {
	
	@Setter @Getter private String filename = "momt.msu";
	@Setter @Getter private int port = 10308;
	
	@Setter @Getter private String dpc = null;
	@Setter @Getter private String opc = null;
	
	@Setter @Getter private boolean isMsudCsp = true;  //是否是msudcsp的接口
	
	@Setter @Getter private boolean msuSpeedByFile = true;  //是否使用
	
	@Setter @Getter private int rate = 100;
	@Setter @Getter private int inteval = 1000/rate - 1;
	@Setter @Getter private int time = 0;  //秒: <0 - total ; 0-loop
	
	private Map<String,List<byte[]>> threadMsuMap =  new HashMap<String,List<byte[]>>();  //存放每个task及msu数据

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			SockServerSimulate sockServerSimulate = new SockServerSimulate();
			sockServerSimulate.usage();
			
			int dport = Integer.parseInt(System.getProperty("port", "10308"));
			sockServerSimulate.setPort(dport);
			
			String dfilename = System.getProperty("filename", "momt.msu");
			sockServerSimulate.setFilename(dfilename);
			
			sockServerSimulate.setDpc(System.getProperty("dpc"));
			sockServerSimulate.setOpc(System.getProperty("opc"));
			
			
			//-Drate=10 -Dtime=0 
			int rate = Integer.parseInt(System.getProperty("rate", "10"));
			sockServerSimulate.setRate(rate);
			int time = Integer.parseInt(System.getProperty("time", "0"));
			int inteval = Integer.parseInt(System.getProperty("inteval", "100"));
			inteval = rate >=1000? 0:1000/rate - 1;
			sockServerSimulate.setInteval(inteval);
			sockServerSimulate.setTime(time);
			
			sockServerSimulate.server();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	


	public void usage(){
		//-Dip=127.0.0.1 -Dport=80  -Dnum=1 -Dfilename=momt.msu -DrspFilename=rsp.txt -Drate=10 -Dtime=0 -Dinteval=100
		System.out.println("Usage: java -Dport=10308 -Dfilename=momt.msu -Ddpc=0x010203 -Dopc=0x030201 -Dthreads=1 -Drate=10 -Dtime=0 -Dinteval=100 -cp packet.jar com.lj.tools.SockServerSimulate ");
		System.out.println("\t-Dport={SERVER_PORT} listening PORT, def: 10308");
		System.out.println("\t-Dfilename={MSU_FILE} MSU file or path, def: momt.msu");
		System.out.println("\t-Ddpc={DPC} HEX string.");
		System.out.println("\t-Dopc={OPC} HEX string.");
		System.out.println("\t-Dthreads={THREAD_NUM}  num threads,def: 1");
		System.out.println("\t-Drate={RATE}  count of a second ( limit by MSU file's first line), def: 100");
		System.out.println("\t-Dtime={RUN_TIME} milliseconds, 0 is unlimit, def: 0");
		System.out.println("\t-Dinteval={SLEEP_TIME} <unused> milliseconds, def: 100");
		
		System.out.println("\t-DTxIP={IP} <unused> special IP list by ';' for TX");
		System.out.println("\t-DRxIP={IP} <unused> special IP list by ';' for RX");
		System.out.println();
	}

	public void server(){
		server(port);
	}
	
	public void server(int port){

		try {
			ServerSocket theServerSocket = new ServerSocket(port);
			System.out.println("THE SERVER BINDING ON port = " + port+ ", IS RUNNING AT " + new Date());
			
			while(true){
				
				Socket newSocket = theServerSocket.accept();
				newSocket.setKeepAlive(true);
				System.out.println("conneted by " + newSocket+ " AT " + new Date());
				newSocket.setSoTimeout(inteval<1?1:inteval);  //防止读阻塞
				
				/*//for test speed
				int count = 0;
				System.out.println(count+", "+System.currentTimeMillis());
				while (true){
					newSocket.getOutputStream().write(new byte[100]);
					count ++;
					if (count %1000==0){
						System.out.println(count+", "+System.currentTimeMillis());
					}
					if (count>1000000){
						break;
					}
				}*/
				
//		        filename = "G://workspace//packet//data//bjcdmazcxc//解失败文件//139PPS_0712_194958.dat.pcap";

//				sendMsu(newSocket);
				
				//生成msu后启动独立线程发送
				
				genMsu(newSocket.getRemoteSocketAddress().toString()) ;  //key is IP
				class _SockClientHandle extends Thread {  
					private Socket sock = null;
			        _SockClientHandle(Socket sock){
			            this.sock = sock;
			        }
			          public void run(){
			        	  int count = 0;
			        	  System.out.println(sock.toString()+" - "+count+" is starting at " + new Date());
			      		
			      		  try {
							InputStream streamFromServer = sock.getInputStream();
							  OutputStream streamToServer = sock.getOutputStream();
							  
							  List<byte[]> msuList = threadMsuMap.get(sock.getRemoteSocketAddress().toString());  //key is IP
							  long __old = System.currentTimeMillis();
							  long _rateOld = System.currentTimeMillis();
							  int _rate = rate;
							  for(int i=0; i<msuList.size(); i++){
								  long _old = System.currentTimeMillis();
								  streamToServer.write(msuList.get(i));
								  count ++;
								  _rate --;
								  
								  if (time >= 0 ){
								   if (time > 0 && (System.currentTimeMillis() - __old) >= time*1000  ){
									   //timeout
									  break;
								   }else {
									   //loop
									   i = i == msuList.size() - 1 ? -1: i;
								   }
								  }
								  
								  //rate
								  if (inteval>0 || count%rate==0){ //仅需延时或每发送100个msg后读一次
									  readAndSleep(streamFromServer, _old );
								  }
								  
								  if (count%(rate*10)==0){
							    	System.out.println(sock.toString()+" - "+count+" is sending at " + new Date());
							      }
								  
								  //每秒发送条数完成且耗时不到一秒需sleep
								  if (_rate == 0 && System.currentTimeMillis() - _rateOld < 1000){
									 try {
										Thread.currentThread().sleep(1000 - (System.currentTimeMillis() - _rateOld));
									} catch (InterruptedException e) {
									}
									 _rateOld = System.currentTimeMillis();
									 _rate = rate;
								  }
							  }
						} catch (Exception e) {
							e.printStackTrace();
						}
			              
			            System.out.println(sock.toString()+" - "+count+" is finished at " + new Date());
			          }
				}
				new _SockClientHandle(newSocket).start();
				
//			    Task task = new Task();
//			    Thread _thread = new SockClientHandle(newSocket,task);
//			    _thread.setName("TASK-"+(task.getTaskId()>0?task.getTaskId() : newSocket.toString()));
//			    _thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	//直接读取文件的msu并发送
	public int sendMsu(Socket socket) throws Exception {
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
        		sendMsuFromMsuOrTxt(socket,f);
        	}else if ("pcap".equalsIgnoreCase(ext)){
        		sendMsuFromPcap(socket,f);
        	}else{
        		System.out.println(f+" is unsupport, ignore it." + new Date());
        		continue;
        	}
        	
        }
		
		return count;
	}
	
	//生成所有msu
	public int genMsu(String keyname) throws Exception {
		int count =0;
		
		File file = new File(filename);
        String filepath = file.getParent();
        String[] datafiles = {file.getName()};
        if (file.isDirectory()){  //支持读取目录
        	datafiles = file.list();
        	Arrays.sort(datafiles);
        	filepath = file.getPath();
        }
        
        String key = keyname;  //task/thread's name
        for (String _file : datafiles){
        	String f = filepath+File.separator+_file;
        	File datafile = new File(f);
        	if (datafile.isDirectory()){
        		System.out.println(f+" is Directory, ignore it." + new Date());
        		continue;
            }
        	
        	int pos = f.indexOf(".", f.length()-5);
        	String ext = f.substring(pos+1);
        	List<byte[]> msuList = new ArrayList<byte[]>();
        	if ("msu".equalsIgnoreCase(ext) || "txt".equalsIgnoreCase(ext)){
        		msuList = getMsuFromMsuOrTxt(f);
        	}else if ("pcap".equalsIgnoreCase(ext)){
        		msuList = getMsuFromPcap(f);
        	}else{
        		System.out.println(f+" is unsupport, ignore it." + new Date());
        		continue;
        	}
        	
        	List<byte[]> _msuList = threadMsuMap.get(key);
        	if (_msuList == null){
        		threadMsuMap.put(key, msuList);
        	}else{
        		_msuList.addAll(msuList);
        	}
        	
        }
		
		return count;
	}
	
	//读取msu或txt文件的msu
	public List<byte[]> getMsuFromMsuOrTxt(String filename) throws Exception {
		
		List<byte[]> msuList = new ArrayList<byte[]>();
		
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
		    		if ( msuSpeedByFile && line.indexOf("speed")>0){
		    			String[] _values = line.split("[ ,;=]");
		    			rate = Integer.parseInt(_values[_values.length-1]);
		    			inteval = rate >=1000? 0:1000/rate - 1;
		    		}
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	//可能有81,85等msu
//		    	if (!line.trim().startsWith("83")) {  //illegal msu
//		    		System.out.println("ERR: "+line);
//		    		
//		    		line = br.readLine();
//		    		continue;
//		    	}
		    	long _old = System.currentTimeMillis();
		    	
		    	//change OPC=0x010203 & DPC=0x030201
		    	//line="8X 03 02 01 01 02 03 ..."
		    	/*
		    	String _dpc = dpc.substring(6,8)+" "+dpc.substring(4,6)+" "+dpc.substring(2,4);
		    	String _opc = opc.substring(6,8)+" "+opc.substring(4,6)+" "+opc.substring(2,4);
		    	line = line.trim();
		    	line = line.substring(0,3)+_dpc+" "+_opc+line.substring(20);
		    	*/
		    	
		    	//去space并换DPC,OPC(仅8X的码流)
		    	line = line.replaceAll(" ", "");
		    	if (line.charAt(0)=='8'){ //可能会有误判,暂忽略
		    		line = line.substring(0,2)+dpc.substring(6,8)+dpc.substring(4,6)+dpc.substring(2,4)+opc.substring(6,8)+opc.substring(4,6)+opc.substring(2,4)+line.substring(14);
		    	}
		    	
		    	byte[] msu = HexFormat.str2bytes(line);
		    	//MSU超长丢弃
		    	if (line.charAt(0)=='8' && msu.length > FileConvert.MSU_MAX_LENGTH){
		    		System.out.println(_count+"; file="+f+" start at " + new Date());
		    		System.out.println("IGNORE MSU because too long ="+msu.length);
		    		System.out.println("MSU start with "+HexFormat.bytes2str(msu, 0, 10, true));
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	//format: type<1B>+length<4B>+msuLen<4B>+msuData<83...>
	    		//type+length+msuLen: 1B+4B+4B
	    		ByteBuffer bb = ByteBuffer.allocate(9+msu.length);
	    		if (isMsudCsp){
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
		
//		return count;
		return msuList;
	}
	
	//读取msu或txt文件的msu并发送
	public int sendMsuFromMsuOrTxt(Socket socket, String filename) throws Exception {
		int count = 0;
		
		InputStream streamFromServer = socket.getInputStream();
        OutputStream streamToServer = socket.getOutputStream();
        
        	
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
		    		if ( msuSpeedByFile && line.indexOf("speed")>0){
		    			String[] _values = line.split("[ ,;=]");
		    			rate = Integer.parseInt(_values[_values.length-1]);
		    			inteval = rate >=1000? 0:1000/rate - 1;
		    			socket.setSoTimeout(inteval);
		    		}
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	//可能有81,85等msu
//		    	if (!line.trim().startsWith("83")) {  //illegal msu
//		    		System.out.println("ERR: "+line);
//		    		
//		    		line = br.readLine();
//		    		continue;
//		    	}
		    	long _old = System.currentTimeMillis();
		    	
		    	//change OPC=0x010203 & DPC=0x030201
		    	//line="83 03 02 01 01 02 03 ..."
		    	/*
		    	String _dpc = dpc.substring(6,8)+" "+dpc.substring(4,6)+" "+dpc.substring(2,4);
		    	String _opc = opc.substring(6,8)+" "+opc.substring(4,6)+" "+opc.substring(2,4);
		    	line = line.trim();
		    	line = line.substring(0,3)+_dpc+" "+_opc+line.substring(20);
		    	*/

		    	//去space并换DPC,OPC(仅8X的码流)
		    	line = line.replaceAll(" ", "");
		    	if (line.charAt(0)=='8'){ //可能会有误判,暂忽略
		    		line = line.substring(0,2)+dpc.substring(6,8)+dpc.substring(4,6)+dpc.substring(2,4)+opc.substring(6,8)+opc.substring(4,6)+opc.substring(2,4)+line.substring(14);
		    	}
		    	
		    	byte[] msu = HexFormat.str2bytes(line);
		    	//MSU超长丢弃
		    	if (line.charAt(0)=='8' && msu.length > FileConvert.MSU_MAX_LENGTH){
		    		System.out.println(_count+"; file="+f+" start at " + new Date());
		    		System.out.println("IGNORE MSU because too long ="+msu.length);
		    		System.out.println("MSU start with "+HexFormat.bytes2str(msu, 0, 10, true));
		    		
		    		line = br.readLine();
		    		continue;
		    	}
		    	
		    	try{
			    	if (isMsudCsp){
			    		//format: type<1B>+length<4B>+msuLen<4B>+msuData<83...>
			    		//type+length+msuLen: 1B+4B+4B
			    		ByteBuffer bb = ByteBuffer.allocate(9);
			    		bb.put((byte)0x06);  //06-msu
			    		bb.putInt(msu.length+4);
			    		bb.putInt(msu.length);
			    		bb.flip();
			    		
			    		byte[] head = bb.array();
			    		streamToServer.write(head);
			    	}
			    	
			    	streamToServer.write(msu);
			        _count ++;
			        
			        if (inteval>0 || _count%100==0){ //仅需延时或每发送100个msg后读一次
			        	readAndSleep(streamFromServer, _old );
			        	_old = System.currentTimeMillis();
			        }
			    } catch (SocketTimeoutException e) {
			    	//ignore
				}catch (Exception e) {
					System.out.println(_count+"; file="+f+" is starting at " + new Date());
					throw e;
				}
		        
		        if (_count%1000==0){
		        	System.out.println(_count+"; file="+f+" is starting at " + new Date());
		        }
		    	
		        line = br.readLine();
		    } //end  while (null!=line)
		    br.close();
		    count = count + _count;
		    
		    System.out.println(_count+"; file="+f+" is finished at " + new Date());
        }
        
        System.out.println("Total: at " + new Date());
        System.out.println("count=" + count + "; files: "+Arrays.toString(datafiles));
		
		return count;
	}
	
	private void  readAndSleep(InputStream streamFromServer, long _old ) {
		
		//read RECV, but ignore them
    	byte[] rbuf = new byte[1024*1024];
        try {
			streamFromServer.read(rbuf);  //超时读,可能影响性能
		} catch (Exception e) {
		}
		        
        //deplay ms
        if (inteval>0 && inteval - (System.currentTimeMillis() - _old) > 0  ){
        	try {
				Thread.currentThread().sleep(inteval - (System.currentTimeMillis() - _old));
			} catch (InterruptedException e) {
			}
        }
		
	}

	//读取pcap文件的msu
	public List<byte[]> getMsuFromPcap(String filename) throws Exception {
		List<byte[]> msuList = new ArrayList<byte[]>();
		
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
		    	long _old = System.currentTimeMillis();
		    	
		    	//read packet head
		    	fileInputStream.read(packetHead);
		    	
		    	//calc msu len
		    	int msuLen = HexFormat.bigbytes2int(packetHead, 12);
		    	
		    	//read packet 
		    	byte[] msu = new byte[msuLen];
		    	fileInputStream.read(msu);
		    	
		    	//MSU超长丢弃
		    	if (msuLen > FileConvert.MSU_MAX_LENGTH){
		    		System.out.println(_count+"; file="+f+" start at " + new Date());
		    		System.out.println("IGNORE MSU because too long ="+msuLen);
		    		System.out.println("MSU start with "+HexFormat.bytes2str(msu, 0, 10, true));
		    		continue;
		    	}
		    	
		    	ByteBuffer bb = ByteBuffer.allocate(9+msu.length);
	    		if (isMsudCsp){
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
		        	System.out.println(_count+"; file="+f+" is starting at " + new Date());
		        }
		    	
		    } //end  while (null!=line)
		    fileInputStream.close();
		    count = count + _count;
		    
		    System.out.println(_count+"; file="+f+" is finished at " + new Date());
        }
        
        System.out.println("count=" + count + "; files: "+Arrays.toString(datafiles));
		
//		return count;
		return msuList;
	}
	
	//读取pcap文件的msu并发送
	public int sendMsuFromPcap(Socket socket, String filename) throws Exception {
		int count = 0;
		
		InputStream streamFromServer = socket.getInputStream();
        OutputStream streamToServer = socket.getOutputStream();
        
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
		    	long _old = System.currentTimeMillis();
		    	
		    	//read packet head
		    	fileInputStream.read(packetHead);
		    	
		    	//calc msu len
		    	int msuLen = HexFormat.bigbytes2int(packetHead, 12);
		    	
		    	//read packet 
		    	byte[] msu = new byte[msuLen];
		    	fileInputStream.read(msu);
		    	
		    	//MSU超长丢弃
		    	if (msuLen > FileConvert.MSU_MAX_LENGTH){
		    		System.out.println(_count+"; file="+f+" start at " + new Date());
		    		System.out.println("IGNORE MSU because too long ="+msuLen);
		    		System.out.println("MSU start with "+HexFormat.bytes2str(msu, 0, 10, true));
		    		continue;
		    	}
		    	
		    	if (isMsudCsp){
		    		//format: type<1B>+length<4B>+msuLen<4B>+msuData<83...>
		    		//type+length+msuLen: 1B+4B+4B
		    		ByteBuffer bb = ByteBuffer.allocate(9);
		    		bb.put((byte)0x06);  //06-msu
		    		bb.putInt(msu.length+4);
		    		bb.putInt(msu.length);
		    		bb.flip();
		    		
		    		byte[] head = bb.array();
		    		streamToServer.write(head);
		    	}
		    	
		    	streamToServer.write(msu);
		        _count ++;
		        
		        if (inteval>0 || _count%100==0){ //仅需延时或每发送100个msg后读一次
		        	readAndSleep(streamFromServer, _old );
		        }
		        
		        if (_count%1000==0){
		        	System.out.println(_count+"; file="+f+" is starting at " + new Date());
		        }
		    	
		    } //end  while (null!=line)
		    fileInputStream.close();
		    count = count + _count;
		    
		    System.out.println(_count+"; file="+f+" is finished at " + new Date());
        }
        
        System.out.println("Total: at " + new Date());
        System.out.println("count=" + count + "; files: "+Arrays.toString(datafiles));
		
		return count;
	}

}
