package com.lj.util.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.lj.util.logging.Logger.Level;

/**
 * @author xiaoran27
 */
public class FileAppender extends Appender {
	public static final File logfile = new File(System.getProperty("user.home")+ "/log", "logfile.txt");
	private PrintWriter writer;
	
	public FileAppender(Level level) {
		setLevel(level);
		try {
			if (!logfile.exists()) logfile.createNewFile();
			writer = new PrintWriter(new FileOutputStream(logfile, true), true);
		} catch (IOException e) {
			Logger.error("Unable to create logfile "+logfile.getAbsolutePath(), e);
		}
	}
	
	@Override
	void append(Level l, String message, Throwable t) {
		if (level.compareTo(l) >= 0 && writer != null) {
			writer.println(format(l, message));
			if (t != null) t.printStackTrace(writer);
		}
	}

}