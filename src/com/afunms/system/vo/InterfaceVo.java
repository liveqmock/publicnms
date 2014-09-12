package com.afunms.system.vo;

import java.io.Serializable;

public class InterfaceVo implements Serializable{ 
	
	private String index;
	
	private String alias;
	
	private String app;
	
	private String kbs;
	
	private String statue;
	
	private String outPerc;
	
	private String inPerc;
	
	private String outs;
	
	private String ins;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getKbs() {
		return kbs;
	}

	public void setKbs(String kbs) {
		this.kbs = kbs;
	}

	public String getStatue() {
		return statue;
	}

	public void setStatue(String statue) {
		this.statue = statue;
	}

	public String getInPerc() {
		return inPerc;
	}

	public void setInPerc(String inPerc) {
		this.inPerc = inPerc;
	}

	public String getIns() {
		return ins;
	}

	public void setIns(String ins) {
		this.ins = ins;
	}

	public String getOuts() {
		return outs;
	}

	public void setOuts(String outs) {
		this.outs = outs;
	}

	public String getOutPerc() {
		return outPerc;
	}

	public void setOutPerc(String outPerc) {
		this.outPerc = outPerc;
	}


}