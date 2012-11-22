/*
 * Created on 2005-7-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.lj.packet.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import junit.framework.TestCase;

import com.lj.packet.NioBytePostMan;

/**
 * @author Xiaoran27
 * Date 2005-7-19
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NioBytePostManTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(NioBytePostManTest.class);
	}

	public void testGetPacket() {
		//TODO Implement GetPacket().
		
		SocketChannel mySocket = null;
		try{
			mySocket = SocketChannel.open(new InetSocketAddress("localhost", 8086));
			mySocket.configureBlocking(false);
		}catch(IOException ioe){
			System.err.println("Fail in I/O : " + ioe);
		}
		
		byte[] src,dst;
		int srcLen, dstLen;
		int rtn=-1;
		int size[]={8192};
		
		dst = new byte[81920];

		//*
		rtn = NioBytePostMan.GetPacket(mySocket,dst,size,3000000L);
		System.out.println("GetPacket: rtn = " + String.valueOf(rtn) + "; packet = " + new String(dst,0,rtn == 1?size[0]:100));
		System.out.println(size[0]);

		try{
			mySocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public void testSendPacket() {
		//TODO Implement SendPacket().
		
		SocketChannel mySocket = null;
		try{
			mySocket = SocketChannel.open(new InetSocketAddress("localhost", 8086));
			mySocket.configureBlocking(false);
		}catch(IOException ioe){
			System.err.println("Fail in I/O : " + ioe);
		}
		
		byte[] src,dst;
		int srcLen, dstLen;
		int rtn=-1;
		int size[]={8192};
		
		dst = new byte[81920];
		
		src = "+HELLO! °Ý°Ý!".getBytes();
		srcLen = src.length;

		rtn = NioBytePostMan.SendPacket(mySocket,src,srcLen);
		System.out.println("SendPacket: rtn = " + String.valueOf(rtn) + "; package = " + new String(src));
		
		long s=System.currentTimeMillis();
		rtn = NioBytePostMan.GetPacket(mySocket,dst,size,3000000L);
		System.out.println(System.currentTimeMillis()-s);
		System.out.println("GetPacket: rtn = " + String.valueOf(rtn) + "; packet = " + new String(dst,0,rtn == 1?size[0]:100));
		System.out.println(size[0]);
		try{
			mySocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
