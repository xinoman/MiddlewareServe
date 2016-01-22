package com.its.core.common.sequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.SystemConstant;
import com.its.core.util.StringHelper;

public abstract class SequenceFactory {
	private static final Log log = LogFactory.getLog(SequenceFactory.class);
	
	private static SequenceFactory sequenceFactory = null; 
	
	static{
		String factoryClassName = SystemConstant.getInstance().getProperty("its.common.sequence_factory.class.name");
		if(StringHelper.isNotEmpty(factoryClassName)){
			try {
				log.debug("Load SequenceFactory = "+factoryClassName);
				sequenceFactory = (SequenceFactory)Class.forName(factoryClassName).newInstance();
			} catch (Exception e) {
				log.error(e);
				sequenceFactory = new ItsSequenceFactory(); 
			}			
		}
		sequenceFactory.config();
	}
	
	public static SequenceFactory getInstance(){
		return sequenceFactory;
	}
	
	public abstract void config();
	
	public double getViolateRecordTempSequence() throws Exception{
		throw new java.lang.UnsupportedOperationException();
	}
	
	public double getVehicleRecordSequence() throws Exception{
		throw new java.lang.UnsupportedOperationException();
	}
	
	public double getStatTrafficHourStatSequence() throws Exception{
		throw new java.lang.UnsupportedOperationException();
	}
	
	public double getStatTrafficDayStatSequence() throws Exception{
		throw new java.lang.UnsupportedOperationException();
	}
	
	public double getDeviceStatusSequence() throws Exception{
		throw new java.lang.UnsupportedOperationException();
	}
}
