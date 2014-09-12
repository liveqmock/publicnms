/**
 * <p>Description:node of topology,all devices are hosts</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

import java.util.*;

import com.afunms.common.util.*;

public class Host extends Node
{    
	private int hasDetected;
	private boolean isDiscovered;   
	private boolean linkAnalysed;	
	private int ipTotal;  
	private String alias;
	private String switchIds;
	private int completSwitchs;
	private boolean isRouter;
	private List<String> vlanCommunities;
	private String bridgeAddress;
	
	//SNMP V3
	private int securitylevel; //安全级别  1:noAuthNopriv 2:authNoPriv 3:authPriv
	private String securityName; //用户名
	private int v3_ap;           //认证协议  1:MD5 2:SHA
	private String authpassphrase; //通行码
	private int v3_privacy;			//加密协议 1:DES 2:AES128 3:AES196 4:AES256
	private String privacyPassphrase; //加密协议码
	
    private static int PER_THREAD_IP = DiscoverResource.getInstance().getPerThreadIps();
    
    public Host()
    {   	  	
    	setDiscovered(false);
        setNetMask("255.255.255.0");
        hasDetected = 0;
        ipTotal = -1;
        alias = null;
    }
    
    public void completeOneSwitch()
    {
    	completSwitchs ++;
    }
    
    public boolean allSwitchesFound()
    {
    	if(switchIds==null) return true;
    	String[] ids = switchIds.split(",");
    	if(completSwitchs==ids.length)
    	   return true;
    	else
    	   return false;	
    }
    
    public void setRouter(boolean is)
    {
        isRouter = is;	
    }
    
    public boolean isRouter()
    {
        return isRouter;	
    }
    
    public void addSwitchId(int id)
    {
        if(switchIds==null)
           switchIds = "" + id;
        else
           switchIds += "," + id;
        SysLogger.info(ipAddress + "加入一交换,id=" + id);
    }
    
    public String getSwitchIds()
    {
        return switchIds;	
    }
    
	public boolean isDiscovered() {
		return isDiscovered;
	}

	public void setDiscovered(boolean isDiscovered) {
		this.isDiscovered = isDiscovered;
	}
    
    public synchronized void updateCount(int detectType)
    {
       if(detectType==1)	 
	      doIPNetProbe();
       else
       	  hasDetected++;
       
	   if(hasDetected == ipTotal)
		  setDiscovered(true);
    }
    
    /**
     * 发现与该设备相连的所有设备
     */
    public void doDiscover()
    {
    	//增加CDP协议发现设备
    	if(this.category == 1 || this.category == 2 || this.category == 3)
    	{
    		if(this.sysOid.indexOf("1.3.6.1.4.1.9")>=0)
    			doCiscoCDPProbe();//用CDP发现网络设备
    	}
    		
    	
    	//增加STP协议发现设备
    
	  if(this.category==3){
		  //SysLogger.info("&&& 用ipnetmedial开始发现"+this.ipAddress);
	   	  doIPNetProbe();//二层交换机
	   	  //SysLogger.info("&&& 用ipnetmedial开始发现"+this.ipAddress);
	  }else{  
		  //SysLogger.info("&&& 用iprouter开始发现"+this.ipAddress);
   	      doIPRouterProbe(); //路由或路由交换机(三层交换机)
	  }
	   
    }
 
    /**
     * 对CDP进行探测
     */
    private void doCiscoCDPProbe()
    {
    	if(DiscoverEngine.getInstance().getStopStatus() == 1)return;
    	DiscoverEngine.getInstance().addThread(new CiscoCDPProbeThread(this));
    }
    /**
     * 对IP_Rouer_Table进行探测
     */
    private void doIPRouterProbe()
    {
    	if(DiscoverEngine.getInstance().getStopStatus() == 1)return;
    	DiscoverEngine.getInstance().addThread(new IPRouterProbeThread(this));
    	//DiscoverEngine.getInstance().addRunnable(new IPRouterProbeThread(this));
    }
   
    /**
     * 对IP_Net_To_Media进行探测
     */
    private void doIPNetProbe()
    {   
       if(DiscoverEngine.getInstance().getStopStatus() == 1)return;
       List ipNetTable = SnmpUtil.getInstance().getIpNetToMediaTable(this.getIpAddress(),this.getCommunity()); 

   	   if(ipNetTable==null||ipNetTable.size()==0)  //important  
   	   {
   	      setDiscovered(true);	
   	      return; 	
   	   }	
   	   ipTotal = ipNetTable.size();
   	   int threadTotal = 0; //线程总数
   	   SysLogger.info("PER_THREAD_IP==="+PER_THREAD_IP);
   	   if(PER_THREAD_IP == 0) PER_THREAD_IP = DiscoverResource.getInstance().getPerThreadIps();
   	   if(PER_THREAD_IP == 0)PER_THREAD_IP = 30;
       if(ipTotal % PER_THREAD_IP==0)  //每个线程对N个ip进行探测
          threadTotal = ipTotal / PER_THREAD_IP;
       else
          threadTotal = ipTotal / PER_THREAD_IP + 1;

       IPNetProbeThread probeThread = null;
       for(int i=0;i<threadTotal;i++)
       {
          if(i==threadTotal-1)         
             probeThread = new IPNetProbeThread(this,ipNetTable.subList(i * PER_THREAD_IP,ipTotal));
          else
             probeThread = new IPNetProbeThread(this,ipNetTable.subList(i * PER_THREAD_IP,(i+1) * PER_THREAD_IP));
          DiscoverEngine.getInstance().addThread(probeThread);
          //DiscoverEngine.getInstance().addRunnable(probeThread);
       }        	
    }   
    
    /**
     * 按接口索引找到接口
     */
    public IfEntity getIfEntityByIndex(String ifIndex)
    {    
    	if(ifEntityList==null||ifEntityList.size()==0)
    		return null;

    	IfEntity ifEntity = null;
    	for(int i=0;i<ifEntityList.size();i++)    	
    	{
    		IfEntity obj = (IfEntity)ifEntityList.get(i);
    		if(obj.getIndex().equals(ifIndex))
    		{
    			ifEntity = obj;
    			break;
    		}	
    	}	
        if(ifEntity == null)    	    	
		   SysLogger.info(ipAddress + "中没有索引为" + ifIndex + "的接口");
        
		return ifEntity;
    }
    
    /**
     * 按端口找到一个接口
     */
    public IfEntity getIfEntityByPort(String port)
    {
    	if(port==null) return null;    	
    	if(ifEntityList==null||ifEntityList.size()==0)
    		return null;	
    	
    	IfEntity ifEntity = null;
    	for(int i=0;i<ifEntityList.size();i++)    	
    	{
    		IfEntity obj = (IfEntity)ifEntityList.get(i);
    		if(obj.getPort().equals(port))
    		{
    			ifEntity = obj;
    			break;
    		}	
    	}
        if(ifEntity == null)    	    	
 		   SysLogger.info(ipAddress + "中没有端口为" + port + "的接口");    	
    	return ifEntity;
    } 
    
    /**
     * 按描述找到一个接口
     */
    public IfEntity getIfEntityByDesc(String desc)
    {
    	if("10.10.0.6".equalsIgnoreCase(ipAddress)||"10.10.0.38".equalsIgnoreCase(ipAddress)){
    		SysLogger.info(ipAddress+" ###------------ "+desc);
    	}
    		
    	if(desc==null) return null;    	
    	if(ifEntityList==null||ifEntityList.size()==0)
    		return null;	
    	
    	IfEntity ifEntity = null;
    	for(int i=0;i<ifEntityList.size();i++)    	
    	{
    		IfEntity obj = (IfEntity)ifEntityList.get(i);
    		if("10.10.0.6".equalsIgnoreCase(ipAddress)||"10.10.0.38".equalsIgnoreCase(ipAddress)){
        		SysLogger.info(ipAddress+" ### one: "+obj.getDescr()+"  two:"+desc);
        	}
    		if(obj.getDescr().equals(desc))
    		{
    			ifEntity = obj;
    			break;
    		}	
    	}
        if(ifEntity == null)    	    	
 		   SysLogger.info(ipAddress + "中没有端口描述为" + desc + "的接口");    	
    	return ifEntity;
    }
    /**
     * 按IP找到接口
     */
    public IfEntity getIfEntityByIP(String ip)
    {    
    	if(ifEntityList==null||ifEntityList.size()==0)
    		return null;	
    	
    	IfEntity ifEntity = null;
    	for(int i=0;i<ifEntityList.size();i++)    	
    	{
    		IfEntity obj = (IfEntity)ifEntityList.get(i);		
    		if(obj.getIpList()!=null)
    		{
    			if(obj.getIpList().split(",").length>0)
    			{
    				int flag = 0;
    				String IPS[] = obj.getIpList().split(",");
    				for(int k=0;k<IPS.length;k++)
    				{
    					//SysLogger.info(this.getIpAddress()+"含有接口地址"+IPS[k]+"===="+ip);
    					if(IPS[k].equalsIgnoreCase(ip))
    					{
    						ifEntity = obj;
    						flag = 1;
    		    			break;
    					}
    				}
    				if(flag == 1)break;
    			}
    			else
    			{
    				//SysLogger.info(this.getIpAddress()+"含有接口地址"+obj.getIpList()+"====="+ip);
    				if(obj.getIpList().equalsIgnoreCase(ip))
    				{
    					ifEntity = obj;
		    			break;
    				}
    			}
    		}
    	}
		return ifEntity;
    }
    
    /**
     * 找到该设备的管理地址
     */
    /*
    public String getAdminIp()
    {    
    	if(ifEntityList==null||ifEntityList.size()==0)
    		return null;

    	IfEntity ifEntity = null;
    	for(int i=0;i<ifEntityList.size();i++)    	
    	{
    		IfEntity obj = (IfEntity)ifEntityList.get(i);
    		if(obj.getType() == 24){
    			//为LOOPBACK地址
    			if(obj.getIpAddress().indexOf("127.0")<0){
    				ifEntity = obj;
    				return obj.getIpAddress();
    			}
    		}
    	}	
        if(ifEntity == null)    	    	
		   SysLogger.info(ipAddress + "对应的设备中没有管理地址");        
		return null;
    }
*/
	public String getAlias() 
	{
		return alias;
	}

	public void setAlias(String alias) 
	{
		this.alias = alias;
	}

	public boolean isLinkAnalysed() {
		return linkAnalysed;
	}

	public void setLinkAnalysed(boolean linkAnalysed) {
		this.linkAnalysed = linkAnalysed;
	}

	public void setVlanCommunities(List<String> vlanCommunities) {
		this.vlanCommunities = vlanCommunities;
	}

	public String getBridgeAddress() {
		return bridgeAddress;
	}

	public void setBridgeAddress(String bridgeAddress) {
		this.bridgeAddress = bridgeAddress;
	}

	public int getSecuritylevel() {
		return securitylevel;
	}

	public void setSecuritylevel(int securitylevel) {
		this.securitylevel = securitylevel;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public int getV3_ap() {
		return v3_ap;
	}

	public void setV3_ap(int v3_ap) {
		this.v3_ap = v3_ap;
	}

	public String getAuthpassphrase() {
		return authpassphrase;
	}

	public void setAuthpassphrase(String authpassphrase) {
		this.authpassphrase = authpassphrase;
	}

	public int getV3_privacy() {
		return v3_privacy;
	}

	public void setV3_privacy(int v3_privacy) {
		this.v3_privacy = v3_privacy;
	}

	public String getPrivacyPassphrase() {
		return privacyPassphrase;
	}

	public void setPrivacyPassphrase(String privacyPassphrase) {
		this.privacyPassphrase = privacyPassphrase;
	} 

}
