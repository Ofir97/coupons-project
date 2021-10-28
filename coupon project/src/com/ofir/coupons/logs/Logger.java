package com.ofir.coupons.logs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	
	private static final String EXCEPTIONS_LOG_FILE_PATH = "exceptions_log.txt";
	private static final String OPERATIONS_LOG_FILE_PATH = "operations_log.txt";
	private static Logger instance = null;
	private File exceptionLogFile;
	private File operationsLogFile;
	
	private Logger() {
		exceptionLogFile = new File(EXCEPTIONS_LOG_FILE_PATH);
		operationsLogFile = new File(OPERATIONS_LOG_FILE_PATH);
	}
	
	public static Logger getInstance() {
		if (instance == null) 
			instance = new Logger();
		return instance;
	}

	/**
	 * this method writes the successful operation to log file and prints the message.
	 * 
	 * @param text is the message of the successful operation that has been made
	 * @throws IOException
	 */
	public void logOperationAndPrint(String text) throws IOException {
		writeToFile(operationsLogFile, text);
		System.out.println(text);
	}
	
	/**
	 * this method writes the exception message to log file and prints the message.
	 * 
	 * @param text is the message of the exception that has been thrown
	 * @throws IOException
	 */
	public void logExceptionAndPrint(String text) throws IOException {
		writeToFile(exceptionLogFile, text);
		System.out.println(text);
	}

	private static void writeToFile(File file, String text) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
		bufferedWriter.write(text+"\n\n");
		bufferedWriter.close();
	}



}
