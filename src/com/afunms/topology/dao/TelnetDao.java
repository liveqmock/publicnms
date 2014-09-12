package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.util.*;
import com.afunms.topology.model.TelnetConfig;
import com.afunms.common.base.*;

public class TelnetDao extends BaseDao implements DaoInterface
{
   public TelnetDao()
   {
	   super("server_telnet_config");	   	  
   }
    
   public boolean update(BaseVo baseVo)
   {
	   TelnetConfig vo = (TelnetConfig)baseVo;
	   StringBuffer sql = new StringBuffer(50);
	   sql.append("update server_telnet_config set users='");
	   sql.append(vo.getUser());
	   sql.append("',password='");
	   sql.append(vo.getPassword());
	   sql.append("',prompt='");
	   sql.append(vo.getPrompt());
	   sql.append("' where node_id=");
	   sql.append(vo.getNodeID());
			
	   return saveOrUpdate(sql.toString());   
   }
   
   public boolean save(BaseVo baseVo)
   {
	   return false;
   }
   
   public TelnetConfig findByNodeID(int nodeId)
   {
	   List list = findByCriteria("select * from sys_pwdbackup_telnetconfig where node_id=" + nodeId);
	   if(list.size()>0)
		  return (TelnetConfig)list.get(0);  
	   else
		  return null; 
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   TelnetConfig vo = new TelnetConfig();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setNodeID(rs.getInt("node_id"));
		   vo.setUser(SysUtil.ifNull(rs.getString("users")));
		   vo.setPassword(SysUtil.ifNull(rs.getString("password")));
		   vo.setPrompt(SysUtil.ifNull(rs.getString("prompt")));
	   }
       catch(Exception e)
       {
 	       SysLogger.error("TelnetDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}