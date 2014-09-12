/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.Session;


import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

public class SyslogDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public SyslogDao()
  {
	  super("nms_netsyslog");	  
  }
  
  
  	//处理Syslog得到的数据，放到历史表里
	public synchronized boolean createSyslogData(Syslog syslog) {
		if (syslog == null )return false;
		try{
			//dataGate = new DataGate();
			//判断是否是已经监视的设备
			Host host = (Host)PollingEngine.getInstance().getNodeByIP(syslog.getIpaddress());
			if (host ==null){
				return false;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = syslog.getIpaddress();
			String allipstr = SysUtil.doip(ip);
			Calendar tempCal = Calendar.getInstance();							
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);					
			String tablename = "log"+allipstr;
			String sql = "";
			
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				sql = "insert into "+tablename+"(ipaddress,hostname,message,eventid,facility,priority,priorityName,facilityName,"
				+"processId,processName,processIdStr,recordtime,username) "
				+"values(\""+ip+"\",\""+syslog.getHostname()+"\",\""+syslog.getMessage()+"\","+syslog.getEventid()+","+syslog.getFacility()+","+syslog.getPriority()+",\""+syslog.getPriorityName()+"\",\""+syslog.getFacilityName()+"\","
				+syslog.getProcessid()+",\""+syslog.getProcessname()+"\",\""+syslog.getProcessidstr()+"\",\""+time+"\",\""+syslog.getUsername()+"\")";
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				sql = "insert into "+tablename+"(ipaddress,hostname,message,eventid,facility,priority,priorityName,facilityName,"
				+"processId,processName,processIdStr,recordtime,username) "
				+"values('"+ip+"','"+syslog.getHostname()+"','"+syslog.getMessage()+"',"+syslog.getEventid()+","+syslog.getFacility()+","+syslog.getPriority()+",'"+syslog.getPriorityName()+"','"+syslog.getFacilityName()+"',"
				+syslog.getProcessid()+",'"+syslog.getProcessname()+"','"+syslog.getProcessidstr()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),'"+syslog.getUsername()+"')";
			}
			
								
			System.out.println(sql); 
			conn.executeUpdate(sql);		
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			
			
		}
		return true;
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

//konglq 修改    c.priorityname
	public List getQuery(String ipaddress,String starttime,String totime,String priorityname) throws Exception{
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			s.append("select * from log"+doip(ipaddress)+" e where e.recordtime>= '"+starttime+"' " +
					"and e.recordtime<='"+totime+"'");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			s.append("select * from log"+doip(ipaddress)+" e where e.recordtime>= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and e.recordtime<=to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')");
		}
		
		
		if(priorityname==null || !priorityname.equals("all")){
			s.append(" and e.priorityname='"+priorityname+"' ");
		}
		s.append(" order by e.id desc");		
		String sql = s.toString();
		SysLogger.info(sql);
		try{
			rs = conn.executeQuery(sql);
			while(rs.next())
	        	list.add(loadFromRS(rs));
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return list;
	}
  
	public Syslog getSyslogData(int id,String ip) throws Exception {
		StringBuffer s = new StringBuffer();
		
		List list = new ArrayList();
	 	ResultSet rs = null;
		
		s.append("select * from log"+doip(ip)+" s where id="+id);
		s.append(" order by s.id desc ");
		String sql = s.toString();
		System.out.println(sql);		
		Session session = null;
		Syslog syslog = new Syslog();
		try{
			rs = conn.executeQuery(sql);
			while (rs.next()) {		
				syslog = (Syslog)loadFromRS(rs);
				break;
			}
	        rs.close();

		}
		catch(Exception e){
			//this.endTransaction(false);
			e.printStackTrace();
		}finally{
				if (rs != null)
				rs.close();
		}
					
		return syslog;
		
		// TODO Auto-generated method stub
		//return null;
	}
	public boolean save(BaseVo baseVo)
	{
		Syslog vo = (Syslog)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_netsyslog(ipaddress,hostname,message,facility,priority,facilityname,priorityname,recordtime,businessid,processid,processname,processidstr)values(");
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
		sql.append(vo.getProcessid());
		sql.append(",\"");
		sql.append(vo.getProcessname());
		sql.append("\",\"");
		sql.append(vo.getProcessname());
		sql.append("\")");
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
		sql.append("' where id=");
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
	   Syslog vo = new Syslog();
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
          vo.setEventid(rs.getInt("eventid"));
          vo.setProcessid(rs.getInt("processid"));
          vo.setProcessidstr(rs.getString("processidstr"));
          vo.setProcessname(rs.getString("processname"));
          vo.setUsername(rs.getString("username"));
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
          SysLogger.error("SyslogDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   } 
	String doip(String ip){
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}
}
