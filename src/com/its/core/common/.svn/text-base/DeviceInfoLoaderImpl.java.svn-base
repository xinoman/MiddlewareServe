/**
 * 
 */
package com.its.core.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-19 下午03:08:37
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceInfoLoaderImpl implements IDeviceInfoLoader {
	private static final Log log = LogFactory.getLog(DeviceInfoLoaderImpl.class);
	
	private String querySql;
    
	private String propertiesPrefix;

	/* (non-Javadoc)
	 * @see com.its.core.common.IDeviceInfoLoader#configure(com.its.core.util.XMLProperties, java.lang.String)
	 */
	public void configure(XMLProperties props, String propertiesPrefix)	throws Exception {
		// TODO Auto-generated method stub
		this.propertiesPrefix = propertiesPrefix;
        this.querySql = props.getProperty(propertiesPrefix + ".querySql");

	}	

	/* (non-Javadoc)
	 * @see com.its.core.common.IDeviceInfoLoader#getDeviceMap()
	 */
	public Map<String, DeviceInfoBean> getDeviceMap() throws Exception {
		// TODO Auto-generated method stub
		Map<String, DeviceInfoBean> deviceMap = new HashMap<String, DeviceInfoBean>(); 
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet resultSet = null;        
        int count = 0;
        try{
            conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
            preStatement = conn.prepareStatement(this.getQuerySql());
            resultSet = preStatement.executeQuery();
            while(resultSet.next()){
            	DeviceInfoBean dib = new DeviceInfoBean();
                dib.setDeviceId(resultSet.getString("id"));
                dib.setDeviceName(resultSet.getString("device_type_name"));              
                dib.setIp(resultSet.getString("ip"));
                dib.setRoadId(String.valueOf(resultSet.getInt("road_id")));
                dib.setRoadName(resultSet.getString("road_name"));
                dib.setTypeId(resultSet.getString("device_type_id"));
                dib.setTypeName(resultSet.getString("device_type_name"));
                dib.setVendorId(resultSet.getString("vendor_id"));
                dib.setVendorName(resultSet.getString("vendor_name"));               
                dib.setDirectionDrive(resultSet.getString("direction_drive"));
                dib.setDirectionCode(resultSet.getString("direction_code"));
                dib.setDeptId(resultSet.getString("dept_id"));
                
    			String thirdPartyDeviceId 	= resultSet.getString("tp_device_id");
    			String thirdPartyRoadId 	= resultSet.getString("tp_road_id");    			
    			String thirdPartyRoadName 	= resultSet.getString("road_name");
    			String thirdPartyDirectId 	= resultSet.getString("tp_direction_id");    			
    			String thirdCompanyName     = resultSet.getString("tp_company_name");
//    			String thirdRemark     = resultSet.getString("tp_remark");    			 			
    			
    			if(StringHelper.isNotEmpty(thirdPartyRoadId)) thirdPartyRoadId = thirdPartyRoadId.trim();
    			    			
    			ThirdPartyDeviceInfoBean thirdPartyDeviceInfoBean = new ThirdPartyDeviceInfoBean();
    			thirdPartyDeviceInfoBean.setDeviceId(thirdPartyDeviceId);
    			thirdPartyDeviceInfoBean.setRoadId(thirdPartyRoadId);    			
    			thirdPartyDeviceInfoBean.setDirectId(thirdPartyDirectId);    			
    			thirdPartyDeviceInfoBean.setRoadName(thirdPartyRoadName); 
    			thirdPartyDeviceInfoBean.setCompanyName(thirdCompanyName);
//    			thirdPartyDeviceInfoBean.setRemark(thirdRemark);
    			dib.setThirdPartyDeviceInfoBean(thirdPartyDeviceInfoBean);
    			//log.debug("第三方路口ID："+dib.getThirdPartyDeviceInfoBean().getRoadId());
                deviceMap.put(dib.getDeviceId(), dib);
                count++;
                log.debug("Load device :"+dib.getDeviceId()+"\t"+dib.getIp());
            }
            log.debug("装载"+count+"套设备信息！");
        }
        catch(Exception ex){
            log.error("装载设备信息时出错：" + ex.getMessage(), ex);
            throw ex;
        }
        finally{
            if(resultSet != null){
                try{
                    resultSet.close();
                }
                catch(Exception ex) { }
            }
            if(preStatement != null){
                try{
                    preStatement.close();
                }
                catch(Exception ex) { }
            }
            if(conn != null){
                try{
                    ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
                }
                catch(Exception ex1) { }
            }
        }
      
        return deviceMap;
	}

	/* (non-Javadoc)
	 * @see com.its.core.common.IDeviceInfoLoader#reconfigure()
	 */
	public void reconfigure() throws Exception {
		// TODO Auto-generated method stub
		SystemConstant.getInstance().clearProperties();
		this.configure(SystemConstant.getInstance().getProperties(),this.propertiesPrefix);

	}

	/**
	 * @return the querySql
	 */
	public String getQuerySql() {
		return querySql;
	}

	/**
	 * @param querySql the querySql to set
	 */
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	/**
	 * @return the propertiesPrefix
	 */
	public String getPropertiesPrefix() {
		return propertiesPrefix;
	}

	/**
	 * @param propertiesPrefix the propertiesPrefix to set
	 */
	public void setPropertiesPrefix(String propertiesPrefix) {
		this.propertiesPrefix = propertiesPrefix;
	}	

}
