/**
 * <p>Description: active_server_alarm</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project 衡水信用社
 * @date 2007-3-23
 */

package com.afunms.application.dao;

import java.sql.ResultSet;

import com.afunms.application.model.ActiveServerAlarm;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;

public class ActiveServerAlarmDao extends BaseDao implements DaoInterface
{
   public ActiveServerAlarmDao()
   {
	   super("active_server_alarm");
   }
   
   public boolean save(BaseVo baseVo)
   {
	   ActiveServerAlarm vo = (ActiveServerAlarm)baseVo;	   
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into active_server_alarm(id,ip_address,event,log_time)values(");
	   sql.append(getNextID());
	   sql.append(",'");	   
	   sql.append(vo.getIpAddress());
	   sql.append("','");	   	   	  
	   sql.append(vo.getEvent());
	   sql.append("','");
	   sql.append(vo.getLogTime());
	   sql.append("')");
	   
	   return saveOrUpdate(sql.toString());
   }
      
   public boolean update(BaseVo vo)
   {
	   return false;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   ActiveServerAlarm vo = new ActiveServerAlarm();
       try
       {
		   vo.setId(rs.getInt("id"));		   
		   vo.setIpAddress(rs.getString("ip_address"));		   
		   vo.setEvent(rs.getString("event"));		   		   
		   vo.setLogTime(rs.getString("log_time"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("ActiveServerAlarmDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}   