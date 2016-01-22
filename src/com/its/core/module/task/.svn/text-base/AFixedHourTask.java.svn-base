/**
 * 
 */
package com.its.core.module.task;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.PropertiesHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-11-8 下午05:13:00
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public abstract class AFixedHourTask extends ATask {
	
	private static final Log log = LogFactory.getLog(AFixedHourTask.class);
	
	 //任务开始时间，缺省凌晨1点。
	public int startHour = 1;

	//任务停止时间（小时），当任务运行时当前时间与该时间匹配时，任务停止。
	public int stopHour = 7;
	
	//当前任务结束时间
	private GregorianCalendar stopCalendar = new GregorianCalendar();

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.period = PropertiesHelper.getLong(propertiesPrefix,no,"period",props,3600L);
		this.startHour = PropertiesHelper.getInt(propertiesPrefix,no,"start_hour",props,1);
		this.stopHour = PropertiesHelper.getInt(propertiesPrefix,no,"stop_hour",props,7);

		if (this.startHour > 23) this.startHour = 23;
		if (this.stopHour > 23) this.stopHour = 23;		
		
		this.setPeriod(this.getPeriod());
		
		this.configureSpecificallyProperties(props, propertiesPrefix, no);	
		
		log.debug("Task Start at :" + this.startHour);

	}
	
   /* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#run()
	 */
	@Override
	public void run() {
		try{
			//当前时间
			GregorianCalendar currentCalendar = new GregorianCalendar();
			
			//启动时间
			GregorianCalendar startCalendar = new GregorianCalendar();
			startCalendar.set(
					currentCalendar.get(Calendar.YEAR), 
					currentCalendar.get(Calendar.MONTH), 
					currentCalendar.get(Calendar.DATE),
					this.startHour, 
					0, 
					0);
			
			//结束时间
			this.stopCalendar.set(
					currentCalendar.get(Calendar.YEAR), 
					currentCalendar.get(Calendar.MONTH), 
					currentCalendar.get(Calendar.DATE),
					this.stopHour, 
					0, 
					0);
			
			//如果开始时间大于或等于结束时间,则可将结束时间理解为第二天的时间
			if(this.startHour>=this.stopHour){
				this.stopCalendar.setTimeInMillis(this.stopCalendar.getTimeInMillis()+24*3600000L);
			}
		
			if(currentCalendar.after(startCalendar) && currentCalendar.before(stopCalendar)){
				//log.debug("可以执行:"+DateHelper.dateToString(currentCalendar.getTime(),"yyyy-MM-dd HH:mm:ss"));
				this.execute();
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}
	}

	//判断是否超时,该方法对于执行时间较长的任务有用,一般在循环中使用,如超时可退出循环。
	protected boolean isOvertime() {		
		//结束时间
		GregorianCalendar currentCalendar = new GregorianCalendar();
		
		if(currentCalendar.after(this.stopCalendar)){
			return true;
		}
		
		return false;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStopHour() {
		return stopHour;
	}

	public void setStopHour(int stopHour) {
		this.stopHour = stopHour;
	}

}
