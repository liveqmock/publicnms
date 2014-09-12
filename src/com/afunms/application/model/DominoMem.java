/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoMem extends BaseVo{
	private String memAllocate = "";//内存总量 wxy edit
	private String memAllocateProcess = "";//进程使用的内存
	private String memAllocateShare = "";//共享内存
	private String memPhysical = "";//物理内存
	private String memFree = "";//空闲物理内存
	private String platformMemPhyPctUtil = "";//平台物理内存利用率
	private String platformMemPhysical = "";//平台物理内存大小
	private String mempctutil = "";//物理内存使用率 wxy edit
	
	
	public DominoMem(){
		memAllocate = "";//已使用内存
		memAllocateProcess = "";//进程使用的内存
		memAllocateShare = "";//共享内存
		memPhysical = "";//物理内存		
		memFree = "";
		mempctutil= "0";
	}
	public String getMemAllocate() {
		return memAllocate;
	}
	public void setMemAllocate(String memAllocate) {
		this.memAllocate = memAllocate;
	}
	public String getMemAllocateProcess() {
		return memAllocateProcess;
	}
	public void setMemAllocateProcess(String memAllocateProcess) {
		this.memAllocateProcess = memAllocateProcess;
	}
	public String getMemAllocateShare() {
		return memAllocateShare;
	}
	public void setMemAllocateShare(String memAllocateShare) {
		this.memAllocateShare = memAllocateShare;
	}
	public String getMemPhysical() {
		return memPhysical;
	}
	public void setMemPhysical(String memPhysical) {
		this.memPhysical = memPhysical;
	}
	public String getMemFree() {
		return memFree;
	}
	public void setMemFree(String memFree) {
		this.memFree = memFree;
	}
	public String getPlatformMemPhyPctUtil() {
		return platformMemPhyPctUtil;
	}
	public void setPlatformMemPhyPctUtil(String platformMemPhyPctUtil) {
		this.platformMemPhyPctUtil = platformMemPhyPctUtil;
	}
	public String getPlatformMemPhysical() {
		return platformMemPhysical;
	}
	public void setPlatformMemPhysical(String platformMemPhysical) {
		this.platformMemPhysical = platformMemPhysical;
	}
	public String getMempctutil() {
		return mempctutil;
	}
	public void setMempctutil(String mempctutil) {
		this.mempctutil = mempctutil;
	}

}