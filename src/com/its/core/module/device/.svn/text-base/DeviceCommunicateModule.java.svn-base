/**
 * 
 */
package com.its.core.module.device;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.its.core.constant.Environment;
import com.its.core.module.IModule;
import com.its.core.util.InstructionHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-19 下午05:30:47
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceCommunicateModule implements IModule {
	private static final Log log = LogFactory.getLog(DeviceCommunicateModule.class);
	
	//服务器监听端口
	private int serverPort = 9995;	

	/**
	 * 中心接收设备发送过来的信息，根据协议头创建各自的信息队列，各处理类从各自的队列中取信息
	 * key:协议头文字
	 * value:队列
	 */
	private Map<String,BlockingQueue<MessageBean>> infoQueueMap = new HashMap<String,BlockingQueue<MessageBean>>();	
	
	/**
	 * 保存所有前端设备SESSION
	 * key:IP地址
	 * value:IoSession
	 */
	private Map<String,IoSession> sessionsMap = Collections.synchronizedMap(new HashMap<String,IoSession>());

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#config(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	public void config(XMLProperties xmlProperties, String propertiesPrefix,int no) throws Exception {
		this.serverPort = PropertiesHelper.getInt(propertiesPrefix,no,"parameters.port",xmlProperties,this.serverPort);
		InstructionHelper.ITS_SERVER_PORT = this.serverPort;
		
		//装载并启动与设备直接通信的处理器程序
		String prefix = Environment.DEVICE_COMMUNICATE_HANDLE_SERVER_PREFIX;
		String protocolPrefix = prefix + ".protocol.head";
		int size = xmlProperties.getPropertyNum(protocolPrefix);
		for (int i = 0; i < size; i++) {
			String valid = xmlProperties.getProperty(protocolPrefix, i, "valid");
			if ("true".equalsIgnoreCase(valid) || "y".equalsIgnoreCase(valid)) {
				String processorClass = xmlProperties.getProperty(protocolPrefix, i, "processor");
				if (processorClass != null && !processorClass.trim().equals("")) {
					try {
						String word = xmlProperties.getProperty(protocolPrefix, i, "word");
						
						//为每种类型的处理器创建各自的消息队列
						BlockingQueue<MessageBean> infoQueue = new LinkedBlockingQueue<MessageBean>(5000);						
						this.infoQueueMap.put(word, infoQueue);
						
						ICommunicateProcessor processor = (ICommunicateProcessor) Class.forName(processorClass).newInstance();
						processor.setHeadWord(word);
						processor.setProcessorType(ICommunicateProcessor.PROCESSOR_TYPE_DEVICE);
						processor.setDeviceCommunicateModule(this);
						processor.configure(xmlProperties, protocolPrefix, i);
						//processorMap.put(word, processor);
						Thread processorThread = new Thread(processor);
						processorThread.start();
						
						log.debug("Loaded : " + processorClass);
					}
					catch (Exception ex) {
						log.error("初始化通信处理程序[" + processorClass + "]时出错：" + ex.getMessage(), ex);
					}
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#start()
	 */
	public void start() throws Exception {
		//设置buffer
		ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        
        //定义acceptor
        IoAcceptor acceptor = new SocketAcceptor();
        //((SocketAcceptorConfig) acceptor.getDefaultConfig()).setReuseAddress(true);
        
        //定义config
        IoAcceptorConfig config = new SocketAcceptorConfig();  
        
        //设置config,加入filter
        SocketSessionConfig ssc = (SocketSessionConfig)config.getSessionConfig();
        ssc.setReuseAddress(true);
        ssc.setTcpNoDelay(true);
        ssc.setReceiveBufferSize(4096);
        ssc.setSendBufferSize(4096);
        ssc.setKeepAlive(true);
        //log.debug("SocketSessionConfig="+ssc);
        
        //使用字符串编码
        DefaultIoFilterChainBuilder chain = config.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        //chain.addLast("logger", new LoggingFilter());
        
        //加入port handler config
        DeviceProtocolHandler deviceProtocolHandler = new DeviceProtocolHandler(this);
        acceptor.bind(new InetSocketAddress(this.serverPort), deviceProtocolHandler,config);

        log.info("DeviceCommunicateModule Listening on port " + this.serverPort);

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#stop()
	 */	
	public void stop() throws Exception {
		throw new java.lang.UnsupportedOperationException();
	}
	
	public BlockingQueue<MessageBean> getInfoQueue(String headWord){
		return this.getInfoQueueMap().get(headWord);
	}	
	
	public IoSession getIoSession(String sessionKey){
		return this.getSessionsMap().get(sessionKey);
	}

	public Map<String, BlockingQueue<MessageBean>> getInfoQueueMap() {
		return infoQueueMap;
	}

	public void setInfoQueueMap(Map<String, BlockingQueue<MessageBean>> infoQueueMap) {
		this.infoQueueMap = infoQueueMap;
	}	

	public Map<String, IoSession> getSessionsMap() {
		return sessionsMap;
	}

	public void setSessionsMap(Map<String, IoSession> sessionsMap) {
		this.sessionsMap = sessionsMap;
	}	

}
