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

public class CicsConfigDao extends BaseDao implements DaoInterface {

	public CicsConfigDao() {
		
		super("nms_cicsconfig");
		
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
	       conn.addBatch("delete from nms_cicsconfig where 1=1");
	       conn.executeBatch();
	       result = true;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("CicsConfigDao.delete()",ex);
	       result = false;
	   }
	   return result;
   }	

	public BaseVo loadFromRS(ResultSet rs) {
		CicsConfig vo=new CicsConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setRegion_name(rs.getString("region_name"));
			vo.setAlias(rs.getString("alias"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setPort_listener(rs.getString("port_listener"));
			vo.setNetwork_protocol(rs.getString("network_protocol"));
			vo.setConn_timeout(rs.getInt("conn_timeout"));
			vo.setFlag(rs.getInt("flag"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setNetid(rs.getString("netid"));
			vo.setGateway(rs.getString("gateway"));
			vo.setSupperid(rs.getInt("supperid"));// snow add supperid at 2010-5-20
			
			
		} catch (SQLException e) {
			
			SysLogger.error("CicsConfigDao.loadFromRS()",e);
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
	
	public boolean save(BaseVo vo) {
		CicsConfig vo1=(CicsConfig)vo;
		StringBuffer sql=new StringBuffer();
		//snow add id at 2010-5-20
		sql.append("insert into nms_cicsconfig(id,region_name,alias,ipaddress,port_listener,network_protocol,conn_timeout,sendemail,sendmobiles,netid,flag,gateway,supperid) values('");
		sql.append(vo1.getId());
		sql.append("','");
		sql.append(vo1.getRegion_name());
		sql.append("','");
		sql.append(vo1.getAlias());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getPort_listener());
		sql.append("','");
		sql.append(vo1.getNetwork_protocol());
		sql.append("','");
		sql.append(vo1.getConn_timeout());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getFlag());
		sql.append("','");
		sql.append(vo1.getGateway());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
		
	}
   public List getCicsByBID(Vector bids){
	   StringBuffer sql = new StringBuffer();
	   String wstr = "";
	   if(bids != null && bids.size()>0){
		   for(int i=0;i<bids.size();i++){
			   if(wstr.trim().length()==0){
				   wstr = wstr+" where ( netid like '%,"+bids.get(i)+",%' "; 
			   }else{
				   wstr = wstr+" or netid like '%,"+bids.get(i)+",%' ";
			   }
			   
		   }
		   wstr=wstr+")";
	   }
	   sql.append("select * from nms_cicsconfig "+wstr);
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
//				String ip1 ="",ip2="",ip3="",ip4="";
//				String tempStr = "";
//				String allipstr = "";
//				if (ip.indexOf(".")>0){
//					ip1=ip.substring(0,ip.indexOf("."));
//					ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//					tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//				}
//				ip2=tempStr.substring(0,tempStr.indexOf("."));
//				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//				allipstr=ip1+ip2+ip3+ip4;
				String allipstr = SysUtil.doip(ip);
		
				Calendar tempCal = (Calendar)pingdata.getCollecttime();							
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";
				tablename = "cicsping"+allipstr;
				String sql="";
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
					+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
					+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
					+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
					+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
					+pingdata.getCount()+",'"+pingdata.getThevalue()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
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
} 