/**
 * 
 */
package com.its.core.util;

import java.io.File;

/**
 * 创建日期 2012-12-13 下午01:12:20
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class FilenameFilterByPostfixAndSize extends FilenameFilterByPostfix {
	
	/**
	 * 文件大小最小值，单位：字节
	 */
	private long minFileSize = 1024;
	
	private long maxFileSize = Long.MAX_VALUE;

	public FilenameFilterByPostfixAndSize(String postfix, boolean ignoreCase, long minFileSize) {
		super(postfix, ignoreCase);

		this.minFileSize = minFileSize;
	}
	
	public FilenameFilterByPostfixAndSize(String postfix, boolean ignoreCase, long minFileSize,long maxFileSize) {
		super(postfix, ignoreCase);

		this.minFileSize = minFileSize;
		this.maxFileSize = maxFileSize;
	}	
	
	public FilenameFilterByPostfixAndSize(String postfix, boolean ignoreCase,boolean containDir, long minFileSize) {
		super(postfix, ignoreCase, containDir);

		this.minFileSize = minFileSize;
	}	
	
	public FilenameFilterByPostfixAndSize(String postfix, boolean ignoreCase,boolean containDir, long minFileSize,long maxFileSize) {
		super(postfix, ignoreCase, containDir);

		this.minFileSize = minFileSize;
		this.maxFileSize = maxFileSize;
	}		

	public FilenameFilterByPostfixAndSize(String prefix, String postfix, boolean ignoreCase, long minFileSize) {
		super(prefix, postfix, ignoreCase);
		this.minFileSize = minFileSize;
	}

	public FilenameFilterByPostfixAndSize(String prefix, String postfix, boolean ignoreCase, long minFileSize,long maxFileSize) {
		super(prefix, postfix, ignoreCase);
		this.minFileSize = minFileSize;
		this.maxFileSize = maxFileSize;
	}
	
	/* （非 Javadoc）
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		if(this.isContainDir() && (new File(dir.getAbsolutePath()+"/"+name)).isDirectory()) return true;
		
		boolean flag = super.accept(dir, name);

		if (!flag) {
			return false;
		}

		try {
			File oriFile = new File(dir.getAbsolutePath() + "/" + name);
			long oriLength = oriFile.length();
			if (oriLength >= this.getMinFileSize() && oriLength <= this.getMaxFileSize()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public long getMinFileSize() {
		return minFileSize;
	}

	public void setMinFileSize(long l) {
		minFileSize = l;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

}
