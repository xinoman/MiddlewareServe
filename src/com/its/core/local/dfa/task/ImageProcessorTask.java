/**
 * 
 */
package com.its.core.local.dfa.task;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.module.task.ATask;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfixAndSize;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-12-21 下午09:04:10
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ImageProcessorTask extends ATask {
	private static final Log log = LogFactory.getLog(ImageProcessorTask.class);
	
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
			
//			File[] files = dir.listFiles();
			File[] files = dir.listFiles(new FilenameFilterByPostfixAndSize(".jpg", true, true, 10240));
			if (files == null)
			return;
			
			for (int i = 0; i < files.length; i++) {	
				
				File file = files[i];
				log.info("文件名：" + file.getName());
				
				if(file.isDirectory()){
					this.imageProcess(file);
				}else{
					boolean moveSuccess = false;
					String fileName = file.getName();
					
					if(fileName.indexOf(".tmp") == -1) {
						String subdirectory =  "/" + fileName.substring(fileName.indexOf("R")+1, fileName.indexOf("D")) +"/"+ fileName.substring(fileName.indexOf("T")+1, fileName.indexOf("T")+9) +"/"+fileName.substring(fileName.indexOf("T")+9, fileName.indexOf("T")+11)+"/";
						FileHelper.createDir(this.getToDir()+subdirectory);
						
//						String targetFileName =  fileName.substring(0, fileName.indexOf("F")) + fileName.substring(fileName.indexOf("S"), fileName.length());
					
						moveSuccess = FileHelper.moveFile(file, new File(this.getToDir() +subdirectory + fileName));
						
						if (moveSuccess)
							log.warn("文件：" + file.getName() + " 处理成功！");
						else
							log.warn("文件：" + file.getName() + " 处理失败，文件可能正在使用中！");
					} else {
						
					}
				
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
