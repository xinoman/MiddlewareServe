/**
 * 
 */
package com.its.core.test;

import com.its.core.util.StringHelper;

/**
 * 创建日期 2013-6-16 下午06:25:28
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class Test{	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String fileName = "闯红灯-192.168.1.105-2015-04-14#164155#098_2_川F6H890.jpg";
//		System.out.println(fileName.substring(0, fileName.lastIndexOf("_")-6));
		String[] fileNameSplit = StringHelper.split(fileName, "-");
//		System.out.println(fileNameSplit[3]+fileNameSplit[4]);
		System.out.println(StringHelper.replace(fileNameSplit[1], ".", ""));
		String timeStr = fileName.substring(fileName.indexOf("#")-10,fileName.indexOf("#")+7);		
		timeStr = StringHelper.replace(StringHelper.replace(timeStr, "#", ""), "-", "");
		System.out.println(timeStr);		
		String[] fileNameSplit2 = StringHelper.split(fileName, "_");
		System.out.println(fileNameSplit2[1]);
		System.out.println(StringHelper.replace(fileNameSplit2[2], ".jpg", ""));

		String line = "12";
		while(line.length()<2){
			line ="0"+line;
		}
		System.out.println(line);

	}

}
