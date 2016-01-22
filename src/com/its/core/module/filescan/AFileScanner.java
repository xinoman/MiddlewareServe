/**
 * 
 */
package com.its.core.module.filescan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-12-13 上午08:16:28
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public abstract class AFileScanner implements Runnable {
	private static final Log log = LogFactory.getLog(AFileScanner.class);

	//执行周期(每次执行间隔多长时间,单位:秒)
	private long period = 60L;
	
	//扫描目录，可以同时扫描多个目录，配置scan_dirs属性时，多个目录之间用逗号分隔
	private String[] scanDirs = null;
	
	//每次扫描的最大文件数目
	private int maxScanFileNum = -1;
	
	protected void preExecute(){}
	
	protected void postExecute(){}
	
	protected void configure(XMLProperties props,String propertiesPrefix,int no) throws Exception{
		this.period = PropertiesHelper.getLong(propertiesPrefix,no,"common.scan_interval_second",props,this.period);	
		this.maxScanFileNum = PropertiesHelper.getInt(propertiesPrefix, no, "common.scan_file_num", props, this.maxScanFileNum);
		String strDirs = PropertiesHelper.getString(propertiesPrefix, no, "common.scan_dir", props, null);
		if(StringHelper.isNotEmpty(strDirs)){
			this.scanDirs = strDirs.split("[,]");
		}
		else{
			log.warn("扫描目录为空，请配置'common.scan_dir'属性!");
		}
		
		this.configureSpecificallyProperties(props, propertiesPrefix, no);		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try{
			this.preExecute();
			this.execute();
			this.postExecute();
		}catch(Exception ex){
			log.error(ex);
		}

	}
	
	/**
	 * 配置特定属性
	 * @param props
	 * @param propertiesPrefix
	 */
	public abstract void configureSpecificallyProperties(XMLProperties props, String propertiesPrefix, int no);
	
	/**
	 * 执行实际任务
	 */
	public abstract void execute();		
	
	
	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public String[] getScanDirs() {
		return scanDirs;
	}

	public void setScanDirs(String[] scanDirs) {
		this.scanDirs = scanDirs;
	}

	public int getMaxScanFileNum() {
		return maxScanFileNum;
	}

	public void setMaxScanFileNum(int maxScanFileNum) {
		this.maxScanFileNum = maxScanFileNum;
	}

}
