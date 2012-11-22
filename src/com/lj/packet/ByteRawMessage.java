
/*************************** COPYRIGHT INFORMATION ***************************\
*                                                                             *
*       INFOMATION INCLUDED IN THIS DOCUMENT IS THE EXCLUSIVE PROPERTY OF     *
*       LIANGJIANG COMMUNICATIONS SOFTWARE INC. COPYING, USE OR DISCLOSURE    *
*       OF ITS CONTENTS, EVEN IN PART, ARE NOT PERMITTED WITHOUT THE PRIOR    *
*       WRITTEN AGREEMENT OF THE PROPRIETOR.                                  *
*                                                                             *
*               Copyright (C) 2002-2006 Liangjiang Software Inc.              *
*                                                                             *
************************** END OF COPYRIGHT INFORMATION ***********************

************************* SOURCE FILE INFORMATION *****************************
**
*LAST UPDATED:: $Id: ByteRawMessage.java,v 1.7 2008/08/06 06:55:57 xiaoran27 Exp $
**
*PRINCIPAL AUTHOR:: Liangjiang Incorporation                                  *
**
*PRUCT NAME      ::
**
*LANGUAGE        :: ASNI                                                      *
**
*TARGET ENVIRONMENT:: JRE 1.5+                                                *
**
*DESCRIPTION     :: 数据(byte)分析处理.
**
*************************** END OF SOURCE FILE INFORMATION ********************

************************* CHANGE REPORT HISTORY *******************************
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2006-8-25
*   create
* V,Xiaoran27,2006-9-30 9:32
*   getSendBuff() 一次读取10个要发送的数据包(想提高写的速度).
* V,Xiaoran27,2006-10-8 15:20
*   getSendBuff() 修复pos没有递加的错误.
* V,Xiaoran27,2006-10-16 15:38
*   + synchronized public BlockingQueue<byte[]> getRecievePacket(boolean codecIsFF)
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2007-7-1
* + clear()
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2008-8-5
* + private long timeout = 60*1000l;  //超时时长（毫秒），默认60秒
    private long lastModified = -1;   //最后一次socket读写时间点（毫秒）
* + protected void finalize() throws Throwable
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.packet;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


@Deprecated
public class ByteRawMessage {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(ByteRawMessage.class);

  //~ Instance fields ----------------------------------------------------------

  /*
  * 我们认为当socket或socketChannel在timeout内没有读写，socket或socketChannel可能需要close。
  */
    private long timeout = 60*1000l;  //超时时长毫秒，默认60秒
    private long lastModified = -1;   //最后一次socket读写时间点

  private static int  BUFFERSIZE=81920;

  /*是否继续处理数据*/
  private boolean cotinueHandle = true;

  /**
   * 存放待处理数据
   */
  private ByteBuffer recieveBuff = ByteBuffer.allocate(BUFFERSIZE);

  /**
   * 存放从待处理数据中分析出的数据包
   */
  private BlockingQueue<byte[]> recievePacket = new LinkedBlockingQueue<byte[]>();

  /**
   * 处理后的数据
   */
  private ByteBuffer sendBuff = ByteBuffer.allocate(BUFFERSIZE);

  /**
   * 存放从处理后的数据中分析出的数据包
   */
  private BlockingQueue<byte[]> sendPacket = new LinkedBlockingQueue<byte[]>();

  //~ Methods ------------------------------------------------------------------


  public long getTimeout() {
	return timeout;
  }

  public void setTimeout(long timeout) {
	this.timeout = timeout;
  }

  public long getLastModified() {
	return lastModified;
  }

  public void setLastModified(long lastModified) {
	this.lastModified = lastModified;
  }

  /**
   * @return Returns the cotinueHandle.
   */
  public boolean isCotinueHandle() {
      return cotinueHandle;
  }

  /**
   * @param cotinueHandle The cotinueHandle to set.
   */
  synchronized public void setCotinueHandle(boolean cotinueHandle) {
      this.cotinueHandle = cotinueHandle;
  }

  /**
   * 获取待处理的数据包.
   *
   * @return BlockingQueue<byte[]> 每个元素是一个完整的数据包.
   */
  synchronized public BlockingQueue<byte[]> getRecievePacket(boolean codecIsFF) {

    if (recieveBuff.position()>0){
        recieveBuff.flip();
    }
    byte[] msgb = byteBuffTobyte(recieveBuff);
    byte[] remainb = ByteCodecRawMsg.parseMessagePacket(msgb, recievePacket,codecIsFF); //分析包
    recieveBuff = ByteBuffer.allocate(BUFFERSIZE);
    if (null != remainb) {
      if (remainb.length>recieveBuff.capacity()){
          byte[] remainb2=new byte[remainb.length%recieveBuff.capacity()];
          System.arraycopy(remainb,remainb.length-remainb2.length,remainb2,0,remainb2.length);
          remainb=remainb2;
      }
      recieveBuff.put(remainb);
      logger.warn("not a complete packet. remainb="+new String(remainb));
    }

    return recievePacket;
  }

  synchronized public BlockingQueue<byte[]> getRecievePacket() {
      return getRecievePacket(true);
  }

  /**
   * 获取处理后的数据包.
   *
   * @return BlockingQueue<byte[]> 每个元素是一个完整的数据包.
   */
  public BlockingQueue<byte[]> getSendPacket() {
      return sendPacket;
  }

  /**
   * 获取处理后的数据(<=81920).
   *
   * @return byte[] - 处理后的数据(<=81920).
   */
  synchronized public byte[] getSendBuff() {

    try {
        final int max=10;
        byte[][] sndPacketBuf=new byte[max][];
        int sumByte=0;
        for (int i=0; i<max && !sendPacket.isEmpty(); i++){
          sndPacketBuf[i]=sendPacket.take();

          /*
          byte[] buf = new byte[sndBuf.length*2+2];
          int bufSize = buf.length;
          int ret= ByteCodecRawMsg.encodeRawMsg(buf, bufSize, sndBuf, sndBuf.length);//编码: +FF
          if( ret > 0 ) {
              byte[] sndBuf2 = new byte[ret];
              System.arraycopy(buf,0,sndBuf2,0,ret);
              appendSend(sndBuf2);
          }else{
              logger.warn("encode error : ret="+ret+"; sndBuf="+new String(sndBuf));
          }
          */
          sumByte=sumByte+sndPacketBuf[i].length;
        }

        if (sumByte>0){
            byte[] sndBuf=new byte[sumByte];
            int pos=0;
            for (int i=0; i<max; i++){
                if (null!=sndPacketBuf[i]){
                    System.arraycopy(sndPacketBuf[i], 0, sndBuf, pos, sndPacketBuf[i].length);
                    pos=pos+sndPacketBuf[i].length;  //递加
                }
            }
            appendSend(sndBuf);
        }

    } catch (InterruptedException e) {
    }

    if (sendBuff.position()>0){
        sendBuff.flip();
    }
    byte[] msgb = byteBuffTobyte(sendBuff);
    if (null!=msgb && msgb.length>BUFFERSIZE){
        sendBuff = ByteBuffer.allocate(msgb.length);
        sendBuff.put(msgb,BUFFERSIZE,msgb.length-BUFFERSIZE);
        byte[] msgb2=new byte[BUFFERSIZE];
        System.arraycopy(msgb,0,msgb2,0,BUFFERSIZE);
        msgb=msgb2;
    }else{
        sendBuff = ByteBuffer.allocate(BUFFERSIZE);
    }

    return msgb;
  }

  /**
   * 追加待处理数据
   *
   * @param rcvb - byte[] 待处理数据
   *
   * @return int - 追加成功的字节
   */
  synchronized public int appendRecieve(byte[] rcvb) {
    if ((null == rcvb) || (rcvb.length < 1)) {
      return 0;
    }

    ByteBuffer recieveBuff2 = null;

    if (recieveBuff.remaining() < rcvb.length) {
      recieveBuff2 = ByteBuffer.allocate(recieveBuff.capacity() + rcvb.length);
      recieveBuff.flip();
      recieveBuff2.put(recieveBuff); //保存原来数据
      recieveBuff = recieveBuff2;
    }

    recieveBuff.put(rcvb);

    return rcvb.length;
  }

  /**
   * 追加待处理的数据包
   *
   * @param rcvb  - byte[] 待处理的数据包
   *
   * @return int - 追加成功的字节
   */
  public int appendRecievePacket(byte[] rcvb) {
    if ((null == rcvb) || (rcvb.length < 1)) {
      return 0;
    }

    try {
        recievePacket.put(rcvb);
    } catch (InterruptedException e) {
        return 0;
    }

    return rcvb.length;
  }

  /**
   * 追加处理后的数据
   *
   * @param sndb - byte[] 处理后的数据
   *
   * @return int - 追加成功的字节
   */
  synchronized public int appendSend(byte[] sndb) {
    if ((null == sndb) || (sndb.length < 1)) {
      return 0;
    }

    ByteBuffer sendBuff2 = null;

    if (sendBuff.remaining() < sndb.length) {
      sendBuff2 = ByteBuffer.allocate(sendBuff.capacity() + sndb.length);
      sendBuff.flip();
      sendBuff2.put(sendBuff); //保存原来数据
      sendBuff = sendBuff2;
    }

    sendBuff.put(sndb);

    return sndb.length;
  }

  /**
   * 追加处理后的数据包
   *
   * @param sndb - byte[] 处理后的数据包
   *
   * @return int - 追加成功的字节
   */
  public int appendSendPacket(byte[] sndb) {
    if ((null == sndb) || (sndb.length < 1)) {
      return 0;
    }

    try {
        sendPacket.put(sndb);
    } catch (InterruptedException e) {
        return 0;
    }

    return sndb.length;
  }

  /**
   * 把ByteBuffer转换为byte[]. 注意: 使用此方法前一定要调用byteBuff.flip()仅且一次.
   *
   * @param byteBuff - ByteBuffer
   * @return byte[] 没有数据返回null.
   */
  public static byte[] byteBuffTobyte(ByteBuffer byteBuff) {

    if ((null == byteBuff) || (byteBuff.limit() < 1) || (byteBuff.position()==0 && byteBuff.limit()==byteBuff.capacity())) {
      return null;
    }

    if (byteBuff.position()>0){
        byteBuff.flip();
    }
    byte[] bytes = new byte[byteBuff.limit()];
    int i = 0;

    while (byteBuff.hasRemaining()) {
      bytes[i] = byteBuff.get();
      i++;
    }

    return bytes;
  }


      /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("cotinueHandle=").append(cotinueHandle);
        sb.append("timeout=").append(timeout);
        sb.append("lastModified=").append(lastModified);
        sb.append("recieveBuff.limit=").append(recieveBuff.limit());
        sb.append("sendBuff.limit=").append(sendBuff.limit());
        sb.append("recievePacket.size=").append(recievePacket.size());
        sb.append("sendPacket.size=").append(sendPacket.size());
        return super.toString();
    }


    //释放资源
    public void clear(){
  	    this.cotinueHandle = false;
  		this.lastModified = -1;
  		this.recieveBuff.clear();
  		this.recieveBuff=null;
  		this.recievePacket.clear();
  		this.recievePacket=null;
  		this.sendBuff.clear();
  		this.sendBuff=null;
  		this.sendPacket.clear();
  		this.sendPacket=null;
  		this.timeout = 0;
    }

    @Override
	protected void finalize() throws Throwable {
		super.finalize();
		clear();
	}

