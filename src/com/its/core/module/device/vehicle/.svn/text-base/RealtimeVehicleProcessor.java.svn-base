/**
 * 
 */
package com.its.core.module.device.vehicle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.module.device.vehicle.filter.IProcessorFilter;
import com.its.core.util.DateHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-20 下午03:24:26
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class RealtimeVehicleProcessor extends ACommunicateProcessor {
	private static final Log log = LogFactory.getLog(RealtimeVehicleProcessor.class);
	
	private List<IProcessorFilter> filterList = new ArrayList<IProcessorFilter>();

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception {
		//String filterPrefix = Environment.REALTIME_VEHICLE_FILTER;
		String filterPrefix = props.getProperty(propertiesPrefix,no,"filter");
		int size = props.getPropertyNum(filterPrefix);
		log.debug("filter size = "+size);
		for(int i=0;i<size;i++){
			String valid = props.getProperty(filterPrefix,i,"valid");
			if("true".equalsIgnoreCase(valid) || "y".equalsIgnoreCase(valid)){
				String filterClass = props.getProperty(filterPrefix,i,"class");
				if(filterClass!=null && !filterClass.trim().equals("")){
					try{				
						IProcessorFilter filter = (IProcessorFilter)Class.forName(filterClass).newInstance();
						filter.configure(props,filterPrefix,i);	
						filterList.add(filter);
						log.debug("Loaded : "+filterClass);
					}catch(Exception ex){
						log.error("初始化实时车辆FILTER类["+filterClass+"]时出错："+ex.getMessage(),ex);
					}
				}					
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#process(com.its.core.module.device.MessageBean)
	 */
	@Override
	public void process(MessageBean messageBean) throws Exception {		
		RealtimeVehicleInfoBean realtimeVehicleInfoBean = this.parseMessage(messageBean);
		
		//各FILTER根据需要依次处理实时辆辆信息
		int filterSize = this.filterList.size();
		for(int i=0;i<filterSize;i++){
			IProcessorFilter filter = this.filterList.get(i);
			if(!filter.process(realtimeVehicleInfoBean)){
				break;
			}
		}

	}
	
	/**
	 * 解析message
	 * @param messageBean
	 * @return
	 * @throws Exception
	 */
	protected RealtimeVehicleInfoBean parseMessage(MessageBean messageBean) throws Exception{
		RealtimeVehicleInfoBean realtimeVehicleInfoBean = new RealtimeVehicleInfoBean();		
		realtimeVehicleInfoBean.setProtocolBean(messageBean);

		/* 消息协议格式示例：
			<?xml version="1.0" encoding="UTF-8"?>
			<message>
				<head>
					<code>DS1005</code>	
					<version>2.0.0.0</version>
				</head>
				<body>
					<content>
						<deviceId></deviceId>
						<directionCode></directionCode>
						<directionNo></directionNo>
						<plateNo></plateNo>
						<plateColor></plateColor>
						<catchTime></catchTime>
						<laneNo></laneNo>
						<speed></speed>
						<limitSpeed></limitSpeed>
						<featureImagePath></featureImagePath>
						<panoramaImagePath></panoramaImagePath>
					</content>
				</body>
			</message>	 
		*/
		XMLParser xmlParser = messageBean.getXmlParser();
		realtimeVehicleInfoBean.setDeviceId(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "deviceId"));
//		realtimeVehicleInfoBean.setDirectionCode(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "directionCode"));		
		realtimeVehicleInfoBean.setPlateNo(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "plateNo"));
		realtimeVehicleInfoBean.setPlateColor(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "plateColor"));
		
		String catchTime = xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "catchTime");
		if(catchTime.length()>14){
			catchTime = catchTime.substring(0,14);
		}
		realtimeVehicleInfoBean.setCatchTime(DateHelper.parseDateString(catchTime,"yyyyMMddHHmmss"));
		realtimeVehicleInfoBean.setDrivewayNo(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "laneNo"));
		realtimeVehicleInfoBean.setSpeed(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "speed"));
		realtimeVehicleInfoBean.setLimitSpeed(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "limitSpeed"));
		realtimeVehicleInfoBean.setFeatureImagePath(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "featureImagePath"));
		realtimeVehicleInfoBean.setPanoramaImagePath(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "panoramaImagePath"));	
		
//		realtimeVehicleInfoBean.setDirectionDrive(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "directionNo"));
		
		//默认使用系统设备信息方向
		try {
			DeviceInfoBean dib = (DeviceInfoBean)DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(realtimeVehicleInfoBean.getDeviceId());
			realtimeVehicleInfoBean.setDirectionDrive(dib.getDirectionDrive());		
			realtimeVehicleInfoBean.setDirectionCode(dib.getDirectionCode());
		}catch(Exception ex){}		
		
		return realtimeVehicleInfoBean;
	}

}
