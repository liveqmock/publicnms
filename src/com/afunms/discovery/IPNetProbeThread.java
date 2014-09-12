/**
 * <p>Description:probe the router table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-13
 */

package com.afunms.discovery;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.afunms.common.util.*;
import com.afunms.polling.task.ThreadPool;
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.model.DiscoverConfig;

public class IPNetProbeThread extends BaseThread
//public class IPNetProbeThread implements Runnable
{
   private Host node;
   private List addressList;
   private SnmpUtil snmp = null;
   public IPNetProbeThread(Host node,List list)
   {
      this.node = node;
      addressList = list;
   }
   
   public void run()
   {       
	   if(DiscoverEngine.getInstance().getStopStatus() == 1)return;
	   snmp = SnmpUtil.getInstance();
	   SysLogger.info("开始分析设备"+node.getIpAddress()+"的地址转发表");	    
	   Set shieldList = DiscoverResource.getInstance().getShieldSet();
	   List netshieldList = DiscoverResource.getInstance().getNetshieldList();
	   List netincludeList = DiscoverResource.getInstance().getNetincludeList();
	   IfEntity ifEntity = null;
	   IfEntity endifEntity = null;

       // 生成线程池
	   //ThreadPool threadPool = new ThreadPool(addressList.size());	
	   int threadNum = 10;
	   
	   if(addressList.size() < 10){
		   threadNum = addressList.size();
	   }
	   
	   
	   ThreadPool threadPool = null;
//	   int MAX_THREADS = DiscoverResource.getInstance().getMaxThreads(); //最大线程数,根据tomcat设置   
//	   ExecutorService runnableExecutor = Executors.newFixedThreadPool(MAX_THREADS);
	   try{
		   threadPool = new ThreadPool(threadNum);	
		   for(int i=0;i<addressList.size();i++)
		   {
			   //###################################
			   //若不需要服务器,则不进行交换机的ARP表采集
			   //if(1==1)continue;
			   //###################################   
				 try{
				   //node.updateCount(2);     	      	 
				   IpAddress ipAddr = (IpAddress)addressList.get(i);
				   threadPool.runTask(NetMediThread.createTask(ipAddr, node)); 	
//				   runnableExecutor.execute(NetMediThread.createTask(ipAddr, node));
				   //SysLogger.info("在"+node.getIpAddress()+"的地址转发表发现IP "+ipAddr.getIpAddress()+",开始分析");
				 }catch(Exception ex){
					 ex.printStackTrace();
				 }
	      }//end_for
		   // 关闭线程池并等待所有任务完成
	  	   threadPool.join();
	  	   threadPool.close();
	  	   threadPool = null;
	   }catch(Exception e){
		   
	   }
	   
//	   try{
//		   //threadPool = new ThreadPool(threadNum);	
//		   for(int i=0;i<addressList.size();i++)
//		   {
//			   //###################################
//			   //若不需要服务器,则不进行交换机的ARP表采集
//			   //if(1==1)continue;
//			   //###################################   
//				 try{
//				   //node.updateCount(2);     	      	 
//				   IpAddress ipAddr = (IpAddress)addressList.get(i);
//				   DiscoverEngine.getInstance().addRunnable(NetMediThread.createTask(ipAddr, node));
//				   //threadPool.runTask(NetMediThread.createTask(ipAddr, node)); 		   
//				   //SysLogger.info("在"+node.getIpAddress()+"的地址转发表发现IP "+ipAddr.getIpAddress()+",开始分析");
//				 }catch(Exception ex){
//					 ex.printStackTrace();
//				 }
//	      }//end_for
//		   // 关闭线程池并等待所有任务完成
//	   }catch(Exception e){
//		   
//	   }

	   DiscoverEngine.getInstance().addDiscoverdcount(); 
	   snmp = null;
	   setCompleted(true);
	   //**************************************
	   //setCompleted(true); 若用THREAD 需要放开
	   //**************************************
	   
   }//end_run
   public void analsysNDPHost(Host node,Host host){
     	if(host.getNdpHash() == null)
      		host.setNdpHash(snmp.getNDPTable(host.getIpAddress(),host.getCommunity()));
      	//判断该NDP中是否包含node的MAC地址,若包含,则说明node是跟该设备直接连接关系
      	Hashtable hostNDPHash = host.getNdpHash();
 		if(host.getMac() == null){
      		String descr = (String)hostNDPHash.get(node.getMac());
      		IfEntity nodeIfEntity = node.getIfEntityByDesc(descr);
      		Link link = new Link();		           
      		link.setStartId(node.getId());          
      		link.setStartIndex(nodeIfEntity.getIndex());
      		link.setStartIp(node.getIpAddress());
      		link.setStartPhysAddress(node.getMac()); //记录下起点的mac
      		link.setStartDescr(descr);
      		link.setEndIp(host.getIpAddress());
      		DiscoverEngine.getInstance().addHost(host,link);
  		}else{
  			if(node.getNdpHash() != null){
          		if(node.getNdpHash().containsKey(host.getMac())){
          			String hostdesc = (String)node.getNdpHash().get(host.getMac());
              		String descr = (String)hostNDPHash.get(node.getMac());
              		IfEntity nodeIfEntity = node.getIfEntityByDesc(descr);
              		IfEntity hostIfEntity = host.getIfEntityByDesc(hostdesc);
              		Link link = new Link();		           
              		link.setStartId(node.getId());          
              		link.setStartIndex(nodeIfEntity.getIndex());
              		link.setStartIp(node.getIpAddress());
              		link.setStartPhysAddress(node.getMac()); //记录下起点的mac
              		link.setStartDescr(descr);
              		link.setEndIndex(hostIfEntity.getIndex());
              		link.setEndIp(host.getIpAddress());
              		link.setEndPhysAddress(hostIfEntity.getPhysAddress());
              		link.setEndDescr(hostdesc);
              		DiscoverEngine.getInstance().addHost(host,link);
          		}else{
              		String descr = (String)hostNDPHash.get(node.getMac());
              		IfEntity nodeIfEntity = node.getIfEntityByDesc(descr);
              		Link link = new Link();		           
              		link.setStartId(node.getId());          
              		link.setStartIndex(nodeIfEntity.getIndex());
              		link.setStartIp(node.getIpAddress());
              		link.setStartPhysAddress(node.getMac()); //记录下起点的mac
              		link.setStartDescr(descr);
              		link.setEndIp(host.getIpAddress());
              		DiscoverEngine.getInstance().addHost(host,link);
          		}
  			}else{
  				String descr = (String)hostNDPHash.get(node.getMac());
          		IfEntity nodeIfEntity = node.getIfEntityByDesc(descr);
          		Link link = new Link();		           
          		link.setStartId(node.getId());          
          		link.setStartIndex(nodeIfEntity.getIndex());
          		link.setStartIp(node.getIpAddress());
          		link.setStartPhysAddress(node.getMac()); //记录下起点的mac
          		link.setStartDescr(descr);
          		link.setEndIp(host.getIpAddress());
          		DiscoverEngine.getInstance().addHost(host,link);
  			}

  		}
   }
}