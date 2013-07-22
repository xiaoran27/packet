
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
*LAST UPDATED:: $Id: HexUtil.java,v 1.3 2009/05/11 10:11:34 xiaoran27 Exp $
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
* V,xiaoran27,2006-3-15
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-3-17
* + byteToHex(byte[], boolean)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2009-5-11
* M byteToHex(byte[] b, int size, boolean left)  //left or right size
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-22
* + String byteToHexstr(byte[] b, boolean space)
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;


/**
 * 转换成16进制串
 *
 * @author $author$
 * @version $Revision: 1.3 $
  */
public class HexUtil {
  /**
   * 指定后size字节转16进制串(格式化)
   * @see #byteToHex(byte[], int, boolean)
   *
   * @param b 字节数据
   * @param size 字节数
   *
   * @return 16进制串
   */
  public static String byteToHex(byte[] b, int size) {
    return byteToHex(b, size, true);
  }

  /**
   * 指定前或后size字节转16进制串(格式化)
   * @see #byteToHex(byte[])
   *
   * @param b 字节数据
   * @param size 字节数
   * @param left 前或后
   *
   * @return 16进制串
   */
  public static String byteToHex(byte[] b, int size, boolean left) {
    byte[] d = new byte[size];

    if (left) {  //left size
      System.arraycopy(b, 0, d, 0,
        (b.length > size) ? size : b.length);
    } else {  //right size
      System.arraycopy(b, (b.length > size) ? (b.length - size) : 0, d, 0,
        (b.length > size) ? size : b.length);
    }

    return byteToHex(d);
  }

  /**
   * 字节转16进制串(格式化)
   * @see #byteToHex(byte[], boolean)
   *
   * @param b 字节数据
   *
   * @return 16进制串
   */
  public static String byteToHex(byte[] b) {
      return byteToHex(b, true);
  }

  /**
   * 字节转16进制串
   *
   * @param b 字节数据
   * @param format 是否格式化: true-是,false-否. 每8byte换行, 每1byte用space分开
   *
   * @return 16进制串
   */
  public static String byteToHex(byte[] b, boolean format) {
    StringBuilder toHex = new StringBuilder();

    for (int i = 0; (null != b) && (i < b.length); i++) {
      if (format) {
          toHex.append(' ');
      }

      char hi = Character.forDigit((b[i] >> 4) & 0x0F, 16);
      char lo = Character.forDigit(b[i] & 0x0F, 16);
      toHex.append(Character.toUpperCase(hi));
      toHex.append(Character.toUpperCase(lo));

      if (format && 15==i%16){
          toHex.append('\r').append('\n');
      }
    }

    return toHex.toString();
  }
  
  /**
   * 字节转16进制串
   *
   * @param b 字节数据
   * @param space 是否e用space分开: true-是,false-否. 
   *
   * @return 16进制串
   */
  public static String byteToHexstr(byte[] b, boolean space) {
	    StringBuilder toHex = new StringBuilder();

	    for (int i = 0; (null != b) && (i < b.length); i++) {
	      if (space) {
	          toHex.append(' ');
	      }

	      char hi = Character.forDigit((b[i] >> 4) & 0x0F, 16);
	      char lo = Character.forDigit(b[i] & 0x0F, 16);
	      toHex.append(Character.toUpperCase(hi));
	      toHex.append(Character.toUpperCase(lo));

	    }

	    return toHex.toString();
	  }

  /**
   * @param args
   */
  public static void main(String[] args) {
      byte[] b = new String("1234567890abcdefg").getBytes();

      b = new byte[]{0x0,0xa,(byte) 0xa0,0xf,(byte) 0xf0,(byte) 0xff,0x0,0xa,(byte) 0xa0,0xf,(byte) 0xf0,(byte) 0xff,0x0,0xa,(byte) 0xa0,0xf,(byte) 0xf0,(byte) 0xff};
      
      System.out.println(HexUtil.byteToHex(b,false));
      System.out.println(HexUtil.byteToHex(b));
      System.out.println(HexUtil.byteToHex(b,10,true));
      System.out.println(HexUtil.byteToHex(b,10,false));
      System.out.println(HexUtil.byteToHex(b,10));

      System.out.println(HexUtil.byteToHex(new byte[]{(byte)0x0}));
      System.out.println(HexUtil.byteToHex(new byte[]{(byte)0xf}));
      System.out.println(HexUtil.byteToHex(new byte[]{(byte)0}));
      System.out.println(HexUtil.byteToHex(new byte[]{(byte)255}));
      System.out.println(HexUtil.byteToHex(new byte[]{(byte)256}));


  }
}
