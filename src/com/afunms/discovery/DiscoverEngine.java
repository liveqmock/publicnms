/**
 * <p>Description:discovery engine</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.discovery;

import java.util.*;
import java.util.concurrent.*;

import com.afunms.common.util.*;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.model.HostNode;
import com.afunms.common.util.SysLogger;

public class DiscoverEngine
{
	private static List subNetList;	
	private static List hostList;
	private static List faildIpList;//发现失败的设备IP，目前情况是第一次发现失败后，之后就不在发现该设备
	private static List existIpList; //已经加入设备的IP	
	private static List linkList;
	private static List routelinkList;
	private static List maclinkList;
	private static List threadList;
	private static List futureList;
	private static List formerNodeList;//存储以前已经发现的设备
	private static List formerNodeLinkList;//存储之前发现或添加的连接关系
	private static int discovermodel;
	private static String writecommunity;
	private static int snmpversion;
	private static String discover_bid;
	private static int stopStatus;
	
	public static int threads;
	public static int discoverdcount;
	private static SnmpUtil snmp = SnmpUtil.getInstance();	
	private static final int MAX_THREADS = DiscoverResource.getInstance().getMaxThreads(); //最大线程数,根据tomcat设置   
	private static ExecutorService threadExecutor = Executors.newFixedThreadPool(MAX_THREADS);	
	private static ExecutorService runnableExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private static DiscoverEngine instance = new DiscoverEngine();
	// 生成线程池
//	private static ThreadPool threadPool = new ThreadPool(200);
    
	public synchronized int getDiscoverdcount(){
		return this.discoverdcount;
	}
	public synchronized void addDiscoverdcount(){
		discoverdcount++;
	}
	
	public synchronized void setDiscoverdcount(int count){
		this.discoverdcount = count ;
	}
	
	public static DiscoverEngine getInstance()
	{	
		//SysLogger.info("#############################");
		try{
		if(instance == null){
			instance = new DiscoverEngine();//增加重新发现,需要重新初始化该类
			//threadExecutor.shutdownNow();
			//threadExecutor = null;
			
			//threadPool = new ThreadPool(200);

			threadExecutor = Executors.newFixedThreadPool(MAX_THREADS);	
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}
		if(threadExecutor == null)threadExecutor = Executors.newFixedThreadPool(MAX_THREADS);	
        return instance;       
    }
   
	public void unload() 
	{		
		//threadExecutor.shutdownNow();
//		threadPool.interrupt();
//		threadPool.close();
		/*
		if (!threadExecutor.isShutdown()) {

			List<Runnable> threads = threadExecutor.shutdownNow();

			for (int i = 0; i < threads.size(); ++i) {
				try {
					((Runnable)threads.get(i));
				} catch (Exception e) {

				}
			}
		}
		*/
		instance = null;
		threadList = null;
		subNetList = null;
		hostList = null;
		faildIpList = null;
		existIpList = null;
		linkList= null;
		routelinkList = null;
		maclinkList = null;
		formerNodeList = null;
		formerNodeLinkList= null;
		threadExecutor = null;
		futureList = null;
		threads = 0;
		discoverdcount = 0;
