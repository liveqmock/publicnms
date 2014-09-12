/**
 * <p>Description:core device,seed node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.discovery;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.DiscoverConfig;

public class Seed
{  
   public static boolean discoverOK;
   private String coreIp;
   private String community;
   private int discovermodel;
   
   public Seed(String coreIp,String community,int discovermodel)
   {
	   this.coreIp = coreIp;	   
   	   this.community = community;
   	   this.discovermodel = discovermodel;
	   discoverOK = true;
   }
   
   public void startDiscover()
   {
	   SysLogger.info("开始时间:" + SysUtil.getCurrentTime());
	   //存储之前发现或添加的设备
	   HostNodeDao nodeDao = new HostNodeDao();
	   List formerNodeList = new ArrayList();
	   try{
		   formerNodeList = nodeDao.loadHost();
	   }catch(Exception e){
		   
	   }finally{
		   nodeDao.close();
	   }
	   //存储之前发现或添加的连接关系
	   LinkDao linkDao = new LinkDao();
	   List formerNodeLinkList = new ArrayList();
	   try{
		   formerNodeLinkList = linkDao.loadAll();
	   }catch(Exception e){
		   
	   }finally{
		   linkDao.close();
	   }
	  // linkDao.close();
	   if(discovermodel == 1)
	   {
		   //补充发现
		   DiscoverEngine.getInstance().setFormerNodeList(formerNodeList);
		   DiscoverEngine.getInstance().setFormerNodeLinkList(formerNodeLinkList);
	   }
	   DiscoverMonitor.getInstance().setCompleted(false);
	   

	   try{
		   DiscoverEngine.getInstance().setDiscovermodel(discovermodel);
		   DiscoverEngine.getInstance().setStopStatus(0);
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   /*
	   //清除原来的拓扑发现表
	   DiscoverConfigDao dao = new DiscoverConfigDao();
	   dao.cleanTOPOTable();
	   */
	   discoverFromNode();
	   
   	   DiscoverMonitor.getInstance().setStartTime(SysUtil.getCurrentTime());
   	   DiscoverMonitor.getInstance().setEndTime(null);
	   ThreadProbe tp = new ThreadProbe(DiscoverEngine.getInstance());
	   tp.setDaemon(true); //设置为守护线程
	   tp.start();
   }
      
   private void discoverFromNode()
   {
	   //清除所有表格
	   DiscoverConfigDao cleandao = new DiscoverConfigDao();
	   try{
		   cleandao.cleanTOPOTable();
	   }catch(Exception e){
		   
	   }finally{
		   cleandao.close();
	   }
	   
	   SysLogger.info("核心IP:"+coreIp+"   SysOid:############");
   	   String sysOid = SnmpUtil.getInstance().getSysOid(coreIp,community);
	   //String sysOid = SnmpUtils.getSysOid(coreIp,community,3,5000);
   	   SysLogger.info("核心IP:"+coreIp+"   SysOid:"+sysOid);   	                                                          
   	   int deviceType = SnmpUtil.getInstance().checkDevice(coreIp,community,sysOid);
   	   //int deviceType = SnmpUtils.checkDevice(coreIp,community,sysOid,3,5000);
   	   SysLogger.info("核心IP:"+coreIp+"   SysOid:"+sysOid+" 设备类型:"+deviceType);   	   
   	   if(deviceType==0||deviceType>3)
   	   {
   	       SysLogger.info("核心设备" + coreIp + "," + community + "不是一台交换设备,发现不能继续!");
   	       discoverOK = false;
   	       return;
   	   }   	     	  
   	   Host seed = new Host();   	  
   	   seed.setCategory(deviceType);
	   seed.setIpAddress(coreIp);
   	   seed.setCommunity(community);
   	   seed.setWritecommunity(DiscoverEngine.getInstance().getWritecommunity());
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
   	   seed.setSnmpversion(default_version);
   	   DiscoverEngine.getInstance().setSnmpversion(default_version);
   	   seed.setSysOid(sysOid);
   	   seed.setSuperNode(-1);
   	   seed.setLayer(1);
   	   seed.setLocalNet(-1);  
   	   
   	   /**
   	    * @param Host
   	    * @param Link
   	    */
   	   DiscoverEngine.getInstance().addHost(seed,null);
   	   	   
   	   DiscoverResource.getInstance().getCommunitySet().add(community);
   	   
   	   //将当前的团体名称设为缺省的团体名称
   	   DiscoverResource.getInstance().setCommunity(community);
   	   DiscoverConfigDao dao = new DiscoverConfigDao();
   	   dao.saveCore(coreIp,community);
   	   List othercoreList = dao.listByFlag("othercore");
   	   dao.close();
   	   if(othercoreList != null && othercoreList.size()>0)
   	   {
   		   for(int i=0;i<othercoreList.size();i++)
   		   {
   			   DiscoverConfig vo = (DiscoverConfig)othercoreList.get(i);
   			   sysOid = SnmpUtil.getInstance().getSysOid(vo.getAddress(),vo.getCommunity());
   			   //sysOid = SnmpUtils.getSysOid(vo.getAddress(),vo.getCommunity(),3,5000);
   			   SysLogger.info("其他种子IP:"+vo.getAddress()+"   SysOid:"+sysOid);   	                                                          
   			   deviceType = SnmpUtil.getInstance().checkDevice(vo.getAddress(),vo.getCommunity(),sysOid);
   			   //deviceType = SnmpUtils.checkDevice(vo.getAddress(),vo.getCommunity(),sysOid,3,5000);
   			   SysLogger.info("其他核心IP:"+vo.getAddress()+"   SysOid:"+sysOid+" 设备类型:"+deviceType);
   			   /*
   			    * 	<option value='1' selected>路由器</option>
            		<option value='2'>路由交换机</option>
            		<option value='3'>交换机</option>
            		<option value='4'>服务器</option>
            		<option value='8'>防火墙</option>
   			    */
   			   if(deviceType==0||deviceType>3)
   			   {
   				   SysLogger.info("其他核心设备" + vo.getAddress() + "," + vo.getCommunity() + "不是一台交换设备,发现不能继续!");
   				   //discoverOK = false;
   				   continue;
   			   }
   			   Host otherseed = new Host();
   			   otherseed.setIpAddress(vo.getAddress());
   			   otherseed.setCommunity(vo.getCommunity());
   			   otherseed.setCategory(deviceType);
   			   otherseed.setSysOid(sysOid);
   			   otherseed.doDiscover();
   		   }
   	   }
   }
}

class ThreadProbe extends Thread
{
   private DiscoverEngine engine;  	
   public ThreadProbe(DiscoverEngine engine)
   {
      this.engine = engine;
   }

   public void run()
   {
      try
      {
         Thread.sleep(50000);
      }
      catch(InterruptedException ie)
      {}
      
   	  while(!engine.isDiscovered())//这里可能会进入死循环
      {
         try
         {
            Thread.sleep(30000);
         }
         catch(InterruptedException ie)
         {}
      }
   	  
   	  DiscoverMonitor.getInstance().setEndTime(SysUtil.getCurrentTime());
      DiscoverMonitor.getInstance().setCompleted(true);
      
      DiscoverComplete dc = new DiscoverComplete();
      dc.completed(Seed.discoverOK);      
      SysLogger.info("结束时间:" + SysUtil.getCurrentTime());
      /**/
  	//CollectIPDetail cfd = new CollectIPDetail();
	//cfd.findDirectDevices();       	
      
   }	  
}