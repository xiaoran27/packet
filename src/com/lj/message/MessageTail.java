
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
*LAST UPDATED:: $Id: MessageTail.java,v 1.2 2007/05/30 06:01:08 xiaoran27 Exp $
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
* V,suntf,2007-3-19
*   create
*-----------------------------------------------------------------------------*
* V,suntf,2007-5-18
* + toString()
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.message;

import org.apache.commons.lang.builder.ToStringBuilder;



/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
  */
public class MessageTail {
  /**
   * 应答标志: 0-成功;!0-其他错误;
   */
  private int answer;

  /**
   * OPTIONAL 具体错误信息: ""-没有错误;
   */
  private String errorInfo;

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public int getAnswer() {
    return this.answer;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String getErrorInfo() {
    return this.errorInfo;
  }

  /**
   * DOCUMENT ME!
   *
   * @param answer DOCUMENT ME!
   */
  public void setAnswer(int answer) {
    this.answer = answer;
  }

  /**
   * DOCUMENT ME!
   *
   * @param errorInfo DOCUMENT ME!
   */
  public void setErrorInfo(String errorInfo) {
    this.errorInfo = errorInfo;
  }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this)
                .append("answer", this.answer)
                .append("errorInfo", this.errorInfo)
                .toString();
    }



}
