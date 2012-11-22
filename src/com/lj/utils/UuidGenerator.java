
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
*LAST UPDATED:: $Id: UuidGenerator.java,v 1.2 2007/06/27 05:37:41 xiaoran27 Exp $
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
* V,xiaoran27,2007-4-29
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-6-25
* + static public String gererateUUID()
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import java.io.Serializable;

import java.net.InetAddress;


/**
 * 生产UUID标识号
 *
 * @author $author$
 * @version $Revision: 1.2 $
  */
public class UuidGenerator {
  /**
   * DOCUMENT ME!
   */
  private static final int IP;

  static {
    int ipadd;

    try {
      ipadd = IptoInt(InetAddress.getLocalHost().getAddress());
    } catch (Exception e) {
      ipadd = 0;
    }

    IP = ipadd;
  }

  /**
   * DOCUMENT ME!
   */
  private static short counter = (short) 0;

  static private UuidGenerator uuidGenerator = new UuidGenerator();

  /**
   * DOCUMENT ME!
   */
  private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

  /**
   * DOCUMENT ME!
   */
  private final static String sep = "";

  /**
   * DOCUMENT ME!
   *
   * @param bytes DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static int IptoInt(byte[] bytes) {
    int result = 0;

    for (int i = 0; i < 4; i++) {
      result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
    }

    return result;
  }

  /**
   * Unique across JVMs on this machine (unless they load this class
   * in the same quater second - very unlikely)
   */
  protected int getJVM() {
    return JVM;
  }

  /**
   * Unique in a millisecond for this JVM instance (unless there
   * are > Short.MAX_VALUE instances created in a millisecond)
   */
  protected short getCount() {
    synchronized (UuidGenerator.class) {
      if (counter < 0) {
        counter = 0;
      }

      return counter++;
    }
  }

  /**
   * Unique in a local network
   */
  protected int getIP() {
    return IP;
  }

  /**
   * Unique down to millisecond
   */
  protected short getHiTime() {
    return (short) (System.currentTimeMillis() >>> 32);
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected int getLoTime() {
    return (int) System.currentTimeMillis();
  }

  /**
   * DOCUMENT ME!
   *
   * @param intval DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected String format(int intval) {
    String formatted = Integer.toHexString(intval);
    StringBuffer buf = new StringBuffer("00000000");
    buf.replace(8 - formatted.length(), 8, formatted);

    return buf.toString();
  }

  /**
   * DOCUMENT ME!
   *
   * @param shortval DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected String format(short shortval) {
    String formatted = Integer.toHexString(shortval);
    StringBuffer buf = new StringBuffer("0000");
    buf.replace(4 - formatted.length(), 4, formatted);

    return buf.toString();
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Serializable generate() {
    return new StringBuffer(36).append(format(getIP())).append(sep)
                               .append(format(getJVM())).append(sep)
                               .append(format(getHiTime())).append(sep)
                               .append(format(getLoTime())).append(sep)
                               .append(format(getCount())).toString();
  }

  static public String gererateUUID(){
      return uuidGenerator.generate().toString();
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
      long old = System.currentTimeMillis();
      while (System.currentTimeMillis()-old < 60*1000l) {
      Serializable s = UuidGenerator.gererateUUID();  //new UuidGenerator().generate();
      System.out.println(s);

      }
  }
}
