
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
*LAST UPDATED:: $Id: ByteFFCode.java,v 1.2 2007/04/08 03:52:29 xiaoran27 Exp $
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
* V,suntf,2007-3-23
*   create
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2007-3-23
* + findFFBytes(byte[] buf)
*-----------------------------------------------------------------------------*
* V,Xiaoran27,2007-4-8
* M fromFFBytes(byte[]  //防止msgc被篡改
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.message;

import com.lj.config.Log4jConfig;

import com.lj.utils.HexUtil;

import org.apache.log4j.Logger;


/**字节FF编解码
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class ByteFFCode {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(ByteFFCode.class);

  /**
   * 消息中的特殊字符
   */
  public static byte MEET_CHAR = (byte) 0xff;

  /**
   * 消息填充字符
   */
  public static byte FILL_CHAR = (byte) 0x00;

  /**
   * 把byte[]转成含FF的byte[]
   * toFFBytes procedure:
   * 1.    内部每含有一个0xFF字节，则在其后加入一个0x00字节。
   * 2.    在消息末尾加上两个0xFF字节。
   *
   * @return 含FF的byte[]
   */
  public static byte[] toFFBytes(byte[] msgb) {
    int srclen = msgb.length;

    if ((null == msgb) || (srclen < 1)) {
      return null;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("toFFBytes(byte[]) - before toFFBytes,msgb(HEX): " +
        HexUtil.byteToHex(msgb));
    }

    byte[] tmp = new byte[(msgb.length << 1) + 2];
    int i = 0;
    int j = 0;

    while (i < msgb.length) {
      tmp[j++] = msgb[i];

      if (msgb[i] == MEET_CHAR) {
        tmp[j++] = FILL_CHAR;
      }

      i++;
    }

    tmp[j++] = MEET_CHAR;
    tmp[j++] = MEET_CHAR;

    byte[] result = new byte[j];
    System.arraycopy(tmp, 0, result, 0, j);

    if (logger.isDebugEnabled()) {
      logger.debug("toFFBytes(byte[]) - after encode,msgb(HEX): " +
        HexUtil.byteToHex(result));
    }

    return result;
  }

  /**
   * 把byte[]转成不含FF的byte[]
   *
   * fromFFBytes procedure:
   * 1.    去掉两个连续的0xFF尾部。
   * 2.    每有一个0xFF字节，则去掉其后的0x00字节。
   *
   * @return 不含FF的byte[]
   */
  public static byte[] fromFFBytes(byte[] msgb) {
    int srclen = msgb.length;

    if ((null == msgb) || (srclen < 1)) {
      return null;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("fromFFBytes(byte[]) - before decode,msgb(HEX): " +
        HexUtil.byteToHex(msgb));
    }

    byte[] tmp = new byte[msgb.length];
    System.arraycopy(msgb, 0, tmp, 0, msgb.length);  //防止msgc被篡改
    int i = 0;
    int j = 0;

    while (i < msgb.length) {
      if (msgb[i] == MEET_CHAR) {
        ++i;

        if (i >= srclen) /*next byte expected*/ {
          return null;
        }

        if (msgb[i] == FILL_CHAR) {
          tmp[j++] = MEET_CHAR;
          ++i;
        } else if (msgb[i] == MEET_CHAR) {
          if ((i + 1) != srclen) /* end of message*/ {
            logger.warn(
              "fromFFBytes(byte[]) - FF is arrived,but msgb is not end ");
          }

          break;
        } else /* unexpected byte*/
         {
          return null;
        }
      } else {
        tmp[j++] = msgb[i++];
      }
    }

    byte[] result = new byte[j];
    System.arraycopy(tmp, 0, result, 0, j);

    if (logger.isDebugEnabled()) {
      logger.debug("fromFFBytes(byte[]) - after decode,msgb(HEX): " +
        HexUtil.byteToHex(result));
    }

    return result;
  }

  /**
   * 找第一个FF结束的位置
   *
   * @return 第一个FF结束的位置
   */
  public static int findFFBytes(byte[] buf) {
      int rtn = 0;
      for(int i=0; i<buf.length; i++){
          if (MEET_CHAR==buf[i] && i<buf.length-1 && MEET_CHAR==buf[i+1]){
              rtn = i+1;  //第一个包的结束位置
              break;
          }
      }

      return rtn;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    byte[] src;
    Log4jConfig.listening();

    if (args.length < 1) {
      src = ("000中国-./09:;<=>?@AZ[\\]^_`az{|}~00").getBytes();
    } else {
      src = args[0].getBytes();
    }

    src[1] = MEET_CHAR;

    System.out.println("Source: len = " + src.length + "; src = " +
      new String(src));
    System.out.println("   :   src          = " +
      HexUtil.byteToHex(src, src.length));

    byte[] result = ByteFFCode.toFFBytes(src);
    System.out.println("toFFBytes:   result = " +
      HexUtil.byteToHex(result, result.length));

    result = ByteFFCode.fromFFBytes(result);
    System.out.println("fromFFBytes: result = " +
      HexUtil.byteToHex(result, result.length));
  }
}
