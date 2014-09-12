package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNInfor  implements Serializable{
	
	private int id;
	 
	private String ipaddress;
	 
	private String type;
	 
    private String subtype;
	 
	private Calendar Collecttime;
	
	private String vpnId; 

	private int vpnTunnelsOpen;

	private int vpnTunnelsEst;

	private int vpnTunnelsRejected;

	private int vpnTunnelsTerminated;

	private long vpnBytesIn;

	private long vpnBytesOut;

	private long vpnUnauthPacketsIn;

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

	public String getVpnId() {
		return vpnId;
	}

	public void setVpnId(String vpnId) {
		this.vpnId = vpnId;
	}

	public int getVpnTunnelsOpen() {
		return vpnTunnelsOpen;
	}

	public void setVpnTunnelsOpen(int vpnTunnelsOpen) {
		this.vpnTunnelsOpen = vpnTunnelsOpen;
	}

	public int getVpnTunnelsEst() {
		return vpnTunnelsEst;
	}

	public void setVpnTunnelsEst(int vpnTunnelsEst) {
		this.vpnTunnelsEst = vpnTunnelsEst;
	}

	public int getVpnTunnelsRejected() {
		return vpnTunnelsRejected;
	}

	public void setVpnTunnelsRejected(int vpnTunnelsRejected) {
		this.vpnTunnelsRejected = vpnTunnelsRejected;
	}

	public int getVpnTunnelsTerminated() {
		return vpnTunnelsTerminated;
	}

	public void setVpnTunnelsTerminated(int vpnTunnelsTerminated) {
		this.vpnTunnelsTerminated = vpnTunnelsTerminated;
	}

	public long getVpnBytesIn() {
		return vpnBytesIn;
	}

	public void setVpnBytesIn(long vpnBytesIn) {
		this.vpnBytesIn = vpnBytesIn;
	}

	public long getVpnBytesOut() {
		return vpnBytesOut;
	}

	public void setVpnBytesOut(long vpnBytesOut) {
		this.vpnBytesOut = vpnBytesOut;
	}

	public long getVpnUnauthPacketsIn() {
		return vpnUnauthPacketsIn;
	}

	public void setVpnUnauthPacketsIn(long vpnUnauthPacketsIn) {
		this.vpnUnauthPacketsIn = vpnUnauthPacketsIn;
	}
}
