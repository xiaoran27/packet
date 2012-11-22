/*
 * Created on 2005-7-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.lj.packet;

import hyjc.tlv.Encoder;
import hyjc.tlv.Decoder;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;

/*
数据包的发送过程：
1，调用相应的encode函数编码产生要发送的数据包（encode函数由协议编译工具自动生成）。
2，调用SendPacket发送数据包，SendPacket会执行以下动作：
    a. 内部每含有一个0xFF字节，则在其后加入一个0x00字节。
    b. 在消息末尾加上两个0xFF字节。
    c. 发送到socket。

数据包的接收过程：
1，调用GetPacket函数，GetPacket函数会执行以下动作：
   a. 侦听socket。
   b. 如有数据包到达，则接收它，以两个连续的0xFF作为判别是否是数据包结尾的标志。
   c. 去掉两个连续的0xFF尾部。
   d. 每有一个0xFF字节，则去掉其后的0x00字节。
2，调用相应的decode函数对接收到的数据包进行解码（decode函数由协议编译工具自动生成）。
*/

/**
 * @author Xiaoran27
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Administrator
 *
 */
@Deprecated
public final class ByteCodecRawMsg {
	private static final Logger log = Logger.getLogger( ByteCodecRawMsg.class );

	public static byte MEET_CHAR = (byte)0xff;
	public static byte FILL_CHAR = (byte)0x00;

	/**
	 *
	 */
	public ByteCodecRawMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * return the actual message length if contains a full message, <= rawlen
	 * return -100 if bad parms
	 * return -1 if bad format
	 * return 0 if incomplete message
	 */
	public static int parseRawMsg ( byte rawbuf[], int rawlen){

		/*check parm*/
		if (null == rawbuf){
			return -100;
		}
		if (rawlen <= 0 || rawbuf.length < rawlen){
			return -100;
		}

		if (log.isInfoEnabled()){
			log.info("parseRawMsg - rawbuf(HEX): "+byteToHex(rawbuf,rawlen));
		}

		int i = 0;
		while (i < rawlen && i < rawbuf.length )
		{

			if (rawbuf[i] == MEET_CHAR)
			{
				++i;
				if (i >= rawlen) /* incomplete message*/
					return 0;
				if (rawbuf[i] == FILL_CHAR)
					++i;
				else if (rawbuf[i] == MEET_CHAR) /* end of message*/
					return i + 1;
				else // unexpected byte in message*/
				{
					return -1;
				}
			}
			else
			{
				++i;
			}
		}
		return 0; /* incomplete message*/
	}


	/*
	 * return -100 if bad parms
	 * return -1 if incorrect format or insufficient buffer
	 * return the message length
	 *
	 * Decode procedure:
	 * 1.	去掉两个连续的0xFF尾部。
	 * 2.	每有一个0xFF字节，则去掉其后的0x00字节。
	 */
	public static int decodeRawMsg (byte dstbuf[], int dstmax, byte srcbuf[], int srclen)
	{

		if (null == srcbuf || null == dstbuf){
			return -100;
		}
		if (srclen <= 0 || srcbuf.length < srclen){
			return -100;
		}
		if (dstmax < (srclen-2)/2 || dstbuf.length < dstmax){
			return -100;
		}

		if (log.isInfoEnabled()){
			log.info("decodeRawMsg - srcbuf(HEX): "+byteToHex(srcbuf,srclen));
		}

		int i = 0;
		int dstlen = 0;
		while (i < srclen)
		{
			if (srcbuf[i] == MEET_CHAR)
			{
				++i;
				if ( i >= srclen) /*next byte expected*/
				{
					return -1;
				}
				if (srcbuf[i] == FILL_CHAR)
				{
					if (dstlen >= dstmax)
					{
						return -1;
					}
					dstbuf[dstlen++] = MEET_CHAR;
					++i;
				}
				else if (srcbuf[i] == MEET_CHAR)
				{
					if (i + 1 != srclen) /* end of message*/
					{
						return -1;
					}

					if (log.isInfoEnabled()){
						log.info("decodeRawMsg - dstbuf(HEX): "+byteToHex(dstbuf,dstlen));
					}

					return dstlen;
				}
				else /* unexpected byte*/
				{
					return -1;
				}
			}
			else
			{
				if (dstlen >= dstmax)
				{
					return -1;
				}
				dstbuf[dstlen++] = srcbuf[i];
				++i;
			}
		}
		return -1;
	}

