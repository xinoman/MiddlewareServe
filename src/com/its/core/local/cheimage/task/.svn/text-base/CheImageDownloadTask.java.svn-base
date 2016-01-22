/**
 * 
 */
package com.its.core.local.cheimage.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.module.task.ATask;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-4-26 下午04:06:54
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class CheImageDownloadTask extends ATask {
	private static final Log log = LogFactory.getLog(CheImageDownloadTask.class);
	
	private String url = null;		
	private String brand = null;	
	private String type = null;
	private String model = null;
	
	
	private String imageSaveDir = null;
	
	Map<String, ProductListBean> deviceMap = new HashMap<String, ProductListBean>();
	List<ProductListBean> productlist = new ArrayList<ProductListBean>();

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.url = props.getProperty(propertiesPrefix,no,"car.url");	
		this.brand = props.getProperty(propertiesPrefix,no,"car.brand");	
		this.type = props.getProperty(propertiesPrefix,no,"car.type");
		this.model = props.getProperty(propertiesPrefix,no,"car.model");	
		
		this.imageSaveDir = props.getProperty(propertiesPrefix,no,"image_save_dir");
		
		int size = props.getPropertyNum(propertiesPrefix,no,"product_list.product");
		for(int i=0;i<size;i++){
			String name		= props.getProperty(propertiesPrefix,no,"product_list.product",i,"name");
			String node		= props.getProperty(propertiesPrefix,no,"product_list.product",i,"node");
			String sn		= props.getProperty(propertiesPrefix,no,"product_list.product",i,"sn");
			
			ProductListBean bean = new ProductListBean();
			bean.setName(name);
			bean.setNode(node);
			bean.setSn(sn);
			productlist.add(bean);
		}

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		
		
		Iterator iterator = productlist.iterator();		
		log.info("size = " + productlist.size());
		
		while(iterator.hasNext()){
			ProductListBean record = (ProductListBean)iterator.next();
			String[] number = StringHelper.split(record.getSn(), ",");
			int start = Integer.parseInt(number[0]);
			int end = Integer.parseInt(number[1]);
			boolean result = true;
			
			long startTime = System.currentTimeMillis();
			short currentExcelRow = 1;
			for(int i=start;i<=end;i++) {
				try {
					byte[] bytes = ImageHelper.getImageBytes(url+record.getNode()+"/"+start+".jpg");
					String dirPath = imageSaveDir+"/"+DateHelper.dateToString(new Date(), "yyyyMMdd")+"/"+brand+"/"+type+"/"+model+"/"+record.getName()+"/";
					FileHelper.createDir(dirPath);
					FileHelper.writeFile(bytes, dirPath+start+".JPG");
				} catch (Exception e) {
					result = false;
					log.debug("未找到图片！" + url+record.getNode()+"/"+start+".jpg");
				}
				if(result) {
					log.debug(url+record.getNode()+"/"+start+".jpg"+" 下载完成！");
				}			
				start++;
				currentExcelRow++;
			}
			long currentTime = System.currentTimeMillis();
			log.info(record.getName()+" 本次共下载图片：" + (currentExcelRow-1) + "张 耗时：" + ((currentTime- startTime) / 1000F) + "秒！");
		}
		
	}

}
