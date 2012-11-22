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
*LAST UPDATED:: $Id: ByteRawMessage.java,v 1.15 2010/05/20 07:33:06 xiaoran27 Exp $
**
*PRINCIPAL AUTHOR:: Liangjiang Incorporation                                  *
**
*PRUCT NAME      ::
**
*LANGUAGE        :: ASNI                                                      *
**
*TARGET ENVIRONMENT:: JRE 1.5+                                                *
**
*DESCRIPTION     :: ����(byte)��������.
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
*   getSendBuff() һ�ζ�ȡ10��Ҫ���͵����ݰ�(�����д���ٶ�).
* V,Xiaoran27,2006-10-8 15:20
*   getSendBuff() �޸�posû�еݼӵĴ���.
* V,Xiaoran27,2006-10-16 15:38
*   + synchronized public BlockingQueue<byte[]> getRecievePacket(boolean codecIsFF)
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2006-4-3
* M getRecievePacket() ���û�յ����������null����
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2006-4-7
* M ԭ����lock, ��Ϊ�ڲ�����lock. ��Сlock��Χ
* M byteBuffTobyte(ByteBuffer)�Ӻ���lock, ʹ��ByteBuffer.get(byte[])
* M getRecievePacket() ��recieveBuff,������sendBuff
* M getSendBuff()  //no data, not deal
* M getSendBuff()  //no data, not deal �޸�����д������
* *-----------------------------------------------------------------------------*
* V,Xiaoran27,2007-5-15/30
* + public void decode()
* + public BlockingQueue<byte[]> getRecievePacketWithDecode()
* + public BlockingQueue<byte[]> getRecievePacketWithoutDecode()
* M public BlockingQueue<byte[]> getRecievePacket()
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2007-6-5/13
* +   synchronized public byte[] getRemainBytes()
* +   public BlockingQueue<byte[]> getRecievePacketWithMsgcFFDecode(BlockingQueue<String> )
* +   private SocketChannel sc = null;
      private boolean first=true;
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2007-7-1
* + clear()
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-1-3
* �޸�Ϊ�±���뷽��
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-1-4
* M �����㹻�ռ�
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-8-6
* +   private int timeout = 60*1000; //sc��ʱʱ�������룩��Ĭ�ϣ�60*1000
      private int lastModified = -1; //sc����дʱ���
* M  isCotinueHandle()
* + protected void finalize() throws Throwable
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-8-19
* + private int scType = 0; //socketChannel's type: 0-unknown
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-9-2
* M toString() + scType
*----------------------------------------------------------------------------*
* V,xiaoran27,2009-5-12
* M private long timeout = 60*60*1000;
*----------------------------------------------------------------------------*
* V,xiaoran27,2010-5-20
* M �����ж�socket�Ƿ�ʱ
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.15 $
  */
public class ByteRawMessage {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(ByteRawMessage.class);

  /**
   * DOCUMENT ME!
   */
  private static int BUFFERSIZE = 81920;

  private static int PACKET_QUEUE_SIZE = 10000;

  private long timeout = 60*60*1000; //sc��ʱʱ�������룩��Ĭ�ϣ�60*60*1000
  private long lastModified = System.currentTimeMillis(); //sc����дʱ���

  /*��ͬ��socket���ܴ����г���*/
  private int scType = 0; //socketChannel's type: 0-unknown

  /**
   * �Ƿ������������
   */
  private boolean cotinueHandle = true;

  /**
   * �Ѿ�ȷ�ϵ�����
   */
  private SocketChannel sc = null;

  /**
   * �Ƿ�������
   */
  private boolean first=true;

  /**
   * ��Ϣ��ʽ
   */
  private String msgFormat = "msgc";

  /**
   * ���������־,����msgFormat = "msgc"��Ч
   */
  private boolean codecIsFF = true;

  /**
   * ��Ŵ���������(δ����)
   */
  private ByteBuffer recieveBuff = ByteBuffer.allocate(BUFFERSIZE);

  /**
   * ��ŴӴ����������з����������ݰ�(�ѽ���)
   */
  //private BlockingQueue<byte[]> recievePacket = new LinkedBlockingQueue<byte[]>();
  private BlockingQueue<byte[]> recievePacket = new ArrayBlockingQueue<byte[]>(PACKET_QUEUE_SIZE);

  /**
   * �����͵�����(��������)
   */
  private ByteBuffer sendBuff = ByteBuffer.allocate(BUFFERSIZE);

