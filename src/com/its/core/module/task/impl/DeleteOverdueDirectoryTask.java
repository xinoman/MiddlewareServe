/**
 * 
 */
package com.its.core.module.task.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.module.task.ATask;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-19 下午04:14:53
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeleteOverdueDirectoryTask extends ATask {
	private static final Log log = LogFactory.getLog(DeleteOverdueDirectoryTask.class);
	
	private String dir = null;
	
	//过期天数(删除过期多少天的目录)	
	private long overdueMills = 90*24*3600000L;
	
	private String timeFormat = "yyyyMMdd";

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.dir = props.getProperty(propertiesPrefix,no,"dir");	
		int overdueDay = PropertiesHelper.getInt(propertiesPrefix, no, "overdue_day", props, 90);
		this.overdueMills = overdueDay*24L*3600000L;

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		File dirFile = new File(this.getDir());
		if(!dirFile.exists()){
			log.warn("目录不存在："+this.getDir());
			return;
		}
		if(!dirFile.isDirectory()){
			log.warn("不是目录："+this.getDir());
			return;			
		}
		
		this.checkDir(dirFile);

	}
	
	private void checkDir(File file){
		if(!file.isDirectory()) return;
		log.debug(file.getAbsolutePath());
		boolean check = false;
		long dirMillis = System.currentTimeMillis();
		String name = StringHelper.replace(file.getName(), "-", "");		
		if(name.length()==this.getTimeFormat().length()){
			try{
				dirMillis = DateHelper.parseDateString(name, this.getTimeFormat()).getTime();
				check = true;
			}catch(Exception ex){
				log.error(ex);
			}
		}
		
		//过期目录
		if(check){
			if((System.currentTimeMillis()-dirMillis)>this.getOverdueMills()){
				try {
					log.debug("删除："+file.getAbsolutePath());
					FileHelper.delFile(file);					
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
		else{
			File[] fileArr = file.listFiles();
			for(int i=0;i<fileArr.length;i++){
				this.checkDir(fileArr[i]);
			}			
		}
	}

	/**
	 * @return the dir
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * @param dir the dir to set
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 * @return the overdueMills
	 */
	public long getOverdueMills() {
		return overdueMills;
	}

	/**
	 * @param overdueMills the overdueMills to set
	 */
	public void setOverdueMills(long overdueMills) {
		this.overdueMills = overdueMills;
	}

	/**
	 * @return the timeFormat
	 */
	public String getTimeFormat() {
		return timeFormat;
	}

	/**
	 * @param timeFormat the timeFormat to set
	 */
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}	

}
