/**
 * <p>Description:probe the router table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-13
 */

package com.afunms.discovery;

import java.util.*;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.*;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.snmp.cpu.BDComCpuSnmp;
import com.afunms.polling.snmp.cpu.CiscoCpuSnmp;
import com.afunms.polling.snmp.cpu.DLinkCpuSnmp;
import com.afunms.polling.snmp.cpu.EnterasysCpuSnmp;
import com.afunms.polling.snmp.cpu.H3CCpuSnmp;
import com.afunms.polling.snmp.cpu.MaipuCpuSnmp;
import com.afunms.polling.snmp.cpu.NortelCpuSnmp;
import com.afunms.polling.snmp.cpu.RadwareCpuSnmp;
import com.afunms.polling.snmp.cpu.RedGiantCpuSnmp;
import com.afunms.polling.snmp.cpu.ZTECpuSnmp;
import com.afunms.polling.snmp.fan.CiscoFanSnmp;
import com.afunms.polling.snmp.fan.H3CFanSnmp;
import com.afunms.polling.snmp.flash.BDComFlashSnmp;
import com.afunms.polling.snmp.flash.CiscoFlashSnmp;
import com.afunms.polling.snmp.flash.H3CFlashSnmp;
import com.afunms.polling.snmp.interfaces.ArpSnmp;
import com.afunms.polling.snmp.interfaces.FdbSnmp;
import com.afunms.polling.snmp.interfaces.InterfaceSnmp;
import com.afunms.polling.snmp.interfaces.PackageSnmp;
import com.afunms.polling.snmp.interfaces.RouterSnmp;
import com.afunms.polling.snmp.memory.BDComMemorySnmp;
import com.afunms.polling.snmp.memory.CiscoMemorySnmp;
import com.afunms.polling.snmp.memory.DLinkMemorySnmp;
import com.afunms.polling.snmp.memory.EnterasysMemorySnmp;
import com.afunms.polling.snmp.memory.H3CMemorySnmp;
import com.afunms.polling.snmp.memory.MaipuMemorySnmp;
import com.afunms.polling.snmp.memory.NortelMemorySnmp;
import com.afunms.polling.snmp.memory.RedGiantMemorySnmp;
import com.afunms.polling.snmp.ping.PingSnmp;
import com.afunms.polling.snmp.power.CiscoPowerSnmp;
import com.afunms.polling.snmp.power.H3CPowerSnmp;
import com.afunms.polling.snmp.system.SystemSnmp;
import com.afunms.polling.snmp.temperature.BDComTemperatureSnmp;
import com.afunms.polling.snmp.temperature.CiscoTemperatureSnmp;
import com.afunms.polling.snmp.temperature.H3CTemperatureSnmp;
import com.afunms.polling.snmp.voltage.CiscoVoltageSnmp;
import com.afunms.polling.snmp.voltage.H3CVoltageSnmp;
import com.afunms.polling.task.ThreadPool;
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.model.DiscoverConfig;

