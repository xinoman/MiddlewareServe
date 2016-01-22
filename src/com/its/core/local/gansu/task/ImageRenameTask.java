/**
 * 
 */
package com.its.core.local.gansu.task;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.module.task.ATask;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-7-17 下午09:48:29
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ImageRenameTask extends ATask {
	
	private static final Log log = LogFactory.getLog(ImageRenameTask.class);
	
	private String fromDir = null;
	private String toDir = null;
	
	public static Map<String,String> HPYS_MAP = new HashMap<String,String>();
	public static Map<String,String> FXBH_MAP = new HashMap<String,String>();
	
	static{
		HPYS_MAP.put("1", "3");
		HPYS_MAP.put("2", "0");
		HPYS_MAP.put("3", "1");
		HPYS_MAP.put("4", "2");
		
		FXBH_MAP.put("15", "1");
		FXBH_MAP.put("51", "2");
		FXBH_MAP.put("37", "3");
		FXBH_MAP.put("73", "4");
		FXBH_MAP.put("26", "1");
		FXBH_MAP.put("62", "1");
		FXBH_MAP.put("48", "1");
		FXBH_MAP.put("84", "1");
		FXBH_MAP.put("1", "1");
		FXBH_MAP.put("2", "1");
	}

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
		
		this.fileProcess(dirFile);	

	}
	
	private void fileProcess(File fromDir) {
		
//		long startTime = System.currentTimeMillis();
		try {
//			FileHelper.createDir(this.getToDir());
			
			File[] files = fromDir.listFiles(new FilenameFilterByPostfix(".ini",false,true));
			if (files == null)
			return;
			
			for (int i = 0; i < files.length; i++) {	
				
				File file = files[i];
				if(file.isDirectory()){
					this.fileProcess(file);
				}else{
											
					RandomAccessFile raf = new RandomAccessFile(file,"r");					
					
					StringBuffer content = new StringBuffer();
	                while (raf.getFilePointer() < raf.length()) {	                	
	                    String line = new String(raf.readLine().trim().getBytes("ISO-8859-1"),"GB2312");  	
	                    content.append(line).append(";");	                   
	                }  
					
					String sbbh = content.substring(content.indexOf("deviceNo=")+9, content.indexOf(";directionNo"));
					String jgsj = content.substring(content.indexOf("date=")+5, content.indexOf(";V1_Time"))+" "+content.substring(content.indexOf("V1_Time=")+8, content.indexOf(";V2_Time"));
					String fxbh = content.substring(content.indexOf("directionID=")+12, content.indexOf(";laneNo"));
					int cdbh = Integer.parseInt(content.substring(content.indexOf("laneNo=")+7, content.indexOf(";laneID")))+1;
					String clsd = content.substring(content.indexOf("speed=")+6, content.indexOf(";speedLimit")).substring(0, content.substring(content.indexOf("speed=")+6, content.indexOf(";speedLimit")).indexOf("."));
					String clxs = content.substring(content.indexOf("speedLimit=")+11, content.indexOf(";V1_RedTime")).substring(0, content.substring(content.indexOf("speedLimit=")+11, content.indexOf(";V1_RedTime")).indexOf("."));
					String hphm = content.substring(content.indexOf("licence=")+8, content.indexOf(";platecolornum"));
					String hpys = content.substring(content.indexOf("platecolornum=")+14, content.indexOf(";peccancyType"));
					int tpzs = Integer.parseInt(content.substring(content.indexOf("c_count=")+8, content.indexOf(";C1")));						
					int zplx = Integer.parseInt(content.substring(content.indexOf("peccancyType=")+13, content.indexOf(";speed")));
					
					String subdirectory =  "/" + sbbh +"/"+ DateHelper.dateToString(DateHelper.parseDateString(jgsj,"yyyy-MM-dd HH:mm:ss"), "yyyyMMdd") +"/"+ DateHelper.dateToString(DateHelper.parseDateString(jgsj,"yyyy-MM-dd HH:mm:ss"), "HH")+"/";
					FileHelper.createDir(this.getToDir()+subdirectory);
					if(zplx == 0) {
						String txtp = content.substring(content.indexOf("C1=")+3, content.indexOf(";C1Pass"));
						
						raf.close();	
						
						//限定速度值为三位
						while(clxs.length()<3) clxs = "0" + clxs;
						while(clsd.length()<3) clsd = "0" + clsd;
						
						int rad = (int) (Math.random() * (99999 - 10000)) + 10000;
						StringBuffer targetName = new StringBuffer("X03R").append(
							sbbh).append("D").append(FXBH_MAP.get(fxbh)).append("L0").append(
							cdbh).append("I").append(clxs).append("V").append(
							clsd).append("N").append(rad).append("T").append(
							DateHelper.dateToString(DateHelper.parseDateString(
									jgsj, "yyyy-MM-dd HH:mm:ss"),
									"yyyyMMddHHmmssSSS"));
						
						if(StringHelper.isNotEmpty(hphm)) {
							targetName.append("&").append(hphm).append("&").append(HPYS_MAP.get(hpys)).append("S11.JPG");
						} else {
							targetName.append("S11.JPG");
						}
						
						String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\")+1);
						boolean moveSuccess = FileHelper.moveFile(new File(path+txtp), new File(this.getToDir() + subdirectory + targetName));
						if (moveSuccess) {
							log.warn("文件：" + txtp + " 转移成功！");
						}
						else {
							log.warn("文件：" + txtp + " 处理失败，文件可能正在使用中！");		
						}
					} else {
						String zjtp1 = content.substring(content.indexOf("C1=")+3, content.indexOf(";C1Pass"));					
						String zjtp2 = content.substring(content.indexOf("C2=")+3, content.indexOf(";C2Pass"));
						String zjtp3 = content.substring(content.indexOf("C3=")+3, content.indexOf(";C3Pass"));
						
						raf.close();	
						
						//限定速度值为三位
						while(clxs.length()<3) clxs = "0" + clxs;
						while(clsd.length()<3) clsd = "0" + clsd;
						
						int rad = (int) (Math.random() * (99999 - 10000)) + 10000;
						StringBuffer targetName = new StringBuffer("X00R").append(
							sbbh).append("D").append(FXBH_MAP.get(fxbh)).append("L0").append(
							cdbh).append("I").append(clxs).append("V").append(
							clsd).append("N").append(rad).append("T").append(
							DateHelper.dateToString(DateHelper.parseDateString(
									jgsj, "yyyy-MM-dd HH:mm:ss"),
									"yyyyMMddHHmmssSSS"));
						
						if(StringHelper.isNotEmpty(hphm)) {
							targetName.append("&").append(hphm).append("&").append(HPYS_MAP.get(hpys));
						} 
						
						String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\")+1);						
						
						if(tpzs==3) {
							boolean moveSuccess1 = FileHelper.moveFile(new File(path+zjtp1), new File(this.getToDir() + subdirectory + targetName +"S31.JPG"));
							if (moveSuccess1) {
								log.warn("文件：" + zjtp1 + " 转移成功！");
							}
							else {
								log.warn("文件：" + zjtp1 + " 处理失败，文件可能正在使用中！");		
							}
							
							boolean moveSuccess2 = FileHelper.moveFile(new File(path+zjtp2), new File(this.getToDir() + subdirectory + targetName+"S32.JPG"));
							if (moveSuccess2) {
								log.warn("文件：" + zjtp2 + " 转移成功！");
							}
							else {
								log.warn("文件：" + zjtp2 + " 处理失败，文件可能正在使用中！");		
							}
							
							boolean moveSuccess3 = FileHelper.moveFile(new File(path+zjtp3), new File(this.getToDir() + subdirectory + targetName+"S33.JPG"));
							if (moveSuccess3) {
								log.warn("文件：" + zjtp3 + " 转移成功！");
							}
							else {
								log.warn("文件：" + zjtp3 + " 处理失败，文件可能正在使用中！");		
							}
							
						} else if(tpzs==2) {
							boolean moveSuccess1 = FileHelper.moveFile(new File(path+zjtp1), new File(this.getToDir() + subdirectory + targetName+"S21.JPG"));
							if (moveSuccess1) {
								log.warn("文件：" + zjtp1 + " 转移成功！");
							}
							else {
								log.warn("文件：" + zjtp1 + " 处理失败，文件可能正在使用中！");		
							}
							
							boolean moveSuccess2 = FileHelper.moveFile(new File(path+zjtp2), new File(this.getToDir() + subdirectory + targetName+"S22.JPG"));
							if (moveSuccess2) {
								log.warn("文件：" + zjtp2 + " 转移成功！");
							}
							else {
								log.warn("文件：" + zjtp2 + " 处理失败，文件可能正在使用中！");		
							}
							
						} else if(tpzs==1) {
							boolean moveSuccess1 = FileHelper.moveFile(new File(path+zjtp1), new File(this.getToDir() + subdirectory + targetName+"S11.JPG"));
							if (moveSuccess1) {
								log.warn("文件：" + zjtp1 + " 转移成功！");
							}
							else {
								log.warn("文件：" + zjtp1 + " 处理失败，文件可能正在使用中！");		
							}
							
						}
						
					}					
					
					//删除ini文件
					FileHelper.delFile(file);
					
//					long endTime = System.currentTimeMillis();
//					log.debug("本次共重命名图片数：[" + (files.length-1) +"]条 耗时："+(endTime-startTime)/1000+"秒！");			
				}				
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} 	

	}

	/**
	 * @return the fromDir
	 */
	public String getFromDir() {
		return fromDir;
	}

	/**
	 * @param fromDir the fromDir to set
	 */
	public void setFromDir(String fromDir) {
		this.fromDir = fromDir;
	}

	/**
	 * @return the toDir
	 */
	public String getToDir() {
		return toDir;
	}

	/**
	 * @param toDir the toDir to set
	 */
	public void setToDir(String toDir) {
		this.toDir = toDir;
	}

}
