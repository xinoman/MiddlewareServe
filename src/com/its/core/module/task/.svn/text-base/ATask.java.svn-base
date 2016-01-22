/**
 * 
 */
package com.its.core.module.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.PropertiesHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-19 下午04:05:42
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public abstract class ATask implements Runnable {
	private static final Log log = LogFactory.getLog(ATask.class);
	
	//执行周期(每次执行间隔多长时间,单位:秒)
	protected long period = 60L;
	
	protected void postExecute(){}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		try{
			this.execute();
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}

	}
	
	public void configure(XMLProperties props,String propertiesPrefix,int no) throws Exception{
		this.period = PropertiesHelper.getLong(propertiesPrefix,no,"period",props,this.period);		
		
		this.configureSpecificallyProperties(props, propertiesPrefix, no);		
	}
	
	/**
	 * 配置特定属性
	 * @param props
	 * @param propertiesPrefix
	 */
	public abstract void configureSpecificallyProperties(XMLProperties props, String propertiesPrefix, int no);
	
	/**
	 * 执行真实任务
	 */
	public abstract void execute();		
	
	
	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

}
