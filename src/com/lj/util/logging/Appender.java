package com.lj.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lj.util.logging.Logger.Level;

/**
 * @author xiaoran27
 */
public abstract class Appender {
	protected Level level;
	private DateFormat df = new SimpleDateFormat("MM/dd HH:mm:ss");
	
	abstract void append(Level l, String message, Throwable t);
	
	String format(Level l, String message) {
		return new StringBuffer().append(df.format(new Date())).append(" [")
					.append(l).append("] - ").append(message).toString();
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level l) {
		if (l != null) this.level = l;
	}
}