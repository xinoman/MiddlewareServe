
package com.its.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;



public final class PropertiesHelper {

	public static boolean getBoolean(String property, Properties properties) {
		return Boolean.valueOf( properties.getProperty(property) ).booleanValue();
	}

	public static boolean getBoolean(String property, Properties properties, boolean defaultValue) {
		String propValue = properties.getProperty(property);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : Boolean.valueOf(propValue).booleanValue();
	}

	public static int getInt(String property, Properties properties, int defaultValue) {
		String propValue = properties.getProperty(property);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : Integer.parseInt(propValue);
	}
	
	public static int getInt(String propertyPrefix,int no,String propertyPostfix, XMLProperties properties,int defaultValue) {
		String propValue = properties.getProperty(propertyPrefix,no,propertyPostfix);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : Integer.parseInt(propValue);
	}		
	
	public static long getLong(String propertyPrefix,int no,String propertyPostfix, XMLProperties properties,long defaultValue) {
		String propValue = properties.getProperty(propertyPrefix,no,propertyPostfix);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : Long.parseLong(propValue);
	}		
	
	public static float getFloat(String propertyPrefix,int no,String propertyPostfix, XMLProperties properties,float defaultValue) {
		String propValue = properties.getProperty(propertyPrefix,no,propertyPostfix);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : Float.parseFloat(propValue);
	}	
	
	public static String getString(String property, Properties properties, String defaultValue) {
		String propValue = properties.getProperty(property);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : propValue;
	}

	public static String getString(String propertyPrefix,int no,String propertyPostfix, XMLProperties properties,String defaultValue) {
		String propValue = properties.getProperty(propertyPrefix,no,propertyPostfix);
		return (StringHelper.isEmpty(propValue)) ? defaultValue : propValue;
	}	
	
	public static Integer getInteger(String property, Properties properties) {
		String propValue = properties.getProperty(property);
		return (StringHelper.isEmpty(propValue)) ? null : Integer.valueOf(propValue);
	}
	


	public static Map toMap(String property, String delim, Properties properties) {
		Map<String, String> map = new HashMap<String, String>();
		String propValue = properties.getProperty(property);
		if (propValue!=null) {
			StringTokenizer tokens = new StringTokenizer(propValue, delim);
			while ( tokens.hasMoreTokens() ) {
				map.put(
					tokens.nextToken(),
					tokens.hasMoreElements() ? tokens.nextToken() : ""
				);
			}
		}
		return map;
	}

	/**
	 * replace a property by a starred version
	 * 
	 * @param props properties to check
	 * @param key proeprty to mask
	 * @return cloned and masked properties
	 */
	public static Properties maskOut(Properties props, String key) {
		Properties clone = (Properties) props.clone();
		if (clone.get(key) != null) {
			clone.setProperty(key, "****");
		}
		return clone;
	}
	


	private PropertiesHelper() {}
}






