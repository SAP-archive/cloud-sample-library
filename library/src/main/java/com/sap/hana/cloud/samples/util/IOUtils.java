package com.sap.hana.cloud.samples.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * This utility class is used for Stream manipulations.
 * */
public class IOUtils {

	private static final int BUFFER_SIZE = 1024;

	/**
	 * This method extracts a byte array out of an {@link java.io.InputStream InputStream}.
	 *
	 *  @param inputStream - the stream we want to extract a byte[] from.
	 *  @return the inputstream's content as a byte array.
	 * */
	public static byte[] toByteArray(InputStream inputStream)
	        throws IOException {

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int length = 0;
	    while ((length = inputStream.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    return baos.toByteArray();
	}

	/**
	 * This method copies the content of an {@link java.io.InputStream InputStream}
	 * into an {@link java.io.OutputStream OutputStream}.
	 *
	 * @param in - the InputStream that the method reads from.
	 * @param out - the OutputStream that the method writes into.
	 * */
	public static void writeToOutputStream(InputStream in, OutputStream out)
			throws IOException {

		byte[] buf = new byte[BUFFER_SIZE];
        int len;
        while ((len = in.read(buf)) > 0){
          out.write(buf, 0, len);
        }
	}
	
	/**
	 * This method reads the data sent via the request and returns a string representation of it.
	 * 
	 * @param request - the request from which data will be read.
	 * @return a string representation of the data.
	 * */
	public static String extractDataFromRequest (HttpServletRequest request) throws IOException {
		
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
               builder.append(line);
        }
        
        return builder.toString();
	}

}
