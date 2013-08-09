/*************************** END OF CHANGE REPORT HISTORY ********************\
************************* CHANGE REPORT HISTORY *******************************
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-X-X
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-17
* + //--，#作为注释行
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-17
* + //防止读阻塞
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 读取REQ码流按指定频率发送给监听端口
 * 接收到REQ码流从读取RSP码流给出响应
 * 可以指定时长，间隔，发送频率进行码流发送
*/
public class SockClientSimulate {
	
	public static List<byte[]> reqArrays = new ArrayList<byte[]>();
	public static List<byte[]> rspArrays = new ArrayList<byte[]>();
	
	
	/**
	 * @param args
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args)  {
		SockClientSimulate scs = new SockClientSimulate();
		scs.usage();
		try {
			scs.run(Integer.parseInt(System.getProperty("threads", "1")));  //-Dthreads=1
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void usage(){
		//-Dip=127.0.0.1 -Dport=80  -Dnum=1 -DreqFilename=req.txt -DrspFilename=rsp.txt -Drate=10 -Dtime=0 -Dinteval=100
		System.out.println("Usage: java -Dip=127.0.0.1 -Dport=80  -Dthreads=1 -DreqFilename=req.txt -DrspFilename=rsp.txt -Drate=10 -Dtime=0 -Dinteval=100 -cp packet.jar com.lj.tools.SockClientSimulate ");
		System.out.println("\t-Dip={SERVER_IP}  server's IP,def: 127.0.0.1");
		System.out.println("\t-Dport={SERVER_PORT} listening PORT, def: 80");
		System.out.println("\t-Dthreads={THREAD_NUM}  num threads,def: 1");
		System.out.println("\t-DreqFilename={BYTE_REQ}  a filename(format: HEX) for REQ, def: req.txt");
		System.out.println("\t-DrspFilename={BYTE_RSP} <unused>  a filename(format: HEX) for RSP, def: rsp.txt");
		System.out.println("\t-Drate={RATE}  count of a second, def: 10");
		System.out.println("\t-Dtime={RUN_TIME} milliseconds, 0 is unlimit, def: 0");
		System.out.println("\t-Dinteval={SLEEP_TIME} <unused> milliseconds, def: 100");
		System.out.println();
	
	}
	
	public void run(int num) throws Exception {
		init();
		for (int i=0; i<num; i++){
			start(i);
		}
	}
	
	private void init() throws Exception {
		String userDir=System.getProperty("user.dir")+System.getProperty("file.separator");
		
		String reqFilename = System.getProperty("reqFilename", "req.txt");
		if (! new File(reqFilename).exists()){
			reqFilename = userDir+reqFilename;
		}
		reqArrays = getBytesFromTxtFile(reqFilename);
		
		String rspFilename = System.getProperty("rspFilename", "rsp.txt");
		if (! new File(rspFilename).exists()){
			rspFilename = userDir+rspFilename;
		}
		rspArrays = getBytesFromTxtFile(rspFilename);
	}
	
	private List<byte[]> getBytesFromTxtFile(String txtFilename) throws Exception{
		List<byte[]> blist = new ArrayList<byte[]>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(txtFilename)));
        String line = br.readLine();
        while (null!=line){
        	if (line.trim().length()<1 || line.startsWith("--")|| line.startsWith("#")) {  //--，#作为注释行
        		line = br.readLine();
        		continue;
        	}
        	
        	blist.add(String2Byte(line));
        	
        	line = br.readLine();
        }
        br.close();
		
		return blist;
	}
	
	//hexStr="01 23 45 67 89 ab cd ef"
	private byte[] String2Byte(String hexStr){
		
		String hex = hexStr.replaceAll(" ", "");
		byte[] b = new byte[hex.length()/2];
		for (int i=0; i<b.length; i++){
			b[i]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
		}
		
		return b;
	}
	
	public void start(int id){
		Thread _thread = new Thread(new SockClientRunnable(),"SockClientRunnable#"+id);
		_thread.start();
	}
	

}


class SockClientRunnable implements Runnable {

	public void run()  {
		System.out.println(Thread.currentThread().getName()+" START AT "+new Date());
		
		try{
			
			//-Drate=10 -Dtime=0 
			int rate = Integer.parseInt(System.getProperty("rate", "10"));
			int time = Integer.parseInt(System.getProperty("time", "0"));
			int inteval = Integer.parseInt(System.getProperty("inteval", "100"));
			inteval = 1000/rate - 1;
			
			String ip = System.getProperty("ip", "127.0.0.1");
			int port = Integer.parseInt(System.getProperty("port", "80"));
			  
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port));
			socket.setKeepAlive(true);
			socket.setSoTimeout(inteval<1?1:inteval);  //防止读阻塞
	        
	        InputStream streamFromServer = socket.getInputStream();
	        OutputStream streamToServer = socket.getOutputStream();
	        
	        long count = 0;
	        int index = 0;
	        long old = System.currentTimeMillis();
	        while(time<=0 || System.currentTimeMillis() - old < time*1000){
	        	index = (int)(count%SockClientSimulate.reqArrays.size());
	        	streamToServer.write(SockClientSimulate.reqArrays.get(index));
	        	count ++;
	        	
	        	try {
					byte[] rcvByte = new byte[1024];
					int rcvCnt = streamFromServer.read(rcvByte);
				} catch (Exception e) {
				}
	        	
	        	if (count%(rate*10)==0)  //10s
	        		System.out.println(Thread.currentThread().getName()+"; STAT: ms="+(System.currentTimeMillis() - old)+"; count="+count);
	        }
	        System.out.println(Thread.currentThread().getName()+"; STAT: ms="+(System.currentTimeMillis() - old)+"; count="+count);
	        	
        }catch (Exception e) {
			e.printStackTrace();
		}
        
        
	}

}
