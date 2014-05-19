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
*LAST UPDATED:: $Id: Config.java,v 1.12 2010/02/22 05:39:05 xiaoran27 Exp $
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
* V,xiaoran27,2006-3-10
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-3-15
* + locate(String base, String name)
* + locate(String name)
* M loadPropertiesConfig(String fileName) ����locate
* * M loadXmlConfig(String fileName) ����locate
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-4-4
* + class ConfigurationInfo
* + class ConfigWatch
* + loadLastXxxConfig(...)
* + loadAndWatchConfig(...)
* M locateAll(...) -> locateFirst(...)
* + stopWatch()
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-4-5
* + private static boolean isRunningWatch = false;
* + startWatch()
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-4-7
* M locateLast(String ) invoke Log4jConfig.locateLast(String)
* M reload() logger.warn(...)
* M startWatch() �����߳�
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-8-1
* M ConfigurationInfo: �����ļ���Ϊ��Ա. init()������getLastModified()�е���
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-14
* M �����ļ�����ı���֪ͨ�������
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-14
* M loadAndWatchConfig(String , int )  + //����Ƿ���ͬһ����
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-24
* + ���øı�ĳ�����ʶ
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-11-5/6
* + reload()ʱ,synchronized (configuration)
* + Config.CONFIG_IS_CHANGED_TIME //����������ʱ��
*-----------------------------------------------------------------------------*
* V,xiaoran27,2008-4-22
* + Configuration getConfig(String fileName)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2009-11-3
* �趨Ĭ�ϵ��ļ������ʽUTF-8
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-02-22
* M reload() //���øı��ʶ���ļ������(���ּ���)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-11-11
* M //no yield
*-----------------------------------------------------------------------------*
* V,xiaoran27,2014-5-13
* M //对应file的Configuration //skip 0
*-----------------------------------------------------------------------------*
* V,xiaoran27,2014-5-19
* M //replace OLD
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.config;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;


/**
 * ����.properties,.xml�������ļ�
 *
 * @author $author$
 * @version $Revision: 1.12 $
  */
public class Config {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(Config.class);
  
  private static boolean usingCoding = true;
  public static String DEFAULT_ENCODING = "UTF-8";

  /*���øı�ĳ�����ʶ*/
  public static String CONFIG_FILENAME = "_CONFIG_FILENAME";
  public static String CONFIG_IS_CHANGED = "_CONFIG_IS_CHANGED";
  public static String CONFIG_IS_CHANGED_COUNT = "_CONFIG_IS_CHANGED_COUNT";

  /*����������ʱ��*/
  public static String CONFIG_IS_CHANGED_TIME = "_CONFIG_IS_CHANGED_TIME";

  /**
   * ������м��ص�����, <0,ConfigurationInfo>�����ø���(ConfigurationInfo.getInterval())
   */
  private static Map<Integer, ConfigurationInfo> watchMap = new HashMap<Integer, ConfigurationInfo>();

  /**
   * ���������߳�
   */
  private static ConfigWatch configWatch = null;

  /**
   * �����߳��Ƿ���������
   */
  private static boolean isRunningWatch = false;

  static {
    watchMap.put(0, new ConfigurationInfo(null, 0)); //���ø���
  }

  /**
   * ���������߳�
   */
  static public void startWatch() {

    if (null == configWatch) {
      configWatch = new ConfigWatch(watchMap);
    }

    if (!isRunningWatch || !configWatch.isRun()) {
      configWatch.setRun(true);
      isRunningWatch = true;
      configWatch.getThread().start(); //start watch thread
    }

    if (logger.isInfoEnabled()) {
        logger.info("startWatch() - " + configWatch.toSimpleString() +
          " is running at " + new Date());
    }
  }

  /**
   * ֹͣ�����߳�
   */
  static public void stopWatch() {
    configWatch.setRun(false);
    isRunningWatch = false;

    if (logger.isInfoEnabled()) {
      logger.info("stopWatch() - " + configWatch.toSimpleString() +
        " is stopped at " + new Date());
    }
  }

  /**
   * Return the location of the specified resource by searching the user home
   * directory, the current classpath and the system classpath.
   *
   * @param base the base path of the resource
   * @param name the name of the resource
   *
   * @return the location of the resource
   */
  static public URL locate(String base, String name) {
    return ConfigurationUtils.locate(base, name);
  }

