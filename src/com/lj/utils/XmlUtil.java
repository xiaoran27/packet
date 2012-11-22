
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
*LAST UPDATED:: $Id: XmlUtil.java,v 1.1 2007/04/19 01:46:32 xiaoran27 Exp $
**
*PRINCIPAL AUTHOR:: Liangjiang Incorporation                                  *
**
*PRUCT NAME      :: PleaseFillProductName                                     *
**
*LANGUAGE        :: ASNI                                                      *
**
*TARGET ENVIRONMENT:: JRE 1.5+                                                *
**
*DESCRIPTION     :: 分析xml文件
**
*************************** END OF SOURCE FILE INFORMATION ********************

************************* CHANGE REPORT HISTORY *******************************
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2006-11-27
*   create
* + 支持File,InputStream,Reader,URL构造
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class XmlUtil {
  //~ Instance fields ----------------------------------------------------------

  /**
   * DOCUMENT ME!
   */
  private File xmlFile = null;
  private InputStream is = null;
  private Reader reader = null;
  private URL url = null;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new XmlUtil object.
   *
   * @param xmlFile DOCUMENT ME!
   */
  public XmlUtil(File xmlFile) {
    this.xmlFile = xmlFile;
  }
  
  public XmlUtil(InputStream is) {
    this.is = is;
  }
  
  public XmlUtil(Reader reader) {
    this.reader = reader;
  }
  
  public XmlUtil(URL url) {
	    this.url = url;
	  }

  //~ Methods ------------------------------------------------------------------

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws DocumentException DOCUMENT ME!
   */
  public Document getDocument() throws DocumentException {
    SAXReader saxReader = new SAXReader();
    Document doc = null;
    
    if (null!=xmlFile){
    	doc = saxReader.read(xmlFile);
    }else if(null!=is){
    	doc = saxReader.read(is);
    }else if(null!=is){
    	doc = saxReader.read(reader);
    }else if(null!=url){
    	doc = saxReader.read(url);
    }

    return doc;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws DocumentException DOCUMENT ME!
   */
  public Element getRoot() throws DocumentException {
    Document doc = getDocument();

    return doc.getRootElement();
  }

  /**
     * 获取指定标记(id)的元素
     *
     * @param e Element 元素
     * @param id String 要找的标记
     *
     * @return List<Element> 所有符合条件的元素
     */
  public List<Element> getElement(Element e, String id) {
    List<Element> allFound = new LinkedList<Element>();

    if ((null != e) && e.getName().equals(id)) {
      allFound.add(e);

      return allFound;
    }

    for (int i = 0; i < e.elements().size(); i++) {
      List<Element> found = getElement((Element) e.elements().get(i), id);
      allFound.addAll(found);
    }

    return allFound;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
  }
}
