/**
 * 
 */
package com.its.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * 创建日期 2012-9-19 下午04:19:11
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class FileHelper {
	
	/**
	 * 创建目录
	 * @param dirStr
	 * @return
	 * @throws Exception
	 */
	public final static boolean createDir(String dirStr) throws Exception {
		boolean result = true;
		try {
			if(!dirStr.startsWith("\\\\")){
				dirStr = StringHelper.replace(dirStr, "\\", "/");
			}
			else{
				
				String subDir = dirStr.substring(2);
				dirStr = "\\\\"+StringHelper.replace(subDir, "\\", "/");
			}
			dirStr = StringHelper.replace(dirStr, "//", "/");
			
			File f = new File(dirStr);
			if (f.exists()) {
				return true;
			}
			String[] dirArr = StringHelper.split(dirStr, "/");
			int size = dirArr.length;

			String dirName = dirArr[0];

			for (int i = 1; i < size; i++) {
				if (dirArr[i].trim().equals("")) {
					continue;
				}
				dirName += "/" + dirArr[i];
				File file = new File(dirName);
				if (!file.exists()) {
					file.mkdir();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			result = false;
			throw ex;
		}
		return result;
	}	
	
	/**
	 * 删除文件
	 * @param file
	 */
	public final static boolean delFile(File file) throws Exception {
		boolean delResult = true;
		if (!file.exists()) {
			return delResult;
		}
		if (file.isFile()) {
			delResult = file.delete();
			//file.deleteOnExit();
			//System.out.println(file.getAbsolutePath()+"文件已经删除");
		}
		else {
			File[] list = file.listFiles();
			for (int i = 0; i < list.length; i++) {
				FileHelper.delFile(list[i]);
			}
			delResult = file.delete();
		}
		return delResult;
	}
	
    public static final boolean moveFile(String source, String target) throws Exception{
    	FileHelper.copy(source, target);
    	return FileHelper.delFile(new File(source));
    }

	public static final boolean moveFile(File source, File target) throws Exception{
		FileHelper.copy(source, target);
	    return FileHelper.delFile(source);
	}	
	
	public final static void copy(String source, String target) throws Exception {
		FileHelper.copy(new File(source),new File(target));
	}
	
	/**
	 * COPY文件
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public final static void copyByte(File source, File target) throws Exception {
		if (!source.exists()) {
			throw new Exception("Source file:" + source.getName() +
								" is not exists!");
		}
		if (!target.exists()) {
			target.createNewFile();

		}
		DataInputStream in = null;
		DataOutputStream out = null;
		try{
			
			in = new DataInputStream(new BufferedInputStream(new
				FileInputStream(source)));
			out = new DataOutputStream(new BufferedOutputStream(new
				FileOutputStream(target)));
			byte b;
			int i = 0;
			while (in.available() != 0) {
				b = in.readByte();
				out.write(b);
				i++;
				if(i>=512){
					try{
						Thread.sleep(1);
					}catch(Exception ex){}
					i=0;
				}
				
			}
			out.flush();
		}catch(Exception ex){
			throw ex;
		}
		finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception ex1){}
			}
			if(out!=null){
				try{
					out.close();	
				}catch(Exception ex1){}
			}						
		}

	}	
	
	/**
	 * 利用NIO特性复制文件（适合大文件复制）
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public static final void copyFile(File source,File target) throws Exception{
		FileChannel sourceChannel= null;
		FileChannel destinationChannel= null;		
		try{
			sourceChannel= new FileInputStream(source).getChannel();
			destinationChannel= new FileOutputStream(target).getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);			
		}
		catch(Exception ex){
			throw ex;	
		}
		finally{
			if(sourceChannel!=null){
				try{
					sourceChannel.close();
					sourceChannel = null;				
				}
				catch(IOException ioExce){
				}
				
			}
			if(destinationChannel!=null){
				try{
					destinationChannel.close();
					destinationChannel = null;
				}
				catch(IOException ioExce){
				}
			}			
		}	
	}

    public static final void copy(File source, File target) throws Exception{
    	FileOutputStream fos = null;   
	    if(!source.exists())
	        throw new Exception("Source file:" + source.getName() + " is not exists!");
	    if(!target.exists())
	        target.createNewFile();
	    try{
	        fos = new FileOutputStream(target);
	        writeFile(source, fos, 10240);
	    }
	    catch(Exception ex){
	        throw ex;
	    }
	    finally{
	        if(fos != null){
	            try{
	            	fos.close();
	            }
	        	catch(Exception ex1) { }	        
	        }
	    }
    }

	public static boolean writeFile(File file, OutputStream os, int blockSize) throws IOException{
	    boolean result = true;
	    FileInputStream fis = null;
	    try{
	        int readed = 0;
	        fis = new FileInputStream(file);
	        byte buff[] = new byte[blockSize];
	        do{
	            readed = fis.read(buff);
	            if(readed < blockSize){
	                if(readed > -1)
	                    os.write(buff, 0, readed);
	                break;
	            }
	            os.write(buff);
	        } while(true);
	        os.flush();
	    }
	    catch(IOException ex){
	        result = false;
	        throw ex;
	    }
	    finally {
	    	if(fis!=null){
			    try{
			        fis.close();
			    }
			    catch(Exception ex) { }	    		
	    	}
		}
	    return result;
	}
	
	public static void writeFile(String fileName,List<byte[]> byteArrList) throws Exception{
		File target = new File(fileName);
		if(!target.exists()){
			target.createNewFile();
		}
		
		FileOutputStream fos = null; 
		try{
			fos = new FileOutputStream(target);
			int size = byteArrList.size();
			for(int i=0;i<size;i++){
				byte[] tmpByte = byteArrList.get(i);
				fos.write(tmpByte, 0, tmpByte.length);
			}
			fos.flush();
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			if(fos != null){
				try{
					fos.close();
					fos = null;
				}
				catch(Exception ex1) { }	        
			}
		}			
	}
	
	public static void writeFile(byte[] bytes,String fileName) throws Exception{
		File target = new File(fileName);
		if(!target.exists()){
			target.createNewFile();
		}
		
		FileOutputStream fos = null; 
		try{
			fos = new FileOutputStream(target);
			fos.write(bytes, 0, bytes.length);
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			if(fos != null){
				try{
					fos.close();
					fos = null;
				}
				catch(Exception ex1) { }	        
			}
		}		
	}
	
	public static void writeBuffered(byte[] bfile, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
//			File dir = new File(filePath);
//			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
//				dir.mkdirs();
//			}
			file = new File(fileName);
			
			if(!file.exists()){
				file.createNewFile();
			}
			
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static byte[] getBytes(File file) throws Exception{
		byte[] result = null;
	    FileInputStream fis = null;
	    ByteArrayOutputStream bos = null;
	    try{
	        int readed = 0;
	        fis = new FileInputStream(file);
	        bos = new ByteArrayOutputStream();
	        
	        byte buff[] = new byte[4096];
	        do{
	            readed = fis.read(buff);
	            if(readed > 0) bos.write(buff,0,readed);
	            else break;
	        } while(true);
	        result = bos.toByteArray();
	    }
	    catch(IOException ex){
	        throw ex;
	    }
	    finally {
	    	if(fis!=null){
			    try{
			        fis.close();
			    }
			    catch(Exception ex) { }	    		
	    	}
	    	if(bos!=null){
			    try{
			    	bos.close();
			    }
			    catch(Exception ex) { }	    		
	    	}	    	
		}
	    return result;		
	}
	
	/**
	 * 从特定文件获取byte[]
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static byte[] getBytes(String file) throws Exception{
		return FileHelper.getBytes(new File(file));
	}
	
	public static void changeExtName(File oriDir,String oriExtName,String destExtName) throws Exception{
		oriExtName = oriExtName.trim().toUpperCase();
		if(oriDir.isDirectory()){
			File[] list = oriDir.listFiles();
			int len = list.length;
			for(int i=0;i<len;i++){
				File curFile = list[i];
				if(curFile.isDirectory()) FileHelper.changeExtName(curFile, oriExtName, destExtName);
				else{
					String absolutePath = curFile.getAbsolutePath();
					String curFileName = curFile.getName().toUpperCase();
					if(curFileName.endsWith(oriExtName)){
						FileHelper.copy(absolutePath, absolutePath.substring(0,absolutePath.lastIndexOf("."))+destExtName);
					}
				}
			}
		}
	}
	
	/**
	 * 移动指定文件或文件夹(包括所有文件和子文件夹)
	 * @param fromDir 要移动的文件或文件夹	    
	 * @param toDir   目标文件夹	         
	 * @throws Exception
	 */
	public static void MoveFolderAndFileWithSelf(String from, String to) throws Exception {
		try {
			File dir = new File(from);
			// 目标
			to +=  File.separator + dir.getName();
			File moveDir = new File(to);
			if(dir.isDirectory()){
				if (!moveDir.exists()) {
					moveDir.mkdirs();
				}
			}else{
				File tofile = new File(to);
				dir.renameTo(tofile);
				return;
			}
			
			// 文件一览
			File[] files = dir.listFiles();
			if (files == null)
				return;

			// 文件移动
			for (int i = 0; i < files.length; i++) {				
				if (files[i].isDirectory()) {
					MoveFolderAndFileWithSelf(files[i].getPath(), to);
					// 成功，删除原文件
					files[i].delete();
				}
				File moveFile = new File(moveDir.getPath() + File.separator + files[i].getName());
				// 目标文件夹下存在的话，删除
				if (moveFile.exists()) {
					moveFile.delete();
				}
				files[i].renameTo(moveFile);
			}
			dir.delete();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 读取指定盘符的可用空间
	 * @param dirName
	 * @return 
	 */
	public static double getFreeDiskSpace(String dirName) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String command;
			if (os.indexOf("win") >= 0) {
				command = "cmd.exe /c dir " + dirName;
			} else {
				command = "command.com /c dir " + dirName;
			}
			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			process = runtime.exec(command);
			if (process == null) {
				return -1d;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			String freeSpace = null;
			while ((line = in.readLine()) != null) {
				freeSpace = line;
			}
			if (freeSpace == null) {
				return -1d;
			}
			process.destroy();

			freeSpace = freeSpace.trim();
			freeSpace = freeSpace.replaceAll("\\.", "");
			freeSpace = freeSpace.replaceAll(",", "");
			String[] items = freeSpace.split(" ");
			int index = 1;
			while (index < items.length) {

				try {
					long bytes = Long.parseLong(items[index++]);
					return bytes / 1024d / 1024d / 1024d;// 单位GB
				} catch (NumberFormatException nfe) {
				}
			}
			return -1d;
		} catch (Exception exception) {
			return -1d;
		}
	}

}
