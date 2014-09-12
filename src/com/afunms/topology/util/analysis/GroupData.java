package com.afunms.topology.util.analysis;

import java.util.List;

public class GroupData {
	private String name;
	private String flow;
	private String pDPRate;
	private String businessRate;
	private String averageRate;
	private String packetLossRate;
	private String retransmission;
	private List<IpData> IpDatas;
	private List<LACData> LACDatas;
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
	public List<IpData> getIpDatas() {
		return IpDatas;
	}
	public void setIpDatas(List<IpData> ipDatas) {
		IpDatas = ipDatas;
	}
	public List<LACData> getLACDatas() {
		return LACDatas;
	}
	public void setLACDatas(List<LACData> datas) {
		LACDatas = datas;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
