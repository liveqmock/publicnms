package com.afunms.config.model;

import java.io.Serializable;


import java.io.Serializable;
import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class NetNodeCfgFile extends BaseVo implements Serializable {

    /** identifier field */
    private Long id;
    
    /** nullable persistent field */
    private String ipaddress;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private java.util.Calendar recordtime;
    

    /** full constructor */
    public NetNodeCfgFile(java.lang.String ipaddress, java.lang.String name, java.util.Calendar recordtime) {
        this.ipaddress = ipaddress;
        this.name = name;
        this.recordtime = recordtime;
    }

    /** default constructor */
    public NetNodeCfgFile() {
    }

    public java.lang.String getIpaddress() {
        return this.ipaddress;
    }

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

    public java.lang.String getName() {
        return this.name;
    }

	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public java.util.Calendar getRecordtime() {
		return recordtime;
	}

	/**
	 * @param calendar
	 */
	public void setRecordtime(java.util.Calendar recordtime) {
		this.recordtime = recordtime;
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
}
