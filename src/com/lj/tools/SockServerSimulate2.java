/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-9-10
*   create
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.lj.tools;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SockServerSimulate2 {
	@Setter @Getter private int port = 10308;
	@Setter @Getter private String sigTxIP = null;
	@Setter @Getter private String sigRxIP = null;
	
	@Setter @Getter private Socket sigTxSocket = null;
	@Setter @Getter private Socket sigRxSocket = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			SockServerSimulate2 sockServerSimulate = new SockServerSimulate2();
			sockServerSimulate.usage();
			
			int dport = Integer.parseInt(System.getProperty("port", "10308"));
			sockServerSimulate.setPort(dport);
			
			sockServerSimulate.setSigTxIP(System.getProperty("sigTxIP","none"));
			sockServerSimulate.setSigRxIP(System.getProperty("sigRxIP","none"));
			
			sockServerSimulate.server();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void usage(){
		System.out.println("Usage: java -Dport=10308 -DsigTxIP={IP} -DsigRxIP={IP} -cp packet.jar com.lj.tools.SockServerSimulate2 ");
		System.out.println("\t-Dport={SERVER_PORT} listening PORT, def: 10308");
		
		System.out.println("\t-DsigTxIP={IP} SIG device IP list by ';' for TX");
		System.out.println("\t-DsigRxIP={IP} SIG device IP list by ';' for RX");
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
				
				if (sigTxIP.indexOf(newSocket.getRemoteSocketAddress().toString()) >=0 ){
					sigTxSocket = newSocket;
					System.out.println("SIG DEVICE FOR TX sigTxSocket = " + sigTxSocket);
					continue;
				}
				if (sigRxIP.indexOf(newSocket.getRemoteSocketAddress().toString()) >=0 ){
					sigRxSocket = newSocket;
					System.out.println("SIG DEVICE FOR RX sigRxSocket = " + sigRxSocket);
					continue;
				}
				
			    Task task = new Task();
			    Thread _thread = new SockClientHandle(newSocket,task);
			    _thread.setName("TASK-"+(task.getTaskId()>0?task.getTaskId() : newSocket.toString()));
			    _thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
}
