/**
 * 
 */
package com.its.core.local.hezhou.task;



import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.its.core.local.hezhou.tmri.tfc.webservice.TransProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DateHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-10-15 下午07:43:11
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicleToWxsTaskNew extends ATask {
	private static final Log log = LogFactory.getLog(ExportVehicleToWxsTask.class);
	
	private String wsEndPoint = "http://10.151.195.2:9080/rminf/services/Trans";

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		// TODO Auto-generated method stub
		this.wsEndPoint = props.getProperty(propertiesPrefix, no, "ws_info.ws_endpoint");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		long initResult = 0;
		try {
			log.debug(this.getWsEndPoint());
			TransProxy proxy = new TransProxy();
			proxy.setEndpoint(wsEndPoint);
			
			initResult = this.initTrans(proxy);		
			
			//获取接口调用错误日志
			this.getLastMessage(proxy);
			
//			if(initResult < 1) {
//				Thread.sleep(600000); 
//				return;			
//			}			
				
			long result = 0;					
			
			result = proxy.writeVehicleInfo(
					"451100100004",//卡口编号
					"1",//方向类型						
					(long)1,//车道编号
					"桂JA3989",//号牌号码
					"02",//号牌种类
					"2013-10-16 16:03:05",//经过时间
					(long)0,//车辆速度(整数，最长3位)
					(long)0,//车辆限速(整数，最长3位)
					"",//违章行为编码
					(long)300,//车外廓长
					"2",//号牌颜色
					"",//车辆类型
					"",//辅助号牌种类
					"",//辅助号牌号码
					"",//辅助号牌颜色
					"",//车辆品牌
					"",//车辆外形
					"",//车身颜色
					"http://10.151.195.1:81/v1",//通行图片路径
					"/451100100004/20131016/16/X03R451100100004D4L01I000V000N55863T20131016160305583S11.JPG",//通行图片1
					"",//通行图片2
					"",//通行图片3
					""
					);
			
			log.debug("调用结果：" + result + "(正数:执行成功；零与负数：错误代码[具体信息参考附录错误代码表]；)");		
			
			//获取接口调用错误日志
			this.getLastMessage(proxy);
			
		}
		catch(Exception ex){
			log.error("上传失败："+ex.getMessage(),ex);				
		}		

	}
	
	private long initTrans(TransProxy proxy) throws Exception {		

		long initResult = 1;
		try {	
			
			StringBuffer info = new StringBuffer();
			info.append("<info>")
			.append("<jkid>62C01</jkid>")			
			.append("<jkxlh>7A1E1D0D06070304091502010002090200060904050817E1B03E6D72692E636E</jkxlh>")
			.append("<time>").append(DateHelper.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")).append("</time>")
			.append("</info>");
			log.info(info);
			long cdh = 1;		
			
//				log.debug("卡口编号：" + rs.getString("device_id") + " 方向类型：" + rs.getString("direction_id"));
				initResult = proxy.initTrans("451100100004", "1", cdh, info.toString());
				log.debug("注册返回结果：" + initResult + " (正数:执行成功；零与负数：错误代码[具体信息参考附录错误代码表]；)");				
					
		} catch(Exception ex){				
			log.error("设备注册失败："+ex.getMessage(),ex);			
			initResult = 0;
		}
		return initResult;
	}	
	
	private void getLastMessage(TransProxy proxy) throws Exception {		
		
        String message = null;
		try {				
			message = proxy.getLastMessage();					
		} catch(Exception ex){				
			log.error("获取错误日志失败："+ex.getMessage(),ex);			

		}		
		log.debug("报错信息：" + message);
	}
	

	/**
	 * @return the wsEndPoint
	 */
	public String getWsEndPoint() {
		return wsEndPoint;
	}

	/**
	 * @param wsEndPoint the wsEndPoint to set
	 */
	public void setWsEndPoint(String wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

}
