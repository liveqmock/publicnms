/**
 * <p>Description:topo hepler</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.snmp4j.mp.SnmpConstants;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.IpAlias;
import com.afunms.config.model.Portconfig;
import com.afunms.discovery.IfEntity;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.loader.HostLoader;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.SubnetDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Subnet;





public class TopoHelper extends BaseManager implements ManagerInterface 
{
	private com.afunms.discovery.Host host;
	
	/**
	 * 增加一台主机
	 */
    public int addHost(String ipAddress,String alias,String community,String writecommunity,int category)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	List tmpList = null;
    	try{
    		tmpList = tmpDao.findByCondition("ip_address",ipAddress);
    	}catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   tmpDao.close();
	       }
    	if(tmpList.size()>0)
    	   return -1;     //IP已经存在	
    	if(NetworkUtil.ping(ipAddress)==0) 
	       return -2;     //ping不通 
	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
	       return -3;     //不支持snmp 
	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //以下把新增加的主机的数据加入数据库
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       
	       host.setCategory(category);
	       host.setCommunity(community);
	       host.setWritecommunity(writecommunity);
	       host.setAlias(alias);
	       host.setIpAddress(ipAddress);
	       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));
	       //host.setSysDescr(SnmpUtil.getInstance().getSysDescr(ipAddress,community));	 
	       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    
	       //host.setSysName(SnmpUtil.getInstance().getSysName(ipAddress,community));	 
	       host.setLocalNet(0);
	       host.setNetMask("255.255.255.0");
	       host.setDiscoverstatus(-1);
	       SnmpUtil snmp = SnmpUtil.getInstance();
	    	SysLogger.info("开始获取设备:"+host.getIpAddress()+"的系统名称");
	    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
	    	if(sysGroupProperty != null){
	    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
	        	//newNode.setsys((String)sysGroupProperty.get("sysUpTime"));
	    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
	    		host.setSysName((String)sysGroupProperty.get("sysName"));
	    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
	    	}
	       
	       SubnetDao netDao = new SubnetDao();
	       List netList = null;
	       try{
	    	   netList = netDao.loadAll(); //找出它属于哪个子网
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   netDao.close();
	       }
	       for(int i=0;i<netList.size();i++)
	       {
	       	  Subnet net = (Subnet)netList.get(i);
	    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
	    	  {
	    		  host.setLocalNet(net.getId());
	    		  host.setNetMask(net.getNetMask());
	    		  break;
	    	  }	
	       }
	       List hostList = new ArrayList(1);
	       hostList.add(host);	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
		   		   
		   result = id; //数据成功插入,返回ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //如果增加数据失败则不继续
	    HostNodeDao dao = new HostNodeDao();
	    try //以下把新数据载入内存(与PollingInitializtion.loadHost()差不多)
	    {	    		    		   
	    		
	    	HostNode vo = (HostNode)dao.findByID(String.valueOf(id));
	    	HostLoader loader = new HostLoader();
	    	loader.loadOne(vo);
	    	loader.close();
	    	SysLogger.info("成功增加一台主机,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }finally{
	    	dao.close();
	    }
	    return result;
    }
    
	/**
	 * 增加一台主机
	 */
    public int addHost(String ipAddress,String alias,String community,String writecommunity,int category,int ostype,int collecttype)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	List tmpList = tmpDao.findByCondition("ip_address",ipAddress);
    	if(tmpList.size()>0)
    	   return -1;     //IP已经存在
    	/*
    	 * 这里为了测试用,目前PING不通的设备也可以加进来
    	 */
    	//if(NetworkUtil.ping(ipAddress)==0) 
	       //return -2;     //ping不通
    	if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
    		//SNMP采集方式
    	   	   String snmpversion = "";
    	   	   snmpversion = ResourceCenter.getInstance().getSnmpversion();
    	   	   int default_version = 0;
    	   	   if(snmpversion.equals("v1")){
    	  			default_version = org.snmp4j.mp.SnmpConstants.version1;
    			  }else if(snmpversion.equals("v2")){
    				default_version = org.snmp4j.mp.SnmpConstants.version2c;
    			  }else if(snmpversion.equals("v1+v2")){
    				default_version = org.snmp4j.mp.SnmpConstants.version1;
    			  }else if(snmpversion.equals("v2+v1")){
    	  			default_version = org.snmp4j.mp.SnmpConstants.version2c;
    		   }
    		
    		
    	   	//SnmpUtil.getInstance().getSysOid(coreIp,community);
    	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
    		       return -3;     //不支持snmp 
    	}

	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //以下把新增加的主机的数据加入数据库
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       host.setCategory(category);
	       host.setOstype(ostype);
	       host.setCollecttype(collecttype);
	       if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
		       host.setCommunity(community);
		       host.setWritecommunity(writecommunity);
		       host.setAlias(alias);
		       host.setIpAddress(ipAddress);
		       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));	 
		       host.setMac(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    	 
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SnmpUtil snmp = SnmpUtil.getInstance();
		    	SysLogger.info("开始获取设备:"+host.getIpAddress()+"的系统名称");
		    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
		    	if(sysGroupProperty != null){
		    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
		    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
		    		host.setSysName((String)sysGroupProperty.get("sysName"));
		    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
		    	}
		       
		       SubnetDao netDao = new SubnetDao();
		       List netList = null;
		       try{
		    	   netList = netDao.loadAll(); //找出它属于哪个子网
		       }catch(Exception e){
		    	   e.printStackTrace();
		       }finally{
		    	   netDao.close();
		       }
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
		     //将设备端口加入到端口配置表里
		       try{
		    	   if(host.getIfEntityList() != null && host.getIfEntityList().size()>0){
		    		   List ifEntityList = host.getIfEntityList();
		    		   IfEntity ifEntity = new IfEntity(); 
		    		   Portconfig portconfig = new Portconfig();
		    		   List portlist = new ArrayList();
		    		   for(int i=0;i<ifEntityList.size();i++){
		    			   ifEntity = (IfEntity)ifEntityList.get(i);
		    			   portconfig = new Portconfig();
							portconfig.setBak("");
							portconfig.setIpaddress(host.getIpAddress());
							portconfig.setLinkuse("");
							portconfig.setName(ifEntity.getDescr());
							portconfig.setPortindex(new Integer(ifEntity.getIndex()));
							portconfig.setSms(new Integer(0));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
							portconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
							portconfig.setInportalarm("2000");//默认入口流速阀值
							portconfig.setOutportalarm("2000");//默认出口流速阀值
							portconfig.setSpeed("10000");
							portconfig.setAlarmlevel("1");
							portconfig.setFlag("1");
							portlist.add(portconfig);
		    		   }
		    		   PortconfigDao dao = new PortconfigDao();
						try{
							dao.save(portlist);
						}catch(Exception ex){
							ex.printStackTrace();
						}finally{
							dao.close();
						}
		    	   }
					
					
		       }catch(Exception e){
		    	   
		       }
	       }else{
	    	   //主机服务器
		       host.setAlias(alias);
		       //SysLogger.info(alias+"================");
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }
	       host.setSendemail("");
	       host.setSendmobiles("");
	       host.setSendphone("");
	       host.setBid("");
	       List hostList = new ArrayList(1);
	       hostList.add(host);
	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
		   		   
		   result = id; //数据成功插入,返回ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //如果增加数据失败则不继续
	    
	    HostNodeDao dao = new HostNodeDao();
	    try //以下把新数据载入内存(与PollingInitializtion.loadHost()差不多)
	    {	    		    		       		
	    	HostNode vo = (HostNode)dao.findByID(String.valueOf(id));
	    	HostLoader loader = new HostLoader();
	    	loader.loadOne(vo);
	    	loader.close();
	    	SysLogger.info("成功增加一台主机,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }finally{
	    	dao.close();
	    }
	    return result;
    }
    
    /**
     * @author nielin add for time-sharing
     * @since 2009-12-29
     * @param String ipAddress,String alias,String community,String writecommunity,int category,
     *        int ostype,int collecttype,String bid,String sendmobiles,String sendemail,String sendphone
     *        ip,,读共同体,写共同体,
	 * 增加一台主机
	 */
    public int addHost(String assetid,String location,String ipAddress,String alias,int snmpversion,String community,String writecommunity,int transfer,int category,int ostype,int collecttype,
    		String bid,String sendmobiles,String sendemail,String sendphone)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	//List tmpList = tmpDao.findByCondition1("ip_address",ipAddress);
    	List tmpList = tmpDao.findBynode("ip_address",ipAddress);
    	if(tmpList.size()>0)
    	   return -1;     //IP已经存在
    	/*
    	 * 这里为了测试用,目前PING不通的设备也可以加进来
    	 */
    	//if(NetworkUtil.ping(ipAddress)==0) 
	       //return -2;     //ping不通
    	if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
    		//SNMP采集方式
    	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
    		       return -3;     //不支持snmp 
    	}

	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //以下把新增加的主机的数据加入数据库
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       host.setCategory(category);
	       host.setOstype(ostype);
	       host.setCollecttype(collecttype);
	       host.setAssetid(assetid);
	       host.setLocation(location);
	       host.setTransfer(transfer);
	       if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
		       host.setCommunity(community);
		       host.setWritecommunity(writecommunity);
		       host.setAlias(alias);
		       host.setSnmpversion(snmpversion);
		       
		       host.setIpAddress(ipAddress);
		       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));	 
		       host.setMac(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    	 
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SnmpUtil snmp = SnmpUtil.getInstance();
		    	SysLogger.info("开始获取设备:"+host.getIpAddress()+"的系统名称");
		    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
		    	if(sysGroupProperty != null){
		    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
		    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
		    		host.setSysName((String)sysGroupProperty.get("sysName"));
		    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
		    	}
		       
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
		       //将设备端口加入到端口配置表里
		       try{
		    	   if(host.getIfEntityList() != null && host.getIfEntityList().size()>0){
		    		   List ifEntityList = host.getIfEntityList();
		    		   IfEntity ifEntity = new IfEntity(); 
		    		   Portconfig portconfig = new Portconfig();
		    		   List portlist = new ArrayList();
		    		   for(int i=0;i<ifEntityList.size();i++){
		    			   ifEntity = (IfEntity)ifEntityList.get(i);
		    			   portconfig = new Portconfig();
							portconfig.setBak("");
							portconfig.setIpaddress(host.getIpAddress());
							portconfig.setLinkuse("");
							portconfig.setName(ifEntity.getDescr());
							portconfig.setPortindex(new Integer(ifEntity.getIndex()));
							portconfig.setSms(new Integer(0));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
							portconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
							portconfig.setInportalarm("2000");//默认入口流速阀值
							portconfig.setOutportalarm("2000");//默认出口流速阀值
							portconfig.setSpeed("10000");
							portconfig.setAlarmlevel("1");
							portconfig.setFlag("1");
							portlist.add(portconfig);
		    		   }
		    		   PortconfigDao dao = new PortconfigDao();
						try{
							dao.save(portlist);
						}catch(Exception ex){
							ex.printStackTrace();
						}finally{
							dao.close();
						}
		    	   }
					
					
		       }catch(Exception e){
		    	   
		       }
		       
		       
	       }else if(collecttype == SystemConstant.COLLECTTYPE_SHELL || collecttype == SystemConstant.COLLECTTYPE_TELNET || collecttype == SystemConstant.COLLECTTYPE_SSH){
	    	   //主机服务器
		       host.setAlias(alias);
		       //SysLogger.info(alias+"================");
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       } else if (ostype == 15 ){
					host.setType("AS400 服务器");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_PING){
	    	   //主机服务器
		       host.setAlias(alias);
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       }else if(ostype==1){
		    	   //CISCO
		    	   host.setSysOid("1.3.6.1.4.1.9");
		    	   host.setType("Cisco");
		       }else if(ostype==2){
		    	   //H3C
		    	   host.setSysOid("1.3.6.1.4.1.2011");
		    	   host.setType("H3C");
		       }else if(ostype==3){
		    	   //Entrasys
		    	   host.setSysOid("1.3.6.1.4.1.9.2.1.57");
		    	   host.setType("Entrasys");
		       }else if(ostype==4){
		    	   //Radware
		    	   host.setSysOid("1.3.6.1.4.1.89");
		    	   host.setType("Radware");
		       }else if(ostype==10){
		    	   //MaiPu
		    	   host.setSysOid("1.3.6.1.4.1.5651");
		    	   host.setType("MaiPu");
		       }else if(ostype==11){
		    	   //RedGiant
		    	   host.setSysOid("1.3.6.1.4.1.4881");
		    	   host.setType("RedGiant");
		       } else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_REMOTEPING){
	    	   //远程PING的设备
	    	   //主机服务器
		       host.setAlias(alias);
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       }else if(ostype==1){
		    	   //CISCO
		    	   host.setSysOid("1.3.6.1.4.1.9");
		    	   host.setType("Cisco");
		       }else if(ostype==2){
		    	   //H3C
		    	   host.setSysOid("1.3.6.1.4.1.2011");
		    	   host.setType("H3C");
		       }else if(ostype==3){
		    	   //Entrasys
		    	   host.setSysOid("1.3.6.1.4.1.9.2.1.57");
		    	   host.setType("Entrasys");
		       }else if(ostype==4){
		    	   //Radware
		    	   host.setSysOid("1.3.6.1.4.1.89");
		    	   host.setType("Radware");
		       }else if(ostype==10){
		    	   //MaiPu
		    	   host.setSysOid("1.3.6.1.4.1.5651");
		    	   host.setType("MaiPu");
		       }else if(ostype==11){
		    	   //RedGiant
		    	   host.setSysOid("1.3.6.1.4.1.4881");
		    	   host.setType("RedGiant");
		       } else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       } 
	       }
	       host.setSendemail(sendemail);
	       host.setSendmobiles(sendmobiles);
	       host.setSendphone(sendphone);
	       host.setBid(bid);
	       List hostList = new ArrayList(1);
	       hostList.add(host);
	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
	       
	     //nielin add for as400 start
			
			if(ostype == 15 ){
				//as400服务器
				DiscoverCompleteDao dcDao2 = new DiscoverCompleteDao();
				
				dcDao2.createTableForAS400(host);
			}
			
			//nielin add for as400 end
			
		   result = id; //数据成功插入,返回ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //如果增加数据失败则不继续
	    
	    try //以下把新数据载入内存(与PollingInitializtion.loadHost()差不多)
	    {	    		    		   
	    	HostNodeDao dao = new HostNodeDao();	
	    	HostNode vo = null;
	    	try{
	    		vo = (HostNode)dao.findByID(String.valueOf(id));
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		dao.close();
	    	}
	    	HostLoader loader = new HostLoader();
	    	try{
	    		loader.loadOne(vo);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		loader.close();
	    	}
	    	
	    	SysLogger.info("成功增加一台主机,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }	    	    
	    return result;
    }
    
    /**
     * @author nielin add for time-sharing
     * @since 2009-12-29
     * @param String ipAddress,String alias,String community,String writecommunity,int category,
     *        int ostype,int collecttype,String bid,String sendmobiles,String sendemail,String sendphone
     *        ip,,读共同体,写共同体,
	 * 增加一台主机
	 */
    public int addHost(String assetid,String location,String ipAddress,String alias,int snmpversion,String community,String writecommunity,int transfer,int category,int ostype,int collecttype,
    		String bid,String sendmobiles,String sendemail,String sendphone,boolean ismanaged)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	//List tmpList = tmpDao.findByCondition1("ip_address",ipAddress);
    	List tmpList = tmpDao.findBynode("ip_address",ipAddress);
    	if(tmpList.size()>0)
    	   return -1;     //IP已经存在
    	/*
    	 * 这里为了测试用,目前PING不通的设备也可以加进来
    	 */
    	//if(NetworkUtil.ping(ipAddress)==0) 
	       //return -2;     //ping不通
    	if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
    		//SNMP采集方式
    	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
    		       return -3;     //不支持snmp 
    	}

	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //以下把新增加的主机的数据加入数据库
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       host.setCategory(category);
	       host.setOstype(ostype);
	       host.setCollecttype(collecttype);
	       host.setAssetid(assetid);
	       host.setLocation(location);
	       host.setTransfer(transfer);
	       host.setManaged(ismanaged);
	       if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
		       host.setCommunity(community);
		       host.setWritecommunity(writecommunity);
		       host.setAlias(alias);
		       host.setSnmpversion(snmpversion);
		       
		       host.setIpAddress(ipAddress);
		       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));	 
		       host.setMac(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    	 
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SnmpUtil snmp = SnmpUtil.getInstance();
		    	SysLogger.info("开始获取设备:"+host.getIpAddress()+"的系统名称");
		    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
		    	if(sysGroupProperty != null){
		    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
		    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
		    		host.setSysName((String)sysGroupProperty.get("sysName"));
		    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
		    	}
		       
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
		     //将设备端口加入到端口配置表里
		       try{
		    	   if(host.getIfEntityList() != null && host.getIfEntityList().size()>0){
		    		   List ifEntityList = host.getIfEntityList();
		    		   IfEntity ifEntity = new IfEntity(); 
		    		   Portconfig portconfig = new Portconfig();
		    		   List portlist = new ArrayList();
		    		   for(int i=0;i<ifEntityList.size();i++){
		    			   ifEntity = (IfEntity)ifEntityList.get(i);
		    			   portconfig = new Portconfig();
							portconfig.setBak("");
							portconfig.setIpaddress(host.getIpAddress());
							portconfig.setLinkuse("");
							portconfig.setName(ifEntity.getDescr());
							portconfig.setPortindex(new Integer(ifEntity.getIndex()));
							portconfig.setSms(new Integer(0));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
							portconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
							portconfig.setInportalarm("2000");//默认入口流速阀值
							portconfig.setOutportalarm("2000");//默认出口流速阀值
							portconfig.setSpeed("10000");
							portconfig.setAlarmlevel("1");
							portconfig.setFlag("1");
							portlist.add(portconfig);
		    		   }
		    		   PortconfigDao dao = new PortconfigDao();
						try{
							dao.save(portlist);
						}catch(Exception ex){
							ex.printStackTrace();
						}finally{
							dao.close();
						}
		    	   }
					
					
		       }catch(Exception e){
		    	   
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_SHELL || collecttype == SystemConstant.COLLECTTYPE_TELNET || collecttype == SystemConstant.COLLECTTYPE_SSH){
	    	   //主机服务器
		       host.setAlias(alias);
		       //SysLogger.info(alias+"================");
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       } else if (ostype == 15 ){
					host.setType("AS400 服务器");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_PING){
	    	   //主机服务器
		       host.setAlias(alias);
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       }else if(ostype==1){
		    	   //CISCO
		    	   host.setSysOid("1.3.6.1.4.1.9.");
		    	   host.setType("Cisco");
		       }else if(ostype==2){
		    	   //H3C
		    	   host.setSysOid("1.3.6.1.4.1.2011.");
		    	   host.setType("H3C");
		       }else if(ostype==3){
		    	   //Entrasys
		    	   host.setSysOid("1.3.6.1.4.1.9.2.1.57.");
		    	   host.setType("Entrasys");
		       }else if(ostype==4){
		    	   //Radware
		    	   host.setSysOid("1.3.6.1.4.1.89.");
		    	   host.setType("Radware");
		       }else if(ostype==10){
		    	   //MaiPu
		    	   host.setSysOid("1.3.6.1.4.1.5651.");
		    	   host.setType("MaiPu");
		       }else if(ostype==11){
		    	   //RedGiant
		    	   host.setSysOid("1.3.6.1.4.1.4881.");
		    	   host.setType("RedGiant");
		       } else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171.");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320.");
					host.setType("BDCom");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_REMOTEPING){
	    	   //远程PING的设备
	    	   //主机服务器
		       host.setAlias(alias);
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX 服务器");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX 服务器");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS 服务器");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX 服务器");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows 服务器");
		       }else if(ostype==1){
		    	   //CISCO
		    	   host.setSysOid("1.3.6.1.4.1.9.");
		    	   host.setType("Cisco");
		       }else if(ostype==2){
		    	   //H3C
		    	   host.setSysOid("1.3.6.1.4.1.2011.");
		    	   host.setType("H3C");
		       }else if(ostype==3){
		    	   //Entrasys
		    	   host.setSysOid("1.3.6.1.4.1.9.2.1.57.");
		    	   host.setType("Entrasys");
		       }else if(ostype==4){
		    	   //Radware
		    	   host.setSysOid("1.3.6.1.4.1.89.");
		    	   host.setType("Radware");
		       }else if(ostype==10){
		    	   //MaiPu
		    	   host.setSysOid("1.3.6.1.4.1.5651.");
		    	   host.setType("MaiPu");
		       }else if(ostype==11){
		    	   //RedGiant
		    	   host.setSysOid("1.3.6.1.4.1.4881.");
		    	   host.setType("RedGiant");
		       } else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171.");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320.");
					host.setType("BDCom");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //找出它属于哪个子网
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       } 
	       }
	       host.setSendemail(sendemail);
	       host.setSendmobiles(sendmobiles);
	       host.setSendphone(sendphone);
	       host.setBid(bid);
	       List hostList = new ArrayList(1);
	       hostList.add(host);
	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
	       
	     //nielin add for as400 start
			
			if(ostype == 15 ){
				//as400服务器
				DiscoverCompleteDao dcDao2 = new DiscoverCompleteDao();
				
				dcDao2.createTableForAS400(host);
			}
			
			//nielin add for as400 end
			
		   result = id; //数据成功插入,返回ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //如果增加数据失败则不继续
	    
	    try //以下把新数据载入内存(与PollingInitializtion.loadHost()差不多)
	    {	    		    		   
	    	HostNodeDao dao = new HostNodeDao();	
	    	HostNode vo = null;
	    	try{
	    		vo = (HostNode)dao.findByID(String.valueOf(id));
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		dao.close();
	    	}
	    	HostLoader loader = new HostLoader();
	    	try{
	    		loader.loadOne(vo);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		loader.close();
	    	}
	    	
	    	SysLogger.info("成功增加一台主机,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }	    	    
	    return result;
    }
    
	/**
	 * @author snow
	 * @param assetid
	 * @param location
	 * @param ipAddress
	 * @param alias
	 * @param community
	 * @param writecommunity
	 * @param category
	 * @param ostype
	 * @param collecttype
	 * @param bid
	 * @param sendmobiles
	 * @param sendemail
	 * @param sendphone
	 * @param supperid
	 * @return 
	 * @date 2010-05-18
	 */
	public int addHost(String assetid,String location,String ipAddress, String alias,int snmpversion, String community,
			String writecommunity,int transfer, int category, int ostype, int collecttype,
			String bid, String sendmobiles, String sendemail, String sendphone, int supperid, boolean managed,
			int securityLevel,String securityName,int v3_ap,String authPassPhrase,int v3_privacy,String privacyPassPhrase) {
//		SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&& add host"+ipAddress);
		
		//emc location待别的参数
		String locations = location;
		if(category == 14){
			locations = location.split(",")[0]; 
		}
		HostNodeDao tmpDao = new HostNodeDao();
		List tmpList = new ArrayList();
		try{
			tmpList = tmpDao.findBynode("ip_address", ipAddress);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			tmpDao.close();
		}
		String sysOID = "";
		if (tmpList.size() > 0)
			return -1; // IP已经存在
		
		//判断IP别名是否存在
        IpAliasDao ipaliasdao = new IpAliasDao();
        Hashtable ipaliasHash = new Hashtable();
        try {
            List aliasList = ipaliasdao.loadAll();
            if (aliasList != null && aliasList.size() > 0) {
                for (int k = 0; k < aliasList.size(); k++) {
                    IpAlias vo = (IpAlias) aliasList.get(k);
                    //SysLogger.info("#### put "+vo.getAliasip() +" to ipaliasHash ####");
                    ipaliasHash.put(vo.getAliasip(), vo.getIpaddress());
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            ipaliasdao.close();
        }
        if(ipaliasHash != null && ipaliasHash.size()>0){
        	if(ipaliasHash.containsKey(ipAddress))return -1;
        }
		
		/*
		 * 这里为了测试用,目前PING不通的设备也可以加进来
		 */
		// if(NetworkUtil.ping(ipAddress)==0)
		// return -2; //ping不通
		if (collecttype == SystemConstant.COLLECTTYPE_SNMP) {
			// SNMP采集方式
			if(snmpversion == 0 || snmpversion == 1 ){
				//SNMP V1 OR V2C
				sysOID = SnmpUtil.getInstance().getSysOid(ipAddress,community);
			}else if(snmpversion == 3){
				sysOID = SnmpUtil.getInstance().getSysOid(ipAddress,
						snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase);
			}
			if(sysOID == null || sysOID.trim().length()==0)
				return -3; // 不支持snmp
		}

		host = new com.afunms.discovery.Host();
		int result = 0;
		int id = 0;
		try // 以下把新增加的主机的数据加入数据库
		{
			id = KeyGenerator.getInstance().getNextKey();
			host.setId(id);
			host.setCategory(category);
			host.setOstype(ostype);
			host.setCollecttype(collecttype);
			host.setSupperid(supperid);
			host.setAssetid(assetid);
			host.setLocation(locations);
			host.setTransfer(transfer);
			host.setManaged(managed);
			//SNMP V3
			host.setSecuritylevel(securityLevel);
			host.setSecurityName(securityName);
			host.setV3_ap(v3_ap);
			host.setAuthpassphrase(authPassPhrase);
			host.setV3_privacy(v3_privacy);
			host.setPrivacyPassphrase(privacyPassPhrase);
			
			if (collecttype == SystemConstant.COLLECTTYPE_SNMP) {
				SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&33");
				host.setCommunity(community);
				host.setWritecommunity(writecommunity);
				host.setSnmpversion(snmpversion);
				host.setAlias(alias);
				host.setIpAddress(ipAddress);
				host.setSysOid(sysOID);				

				if(snmpversion == 0 || snmpversion == 1){
					//SNMP V1 OR V2C
					host.setMac(SnmpUtil.getInstance().getMacAddress(ipAddress,community));
					if(host.getCategory()==4){
						host.setBridgeAddress(SnmpUtil.getInstance().getHostBridgeAddress(
								ipAddress, community));
					}else{
							if(host.getCategory() == 17)
						{
							host.setBridgeAddress("");
						}else{
						host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(
							ipAddress, community));
						}					
						}
					if(host.getSysOid().startsWith("1.3.6.1.4.1.1588.2")){//博科的网络设备
						host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList_brocade(
								ipAddress, community, category));
					}else{
						host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(
								ipAddress, community, category));
					}
				}else if(snmpversion == 3){
					//SNMP V3
					host.setMac(SnmpUtils.getMacAddress(ipAddress,community,
							snmpversion,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,3, 1000*30));
					if(host.getCategory()==4){
						host.setBridgeAddress(SnmpUtils.getHostBridgeAddress(
								ipAddress,community,snmpversion,
								  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
								  3, 1000*30));
					}else{
						host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(
								ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase));
					}
					if(host.getSysOid().startsWith("1.3.6.1.4.1.1588.2")){//博科的网络设备
						host.setIfEntityList(SnmpUtils.getIfEntityList_brocade(
								ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,category));
					}else{
						host.setIfEntityList(SnmpUtils.getIfEntityList(
								ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,category));
					}
				}
				
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SnmpUtil snmp = SnmpUtil.getInstance();
				SysLogger.info("开始获取设备:" + host.getIpAddress() + "的系统名称");
				Hashtable sysGroupProperty = null;
				if(snmpversion == 0 || snmpversion == 1){
					//SNMP V1/V2C
					sysGroupProperty = snmp.getSysGroup(host.getIpAddress(), host.getCommunity());
				}else if(snmpversion == 3){
					//SNMP V3
					sysGroupProperty = snmp.getSysGroup(ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase);
				}
				
				if (sysGroupProperty != null) {
					host.setSysDescr((String) sysGroupProperty.get("sysDescr"));
					host.setSysContact((String) sysGroupProperty
							.get("sysContact"));
					host.setSysName((String) sysGroupProperty.get("sysName"));
					host.setSysLocation((String) sysGroupProperty
							.get("sysLocation"));
				}

				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
				//将设备端口加入到端口配置表里
			       try{
			    	   if(host.getIfEntityList() != null && host.getIfEntityList().size()>0){
			    		   List ifEntityList = host.getIfEntityList();
			    		   IfEntity ifEntity = new IfEntity(); 
			    		   Portconfig portconfig = new Portconfig();
			    		   List portlist = new ArrayList();
			    		   for(int i=0;i<ifEntityList.size();i++){
			    			   ifEntity = (IfEntity)ifEntityList.get(i);
			    			   portconfig = new Portconfig();
								portconfig.setBak("");
								portconfig.setIpaddress(host.getIpAddress());
								portconfig.setLinkuse("");
								portconfig.setName(ifEntity.getDescr());
								portconfig.setPortindex(new Integer(ifEntity.getIndex()));
								portconfig.setSms(new Integer(0));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
								portconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
								portconfig.setInportalarm("2000");//默认入口流速阀值
								portconfig.setOutportalarm("2000");//默认出口流速阀值
								portconfig.setSpeed("10000");
								portconfig.setAlarmlevel("1");
								portconfig.setFlag("1");
								portlist.add(portconfig);
			    		   }
			    		   PortconfigDao dao = new PortconfigDao();
							try{
								dao.save(portlist);
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{
								dao.close();
							}
			    	   }
						
						
			       }catch(Exception e){
			    	   
			       }
			} else if (collecttype == SystemConstant.COLLECTTYPE_SHELL) {
				// 主机服务器
				host.setAlias(alias);
				SysLogger.info(host.getIpAddress()+"=======ostype:"+ostype);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    }
		        else if (ostype == 44 ){
		        	//hp storage
			    	host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP Storage");
			    }
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
				
			} else if (collecttype == SystemConstant.COLLECTTYPE_WMI) {
				// 主机服务器
				host.setAlias(alias);
				// SysLogger.info(alias+"================");

				if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} 
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_TELNET || collecttype == SystemConstant.COLLECTTYPE_SSH) {
				// 主机服务器
				host.setAlias(alias);
				// SysLogger.info(alias+"================");
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    } else if (ostype == 44 ){
			    	host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP 存储");
			    }
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_PING || collecttype == SystemConstant.COLLECTTYPE_TELNETCONNECT 
					||collecttype == SystemConstant.COLLECTTYPE_SSHCONNECT || collecttype == SystemConstant.COLLECTTYPE_DATAINTERFACE) {
				//PING TELNET或SSH连通检测 或 数据接口采集方式
				host.setAlias(alias);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    } else if (ostype == 1) {
					// CISCO
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 2) {
					// H3C
					host.setSysOid("1.3.6.1.4.1.2011.");
					host.setType("H3C");
				} else if (ostype == 3) {
					// Entrasys
					host.setSysOid("1.3.6.1.4.1.9.2.1.57.");
					host.setType("Entrasys");
				} else if (ostype == 4) {
					// Radware
					host.setSysOid("1.3.6.1.4.1.89.");
					host.setType("Radware");
				} else if (ostype == 10) {
					// MaiPu
					host.setSysOid("1.3.6.1.4.1.5651.");
					host.setType("MaiPu");
				} else if (ostype == 11) {
					// RedGiant
					host.setSysOid("1.3.6.1.4.1.4881.");
					host.setType("RedGiant");
				} else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171.");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320.");
					host.setType("BDCom");
				} else if (ostype == 16) {
					// ZTE
					host.setSysOid("1.3.6.1.4.1.3902.");
					host.setType("ZTE");
				} else if (ostype == 17) {
					// ATM
					host.setSysOid("net_atm");
					host.setType("ATM");
				} else if (ostype == 34) {
					// CISCO CMTS
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 36) {
					// Motorola CMTS
					host.setSysOid("1.3.6.1.4.1.4981.");
					host.setType("Motorola");
				} else if (ostype == 18) {
					// Array Networks
					host.setSysOid("1.3.6.1.4.1.7564");
					host.setType("ArrayNetworks");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
				} else if (ostype == 35) {
					// 日立存储
					host.setSysOid("1.3.6.1.4.1.116.");
					host.setType("HDS存储");
				} else if (ostype == 40) {
					// VMWare
					host.setSysOid("1.3.6.1.4.1.6876.");
					host.setType("VMWare");
				} else if (ostype == 45) {
					// ALCATEL
					host.setSysOid("1.3.6.1.4.1.800.");
					host.setType("Alcatel");
				} else if (ostype == 46) {
					// Avaya
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("Avaya");
				} else if (ostype == 47) {
					// Juniper
					host.setSysOid("1.3.6.1.4.1.2636.");
					host.setType("Juniper");
					
				} else if (ostype == 28) {
					// Brocade
					host.setSysOid("1.3.6.1.4.1.1588.2.1.1.");
					host.setType("Brocade");
					
				}  else if (ostype == 41) {
					// EMC
					String subtype = location.split(",")[1];
//					System.out.println("-------------subtype---------------"+subtype);
				if(subtype != null){
					if(subtype.equalsIgnoreCase("EMC_VNX")){
						host.setSysOid("1.3.6.1.4.1.1981.1");
						host.setType("EMC");
					}else if(subtype.equalsIgnoreCase("EMC_DMX")){
						host.setSysOid("1.3.6.1.4.1.1981.2");
						host.setType("EMC");
					}else if(subtype.equalsIgnoreCase("EMC_VMAX")){
						host.setSysOid("1.3.6.1.4.1.1981.3");
						host.setType("EMC");
					}
				 }
				}
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_REMOTEPING) {
				// 远程PING的设备
				host.setAlias(alias);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    } else if (ostype == 1) {
					// CISCO
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 2) {
					// H3C
					host.setSysOid("1.3.6.1.4.1.2011.");
					host.setType("H3C");
				} else if (ostype == 3) {
					// Entrasys
					host.setSysOid("1.3.6.1.4.1.9.2.1.57");
					host.setType("Entrasys");
				} else if (ostype == 4) {
					// Radware
					host.setSysOid("1.3.6.1.4.1.89");
					host.setType("Radware");
				} else if (ostype == 10) {
					// MaiPu
					host.setSysOid("1.3.6.1.4.1.5651");
					host.setType("MaiPu");
				} else if (ostype == 11) {
					// RedGiant
					host.setSysOid("1.3.6.1.4.1.4881");
					host.setType("RedGiant");
				} else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
				} else if (ostype == 16) {
					// ZTE
					host.setSysOid("1.3.6.1.4.1.3902");
					host.setType("ZTE");
				} else if (ostype == 12){
					// Array Networks
					host.setSysOid("1.3.6.1.4.1.7564");
					host.setType("ArrayNetworks");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
				}
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			}
			host.setSendemail(sendemail);
			host.setSendmobiles(sendmobiles);
			host.setSendphone(sendphone);
			host.setBid(bid);
			List hostList = new ArrayList(1);
			hostList.add(host);
			SysLogger.info(host.getIpAddress()+"  -------typ:"+host.getType());
			DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
			try {
				dcDao.addHostDataByHand(hostList);
				dcDao.addInterfaceData(hostList);
				dcDao.addMonitor(hostList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dcDao.close();
			}

			//nielin add for as400 start
			
			if(ostype == 15 ){
				//as400服务器
				DiscoverCompleteDao dcDao2 = new DiscoverCompleteDao();
				
				try{
					dcDao2.createTableForAS400(host);
				}catch(Exception e){
					
				}finally{
					dcDao2.close();
				}
			}
			
			//nielin add for as400 end
			
			result = id; // 数据成功插入,返回ID
		} catch (Exception e) {
			SysLogger.error("TopoHelper.addHost(),insert db", e);
		}
		if (result == 0)
			return 0; // 如果增加数据失败则不继续

		try // 以下把新数据载入内存(与PollingInitializtion.loadHost()差不多)
		{
			HostNodeDao dao = new HostNodeDao();
			HostNode vo = null;
			try {
				vo = (HostNode) dao.findByID(String.valueOf(id));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			HostLoader loader = new HostLoader();
			try {
				loader.loadOne(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loader.close();
			}

			SysLogger.info("成功增加一台主机,id=" + id);
		} catch (Exception e) {
			SysLogger.error("TopoUtil.addHost(),insert memory", e);
			result = 0;
		}
		return result;
	}
	
	/**
	 * @author snow
	 * @param assetid
	 * @param location
	 * @param ipAddress
	 * @param alias
	 * @param community
	 * @param writecommunity
	 * @param category
	 * @param ostype
	 * @param collecttype
	 * @param bid
	 * @param sendmobiles
	 * @param sendemail
	 * @param sendphone
	 * @param supperid
	 * @return 
	 * @date 2010-05-18
	 */
	public int addHost(String assetid,String location,String ipAddress, String alias,int snmpversion, String community,
			String writecommunity,int transfer, int category, int ostype, int collecttype,
			String bid, String sendmobiles, String sendemail, String sendphone, int supperid, boolean managed,
			int securityLevel,String securityName,int v3_ap,String authPassPhrase,int v3_privacy,String privacyPassPhrase,int layer) {
//		SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&& add host"+ipAddress);
		
		//emc location待别的参数
		String locations = location;
		if(category == 14){
			locations = location.split(",")[0]; 
		}
		HostNodeDao tmpDao = new HostNodeDao();
		List tmpList = new ArrayList();
		try{
			tmpList = tmpDao.findBynode("ip_address", ipAddress);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			tmpDao.close();
		}
		String sysOID = "";
		if (tmpList.size() > 0)
			return -1; // IP已经存在
		
        IpAliasDao ipaliasdao = new IpAliasDao();
        Hashtable ipaliasHash = new Hashtable();
        try {
            List aliasList = ipaliasdao.loadAll();
            if (aliasList != null && aliasList.size() > 0) {
                for (int k = 0; k < aliasList.size(); k++) {
                    IpAlias vo = (IpAlias) aliasList.get(k);
                    //SysLogger.info("#### put "+vo.getAliasip() +" to ipaliasHash ####");
                    ipaliasHash.put(vo.getAliasip(), vo.getIpaddress());
                }
            }
        } catch (Exception e) {

        } finally {
            ipaliasdao.close();
        }
        if(ipaliasHash != null && ipaliasHash.size()>0){
        	if(ipaliasHash.containsKey(ipAddress))return -1;
        }
		
		
		/*
		 * 这里为了测试用,目前PING不通的设备也可以加进来
		 */
		// if(NetworkUtil.ping(ipAddress)==0)
		// return -2; //ping不通
		if (collecttype == SystemConstant.COLLECTTYPE_SNMP) {
			// SNMP采集方式
			if(snmpversion == 0 || snmpversion == 1 ){
				//SNMP V1 OR V2C
				sysOID = SnmpUtil.getInstance().getSysOid(ipAddress,community);
			}else if(snmpversion == 3){
				sysOID = SnmpUtil.getInstance().getSysOid(ipAddress,
						snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase);
			}
			if(sysOID == null || sysOID.trim().length()==0)
				return -3; // 不支持snmp
		}

		host = new com.afunms.discovery.Host();
		int result = 0;
		int id = 0;
		try // 以下把新增加的主机的数据加入数据库
		{
			id = KeyGenerator.getInstance().getNextKey();
			host.setId(id);
			host.setCategory(category);
			host.setOstype(ostype);
			host.setCollecttype(collecttype);
			host.setSupperid(supperid);
			host.setAssetid(assetid);
			host.setLocation(locations);
			host.setTransfer(transfer);
			host.setManaged(managed);
			//SNMP V3
			host.setSecuritylevel(securityLevel);
			host.setSecurityName(securityName);
			host.setV3_ap(v3_ap);
			host.setAuthpassphrase(authPassPhrase);
			host.setV3_privacy(v3_privacy);
			host.setPrivacyPassphrase(privacyPassPhrase);
			host.setLayer(layer);
			
			if (collecttype == SystemConstant.COLLECTTYPE_SNMP) {
				//SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&33");
				host.setCommunity(community);
				host.setWritecommunity(writecommunity);
				host.setSnmpversion(snmpversion);
				host.setAlias(alias);
				host.setIpAddress(ipAddress);
				host.setSysOid(sysOID);				

				if(snmpversion == 0 || snmpversion == 1){
					//SNMP V1 OR V2C
					host.setMac(SnmpUtil.getInstance().getMacAddress(ipAddress,community));
					if(host.getCategory()==4){
						host.setBridgeAddress(SnmpUtil.getInstance().getHostBridgeAddress(
								ipAddress, community));
					}else{
						host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(
							ipAddress, community));
					}
					if(host.getSysOid().startsWith("1.3.6.1.4.1.1588.2")){//博科的网络设备
						host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList_brocade(
								ipAddress, community, category));
					}else{
						host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(
								ipAddress, community, category));
					}
				}else if(snmpversion == 3){
					//SNMP V3
					host.setMac(SnmpUtils.getMacAddress(ipAddress,community,
							snmpversion,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,3, 1000*30));
					if(host.getCategory()==4){
						host.setBridgeAddress(SnmpUtils.getHostBridgeAddress(
								ipAddress,community,snmpversion,
								  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
								  3, 1000*30));
					}else{
						host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(
								ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase));
					}
					if(host.getSysOid().startsWith("1.3.6.1.4.1.1588.2")){//博科的网络设备
						host.setIfEntityList(SnmpUtils.getIfEntityList_brocade(
								ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,category));
					}else{
						host.setIfEntityList(SnmpUtils.getIfEntityList(
								ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,category));
					}
				}
				
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SnmpUtil snmp = SnmpUtil.getInstance();
				SysLogger.info("开始获取设备:" + host.getIpAddress() + "的系统名称");
				Hashtable sysGroupProperty = null;
				if(snmpversion == 0 || snmpversion == 1){
					//SNMP V1/V2C
					sysGroupProperty = snmp.getSysGroup(host.getIpAddress(), host.getCommunity());
				}else if(snmpversion == 3){
					//SNMP V3
					sysGroupProperty = snmp.getSysGroup(ipAddress,snmpversion,community,securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase);
				}
				
				if (sysGroupProperty != null) {
					host.setSysDescr((String) sysGroupProperty.get("sysDescr"));
					host.setSysContact((String) sysGroupProperty
							.get("sysContact"));
					host.setSysName((String) sysGroupProperty.get("sysName"));
					host.setSysLocation((String) sysGroupProperty
							.get("sysLocation"));
				}

				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
				//将设备端口加入到端口配置表里
			       try{
			    	   if(host.getIfEntityList() != null && host.getIfEntityList().size()>0){
			    		   List ifEntityList = host.getIfEntityList();
			    		   IfEntity ifEntity = new IfEntity(); 
			    		   Portconfig portconfig = new Portconfig();
			    		   List portlist = new ArrayList();
			    		   for(int i=0;i<ifEntityList.size();i++){
			    			   ifEntity = (IfEntity)ifEntityList.get(i);
			    			   portconfig = new Portconfig();
								portconfig.setBak("");
								portconfig.setIpaddress(host.getIpAddress());
								portconfig.setLinkuse("");
								portconfig.setName(ifEntity.getDescr());
								portconfig.setPortindex(new Integer(ifEntity.getIndex()));
								portconfig.setSms(new Integer(0));// 0：不发送短信 1：发送短信，默认的情况是不发送短信
								portconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
								portconfig.setInportalarm("2000");//默认入口流速阀值
								portconfig.setOutportalarm("2000");//默认出口流速阀值
								portconfig.setSpeed("10000");
								portconfig.setAlarmlevel("1");
								portconfig.setFlag("1");
								portlist.add(portconfig);
			    		   }
			    		   PortconfigDao dao = new PortconfigDao();
							try{
								dao.save(portlist);
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{
								dao.close();
							}
			    	   }
						
						
			       }catch(Exception e){
			    	   
			       }
			} else if (collecttype == SystemConstant.COLLECTTYPE_SHELL) {
				// 主机服务器
				host.setAlias(alias);
				SysLogger.info(host.getIpAddress()+"=======ostype:"+ostype);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    }
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
				
			} else if (collecttype == SystemConstant.COLLECTTYPE_WMI) {
				// 主机服务器
				host.setAlias(alias);
				// SysLogger.info(alias+"================");

				if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} 
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_TELNET || collecttype == SystemConstant.COLLECTTYPE_SSH) {
				// 主机服务器
				host.setAlias(alias);
				// SysLogger.info(alias+"================");
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    }
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_PING || collecttype == SystemConstant.COLLECTTYPE_TELNETCONNECT 
					||collecttype == SystemConstant.COLLECTTYPE_SSHCONNECT || collecttype == SystemConstant.COLLECTTYPE_DATAINTERFACE) {
				//PING TELNET或SSH连通检测 或 数据接口采集方式
				host.setAlias(alias);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    } else if (ostype == 1) {
					// CISCO
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 2) {
					// H3C
					host.setSysOid("1.3.6.1.4.1.2011.");
					host.setType("H3C");
				} else if (ostype == 3) {
					// Entrasys
					host.setSysOid("1.3.6.1.4.1.9.2.1.57.");
					host.setType("Entrasys");
				} else if (ostype == 4) {
					// Radware
					host.setSysOid("1.3.6.1.4.1.89.");
					host.setType("Radware");
				} else if (ostype == 10) {
					// MaiPu
					host.setSysOid("1.3.6.1.4.1.5651.");
					host.setType("MaiPu");
				} else if (ostype == 11) {
					// RedGiant
					host.setSysOid("1.3.6.1.4.1.4881.");
					host.setType("RedGiant");
				} else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171.");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320.");
					host.setType("BDCom");
				} else if (ostype == 16) {
					// ZTE
					host.setSysOid("1.3.6.1.4.1.3902.");
					host.setType("ZTE");
				} else if (ostype == 17) {
					// ATM
					host.setSysOid("net_atm");
					host.setType("ATM");
				} else if (ostype == 34) {
					// CISCO CMTS
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 36) {
					// Motorola CMTS
					host.setSysOid("1.3.6.1.4.1.4981.");
					host.setType("Motorola");
				} else if (ostype == 18) {
					// Array Networks
					host.setSysOid("1.3.6.1.4.1.7564");
					host.setType("ArrayNetworks");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
				} else if (ostype == 35) {
					// 日立存储
					host.setSysOid("1.3.6.1.4.1.116.");
					host.setType("HDS存储");
				} else if (ostype == 40) {
					// VMWare
					host.setSysOid("1.3.6.1.4.1.6876.");
					host.setType("VMWare");
				} else if (ostype == 45) {
					// ALCATEL
					host.setSysOid("1.3.6.1.4.1.800.");
					host.setType("Alcatel");
				} else if (ostype == 46) {
					// Avaya
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("Avaya");
				} else if (ostype == 47) {
					// Juniper
					host.setSysOid("1.3.6.1.4.1.2636.");
					host.setType("Juniper");
					
				} else if (ostype == 28) {
					// Brocade
					host.setSysOid("1.3.6.1.4.1.1588.2.1.1.");
					host.setType("Brocade");
					
				}  else if (ostype == 41) {
					// EMC
					String subtype = location.split(",")[1];
//					System.out.println("-------------subtype---------------"+subtype);
				if(subtype != null){
					if(subtype.equalsIgnoreCase("EMC_VNX")){
						host.setSysOid("1.3.6.1.4.1.1981.1");
						host.setType("EMC");
					}else if(subtype.equalsIgnoreCase("EMC_DMX")){
						host.setSysOid("1.3.6.1.4.1.1981.2");
						host.setType("EMC");
					}else if(subtype.equalsIgnoreCase("EMC_VMAX")){
						host.setSysOid("1.3.6.1.4.1.1981.3");
						host.setType("EMC");
					}
				 }
				}
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_REMOTEPING) {
				// 远程PING的设备
				host.setAlias(alias);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX 服务器");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX 服务器");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS 服务器");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX 服务器");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows 服务器");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 服务器");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE 服务器");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER 服务器");
			    } else if (ostype == 1) {
					// CISCO
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 2) {
					// H3C
					host.setSysOid("1.3.6.1.4.1.2011.");
					host.setType("H3C");
				} else if (ostype == 3) {
					// Entrasys
					host.setSysOid("1.3.6.1.4.1.9.2.1.57");
					host.setType("Entrasys");
				} else if (ostype == 4) {
					// Radware
					host.setSysOid("1.3.6.1.4.1.89");
					host.setType("Radware");
				} else if (ostype == 10) {
					// MaiPu
					host.setSysOid("1.3.6.1.4.1.5651");
					host.setType("MaiPu");
				} else if (ostype == 11) {
					// RedGiant
					host.setSysOid("1.3.6.1.4.1.4881");
					host.setType("RedGiant");
				} else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
				} else if (ostype == 16) {
					// ZTE
					host.setSysOid("1.3.6.1.4.1.3902");
					host.setType("ZTE");
				} else if (ostype == 12){
					// Array Networks
					host.setSysOid("1.3.6.1.4.1.7564");
					host.setType("ArrayNetworks");
				} else if (ostype == 30) {
					// HP
					host.setSysOid("1.3.6.1.4.1.11.2.3.7.11");
					host.setType("HP");
				}
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = new ArrayList();
				try{
					netList = netDao.loadAll(); // 找出它属于哪个子网
				}catch(Exception e){
					
				}finally{
					netDao.close();
				}
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			}
			host.setSendemail(sendemail);
			host.setSendmobiles(sendmobiles);
			host.setSendphone(sendphone);
			host.setBid(bid);
			List hostList = new ArrayList(1);
			hostList.add(host);
			//SysLogger.info(host.getIpAddress()+"  -------typ:"+host.getType());
			DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
			try {
				dcDao.addHostDataByHand(hostList);
				dcDao.addInterfaceData(hostList);
				dcDao.addMonitor(hostList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dcDao.close();
			}

			//nielin add for as400 start
			
			if(ostype == 15 ){
				//as400服务器
				DiscoverCompleteDao dcDao2 = new DiscoverCompleteDao();
				
				try{
					dcDao2.createTableForAS400(host);
				}catch(Exception e){
					
				}finally{
					dcDao2.close();
				}
			}
			
			//nielin add for as400 end
			
			result = id; // 数据成功插入,返回ID
		} catch (Exception e) {
			SysLogger.error("TopoHelper.addHost(),insert db", e);
		}
		if (result == 0)
			return 0; // 如果增加数据失败则不继续

		try // 以下把新数据载入内存(与PollingInitializtion.loadHost()差不多)
		{
			HostNodeDao dao = new HostNodeDao();
			HostNode vo = null;
			try {
				vo = (HostNode) dao.findByID(String.valueOf(id));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			HostLoader loader = new HostLoader();
			try {
				loader.loadOne(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loader.close();
			}

			SysLogger.info("成功增加一台主机,id=" + id);
		} catch (Exception e) {
			SysLogger.error("TopoUtil.addHost(),insert memory", e);
			result = 0;
		}
		return result;
	}
    
    /**
     * 返回增加的主机
     */
    public com.afunms.discovery.Host getHost()
    {
    	return host;
    }

	public String execute(String action) {
		// TODO Auto-generated method stub
		return null;
	}                 
}