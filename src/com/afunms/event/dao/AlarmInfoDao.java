/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.Query;
import net.sf.hibernate.Session;


import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.AlarmInfo;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AlarmInfoDao extends BaseDao implements DaoInterface{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public AlarmInfoDao() {
		super("nms_alarminfo");
	}
	
	public boolean save(BaseVo baseVo)
	{
		AlarmInfo vo = (AlarmInfo)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_alarminfo(content,ipaddress,level1,recordtime,type)values(");
		sql.append("'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getIpaddress());	
		sql.append("',");
		sql.append(vo.getLevel1());
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append(",'");
			sql.append(recordtime);
			sql.append("','");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append(",");
			sql.append("to_date('"+recordtime+"','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",'");
		}
		sql.append(vo.getType());
		sql.append("')");
		//SysLogger.info("开始写入声音告警数据=======================");
		//SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	  public boolean update(BaseVo baseVo)
	  {
		  AlarmInfo vo = (AlarmInfo)baseVo;
			Calendar tempCal = (Calendar)vo.getRecordtime();							
			Date cc = tempCal.getTime();
			String recordtime = sdf.format(cc);
			boolean result = false;
			/*
			StringBuffer sql = new StringBuffer();
			sql.append("update nms_alarminfo set eventtype='");
			sql.append(vo.getEventtype());
			sql.append("',eventlocation='");
			sql.append(vo.getEventlocation());	
			sql.append("',content='");
			sql.append(vo.getContent());
			sql.append("',level1=");
			sql.append(vo.getLevel1());
			sql.append(",managesign=");
			sql.append(vo.getManagesign());
			sql.append(",bak='");
			sql.append(vo.getBak());
			sql.append("',recordtime='");
			sql.append(recordtime);
			sql.append("',reportman='");
			sql.append(vo.getReportman());
			sql.append("',nodeid=");
			sql.append(vo.getNodeid());
			sql.append(",businessid='");
			sql.append(vo.getBusinessid());
			sql.append("',oid=");
			sql.append(vo.getOid());
			sql.append(",subtype='");
			sql.append(vo.getSubtype());
			sql.append("',managetime='");
			sql.append(vo.getManagetime());  
			sql.append("',subentity='");
			sql.append(vo.getSubentity()); 
			sql.append("' where id=");
			sql.append(vo.getId());
	     
	     try
	     {
	         conn.executeUpdate(sql.toString());
	         result = true;
	     }
	     catch(Exception e)
	     {
	    	 result = false;
	         SysLogger.error("EventListDao:update()",e);
	     }
	     finally
	     {
	    	 conn.close();
	     }  
	     */
	     return result;
	  }

	
	  public List loadAll()
	  {
	     List list = new ArrayList(5);
	     try
	     {
	         rs = conn.executeQuery("select * from nms_alarminfo order by id");
	         while(rs.next())
	        	list.add(loadFromRS(rs)); 
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("EventListDao:loadAll()",e);
	         list = null;
	     }
	     finally
	     {
	         conn.close();
	     }
	     return list;
	  }
				
	public List getByTime(String starttime,String totime) throws Exception{
		List alarminfoList=new ArrayList();
		Session session=null;
		try{
			//StringBuffer s = new StringBuffer();
			Query query=session.createQuery("from AlarmInfo e where e.recordtime>= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') " +
					"and e.recordtime<=to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS') order by e.recordtime desc");
			alarminfoList = query.list();
		}
		catch(Exception  e){
			e.printStackTrace();
		}
				// TODO Auto-generated method stub
		return alarminfoList;
	}
	
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   AlarmInfo vo = new AlarmInfo();
	      try
	      {
	          //vo.setId(rs.getInt("id"));
	          vo.setContent(rs.getString("content"));
	          vo.setLevel1(rs.getInt("level1"));
	          Calendar cal = Calendar.getInstance();
	          Date newdate = new Date();
	          cal.setTime(newdate);
	          vo.setRecordtime(cal);
	          vo.setIpaddress(rs.getString("ipaddress"));
	          vo.setType(rs.getString("type"));
	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	          vo = null;
	      }
	      return vo;
	   } 
	//quzhi add  Trap 转换IP   
	   public String ipchange(String ipalias){
		   
		     List list = new ArrayList();
		     try
		     {   
		    	 StringBuffer sql=new StringBuffer();
					sql.append("select ipaddress from topo_ipalias where aliasip=");
					sql.append("'"+ipalias+"'");
		          rs = conn.executeQuery(sql.toString());
		         while(rs.next()){
		        	list.add(rs.getString("ipaddress"));
		         }
					
		     }
		     catch(Exception e)
		     {
		         SysLogger.error("EventListDao:loadAll()",e);
		         list = null;
		     }	     
		     finally
		     {
		         conn.close();
		     }  
		     if(list!=null&&list.size()>0){
				String ip_address=(String)list.get(0);
	                //System.out.println("+++++++ipchange++++++++++++++++==="+ip_address);
					return ip_address;
				}else{
					//System.out.println("+++++++++noipchange++++++++++++++==="+ipalias);
					return ipalias;
				}
		    
				}
	
}