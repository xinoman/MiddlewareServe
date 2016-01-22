/**
 * 
 */
package com.its.core.module.task.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-11-8 下午05:28:13
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ExecuteSqlTimerTask extends ATask {
	
	private static final Log log = LogFactory.getLog(ExecuteSqlTimerTask.class);
	
	private boolean autoCommit = true;
	
	private List<String> sqlList = new ArrayList<String>();

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		String autoCommitStr = props.getProperty(propertiesPrefix, no, "trans.auto_commit");
		if(StringHelper.isEmpty(autoCommitStr)){
			autoCommitStr = "true";
		}
		this.autoCommit = StringHelper.getBoolean(autoCommitStr);
		
		int size = props.getPropertyNum(propertiesPrefix, no, "sql_list.sql");	
		
		log.debug("size = "+size);
		for(int i=0;i<size;i++){
			String sql = props.getProperty(propertiesPrefix, no, "sql_list.sql", i, "value");
			log.debug(sql);
			sqlList.add(sql);
		}

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		Connection conn = null;
		PreparedStatement preStatement = null;
		try{		
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(this.isAutoCommit());
			int size = this.getSqlList().size();
			for(int i=0;i<size;i++){
				log.debug("执行："+this.getSqlList().get(i));
				preStatement = conn.prepareStatement(this.getSqlList().get(i));
				int records = preStatement.executeUpdate();
				log.debug("update num = "+records);
				DatabaseHelper.close(null,preStatement);
			}
			
			if(!this.isAutoCommit()){
				conn.commit();
			}
		}
		catch(Exception ex){
			if(conn!=null && !this.isAutoCommit()){
				try {
					conn.rollback();
				} catch (Exception e) {
					log.error(e);
				}
			}
			log.error("执行SQL失败："+ex.getMessage(),ex);		
		}
		finally{
			DatabaseHelper.close(null,preStatement);
			if(conn!=null){
				try{
					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
				}
				catch(Exception ex2){}
			}
		}

	}
	
	public List<String> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<String> sqlList) {
		this.sqlList = sqlList;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

}
