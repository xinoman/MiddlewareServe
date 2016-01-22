/**
 * 
 */
package com.its.core.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.Environment;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-8-3 上午11:30:23
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceInfoLoaderFactory implements Runnable {
	private static final Log log = LogFactory.getLog(DeviceInfoLoaderFactory.class);
	
	private XMLProperties props = null;
	
	private static DeviceInfoLoaderFactory dilf = new DeviceInfoLoaderFactory();
	
	/**
	 * 是否需要重新装载设备信息
	 */
	private boolean requireReload = false;
	
	private Map<String, DeviceInfoBean> deviceMap = null;
	
	/**
	 * 需要重新装载设备信息的间隔时间（单位：秒）
	 */
	private static final long RELOAD_PERIOD = 3600;
	
	private ScheduledThreadPoolExecutor reloadExecutor = new ScheduledThreadPoolExecutor(1);
	
	private DeviceInfoLoaderFactory(){
		this.reloadExecutor.scheduleWithFixedDelay(this, RELOAD_PERIOD, RELOAD_PERIOD, TimeUnit.SECONDS);	
	}
	
	public static DeviceInfoLoaderFactory getInstance(){
		return dilf;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//log.debug(this.isRequireReload());
		if(this.isRequireReload()){
			this.reloadDeviceMap();
			this.setRequireReload(false);
		}
	}

	/**
	 * 初始化设备信息装载程序。
	 * @param props
	 */
	public void initDeviceInfoLoader(XMLProperties props){
		this.props = props;
		String prefix = Environment.DEVICE_INFO_LOADER_PREFIX;
	
		String loadClass = props.getProperty(prefix+".load_class");
		log.debug("Device load class = "+loadClass);
		if(loadClass!=null && !loadClass.trim().equals("")){
			try{				
				IDeviceInfoLoader deviceLoader = (IDeviceInfoLoader)Class.forName(loadClass).newInstance();
				deviceLoader.configure(props,prefix);
				this.deviceMap = deviceLoader.getDeviceMap();
			}catch(Exception ex){
				log.error("初始化设备信息装载程序["+loadClass+"]时出错："+ex.getMessage(),ex);
			}
		}
	}
	
	public Map<String, DeviceInfoBean> getDeviceMap(){
		if(this.deviceMap == null){
			log.debug("重新装载设备信息！");
			initDeviceInfoLoader(this.props);
		}
		if(this.deviceMap == null){
			return new HashMap<String, DeviceInfoBean>();			
		}
		return this.deviceMap;
	}
	
	public void reloadDeviceMap(){
		this.deviceMap.clear();
		this.deviceMap = null;
		this.getDeviceMap();	
	}

	public boolean isRequireReload() {
		return requireReload;
	}

	public void setRequireReload(boolean requireReload) {
		this.requireReload = requireReload;
	}

}
