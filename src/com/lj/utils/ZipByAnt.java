
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-8-17
*   create
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import java.io.File;


/**
 * ����ant��zip����ѹ��
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class ZipByAnt {
  /**
   * ����ant��zip����ѹ��
   *
   * @param desPathName ZIP�ļ���
   * @param srcPathName ��ѹ�����ļ���Ŀ¼
   * @param includes ѹ���ļ�������ƥ��ģʽ
   * @param excludes ѹ���ļ���������ƥ��ģʽ
   */
  public static void zip(String desPathName, String srcPathName, String includes,
    String excludes) {
    File zipFile = new File(desPathName);
    File srcdir = new File(srcPathName);

    if (!srcdir.exists()) {
      throw new RuntimeException(srcPathName + "�����ڣ�");
    } else if (srcdir.isFile()){
    	includes = srcdir.getName();
    	excludes = null;
    	srcdir = srcdir.getParentFile();
    }

    Project prj = new Project();
    Zip zip = new Zip();
    zip.setProject(prj);
    zip.setDestFile(zipFile);

    FileSet fileSet = new FileSet();
    fileSet.setProject(prj);
    fileSet.setDir(srcdir);
    if (null!=includes && includes.length()>1){
    	fileSet.setIncludes(includes);
    }
    if (null!=excludes && excludes.length()>1){
    	fileSet.setExcludes(excludes);
    }
    zip.addFileset(fileSet);

    zip.execute();
    
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
//	  zip("G:/worktmp/test����.zip","G:/worktmp/imsitrace����.dsv",null,null); 
//	  zip("G:/worktmp/test����.zip","G:/worktmp","imsitrace*.dsv",null); 
	  
	  zip("G:/worktmp/����Ŀ¼/����.zip","G:/worktmp/����Ŀ¼/����.dsv",null,null); 
	  
	  
  }
}
