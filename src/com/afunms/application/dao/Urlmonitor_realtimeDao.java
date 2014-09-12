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
import com.afunms.application.model.Urlmonitor_realtime;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;

public class Urlmonitor_realtimeDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   public Urlmonitor_realtimeDao()
   {
	   super("nms_web_realtime");
   }

   public boolean update(BaseVo baseVo)
   {
	   Urlmonitor_realtime vo = (Urlmonitor_realtime)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("update nms_web_realtime set url_id=");
	   sql.append(vo.getUrl_id());
	   sql.append(",is_canconnected=");
	   sql.append(vo.getIs_canconnected());
	   sql.append(",is_valid=");
	   sql.append(vo.getIs_valid());   
	   sql.append(",is_refresh=");
	   sql.append(vo.getIs_refresh());
	   sql.append(",reason='");
	   sql.append(vo.getReason());
	   sql.append("',page_context='");
	   sql.append(vo.getPage_context());
	   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',mon_time='");
		   sql.append(time);
		   sql.append("',condelay=");
	   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		   sql.append("',mon_time=");
		   sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
		   sql.append(",condelay=");
	   }
	   sql.append(vo.getCondelay());
	   sql.append(",key_exist='");
	   sql.append(vo.getKey_exist());
	   sql.append("',pagesize='");
	   sql.append(vo.getPagesize());
	   sql.append("',change_rate='");
	   sql.append(vo.getChange_rate());
	   sql.append("' where id="+vo.getId());
	   //SysLogger.info(sql.toString());
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean save(BaseVo baseVo)
   {
	   Urlmonitor_realtime vo = (Urlmonitor_realtime)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
	   Date cc = tempCal.getTime();
	   String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into nms_web_realtime(url_id,is_canconnected,is_valid,is_refresh,reason,page_context,mon_time,condelay,key_exist,pagesize,change_rate)values(");
	   sql.append(vo.getUrl_id());
	   sql.append(",");
	   sql.append(vo.getIs_canconnected());
	   sql.append(",");
	   sql.append(vo.getIs_valid());   
	   sql.append(",");
	   sql.append(vo.getIs_refresh());
	   sql.append(",'");
	   sql.append(vo.getReason());
	   sql.append("','");
	   sql.append(vo.getPage_context());
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
	   sql.append(vo.getKey_exist());
	   sql.append("','");
	   sql.append(vo.getPagesize());
	   sql.append("','");
	   sql.append(vo.getChange_rate());
	   sql.append("')");
	   conn= new DBManager();
	   return saveOrUpdate(sql.toString());
   }
   
   public List getByUrlId(int url_id){
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from nms_web_realtime where url_id= "+url_id);
	   return findByCriteria(sql.toString());
   }
   
   /**
	  * É¾³ýËùÓÐ¼ÇÂ¼
	  */
    public boolean deleteByUrl(String id) {
	
	   boolean result = false;
	   try
	   {
	       conn.addBatch("delete from nms_web_realtime where url_id="+id);
	       conn.executeBatch();
	       result = true;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("Urlmonitor_realtimeDao.deleteByUrl()",ex);
	       result = false;
	   }
	   return result;
    }


   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from nms_web_realtime where id=" + id);
		   conn.executeBatch();
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("Urlmonitor_realtimeDao.delete()",e); 
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
		try {
			String sql = "select * from nms_web_realtime";
			try{
				rs = conn.executeQuery(sql);
				while(rs.next())
		        	list.add(loadFromRS(rs));
			}
			catch(Exception e){
				e.printStackTrace();
			}
			for (int i = 0; i < list.size(); i++) {
				Urlmonitor_realtime urlmonitor_realtime = (Urlmonitor_realtime) list.get(i);
				returnVal.put(urlmonitor_realtime.getUrl_id(),urlmonitor_realtime);
			}
		} catch (Exception e) {
					e.printStackTrace();
		}
		return returnVal;
	}  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Urlmonitor_realtime vo = new Urlmonitor_realtime();
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
		   vo.setPage_context(rs.getString("page_context"));
		   vo.setKey_exist(rs.getString("key_exist"));
		   vo.setPagesize(rs.getString("pagesize"));
		   vo.setUrl_id(rs.getInt("url_id"));
		   vo.setChange_rate(rs.getString("change_rate"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("Urlmonitor_realtimeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}   