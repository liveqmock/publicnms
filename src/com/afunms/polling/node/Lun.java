package com.afunms.polling.node;

public class Lun {
	private String name;
	private String redundancyGroup;
	private String active;
	private String dataCapacity;
	private String wwn;
	private String numberOfBusinessCopies;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRedundancyGroup() {
		return redundancyGroup;
	}
	public void setRedundancyGroup(String redundancyGroup) {
		this.redundancyGroup = redundancyGroup;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getDataCapacity() {
		return dataCapacity;
	}
	public void setDataCapacity(String dataCapacity) {
		this.dataCapacity = dataCapacity;
	}
	public String getWwn() {
		return wwn;
	}
	public void setWwn(String wwn) {
		this.wwn = wwn;
	}
	public String getNumberOfBusinessCopies() {
		return numberOfBusinessCopies;
	}
	public void setNumberOfBusinessCopies(String numberOfBusinessCopies) {
		this.numberOfBusinessCopies = numberOfBusinessCopies;
	}
	
}
