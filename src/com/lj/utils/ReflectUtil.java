
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-7-28
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-7-29
* M ReflectUtil.newInstance()
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.lj.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

/**
 *  利用reflect机制对对象进行初始化
 *  
 * @author wuxr
 *
 */
public class ReflectUtil {
	
	public static Object newInstance(String clazz) throws Exception {
		  Class clz = Class.forName(clazz);
		  Object obj = clz.newInstance();
		  Field[] fields = clz.getDeclaredFields();
		  for(int i=0; i<fields.length; i++){
			  fields[i].setAccessible(true);
			  Object v = fields[i].get(obj);
			  //Type type = fields[i].getType();  //Xxxx.class
			  String typename = fields[i].getType().getName();
			  //System.out.println(typename+'\t'+fields[i].getGenericType()+'\t'+v);
			  if (v==null){
				try {
					v = Class.forName(typename).newInstance();
				} catch (InstantiationException e) {
					//array || not default constuct
					//e.printStackTrace();
				}
			  }
			  if ( v instanceof Number){
				  fields[i].set(obj, i);
			  }else if (v instanceof String){
				  fields[i].set(obj, "S"+i);
			  }else if (v instanceof Character){
				  fields[i].set(obj, (char)('0'+i));
			  }else if (v instanceof Date){
				  fields[i].set(obj, new Date(i));
			  }else if (typename.equals("java.sql.Timestamp")||typename.equals("Timestamp")){
				  fields[i].set(obj, new Timestamp(i));
			  }else{
				  //array | object
				  int pos = typename.indexOf("[L");
				  if (pos>-1){
					  String clazz2 = typename.substring(pos+2,typename.length()-1);  //class [Lsimn.msg.SimnRnmishowInfo;
					  Class classType=Class.forName(clazz2);              
					  Object fieldval= Array.newInstance(classType,1);
					  Array.set(fieldval,0,newInstance(clazz2));
					  fields[i].set(obj, fieldval);  
				  }else{
					  String clazz2 = typename;
					  fields[i].set(obj, newInstance(clazz2));
				  }
			  }
		  }
		  
		  return obj;
	}
	
}
