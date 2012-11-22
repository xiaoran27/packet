/*
 * Created on 2005-7-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.lj.packet;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;


/**
 * @author Xiaoran27
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
@Deprecated
public final class NioBytePostMan {
	private static final Logger log = Logger.getLogger( NioBytePostMan.class );

	private static PacketCount packetCount = new PacketCount();
	/**
	 *
	 */
	public NioBytePostMan() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
     * 从socket中读取数据,返回读取的字节数
     *
	 * @param fd - SocketChannel 确定的连接
	 * @param ptr - byte[] 存放收到的数据
	 * @param nbytes - 收数据的最大字节数
	 * @return int 实际收到的字节数
	 * return -101 if socket is disconnected or not opened.
	 * return -10 other Exception.
	 * @throws IOException
	 */
	public static int readn(SocketChannel fd, byte[] ptr, int nbytes) throws IOException
	{
		if (fd == null || ptr == null || nbytes <= 0 || ptr.length < nbytes) {
			return -100;
		}

		int nleft, nreads;

		nleft = nbytes;
		nreads = 0;
		while(nleft > 0 ) {

			if (fd.isConnected() && fd.isOpen()){
				;
			}else{
				try{
					fd.close(); //认为连接断了
				}catch(Exception e){}
				return -101;
			}

			ByteBuffer tptr = ByteBuffer.allocate(nleft);
			int nread=0;
			try{

				nread = fd.read(tptr);
				tptr.flip();
				if(nread == 0){/*EOF*/
					break;
				}
				if (nread<0){ //-1
					try{
						fd.close(); //认为连接断了
					}catch(Exception e1){}
					return -101;
				}

			}catch (IOException ioe){
				if(ioe instanceof SocketTimeoutException){
					return nreads;
				}
				throw ioe;
			}catch (Exception e){
				System.out.println("Failure in Exception: "+e);
				System.out.println(packetCount.toString());
				log.error("Failure in Exception: "+e,e);
				log.error(packetCount.toString());

				return -10;
			}

			nleft -= nread;
			System.arraycopy(tptr.array(),0,ptr,nreads,nread);
			nreads += nread;

			if (log.isInfoEnabled()) {
				log.info("sc="+fd);
				log.info("readn all bytes(HEX): "+ByteCodecRawMsg.byteToHex(ptr,nreads));
			}

		}

        //packetCount.countReadIncre(); //读的次数不是包数
		return nreads;  /*return >= 0*/
	}

	/**
     * 向socket中写数据,返回写的字节数
	 * @param fd - SocketChannel 确定的连接
	 * @param ptr - byte[] 存放要写的数据
	 * @param nbytes - 要写的最大字节数
	 * @return int 实际写的字节数
	 *  return -101 if socket is disconnected or not opened.
	 *  return -10 other Exception.
	 * @throws IOException
	 */
	public static int writen(SocketChannel fd, byte[] ptr, int nbytes) throws IOException
	{
		if (fd == null || ptr == null || nbytes <= 0 || ptr.length < nbytes) {
			return -100;
		}

		int nleft, nwrites;

		nleft = nbytes;
		nwrites = 0;

		while(nleft > 0 ) {

			if (fd.isConnected() && fd.isOpen()){
				;
			}else{
				try{
					fd.close(); //认为连接断了
				}catch(Exception e){}
				return -101;
			}

			ByteBuffer tptr = ByteBuffer.allocate(nleft);
			tptr.put(ptr,nwrites,nleft);
			int nwrite=0;
			try{

				tptr.flip();
				nwrite = fd.write(tptr);

				if(0 == nwrite){
					continue;
				}

			}catch (IOException ioe){
				if(ioe instanceof SocketTimeoutException){
					return nwrites;
				}
				throw ioe;
			}catch (Exception e){
				System.out.println("Failure in Exception: "+e);
				System.out.println(packetCount.toString());
				log.error("Failure in Exception: "+e,e);
				log.error(packetCount.toString());
				return -10;
			}

			nleft -= nwrite;
			//System.arraycopy(tptr.array(),0,ptr,nwrites,nwrite);
			nwrites += nwrite;

		}

		if (log.isInfoEnabled()) {
			log.info("sc="+fd);
			log.info("writen all bytes(HEX): "+ByteCodecRawMsg.byteToHex(ptr,nwrites));
		}

        //packetCount.countSendIncre(); //写的次数不是包数
		return nwrites;
	}


	/**
	 * 读取一个完整的包,否则丢弃
	 * @param sockfd
	 * @param pPacket
	 * @param size:
	 * @param timeout
	 * @return
	 * return -100 if bad parms
	 * return -101 if socket is disconnected or not opened.
	 * return -10 other Exception.
	 * return -9 when socket terminate by remote
	 * return -8 when terminate by a signal
	 * return -3 when msg too long to fit in the buffer
	 * return -2 when msg format error
	 * return -1 when socket IO error
	 * return 0 when timeout and no msg got
	 * return 1 when suc and msg got
	 *
	 * packet received stored in pPacket, received packet size stored in *size.
	 * timeout: time in microsecond.
	 *          -1, block until message got
	 *          0, no block, return at once if no message
	 */
	public static int GetPacket(SocketChannel sockfd, byte[] pPacket, int[] size, long timeout)
	{
		if(null == sockfd || null == pPacket || null == size ){
			return -100;
		}

		byte[] buf = new byte[81920];
		int bufSize;
		int rcvlen = 0;
		int rtn=0;

		bufSize = buf.length;
		while(true) {

			rtn=0;  //需要初始化
			if (sockfd.isConnected() && sockfd.isOpen()){
				;
			}else{
				try{
					sockfd.close(); //认为连接断了
				}catch(Exception e){}
				return -101;
			}

			if (log.isDebugEnabled()){
				log.debug("GetPacket read all bytes(HEX): "+ByteCodecRawMsg.byteToHex(buf,rcvlen));
			}

			ByteBuffer tbuf = null;
			try{
				tbuf = ByteBuffer.allocate(1);

				long oldMs = System.currentTimeMillis();
				while (System.currentTimeMillis() - oldMs <= timeout || timeout < 0){
					try{
						rtn = sockfd.read(tbuf);
					}catch(IOException ioe){
						System.out.println("GetPacket fail in IOException: " + ioe);
						System.out.println(packetCount.toString());
						log.error("GetPacket fail in IOException: "+ioe,ioe);
						log.error(packetCount.toString());

						try{
							sockfd.close(); //认为连接断了
						}catch(Exception e1){}
						return -1;
					}
					if (rtn<0){ //-1
						try{
							sockfd.close(); //认为连接断了
						}catch(Exception e1){}
						return -101;
					}
					if (rtn>0){
						break;
					}
				}

				if (0 == rtn && log.isInfoEnabled()) {
					log.info("sc="+sockfd);
					log.info("GetPacket all bytes(socket): rcvlen="+rcvlen+"; rtn=0"+"; " + ByteCodecRawMsg.byteToHex(buf,rcvlen));
				}

				if ( 0 == rtn ) return 0; // 可能是超时或没读到数据
				tbuf.flip();

			}catch (Exception e){
				System.out.println("Failure in Exception: "+e);
				System.out.println(packetCount.toString());
				log.error("Failure in Exception: "+e,e);
				log.error(packetCount.toString());

				return -10;
			}

			if (rtn < 0) break;
			if (rcvlen + rtn >= bufSize) {
			  return -3;
			}

			System.arraycopy(tbuf.array(),0,buf,rcvlen,rtn);

			rcvlen += rtn;
			size[0] = rcvlen;
			if (1==rcvlen) continue;

			if (buf[rcvlen-2]==ByteCodecRawMsg.MEET_CHAR && buf[rcvlen-1]==ByteCodecRawMsg.FILL_CHAR){
				continue;
			}else if (buf[rcvlen-2]==ByteCodecRawMsg.MEET_CHAR && buf[rcvlen-1]==ByteCodecRawMsg.MEET_CHAR){
				break;
			}if (buf[rcvlen-2]==ByteCodecRawMsg.MEET_CHAR && buf[rcvlen-1]!=ByteCodecRawMsg.FILL_CHAR){
				return -2;
			}

		}

		if (log.isInfoEnabled()) {
			log.info("sc="+sockfd);
			log.info("GetPacket all bytes(socket): rcvlen="+rcvlen+"; rtn="+rtn+"; " + ByteCodecRawMsg.byteToHex(buf,rcvlen));
		}

		rtn=ByteCodecRawMsg.decodeRawMsg(pPacket, size[0], buf, rcvlen);
		if (rtn < 0) {
			return -2;
		}
		size[0] = rtn;

        packetCount.countReadIncre();
		return 1;
	}

	/**
	 * @param sockfd
	 * @param pPacket
	 * @param size
	 * @return
	 * return 0 when error encode msg
	 * 	     -100 if bad parms
	 *       -101 if socket is disconnected or not opened.
	 *       -10 other Exception.
	 *       -8 when terminate by a signal
	 *       -1 when socket io error
	 *       1 when suc
	 */
	public static int SendPacket(SocketChannel sockfd, byte[] pPacket, int size)
	{
		if(null == sockfd || null == pPacket || size <= 0 || pPacket.length < size){
			return -100;
		}

		byte[] buf = new byte[81920];
		int bufSize = buf.length;
		int ret;

		ret = ByteCodecRawMsg.encodeRawMsg(buf, bufSize, pPacket, size);
		if( ret < 0 ) {
			return 0;
		}

		if (sockfd.isConnected() && sockfd.isOpen()){
			;
		}else{
			try{
				sockfd.close(); //认为连接断了
			}catch(Exception e){}
			return -101;
		}

		try{

			int sndlen = ret;
			ret = writen(sockfd, buf, sndlen);

			if (log.isInfoEnabled()) {
				log.info("sc="+sockfd);
				log.info("SendPacket all bytes(socket): sndlen="+sndlen+"; ret="+ret+"; " + ByteCodecRawMsg.byteToHex(buf,ret));
			}

		}catch(IOException ioe){
			System.out.println("SendPacket Fail I/O: " + ioe);
			System.out.println(packetCount.toString());
			log.error("SendPacket Fail I/O: "+ioe,ioe);
			log.error(packetCount.toString());

			try{
				sockfd.close(); //认为连接断了
			}catch(Exception e){}

			return -1;
		}catch (Exception e){
			System.out.println("Failure in Exception: "+e);
			System.out.println(packetCount.toString());
			log.error("Failure in Exception: "+e,e);
			log.error(packetCount.toString());
			return -10;
		}

		if (ret < 0){
			return ret;
		}

		packetCount.countSendIncre();
		return 1;
	}


	public static void main(String[] args) {

	}
}
