/**
 * 
 */
package com.lj.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author Xiaoran27
 * Date Aug 15, 2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TextFormat {
	static public final String YYYY_MM_DD = "yyyy-MM-dd";
	static public final String YYYYMMDD = "yyyyMMdd";
	static public final String HH_MM_SS = "HH:mm:ss";
	static public final String HHMMSS = "HHmmss";
	
	static final String DATE_FORMAT = YYYY_MM_DD;
	static final String TIME_FORMAT = HH_MM_SS;
	static final String DATETIME_FORMAT = DATE_FORMAT+" "+TIME_FORMAT;
	
	static public String getDateTime(){
		return new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
	}
	
	static public String getDateTime(String format){
		return new SimpleDateFormat(format).format(new Date());
	}
	
	static public String getDate(){
		return new SimpleDateFormat(DATE_FORMAT).format(new Date());
	}
	
	static public String getDate(String format){
		return new SimpleDateFormat(format).format(new Date());
	}
	
	static public String getTime(){
		return new SimpleDateFormat(TIME_FORMAT).format(new Date());
	}
	
	static public String getTime(String format){
		return new SimpleDateFormat(format).format(new Date());
	}
	
	static public int getWeek(){
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
	
	static public String getDateTime(Date date){
		return new SimpleDateFormat(DATETIME_FORMAT).format(date);
	}
	
	static public String getDateTime(String format,Date date){
		return new SimpleDateFormat(format).format(date);
	}
	
	static public String getDate(Date date){
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
	
	static public String getDate(String format,Date date){
		return new SimpleDateFormat(format).format(date);
	}
	
	static public String getTime(Date date){
		return new SimpleDateFormat(TIME_FORMAT).format(date);
	}	
	
	static public String getTime(String format,Date date){
		return new SimpleDateFormat(format).format(date);
	}
	
	static public int getWeek(Date date){
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		return now.get(Calendar.DAY_OF_WEEK);
 	}

	static public String getDateTime(long millis){
		return new SimpleDateFormat(DATETIME_FORMAT).format(new Date(millis));
	}
	
	static public String getDateTime(String format,long millis){
		return new SimpleDateFormat(format).format(new Date(millis));
	}
	
	static public String getDate(long millis){
		return new SimpleDateFormat(DATE_FORMAT).format(new Date(millis));
	}
	
	static public String getDate(String format,long millis){
		return new SimpleDateFormat(format).format(new Date(millis));
	}
	
	static public String getTime(long millis){
		return new SimpleDateFormat(TIME_FORMAT).format(new Date(millis));
	}
	
	static public String getTime(String format,long millis){
		return new SimpleDateFormat(format).format(new Date(millis));
	}
	
	static public int getWeek(long millis){
		Calendar now = Calendar.getInstance();
		now.setTime(new Date(millis));
		return now.get(Calendar.DAY_OF_WEEK);
	}
	
	public static void main(String[] args) {
		System.out.println(TextFormat.getDateTime());
		System.out.println(TextFormat.getDate());
		System.out.println(TextFormat.getTime());
		System.out.println(TextFormat.getWeek());
		
		System.out.println(TextFormat.getDateTime(TextFormat.YYYYMMDD+TextFormat.HHMMSS));
		System.out.println(TextFormat.getDate(TextFormat.YYYYMMDD));
		System.out.println(TextFormat.getTime(TextFormat.HHMMSS));
		System.out.println(TextFormat.getWeek());
	}
	
}
