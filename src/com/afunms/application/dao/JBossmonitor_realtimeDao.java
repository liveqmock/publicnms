package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import net.sf.hibernate.Session;

import com.afunms.application.model.JBossmonitor_realtime;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class JBossmonitor_realtimeDao extends BaseDao implements DaoInterface{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   public JBossmonitor_realtimeDao()
	   {
		   super("nms_jboss_realtime");
	   }

	   public boolean update(BaseVo baseVo)
	   {
		   JBossmonitor_realtime vo = (JBossmonitor_realtime)baseVo;	  
		   Calendar tempCal = (Calendar)vo.getMon_time();							
		   Date cc = tempCal.getTime();
		   String time = sdf.format(cc);
		   StringBuffer sql = new StringBuffer();
		   sql.append("update nms_jboss_realtime set jboss_id=");
		   sql.append(vo.getJboss_id());
		   sql.append(",is_canconnected=");
		   sql.append(vo.getIs_canconnected());
		   sql.append(",reason='");
		   sql.append(vo.getReason());
		   sql.append("',mon_time= ");
		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			   sql.append("'");
			   sql.append(time);
			   sql.append("'");
		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   }		   
		   sql.append(",sms_sign='");
		   sql.append(vo.getSms_sign());
		   sql.append("'");
		   sql.append(" where id="+vo.getId());
		   //SysLogger.info(sql.toString());
		   return saveOrUpdate(sql.toString());
	   }
	   
	   public boolean save(BaseVo baseVo)
	   {
		   JBossmonitor_realtime vo = (JBossmonitor_realtime)baseVo;	  
		   Calendar tempCal = (Calendar)vo.getMon_time();							
		   Date cc = tempCal.getTime();
		   String time = sdf.format(cc);
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_jboss_realtime(jboss_id,is_canconnected,reason,mon_time,sms_sign)values(");
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
		   sql.append(",'");
		   sql.append(vo.getSms_sign());
		   sql.append("'");
		   sql.append(")");
		   return saveOrUpdate(sql.toString());
	   }
	   
	   public List getByJBossId(int jboss_id){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_jboss_realtime where jboss_id= "+jboss_id);
		   return findByCriteria(sql.toString());
	   }
	   
	   public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.addBatch("delete from nms_jboss_realtime where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("JBossmonitor_realtimeDao.delete()",e); 
		   }
		   finally
		   {
			   conn.close();
		   }
		   return result;
	   }
		public Hashtable getAllReal() throws Exception {
			List list = new ArrayList();
			Hashtable returnVal = new Hashtable();
			Session session =null;
			try {
				String sql = "select * from nms_jboss_realtime";
				try{
					rs = conn.executeQuery(sql);
					while(rs.next())
			        	list.add(loadFromRS(rs));
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//System.out.println("all list size is ------"+list.size());
				for (int i = 0; i < list.size(); i++) {
					JBossmonitor_realtime jbossmonitor_realtime = (JBossmonitor_realtime) list.get(i);
					returnVal.put(jbossmonitor_realtime.getJboss_id(),jbossmonitor_realtime);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return returnVal;
		}  
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   JBossmonitor_realtime vo = new JBossmonitor_realtime();
	       try
	       {
			   vo.setId(rs.getInt("id"));
			   vo.setSms_sign((rs.getInt("sms_sign")));
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
	   	       SysLogger.error("JBossmonitor_realtimeDao.loadFromRS()",e); 
	       }	   
	       return vo;
	   }

}
