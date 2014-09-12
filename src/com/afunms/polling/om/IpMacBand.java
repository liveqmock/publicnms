package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class IpMacBand implements Serializable {

    /** identifier field */
    private Long id;
    
    /** nullable persistent field */
    private String relateipaddr;

    /** nullable persistent field */
    private String ifindex;
    
    /** nullable persistent field */
    private String ipaddress;

    /** nullable persistent field */
    private String mac;

    /** nullable persistent field */
    private java.util.Calendar collecttime;
    
    /** nullable persistent field */
    private String ifband;

    /** nullable persistent field */
    private String ifsms;

	private String bak;
	
	/** nullable persistent field */
	private Integer employee_id;


    /** full constructor */
    public IpMacBand(java.lang.String relateipaddr,java.lang.String ifindex,java.lang.String ipaddress, java.lang.String mac, java.util.Calendar collecttime, java.lang.String ifband, java.lang.String ifsms,Integer employee_id) {
        this.relateipaddr = relateipaddr;
        this.ifindex=ifindex;
        this.ipaddress = ipaddress;
        this.mac = mac;
        this.collecttime = collecttime;
        this.ifband = ifband;
        this.ifsms = ifsms;
        this.employee_id = employee_id;
    }

    /** default constructor */
    public IpMacBand() {
    }

    public java.lang.String getRelateipaddr() {
        return this.relateipaddr;
    }

	public void setRelateipaddr(java.lang.String relateipaddr) {
		this.relateipaddr = relateipaddr;
	}

    public java.lang.String getIfindex() {
        return this.ifindex;
    }

	public void setIfindex(java.lang.String ifindex) {
		this.ifindex = ifindex;
	}

    public java.lang.String getIpaddress() {
        return this.ipaddress;
    }

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

    public java.lang.String getMac() {
        return this.mac;
    }

	public void setMac(java.lang.String mac) {
		this.mac = mac;
	}

    public java.lang.String getIfband() {
        return this.ifband;
    }

	public void setIfband(java.lang.String ifband) {
		this.ifband = ifband;
	}

    public java.lang.String getIfsms() {
        return this.ifsms;
    }

	public void setIfsms(java.lang.String ifsms) {
		this.ifsms = ifsms;
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

		/**
		 * @return
		 */
		public String getBak() {
			return bak;
		}

		/**
		 * @param string
		 */
		public void setBak(String string) {
			bak = string;
		}
		
		public Integer getEmployee_id(){
			return employee_id;
		}
		public void setEmployee_id(Integer employee_id){
			this.employee_id = employee_id;
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
