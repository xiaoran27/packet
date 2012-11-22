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
 * ���շ�ԭʼ���ݽ��з�������/���봦��
 *
 * @author $author$
 * @version $Revision: 1.2 $
  */
public interface IMessage {
  /**
   * ������ݻ�������С
   */
  public static int SOCKET_BUFFER_MAX = 8192*10;

  /**
   * ��Ϣ�е������ַ�
   */
  public static byte MEET_CHAR = (byte) 0xff;

  /**
   * ��Ϣ����ַ�
   */
  public static byte FILL_CHAR = (byte) 0x00;

  /**
    * �ַ�������
    */
  public static String DEFUALT_ENCODING = "UTF-8";

  /**
   * ��Ϣ�����Ƿ�FFFF����. Ĭ��:false
   * FFFF��������: ��MEET_CHAR׷��һ��FILL_CHAR�ֽ�,ĩβ������MEET_CHAR�ֽ�
   */
  public static boolean DEFUALT_CODECISFF = false;

  /**
   * ȡ�ַ�������
   *
   * @return �ַ�������
   */
  public String getEncoding();

  /**
   * �����ַ�������
   *
   * @param encoding �ַ�������
   */
  public void setEncoding(String encoding);

  /**
   * ȡ��Ϣ������
   *
   * @return ��Ϣ������
   */
  public int getMessageType();

  /**
   * ������Ϣ������
   *
   * @param messageType ��Ϣ������
   *
   */
  public void setMessageType(int messageType);

  /**
   * ȡ��Ϣ��id
   *
   * @return ��Ϣ��id
   */
  public int getMessageId();

  /**
   * ������Ϣ��id
   *
   * @param messageId ��Ϣ��id
   *
   */
  public void setMessageId(int messageId);

  /**
   * �����Ϣ���ݵĺϷ���.
   *
   * @param data ʵ������
   *
   * @return int ������Ϣ��ʶ[0,255], ����,����<0.
   */
  public int checkMessageData(byte[] data);

  /**
   * ȡʵ������
   *
   * @return ʵ������
   */
  public byte[] getMessageData();

  /**
   * ����ʵ������
   *
   * @param data ʵ������
   *
   */
  public void setMessageData(byte[] data);

  /**
   * �Դ�����Ϣ���ݱ���
   *
   * @param msgb һ�����������ݰ�(δ����)
   *
   * @return ��������ݰ�
   */
  public byte[] encodeMessagePacket(byte[] msgb);

  /**
   * ���յ���Ϣ���ݽ���
   *
   * @param msgb һ�����������ݰ�(δ����)
   *
   * @return ��������ݰ�
   */
  public byte[] decodeMessagePacket(byte[] msgb);

  /**
   * ������Ϣ��ȡ����Ϣ��ʶ
   *
   * @param msgb һ�����������ݰ�(δ����)
   * @param codecIsFF  �Ƿ�FFFF���뷽ʽ: true-��,false-��
   *
   * @return  ������Ϣ��ʶ[0,255], ����,����<0.
   */
  public int checkMessagePacket(byte[] msgb, boolean codecIsFF);

  /**
   * ��ԭʼ���ݽ��з���, ����һ�������İ�(δ����)��λ��.
   *
   * @param rcvBuf ԭʼ����(δ����)
   * @param codecIsFF �Ƿ�FFFF���뷽ʽ: true-��,false-��
   *
   * @return ʵ�ʵ�һ�������İ�(δ����)�Ľ���λ��: 0-û����ɵ����ݰ�, >0-������λ��
   */
  public int parseMessage(byte[] rcvBuf, boolean codecIsFF);

  /**
   * ȡ��Ϣ����.
   *
   * @return ��Ϣ����
   */
  public String getMsgFormat();

  /**
   * �Ƿ�FFFF���뷽ʽ
   *
   * @return true-��,false-��
   */
  public boolean isCodecIsFF();

  /**
   * �Ƿ�FFFF���뷽ʽ
   *
   *@param codecIsFF �Ƿ�FFFF���뷽ʽ: true-��,false-��
   */
  public void setCodecIsFF(boolean codecIsFF);
}
