/*
 * Title:根据文件后缀(扩展名)过滤文件
 * 创建日期 2006-5-19
 * @author GuoPing.Wu
 * Copyright: UniHz Technologies CO.,LTD.
 */
package com.its.core.util;

import java.io.File;
import java.io.FilenameFilter;



public class FilenameFilterByPostfix implements FilenameFilter {

	/**
	 * 文件名后缀
	 */
	private String[] postfix = null;
	
	/**
	 * 文件名前缀
	 */
	private String prefix = null;
	
	/**
	 * 是否忽略大小写
	 */
	private boolean ignoreCase;
	
	/**
	 * 是否包含（返回）目录,默认不返回
	 */
	private boolean containDir = false;	
	
	public FilenameFilterByPostfix(String postfix,boolean ignoreCase){
		this.postfix = StringHelper.split(postfix, ",");	
		this.ignoreCase = ignoreCase;
	}
	
	public FilenameFilterByPostfix(String postfix,boolean ignoreCase,boolean containDir){
		this.postfix = StringHelper.split(postfix, ",");
		this.ignoreCase = ignoreCase;
		this.containDir = containDir;
	}	
	
	public FilenameFilterByPostfix(String prefix,String postfix,boolean ignoreCase){
		this.prefix = prefix;
		this.postfix = StringHelper.split(postfix, ",");
		this.ignoreCase = ignoreCase;
	}	
	
	public FilenameFilterByPostfix(String prefix,String postfix,boolean ignoreCase,boolean containDir){
		this.prefix = prefix;
		this.postfix = StringHelper.split(postfix, ",");
		this.ignoreCase = ignoreCase;
		this.containDir = containDir;
	}	
	
	/* 
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		//System.out.println(dir.getAbsoluteFile()); 
		if(this.isContainDir() && (new File(dir.getAbsolutePath()+"/"+name)).isDirectory()) return true;
		
		if(this.isIgnoreCase()){
			int len = this.getPostfix().length;
			for(int i=0;i<len;i++){
				if(name.toUpperCase().endsWith(this.getPostfix()[i].toUpperCase())){
					if(this.getPrefix()==null){
						return true;
					}
					else{
						if(name.toUpperCase().startsWith(this.getPrefix().toUpperCase())) return true;
					}				
				}
			}
		}
		else{
			int len = this.getPostfix().length;
			for(int i=0;i<len;i++){			
				if(name.endsWith(this.getPostfix()[i])){
					if(this.getPrefix()==null){
						return true;
					}
					else{
						if(name.startsWith(this.getPrefix())) return true;
					}
				}
			}
		}		
		return false;
	}

	public String[] getPostfix() {
		return postfix;
	}

	public void setPostfix(String[] postfix) {
		this.postfix = postfix;
	}

	/**
	 * @return
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * @param b
	 */
	public void setIgnoreCase(boolean b) {
		ignoreCase = b;
	}

	/**
	 * @return
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param string
	 */
	public void setPrefix(String string) {
		prefix = string;
	}

	public boolean isContainDir() {
		return containDir;
	}

	public void setContainDir(boolean containDir) {
		this.containDir = containDir;
	}
	
	public static void main(String[] args) {
		File[] files = (new File("E:/测试用图片/闯红灯/")).listFiles(new FilenameFilterByPostfix(".MPEG",false,true));
		int size = files.length;
		for(int i=0;i<size;i++){
			System.out.println(files[i].getName());
		}
		System.out.println("=========================");
		files = (new File("E:/测试用图片/闯红灯/R10003T20050929074248L01V000ID2E40.C.JPG")).listFiles();
		if(files==null) return;
		size = files.length;
		for(int i=0;i<size;i++){
			System.out.println(files[i].getName());
		}		
	}

}
