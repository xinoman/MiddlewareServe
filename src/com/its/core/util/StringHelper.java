/*
 * Title:
 * 创建日期 2006-5-19
 * @author GuoPing.Wu
 * Copyright: UniHz Technologies CO.,LTD.
 */
package com.its.core.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

	public static String[] splitByTokenizer(String s, String spliter) {
		StringTokenizer strToken = new StringTokenizer(s, spliter, false);
		StringTokenizer childToken = null;
		int tokenNum = strToken.countTokens();
		String sa[] = new String[tokenNum];
		int i = 0;
		while (strToken.hasMoreTokens()) {
			childToken = new StringTokenizer(strToken.nextToken());
			sa[i++] = childToken.nextToken();
		}

		return sa;

	}
	
	public static String[] split(String s, String spliter) {
		String temp = s;
		if (s==null || s.trim().equals("")) {
			return new String[] {};
		}
		List<String> list = new ArrayList<String>();
		int index, len = spliter.length();
		while ( (index = temp.indexOf(spliter)) != -1) {
			list.add(temp.substring(0, index));
			temp = temp.substring(index + len);
		}
		list.add(temp);
		String[] rs = new String[list.size()];
		list.toArray(rs);
		return rs;
	}	

	/**
	 * 分隔字符串，不包括空（'',null,'null')字符
	 * @param s
	 * @param spliter
	 * @return
	 */
	public static String[] splitExcludeEmpty(String s, String spliter) {
		ArrayList v = new ArrayList();
		String temp = s;
		if (s==null || s.trim().equals("")) {
			return new String[] {};
		}
		int index, len = spliter.length();
		while ( (index = temp.indexOf(spliter)) != -1) {
			String val = temp.substring(0, index);
			if(StringHelper.isNotEmpty(val)) v.add(val);
			temp = temp.substring(index + len);
		}
		if(StringHelper.isNotEmpty(temp)) v.add(temp);
		String[] rs = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			rs[i] = (String) v.get(i);
		}
		return rs;
	}	
	
	/*
	public static String[] split(String s, String spliter) {
		Vector<String> v = new Vector<String>();
		String temp = s;
		if (s==null || s.trim().equals("")) {
			return new String[] {};
		}
		int index, len = spliter.length();
		while ( (index = temp.indexOf(spliter)) != -1) {
			v.addElement(temp.substring(0, index));
			temp = temp.substring(index + len);
		}
		v.addElement(temp);
		String[] rs = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			rs[i] = (String) v.elementAt(i);
		}
		return rs;
	}
	*/
	
	public static byte[] copyByte(byte[] byteArr,int from ,int to){
		byte[] result = new byte[to-from];
		for(int i=from;i<to;i++){
			result[i-from] = byteArr[i];
		}
		return result;
	}
	
	public static String replace(String s, String tobeReplaced, String replacer) {
		String right = "";
		StringBuffer sr = new StringBuffer("");
		int index = -1;
		String sb = tobeReplaced, st = replacer;

		if (s == "" || sb == "") {
			return s;
		}
		right = s;

		while ( (index = right.indexOf(sb)) != -1) {
			sr.append(right.substring(0, index)).append(st);
			right = right.substring(index + sb.length());
		}
		return sr.append(right).toString();
	}	
	
	/**
	 * 获取boolean值
	 * @param strBool
	 * @return
	 */
	public static boolean getBoolean(String strBool){
		boolean result = false;
		if(StringHelper.isNotEmpty(strBool)){
			String tmpBool = strBool.trim().toUpperCase();
			if("TRUE".equals(tmpBool) || "Y".equals(tmpBool) || "YES".equals(tmpBool) || "T".equals(tmpBool) || "1".equals(tmpBool)){
				result = true;
			}
		}
		return result;
	}	
	
	/**
	 * 获取boolean值
	 * @param strBool
	 * @param defaultValue 为空或其它值时的缺省值
	 * @return
	 */
	public static boolean getBoolean(String strBool,boolean defaultValue){
		boolean result = defaultValue;
		if(StringHelper.isNotEmpty(strBool)){
			String tmpBool = strBool.trim().toUpperCase();
			if("TRUE".equals(tmpBool) || "Y".equals(tmpBool) || "YES".equals(tmpBool) || "T".equals(tmpBool) || "1".equals(tmpBool)){
				result = true;
			}
		}
		return result;
	}		
	
	public static boolean isEmpty(String str){
		if(str==null || "".equals(str.trim()) || "null".equalsIgnoreCase(str.trim())) return true;
		return false;
	}
	
	public static boolean isNotEmpty(String str){
		return !StringHelper.isEmpty(str);
	}
	
	public static boolean isNotEmpty(Object obj){
		return obj!=null && StringHelper.isNotEmpty(String.valueOf(obj));
	}	
	
	public static String generateFillZeroString(int value,int len){
		String strValue = String.valueOf(value);
		while(strValue.length()<len) strValue = "0"+strValue;
		return strValue;
	}
	
	public static String getSendContentNoLength(String content,int relength,String replenisher ) throws Exception{
		int length = 0;
		if(content != null){
			length = content.getBytes("GBK").length;				
		}else{
			content = "";
		}
		int num = relength - length;
		if(num <= 0){
			return content.substring(0,relength);
		}
		String head = ""; 
		for(int n = 0; n < num; n++){
			head += replenisher;
		}
		if(" ".equals(replenisher)){
			content = content+head;
		}else{
			content = head+content;
		}				
		return content;
	}
	
	public static byte[] getStringByte(String content) throws Exception{
		return content.getBytes("GBK");
	}
	
	public static String encodeUTF8(String xmlDoc) throws Exception {
		String str = "";
		str = URLEncoder.encode(xmlDoc, "UTF-8");
		return str;
	}
	
	public static String decodeUTF8(String xmlDoc) throws Exception {
		String str = "";
		str = URLDecoder.decode(xmlDoc, "UTF-8");
		return str;
	}
	
	/**
	 * 获取字符串中第N次目标字符出现的位置
	 * @param str
	 * @param target
	 * @param index
	 * @return
	 */
	public static int getCharacterPosition(String str,String target,int index) {
		Matcher slashMatcher = Pattern.compile(target).matcher(str);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			if (mIdx == index) {
				break;
			}
		}
		return slashMatcher.start();
	}
	
	public static int findCharacterPosition(String str, String target, int index) {
		int i = 0;
		int m = 0;
		char c = new String(target).charAt(0);
		char[] ch = str.toCharArray();
		for (int j = 0; j < ch.length; j++) {
			if (ch[j] == c) {
				i++;
				if (i == index) {
					m = j;
					break;
				}
			}
		}
		return m;
	}
	
}
