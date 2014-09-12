package com.afunms.system.vo;

import java.io.Serializable;

public class RouteVo implements Serializable{ 
	
	private String ifindex;
	
	private String dest;
	
	private String nexthop;
	
	private String routetype;
	
	private String routeproto;
	
	private String mask;
	
	private String collecttime;
	
	public String getCollecttime() {
		return collecttime;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public String getIfindex() {
		return ifindex;
	}

	public void setIfindex(String ifindex) {
		this.ifindex = ifindex;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getNexthop() {
		return nexthop;
	}

	public void setNexthop(String nexthop) {
		this.nexthop = nexthop;
	}

	public String getRouteproto() {
		return routeproto;
	}

	public void setRouteproto(String routeproto) {
		this.routeproto = routeproto;
	}

	public String getRoutetype() {
		return routetype;
	}

	public void setRoutetype(String routetype) {
		this.routetype = routetype;
	}

}