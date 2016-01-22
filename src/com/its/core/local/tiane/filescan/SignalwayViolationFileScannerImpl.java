/**
 * 
 */
package com.its.core.local.tiane.filescan;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.filescan.violation.ViolationInfoBean;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2014-6-15 上午07:28:17
 * 
 * @author GuoPing.Wu Copyright: Xinoman Technologies CO.,LTD.
 */
public class SignalwayViolationFileScannerImpl extends AViolationFileScanner {
	private static final Log log = LogFactory.getLog(SignalwayViolationFileScannerImpl.class);

	private String insertSql = null;
	private String filePathPrefix = null;

	// 图片是否合并
	private boolean merge = false;
	private boolean orientation = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#
	 * configureLocalProperties(com.swy.tiip.tools.util.XMLProperties,
	 * java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.filePathPrefix = props.getProperty(propertiesPrefix, no,"standard_version.file_path_prefix");
		this.merge = StringHelper.getBoolean(props.getProperty(	propertiesPrefix, no, "standard_version.merge"));
		this.orientation = StringHelper.getBoolean(props.getProperty(propertiesPrefix, no, "standard_version.orientation"));
		this.insertSql = props.getProperty(propertiesPrefix, no,"standard_version.insert_sql");

	}
	
	/**
	 * 根据第一个图片文件名，获取第二张图片文件
	 * @param imageFile
	 * @return
	 */
	protected File getSecondFile(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf(".")-1);		
		String postfix = ".bmp";			
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}

	/**
	 * 根据第一个图片文件名，获取第二、三张图片文件
	 * 
	 * @param imageFile
	 * @return
	 */
	protected File getThirdFile1(String scanDir, String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("往") + 3);
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,"2.JPG", true));
		if (fileArr != null) {
			int len = fileArr.length;
			if (len > 0)
				return fileArr[0];
		}
		return null;
	}

	protected File getThirdFile2(String scanDir, String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("往") + 3);
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,"3.JPG", true));
		if (fileArr != null) {
			int len = fileArr.length;
			if (len > 0)
				return fileArr[0];
		}
		return null;
	}

	@Override
	protected ViolationInfoBean parseViolationInfo(String scanDir,File imageFile) throws Exception {

		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();

		ViolationInfoBean bean = new ViolationInfoBean();

		String[] fileNameSplit = StringHelper.split(fileName, "_");

		// 设备编号
		bean.setDeviceId(fileNameSplit[3]);

		// 行驶方向
		String directionNo = fileNameSplit[4];
		if (StringHelper.isEmpty(directionNo)) {
			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(bean.getDeviceId());
			bean.setDirectionNo(dib.getDirectionCode());
		} else {
			directionNo = SystemConstant.getInstance().getDirectionNoByName(fileNameSplit[4]);
			bean.setDirectionNo(directionNo);
		}

		// 默认车道
		bean.setLine("01");

		// 抓拍时间
		String timeStr = fileNameSplit[6].substring(0, 14);
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(date);

		// 有车牌信息
		String plateNo = fileNameSplit[0];
		if (StringHelper.isNotEmpty(plateNo) && plateNo.indexOf("无") == -1) {
			bean.setPlateNo(plateNo);
		}			

		String plateColor = fileNameSplit[1];
		if (StringHelper.isNotEmpty(plateColor)&& plateColor.indexOf("无") == -1) {			
			plateColor = SystemConstant.getInstance().getPlateTypeIdByColorName(plateColor+"色");
			bean.setPlateColor(plateColor);
		}

		String[] imageFiles = null;

		// 如果是违法图片最多支持三张图片
		if (fileName.indexOf("正常") != -1) {
			// 第二张
			File file2 = this.getSecondFile(scanDir, oriFileName);
			if (file2 == null) {
//				log.warn("未找到匹配的第二张图片，对于：" + imageFile.getAbsolutePath());
				return null;
			}
			String fileName2 = file2.getName();
			if ((new File(scanDir + "/" + fileName2)).exists()) {
				imageFiles = new String[2];
				imageFiles[0] = fileName2;
				imageFiles[1] = oriFileName;
			}
		} else {
			String wfxwCode = fileNameSplit[2];
			if(wfxwCode.indexOf("闯红灯") != -1) {
				bean.setWfxwCode("16250");
			} else if(wfxwCode.indexOf("逆行") != -1) {
				bean.setWfxwCode("13010");
			} else if(wfxwCode.indexOf("不按车道行驶") != -1) {
				bean.setWfxwCode("12080");
			} else if(wfxwCode.indexOf("压线") != -1) {
				bean.setWfxwCode("13450");
			} 
//			else if(wfxwCode.indexOf("非机动车道") != -1) {
//				bean.setWfxwCode("16250");
//			}
			// 第二张
			File file2 = this.getThirdFile1(scanDir, oriFileName);
			if (file2 == null) {
//				log.warn("未找到匹配的第二张图片，对于：" + imageFile.getAbsolutePath());
				return null;
			}
			String fileName2 = file2.getName();
			// 第三张
			File file3 = this.getThirdFile2(scanDir, oriFileName);
			if (file3 == null) {
//				log.warn("未找到匹配的第三张图片，对于：" + imageFile.getAbsolutePath());
				return null;
			}
			String fileName3 = file3.getName();

			if ((new File(scanDir + "/" + fileName2)).exists()
					&& (new File(scanDir + "/" + fileName3)).exists()) {
				imageFiles = new String[3];
				imageFiles[0] = oriFileName;
				imageFiles[1] = fileName2;
				imageFiles[2] = fileName3;
			}
		}

		if (imageFiles == null) {
			imageFiles = new String[1];
			imageFiles[0] = oriFileName;
		}
		bean.setImageFiles(imageFiles);

		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#
	 * processViolationInfoBean(java.sql.Connection,
	 * com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
		if (StringHelper.isEmpty(this.getInsertSql())) {
			log.warn("未定义'insert_sql'参数！");
			return 0;
		}
		int result = -1;
		if (violationInfoBean.getImageFiles().length > 2) {
			PreparedStatement preStatement = null;
			try {
				// id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,plate,plate_type_id,file_path_1,file_path_2,file_path_3,file_path_4,video_file_path
				preStatement = conn.prepareStatement(this.getInsertSql());
				preStatement.setLong(1, (long) SequenceFactory.getInstance().getViolateRecordTempSequence());
				preStatement.setTimestamp(2, new Timestamp(violationInfoBean.getViolateTime().getTime()));
				preStatement.setString(3, violationInfoBean.getDeviceInfoBean().getRoadId());
				preStatement.setString(4, violationInfoBean.getDeviceId());
				preStatement.setString(5, violationInfoBean.getDirectionNo());
				preStatement.setString(6, violationInfoBean.getLine());
				preStatement.setString(7, violationInfoBean.getSpeed());
				preStatement.setString(8, violationInfoBean.getLimitSpeed());
				preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
				preStatement.setString(10, violationInfoBean.getPlateNo());

				String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(violationInfoBean.getPlateColor());
				if (StringHelper.isEmpty(plateTypeId)) {
					// 缺省：小型汽车
					plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
				} else {
					plateTypeId = plateTypeId.trim();
				}

				preStatement.setString(11, plateTypeId);

				String pathPrefix = this.getFilePathPrefix()+ violationInfoBean.getDeviceId()+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd")	+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") + "/";
				int i = 0;
				for (; i < violationInfoBean.getImageFiles().length; i++) {
					String imageFileName = violationInfoBean.getImageFiles()[i];
					if (StringHelper.isNotEmpty(imageFileName)) {
						preStatement.setString(12 + i, pathPrefix+ imageFileName);
					} else {
						preStatement.setString(12 + i, null);
					}
				}
				for (; i < 4; i++) {
					preStatement.setString(12 + i, null);
				}

				if (StringHelper.isNotEmpty(violationInfoBean.getViolateType()))
					preStatement.setString(16, violationInfoBean.getViolateType());
				else
					preStatement.setString(16, violationInfoBean.getWfxwCode());

				preStatement.execute();
				result = 0;
			} catch (Exception ex) {
				log.error("数据入库失败：" + ex.getMessage(), ex);
				result = -1;
			} finally {
				if (preStatement != null) {
					try {
						preStatement.close();
					} catch (Exception ex) {
					}
				}
//				try {
//					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
//				} catch (Exception ex1) {
//				}
			}
		} else {
			result = 0;
			PreparedStatement preStatement = null;
			ResultSet resultSet = null;
			int blacklistTypeId = 0;
			try {
				long key = (long) SequenceFactory.getInstance().getVehicleRecordSequence();
				preStatement = conn.prepareStatement("select b.name as blacklist_type,b.id as blacklist_type_id,a.plate from t_its_blacklist a left join t_its_blacklist_type b on a.blacklist_type_id = b.id where a.delete_flag = 'N' and a.plate = ? and a.plate_color_code = ?");
				preStatement.setString(1, violationInfoBean.getPlateNo());
				preStatement.setInt(2, Integer.parseInt(SystemConstant.getInstance().getPlateColorByPlateTypeId(violationInfoBean.getPlateColor())));
				resultSet = preStatement.executeQuery();
				if (resultSet.next()) {
					blacklistTypeId = resultSet.getInt(2);
					this.insertVehicleRecord(conn, key, violationInfoBean,blacklistTypeId);
					this.insertVehicleRecordAlarm(conn, key, violationInfoBean,blacklistTypeId);
				} else {
					this.insertVehicleRecord(conn, key, violationInfoBean,blacklistTypeId);
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}finally {
				DatabaseHelper.close(resultSet, preStatement);
//				try {
//					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
//				} catch (Exception ex1) {
//				}
			}
		}
		return result;
	}

	protected void insertVehicleRecord(Connection conn, long key,ViolationInfoBean violationInfoBean, int blacklistTypeId)throws Exception {
		PreparedStatement preStatement = null;

		try {
			preStatement = conn.prepareStatement("insert into t_its_vehicle_record(id,plate,plate_color_code,catch_time,road_id,device_id,direction_code,direction_drive,driveway_no,speed,limit_speed,alarm_type_id,blacklist_type_id,panorama_image_path,feature_image_path,create_time)values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			preStatement.setLong(1, key);
			preStatement.setString(2, violationInfoBean.getPlateNo());
			preStatement.setInt(3, Integer.parseInt(violationInfoBean.getPlateColor()));
			preStatement.setTimestamp(4, new java.sql.Timestamp(violationInfoBean.getViolateTime().getTime()));

			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(violationInfoBean.getDeviceId());
			preStatement.setInt(5, Integer.parseInt(dib.getRoadId()));

			preStatement.setString(6, violationInfoBean.getDeviceId());
			preStatement.setString(7, violationInfoBean.getDirectionNo());
			preStatement.setString(8, dib.getDirectionDrive());

			String drivewayNo = violationInfoBean.getLine();
			preStatement.setString(9, drivewayNo);
			preStatement.setInt(10, 0);
			preStatement.setInt(11, 0);
			if (blacklistTypeId > 0) {
				preStatement.setInt(12, 1);
			} else {
				preStatement.setInt(12, 0);
			}
			preStatement.setInt(13, blacklistTypeId);
			String pathPrefix = this.getFilePathPrefix()+ violationInfoBean.getDeviceId()+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd")+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") + "/";
			preStatement.setString(14, pathPrefix+ violationInfoBean.getImageFiles()[0]);
			preStatement.setString(15, pathPrefix+ violationInfoBean.getImageFiles()[1]);
			preStatement.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
			preStatement.executeUpdate();
			log.debug("入库成功生成的ID为：" + key);

		} catch (Exception ex) {
			log.error("往通行记录表（T_ITS_VEHICLE_RECORD）中插入记录时出错：" + ex.getMessage(),ex);
			throw ex;
		} finally {
			DatabaseHelper.close(null, preStatement);
		}
	}

	protected void insertVehicleRecordAlarm(Connection conn, long key,ViolationInfoBean violationInfoBean, int blacklistTypeId)	throws Exception {
		PreparedStatement preStatement = null;
		try {
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement("insert into t_its_vehicle_record_alarm(id,plate,plate_color_code,catch_time,road_id,device_id,direction_code,direction_drive,driveway_no,speed,limit_speed,alarm_type_id,blacklist_type_id,panorama_image_path,feature_image_path,create_time)values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			preStatement.setLong(1, key);
			preStatement.setString(2, violationInfoBean.getPlateNo());
			preStatement.setInt(3, Integer.parseInt(violationInfoBean.getPlateColor()));
			preStatement.setTimestamp(4, new java.sql.Timestamp(violationInfoBean.getViolateTime().getTime()));

			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(violationInfoBean.getDeviceId());
			preStatement.setInt(5, Integer.parseInt(dib.getRoadId()));

			preStatement.setString(6, violationInfoBean.getDeviceId());
			preStatement.setString(7, violationInfoBean.getDirectionNo());
			preStatement.setString(8, dib.getDirectionDrive());

			String drivewayNo = violationInfoBean.getLine();
			preStatement.setString(9, drivewayNo);
			preStatement.setInt(10, 0);
			preStatement.setInt(11, 0);
			preStatement.setInt(12, 1);
			preStatement.setInt(13, blacklistTypeId);
			String pathPrefix = this.getFilePathPrefix()+ violationInfoBean.getDeviceId()+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd")+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") + "/";
			preStatement.setString(14, pathPrefix+ violationInfoBean.getImageFiles()[0]);
			preStatement.setString(15, pathPrefix+ violationInfoBean.getImageFiles()[1]);
			preStatement.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
			preStatement.addBatch();

			preStatement.executeBatch();
		} catch (Exception ex) {
			log.error("往通行告警记录表（T_ITS_VEHICLE_RECORD_ALARM）中插入记录时出错："+ ex.getMessage(), ex);
			throw ex;
		} finally {
			DatabaseHelper.close(null, preStatement);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#
	 * postProcessViolationInfoBean
	 * (com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean) {

		boolean backupSuccess = true;
		try {
			String backDir = this.getScannerParam().getBackupDir()+ "/"+ violationInfoBean.getDeviceId()+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd")+ "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") + "/";
			FileHelper.createDir(backDir);

			for (int i = 0; i < violationInfoBean.getImageFiles().length; i++) {
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if (StringHelper.isNotEmpty(imageFileName)) {
					String sourceFileName = violationInfoBean.getFileDir()+ "/" + imageFileName;
					String targetFileName = backDir + "/" + imageFileName;
					log.debug("开始移动文件：" + sourceFileName + " 到 "+ targetFileName);
					FileHelper.moveFile(sourceFileName, targetFileName);
				}
			}

			if (StringHelper.isNotEmpty(violationInfoBean.getVideoFile())) {
				String sourceFileName = violationInfoBean.getFileDir() + "/"+ violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/"+ violationInfoBean.getVideoFile();
				FileHelper.moveFile(sourceFileName, targetFileName);
			}
		} catch (Exception ex) {
			backupSuccess = false;
			log.error(ex.getMessage(), ex);
		}
		return backupSuccess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#
	 * postRestoreViolationInfoBean
	 * (com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean) {
		return true;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getFilePathPrefix() {
		return filePathPrefix;
	}

	public void setFilePathPrefix(String filePathPrefix) {
		this.filePathPrefix = filePathPrefix;
	}

	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public boolean isOrientation() {
		return orientation;
	}

	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

}
