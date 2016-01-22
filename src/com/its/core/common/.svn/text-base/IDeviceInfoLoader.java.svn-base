package com.its.core.common;

import java.util.Map;

import com.its.core.util.XMLProperties;

/**
 * 设备信息读取（装载）器接口
 * 创建日期 2012-08-03 下午05:37:53
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface IDeviceInfoLoader {
	/**
	 * 配置参数
	 * @param props
	 * @throws Exception
	 */
	public void configure(XMLProperties props,String propertiesPrefix) throws Exception;
	
	/**
	 * 重新配置
	 * @throws Exception
	 */
	public void reconfigure() throws Exception;	
	
	/**
	 * 获取设备信息Map
	 * @return
	 * @throws Exception
	 */
	public Map<String, DeviceInfoBean> getDeviceMap() throws Exception;	
}
