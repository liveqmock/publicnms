/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.NetSyslogEvent;

public class NetSyslogEventDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public NetSyslogEventDao()
  {
	  super("nms_netsyslog");
  }
  /**
   * 根据 where 获取到告警数
   * @author nielin
   * @date 2010-08-05
   * @param where
   * @return
   */
  public int getCountByWhere(String table, String where){
  	try {
  		String sql = "select count(*) as cnt from " + table + where;
  		rs = conn.executeQuery(sql);
  		if(rs.next()){
			   return rs.getInt("cnt");
  		}
  	} catch (SQLException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}finally{
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
  	}
  	return 0;
  }
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NetSyslogEvent vo = new NetSyslogEvent();
      try
      {
          vo.setId(rs.getLong("id"));
          vo.setIpaddress(rs.getString("ipaddress"));
          vo.setHostname(rs.getString("hostname"));
          vo.setMessage(rs.getString("message"));
          vo.setFacility(rs.getInt("facility"));
          vo.setPriority(rs.getInt("priority"));
          vo.setFacilityName(rs.getString("facilityName"));
          vo.setPriorityName(rs.getString("priorityName"));
          Calendar cal = Calendar.getInstance();
          Date newdate = new Date();
          newdate.setTime(rs.getTimestamp("recordtime").getTime());
          cal.setTime(newdate);
          vo.setRecordtime(cal);
          //nms_netsyslog表没有下列字段 而log有
          try{
	          vo.setEventid(rs.getInt("eventid"));
          }catch(Exception e) {
        	  vo.setEventid(-1);
          }

          try {
        	  vo.setProcessId(rs.getInt("processId"));
          }catch(Exception e) {
        	  vo.setProcessId(-1);
          }
          
          try {
        	  vo.setProcessIdStr(rs.getString("processidstr"));
          }catch(Exception e) {
        	  vo.setProcessIdStr("");
          }
          
          try {
        	  vo.setProcessName(rs.getString("processname"));
          }catch(Exception e) {
        	  vo.setProcessName("");
          }
      }
      catch(Exception e)
      {
          SysLogger.error("NetSyslogEventDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }

   public BaseVo findByID(String table, String id)
   {
      BaseVo vo = null;
      try
      {
         rs = conn.executeQuery("select * from " + table + " where id=" + id );
         if(rs.next())
            vo = loadFromRS(rs);
      }
      catch(Exception e)
      {
          SysLogger.error("EventListDao.findByID()",e);
          vo = null;
      }
      finally
      {
         conn.close();
      }
      return vo;
   }
   /**
    * 有条件分页显示
    */
	public List listByPage(String table, int currentpage, String where, int perpage) {
			   List list = new ArrayList();
			   try 
			   {	
				   rs = conn.executeQuery("select count(*) from " + table + " " + where);
				   if(rs.next())
					   jspPage = new JspPage(perpage,currentpage,rs.getInt(1));

				   rs = conn.executeQuery("select * from " + table + " " + where );
				   //System.out.println("NetSyslogEventDao.java:124==> select * from " + table + " " + where );
				   int loop = 0;
				   while(rs.next()){
					  loop++;
					  if(loop<jspPage.getMinNum()) continue;
					  list.add(loadFromRS(rs));
					  if(loop==jspPage.getMaxNum()) break;
				   }
			   } 
			   catch (Exception e) 
			   {
				   SysLogger.error("BaseDao.listByPage()",e);
				   list = null;
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

public boolean save(BaseVo vo) {
	// TODO Auto-generated method stub
	return false;
}

public boolean update(BaseVo vo) {
	// TODO Auto-generated method stub
	return false;
}
	public int getCountByPriority(String ipaddress, String starttime,
			String totime, String priority) {
		String where = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where ipaddress = '" + ipaddress.trim() + "' and priorityname like '%" + priority.trim() + "%' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where ipaddress = '" + ipaddress.trim() + "' and priorityname like '%" + priority.trim() + "%' and recordtime>= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') " +
				"and recordtime<=to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS') ";
		}
		 
//		String sql = "select count(*) from nms_netsyslog " + where;
		String sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + where;
		rs = conn.executeQuery(sql);
		//System.out.println("getCountByPriority sql = " + sql);
		try {
			while(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getCountByEvent(String ipaddress, String starttime, String totime,
			String eventMsg) {
		String where = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where recordtime >= '" + starttime + "' and recordtime <= '" + totime + "' and " + eventMsg;
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where recordtime>= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') " +
				"and recordtime<=to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')  and " + eventMsg;
		}
		 
//		String sql = "select count(*) from nms_netsyslog " + where;
		String sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + where;
		rs = conn.executeQuery(sql);
		//System.out.println("getCountByPriority sql = " + sql);
		try {
			while(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}  
}
