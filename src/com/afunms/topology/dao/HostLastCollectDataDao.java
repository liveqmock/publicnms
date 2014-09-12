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
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.HostNode;
import com.afunms.discovery.Host;
import com.afunms.polling.node.*;
import com.afunms.polling.snmp.Hostlastcollectdata;
import com.afunms.polling.base.*;

public class HostLastCollectDataDao extends BaseDao implements DaoInterface
{
   public HostLastCollectDataDao()
   {
	   super("hostlastcollectdata");	   	  
   }
   
   public List findByCondition(String key,String value)
   {	  
	   return findByCriteria("select * from hostlastcollectdata where " + key + "='" + value + "'");   
   }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Hostlastcollectdata vo = new Hostlastcollectdata();
       try
       {
    	   vo.setId(new Long(rs.getInt("id")));
    	   vo.setIpaddress(rs.getString("ipaddress"));
    	   vo.setRestype(rs.getString("restype"));
    	   vo.setCategory(rs.getString("category"));
    	   vo.setEntity(rs.getString("entity"));
    	   vo.setSubentity(rs.getString("subentity"));
    	   vo.setThevalue(rs.getString("thevalue"));
    	   Date timestamp = rs.getTimestamp("collecttime");
    	   Calendar date=Calendar.getInstance();
    	   date.setTime(timestamp);
    	   vo.setCollecttime(date);
    	   vo.setUnit(rs.getString("unit"));
       }
       catch(Exception e)
       {
 	       SysLogger.error("HostLastCollectDataDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
   
   public boolean save(BaseVo vo)
   {
	   return false;
   }
   
   public boolean update(BaseVo baseVo)
   {	
	   HostNode vo = (HostNode)baseVo;
	   int managed=0;
	   if(vo.isManaged())managed =1;
	   String sql = "update topo_host_node set alias='" + vo.getAlias() + "',managed="+managed+" where id=" + vo.getId();
	   return saveOrUpdate(sql);			
   }
  
   
   public String loadOneColFromRS(ResultSet rs){
	   return "";
   }
}
