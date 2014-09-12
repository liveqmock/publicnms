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

import com.afunms.application.model.JBossmonitor_history;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class JBossmonitor_historyDao extends BaseDao implements DaoInterface{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   public JBossmonitor_historyDao()
	   {
		   super("nms_jboss_history");
	   }

	   public boolean update(BaseVo baseVo)
	   {
		   return false;
	   }
	   
	   public boolean save(BaseVo baseVo)
	   {
		   JBossmonitor_history vo = (JBossmonitor_history)baseVo;	  
		   Calendar tempCal = (Calendar)vo.getMon_time();	
		   Date cc = tempCal.getTime();
		   String time = sdf.format(cc);
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_jboss_history(jboss_id,is_canconnected,reason,mon_time)values(");
		   sql.append("'");
		   sql.append(vo.getJboss_id());
		   sql.append("','");
		   sql.append(vo.getIs_canconnected());
		   sql.append("','");
		   sql.append(vo.getReason());
		   sql.append("',");
		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			   sql.append("'");
			   sql.append(time);
			   sql.append("'");
		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   }		   
		   sql.append(")");
		   
		   return saveOrUpdate(sql.toString());
	   }
	   
	   public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.addBatch("delete from nms_jboss_history where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("JBossmonitor_historyDao.delete()",e); 
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
	   
		public Vector getByJBossid(Integer jbossid,String starttime,String totime,Integer isconnected) throws Exception{
			List list = new ArrayList();
	    	Vector returnVal = new Vector();
	    	try{
	    		String sql="";
	    		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	    			sql="select a.is_canconnected,a.reason,a.mon_time from nms_jboss_history a where " +
					"a.jboss_id="+jbossid+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+totime+"')";
	    		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	    			sql="select a.is_canconnected,a.reason,a.mon_time from nms_jboss_history a where " +
					"a.jboss_id="+jbossid+" and (a.mon_time >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and  a.mon_time <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS'))";
	    		}	    		
				SysLogger.info(sql);
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
		public String[] getAvailability(Integer jboss_id,String starttime,String totime,String type)throws Exception{
			String[] value={"",""};
			try {
				String parm="";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					parm=" aa.mon_time >= '";
					parm=parm+starttime;
					parm=parm+"' and aa.mon_time <= '";
					parm=parm+totime;
					parm=parm+"'";
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					parm=" aa.mon_time >= ";
					parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
					parm=parm+" and aa.mon_time <= ";
					parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				}				
				String sql = "select sum(aa."+type+") as stype ,COUNT(aa.jboss_id) as countid from nms_jboss_history aa where aa.jboss_id="+jboss_id+" and "+parm;
				System.out.println(sql);
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
		   JBossmonitor_history vo = new JBossmonitor_history();
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
			   vo.setJboss_id(rs.getInt("jboss_id"));
	       }
	       catch(Exception e)
	       {
	   	       SysLogger.error("JBossmonitor_historyDao.loadFromRS()",e); 
	       }	   
	       return vo;
	   }
	   
	 //quzhi add
	   public Hashtable getPingData(Integer jboss_id,String starttime,String endtime,String type) {
			Hashtable hash = new Hashtable();
			if (!starttime.equals("") && !endtime.equals("")) {
				List list1 = new ArrayList();
				String sql="";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "select a.is_canconnected," +
		             " a.reason,a.mon_time from nms_jboss_history a where " +
			         "a.jboss_id="+jboss_id+" and (a.mon_time >= '"+starttime +"' and  a.mon_time <= '"+endtime+"')";
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "select a.is_canconnected," +
		             " a.reason,a.mon_time from nms_jboss_history a where " +
			         "a.jboss_id="+jboss_id+" and (a.mon_time >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and  a.mon_time <= to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS'))";;
				}
				SysLogger.info(sql);
				int i = 0;
				double tempfloat = 0;
				double avgput1 = 0;
				
				rs = conn.executeQuery(sql);
				try {
					while (rs.next()) {
						i = i + 1;
						Vector v = new Vector();
						String thevalue = rs.getString("is_canconnected");
						String collecttime = rs.getString("mon_time");
						
						v.add(0, thevalue);
						v.add(1, collecttime);
						
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
				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1 != null && list1.size() > 0) {
					hash.put("avgput1", CEIString.round(avgput1/ list1.size(), 2)+"");
				} else {
					hash.put("avgput1", "0.0");
				}
			}
			return hash;
		}

}
