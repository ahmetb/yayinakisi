package com.avior.yayinakisi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UrlOpener {
	
	public static String getContents(URL url){
	    InputStream stream = null;
		try {
			stream = url.openStream();
		} catch (IOException e) {
			e.printStackTrace(); // TODO log
			return null;
		}
		
	    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String contents = bufferStream(reader);

		return contents;
	}

	public static String bufferStream(BufferedReader reader){
		StringBuffer contents = new StringBuffer();
		try {
			String c;
			c = reader.readLine();
			while (c != null) {
		      contents.append(c);
		      c = reader.readLine();
		    }
		} catch (IOException e) {
			e.printStackTrace(); // TODO log
			return null;
		}
		return contents.toString();
	}
}
