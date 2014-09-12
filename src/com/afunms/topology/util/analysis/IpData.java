package com.afunms.topology.util.analysis;

public class IpData {
	private String addr;
	private String flow;
	private String pDPRate;
	private String businessRate;
	private String averageRate;
	private String packetLossRate;
	private String retransmission;
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getAverageRate() {
		return averageRate;
	}
	public void setAverageRate(String averageRate) {
		this.averageRate = averageRate;
	}
	public String getBusinessRate() {
		return businessRate;
	}
	public void setBusinessRate(String businessRate) {
		this.businessRate = businessRate;
	}
	public String getFlow() {
		return flow;
	}
	public void setFlow(String flow) {
		this.flow = flow;
	}
	public String getPacketLossRate() {
		return packetLossRate;
	}
	public void setPacketLossRate(String packetLossRate) {
		this.packetLossRate = packetLossRate;
	}
	public String getPDPRate() {
		return pDPRate;
	}
	public void setPDPRate(String rate) {
		pDPRate = rate;
	}
	public String getRetransmission() {
		return retransmission;
	}
	public void setRetransmission(String retransmission) {
		this.retransmission = retransmission;
	}

}
