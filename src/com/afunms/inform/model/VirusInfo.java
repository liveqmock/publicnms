package com.afunms.inform.model;

import com.afunms.common.base.BaseVo;

public class VirusInfo extends BaseVo
{
	private String ip;
	private String virusFile;
	private String virusName;
	private int numOfTimes;
	private int numOfVirus;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getNumOfTimes() {
		return numOfTimes;
	}
	public void setNumOfTimes(int numOfTimes) {
		this.numOfTimes = numOfTimes;
	}
	public int getNumOfVirus() {
		return numOfVirus;
	}
	public void setNumOfVirus(int numOfVirus) {
		this.numOfVirus = numOfVirus;
	}
	public String getVirusFile() {
		return virusFile;
	}
	public void setVirusFile(String virusFile) {
		this.virusFile = virusFile;
	}
	public String getVirusName() {
		return virusName;
	}
	public void setVirusName(String virusName) {
		this.virusName = virusName;
	}		
}
