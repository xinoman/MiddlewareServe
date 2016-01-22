/**
 * 
 */
package com.its.core.local.dianbai.task;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.local.hezhou.tmri.tfc.webservice.TransProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DateHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2014-7-23 下午08:48:22
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicleToWxsSyncTimeTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportVehicleToWxsSyncTimeTask.class);
	
	private String wsEndPoint = "http://10.47.98.188:9080/jcbktrans/services/Trans";

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		// TODO Auto-generated method stub
		this.wsEndPoint = props.getProperty(propertiesPrefix, no, "ws_info.ws_endpoint");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		try {			
			log.debug(this.getWsEndPoint());
			TransProxy proxy = new TransProxy();
			proxy.setEndpoint(wsEndPoint);
			
			String time = proxy.querySyncTime();	
			log.info("同步时间为：" + time + " 当前系统时间为："+DateHelper.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
			
			this.updateSystemTime(DateHelper.parseDateString(time, "yyyy-MM-dd HH:mm:ss"));			
			log.info("更新后时间为：" + DateHelper.dateToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
			
			//获取接口调用错误日志
			this.getLastMessage(proxy);
			
		}
		catch(Exception ex){
			log.error("同步时间失败："+ex.getMessage(),ex);				
		}
	}
	
	/**
	 *  更新系统时钟 
	 * @param currentTime
	 */
	 
	private void updateSystemTime(Date currentTime){
		try {			
			String date = DateHelper.dateToString(currentTime, "yyyy-MM-dd");
			String time = DateHelper.dateToString(currentTime, "HH:mm:ss");
			String[] command = null;
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("windows 9") > -1) {
			    command = new String[]{"command /c date "+date,"command /c time "+time};
			}
			else if ((os.indexOf("nt") > -1) ||
					 (os.indexOf("windows 20") > -1 ) ||
					 (os.indexOf("windows xp") > -1) ) {
				command = new String[]{"cmd /c date "+date,"cmd /c time "+time};
			}
			//Unix or Linux
			else{
				command = new String[]{"date -s "+String.valueOf(currentTime)};
			}
			
			if(command!=null){
				int len = command.length;
				log.debug("更新系统时间："+date+" "+time);
				for(int i=0;i<len;i++){
					log.info(command[i]);
					java.lang.Runtime.getRuntime().exec(command[i]);
				}
			}
		} catch (java.io.IOException e)	{
			log.error(e); 
		}		
	}
	
	private void getLastMessage(TransProxy proxy) throws Exception {		
		
        String message = null;
		try {				
			message = proxy.getLastMessage();					
		} catch(Exception ex){				
			log.error("获取错误日志失败："+ex.getMessage(),ex);			

		}		
		log.debug("报错信息：" + message);
	}

	/**
	 * @return the wsEndPoint
	 */
	public String getWsEndPoint() {
		return wsEndPoint;
	}

	/**
	 * @param wsEndPoint the wsEndPoint to set
	 */
	public void setWsEndPoint(String wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

}
