package com.afunms.topology.model;

import java.util.Hashtable;

import com.afunms.common.base.BaseVo;

public class MonitorNodeDTO extends BaseVo{
	private String hostnum;//物理机个数
	
	private String vMnum;//虚拟机个数
	
	private String Poolnum;//云资源
	
	private String num;//存储
	
	private int id; 
	
	private String ipAddress;
	
	private String alias;
	
	private String status;
	
	private String category;
	
	private String type;
	
	private String subtype;
	
	private String pingValue;  
	
	private String cpuValue;  
	
	private String memoryValue;
	
	private String virtualMemoryValue;
	private String cpuValueColor;  
	
	private String memoryValueColor;
	private String virtualMemoryValueColor;
	
	private String inutilhdxValue;
	
	private String oututilhdxValue;
	
	private String eventListCount;
	
	private String collectType;
	
	private Hashtable eventListSummary;
	
	private int entityNumber;//接口数量

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getEntityNumber() {
		return entityNumber;
	}

	public void setEntityNumber(int entityNumber) {
		this.entityNumber = entityNumber;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the pingValue
	 */
	public String getPingValue() {
		return pingValue;
	}

	/**
	 * @param pingValue the pingValue to set
	 */
	public void setPingValue(String pingValue) {
		this.pingValue = pingValue;
	}

	/**
	 * @return the cpuValue
	 */
	public String getCpuValue() {
		return cpuValue;
	}

	/**
	 * @param cpuValue the cpuValue to set
	 */
	public void setCpuValue(String cpuValue) {
		this.cpuValue = cpuValue;
	}

	/**
	 * @return the memoryValue
	 */
	public String getMemoryValue() {
		return memoryValue;
	}

	/**
	 * @param memoryValue the memoryValue to set
	 */
	public void setMemoryValue(String memoryValue) {
		this.memoryValue = memoryValue;
	}

	/**
	 * @return the cpuValueColor
	 */
	public String getCpuValueColor() {
		return cpuValueColor;
	}

	/**
	 * @param cpuValueColor the cpuValueColor to set
	 */
	public void setCpuValueColor(String cpuValueColor) {
		this.cpuValueColor = cpuValueColor;
	}

	/**
	 * @return the memoryValueColor
	 */
	public String getMemoryValueColor() {
		return memoryValueColor;
	}

	/**
	 * @param memoryValueColor the memoryValueColor to set
	 */
	public void setMemoryValueColor(String memoryValueColor) {
		this.memoryValueColor = memoryValueColor;
	}

	/**
	 * @return the inutilhdxValue
	 */
	public String getInutilhdxValue() {
		return inutilhdxValue;
	}

	/**
	 * @param inutilhdxValue the inutilhdxValue to set
	 */
	public void setInutilhdxValue(String inutilhdxValue) {
		this.inutilhdxValue = inutilhdxValue;
	}

	/**
	 * @return the oututilhdxValue
	 */
	public String getOututilhdxValue() {
		return oututilhdxValue;
	}

	/**
	 * @param oututilhdxValue the oututilhdxValue to set
	 */
	public void setOututilhdxValue(String oututilhdxValue) {
		this.oututilhdxValue = oututilhdxValue;
	}

	/**
	 * @return the eventListCount
	 */
	public String getEventListCount() {
		return eventListCount;
	}

	/**
	 * @param eventListCount the eventListCount to set
	 */
	public void setEventListCount(String eventListCount) {
		this.eventListCount = eventListCount;
	}

	/**
	 * @return the collectType
	 */
	public String getCollectType() {
		return collectType;
	}

	/**
	 * @param collectType the collectType to set
	 */
	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}

	/**
	 * @return the eventListSummary
	 */
	public Hashtable getEventListSummary() {
		return eventListSummary;
	}

	/**
	 * @param eventListSummary the eventListSummary to set
	 */
	public void setEventListSummary(Hashtable eventListSummary) {
		this.eventListSummary = eventListSummary;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHostnum() {
		return hostnum;
	}

	public void setHostnum(String hostnum) {
		this.hostnum = hostnum;
	}

	public String getVMnum() {
		return vMnum;
	}

	public void setVMnum(String mnum) {
		vMnum = mnum;
	}

	public String getPoolnum() {
		return Poolnum;
	}

	public void setPoolnum(String poolnum) {
		Poolnum = poolnum;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

    /**    
     * virtualMemoryValue    
     *    
     * @return  the virtualMemoryValue    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getVirtualMemoryValue() {
        return virtualMemoryValue;
    }

    /**    
     * @param virtualMemoryValue the virtualMemoryValue to set    
     */
    public void setVirtualMemoryValue(String virtualMemoryValue) {
        this.virtualMemoryValue = virtualMemoryValue;
    }

    /**    
     * virtualMemoryValueColor    
     *    
     * @return  the virtualMemoryValueColor    
     * @since   CodingExample Ver(编码范例查看) 1.0    
     */
    
    public String getVirtualMemoryValueColor() {
        return virtualMemoryValueColor;
    }

    /**    
     * @param virtualMemoryValueColor the virtualMemoryValueColor to set    
     */
    public void setVirtualMemoryValueColor(String virtualMemoryValueColor) {
        this.virtualMemoryValueColor = virtualMemoryValueColor;
    }

	
	

}