  /**
   * ��Ŵ����͵�ԭʼ���ݰ�(δ����)
   */
  //private BlockingQueue<byte[]> sendPacket = new LinkedBlockingQueue<byte[]>(PACKET_QUEUE_SIZE);
  private BlockingQueue<byte[]> sendPacket = new ArrayBlockingQueue<byte[]>(PACKET_QUEUE_SIZE);

  /**
   * Creates a new ByteRawMessage object.
   */
  public ByteRawMessage() {
  }

  /**
   * Creates a new ByteRawMessage object.
   *
   * @param msgFormat DOCUMENT ME!
   */
  public ByteRawMessage(String msgFormat) {
    this.msgFormat = msgFormat;
  }

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
	//sc�Ƿ���Ч���Ƿ�ʱ
    boolean notContinue = null==sc || !sc.isConnected()
        || !sc.isOpen()
        ||  sc.socket().isClosed()
        || !sc.socket().isConnected()
        || sc.socket().isInputShutdown()
        || sc.socket().isOutputShutdown()
        //|| (System.currentTimeMillis() - lastModified) > timeout
        ;

    return cotinueHandle && !notContinue;
    //return cotinueHandle && null!=sc && sc.isConnected() && sc.isOpen();
  }

  /**
  * @return the codecIsFF
  */
  public boolean isCodecIsFF() {
    return codecIsFF;
  }

  /**
   * @param codecIsFF the codecIsFF to set
   */
  public void setCodecIsFF(boolean codecIsFF) {
    this.codecIsFF = codecIsFF;
  }

   public int getScType() {
	return scType;
   }

   public void setScType(int scType) {
	this.scType = scType;
   }

   /**
   * @return the msgFormat
   */
  public String getMsgFormat() {
    return msgFormat;
  }

  /**
     * @param cotinueHandle The cotinueHandle to set.
     */
  synchronized public void setCotinueHandle(boolean cotinueHandle) {
    this.cotinueHandle = cotinueHandle;
  }

  synchronized public byte[] getRemainBytes() {
      if ((null == recieveBuff) || (recieveBuff.limit() < 1) || (recieveBuff.position()==0 && recieveBuff.limit()==recieveBuff.capacity())) {
          return new byte[0];
      }

      if (recieveBuff.position()>0){
          recieveBuff.flip();
        }

        byte[] bytes = new byte[recieveBuff.limit()];
        recieveBuff.get(bytes);  //�Ż�
        recieveBuff.clear();

        return bytes;
  }

  /**
   * ��ByteBufferת��Ϊbyte[]. ע��: ʹ�ô˷���ǰһ��Ҫ����byteBuff.flip()����һ��.
   *
   * @param byteBuff - ByteBuffer
   * @return byte[] û�����ݷ���null.
   */
  public static byte[] byteBuffTobyte(ByteBuffer byteBuff) {

    if ((null == byteBuff) || (byteBuff.limit() < 1) || (byteBuff.position()==0 && byteBuff.limit()==byteBuff.capacity())) {
      return null;
    }

    if (byteBuff.position()>0){
        byteBuff.flip();
    }
    byte[] bytes = new byte[byteBuff.limit()];
    byteBuff.get(bytes);  //�Ż�
    byteBuff.clear();

    /*
    int i = 0;

    while (byteBuff.hasRemaining()) {
      bytes[i] = byteBuff.get();
      i++;
    }
    */

    return bytes;
  }

  /**
   * ��ȡ���������ݰ�(�ѽ���)�Ķ���(��������).
   *
   * @return BlockingQueue<byte[]> ÿ��Ԫ����һ�����������ݰ�(�ѽ���).
   */
  public BlockingQueue<byte[]> getRecievePacketWithoutDecode() {
      return recievePacket;
  }

  /**
   * ��ȡ���������ݰ�(�ѽ���)�Ķ���(û���ݰ�,���н���).
   *
   * @return BlockingQueue<byte[]> ÿ��Ԫ����һ�����������ݰ�(�ѽ���).
   */
  public BlockingQueue<byte[]> getRecievePacket(){

       if (recievePacket.size()>0){
           return recievePacket;  //��δ����İ�ֱ�ӷ���
       }

      return getRecievePacketWithDecode();
  }

  /**
   * ��ȡ���������ݰ�(�ѽ���)�Ķ���(û���ݰ�,���н���).
   *
   * @return BlockingQueue<byte[]> ÿ��Ԫ����һ�����������ݰ�(�ѽ���).
   */
   public BlockingQueue<byte[]> getRecievePacketWithDecode() {

       long old = System.currentTimeMillis();
       synchronized(recieveBuff){
           if (recieveBuff.position() > 0) {
             recieveBuff.flip();
           }

           byte[] msgb = byteBuffTobyte(recieveBuff);
           if (null==msgb || msgb.length<1 ){  //���û�յ����������null����
               return recievePacket;
           }

           byte[] remainb = new ByteCodecRawMsg(msgFormat, codecIsFF).decodeMessagePacket(msgb, recievePacket); //����
           if (null != remainb && remainb.length>0) {
             recieveBuff.put(remainb);
           }

        }
       if (logger.isInfoEnabled() ){
           logger.info("getRecievePacket() - cost time(ms): "+(System.currentTimeMillis()-old));
       }

       lastModified = System.currentTimeMillis(); //sc����дʱ���

      return recievePacket;

  }

   /**
    * ��ȡ���������ݰ�(�ѽ���)�Ķ���(û���ݰ�,���н���), ͬʱ����֪ͨ����.
    *
    * @return BlockingQueue<byte[]> ÿ��Ԫ����һ�����������ݰ�(�ѽ���).
    */
    public BlockingQueue<byte[]> getRecievePacketWithMsgcFFDecode(BlockingQueue<String> packetDataArrivedKeyQueue) {

        long old = System.currentTimeMillis();
        synchronized(recieveBuff){
            if (recieveBuff.position() > 0) {
              recieveBuff.flip();
            }

            byte[] msgb = byteBuffTobyte(recieveBuff);
            if (null==msgb || msgb.length<1 ){  //���û�յ����������null����
                return recievePacket;
            }

            byte[] remainb = new ByteCodecRawMsg(msgFormat, codecIsFF).decodeMessagePacket(msgb, recievePacket, packetDataArrivedKeyQueue, sc.toString()); //����
            if (null != remainb && remainb.length>0) {
              recieveBuff.put(remainb);
            }
         }
        if (logger.isInfoEnabled() ){
            logger.info("getRecievePacket() - cost time(ms): "+(System.currentTimeMillis()-old));
        }

        lastModified = System.currentTimeMillis(); //sc����дʱ���

       return recievePacket;

   }

  /**
   * ����
   */
   public void decode(){
       synchronized(recieveBuff){
          if (recieveBuff.position() > 0) {
            recieveBuff.flip();
          }

          byte[] msgb = byteBuffTobyte(recieveBuff);
          if (null==msgb || msgb.length<1 ){  //���û�յ����������null����
              return ;
          }

          //ÿ�ν�һ����
          byte[] remainb = new ByteCodecRawMsg(msgFormat, codecIsFF).decodeMessagePacket( msgb, recievePacket); //����
          recieveBuff.clear();
          if (null != remainb && remainb.length>0) {
            recieveBuff.put(remainb);
          }


          lastModified = System.currentTimeMillis(); //sc����дʱ���

       }
   }

  /**
   * ��ȡ���������ݰ�(δ����)����.
   *
   * @return BlockingQueue<byte[]> ÿ��Ԫ����һ�����������ݰ�(δ����).
   */
  public BlockingQueue<byte[]> getSendPacket() {
    return sendPacket;
  }

  /**
   * ��ȡ����������(�ѱ���,����<=81920).
   *
   * @return byte[] - ����������(�ѱ���,����<=81920).
   */
   public byte[] getSendBuff() {
     if (!sendPacket.isEmpty() || sendPacket.size()>0){  //no data, not deal
        try {
            byte[] sndPacketb = sendPacket.take();
            byte[] sndPacket = new ByteCodecRawMsg(msgFormat, codecIsFF).encodeMessagePacket(sndPacketb); //����

            appendSend(sndPacket);

        } catch (InterruptedException e) {
        }
     }

    byte[] msgb = null;
    synchronized(sendBuff){
        if (sendBuff.position() > 0) {
          sendBuff.flip();
        }

        msgb = byteBuffTobyte(sendBuff);

        if ((null != msgb) && (msgb.length > BUFFERSIZE)) {
          sendBuff.put(msgb, BUFFERSIZE, msgb.length - BUFFERSIZE);

          byte[] msgb2 = new byte[BUFFERSIZE];
          System.arraycopy(msgb, 0, msgb2, 0, BUFFERSIZE);
          msgb = msgb2;
        }
    }

    lastModified = System.currentTimeMillis(); //sc����дʱ���

    return msgb;
  }

  /**
   * ׷�Ӵ���������(δ����)
   *
   * @param rcvb - byte[] ����������(δ����)
   *
   * @return int - ׷�ӳɹ����ֽ�
   */
   public int appendRecieve(byte[] rcvb) {

    if ((null == rcvb) || (rcvb.length < 1)) {
      return 0;
    }

    ByteBuffer recieveBuff2 = null;
    synchronized(recieveBuff){
        if (recieveBuff.remaining() < rcvb.length) {
          recieveBuff2 = ByteBuffer.allocate(recieveBuff.capacity() + BUFFERSIZE);  //�����㹻�ռ�
          recieveBuff.flip();
          recieveBuff2.put(recieveBuff); //����ԭ������
          recieveBuff = recieveBuff2;
        }

        recieveBuff.put(rcvb);

    }

    lastModified = System.currentTimeMillis(); //sc����дʱ���

    return rcvb.length;
  }

  /**
   * ׷�Ӵ���������ݰ�(�ѽ���)
   *
   * @param rcvb  - byte[] ����������ݰ�(�ѽ���)
   *
   * @return int - ׷�ӳɹ����ֽ�
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

    lastModified = System.currentTimeMillis(); //sc����дʱ���

    return rcvb.length;
  }

  /**
   * ׷�Ӵ����͵�����(�ѱ���)
   *
   * @param sndb - byte[] �����͵�����(�ѱ���)
   *
   * @return int - ׷�ӳɹ����ֽ�
   */
  public int appendSend(byte[] sndb) {
    if ((null == sndb) || (sndb.length < 1)) {
      return 0;
    }

    ByteBuffer sendBuff2 = null;
    synchronized(sendBuff){
        if (sendBuff.remaining() < sndb.length) {
          sendBuff2 = ByteBuffer.allocate(sendBuff.capacity() + Math.max(sndb.length,BUFFERSIZE));  //overflow
          sendBuff.flip();
          sendBuff2.put(sendBuff); //����ԭ������
          sendBuff = sendBuff2;
        }

        sendBuff.put(sndb);
    }

    lastModified = System.currentTimeMillis(); //sc����дʱ���

    return sndb.length;
  }

  /**
   * ׷�Ӵ����͵����ݰ�(δ����)
   *
   * @param sndb - byte[] �����͵����ݰ�(δ����)
   *
   * @return int - ׷�ӳɹ����ֽ�
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

    lastModified = System.currentTimeMillis(); //sc����дʱ���

    return sndb.length;
  }


  public int getRecievePacketSize(){
      return recievePacket.size();
  }

  public int getSendPacketSize(){
      return sendPacket.size();
  }

  public SocketChannel getSc() {
    return sc;
  }

    public void setSc(SocketChannel sc) {
        this.sc = sc;
    }

   public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    @Override
	protected void finalize() throws Throwable {
		super.finalize();
		clear();
	}

	//�ͷ���Դ
    public void clear(){
      this.cotinueHandle = false;
      if (this.recieveBuff!=null){
          this.recieveBuff.clear();
      }
      if (this.recievePacket!=null){
          this.recievePacket.clear();
      }
      if (this.sendBuff!=null){
          this.sendBuff.clear();
      }
      if (this.sendPacket!=null){
          this.sendPacket.clear();
      }
      if (this.sc!=null){
          try {
            this.sc.close();
            this.sc.socket().close();
        } catch (IOException e) {
        }
      }
    }


  /**
   * @param args
   */
  public static void main(String[] args) {

  }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .appendSuper(super.toString()).append("recievePacket.size()",
                        this.recievePacket.size()).append("msgFormat",
                        this.msgFormat).append("sendPacket.size()",
                        this.sendPacket.size()).append("sendBuff.position()",
                        this.sendBuff.position()).append("cotinueHandle",
                        this.cotinueHandle).append("recieveBuff.position()",
                        this.recieveBuff.position()).append("codecIsFF",
                        this.codecIsFF).append("timeout",
                        this.timeout).append("lastModified",
                        this.lastModified).append("scType",
                                this.scType).toString();
    }
}
