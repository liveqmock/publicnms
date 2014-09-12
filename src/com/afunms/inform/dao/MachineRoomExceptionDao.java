/**
 * <p>Description: nms_alarm_info</p>
 * <p>Company:dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-13
 */

package com.afunms.inform.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.inform.model.MachineRoomException;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;

public class MachineRoomExceptionDao extends BaseDao
{
   public MachineRoomExceptionDao()
   {
	   super("machine_room_exception");
   }
      
   public List findByIP(String address)
   {
	   String sql = "select * from machine_room_exception where ip_address='" + address + "' order by log_time desc";
	   return findByCriteria(sql);
   }

   public List findByDay(String day)
   {	   	   
	   String sql = "select * from machine_room_exception where substring(log_time,1,10)='" + day + "' order by log_time desc";
	   return findByCriteria(sql);
   }

   public void insert(List list)
   {	   
	   for(int i=0;i<list.size();i++)
	   {
		    MachineRoomException vo = (MachineRoomException)list.get(i);
		    StringBuffer sql = new StringBuffer(100);
		    sql.append("insert into machine_room_exception(id,ip_address,message,alarm_level,category,log_time)values('");
		    sql.append((new java.util.Date()).getTime());
		    sql.append("','");
			sql.append(vo.getIpAddress());
			sql.append("','");
			sql.append(vo.getMessage());
			sql.append("',");
			sql.append(vo.getLevel());
			sql.append(",");
			sql.append(vo.getCategory());
			sql.append(",'");
			sql.append(vo.getLogTime());
			sql.append("')");
            conn.addBatch(sql.toString());
	   }
	   conn.executeBatch();
	   conn.close();
   }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   MachineRoomException vo = new MachineRoomException();
       try
       {
		   vo.setId(rs.getString("id"));
		   vo.setIpAddress(rs.getString("ip_address"));
		   vo.setMessage(rs.getString("message"));		   
		   vo.setLevel(rs.getInt("alarm_level"));
		   vo.setCategory(rs.getInt("category"));
		   vo.setLogTime(rs.getString("log_time"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("MachineRoomExceptionDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
}