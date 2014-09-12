package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;
import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class VMWareConnectConfig extends BaseVo implements Serializable {

    /** identifier field */
    private Long id;

    /** nullable persistent field */
    private Long nodeid;

    /** nullable persistent field */
    private String username;
    
    /** nullable persistent field */
    private String pwd;
    
    /** nullable persistent field */
    private String hosturl;
    
    /** nullable persistent field */
    private String bak;


    /** default constructor */
    public VMWareConnectConfig() {
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



	


	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPwd() {
		return pwd;
	}



	public void setPwd(String pwd) {
		this.pwd = pwd;
	}



	public String getHosturl() {
		return hosturl;
	}



	public void setHosturl(String hosturl) {
		this.hosturl = hosturl;
	}



	public String getBak() {
		return bak;
	}



	public void setBak(String bak) {
		this.bak = bak;
	}


}
