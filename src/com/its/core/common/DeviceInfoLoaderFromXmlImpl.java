/**
 * 
 */
package com.its.core.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.SystemConstant;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-4-26 下午03:56:06
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class DeviceInfoLoaderFromXmlImpl implements IDeviceInfoLoader {
	private static final Log log = LogFactory.getLog(DeviceInfoLoaderFromXmlImpl.class);
	private XMLProperties props = null;
	private String propertiesPrefix = null;

	/* (non-Javadoc)
	 * @see com.its.core.common.IDeviceInfoLoader#configure(com.its.core.util.XMLProperties, java.lang.String)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix)	throws Exception {
		this.props = props;
		this.propertiesPrefix = propertiesPrefix;

	}

	/* (non-Javadoc)
	 * @see com.its.core.common.IDeviceInfoLoader#getDeviceMap()
	 */
	@Override
	public Map<String, DeviceInfoBean> getDeviceMap() throws Exception {
		Map<String, DeviceInfoBean> deviceMap = new HashMap<String, DeviceInfoBean>(); 
		String deviceListPrefix = this.propertiesPrefix + ".device";
		int size = this.props.getPropertyNum(deviceListPrefix);
		for(int i=0;i<size;i++){
			String deviceId 	= this.props.getProperty(deviceListPrefix, i, "id");
			String type 		= this.props.getProperty(deviceListPrefix, i, "type");
			String ip 			= this.props.getProperty(deviceListPrefix, i, "ip");
			String from 		= this.props.getProperty(deviceListPrefix, i, "from");
			String to 			= this.props.getProperty(deviceListPrefix, i, "to");
			String roadName 	= this.props.getProperty(deviceListPrefix, i, "road_name");
			
			String thirdPartyDeviceId 	= this.props.getProperty(deviceListPrefix, i, "third_party.device_id");
			String thirdPartyRoadId 	= this.props.getProperty(deviceListPrefix, i, "third_party.road_id");
			String thirdPartyRoadName 	= this.props.getProperty(deviceListPrefix, i, "third_party.road_name");
			String thirdPartyDirectId 	= this.props.getProperty(deviceListPrefix, i, "third_party.direct_id");
			String thirdPartyDeptId 	= this.props.getProperty(deviceListPrefix, i, "third_party.dept_id");
			
			ThirdPartyDeviceInfoBean thirdPartyDeviceInfoBean = new ThirdPartyDeviceInfoBean();
			thirdPartyDeviceInfoBean.setDeviceId(thirdPartyDeviceId);
			thirdPartyDeviceInfoBean.setRoadId(thirdPartyRoadId);
			thirdPartyDeviceInfoBean.setDirectId(thirdPartyDirectId);
			thirdPartyDeviceInfoBean.setDeptId(thirdPartyDeptId);
			thirdPartyDeviceInfoBean.setRoadName(thirdPartyRoadName);
			
        	DeviceInfoBean dib = new DeviceInfoBean();
            dib.setDeviceId(deviceId);
            //dib.setDeviceName();
            dib.setFrom(from);
            dib.setTo(to);
            dib.setIp(ip);
            //dib.setRoadId();
            dib.setRoadName(roadName);
            //dib.setDeptId();
            //dib.setDeptName();
            dib.setTypeId(type);
            //dib.setTypeName();
            //dib.setVendorId();
            //dib.setVendorName();
            dib.setDirectionCode(SystemConstant.getInstance().getDirectionNoByName(from));
            dib.setThirdPartyDeviceInfoBean(thirdPartyDeviceInfoBean);
            deviceMap.put(deviceId, dib);	
            
            log.debug("Load device :"+deviceId+"\t"+ip);
		}      
        return deviceMap;
	}

	/* (non-Javadoc)
	 * @see com.its.core.common.IDeviceInfoLoader#reconfigure()
	 */
	@Override
	public void reconfigure() throws Exception {
		SystemConstant.getInstance().clearProperties();
	}

}
