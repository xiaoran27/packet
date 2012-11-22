
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
*LAST UPDATED:: $Id: MessageFactory.java,v 1.5 2007/07/19 06:34:42 xiaoran27 Exp $
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
* V,xiaoran27,2007-3-12
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-3-17
* M message.properties/xml -> msg.properties/xml 防止与国际化配置混淆
*-----------------------------------------------------------------------------*
* V,suntf,2007-3-17
*-----------------------------------------------------------------------------*
* V,suntf,2007-4-6
* M mergeClazz(String) - 数组越界
* M handleMap -> msgMap
*----------------------------------------------------------------------------*
* V,xiaoran27,2007-7-19
* M  每个线程使用独立的message实例
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.message;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.lj.config.Config;
import com.lj.config.Log4jConfig;


/**
 * 创建消息编码/解码类. 依据消息的格式,拼成类名并实例化返回
 *
 * @author $author$
 * @version $Revision: 1.5 $
  */
public class MessageFactory {
  /**
     * DOCUMENT ME!
     */
  private static final Logger logger = Logger.getLogger(MessageFactory.class);

  /**
   * DOCUMENT ME!
   */
  static private Configuration config = null;


  private static final ThreadLocal threadLocal = new ThreadLocal();

  private static Map<String, String> clazzMap = new HashMap<String, String>();

  /**
   * DOCUMENT ME!
   */
  static private MessageFactory messageFactory = null;

  static {
    init();
  }

  /**
   * Creates a new MessageFactory object.
   */
  private MessageFactory() {
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  static public MessageFactory newInstance() {
    if (messageFactory == null) {
      messageFactory = new MessageFactory();
    }

    return messageFactory;
  }

  /**
   * DOCUMENT ME!
   *
   * @param msgFormat DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  static public IMessage createMessage(String msgFormat) {
      String clazz = clazzMap.get(msgFormat);
      Map<String, IMessage> msgMap = (Map<String, IMessage>)threadLocal.get();
      if (null==msgMap){
          init();
          msgMap = (Map<String, IMessage>)threadLocal.get();
      }
      return msgMap.get(clazz);

    //return msgMap.get(mergeClazz(msgFormat));
  }

  /**
   * DOCUMENT ME!
   *
   * @param msgFormat DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  static private String mergeClazz(String msgFormatValue) {
    StringBuilder clazz = new StringBuilder();

    clazz.append(config.getStringArray("package")[config.getStringArray(
        "package").length-1]).append('.');
    clazz.append(msgFormatValue.substring(0,1).toUpperCase()).append(msgFormatValue.substring(1));
    clazz.append(config.getStringArray("classSuffix")[config.getStringArray(
          "classSuffix").length-1]);

    if (logger.isDebugEnabled()) {
      logger.debug("mergeClazz( String ) - clazz=" + clazz);
    }

    return clazz.toString();
  }

  /**
   * 加载配置(message.properties或message.xml)
   *
   * @return 配置对象实例
   *
   * @throws Exception
   */
  static private Configuration loadConfig() throws Exception {
      Configuration cfg = null;
      try {
        cfg = Config.loadAndWatchConfig("msg.properties");
    } catch (Exception e) {
        cfg = Config.loadAndWatchConfig("msg.xml");
    }
    return cfg ;
  }

  /**
   * DOCUMENT ME!
   */
  static private void init() {

    try {
      config = loadConfig();
    } catch (Exception e) {
      logger.error("init() - Exception e=" + e, e);
      return;
    }

    String[] msgformates = config.getStringArray("msgformat");
    Map<String, IMessage> msgMap = new HashMap<String, IMessage>();
    for (String msgformat : msgformates) {
      try {
        String clazz = mergeClazz(msgformat);

        if ((null == clazz) || (clazz.trim().length() < 1)) {
          continue;
        } else {
            clazzMap.put(msgformat,clazz);
            msgMap.put(clazz, (IMessage) Class.forName(clazz).newInstance());
        }

        threadLocal.set(msgMap);
      } catch (Exception e) {
        logger.warn("init() - Exception e=" + e, e);
      }
    }

    if (logger.isInfoEnabled()) {
      logger.info("init() - msgMap=" + msgMap);
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
      Log4jConfig.listening();
      MessageFactory test = MessageFactory.newInstance();
      IMessage iMsg = test.createMessage("msgc");
      System.out.print(iMsg);
  }
}