	/*
	 * return -100 if bad parms
	 * return -1 if insufficient buffer
	 * return the destination length
	 *
	 * Encode procedure:
	 * 1.	内部每含有一个0xFF字节，则在其后加入一个0x00字节。
	 * 2.	在消息末尾加上两个0xFF字节。
	 */
	public static int encodeRawMsg (byte dstbuf[], int dstmax, byte srcbuf[], int srclen)
	{

		if (null == srcbuf || null == dstbuf){
			return -100;
		}
		if (srclen <= 0 || srcbuf.length < srclen){
			return -100;
		}
		if (dstmax < srclen + 2 || dstbuf.length < dstmax){
			return -100;
		}

		if (log.isInfoEnabled()){
			log.info("encodeRawMsg - srcbuf(HEX): "+byteToHex(srcbuf,srclen));
		}

		int i;
		int dstlen = 0;

		i = 0;
		while (i < srclen)
		{
			if (srcbuf[i] == MEET_CHAR)
			{
				if (dstlen + 1 >= dstmax)
				{
					return -1;
				}

				dstbuf[dstlen++] = MEET_CHAR;
				dstbuf[dstlen++] = FILL_CHAR;
			}
			else
			{
				if (dstlen >= dstmax)
				{
					return -1;
				}

				dstbuf[dstlen++] = srcbuf[i];
			}
			++i;
		}
		if (dstlen + 2 > dstmax)
		{
			return -1;
		}

		dstbuf[dstlen++] = MEET_CHAR;
		dstbuf[dstlen++] = MEET_CHAR;

		if (log.isInfoEnabled()){
			log.info("encodeRawMsg - dstbuf(HEX): "+byteToHex(dstbuf,dstlen));
		}

		return dstlen;
	}

	public static String byteToHex(byte[] o, int size){
		StringBuffer toHex = new StringBuffer();

		CRC32 crc32 = new CRC32();
		crc32.update(o, 0, size);
		long crc32Value=crc32.getValue();

		toHex.append("CRC32=").append(crc32Value).append("; ");
		toHex.append("byte to Hex(last 100): ");

		for (int i=(size>100?size-100:0); null != o && i<size; i++){
			toHex.append("0x").append(Integer.toHexString((int)o[i]).toUpperCase());
		}

		return toHex.toString();
	}

    /** 从byte[]提取packet. 完整的包存放在list中,错误包丢弃.
     * @param msgb - byte[] 存放要分析的数据
     * @param packeList - List 存放提取的完整的包.
     * @param isFF - boolean 数据包是不是FF结尾.
     * @return byte[] 剩余的byte. 若没有返回null.
     */
    synchronized static public byte[] parseMessagePacket(byte[] msgb, List<byte[]> packetList, boolean isFF){


        if (null==msgb || msgb.length<1){
            return null;
        }

        if (null==packetList){
            packetList = new LinkedList<byte[]>();
        }
        BlockingQueue<byte[]> packetQueue = new LinkedBlockingQueue<byte[]>(1024);
        byte[] remainb = parseMessagePacket(msgb, packetQueue, isFF);

        for(byte[] packetb : packetQueue){
            packetList.add(packetb);
        }

        return remainb;
    }

    /**
     * @see parseMessagePacket(byte[] , List<byte[]> , boolean )
     */
    synchronized static public byte[] parseMessagePacket(byte[] msgb, List<byte[]> packetList){
      return parseMessagePacket( msgb, packetList, true);
    }

    /** 从byte[]提取packet. 完整的包存放在BlockingQueue中,错误包丢弃.
     * @param msgb - byte[] 存放要分析的数据
     * @param packetQueue - BlockingQueue 存放提取的完整的包.
     * @param isFF - boolean 数据包是不是FF结尾.
     * @return byte[] 剩余的byte. 若没有返回null.
     */
    synchronized static public byte[] parseMessagePacket(byte[] msgb, BlockingQueue<byte[]> packetQueue, boolean isFF){
        if (null==msgb || msgb.length<1){
            return null;
        }

        if (null==packetQueue){
            packetQueue = new LinkedBlockingQueue<byte[]>();
        }

        if (!isFF){  //非FF结束
        	int n = -1;
        	byte[] msgb2=msgb;
        	do{

        	  n = Decoder.getOneMessageLength(msgb2,msgb2.length);
    		  if( n <= 0 ) {
    			return msgb2;  // 没有完整的数据包了
    		  }

    		  //put a packet to Queue
    		 byte[] packetb=new byte[n];
             System.arraycopy(msgb2,0,packetb,0,packetb.length);
             try {
                 packetQueue.put(packetb);
             } catch (Exception e) {
                 // TODO Auto-generated catch block
                 log.error("can't put Element.",e);
             }

             //
             byte[] remainb=new byte[msgb2.length-n];
             System.arraycopy(msgb2,n,remainb,0,remainb.length);
             msgb2=remainb;

        	}while(true);
        }

        //FF结束
        int packetPos=0;
        byte priorb = msgb[0];
        byte currentb = 0;
        for(int i=1; i<msgb.length; i++){
            currentb = msgb[i];
            if (priorb==ByteCodecRawMsg.MEET_CHAR && currentb==ByteCodecRawMsg.FILL_CHAR){
            /*合法的包*/
            }else if (priorb==ByteCodecRawMsg.MEET_CHAR && currentb==ByteCodecRawMsg.MEET_CHAR){
                /*完整的包*/

                byte[] packetb=new byte[i-packetPos+1];
                System.arraycopy(msgb,packetPos,packetb,0,packetb.length);
                try {
                    packetQueue.put(packetb);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    log.error("can't put Element.",e);
                }

                i++;
                packetPos=i;
                if(i<msgb.length-1){
                	currentb = msgb[i];
                	priorb = currentb;
                }

                continue;
            }else if (priorb==ByteCodecRawMsg.MEET_CHAR && currentb!=ByteCodecRawMsg.FILL_CHAR){
                /*错误的包*/
                packetPos=i;
            }

            priorb = currentb;

        }

        /*计算剩下的byte(不完整的包)*/
        if (msgb.length-packetPos<1){
            /*刚好结束*/
            msgb=null;
        }else{
            byte[] remainb=new byte[msgb.length-packetPos];
            System.arraycopy(msgb,packetPos,remainb,0,remainb.length);
            msgb=remainb;
        }

        return msgb;
    }


