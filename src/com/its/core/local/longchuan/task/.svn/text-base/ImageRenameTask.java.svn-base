/**
 * 
 */
package com.its.core.local.longchuan.task;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.module.task.ATask;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfixAndSize;
import com.its.core.util.ImageHelper;
import com.its.core.util.ImageIO;
import com.its.core.util.XMLProperties;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 创建日期 2013-9-15 下午10:09:44
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ImageRenameTask extends ATask {
	private static final Log log = LogFactory.getLog(ImageRenameTask.class);
	
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
			File[] files = dir.listFiles(new FilenameFilterByPostfixAndSize(".jpg", true, true, 204800));
			if (files == null)
			return;
			
			for (int i = 0; i < files.length; i++) {	
				
				File file = files[i];
				log.info("文件名：" + file.getName());
				
				if(file.isDirectory()){
					this.imageProcess(file);
				}else{
					
					byte[] imgByte = FileHelper.getBytes(file);
					String[] waterMarkArr = new String[]{"waterMark"};
					ByteArrayInputStream bis = new ByteArrayInputStream(imgByte,0,imgByte.length);
					Image imageSrc = ImageIO.read(bis);
					boolean result = this.createWaterMark(imageSrc, waterMarkArr, 28, 31, Color.YELLOW, null, 5, 5);
					
					if(result) {
						boolean moveSuccess = false;
						String fileName = file.getName();
						String subdirectory =  "/" + fileName.substring(fileName.indexOf("R")+1, fileName.indexOf("D")) +"/"+ fileName.substring(fileName.indexOf("T")+1, fileName.indexOf("T")+9) +"/"+fileName.substring(fileName.indexOf("T")+9, fileName.indexOf("T")+11)+"/";
						FileHelper.createDir(this.getToDir()+subdirectory);
						
						String targetFileName =  fileName.substring(0, fileName.indexOf("F")) + fileName.substring(fileName.indexOf("S"), fileName.length());
					
						moveSuccess = FileHelper.moveFile(file, new File(this.getToDir() +subdirectory + targetFileName));
						
						if (moveSuccess)
							log.warn("文件：" + file.getName() + " 处理成功！");
						else
							log.warn("文件：" + file.getName() + " 处理失败，文件可能正在使用中！");
					} else {
						log.info("图片完整性检查失败：直接删除！" + file.getAbsolutePath());
						FileHelper.delFile(file);
					}
				
				}				
			}			
			
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}				
	}
	
	public boolean createWaterMark(Image imageSrc,String[] waterMarkArr,int fontSize,int fontHeight,Color fontColor,Color bgColor,int leftMargin,int topMargin) throws Exception{
		boolean result = true;
		ByteArrayOutputStream bos = null;
		try {
			//log.debug("imageSrc = "+imageSrc);
			int width = imageSrc.getWidth(null);
			int height = imageSrc.getHeight(null);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.drawImage(imageSrc, 0, 0, width, height, null);
			Font font = new Font("微软雅黑", Font.BOLD, fontSize);			
			g.setFont(font);
			
			int size = waterMarkArr.length;
			for(int i=0;i<size;i++){
				if(bgColor!=null){
					g.setColor(bgColor);
					
					int bgWidth = 0;
					int len = waterMarkArr[i].length();
					for(int j=0;j<len;j++){
						char theChar = waterMarkArr[i].charAt(j);
						if((theChar>'0' && theChar<'9') || (theChar>'a' && theChar<'z') || (theChar>'A' && theChar<'Z') || (theChar==' ') || (theChar=='-') || (theChar==':')){
							bgWidth += Math.round(font.getSize()/3.0);
						}
						else{
							//bgWidth += font.getSize();
							bgWidth += font.getSize()*1.2;
						}
					}
					g.fillRect(leftMargin,topMargin+fontHeight*i+4,bgWidth,fontHeight-2);
				}				
				g.setColor(fontColor);
				g.drawString(waterMarkArr[i], leftMargin, topMargin+fontHeight*(i+1));
			}
			
			g.dispose();
			
			bos = new ByteArrayOutputStream(); 
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			encoder.encode(image);
			bos.toByteArray();	
		} catch (Exception e) {
			result = false;
			log.error(e.getMessage(),e);			
		} finally{
			if(bos!=null){
				try {
					bos.close();
				} catch (IOException e1) {
					log.error(e1);
				}
			}
		}
		return result;
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
