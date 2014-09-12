package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import net.sf.hibernate.Session;

import com.afunms.application.model.Apachemonitor_realtime;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class Apachemonitor_realtimeDao extends BaseDao implements DaoInterface{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   public Apachemonitor_realtimeDao()
	   {
		   super("nms_apache_realtime");
	   }

	   public boolean update(BaseVo baseVo)
	   {
		   Apachemonitor_realtime vo = (Apachemonitor_realtime)baseVo;	  
		   Calendar tempCal = (Calendar)vo.getMon_time();							
		   Date cc = tempCal.getTime();
		   String time = sdf.format(cc);
		   StringBuffer sql = new StringBuffer();
		   sql.append("update nms_apache_realtime set apache_id=");
		   sql.append(vo.getApache_id());
		   sql.append(",is_canconnected=");
		   sql.append(vo.getIs_canconnected());
		   sql.append(",reason='");
		   sql.append(vo.getReason());
		   sql.append("',mon_time=");
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
		   Apachemonitor_realtime vo = (Apachemonitor_realtime)baseVo;	  
		   Calendar tempCal = (Calendar)vo.getMon_time();							
		   Date cc = tempCal.getTime();
		   String time = sdf.format(cc);
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_apache_realtime(apache_id,is_canconnected,reason,mon_time,sms_sign)values(");
		   sql.append("'");
		   sql.append(vo.getApache_id());
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
	   
	   public List getByApacheId(int apache_id){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_apache_realtime where apache_id= "+apache_id);
		   return findByCriteria(sql.toString());
	   }
	   
	   public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.addBatch("delete from nms_apache_realtime where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("Apachemonitor_realtimeDao.delete()",e); 
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
				String sql = "select * from nms_apache_realtime";
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
					Apachemonitor_realtime apachemonitor_realtime = (Apachemonitor_realtime) list.get(i);
					returnVal.put(apachemonitor_realtime.getApache_id(),apachemonitor_realtime);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return returnVal;
		}  
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   Apachemonitor_realtime vo = new Apachemonitor_realtime();
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
			   vo.setApache_id(rs.getInt("apache_id"));
	       }
	       catch(Exception e)
	       {
	   	       SysLogger.error("Apachemonitor_realtimeDao.loadFromRS()",e); 
	       }	   
	       return vo;
	   }

}
