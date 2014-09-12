/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


import com.afunms.application.model.EmailHistory;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class EmailHistoryDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   public EmailHistoryDao()
   {
	   super("nms_email_history");
   }

   public boolean update(BaseVo baseVo)
   {
	   return false;
   }
   
   public boolean save(BaseVo baseVo)
   {
	   EmailHistory vo = (EmailHistory)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();	
	   Date cc = tempCal.getTime();
	   String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into nms_email_history(email_id,is_canconnected,reason,mon_time)values(");
	   sql.append("'");
	   sql.append(vo.getEmail_id());
	   sql.append("','");
	   sql.append(vo.getIs_canconnected());
	   sql.append("','");
	   sql.append(vo.getReason());
	   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("','");
		   sql.append(time);
		   sql.append("'");
	   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',");
		   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   sql.append("");
	   }
	   sql.append(")");
//	   
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from nms_email_history where id=" + id);
		   conn.executeBatch();
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("Emailmonitor_historyDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return result;
   }
   /*
   public String[] getAvailability(Integer url_id,InitCoordinate initer,String type)throws Exception{
		String[] value={"",""};
		String starttime=initer.getBefore();
		String totime=initer.getNow();
		try {
			
			String parm=" aa.mon_time >= '";
			parm=parm+starttime;
			parm=parm+"' and aa.mon_time <= '";
			parm=parm+totime;
			parm=parm+"'";
			String sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+url_id+" and "+parm;
			rs = conn.executeQuery(sql);
			while(rs.next()){
				value[0] = rs.getInt("stype")+"";
				value[1] = rs.getInt("countid")+"";
				value[1]=new Integer(new Integer(value[1]).intValue()-new Integer(value[0]).intValue()).toString();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
		}
		*/
   /*
	public String[] getAvailability(Integer url_id,String starttime,String totime,String type)throws Exception{
		String[] value={"",""};
		try {
			String parm=" aa.mon_time >= '";
			parm=parm+starttime;
			parm=parm+"' and aa.mon_time <= '";
			parm=parm+totime;
			parm=parm+"'";
			String sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+url_id+" and "+parm;
			rs = conn.executeQuery(sql);
			while(rs.next()){
				value[0] = rs.getInt("stype")+"";
				value[1] = rs.getInt("countid")+"";
				value[1]=new Integer(new Integer(value[1]).intValue()-new Integer(value[0]).intValue()).toString();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
		}   
		
		*/
	//当flag = 0 时,则取出的是否连接为0/1,当flag = 1时,取出的数据为中文的是否连接成功
	/*
	public Vector getByUrlid(Integer urlid,String starttime,String totime,int flag) throws Exception{
		List list = new ArrayList();
   	Vector returnVal = new Vector();
   	Session session =null;
   	try{
   		WebConfigDao configdao = new WebConfigDao();
   		WebConfig webconfig = (WebConfig)configdao.findByID(urlid+"");
   		
   		String sql = "select a.is_canconnected,a.is_valid,a.is_refresh," +
   					"a.reason,a.mon_time,a.condelay from nms_web_history a where " +
   					"a.url_id="+urlid+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+totime+"')";
   		rs = conn.executeQuery(sql);
		while(rs.next()){
			Object[] obj = new Object[7];
			Hashtable ht = new Hashtable();
			if (flag ==0){
				obj[0] = rs.getInt("is_canconnected")+"";
				obj[1] = rs.getInt("is_valid")+"";
				obj[2] = rs.getInt("is_refresh")+"";
			}else{
				if(0==(rs.getInt("is_canconnected"))){
					obj[0] = "连接失败";
				}else{
					obj[0] = "连接成功";
				}
				if(0==(rs.getInt("is_valid"))){
					obj[1] ="服务无效";
				}else{
					obj[1] = "服务有效";
				}				
				if(0==(rs.getInt("is_refresh"))){
					obj[2] = "未刷新";
				}else{
					obj[2] = "页面刷新";
				}
			}
			obj[3] = rs.getString("reason");
				Calendar cal = Calendar.getInstance();
		       Date newdate = new Date();
		       newdate.setTime(rs.getTimestamp("mon_time").getTime());
		       cal.setTime(newdate);
		       obj[4] = sdf.format(cal.getTime());
		       obj[5] = rs.getInt("condelay");
		       obj[6] = webconfig.getName();
				ht.put("conn",obj[0]);
				ht.put("valid",obj[1]);
				ht.put("refresh",obj[2]);
				ht.put("reason",obj[3]);
				ht.put("mon_time",obj[4]);
				if (obj[5] == null)obj[5]="0";
				ht.put("condelay",obj[5]);
				ht.put("url_name",obj[6]);
				returnVal.addElement(ht);
				ht = null;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
		
	}
	*/
   
	public Vector getByMailid(Integer mailid,String starttime,String totime,Integer isconnected) throws Exception{
		List list = new ArrayList();
    	Vector returnVal = new Vector();
    	try{
    		String sql="";
    		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
    			sql = "select is_canconnected," +
				"reason,mon_time from nms_email_history where " +
				"email_id="+mailid+" and is_canconnected="+isconnected +" and "+
				" mon_time >= '"+starttime +"' "+
				" and  mon_time <= '"+totime+"'";
    		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
    			sql = "select is_canconnected," +
				"reason,mon_time from nms_email_history where " +
				"email_id="+mailid+" and is_canconnected="+isconnected +" and "+
				" mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') "+" "+
				" and  mon_time <= "+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS') "+"";
    		}
			
//			SysLogger.info(sql);
			rs = conn.executeQuery(sql);
			while(rs.next()){
				Object[] obj = new Object[3];
				obj[0] = rs.getString("is_canconnected");
				Hashtable ht = new Hashtable();
				/*
				if("0".equals(obj[0].toString())){
					obj[0] = "服务失败";
				}else{
					obj[0] = "服务成功";
				}
				*/
				obj[1] = rs.getString("reason");	
				if(obj[1] == null) 
					obj[1] = "";
				
				Calendar cal = Calendar.getInstance();
			       Date newdate = new Date();
			       newdate.setTime(rs.getTimestamp("mon_time").getTime());
			       cal.setTime(newdate);
			       obj[2] = sdf.format(cal.getTime());
				
				
				//Calendar c = (Calendar)obj[2];
				//obj[2] = sdf.format(c.getTime());
				ht.put("conn",obj[0]);
				ht.put("reason",obj[1]);
				ht.put("mon_time",obj[2]);
				returnVal.addElement(ht);
				ht = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
	}
	public String[] getAvailability(Integer mail_id,String starttime,String totime,String type)throws Exception{
		String[] value={"",""};
		try {
			String sql = "";
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.mon_time >= '";
				parm=parm+starttime;
				parm=parm+"' and aa.mon_time <= '";
				parm=parm+totime;
				parm=parm+"'";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.email_id) as countid from nms_email_history aa where aa.email_id="+mail_id+" and "+parm;
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.mon_time >= ";
				parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+" and aa.mon_time <= ";
				parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+"";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.email_id) as countid from nms_email_history aa where aa.email_id="+mail_id+" and "+parm;
			}
			rs = conn.executeQuery(sql);
			while(rs.next()){
				value[0] = rs.getInt("stype")+"";
				value[1] = rs.getInt("countid")+"";
				value[1]=new Integer(new Integer(value[1]).intValue()-new Integer(value[0]).intValue()).toString();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
		}
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   EmailHistory vo = new EmailHistory();
       try
       {
		   vo.setId(rs.getInt("id"));
		   vo.setIs_canconnected(rs.getInt("is_canconnected"));
		   Calendar cal = Calendar.getInstance();
	       Date newdate = new Date();
	       newdate.setTime(rs.getTimestamp("mon_time").getTime());
	       cal.setTime(newdate);
		   vo.setMon_time(cal);
		   vo.setReason(rs.getString("reason"));
		   vo.setEmail_id(rs.getInt("email_id"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("Urlmonitor_historyDao.loadFromRS()",e); 
       }	   
       return vo;
   }
 //yangjun add
   public Hashtable getPingData(Integer email_id,String starttime,String endtime) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				sql = "select a.is_canconnected,a.reason,a.mon_time from nms_email_history a where " +
				"a.email_id="+email_id+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+endtime+"')";
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				sql = "select a.is_canconnected,a.reason,a.mon_time from nms_email_history a where " +
				"a.email_id="+email_id+" and (a.mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.mon_time <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+")";
			}
			SysLogger.info(sql);
			int i = 0;
			double avgput1 = 0;
			rs = conn.executeQuery(sql);
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("is_canconnected");
					String collecttime = rs.getString("mon_time");
					String reason = rs.getString("reason");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, reason);
					avgput1 = avgput1 + Float.parseFloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			hash.put("list", list1);
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput1", CEIString.round(avgput1/ list1.size(), 2)+"");
			} else {
				hash.put("avgput1", "0");
			}
		}
		return hash;
	}
   public Hashtable getPingDataById(Integer email_id,String starttime,String endtime) {
	   Hashtable hash = new Hashtable();
	   if (!starttime.equals("") && !endtime.equals("")) {
		   List list1 = new ArrayList();
		   String sql = "";
		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			   sql = "select a.is_canconnected,a.reason,a.mon_time from nms_email_history a where " +
			   "a.email_id="+email_id+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+endtime+"')";
		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			   sql = "select a.is_canconnected,a.reason,a.mon_time from nms_email_history a where " +
			   "a.email_id="+email_id+" and (a.mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.mon_time <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+")";
		   }
		   int i = 0;
		   double curPing=0;
		   double avgPing = 0;
		  double  minPing=0;
		   rs = conn.executeQuery(sql);
		   try {
			   while (rs.next()) {
				   i = i + 1;
				   Vector v = new Vector();
				   String thevalue = rs.getString("is_canconnected");
				   String collecttime = rs.getString("mon_time");
				  // String reason = rs.getString("reason");
				   thevalue=String.valueOf(Integer.parseInt(thevalue)*100);
				   v.add(0, thevalue);
				   v.add(1, collecttime);
				   v.add(2, "%");
				   avgPing = avgPing + Float.parseFloat(thevalue);
				   curPing=Float.parseFloat(thevalue);
				   if (curPing<minPing)
					 minPing=curPing;
				    list1.add(v);
			   }
			   
		   } catch (SQLException e) {
			   e.printStackTrace();
		   } finally {
			   try {
				   if (rs!=null)
				   rs.close();
				   if (conn!=null)
					conn.close();
				
			   } catch (SQLException e) {
				   e.printStackTrace();
			   }
		   }
		   hash.put("list", list1);
		   if (list1 != null && list1.size() > 0) {
			   hash.put("avgPing", CEIString.round(avgPing/ list1.size(), 2)+"");
		   } else {
			   hash.put("avgPing", "0");
		   }
		   hash.put("minPing", minPing+"");
		   hash.put("curPing", curPing+"");
	   }
	   return hash;
   }
}   