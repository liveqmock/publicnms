/**
 * <p>Description:operate table NMS_NODE_MONITOR</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.Department;
import com.afunms.topology.model.*;

public class NodeMonitorDao extends BaseDao implements DaoInterface
{   
   public NodeMonitorDao()
   {
	   super("topo_node_monitor");	   	  
   }
   
	public boolean save(BaseVo baseVo)
	{
		Department vo = (Department)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_department(id,dept,man,tel)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getDept());
		sql.append("','");
		sql.append(vo.getMan());		
		sql.append("','");
		sql.append(vo.getTel());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean update(BaseVo baseVo)
	{
		Department vo = (Department)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("update system_department set dept='");
		sql.append(vo.getDept());
		sql.append("',man='");
		sql.append(vo.getMan());
		sql.append("',tel='");
		sql.append(vo.getTel()); 
		sql.append("' where id=");
		sql.append(vo.getId());		
		return saveOrUpdate(sql.toString());			
	}
   public List loadByNodeID(int nodeId)
   {
	   List list = new ArrayList(10);
	   try
	   {  //一定要order by id,把连通性测试放在第一个
	      rs = conn.executeQuery("select * from topo_node_monitor where node_id=" + nodeId + " order by id");
	      while(rs.next())
		     list.add(loadFromRS(rs)); 
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeMonitorDao.loadByNodeID()",e);
	   }
	   return list;
   }
   
   public List loadByNodeIp(String ip)
   {
	   List list = new ArrayList(10);
	   try
	   {  //一定要order by id,把连通性测试放在第一个
	      rs = conn.executeQuery("select * from topo_node_monitor where node_ip='" + ip + "' order by id");
	      while(rs.next())
		     list.add(loadFromRS(rs)); 
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeMonitorDao.loadByNodeIp()",e);
	   }
	   return list;
   }
   
   public List loadByBID(String businessid)
   {
	   StringBuffer s = new StringBuffer();
	   int _flag = 0;
		if (businessid != null){
			if(businessid !="-1"){
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
		
	   List list = new ArrayList(10);
	   try
	   {  //一定要order by id,把连通性测试放在第一个
	      rs = conn.executeQuery("select * from topo_node_monitor where node_id in(select id from topo_host_node where 1=1 "+ s.toString()+") order by id");
	      while(rs.next())
		     list.add(loadFromRS(rs)); 
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeMonitorDao.loadByNodeIp()",e);
	   }
	   return list;
   }
   
   //------------------以下这三个方法用于轮询初始化-------------------------
   public Hashtable loadTelnetConfig()
   {
	   Hashtable telnetHash = new Hashtable();
	   try
	   {
		   rs = conn.executeQuery("select * from nms_telnet_config");
		   while(rs.next())
		   {
			   TelnetConfig vo = new TelnetConfig();
			   vo.setId(rs.getInt("id"));
			   vo.setPrompt(rs.getString("prompt"));
			   vo.setPassword(rs.getString("password"));
			   vo.setUser(rs.getString("user"));
			   vo.setNodeID(rs.getInt("node_id"));
			   telnetHash.put(String.valueOf(rs.getInt("node_id")),vo);
		   }  	   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeMonitorDao.loadTelnetConfig()",e);
	   }
	   finally
	   {
		   conn.close();
	   }
	   return telnetHash;
   }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NodeMonitor vo = new NodeMonitor();
	   try
	   {
		  vo.setId(rs.getInt("id"));
		  vo.setNodeID(rs.getInt("node_id"));
		  vo.setMoid(rs.getString("moid"));
		  vo.setDescr(rs.getString("descr"));
		  vo.setUnit(rs.getString("unit"));
		  vo.setThreshold(rs.getInt("threshold"));
	      vo.setCompare(rs.getInt("compare"));
	      vo.setCompareType(rs.getInt("compare_type"));
	      vo.setUpperTimes(rs.getInt("upper_times"));
	      vo.setAlarmInfo(rs.getString("alarm_info"));
	      vo.setEnabled(rs.getInt("enabled")==1?true:false);
	      vo.setAlarmLevel(rs.getInt("alarm_level"));
	      vo.setPollInterval(rs.getInt("poll_interval"));
	      vo.setIntervalUnit(rs.getString("interval_unit"));
	      vo.setIp(rs.getString("node_ip"));
	      vo.setCategory(rs.getString("category"));
	      vo.setNodetype(rs.getString("nodetype"));
	      vo.setSubentity(rs.getString("subentity"));
	      vo.setLimenvalue0(rs.getInt("limenvalue0"));
	      vo.setLimenvalue1(rs.getInt("limenvalue1"));
	      vo.setLimenvalue2(rs.getInt("limenvalue2"));
	      vo.setTime0(rs.getInt("time0"));
	      vo.setTime1(rs.getInt("time1"));
	      vo.setTime2(rs.getInt("time2"));
	      vo.setSms0(rs.getInt("sms0"));
	      vo.setSms1(rs.getInt("sms1"));
	      vo.setSms2(rs.getInt("sms2"));
	   }
       catch(Exception e)
       {
 	       SysLogger.error("NodeMonitorDao.loadFromRS()",e); 
       }	   
       return vo;   
   }
   public String deleteByID(String nodeId) {//yangjun
       try {
			conn.executeUpdate("delete from topo_node_monitor where node_id="+ nodeId);
		} catch (Exception ex) {
			SysLogger.error("Error in NodeMonitorDao.deleteByID()", ex);
		} finally {
			conn.close();
		}
		return null;
	}
 //zhushouzhi----------------------------------
   public List loadall()
   {
	   return findByCriteria("select * from topo_node_monitor"); 
   }
}   