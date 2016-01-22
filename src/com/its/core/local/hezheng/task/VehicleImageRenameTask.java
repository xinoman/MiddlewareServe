/**
 * 
 */
package com.its.core.local.hezheng.task;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.its.core.module.task.ATask;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfixAndSize;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2014年9月21日 下午6:13:58
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class VehicleImageRenameTask extends ATask {
	private static final Log log = LogFactory.getLog(VehicleImageRenameTask.class);
	
	private String fromDir = null;
	private String toDir = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.fromDir = props.getProperty(propertiesPrefix, no, "from_dir");
		this.toDir = props.getProperty(propertiesPrefix, no, "to_dir");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		File dirFile = new File(this.getFromDir());
		if(!dirFile.exists()){
			log.warn("源目录不存在："+this.getFromDir());
			return;
		}
		if(!dirFile.isDirectory()){
			log.warn("不是目录："+this.getFromDir());
			return;			
		}
		
		this.imageProcess(dirFile);		
		
	}
	
	private void imageProcess(File dir){	
		
		try {				
			File[] files = dir.listFiles(new FilenameFilterByPostfixAndSize(".jpg", true, true, 102400));
			if (files == null) return;
			
			for (int i = 0; i < files.length; i++) {	
				
				File file = files[i];
				log.info("文件名：" + file.getName());
				
				if(file.isDirectory()){
					this.imageProcess(file);
				}else{
					
					boolean moveSuccess = false;
					String fileName = file.getName();
					String sbbh ="",jgsj="",hphm="",hpys="",clsd="",clxs="";
					try {
						String[] fileNameSplit = StringHelper.split(fileName, "_");
						sbbh = fileNameSplit[2];
						jgsj = fileNameSplit[0];
						clsd = fileNameSplit[4].substring(fileNameSplit[4].indexOf("速度")+2, fileNameSplit[4].indexOf("Km"));
						while(clsd.length()<3) {
							clsd = "0"+clsd;
						}
						
						if(fileNameSplit[5].indexOf(".") != -1){
							clxs = fileNameSplit[5].substring(fileNameSplit[5].indexOf("限速")+2, fileNameSplit[5].indexOf("."));
						}else {
							clxs = fileNameSplit[5].substring(fileNameSplit[5].indexOf("限速")+2, fileNameSplit[5].indexOf("km"));
						}
						clxs = StringHelper.replace(clxs, "Km", "");
//						System.out.println(clxs);
						while(clxs.length()<3) {
							clxs = "0"+clxs;
						}
						hphm = fileNameSplit[3];
						hpys = "2";
						
					} catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						log.debug("格式错误,删除文件："+file.getName());
						FileHelper.delFile(file);
					}
					
				
					String subdirectory =  "/" + sbbh +"/" + jgsj.substring(0, 8) +"/" + jgsj.substring(8, 10) + "/";
					FileHelper.createDir(this.getToDir()+subdirectory);					
					
					int rad = (int) (Math.random() * (99999 - 10000)) + 10000;
					StringBuffer targetName = new StringBuffer("X03R").append(
						sbbh).append("D1L01").append("I").append(clxs).append("V").append(clsd).append("N").append(rad).append("T").append(jgsj);
					
					if(StringHelper.isNotEmpty(hphm) && hphm.indexOf("无") == -1) {
						
						targetName.append("&").append(hphm).append("&").append(hpys).append("S11.JPG");
					} else {
						targetName.append("S11.JPG");
					}
					
					moveSuccess = FileHelper.moveFile(file, new File(this.getToDir() +subdirectory + targetName.toString()));
					
					if (moveSuccess)
						log.warn("文件：" + file.getName() + " 处理成功！");
					else
						log.warn("文件：" + file.getName() + " 处理失败，文件可能正在使用中！");					
				
				}				
			}			
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}				
	}	

	public String getFromDir() {
		return fromDir;
	}

	public void setFromDir(String fromDir) {
		this.fromDir = fromDir;
	}

	public String getToDir() {
		return toDir;
	}

	public void setToDir(String toDir) {
		this.toDir = toDir;
	}

}
