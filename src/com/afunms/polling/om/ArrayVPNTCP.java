package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNTCP implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;
	 
	private int ctcpActiveOpens;

	private long ctcpPassiveOpens;

	private int ctcpAttemptFails;

	private long ctcpEstabResets;

	private int ctcpCurrEstab;

	private long ctcpInSegs;

	private long ctcpOutSegs;

	private long ctcpRetransSegs;

	private int ctcpInErrs;

	private long ctcpOutRsts;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public int getCtcpActiveOpens() {
		return ctcpActiveOpens;
	}

	public void setCtcpActiveOpens(int ctcpActiveOpens) {
		this.ctcpActiveOpens = ctcpActiveOpens;
	}

	public long getCtcpPassiveOpens() {
		return ctcpPassiveOpens;
	}

	public void setCtcpPassiveOpens(long ctcpPassiveOpens) {
		this.ctcpPassiveOpens = ctcpPassiveOpens;
	}

	public int getCtcpAttemptFails() {
		return ctcpAttemptFails;
	}

	public void setCtcpAttemptFails(int ctcpAttemptFails) {
		this.ctcpAttemptFails = ctcpAttemptFails;
	}

	public long getCtcpEstabResets() {
		return ctcpEstabResets;
	}

	public void setCtcpEstabResets(long ctcpEstabResets) {
		this.ctcpEstabResets = ctcpEstabResets;
	}

	public int getCtcpCurrEstab() {
		return ctcpCurrEstab;
	}

	public void setCtcpCurrEstab(int ctcpCurrEstab) {
		this.ctcpCurrEstab = ctcpCurrEstab;
	}

	public long getCtcpInSegs() {
		return ctcpInSegs;
	}

	public void setCtcpInSegs(long ctcpInSegs) {
		this.ctcpInSegs = ctcpInSegs;
	}

	public long getCtcpOutSegs() {
		return ctcpOutSegs;
	}

	public void setCtcpOutSegs(long ctcpOutSegs) {
		this.ctcpOutSegs = ctcpOutSegs;
	}

	public long getCtcpRetransSegs() {
		return ctcpRetransSegs;
	}

	public void setCtcpRetransSegs(long ctcpRetransSegs) {
		this.ctcpRetransSegs = ctcpRetransSegs;
	}

	public int getCtcpInErrs() {
		return ctcpInErrs;
	}

	public void setCtcpInErrs(int ctcpInErrs) {
		this.ctcpInErrs = ctcpInErrs;
	}

	public long getCtcpOutRsts() {
		return ctcpOutRsts;
	}

	public void setCtcpOutRsts(long ctcpOutRsts) {
		this.ctcpOutRsts = ctcpOutRsts;
	}

	
}
