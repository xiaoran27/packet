package com.lj.util.logging;

import com.lj.util.logging.Logger.Level;


/**
 * @author xiaoran27
 */
public class ConsoleAppender extends Appender {
	
	public ConsoleAppender(Level level) {
		setLevel(level);
	}
	
	@Override
	void append(Level l, String message, Throwable t) {
		if (level.compareTo(l) >= 0) {
			System.out.println(format(l, message));
			if (t != null) t.printStackTrace();
		}
	}		
}