/**
 * <p>Description:operate table NMS_DISCOVER_CONDITION</p>
 * 主要用于发现完之后，数据入库 
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.PollDataUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;
import com.afunms.discovery.DiscoverEngine;
import com.afunms.discovery.Host;
import com.afunms.discovery.IfEntity;
import com.afunms.discovery.IpAddress;
import com.afunms.discovery.KeyGenerator;
import com.afunms.discovery.Link;
import com.afunms.discovery.RepairLink;
import com.afunms.discovery.SubNet;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.monitor.executor.base.MonitorFactory;
import com.afunms.monitor.item.base.MonitorObject;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.snmp.LoadAixFile;
import com.afunms.polling.snmp.LoadHpUnixFile;
import com.afunms.polling.snmp.LoadLinuxFile;
import com.afunms.polling.snmp.LoadSunOSFile;
import com.afunms.polling.snmp.LoadWindowsWMIFile;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;

public class DiscoverCompleteDao extends BaseDao
{
   private int nmID = 0;	
   private int telnetID = 0;
   private static final List moList = MonitorFactory.getMonitorObjectList();
   public DiscoverCompleteDao()
   {
	   super("topo_host_node");
	   nmID = getNextID("topo_node_monitor");
   }
         
   /**
    * 清空数据
    */
   public void clear()
   {
	   
	   //开始清除所有的节点历史数据信息 修改人:hukelei 修改时间: 2010-07-28
       conn.addBatch("delete from topo_host_node");
       
       //开始清除所有的链路历史数据信息 修改人:hukelei 修改时间: 2010-07-28
       LinkDao linkdao = new LinkDao();
       List linkList = new ArrayList();
       try{
    	   linkList = linkdao.loadByTpye(0);
    	   linkdao = new LinkDao();
    	   linkdao.deleteutils(linkList);
       }catch(Exception e){
    	   e.printStackTrace();
       }finally{
    	   linkdao.close();
       }
       //结束清除所有的链路历史数据信息 修改人:hukelei 修改时间: 2010-07-28 
       conn.addBatch("delete from topo_network_link");
       
       conn.addBatch("delete from topo_subnet");
       conn.addBatch("delete from topo_node_monitor");      
       conn.addBatch("delete from topo_node_multi_data");
       conn.addBatch("delete from topo_node_single_data");
       conn.addBatch("delete from topo_interface");
       conn.addBatch("delete from topo_interface_data");
       conn.addBatch("delete from topo_custom_xml");
       conn.addBatch("delete from nms_alarm_message");
       conn.addBatch("delete from server_telnet_config");
       conn.addBatch("delete from app_ip_node");
       conn.addBatch("delete from app_tomcat_node");   
       conn.addBatch("delete from app_db_node");
       conn.addBatch("delete from system_eventlist where subtype='host' or subtype='net' or subtype='db'");
       conn.executeBatch();	   
       nmID = 0;   
   }
   
   public void addID()
   {
	   conn.executeUpdate("update topo_node_id set id=" + KeyGenerator.getInstance().getHostKey());
   }
   
   /**
    * 加入链路数据
    */
   public void addLinkData(List linkList)
   {
	  if(linkList==null) return;
	  
	  RepairLinkDao repairdao = new RepairLinkDao();
	  List repairlist = repairdao.loadAll();
	  if(repairlist == null)repairlist= new ArrayList();
      for(int i=0;i<linkList.size();i++)
      {    	  
      	 Link link = (Link)linkList.get(i);
      	 
      	 for(int k=0;k<repairlist.size();k++){
      		 RepairLink repairlink = (RepairLink)repairlist.get(k);
      		 Host starthost = DiscoverEngine.getInstance().getHostByID(link.getStartId());
      		 Host endhost = DiscoverEngine.getInstance().getHostByID(link.getEndId());
      		SysLogger.info("================================");
      		SysLogger.info(starthost.getIpAddress()+"=="+repairlink.getStartIp()+"=="+link.getStartIndex()+"=="+repairlink.getStartIndex());
      		SysLogger.info(endhost.getIpAddress()+"=="+repairlink.getEndIp()+"=="+link.getEndIndex()+"=="+repairlink.getEndIndex());
      		SysLogger.info("================================");
      		 if(starthost.getIpAddress().equalsIgnoreCase(repairlink.getStartIp()) && link.getStartIndex().equalsIgnoreCase(repairlink.getStartIndex())
      				 && endhost.getIpAddress().equalsIgnoreCase(repairlink.getEndIp()) && link.getEndIndex().equalsIgnoreCase(repairlink.getEndIndex())){
      			 //存在修改过的连接关系
      			SysLogger.info("存在修改过的连接关系!"+starthost.getIpAddress()+"==="+endhost.getIpAddress());
      			 link.setStartIndex(repairlink.getNewStartIndex());
      			 link.setEndIndex(repairlink.getNewEndIndex());
      			 link.setStartDescr(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getDescr());
      			 link.setEndDescr(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getDescr());
      			 link.setStartPort(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getPort());
      			 link.setEndPort(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getPort());
      			 linkList.set(i, link);
      		 }else if(starthost.getIpAddress().equalsIgnoreCase(repairlink.getEndIp()) && link.getStartIndex().equalsIgnoreCase(repairlink.getEndIndex())
      				 && endhost.getIpAddress().equalsIgnoreCase(repairlink.getStartIp()) && link.getEndIndex().equalsIgnoreCase(repairlink.getStartIndex())){
      			 //存在修改过的连接关系
      			SysLogger.info("存在修改过的连接关系!"+starthost.getIpAddress()+"==="+endhost.getIpAddress());
      			 link.setStartIndex(repairlink.getNewEndIndex());
      			 link.setEndIndex(repairlink.getNewStartIndex());
      			 link.setStartDescr(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getDescr());
      			 link.setEndDescr(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getDescr());
      			 link.setStartPort(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getPort());
      			 link.setEndPort(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getPort());
      			 linkList.set(i, link);
      		 }
      	 }
      }
	  
      int id = getNextID("topo_network_link");
      Hashtable donelinkhashtable = new Hashtable();
      for(int i=0;i<linkList.size();i++){    	
    	 
      	 Link link = (Link)linkList.get(i);
      	 String startPhysAddress = link.getStartPhysAddress();
      	 String endPhysAddress = link.getEndPhysAddress();
      	 if(startPhysAddress != null){
      		startPhysAddress = CommonUtil.removeIllegalStr(startPhysAddress);
      	 }else{
      		startPhysAddress = "";
      	 }
      	 if(endPhysAddress != null){
      		endPhysAddress = CommonUtil.removeIllegalStr(endPhysAddress);
      	 }else{
      		endPhysAddress = "";
      	 }
      	 if(donelinkhashtable.containsKey(link.getStartIp()+"_"+link.getStartIndex()+"/"+link.getEndIp()+"_"+link.getEndIndex())){
      		 continue;
      	 }else{
      		donelinkhashtable.put(link.getStartIp()+"_"+link.getStartIndex()+"/"+link.getEndIp()+"_"+link.getEndIndex(),"");
      	 }
      	 id = id + 1;
      	 
      	 StringBuffer sql = new StringBuffer(300);
      	 sql.append("insert into topo_network_link(id,link_name,start_id,end_id,start_ip,end_ip,start_index,");
      	 sql.append("end_index,start_descr,end_descr,start_port,end_port,start_mac,end_mac,assistant,type,findtype,linktype,max_speed)values(");
	     sql.append(id);
	     sql.append(",'");
	     //sql.append(link.getStartIp()+"_"+link.getStartPort()+"/"+link.getEndIp()+"_"+link.getEndPort());
	     sql.append(link.getStartIp()+"_"+link.getStartIndex()+"/"+link.getEndIp()+"_"+link.getEndIndex());
	     sql.append("',");
	     sql.append(link.getStartId());
	     sql.append(",");
	     sql.append(link.getEndId());
	     sql.append(",'");
	     sql.append(link.getStartIp());
	     sql.append("','");
	     sql.append(link.getEndIp());
	     sql.append("','");
	     sql.append(link.getStartIndex());
	     sql.append("','");
	     sql.append(link.getEndIndex());
	     sql.append("','");
	     sql.append(link.getStartDescr());
	     sql.append("','");
	     sql.append(link.getEndDescr());
	     sql.append("','");
	     sql.append(link.getStartPort()==null?"":link.getStartPort());
	     sql.append("','");
	     sql.append(link.getEndPort()==null?"":link.getEndPort());
	     sql.append("','");
	     sql.append(startPhysAddress);
	     sql.append("','");
	     sql.append(endPhysAddress);	  
	     sql.append("',");
	     sql.append(link.getAssistant());
	     sql.append(",");
	     sql.append(1);	
	     sql.append(",");
	     sql.append(link.getFindtype());
	     sql.append(",");
	     sql.append(link.getLinktype());
	     sql.append(",'200000')");
	     SysLogger.info(sql.toString());
	     try{
	    	 conn.executeUpdate(sql.toString(),false);  
	     }catch(Exception e){
	    	 
	     }
	     
	     	//生成相应的链路历史数据表 修改人:hukelei 2010-07-28
		    CreateTableManager ctable = new CreateTableManager();
		    try{
			    ctable.createTable(conn,"lkping",(id)+"","lkping");//链路状态
				ctable.createTable(conn,"lkpinghour",(id)+"","lkpinghour");//链路状态按小时
				ctable.createTable(conn,"lkpingday",(id)+"","lkpingday");//链路状态按天
				
				
				ctable.createTable(conn,"lkuhdx",(id)+"","lkuhdx");//链路流速
				ctable.createTable(conn,"lkuhdxhour",(id)+"","lkuhdxhour");//链路流速
				ctable.createTable(conn,"lkuhdxday",(id)+"","lkuhdxday");//链路流速
				
				ctable.createTable(conn,"lkuhdxp",(id)+"","lkuhdxp");//链路带宽利用率
				ctable.createTable(conn,"lkuhdxphour",(id)+"","lkuhdxphour");//链路带宽利用率
				ctable.createTable(conn,"lkuhdxpday",(id)+"","lkuhdxpday");//链路带宽利用率
				
				conn.executeBatch();
		    }catch(Exception e){
		    	e.printStackTrace();
		    }finally{
		    	//conn.close();
		    }
      }	
      donelinkhashtable.clear();
      donelinkhashtable = null;
      conn.commit(); 
   }

   /**
    * 加入主机接口数据
    */   
   public void addInterfaceData(List hostList)
   {
      int id = getNextID("topo_interface");      
      for(int i=0;i<hostList.size();i++)
      {
    	 com.afunms.discovery.Host host = (com.afunms.discovery.Host)hostList.get(i);
    	 List ifList = host.getIfEntityList();
    	 if(ifList==null) continue;
    	 com.afunms.discovery.IfEntity ifEntity = null;
    	 for(int j=0;j<ifList.size();j++)
    	 {
    		 ifEntity = (com.afunms.discovery.IfEntity)ifList.get(j);
					 String physAddress = ifEntity.getPhysAddress().replace("'", "").replace("<", "").replace(">", "");
    		 physAddress = CommonUtil.removeIllegalStr(physAddress);
    		 StringBuffer sql = new StringBuffer(300);    		 
    		 sql.append("insert into topo_interface(id,node_id,entity,descr,port,speed,phys_address,ip_address,oper_status,type,chassis,slot,uport)values(");
    		 sql.append(id++);
    		 sql.append(","); 
    		 sql.append(host.getId());
    		 sql.append(",'");
    		 sql.append(ifEntity.getIndex());
    		 sql.append("','");
    		 sql.append(replace(ifEntity.getDescr())); 
    		 sql.append("','");
    		 sql.append(ifEntity.getPort()==null?"":ifEntity.getPort());
    		 sql.append("','");
    		 sql.append(ifEntity.getSpeed());
    		 sql.append("','");
    		 sql.append(physAddress);
    		 sql.append("','");
    		 sql.append(ifEntity.getIpList()); //所有IP地址
    		 sql.append("',");
    		 sql.append(ifEntity.getOperStatus());
    		 sql.append(",");
    		 sql.append(ifEntity.getType()); //端口类型
    		 sql.append(",");
    		 sql.append(ifEntity.getChassis()); //框架
    		 sql.append(",");
    		 sql.append(ifEntity.getSlot()); //槽
    		 sql.append(",");
    		 sql.append(ifEntity.getUport()); //口
    		 sql.append(")");  
    		 //SysLogger.info(sql.toString());
    		 conn.executeUpdate(sql.toString(),false);    
    	 }//end_for_j
    	 conn.commit();
    	 conn.executeUpdate("update topo_interface set alias=descr");
      }//end_for_i
   }
   
   /**
    * 加入主机接口数据
    */   
   public void addARPData(List hostList)
   {
      int id = getNextID("topo_arp");   
      List calhostlist = hostList;
      Hashtable tempHash = new Hashtable();
      Hashtable aliasHash = new Hashtable();
      List aliaslist = new ArrayList();
      for(int k=0;k<calhostlist.size();k++){
    	  com.afunms.discovery.Host host = (com.afunms.discovery.Host)hostList.get(k);
    	  tempHash.put(host.getIpAddress(), "");
    	  aliaslist = host.getAliasIPs();
    	  if(aliaslist != null && aliaslist.size()>0){
    		  for(int m=0;m<aliaslist.size();m++){
    			  aliasHash.put((String)aliaslist.get(m), host.getIpAddress());
    		  }
    	  }
      }
      for(int i=0;i<hostList.size();i++)
      {
    	 com.afunms.discovery.Host host = (com.afunms.discovery.Host)hostList.get(i);
    	 List arpList = host.getIpNetTable();
    	 if(arpList==null) continue;
    	 IpAddress ipAddress = null;
    	 int snmpflag = 0;
    	 for(int j=0;j<arpList.size();j++)
    	 {
    		 snmpflag = 0;
    		 ipAddress = (IpAddress)arpList.get(j);
    		 if(tempHash.containsKey(ipAddress.getIpAddress())){
    			 //snmp is open
    			 snmpflag = 1;
    		 }else if(aliasHash.containsKey(ipAddress.getIpAddress())){
    			 //alias ip is existed
    			 snmpflag = 1;
    		 }
    		 String physAddress = ipAddress.getPhysAddress();
    		 physAddress = CommonUtil.removeIllegalStr(physAddress);
    		 StringBuffer sql = new StringBuffer(300);    		 
    		 sql.append("insert into topo_arp(id,node_id,ifindex,physaddress,ipaddress,monflag)values(");
    		 sql.append(id++);
    		 sql.append(","); 
    		 sql.append(host.getId());
    		 sql.append(",'");
    		 sql.append(ipAddress.getIfIndex());
    		 sql.append("','");
    		 sql.append(ipAddress.getPhysAddress()); 
    		 sql.append("','");
    		 sql.append(ipAddress.getIpAddress());
    		 sql.append("',");
    		 sql.append(snmpflag); //默认情况下是未启动SNMP
    		 sql.append(")");    		 
    		 conn.executeUpdate(sql.toString(),false);    
    	 }//end_for_j
    	 conn.commit();
    	 //conn.executeUpdate("update topo_interface set alias=descr");
      }//end_for_i
   }
   
   /**
    * 加入主机数据,同时加入网络设备IP的别名
    */   
   public void addHostData(List hostList)
   {    
	   Hashtable donehost = new Hashtable();
	  for(int i=0;i<hostList.size();i++)
      {
		 try{
		 hostList.get(i).toString();
    	 Host node = (Host)hostList.get(i);
    	 if(donehost.containsKey(node.getId()))continue;
    	 node.setBid(DiscoverEngine.getInstance().getDiscover_bid());
			//测试生成表
			String ip = node.getIpAddress();
//			String ip1 ="",ip2="",ip3="",ip4="";
//			String[] ipdot = ip.split(".");	
//			String tempStr = "";
			String allipstr = SysUtil.doip(ip);
//			if (ip.indexOf(".")>0){
//				ip1=ip.substring(0,ip.indexOf("."));
//				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//			}
//			ip2=tempStr.substring(0,tempStr.indexOf("."));
//			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//			allipstr=ip1+ip2+ip3+ip4;
			CreateTableManager ctable = new CreateTableManager();
			if ((node.getCategory()>0 && node.getCategory()< 4) || node.getCategory() == 7){
				try{
					if(DiscoverEngine.getInstance() == null){
						//SysLogger.info("DiscoverEngine ===== null");
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//SysLogger.info(DiscoverEngine.getInstance().getDiscovermodel()+"=====");
				if(DiscoverEngine.getInstance().getDiscovermodel() == 1){
					//补充发现
					if(node.getDiscoverstatus() == -1){
						//新发现的设备
						
						//生成网络设备表	
						//连通率
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
						
						//内存
						ctable.createTable(conn,"memory",allipstr,"mem");//内存
						ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
						ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
						
						ctable.createTable(conn,"flash",allipstr,"flash");//闪存
						ctable.createTable(conn,"flashhour",allipstr,"flashhour");//闪存
						ctable.createTable(conn,"flashday",allipstr,"flashday");//闪存
						
						ctable.createTable(conn,"buffer",allipstr,"buffer");//缓存
						ctable.createTable(conn,"bufferhour",allipstr,"bufferhour");//缓存
						ctable.createTable(conn,"bufferday",allipstr,"bufferday");//缓存
						
						ctable.createTable(conn,"fan",allipstr,"fan");//风扇
						ctable.createTable(conn,"fanhour",allipstr,"fanhour");//风扇
						ctable.createTable(conn,"fanday",allipstr,"fanday");//风扇
						
						ctable.createTable(conn,"power",allipstr,"power");//电源
						ctable.createTable(conn,"powerhour",allipstr,"powerhour");//电源
						ctable.createTable(conn,"powerday",allipstr,"powerday");//电源
						
						ctable.createTable(conn,"vol",allipstr,"vol");//电压
						ctable.createTable(conn,"volhour",allipstr,"volhour");//电压
						ctable.createTable(conn,"volday",allipstr,"volday");//电压
						
						//CPU
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						//带宽利用率
						ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
						ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
						ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
						
						//每个端口流速
						ctable.createTable(conn,"utilhdx",allipstr,"hdx");
						ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
						ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
						
						//综合流速
						ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
						ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
						ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
						
						//关键端口状态
						ctable.createTable(conn,"portstatus",allipstr,"port");
						
						//丢包率
						ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
						ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
						ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
						
						//错误率
						ctable.createTable(conn,"errorsperc",allipstr,"errperc");
						ctable.createTable(conn,"errperch",allipstr,"errperch");
						ctable.createTable(conn,"errpercd",allipstr,"errpercd");
						
						//数据包
						ctable.createTable(conn,"packs",allipstr,"packs");	
						ctable.createTable(conn,"packshour",allipstr,"packshour");
						ctable.createTable(conn,"packsday",allipstr,"packsday");
						
						//入口数据库包
						ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
						ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
						ctable.createTable(conn,"ipackd",allipstr,"ipackd");
						
						//出口数据包
						ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
						ctable.createTable(conn,"opackh",allipstr,"opackh");
						ctable.createTable(conn,"opacksd",allipstr,"opacksd");
						
						//温度
						ctable.createTable(conn,"temper",allipstr,"temper");	
						ctable.createTable(conn,"temperh",allipstr,"temperh");
						ctable.createTable(conn,"temperd",allipstr,"temperd");
					}
				}else{
					//重新发现
					//先删除网络设备表	
					
//					//连通率表
//					ctable.deleteTable(conn,"ping",allipstr,"ping");//Ping
//					ctable.deleteTable(conn,"pinghour",allipstr,"pinghour");//Ping
//					ctable.deleteTable(conn,"pingday",allipstr,"pingday");//Ping
//					
//					//内存表
//					ctable.deleteTable(conn,"memory",allipstr,"mem");//内存
//					ctable.deleteTable(conn,"memoryhour",allipstr,"memhour");//内存
//					ctable.deleteTable(conn,"memoryday",allipstr,"memday");//内存
//					
//					ctable.deleteTable(conn,"flash",allipstr,"flash");//闪存
//					ctable.deleteTable(conn,"flashhour",allipstr,"flashhour");//闪存
//					ctable.deleteTable(conn,"flashday",allipstr,"flashday");//闪存
//					
//					ctable.deleteTable(conn,"buffer",allipstr,"buffer");//缓存
//					ctable.deleteTable(conn,"bufferhour",allipstr,"bufferhour");//缓存
//					ctable.deleteTable(conn,"bufferday",allipstr,"bufferday");//缓存
//					
//					ctable.deleteTable(conn,"fan",allipstr,"fan");//风扇
//					ctable.deleteTable(conn,"fanhour",allipstr,"fanhour");//风扇
//					ctable.deleteTable(conn,"fanday",allipstr,"fanday");//风扇
//					
//					ctable.deleteTable(conn,"power",allipstr,"power");//电源
//					ctable.deleteTable(conn,"powerhour",allipstr,"powerhour");//电源
//					ctable.deleteTable(conn,"powerday",allipstr,"powerday");//电源
//					
//					ctable.deleteTable(conn,"vol",allipstr,"vol");//电压
//					ctable.deleteTable(conn,"volhour",allipstr,"volhour");//电压
//					ctable.deleteTable(conn,"volday",allipstr,"volday");//电压
//					
//					//CPU表
//					ctable.deleteTable(conn,"cpu",allipstr,"cpu");//CPU
//					ctable.deleteTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
//					ctable.deleteTable(conn,"cpuday",allipstr,"cpuday");//CPU
//					
//					//带宽利用率表
//					ctable.deleteTable(conn,"utilhdxperc",allipstr,"hdperc");					
//					ctable.deleteTable(conn,"hdxperchour",allipstr,"hdperchour");
//					ctable.deleteTable(conn,"hdxpercday",allipstr,"hdpercday");	
//					
//					//流速表
//					ctable.deleteTable(conn,"utilhdx",allipstr,"hdx");
//					ctable.deleteTable(conn,"utilhdxhour",allipstr,"hdxhour");
//					ctable.deleteTable(conn,"utilhdxday",allipstr,"hdxday");	
//					
//					//综合流速
//					ctable.deleteTable(conn,"allutilhdx",allipstr,"allhdx");
//					ctable.deleteTable(conn,"allutilhdxhour",allipstr,"allhdxhour");
//					ctable.deleteTable(conn,"allutilhdxday",allipstr,"allhdxday");
//					
//					//丢包率表
//					ctable.deleteTable(conn,"discardsperc",allipstr,"dcardperc");
//					ctable.deleteTable(conn,"dcarperh",allipstr,"dcarperh");
//					ctable.deleteTable(conn,"dcardpercday",allipstr,"dcardpercday");
//					
//					//错误率表
//					ctable.deleteTable(conn,"errorsperc",allipstr,"errperc");
//					ctable.deleteTable(conn,"errperch",allipstr,"errperch");
//					ctable.deleteTable(conn,"errpercd",allipstr,"errpercd");
//					
//					//数据包表
//					ctable.deleteTable(conn,"packs",allipstr,"packs");	
//					ctable.deleteTable(conn,"packshour",allipstr,"packshour");
//					ctable.deleteTable(conn,"packsday",allipstr,"packsday");
//					
//					//入口数据包表
//					ctable.deleteTable(conn,"inpacks",allipstr,"inpacks");	
//					ctable.deleteTable(conn,"ipacksh",allipstr,"ipacksh");
//					ctable.deleteTable(conn,"ipackd",allipstr,"ipackd");
//					
//					//出口数据包表
//					ctable.deleteTable(conn,"outpacks",allipstr,"outpacks");	
//					ctable.deleteTable(conn,"opackh",allipstr,"opackh");
//					ctable.deleteTable(conn,"opacksd",allipstr,"opacksd");
//					
//					//温度表
//					ctable.deleteTable(conn,"temper",allipstr,"temper");	
//					ctable.deleteTable(conn,"temperh",allipstr,"temperh");
//					ctable.deleteTable(conn,"temperd",allipstr,"temperd");
					
					//生成网络设备表	
					//连通率表
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					
					//内存表
					ctable.createTable(conn,"memory",allipstr,"mem");//内存
					ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
					ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
					
					ctable.createTable(conn,"flash",allipstr,"flash");//闪存
					ctable.createTable(conn,"flashhour",allipstr,"flashhour");//闪存
					ctable.createTable(conn,"flashday",allipstr,"flashday");//闪存
					
					ctable.createTable(conn,"buffer",allipstr,"buffer");//缓存
					ctable.createTable(conn,"bufferhour",allipstr,"bufferhour");//缓存
					ctable.createTable(conn,"bufferday",allipstr,"bufferday");//缓存
					
					ctable.createTable(conn,"fan",allipstr,"fan");//风扇
					ctable.createTable(conn,"fanhour",allipstr,"fanhour");//风扇
					ctable.createTable(conn,"fanday",allipstr,"fanday");//风扇
					
					ctable.createTable(conn,"power",allipstr,"power");//电源
					ctable.createTable(conn,"powerhour",allipstr,"powerhour");//电源
					ctable.createTable(conn,"powerday",allipstr,"powerday");//电源
					
					ctable.createTable(conn,"vol",allipstr,"vol");//电压
					ctable.createTable(conn,"volhour",allipstr,"volhour");//电压
					ctable.createTable(conn,"volday",allipstr,"volday");//电压
					
					//CPU
					ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
					ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
					ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
					
					//带宽利用率表
					ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
					ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
					ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
					
					//流速
					ctable.createTable(conn,"utilhdx",allipstr,"hdx");
					ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
					ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");
					
					//综合流速
					ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
					ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
					ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
					
					//关键端口状态
					ctable.createTable(conn,"portstatus",allipstr,"port");
					
					//丢包
					ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
					ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
					ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
					
					//错误率
					ctable.createTable(conn,"errorsperc",allipstr,"errperc");
					ctable.createTable(conn,"errperch",allipstr,"errperch");
					ctable.createTable(conn,"errpercd",allipstr,"errpercd");
					
					//数据包
					ctable.createTable(conn,"packs",allipstr,"packs");	
					ctable.createTable(conn,"packshour",allipstr,"packshour");
					ctable.createTable(conn,"packsday",allipstr,"packsday");
					
					//入口数据包
					ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
					ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
					ctable.createTable(conn,"ipackd",allipstr,"ipackd");
					
					//出口数据包
					ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
					ctable.createTable(conn,"opackh",allipstr,"opackh");
					ctable.createTable(conn,"opacksd",allipstr,"opacksd");
					
					//温度
					ctable.createTable(conn,"temper",allipstr,"temper");	
					ctable.createTable(conn,"temperh",allipstr,"temperh");
					ctable.createTable(conn,"temperd",allipstr,"temperd");
				}
			}else if(node.getCategory() == 4){
				//主机设备
				try{
					if(DiscoverEngine.getInstance() == null){
						//SysLogger.info("DiscoverEngine ===== null");
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//SysLogger.info(DiscoverEngine.getInstance().getDiscovermodel()+"=====");
				if(DiscoverEngine.getInstance().getDiscovermodel() == 1){
					//补充发现
					if(node.getDiscoverstatus() == -1){
						//新发现的设备				
						//生成主机设备表					
						ctable.createTable(conn,"pro",allipstr,"pro");//进程
						ctable.createTable(conn,"prohour",allipstr,"prohour");//进程小时
						ctable.createTable(conn,"proday",allipstr,"proday");//进程天
						
						ctable.createSyslogTable(conn,"log",allipstr,"log");//进程天
						
						ctable.createTable(conn,"memory",allipstr,"mem");//内存
						ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
						ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
						
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						ctable.createTable(conn,"diskincre",allipstr,"diskincre");//磁盘增长率yangjun
						ctable.createTable(conn,"diskinch",allipstr,"diskinch");//磁盘增长率小时
						ctable.createTable(conn,"diskincd",allipstr,"diskincd");//磁盘增长率天
						
						ctable.createTable(conn,"disk",allipstr,"disk");//yangjun
						ctable.createTable(conn,"diskhour",allipstr,"diskhour");
						ctable.createTable(conn,"diskday",allipstr,"diskday");
						
						/*
						ctable.createTable("disk",allipstr,"disk");
						ctable.createTable("diskhour",allipstr,"diskhour");
						ctable.createTable("diskday",allipstr,"diskday");
						*/
						ctable.createTable(conn,"ping",allipstr,"ping");
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");
						ctable.createTable(conn,"pingday",allipstr,"pingday");
						
						ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
						ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
						ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
						

						ctable.createTable(conn,"utilhdx",allipstr,"hdx");
						ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
						ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");
						
						ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
						ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
						ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
						
						ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
						ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
						ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
						
						ctable.createTable(conn,"errorsperc",allipstr,"errperc");
						ctable.createTable(conn,"errperch",allipstr,"errperch");
						ctable.createTable(conn,"errpercd",allipstr,"errpercd");
						
						ctable.createTable(conn,"packs",allipstr,"packs");	
						ctable.createTable(conn,"packshour",allipstr,"packshour");
						ctable.createTable(conn,"packsday",allipstr,"packsday");
						
						ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
						ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
						ctable.createTable(conn,"ipackd",allipstr,"ipackd");
						
						ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
						ctable.createTable(conn,"opackh",allipstr,"opackh");
						ctable.createTable(conn,"opacksd",allipstr,"opacksd");

					}
				}else{
					//重新发现
//					//先删除服务器设备表		
//					ctable.deleteTable(conn,"pro",allipstr,"pro");//进程
//					ctable.deleteTable(conn,"prohour",allipstr,"prohour");//进程小时
//					ctable.deleteTable(conn,"proday",allipstr,"proday");//进程天
//					
//					ctable.deleteTable(conn,"ping",allipstr,"ping");//Ping
//					ctable.deleteTable(conn,"pinghour",allipstr,"pinghour");//Ping
//					ctable.deleteTable(conn,"pingday",allipstr,"pingday");//Ping
//					
//					
//					ctable.deleteTable(conn,"memory",allipstr,"mem");//内存
//					ctable.deleteTable(conn,"memoryhour",allipstr,"memhour");//内存
//					ctable.deleteTable(conn,"memoryday",allipstr,"memday");//内存
//					
//					ctable.deleteTable(conn,"cpu",allipstr,"cpu");//CPU
//					ctable.deleteTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
//					ctable.deleteTable(conn,"cpuday",allipstr,"cpuday");//CPU
//					
//					ctable.deleteTable(conn,"log",allipstr,"log");//CPU
//					
//					ctable.deleteTable(conn,"disk",allipstr,"disk");//yangjun
//					ctable.deleteTable(conn,"diskhour",allipstr,"diskhour");
//					ctable.deleteTable(conn,"diskday",allipstr,"diskday");
//					
//					ctable.deleteTable(conn,"diskincre",allipstr,"diskincre");//磁盘增长率yangjun
//					ctable.deleteTable(conn,"diskinch",allipstr,"diskinch");//磁盘增长率小时
//					ctable.deleteTable(conn,"diskincd",allipstr,"diskincd");//磁盘增长率天
//					
//					ctable.deleteTable(conn,"utilhdxperc",allipstr,"hdperc");					
//					ctable.deleteTable(conn,"hdxperchour",allipstr,"hdperchour");
//					ctable.deleteTable(conn,"hdxpercday",allipstr,"hdpercday");	
//					
//
//					ctable.deleteTable(conn,"utilhdx",allipstr,"hdx");
//					ctable.deleteTable(conn,"utilhdxhour",allipstr,"hdxhour");
//					ctable.deleteTable(conn,"utilhdxday",allipstr,"hdxday");
//					
//					
//					
//					ctable.deleteTable(conn,"allutilhdx",allipstr,"allhdx");
//					ctable.deleteTable(conn,"allutilhdxhour",allipstr,"allhdxhour");
//					ctable.deleteTable(conn,"allutilhdxday",allipstr,"allhdxday");
//					
//					ctable.deleteTable(conn,"temper",allipstr,"temper");	
//					ctable.deleteTable(conn,"temperh",allipstr,"temperh");
//					ctable.deleteTable(conn,"temperd",allipstr,"temperd");
					
					//生成主机设备表					
					ctable.createTable(conn,"pro",allipstr,"pro");//进程
					ctable.createTable(conn,"prohour",allipstr,"prohour");//进程小时
					ctable.createTable(conn,"proday",allipstr,"proday");//进程天
					
					ctable.createSyslogTable(conn,"log",allipstr,"log");//进程天
					
					ctable.createTable(conn,"memory",allipstr,"mem");//内存
					ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
					ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
					
					ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
					ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
					ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
					
					ctable.createTable(conn,"cpudtl",allipstr,"cpudtl");//CPU detail
					ctable.createTable(conn,"cpudtlhour",allipstr,"cpudtlhour");//CPU detail
					ctable.createTable(conn,"cpudtlday",allipstr,"cpudtlday");//CPU detail
					
					ctable.createTable(conn,"disk",allipstr,"disk");//yangjun
					ctable.createTable(conn,"diskhour",allipstr,"diskhour");
					ctable.createTable(conn,"diskday",allipstr,"diskday");
					
					ctable.createTable(conn,"diskincre",allipstr,"diskincre");//磁盘增长率yangjun
					ctable.createTable(conn,"diskinch",allipstr,"diskinch");//磁盘增长率小时
					ctable.createTable(conn,"diskincd",allipstr,"diskincd");//磁盘增长率天
					/*
					ctable.createTable("disk",allipstr,"disk");
					ctable.createTable("diskhour",allipstr,"diskhour");
					ctable.createTable("diskday",allipstr,"diskday");
					*/
					ctable.createTable(conn,"ping",allipstr,"ping");
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");
					ctable.createTable(conn,"pingday",allipstr,"pingday");
					
					ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
					ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
					ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
					

					ctable.createTable(conn,"utilhdx",allipstr,"hdx");
					ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
					ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");
					
					ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
					ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
					ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
					
//					ctable.createTable(conn,"temper",allipstr,"temper");	
//					ctable.createTable(conn,"temperh",allipstr,"temperh");
//					ctable.createTable(conn,"temperd",allipstr,"temperd");
				}
			}
    	 try{
    		 conn.executeBatch();
    	 }catch(Exception e){
    		 
    	 }
    	 String bridgeAddress = node.getBridgeAddress();
		 bridgeAddress = CommonUtil.removeIllegalStr(bridgeAddress);
    	 StringBuffer sql = new StringBuffer(300);
	     sql.append("insert into topo_host_node(id,ip_address,ip_long,net_mask,category,community,sys_oid,sys_name,super_node,");
	     sql.append("local_net,layer,sys_descr,sys_location,sys_contact,alias,type,managed,bridge_address,status,discoverstatus,write_community,snmpversion,ostype,collecttype,bid,sendemail,sendmobiles,sendphone)values(");
	     sql.append(node.getId());
	     sql.append(",'");
	     sql.append(node.getIpAddress());
	     sql.append("',");
	     sql.append(NetworkUtil.ip2long(node.getIpAddress()));
	     sql.append(",'");
	     sql.append(node.getNetMask());
	     sql.append("',");
	     sql.append(node.getCategory());
	     sql.append(",'");
	     sql.append(node.getCommunity());
	     sql.append("','");
	     sql.append(node.getSysOid());
	     sql.append("','");
         sql.append(replace(node.getSysName()));	     
	     sql.append("',");
	     sql.append(node.getSuperNode());
	     sql.append(",");
	     sql.append(node.getLocalNet());
	     sql.append(",");
	     sql.append(node.getLayer());
	     sql.append(",'");
	     sql.append(replace(node.getSysDescr()));
	     sql.append("','");
	     sql.append(replace(node.getSysLocation()));
	     sql.append("','");
	     sql.append(replace(node.getSysContact()));
	     sql.append("','");
	     if(node.getAlias()==null)
	        sql.append(replace(node.getSysName()));
	     else
	    	sql.append(replace(node.getAlias())); 
	     sql.append("','',0,'");//默认情况下不监视
	     sql.append(bridgeAddress);
	     sql.append("',");
	     sql.append(node.getStatus());
	     sql.append(",");
	     sql.append(node.getDiscoverstatus());
	     sql.append(",'");
	     sql.append(node.getWritecommunity());
	     sql.append("',");
	     sql.append(node.getSnmpversion());
	     sql.append(",");
	     sql.append(node.getOstype());
	     sql.append(",1");//默认情况下是SNMP采集方式
	     //sql.append(node.getCollecttype());
	     sql.append(",'");
	     sql.append(node.getBid());
	     sql.append("','");
	     sql.append("");
	     sql.append("','");
	     sql.append("");
	     sql.append("','");
	     sql.append("");
	     sql.append("')");
	     SysLogger.info(sql.toString());
	     conn.executeUpdate(sql.toString(),false);
	     donehost.put(node.getId(), node);
	     
	     //设置采集指标
	     //Host node = (Host)PollingEngine.getInstance().getNodeByIP(ipAddress);
	       //采集设备信息
	       try{
	    	   SysLogger.info("endpoint: "+node.getEndpoint()+"====collecttype: "+node.getCollecttype()+"====category: "+node.getCategory());
	    	   try{
//	    		   pingData(node);
//	    		   PingSnmp pingsnmp = new PingSnmp();
	    	   }catch(Exception ex){
	    		   ex.printStackTrace();
	    	   }
	    	   if(node.getEndpoint() == 2){
		   			//REMOTEPING的子节点，跳过
		   			//return;
		   		}else{
		   			if(node.getCategory() == 4){
		   				//初始化服务器采集指标和阀值
		   				SysLogger.info(node.getSysOid());
		   				if(node.getSysOid().startsWith("1.3.6.1.4.1.311.")){
		   					//windows服务器
		   					//阀值
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "windows");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				//采集指标
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "windows","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.2021") || node.getSysOid().startsWith("1.3.6.1.4.1.8072")){
		   					//LINUX服务器
		   					SysLogger.info(node.getSysOid()+"### 开始初始化采集指标 ###");
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "linux");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "linux","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("as400")){
		   					//AS400服务器
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "as400");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "as400","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}
		   				
		   			}else if(node.getCategory() < 4 || node.getCategory() == 7 || node.getCategory() == 8){
		   				//初始化网络设备采集指标
		   				if(node.getSysOid().startsWith("1.3.6.1.4.1.9.")){
		   					//cisco网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "cisco");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "cisco","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.25506.")){
		   					//h3c网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "h3c");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "h3c","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.2011.")){
		   					//h3c网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "h3c");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "h3c","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.4881.")){
		   					//锐捷网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "redgiant");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "redgiant","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.5651.")){
		   					//迈普网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "maipu");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "maipu","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.171.")){
		   					//DLink网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "dlink");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "dlink","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.2272.")){
		   					//北电网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "northtel");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "northtel","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.89.")){
		   					//RADWARE网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "radware");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "radware","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}else if(node.getSysOid().startsWith("1.3.6.1.4.1.3320.")){
		   					//博达网络设备
		   					try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "bdcom");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
			   				try {
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "bdcom","1");
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}
		   				}
		   			}
		   			
		   			
		   			
		   			//若只用PING TELNET SSH方式检测可用性,则性能数据不采集,跳过
		   			if(node.getCollecttype() == SystemConstant.COLLECTTYPE_PING ||
		   					node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT||
		   					node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
		   					//SysLogger.info("只PING TELNET SSH方式检测可用性,性能数据不采集,跳过");
		   			}else{
		   				//threadPool.runTask(createTask(node));
		   				if(node.getCategory() < 4 || node.getCategory() == 7){
		   					//collectNetData(node);
		   					PollDataUtil polldata = new PollDataUtil();
		   					polldata.collectNetData(node.getId()+"");
		   				}else if(node.getCategory() == 4){
		   					collectHostData(node);
		   				}		   				
		   			}
		   		}
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }
//	     if(node.getCategory() == 4){
//				//初始化服务器采集指标
//				if(node.getSysOid().startsWith("1.3.6.1.4.1.311.")){
//					//windows服务器
//					try {
//	   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//	   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_HOST, "windows");
//	   				} catch (RuntimeException e) {
//	   					// TODO Auto-generated catch block
//	   					e.printStackTrace();
//	   				}
//				}
//				
//			}else if(node.getCategory() < 4 || node.getCategory() == 7 || node.getCategory() == 8){
//				//初始化网络设备采集指标
//				if(node.getSysOid().startsWith("1.3.6.1.4.1.9.")){
//					//cisco网络设备
//					try {
//	   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//	   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "cisco");
//	   				} catch (RuntimeException e) {
//	   					// TODO Auto-generated catch block
//	   					e.printStackTrace();
//	   				}
//				}
//			}
	     
	     
	     
	     
	     
	     //加入连通性检测类型表,若是PING检测则不加入
	     	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT||
					node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
	     		StringBuffer configsql = new StringBuffer(200);
		     	configsql.append("insert into nms_connecttypeconfig(node_id,connecttype,username,password,login_prompt,password_prompt,shell_prompt)" +
						"values('");
		     	configsql.append(node.getId());
		     	configsql.append("','");
		     	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT){
		     		configsql.append("telnet");
		     	}else if(node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
		     		configsql.append("ssh");
		     	}else
		     		configsql.append("ping");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("')");
		     	SysLogger.info(configsql.toString());
		     	conn.executeUpdate(configsql.toString(),false);
	     	}
	     
	     
	     NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
	     NetSyslogNodeRuleDao netlog = new NetSyslogNodeRuleDao();
	     try{
		    String strFacility = "";
			List rulelist = new ArrayList();
			try{
				rulelist = ruledao.loadAll();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				ruledao.close();
			}
			if(rulelist != null && rulelist.size()>0){
				NetSyslogRule logrule = (NetSyslogRule)rulelist.get(0);
				strFacility = logrule.getFacility();
			}
		    
		    String strSql = "";
		    strSql = "insert into nms_netsyslogrule_node(nodeid,facility)values('"+node.getId()+"','"+strFacility+"')";
		    try{
		    	SysLogger.info(strSql);
		    	netlog.saveOrUpdate(strSql);
		    }catch(Exception e){
		    	e.printStackTrace();
		    }finally{
		    	netlog.close();
		    }
	     }catch(Exception e){
	    	 e.printStackTrace();
	     }finally{
	    	 ruledao.close();
	    	 netlog.close();
	     }	     
		 }catch(Exception e){
			 e.printStackTrace();
			 continue;
		 }
      }           
      conn.commit();
      conn.executeUpdate("update topo_host_node a set a.type=(select b.descr  from nms_device_type b where a.sys_oid=b.sys_oid)");
      //加入网络设备IP的别名
      donehost = new Hashtable();
      for(int i=0;i<hostList.size();i++){
    	  try{
    		  Host node = (Host)hostList.get(i);
    		  if(donehost.containsKey(node.getId()))continue;
    	      if(node.getAliasIfEntitys() != null && node.getAliasIfEntitys().size()>0){
    	    	  for(int k =0;k<node.getAliasIfEntitys().size();k++){
    	    		  IfEntity ifEntity = (IfEntity)node.getAliasIfEntitys().get(k);
    	    		  //String aliasip = (String)node.getAliasIPs().get(k);
    	    	      StringBuffer sql = new StringBuffer(300);
    	    	      
    	    	 	  sql.append("insert into topo_ipalias(ipaddress,aliasip,indexs,descr,speeds,types) values('");
    	    	 	 if(node.getAdminIp() != null && node.getAdminIp().trim().length()>0){
    	    	 		sql.append(node.getAdminIp());
    	    	 		//过滤掉管理地址和别名IP相同的记录
    	    	 		//SysLogger.info(node.getAdminIp()+"----"+aliasip);
    	    	 		if(node.getAdminIp().equalsIgnoreCase(ifEntity.getIpAddress()))continue;
    	    	 	 }else{
    	    	 		//过滤掉管理地址和别名IP相同的记录
    	    	 		sql.append(node.getIpAddress());
    	    	 		//SysLogger.info(node.getIpAddress()+"----"+aliasip);
    	    	 		if(node.getIpAddress().equalsIgnoreCase(ifEntity.getIpAddress()))continue;
    	    	 	 }
    	    	 	  
    	    	 	 sql.append("','");
    	    	 	 sql.append(ifEntity.getIpAddress());
    	    	 	 sql.append("','");
      	    	 	 sql.append(ifEntity.getIndex());
      	    	 	 sql.append("','");
      	    	 	 sql.append(ifEntity.getDescr());
      	    	 	 sql.append("','");
      	    	 	 sql.append(ifEntity.getSpeed());
      	    	 	 sql.append("',");
      	    	 	 sql.append(ifEntity.getType()); 
    	    	 	 
    	    	 	 sql.append(")");
    	    	     SysLogger.info(sql.toString());
    	    	     conn.executeUpdate(sql.toString(),false);
    	    	     donehost.put(node.getId(), node);
    	    	  }   	  
    	      }
    	    	 
    	  }catch(Exception e){
    		  e.printStackTrace();
    	  }
      }
      IpAliasDao ipaliasdao = new IpAliasDao();
      try{
    	  ipaliasdao.RefreshIpAlias();
      }catch(Exception e){
    	  e.printStackTrace();
      }finally{
    	  ipaliasdao.close();
      }
   }
   
   /**
    * 手工加入主机数据,同时加入网络设备IP的别名
    */   
   public synchronized void addHostDataByHand(List hostList)
   {    
	   SysLogger.info("运行: 开始手工加入设备...");
	   //
	   
	   Hashtable donehost = new Hashtable();
	  for(int i=0;i<hostList.size();i++)
      {
		 try{
		 
		 hostList.get(i).toString();
    	 Host node = (Host)hostList.get(i);
    	 
    	 
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
         
         Hashtable nodelistHash = new Hashtable();
         try {
         	List hostlist = PollingEngine.getInstance().getNodeList();
             if (hostlist != null && hostlist.size() > 0) {
                 for (int k = 0; k < hostlist.size(); k++) {
                 	Host _node = (Host) hostlist.get(k);
                     //SysLogger.info("====put "+_node.getIpAddress()+" to nodelistHash ====");
                     nodelistHash.put(_node.getIpAddress(), _node.getIpAddress());
                 }
             }
         } catch (Exception e) {

         }
         
  	   if(ipaliasHash.containsKey(node.getIpAddress())){
         	//已经存在该设备
         	String nodeIp = "";
             if(nodelistHash.containsKey(node.getIpAddress()))nodeIp = (String)nodelistHash.get(node.getIpAddress());
             if(nodeIp == null || nodeIp.trim().length()==0){
             	if(ipaliasHash.containsKey(node.getIpAddress()))nodeIp = (String)ipaliasHash.get(node.getIpAddress());
             }
             com.afunms.polling.node.Host  _host = null;
             if(nodeIp != null && nodeIp.trim().length()>0){
             	_host = (com.afunms.polling.node.Host) PollingEngine.getInstance().getNodeByIp(nodeIp);
             	if(_host != null){
             		//只计算链路关系
             		continue;
             	}
             }
         }
    	 
    	 
    	 if(donehost.containsKey(node.getId()))continue;
    	 
			//测试生成表
			String ip = node.getIpAddress();
			String allipstr = SysUtil.doip(ip);
			CreateTableManager ctable = new CreateTableManager();
			//SysLogger.info(node.getIpAddress()+"======category:"+node.getCategory()+"==="+node.getDiscoverstatus());
			if ((node.getCategory()>0 && node.getCategory()< 4) || node.getCategory() == 7 || node.getCategory() == 8 || node.getCategory() == 9 || node.getCategory() == 10|| node.getCategory() == 11){
				if(node.getDiscoverstatus() == -1){
					//新发现的设备
					//生成网络设备表	
					if(node.getCollecttype()==3){ 
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					}else{
						ctable.createTable(conn,"ping",allipstr,"ping");//Ping
						ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
						ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
						
						
						ctable.createTable(conn,"memory",allipstr,"mem");//内存
						ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
						ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
						
						ctable.createTable(conn,"flash",allipstr,"flash");//闪存
						ctable.createTable(conn,"flashhour",allipstr,"flashhour");//闪存
						ctable.createTable(conn,"flashday",allipstr,"flashday");//闪存
						
						ctable.createTable(conn,"buffer",allipstr,"buffer");//缓存
						ctable.createTable(conn,"bufferhour",allipstr,"bufferhour");//缓存
						ctable.createTable(conn,"bufferday",allipstr,"bufferday");//缓存
						
						ctable.createTable(conn,"fan",allipstr,"fan");//风扇
						ctable.createTable(conn,"fanhour",allipstr,"fanhour");//风扇
						ctable.createTable(conn,"fanday",allipstr,"fanday");//风扇
						
						ctable.createTable(conn,"power",allipstr,"power");//电源
						ctable.createTable(conn,"powerhour",allipstr,"powerhour");//电源
						ctable.createTable(conn,"powerday",allipstr,"powerday");//电源
						
						ctable.createTable(conn,"vol",allipstr,"vol");//电压
						ctable.createTable(conn,"volhour",allipstr,"volhour");//电压
						ctable.createTable(conn,"volday",allipstr,"volday");//电压
						
						ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
						ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
						ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
						
						ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");
						ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
						ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
						
						ctable.createTable(conn,"portstatus",allipstr,"port");
					//	ctable.createTable(conn,"portstatushour",allipstr,"porthour");
					//	ctable.createTable(conn,"portstatusday",allipstr,"portday");
						
						ctable.createTable(conn,"utilhdx",allipstr,"hdx");
						ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
						ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
						
						ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
						ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
						ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
						
						ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
						ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
						ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
						
						ctable.createTable(conn,"errorsperc",allipstr,"errperc");
						ctable.createTable(conn,"errperch",allipstr,"errperch");
						ctable.createTable(conn,"errpercd",allipstr,"errpercd");
						
						ctable.createTable(conn,"packs",allipstr,"packs");	
						ctable.createTable(conn,"packshour",allipstr,"packshour");
						ctable.createTable(conn,"packsday",allipstr,"packsday");
						
						ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
						ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
						ctable.createTable(conn,"ipackd",allipstr,"ipackd");
						
						ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
						ctable.createTable(conn,"opackh",allipstr,"opackh");
						ctable.createTable(conn,"opacksd",allipstr,"opacksd");
						
						ctable.createTable(conn,"temper",allipstr,"temper");	
						ctable.createTable(conn,"temperh",allipstr,"temperh");
						ctable.createTable(conn,"temperd",allipstr,"temperd");
					}
				}
			}else if(node.getCategory() == 12){
				//生成VPN设备表	
				if(node.getCollecttype()==3){ 
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				}else{
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					
					
					ctable.createTable(conn,"memory",allipstr,"mem");//内存
					ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
					ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
					
					ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
					ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
					ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
					
					ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");
					ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
					ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
					
					ctable.createTable(conn,"portstatus",allipstr,"port");
				//	ctable.createTable(conn,"portstatushour",allipstr,"porthour");
				//	ctable.createTable(conn,"portstatusday",allipstr,"portday");
					
					ctable.createTable(conn,"utilhdx",allipstr,"hdx");
					ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
					ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
					
					ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
					ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
					ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
					
					ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
					ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
					ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
					
					ctable.createTable(conn,"errorsperc",allipstr,"errperc");
					ctable.createTable(conn,"errperch",allipstr,"errperch");
					ctable.createTable(conn,"errpercd",allipstr,"errpercd");
					
					ctable.createTable(conn,"packs",allipstr,"packs");	
					ctable.createTable(conn,"packshour",allipstr,"packshour");
					ctable.createTable(conn,"packsday",allipstr,"packsday");
					
					ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
					ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
					ctable.createTable(conn,"ipackd",allipstr,"ipackd");
					
					ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
					ctable.createTable(conn,"opackh",allipstr,"opackh");
					ctable.createTable(conn,"opacksd",allipstr,"opacksd");					
				}
			}else if(node.getCategory() == 13){
				//生成CMTS设备表	
				if(node.getCollecttype()==3){ 
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				}else{
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					
					ctable.createTable(conn,"status",allipstr,"status");//通道状态
					ctable.createTable(conn,"statushour",allipstr,"statushour");//通道状态按小时
					ctable.createTable(conn,"statusday",allipstr,"statusday");//通道状态按天
					
					ctable.createCiscoCMTSTable(conn,"noise",allipstr,"noise");//通道信躁比
					ctable.createCiscoCMTSTable(conn,"noisehour",allipstr,"noisehour");//通道信躁比按小时
					ctable.createCiscoCMTSTable(conn,"noiseday",allipstr,"noiseday");//通道信躁比按天
					
					ctable.createCiscoCMTSIPMACTable(conn,"ipmac",allipstr,"ipmac");//IPMAC信息（在线用户信息）
					
					ctable.createTable(conn,"utilhdxpercs",allipstr,"hdpercs");
					ctable.createTable(conn,"hdxperchours",allipstr,"hdperchours");
					ctable.createTable(conn,"hdxpercdays",allipstr,"hdpercdays");	
					
					
					ctable.createTable(conn,"utilhdxs",allipstr,"hdxs");
					ctable.createTable(conn,"utilhdxhours",allipstr,"hdxhours");
					ctable.createTable(conn,"utilhdxdays",allipstr,"hdxdays");	
					
					ctable.createTable(conn,"allutilhdxs",allipstr,"allhdxs");
					ctable.createTable(conn,"autilhdxhs",allipstr,"ahdxhs");
					ctable.createTable(conn,"autilhdxds",allipstr,"ahdxds");
					
					ctable.createTable(conn,"discardspercs",allipstr,"dcardpercs");
					ctable.createTable(conn,"dcarperhs",allipstr,"dcarperhs");
					ctable.createTable(conn,"dcarperds",allipstr,"dcarperds");
					
					ctable.createTable(conn,"errorspercs",allipstr,"errpercs");
					ctable.createTable(conn,"errperchs",allipstr,"errperchs");
					ctable.createTable(conn,"errpercds",allipstr,"errpercds");
					
					ctable.createTable(conn,"packss",allipstr,"packss");	
					ctable.createTable(conn,"packshours",allipstr,"packshours");
					ctable.createTable(conn,"packsdays",allipstr,"packsdays");
					
					ctable.createTable(conn,"inpackss",allipstr,"inpackss");	
					ctable.createTable(conn,"ipackshs",allipstr,"ipackshs");
					ctable.createTable(conn,"ipackds",allipstr,"ipackds");
					
					ctable.createTable(conn,"outpackss",allipstr,"outpackss");	
					ctable.createTable(conn,"opackhs",allipstr,"opackhs");
					ctable.createTable(conn,"opacksds",allipstr,"opacksds");					
				}
			}else if(node.getCategory() == 14){
				//生成存储设备表	
				
				if(node.getCollecttype()==3){ 
					ctable.createTable(conn,"pings",allipstr,"pings");//Ping
					ctable.createTable(conn,"pinghours",allipstr,"pinghours");//Ping
					ctable.createTable(conn,"pingdays",allipstr,"pingdays");//Ping
				}else if(node.getCollecttype()==7||(node.getSysOid() != null && node.getSysOid().trim().startsWith("1.3.6.1.4.1.11.2.3.7.11")))
				{
					//hp storage
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				}				
				else if(node.getSysOid() != null && node.getSysOid().trim().startsWith("1.3.6.1.4.1.789.")){
					//NETAPP存储
					ctable.createTable(conn,"pings",allipstr,"pings");//Ping
					ctable.createTable(conn,"pingshour",allipstr,"pingshour");//Ping
					ctable.createTable(conn,"pingsday",allipstr,"pingsday");//Ping
					
					ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
					ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
					ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
					
					ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
					ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
					ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
					
	
					ctable.createTable(conn,"utilhdx",allipstr,"hdx");
					ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
					ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
					
					ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
					ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
					ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
					
					ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
					ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
					ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
					
					ctable.createTable(conn,"errorsperc",allipstr,"errperc");
					ctable.createTable(conn,"errperch",allipstr,"errperch");
					ctable.createTable(conn,"errpercd",allipstr,"errpercd");
					
					ctable.createTable(conn,"packs",allipstr,"packs");	
					ctable.createTable(conn,"packshour",allipstr,"packshour");
					ctable.createTable(conn,"packsday",allipstr,"packsday");
					
					ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
					ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
					ctable.createTable(conn,"ipackd",allipstr,"ipackd");
					
					ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
					ctable.createTable(conn,"opackh",allipstr,"opackh");
					ctable.createTable(conn,"opacksd",allipstr,"opacksd");
				}else if(node.getSysOid() != null && !node.getSysOid().trim().startsWith("1.3.6.1.4.1.11.2.3.7.11")
						&& !node.getSysOid().trim().startsWith("1.3.6.1.4.1.789."))
				{
					ctable.createTable(conn,"pings",allipstr,"pings");//Ping
					ctable.createTable(conn,"pinghours",allipstr,"pinghours");//Ping
					ctable.createTable(conn,"pingdays",allipstr,"pingdays");//Ping
					
					ctable.createTable(conn,"env",allipstr,"env");//存储设备环境-风扇\电源\环境状态\驱动状态					
					
					ctable.createTable(conn,"efan",allipstr,"efan");//存储设备环境-风扇					
					ctable.createTable(conn,"epower",allipstr,"epower");//存储设备环境-电源					
					ctable.createTable(conn,"eenv",allipstr,"eenv");//存储设备环境-环境状态
					ctable.createTable(conn,"edrive",allipstr,"edrive");//存储设备环境-驱动状态					
					
					ctable.createTable(conn,"rcable",allipstr,"rcable");//运行状体：内部总线状态					
					ctable.createTable(conn,"rcache",allipstr,"rcache");//运行状体：缓存状态					
					ctable.createTable(conn,"rmemory",allipstr,"rmemory");//运行状体：共享内存状态
					ctable.createTable(conn,"rpower",allipstr,"rpower");//运行状体：电源状态
					ctable.createTable(conn,"rbutter",allipstr,"rbutter");//运行状体：电池状态					
					ctable.createTable(conn,"rfan",allipstr,"rfan");//运行状体：风扇状态					
					ctable.createTable(conn,"renv",allipstr,"renv");//存储设备环境-环境状态
					ctable.createTable(conn,"rluncon",allipstr,"rluncon");
					ctable.createTable(conn,"rsluncon",allipstr,"rsluncon");
					ctable.createTable(conn,"rwwncon",allipstr,"rwwncon");	
					ctable.createTable(conn,"rsafety",allipstr,"rsafety");
					ctable.createTable(conn,"rnumber",allipstr,"rnumber");
					ctable.createTable(conn,"rswitch",allipstr,"rswitch");	
					ctable.createTable(conn,"rcpu",allipstr,"rcpu");	
					
					ctable.createTable(conn,"events",allipstr,"events");//事件
					
					ctable.createEmcTable(conn,"emcdiskper",allipstr,"emcdiskper");
					ctable.createEmcTable(conn,"emclunper",allipstr,"emclunper");
					ctable.createEmcTable(conn,"emcenvpower",allipstr,"emcenvpower");
					ctable.createEmcTable(conn,"emcenvstore",allipstr,"emcenvstore");
					ctable.createEmcTable(conn,"emcbakpower",allipstr,"emcbakpower");
					
//					ctable.createTable(conn,"utilhdxpercs",allipstr,"hdpercs");
//					ctable.createTable(conn,"hdxperchours",allipstr,"hdperchours");
//					ctable.createTable(conn,"hdxpercdays",allipstr,"hdpercdays");	
					
					
//					ctable.createTable(conn,"utilhdxs",allipstr,"hdxs");
//					ctable.createTable(conn,"utilhdxhours",allipstr,"hdxhours");
//					ctable.createTable(conn,"utilhdxdays",allipstr,"hdxdays");	
//					
//					ctable.createTable(conn,"allutilhdxs",allipstr,"allhdxs");
//					ctable.createTable(conn,"autilhdxhs",allipstr,"ahdxhs");
//					ctable.createTable(conn,"autilhdxds",allipstr,"ahdxds");
//					
//					ctable.createTable(conn,"discardspercs",allipstr,"dcardpercs");
//					ctable.createTable(conn,"dcarperhs",allipstr,"dcarperhs");
//					ctable.createTable(conn,"dcarperds",allipstr,"dcarperds");
//					
//					ctable.createTable(conn,"errorspercs",allipstr,"errpercs");
//					ctable.createTable(conn,"errperchs",allipstr,"errperchs");
//					ctable.createTable(conn,"errpercds",allipstr,"errpercds");
//					
//					ctable.createTable(conn,"packss",allipstr,"packss");	
//					ctable.createTable(conn,"packshours",allipstr,"packshours");
//					ctable.createTable(conn,"packsdays",allipstr,"packsdays");
//					
//					ctable.createTable(conn,"inpackss",allipstr,"inpackss");	
//					ctable.createTable(conn,"ipackshs",allipstr,"ipackshs");
//					ctable.createTable(conn,"ipackds",allipstr,"ipackds");
//					
//					ctable.createTable(conn,"outpackss",allipstr,"outpackss");	
//					ctable.createTable(conn,"opackhs",allipstr,"opackhs");
//					ctable.createTable(conn,"opacksds",allipstr,"opacksds");					
				}
			}else if(node.getCategory() == 15){
				//生成VMWare表	
				if(node.getCollecttype()==3){ 
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				}else{
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
					
					ctable.createTable(conn,"memory",allipstr,"memory");//内存利用率	
					ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
					ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
					
					ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
					ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
					ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
					
					ctable.createTable(conn,"state",allipstr,"state");//虚拟机电源状况（打开或关闭）。 
					ctable.createTable(conn,"gstate",allipstr,"gstate");//客户机操作系统的状况（开或关）。 
					ctable.createVMhostTable(conn,"vm_host",allipstr,"vm_host");//创建VMWare 物理机的性能信息表
					ctable.createVMguesthostTable(conn,"vm_guesthost",allipstr,"vm_guesthost");//创建VMWare 虚拟机的性能信息表
					ctable.createVMCRTable(conn,"vm_cluster",allipstr,"vm_cluster");//创建VMWare 集群的性能信息表
					ctable.createVMDSTable(conn,"vm_datastore",allipstr,"vm_datastore");//创建VMWare 存储的性能信息表
					ctable.createVMRPTable(conn,"vm_resourcepool",allipstr,"vm_resourcepool");//创建VMWare 资源池的性能信息表
					//vm_basephysical
					ctable.createVMBaseTable(conn,"vm_basephysical",allipstr,"vm_basephysical");//创建VMWare 物理机的基础信息表
					ctable.createVMBaseTable(conn,"vm_basevmware",allipstr,"vm_basevmware");//创建VMWare 虚拟机的基础信息表
					ctable.createVMBaseTable(conn,"vm_baseyun",allipstr,"vm_baseyun");//创建VMWare 云资源的基础信息表
					ctable.createVMBaseTable(conn,"vm_basedatastore",allipstr,"vm_basedatastore");//创建VMWare 存储的基础信息表
					ctable.createVMBaseTable(conn,"vm_basedatacenter",allipstr,"vm_basedatacenter");//创建VMWare 数据中心的基础信息表
					ctable.createVMBaseTable(conn,"vm_baseresource",allipstr,"vm_baseresource");//创建VMWare 资源池的基础信息表
					
				}
			}else if(node.getCategory() == 4){
				//主机服务器
				//生成主机表	
				if(node.getCollecttype()==3){ 
					ctable.createTable(conn,"ping",allipstr,"ping");//Ping
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
					ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				}else{
					ctable.createTable(conn,"ping",allipstr,"ping");
					ctable.createTable(conn,"pinghour",allipstr,"pinghour");
					ctable.createTable(conn,"pingday",allipstr,"pingday");
					
					/*
					ctable.createTable("disk",allipstr,"disk");
					ctable.createTable("diskhour",allipstr,"diskhour");
					ctable.createTable("diskday",allipstr,"diskday");
					*/
					ctable.createTable(conn,"pro",allipstr,"pro");//进程
					ctable.createTable(conn,"prohour",allipstr,"prohour");//进程小时
					ctable.createTable(conn,"proday",allipstr,"proday");//进程天
					
					ctable.createSyslogTable(conn,"log",allipstr,"log");//进程天
					
					ctable.createTable(conn,"memory",allipstr,"mem");//内存
					ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
					ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
					
					ctable.createTable(conn,"cpu",allipstr,"cpu");//CPU
					ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
					ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
					
					ctable.createTable(conn,"cpudtl",allipstr,"cpudtl");	
					ctable.createTable(conn,"cpudtlhour",allipstr,"cpudtlhour");
					ctable.createTable(conn,"cpudtlday",allipstr,"cpudtlday");
					
					ctable.createTable(conn,"disk",allipstr,"disk");//yangjun
					ctable.createTable(conn,"diskhour",allipstr,"diskhour");
					ctable.createTable(conn,"diskday",allipstr,"diskday");
					
					ctable.createTable(conn,"diskincre",allipstr,"diskincre");//磁盘增长率yangjun
					ctable.createTable(conn,"diskinch",allipstr,"diskinch");//磁盘增长率小时
					ctable.createTable(conn,"diskincd",allipstr,"diskincd");//磁盘增长率天 
	
					ctable.createTable(conn,"utilhdxperc",allipstr,"hdperc");					
					ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
					ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
					
	
					ctable.createTable(conn,"utilhdx",allipstr,"hdx");
					ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
					ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
					
					ctable.createTable(conn,"allutilhdx",allipstr,"allhdx");
					ctable.createTable(conn,"autilhdxh",allipstr,"ahdxh");
					ctable.createTable(conn,"autilhdxd",allipstr,"ahdxd");
					
					ctable.createTable(conn,"discardsperc",allipstr,"dcardperc");
					ctable.createTable(conn,"dcarperh",allipstr,"dcarperh");
					ctable.createTable(conn,"dcarperd",allipstr,"dcarperd");
					
					ctable.createTable(conn,"errorsperc",allipstr,"errperc");
					ctable.createTable(conn,"errperch",allipstr,"errperch");
					ctable.createTable(conn,"errpercd",allipstr,"errpercd");
					
					ctable.createTable(conn,"packs",allipstr,"packs");	
					ctable.createTable(conn,"packshour",allipstr,"packshour");
					ctable.createTable(conn,"packsday",allipstr,"packsday");
					
					ctable.createTable(conn,"inpacks",allipstr,"inpacks");	
					ctable.createTable(conn,"ipacksh",allipstr,"ipacksh");
					ctable.createTable(conn,"ipackd",allipstr,"ipackd");
					
					ctable.createTable(conn,"outpacks",allipstr,"outpacks");	
					ctable.createTable(conn,"opackh",allipstr,"opackh");
					ctable.createTable(conn,"opacksd",allipstr,"opacksd");
					
					ctable.createTable(conn,"software",allipstr,"software");
//					System.out.println("com.afunms.topology.dao---->DiscoverCompleteDao.java----1502hang------==>"+node.getSysOid());
					if(node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.2.3.1.2.1.1")){
						//生成换页率
						ctable.createTable(conn,"pgused",allipstr,"pgused");	
						ctable.createTable(conn,"pgusedhour",allipstr,"pgusedhour");
						ctable.createTable(conn,"pgusedday",allipstr,"pgusedday");
					}
				}
			}
            else if(node.getCategory() == 16)
			{// 中电国际艾默生空调添加的类型
				ctable.createTable(conn,"ping",allipstr,"ping");//Ping
				ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping
				ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
			}
			else if(node.getCategory() == 17)
			{
				ctable.createTable(conn,"ping",allipstr,"ping");//Ping				
				ctable.createTable(conn,"pinghour",allipstr,"pinghour");//Ping				
				ctable.createTable(conn,"pingday",allipstr,"pingday");//Ping
				ctable.createTable(conn,"input",allipstr,"input");//input			
				ctable.createTable(conn,"inputhour",allipstr,"inputhour");//input			
				ctable.createTable(conn,"inputday",allipstr,"inputday");//input			
				ctable.createTable(conn,"output",allipstr,"output");//output				
				ctable.createTable(conn,"outputhour",allipstr,"outputhour");//output				
				ctable.createTable(conn,"outputday",allipstr,"outputday");//output
			}
			
		 String bridgeAddress = node.getBridgeAddress();
		 if(bridgeAddress != null && !"".equalsIgnoreCase(bridgeAddress))
		 {
			 bridgeAddress = CommonUtil.removeIllegalStr(bridgeAddress.replace("'", "").replace("<", "").replace(">", ""));
		 }
    	 
		 StringBuffer sql = new StringBuffer(300);
	     sql.append("insert into topo_host_node(id,asset_id,location,ip_address,ip_long,net_mask,category,community,sys_oid,sys_name,super_node,");
	     sql.append("local_net,layer,sys_descr,sys_location,sys_contact,alias,type,managed,bridge_address,status,discoverstatus,write_community,snmpversion,ostype,transfer,collecttype,bid,sendemail,sendmobiles,sendphone,supperid,");
	     sql.append("securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase)values(");
	     sql.append(node.getId());
	     sql.append(",'");
	     sql.append(node.getAssetid());
	     sql.append("','");
	     sql.append(node.getLocation());
	     sql.append("','");
	     sql.append(node.getIpAddress());
	     sql.append("',");
	     sql.append(NetworkUtil.ip2long(node.getIpAddress()));
	     sql.append(",'");
	     sql.append(node.getNetMask());
	     sql.append("',");
	     sql.append(node.getCategory());
	     sql.append(",'");
	     sql.append(node.getCommunity());
	     sql.append("','");
	     sql.append(node.getSysOid());
	     sql.append("','");
         sql.append(replace(node.getSysName()));	     
	     sql.append("',");
	     sql.append(node.getSuperNode());
	     sql.append(",");
	     sql.append(node.getLocalNet());
	     sql.append(",");
	     sql.append(node.getLayer());
	     sql.append(",'");
	     sql.append(replace(node.getSysDescr()));
	     sql.append("','");
	     sql.append(replace(node.getSysLocation()));
	     sql.append("','");
	     sql.append(replace(node.getSysContact()));
	     sql.append("','");
	     if(node.getAlias()==null)
	        sql.append(replace(node.getSysName()));
	     else
	    	sql.append(replace(node.getAlias())); 
	     sql.append("','");
	     sql.append(node.getType());
	     sql.append("',");
	     if(node.isManaged()){
	    	 sql.append("1,'");
	     }else{
	    	 sql.append("0,'");
	     }
	     
	     
	     if(null!=bridgeAddress && bridgeAddress.length() >60)
	     {
	    	 bridgeAddress=""; 
	     }
	     
	     sql.append(bridgeAddress);
	     sql.append("',");
	     sql.append(node.getStatus());
	     sql.append(",");
	     sql.append(node.getDiscoverstatus());
	     sql.append(",'");
	     sql.append(node.getWritecommunity());
	     sql.append("',");
	     sql.append(node.getSnmpversion());
	     sql.append(",");
	     sql.append(node.getOstype());
	     sql.append(",");
	     sql.append(node.getTransfer());
	     sql.append(",");
	     sql.append(node.getCollecttype());
	     sql.append(",'");
	     sql.append(node.getBid());
	     sql.append("','");
	     sql.append(node.getSendemail());
	     sql.append("','");
	     sql.append(node.getSendmobiles());
	     sql.append("','");
	     sql.append(node.getSendphone());
	     sql.append("','");
	     sql.append(node.getSupperid());
	     sql.append("',");
	     sql.append(node.getSecuritylevel());
	     sql.append(",'");
	     sql.append(node.getSecurityName());
	     sql.append("',");
	     sql.append(node.getV3_ap());
	     sql.append(",'");
	     sql.append(node.getAuthpassphrase());
	     sql.append("',");
	     sql.append(node.getV3_privacy());
	     sql.append(",'");
	     sql.append(node.getPrivacyPassphrase());
	     sql.append("')");
	     SysLogger.info(sql.toString());
	     //conn.addBatch(sql.toString(),false);
	     conn.addBatch(sql.toString());
	     	donehost.put(node.getId(), node);
	     	
	     	//加入连通性检测类型表,若是PING检测则不加入
	     	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT||
					node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
	     		StringBuffer configsql = new StringBuffer(200);
		     	configsql.append("insert into nms_connecttypeconfig(node_id,connecttype,username,password,login_prompt,password_prompt,shell_prompt)" +
						"values('");
		     	configsql.append(node.getId());
		     	configsql.append("','");
		     	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT){
		     		configsql.append("telnet");
		     	}else if(node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
		     		configsql.append("ssh");
		     	}else
		     		configsql.append("ping");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("','");
		     	configsql.append("");
		     	configsql.append("')");
		     	//SysLogger.info(configsql.toString());
		     	//conn.addBatch(sql.toString(),false);
		     	conn.addBatch(sql.toString());
	     	} 
		 }catch(Exception e){
			 e.printStackTrace();
			 continue;
		 }
      } 
		try{
			conn.executeBatch();
		}catch(Exception e){
			e.printStackTrace();
		}
      conn.commit();
      conn.addBatch("update topo_host_node a set a.type=(select b.descr  from nms_device_type b where a.sys_oid=b.sys_oid)");
      //加入网络设备IP的别名
      donehost = new Hashtable();
      for(int i=0;i<hostList.size();i++){
    	  try{
    		  Host node = (Host)hostList.get(i);
    	      if(donehost.containsKey(node.getId()))continue;
    	      if(node.getAliasIfEntitys() != null && node.getAliasIfEntitys().size()>0){
    	    	  for(int k =0;k<node.getAliasIfEntitys().size();k++){
    	    		  IfEntity ifEntity = (IfEntity)node.getAliasIfEntitys().get(k);
    	    		  //String aliasip = (String)node.getAliasIPs().get(k);
    	    	      StringBuffer sql = new StringBuffer(300);
    	    	      
    	    	 	  sql.append("insert into topo_ipalias(ipaddress,aliasip,indexs,descr,speeds,types) values('");
    	    	 	 if(node.getAdminIp() != null && node.getAdminIp().trim().length()>0){
    	    	 		sql.append(node.getAdminIp());
    	    	 		//过滤掉管理地址和别名IP相同的记录
    	    	 		//SysLogger.info(node.getAdminIp()+"----"+aliasip);
    	    	 		if(node.getAdminIp().equalsIgnoreCase(ifEntity.getIpAddress()))continue;
    	    	 	 }else{
    	    	 		//过滤掉管理地址和别名IP相同的记录
    	    	 		sql.append(node.getIpAddress());
    	    	 		//SysLogger.info(node.getIpAddress()+"----"+aliasip);
    	    	 		if(node.getIpAddress().equalsIgnoreCase(ifEntity.getIpAddress()))continue;
    	    	 	 }
    	    	 	  
    	    	 	 sql.append("','");
    	    	 	 sql.append(ifEntity.getIpAddress());
    	    	 	 sql.append("','");
      	    	 	 sql.append(ifEntity.getIndex());
      	    	 	 sql.append("','");
      	    	 	 sql.append(ifEntity.getDescr());
      	    	 	 sql.append("','");
      	    	 	 sql.append(ifEntity.getSpeed());
      	    	 	 sql.append("',");
      	    	 	 sql.append(ifEntity.getType()); 
    	    	 	 
    	    	 	 sql.append(")");
    	    	     //SysLogger.info(sql.toString());
    	    	     //conn.addBatch(sql.toString(),false);
    	    	 	conn.addBatch(sql.toString());
    	    	     donehost.put(node.getId(), node);
    	    	  }   	  
    	      }
    	    	 
    	  }catch(Exception e){
    		  e.printStackTrace();
    	  }
      }
		try{
			conn.executeBatch();
		}catch(Exception e){
			
		}
	      IpAliasDao ipaliasdao = new IpAliasDao();
	      try{
	    	  ipaliasdao.RefreshIpAlias();
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }finally{
	    	  ipaliasdao.close();
	      }
   }   
   
   /**
    * 加入子网数据
    */   
   public void addSubNetData(List netList)
   {
	  if(netList==null) return;
	   
      for(int i=0;i<netList.size();i++)
      {
    	 SubNet subnet = (SubNet)netList.get(i);
    	 StringBuffer sql = new StringBuffer(200);
    	 sql.append("insert into topo_subnet(id,net_address,net_mask,net_long,managed)values(");
    	 sql.append(subnet.getId());
    	 sql.append(",'");
    	 sql.append(subnet.getNetAddress());
    	 sql.append("','");
    	 sql.append(subnet.getNetMask());
    	 sql.append("',");
    	 sql.append(NetworkUtil.ip2long(subnet.getNetAddress()));
    	 sql.append(",1)");
    	 conn.executeUpdate(sql.toString(),false);
      }
      conn.commit(); 
   }
   
   /**
    * 加入监视器数据
    */      
   public void addMonitor(List hostList)
   {     
      for(int i=0;i<hostList.size();i++)
      {
    	 Host node = (Host)hostList.get(i);
    	 try
    	 {            
    		 //addPingMonitor(node.getId());
    		 //System.out.println("sysoid====="+node.getSysOid());    	     
    		 if(node.getCategory()==4){
    			 addMonitor(node.getId(),node.getIpAddress(),"host");
    		 }
    			//addServiceMonitor(node.getId());
    		 if(node.getCategory() <4){
    			 //网络设备
    			 addMonitor(node.getId(),node.getIpAddress(),"net");
    		 }
    		 /*
    		 if(node.getCategory()<5)  //1,2,3,4 加入接口监视器
	            addMonitor(node.getId(),node.getIpAddress(),"traffic");
	        
	         if(node.getSysOid().startsWith("1.3.6.1.4.1.311.")) //windows
	    	    addMonitor(node.getId(),node.getIpAddress(),"windows");
	         else if(node.getSysOid().startsWith("1.3.6.1.4.1.9.")) //cisco
		        addMonitor(node.getId(),node.getIpAddress(),"cisco");
	         
	         else if(node.getSysOid().startsWith("1.3.6.1.4.1.25506")) //huawei
	         {
	        	 addMonitor(node.getId(),node.getIpAddress(),"huawei");	        	 
	         }
	         else if(node.getSysOid().startsWith("1.3.6.1.4.1.2.")) //ibm_aix
	    	    addMonitor(node.getId(),node.getIpAddress(),"aix");
	         else if(node.getSysOid().startsWith("1.3.6.1.4.1.42.")) //sun_solaris
	    	    addMonitor(node.getId(),node.getIpAddress(),"solaris");  //linux
	         else if(node.getSysOid().equals("1.3.6.1.4.1.2021.250.10")||
	        		node.getSysOid().equals("1.3.6.1.4.1.8072.3.2.10")) 
		        addMonitor(node.getId(),node.getIpAddress(),"linux");
	         else if(node.getSysOid().startsWith("1.3.6.1.4.1.11.2.3."))
	            addMonitor(node.getId(),node.getIpAddress(),"hp-ux");
	         else if(node.getSysOid().startsWith("1.3.6.1.4.1.36."))
	        	addMonitor(node.getId(),node.getIpAddress(),"tru64");
	         */
	         
    	 }
    	 catch(Exception e)
    	 {
    		 SysLogger.error("DiscoverCompleteDao.addMonitor(),node_id=" + node.getId());
    	 }
      }   //end_for
   }
      
   private void addPingMonitor(int node_id)
   {
	   nmID++;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_node_monitor(id,node_id,moid,threshold,compare,compare_type,upper_times,");
	   sql.append("alarm_info,enabled,alarm_level,poll_interval,interval_unit,threshold_unit)values(");
	   sql.append(nmID);
	   sql.append(",");
	   sql.append(node_id);
	   sql.append(",'999001',0,2,1,3,'ping不通',1,3,5,'m','')");
	   conn.executeUpdate(sql.toString());
   }

   private void addServiceMonitor(int node_id)
   {
	   nmID++;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_node_monitor(id,node_id,moid,threshold,compare,compare_type,upper_times,");
	   sql.append("alarm_info,enabled,alarm_level,poll_interval,interval_unit,threshold_unit)values(");
	   sql.append(nmID);
	   sql.append(",");
	   sql.append(node_id);
	   sql.append(",'999002',0,2,1,1,'有些服务不可用',0,3,1,'h','')");
	   conn.executeUpdate(sql.toString());
   }

   private void addTelnetConfig(int node_id)
   {
	   if(telnetID==0)
		  telnetID = getNextID("server_telnet_config");
	   else
		  telnetID++;	   	  
	   conn.executeUpdate("insert into server_telnet_config(id,node_id)values(" + telnetID + "," + node_id + ")");
   }
   
   /**
    * 增加数据库监控项
    */
   public void addDBMonitor(int nodeId,String ip,String category)
   {
	   nmID++;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_node_monitor(id,node_id,moid,threshold,compare,compare_type,upper_times,");
	   sql.append("alarm_info,enabled,alarm_level,poll_interval,interval_unit,threshold_unit)values(");
	   sql.append(nmID);
	   sql.append(",");
	   sql.append(nodeId);
	   sql.append(",'052001',-1,1,1,2,'数据库不可用',1,3,20,'m','')");
	   conn.executeUpdate(sql.toString());
	   
	   addMonitor(nodeId,ip,category);	   
   }

   
   public void addMonitor(int node_id,String ip,String category)
   {
	   try
	   {	   		   
		   for(int i=0;i<moList.size();i++)
		   {
			   MonitorObject moid = (MonitorObject)moList.get(i);	
			   //System.out.println("category---"+category+"     mo----"+moid.getCategory());
			   //if(!moid.getCategory().equals(category)) continue;
			   if(!moid.isDefault()) continue; //只加入默认需要的监视器
			   if(!moid.getNodetype().equalsIgnoreCase(category))continue;		   
			   //nmID++;
			   StringBuffer sql = new StringBuffer(200);
			   sql.append("insert into topo_node_monitor(node_id,node_ip,category,moid,unit,threshold,compare,compare_type,upper_times,");
			   sql.append("alarm_info,enabled,alarm_level,poll_interval,interval_unit,threshold_unit,descr,nodetype,subentity,limenvalue0,limenvalue1,limenvalue2,");
			   sql.append("time0,time1,time2,sms0,sms1,sms2) values(");
//			   sql.append(nmID);
//			   sql.append(",");
			   sql.append(node_id);
			   sql.append(",'");
			   sql.append(ip);
			   sql.append("','");
			   sql.append(moid.getCategory());
			   sql.append("','");
			   sql.append(moid.getMoid());
			   sql.append("','");
			   sql.append(moid.getUnit());
			   sql.append("',");
			   sql.append(moid.getThreshold());
			   sql.append(",");
			   sql.append(moid.getCompare());
			   sql.append(",");
			   sql.append(moid.getCompareType());			   
			   sql.append(",");
			   sql.append(moid.getUpperTimes());
			   sql.append(",'");
			   sql.append(moid.getAlarmInfo());
			   sql.append("',");
			   sql.append(moid.isEnabled()?1:0);
			   sql.append(",");
			   sql.append(moid.getAlarmLevel());
			   sql.append(",");
			   sql.append(moid.getPollInterval());
			   sql.append(",'");
			   sql.append(moid.getIntervalUnit());
			   sql.append("','");
			   sql.append(moid.getUnit());
			   sql.append("','");
			   sql.append(moid.getDescr());
			   sql.append("','");
			   sql.append(moid.getNodetype());
			   sql.append("','");
			   sql.append(moid.getSubentity());
			   sql.append("',");
			   sql.append(moid.getLimenvalue0());
			   sql.append(",");
			   sql.append(moid.getLimenvalue1());
			   sql.append(",");
			   sql.append(moid.getLimenvalue2());
			   sql.append(",");
			   sql.append(moid.getTime0());
			   sql.append(",");
			   sql.append(moid.getTime1());
			   sql.append(",");
			   sql.append(moid.getTime2());
			   sql.append(",");
			   sql.append(moid.getSms0());
			   sql.append(",");
			   sql.append(moid.getSms1());
			   sql.append(",");
			   sql.append(moid.getSms2());
			   sql.append(")");			   
			   conn.executeUpdate(sql.toString(),false);	   
		   }		   
		   conn.commit();
		   /*
		   if(category.equals("aix")||category.equals("solaris")
			   ||category.equals("hp-ux")||category.equals("linux")
			   ||category.equals("tru64"))
		      addTelnetConfig(node_id);
		   */
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeMonitorDao.addMonitor()",e);
	   }	      
   }
   /*
    * 删除监视项
    */
   public void deleteMonitor(int node_id,String ip)
   {
	   try
	   {	   	
		   String sql = "delete from topo_node_monitor where node_id="+node_id;
		   conn.executeUpdate(sql.toString(),false);
		   conn.commit();
	   }
	   catch(Exception e)
	   {
		   conn.rollback();
		   SysLogger.error("NodeMonitorDao.deleteMonitor()",e);
	   }	      
   }
   
   /*
    * 删除监视项
    */
   public void deleteIpAlias(int node_id,String ip)
   {
	   try
	   {	   	
		   String sql = "delete from topo_node_monitor where node_id="+node_id;
		   conn.executeUpdate(sql.toString(),false);
		   conn.commit();
	   }
	   catch(Exception e)
	   {
		   conn.rollback();
		   SysLogger.error("NodeMonitorDao.deleteMonitor()",e);
	   }	      
   }
   
   /**
    * 把'换成_,保证sql不出错
    */
   private String replace(String oldStr)
   {
	   if(oldStr==null) return "";
	   
	   if(oldStr.length()>45) 
		  oldStr = oldStr.substring(0,45);	   
	   if(oldStr.indexOf("'")>=0)
		  return oldStr.replace('\'','_');
	   else
		  return oldStr; 
   }
     
   public BaseVo loadFromRS(ResultSet rs)
   {
      return null;
   }
   
   public boolean createTableForAS400(Host host){
   	boolean result = false;
   	
   	try {
			CreateTableManager ctable = new CreateTableManager();
			
			String ip = host.getIpAddress();
			//SysLogger.info("IP: ====="+ip);
//			String ip1 ="",ip2="",ip3="",ip4="";
//			String tempStr = "";
			String allipstr = "";
//			if (ip.indexOf(".")>0){
//				ip1=ip.substring(0,ip.indexOf("."));
//				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//			}
//			ip2=tempStr.substring(0,tempStr.indexOf("."));
//			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//			allipstr=ip1+ip2+ip3+ip4;
			allipstr = SysUtil.doip(ip); 
			
			ctable.createRootTable(conn, "systemasp", allipstr);
			ctable.createRootTable(conn, "systemasphour", allipstr);
			ctable.createRootTable(conn, "systemaspday", allipstr);
			
			ctable.createRootTable(conn, "dbcapability", allipstr);
			ctable.createRootTable(conn, "dbcaphour", allipstr);
			ctable.createRootTable(conn, "dbcapday", allipstr);
			
			result = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			result = false;
		}
   	
   	return result;
   }
   
   private void collectHostData(Host node){	   
       try {                	
       	Vector vector=null;
       	Hashtable hashv = null;
       	LoadAixFile aix=null;
       	LoadLinuxFile linux=null;
       	LoadHpUnixFile hpunix = null;
       	LoadSunOSFile sununix = null;
       	LoadWindowsWMIFile windowswmi = null;
       	I_HostCollectData hostdataManager=new HostCollectDataManager();

       	
       	I_HostLastCollectData hostlastdataManager=new HostLastCollectDataManager();
       	if(node.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL){
       		//SHELL获取方式        		
       		try{
       			if(node.getOstype() == 6){
//       				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为AIX主机服务器的数据");
//       				//AIX服务器
//       				try{
//       					aix = new LoadAixFile(node.getIpAddress());
//       					hashv=aix.getTelnetMonitorDetail();
//       					hostdataManager.createHostData(node.getIpAddress(),hashv);
//       				}catch(Exception e){
//       					e.printStackTrace();
//       				}
       			}else if(node.getOstype() == 9){
//       				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为LINUX主机服务器的数据");
//       				//LINUX服务器
//       				try{
//       					linux = new LoadLinuxFile(node.getIpAddress());
//       					hashv=linux.getTelnetMonitorDetail();
//       					hostdataManager.createHostData(node.getIpAddress(),hashv);
//       				}catch(Exception e){
//       					e.printStackTrace();
//       				}
       			}else if(node.getOstype() == 7){
//       				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为HPUNIX主机服务器的数据");
//       				//HPUNIX服务器
//       				try{
//       					hpunix = new LoadHpUnixFile(node.getIpAddress());
//       					hashv=hpunix.getTelnetMonitorDetail();
//       					hostdataManager.createHostData(node.getIpAddress(),hashv);
//       				}catch(Exception e){
//       					e.printStackTrace();
//       				}
       			}else if(node.getOstype() == 8){
//       				SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为SOLARIS主机服务器的数据");
//       				//WINDOWS服务器
//       				try{
//       					sununix = new LoadSunOSFile(node.getIpAddress());
//       					hashv=sununix.getTelnetMonitorDetail();
//       					hostdataManager.createHostData(node.getIpAddress(),hashv);
//       				}catch(Exception e){
//       					e.printStackTrace();
//       				}
       			}else if(node.getOstype() == 5){
       				SysLogger.info("采集: 开始用WMI方式采集IP地址为"+node.getIpAddress()+"类型为WINDOWS主机服务器的数据");
       				try{
       					windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
       					hashv=windowswmi.getTelnetMonitorDetail();
       					hostdataManager.createHostData(node.getIpAddress(),hashv);
       				}catch(Exception e){
       					e.printStackTrace();
       				}               				
       			}

       		}catch(Exception e){
       			e.printStackTrace();
       		}
				aix=null;
				hashv=null;
       	}
       	
       	else if(node.getCollecttype() == SystemConstant.COLLECTTYPE_WMI){
       		//WINDOWS下的WMI采集方式
       		SysLogger.info("采集: 开始用WMI方式采集IP地址为"+node.getIpAddress()+"类型为WINDOWS主机服务器的数据");
				try{
					windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
					hashv=windowswmi.getTelnetMonitorDetail();
					hostdataManager.createHostData(node.getIpAddress(),hashv);
				}catch(Exception e){
					e.printStackTrace();
				}
				aix=null;
				hashv=null;
       	}
       
       else{
       		//SNMP采集方式
    	   	HostNode hostnode = new HostNode();
   			//Host host = new Host();
    	   	hostnode.setId(node.getId());
    	   	hostnode.setSysName(node.getSysName());  
    	   	hostnode.setCategory(node.getCategory());
    	   	hostnode.setCommunity(node.getCommunity());
    	   	//hostnode.setWritecommunity(node.getWritecommunity());
    	   	hostnode.setSnmpversion(node.getSnmpversion());
    	   	hostnode.setIpAddress(node.getIpAddress());
    	   	hostnode.setLocalNet(node.getLocalNet());
    	   	hostnode.setNetMask(node.getNetMask());
    	   	hostnode.setAlias(node.getAlias());
    	   	hostnode.setSysDescr(node.getSysDescr());
    	   	hostnode.setSysOid(node.getSysOid());
    	   	hostnode.setType(node.getType());
    	   	hostnode.setManaged(node.isManaged());
    	   	hostnode.setOstype(node.getOstype());
    	   	hostnode.setCollecttype(node.getCollecttype());
    	   	hostnode.setSysLocation(node.getSysLocation());
    	   	hostnode.setSendemail(node.getSendemail());
    	   	hostnode.setSendmobiles(node.getSendmobiles());
    	   	hostnode.setSendphone(node.getSendphone());
    	   	hostnode.setBid(node.getBid());
    	   	hostnode.setEndpoint(node.getEndpoint());
    	   	hostnode.setStatus(0);
    	   	hostnode.setSupperid(node.getSupperid());
    	   
           	try{
           		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
    	    	try{
    	    		//获取被启用的所有被监视指标
    	    		monitorItemList = indicatorsdao.findByNodeIdAndTypeAndSubtype(hostnode.getId()+"", "host", "");
    	    	}catch(Exception e){
    	    		e.printStackTrace();
    	    	}finally{
    	    		indicatorsdao.close();
    	    	}
    	    	if(monitorItemList != null && monitorItemList.size()>0){
    	    		for (int i=0; i<monitorItemList.size(); i++) {
    	    			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
    	    			PollDataUtil polldatautil = new PollDataUtil();
    	    			polldatautil.collectHostData(nodeGatherIndicators);
    	    		}
    	    	}
           	}catch(Exception e){
           		
           	}
    	   
//           	if(node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3.1.1") || 
//           			node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3.1.2")||
//           			node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3.1.3")||
//           			node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3.1")){
//           		SysLogger.info("采集: 开始采集IP地址为"+node.getIpAddress()+"类型为WINDOWS主机服务器的数据");
//           		//windows
//           		WindowsSnmp windows=new WindowsSnmp();
//           		try{
//           			hashv=windows.collect_Data(hostnode);
//           			hostdataManager.createHostData(node.getIpAddress(),hashv);
//           		}catch(Exception ex){
//           			ex.printStackTrace();
//           		}
//           		windows=null;
//           		vector=null;
//           		
//           	}else if(node.getOstype() == 9){
//           		if(node.getCollecttype() == 1){
//						//System.out.println("==================linux================");
//						LinuxSnmp linuxSnmp = new LinuxSnmp();
//						hashv = linuxSnmp.collect_Data(hostnode);
//						//System.out.println("==================linux SNMP end================");
//						hostdataManager.createHostData(node.getIpAddress(),hashv);
//					}else{
//						
//					}
//           	}                		
       	}
       }catch(Exception exc){
       	
       }
}
}