/*
 * Created on 2005-7-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.lj.packet.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.lj.packet.NioBytePostMan;

/**
 * @author Xiaoran27
 * Date 2005-7-19
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NioBytePostManTestServer  {

	/**
	 * 
	 */
	public NioBytePostManTestServer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		ServerSocketChannel listenSocket = null;
	
		try{
			//Create a listening socket.
			listenSocket = ServerSocketChannel.open();
			listenSocket.socket().bind(new InetSocketAddress(8086));
			listenSocket.configureBlocking(false);
		}catch(IOException ioe){
			System.err.println("Unable to listen on port 8086:" + ioe);
		}
		System.out.println("Waiting Socket on port 8086." );
		
		try{
			
			Selector selector = Selector.open();
			listenSocket.register(selector,SelectionKey.OP_ACCEPT);
			while(true){
				
				if (selector.select()<0) continue;
				Set selected = selector.selectedKeys(); 

                for (Iterator itr = selected.iterator(); itr.hasNext();) { 
                   SelectionKey key = (SelectionKey) itr.next(); 
                   itr.remove();
                   
                   ServerSocketChannel nextReady = (ServerSocketChannel)key.channel();
                   SocketChannel clientSocket = nextReady.accept();
  				   clientSocket.configureBlocking(false);
                   System.out.println(new Date());
                   System.out.println(clientSocket);
  					
   					//hello at Date
   					StringBuffer sb = new StringBuffer();
   					sb.append("HELLO!").append('\377').append("ÄãºÃ! ");//.append(new Date());
   					int rtn = NioBytePostMan.SendPacket(clientSocket,sb.toString().getBytes(),sb.toString().getBytes().length);
   					System.out.println("Send Hello: rtn = " + String.valueOf(rtn) + "; package = " + String.valueOf(sb));
   					rtn = NioBytePostMan.SendPacket(clientSocket,sb.toString().getBytes(),sb.toString().getBytes().length);
   					System.out.println("Send Hello: rtn = " + String.valueOf(rtn) + "; package = " + String.valueOf(sb));
   					
   					//Create a new handler.
   					NioByteServerHandler newHandler = new NioByteServerHandler(clientSocket);
   					
   					Thread newHandlerThread = new Thread(newHandler);
   					newHandlerThread.start();
   					
                 	//clientSocket.close();

                } 
			}
		}catch(IOException ioe){
			System.err.println("Failed I/O: " + ioe);
		}
		
	}
}


class NioByteServerHandler implements Runnable {
	private static final boolean AUTOFLUSH = true;
	private SocketChannel mySocket = null;
	
	public NioByteServerHandler(SocketChannel newSocket){
		mySocket = newSocket;
	}
	
	/**
	 * This is the thread of execution which implements the communication.
	 */
	public void run(){
		byte[] src,dst,olddst;
		int srcLen, dstLen;
		int rtn;
		int size[]={8192};
	
		dst = new byte[81920];
		long s=System.currentTimeMillis();
		rtn = NioBytePostMan.GetPacket(mySocket,dst,size,100L);
		System.out.println(System.currentTimeMillis()-s);
		System.out.println("Receive: rtn = " + String.valueOf(rtn) + "; packet = " + new String(dst,0,rtn == 1?size[0]:100));
		System.out.println(size[0]);
		
		src = dst;
		srcLen = size[0];
		rtn = NioBytePostMan.SendPacket(mySocket,src,srcLen);
		System.out.println("Send: rtn = " + String.valueOf(rtn) + "; package = " + new String(src,0,rtn == 1?srcLen:100));
		
	}
	
}

