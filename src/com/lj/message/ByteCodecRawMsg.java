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
*LAST UPDATED:: $Id: ByteCodecRawMsg.java,v 1.7 2008/01/04 05:42:03 xiaoran27 Exp $
**
*PRINCIPAL AUTHOR:: Liangjiang Incorporation                                  *
**
*PRUCT NAME      :: PleaseFillProductName
**
*LANGUAGE        :: ASNI                                                      *
**
*TARGET ENVIRONMENT:: JRE 1.5+                                                *
**
*DESCRIPTION     :: PleaseFill
**
*************************** END OF SOURCE FILE INFORMATION ********************

************************* CHANGE REPORT HISTORY *******************************
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-3-12
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-3-15
* + byteToHex(byte[] b, int size, boolean left)
* + byteToHex(byte[] b)
* M byteToHex(byte[] b, int size)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-4-6
* M decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) ��1byte��ֵ
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-4-8
* M ��IMessage iMessage = MessageFactory.createMessage(msgFormat);���
*      iMessage.setCodecIsFF(codecIsFF);
* M byteToHex(...) @Deprecated
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-5-26
* M iMessage��Ϊ��Աʵ��
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-6-5/13
* + decodeMessagePacket(String , byte[] ,
    BlockingQueue<byte[]> , boolean ,BlockingQueue<String> , String )
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-1-3
* �ϳ�static����(@Deprecated),��ֹ���̳߳�������
\*************************** END OF CHANGE REPORT HISTORY ********************/
package com.lj.message;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.lj.utils.HexUtil;


/**
 * ��ԭʼ���ݰ����б���/����.
 *
 * @author $author$
 * @version $Revision: 1.7 $
  */
public final class ByteCodecRawMsg {
  /**
   * DOCUMENT ME!
   */
  private static final Logger logger = Logger.getLogger(ByteCodecRawMsg.class);

  private IMessage iMessage = null;

  /**
   * Creates a new ByteCodecRawMsg object.
   */
  public ByteCodecRawMsg(String msgFormat, boolean codecIsFF) {
      iMessage = MessageFactory.createMessage(msgFormat);
      iMessage.setCodecIsFF(codecIsFF);
  }

  public ByteCodecRawMsg() {
      this("msgc",true);
  }

  /**
   * ��ԭʼ���ݰ����б���.
   *
   * @param sndPacketBuf byte[] - һ�����������ݰ�(δ����)
   *
   * @return ���������ݰ�
   */
  public byte[] encodeMessagePacket( byte[] sndPacketBuf) {
    return iMessage.encodeMessagePacket(sndPacketBuf);
  }

  /**
   * ��ԭʼ���ݽ��н���.
   *
   * @param rcvBuf[] - ԭʼ����(δ����)
   * @param recievePacket BlockingQueue<byte[]> - ������ݰ�(�ѽ���)�Ķ���
   *
   * @return δ���������(���������İ�)
   */
  public byte[] decodeMessagePacket(byte[] rcvBuf,
    BlockingQueue<byte[]> recievePacket) {
    byte[] remainb = new byte[rcvBuf.length];
    System.arraycopy(rcvBuf, 0, remainb, 0, remainb.length);

    long old = System.currentTimeMillis();
    int end = parseMessagePacket(remainb);
    while (end > 0) {
      end ++; //��������

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //�����İ�(δ����)

      int tag = iMessage.checkMessagePacket(msgb,iMessage.isCodecIsFF());

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //�����İ�(�ѽ���)
          recievePacket.put(packet);
        } catch (Exception e) {
          logger.error(
            "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - Exception e=" +
            e, e);
        }
      } else {
        logger.warn(
          "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - discard: " +
          HexUtil.byteToHex(msgb, msgb.length));
        break; //û�������İ�,�˳�
      }

      byte[] remainb2 = new byte[remainb.length - end];
      System.arraycopy(remainb, end, remainb2, 0, remainb2.length);
      remainb = remainb2;

      if (logger.isInfoEnabled() ){
          logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - cost time(ms): "+(System.currentTimeMillis()-old));
      }

//      if (tag>=0){
//          break;  //����һ�������˳�,����,���ʱ�䳤����������ʱ��
//      }

