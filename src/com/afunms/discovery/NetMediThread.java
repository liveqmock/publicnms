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
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.model.DiscoverConfig;

public class NetMediThread 
//public class IPNetProbeThread implements Runnable
{
   private Host node;
   private List addressList;
   
   /**
   创建任务
	 */	
	public static Runnable createTask(final IpAddress ipAddr,final Host node) {
   return new Runnable() {
       public void run() {
    	   if(DiscoverEngine.getInstance().getStopStatus() == 1)return;
    	   SnmpUtil snmp = SnmpUtil.getInstance();
    	   Set shieldList = DiscoverResource.getInstance().getShieldSet();
    	   List netshieldList = DiscoverResource.getInstance().getNetshieldList();
    	   List netincludeList = DiscoverResource.getInstance().getNetincludeList();
    	   IfEntity ifEntity = null;
    	   IfEntity endifEntity = null;
    	   try{
    		   node.updateCount(2);     	      	    		   
    		   //判断该IP是否是已经屏蔽的IP地址
    		   SysLogger.info("在"+node.getIpAddress()+"的地址转发表发现IP "+ipAddr.getIpAddress()+",开始分析");
    		 //判断该IP是否在只需要发现的网段内
       		  int netincludeflag = 0;
       		  if(netincludeList != null && netincludeList.size()>0){
       			   long longip = DiscoverEngine.getInstance().ip2long(ipAddr.getIpAddress());
       			   for(int k=0;k<netincludeList.size();k++){
       				   Vector netinclude = (Vector)netincludeList.get(k);
       				   if(netinclude != null && netinclude.size()==2){
       					   try{
       						   if(longip>=((Long)netinclude.get(0)).longValue() && longip<=((Long)netinclude.get(1)).longValue()){
       							   SysLogger.info("设备IP "+ipAddr.getIpAddress()+"属于需要发现的网段");
       							   netincludeflag = 1;
       							   break;
       						   }
       					   }catch(Exception ex){
       						   ex.printStackTrace();
       					   }
       				   }
       			   }
       			   if(netincludeflag == 0)return;
       		   }
    		   
    		   SysLogger.info("开始处理IP "+ipAddr.getIpAddress()+"是否被屏蔽");
    		   if(shieldList != null && shieldList.size()>0){
    			   if(shieldList.contains(ipAddr.getIpAddress()))return;
    		   }
    		   
    		   //判断该IP是否在被屏蔽的网段内
    		   int netshieldflag = 0;
    		   if(netshieldList != null && netshieldList.size()>0){
    			   long longip = DiscoverEngine.getInstance().ip2long(ipAddr.getIpAddress());
    			   for(int k=0;k<netshieldList.size();k++){
    				   Vector netshield = (Vector)netshieldList.get(k);
    				   if(netshield != null && netshield.size()==2){
    					   //SysLogger.info(netshield.get(0)+"==="+netshield.get(1)+"==="+longip);
    					   try{
    						   if(longip>=((Long)netshield.get(0)).longValue() && longip<=((Long)netshield.get(1)).longValue()){
    							   SysLogger.info("设备IP "+ipAddr.getIpAddress()+"属于被屏蔽网段");
    							   netshieldflag = 1;
    							   break;
    						   }
    					   }catch(Exception ex){
    						   ex.printStackTrace();
    					   }
    				   }
    			   }
    			   if(netshieldflag == 1)return;
    		   }
         	//判断该IP是否已经存在
         	//if(DiscoverEngine.getInstance().getExistIpList().contains(ipAddr)) continue;
    		SysLogger.info("结束处理IP "+ipAddr.getIpAddress()+"是否被屏蔽");
    		
         	if(node.getIfEntityList() != null){
         		for(int j=0;j<node.getIfEntityList().size();j++)
         		{
         			//默认情况下,需要一个缺省的连接关系,当找不到物理链路的时候,用VLAN的接口来计算
         			ifEntity = (IfEntity)node.getIfEntityList().get(j);
         			SysLogger.info("判断设备IP "+ipAddr.getIpAddress()+"的ifIndex:"+ipAddr.getIfIndex()+" nodeIfIndex:"+ifEntity.getIndex()+"设备"+node.getIpAddress());
         			if(ifEntity.getIndex().equals(ipAddr.getIfIndex()))	
         				break;
         		}
         	}
    	     //SysLogger.info("####begin NetMediaThread getSysOid ##########"+ipAddr.getIpAddress());     
         	 String community = snmp.getCommunity(ipAddr.getIpAddress());  
         	//SysLogger.info("####end NetMediaThread getSysOid ##########"+ipAddr.getIpAddress());
    	     if(community==null){
    	    	 DiscoverEngine.getInstance().getFaildIpList().add(ipAddr.getIpAddress());
    	    	 return;
    	     }
    	     
    	      //判断已经发现的设备列表里是否已经存在该IP(或者该IP对应的管理IP)
    	     SysLogger.info("开始判断IP "+ipAddr.getIpAddress()+"是否已经是已经发现的IP或者IP别名");
    	      List hostList = DiscoverEngine.getInstance().getHostList();
    	      Host existHost = null;
    	      if(hostList != null && hostList.size()>0){
    		    	for(int k=0;k<hostList.size();k++)
    		    	{
    		    		Host tmpNode = (Host)hostList.get(k);
    		    		if(tmpNode.getIpAddress().equalsIgnoreCase(ipAddr.getIpAddress())){
    		    			existHost = tmpNode;
    		    			SysLogger.info("已发现的设备列表中已经存在"+tmpNode.getCategory()+"的设备:"+ipAddr.getIpAddress());		    			
    		    			break;
    		    		}else{
    		    			//判断别名IP是否存在
    		    			List aliasIPs = tmpNode.getAliasIPs();
    		    			if(aliasIPs != null && aliasIPs.size()>0){
    		    				if(aliasIPs.contains(ipAddr.getIpAddress())){
    		    					existHost = tmpNode;
    		    					SysLogger.info("已发现的设备列表中已经存在"+tmpNode.getCategory()+"的设备:"+ipAddr.getIpAddress());
    		    					break;
    		    				}
    		    			}
    		    		}
//    	  					//判断系统名称
//    	  					Hashtable sysGroupProperty = snmp.getSysGroup(ipAddr.getIpAddress(),community);
//    	  					if(sysGroupProperty != null){
//    	  						if(sysGroupProperty.containsKey("sysName")){
//    	  							String sysName = (String)sysGroupProperty.get("sysName");
//    	  							if(sysName != null && sysName.length()>0){
//    	  								if(sysName.equalsIgnoreCase(tmpNode.getSysName())){
//    	  									existHost = tmpNode;
//    	  									break;
//    	  								}
//    	  							}
//    	  						}
//    	  					}
    		    	}
    	      }
    	      SysLogger.info("结束判断IP "+ipAddr.getIpAddress()+"是否已经是已经发现的IP或者IP别名");
    	      
    	      //判断已经发现失败的设备列表里是否已经存在该IP
    		  SysLogger.info("开始判断IP "+ipAddr.getIpAddress()+"是否已经是已经发现失败的IP或者IP别名");
    		  List faildIpList = DiscoverEngine.getInstance().getFaildIpList();
    	      //existHost = null;
    	      if(faildIpList != null && faildIpList.size()>0){
    	    	  if(faildIpList.contains(ipAddr.getIpAddress()))return;
    	      }
    	      SysLogger.info("结束判断IP "+ipAddr.getIpAddress()+"是否已经是已经发现失败的IP或者IP别名");
    	      
    	      String sysOid = "";
    	      int deviceType = 0;
    	      SysLogger.info("开始获取设备 "+ipAddr.getIpAddress()+"的类型");
    	      if(existHost != null){
    	    	  //若该IP已经存在
    	    	  sysOid = existHost.getSysOid();
    	    	  deviceType = existHost.getCategory();
    	    	  
     		     if(existHost.getIfEntityList() != null){
 		     		for(int j=0;j<existHost.getIfEntityList().size();j++)
 		     		{
 		     			//默认情况下,需要一个缺省的连接关系,当找不到物理链路的时候,用VLAN的接口来计算
 		     			endifEntity = (IfEntity)existHost.getIfEntityList().get(j);
 		     			//SysLogger.info("判断设备IP "+ipAddr.getIpAddress()+"的ifIndex:"+ipAddr.getIfIndex()+" nodeIfIndex:"+ifEntity.getIndex()+"设备"+node.getIpAddress());
 		     			if(endifEntity.getIpAddress().equalsIgnoreCase(ipAddr.getIpAddress()))	
 		     				break;
 		     		}
     		     }
    	    	  
    	    	  Link link = new Link();	
                  try{
    	         	  link.setStartId(node.getId());          
    	         	  link.setStartIndex(ifEntity.getIndex());
    	         	  link.setStartIp(ifEntity.getIpAddress());
    	         	  link.setStartPhysAddress(ifEntity.getPhysAddress()); //记录下起点的mac
    	         	  link.setStartDescr(ifEntity.getDescr());
    	         	  link.setVlanStartIndex(ifEntity.getIndex());
    	         	  link.setEndIp(existHost.getIpAddress());
    	         	  link.setEndId(existHost.getId());
    	         	  link.setEndIndex(endifEntity.getIndex());
    	         	  link.setEndDescr(endifEntity.getDescr());
    	         	  link.setEndPhysAddress(endifEntity.getPhysAddress());
    	         	  link.setVlanEndIp(ipAddr.getIpAddress());//设置当前VLAN地址
    	         	  link.setVlanEndIndex(endifEntity.getIndex());
    	         	  link.setFindtype(SystemConstant.ISMac);//利用MAC发现
    	         	  link.setSublinktype(SystemConstant.NONEPHYSICALLINK);//是逻辑连接关系
                  }catch(Exception e){
                	  e.printStackTrace();
                  }  
    	    	//以下对于网络设备
                  DiscoverEngine.getInstance().dealLink(existHost,link);//link = null;

    	    	  return;
    	      }else{
    	    	  //不存在
    	    	  sysOid = SnmpUtil.getInstance().getSysOid(ipAddr.getIpAddress(),community);
    	    	  deviceType = SnmpUtil.getInstance().checkDevice(ipAddr.getIpAddress(),community,sysOid);
    	      }
    	      SysLogger.info("结束获取设备 "+ipAddr.getIpAddress()+"的类型,类型为"+deviceType); 

    	     	     
    	     if(deviceType==0) //未知设备
    	     {	    	 
    	    	 if(NetworkUtil.checkService(ipAddr.getIpAddress()))
    	    		deviceType=4;
    	    	 else
    	    	 {
    		    	 try{
    		    		 DiscoverEngine.getInstance().getExistIpList().add(ipAddr.getIpAddress());
    		    	 }catch(Exception e){
    		    		 e.printStackTrace();
    		    	 }
    		    	 //DiscoverEngine.getInstance().getFaildIpList().add(ipAddr.getIpAddress());
    		    	 SysLogger.info("一个支持SNMP,但不能确定其类型的设备:" + ipAddr.getIpAddress() + ",community=" + community + ",sysOid=" + sysOid);
    		    	 return;	    		 	    		 
    	    	 }	    	 
    	     }	     	     	     
        	 boolean isValid = false;		     
    	     SubNet subnet = null; 
    	     
    	     //初始化一个新节点
    	     Host host = new Host();
    	     host = DiscoverEngine.getInstance().getHostByIP(ipAddr.getIpAddress());
    	     if(host == null){
    	    	 //判断别名IP是否存在
    	    	 Host aliashost = null;
    	    	 aliashost = DiscoverEngine.getInstance().getHostByAliasIP(ipAddr.getIpAddress()); 
    	    	 if(aliashost != null){
    	    		 host = new Host();
    	    		 host.setBridgestpList(aliashost.getBridgestpList());
    	    		 host.setCdpList(aliashost.getCdpList());
    	    		 host.setIfEntityList(aliashost.getIfEntityList());
    	    	 }
    	     }	     
    	     if(host == null)host = new Host();
    	     
    	     if(node.getCategory()==1||node.getCategory()==2) //如果是二层交换机,它的接口没有相连子网 
    	     {	 	    	
    	        try{
    	        	subnet = ifEntity.isValidIP(ipAddr.getIpAddress());
    	        }catch(Exception e){
    	        	//e.printStackTrace();
    	        }
    	        if(subnet!=null) isValid = true;
    	        if(!isValid)  //会不会有这个情况??
    	        {
    	    	    SysLogger.info("NetMedia:节点" + node.getIpAddress()+ "上的" + ipAddr.getIpAddress() + "不在相应接口所连的子网中");
    	 	        host.setNetMask("255.255.255.0");
    	            host.setLocalNet(0);		        
    	        }
    	        else 
    	        {	        		        	
    	        	host.setNetMask(subnet.getNetMask());
                    host.setLocalNet(subnet.getId());
    	        }
    	     }   
    	     else //二层交换机
    	     {	 
    			 host.setNetMask(node.getNetMask());  //对二层交换,它的子网与父节点相同
    			 host.setLocalNet(node.getLocalNet());		        
    	     }
    	     
    	     if(existHost == null){
    		     host.setCategory(deviceType); 
    	         host.setIpAddress(ipAddr.getIpAddress());                                
    	         host.setCommunity(community);
    	         host.setWritecommunity(DiscoverEngine.getInstance().getWritecommunity());
    	         host.setSnmpversion(DiscoverEngine.getInstance().getSnmpversion());
    	         host.setSysOid(sysOid);         
    	         host.setLayer(node.getLayer() + 1);
    	         
    	         if(node.isRouter())
    	            host.setSuperNode(node.getId());
    	         else
    	        	host.setSuperNode(node.getSuperNode());
    	     }else{
    	    	 host = existHost;
    	     }

             SysLogger.info("从地址转发表中发现设备IP:"+host.getIpAddress()+" 设备类型:"+deviceType);         
             if(deviceType==4 || deviceType==5 || deviceType==6 ) //服务器或打印机
             {
            	 DiscoverEngine.getInstance().addHost(host,null);	
            	 return;
             }
             if(deviceType==2 || deviceType==3 || deviceType==7){
            	 //网络设备
            	 //设置STP数据
            	 if(host.getBridgestpList() == null){
            		 try{
            			 host.setBridgestpList(snmp.getBridgeStpList(ipAddr.getIpAddress(), community));
            		 }catch(Exception e){
            			 SysLogger.info("获取设备"+ipAddr.getIpAddress()+"的STP桥数据出错"+e.getMessage()); 
            		 }
            	 }
            	 //设置接口数据
            	 if(host.getIfEntityList() == null){
            		 try{
            			 host.setIfEntityList(snmp.getIfEntityList(ipAddr.getIpAddress(), community, deviceType));
            		 }catch(Exception e){
            			 SysLogger.info("获取设备"+ipAddr.getIpAddress()+"的接口数据出错"+e.getMessage()); 
            		 }
            	 }
            	 /*
            	//设置fdb接口数据
            	 if(host.getFdbList() == null){
            		 try{
            			 host.setFdbList(snmp.getFdbTable(ipAddr.getIpAddress(), community));
            		 }catch(Exception e){
            			 SysLogger.info("获取设备"+ipAddr.getIpAddress()+"的FDB数据出错"+e.getMessage()); 
            		 }
            	 }
            	 */
     			//若是H3C的交换设备,取得NDP信息
    			 if(host.getSysOid().indexOf("1.3.6.1.4.1.25506")>=0 || host.getSysOid().indexOf("1.3.6.1.4.1.2011")>=0){
    	        		//H3C的交换设备,取得NDP信息
    	        		 SysLogger.info("==========================================================");
    	        		 SysLogger.info(node.getIpAddress() + "的ARP表中发现H3C交换机:" + host.getIpAddress());
    	        		 SysLogger.info("=========================================================="); 
    	        		 
    	              	if(host.getNdpHash() == null || host.getNdpHash().size()==0)
    	              		host.setNdpHash(snmp.getNDPTable(host.getIpAddress(),host.getCommunity()));
    			 }
    			 
    		     if(host.getIfEntityList() != null){
    		     		for(int j=0;j<host.getIfEntityList().size();j++)
    		     		{
    		     			//默认情况下,需要一个缺省的连接关系,当找不到物理链路的时候,用VLAN的接口来计算
    		     			endifEntity = (IfEntity)host.getIfEntityList().get(j);
    		     			//SysLogger.info("判断设备IP "+ipAddr.getIpAddress()+"的ifIndex:"+ipAddr.getIfIndex()+" nodeIfIndex:"+ifEntity.getIndex()+"设备"+node.getIpAddress());
    		     			if(endifEntity.getIpAddress().equalsIgnoreCase(ipAddr.getIpAddress()))	
    		     				break;
    		     		}
    		     }
    			  SysLogger.info("###################################");
    			  SysLogger.info(node.getIpAddress() + "的ARP表中发现交换机:" + host.getIpAddress());
    			  SysLogger.info("###################################");
                  Link link = new Link();	
                  try{
    	         	  link.setStartId(node.getId());          
    	         	  link.setStartIndex(ifEntity.getIndex());
    	         	  link.setStartIp(ifEntity.getIpAddress());
    	         	  link.setStartPhysAddress(ifEntity.getPhysAddress()); //记录下起点的mac
    	         	  link.setStartDescr(ifEntity.getDescr());
    	         	  link.setVlanStartIndex(ifEntity.getIndex());
    	         	  link.setEndIp(host.getIpAddress());
    	         	  link.setEndId(host.getId());
    	         	  link.setEndIndex(endifEntity.getIndex());
    	         	  link.setEndDescr(endifEntity.getDescr());
    	         	  link.setEndPhysAddress(endifEntity.getPhysAddress());
    	         	  link.setVlanEndIp(ipAddr.getIpAddress());//设置当前VLAN地址
    	         	  link.setVlanEndIndex(endifEntity.getIndex());
    	         	  link.setFindtype(SystemConstant.ISMac);//利用MAC发现
    	         	  link.setSublinktype(SystemConstant.NONEPHYSICALLINK);//是逻辑连接关系
                  }catch(Exception e){
                	  
                  }
             	  DiscoverEngine.getInstance().addHost(host,link);
             }
    	   }catch(Exception ex){
    		   ex.printStackTrace();
    	   }
       }
   };
	}
}