  /**
   * Return the location of the specified resource by searching the user home
   * directory, the current classpath and the system classpath.
   *
   * @param name the name of the resource
   *
   * @return the location of the resource
   */
  static public URL locate(String name) {
    return ConfigurationUtils.locate(name);
  }

  /**
   * Return the location of the specified resource by searching the current classpath and the system classpath, the user home
   * directory, conf, cfg.
   *
   * @param name the name of the resource
   *
   * @return the location of the resource
   */
  static public URL locateBefore(String name) {
    URL url = ConfigurationUtils.locate(Long.toHexString(
          System.currentTimeMillis()), name); //CLASSPATH

    if (null == url) {
      url = ConfigurationUtils.locate(null, name); //user.dir
    }

    if (null == url) {
      url = ConfigurationUtils.locate("conf", name); //conf
    }

    if (null == url) {
      url = ConfigurationUtils.locate("cfg", name); //cfg
    }

    return url;
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
      /*
      URL[] urles = new URL[4];

      URL url = ConfigurationUtils.locate(Long.toHexString(System.currentTimeMillis()), name); //CLASSPATH
      if (null != url && !Arrays.asList(urles).contains(url)) {
          urles[0] = url;
      }

      url = ConfigurationUtils.locate(null, name);  //user.dir
      if (null != url && !Arrays.asList(urles).contains(url)) {
          urles[1] = url;
      }

      url = ConfigurationUtils.locate("conf", name);  //conf
      if (null != url && !Arrays.asList(urles).contains(url)) {
          urles[2] = url;
      }

      url = ConfigurationUtils.locate("cfg", name);  //cfg
      if (null != url && !Arrays.asList(urles).contains(url)) {
          urles[3] = url;
      }

      for(int i=urles.length-1; i>=0; i--){
          url = urles[i];
          if (null!=url){
              break;
          }
      }

      if (logger.isInfoEnabled()) {
        logger.info("locateLast(String) - url=" + url);
      }

      return url;
      */
      return Log4jConfig.locateLast(name);
  }

  /**
   * ��ȡָ���ļ��ļ�������.
   *
   * @param fileName String �ļ���.  ����·��: .jar, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @return Configuration ���ö���ʵ��. null - û�м��ع�
   *
   * @throws Exception
   */
  static public Configuration getConfig(String fileName) {
	  Configuration cfg = null;

	    //����Ƿ���ͬһ����
	    for (Iterator<ConfigurationInfo> iter=watchMap.values().iterator(); iter.hasNext();) {
	        ConfigurationInfo ci = iter.next();
	        cfg = ci.getConfiguration();  //对应file的Configuration
	        if (null==cfg){  //skip 0
	            continue;
	        }
	        File file = ci.getFile();
	        if (file != null && file.getName().indexOf(fileName)>=0){
	        	break;
	        }
	    }

	    return cfg;
  }

