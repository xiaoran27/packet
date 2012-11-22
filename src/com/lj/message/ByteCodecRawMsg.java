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
* M decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) 少1byte赋值
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-4-8
* M 在IMessage iMessage = MessageFactory.createMessage(msgFormat);后加
*      iMessage.setCodecIsFF(codecIsFF);
* M byteToHex(...) @Deprecated
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-5-26
* M iMessage作为成员实例
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-6-5/13
* + decodeMessagePacket(String , byte[] ,
    BlockingQueue<byte[]> , boolean ,BlockingQueue<String> , String )
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-1-3
* 废除static方法(@Deprecated),防止多线程出现问题
\*************************** END OF CHANGE REPORT HISTORY ********************/
package com.lj.message;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.lj.utils.HexUtil;


/**
 * 对原始数据包进行编码/解码.
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
   * 对原始数据包进行编码.
   *
   * @param sndPacketBuf byte[] - 一个完整的数据包(未编码)
   *
   * @return 编码后的数据包
   */
  public byte[] encodeMessagePacket( byte[] sndPacketBuf) {
    return iMessage.encodeMessagePacket(sndPacketBuf);
  }

  /**
   * 对原始数据进行解码.
   *
   * @param rcvBuf[] - 原始数据(未解码)
   * @param recievePacket BlockingQueue<byte[]> - 存放数据包(已解码)的队列
   *
   * @return 未解码的数据(不够完整的包)
   */
  public byte[] decodeMessagePacket(byte[] rcvBuf,
    BlockingQueue<byte[]> recievePacket) {
    byte[] remainb = new byte[rcvBuf.length];
    System.arraycopy(rcvBuf, 0, remainb, 0, remainb.length);

    long old = System.currentTimeMillis();
    int end = parseMessagePacket(remainb);
    while (end > 0) {
      end ++; //整包长度

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //完整的包(未解码)

      int tag = iMessage.checkMessagePacket(msgb,iMessage.isCodecIsFF());

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //完整的包(已解码)
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
        break; //没有完整的包,退出
      }

      byte[] remainb2 = new byte[remainb.length - end];
      System.arraycopy(remainb, end, remainb2, 0, remainb2.length);
      remainb = remainb2;

      if (logger.isInfoEnabled() ){
          logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - cost time(ms): "+(System.currentTimeMillis()-old));
      }

//      if (tag>=0){
//          break;  //解了一个包就退出,否则,解包时间长会增加锁的时间
//      }

      old = System.currentTimeMillis();
      end = parseMessagePacket( remainb );
    }

    return remainb;
  }

  /**
   * 对原始数据进行解码. 解码后通知处理线程有数据包需要处理
   *
   * @param rcvBuf[] - 原始数据(未解码)
   * @param recievePacket BlockingQueue<byte[]> - 存放数据包(已解码)的队列
   * @param packetDataArrivedKeyQueue BlockingQueue<String> - 数据包存在通知
   * @param scKey String - 连接的标识
   *
   * @return 未解码的数据(不够完整的包)
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
      end ++; //整包长度

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //完整的包(未解码)

      int tag = iMessage.checkMessagePacket(msgb,iMessage.isCodecIsFF());

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //完整的包(已解码)
          recievePacket.put(packet);
          packetDataArrivedKeyQueue.put(scKey);  //通知处理线程
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
        break; //没有完整的包,退出
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
   * 对原始数据进行分析校验, 分离一个完整的包(未解码)的位置.
   *
   * @param rcvBuf[] - 原始数据(未解码)
   *
   * @return int 实际第一个完整的包(未解码)的结束位置: 0-没有完成的数据包, >0-包结束位置
   */
  public int parseMessagePacket(byte[] rcvBuf) {
    return iMessage.parseMessage(rcvBuf,iMessage.isCodecIsFF());
  }

  public IMessage getIMessage() {
    return iMessage;
  }


  /**
   * 对原始数据包进行编码.
   *
   * @param msgFormat String - 消息编码方式
   * @param sndPacketBuf byte[] - 一个完整的数据包(未编码)
   * @param codecIsFF boolean - 是否FFFF编码方式: true-是,false-否
   *
   * @return 编码后的数据包
   */
  @Deprecated
  public static byte[] encodeMessagePacket(String msgFormat,
    byte[] sndPacketBuf, boolean codecIsFF) {
    IMessage  iMessage = MessageFactory.createMessage(msgFormat);
    iMessage.setCodecIsFF(codecIsFF);

    return iMessage.encodeMessagePacket(sndPacketBuf);
  }

  /**
   * 对原始数据进行解码.
   *
   * @param msgFormat String - 消息编码方式
   * @param rcvBuf[] - 原始数据(未解码)
   * @param recievePacket BlockingQueue<byte[]> - 存放数据包(已解码)的队列
   * @param codecIsFF boolean - 是否FFFF编码方式: true-是,false-否
   *
   * @return 未解码的数据(不够完整的包)
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
      end ++; //整包长度

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //完整的包(未解码)

      int tag = iMessage.checkMessagePacket(msgb, codecIsFF);

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //完整的包(已解码)
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
        break; //没有完整的包,退出
      }

      byte[] remainb2 = new byte[remainb.length - end];
      System.arraycopy(remainb, end, remainb2, 0, remainb2.length);
      remainb = remainb2;

      if (logger.isInfoEnabled() ){
          logger.info("decodeMessagePacket(String , byte[] , BlockingQueue<byte[]> , boolean ) - cost time(ms): "+(System.currentTimeMillis()-old));
      }

//      if (tag>=0){
//          break;  //解了一个包就退出,否则,解包时间长会增加锁的时间
//      }

      old = System.currentTimeMillis();
      end = parseMessagePacket(msgFormat, remainb, codecIsFF);
    }

    return remainb;
  }

  /**
   * 对原始数据进行解码. 解码后通知处理线程有数据包需要处理
   *
   * @param msgFormat String - 消息编码方式
   * @param rcvBuf[] - 原始数据(未解码)
   * @param recievePacket BlockingQueue<byte[]> - 存放数据包(已解码)的队列
   * @param codecIsFF boolean - 是否FFFF编码方式: true-是,false-否
   * @param packetDataArrivedKeyQueue BlockingQueue<String> - 数据包存在通知
   * @param scKey String - 连接的标识
   *
   * @return 未解码的数据(不够完整的包)
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
      end ++; //整包长度

      byte[] msgb = new byte[end];
      System.arraycopy(remainb, 0, msgb, 0, msgb.length); //完整的包(未解码)

      int tag = iMessage.checkMessagePacket(msgb, codecIsFF);

      if (tag >= 0) {
        try {
          byte[] packet = iMessage.decodeMessagePacket(msgb); //完整的包(已解码)
          recievePacket.put(packet);
          packetDataArrivedKeyQueue.put(scKey);  //通知处理线程
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
        break; //没有完整的包,退出
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
   * 对原始数据进行分析校验, 分离一个完整的包(未解码)的位置.
   *
   * @param msgFormat String - 消息编码方式
   * @param rcvBuf[] - 原始数据(未解码)
   * @param codecIsFF boolean - 是否FFFF编码方式: true-是,false-否
   *
   * @return int 实际第一个完整的包(未解码)的结束位置: 0-没有完成的数据包, >0-包结束位置
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
