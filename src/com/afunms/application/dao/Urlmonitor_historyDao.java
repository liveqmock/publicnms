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
import com.afunms.application.model.Urlmonitor_history;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.application.model.*;

public class Urlmonitor_historyDao extends BaseDao implements DaoInterface  
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   public Urlmonitor_historyDao()
   {
	   super("nms_web_history");
   }
   
   /**
	  * 删除所有记录
	  */
  public boolean deleteByUrl(String id) {
  	
	   boolean result = false;
	   try
	   {
	       conn.addBatch("delete from nms_web_history where url_id=" + id);
	       conn.executeBatch();
	       result = true;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("Urlmonitor_historyDao.delete()",ex);
	       result = false;
	   }
	   return result;
 }

   public boolean update(BaseVo baseVo)
   {
	   return false;
   }
   
   public boolean save(BaseVo baseVo)
   {
	   Urlmonitor_history vo = (Urlmonitor_history)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into nms_web_history(url_id,is_canconnected,is_valid,is_refresh,reason,mon_time,condelay,pagesize,key_exist,change_rate)values(");
	   sql.append(vo.getUrl_id());
	   sql.append(",");
	   sql.append(vo.getIs_canconnected());
	   sql.append(",");
	   sql.append(vo.getIs_valid());   
	   sql.append(",");
	   sql.append(vo.getIs_refresh());
	   sql.append(",'");
	   sql.append(vo.getReason());
	   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("','");
		   sql.append(time);
		   sql.append("',");
	   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',");
		   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   sql.append(",");
	   }
	   sql.append(vo.getCondelay());
	   sql.append(",'");
	   sql.append(vo.getPagesize());
	   sql.append("','");
	   sql.append(vo.getKey_exist());
	   sql.append("','");
	   sql.append(vo.getChange_rate());
	   sql.append("')");
	   
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from nms_web_history where id=" + id);
		   conn.executeBatch();
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("DBTypeDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return result;
   }
   
   public String[] getAvailability(Integer url_id,InitCoordinate initer,String type)throws Exception{
		String[] value={"",""};
		String starttime=initer.getBefore();
		String totime=initer.getNow();
		try {
			String sql = "";
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.mon_time >= '";
				parm=parm+starttime;
				parm=parm+"' and aa.mon_time <= '";
				parm=parm+totime;
				parm=parm+"'";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+url_id+" and "+parm;
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.mon_time >= ";
				parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+" and aa.mon_time <= ";
				parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+"";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+url_id+" and "+parm;
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
   public String[] getCommonAvailability(String tablename,String starttime,String totime,String type)throws Exception{
		String[] value={"",""};
		try {
			String sql = "";
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.COLLECTTIME >= '";
				parm=parm+starttime;
				parm=parm+"' and aa.COLLECTTIME <= '";
				parm=parm+totime;
				parm=parm+"'";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.id) as countid from "+tablename+" aa where "+parm;
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.COLLECTTIME >= ";
				parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+" and aa.COLLECTTIME <= ";
				parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+"";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.id) as countid from "+tablename+" aa where "+parm;
			}

			rs = conn.executeQuery(sql);
			while(rs.next()){
				value[0] = rs.getInt("stype")/100+"";
				value[1] = rs.getInt("countid")+"";
				value[1]=new Integer(new Integer(value[1]).intValue()-new Integer(value[0]).intValue()).toString();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
		}
	public String[] getAvailability(Integer url_id,String starttime,String totime,String type)throws Exception{
		String[] value={"",""};
		try {
			String sql = "";
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.mon_time >= '";
				parm=parm+starttime;
				parm=parm+"' and aa.mon_time <= '";
				parm=parm+totime;
				parm=parm+"'";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+url_id+" and "+parm;
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.mon_time >= ";
				parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+" and aa.mon_time <= ";
				parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+"";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+url_id+" and "+parm;
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
	//当flag = 0 时,则取出的是否连接为0/1,当flag = 1时,取出的数据为中文的是否连接成功
    public Vector getByUrlid(Integer urlid,String starttime,String totime,int flag) throws Exception{
   	    Vector returnVal = new Vector();
   	    WebConfigDao configdao = new WebConfigDao();
   	    try{
   		    WebConfig webconfig = (WebConfig)configdao.findByID(urlid+"");
   		    String sql = "";
   		 if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
   			sql = "select a.is_canconnected,a.is_valid,a.is_refresh," +
			     "a.reason,a.mon_time,a.condelay,a.key_exist,a.pagesize,a.change_rate from nms_web_history a where " +
			     "a.url_id="+urlid+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+totime+"')  order by a.mon_time";
   		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
   			sql = "select a.is_canconnected,a.is_valid,a.is_refresh," +
			     "a.reason,a.mon_time,a.condelay,a.key_exist,a.pagesize,a.change_rate from nms_web_history a where " +
			     "a.url_id="+urlid+" and (a.mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.mon_time <= "+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')"+")  order by a.mon_time";
   		}
   		    
	   		SysLogger.info(sql);
	   		rs = conn.executeQuery(sql);
			while(rs.next()){
			
				Object[] obj = new Object[10];
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
				if(obj[3] == null) obj[3] = "";
				Calendar cal = Calendar.getInstance();
			    Date newdate = new Date();
			    newdate.setTime(rs.getTimestamp("mon_time").getTime());
			    cal.setTime(newdate);
			    obj[4] = sdf.format(cal.getTime());
			    obj[5] = rs.getInt("condelay");
			    if (obj[5] == null)obj[5]="0";
			    obj[6] = webconfig.getAlias();
			    if(obj[6] == null)	obj[6] = "";
			    obj[7] = rs.getString("key_exist");
			    if(obj[7] == null)	obj[7] = "";
			    obj[8] = rs.getString("pagesize");
			    if(obj[8] == null)	obj[8] = "";
			    obj[9] = rs.getString("change_rate");
			    if(obj[9] == null )	obj[9] = "";
			    ht.put("conn",obj[0]);
				ht.put("valid",obj[1]);
				ht.put("refresh",obj[2]);
				ht.put("reason",obj[3]);
				ht.put("mon_time",obj[4]);
				ht.put("condelay",obj[5]);
				ht.put("url_name",obj[6]);
				ht.put("key_exist",obj[7]);
				ht.put("pagesize",obj[8]);
				ht.put("change_rate",obj[9]);
				returnVal.addElement(ht);
				ht = null;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		return returnVal;
	}
    public Hashtable getPingDataById(Integer ftp_id,String starttime,String endtime) {
 	   Hashtable hash = new Hashtable();
 	   if (!starttime.equals("") && !endtime.equals("")) {
 		   List list1 = new ArrayList();
 		   List list2 = new ArrayList();
 		   List list3 = new ArrayList();
 		   List list4 = new ArrayList();
 		   String sql = "";
 		  if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
 			 sql = "select a.is_canconnected,a.reason,a.mon_time,a.condelay,a.pagesize,a.change_rate from nms_web_history a where " +
 	 		   "a.url_id="+ftp_id+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+endtime+"') order by id";;
 		 }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
 			sql = "select a.is_canconnected,a.reason,a.mon_time,a.condelay,a.pagesize,a.change_rate from nms_web_history a where " +
  		   "a.url_id="+ftp_id+" and (a.mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.mon_time <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+") order by id";;
 		 }
 		   
 		   SysLogger.info(sql);
 		   int i = 0;
 		   double curPing=0;
 		   double avgPing = 0;
 		  double  minPing=0;
 		   rs = conn.executeQuery(sql);
 		   try {
 			   while (rs.next()) {
 				   i = i + 1;
 				   Vector v = new Vector();
 				   Vector vecDelay = new Vector();
 				   Vector vecPage = new Vector();
 				   Vector vecChange = new Vector();
 				   String thevalue = rs.getString("is_canconnected");
 				   String collecttime = rs.getString("mon_time");
 				   String delay=rs.getString("condelay");
 				   String page=rs.getString("pagesize");
 				   String change=rs.getString("change_rate");
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
 				    
 				    vecDelay.add(0,delay);
 				    vecDelay.add(1, collecttime);
 				    vecDelay.add(2,"毫秒");
 				    list2.add(vecDelay);
 				  
 				    vecPage.add(0,page);
 				    vecPage.add(1, collecttime);
 				    vecPage.add(2,"KB");
 				    list3.add(vecPage);
 				    
 				   if (!change.equals("null")&&change!=null) {
 					  vecChange.add(0,change);
 	 				    vecChange.add(1, collecttime);
 	 				    vecChange.add(2,"%");
 	 				    list4.add(vecChange);	
 					} 
 				   
 				   
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
 		   hash.put("pingList", list1);
 		   hash.put("delayList", list2);
 		   hash.put("pageList", list3);
 		   hash.put("changeList", list4);
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
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Urlmonitor_history vo = new Urlmonitor_history();
       try
       {
		   vo.setId(rs.getInt("id"));
		   vo.setCondelay(rs.getInt("condelay"));
		   vo.setIs_canconnected(rs.getInt("is_canconnected"));
		   vo.setIs_refresh(rs.getInt("is_refresh"));	
		   vo.setIs_valid(rs.getInt("is_valid"));
		   Calendar cal = Calendar.getInstance();
	       Date newdate = new Date();
	       newdate.setTime(rs.getTimestamp("mon_time").getTime());
	       cal.setTime(newdate);
		   vo.setMon_time(cal);
		   vo.setReason(rs.getString("reason"));
		   vo.setKey_exist(rs.getString("key_exist"));
		   vo.setPagesize(rs.getString("pagesize"));
		   vo.setChange_rate(rs.getString("change_rate"));
		   vo.setUrl_id(rs.getInt("url_id"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("Urlmonitor_historyDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   //yangjun add
   public Hashtable getPingData(Integer url_id,String starttime,String endtime,String type) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				sql = "select a.is_canconnected,a.is_valid,a.is_refresh," +
				"a.reason,a.mon_time,a.condelay,a.key_exist,a.pagesize,a.change_rate from nms_web_history a where " +
				"a.url_id="+url_id+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+endtime+"')";;
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				sql = "select a.is_canconnected,a.is_valid,a.is_refresh," +
				"a.reason,a.mon_time,a.condelay,a.key_exist,a.pagesize,a.change_rate from nms_web_history a where " +
				"a.url_id="+url_id+" and (a.mon_time >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.mon_time <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+")";;
			}
			SysLogger.info(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput1 = 0;
			double avgput2 = 0;
			double avgput3 = 0;
			double avgput4 = 0;
			rs = conn.executeQuery(sql);
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("is_canconnected");
					String collecttime = rs.getString("mon_time");
					String condelay = rs.getString("condelay");
					String key_exist = rs.getString("key_exist");
					String pagesize = rs.getString("pagesize");
					String change_rate = rs.getString("change_rate");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, condelay);
					v.add(3, key_exist);
					v.add(4, pagesize);
					v.add(5, change_rate);
					avgput1 = avgput1 + Float.parseFloat(thevalue);
					avgput2 = avgput2 + Float.parseFloat(condelay);
					avgput3 = avgput3 + Float.parseFloat(pagesize);
					if(change_rate != null && !"null".equalsIgnoreCase(change_rate)){
						avgput4 = avgput4 + Float.parseFloat(change_rate);
					}else{
						avgput4 = avgput4 + Float.parseFloat("0");
					}
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
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput1", CEIString.round(avgput1/ list1.size(), 2)+"");
				hash.put("avgput2", CEIString.round(avgput2/ list1.size(), 2)+"");
				hash.put("avgput3", CEIString.round(avgput3/ list1.size(), 2)+"");
				hash.put("avgput4", CEIString.round(avgput4/ list1.size(), 2)+"");
			} else {
				hash.put("avgput1", "0.0");//yangjun xiugai
				hash.put("avgput2", "0.0");//yangjun xiugai
				hash.put("avgput3", "0.0");//yangjun xiugai
				hash.put("avgput4", "0.0");//yangjun xiugai
			}
		}
		return hash;
	}
}   