package org.paulpell.miam;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Log
{
	// initialize local variables, but a call to initLog is needed
	private static Logger logger_ = Logger.getLogger("Snakesss");
	private static StreamHandler handler_;
	
	private Log()
	{
		
	}
	
	private static void consoleFallback()
	{
		handler_ = new ConsoleHandler();
		logger_.addHandler(handler_);
		log(Level.INFO, "Initialized fallback log to console");
	}
	
	private static void finetuneHandler()
	{
		handler_.setLevel(Level.ALL);
	}
	
	/**
	 * 
	 * @param filepattern The file pattern, as provided to FileHandler
	 */
	public static void initLog(String filepattern)
	{
		boolean success = false;
		try {
			handler_ = new FileHandler(filepattern);
			logger_.addHandler(handler_);
			log(Level.INFO, "Initialized log to file "+ filepattern);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ( ! success)
			consoleFallback();
		finetuneHandler();
	}
	
	public static void log(Level level, String msg)
	{
		logger_.log(level, msg);
	}
}
