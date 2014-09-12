/**
 * <p>Description: topo_tomcat_node</p>
 * <p>Company:dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-13
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TomcatPre;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;

public class TomcatPreDao extends BaseDao implements DaoInterface
{
   public TomcatPreDao()
   {
	   super("app_tomcat_pre");
   }
   public List getTomcatAll(){
	   List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from app_tomcat_node ");
	   return findByCriteria(sql.toString());
   }
   
   
   public boolean save(BaseVo baseVo)
   {
	   boolean flag = true;
	   TomcatPre vo = (TomcatPre)baseVo;	   
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into app_tomcat_pre(nodeid,maxthread,minsthread,maxsthread,CurCount,curthbusy,maxprotime,protime,requestcount,errorcount,BytesReceived,BytesSent)values(");
	   sql.append(vo.getNodeid());
	   sql.append(",'");
	   sql.append(vo.getMaxThread());
	   sql.append("','");
	   sql.append(vo.getMinSThread());
	   sql.append("','");
	   sql.append(vo.getMaxSThread());
	   sql.append("','");	   
	   sql.append(vo.getCurCount());
	   sql.append("','");
	   sql.append(vo.getCurThBusy());
	   sql.append("','");
	   sql.append(vo.getMaxProTime());
	   sql.append("','");
	   sql.append(vo.getProTime());	   
	   sql.append("','");
	   sql.append(vo.getRequestCount());	   
	   sql.append("','");
	   sql.append(vo.getErrorCount());	   
	   sql.append("','");
	   sql.append(vo.getBytesReceived());
	   sql.append("','");
	   sql.append(vo.getBytesSent());
	   sql.append("')");	   
	   try{
		   saveOrUpdate(sql.toString());
	   }catch(Exception e){
		   e.printStackTrace();
		   flag = false;
	   }finally{
			try{
//				conn.executeBatch();
			}catch(Exception e){
				
			}
		   conn.close();
	   }
	   return flag;
   }
   
  
 //处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Pingcollectdata pingdata) {
		if (pingdata == null )
			return false;	
		//DBManager dbmanager = new DBManager();
		try{			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Vector v = new Vector();
			//for (int i = 0; i < hostdatavec.size(); i++) {
				//Pingcollectdata pingdata = (Pingcollectdata)hostdatavec.elementAt(i);	
				String ip = pingdata.getIpaddress();				
				if (pingdata.getRestype().equals("dynamic")) {						
//					String ip1 ="",ip2="",ip3="",ip4="";
//					String[] ipdot = ip.split(".");	
//					String tempStr = "";
//					String allipstr = "";
//					if (ip.indexOf(".")>0){
//						ip1=ip.substring(0,ip.indexOf("."));
//						ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//						tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//					}
//					ip2=tempStr.substring(0,tempStr.indexOf("."));
//					ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//					allipstr=ip1+ip2+ip3+ip4;
					String allipstr = SysUtil.doip(ip);
					Calendar tempCal = (Calendar)pingdata.getCollecttime();							
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					String tablename = "";
					String type=pingdata.getCategory();
					if("TomcatPing".equals(type)){
						tablename = "tomcatping"+allipstr;
					}else if("tomcat_jvm".equals(type))	{
						tablename = "tomcat_jvm"+allipstr;
					}
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
	
	   public List getTomcatByBID(Vector bids){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   String wstr = "";
		   if(bids != null && bids.size()>0){
			   for(int i=0;i<bids.size();i++){
				   if(wstr.trim().length()==0){
					   wstr = wstr+" where ( bid like '%,"+bids.get(i)+",%' "; 
				   }else{
					   wstr = wstr+" or bid like '%,"+bids.get(i)+",%' ";
				   }
				   
			   }
			   wstr=wstr+")";
		   }
		   sql.append("select * from app_tomcat_node "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   TomcatPre vo = new TomcatPre();
       try
       {
		  vo.setNodeid(rs.getInt("nodeid"));
		  vo.setMaxThread(rs.getString("MaxThread"));
		  vo.setMinSThread(rs.getString("MinSThread"));
          vo.setMaxSThread(rs.getString("MaxSThread"));
          vo.setCurCount(rs.getString("CurCount"));
          vo.setCurThBusy(rs.getString("CurThBusy"));
          vo.setMaxProTime(rs.getString("MaxProTime"));
          vo.setProTime(rs.getString("protime"));
          vo.setRequestCount(rs.getString("requestcount"));
          vo.setErrorCount(rs.getString("errorcount"));
          vo.setBytesReceived(rs.getString("bytesreceived"));
          vo.setBytesSent(rs.getString("bytessent"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("AlarmDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
   

   

    
    /**
     * 从数据库中获取tomcat的采集的数据信息
     */
    public Hashtable getTomcatDataHashtable(String nodeid) throws SQLException {
    	Hashtable hm = new Hashtable();
		try {
			//SysLogger.info();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select * from app_tomcat_pre where nodeid = ");
			sqlBuffer.append(nodeid);
			 rs = conn.executeQuery(sqlBuffer.toString());
			// 取得列名,
			while (rs.next()) {
				String maxthread = rs.getString("maxthread");
				hm.put("maxthread", maxthread);
				String minsthread = rs.getString("minsthread");
				hm.put("minsthread", minsthread);
				String maxsthread = rs.getString("maxsthread");
				hm.put("maxsthread", maxsthread);
				String curcount = rs.getString("curcount");
				hm.put("curcount", curcount);
				String curthbusy = rs.getString("curthbusy");
				hm.put("curthbusy", curthbusy);
				String maxprotime = rs.getString("maxprotime");
				hm.put("maxprotime", maxprotime);
				String protime = rs.getString("protime");
				hm.put("protime", protime);
				String requestcount = rs.getString("requestcount");
				hm.put("requestcount", requestcount);
				String errorcount = rs.getString("errorcount");
				hm.put("errorcount", errorcount);
				String bytesreceived = rs.getString("BytesReceived");
				hm.put("bytesreceived", bytesreceived);
				String bytessent = rs.getString("bytessent");
				hm.put("bytessent", bytessent);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs != null){
				rs.close();
			}
		}
		return hm;
	}
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public int  count(int nodeid){
	    int count = 0;
	    rs = conn.executeQuery("select count(*) as count from app_tomcat_pre where nodeid = "+nodeid);
	    try {
	    	while (rs.next()) 
			{
	    		count = Integer.parseInt(rs.getString("count"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return count;
	}
	
	public boolean delete(int nodeid){
		boolean result = false;
		try
		{
			conn.addBatch("delete from app_tomcat_pre where nodeid="+nodeid);
			conn.executeBatch();
		    result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("TomcatPreDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return result;
	}
	
}   