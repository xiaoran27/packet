/************************* CHANGE REPORT HISTORY *****************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2009-6-11
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2011-9-19
*  //从当前的工作目录下找文件
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.dbms;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


/**
 * PROXOOL连接池管理
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class Proxool {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Proxool.class);

  /**
   * 默认配置文件
   */
  static final private String PROXOOL_CFG = "proxool.properties";

  
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
	  
	  //支持.,cfg,conf下找文件
	  String[] fileNames = {name,"conf/"+name,"cfg/"+name};
	  boolean found = false;
	  for (String fileName : fileNames){
		try{
			URL url = getFilename(fileName);
			  if (url!=null){
				  fileName = url.getFile();
			  }
			  
			//从当前的工作目录下找文件
		    if (".xml".equalsIgnoreCase(name.substring(name.length()-4))){
		      org.logicalcobwebs.proxool.configuration.JAXPConfigurator.configure(fileName, false);
		    }else{
		      org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure(fileName);
		    }
		    
		    if (logger.isInfoEnabled()){
		    	logger.info(" init(String ) - load property file "+fileName);
		    }
		    
		    found = true;
		    break;
		}catch(Exception e){
			//logger.warn(" init(String ) - "+e);
		}
	  }
	  if (!found){
		  throw new Exception("Couldn't load(.;conf;cfg) property file " + name);
	  }
  }
  
  static private URL getFilename(String name){  
	  String fileName = name;
	  URL url = Proxool.class.getClassLoader().getResource(name);
	  if (url != null){
		  fileName = url.getFile();
	  }else{
		  url = Proxool.class.getResource(name);
		  if (url != null){
			  fileName = url.getFile();
		  } else{
			  url = ClassLoader.getSystemResource(name);
			  if (url != null){
				  fileName = url.getFile();
			  }
		  }
	  }
	  
	  return url;
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

      String[] alias = new String[] {"mysqldb"};//{ "sybase", "hsqldb" };

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

                st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );  //rs要next(),否则不能rs.close()
                st.setMaxRows(1);
                rs = st.executeQuery("select * from user where 0=1");

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

