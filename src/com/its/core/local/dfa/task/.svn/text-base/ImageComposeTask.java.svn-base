/**
 * 
 */
package com.its.core.local.dfa.task;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.FilenameFilterByPostfixAndSize;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-10-22 下午09:39:06
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ImageComposeTask extends ATask {
	
	private static final Log log = LogFactory.getLog(ImageComposeTask.class);
	
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
		
		this.imageProcess(dirFile,this.getFromDir());		
		
	}
	
	private void imageProcess(File dir,String scanDir){	
		
		try {								
			
//			File[] files = dir.listFiles();
			File[] files = dir.listFiles(new FilenameFilterByPostfixAndSize("1.jpg", true, true, 10240));
			
			if (files == null)
			return;
			
			for (int i = 0; i < files.length; i++) {	
				
				File file = files[i];
				log.info("文件名：" + file.getName());
				
				if(file.isDirectory()){
					this.imageProcess(file,scanDir);
				}else{
					boolean moveSuccess = false;
					String fileName = file.getName();
					
					if(fileName.indexOf(".tmp") == -1) {
						String subdirectory =  "/" + fileName.substring(fileName.indexOf("R")+1, fileName.indexOf("D")) +"/"+ fileName.substring(fileName.indexOf("T")+1, fileName.indexOf("T")+9) +"/"+fileName.substring(fileName.indexOf("T")+9, fileName.indexOf("T")+11)+"/";
						FileHelper.createDir(this.getToDir()+subdirectory);
						
						//违法图片最多支持四张图片		
						String imageNum = fileName.substring(fileName.lastIndexOf("S") + 1, fileName.lastIndexOf("S") + 2) ;
						if(Integer.parseInt(imageNum) == 3) {
							//第二张
							File file2 = this.getThirdFile1(scanDir, fileName);
							if(file2==null){
								log.warn("未找到匹配的第二张图片，对于："+fileName);								
							}
							String fileName2 = file2.getName();
							//第三张
							File file3 = this.getThirdFile2(scanDir, fileName);
							if(file3==null){
								log.warn("未找到匹配的第三张图片，对于："+fileName);
							}
							String fileName3 = file3.getName();
							
							if((new File(scanDir+"/"+fileName2)).exists() && (new File(scanDir+"/"+fileName3)).exists()){
								moveSuccess = FileHelper.moveFile(file, new File(this.getToDir() +subdirectory + fileName));
							}
						}
						
						
						
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
