/**
 * 
 */
package com.its.core.module.task;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.Environment;
import com.its.core.module.IModule;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-19 下午04:01:18
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class TaskModule implements IModule {
	private static final Log log = LogFactory.getLog(TaskModule.class);
	
	private ScheduledThreadPoolExecutor executor = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#config(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	public void config(XMLProperties xmlProperties, String propertiesPrefix,int no) throws Exception {
		// TODO Auto-generated method stub
		String prefix = Environment.TASK_PREFIX+".task";
		int taskNum = xmlProperties.getPropertyNum(prefix);
		int corePoolSize = 0;
		executor = new ScheduledThreadPoolExecutor(taskNum);
		for(int i=0;i<taskNum;i++){
			String run = xmlProperties.getProperty(prefix,i,"run");
			if("true".equalsIgnoreCase(run) || "y".equalsIgnoreCase(run)){
				String impClass = xmlProperties.getProperty(prefix,i,"imp_class");
				if(impClass!=null && !impClass.trim().equals("")){
					try{				
						ATask task = (ATask)Class.forName(impClass).newInstance();
						task.configure(xmlProperties,prefix,i);
						executor.scheduleWithFixedDelay(task, task.getPeriod(), task.getPeriod(), TimeUnit.SECONDS);
						corePoolSize++;
						log.info("装载定时器任务："+impClass);
					}catch(Exception ex){
						log.error("初始化任务["+impClass+"]时出错："+ex.getMessage(),ex);
						ex.printStackTrace();
					}
				}
			}	
		}
		executor.setCorePoolSize(corePoolSize+1);

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#start()
	 */
	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#stop()
	 */
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		if(executor!=null) executor.shutdown();

	}

}