  /**
   * ���������ļ�(.properties,.xml). ����·��: .jar, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param fileName String �ļ���
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadConfig(String fileName)
    throws Exception {
    if ((fileName.length() > "properties".length()) &&
        "properties".equalsIgnoreCase(fileName.substring(fileName.length() -
            "properties".length()))) {
      return loadPropertiesConfig(fileName);
    } else if ((fileName.length() > "xml".length()) &&
        "xml".equalsIgnoreCase(fileName.substring(fileName.length() -
            "xml".length()))) {
      return loadXmlConfig(fileName);
    } else {
      throw new ConfigurationException(fileName + " is not supported.");
    }
  }

  /**
   * ���������ļ�(.properties). ����·��: CLASSPATH, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param fileName String �ļ���
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadPropertiesConfig(String fileName)
    throws Exception {
    PropertiesConfiguration cfg = new PropertiesConfiguration();
    if (usingCoding){
    	 cfg.setEncoding(DEFAULT_ENCODING);
    }
    URL[] urles = new URL[4];
    URL urlTmp = null;

    urlTmp = locate(Long.toHexString(System.currentTimeMillis()), fileName); //CLASSPATH

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[0] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    urlTmp = locate(null, fileName); //user.dir

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[1] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    urlTmp = locate("conf", fileName); //conf

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[2] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    urlTmp = locate("cfg", fileName); //cfg

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[3] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    if (logger.isInfoEnabled()) {
      logger.info("loadPropertiesConfig(String) - urles=" +
        Arrays.toString(urles));
    }

    boolean allNull = true;

    for (URL url : urles) {
      allNull = allNull && (null == url);
    }

    if (allNull) {
      throw new ConfigurationException(fileName +
        " is not found or not supported.");
    }

    return cfg;
  }

  /**
   * ���������ļ�(.xml). ����·��: CLASSPATH, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param fileName String �ļ���
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadXmlConfig(String fileName)
    throws Exception {
    XMLConfiguration cfg = new XMLConfiguration();
    if (usingCoding){
   	 cfg.setEncoding(DEFAULT_ENCODING);
   }
    URL[] urles = new URL[4];
    URL urlTmp = null;

    urlTmp = locate(Long.toHexString(System.currentTimeMillis()), fileName); //CLASSPATH

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[0] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    urlTmp = locate(null, fileName); //user.dir

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[1] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    urlTmp = locate("conf", fileName); //conf

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[2] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    urlTmp = locate("cfg", fileName); //cfg

    if ((null != urlTmp) && !Arrays.asList(urles).contains(urlTmp)) {
      urles[3] = urlTmp;
      cfg.setURL(urlTmp);
      cfg.load();
    }

    if (logger.isInfoEnabled()) {
      logger.info("loadXmlConfig(String) - urles=" + Arrays.toString(urles));
    }

    boolean allNull = true;

    for (URL url : urles) {
      allNull = allNull && (null == url);
    }

    if (allNull) {
      throw new ConfigurationException(fileName +
        " is not found or not supported.");
    }

    return cfg;
  }

  /**
   * �������һ�������ļ�(.properties,.xml). ����·��: CLASSPATH, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param fileName String �ļ���
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadLastConfig(String fileName)
    throws Exception {
    if ((fileName.length() > ".properties".length()) &&
        ".properties".equalsIgnoreCase(fileName.substring(fileName.length() -
            ".properties".length()))) {
      return loadLastPropertiesConfig(fileName);
    } else if ((fileName.length() > ".xml".length()) &&
        ".xml".equalsIgnoreCase(fileName.substring(fileName.length() -
            ".xml".length()))) {
      return loadLastXmlConfig(fileName);
    } else {
      throw new ConfigurationException(fileName + " is not supported.");
    }
  }

  /**
   * �������һ�������ļ�(.properties). ����·��: CLASSPATH, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param fileName String �ļ���
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadLastPropertiesConfig(String fileName)
    throws Exception {
	  PropertiesConfiguration cfg = new PropertiesConfiguration();
	    if (usingCoding){
	    	 cfg.setEncoding(DEFAULT_ENCODING);
	    }
	  cfg.setURL(locateLast(fileName));
	  cfg.load();
    return cfg;
  }

  /**
   * �������һ�������ļ�(.xml). ����·��: CLASSPATH, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param fileName String �ļ���
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadLastXmlConfig(String fileName)
    throws Exception {
	  XMLConfiguration cfg = new XMLConfiguration();
	    if (usingCoding){
	    	 cfg.setEncoding(DEFAULT_ENCODING);
	    }
	  cfg.setURL(locateLast(fileName));
	  cfg.load();
	  
    return cfg;
  }

  /** �������һ�������ļ�(.properties,.xml),�������ļ����ж�̬����.
   *  ����·��: .jar, {user.dir}, conf, cfg�µ������ļ�.
   *
   * @param file �����ļ�(.properties,.xml)
   * @param tims spoolʱ��(s)
   *
   * @return Configuration ���ö���ʵ��
   *
   * @throws Exception
   */
  static public Configuration loadAndWatchConfig(String file, int times)
    throws Exception {
    Configuration config = loadLastConfig(file);
    ConfigurationInfo cfgInfo = watchMap.get(0);

    //����Ƿ���ͬһ����
    for (Iterator<ConfigurationInfo> iter=watchMap.values().iterator(); iter.hasNext();) {
        ConfigurationInfo ci = iter.next();
        Configuration cfg = ci.getConfiguration();
        if (null==cfg){  //������
            continue;
        }

        boolean isExists = false;
        if (cfg instanceof PropertiesConfiguration && config instanceof PropertiesConfiguration){
            isExists = ((PropertiesConfiguration)cfg).getURL().getFile().equals(((PropertiesConfiguration)config).getURL().getFile());
        }else if (cfg instanceof XMLConfiguration && config instanceof XMLConfiguration){
            isExists = ((XMLConfiguration)cfg).getURL().getFile().equals(((XMLConfiguration)config).getURL().getFile());
        }
        if (isExists){
        	ci.setConfiguration(config);  //replace OLD
        	cfg = config;
            return cfg;
        }
    }

    cfgInfo.setInterval(cfgInfo.getInterval() + 1);
    watchMap.put(cfgInfo.getInterval(),
      new ConfigurationInfo(config, (times < 1) ? 1 : times));

    startWatch();

    return config;
  }

