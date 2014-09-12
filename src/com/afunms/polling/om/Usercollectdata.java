package com.afunms.polling.om;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class Usercollectdata implements Serializable {

    /** identifier field */
    private Long id;

    /** nullable persistent field */
    private String ipaddress;

    /** nullable persistent field */
    private String restype;

    /** nullable persistent field */
    private String category;

    /** nullable persistent field */
    private String entity;

	/** nullable persistent field */
	private String subentity;

    /** nullable persistent field */
    private String thevalue;

    /** nullable persistent field */
    private java.util.Calendar collecttime;

    /** nullable persistent field */
    private String unit;

    /** nullable persistent field */
    private Long count;

	private String chname;

	private String bak;

    /** full constructor */
    public Usercollectdata(java.lang.String ipaddress, java.lang.String restype, java.lang.String category, java.lang.String entity, java.lang.String thevalue, java.util.Calendar collecttime, java.lang.String unit, Long count) {
        this.ipaddress = ipaddress;
        this.restype = restype;
        this.category = category;
        this.entity = entity;
        this.thevalue = thevalue;
        this.collecttime = collecttime;
        this.unit = unit;
        this.count = count;
    }

    /** default constructor */
    public Usercollectdata() {
    }



    public java.lang.String getIpaddress() {
        return this.ipaddress;
    }

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

    public java.lang.String getRestype() {
        return this.restype;
    }

	public void setRestype(java.lang.String restype) {
		this.restype = restype;
	}

    public java.lang.String getCategory() {
        return this.category;
    }

	public void setCategory(java.lang.String category) {
		this.category = category;
	}

    public java.lang.String getEntity() {
        return this.entity;
    }

	public void setEntity(java.lang.String entity) {
		this.entity = entity;
	}

    public java.lang.String getThevalue() {
        return this.thevalue;
    }

	public void setThevalue(java.lang.String thevalue) {
		this.thevalue = thevalue;
	}


    public java.lang.String getUnit() {
        return this.unit;
    }

	public void setUnit(java.lang.String unit) {
		this.unit = unit;
	}

	/**
	 * @return
	 */
	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	/**
	 * @return
	 */
	public Long getCount() {
		return count;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param calendar
	 */
	public void setCollecttime(java.util.Calendar calendar) {
		collecttime = calendar;
	}

	/**
	 * @param long1
	 */
	public void setCount(Long long1) {
		count = long1;
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
	public String getSubentity() {
		return subentity;
	}

	/**
	 * @param string
	 */
	public void setSubentity(String string) {
		subentity = string;
	}

		/**
		 * @return
		 */
		public String getBak() {
			return bak;
		}

	/**
	 * @return
	 */
	public String getChname() {
		return chname;
	}

		/**
		 * @param string
		 */
		public void setBak(String string) {
			bak = string;
		}

	/**
	 * @param string
	 */
	public void setChname(String string) {
		chname = string;
	}

}