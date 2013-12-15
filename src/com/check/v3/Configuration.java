/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.check.v3;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Properties;


public class Configuration {
	private static Properties defaultProperty;

	static {
		init();
	}

	/* package */static void init() {
		defaultProperty = new Properties();
		defaultProperty.setProperty("cloudcheck.debug", "true");
		defaultProperty.setProperty("cloudcheck.clientURL",
				"http://sandin.tk/cloudcheck.xml");
		defaultProperty.setProperty("cloudcheck.http.userAgent",
				"cloudcheck 1.0");
		defaultProperty.setProperty("cloudcheck.http.connectionTimeout",
				"20000");
		defaultProperty.setProperty("cloudcheck.http.readTimeout", "120000");
		defaultProperty.setProperty("cloudcheck.http.retryCount", "3");
		defaultProperty.setProperty("cloudcheck.http.retryIntervalSecs", "10");
		defaultProperty.setProperty("cloudcheck.oauth.baseUrl","http://fanfou.com/oauth");
		defaultProperty.setProperty("cloudcheck.async.numThreads", "1");
		defaultProperty.setProperty("cloudcheck.clientVersion", "3.0");

		String t4jProps = "cloudcheck.properties";
		boolean loaded = loadProperties(defaultProperty, "."
				+ File.separatorChar + t4jProps)
				|| loadProperties(defaultProperty,
						Configuration.class.getResourceAsStream("/" + t4jProps));
	}

	private static boolean loadProperties(Properties props, String path) {
		try {
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				props.load(new FileInputStream(file));
				return true;
			}
		} catch (Exception ignore) {
		}
		return false;
	}

	private static boolean loadProperties(Properties props, InputStream is) {
		try {
			props.load(is);
			return true;
		} catch (Exception ignore) {
		}
		return false;
	}

	public static boolean useSSL() {
		return getBoolean("cloudcheck.http.useSSL");
	}

	public static String getScheme() {
		return useSSL() ? "https://" : "http://";
	}

	public static String getCilentVersion() {
		return getProperty("cloudcheck.clientVersion");
	}

	public static String getCilentVersion(String clientVersion) {
		return getProperty("cloudcheck.clientVersion", clientVersion);
	}

	public static String getClientURL() {
		return getProperty("cloudcheck.clientURL");
	}

	public static String getClientURL(String clientURL) {
		return getProperty("cloudcheck.clientURL", clientURL);
	}

	public static int getConnectionTimeout() {
		return getIntProperty("cloudcheck.http.connectionTimeout");
	}

	public static int getConnectionTimeout(int connectionTimeout) {
		return getIntProperty("cloudcheck.http.connectionTimeout",
				connectionTimeout);
	}

	public static int getReadTimeout() {
		return getIntProperty("cloudcheck.http.readTimeout");
	}

	public static int getReadTimeout(int readTimeout) {
		return getIntProperty("cloudcheck.http.readTimeout", readTimeout);
	}

	public static int getRetryCount() {
		return getIntProperty("cloudcheck.http.retryCount");
	}

	public static int getRetryCount(int retryCount) {
		return getIntProperty("cloudcheck.http.retryCount", retryCount);
	}

	public static int getRetryIntervalSecs() {
		return getIntProperty("cloudcheck.http.retryIntervalSecs");
	}

	public static int getRetryIntervalSecs(int retryIntervalSecs) {
		return getIntProperty("cloudcheck.http.retryIntervalSecs",
				retryIntervalSecs);
	}

	public static String getUserAgent() {
		return getProperty("cloudcheck.http.userAgent");
	}

	public static String getUserAgent(String userAgent) {
		return getProperty("cloudcheck.http.userAgent", userAgent);
	}

	public static boolean getBoolean(String name) {
		String value = getProperty(name);
		return Boolean.valueOf(value);
	}

	public static int getIntProperty(String name) {
		String value = getProperty(name);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static int getIntProperty(String name, int fallbackValue) {
		String value = getProperty(name, String.valueOf(fallbackValue));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static long getLongProperty(String name) {
		String value = getProperty(name);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static String getProperty(String name) {
		return getProperty(name, null);
	}

	public static String getProperty(String name, String fallbackValue) {
		String value;
		try {
			value = System.getProperty(name, fallbackValue);
			if (null == value) {
				value = defaultProperty.getProperty(name);
			}
			if (null == value) {
				String fallback = defaultProperty.getProperty(name
						+ ".fallback");
				if (null != fallback) {
					value = System.getProperty(fallback);
				}
			}
		} catch (AccessControlException ace) {
			// Unsigned applet cannot access System properties
			value = fallbackValue;
		}
		return replace(value);
	}

	private static String replace(String value) {
		if (null == value) {
			return value;
		}
		String newValue = value;
		int openBrace = 0;
		if (-1 != (openBrace = value.indexOf("{", openBrace))) {
			int closeBrace = value.indexOf("}", openBrace);
			if (closeBrace > (openBrace + 1)) {
				String name = value.substring(openBrace + 1, closeBrace);
				if (name.length() > 0) {
					newValue = value.substring(0, openBrace)
							+ getProperty(name)
							+ value.substring(closeBrace + 1);

				}
			}
		}
		if (newValue.equals(value)) {
			return value;
		} else {
			return replace(newValue);
		}
	}

	public static int getNumberOfAsyncThreads() {
		return getIntProperty("cloudcheck.async.numThreads");
	}

	public static boolean getDebug() {
		return getBoolean("cloudcheck.debug");

	}


}
