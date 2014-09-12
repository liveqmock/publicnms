/**
 * <p>Description:与nodedao都是操作表nms_topo_node,但nodedao主要用于发现</p>
 * <p>Description:而toponodedao主要用于页面操作</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpService;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;
import com.afunms.detail.net.service.NetService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.ARP;
import com.afunms.polling.om.IpMac;

public class ARPDao extends BaseDao implements DaoInterface
{
   public ARPDao()
   {
	   super("topo_arp");	   	  
   }
   
   public List findByCondition(String key,String value)
   {	  
	   return findByCriteria("select * from topo_arp where " + key + "='" + value + "'");   
   }
     
   public boolean deleteByHostIp(String hostip)
   {	
 	   String sql = "delete from topo_arp where relateipaddr='"+hostip+"'";
 	   return saveOrUpdate(sql);			
   }
   
   public List loadIpMac()
   {
	   return findByCriteria("select * from topo_arp order by ipaddress"); 
   }
   
   
   public IpMac loadIpMac(int id)
   {
	   
	   List retList = new ArrayList();
	   List ipmacList = findByCriteria("select * from topo_arp where id="+id); 
	   if(ipmacList != null && ipmacList.size()>0){
		   IpMac ipmac = (IpMac)ipmacList.get(0);
			   return ipmac;
		   
	   }
	   return null;
   }
   
   public List loadIpMacByIP(String relateipaddr)
   {
	   List ipmacList = findByCriteria("select * from topo_arp where relateipaddr='"+relateipaddr+"'"); 
	   return ipmacList;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
   
   public List loadARPByNodeId(int nodeid)
   {
	   List ipmacList = findByCriteria("select * from topo_arp where node_id="+nodeid); 
	   return ipmacList;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   ARP vo = new ARP();
       try
       {
		   vo.setId(rs.getLong("id"));
		   vo.setNodeid(rs.getLong("node_id"));
		   vo.setIfindex(rs.getString("ifindex"));
		   vo.setPhysaddress(rs.getString("physaddress"));
		   vo.setIpaddress(rs.getString("ipaddress"));
		   vo.setMonflag(rs.getInt("monflag"));
       }
       catch(Exception e)
       {
 	       SysLogger.error("ARPDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
   
   public boolean save(BaseVo vo)
   {
	   return false;
   }
   
   public boolean update(BaseVo baseVo)
   {	
	   IpMac vo = (IpMac)baseVo;
	   String sql = "update ipmac set ifband='" + vo.getIfband() + "',ifsms='"+vo.getIfsms()+"' where id=" + vo.getId();
	   return saveOrUpdate(sql);			
   }
   
   public boolean deleteall()
   {	
	   String sql = "delete from ipmac";
	   return saveOrUpdate(sql);			
   }

   
   public List<String> getIfIps()
   {
	   List<String> allIps = new ArrayList<String>();
	   try
	   {
		   rs = conn.executeQuery("select a.ip_address from topo_interface a,topo_host_node b where a.node_id=b.id and b.category<4 and a.ip_address<>'' order by a.id");
		   while(rs.next())
		   {
		       String ips = rs.getString("ip_address");
		       allIps.add(ips);
		   }
	   }
	   catch(Exception e)
	   {		   
	   }
	   return allIps;
   }
   public String loadOneColFromRS(ResultSet rs){
	   return "";
   }
   public void todb(Host host){
	      List calhostlist = PollingEngine.getInstance().getNodeList();
	      List hostList = PollingEngine.getInstance().getNodeList();
	      Hashtable tempHash = new Hashtable();
	      Hashtable aliasHash = new Hashtable();
	      List aliaslist = new ArrayList();
	      for(int k=0;k<calhostlist.size();k++){
	    	  Host pollhost = (Host)hostList.get(k);
	    	  tempHash.put(pollhost.getIpAddress(), "");
	    	  
	    	  IpAliasDao ipdao = new IpAliasDao();
		     	List iplist = new ArrayList();
		     	try{
		     		iplist = ipdao.loadAll();
		     		if(iplist != null && iplist.size()>0){
		     			IpAlias vo = null;
		     			for(int i=0;i<iplist.size();i++){
		     				vo = (IpAlias)iplist.get(i);
		     				aliasHash.put(vo.getAliasip(), vo.getIpaddress());
		     			}
		     		}
		     	}catch(Exception e){
		     		e.printStackTrace();
		     	}finally{
		     		ipdao.close();
		     	}
	      }
			Vector ipmacVector = new Vector();
		     try{
		    	 String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
		    	 if("0".equals(runmodel)){
		    		//采集与访问是集成模式
		    			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		    			//得到IPMAC信息
		    			ipmacVector = (Vector)ipAllData.get("ipmac");
		    			if (ipmacVector == null)ipmacVector = new Vector();
		    	 }else{
		    		//采集与访问是分离模式
		    		 List arpList = new ArrayList();
		    		 try{
		    			   	NodeUtil nodeUtil = new NodeUtil();
		    				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);    
		    				arpList = new NetService(host.getId()+"", nodedto.getType(), nodedto.getSubtype()).getARPInfo();
		    				if(arpList != null && arpList.size()>0){
		    					for(int i=0;i<arpList.size();i++){
		    						IpMac ipmac = (IpMac)arpList.get(i);
		    						ipmacVector.add(ipmac);
		    					}
		    				}
		    			}catch(Exception e){
		    			}
		    	 }
		    	 if(ipmacVector != null && ipmacVector.size()>0){
		    		 //int snmpflag = 0;
		    		 DBManager dbm = new DBManager();
		    		 IpAliasDao ipaliasdao = new IpAliasDao();

		    		 int id = getNextID("topo_arp");
		    		 StringBuffer sql = null;
		    		 IpMac ipmac = null;
		    		 for(int i=0 ;i<ipmacVector.size(); i++){
		    			 	int snmpflag = 0;
							ipmac = (IpMac)ipmacVector.get(i);
							if(tempHash.containsKey(ipmac.getIpaddress())){
				    			 //snmp is open
				    			 snmpflag = 1;
				    		 }else if(aliasHash.containsKey(ipmac.getIpaddress())){
				    			 //alias ip is existed
				    			 snmpflag = 1;
				    		 }
							String physAddress = ipmac.getMac();
				    		 physAddress = CommonUtil.removeIllegalStr(physAddress);
				    		 sql = new StringBuffer(300);    		 
				    		 sql.append("insert into topo_arp(id,node_id,ifindex,physaddress,ipaddress,monflag)values(");
				    		 sql.append(id++);
				    		 sql.append(","); 
				    		 sql.append(host.getId());
				    		 sql.append(",'");
				    		 sql.append(ipmac.getIfindex());
				    		 sql.append("','");
				    		 sql.append(physAddress); 
				    		 sql.append("','");
				    		 sql.append(ipmac.getIpaddress());
				    		 sql.append("',");
				    		 sql.append(snmpflag); //默认情况下是未启动SNMP
				    		 sql.append(")");
				    		 conn.addBatch(sql.toString());
		    		 }
		    		 try{
		    			 conn.executeUpdate("delete from topo_arp where node_id="+host.getId());
		    			 conn.executeBatch();
		    		 }catch(Exception e){
		    			 
		    		 }
		    	 }
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
   }
}
