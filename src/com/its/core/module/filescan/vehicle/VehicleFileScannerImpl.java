/**
 * 
 */
package com.its.core.module.filescan.vehicle;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.module.device.DeviceCommunicateModule;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.module.filescan.violation.AViolationFileScanner;
import com.its.core.module.filescan.violation.ViolationInfoBean;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-12-29 上午10:33:39
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class VehicleFileScannerImpl extends AViolationFileScanner {
	private static final Log log = LogFactory.getLog(VehicleFileScannerImpl.class);
	
	private DeviceCommunicateModule deviceCommunicateModule = null;
	
	public DeviceCommunicateModule getDeviceCommunicateModule() {
		return deviceCommunicateModule;
	}

	public void setDeviceCommunicateModule(DeviceCommunicateModule deviceCommunicateModule) {
		this.deviceCommunicateModule = deviceCommunicateModule;
	}

	public VehicleFileScannerImpl(DeviceCommunicateModule deviceCommunicateModule){
		this.deviceCommunicateModule = deviceCommunicateModule;
	}
	
   private String filePathPrefix = null;

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#configureLocalProperties(com.swy.tiip.tools.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.filePathPrefix = props.getProperty(propertiesPrefix,no,"standard_version.file_path_prefix");
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
		
		//行驶方向
		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(bean.getDeviceId());
		bean.setDirectionNo(dib.getDirectionCode());
		
		String line = "01";
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
		String plateNo ="";
		String plateColor = "";
		int plateFirstIndexOf = fileName.indexOf("&");
		int plateColorLastIndexOf = fileName.lastIndexOf("&");
		if (plateFirstIndexOf != -1 && plateColorLastIndexOf != -1 && plateFirstIndexOf != plateColorLastIndexOf) {
			plateNo = fileName.substring(plateFirstIndexOf + 1, plateColorLastIndexOf);		
			plateColor = fileName.substring(plateColorLastIndexOf + 1, plateColorLastIndexOf + 2);
			bean.setPlateNo(plateNo);
			bean.setPlateColor(plateColor);
		}		
		
		String[] imageFiles = new String[1];
		imageFiles[0] = oriFileName;
		bean.setImageFiles(imageFiles);		
		
		//创建XML Content
		StringBuffer xml = new StringBuffer();
		
		xml.append("<message>");
		xml.append("<head>");
			xml.append("<code>DS1005</code>");
			xml.append("<version>2.0.0.0</version>");
		xml.append("</head>");
		xml.append("<body>");		
				xml.append("<content>");
				xml.append("<sbbh>").append(deviceId).append("</sbbh>");
				xml.append("<fxbh>").append(dib.getDirectionCode()).append("</fxbh>");
				if(StringHelper.isNotEmpty(plateNo)) {
					xml.append("<hphm>").append(plateNo).append("</hphm>");
				} else {
					xml.append("<hphm></hphm>");
				}
				
				if(StringHelper.isNotEmpty(plateColor)) {
					xml.append("<hpys>").append(plateColor).append("</hpys>");
				} else {
					xml.append("<hpys></hpys>");
				}
				xml.append("<jgsj>").append(date).append("</jgsj>");
				xml.append("<cdbh>").append(line).append("</cdbh>");
				xml.append("<clsd>").append(deviceId).append("</clsd>");
				xml.append("<clxs>").append(deviceId).append("</clxs>");
				xml.append("<txtp>").append(this.getFilePathPrefix()+imageFiles).append("</txtp>");
			xml.append("</content>");
		xml.append("</body>");
		xml.append("</message>");
		
		log.info(this.getFilePathPrefix()+oriFileName);
		
//		Iterator<MessageBean> iterator = MessageHelper.parse(xml.toString()).iterator();
//		while(iterator.hasNext()){
//			MessageBean messageBean = iterator.next();
////			messageBean.setSessionKey(IoSessionUtils.generateKey(session));
////			messageBean.setFromIp(IoSessionUtils.getRemoteIp(session));
//			
//			String headWord = messageBean.getHead();			
//			BlockingQueue<MessageBean> queue = this.getDeviceCommunicateModule().getInfoQueue(headWord);				
//				if(queue==null){					
//					queue = this.getDeviceCommunicateModule().getInfoQueue("other");
//				}				
////			}
//			
//			if(queue==null){
//				log.warn("对于头文字为：'"+headWord+"'的信息没有找到相应的处理程序！");				
//			}
//			else{
////				queue.put(messageBean);				
//				try {
//					boolean success = queue.offer(messageBean, 1, TimeUnit.SECONDS);					
//					if(!success){
//						log.warn("队列满（"+headWord+"），当前队列长度:"+queue.size());
//						queue.clear();
//					}	
//					log.debug("当前队列长度:"+queue.size());
//				} catch (InterruptedException e) {
//					queue.clear();
//					log.error(e.getMessage(),e);
//				}
//			}
//			
//			iterator.remove();
//		}
		
		return bean;
	}

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#processViolationInfoBean(java.sql.Connection, com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
		return 0;
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

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#postRestoreViolationInfoBean(com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean) {
		return true;
	}
	
	public String getFilePathPrefix() {
		return filePathPrefix;
	}

	public void setFilePathPrefix(String filePathPrefix) {
		this.filePathPrefix = filePathPrefix;
	}

}
