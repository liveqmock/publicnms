package com.afunms.polling.om;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class IpMacChange extends BaseVo implements Serializable {

    /** identifier field */
    private Long id;
    private String relateipaddr;
    private String ifindex;
    /** nullable persistent field */
    private String ipaddress;

    /** nullable persistent field */
    private String mac;

    /** nullable persistent field */
    private java.util.Calendar collecttime;
    
    private String changetype;  /*1：新增 2：IP-MAC地址变更 3:PORT-MAC变更 4:IP-PORT-MAC变更*/
    
    /** nullable persistent field */
    private String detail;

	private String bak;

    /** full constructor */
    public IpMacChange(java.lang.String ipaddress, java.lang.String mac, java.util.Calendar collecttime,java.lang.String changetype, java.lang.String detail,String relateaddr,String ifindex) {
        this.ipaddress = ipaddress;
        this.mac = mac;
        this.collecttime = collecttime;
        this.detail = detail;
        this.changetype = changetype;
        this.relateipaddr = relateipaddr;
        this.ifindex=ifindex;
    }

    /** default constructor */
    public IpMacChange() {
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

    public java.lang.String getChangetype() {
        return this.changetype;
    }

	public void setChangetype(java.lang.String changetype) {
		this.changetype = changetype;
	}

    public java.lang.String getDetail() {
        return this.detail;
    }

	public void setDetail(java.lang.String detail) {
		this.detail = detail;
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
