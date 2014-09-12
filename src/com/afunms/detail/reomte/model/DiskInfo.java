package com.afunms.detail.reomte.model;

public class DiskInfo {
	
	/**
	 * 磁盘名称(索引)
	 */
	private String sindex;
	
	/**
	 * 总大小(容量)
	 */
	private String allSize;
	
	/**
	 * 已使用大小
	 */
	private String usedSize;
	
	/**
	 * 利用率
	 */
	private String utilization;
	
	/**
	 * 增长率
	 */
	private String utilizationInc;
	
	/**
	 * 已使用大小的单位 G , M ,....
	 */
	private String allSizeUnit;
	
	
	/**
	 * 总大小(容量)的单位 G , M ,....
	 */
	private String usedSizeUnit;
	
	/**
	 * 利用率的单位 %
	 */
	private String utilizationUnit;


	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}


	/**
	 * @param sindex the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}


	/**
	 * @return the allSize
	 */
	public String getAllSize() {
		return allSize;
	}


	/**
	 * @param allSize the allSize to set
	 */
	public void setAllSize(String allSize) {
		this.allSize = allSize;
	}


	/**
	 * @return the usedSize
	 */
	public String getUsedSize() {
		return usedSize;
	}


	/**
	 * @param usedSize the usedSize to set
	 */
	public void setUsedSize(String usedSize) {
		this.usedSize = usedSize;
	}


	/**
	 * @return the utilization
	 */
	public String getUtilization() {
		return utilization;
	}


	/**
	 * @param utilization the utilization to set
	 */
	public void setUtilization(String utilization) {
		this.utilization = utilization;
	}


	/**
	 * @return the utilizationInc
	 */
	public String getUtilizationInc() {
		return utilizationInc;
	}


	/**
	 * @param utilizationInc the utilizationInc to set
	 */
	public void setUtilizationInc(String utilizationInc) {
		this.utilizationInc = utilizationInc;
	}


	/**
	 * @return the allSizeUnit
	 */
	public String getAllSizeUnit() {
		return allSizeUnit;
	}


	/**
	 * @param allSizeUnit the allSizeUnit to set
	 */
	public void setAllSizeUnit(String allSizeUnit) {
		this.allSizeUnit = allSizeUnit;
	}


	/**
	 * @return the usedSizeUnit
	 */
	public String getUsedSizeUnit() {
		return usedSizeUnit;
	}


	/**
	 * @param usedSizeUnit the usedSizeUnit to set
	 */
	public void setUsedSizeUnit(String usedSizeUnit) {
		this.usedSizeUnit = usedSizeUnit;
	}


	/**
	 * @return the utilizationUnit
	 */
	public String getUtilizationUnit() {
		return utilizationUnit;
	}


	/**
	 * @param utilizationUnit the utilizationUnit to set
	 */
	public void setUtilizationUnit(String utilizationUnit) {
		this.utilizationUnit = utilizationUnit;
	}
	
	
	
	
	
}
