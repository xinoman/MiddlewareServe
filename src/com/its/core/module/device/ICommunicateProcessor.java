/**
 * 
 */
package com.its.core.module.device;

import com.its.core.util.XMLProperties;

/**
 * 设备通讯处理器接口
 * 所有从：ICommunicateProcessor 扩展的类都必须是独立线程的方式执行
 * 创建日期 2012-9-19 下午05:47:22
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface ICommunicateProcessor extends Runnable {
	
	/**
	 * 与设备之间进行通讯的协议处理器类型
	 */
	public static final int PROCESSOR_TYPE_DEVICE = 0x1;
	
	/**
	 * 与设备之外的系统进行通讯的协议处理器类型（如：接收应用服务器发送过来的信息进行处理）
	 */
	public static final int PROCESSOR_TYPE_TRANSFER = 0x2;
	
	/**
	 * 设置处理器类型
	 * @param processorType
	 */
	public void setProcessorType(int processorType);
	
	/**
	 * 设置头文字
	 * @param headWord
	 */
	public void setHeadWord(String headWord);
	
	/**
	 * 设置设备通讯模块主类
	 * @param deviceCommunicateModule
	 */
	public void setDeviceCommunicateModule(DeviceCommunicateModule deviceCommunicateModule);
	
	/**
	 * 配置参数，从XML属性文件中
	 * @param props
	 * @throws Exception
	 */
	public void configure(XMLProperties props,String propertiesPrefix,int no) throws Exception;

}
