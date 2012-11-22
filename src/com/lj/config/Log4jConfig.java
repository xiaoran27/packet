
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
*LAST UPDATED:: $Id: Log4jConfig.java,v 1.4 2007/08/16 05:53:44 xiaoran27 Exp $
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
* V,xiaoran27,2007-4-5
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-4-6
* + locateLast(String)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-4-21
* M locateLast(String ) 先CLASSPATH后其它目录
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-14
* M 输出加线程名称
*-----------------------------------------------------------------------------*
* V,xiaoran27,2012-11-21
* + //以-Dlog4jFile为准
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.config;

import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

import java.util.Arrays;


/**
 * log4j应用
 *
 * @author $author$
 * @version $Revision: 1.4 $
  */
public class Log4jConfig {
  /**
   * DOCUMENT ME!
   */
  private static String cfgFile = "log4j.properties";
  
  public static String getCfgFile() {
	return cfgFile;
 }

  public static void setCfgFile(String cfgFile) {
	Log4jConfig.cfgFile = cfgFile;
  }

/**
   * DOCUMENT ME!
   */
  static public void listening() {
	  
	String log4jFile = System.getProperty("log4jFile", cfgFile);
	if (!log4jFile.equals(cfgFile)){
		System.out.println(Thread.currentThread().getName()+" log4jFile="+log4jFile+"; cfgFile=" + cfgFile);
		cfgFile = log4jFile;  //以-Dlog4jFile为准
	}
	  
    boolean isfirst = false;

    if (isfirst) {
      PropertiesConfiguration cfg = new PropertiesConfiguration();
      URL[] urles = new URL[4];

      cfg.setFileName(cfgFile);
      cfg.setBasePath(Long.toHexString(System.currentTimeMillis())); //from CLASSPATH

      if ((null != cfg.getURL()) &&
          !Arrays.asList(urles).contains(cfg.getURL())) {
        urles[0] = cfg.getURL();
        PropertyConfigurator.configureAndWatch(cfg.getURL().getFile());
      }

      cfg.setBasePath(null); //user.dir

      if ((null != cfg.getURL()) &&
          !Arrays.asList(urles).contains(cfg.getURL())) {
        urles[1] = cfg.getURL();
        PropertyConfigurator.configureAndWatch(cfg.getURL().getFile());
      }

      cfg.setBasePath("conf"); //conf

      if ((null != cfg.getURL()) &&
          !Arrays.asList(urles).contains(cfg.getURL())) {
        urles[2] = cfg.getURL();
        PropertyConfigurator.configureAndWatch(cfg.getURL().getFile());
      }

      cfg.setBasePath("cfg"); //cfg

      if ((null != cfg.getURL()) &&
          !Arrays.asList(urles).contains(cfg.getURL())) {
        urles[3] = cfg.getURL();
        PropertyConfigurator.configureAndWatch(cfg.getURL().getFile());
      }

      System.out.println(Thread.currentThread().getName()+" Log4jConfig.listening() - urles=" +
        Arrays.toString(urles));
    } else {
      URL log4jURL = locateLast(cfgFile);
      System.out.println(Thread.currentThread().getName()+" Log4jConfig.listening() - log4jURL.getFile()" +
        log4jURL.getFile());
      PropertyConfigurator.configureAndWatch(log4jURL.getFile());
    }
  }

  /**
   * Return the last location of the specified resource by searching the current classpath and the system classpath, the user home
   * directory, conf, cfg.
   *
   * @param name the name of the resource
   *
   * @return the location of the resource
   */
  static public URL locateLast(String name) {
    URL[] urles = new URL[4];

    String base = Long.toHexString(System.currentTimeMillis());
    URL url = ConfigurationUtils.locate(base, name); //CLASSPATH
    if (null==url){
        url = ConfigurationUtils.locate(base, "conf/"+name); //conf
    }
    if (null==url){
        url = ConfigurationUtils.locate(base, "cfg/"+name); //conf
    }

    if ((null != url) && !Arrays.asList(urles).contains(url)) {
      urles[0] = url;
    }

    url = ConfigurationUtils.locate(null, name); //user.dir

    if ((null != url) && !Arrays.asList(urles).contains(url)) {
      urles[1] = url;
    }

    url = ConfigurationUtils.locate("conf", name); //conf

    if ((null != url) && !Arrays.asList(urles).contains(url)) {
      urles[2] = url;
    }

    url = ConfigurationUtils.locate("cfg", name); //cfg

    if ((null != url) && !Arrays.asList(urles).contains(url)) {
      urles[3] = url;
    }

    for (int i = urles.length - 1; i >= 0; i--) {
      url = urles[i];

      if (null != url) {
        break;
      }
    }

    System.out.println(Thread.currentThread().getName()+" Log4jConfig.locateLast(String) - url("+name+")=" + url);

    return url;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      Log4jConfig.listening();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