    /**
     * @see parseMessagePacket(byte[] , BlockingQueue<byte[]> , boolean )
     */
    synchronized static public byte[] parseMessagePacket(byte[] msgb, BlockingQueue<byte[]> packetQueue){
    	return parseMessagePacket(msgb, packetQueue, true);
    }

	public static void main(String[] args) {
		byte[] src,dst;
		int srcLen, dstLen;

		if (args.length<1){
			src = (" !\"#$%&\'()*+," + String.valueOf(MEET_CHAR)+ "中国" + String.valueOf(MEET_CHAR) + "-./09:;<=>?@AZ[\\]^_`az{|}~").getBytes();
		}else{
			src = args[0].getBytes();
		}
		System.out.println("Source: len = " + src.length + "; src = " + new String(src));

		srcLen = src.length;
		dstLen = srcLen*2 + 2;
		dst = new byte[dstLen];
		int rtn;

		rtn = encodeRawMsg(dst,dstLen,src, srcLen);
		System.out.println("Encode: rtn = " + String.valueOf(rtn) + "; dst = " + new String(dst,0,rtn));

		byte[] olddst = new byte[rtn];
		System.arraycopy(dst,0,olddst,0,rtn);

        ByteBuffer revBuf = ByteBuffer.allocate(8092);
        byte[] msgb = new byte[rtn+4];
        System.arraycopy(dst,0,msgb,0,rtn);//52
        msgb[9]=MEET_CHAR;
        msgb[10]=MEET_CHAR;
        msgb[19]=MEET_CHAR;
        msgb[20]='M';
        msgb[29]=MEET_CHAR;
        msgb[30]=FILL_CHAR;

        msgb[rtn]='1';
        msgb[rtn+1]='2';
        msgb[rtn+2]=MEET_CHAR;
        msgb[rtn+3]=FILL_CHAR;

        System.out.println("old msgb = " + new String(msgb,0,msgb.length));
        List<byte[]> packetList = new LinkedList<byte[]>();
        byte[] remainb=parseMessagePacket(msgb,packetList);
        System.out.println("remainb = " + (null==remainb?null:new String(remainb,0,remainb.length)));
        for(byte[] packet: packetList){
            System.out.println("packet = " + new String(packet,0,packet.length));
        }

        System.out.println("now msgb = " + new String(msgb,0,msgb.length));

		src = new byte[rtn];
		rtn = decodeRawMsg(src, rtn,dst,rtn);
		System.out.println("Decode: rtn = " + String.valueOf(rtn) + "; src = " + new String(src,0,rtn));

		System.out.println("Pcode1: len = " + String.valueOf(olddst.length) + "; src = " + new String(olddst,0,olddst.length));
		rtn = parseRawMsg(olddst,olddst.length);
		System.out.println("Pcode1: rtn = " + String.valueOf(rtn) );

		olddst[olddst.length - 1] = FILL_CHAR;
		System.out.println("Pcode2: len = " + String.valueOf(olddst.length) + "; src = " + new String(olddst,0,olddst.length));
		rtn = parseRawMsg(olddst,olddst.length);
		System.out.println("Pcode2: rtn = " + String.valueOf(rtn) );

		olddst[0] = MEET_CHAR;
		olddst[olddst.length - 1] = MEET_CHAR;
		System.out.println("Pcode2: len = " + String.valueOf(olddst.length) + "; src = " + new String(olddst,0,olddst.length));
		rtn = parseRawMsg(olddst,olddst.length);
		System.out.println("Pcode2: rtn = " + String.valueOf(rtn) );


	}
}
