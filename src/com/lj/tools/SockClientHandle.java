package com.lj.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SockClientHandle extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Setter @Getter private  Socket sock = null;
	@Setter @Getter private Task task = null;
	
	@Setter @Getter private List<byte[]> msuList = new ArrayList<byte[]>();
	
   public SockClientHandle( Socket sock, Task task){
        this.sock = sock;
        this.task = task;
    }
   
   
   public void run(){
    	  
    	  int count = 0;
    	  try {
	    	  count = task.genMsu();
	    	  System.out.println(sock.toString()+" - total msu = "+count+" at " + new Date());
	    	  System.out.println(sock.toString()+" - task will start at " + task.getStartTime());
	    	  
	    	  //定时任务开始
	    	  while (task.getMsFrom4y2M2d_2H2m2s(task.getStartTime()) < System.currentTimeMillis()){
	    		try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
	    	  }
	    	  
	    	  //计算2个msg间的间隔时间
	    	  int inteval = (task.getSendRate() >=1000)? 0:(1000/task.getSendRate() - 1);
	    	  sock.setSoTimeout(inteval<1?1:inteval);  //防止读阻塞
	    	  
	    	  count = 0;
	    	  System.out.println(sock.toString()+" - "+count+" is starting at " + new Date());
  		
  		  
	    	  InputStream streamFromServer = sock.getInputStream();
			  OutputStream streamToServer = sock.getOutputStream();
			  
			  long __old = System.currentTimeMillis();
			  for(int i=0; i<msuList.size(); i++){
				  long _old = System.currentTimeMillis();
				  streamToServer.write(msuList.get(i));
				  count ++;
				  
				  boolean isOver = false;
				  switch(task.getSendMode()){
				  case Task.SEND_MODE_LOOP:
					  i = (i == msuList.size() - 1) ? -1: i;
					  break;
				  case Task.SEND_MODE_TOTAL:
					  isOver = count >= task.getSendModeValue();
					  break;
				  case Task.SEND_MODE_TIME:
					  isOver = (System.currentTimeMillis() - __old )/1000 >= task.getSendModeValue();
					  break;
				  }
				  if (isOver){
					  break;
				  }
				  
				  //rate/10时读一次并可能sleep
				  if (task.getSendRate() < 1000 || count%task.getSendRate()==0){
					  readAndSleep(streamFromServer, _old , inteval);
				  }
				  
				  if (count%(task.getSendRate()*10)==0){
			    	System.out.println(sock.toString()+" - "+count+" is sending at " + new Date());
			      }
			  }
		} catch (Exception e) {
			e.printStackTrace();
		}
          
        System.out.println(sock.toString()+" - "+count+" is finished at " + new Date());
      }
      
  	public byte[]  readAndSleep(InputStream streamFromServer, long _old, long inteval ) {
  		
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
          
          return rbuf;
  		
  	}
      
}
