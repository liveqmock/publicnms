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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslog;

public class NetSyslogDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public NetSyslogDao()
  {
	  super("nms_netsyslog");	  
  }

  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_netsyslog order by id desc");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("NetSyslogDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  	}
  
  public boolean deleteByHostIp(String hostip)
  {	
	   String sql = "delete from nms_netsyslog where ipaddress='"+hostip+"'";
	   return saveOrUpdate(sql);			
  }

	public List getQuery(String starttime,String totime,String status,String level,String businessid,Integer nodeid) throws Exception{
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			s.append("select * from system_eventlist e where e.recordtime>= '"+starttime+"' " +
					"and e.recordtime<='"+totime+"'");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime
				+ "','YYYY-MM-DD HH24:MI:SS') " + "and e.recordtime<=to_date('"
				+ totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		
		
		if(!"99".equals(level)){
			s.append(" and e.level1="+level);
		}
		if(!"99".equals(status)){
			s.append(" and e.managesign="+status);
		}
		
		if (nodeid != null){
			if(nodeid.intValue()!=99){
				s.append(" and nodeid="+nodeid);
			}	
		}
		int flag = 0;
		if (businessid != null){
			if(businessid !="-1"){
				String[] bids = businessid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(flag==0){
								s.append(" and ( businessid = ',"+bids[i].trim()+",' ");
								flag = 1;
							}else{
								//flag = 1;
								s.append(" or businessid = ',"+bids[i].trim()+",' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
		s.append(" order by e.recordtime desc");		
		String sql = s.toString();
		SysLogger.info(sql);
		try{
			rs = conn.executeQuery(sql);
			while(rs.next())
	        	list.add(loadFromRS(rs));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
  
	public boolean save(BaseVo baseVo)
	{
		NetSyslog vo = (NetSyslog)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append("insert into nms_netsyslog(ipaddress,hostname,message,facility,priority,facilityname,priorityname,recordtime,businessid,category)values(");
			sql.append("\"");
			sql.append(vo.getIpaddress());
			sql.append("\",\"");
			sql.append(vo.getHostname());	
			sql.append("\",\"");
			sql.append(vo.getMessage());
			sql.append("\",");
			sql.append(vo.getFacility());
			sql.append(",");
			sql.append(vo.getPriority());
			sql.append(",\"");
			sql.append(vo.getFacilityName());
			sql.append("\",\"");
			sql.append(vo.getPriorityName());
			sql.append("\",\"");
			sql.append(recordtime);
			sql.append("\",\"");
			sql.append(vo.getBusinessid());
			sql.append("\",");
			sql.append(vo.getCategory());
			sql.append(")");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append("insert into nms_netsyslog(ipaddress,hostname,message,facility,priority,facilityname,priorityname,recordtime,businessid,category)values(");
			sql.append("'");
			sql.append(vo.getIpaddress());
			sql.append("','");
			sql.append(vo.getHostname());	
			sql.append("','");
			sql.append(vo.getMessage());
			sql.append("',");
			sql.append(vo.getFacility());
			sql.append(",");
			sql.append(vo.getPriority());
			sql.append(",'");
			sql.append(vo.getFacilityName());
			sql.append("','");
			sql.append(vo.getPriorityName());
			sql.append("',");
			sql.append("to_date('"+recordtime+"','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",'");
			sql.append(vo.getBusinessid());
			sql.append("',");
			sql.append(vo.getCategory());
			sql.append(")");
		}
		
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {
	  EventList vo = (EventList)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_eventlist set eventtype='");
		sql.append(vo.getEventtype());
		sql.append("',eventlocation='");
		sql.append(vo.getEventlocation());	
		sql.append("',content='");
		sql.append(vo.getContent());
		sql.append("',level1=");
		sql.append(vo.getLevel1());
		sql.append(",managesign=");
		sql.append(vo.getManagesign());
		sql.append(",bak='");
		sql.append(vo.getBak());
		sql.append("',recordtime='");
		sql.append(recordtime);
		sql.append("',reportman='");
		sql.append(vo.getReportman());
		sql.append("',nodeid=");
		sql.append(vo.getNodeid());
		sql.append(",businessid='");
		sql.append(vo.getBusinessid());
		sql.append("',oid=");
		sql.append(vo.getOid());
		sql.append(",subtype='");
		sql.append(vo.getSubtype());
		sql.append("',managetime='");
		sql.append(vo.getManagetime());  
		sql.append("',subentity='");
		sql.append(vo.getSubentity()); 
		sql.append("',category=");
		sql.append(vo.getSubentity());
		sql.append(" where id=");
		sql.append(vo.getId());
     
     try
     {
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
  
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from nms_netsyslog where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("EventListDao.delete()",e);	        
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
        rs = conn.executeQuery("select * from nms_netsyslog where id=" + id );
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
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NetSyslog vo = new NetSyslog();
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
          vo.setBusinessid(rs.getString("businessid"));
          vo.setCategory(rs.getInt("category"));
      }
      catch(Exception e)
      {
          SysLogger.error("NetSyslogDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }

   public BaseVo loadFromResultSet(ResultSet rs)
   {
	   NetSyslog vo = new NetSyslog();
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
          vo.setBusinessid(rs.getString("businessid"));
          vo.setCategory(rs.getInt("category"));
      }
      catch(Exception e)
      {
          SysLogger.error("NetSyslogDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }
	
	public List loadByPriority(String ipaddress, String priority, String starttime, String totime, String processname, String message) {
		
		String where = "";
		List list = null;
		try {
			if (null == priority || "null".equalsIgnoreCase(priority)) {//全部
				where = " where ipaddress = '" + ipaddress.trim() + "'";
			}else if(null != priority && "others".equals(priority)){
				where = " where trim(priorityname) != 'error' and trim(priorityname) != 'warning' and ipaddress = '" + ipaddress.trim() + "'";
			}else{
				where = " where trim(priorityname)= '" + priority.trim() + "' and ipaddress = '" + ipaddress.trim() + "'";
			}
			int totalCount = Integer.parseInt(getCountByWhere(where));
			where += " order by id desc";
			list = listByPage(1, where, totalCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}  
	public List loadByPriority(String where) {
		List list = null;
		try {
			int totalCount = Integer.parseInt(getCountByWhere(where));
			where += " order by id desc";
			list = listByPage(1, where, totalCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map getCountByUserAccout(String ipaddress, String starttime, String totime,
			String msgRegexp) {
		String where = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where ipaddress = '" + ipaddress + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "' and " + msgRegexp;
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			where = " where ipaddress = '" + ipaddress + "' and e.recordtime>= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') " +
				"and e.recordtime<=to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')  and " + msgRegexp;
		}
		 
		String sql = "select * from nms_netsyslog " + where;
		rs = conn.executeQuery(sql);
		//System.out.println("getCountByUserAccout sql = " + sql);
		NetSyslogDao dao = new NetSyslogDao();
		NetSyslog syslog = new NetSyslog();
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			while(rs.next()){
				syslog = (NetSyslog)dao.loadFromRS(rs);
				String message = syslog.getMessage();
				int begin = message.indexOf("目标帐户名:");
				if (begin == -1) {
					continue;
				}
				begin +=  "目标帐户名:".length();
				int end = message.indexOf("目标域:");
				String username = message.substring(begin, end);
				if(null == map.get(username.trim())){
					map.put(username.trim(), 1);
				}else{
					map.put(username.trim(), map.get(username.trim()) + 1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	public int getCountByEvent(String ipaddress, String starttime, String totime, String eventMsg) {
		String sql = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql = "select count(*) from nms_netsyslog where ipaddress = '" + ipaddress + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "' and " + eventMsg;
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sql = "select count(*) from nms_netsyslog where ipaddress = '" + ipaddress + "' and recordtime>= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') " +
				"and recordtime<=to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')and " + eventMsg;
		}
		
		rs = conn.executeQuery(sql);
		try {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
