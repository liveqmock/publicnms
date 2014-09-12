package com.afunms.polling.om;

import java.io.Serializable;

public class Avgcollectdata implements Serializable {

	/** nullable persistent field */
    private String ipaddress;
    
    /** nullable persistent field */
    private String thevalue;
    
    private String bak;
    
    private String unit;

    public java.lang.String getIpaddress() {
        return this.ipaddress;
    }

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

  

    public java.lang.String getThevalue() {
        return this.thevalue;
    }

	public void setThevalue(java.lang.String thevalue) {
		this.thevalue = thevalue;
	}

	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}