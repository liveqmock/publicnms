package com.afunms.inform.model;

import java.util.HashMap;

public class SystemSnap 
{
	private String networkClass;
	private String serverClass;
	private String databaseClass;
	private String linksClass;
	private String virusClass;
	private String roomStatusClass;
	private String governClientClass;
	private String internetClass;
	private String oaStatusClass;
	private String doorSystemClass;
	public HashMap urlsTbl;
	
	public SystemSnap()
	{
		urlsTbl = new HashMap();
	}
	public String getDatabaseClass() {
		return databaseClass;
	}
	public void setDatabaseClass(String databaseClass) {
		this.databaseClass = databaseClass;
	}
	public String getDoorSystemClass() {
		return doorSystemClass;
	}
	public void setDoorSystemClass(String doorSystemClass) {
		this.doorSystemClass = doorSystemClass;
	}
	public String getGovernClientClass() {
		return governClientClass;
	}
	public void setGovernClientClass(String governClientClass) {
		this.governClientClass = governClientClass;
	}
	public String getInternetClass() {
		return internetClass;
	}
	public void setInternetClass(String internetClass) {
		this.internetClass = internetClass;
	}
	public String getLinksClass() {
		return linksClass;
	}
	public void setLinksClass(String linksClass) {
		this.linksClass = linksClass;
	}
	public String getNetworkClass() {
		return networkClass;
	}
	public void setNetworkClass(String networkClass) {
		this.networkClass = networkClass;
	}
	public String getOaStatusClass() {
		return oaStatusClass;
	}
	public void setOaStatusClass(String oaStatusClass) {
		this.oaStatusClass = oaStatusClass;
	}
	public String getRoomStatusClass() {
		return roomStatusClass;
	}
	public void setRoomStatusClass(String roomStatusClass) {
		this.roomStatusClass = roomStatusClass;
	}
	public String getServerClass() {
		return serverClass;
	}
	public void setServerClass(String serverClass) {
		this.serverClass = serverClass;
	}
	public String getVirusClass() {
		return virusClass;
	}
	public void setVirusClass(String virusClass) {
		this.virusClass = virusClass;
	}
}