/**
   * @param args
   */
  public static void main(String[] args) {
    byte[] data = (" !\"#$%&\'()*+," + String.valueOf(ByteCodecRawMsg.MEET_CHAR)+ "中国" + String.valueOf(ByteCodecRawMsg.MEET_CHAR) + "-./09:;<=>?@AZ[\\]^_`az{|}~").getBytes();
    System.out.println("data: len = " + data.length + "; data = " + new String(data));

    byte[] encodeData = new byte[data.length*2+2];
    int rtn = ByteCodecRawMsg.encodeRawMsg(encodeData,encodeData.length,data, data.length);

    byte[] rcvData = new byte[rtn]; //a packet
    System.arraycopy(encodeData,0,rcvData,0,rtn);
    System.out.println("rcvData: len = " + rcvData.length + "; rcvData = " + new String(rcvData));

    ByteRawMessage byteRawMessage = new ByteRawMessage();
    rtn=byteRawMessage.appendRecievePacket(data);  //ok
    System.out.println("appendRecieve rtn="+rtn);
    rcvData[0]='1';
    rtn=byteRawMessage.appendRecieve(rcvData);  //ok
    System.out.println("appendRecieve rtn="+rtn);
    rcvData[rtn-4]='0';
    rcvData[rtn-3]='0';
    rcvData[rtn-2]='0';
    rcvData[rtn-1]='0';
    rtn=byteRawMessage.appendRecieve(rcvData); //不完整的包
    System.out.println("appendRecieve rtn="+rtn);

    try {
        byte[] rcvPacket = null;
        BlockingQueue<byte[]> recievePacket = byteRawMessage.getRecievePacket();
        System.out.println("expected rcvPacket count : 2");
        while(!recievePacket.isEmpty()){
          rcvPacket = recievePacket.take();
          System.out.println("rcvPacket: len = " + rcvPacket.length + "; rcvPacket = " + new String(rcvPacket));
          byteRawMessage.appendSendPacket(rcvPacket);
        }

        System.out.println("expected sndbuf count : 2");
        byte[] sndbuf = byteRawMessage.getSendBuff();
        while(sndbuf!=null && sndbuf.length>0){
            System.out.println("sndbuf: len = " + sndbuf.length + "; sndbuf = " + new String(sndbuf));

            //编码
            byte[] encodeData2 = new byte[sndbuf.length*2+2];
            rtn = ByteCodecRawMsg.encodeRawMsg(encodeData2,encodeData2.length,sndbuf, sndbuf.length);
            byte[] rcvData2 = new byte[rtn]; //a packet
            System.arraycopy(encodeData2,0,rcvData2,0,rtn);

            byteRawMessage.appendRecieve(rcvData2);
            sndbuf = byteRawMessage.getSendBuff();
        }

        recievePacket = byteRawMessage.getRecievePacket();
        System.out.println("expected sndbuf count : 2, the first is lengther than the second.");
        while(!recievePacket.isEmpty()){
            rcvPacket = recievePacket.take();
            System.out.println("rcvPacket2: len = " + rcvPacket.length + "; rcvPacket = " + new String(rcvPacket));
          }

        ByteBuffer bb = ByteBuffer.allocate(10);
        byte[] b=ByteRawMessage.byteBuffTobyte(bb);
        System.out.println("null="+b);
        bb.put("123".getBytes());
        b=ByteRawMessage.byteBuffTobyte(bb);
        System.out.println("123="+new String(b));
        bb.flip();
        bb.flip();
        b=ByteRawMessage.byteBuffTobyte(bb);
        System.out.println("null="+b);

    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

  }

}
