/**
 * 
 */
package com.its.core.module.device.vehicle.filter;

import com.its.core.module.device.vehicle.RealtimeVehicleInfoBean;
import com.its.core.util.XMLProperties;

/**
 * 所有需要处理实时车辆信息的模块必须扩展该类
 * 创建日期 2012-9-20 下午03:26:58
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface IProcessorFilter {
	
	/**
	 * 配置属性
	 * @param props
	 * @param propertiesPrefix
	 * @param no
	 * @throws Exception
	 */
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception;
	
	/**
	 * 处理实时车辆信息：RealtimeVehicleInfoBean
	 * @param realtimeVehicleInfoBean
	 * @return 处理结果，true:可以继续执行下一个IProcessorFilter; false:停止执行后续的IProcessorFilter
	 * @throws Exception
	 */
	public boolean process(RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception;

}
