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
*LAST UPDATED:: $Id: IMessage.java,v 1.2 2007/03/16 08:48:51 xiaoran27 Exp $
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
* V,xiaoran27,2006-3-13
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-3-16
* + get/setMessageId, get/setMessageType
\*************************** END OF CHANGE REPORT HISTORY ********************/
package com.lj.message;


/**
 * 对收发原始数据进行分析编码/解码处理
 *
 * @author $author$
 * @version $Revision: 1.2 $
  */
public interface IMessage {
  /**
   * 最大数据缓存区大小
   */
  public static int SOCKET_BUFFER_MAX = 8192*10;

  /**
   * 消息中的特殊字符
   */
  public static byte MEET_CHAR = (byte) 0xff;

  /**
   * 消息填充字符
   */
  public static byte FILL_CHAR = (byte) 0x00;

  /**
    * 字符集编码
    */
  public static String DEFUALT_ENCODING = "UTF-8";

  /**
   * 消息编码是否FFFF结束. 默认:false
   * FFFF结束编码: 遇MEET_CHAR追加一个FILL_CHAR字节,末尾加两个MEET_CHAR字节
   */
  public static boolean DEFUALT_CODECISFF = false;

  /**
   * 取字符集编码
   *
   * @return 字符集编码
   */
  public String getEncoding();

  /**
   * 设置字符集编码
   *
   * @param encoding 字符集编码
   */
  public void setEncoding(String encoding);

  /**
   * 取消息的类型
   *
   * @return 消息的类型
   */
  public int getMessageType();

  /**
   * 设置消息的类型
   *
   * @param messageType 消息的类型
   *
   */
  public void setMessageType(int messageType);

  /**
   * 取消息的id
   *
   * @return 消息的id
   */
  public int getMessageId();

  /**
   * 设置消息的id
   *
   * @param messageId 消息的id
   *
   */
  public void setMessageId(int messageId);

  /**
   * 检查消息数据的合法性.
   *
   * @param data 实际数据
   *
   * @return int 返回消息标识[0,255], 否则,返回<0.
   */
  public int checkMessageData(byte[] data);

  /**
   * 取实际数据
   *
   * @return 实际数据
   */
  public byte[] getMessageData();

  /**
   * 设置实际数据
   *
   * @param data 实际数据
   *
   */
  public void setMessageData(byte[] data);

  /**
   * 对待发消息数据编码
   *
   * @param msgb 一个完整的数据包(未编码)
   *
   * @return 编码后数据包
   */
  public byte[] encodeMessagePacket(byte[] msgb);

  /**
   * 对收到消息数据解码
   *
   * @param msgb 一个完整的数据包(未解码)
   *
   * @return 解码后数据包
   */
  public byte[] decodeMessagePacket(byte[] msgb);

  /**
   * 分析消息获取其消息标识
   *
   * @param msgb 一个完整的数据包(未解码)
   * @param codecIsFF  是否FFFF编码方式: true-是,false-否
   *
   * @return  返回消息标识[0,255], 否则,返回<0.
   */
  public int checkMessagePacket(byte[] msgb, boolean codecIsFF);

  /**
   * 对原始数据进行分析, 分离一个完整的包(未解码)的位置.
   *
   * @param rcvBuf 原始数据(未解码)
   * @param codecIsFF 是否FFFF编码方式: true-是,false-否
   *
   * @return 实际第一个完整的包(未解码)的结束位置: 0-没有完成的数据包, >0-包结束位置
   */
  public int parseMessage(byte[] rcvBuf, boolean codecIsFF);

  /**
   * 取消息编码.
   *
   * @return 消息编码
   */
  public String getMsgFormat();

  /**
   * 是否FFFF编码方式
   *
   * @return true-是,false-否
   */
  public boolean isCodecIsFF();

  /**
   * 是否FFFF编码方式
   *
   *@param codecIsFF 是否FFFF编码方式: true-是,false-否
   */
  public void setCodecIsFF(boolean codecIsFF);
}