  /** �������һ�������ļ�(.properties,.xml),������(���1s)�ļ����ж�̬����.
    *  ����·��: .jar, {user.dir}, conf, cfg�µ������ļ�.
    *
    * @param file �����ļ�(.properties,.xml)
    *
    * @return Configuration ���ö���ʵ��
    *
    * @throws Exception
    */
  static public Configuration loadAndWatchConfig(String file)
    throws Exception {
    return loadAndWatchConfig(file, 1);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
//      Log4jConfig.listening();

      Configuration test = Config.loadAndWatchConfig("msg.properties");
//      Config.loadPropertiesConfig("log4j.properties");
//      System.out.println(test.getList("msgformat"));
//
//      Configuration test2 = Config.loadAndWatchConfig("log4j.properties");
//      System.out.println(test2.getList("log4j.rootLogger"));

      long old = System.currentTimeMillis();

      while ((System.currentTimeMillis() - old) < (300 * 1000)) {
        Thread.sleep(1000);
        System.out.println(test.getList("msgformat"));
//        System.out.println(test2.getList("log4j.rootLogger"));
        System.out.println(test.getList("test"));
        
        test = Config.loadAndWatchConfig("msg.properties");
        System.out.println(Config.getConfig("msg.properties"));
      }

      //Config.stopWatch();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.12 $
  */
class ConfigurationInfo {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(ConfigurationInfo.class);

  private File file = null;

  /**
   * DOCUMENT ME!
   */
  private Configuration configuration = null;

  /**
   * DOCUMENT ME!
   */
  private int interval = 1;

  /**
   * DOCUMENT ME!
   */
  private long lastLoadTime = System.currentTimeMillis();

  /**
   * @param configuration
   *  @param interval
   */
  public ConfigurationInfo(Configuration configuration, int interval) {
    this.configuration = configuration;
    this.interval = interval;
  }

  /**
   * @param configuration
   */
  public ConfigurationInfo(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * @return the configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * @param configuration the configuration to set
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * @return the interval
   */
  public int getInterval() {
    return interval;
  }

  /**
   * @param interval the interval to set
   */
  public void setInterval(int interval) {
    this.interval = interval;
  }

  /**
   * @return the lastLoadTime
   */
  public long getLastLoadTime() {
    return lastLoadTime;
  }

  /**
   * @param lastLoadTime the lastLoadTime to set
   */
  public void setLastLoadTime(long lastLoadTime) {
    this.lastLoadTime = lastLoadTime;
  }

  /**
   * ���¼��������ļ�
   */
  public void reload() {
    try {
      synchronized (configuration){
          if (configuration instanceof XMLConfiguration) {
            configuration.clear();
            ((XMLConfiguration) configuration).load();
//        	  ((XMLConfiguration) configuration).reload(); //unusing this
          } else if (configuration instanceof PropertiesConfiguration) {
            configuration.clear();
            ((PropertiesConfiguration) configuration).load();
//            ((PropertiesConfiguration) configuration).reload(); //unusing this
          } else {
            logger.warn("reload() - unknow configuration=" +
              configuration.toString());
          }

          //����̬����handleʹ��
          configuration.setProperty(Config.CONFIG_IS_CHANGED, new Boolean(true));
          configuration.setProperty(Config.CONFIG_IS_CHANGED_COUNT, 0);

          this.lastLoadTime = getLastModified();
          //���øı�������ʱ��
          configuration.setProperty(Config.CONFIG_IS_CHANGED_TIME, lastLoadTime);
        
          //���øı��ʶ���ļ������(���ּ���)
          configuration.setProperty(Config.CONFIG_FILENAME, file.getName());
          configuration.setProperty(file.getName()+Config.CONFIG_IS_CHANGED, new Boolean(true));
          configuration.setProperty(file.getName()+Config.CONFIG_IS_CHANGED_COUNT, 0);
          configuration.setProperty(file.getName()+Config.CONFIG_IS_CHANGED_TIME, lastLoadTime);
      
      }
    } catch (Exception e) {
      logger.warn("reload() - Exception e=" + e, e);
    }

    logger.warn("reload() - finished. ConfigurationInfo=" + this.toString());
  }

  /**
   * �����ļ����һ���޸�ʱ��
   */
  public long getLastModified() {
    if (null == file) {
        init();
    }

    if (null != file) {
        return file.lastModified();
      }

    return 0;
  }

  /**
   * �����ļ�
   */
  public File getFile() {
    if (null == file) {
        init();
    }

    return file;
  }

  private void init(){
      if (configuration instanceof XMLConfiguration) {
          file = ((XMLConfiguration) configuration).getFile();
        } else if (configuration instanceof PropertiesConfiguration) {
          file = ((PropertiesConfiguration) configuration).getFile();
        } else {
          logger.warn("init() - unknow configuration=" + configuration);
        }
  }
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).appendSuper(super.toString())
                                                                 .append("lastLoadTime",
      this.lastLoadTime).append("configuration", this.configuration)
                                                                 .append("interval",
      this.interval).toString();
  }
}


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.12 $
  */
class ConfigWatch implements Runnable {
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(ConfigWatch.class);

