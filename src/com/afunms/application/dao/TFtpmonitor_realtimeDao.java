/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import net.sf.hibernate.Session;

import com.afunms.application.model.TFtpmonitor_realtime;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

/**
 * 
 * @author GANYI
 * @since 2012-04-23 18:47:00
 */
public class TFtpmonitor_realtimeDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   public TFtpmonitor_realtimeDao()
   {
	   super("nms_tftp_realtime");
   }

   public boolean update(BaseVo baseVo)
   {
	   TFtpmonitor_realtime vo = (TFtpmonitor_realtime)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
	   Date cc = tempCal.getTime();
	   String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("update nms_tftp_realtime set tftp_id=");
	   sql.append(vo.getTftp_id());
	   sql.append(",is_canconnected=");
	   sql.append(vo.getIs_canconnected());
	   sql.append(",reason='");
	   sql.append(vo.getReason());
	   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',mon_time='");
		   sql.append(time);
		   sql.append("',sms_sign='");
	   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',mon_time=");
		   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   sql.append(",sms_sign='");
	   }
	   sql.append(vo.getSms_sign());
	   sql.append("'");
	   sql.append(" where id="+vo.getId());
	   //SysLogger.info(sql.toString());
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean save(BaseVo baseVo)
   {
	   TFtpmonitor_realtime vo = (TFtpmonitor_realtime)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
	   Date cc = tempCal.getTime();
	   String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into nms_tftp_realtime(tftp_id,is_canconnected,reason,mon_time,sms_sign)values(");
	   sql.append("'");
	   sql.append(vo.getTftp_id());
	   sql.append("','");
	   sql.append(vo.getIs_canconnected());
	   sql.append("','");
	   sql.append(vo.getReason());
	   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("','");
		   sql.append(time);
		   sql.append("','");
	   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',");
		   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   sql.append(",'");
	   }
	   sql.append(vo.getSms_sign());
	   sql.append("'");
	   sql.append(")");
	   return saveOrUpdate(sql.toString());
   }
   
   public List getByTFTPId(int tftp_id){
	   List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from nms_tftp_realtime where tftp_id= "+tftp_id);
	   return findByCriteria(sql.toString());
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from nms_tftp_realtime where id=" + id);
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
	public Hashtable getAllReal() throws Exception {
		List list = new ArrayList();
		Hashtable returnVal = new Hashtable();
		Session session =null;
		try {
			String sql = "select * from nms_tftp_realtime";
			try{
				rs = conn.executeQuery(sql);
				while(rs.next())
		        	list.add(loadFromRS(rs));
			}catch(Exception e){
				e.printStackTrace();
			}
			//System.out.println("all list size is ------"+list.size());
			for (int i = 0; i < list.size(); i++) {
				TFtpmonitor_realtime emailmonitor_realtime = (TFtpmonitor_realtime) list.get(i);
				returnVal.put(emailmonitor_realtime.getTftp_id(),emailmonitor_realtime);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
		}
		return returnVal;
	}  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   TFtpmonitor_realtime vo = new TFtpmonitor_realtime();
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
		   vo.setTftp_id(rs.getInt("tftp_id"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("TFtpmonitor_realtimeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}   