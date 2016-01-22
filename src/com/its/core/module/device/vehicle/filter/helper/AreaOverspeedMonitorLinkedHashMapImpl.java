/**
 * 
 */
package com.its.core.module.device.vehicle.filter.helper;

import java.util.LinkedHashMap;

/**
 * 创建日期 2012-12-6 下午03:32:55
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class AreaOverspeedMonitorLinkedHashMapImpl extends LinkedHashMap<String,CarRecordBean> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4561380856254534242L;

	public AreaOverspeedMonitorLinkedHashMapImpl(){
		super();
	}
	
    public AreaOverspeedMonitorLinkedHashMapImpl(int initialCapacity) {
    	super(initialCapacity);
    }
    
	public AreaOverspeedMonitorLinkedHashMapImpl(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
	}

    public AreaOverspeedMonitorLinkedHashMapImpl(int initialCapacity, float loadFactor,boolean accessOrder) {
       super(initialCapacity, loadFactor,accessOrder);
   }

}