      old = System.currentTimeMillis();
      end = parseMessagePacket( remainb );
    }

    return remainb;
  }

  /**
   * ��ԭʼ���ݽ��н���. �����֪ͨ�����߳������ݰ���Ҫ����
   *
   * @param rcvBuf[] - ԭʼ����(δ����)
   * @param recievePacket BlockingQueue<byte[]> - ������ݰ�(�ѽ���)�Ķ���
   * @param packetDataArrivedKeyQueue BlockingQueue<String> - ���ݰ�����֪ͨ
   * @param scKey String - ���ӵı�ʶ
   *
   * @return δ���������(���������İ�)
   */
  public byte[] decodeMessagePacket( byte[] rcvBuf,
    BlockingQueue<byte[]> recievePacket, BlockingQueue<String> packetDataArrivedKeyQueue, String scKey) {
    byte[] remainb = new byte[rcvBuf.length];
    System.arraycopy(rcvBuf, 0, remainb, 0, remainb.length);

    int count=0;
    long old = System.currentTimeMillis();
    long old0 = old;
    int end = parseMessagePacket( remainb );
    while (end > 0) {
      end ++; //��������

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //�����İ�(δ����)

      int tag = iMessage.checkMessagePacket(msgb,iMessage.isCodecIsFF());

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //�����İ�(�ѽ���)
          recievePacket.put(packet);
          packetDataArrivedKeyQueue.put(scKey);  //֪ͨ�����߳�
          count ++;

        } catch (Exception e) {
          logger.error(
            "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - Exception e=" +
            e, e);
        }
      } else {
        logger.warn(
          "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - discard: " +
          HexUtil.byteToHex(msgb, msgb.length));
        break; //û�������İ�,�˳�
      }

      byte[] remainb2 = new byte[remainb.length - end];
      System.arraycopy(remainb, end, remainb2, 0, remainb2.length);
      remainb = remainb2;

      if (logger.isInfoEnabled() ){
          logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - cost time(ms): "+(System.currentTimeMillis()-old));
      }

      old = System.currentTimeMillis();
      end = parseMessagePacket( remainb );
    }

    if (logger.isInfoEnabled() ){
        logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - all cost time(ms): "+(System.currentTimeMillis()-old0)+"; packet.size="+count);
    }

    return remainb;
  }

  /**
   * ��ԭʼ���ݽ��з���У��, ����һ�������İ�(δ����)��λ��.
   *
   * @param rcvBuf[] - ԭʼ����(δ����)
   *
   * @return int ʵ�ʵ�һ�������İ�(δ����)�Ľ���λ��: 0-û����ɵ����ݰ�, >0-������λ��
   */
  public int parseMessagePacket(byte[] rcvBuf) {
    return iMessage.parseMessage(rcvBuf,iMessage.isCodecIsFF());
  }

  public IMessage getIMessage() {
    return iMessage;
  }


  /**
   * ��ԭʼ���ݰ����б���.
   *
   * @param msgFormat String - ��Ϣ���뷽ʽ
   * @param sndPacketBuf byte[] - һ�����������ݰ�(δ����)
   * @param codecIsFF boolean - �Ƿ�FFFF���뷽ʽ: true-��,false-��
   *
   * @return ���������ݰ�
   */
  @Deprecated
  public static byte[] encodeMessagePacket(String msgFormat,
    byte[] sndPacketBuf, boolean codecIsFF) {
    IMessage  iMessage = MessageFactory.createMessage(msgFormat);
    iMessage.setCodecIsFF(codecIsFF);

    return iMessage.encodeMessagePacket(sndPacketBuf);
  }

  /**
   * ��ԭʼ���ݽ��н���.
   *
   * @param msgFormat String - ��Ϣ���뷽ʽ
   * @param rcvBuf[] - ԭʼ����(δ����)
   * @param recievePacket BlockingQueue<byte[]> - ������ݰ�(�ѽ���)�Ķ���
   * @param codecIsFF boolean - �Ƿ�FFFF���뷽ʽ: true-��,false-��
   *
   * @return δ���������(���������İ�)
   */
  @Deprecated
  public static byte[] decodeMessagePacket(String msgFormat, byte[] rcvBuf,
    BlockingQueue<byte[]> recievePacket, boolean codecIsFF) {
    byte[] remainb = new byte[rcvBuf.length];
    System.arraycopy(rcvBuf, 0, remainb, 0, remainb.length);

    IMessage  iMessage = MessageFactory.createMessage(msgFormat);
    iMessage.setCodecIsFF(codecIsFF);

    long old = System.currentTimeMillis();
    int end = parseMessagePacket(msgFormat, remainb, codecIsFF);
    while (end > 0) {
      end ++; //��������

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //�����İ�(δ����)

      int tag = iMessage.checkMessagePacket(msgb, codecIsFF);

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //�����İ�(�ѽ���)
          recievePacket.put(packet);
        } catch (Exception e) {
          logger.error(
            "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - Exception e=" +
            e, e);
        }
      } else {
        logger.warn(
          "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - discard: " +
          HexUtil.byteToHex(msgb, msgb.length));
        break; //û�������İ�,�˳�
      }

      byte[] remainb2 = new byte[remainb.length - end];
      System.arraycopy(remainb, end, remainb2, 0, remainb2.length);
      remainb = remainb2;

      if (logger.isInfoEnabled() ){
          logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - cost time(ms): "+(System.currentTimeMillis()-old));
      }

