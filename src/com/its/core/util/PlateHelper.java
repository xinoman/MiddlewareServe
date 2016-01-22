/**
 * 
 */
package com.its.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建日期 2013-1-30 下午03:32:27
 * @author GuoPing.Wu QQ:365175040
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class PlateHelper {
	
	public static final char PLATE_PLACEHOLDER = '@';
	
	public static Map<String,String> IGNORE_PLATE_CHAR_MAP = new HashMap<String,String>();
	
	static{
		IGNORE_PLATE_CHAR_MAP.put("0", "O");
		IGNORE_PLATE_CHAR_MAP.put("1", "I");
		IGNORE_PLATE_CHAR_MAP.put("6", "G");
		IGNORE_PLATE_CHAR_MAP.put("8", "B");
		IGNORE_PLATE_CHAR_MAP.put("9", "8");
		IGNORE_PLATE_CHAR_MAP.put("B", "8");
		IGNORE_PLATE_CHAR_MAP.put("C", "O");
		IGNORE_PLATE_CHAR_MAP.put("D", "O");
		IGNORE_PLATE_CHAR_MAP.put("G", "6");
		IGNORE_PLATE_CHAR_MAP.put("I", "1");
		IGNORE_PLATE_CHAR_MAP.put("O", "0");
		IGNORE_PLATE_CHAR_MAP.put("Q", "O");
	}
	
	/**
	 * 获取与给定的号牌号码匹配的模糊化的车牌号码
	 * @param plate
	 * @param ignoreFirstChar 是否忽略第一个字符，如果为true,则使用占位符替换
	 * @param leastCheckBitNum 最少需要检测多少位才匹配成功
	 * @return
	 */
	public static String getMatchPlate(String plate,boolean ignoreFirstChar,int leastCheckBitNum){
		if(plate==null) return null;
		plate = plate.trim().toUpperCase();
		StringBuilder sb = new StringBuilder(7);
		int len = plate.length();		
		int i = 0;
		int sameCount = 0;
		if(ignoreFirstChar){
			sb.append(PLATE_PLACEHOLDER);
			i = 1;
		}
		for(;i<len;i++){			
			if(IGNORE_PLATE_CHAR_MAP.containsKey(String.valueOf(plate.charAt(i)))){
				sb.append(PLATE_PLACEHOLDER);
			}
			else{
				sameCount++;
				sb.append(plate.charAt(i));
			}
		}		
		String result = null;
		if(sameCount>=leastCheckBitNum){
			result = sb.toString();
		}
		return result;
	}
	
	public static int getMatchNumber(String plateA,String plateB){
		int matchNumber = 0;
		if(StringHelper.isEmpty(plateA) || StringHelper.isEmpty(plateB)){
			return matchNumber;
		}
		
		int len = Math.min(plateA.length(),plateB.length());
		for(int i=0;i<len;i++){
			if(plateA.charAt(i)==plateB.charAt(i)) matchNumber++;
		}
		
		return matchNumber;
	}
	
	/**
	 * 获取有效的车牌位数
	 * @param plate
	 * @return
	 */
	public static int getValidNumber(String plate){
		int validNum = 0;
		if(StringHelper.isEmpty(plate)){
			return 0;
		}
		int len = plate.length();
		for(int i=0;i<len;i++){
			if(PLATE_PLACEHOLDER!=plate.charAt(i) && '.'!=plate.charAt(i)){
				validNum++;
			}
		}
		return validNum;
	}
	
	public static void main(String[] args) {
		String plate = "粤B12345";
		System.out.println(plate+"\t "+PlateHelper.getMatchPlate(plate, true,4));
//		System.out.println(PlateHelper.getMatchNumber("粤A12345", "中B@234@"));
	}	

}
