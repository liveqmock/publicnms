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


import com.afunms.application.model.Socketmonitor_realtime;
import com.afunms.application.util.DbConversionUtil;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class Socketmonitor_realtimeDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   public Socketmonitor_realtimeDao()
   {
	   super("nms_socket_realtime");
   }

   public boolean update(BaseVo baseVo)
   {
	   Socketmonitor_realtime vo = (Socketmonitor_realtime)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("update nms_socket_realtime set socket_id=");
	   sql.append(vo.getSocket_id());
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
	   sql.append("',mon_time=");
	   sql.append(DbConversionUtil.coversionTimeSql(time));
	   sql.append(",condelay=");
	   sql.append(vo.getCondelay());
	   sql.append(" where id="+vo.getId());
	   //SysLogger.info(sql.toString());
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean save(BaseVo baseVo)
   {
	   Socketmonitor_realtime vo = (Socketmonitor_realtime)baseVo;	  
	   Calendar tempCal = (Calendar)vo.getMon_time();							
		Date cc = tempCal.getTime();
//	   String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
	   String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into nms_socket_realtime(socket_id,is_canconnected,is_valid,is_refresh,reason,page_context,mon_time,condelay)values(");
	   sql.append(vo.getSocket_id());
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
	   sql.append(")");
	   conn= new DBManager();
	   
	   return saveOrUpdate(sql.toString());
   }
   
   public List getBySocketId(int socket_id){
	   List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from nms_socket_realtime where socket_id= "+socket_id);
	   return findByCriteria(sql.toString());
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from nms_socket_realtime where id=" + id);
		   conn.executeBatch();
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("Socketmonitor_realtimeDao.delete()",e); 
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
			String sql = "select * from nms_socket_realtime";
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
				Socketmonitor_realtime urlmonitor_realtime = (Socketmonitor_realtime) list.get(i);
				returnVal.put(urlmonitor_realtime.getSocket_id(),urlmonitor_realtime);
			}
		}
		catch (Exception e) {
					e.printStackTrace();
				}
		return returnVal;
	}  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Socketmonitor_realtime vo = new Socketmonitor_realtime();
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
		   vo.setSocket_id(rs.getInt("socket_id"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("Socketmonitor_realtimeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
	public String[] getAvailability(Integer socket_id,String starttime,String totime,String type)throws Exception{
		String[] value={"",""};
		String sql = "";
		try {
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.collecttime >= '";
				parm=parm+starttime;
				parm=parm+"' and aa.collecttime <= '";
				parm=parm+totime;
				parm=parm+"'";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+socket_id+" and "+parm;
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				String parm=" aa.collecttime >= ";
				parm=parm+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+" and aa.collecttime <= ";
				parm=parm+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				parm=parm+"";
				sql = "select sum(aa."+type+") as stype ,COUNT(aa.url_id) as countid from nms_web_history aa where aa.url_id="+socket_id+" and "+parm;
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
}   