  /**
   * ��ǰ�߳�
   */
  private Thread thread = null;

  /**
   * ������м��ص�����, <0,ConfigurationInfo>�����ø���(ConfigurationInfo.getInterval())
   */
  private Map<Integer, ConfigurationInfo> watchMap = null;

  /**
   * �����߳��Ƿ�����
   */
  private volatile boolean run = true;

  /**
   * @param watchMap
   */
  public ConfigWatch(Map<Integer, ConfigurationInfo> watchMap) {
    this.watchMap = watchMap;
  }

  /**
   * @return the run
   */
  public boolean isRun() {
    return run;
  }

  /**
   * @param run the run to set
   */
  public void setRun(boolean run) {
    this.run = run;
  }

  /**
   * @return the thread
   */
  public Thread getThread() {
    if ((null == thread) || !thread.isAlive()) {
      thread = new Thread(this, toSimpleString());
    }

    return thread;
  }

  /** ��̬���Configuration
   *
   * @see java.lang.Runnable#run()
   */
  public void run() {
    //����߳̿�ʼ��Ϣ
    StringBuilder sbTmp = new StringBuilder();
    sbTmp.append(this.getClass().getSimpleName()).append(" is running at ")
         .append(new Date());
    System.out.println(sbTmp);

    if (logger.isInfoEnabled()) {
      logger.info(sbTmp);
    }

    while (run) {
      try {
        //Thread.yield();  //no yield
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }

      /*��̬��������*/
      int count = watchMap.get(0).getInterval(); //���ø���

      for (int i = 1; i <= count; i++) {
        ConfigurationInfo configInfo = watchMap.get(i);
        long lastLoadTime = configInfo.getLastLoadTime();
        if ((lastLoadTime > 0) &&
            ((System.currentTimeMillis() - lastLoadTime) > (configInfo.getInterval() * 1000)) &&
            (configInfo.getLastModified() > lastLoadTime)) {
          configInfo.reload();
        }

        //System.out.println(new Date()+": configInfo.getLastModified()="+configInfo.getLastModified()+"; configInfo="+configInfo);
      }
    }
  }

  /**
   * ƴд�򻯵��ַ�,����Ϊ�̵߳�ȫ��
   *
   * @return �򻯵��ַ�,����Ϊ�̵߳�ȫ��
   */
  public String toSimpleString() {
    return this.getClass().getSimpleName();
  }
}
