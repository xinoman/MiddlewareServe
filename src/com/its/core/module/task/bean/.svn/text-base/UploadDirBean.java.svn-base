/**
 * 
 */
package com.its.core.module.task.bean;

import java.io.Serializable;

import org.apache.commons.net.ftp.FTP;

/**
 * 创建日期 2013-11-19 下午08:13:53
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class UploadDirBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2100585737034355338L;
	
	/**
	 * 文件目录路径
	 */
	private String path = null;
	
	/**
	 * 是否上传子目录
	 */
	private boolean includeSubdirectory = true;
	
	/**
	 * 是否在服务器上创建同名子目录
	 */
	private boolean createSubdirectoryAtFtpserver = false;
	
	/**
	 * 文件上传成功后是否删除本地文件
	 */
	private boolean deleteLocalFile = true;
	
	/**
	 * 文件上传成功后是否删除本地的空文件目录
	 */
	private boolean deleteLocalEmptySubdirectory = true;
	
	private  int fileType = FTP.BINARY_FILE_TYPE;

	public boolean isCreateSubdirectoryAtFtpserver() {
		return createSubdirectoryAtFtpserver;
	}

	public void setCreateSubdirectoryAtFtpserver(
			boolean createSubdirectoryAtFtpserver) {
		this.createSubdirectoryAtFtpserver = createSubdirectoryAtFtpserver;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public boolean isIncludeSubdirectory() {
		return includeSubdirectory;
	}

	public void setIncludeSubdirectory(boolean includeSubdirectory) {
		this.includeSubdirectory = includeSubdirectory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDeleteLocalEmptySubdirectory() {
		return deleteLocalEmptySubdirectory;
	}

	public void setDeleteLocalEmptySubdirectory(boolean deleteLocalEmptySubdirectory) {
		this.deleteLocalEmptySubdirectory = deleteLocalEmptySubdirectory;
	}

	public boolean isDeleteLocalFile() {
		return deleteLocalFile;
	}

	public void setDeleteLocalFile(boolean deleteLocalFile) {
		this.deleteLocalFile = deleteLocalFile;
	}

}
