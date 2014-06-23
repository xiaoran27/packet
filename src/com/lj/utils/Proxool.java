
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
*LAST UPDATED:: $Id: Proxool.java,v 1.4 2007/08/01 16:27:01 xiaoran27 Exp $
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
* V,xiaoran27,2007-4-8
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-4-26
*   + private static boolean isInited = false //默认初始化一次
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import com.lj.config.Log4jConfig;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;


/**
 * PROXOOL连接池管理
 *
 * @author $author$
 * @version $Revision: 1.4 $
  */
@Deprecated
public class Proxool {
  /**
   * 默认配置文件
   */
  static final private String PROXOOL_CFG = "proxool.properties";

  /**
   * DOCUMENT ME!
   */
  private static Logger logger = Logger.getLogger(Proxool.class);
  /**
   * 默认初始化一次
   */
  private static boolean isInited = false;

  private static Proxool proxool = null;

  /*get a instance of Proxool for global*/
  public static Proxool newInstance() throws Exception {
      Proxool.init();
      if (proxool == null){
          proxool = new Proxool();
      }
      return proxool;
  }

  /**
   * 加载指定的配置文件
   *
   * @param name DOCUMENT ME!
   *
   * @throws Exception DOCUMENT ME!
   */
  public static void init(String name) throws Exception {
    String fileName = Log4jConfig.locateLast(name).getFile();

    if (logger.isInfoEnabled()) {
      logger.info("init() - configure from " + fileName);
    }
    if (".xml".equalsIgnoreCase(name.substring(name.length()-4))){
      org.logicalcobwebs.proxool.configuration.JAXPConfigurator.configure(fileName, false);
    }else{
      org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure(fileName);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @throws Exception DOCUMENT ME!
   */
  public static void init() throws Exception {
      if(isInited)  return;
      init(PROXOOL_CFG);
      isInited = true;
  }

  /**
   * DOCUMENT ME!
   *
   * @param alias DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws SQLException DOCUMENT ME!
   */
  public static Connection getConnection(String alias) throws SQLException {
      if(!isInited)  {
          try {
            init();
        } catch (Exception e) {
            logger.warn("getConnection(String ) - not init proxool. ");
        }
      }

    Connection conn = DriverManager.getConnection("proxool." + alias);

    if (logger.isDebugEnabled()) {
      logger.debug("getConnection(String) - alias=" + alias + "; conn=" + conn+"; db="+conn.getCatalog());
    }

    return conn;
  }

  /**
   * DOCUMENT ME!
   *
   * @param alias DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean testConnection(String alias) {
    boolean result = true;
    Connection conn = null;

    try {
      conn = getConnection(alias);
    } catch (Exception e) {
         logger.warn("testConnection(String) - alias=" + alias +
                    ". Exception e=" + e, e);
      result = false;
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          logger.error("testConnection(String) - alias=" + alias +
            ". SQLException e=" + e, e);
        }
      }
    }

    return result;
  }

  /**
   * DOCUMENT ME!
   *
   * @param args DOCUMENT ME!
   */
  public static void main(String[] args) {
    try {
      Proxool.init();

      String[] alias = new String[] { "DB", "MDB" };

      if ((null != args) && (args.length > 0)) {
        alias = args;
      }

      while (true) {
          Thread.sleep(1000L);
        for (String db : alias) {
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;
            try {

                conn = getConnection(db);
                System.out.println(Arrays.toString(conn.getClass().getFields()));
                System.out.println(Arrays.toString(conn.getClass().getDeclaredMethods()));

                st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );  //rs要next(),否则不能rs.close()
                st.setMaxRows(1);
                rs = st.executeQuery("select * from roler where 0=1");

            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if (null!=rs) rs.close();
                if (null!=st) st.close();
                if (null!=conn) conn.close();
            }

        }


      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
