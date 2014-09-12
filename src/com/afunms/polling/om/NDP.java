package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;
import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class NDP extends BaseVo implements Serializable {

    /** identifier field */
    private Long id;

    /** nullable persistent field */
    private Long nodeid;

    /** nullable persistent field */
    private String deviceId;
    
    /** nullable persistent field */
    private String portName;
    
    /** nullable persistent field */
    private java.util.Calendar collecttime;

    /** default constructor */
    public NDP() {
    }

    

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}



	public Long getNodeid() {
		return nodeid;
	}



	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}



	public String getDeviceId() {
		return deviceId;
	}



	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	public String getPortName() {
		return portName;
	}



	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	/**
	 * @return
	 */
	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	/**
	 * @param calendar
	 */
	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}


}
