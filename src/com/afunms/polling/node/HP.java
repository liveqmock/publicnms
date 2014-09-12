package com.afunms.polling.node;

import java.util.List;

public class HP {
	private SystemInfo systemInfo;
	private ArrayInfo arrayInfo;
	private List<Enclosure> enclosures;
	private List<Controller> controllers;
	private List<Port> ports;
	private List<Disk> disks;
	private List<Lun> luns;
	private List<VFP> vfps;
	private SubSystemInfo subSystemInfo;
	
	public SystemInfo getSystemInfo() {
		return systemInfo;
	}
	public void setSystemInfo(SystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}
	public ArrayInfo getArrayInfo() {
		return arrayInfo;
	}
	public void setArrayInfo(ArrayInfo arrayInfo) {
		this.arrayInfo = arrayInfo;
	}
	public List<Enclosure> getEnclosures() {
		return enclosures;
	}
	public void setEnclosures(List<Enclosure> enclosures) {
		this.enclosures = enclosures;
	}
	public List<Controller> getControllers() {
		return controllers;
	}
	public void setControllers(List<Controller> controllers) {
		this.controllers = controllers;
	}
	public List<Port> getPorts() {
		return ports;
	}
	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}
	public List<Disk> getDisks() {
		return disks;
	}
	public void setDisks(List<Disk> disks) {
		this.disks = disks;
	}
	public List<Lun> getLuns() {
		return luns;
	}
	public void setLuns(List<Lun> luns) {
		this.luns = luns;
	}
	public List<VFP> getVfps() {
		return vfps;
	}
	public void setVfps(List<VFP> vfps) {
		this.vfps = vfps;
	}
	public SubSystemInfo getSubSystemInfo() {
		return subSystemInfo;
	}
	public void setSubSystemInfo(SubSystemInfo subSystemInfo) {
		this.subSystemInfo = subSystemInfo;
	}	
}