package com.afunms.system.vo;

import com.afunms.common.base.BaseVo;

public class PortTypeVo extends BaseVo{
	private Integer id;
	/** nullable persistent field */
	private Integer typeid;
			

	/** identifier field */
	private String chname;

	/** nullable persistent field */
	private String bak;
	
	
	/** full constructor */
	public PortTypeVo(Integer typeid, String chname, String bak) {		
		this.typeid = typeid;
		this.chname = chname;
		this.bak=bak;
}

	public Integer getTypeid() {
		return typeid;
	}

	public void setTypeid(Integer typeid) {
		this.typeid = typeid;
	}

	/** default constructor */
	public PortTypeVo() {
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
	public void setChname(String string) {
		chname = string;
	}

    public Integer getId() {
        return this.id;
    }

	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