//      if (tag>=0){
//          break;  //����һ�������˳�,����,���ʱ�䳤����������ʱ��
//      }

      old = System.currentTimeMillis();
      end = parseMessagePacket(msgFormat, remainb, codecIsFF);
    }

    return remainb;
  }

  /**
   * ��ԭʼ���ݽ��н���. �����֪ͨ�����߳������ݰ���Ҫ����
   *
   * @param msgFormat String - ��Ϣ���뷽ʽ
   * @param rcvBuf[] - ԭʼ����(δ����)
   * @param recievePacket BlockingQueue<byte[]> - ������ݰ�(�ѽ���)�Ķ���
   * @param codecIsFF boolean - �Ƿ�FFFF���뷽ʽ: true-��,false-��
   * @param packetDataArrivedKeyQueue BlockingQueue<String> - ���ݰ�����֪ͨ
   * @param scKey String - ���ӵı�ʶ
   *
   * @return δ���������(���������İ�)
   */
  @Deprecated
  public static byte[] decodeMessagePacket(String msgFormat, byte[] rcvBuf,
    BlockingQueue<byte[]> recievePacket, boolean codecIsFF,BlockingQueue<String> packetDataArrivedKeyQueue, String scKey) {
    byte[] remainb = new byte[rcvBuf.length];
    System.arraycopy(rcvBuf, 0, remainb, 0, remainb.length);

    IMessage  iMessage = MessageFactory.createMessage(msgFormat);
    iMessage.setCodecIsFF(codecIsFF);

    int count=0;
    long old = System.currentTimeMillis();
    long old0 = old;
    int end = parseMessagePacket(msgFormat, remainb, codecIsFF);
    while (end > 0) {
      end ++; //��������

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //�����İ�(δ����)

      int tag = iMessage.checkMessagePacket(msgb, codecIsFF);

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //�����İ�(�ѽ���)
          recievePacket.put(packet);
          packetDataArrivedKeyQueue.put(scKey);  //֪ͨ�����߳�
          count ++;

        } catch (Exception e) {
          logger.error(
            "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - Exception e=" +
            e, e);
        }
      } else {
        logger.warn(
          "decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - discard: " +
          HexUtil.byteToHex(msgb, msgb.length));
        break; //û�������İ�,�˳�
      }

      byte[] remainb2 = new byte[remainb.length - end];
      System.arraycopy(remainb, end, remainb2, 0, remainb2.length);
      remainb = remainb2;

      if (logger.isInfoEnabled() ){
          logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - cost time(ms): "+(System.currentTimeMillis()-old));
      }

      old = System.currentTimeMillis();
      end = parseMessagePacket(msgFormat, remainb, codecIsFF);
    }

    if (logger.isInfoEnabled() ){
        logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean, BlockingQueue<String>, String ) - all cost time(ms): "+(System.currentTimeMillis()-old0)+"; packet.size="+count);
    }

    return remainb;
  }

  /**
   * ��ԭʼ���ݽ��з���У��, ����һ�������İ�(δ����)��λ��.
   *
   * @param msgFormat String - ��Ϣ���뷽ʽ
   * @param rcvBuf[] - ԭʼ����(δ����)
   * @param codecIsFF boolean - �Ƿ�FFFF���뷽ʽ: true-��,false-��
   *
   * @return int ʵ�ʵ�һ�������İ�(δ����)�Ľ���λ��: 0-û����ɵ����ݰ�, >0-������λ��
   */
  @Deprecated
  public static int parseMessagePacket(String msgFormat, byte[] rcvBuf,
    boolean codecIsFF) {
      IMessage  iMessage = MessageFactory.createMessage(msgFormat);
      iMessage.setCodecIsFF(codecIsFF);
    return iMessage.parseMessage(rcvBuf, codecIsFF);
  }


/**
   * DOCUMENT ME!
   *
   * @param args DOCUMENT ME!
   */
  public static void main(String[] args) {
  }
}
