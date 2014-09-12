/**
 * <p>Description:与nodedao都是操作表nms_topo_node,但nodedao主要用于发现</p>
 * <p>Description:而toponodedao主要用于页面操作</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.PollDataUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.PortConfigCenter;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;

public class HostNodeDao extends BaseDao implements DaoInterface
{
   public HostNodeDao()
   {
	   super("topo_host_node");
   }
   
   /**
    * 查找相同ip的网元
    * @param key
    * @param value
    * @return
    *  konglq
    */
   public List findBynode(String key,String value)
   {	  
	   //SysLogger.info("select * from topo_host_node where " + key + " = '" + value + "'");
	   return findByCriteria("select * from topo_host_node where " + key + " = '" + value + "'");   
   }
   
   public List findByCondition(String key,String value)
   {	  
	   return findByCriteria("select * from topo_host_node where " + key + " like '%" + value + "%'");   
   }
   public List findByCondition1(String key,String value)
   {	  
	   return findByCriteria("select * from topo_host_node where " + key + " = '" + value + "'");   
   }
     
   public List loadServer()
   {
	   return findByCriteria("select * from topo_host_node where category=4 order by ip_long"); 
   }

   public List loadNetwork(int nodetypeflag)
   {
	   if(nodetypeflag == 1)
		   ////网络设备
		   return findByCriteria("select * from topo_host_node where managed = 1 and (category<4 or category=7 or category=8) order by ip_long"); 
   		else if(nodetypeflag == 2){
   			//主机设备
   			return findByCriteria("select * from topo_host_node where managed = 1 and (category<5 or category=7 or category=8) order by ip_long");
   		}else if(nodetypeflag == 8){
   			//防火墙
   	   		return findByCriteria("select * from topo_host_node where managed = 1 and category=8 order by ip_long");
   		}else 
//   		   System.out.println("select * from topo_host_node where category<4 or category=7 or category=8 order by ip_long");
		   return findByCriteria("select * from topo_host_node where category<4 or category=7 or category=8 order by ip_long");
   }
   
   public List loadNetworkByBid(int typeflag,String businessid)
   {
	   List list = new ArrayList();
	   StringBuffer s = new StringBuffer();
	   int _flag = 0;
		if (businessid != null && !"".equals(businessid)){
			if(!"-1".equals(businessid)){
				String[] bids = businessid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								//flag = 1;
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}				
			}	
		}
	   if(typeflag == 1){//网络
		   
			  try{
				  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and (category<4 or category=7) "+s.toString()+" order by ip_long");
				  while(rs.next())
				  {
					  BaseVo vo = loadFromRS(rs);
					  list.add(vo);
				  }
			  }catch(Exception e){e.printStackTrace();}
			  return list;
		   
		   
		   //return findByCriteria("select * from topo_host_node where managed = 1 and (category<4 or category=7) "+s.toString()+" order by ip_long"); 
	   } else if(typeflag == 2){//网络与主机
		   try{
				  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and (category<5 or category=7) "+s.toString()+" order by ip_long");
				  while(rs.next())
				  {
					  BaseVo vo = loadFromRS(rs);
					  list.add(vo);
				  }
			  }catch(Exception e){e.printStackTrace();}
			  return list;
		   
   			//return findByCriteria("select * from topo_host_node where managed = 1 and (category<5 or category=7) "+s.toString()+" order by ip_long");
   	   } else if(typeflag == 8){//防火墙
   		try{
			  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and category=8 "+s.toString()+" order by ip_long");
			  while(rs.next())
			  {
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
   	   		//return findByCriteria("select * from topo_host_node where managed = 1 and category=8 "+s.toString()+" order by ip_long");
   	   } else if(typeflag == 5){//网络/主机/防火墙
   		   try{
			  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and category<5 or category=7 or category=8 "+s.toString()+" order by ip_long");
			  while(rs.next())
			  {
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
	   		//return findByCriteria("select * from topo_host_node where managed = 1 and category<5 or category=7 or category=8 "+s.toString()+" order by ip_long");
   	   } else if(typeflag == 4){
   		try{
			  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and category=4 "+s.toString()+" order by ip_long");
			  while(rs.next())
			  {
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
   		    //return findByCriteria("select * from topo_host_node where managed = 1 and category=4 "+s.toString()+" order by ip_long");
   	   }else if(typeflag == 10){//网络
		   
			  try{
				  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and (category<4 or category=7 or category=8) "+s.toString()+" order by ip_long");
				  while(rs.next())
				  {
					  BaseVo vo = loadFromRS(rs);
					  list.add(vo);
				  }
			  }catch(Exception e){e.printStackTrace();}
			  return list;
		   
		   
		   //return findByCriteria("select * from topo_host_node where managed = 1 and (category<4 or category=7) "+s.toString()+" order by ip_long"); 
	   }else if(typeflag == 14){//存储
		   
			  try{
				  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and category=14 "+s.toString()+" order by ip_long");
				  while(rs.next())
				  {
					  BaseVo vo = loadFromRS(rs);
					  list.add(vo);
				  }
			  }catch(Exception e){e.printStackTrace();}
			  return list;
		   
		   
		   //return findByCriteria("select * from topo_host_node where managed = 1 and (category<4 or category=7) "+s.toString()+" order by ip_long"); 
	   } else {
   		try{
			  rs = conn.executeQuery("select * from topo_host_node where category<4 or category=7 "+s.toString()+" order by ip_long");
			  while(rs.next())
			  {
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
   	   }
		   //return findByCriteria("select * from topo_host_node where category<4 or category=7 "+s.toString()+" order by ip_long");
	   
   }

   /**
    * @param typeflag
    * @param businessid
    * @return by zhangys
    */
   public List loadNetworkByBid2(int typeflag, String businessid) {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (businessid != null && !"".equals(businessid)) {
			if (!"-1".equals(businessid)) {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim()
										+ ",%' ");
								_flag = 1;
							} else {
								s.append(" or bid like '%," + bids[i].trim()
										+ ",%' ");
							}
						}
					}
					s.append(") ");
				}
			}
		}else{
			s.append(" and (bid is null or bid = '') ");
		}
		
		if (typeflag == 1) {// 网络
			try {
				rs = conn
						.executeQuery("select * from topo_host_node where managed = 1 and (category<4 or category=7) "
								+ s.toString() + " order by ip_long");
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		} else if (typeflag == 2) {// 网络与主机
			try {
				rs = conn
						.executeQuery("select * from topo_host_node where managed = 1 and (category<5 or category=7) "
								+ s.toString() + " order by ip_long");
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		} else if (typeflag == 8) {// 防火墙
			try {
				rs = conn
						.executeQuery("select * from topo_host_node where managed = 1 and category=8 "
								+ s.toString() + " order by ip_long");
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		} else if (typeflag == 5) {// 网络/主机/防火墙
			try {
				rs = conn
						.executeQuery("select * from topo_host_node where managed = 1 and category<5 or category=7 or category=8 "
								+ s.toString() + " order by ip_long");
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		} else if (typeflag == 4) {
			try {
				rs = conn
						.executeQuery("select * from topo_host_node where managed = 1 and category=4 "
								+ s.toString() + " order by ip_long");
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		} else {
			try {
				rs = conn
						.executeQuery("select * from topo_host_node where category<4 or category=7 "
								+ s.toString() + " order by ip_long");
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
	}
   
   public List loadNetworkByBidAndCategory(int typeflag,String businessid)
   {
	   List list = new ArrayList();
	   StringBuffer s = new StringBuffer();
	   int _flag = 0;
		if (businessid != null && !"".equals(businessid)){
			if(!"-1".equals(businessid)){
				String[] bids = businessid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								//flag = 1;
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
	   if(typeflag == 1){//路由设备
	   		try{
				  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and category=1 "+s.toString()+" order by ip_long");
				  while(rs.next())
				  {
					  BaseVo vo = loadFromRS(rs);
					  list.add(vo);
				  }
			  }catch(Exception e){e.printStackTrace();}
			  return list;
		   //return findByCriteria("select * from topo_host_node where managed = 1 and category=1 "+s.toString()+" order by ip_long"); 
	   	}else{
	   		try{
				  rs = conn.executeQuery("select * from topo_host_node where managed = 1 and (category=2 or category=3 or category=7) "+s.toString()+" order by ip_long");
				  while(rs.next())
				  {
					  BaseVo vo = loadFromRS(rs);
					  list.add(vo);
				  }
			  }catch(Exception e){e.printStackTrace();}
			  return list;
   		}
		   //return findByCriteria("select * from topo_host_node where managed = 1 and (category=2 or category=3 or category=7) "+s.toString()+" order by ip_long");
	   
   }
   public List findByIDs(String nodeIDs)
   {
	   List list = new ArrayList();
		  try{
			  rs = conn.executeQuery("select * from topo_host_node where id in(" + nodeIDs +")");
			  while(rs.next())
			  {
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
   }
   public List loadIsMonitored(int monitorflag)
   {
	   if(monitorflag == 1)
		   return findByCriteria("select * from topo_host_node where managed = 1 order  by category,ip_long"); 
	   else
		   return findByCriteria("select * from topo_host_node where managed = 0 order by category,ip_long");
   }
   public List loadHostByFlag(int monitorflag)
   {
	   if(monitorflag == 1)
		   return findByCriteria("select * from topo_host_node where managed = 1 and  category=4 order by ip_long"); 
	   else //服务器
		   return findByCriteria("select * from topo_host_node where category=4 order by ip_long");
   }
   
   public List loadNode()
   {
	   return findByCriteria("select * from topo_host_node where category<5 or category=7 order by ip_long"); 
   }
   
   public List loadHost()
   {
	   List retList = new ArrayList();
	   List nodeList = findByCriteria("select * from topo_host_node where category<5 or category=7 order by ip_long"); 
	   if(nodeList != null && nodeList.size()>0){
		   for(int i=0;i<nodeList.size();i++){
			   HostNode node = (HostNode)nodeList.get(i);
			   com.afunms.discovery.Host vo = new com.afunms.discovery.Host();
//			   vo.setId(node.getId());
//			   vo.setIpAddress(node.getIpAddress());
//			   vo.setIpLong(node.getIpLong());
//			   vo.setSysName(node.getSysName());
//			   vo.setAlias(node.getAlias());
//			   vo.setNetMask(node.getNetMask());
//			   vo.setSysDescr(node.getSysDescr());
//			   vo.setSysOid(node.getSysOid());
//			   vo.setSysContact(node.getSysContact());
//			   vo.setSysLocation(node.getSysLocation());
//			   vo.setCommunity(node.getCommunity());
//			   vo.setSuperNode(node.getSuperNode());
//			   vo.setWritecommunity(node.getWriteCommunity());
//			   vo.setSnmpversion(node.getSnmpversion());
//			   //vo.setWriteCommunity(rs.getString("write_community"));
//			   vo.setCategory(node.getCategory());		   
//			   vo.setManaged( node.isManaged());
//			   vo.setType(node.getType());
//			   vo.setLocalNet(node.getLocalNet());
//			   vo.setLayer(node.getLayer());
//			   vo.setBridgeAddress(node.getBridgeAddress());
//			   vo.setStatus(node.getStatus());
//			   vo.setDiscoverstatus(node.getDiscovertatus());
//			   vo.setOstype(node.getOstype());
//			   vo.setCollecttype(node.getCollecttype());
//			   vo.setSendemail(node.getSendemail());
//			   vo.setSendmobiles(vo.getSendmobiles());
//			   vo.setBid(node.getBid());
//			   vo.setEndpoint(node.getEndpoint());
//			   vo.setSupperid(node.getSupperid());
			   try {
				BeanUtils.copyProperties(vo,node);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} 
				retList.add(vo);
		   }
	   }
	   return retList;
	   //return findByCriteria("select * from topo_host_node where category<5 or category=7 order by ip_long"); 
   }
   
   public HostNode loadHost(int id)
   {
	   
	   List retList = new ArrayList();
	   List nodeList = findByCriteria("select * from topo_host_node where id="+id); 
	   if(nodeList != null && nodeList.size()>0){
			   HostNode node = (HostNode)nodeList.get(0);
			   return node;
		   
	   }
	   return null;
   }
   
   public List loadMonitorNet()
   {
	   return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7 or category=12) order by ip_long"); 
   }
   
   public List loadMonitorGateway()
   {
	   return findByCriteria("select * from topo_host_node where managed=1 and category=10 order by ip_long"); 
   }
   
   public List loadMonitorF5()
   {
	   return findByCriteria("select * from topo_host_node where managed=1 and category=11 order by ip_long"); 
   }
   
   public List loadMonitorByMonCategory(int managed,int category)
   {
	   return findByCriteria("select * from topo_host_node where managed="+managed+" and category="+category+" order by ip_long"); 
   }
   
//   public List loadMonitorByMonCategoryAndSNMP(int managed,int category)
//   {
//	   return findByCriteria("select * from topo_host_node where managed="+managed+" and category="+category+" order by ip_long"); 
//   }
   
   /**
    * 取出该用户能访问的设备
    * @param managed  被管理状态
    * @param category 类别  4：服务器   1：网络设备（路由器和交换机）
    * @param bid      业务  如
    * @return
    */
   public List loadMonitorByMonCategory(int managed,int category,String[] bids)
   {
	   StringBuffer sqlBuffer = new StringBuffer();
	   sqlBuffer.append("select * from topo_host_node where managed = '");
	   sqlBuffer.append(managed);
	   if(category == 4){
		   sqlBuffer.append("' and category = '");
		   sqlBuffer.append(category);
		   sqlBuffer.append("'");
	   }else{
		   sqlBuffer.append("' and category in ('3','1','2')");
	   }
	   int _flag = 0;
	   if(bids != null && bids.length>0){
			for(int i=0;i<bids.length;i++){
				if(bids[i].trim().length()>0){
					if(_flag==0){
						sqlBuffer.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
						_flag = 1;
					}else{
						//flag = 1;
						sqlBuffer.append(" or bid like '%,"+bids[i].trim()+",%' ");
					}
				}
			}
			if(_flag == 1)sqlBuffer.append(") ") ;
			sqlBuffer.append(" order by ip_long") ;
		}
//	   System.out.println("HostNodeDao.java--- loadMonitorByMonCategory()--------->>>"+sqlBuffer.toString());
	   return findByCriteria(sqlBuffer.toString()); 
   }
   
   /**
    * 查询记录的条数
    * @param managed
    * @param category
    * @param bids
    * @return
    */
   public int getMonitorCountByMonCategory(int managed,int category,String[] bids){
	   int count = 0;
	   StringBuffer sqlBuffer = new StringBuffer();
	   sqlBuffer.append("select count(*) count from topo_host_node where managed = '");
	   sqlBuffer.append(managed);
	   if(category == 4){
		   sqlBuffer.append("' and category = '");
		   sqlBuffer.append(category);
		   sqlBuffer.append("'");
	   }else{
		   sqlBuffer.append("' and category in ('3','1','2')");
	   }
	   int _flag = 0;
	   if(bids != null && bids.length>0){
			for(int i=0;i<bids.length;i++){
				if(bids[i].trim().length()>0){
					if(_flag==0){
						sqlBuffer.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
						_flag = 1;
					}else{
						//flag = 1;
						sqlBuffer.append(" or bid like '%,"+bids[i].trim()+",%' ");
					}
				}
			}
			if(_flag == 1){
				sqlBuffer.append(") ") ;
			}
		}
//	   SysLogger.info(sqlBuffer.toString());
	   try {
		   rs = conn.executeQuery(sqlBuffer.toString());
		   if(rs.next()){
			   count = Integer.parseInt(rs.getString("count"));
		   }
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	   return count; 
   }
   
   /**
    * 取出该用户能访问的设备
    * @param managed  被管理状态
    * @param category 类别  4：服务器   1：网络设备（路由器和交换机）
    * @param bid      业务  如
    * @return
    */
   public List loadMonitorByMonCategory(int managed,int category,String[] bids, String start, String limit){
	   StringBuffer sqlBuffer = new StringBuffer();
	   sqlBuffer.append("select * from topo_host_node where managed = '");
	   sqlBuffer.append(managed);
	   if(category == 4){
		   sqlBuffer.append("' and category = '");
		   sqlBuffer.append(category);
		   sqlBuffer.append("'");
	   }else{
		   sqlBuffer.append("' and category in ('3','1','2')");
	   }
	   int _flag = 0;
	   if(bids != null && bids.length>0){
			for(int i=0;i<bids.length;i++){
				if(bids[i].trim().length()>0){
					if(_flag==0){
						sqlBuffer.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
						_flag = 1;
					}else{
						//flag = 1;
						sqlBuffer.append(" or bid like '%,"+bids[i].trim()+",%' ");
					}
				}
			}
			if(_flag == 1){
				sqlBuffer.append(") ") ;
			}
			if(start != null && !start.equals("") && limit != null && !limit.equals("")){
				sqlBuffer.append(" limit ");
				sqlBuffer.append(start);
				sqlBuffer.append(",");
				sqlBuffer.append(limit);
			}
		}
//	   SysLogger.info(sqlBuffer.toString());
	   return findByCriteria(sqlBuffer.toString()); 
   }

   public List<HostNode> loadSwitch()
   {
	   return findByCriteria("select * from topo_host_node where category=2 or category=3 order by ip_long"); 
   }
   
   /**
    * 删除一个主机节点
    */
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {	    
//		   SysLogger.info("delete from topo_node_monitor where node_id=" + id);
    	   try{
    		   conn.executeUpdate("delete from topo_node_monitor where node_id=" + id);
    	   }catch(Exception e){
    		   e.printStackTrace();
    	   }
    	   //conn.executeUpdate(sql)
    	   try{
    		   conn.executeUpdate("delete from topo_node_multi_data where node_id=" + id);
    	   }catch(Exception e){
    		   e.printStackTrace();
    	   }
    	   try{
    		   conn.executeUpdate("delete from topo_node_single_data where node_id=" + id);
    	   }catch(Exception e){
    		   e.printStackTrace();
    	   }
           try{
        	   conn.executeUpdate("delete from topo_interface where node_id=" + id);
           }catch(Exception e){
    		   e.printStackTrace();
    	   }
           try{
        	   conn.executeUpdate("delete from topo_interface_data where node_id=" + id);
           }catch(Exception e){
    		   e.printStackTrace();
    	   }
           
           try{
        	   conn.executeUpdate("delete from server_telnet_config where node_id=" + id);
           }catch(Exception e){
    		   e.printStackTrace();
    	   }
           try{
        	   conn.executeUpdate("delete from topo_ipalias where ipaddress='" + loadipaddressbyid(id)+"'");
           }catch(Exception e){
    		   e.printStackTrace();
    	   }
           try{
        	   conn.executeUpdate("delete from topo_host_node where id=" + id);	
           }catch(Exception e){
    		   e.printStackTrace();
    	   }
	       //conn.executeBatch();
	       result = true;
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	       result = false;
	       SysLogger.error("TopoNodeDao.delete()",e);	        
	   }
	   finally
	   {
	       conn.close();
	   }
	   return result;
   }
   
   /**
    * 更新接口状态
    */
   public void updateInterfaceData(List nodeList)
   {
      for(int i=0;i<nodeList.size();i++)
      {
    	 Node node = (Node)nodeList.get(i); 
    	 if(node.getCategory()>4) continue;
    	 com.afunms.polling.node.Host host = (com.afunms.polling.node.Host)node;
    	 if(host.getInterfaceHash()==null) continue;
    	 
    	 Iterator it = host.getInterfaceHash().values().iterator();
    	 while(it.hasNext())
    	 {	 
    		 IfEntity ifObj = (IfEntity)it.next();    	    
    		 StringBuffer sql = new StringBuffer(100);
    	     sql.append("update topo_interface set oper_status=");
    	     sql.append(ifObj.getOperStatus());
	    	 sql.append(" where id=");
	    	 sql.append(ifObj.getId());
		     conn.addBatch(sql.toString());  
    	 }    
	     conn.executeBatch();   
      }      	
      conn.close();
   }
     
   @Override
   public BaseVo loadFromRS(ResultSet rs)
   {
	   HostNode vo = new HostNode();
       try
       {
		   vo.setId(rs.getInt("id"));
		   vo.setAssetid(rs.getString("asset_id"));
		   vo.setLocation(rs.getString("location"));
		   vo.setIpAddress(rs.getString("ip_address"));
		   vo.setIpLong(rs.getLong("ip_long"));
		   vo.setSysName(rs.getString("sys_name"));
		   vo.setAlias(rs.getString("alias"));
		   vo.setNetMask(rs.getString("net_mask"));
		   vo.setSysDescr(rs.getString("sys_descr"));
		   vo.setSysLocation(rs.getString("sys_location"));
		   vo.setSysContact(rs.getString("sys_contact"));
		   vo.setSysOid(rs.getString("sys_oid"));
		   vo.setCommunity(rs.getString("community"));
		   vo.setWriteCommunity(rs.getString("write_community"));
		   vo.setSnmpversion(rs.getInt("snmpversion"));
		   vo.setTransfer(rs.getInt("transfer"));
		   vo.setCategory(rs.getInt("category"));		   
		   vo.setManaged(rs.getInt("managed")==1?true:false);
		   vo.setType(rs.getString("type"));
		   vo.setSuperNode(rs.getInt("super_node"));
		   vo.setLocalNet(rs.getInt("local_net"));
		   vo.setLayer(rs.getInt("layer"));
		   vo.setBridgeAddress(rs.getString("bridge_address"));
		   vo.setStatus(rs.getInt("status"));
		   vo.setDiscovertatus(rs.getInt("discoverstatus"));
		   vo.setOstype(rs.getInt("ostype"));
		   vo.setCollecttype(rs.getInt("collecttype"));
		   vo.setSendemail(rs.getString("sendemail"));
		   vo.setSendmobiles(rs.getString("sendmobiles"));
		   vo.setSendphone(rs.getString("sendphone"));
		   vo.setBid(rs.getString("bid"));
		   vo.setEndpoint(rs.getInt("endpoint"));
		   vo.setSupperid(rs.getInt("supperid"));//snow add at 2010-05-18
		   //SNMP V3
		   vo.setSecuritylevel(rs.getInt("securitylevel"));
		   vo.setSecurityName(rs.getString("securityName"));
		   vo.setV3_ap(rs.getInt("v3_ap"));
		   vo.setAuthpassphrase(rs.getString("authpassphrase"));
		   vo.setV3_privacy(rs.getInt("v3_privacy"));
		   vo.setPrivacyPassphrase(rs.getString("privacyPassphrase"));
		   //SysLogger.info(vo.getSendemail()+"==="+vo.getBid());
       }
       catch(Exception e)
       {
 	       SysLogger.error("HostNodeDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
   
   public boolean save(BaseVo vo)
   {
	   return false;
   }
   
   public boolean save(HostNode vo)
   {
	   String sql = "insert into topo_host_node(id, ip_address, sys_name, alias) values (" + vo.getId() + ",'" + vo.getIpAddress()+"','" + vo.getSysName() + "','" + vo.getAlias() + "')";
	   SysLogger.info(sql);
	   return this.saveOrUpdate(sql);
   }
   
   public boolean updatesysgroup(HostNode baseVo,Hashtable mibvalues)
   {	
	   HostNode vo = (HostNode)baseVo;
	   int managed=0;
	   SnmpUtil snmp = SnmpUtil.getInstance();
	   boolean flag = false;
	  
		   try{
			   flag = snmp._setSysGroup(vo.getIpAddress(), vo.getWriteCommunity(),vo.getSnmpversion(), mibvalues);
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
	   
	   if(flag == true){
		   //修改数据库
		   String sql = "update topo_host_node set sys_name='" + vo.getSysName() + "',sys_location='"+vo.getSysLocation()+"',sys_contact='"+vo.getSysContact()+"' where id=" + vo.getId();
		   return saveOrUpdate(sql);
	   }
	   
	   return flag;
   }
   
   public boolean updateipalias(HostNode baseVo,Hashtable mibvalues)
   {	
	   HostNode vo = (HostNode)baseVo;
	   int managed=0;
	   SnmpUtil snmp = SnmpUtil.getInstance();
	   boolean flag = false;
	   try{
		   flag = snmp.setSysGroup(vo.getIpAddress(), vo.getWriteCommunity(),vo.getSnmpversion(), mibvalues);
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   if(flag == true){
		   //修改数据库
		   String sql = "update topo_host_node set sys_name='" + vo.getSysName() + "',sys_location='"+vo.getSysLocation()+"',sys_contact='"+vo.getSysContact()+"' where id=" + vo.getId();
		   return saveOrUpdate(sql);
	   }else{
		   return saveOrUpdate("update topo_host_node set id=" + vo.getId() +" where id=" + vo.getId());
	   }		
   }
   
   public boolean update(BaseVo baseVo)
   {	
	   HostNode vo = (HostNode)baseVo;
	   int managed=0;
	   if(vo.isManaged())managed =1;
	 //snow modify at 2010-05-18
	   String sql = "update topo_host_node set ip_address ='"+vo.getIpAddress()+"',asset_id='"+vo.getAssetid()+"',location='"+vo.getLocation()+"',snmpversion="+vo.getSnmpversion()+",transfer="+vo.getTransfer()+", alias='" + vo.getAlias() + "',sendmobiles='"+vo.getSendmobiles()
			+"',sendemail='"+vo.getSendemail()+"',bid='"+vo.getBid()+"',managed="+managed+",endpoint="+vo.getEndpoint()+",sendphone='"+vo.getSendphone()+"',supperid="+vo.getSupperid()+" where id=" + vo.getId();
	   //+"',sendemail='"+vo.getSendemail()+"',bid='"+vo.getBid()+"',endpoint="+vo.getEndpoint()+",sendphone='"+vo.getSendphone()+"',supperid="+vo.getSupperid()+" where id=" + vo.getId();
	   //SysLogger.info(sql);
	   return saveOrUpdate(sql);			
   }
   //wxy add
   public boolean editAlias(BaseVo baseVo)
   {	
	   HostNode vo = (HostNode)baseVo;
	   int managed=0;
	   if(vo.isManaged())managed =1;
	 
	   String sql = "update topo_host_node set  alias='" + vo.getAlias()+"' where id=" + vo.getId() ;
	   
	   return saveOrUpdate(sql);			
   }
   public String refreshSysName(int id)
   {	
	   HostNode vo = (HostNode)findByID(id+"");
	   SnmpUtil snmp = SnmpUtil.getInstance();
	   String sysName = snmp.getSysName(vo.getIpAddress(),vo.getCommunity());
	   String sql = "update topo_host_node set sys_name='" +sysName + "' ,alias = '"+sysName+"' where id=" + id;
	   try{
		   saveOrUpdate(sql);
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return sysName;
   }
   
   public boolean updatesnmp(BaseVo baseVo)
   {	
	   boolean flag = false;
	   HostNode vo = (HostNode)baseVo;
	   String sql = "update topo_host_node set community='" +vo.getCommunity() + "' ,write_community = '"+vo.getWriteCommunity()+"',snmpversion="+vo.getSnmpversion()+" where id=" + vo.getId();
	   try{
		   flag = saveOrUpdate(sql);
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return flag;
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
   //quzhi
   public boolean updatebid(String bid,String hostid)
   {	
	   String sql = "update topo_host_node set bid = '"+bid+"' where id ='"+hostid+"'";
//	   SysLogger.info(sql);
	   return saveOrUpdate(sql);			
   }
   
   /**
    * This method according to parameter oid<code>String</code>, 
    * from the database to find out the list of network devices.
    * 这个方法根据参数oid，从数据库中查找出网络设备的列表
    * 
    * @author nielin
    * add 2010-01-08
    * @param oid<code>String</code>
    * @return {@link List}
    * 
    * 
    */
   public List loadHostByOid(String oid){
	   return findByCriteria("select * from topo_host_node where sys_oid ='"+ oid +"'"); 
   }
   public List loadByEndPoint(String endPoint)
   {	
	   return findByCriteria("select * from topo_host_node where endpoint ='"+ endPoint +"'"); 
   }
   public List loadByNotEndPoint(String endPoint)
   {	
	   return findByCriteria("select * from topo_host_node where endpoint !='"+ endPoint +"'"); 
   }
   
   
   public boolean updateEndPoint(String nodeId , String endPoint)
   {	
	   String sql = "update topo_host_node set endpoint ='"+ endPoint +"' where id ='"+nodeId+"'";
	   SysLogger.info(sql);
	   return saveOrUpdate(sql);	
   }
   
   public boolean updateEndPoint(List list)
   {	
	   boolean result = false;
		try{
			if(list != null && list.size() >0){
				String sql = "update topo_host_node set endpoint='";
				for(int i = 0 ; i<list.size(); i++){
					List hostlist = (List)list.get(i);
					String sql2 = sql+hostlist.get(1)+"' where id ='"+ hostlist.get(0) + "'";
					conn.addBatch(sql2);
				}
				conn.executeBatch();
			}
			result = true;
		}catch(Exception ex){
			SysLogger.error("updateEndPoint.saveList()",ex);
			result = false;
		}finally{
			conn.close();
		}
		return result;
   }
   
   
   public List loadByPingChildNode()
   {	
	   return findByCriteria("select * from topo_host_node where endpoint='0' or endpoint='2'"); 
   }
   
      public HostNode findByIpaddress(String ipaddress){
    	  HostNode hostNode = new HostNode();
	   String sql = "select * from topo_host_node where ip_address = '"+ ipaddress +"'";
	   try {
			rs = conn.executeQuery(sql);
			if(rs.next()){
				hostNode = (HostNode)loadFromRS(rs);
			}
	   } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   }
	   return hostNode;
   }
      
    //zhushouzhi----------------------------------
      public List loadall()
      {
   	   return findByCriteria("select * from topo_host_node"); 
      }
      
      public String loadipaddressbyid(String id)
      { 
    	String ipaddess = "";     
    	String sql = "select ip_address from topo_host_node where id ="+id;
    	 try {
 			rs = conn.executeQuery(sql);
 			if(rs.next()){
 				ipaddess = rs.getString("ip_address");
 			}
 	   } catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 	   }finally{
			//conn.close();
	   }
 	   return ipaddess;
      }
   
   //LINAN AIX mac update   
   public boolean UpdateAixMac(int id,String mac){
	   String sql="Update topo_host_node set bridge_address='"+mac+"' where id="+id;
	  // System.out.println("com.afunms.topology.dao--------------------HostNodeDao.java----982行------->"+sql);
	   return saveOrUpdate(sql);
   } 
   
   public String updateSql(BaseVo baseVo){
	   HostNode vo = (HostNode)baseVo;
	   int managed=0;
	   if(vo.isManaged())managed =1;
	 //snow modify at 2010-05-18
	   String sql = "update topo_host_node set ip_address ='"+vo.getIpAddress()+"',asset_id='"+vo.getAssetid()+"',location='"+vo.getLocation()+"',snmpversion="+vo.getSnmpversion()+",transfer="+vo.getTransfer()+", alias='" + vo.getAlias() + "',sendmobiles='"+vo.getSendmobiles()
			+"',sendemail='"+vo.getSendemail()+"',bid='"+vo.getBid()+"',managed="+managed+",endpoint="+vo.getEndpoint()+",sendphone='"+vo.getSendphone()+"',supperid="+vo.getSupperid()+" where id=" + vo.getId();
	   //+"',sendemail='"+vo.getSendemail()+"',bid='"+vo.getBid()+"',endpoint="+vo.getEndpoint()+",sendphone='"+vo.getSendphone()+"',supperid="+vo.getSupperid()+" where id=" + vo.getId();
	   //SysLogger.info(sql);
	   return sql;
   }
   
   public int getNodeID(String ip){
	   int nodeID = 0;
	   HostNode vo = (HostNode)findByIpaddress(ip);
       if (null != vo && vo.getIpAddress() != null) {
       		nodeID = vo.getId();
       		if(nodeID == 0)nodeID = getNextID("topo_host_node");
       	}else
       		nodeID = getNextID("topo_host_node");
	   return nodeID;
   }
   
   public int getCountByIpaddress(String ipaddress){
   	   int count = 0;
   	   String sql = "select count(1) from topo_host_node where ip_address = '"+ ipaddress +"'";
   	   try {
   			rs = conn.executeQuery(sql);
   			if(rs.next()){
   				count = rs.getInt(1);
   			}
   	   } catch (SQLException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   	   }
   	   return count;
      }

   
   public List getOrderByIP()
   {
	   List list = new ArrayList();
	   try 
	   {
		   rs = conn.executeQuery("select * from  topo_host_node where id not in(select distinct nodeid from nms_cabinet_equipments) order by ip_long");
		   //SysLogger.info("select * from " + table + " order by ip_long");
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
           list = null;
		   SysLogger.error("BaseDao.loadAll()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   public void addNodeByNDP(com.afunms.discovery.Host node,int addResult){
	   if(addResult>0){
		   //默认情况下，接收最高4个级别
		   String faci_str="0,1,2,3,";
			
		    NetSyslogNodeRuleDao netruledao = new NetSyslogNodeRuleDao();
		    NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
		    try{
			    String strNodeId = "";
			    try{
			    	strNodeId = netruledao.findIdByIpaddress(node.getIpAddress());
			    }catch(Exception e){
			    	e.printStackTrace();
			    }
			    String strFacility = "";
				List rulelist = new ArrayList();
				try{
					rulelist = ruledao.loadAll();
				}catch(Exception e){
					
				}finally{
					ruledao.close();
				}
				if(rulelist != null && rulelist.size()>0 && "".equals(faci_str)){
					NetSyslogRule logrule = (NetSyslogRule)rulelist.get(0);
					strFacility = logrule.getFacility();
				}else{
					strFacility = faci_str;
				}
			    String strSql = "";
			    strSql = "insert into nms_netsyslogrule_node(nodeid,facility)values('"+strNodeId+"','"+strFacility+"')";
			    try{
			    	netruledao.saveOrUpdate(strSql);
			    }catch(Exception e){
			    	e.printStackTrace();
			    }
		    }catch(Exception e){
		    	e.printStackTrace();
		    }finally{
		    	netruledao.close();
		    	ruledao.close();
		    }
		    
			 //采集设备信息
		       try{
		    	   if(node.getEndpoint() == 2){
			   		}else{
			   			//SysLogger.info("### 开始添加采集指标 "+ipAddress+" 设备############");
			   			if(node.getCategory() == 4){
			   				//初始化服务器采集指标和阀值
			   			try {
		   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()));
		   					
		   					//判断采集方式
//		   					if(node.getCollecttype()==1){
//		   						//snmp方式采集
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
								nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()),"1");
//		   					}else if(node.getCollecttype()>1){
//		   						//其他方式采集			
//		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
//		   						nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId()+"", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()),"0",node.getCollecttype());  						
//		   					}
		   				} catch (RuntimeException e) {
		   					// TODO Auto-generated catch block
		   					e.printStackTrace();
		   				}
			   				
			   			
			   			
			   				
			   			}else if(node.getCategory() < 4 || node.getCategory() == 7 || node.getCategory() == 8){
			   				//初始化服务器采集指标和阀值
			   				//SysLogger.info(node.getSysOid());
			   				
			   			  // ------
			   			try {
		   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()));
		   					
		   					//判断采集方式
//		   					if(node.getCollecttype()==1){
//		   						//snmp方式采集
		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
								nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()),"1");
//		   					}else if(node.getCollecttype()>1){
//		   						//其他方式采集
//		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
//		   						nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId()+"", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()),"0",node.getCollecttype());
//		   						
//		   					}
								PortConfigCenter.getInstance().setPortHastable();
		   				} catch (RuntimeException e) {
		   					// TODO Auto-generated catch block
		   					e.printStackTrace();
		   				}	
			   			}else if(node.getCategory()  == 13){
				   				//初始化服务器采集指标和阀值
				   				//SysLogger.info(node.getSysOid());
				   				
				   			  // ------
				   			try {
			   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()));
			   					
			   					//判断采集方式
	//		   					if(node.getCollecttype()==1){
	//		   						//snmp方式采集
			   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()),"1");
	//		   					}else if(node.getCollecttype()>1){
	//		   						//其他方式采集
	//		   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
	//		   						nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId()+"", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()),"0",node.getCollecttype());
	//		   						
	//		   					}
									PortConfigCenter.getInstance().setPortHastable();
			   				} catch (RuntimeException e) {
			   					// TODO Auto-generated catch block
			   					e.printStackTrace();
			   				}	
			   			}else if( node.getCategory() == 9 ){
			   				//初始化ATM设备采集指标
			   				//if(node.getSysOid().startsWith("1.3.6.1.4.1.9.")){
			   					//ATM设备
			   					if(node.getCollecttype() == SystemConstant.COLLECTTYPE_PING){
			   						//只用PING检测连通性
			   						try {
					   					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					   					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId()+"", AlarmConstant.TYPE_NET, "atm","ping");
					   				} catch (RuntimeException e) {
					   					// TODO Auto-generated catch block
					   					e.printStackTrace();
					   				}
					   				try {
				   						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				   						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId()+"", AlarmConstant.TYPE_NET, "atm","1","ping");
					   				} catch (RuntimeException e) {
					   					// TODO Auto-generated catch block
					   					e.printStackTrace();
					   				}
			   					}
			   				//}
			   			}
			   			//SysLogger.info("### 结束添加指标 "+ipAddress+" 设备############");
			   			
			   			
			   			//若只用PING TELNET SSH方式检测可用性,则性能数据不采集,跳过
			   			if(node.getCollecttype() == SystemConstant.COLLECTTYPE_PING ||
			   					node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT||
			   					node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT){
			   					//SysLogger.info("只PING TELNET SSH方式检测可用性,性能数据不采集,跳过");
				   				if(node.getCategory() < 4 || node.getCategory() == 7){
				   					PollDataUtil polldata = new PollDataUtil();
				   					polldata.collectNetData(node.getId()+"");
				   				}else if(node.getCategory() == 4){
				   					//collectHostData(node);
				   				}
			   			}
			   		}
		    	   
		       }catch(Exception e){
		    	   e.printStackTrace();
		       }
		       
		       HostNodeDao hostNodeDao = new HostNodeDao();
				Hashtable nodehash = new Hashtable();
				try{
					List hostlist = hostNodeDao.loadIsMonitored(1);
					if(hostlist != null && hostlist.size()>0){
						for(int i=0;i<hostlist.size();i++){
							HostNode _node = (HostNode)hostlist.get(i);
							if(nodehash.containsKey(_node.getCategory()+"")){
								((List)nodehash.get(_node.getCategory()+"")).add(_node);
							}else{
								List nodelist = new ArrayList();
								nodelist.add(_node);
								nodehash.put(_node.getCategory()+"", nodelist);
							}
						}
					}
				}catch(Exception e){
					
				}finally{
					hostNodeDao.close();
				}
				ShareData.setNodehash(nodehash);
	    }
   }
   
   /**
	 * 
	 * 根据 oids 来判断子类型
	 * @param oids 设备oids
	 * @return 返回子类型
	 */
	public String getSutType(String oids)
	{
		String subtype="";
			if(oids.startsWith("1.3.6.1.4.1.311."))
			{
				subtype="windows";
			}else if(oids.startsWith("1.3.6.1.4.1.2021") || oids.startsWith("1.3.6.1.4.1.8072"))
			 {
				subtype="linux";
			 }else if(oids.startsWith("as400"))
			 {
				subtype="as400"; 
				 
			 }else if(oids.startsWith("1.3.6.1.4.1.42.2.1.1"))
			 {
					subtype="solaris";  
		     }else if(oids.startsWith("1.3.6.1.4.1.2.3.1.2.1.1"))
			 {
					subtype="aix";  
		     }else if(oids.startsWith("1.3.6.1.4.1.11.2.3.10.1"))
			 {
					subtype="hpunix"; 
			 }else if(oids.startsWith("1.3.6.1.4.1.11.2.3.7.11"))
			 {
					subtype="hp";		
		     }else if(oids.startsWith("1.3.6.1.4.1.9."))
			 {
					subtype="cisco";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.25506.") || oids.startsWith("1.3.6.1.4.1.2011."))
			 {
					subtype="h3c";  
		     }else if(oids.startsWith("1.3.6.1.4.1.4881."))
			 {
					subtype="redgiant";  
		     }else if(oids.startsWith("1.3.6.1.4.1.5651."))
			 {
					subtype="maipu";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.171."))
			 {
					subtype="dlink";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.2272."))
			 {
					subtype="northtel";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.89."))
			 {
					subtype="radware";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.3320."))
			 {
					subtype="bdcom";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.1588.2.1."))
			 {
					subtype="brocade";  
		     }else if (oids.startsWith("1.3.6.1.4.1.3902.")) {
		    	 subtype="zte";
			}
			
	        return subtype;		
	}
}
