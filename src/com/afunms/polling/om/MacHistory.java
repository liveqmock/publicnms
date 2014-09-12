package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;
import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class MacHistory extends BaseVo implements Serializable {

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
    private String thevalue;

	private String bak;

    /** full constructor */
    public MacHistory(java.lang.String relateipaddr,java.lang.String ifindex,java.lang.String ipaddress, java.lang.String mac, java.util.Calendar collecttime, java.lang.String thevalue) {
        this.relateipaddr = relateipaddr;
        this.ifindex=ifindex;
        this.ipaddress = ipaddress;
        this.mac = mac;
        this.collecttime = collecttime;
        this.thevalue = thevalue;
    }

    /** default constructor */
    public MacHistory() {
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

    public java.lang.String getThevalue() {
        return this.thevalue;
    }

	public void setThevalue(java.lang.String thevalue) {
		this.thevalue = thevalue;
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
	public Long getId() {
		return id;
	}
	
	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
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
}
