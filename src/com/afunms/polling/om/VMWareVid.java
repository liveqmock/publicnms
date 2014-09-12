package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;
import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class VMWareVid extends BaseVo implements Serializable {

    /** identifier field */
    private Long id;

    /** nullable persistent field */
    private Long nodeid;

    /** nullable persistent field */
    private String vid;
    
    /** nullable persistent field */
    private String guestname;
    /** nullable persistent field */
    private String bak;

    private String hoid;
    
    private String flag;
    
    private String category;

    public String getHoid() {
		return hoid;
	}



	public void setHoid(String hoid) {
		this.hoid = hoid;
	}



	public String getFlag() {
		return flag;
	}



	public void setFlag(String flag) {
		this.flag = flag;
	}







	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	/** default constructor */
    public VMWareVid() {
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



	public String getBak() {
		return bak;
	}



	public void setBak(String bak) {
		this.bak = bak;
	}



	public String getVid() {
		return vid;
	}



	public void setVid(String vid) {
		this.vid = vid;
	}



	public String getGuestname() {
		return guestname;
	}



	public void setGuestname(String guestname) {
		this.guestname = guestname;
	}


}
