package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class AgentNode extends BaseVo {
	
	private int nodeid; 			//topo_host_node中ID
	private int agentid;			//nms_agent_config中ID
	private String ip_address;		//topo_host_node中IP地址
	private String alias;			//topo_host_node中系统名称
	
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public int getAgentid() {
		return agentid;
	}
	public void setAgentid(int d) {
		this.agentid = d;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
}
