package com.afunms.polling.om;

import java.io.Serializable;


import java.io.Serializable;
import com.afunms.common.base.*;


/** @author Hibernate CodeGenerator */
public class ARP extends BaseVo implements Serializable {

    /** identifier field */
    private Long id;

    /** nullable persistent field */
    private Long nodeid;

    /** nullable persistent field */
    private String ifindex;
    
    /** nullable persistent field */
    private String ipaddress;

    /** nullable persistent field */
    private String physaddress;

    
    /** nullable persistent field */
    private int monflag;

    /** default constructor */
    public ARP() {
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



	public String getPhysaddress() {
		return physaddress;
	}



	public void setPhysaddress(String physaddress) {
		this.physaddress = physaddress;
	}



	public int getMonflag() {
		return monflag;
	}



	public void setMonflag(int monflag) {
		this.monflag = monflag;
	}



	public Long getNodeid() {
		return nodeid;
	}



	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}


}
