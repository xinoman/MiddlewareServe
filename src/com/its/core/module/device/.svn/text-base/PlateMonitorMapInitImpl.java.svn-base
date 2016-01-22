/**
 * 
 */
package com.its.core.module.device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-1-30 下午03:35:46
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class PlateMonitorMapInitImpl implements IPlateMonitorMapInit {
	private static final Log log = LogFactory.getLog(PlateMonitorMapInitImpl.class);
	
	//获取所有黑名单信息
	private String sqlSelectAllBlacklist = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.device.IPlateMonitorMapInit#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception {
		this.sqlSelectAllBlacklist = props.getProperty(propertiesPrefix,no,"plate_monitor_init.sql_select_all_blacklist");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.IPlateMonitorMapInit#load()
	 */
	@Override
	public List<BlacklistBean> load() throws Exception {
		List<BlacklistBean> blacklistList = new ArrayList<BlacklistBean>();
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement(this.getSqlSelectAllBlacklist());
			resultSet = preStatement.executeQuery();
			
			while(resultSet.next()){
				BlacklistBean blacklist = new BlacklistBean();
				blacklist.setId(String.valueOf(resultSet.getLong("id")));
				blacklist.setPlate(resultSet.getString("plate"));
				int plateColorCode = resultSet.getInt("plate_color_code");
				if(plateColorCode!=-1){
					blacklist.setPlateColorCode(String.valueOf(plateColorCode));
				}
				Timestamp watchEndTime = resultSet.getTimestamp("watch_end_time");
				if(watchEndTime!=null){
					blacklist.setEndTime(Long.valueOf(watchEndTime.getTime()));
				}
				blacklist.setMatchNumber(Integer.valueOf(resultSet.getInt("plate_match_num")));
				blacklist.setTypeName(resultSet.getString("blacklist_type"));
				blacklist.setTypeId(String.valueOf(resultSet.getLong("blacklist_type_id")));
				blacklistList.add(blacklist);
			}
		}
		catch(Exception ex){
			log.error("获取黑名单信息时出错：" + ex.getMessage(), ex);		
		}
		finally{
			DatabaseHelper.close(resultSet, preStatement);
			try{
				ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
			}catch(Exception ex1){}			
		}				
		
		return blacklistList;
	}

	public String getSqlSelectAllBlacklist() {
		return sqlSelectAllBlacklist;
	}

	public void setSqlSelectAllBlacklist(String sqlSelectAllBlacklist) {
		this.sqlSelectAllBlacklist = sqlSelectAllBlacklist;
	}	

}
