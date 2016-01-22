/**
 * 
 */
package com.its.core.local.ningxiang.filescan;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.filescan.violation.ViolationInfoBean;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-7-24 下午08:31:45
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class SynjonesViolationFileScannerImpl extends AViolationFileScanner {
	private static final Log log = LogFactory.getLog(SynjonesViolationFileScannerImpl.class);
	
    private String insertSql = null;
    private String filePathPrefix = null;
    
  //图片是否合并
	private boolean merge = false;
	private boolean orientation = false;

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#configureLocalProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.filePathPrefix = props.getProperty(propertiesPrefix,no,"standard_version.file_path_prefix");
		this.merge = StringHelper.getBoolean(props.getProperty(propertiesPrefix,no,"standard_version.merge"));
		this.orientation = StringHelper.getBoolean(props.getProperty(propertiesPrefix,no,"standard_version.orientation"));
		this.insertSql = props.getProperty(propertiesPrefix,no,"standard_version.insert_sql");
	}	

	/* (non-Javadoc)
	 * @see com.its.core.local.ningxiang.filescan.AViolationFileScanner#parseViolationInfo(java.lang.String, java.io.File)
	 */
	@Override
	protected ViolationInfoBean parseViolationInfo(String scanDir,File imageFile) throws Exception {
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();		
		
		ViolationInfoBean bean = new ViolationInfoBean();
		
		//路口名称
		String roadName = scanDir.substring((StringHelper.findCharacterPosition(scanDir,"\\",2)+1), StringHelper.findCharacterPosition(scanDir,"\\",3));
		bean.setRoadName(roadName);
		
		Connection conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
		PreparedStatement preStatement = null;
	    ResultSet resultSet = null;
	    
		String sql = "select r.id road_id,r.code road_code,d.id device_id from T_ITS_DEVICE d left join T_ITS_ROAD r on d.road_id = r.id where r.name=?";
   		preStatement = conn.prepareStatement(sql);
   		preStatement.setString(1, bean.getRoadName());				
		resultSet = preStatement.executeQuery();
		if(resultSet.next()){
//			String roadId = resultSet.getString("road_id");				
			String deviceId = resultSet.getString("device_id");
			bean.setDeviceId(deviceId);			
			
			//方向
			String directionName = fileName.substring(fileName.indexOf("至")-1, fileName.indexOf("至")+2);
			bean.setDirectionNo(SystemConstant.getInstance().getDirectionNoByName(directionName));
			
			//违法时间
			String time = fileName.substring(9, 29);
			Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(DateHelper.dateToString(DateHelper.parseDateString(time, "yyyy年MM月dd日HH时mm分ss秒"), "yyyyMMddHHmmss"));
			bean.setViolateTime(date);
			
			//车道
			String line = "01";
			bean.setLine(line);
			
			//速度
			if(fileName.indexOf("时速")>1) {
				String speed = fileName.substring(fileName.indexOf("时速")+2, fileName.indexOf("公里"));
				log.info("speed = " + speed);
				bean.setSpeed(speed);
				bean.setLimitSpeed("70");
			} else {
				
				bean.setSpeed("000");
				bean.setLimitSpeed("000");				
			}
			
			//号牌号码
			if(fileName.indexOf("字P")>1) {
				String plateNo = fileName.substring(fileName.indexOf("字P")+2, fileName.indexOf("驶向"));
				bean.setPlateNo(plateNo);
			}			
			
			//号牌种类
			if(fileName.indexOf("底")>1) {
				String plateColor = fileName.substring(fileName.indexOf("底")-1, fileName.indexOf("底"))+"色";
				bean.setPlateType(SystemConstant.getInstance().getPlateTypeIdByColorName(plateColor));
			} 
			
			String[] imageFiles = new String[1];
			imageFiles[0] = oriFileName;
			bean.setImageFiles(imageFiles);
			
		}		
		
		return bean;
	}
	
	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#postProcessViolationInfoBean(com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean) {
		boolean backupSuccess = true;
		try {
			String backDir = this.getScannerParam().getBackupDir() + "/" + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd")+"/"+DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			FileHelper.createDir(backDir);
			
			for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){
					String sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
					String targetFileName = backDir + "/" + imageFileName;
					log.debug("开始移动文件：" + sourceFileName + " 到 " + targetFileName);
					FileHelper.moveFile(sourceFileName, targetFileName);					
				}
			}
			
			if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
				String sourceFileName = violationInfoBean.getFileDir() + "/"  + violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/" + violationInfoBean.getVideoFile();		
				FileHelper.moveFile(sourceFileName, targetFileName);
			}
		} catch (Exception ex) {
			backupSuccess = false;
			log.error(ex.getMessage(), ex);
		}
		return backupSuccess;
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#postRestoreViolationInfoBean(com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#processViolationInfoBean(java.sql.Connection, com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		if(StringHelper.isEmpty(this.getInsertSql())){
			log.warn("未定义'insert_sql'参数！");
			return 0;
		}
        int result = -1;
        PreparedStatement preStatement = null;
       	try{
				
			//id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,plate,plate_type_id,file_path_1,file_path_2,file_path_3,file_path_4,video_file_path
			preStatement = conn.prepareStatement(this.getInsertSql());
			preStatement.setLong(1, (long)SequenceFactory.getInstance().getViolateRecordTempSequence());				
			preStatement.setTimestamp(2, new Timestamp(violationInfoBean.getViolateTime().getTime()));
			preStatement.setString(3, violationInfoBean.getDeviceInfoBean().getRoadId());				
			preStatement.setString(4, violationInfoBean.getDeviceId());
			preStatement.setString(5, violationInfoBean.getDirectionNo());
			preStatement.setString(6, violationInfoBean.getLine());
			preStatement.setString(7, violationInfoBean.getSpeed());
			preStatement.setString(8, violationInfoBean.getLimitSpeed());
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, violationInfoBean.getPlateNo());
			preStatement.setString(11, violationInfoBean.getPlateType());
			String pathPrefix = this.getFilePathPrefix() + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			String imageFileName = violationInfoBean.getImageFiles()[0];
			preStatement.setString(12, pathPrefix + imageFileName);		

			preStatement.execute();
			result = 0;	
       		       		
       	}
       	catch(Exception ex){
			log.error("数据入库失败：" + ex.getMessage(), ex);
			result = -1;		
       	}
       	finally{
			if(preStatement != null){
				try
				{
					preStatement.close();
				}
				catch(Exception ex) { }      
			}       		
       	}
        return result;
	}

	/**
	 * @return the insertSql
	 */
	public String getInsertSql() {
		return insertSql;
	}

	/**
	 * @param insertSql the insertSql to set
	 */
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	/**
	 * @return the filePathPrefix
	 */
	public String getFilePathPrefix() {
		return filePathPrefix;
	}

	/**
	 * @param filePathPrefix the filePathPrefix to set
	 */
	public void setFilePathPrefix(String filePathPrefix) {
		this.filePathPrefix = filePathPrefix;
	}

	/**
	 * @return the merge
	 */
	public boolean isMerge() {
		return merge;
	}

	/**
	 * @param merge the merge to set
	 */
	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	/**
	 * @return the orientation
	 */
	public boolean isOrientation() {
		return orientation;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

}
