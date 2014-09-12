package com.afunms.application.model;

import java.util.Calendar;

import com.afunms.common.base.BaseVo;

/**
 * @author hukelei 
 * @version 创建时间：2011-12-31
 * 类说明：
 */
public class TracertsDetail extends BaseVo{
	private int id;
	
	/**
	 * 节点类型
	 */
	private String nodetype;
	
	/**
	 * TRACERTSID
	 */
	private int tracertsid;
	
	/**
	 * 执行时间
	 */
	private String details;
	private int configid;

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

	public int getTracertsid() {
		return tracertsid;
	}

	public void setTracertsid(int tracertsid) {
		this.tracertsid = tracertsid;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public int getConfigid() {
		return configid;
	}

	public void setConfigid(int configid) {
		this.configid = configid;
	}

	
}
