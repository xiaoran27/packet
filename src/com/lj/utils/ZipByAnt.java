
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
 * 利用ant的zip进行压缩
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class ZipByAnt {
  /**
   * 利用ant的zip进行压缩
   *
   * @param desPathName ZIP文件名
   * @param srcPathName 被压缩的文件或目录
   * @param includes 压缩文件包含的匹配模式
   * @param excludes 压缩文件不包含的匹配模式
   */
  public static void zip(String desPathName, String srcPathName, String includes,
    String excludes) {
    File zipFile = new File(desPathName);
    File srcdir = new File(srcPathName);

    if (!srcdir.exists()) {
      throw new RuntimeException(srcPathName + "不存在！");
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
//	  zip("G:/worktmp/test中文.zip","G:/worktmp/imsitrace中文.dsv",null,null); 
//	  zip("G:/worktmp/test中文.zip","G:/worktmp","imsitrace*.dsv",null); 
	  
	  zip("G:/worktmp/中文目录/中文.zip","G:/worktmp/中文目录/中文.dsv",null,null); 
	  
	  
  }
}
