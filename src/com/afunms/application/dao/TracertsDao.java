/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.application.model.*;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;
import com.gatherdb.GathersqlListManager;

public class TracertsDao extends BaseDao implements DaoInterface {

	public TracertsDao() {
		
		super("nms_tracerts");
		
	}
	
	public boolean delete(String []ids){
		return super.delete(ids);
	}
	
	/**
	  * 删除所有记录
	  */
    public boolean delete() {
    	
	   boolean result = false;
	   try
	   {
	       conn.addBatch("delete from nms_tracerts");
	       conn.executeBatch();
	       result = true;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("TracertsDao.delete()",ex);
	       result = false;
	   }
	   return result;
   }	

	public BaseVo loadFromRS(ResultSet rs) {
		Tracerts vo=new Tracerts();		
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodetype(rs.getString("nodetype"));
			vo.setConfigid(rs.getInt("configid"));
			Date timestamp = rs.getTimestamp("dotime");
	    	Calendar date=Calendar.getInstance();
	    	date.setTime(timestamp);
			vo.setDotime(date);
			
			
		} catch (SQLException e) {
			
			SysLogger.error("TracertsDao.loadFromRS()",e);
		}
		return vo;
	}

	//保存一批数据
	public boolean save(String[] serverName,String url) {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<serverName.length;i++)
		           conn.addBatch("insert into nms_cicsconfig(region_name,alias,ipaddress,port_listener,network_protocol," +
		           		          "conn_timeout,sendemail,sendmobiles,netid,flag,gateway) values('"+serverName[i]+"','"+serverName[i]+"'," +
		           		          "'','','TCP/IP',10,'','',',2,3,',1,'"+url+"')");
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("CicsConfigDao.save()",ex);
		       result = false;
		   }
		   return result;
	}
	
	public int queryId(int configid,Calendar time){
		int id = 0;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = (Calendar) time;
		Date cc = tempCal.getTime();
		String time1 = sdf.format(cc);
		String sql = "select id from nms_tracerts where configid="+configid+" and dotime = '"+time1+"'";
//		System.out.println("==========>"+sql);
		rs = conn.executeQuery(sql);
		try {
			while(rs.next())
			{
				id = Integer.parseInt(rs.getString("id"));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	
	
	public boolean save(BaseVo vo) {
		Tracerts vo1=(Tracerts)vo;
		StringBuffer sql=new StringBuffer();
		vo1.setId(getNextID());
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = (Calendar) vo1.getDotime();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		//snow add id at 2010-5-20
		sql.append("insert into nms_tracerts(nodetype,configid,dotime) values(");
//		sql.append(vo1.getId());
//		sql.append(",'");
		sql.append("'");
		sql.append(vo1.getNodetype());
		sql.append("',");
		sql.append(vo1.getConfigid());
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append(",'");
			sql.append(time);
			sql.append("')");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append(",");
			sql.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
			sql.append(")");
		}

//		try
//		{
//			GathersqlListManager.Addsql(sql.toString());
//			SysLogger.info(sql.toString());
//			return true;
//		}catch(Exception e){
//			SysLogger.info(sql.toString());
//			e.printStackTrace();
//			return false;
//		}
		SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
		
	}
   public Tracerts getTracertsByTypeAndConfigId(String nodetype,int configid){
	   Tracerts vo = null;
	   StringBuffer sql = new StringBuffer();
	   String wstr = " where nodetype='"+nodetype+"' and configid="+configid;
	   sql.append("select * from nms_tracerts "+wstr);
	   List list = findByCriteria(sql.toString());
	   if(list != null && list.size()>0){
		   vo = (Tracerts)list.get(0);
	   }	   
	   return vo;
   }
   
   public void deleteTracertsByTypeAndConfigIds(String nodetype,String[] configids){
	   Tracerts vo = null;
	   StringBuffer sql = new StringBuffer();
	   if(configids != null && configids.length>0){
		   for(int i=0;i<configids.length;i++){
			   String id = configids[i];
			   sql = sql.append("delete from nms_tracerts where nodetype='"+nodetype+"' and configid="+id);
			   conn.addBatch(sql.toString());
			   sql = new StringBuffer();
		   }
		   try{
			   conn.executeBatch();
		   } catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
				
			}
	   }	   
	   return ;
   }
	   
    public List getCicsByFlag(int flag){
		   //List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_cicsconfig where flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	
	public boolean update(BaseVo vo) {
		CicsConfig vo1=(CicsConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_cicsconfig set region_name ='");
		sql.append(vo1.getRegion_name());
		sql.append("',alias='");
		sql.append(vo1.getAlias());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',port_listener='");
		sql.append(vo1.getPort_listener());
		sql.append("',network_protocol='");
		sql.append(vo1.getNetwork_protocol());
		sql.append("',conn_timeout='");
		sql.append(vo1.getConn_timeout());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',flag='");
		sql.append(vo1.getFlag());
		sql.append("',gateway='");
		sql.append(vo1.getGateway());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("' where id="+vo1.getId());
		return saveOrUpdate(sql.toString());
	}
    
    //处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata) {
		if (pingdata == null )
			return false;	
		try{			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = pingdata.getIpaddress();				
			if (pingdata.getRestype().equals("dynamic")) {						
				String allipstr = SysUtil.doip(ip);
		
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";
				tablename = "cicsping"+allipstr;
				String sql = "";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
					+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
					+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
					+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
					+pingdata.getCount()+",'"+pingdata.getThevalue()+"',"+"to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')"+")";
				}
				conn.executeUpdate(sql);
																								
			}				
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			
		}
		return true;
	}
	public List getAllRsByDoTime(String where){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_tracerts where " + where );
		return findByCriteria(sql.toString());
	}
	
	 public void deleteTracertsByConfigIds(String[] configids){
		   Tracerts vo = null;
		   StringBuffer sql = new StringBuffer();
		   if(configids != null && configids.length>0){
			   for(int i=0;i<configids.length;i++){
				   String id = configids[i];
				   sql = sql.append("delete from nms_tracerts where configid="+id);
				   conn.addBatch(sql.toString());
				   sql = new StringBuffer();
			   }
			   try{
				   conn.executeBatch();
			   } catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.close();
					
				}
		   }	   
		   return ;
	   }
	 
	 
	 
} 