//		threadPool = null;
		SysLogger.info("DiscoverEngine.unload()");
	}

    private DiscoverEngine()
    {        	
    	subNetList = new ArrayList(100);
    	hostList = new ArrayList(50);
    	existIpList = new ArrayList(200);
        linkList = new ArrayList(50);
        routelinkList = new ArrayList(50);
        maclinkList = new ArrayList(50);
        threadList = new ArrayList(50);
        faildIpList = new ArrayList(50);
        futureList = new ArrayList(200);
        formerNodeList = new ArrayList(200);
        formerNodeLinkList = new ArrayList(200);
        threadExecutor = Executors.newFixedThreadPool(MAX_THREADS);	
//        threadPool = new ThreadPool(200);
		threads = 0;
		discoverdcount = 0;
    }
    
    public synchronized void addThread(Thread newThread)  
    {
    	threadExecutor.execute(newThread);  
    	threadList.add(newThread);    	
    	newThread.setName("afunms[" + threads + "]");
    	threads++;
    }
    
    public synchronized void addRunnable(Runnable newRunnable)  
    {
    	runnableExecutor.execute(newRunnable);  
    	//threadList.add(newRunnable);    	
    	//newRunnable.setName("afunms[" + threads + "]");
    	//threads++;
    }
    /*
    public synchronized void addThread(Runnable newThread)  
    {
    	threadPool.runTask(newThread);
    	//threadPool.join();
    	//threadExecutor.execute(newThread);  
    	//futureList.add(future);
    	//threadList.add(newThread);    	
    	//newThread.setName("afunms[" + threads + "]");
    	threads++;
    }
    */
         
    /**
     * 把设备类型已经确定的设备加入其中,deviceType=1,2,3,4,5,7,8 
     */
    public void addHost(Host newNode,Link link)
    {
    	/**
    	 * 
    	 * <select size=1 name='type' style='width:100px;' onchange="unionSelect();">
            								<option value='1' selected>路由器</option>
            								<option value='2'>路由交换机</option>
            								<option value='3'>交换机</option>
            								<option value='4'>服务器</option>
            								<option value='8'>防火墙</option>
            							</select>
    	 */
    	int flag = 0;
    	if(newNode.getSysOid() == null)
    	{
    		newNode.setDiscovered(true);//感觉这里设置为true，并不代表设备已经被发现了，而是为了设置这样一个状态，使后面部分代码不执行。
    		return;
    	}
    	//这里需要添加从已经存在的设备列表中获取设备,这样可以避免重复取设备数据
    	for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&&&&******"+newNode.getIpAddress()+"----BRIDGE  "+newNode.getBridgeAddress()+"======="+tmpNode.getIpAddress()+"===BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(newNode.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+newNode.getCategory()+"的设备"+newNode.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    		if(tmpNode.getIpAddress().equalsIgnoreCase(newNode.getIpAddress()))//查看是否是存在指定IP的网络设备
    		{
    			newNode = tmpNode;
    			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
    			flag = 1;
    			break;
    		}
    		else
    		{
    			List aliasIPS = tmpNode.getAliasIPs();
    			if(aliasIPS == null)continue;
    			if(aliasIPS.contains(newNode.getIpAddress()))//查看该IP是否是网络设备众多网口的其中一个网口，如果是，说明该IP所代表的网络设备已经被发现过。
    			{
        			newNode = tmpNode;
        			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
        			flag = 1;
        			break;
    			}
    		}
    	}
    	if(flag == 1)//表明设备已经被发现过一次
    	{
    		newNode.setDiscovered(true);
    		return;
    	}
    	
    	
    	SysLogger.info("开始分析类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress());    	
    	//路由交换或二层交换,并且增加了防火墙
    	if(newNode.getCategory()==2 || newNode.getCategory()==3 || newNode.getCategory()==7 || newNode.getCategory()==8)
    	{
    		newNode.setBridgeAddress(snmp.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity()));//设置该IP对应的mac地址
//    		try{
//    			newNode.setBridgeAddress(SnmpUtils.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity(),3,5000));//设置该IP对应的mac地址
//    		}catch(Exception e){
//    			
//    		}
    		SysLogger.info("&&&&&&&&&&&&&&&&&&");
    		SysLogger.info(newNode.getIpAddress()+" "+newNode.getBridgeAddress());
    		SysLogger.info("&&&&&&&&&&&&&&&&&&");
       	 	//得到STP的桥数据(STP只对二层交换起作用)
    		if(newNode.getCategory()!=7)
    		{
    			if(newNode.getBridgestpList() == null || newNode.getBridgestpList().size()==0)
    				//snmp.getBridgeStpList(newNode.getIpAddress(),newNode.getCommunity())获得的是com.afunms.discovery.BridgeStpInterface类型的List
        			//该List的size与getIfEntityList返回的List的size大小可以不一样，但貌似这两个变量的物理意义是有关联的。
    				newNode.setBridgestpList(snmp.getBridgeStpList(newNode.getIpAddress(),newNode.getCommunity()));
    				//newNode.setBridgestpList(SnmpUtils.getBridgeStpList(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
    		}
    	}
    	//获取该设备的接口地址
        if(newNode.getCategory() <5 || newNode.getCategory()==7)
        {
        	//对于路由和服务器 	
        	SysLogger.info("开始获取设备:"+newNode.getIpAddress()+"的接口表");  
        	if(newNode.getIfEntityList() == null)
        		//snmp.getIfEntityList(newNode.getIpAddress(),newNode.getCommunity(),newNode.getCategory())返回com.afunms.discovery.IfEntity类型的List，该IfEntity既有ethernet这样的是端口信息，也有vlan虚端口信息
        		newNode.setIfEntityList(snmp.getIfEntityList(newNode.getIpAddress(),newNode.getCommunity(),newNode.getCategory()));
        		//newNode.setIfEntityList(SnmpUtils.getIfEntityList(newNode.getIpAddress(),newNode.getCommunity(),3,5000,newNode.getCategory()));
        }

//        //获取该设备的管理地址
//        if(newNode.getAdminIp() != null){
//        	newNode.setIpAddress(newNode.getAdminIp());
//        	newNode.setAdminIp(newNode.getAdminIp());
//        	//link.setStartIp(newNode.getIpAddress());
//        }
        
        if(newNode.getSysOid() != null )
        {
            if(newNode.getSysOid().indexOf("1.3.6.1.4.1.25506")>=0 &&(newNode.getCategory()==2 || newNode.getCategory()==3))
            {
            	//H3C的交换设备
            	if(newNode.getNdpHash() == null)
            		//返回的hashtable描述： key 保存mac地址，value 保存mac地址对应的端口的名字，如ethernet1/1/1
            		newNode.setNdpHash(snmp.getNDPTable(newNode.getIpAddress(),newNode.getCommunity()));
            		//newNode.setNdpHash(SnmpUtils.getNDPTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
            }
        }
        
    	//若一个设备已经存在,是否可以分析第二次????
        SysLogger.info("开始判断类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+"=======是否已经存在"); 
        
       
        for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&&&&@@@@@"+newNode.getIpAddress()+"----BRIDGE  "+newNode.getBridgeAddress()+"======="+tmpNode.getIpAddress()+"===BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(newNode.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+newNode.getCategory()+"的设备"+newNode.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    	}
    	if(flag == 1)//表明设备已经被发现过一次
    	{
    		newNode.setDiscovered(true);
    		return;
    	}
        
        
        
        
        
        if(link != null)
        	SysLogger.info(newNode.getIpAddress()+" "+newNode.getAdminIp()+"   link:startip: "+link.getStartIp()+" endip:"+link.getEndIp());
    	//该方法虽然传入link对象，但却未使用到该对象，此方法只是检查设备列表里是否已经加入了newNode节点，通过比较设备IP地址情况
//        if(isHostExist(newNode))
//        {
//    		SysLogger.info("结束判断类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+"是否已经存在,当前为已经存在并返回");
//    		return;
//    	}
    	SysLogger.info("结束判断类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+"======是否已经存在");
    	
    	newNode.setId(KeyGenerator.getInstance().getHostKey());
    	//若是网络设备
    	if(newNode.getCategory()==1 || newNode.getCategory()==2 || newNode.getCategory()==3 || newNode.getCategory()==7 || newNode.getCategory()==8)
    	{
    		if(newNode.getIpNetTable() == null || newNode.getIpNetTable().size()==0)
    		{
    			//返回com.afunms.discovery.IpAddress类型的List，
    			newNode.setIpNetTable(snmp.getIpNetToMediaTable(newNode.getIpAddress(),newNode.getCommunity()));//得到所有IpNetToMedia,即直接与该设备连接的ip
    			//newNode.setIpNetTable(SnmpUtils.getIpNetToMediaTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));//得到所有IpNetToMedia,即直接与该设备连接的ip
    		}
    		if(newNode.getPortMacs() == null || newNode.getPortMacs().size()==0)
    		{
    			//返回hashtable，50=[00:0f:e2:49:fc:47, 00:0f:e2:49:fd:fa]，这是其中一个的键值对
    			newNode.setPortMacs(snmp.getDtpFdbTable(newNode.getIpAddress(),newNode.getCommunity()));
    			//newNode.setPortMacs(SnmpUtils.getDtpFdbTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
    		}
    		SysLogger.info(newNode.getIpAddress()+" ################# PortMacs size "+newNode.getPortMacs().size());
    		if(newNode.getAdminIp() == null)
    		{
    			newNode.setAtInterfaces(snmp.getAtInterfaceTable(newNode.getIpAddress(),newNode.getCommunity()));
    			//newNode.setAtInterfaces(SnmpUtils.getAtInterfaceTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
    		}
    		if(newNode.getBridgeIdentifiers() == null){
    			newNode.addBridgeIdentifier(snmp.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity()));
//    			try{
//    				newNode.addBridgeIdentifier(SnmpUtils.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
//    			}catch(Exception e){
//    				
//    			}
    		}
    		//设置fdb接口数据
       	 if(newNode.getFdbList() == null)
       	 {
       		 try{
       			 //返回值是 String[]类型的List，其中的一个String[]为 [67108897, 00:0f:e2:49:fc:47]
       			newNode.setFdbList(snmp.getFdbTable(newNode.getIpAddress(), newNode.getCommunity()));
       			//newNode.setFdbList(SnmpUtils.getFdbTable(newNode.getIpAddress(), newNode.getCommunity(),-1,3,5000));
       		 }catch(Exception e){
       			 SysLogger.info("获取设备"+newNode.getIpAddress()+"的FDB数据出错"+e.getMessage()); 
       		 }
       	 }
 		//若是CISCO类型的设备,得到CDP数据
 		if(newNode.getSysOid().indexOf("1.3.6.1.4.1.9")>=0)
 		{
     		if(newNode.getCdpList() == null || newNode.getCdpList().size()==0)
         		newNode.setCdpList(snmp.getCiscoCDPList(newNode.getIpAddress(),newNode.getCommunity()));
     			//newNode.setCdpList(SnmpUtils.getCiscoCDPList(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
 		}
    }
       	       	
    	
    	if(newNode.getSuperNode()==-1)
    	{
    		newNode.setSuperNode(newNode.getId());
    	}
    	else if(newNode.getCategory()==2 || newNode.getCategory()==3 || newNode.getCategory()==7 || newNode.getCategory()==8)//把该交换设备加到它到它父节点的交换列表中
    	{
    		try{
    			getHostByID(newNode.getSuperNode()).addSwitchId(newNode.getId());    		
    		}catch(Exception e){
    			
    		}
    	}
    	SysLogger.info("开始获取设备:"+newNode.getIpAddress()+"的系统名称");
    	Hashtable sysGroupProperty = snmp.getSysGroup(newNode.getIpAddress(),newNode.getCommunity());
    	//Hashtable sysGroupProperty = SnmpUtils.getSysGroup(newNode.getIpAddress(),newNode.getCommunity(),3,5000);
    	if(sysGroupProperty != null)
    	{
        	newNode.setSysDescr((String)sysGroupProperty.get("sysDescr"));
        	//newNode.setsys((String)sysGroupProperty.get("sysUpTime"));
        	newNode.setSysContact((String)sysGroupProperty.get("sysContact"));
        	newNode.setSysName((String)sysGroupProperty.get("sysName"));
        	newNode.setSysLocation((String)sysGroupProperty.get("sysLocation"));
    	}
        	                                    
        //
        existIpList.add(newNode.getIpAddress());  
        
        if(newNode.getCategory()==4||newNode.getCategory()==5||newNode.getCategory()==6) //对于服务器和打印机,防火墙,无线接入  
        {
        	newNode.setDiscovered(true);
        	//开始设置设备的发现状态,若为新发现设备,设为-1
        	setStatus(newNode);
        	//newNode.setDiscovered(true);
        	//hostList.add(newNode);
        	addHost(newNode);//添加进hostList
        	SysLogger.info(newNode.toString());        	        	        	       
            return;        	
        }
        

        //开始设置设备的发现状态,若为新发现设备,设为-1，与formerNodeList队列中的对象进行比较
        setStatus(newNode);
        SysLogger.info("新增加接点"+newNode.getIpAddress()+" Layer:"+newNode.getLayer());
        
        //需要判断节点是否存在
        //这里需要添加从已经存在的设备列表中获取设备,这样可以避免重复取设备数据
    	for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&&&&#####"+newNode.getIpAddress()+"----BRIDGE  "+newNode.getBridgeAddress()+"======="+tmpNode.getIpAddress()+"===BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(newNode.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+newNode.getCategory()+"的设备"+newNode.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    		if(tmpNode.getIpAddress().equalsIgnoreCase(newNode.getIpAddress()))//查看是否是存在指定IP的网络设备
    		{
    			newNode = tmpNode;
    			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
    			flag = 1;
    			break;
    		}
    		else
    		{
    			List aliasIPS = tmpNode.getAliasIPs();
    			if(aliasIPS == null)continue;
    			if(aliasIPS.contains(newNode.getIpAddress()))//查看该IP是否是网络设备众多网口的其中一个网口，如果是，说明该IP所代表的网络设备已经被发现过。
    			{
        			newNode = tmpNode;
        			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
        			flag = 1;
        			break;
    			}
    		}
    	}
    	if(flag == 1)//表明设备已经被发现过一次
    	{
    		newNode.setDiscovered(true);
    		return;
    	}  
        
    	//hostList.add(newNode);
    	addHost(newNode);
    	
        //以下对于网络设备
        dealLink(newNode,link);//link = null;
        
        SysLogger.info(newNode.toString());
        
        //对接口list不为空的网络设备再发现
        if(newNode.getCategory() != 7){
        	if(newNode.getIfEntityList()!=null&&newNode.getIfEntityList().size()!=0)
        		newNode.doDiscover(); 
        }
    }
    
    /**
     * 把设备类型已经确定的设备加入其中,deviceType=1,2,3,4,5,7,8 
     */
    public void addHost_SOLO(Host newNode,Link link)
    {
    	/**
    	 * 
    	 * <select size=1 name='type' style='width:100px;' onchange="unionSelect();">
            								<option value='1' selected>路由器</option>
            								<option value='2'>路由交换机</option>
            								<option value='3'>交换机</option>
            								<option value='4'>服务器</option>
            								<option value='8'>防火墙</option>
            							</select>
    	 */
    	int flag = 0;
    	if(newNode.getSysOid() == null)
    	{
    		newNode.setDiscovered(true);//感觉这里设置为true，并不代表设备已经被发现了，而是为了设置这样一个状态，使后面部分代码不执行。
    		return;
    	}
    	//这里需要添加从已经存在的设备列表中获取设备,这样可以避免重复取设备数据
    	for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&&&&******"+newNode.getIpAddress()+"----BRIDGE  "+newNode.getBridgeAddress()+"======="+tmpNode.getIpAddress()+"===BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(newNode.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+newNode.getCategory()+"的设备"+newNode.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    		if(tmpNode.getIpAddress().equalsIgnoreCase(newNode.getIpAddress()))//查看是否是存在指定IP的网络设备
    		{
    			newNode = tmpNode;
    			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
    			flag = 1;
    			break;
    		}
    		else
    		{
    			List aliasIPS = tmpNode.getAliasIPs();
    			if(aliasIPS == null)continue;
    			if(aliasIPS.contains(newNode.getIpAddress()))//查看该IP是否是网络设备众多网口的其中一个网口，如果是，说明该IP所代表的网络设备已经被发现过。
    			{
        			newNode = tmpNode;
        			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
        			flag = 1;
        			break;
    			}
    		}
    	}
    	if(flag == 1)//表明设备已经被发现过一次
    	{
    		newNode.setDiscovered(true);
    		return;
    	}
    	
    	
    	SysLogger.info("开始分析类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress());    	
    	//路由交换或二层交换,并且增加了防火墙
    	if(newNode.getCategory()==2 || newNode.getCategory()==3 || newNode.getCategory()==7 || newNode.getCategory()==8)
    	{
    		newNode.setBridgeAddress(snmp.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity()));//设置该IP对应的mac地址
//    		try{
//    			newNode.setBridgeAddress(SnmpUtils.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity(),3,5000));//设置该IP对应的mac地址
//    		}catch(Exception e){
//    			
//    		}
    		SysLogger.info("&&&&&&&&&&&&&&&&&&");
    		SysLogger.info(newNode.getIpAddress()+" "+newNode.getBridgeAddress());
    		SysLogger.info("&&&&&&&&&&&&&&&&&&");
       	 	//得到STP的桥数据(STP只对二层交换起作用)
    		if(newNode.getCategory()!=7)
    		{
    			if(newNode.getBridgestpList() == null || newNode.getBridgestpList().size()==0)
    				//snmp.getBridgeStpList(newNode.getIpAddress(),newNode.getCommunity())获得的是com.afunms.discovery.BridgeStpInterface类型的List
        			//该List的size与getIfEntityList返回的List的size大小可以不一样，但貌似这两个变量的物理意义是有关联的。
    				newNode.setBridgestpList(snmp.getBridgeStpList(newNode.getIpAddress(),newNode.getCommunity()));
    				//newNode.setBridgestpList(SnmpUtils.getBridgeStpList(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
    		}
    	}
    	//获取该设备的接口地址
        if(newNode.getCategory() <5 || newNode.getCategory()==7)
        {
        	//对于路由和服务器 	
        	SysLogger.info("开始获取设备:"+newNode.getIpAddress()+"的接口表");  
        	if(newNode.getIfEntityList() == null)
        		newNode.setIfEntityList(snmp.getIfEntityList2(newNode.getIpAddress(),newNode.getCommunity(),newNode.getCategory(),newNode.getSnmpversion()));
        		//newNode.setIfEntityList(SnmpUtils.getIfEntityList2(newNode.getIpAddress(),newNode.getCommunity(),newNode.getCategory(),newNode.getSnmpversion()));
        		
        		//snmp.getIfEntityList(newNode.getIpAddress(),newNode.getCommunity(),newNode.getCategory())返回com.afunms.discovery.IfEntity类型的List，该IfEntity既有ethernet这样的是端口信息，也有vlan虚端口信息
        		//newNode.setIfEntityList(snmp.getIfEntityList(newNode.getIpAddress(),newNode.getCommunity(),newNode.getCategory()));
        	   
        	
        }
        
        if(newNode.getSysOid() != null )
        {
            if(newNode.getSysOid().indexOf("1.3.6.1.4.1.25506")>=0 &&(newNode.getCategory()==2 || newNode.getCategory()==3))
            {
            	//H3C的交换设备
            	if(newNode.getNdpHash() == null)
            		//返回的hashtable描述： key 保存mac地址，value 保存mac地址对应的端口的名字，如ethernet1/1/1
            		newNode.setNdpHash(snmp.getNDPTable(newNode.getIpAddress(),newNode.getCommunity()));
            		//newNode.setNdpHash(SnmpUtils.getNDPTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
            }
        }
        
    	//若一个设备已经存在,是否可以分析第二次????
        SysLogger.info("开始判断类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+"=======是否已经存在"); 
        
       
        for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&&&&@@@@@"+newNode.getIpAddress()+"----BRIDGE  "+newNode.getBridgeAddress()+"======="+tmpNode.getIpAddress()+"===BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(newNode.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+newNode.getCategory()+"的设备"+newNode.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    	}
    	if(flag == 1)//表明设备已经被发现过一次
    	{
    		newNode.setDiscovered(true);
    		return;
    	}
        
        
        
        
        
        if(link != null)
        	SysLogger.info(newNode.getIpAddress()+" "+newNode.getAdminIp()+"   link:startip: "+link.getStartIp()+" endip:"+link.getEndIp());
    	//该方法虽然传入link对象，但却未使用到该对象，此方法只是检查设备列表里是否已经加入了newNode节点，通过比较设备IP地址情况
//        if(isHostExist(newNode))
//        {
//    		SysLogger.info("结束判断类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+"是否已经存在,当前为已经存在并返回");
//    		return;
//    	}
    	SysLogger.info("结束判断类型为"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+"======是否已经存在");
    	
    	newNode.setId(KeyGenerator.getInstance().getHostKey());
    	//若是网络设备
    	if(newNode.getCategory()==1 || newNode.getCategory()==2 || newNode.getCategory()==3 || newNode.getCategory()==7 || newNode.getCategory()==8)
    	{
    		if(newNode.getIpNetTable() == null || newNode.getIpNetTable().size()==0)
    		{
    			//返回com.afunms.discovery.IpAddress类型的List，
    			newNode.setIpNetTable(snmp.getIpNetToMediaTable(newNode.getIpAddress(),newNode.getCommunity()));//得到所有IpNetToMedia,即直接与该设备连接的ip
    			//newNode.setIpNetTable(SnmpUtils.getIpNetToMediaTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));//得到所有IpNetToMedia,即直接与该设备连接的ip
    		}
    		if(newNode.getPortMacs() == null || newNode.getPortMacs().size()==0)
    		{
    			//返回hashtable，50=[00:0f:e2:49:fc:47, 00:0f:e2:49:fd:fa]，这是其中一个的键值对
    			newNode.setPortMacs(snmp.getDtpFdbTable(newNode.getIpAddress(),newNode.getCommunity()));
    			//newNode.setPortMacs(SnmpUtils.getDtpFdbTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
    		}
    		SysLogger.info(newNode.getIpAddress()+" ################# PortMacs size "+newNode.getPortMacs().size());
    		if(newNode.getAdminIp() == null)
    		{
    			newNode.setAtInterfaces(snmp.getAtInterfaceTable(newNode.getIpAddress(),newNode.getCommunity()));
    			//newNode.setAtInterfaces(SnmpUtils.getAtInterfaceTable(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
    		}
    		if(newNode.getBridgeIdentifiers() == null){
    			newNode.addBridgeIdentifier(snmp.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity()));
//    			try{
//    				newNode.addBridgeIdentifier(SnmpUtils.getBridgeAddress(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
//    			}catch(Exception e){
//    				
//    			}
    		}
    		//设置fdb接口数据
       	 if(newNode.getFdbList() == null)
       	 {
       		 try{
       			 //返回值是 String[]类型的List，其中的一个String[]为 [67108897, 00:0f:e2:49:fc:47]
       			newNode.setFdbList(snmp.getFdbTable(newNode.getIpAddress(), newNode.getCommunity()));
       			//newNode.setFdbList(SnmpUtils.getFdbTable(newNode.getIpAddress(), newNode.getCommunity(),-1,3,5000));
       		 }catch(Exception e){
       			 SysLogger.info("获取设备"+newNode.getIpAddress()+"的FDB数据出错"+e.getMessage()); 
       		 }
       	 }
 		//若是CISCO类型的设备,得到CDP数据
 		if(newNode.getSysOid().indexOf("1.3.6.1.4.1.9")>=0)
 		{
     		if(newNode.getCdpList() == null || newNode.getCdpList().size()==0)
         		newNode.setCdpList(snmp.getCiscoCDPList(newNode.getIpAddress(),newNode.getCommunity()));
     			//newNode.setCdpList(SnmpUtils.getCiscoCDPList(newNode.getIpAddress(),newNode.getCommunity(),3,5000));
 		}
    }
       	       	
    	
    	if(newNode.getSuperNode()==-1)
    	{
    		newNode.setSuperNode(newNode.getId());
    	}
    	else if(newNode.getCategory()==2 || newNode.getCategory()==3 || newNode.getCategory()==7 || newNode.getCategory()==8)//把该交换设备加到它到它父节点的交换列表中
    	{
    		try{
    			getHostByID(newNode.getSuperNode()).addSwitchId(newNode.getId());    		
    		}catch(Exception e){
    			
    		}
    	}
    	SysLogger.info("开始获取设备:"+newNode.getIpAddress()+"的系统名称");
    	Hashtable sysGroupProperty = snmp.getSysGroup(newNode.getIpAddress(),newNode.getCommunity());
    	//Hashtable sysGroupProperty = SnmpUtils.getSysGroup(newNode.getIpAddress(),newNode.getCommunity(),3,5000);
    	if(sysGroupProperty != null)
    	{
        	newNode.setSysDescr((String)sysGroupProperty.get("sysDescr"));
        	//newNode.setsys((String)sysGroupProperty.get("sysUpTime"));
        	newNode.setSysContact((String)sysGroupProperty.get("sysContact"));
        	newNode.setSysName((String)sysGroupProperty.get("sysName"));
        	newNode.setSysLocation((String)sysGroupProperty.get("sysLocation"));
    	}
        	                                    
        //
        existIpList.add(newNode.getIpAddress());  
        
        if(newNode.getCategory()==4||newNode.getCategory()==5||newNode.getCategory()==6) //对于服务器和打印机,防火墙,无线接入  
        {
        	newNode.setDiscovered(true);
        	//开始设置设备的发现状态,若为新发现设备,设为-1
        	setStatus(newNode);
        	//newNode.setDiscovered(true);
        	//hostList.add(newNode);
        	addHost(newNode);//添加进hostList
        	SysLogger.info(newNode.toString());        	        	        	       
            return;        	
        }
        

        //开始设置设备的发现状态,若为新发现设备,设为-1，与formerNodeList队列中的对象进行比较
        setStatus(newNode);
        SysLogger.info("新增加接点"+newNode.getIpAddress()+" Layer:"+newNode.getLayer());
        
        //需要判断节点是否存在
        //这里需要添加从已经存在的设备列表中获取设备,这样可以避免重复取设备数据
    	for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&&&&#####"+newNode.getIpAddress()+"----BRIDGE  "+newNode.getBridgeAddress()+"======="+tmpNode.getIpAddress()+"===BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(newNode.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+newNode.getCategory()+"的设备"+newNode.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    		if(tmpNode.getIpAddress().equalsIgnoreCase(newNode.getIpAddress()))//查看是否是存在指定IP的网络设备
    		{
    			newNode = tmpNode;
    			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
    			flag = 1;
    			break;
    		}
    		else
    		{
    			List aliasIPS = tmpNode.getAliasIPs();
    			if(aliasIPS == null)continue;
    			if(aliasIPS.contains(newNode.getIpAddress()))//查看该IP是否是网络设备众多网口的其中一个网口，如果是，说明该IP所代表的网络设备已经被发现过。
    			{
        			newNode = tmpNode;
        			SysLogger.info("已发现的设备列表中已经存在"+newNode.getCategory()+"的设备:"+newNode.getIpAddress()+",从列表中得到该设备");
        			flag = 1;
        			break;
    			}
    		}
    	}
    	if(flag == 1)//表明设备已经被发现过一次
    	{
    		newNode.setDiscovered(true);
    		return;
    	}  
    	addHost(newNode);
    	
        //以下对于网络设备
        dealLink(newNode,link);//link = null;
        
        SysLogger.info(newNode.toString());
    }
    
    /**
     * 加入一个子网 
     */
    public synchronized void addSubNet(SubNet subnet)
    {
    	//判断是否已经存在
    	if(subNetList.contains(subnet)) return;
    	
    	int id = KeyGenerator.getInstance().getSubNetKey();
    	subnet.setId(id);
    	subNetList.add(subnet);
    }
     
    /**
     * 在分析完交换机的直连关系后，把新链路加入
     */
    public synchronized void addLinks(List temporaryLinks)
    {
    	if(temporaryLinks==null) return;
    	
    	for(int i=0;i<temporaryLinks.size();i++)
    	{
    		TemporaryLink templink = (TemporaryLink)temporaryLinks.get(i);
    		if(templink.isDel()) continue;
    		
    		Host startNode = getHostByID(templink.getStart().getId());
    		IfEntity if1 = startNode.getIfEntityByIndex(templink.getStart().getIfIndex());
            if(if1==null) continue;
            
            if(if1.getDescr() != null){
            	String descr = if1.getDescr();
            	if(descr.indexOf("GigabitEthernet")>=0){
            		String allchassis = descr.substring(descr.lastIndexOf("t"));
            		String[] chassis = allchassis.split("/");
            		if(chassis.length>1){
            			String printstr = "";
            			for(int k=0;k<chassis.length;k++){
            				printstr=printstr+"=="+chassis[k];
            			}
            			System.out.println(printstr);
            		}
            		System.out.println(descr.substring(descr.lastIndexOf("t"))); 
            	}else if(descr.indexOf("Ethernet")>=0){
            		
            	}
            }
    		
    		Host endNode = getHostByID(templink.getEnd().getId());
    		IfEntity if2 = endNode.getIfEntityByIndex(templink.getEnd().getIfIndex());
    		if(if2==null) continue;
    		
    		Link link = new Link();
    		link.setStartId(templink.getStart().getId());
    		link.setStartIndex(if1.getIndex());
    		if(if1.getIpAddress().equals(""))
    		   link.setStartIp(startNode.getIpAddress());	
    		else    			
    		   link.setStartIp(if1.getIpAddress());
    		link.setStartPhysAddress(if1.getPhysAddress());
    		link.setStartPort(if1.getPort());
    		link.setStartDescr(if1.getDescr());

    		link.setEndId(templink.getEnd().getId());
    		link.setEndIndex(if2.getIndex());
    		link.setEndIp(if2.getIpAddress());
    		if(if2.getIpAddress().equals(""))
     		   link.setEndIp(endNode.getIpAddress());	
     		else    			
     		   link.setEndIp(if2.getIpAddress());    		
    		link.setEndPhysAddress(if2.getPhysAddress());
    		link.setEndPort(if2.getPort());
    		link.setEndDescr(if2.getDescr());
    		
    		if(linkList.contains(link)) continue;    		
    		for(int j=0;j<linkList.size();j++)
    		{
    			Link tempLink = (Link)linkList.get(j);
    			
    			if(tempLink.getStartId()==link.getStartId() 
    			   && tempLink.getEndId()==link.getEndId())
    			{
    				System.out.println("Temp双链路:" + link.getStartIp() + "|" + link.getStartIndex() + "<--->" + link.getEndIp() + "|" + link.getEndIndex());
    				System.out.println("和" + tempLink.getStartIp() + "|" + tempLink.getStartIndex() + "<--->" + tempLink.getEndIp() + "|" + tempLink.getEndIndex());
                    link.setAssistant(1);				
    				break;
    			} 	
    		}       		
    		linkList.add(link);
    	}    		
    }
    
    private synchronized void addHost(Host node){
    	int flag = 0;
    	for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		//SysLogger.info("&&**"+node.getIpAddress()+"--BRIDGE  "+node.getBridgeAddress()+"   "+tmpNode.getIpAddress()+"  BRIDGE "+tmpNode.getBridgeAddress());
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(node.getBridgeAddress()))	
		    {
		    	SysLogger.info("类型为"+node.getCategory()+"的设备"+node.getIpAddress()+"已经存在");
		    	//exist = true;
		    	flag = 1;
		    	break;	
		    }
    		if(tmpNode.getIpAddress().equalsIgnoreCase(node.getIpAddress())){
    			flag = 1;
    			break;
    		}
    		//if(tmpNode.getCategory()==5 || tmpNode.getCategory()==6 ) continue; //打印机或防火墙
    	}
    	if(flag == 0){
    		hostList.add(node);
        	//将设备加入数据库,同时分析链路关系
        	DiscoverCompleteDao nodeDao = new DiscoverCompleteDao();
        	List tempList = new ArrayList();
        	tempList.add(node);
        	try{
        		nodeDao.addHostData(tempList);
        	}catch(Exception e){
        		
        	}finally{
        		nodeDao.close();
        	}
    	}

    	
    }
    /**
     * 确定一个设备是否已经存在
     * 当前Link只有起始IP和起始端口连接ID
     */
    private boolean isHostExist(Host node)
    {
    	boolean exist = false;
    	int flag = 0;
    	List node_ipalias = node.getAliasIPs();
    	for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		
    		if(tmpNode.getCategory()==5 || tmpNode.getCategory()==6 ) continue; //打印机或防火墙
    		
    		//SysLogger.info("&&&&&******"+node.getIpAddress()+"----Bridge  "+node.getBridgeAddress()+"   "+tmpNode.getIpAddress()+"===Bridge:"+tmpNode.getBridgeAddress());
    		
		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(node.getBridgeAddress()))	
		    {
		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
		    	SysLogger.info("类型为"+node.getCategory()+"的设备"+node.getIpAddress()+"已经存在");
		    	exist = true;
		    	flag = 1;
		    	break;	
		    }
    		//if(flag == 1)return exist;
    		
    		if(tmpNode.getIpAddress().equalsIgnoreCase(node.getIpAddress()))
			{
				//existHost = tmpNode;
				SysLogger.info("已发现的设备列表中已经存在"+tmpNode.getCategory()+"的设备:"+node.getIpAddress());	
				exist = true;
				break;
			}else{
				//判断别名IP是否存在
				List aliasIPs = tmpNode.getAliasIPs();
				if(aliasIPs != null && aliasIPs.size()>0){
					if(aliasIPs.contains(node.getIpAddress())){
						SysLogger.info("已发现的设备列表中已经存在"+tmpNode.getCategory()+"的设备:"+node.getIpAddress());
						exist = true;
						break;
					}
					for(int k=0;k<aliasIPs.size();k++){
						String temp_ip = (String)aliasIPs.get(k);						
						if(node_ipalias != null && node_ipalias.size()>0){
							for(int m=0;m<node_ipalias.size();m++){								
								if(temp_ip.equalsIgnoreCase((String)node_ipalias.get(m))){
									//SysLogger.info("@*"+node.getIpAddress()+"-别名  "+node_ipalias.get(m)+"="+tmpNode.getIpAddress()+"=temp_ip:"+temp_ip+" nodeBRIDGE:"+node.getBridgeAddress()+" tmpNodeBRIDGE:"+tmpNode.getBridgeAddress());
									//同时判断BRIDGE
									
									
					    		    if(tmpNode.getBridgeAddress()!=null && tmpNode.getBridgeAddress().equalsIgnoreCase(node.getBridgeAddress()))	
					    		    {
					    		    	//SysLogger.info("&&&&&******"+node.getIpAddress()+"----别名  "+node_ipalias.get(m)+"======="+tmpNode.getIpAddress()+"===temp_ip:"+temp_ip);
					    		    	SysLogger.info("类型为"+node.getCategory()+"的设备"+node.getIpAddress()+"已经存在");
					    		    	exist = true;
					    		    	flag = 1;
					    		    	break;	
					    		    }
								}
							}
							if(flag == 1)break;
							
//							if(node_ipalias.contains(temp_ip)){
//								SysLogger.info("###-----已发现的设备列表中已经存在"+tmpNode.getCategory()+" "+tmpNode.getIpAddress()+" 的设备:"+node.getIpAddress());
//								exist = true;
//								flag = 1;
//								break;
//							}
						}	
					}
					if(flag == 1)break;
				}
			}
    		
    	}
    	SysLogger.info("类型为"+node.getCategory()+"的设备"+node.getIpAddress()+"目前存在为--"+exist);
    	  	
       	return exist;
    } 
    
    public boolean isDiscovered()
    {
//    	threadPool.interrupt();
//		threadPool.close();
//		threadPool = null;
    	/*
    	SysLogger.info("############当前有"+threads+"个线程在运行##############"+"已经结束"+discoverdcount+"个线程");
    	//Runnable test = (Runnable)threadList.get(0);
    	if(this.discoverdcount == this.threads){
    		//threadPool.join();
    		//threadPool.interrupt();
    		//threadPool.destroy();
    		return true;
    	}else
    		return false;
    	*/
    	
    	
    	boolean finish = true;
    	for(int i=0;i<threadList.size();i++)
    	{
    		BaseThread bt = (BaseThread)threadList.get(i); 
    		if(!bt.isCompleted())
    	    {
    			SysLogger.info(((BaseThread)threadList.get(i)).getName() + "(" + ((BaseThread)threadList.get(i)).getClass().getName() + ") has not complete");
    			finish = false;
    	        break;
    	    }
    		else
    		{
    			threadList.remove(bt);//gzm
    	    	bt = null;
    	    }
    	}
    	if(finish)
    	{
    		SysLogger.info("----发现完毕----");
    		//threadExecutor.notifyAll();
    		threadExecutor.shutdown();
			if (!threadExecutor.isShutdown()) 
			{

				List<Runnable> threads = threadExecutor.shutdownNow();

				for (int i = 0; i < threads.size(); ++i) {
					try {
						((Thread)threads.get(i)).interrupt();
					} catch (Exception e) {

					}
				}
			}	
    	}	
    	return finish;
    	
    	//return false;
    	
    }
    	    
    /**
     * node为链路的终点
     */
	public void dealLink(Host node,Link link)
	{        
		if(link==null) return;
		if(link.getStartId() == link.getEndId())return;
		//过滤掉端口聚合的情况(逻辑连接关系)
		if(link.getStartDescr().toLowerCase().indexOf("channel")>=0) return;
		Hashtable hostidHash = new Hashtable();
		for(int i=0;i<hostList.size();i++)
    	{
    		Host tmpNode = (Host)hostList.get(i);
    		hostidHash.put(tmpNode.getId()+"", tmpNode.getId()+"");
    	}
		//if( !hostidHash.containsKey(node.getId()+"") || !hostidHash.containsKey(link.getStartIndex()+""))return;
		if( !hostidHash.containsKey(node.getId()+"") || !hostidHash.containsKey(link.getStartId()+""))return;
		//hostList
		
		
		//特殊处理RouterLink
		if(link.getFindtype() == SystemConstant.ISRouter){
			//路由表获得的连接
		    IfEntity ipObj = node.getIfEntityByIP(link.getEndIp());
		    //if(ipObj==null)ipObj = node.getIfEntityByIP(link.getStartIp());
	        if(ipObj==null)	{ 
	        	SysLogger.info("======================");
	        	SysLogger.info(node.getIpAddress()+"设备中,找不到"+link.getEndIp()+"和"+link.getStartIp()+"对应的接口,返回");
	        	SysLogger.info("======================");
	        	return;				
	        }
			link.setEndDescr(ipObj.getDescr());
			link.setEndIndex(ipObj.getIndex());
			link.setEndPort(ipObj.getIndex());	
			link.setEndPhysAddress(ipObj.getPhysAddress());        	
			link.setEndId(node.getId());
			//link.setFindtype(5);//MAC发现
			link.setLinktype(SystemConstant.LOGICALLINK);//物理连接
			addRouteLink(routelinkList,link);//检查并添加连接关系
			//checkAssistantLink(linkList,link,1);
		}else if(link.getFindtype() == SystemConstant.ISMac){
			//MAC地址转发表获得的连接
		    IfEntity ipObj = node.getIfEntityByIP(link.getEndIp());
	        if(ipObj==null)	{
	        	SysLogger.info("======================");
	        	SysLogger.info(node.getIpAddress()+"设备中,找不到"+link.getEndIp()+"和"+link.getStartIp()+"对应的接口,返回");
	        	SysLogger.info("======================");
	        	return;				
	        }
			link.setEndDescr(ipObj.getDescr());
			link.setEndIndex(ipObj.getIndex());
			link.setEndPort(ipObj.getIndex());	
			link.setEndPhysAddress(ipObj.getPhysAddress());        	
			link.setEndId(node.getId());
			link.setLinktype(SystemConstant.LOGICALLINK);//逻辑连接
			addMacLink(maclinkList,link);//检查并添加连接关系
		}
	}
	
	public synchronized void addLink(List linkList,Link link){
		if(linkList != null && linkList.size()>0){
			List existList = new ArrayList();
			int existFlag = 0;
			if(link.getLinktype() != -1){
				//不判断VLAN连接关系
				for(int i=0;i<linkList.size();i++){
					Link tempLink = (Link)linkList.get(i);
					if(tempLink.getLinktype() == -1)continue;
					if(isSameLink(link,tempLink)){
						//已经存在该连接
						existFlag = 1;
						break;
						//existList.add(tempLink);
					}
				}
			}else{
				//只判断VLAN连接关系
				for(int i=0;i<linkList.size();i++){
					Link tempLink = (Link)linkList.get(i);
					if(tempLink.getLinktype() != -1)continue;
					if(isSameLink(link,tempLink)){
						//已经存在该连接
						existFlag = 1;
						break;
						//existList.add(tempLink);
					}
				}
			}

			if(existFlag == 0){
				linkList.add(link);
				SysLogger.info("新增连接链路关系 "+link.getStartIp()+ "|"+link.getStartIndex()+"<--->" + link.getEndIp() + "|" + link.getEndIndex());
			}
		}else{
			linkList.add(link);
			SysLogger.info("新增连接链路关系 "+link.getStartIp()+ "|"+link.getStartIndex()+"<--->" + link.getEndIp() + "|" + link.getEndIndex());
		}
		
	}
	
	public synchronized void addRouteLink(List linkList,Link link){
		if(linkList != null && linkList.size()>0){
			List existList = new ArrayList();
			int existFlag = 0;
			if(link.getLinktype() != -1){
				//不判断VLAN连接关系
				for(int i=0;i<linkList.size();i++){
					Link tempLink = (Link)linkList.get(i);
					if(tempLink.getLinktype() == -1)continue;
					if(isSameLink(link,tempLink)){
						//已经存在该连接
						existFlag = 1;
						break;
						//existList.add(tempLink);
					}
				}
			}else{
				//只判断VLAN连接关系
				for(int i=0;i<linkList.size();i++){
					Link tempLink = (Link)linkList.get(i);
					if(tempLink.getLinktype() != -1)continue;
					if(isSameLink(link,tempLink)){
						//已经存在该连接
						existFlag = 1;
						break;
						//existList.add(tempLink);
					}
				}
			}

			if(existFlag == 0){
				linkList.add(link);
				SysLogger.info("新增连接链路关系 "+link.getStartIp()+ "|"+link.getStartIndex()+"<--->" + link.getEndIp() + "|" + link.getEndIndex());
			}
		}else{
			linkList.add(link);
			SysLogger.info("新增连接链路关系 "+link.getStartIp()+ "|"+link.getStartIndex()+"<--->" + link.getEndIp() + "|" + link.getEndIndex());
		}
		
	}
	
	public synchronized void addMacLink(List linkList,Link link){
		if(linkList != null && linkList.size()>0){
			List existList = new ArrayList();
			int existFlag = 0;
			if(link.getLinktype() != -1){
				//不判断VLAN连接关系
				for(int i=0;i<linkList.size();i++){
					Link tempLink = (Link)linkList.get(i);
					if(tempLink.getLinktype() == -1)continue;
					if(isSameLink(link,tempLink)){
						//已经存在该连接
						existFlag = 1;
						break;
						//existList.add(tempLink);
					}
				}
			}else{
				//只判断VLAN连接关系
				for(int i=0;i<linkList.size();i++){
					Link tempLink = (Link)linkList.get(i);
					if(tempLink.getLinktype() != -1)continue;
					if(isSameLink(link,tempLink)){
						//已经存在该连接
						existFlag = 1;
						break;
						//existList.add(tempLink);
					}
				}
			}

			if(existFlag == 0){
				linkList.add(link);
				SysLogger.info("新增连接链路关系 "+link.getStartIp()+ "|"+link.getStartIndex()+"<--->" + link.getEndIp() + "|" + link.getEndIndex());
			}
		}else{
			linkList.add(link);
			SysLogger.info("新增连接链路关系 "+link.getStartIp()+ "|"+link.getStartIndex()+"<--->" + link.getEndIp() + "|" + link.getEndIndex());
		}
		
	}
	/*
	 * 判断双链路关系
	 */
	public synchronized void checkAssistantLink(List linkList,Link link,int linktype){
		if(linkList != null && linkList.size()>0){
			for(int i=0;i<linkList.size();i++)
			{
				Link tempLink = (Link)linkList.get(i);
				if(linktype != -1){
					if(tempLink.getLinktype() < 0 )continue;
				}else{
					if(tempLink.getLinktype() != linktype)continue;
				}
				
				if((tempLink.getStartId()==link.getStartId() 
				   && tempLink.getEndId()==link.getEndId())||
				   (tempLink.getStartId()==link.getEndId() 
						   && tempLink.getEndId()==link.getStartId()))
				{
					if((link.getStartIp().equalsIgnoreCase(tempLink.getStartIp())
							&& link.getStartIndex().equalsIgnoreCase(tempLink.getStartIndex())) ||
							(link.getEndIp().equalsIgnoreCase(tempLink.getEndIp())
									&& link.getEndIndex().equalsIgnoreCase(tempLink.getEndIndex()))
							) continue;
					
					SysLogger.info("======================");
					SysLogger.info("dealLink找到双链路:" + link.getStartIp() + "|" + link.getStartIndex() + "<--->" + link.getEndIp() + "|" + link.getEndIndex());
					SysLogger.info("和" + tempLink.getStartIp() + "|" + tempLink.getStartIndex() + "<--->" + tempLink.getEndIp() + "|" + tempLink.getEndIndex());
	                link.setAssistant(1);				
					break;
				} 	
			}
		}
	}
	
	/*
	 * 判断两个连接是否相同
	 * @param (Link)sourceLink 
	 * @param (Link)destLink 
	 */
	public boolean isSameLink(Link sourceLink,Link destLink){
		if((destLink.getStartIp().equalsIgnoreCase(sourceLink.getStartIp())&&destLink.getStartIndex().equalsIgnoreCase(sourceLink.getStartIndex())&&destLink.getEndIp().equalsIgnoreCase(sourceLink.getEndIp()) && destLink.getEndIndex().equalsIgnoreCase(sourceLink.getEndIndex()))
			||(destLink.getStartIp().equalsIgnoreCase(sourceLink.getEndIp())&&destLink.getStartIndex().equalsIgnoreCase(sourceLink.getEndIndex())&&destLink.getEndIp().equalsIgnoreCase(sourceLink.getStartIp()) && destLink.getEndIndex().equalsIgnoreCase(sourceLink.getStartIndex())))
			return true;
		return false;
	}
	
	/**
	 * 找到网络设备的管理地址
	 */
	private void findManageAddress(Host node)
	{
		if(node.getIfEntityList()==null) return;
		
		List ifList = node.getIfEntityList();
		for(int i=0;i<ifList.size();i++)
		{
			IfEntity ifObj = (IfEntity)ifList.get(i);
			if(ifObj.getType()==24&&!"".equals(ifObj.getIpAddress())&&!"127.0.0.1".equals(ifObj.getIpAddress()))
			{
				SysLogger.info(node.getIpAddress() + " 的管理地址是 " + ifObj.getIpAddress());
				node.setIpAddress(ifObj.getIpAddress());				
				break;
			}
		}
	}
	
	/**
	 * 设置设备的发现状态
	 */
	private void setStatus(Host node)
	{
		//得到上次已经发现过的设备
		List formerNodeList = DiscoverEngine.getInstance().getFormerNodeList();
		if(formerNodeList != null && formerNodeList.size()>0){
			int flag = 0;
			for(int k=0;k<formerNodeList.size();k++){
				   Host formernode = (Host)formerNodeList.get(k);
				   //if(formernode.getIpAddress().equals(node.getIpAddress())&& formernode.getBridgeAddress().equals(node.getBridgeAddress())){
				   if(formernode.getIpAddress().equals(node.getIpAddress())){
					   int status = formernode.getDiscoverstatus();
					   if(status == -1){
						   node.setDiscoverstatus(status+1);
					   }else if(status > 0){
						   node.setDiscoverstatus(-2);
					   }
					   flag = 1;
					   formerNodeList.remove(k);//删除该设备
					   break;
				   }
			}
			if(flag == 0){
				//新发现的设备
				node.setDiscoverstatus(-1);
			}
		}else{
			//第一次自动发现
			node.setDiscoverstatus(-1);
		}
	}
	
	public synchronized Host getHostByID(int id)
	{
		Host host = null;
		for(int i=0;i<hostList.size();i++)
		{
			SysLogger.info("已有设备"+((Host)hostList.get(i)).getIpAddress()+" ID:"+((Host)hostList.get(i)).getId()+" id:"+id);
			if(((Host)hostList.get(i)).getId()==id)
			{
			    host = (Host)hostList.get(i);
			    break;
			}			    	
		}
		return host;
	}
	
	public synchronized Host getHostByIP(String ip)
	{
		Host host = null;
		for(int i=0;i<hostList.size();i++)
		{
			//SysLogger.info("IP:"+ip+"-------host:"+((Host)hostList.get(i)).getIpAddress());			
			if(((Host)hostList.get(i)).getIpAddress().equalsIgnoreCase(ip))
			{
			    host = (Host)hostList.get(i);
			    break;
			}			    	
		}
		return host;
	}
	
	public synchronized Host getHostByAliasIP(String ip)
	{
		Host host = null;
		for(int i=0;i<hostList.size();i++)
		{
			if(((Host)hostList.get(i)).getAliasIPs() == null) continue;
			if(((Host)hostList.get(i)).getAliasIPs().contains(ip))
			{
			    host = (Host)hostList.get(i);
			    break;
			}			    	
		}
		return host;
	}
	
    public List getThreadList()
    {
       return threadList;	
    }
    public List getExistIpList()
    {
        return existIpList;
    }
    
    public List getSubNetList()
    {
       return subNetList;	
    }

    public List getHostList()
    {
       return hostList;	
    }
    
    public int getSnmpversion()
    {
       return snmpversion;	
    }
    public void setSnmpversion(int snmpversion)
    {
       this.snmpversion = snmpversion;	
    }
    
    public int getDiscovermodel()
    {
       return discovermodel;	
    }
    public void setDiscovermodel(int discovermodel)
    {
       this.discovermodel = discovermodel;	
    }
    
    public String getWritecommunity()
    {
       return writecommunity;	
    }
    public void setWritecommunity(String writecommunity)
    {
       this.writecommunity = writecommunity;	
    }
    
    public String getDiscover_bid()
    {
       return discover_bid;	
    }
    public void setDiscover_bid(String discover_bid)
    {
       this.discover_bid = discover_bid;	
    }
    
    public List getFaildIpList()
    {
       return faildIpList;	
    }

    public List getLinkList()
    {    	
    	return linkList;
    }
    public List getRouteLinkList()
    {    	
    	return routelinkList;
    }
    public List getMacLinkList()
    {    	
    	return maclinkList;
    }
    public List getFormerNodeList()
    {    	
    	return formerNodeList;
    }
    public void setFormerNodeList(List formerNodeList)
    {    	
    	this.formerNodeList = formerNodeList;
    }
    
    public List getFormerNodeLinkList()
    {    	
    	return formerNodeLinkList;
    }
    public void setFormerNodeLinkList(List formerNodeLinkList)
    {    	
    	this.formerNodeLinkList = formerNodeLinkList;
    }
    
    
    
	/**
	 * 将一个字符串形式的ip地址转换成一个长整数，如果是非法数据，则返回0
	 * 
	 * @param ip
	 * @return
	 */
	static public long ip2long(String ip) {
		long result = 0;
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}
	public static int getStopStatus() {
		return stopStatus;
	}
	public static void setStopStatus(int stopStatus) {
		DiscoverEngine.stopStatus = stopStatus;
	}
}
