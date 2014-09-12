/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

public class Procs extends BaseVo implements Serializable {
	private Integer id;
	/** nullable persistent field */
	private Integer nodeid;
	
	private Integer wbstatus;
	
	private Integer flag;
	
	private String ipaddress;
	
	/** nullable persistent field */
	private String procname;		

	/** identifier field */
	private String chname;

	/** nullable persistent field */
	private String bak;
	
	private String bid;
	
	private java.util.Calendar collecttime;
	
	private int supperid;//π©”¶…Ãid snow add at 2010-5-21
	
	public int getSupperid() {
		return supperid;
	}
	public void setSupperid(int supperid) {
		this.supperid = supperid;
	}

	
	/** full constructor */
	public Procs(Integer nodeid,Integer wbstatus,Integer flag,String ipaddress,String procname, String chname, String bak,java.util.Calendar collecttime,int supperid) {		
		this.nodeid=nodeid;
		this.wbstatus=wbstatus;
		this.flag=flag;
		this.ipaddress = ipaddress;
		this.procname = procname;
		this.chname = chname;
		this.bak=bak;
		this.collecttime = collecttime;
		this.supperid = supperid;
}

	/** default constructor */
	public Procs() {
	}


	/**
	 * @return
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @return
	 */
	public String getProcname() {
		return procname;
	}

	/**
	 * @return
	 */
	public String getChname() {
		return chname;
	}

	/**
	 * @return
	 */
	public String getBak() {
		return bak;
	}

	/**
	 * @param serializable
	 */
	public void setBak(String string) {
		bak = string;
	}

	/**
	 * @param string
	 */
	public void setProcname(String string) {
		procname = string;
	}

	/**
	 * @param string
	 */
	public void setChname(String string) {
		chname = string;
	}

	/**
	 * @param string
	 */
	public void setIpaddress(String string) {
		ipaddress = string;
	}

    public Integer getId() {
        return this.id;
    }

	public void setId(Integer id) {
		this.id = id;
	}
	
	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}

	public Integer getWbstatus() {
		return wbstatus;
	}

	public void setWbstatus(Integer wbstatus) {
		this.wbstatus = wbstatus;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	
	
}