public class IPRouterProbeThread_SOLO_Runable 
//public class IPNetProbeThread implements Runnable
{
   private Host node;
   private List addressList;
   
   /**
   创建任务
	 */	
	public static Runnable createTask(final Host node) {
   return new Runnable() {
       public void run() {
    	   if(DiscoverEngine.getInstance().getStopStatus() == 1)return;
    	   SysLogger.info("开始分析"+node.getIpAddress()+"路由表数据");
    	   //首先找出合法的子网
    	   List subNetList = new ArrayList();
    	   try{
    		   subNetList = SnmpUtil.getInstance().getSubNetList(node.getIpAddress(),node.getCommunity()); 
    	   }catch(Exception ex){
        	  ex.printStackTrace();
    	   }
    	   List netshieldList = DiscoverResource.getInstance().getNetshieldList();
    	   //若子网不存在或子网数为0,则对该设备进行地址转发表分析
      	  	if(subNetList==null||subNetList.size()==0)   
      	  	{
      	  		node.updateCount(1);
      	  		//setCompleted(true);
      	  		return; 	
      	  	}	
      	  	//SysLogger.info("子网数为 : "+subNetList.size());  	  
      	  	for(int i=0;i<subNetList.size();i++)
      	  	{
      	  		SubNet subNet = (SubNet)subNetList.get(i);  
      	  		IfEntity ifEntity = node.getIfEntityByIndex(subNet.getIfIndex());
      	  		if(ifEntity==null) continue;    	  
        	  
      	  		if(!ifEntity.getSubNetList().contains(subNet))
      	  		{	  
      	  			ifEntity.getSubNetList().add(subNet);
      	  			DiscoverEngine.getInstance().addSubNet(subNet);    		     
      	  		}   
        	      	  
      	  		//===========对于核心设备,因为开始不确定它在哪个子网,在这里才可以确定=============
      	  		if(node.getLocalNet()==-1
        	    && NetworkUtil.isValidIP(subNet.getNetAddress(),subNet.getNetMask(),node.getIpAddress()))
      	  			node.setLocalNet(subNet.getId());   	  
          	}	
    	          
      	  		//再找出合法的router
      	  		List routerList = SnmpUtil.getInstance().getRouterList(node.getIpAddress(),node.getCommunity()); 
      	  		node.setRouteList(routerList);
      	  		if(routerList==null||routerList.size()==0)   
      	  		{
      	  			//若路由表列表为空的时候,则对该设备的地址转发表进行采集发现
      	  			SysLogger.info("设备"+node.getIpAddress()+"路由表记录为空,则进行该设备的地址转发表进行分析发现");
      	  			//node.updateCount(1);
      	  			//取消对ARP表进行分析
      	  			node.updateCount(0);
      	  			//setCompleted(true);
      	  			return; 	
      	  		}	
      	  		SysLogger.info("设备"+node.getIpAddress()+"路由表记录数为: "+routerList.size());
      	  		List nextRouterList = new ArrayList();
      	  		List doRouterList = new ArrayList();
      	  		for(int i=0;i<routerList.size();i++){
      	  			IpRouter ipr = (IpRouter)routerList.get(i);	
      	  			String nextRouter = null;	   
      	  			if(ipr.getType()==4){
      	  				//indirect
      	  				nextRouter = ipr.getNextHop();
      	  			}    	 
      	  			else
      	  			{
      	  				continue;
      	  			}
    	  	  		if(isHostExist(nextRouter)){
    	  	  			continue;
    	  	  		}
    	  	  		DiscoverEngine.getInstance().getHostList();
      	  			//判断该IP是否已经存在
      	  			if(nextRouterList.contains(nextRouter))continue;
      	  			nextRouterList.add(nextRouter);
      	  			doRouterList.add(ipr);
      	  		}
      	  		Date startdate = new Date();
      	  		//IPRouterProbeSubThread probeThread = null;
        		// 生成线程池
        		ThreadPool threadPool = new ThreadPool(doRouterList.size());														
        		// 运行任务
      	  		for(int i=0;i<doRouterList.size();i++)
      	  		{
      	  			IpRouter ipr = (IpRouter)doRouterList.get(i);	
      	  			threadPool.runTask(RouterThread_SOLO.createTask(ipr, node));   	   
      	  		}
      	  		// 关闭线程池并等待所有任务完成
        		threadPool.join();
        		threadPool.close();
        	  	threadPool = null;
        		Date enddate = new Date();
        		SysLogger.info("##############################");
    			SysLogger.info("### "+node.getIpAddress()+" 发现时间 "+(enddate.getTime()-startdate.getTime())+"####");
    			SysLogger.info("##############################");
        		
        		
      	  		//node.updateCount(1);
      	  		//取消对ARP表进行分析
      	  		node.updateCount(0);
      	  		SysLogger.info("分析完路由表后,开始分析"+node.getIpAddress()+"设备的地址转发表");
      	  		SysLogger.info("分析完路由表后,结束分析"+node.getIpAddress()+"设备的地址转发表");
      	  		DiscoverEngine.getInstance().addDiscoverdcount();
      	  		//snmp = null;
      	  		//setCompleted(true);
       }
   };
	}
	
	/**
	    * 确定一个设备是否已经存在
	    * 当前Link只有起始IP和起始端口连接ID
	    */
	   private static boolean isHostExist(String ip)
	   {
		List hostList = DiscoverEngine.getInstance().getHostList();
		if(hostList == null)hostList = new ArrayList();
	   	boolean exist = false;
	   	for(int i=0;i<hostList.size();i++)
	   	{
	   		Host tmpNode = (Host)hostList.get(i);
	   		if(tmpNode.getCategory()==5 || tmpNode.getCategory()==6 ) continue; //打印机或防火墙
	   		
	   		if(tmpNode.getCategory()==2 || tmpNode.getCategory()==3 || tmpNode.getCategory()==7)//对于路由交换机或交换机的情况
	   		{
					if(tmpNode.getIpAddress().equalsIgnoreCase(ip))
						{
							//existHost = tmpNode;
							SysLogger.info("已发现的设备列表中已经存在"+tmpNode.getCategory()+"的设备:"+ip);	
							exist = true;
							break;
						}
						else
						{
							//判断别名IP是否存在
							List aliasIPs = tmpNode.getAliasIPs();
							if(aliasIPs != null && aliasIPs.size()>0){
								if(aliasIPs.contains(ip)){
									//existHost = tmpNode;
									SysLogger.info("已发现的设备列表中已经存在"+tmpNode.getCategory()+"的设备:"+ip);
									exist = true;
									break;
								}
							}
						}
	   		}

	   	}
	   	//SysLogger.info("类型为"+node.getCategory()+"的设备"+node.getIpAddress()+"目前存在为--"+exist);
	   	  	
	      	return exist;
	   }	
}