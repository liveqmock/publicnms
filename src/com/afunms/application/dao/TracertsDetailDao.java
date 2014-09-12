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
import com.afunms.polling.om.Pingcollectdata;

public class TracertsDetailDao extends BaseDao implements DaoInterface {

	public TracertsDetailDao() {
		
		super("nms_tracerts_details");
		
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
	       conn.addBatch("delete from nms_tracerts_details");
	       conn.executeBatch();
	       result = true;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("TracertsDetailDao.delete()",ex);
	       result = false;
	   }
	   return result;
   }	

	public BaseVo loadFromRS(ResultSet rs) {
		TracertsDetail vo=new TracertsDetail();		
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodetype(rs.getString("nodetype"));
			vo.setTracertsid(rs.getInt("tracertsid"));
			vo.setDetails(rs.getString("details"));
			vo.setConfigid(rs.getInt("configid"));
			
			
		} catch (SQLException e) {
			
			SysLogger.error("TracertsDetailDao.loadFromRS()",e);
		}
		return vo;
	}

	//保存一批数据
	public boolean save(List list) {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<list.size();i++){
		    	   TracertsDetail vo=(TracertsDetail)list.get(i);
		    	   vo.setId(getNextID()+i);
		    	   conn.addBatch("insert into nms_tracerts_details(tracertsid,nodetype,details,configid" +
	           		          ") values("+vo.getTracertsid()+",'" +vo.getNodetype()+"','"+vo.getDetails()+"',"+vo.getConfigid()
	           		          +")");
		       }
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
			   ex.printStackTrace();
		       SysLogger.error("TracertsDetailDao.save()",ex);
		       result = false;
		   }
		   return result;
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
		sql.append("insert into nms_tracerts(id,nodetype,configid,dotime) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getNodetype());
		sql.append("',");
		sql.append(vo1.getConfigid());
		sql.append(",'");
		sql.append(time);
		sql.append("')");
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
	   
   public List getListByTracertId(int tracertId){
	   //List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from nms_tracerts_details where tracertsid = "+ tracertId);
	   return findByCriteria(sql.toString());
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
				String sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
				
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
	
	 public void deleteTracertsDetaiByConfigIds(String[] configids){
		   Tracerts vo = null;
		   StringBuffer sql = new StringBuffer();
		   if(configids != null && configids.length>0){
			   for(int i=0;i<configids.length;i++){
				   String id = configids[i];
				   sql = sql.append("delete from nms_tracerts_details where configid="+id);
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