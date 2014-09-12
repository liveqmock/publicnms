/**
 * <p>Description: nms_alarm_info</p>
 * <p>Company:dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-13
 */

package com.afunms.inform.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.inform.model.Alarm;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;

public class AlarmDao extends BaseDao implements DaoInterface
{
   public AlarmDao()
   {
	   super("nms_alarm_message");
   }

   public List listByPage(int curpage)
   {
	   List list = new ArrayList();	   
	   try 
	   {
		   rs = conn.executeQuery("select count(*) from nms_alarm_message");
		   if(rs.next())
			   jspPage = new JspPage(curpage,rs.getInt(1));

		   rs = conn.executeQuery("select * from nms_alarm_message order by log_time desc");
		   int loop = 0;
		   while(rs.next())
		   {
			   loop++;
			   if(loop<jspPage.getMinNum()) continue;
			   list.add(loadFromRS(rs));
			   if(loop==jspPage.getMaxNum()) break;
		   }
	   } 
	   catch (Exception e) 
	   {
		   SysLogger.error("AlarmDao.listByPage()",e);
		   list = null;
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public List listNewAlarm(String day)
   {
	   List list = new ArrayList();	   
	   try 
	   {
		   String sql = "select * from nms_alarm_message a where substring(a.log_time,1,10)='"
			   + day + "' order by a.log_time desc";
		   rs = conn.executeQuery(sql);
		   int loop = 0;
		   while(rs.next())
		   {
			  list.add(loadFromRS(rs));			  
			  if(loop > 5) break;
			  loop++;
		   }
	   } 
	   catch (Exception e) 
	   {
		   SysLogger.error("AlarmDao.listNewAlarm()",e);
		   list = null;
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;  
   }
   
   public List listByCategory(String snap,String day)
   {
	   String sql = null;
	   if(snap==null)
		  sql = "select * from nms_alarm_message where substring(log_time,1,10)='" + day + "'";
	   else if(snap.equals("network"))
		  sql = "select * from nms_alarm_message where substring(log_time,1,10)='" + day + "' and category<4";
	   else if(snap.equals("server"))
	      sql = "select * from nms_alarm_message where substring(log_time,1,10)='" + day + "' and category=4";
	   else if(snap.equals("app"))
		  sql = "select * from nms_alarm_message where substring(log_time,1,10)='" + day + "' and category>=50";

	   return findByCriteria(sql + " order by log_time desc");   
   }

   public List findByIP(String address)
   {
	   String sql = "select * from nms_alarm_message where ip_address='" + address + "' order by log_time desc";
	   return findByCriteria(sql);
   }

   public void deleteAll()
   {	   	   
	   saveOrUpdate("delete from nms_alarm_message");
   }

   public boolean update(BaseVo baseVo)
   {
	   return false;
   }
   
   public boolean save(BaseVo baseVo)
   {
	   return false;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Alarm vo = new Alarm();
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
   	       SysLogger.error("AlarmDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
}