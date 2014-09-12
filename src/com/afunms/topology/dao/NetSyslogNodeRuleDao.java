/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.topology.dao;

import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.ResultSet;


import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.BaseVo;
import com.afunms.topology.model.NetSyslogNodeRule;

public class NetSyslogNodeRuleDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public NetSyslogNodeRuleDao()
  {
	  super("nms_netsyslogrule_node");	  
  }
  
  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList();
     try
     {
         rs = conn.executeQuery("select * from nms_netsyslogrule_node order by id desc");
         while(rs.next())
        	list.add(loadFromRS(rs));
     }
     catch(Exception e)
     {
         //SysLogger.error("NetSyslogRuleDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {
	  boolean result = false;
	  NetSyslogNodeRule vo = (NetSyslogNodeRule)baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_netsyslogrule_node set facility='");
		sql.append(vo.getFacility());	 
		sql.append("' where id=");
		sql.append(vo.getId());
     
     try
     {
    	 //SysLogger.info(sql.toString());
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("EventListDao:update()",e);
     }
     finally
     {
    	 conn.close();
     }     
     return result;
  }
  
	
  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from nms_netsyslogrule_node where nodeid='" + id+"'" );
        if(rs.next())
           vo = loadFromRS(rs);
        else
        	return null;
     }
     catch(Exception e)
     {
         //SysLogger.error("EventListDao.findByID()",e);
         vo = null;
     }
     return vo;
  }
  
  public boolean deleteByNodeID(String id)
  {
	  boolean flag = false;
     BaseVo vo = null;
     try
     {
    	 conn.executeUpdate("delete from nms_netsyslogrule_node where nodeid='" + id+"'" );
    	 flag = true;
     }
     catch(Exception e)
     {
         vo = null;
     
     }
     return flag;
  }
  
  public BaseVo findByIpaddress(String ip)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select n.id,n.nodeid,n.facility from nms_netsyslogrule_node n,topo_host_node t where n.nodeid=t.id and  t.ip_address='" + ip+"'" );
        if(rs.next())
           vo = loadFromRS(rs);
        else
        	return null;
     }
     catch(Exception e)
     {
         //SysLogger.error("EventListDao.findByID()",e);
         vo = null;
     }
     return vo;
  }
  
  public String findIdByIpaddress(String ip)
  {
	  String strId = "";
     try
     {
        rs = conn.executeQuery("select id from topo_host_node where ip_address='" + ip+"'" );
        if(rs.next())
        	strId = rs.getString("id");
        else
        	return null;
     }
     catch(Exception e)
     {
         SysLogger.error("EventListDao.findByID()",e);
     }
     return strId;
  }
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NetSyslogNodeRule vo = new NetSyslogNodeRule();
      try
      {
//    	  System.out.println("com.afunms.topology.dao--------NetSyslogNodeRuleDao.java----164hang----->"+rs);
//    	  System.out.println("com.afunms.topology.dao--------NetSyslogNodeRuleDao.java----164hang----->"+rs.getLong("id")+"");
          vo.setId(rs.getLong("id")+"");
          vo.setNodeid(rs.getString("nodeid"));
          vo.setFacility(rs.getString("facility"));
          
      }
      catch(Exception e)
      {
          SysLogger.error("NetSyslogRuleDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }
   
 //quzhi add
   public boolean updateAlarmAll(String bid,String hostid)
   {	
	   String sql = "update nms_netsyslogrule_node set facility = '"+bid+"' where nodeid ='"+hostid+"'";
	   SysLogger.info(sql);
	   return saveOrUpdate(sql);			
   }

   public boolean save(BaseVo baseVo) {
	  boolean result = false;
	  NetSyslogNodeRule vo = (NetSyslogNodeRule)baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_netsyslogrule_node(nodeid,facility) values('"+vo.getNodeid()+"','");	
		sql.append(vo.getFacility());	 
		sql.append("')");
     
     try
     {
    	 //SysLogger.info(sql.toString());
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         //SysLogger.error("EventListDao:update()",e);
     }
     finally
     {
    	 conn.close();
     }     
     return result;
   }
}
