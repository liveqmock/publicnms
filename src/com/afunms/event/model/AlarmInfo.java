/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class AlarmInfo extends BaseVo implements java.io.Serializable
{
	private Integer id;
    private String content;
    private String ipaddress;
    private Integer level1;
    private java.util.Calendar recordtime;
    private String type;
    
    /** default constructor */
    public AlarmInfo() {
    }
    public Integer getId() {
        return this.id;
    }

	public void setId(Integer id) {
		this.id = id;
	}

    public java.lang.String getContent() {
        return this.content;
    }

	public void setContent(java.lang.String content) {
		this.content = content;
	}

    public java.lang.String getIpaddress() {
        return this.ipaddress;
    }

	public void setIpaddress(java.lang.String ipaddress) {
		this.ipaddress = ipaddress;
	}

    public java.lang.Integer getLevel1() {
        return this.level1;
    }

	public void setLevel1(java.lang.Integer level1) {
		this.level1 = level1;
	}



    public java.util.Calendar getRecordtime() {
        return this.recordtime;
    }

	public void setRecordtime(java.util.Calendar recordtime) {
		this.recordtime = recordtime;
	}
	
    public java.lang.String getType() {
        return this.type;
    }

	public void setType(java.lang.String type) {
		this.type = type;
	}

}
