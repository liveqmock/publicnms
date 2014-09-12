/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class Smscontent extends BaseVo implements java.io.Serializable
{
    private Integer id; 
    private String message;
    private String type;
    private String ip;
    private String subtype;
    private String objname;
    private String objid;
    private String category;
    private String subentity;
    private String value;
    private String level;
    private String recordtime;
	private java.util.Calendar sendtime;
    
    /** default constructor */
    public Smscontent() {
    }
    public java.lang.String getObjid() {
        return this.objid;
    }
	public void setObjid(java.lang.String objid) {
		this.objid = objid;
	}
	
    public java.lang.String getRecordtime() {
        return this.recordtime;
    }
	public void setRecordtime(java.lang.String recordtime) {
		this.recordtime = recordtime;
	}
	
    public java.lang.String getLevel() {
        return this.level;
    }
	public void setLevel(java.lang.String level) {
		this.level = level;
	}	
	
    public java.lang.String getValue() {
        return this.value;
    }
	public void setValue(java.lang.String value) {
		this.value = value;
	}
	
    public java.lang.String getSubentity() {
        return this.subentity;
    }
	public void setSubentity(java.lang.String subentity) {
		this.subentity = subentity;
	}
	
    public java.lang.String getCategory() {
        return this.category;
    }
	public void setCategory(java.lang.String category) {
		this.category = category;
	}
	
    public java.lang.String getObjname() {
        return this.objname;
    }
	public void setObjname(java.lang.String objname) {
		this.objname = objname;
	}
	
    public java.lang.String getSubtype() {
        return this.subtype;
    }

	public void setSubtype(java.lang.String subtype) {
		this.subtype = subtype;
	}
	
	
    public java.lang.String getIp() {
        return this.ip;
    }

	public void setIp(java.lang.String ip) {
		this.ip = ip;
	}
	
    public java.lang.String getType() {
        return this.type;
    }

	public void setType(java.lang.String type) {
		this.type = type;
	}
	
	
    /** full constructor */
    public Smscontent(Integer id, java.lang.String message) {
        this.id = id;
        this.message = message;
    }
    public Integer getId() {
        return this.id;
    }

	public void setId(Integer id) {
		this.id = id;
	}

    public java.lang.String getMessage() {
        return this.message;
    }

	public void setMessage(java.lang.String message) {
		this.message = message;
	}

	/**
	 * @return
	 */
	public java.util.Calendar getSendtime() {
		return sendtime;
	}

	/**
	 * @param calendar
	 */
	public void setSendtime(java.util.Calendar calendar) {
		sendtime = calendar;
	}

}
