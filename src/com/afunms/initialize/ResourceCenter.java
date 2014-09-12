/**
 * <p>Description:system resources center</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.initialize;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class ResourceCenter
{
    private Hashtable managerMap;
    private Hashtable ajaxManagerMap;
    private Hashtable menuMap;  // 菜单
    private Hashtable actionMap;
    private String sysPath;    //系统路径
    private String appServer;  //系统服务器
    private String jndi;       //数据库连接池JNDI
    private String snmpversion;       //snmp版本
    private Hashtable chartStorage;
    private Hashtable monitorMap;    
    private Set showMoidsSet; //需要在拓扑上显示的指标    
    private int perThreadNodes; //每个线程处理被监视对象的个数    
    private int maxThreads;    //最大线程数    
    private boolean logInfo;   //是否打印调试信息
    private boolean logError;  //是否打印错误信息
    private boolean startPolling;   //是否启动轮询
    private List serviceList;
    private int pollingThreadInterval; //线程运行时隔时间                
    private boolean hasDiscovered; //发现过了吗？ 
    private int ipIdleDays;     //IP空闲的天数
    private Hashtable alarmHashtable;//存放告警表
    private Hashtable cfgHash;
    private String dbtype;   //数据库类型
    
    private static ResourceCenter instance = new ResourceCenter();    
    
    //new add
    private Hashtable hardware;
    
    /**
     * 指标变化信息集合
     */
    private Hashtable indicatorsChangeInfoHash = new Hashtable();
    
	public Hashtable getHardware() {
		return hardware;
	}

	public void setHardware(Hashtable hardware) {
		this.hardware = hardware;
	}
        
	public Hashtable getIndicatorsChangeInfoHash() {
		return indicatorsChangeInfoHash;
	}

	public void setIndicatorsChangeInfoHash(Hashtable indicatorsChangeInfoHash) {
		this.indicatorsChangeInfoHash = indicatorsChangeInfoHash;
	}

	public static ResourceCenter getInstance()
    {
        return instance;
    }
   
    private ResourceCenter()
    {    
    	logInfo = true;
    	logError = true;
    	ipIdleDays = 30; //默认为30天
    	chartStorage = new Hashtable();
    	dbtype = "mysql";
    }
     
    
    
    public void setAlarmHashtable(Hashtable alarmHashtable) {
    	
    	this.alarmHashtable = alarmHashtable;
    } 
    public Hashtable getAlarmHashtable() {
    	return  alarmHashtable;
    }  
    
    public Hashtable getAjaxManagerMap() {
		return ajaxManagerMap;
	}

	public void setAjaxManagerMap(Hashtable ajaxManagerMap) {
		this.ajaxManagerMap = ajaxManagerMap;
	}
    	
    public String getAppServer() 
    {
		return appServer;
    }
	
	public void setAppServer(String appServer) 
	{
		this.appServer = appServer;
	}
	
	public String getJndi() 
	{
		return jndi;
	}
	
	public void setJndi(String jndi) 
	{
		this.jndi = jndi;
	}
	
	public String getSnmpversion() 
	{
		return snmpversion;
	}
	
	public void setSnmpversion(String snmpversion) 
	{
		this.snmpversion = snmpversion;
	}
	
	public boolean isLogError() 
	{
		return logError;
	}
	
	public void setLogError(boolean logError) 
	{
		this.logError = logError;
	}
	
	public boolean isLogInfo() 
	{
		return logInfo;
	}
	
	public void setLogInfo(boolean logInfo) 
	{
		this.logInfo = logInfo;
	}
	
	public String getSysPath() 
	{
		return sysPath;
	}
	
	public void setSysPath(String sysPath) 
	{
		this.sysPath = sysPath;
	}
      
    public void setActionMap(Hashtable actionMap)
    {
        this.actionMap = actionMap;
    }

    public Hashtable getActionMap()
    {
        return actionMap;
    }

    public void setManagerMap(Hashtable managerMap)
    {
        this.managerMap = managerMap;
    }

    public Hashtable getManagerMap()
    {
        return managerMap;
    }  
    
    public void setChartStorage(Hashtable chartStorage)
    {
    	this.chartStorage = chartStorage; 
    }
    
    public Hashtable getChartStorage()
    {
    	return chartStorage;
    }

	public Hashtable getMonitorMap() {
		return monitorMap;
	}

	public void setMonitorMap(Hashtable monitorMap) {
		this.monitorMap = monitorMap;
	}

	public static void setInstance(ResourceCenter instance) {
		ResourceCenter.instance = instance;
	}

	public int getPerThreadNodes() {
		return perThreadNodes;
	}

	public void setPerThreadNodes(int perThreadNodes) {
		this.perThreadNodes = perThreadNodes;
	}
	
	public void setShowMoidsSet(Set showMoidsSet)
	{
		this.showMoidsSet = showMoidsSet;
	}
	
	public Set getShowMoidsSet()
	{
		return showMoidsSet;
	}

	public boolean isStartPolling() 
	{
		return startPolling;
	}

	public void setStartPolling(boolean startPolling) 
	{
		this.startPolling = startPolling;
	}
	
	public List getServiceList() 
	{
		return serviceList;
	}
	
	public void setServiceList(List serviceList) 
	{
		this.serviceList = serviceList;
	}
	
	public int getPollingThreadInterval() 
	{
		return pollingThreadInterval;
	}

	public void setPollingThreadInterval(int pollingThreadInterval) 
	{
		this.pollingThreadInterval = pollingThreadInterval;
	}
	
	public int getMaxThreads() 
	{
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) 
	{
		this.maxThreads = maxThreads;
	}

	public boolean hasDiscovered() 
	{
		return hasDiscovered;
	}

	public void setHasDiscovered(boolean hasDiscovered) 
	{
		this.hasDiscovered = hasDiscovered;
	}

	public int getIpIdleDays() {
		return ipIdleDays;
	}

	public void setIpIdleDays(int ipIdleDays) {
		this.ipIdleDays = ipIdleDays;
	}
	/**
	 * @return the menuMap
	 */
	public Hashtable getMenuMap() {
		return menuMap;
	}

	/**
	 * @param menuMap the menuMap to set
	 */
	public void setMenuMap(Hashtable menuMap) {
		this.menuMap = menuMap;
	}
	public Hashtable getCfgHash() {
		return cfgHash;
	}

	public void setCfgHash(Hashtable cfgHash) {
		this.cfgHash = cfgHash;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
}
