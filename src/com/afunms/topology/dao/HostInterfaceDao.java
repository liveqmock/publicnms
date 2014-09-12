/**
 * <p>Description:operate table NMS_INTERFACE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-16
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.node.IfEntity;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.InterfaceNode;



public class HostInterfaceDao extends BaseDao 
{
   public HostInterfaceDao()
   {
	   super("topo_interface");	   	  
   }
   
   /**
    * 加载所有交换网络接口的mac(不重复)
    */
   public Set loadExchangeMac()
   {
	   Set macSet = new HashSet();
	   try
	   {
		   rs = conn.executeQuery("select * from topo_interface where node_id in (select id from topo_host_node where category<4)");
		   while(rs.next())
			  macSet.add(rs.getString("phys_address")); 
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HostInterfaceDao.loadExchangeMac()",e);
	   }
	   finally
	   {
		   conn.close();
	   }
	   return macSet;
   }
   
   public boolean deleteByHostId(String nodeid)
	  {	
		   String sql = "delete from topo_interface where node_id='"+nodeid+"'";
		   return saveOrUpdate(sql);			
	  }
   
   public Hashtable loadInterfaces(int nodeId)
   {
	   Hashtable hash = new Hashtable();
	   try
	   {
		   rs = conn.executeQuery("select * from topo_interface where node_id=" + nodeId + " order by port");
		   //SysLogger.info("-----------------------------");
		   SysLogger.info("select * from topo_interface where node_id=" + nodeId + " order by port");
		   while(rs.next())
		   {
			   IfEntity ifEntity = new IfEntity();
			   ifEntity.setId(rs.getInt("id"));
			   ifEntity.setAlias(rs.getString("alias"));
			   ifEntity.setIndex(rs.getString("entity"));
			   ifEntity.setDescr(rs.getString("descr"));
			   ifEntity.setIpAddress(rs.getString("ip_address"));
			   ifEntity.setPhysAddress(rs.getString("phys_address"));
			   ifEntity.setPort(rs.getString("port"));
			   ifEntity.setSpeed(rs.getLong("speed"));
			   ifEntity.setOperStatus(rs.getInt("oper_status"));
			   ifEntity.setType(rs.getInt("type"));
			   ifEntity.setChassis(rs.getInt("chassis"));
			   ifEntity.setSlot(rs.getInt("slot"));
			   ifEntity.setUport(rs.getInt("uport"));
			   Hashtable chassisHash = new Hashtable();

			   int intchassis = ifEntity.getChassis();
			   if(chassisHash.containsKey(intchassis)){
				   //已经存在CHASSIS数据
				   Hashtable slot_hash = (Hashtable)chassisHash.get(intchassis);
				   int slot = ifEntity.getSlot();
				   if(slot_hash.containsKey(slot)){
					   //已经存在SLOT数据
					   Hashtable uport_hash = (Hashtable)slot_hash.get(intchassis);
					   int uport = ifEntity.getUport();
					   uport_hash.put(uport, ifEntity);
					   slot_hash.put(slot, uport_hash);
					   chassisHash.put(intchassis, slot_hash);
				   }else{
					   //不存在SLOT数据
					   Hashtable uport_hash = new Hashtable();
					   int uport = ifEntity.getUport();
					   uport_hash.put(uport, ifEntity);
					   slot_hash = new Hashtable();
					   slot_hash.put(slot, uport_hash);
					   chassisHash.put(intchassis,slot_hash);
				   }  
			   }else{
				   //不存在CHASSIS数据
				   Hashtable slot_hash = new Hashtable();
				   int slot = ifEntity.getSlot();
				   Hashtable uport_hash = new Hashtable();
				   int uport = ifEntity.getUport();
				   uport_hash.put(uport, ifEntity);
				   slot_hash.put(slot, uport_hash);
				   chassisHash.put(intchassis, slot_hash);				   
			   }
			   //SysLogger.info(ifEntity.getIpAddress()+" "+ifEntity.getIndex()+" is added!-----");
			   hash.put(ifEntity.getIndex(),ifEntity);
		   }	   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HostInterfaceDao.loadInterfaces()",e);
	   }
	   return hash;
   }
   
   public com.afunms.discovery.IfEntity loadInterfacesByNodeIDAndDesc(int nodeId,String descr)
   {
	   com.afunms.discovery.IfEntity ifEntity = null;	   
	   try
	   {
		   rs = conn.executeQuery("select * from topo_interface where node_id=" + nodeId + " and descr='"+descr+"' order by port");
		   SysLogger.info("select * from topo_interface where node_id=" + nodeId + " and descr='"+descr+"' order by port");
		   while(rs.next())
		   {
			   ifEntity = new com.afunms.discovery.IfEntity();
			   //ifEntity.setId(rs.getInt("id"));
			   //ifEntity.setAlias(rs.getString("alias"));
			   ifEntity.setIndex(rs.getString("entity"));
			   ifEntity.setDescr(rs.getString("descr"));
			   ifEntity.setIpAddress(rs.getString("ip_address"));
			   ifEntity.setPhysAddress(rs.getString("phys_address"));
			   ifEntity.setPort(rs.getString("port"));
			   ifEntity.setSpeed(rs.getLong("speed")+"");
			   ifEntity.setOperStatus(rs.getInt("oper_status"));
			   ifEntity.setType(rs.getInt("type"));
			   ifEntity.setChassis(rs.getInt("chassis"));
			   ifEntity.setSlot(rs.getInt("slot"));
			   ifEntity.setUport(rs.getInt("uport"));
		   }	   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HostInterfaceDao.loadInterfacesByNodeIDAndDesc()",e);
	   }
	   return ifEntity;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   InterfaceNode vo = new InterfaceNode();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setNode_id(rs.getInt("node_id"));
		   vo.setEntity(rs.getString("entity"));
		   vo.setDescr(rs.getString("descr"));
		   vo.setPort(rs.getString("port"));
		   vo.setSpeed(rs.getString("speed"));
		   vo.setAlias(rs.getString("alias"));
		   vo.setPhys_address(rs.getString("phys_address"));
		   vo.setIp_address(rs.getString("ip_address"));
		   vo.setOper_status(rs.getInt("oper_status"));
		   vo.setType(rs.getInt("type"));
		   vo.setChassis(rs.getInt("chassis"));
		   vo.setSlot(rs.getInt("slot"));
		   vo.setUport(rs.getInt("uport"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("HostInterfaceDao.loadFromRS()",e); 
       }	   
       return vo;
   }	   
   
   //根据nodeid地址查找出接口的数量
   public int getEntityNumByNodeid(int nodeid){
	   int num = 0;
	   StringBuffer sBuffer = new StringBuffer();
	   sBuffer.append("select count(*) from topo_interface where node_id = '");
	   sBuffer.append(nodeid);
	   sBuffer.append("'");
	   try {
		   rs = conn.executeQuery(sBuffer.toString());
		   if(rs.next()){
			   num = Integer.valueOf(rs.getString(1));
		   }
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return num;
   }

   /**
    * 得到被监视的节点的端口列表
    * @param monitornodelist
    * @return
    */
	public Hashtable getHostInterfaceList(List monitorNodelist) {
		Hashtable retHash = new Hashtable();
		StringBuffer sql = new StringBuffer();
		sql.append("select node_id,count(*) num from topo_interface where node_id in ('");
		if (monitorNodelist != null && monitorNodelist.size() > 0) {
			for (int i = 0; i < monitorNodelist.size(); i++) {
				Object obj = monitorNodelist.get(i);
				if (obj instanceof HostNode) {
					HostNode hostNode = (HostNode) obj;
					sql.append(hostNode.getId());
					if (i != monitorNodelist.size() - 1) {
						sql.append("','");
					}
				}
			}
		}
		sql.append("') group by node_id");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				String num = rs.getString("num");
				String nodeid = rs.getString("node_id");
				retHash.put(nodeid, num);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return retHash;
	}
	public synchronized int getNextID(){
		return super.getNextID("topo_interface");
	}
	public void RefreshPortconfigs(){
		
	}
}