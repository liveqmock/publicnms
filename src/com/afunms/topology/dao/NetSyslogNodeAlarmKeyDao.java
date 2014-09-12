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
import com.afunms.topology.model.NetSyslogNodeAlarmKey;

public class NetSyslogNodeAlarmKeyDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public NetSyslogNodeAlarmKeyDao()
  {
	  super("nms_netsyslogalarmkey_node");	  
  }
  
  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList();
     try
     {
         rs = conn.executeQuery("select * from nms_netsyslogalarmkey_node order by id desc");
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
	  NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey)baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_netsyslogalarmkey_node set keywords='");
		sql.append(vo.getKeywords());	 
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
        rs = conn.executeQuery("select * from nms_netsyslogalarmkey_node where nodeid=" + id );
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
  
  
  public BaseVo findByIpaddress(String ip)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select n.id,n.nodeid,n.keywords from nms_netsyslogalarmkey_node n,topo_host_node t where n.nodeid=t.id and  t.ip_address='" + ip+"'" );
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
	   NetSyslogNodeAlarmKey vo = new NetSyslogNodeAlarmKey();
      try
      {
          vo.setId(rs.getLong("id")+"");
          vo.setNodeid(rs.getString("nodeid"));
          vo.setKeywords(rs.getString("keywords"));
          vo.setLevel(rs.getString("levels"));
      }
      catch(Exception e)
      {
          SysLogger.error("NetSyslogRuleDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }
   
   public boolean updateAlarmAll(String bid,String keywords){	
	   String sql = "update nms_netsyslogalarmkey_node set keywords = '"+bid+"' where keywords ='"+keywords+"'";
	   SysLogger.info(sql);
	   return saveOrUpdate(sql);			
   }

   public boolean save(BaseVo baseVo) {
	  boolean result = false;
	  NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey)baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_netsyslogalarmkey_node(nodeid,keywords) values('"+vo.getNodeid()+"','");	
		sql.append(vo.getKeywords());	 
		sql.append("')");
     
     try{
    	 //SysLogger.info(sql.toString());
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
     }
     finally
     {
    	 conn.close();
     }     
     return result;
   }
   
   /**
    * 删除已知节点的syslog告警关键字信息
    * @param nodeid
    * @return
    */
   public boolean delete(String nodeid) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("delete from nms_netsyslogalarmkey_node where nodeid = '"
				+ nodeid + "'");
		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
	    return result;
	}

   /**
    * 保存告警关键字信息列表
    * @param alarmkeyDetailList
    */
	public void save(List alarmkeyDetailList) {
		try {
			for(int i=0; i<alarmkeyDetailList.size(); i++){
				NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey)alarmkeyDetailList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into nms_netsyslogalarmkey_node(nodeid,keywords,levels) values('"+vo.getNodeid()+"','");	
				sql.append(vo.getKeywords());
				sql.append("','");
				sql.append(vo.getLevel());
				sql.append("')");
				try {
					conn.addBatch(sql.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
	}
}
