/**
 * 
 */
package com.its.core.module.filescan;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.Environment;
import com.its.core.module.IModule;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-12-13 上午08:13:01
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class FileScanModule implements IModule {
	private static final Log log = LogFactory.getLog(FileScanModule.class);
	
	private ScheduledThreadPoolExecutor executor = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#config(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void config(XMLProperties xmlProperties, String propertiesPrefix,int no) throws Exception {
		String prefix = Environment.FILESCANNER_PREFIX+".scanner";
		int corePoolSize = 0;
		executor = new ScheduledThreadPoolExecutor(0);
		int scannerNum = xmlProperties.getPropertyNum(prefix);
		for(int i=0;i<scannerNum;i++){
			String run = xmlProperties.getProperty(prefix,i,"run");
			if("true".equalsIgnoreCase(run) || "y".equalsIgnoreCase(run)){
				String impClass = xmlProperties.getProperty(prefix,i,"class");
				if(impClass!=null && !impClass.trim().equals("")){
					try{				
						AFileScanner scanner = (AFileScanner)Class.forName(impClass).newInstance();
						scanner.configure(xmlProperties,prefix,i);
						executor.scheduleWithFixedDelay(scanner, scanner.getPeriod(), scanner.getPeriod(), TimeUnit.SECONDS);
						corePoolSize++;
						log.info("装载文件扫描器："+impClass);
					}catch(Exception ex){
						log.error("装载文件扫描器["+impClass+"]时出错："+ex.getMessage(),ex);
						ex.printStackTrace();
					}
				}
			}	
		}	
		//executor.setCorePoolSize((corePoolSize+1)/2);
		executor.setCorePoolSize(corePoolSize);

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#start()
	 */
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#stop()
	 */
	@Override
	public void stop() throws Exception {
		if(executor!=null) executor.shutdown();
	}

}
