/**
 * 
 */
package com.its.core.module.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.XMLProperties;

/**
 * 设备通讯处理器抽象类，一般增加新的协议时扩展该类即可
 * 创建日期 2012-9-20 下午02:39:39
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public abstract class ACommunicateProcessor implements ICommunicateProcessor {
	private static final Log log = LogFactory.getLog(ACommunicateProcessor.class);
	
	//对应于该处理器类的头文字标识
	protected String headWord = null;
	
	//处理器类型
	protected int processorType = PROCESSOR_TYPE_DEVICE;
	
	//设备通讯模块主类，需要从该类中获取相关信息
	protected DeviceCommunicateModule deviceCommunicateModule = null;

	abstract public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception ;
	
	abstract public void process(MessageBean messageBean) throws Exception;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			MessageBean messageBean = null;
			try {
				messageBean = this.getCurrentProtocolBean();
			} catch (InterruptedException e) {
				log.error(e);
				log.error("停止线程："+this.getClass().getName());
				break;
			} catch (Exception ex){
				log.error(ex);
			}
			if(messageBean!=null){
//				log.debug("处理队列中的信息："+messageBean.getFull());
				try {
					this.process(messageBean);
				} catch (Exception e) {
					log.error("处理信息'"+messageBean.getFull()+"'时出错："+e.getMessage(),e);
				}
			}
		}

	}
	
	/**
	 * 从队列中取当前协议信息
	 * @return
	 * @throws InterruptedException
	 */
	private MessageBean getCurrentProtocolBean() throws InterruptedException{		
		return (MessageBean)this.getDeviceCommunicateModule().getInfoQueue(this.getHeadWord()).take();
	}
	
	/**
	 * @return the headWord
	 */
	public String getHeadWord() {
		return headWord;
	}

	/**
	 * @param headWord the headWord to set
	 */
	public void setHeadWord(String headWord) {
		this.headWord = headWord;
	}

	/**
	 * @return the processorType
	 */
	public int getProcessorType() {
		return processorType;
	}

	/**
	 * @param processorType the processorType to set
	 */
	public void setProcessorType(int processorType) {
		this.processorType = processorType;
	}

	/**
	 * @return the deviceCommunicateModule
	 */
	public DeviceCommunicateModule getDeviceCommunicateModule() {
		return deviceCommunicateModule;
	}

	/**
	 * @param deviceCommunicateModule the deviceCommunicateModule to set
	 */
	public void setDeviceCommunicateModule(DeviceCommunicateModule deviceCommunicateModule) {
		this.deviceCommunicateModule = deviceCommunicateModule;
	}

}
