package org.paulpell.miam.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Log
{

	private static FileWriter writer_; 
	private static PrintWriter printWriter_;
	static
	{
		String tmpdir = System.getProperty("java.io.tmpdir");
		String logFilePath = tmpdir + File.separator + "log.miam" + System.currentTimeMillis();
		System.out.println("New Log file: " + logFilePath);
		File logFile = new File(logFilePath);
		try
		{
			logFile.createNewFile();
			if (!logFile.canWrite())
				throw new Exception("No write permissions on " + logFilePath);
			writer_ = new FileWriter(logFile);
			logMsg("Startup");
			printWriter_ = new PrintWriter(writer_);
		}
		catch (Exception e)
		{
			System.err.println("Log initialisation failed: " + e.getMessage());
		}
	}
	
	private Log() {
	}
	
	public static void logMsg(String msg)
	{
		try
		{
			String s = System.currentTimeMillis() + " Info    " + msg + "\n";
			writer_.write(s);
			writer_.flush();
			System.out.println(s);
		}
		catch (Exception e) {}
	}

	public static void logErr(String msg)
	{
		try
		{
			String s = System.currentTimeMillis() + " ERR     " + msg;
			writer_.write(s);
			writer_.flush();
			System.err.println(s);
		}
		catch (Exception e) {}
	}
	
	public static void logException(Exception e)
	{
		try
		{
			if ( null != e )
			{
				e.printStackTrace(printWriter_);
				printWriter_.flush();
			}
		}
		catch (Exception e2) {}
	}
}
