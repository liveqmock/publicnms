package com.afunms.application.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

/**
 * @author hukelei 
 * @version 创建时间：2011-12-31
 * 类说明：
 */
public class Tracerts extends BaseVo {
	private int id;
	
	/**
	 * 节点类型
	 */
	private String nodetype;
	
	/**
	 * 节点配置ID
	 */
	private int configid;
	
	/**
	 * 执行时间
	 */
	private Calendar dotime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNodetype() {
		return nodetype;
	}

	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}

	public int getConfigid() {
		return configid;
	}

	public void setConfigid(int configid) {
		this.configid = configid;
	}

	public Calendar getDotime() {
		return dotime;
	}

	public void setDotime(Calendar dotime) {
		this.dotime = dotime;
	}

	
}
