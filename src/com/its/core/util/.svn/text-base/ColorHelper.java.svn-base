/**
 * 
 */
package com.its.core.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建日期 2012-12-13 上午09:49:56
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ColorHelper {
	
	private static Map<String,Color> colorMap = new HashMap<String,Color>();

	static{
		colorMap.put("black", Color.black);
		colorMap.put("white", Color.white);
		colorMap.put("blue", Color.blue);
		colorMap.put("cyan", Color.cyan);
		colorMap.put("darkGray", Color.darkGray);
		colorMap.put("gray", Color.gray);
		colorMap.put("green", Color.green);
		colorMap.put("lightGray", Color.lightGray);
		colorMap.put("magenta", Color.magenta);
		colorMap.put("orange", Color.orange);
		colorMap.put("pink", Color.pink);
		colorMap.put("red", Color.red);
		colorMap.put("yellow", Color.yellow);		
	}
	
	public static final Color getColor(String str){
		if(StringHelper.isNotEmpty(str)){
			return colorMap.get(str.trim().toLowerCase());
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(ColorHelper.getColor("black"));
	}

}
