/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.ImageHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-12-13 下午01:33:09
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ViolationFileScannerImpl extends AViolationFileScanner {
	private static final Log log = LogFactory.getLog(ViolationFileScannerImpl.class);
	
    private String insertSql = null;
    private String filePathPrefix = null;
    
    //图片是否合并
	private boolean merge = false;
	private boolean orientation = false;

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#configureLocalProperties(com.swy.tiip.tools.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.filePathPrefix = props.getProperty(propertiesPrefix,no,"standard_version.file_path_prefix");
		this.merge = StringHelper.getBoolean(props.getProperty(propertiesPrefix,no,"standard_version.merge"));
		this.orientation = StringHelper.getBoolean(props.getProperty(propertiesPrefix,no,"standard_version.orientation"));
		this.insertSql = props.getProperty(propertiesPrefix,no,"standard_version.insert_sql");

	}	
	
	/**
	 * 根据第一个图片文件名，获取第二张图片文件
	 * @param imageFile
	 * @return
	 */
	protected File getSecondFile(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("T"));			
		String postfix = StringHelper.replace(firstFileName.substring(firstFileName.indexOf("S")),"1","2");			
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}
	
	/**
	 * 根据第一个图片文件名，获取第二、三张图片文件
	 * @param imageFile
	 * @return
	 */
	protected File getThirdFile1(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("T"));			
		String postfix = StringHelper.replace(firstFileName.substring(firstFileName.indexOf("S")),"1","2");			
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}
	
	protected File getThirdFile2(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("T"));			
		String postfix = StringHelper.replace(firstFileName.substring(firstFileName.indexOf("S")),"1","3");				
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}
	
	/**
	 * 根据第一个图片文件名，获取第二、三、四张图片文件
	 * @param imageFile
	 * @return
	 */
	protected File getFourthFile1(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("T"));			
		String postfix = StringHelper.replace(firstFileName.substring(firstFileName.indexOf("S")),"1","2");			
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}
	
	protected File getFourthFile2(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("T"));			
		String postfix = StringHelper.replace(firstFileName.substring(firstFileName.indexOf("S")),"1","3");			
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}
	
	protected File getFourthFile3(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("T"));		
		String postfix = StringHelper.replace(firstFileName.substring(firstFileName.indexOf("S")),"1","4");		
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;			
			if(len>0) return fileArr[0];
		}
		return null;
	}

	@Override
	protected ViolationInfoBean parseViolationInfo(String scanDir, File imageFile) throws Exception {
		
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();			
		
		ViolationInfoBean bean = new ViolationInfoBean();
		
		int violateTypeIndex = fileName.indexOf("X") + 1;
		String violateType = fileName.substring(violateTypeIndex, violateTypeIndex + 2);
		bean.setViolateType(violateType);
		
		int deviceIndex = fileName.indexOf("R") + 1;
		int directionIndex = fileName.indexOf("D");
		String deviceId = fileName.substring(deviceIndex, directionIndex);
		bean.setDeviceId(deviceId);
		
//		int directionIndexOf = fileName.indexOf("D");
//		if(directionIndexOf!=-1){
//			 String directionNo = fileName.substring(directionIndexOf+1,directionIndexOf+2);
//			 bean.setDirectionNo(directionNo);
//						 
//		}
		
		//行驶方向
		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(bean.getDeviceId());
		bean.setDirectionNo(dib.getDirectionCode());
		
		String line = "00";
		if(fileName.indexOf("L")!=-1){
			int lineIndex = fileName.indexOf("L") + 1;
			line = fileName.substring(lineIndex, lineIndex + 2);			
		}
		bean.setLine(line);	
		
		int limitSpeedIndex = fileName.indexOf("I");
		int realSpeedIndex = fileName.indexOf("V");
		if (limitSpeedIndex != -1 && realSpeedIndex != -1) {
			String limitSpeed = fileName.substring(limitSpeedIndex + 1, limitSpeedIndex + 4);
			bean.setLimitSpeed(limitSpeed);
			String realSpeed = fileName.substring(realSpeedIndex + 1, realSpeedIndex + 4);
			bean.setSpeed(realSpeed);
		}
		
		int timeIndex = fileName.indexOf("T") + 1;
		String timeStr = fileName.substring(timeIndex, timeIndex + 14);
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(date);		
		
		//有车牌信息
		int plateFirstIndexOf = fileName.indexOf("&");
		int plateColorLastIndexOf = fileName.lastIndexOf("&");
		if (plateFirstIndexOf != -1 && plateColorLastIndexOf != -1 && plateFirstIndexOf != plateColorLastIndexOf) {
			String plateNo = fileName.substring(plateFirstIndexOf + 1, plateColorLastIndexOf);			
			bean.setPlateNo(plateNo);
			bean.setPlateColor(fileName.substring(plateColorLastIndexOf + 1, plateColorLastIndexOf + 2));
		}		
		
		String[] imageFiles = null;
		
		//如果是违法图片最多支持四张图片		
		String imageNum = fileName.substring(fileName.lastIndexOf("S") + 1, fileName.lastIndexOf("S") + 2) ;		
		if(Integer.parseInt(imageNum)== 2){
			File file2 = this.getSecondFile(scanDir, oriFileName);
			if(file2==null){
				log.warn("未找到匹配的第二张图片，对于："+imageFile.getAbsolutePath());
				return null;
			}
			String fileName2 = file2.getName();
			if((new File(scanDir+"/"+fileName2)).exists()){
				imageFiles = new String[2];				
				imageFiles[0] = oriFileName;
				imageFiles[1] = fileName2;
			}			
		} else if(Integer.parseInt(imageNum) == 3) {
			//第二张
			File file2 = this.getThirdFile1(scanDir, oriFileName);
			if(file2==null){
				log.warn("未找到匹配的第二张图片，对于："+imageFile.getAbsolutePath());
				return null;
			}
			String fileName2 = file2.getName();
			//第三张
			File file3 = this.getThirdFile2(scanDir, oriFileName);
			if(file3==null){
				log.warn("未找到匹配的第三张图片，对于："+imageFile.getAbsolutePath());
				return null;
			}
			String fileName3 = file3.getName();
			
			if((new File(scanDir+"/"+fileName2)).exists() && (new File(scanDir+"/"+fileName3)).exists()){
				imageFiles = new String[3];				
				imageFiles[0] = oriFileName;
				imageFiles[1] = fileName2;
				imageFiles[2] = fileName3;
			}			
		} else if(Integer.parseInt(imageNum) == 4) {
			//第二张
			File file2 = this.getFourthFile1(scanDir, oriFileName);
			if(file2==null){
				log.warn("未找到匹配的第二张图片，对于："+imageFile.getAbsolutePath());
				return null;
			}
			String fileName2 = file2.getName();
			//第三张
			File file3 = this.getFourthFile2(scanDir, oriFileName);
			if(file3==null){
				log.warn("未找到匹配的第三张图片，对于："+imageFile.getAbsolutePath());
				return null;
			}
			String fileName3 = file3.getName();
			//第三张
			File file4 = this.getFourthFile3(scanDir, oriFileName);
			if(file4==null){
				log.warn("未找到匹配的第四张图片，对于："+imageFile.getAbsolutePath());
				return null;
			}
			String fileName4 = file4.getName();
			if((new File(scanDir+"/"+fileName2)).exists() && (new File(scanDir+"/"+fileName3)).exists() &&(new File(scanDir+"/"+fileName4)).exists()){
				imageFiles = new String[4];				
				imageFiles[0] = oriFileName;
				imageFiles[1] = fileName2;
				imageFiles[2] = fileName3;
				imageFiles[3] = fileName4;
			}
		} 
		
		if(imageFiles==null){			
			imageFiles = new String[1];
			imageFiles[0] = oriFileName;
		}		
		bean.setImageFiles(imageFiles);		
		
		return bean;
	}

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#processViolationInfoBean(java.sql.Connection, com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
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
			
			String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(violationInfoBean.getPlateColor());
			if(StringHelper.isEmpty(plateTypeId)){
				//缺省：小型汽车
				plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
			}
			else{
				plateTypeId = plateTypeId.trim();
			}
			
			preStatement.setString(11, plateTypeId);
			
			String pathPrefix = this.getFilePathPrefix() + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			int i=0;
			for(;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){
					preStatement.setString(12+i, pathPrefix + imageFileName);
				}
				else{
					preStatement.setString(12+i, null);
				}
			}
			for(;i<4;i++){
				preStatement.setString(12+i, null);
			}				
			
			if(StringHelper.isNotEmpty(violationInfoBean.getViolateType()))
				preStatement.setString(16, violationInfoBean.getViolateType());
			else
				preStatement.setString(16, "00");			

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
	
	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#postProcessViolationInfoBean(com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
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

	@Override
	protected void createMark(ViolationInfoBean violationInfoBean) {		
		String[] waterMarkArr = null;
		String scanDir = violationInfoBean.getFileDir();
		String line = violationInfoBean.getLine();		
		
		String filePrefix = violationInfoBean.getImageFiles()[0].substring(0,violationInfoBean.getImageFiles()[0].indexOf("S"));		
		
		//时间格式示例：yyyy-MM-dd HH:mm:ss/yyyy年MM月dd日HH点mm分ss秒
		if(StringHelper.isNotEmpty(violationInfoBean.getSpeed()) && Integer.parseInt(violationInfoBean.getSpeed())>0 && Integer.parseInt(violationInfoBean.getSpeed())>Integer.parseInt(violationInfoBean.getLimitSpeed())){				
			waterMarkArr = new String[]{
					"时间:"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss") +
					" 地点:"+violationInfoBean.getDeviceInfoBean().getRoadName()+
					"("+SystemConstant.getInstance().getDirectionNameByCode(violationInfoBean.getDirectionNo())+ "方向)" +
					" 车道:"+line +
					" 限速:"+Integer.parseInt(violationInfoBean.getSpeed()) +
					" 实速:"+Integer.parseInt(violationInfoBean.getLimitSpeed())
			};		
			
		}
		else{				
			waterMarkArr = new String[]{
					"时间:"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss") +
					"地点:"+violationInfoBean.getDeviceInfoBean().getRoadName()+
					"("+SystemConstant.getInstance().getDirectionNameByCode(violationInfoBean.getDirectionNo())+ "方向)" +
					" 车道:"+line
																	
			};					
		}		
		
		try {
			String imageFileName1 = violationInfoBean.getImageFiles()[0];		
			String fullImageName1 = scanDir + "/" + imageFileName1;
			
			byte[] fileByte1 = FileHelper.getBytes(new File(fullImageName1));
			fileByte1 = ImageHelper.createWaterMark(
					fileByte1,
					waterMarkArr,
					this.getScannerParam().getWaterMarkFontSize(),
					this.getScannerParam().getWaterMarkFontHeight(),
					this.getScannerParam().getWaterMarkFontColor(),
					null,
					this.getScannerParam().getWaterMarkLeftMargin(),
					this.getScannerParam().getWaterMarkTopMargin());			

			
			byte[] fileByte2 = null;
			if(violationInfoBean.getImageFiles().length>1) {
				String imageFileName2 = violationInfoBean.getImageFiles()[1];		
				String fullPicName2 = scanDir + "/" + imageFileName2;
				fileByte2 = FileHelper.getBytes(new File(fullPicName2));
				fileByte2 = ImageHelper.createWaterMark(
						fileByte2,
						waterMarkArr,
						this.getScannerParam().getWaterMarkFontSize(),
						this.getScannerParam().getWaterMarkFontHeight(),
						this.getScannerParam().getWaterMarkFontColor(),
						null,
						this.getScannerParam().getWaterMarkLeftMargin(),
						this.getScannerParam().getWaterMarkTopMargin());
			}			
			
			byte[] fileByte3 = null;
			if(violationInfoBean.getImageFiles().length>2) {
				String imageFileName3 = violationInfoBean.getImageFiles()[2];
				String fullPicName3 = scanDir + "/" + imageFileName3;
				fileByte3 = FileHelper.getBytes(new File(fullPicName3));
				fileByte3 = ImageHelper.createWaterMark(
						fileByte3,
						waterMarkArr,
						this.getScannerParam().getWaterMarkFontSize(),
						this.getScannerParam().getWaterMarkFontHeight(),
						this.getScannerParam().getWaterMarkFontColor(),
						null,
						this.getScannerParam().getWaterMarkLeftMargin(),
						this.getScannerParam().getWaterMarkTopMargin());
			}
			
			if(this.isMerge()) {
				String newFileName = filePrefix+"S11.JPG";
				if(violationInfoBean.getImageFiles().length>2) {
					
					FileHelper.writeFile(ImageHelper.compose(ImageHelper.compose(fileByte1, fileByte2, this.isOrientation()),fileByte3,this.isOrientation()),violationInfoBean.getFileDir()+"/"+newFileName);
					FileHelper.delFile(new File(violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[0]));
					FileHelper.delFile(new File(violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[1]));
					FileHelper.delFile(new File(violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[2]));
				} else {
					
					FileHelper.writeFile(ImageHelper.compose(fileByte1, fileByte2, this.isOrientation()),violationInfoBean.getFileDir()+"/"+newFileName);
					FileHelper.delFile(new File(violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[0]));
					FileHelper.delFile(new File(violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[1]));
				}		
				
				String[] imageFiles = new String[]{newFileName};
				violationInfoBean.setImageFiles(imageFiles);
			} else {
				
				if(violationInfoBean.getImageFiles().length>2) {
					FileHelper.writeFile(fileByte1, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[0]);
					FileHelper.writeFile(fileByte2, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[1]);
					FileHelper.writeFile(fileByte3, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[2]);
				} else if(violationInfoBean.getImageFiles().length>1){
					FileHelper.writeFile(fileByte1, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[0]);
					FileHelper.writeFile(fileByte2, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[1]);
				} else {
					FileHelper.writeFile(fileByte1, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[0]);
				}
			}
		
		
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		System.gc();
	}

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#postRestoreViolationInfoBean(com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
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
