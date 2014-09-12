/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;


import net.sf.hibernate.Session;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.model.*;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.event.model.AlarmInfo;
import com.afunms.polling.om.*;
import com.afunms.polling.task.*;

public class SqldbconfigDao extends BaseDao implements DaoInterface{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public SqldbconfigDao() {
		super("system_sqldbconf");
	}
	
	public boolean save(BaseVo baseVo)
	{
		Sqldbconfig vo = (Sqldbconfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_sqldbconf(ipaddress,dbname,linkuse,sms,bak,reportflag,alarmvalue,logflag)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getDbname());	
		sql.append("','");
		sql.append(vo.getLinkuse());
		sql.append("',");
		sql.append(vo.getSms());
		sql.append(",'");
		sql.append(vo.getBak());
		sql.append("',");
		sql.append(vo.getReportflag());
		sql.append(",");
		sql.append(vo.getAlarmvalue());
		sql.append(",");
		sql.append(vo.getLogflag());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}
	
	  public boolean update(BaseVo baseVo)
	  {
		  Sqldbconfig vo = (Sqldbconfig)baseVo;
			boolean result = false;
			
			StringBuffer sql = new StringBuffer();
			sql.append("update system_sqldbconf set ipaddress='");
			sql.append(vo.getIpaddress());
			sql.append("',dbname='");
			sql.append(vo.getDbname());	
			sql.append("',linkuse='");
			sql.append(vo.getLinkuse());
			sql.append("',sms=");
			sql.append(vo.getSms());
			sql.append(",bak='");
			sql.append(vo.getBak());
			sql.append("',reportflag=");
			sql.append(vo.getReportflag());
			sql.append(",alarmvalue=");
			sql.append(vo.getAlarmvalue());
			sql.append(",logflag=");
			sql.append(vo.getLogflag());
			sql.append(" where id=");
			sql.append(vo.getId());
	     
	     try
	     {
	         conn.executeUpdate(sql.toString());
	         result = true;
	     }
	     catch(Exception e)
	     {
	    	 e.printStackTrace();
	    	 result = false;
	         SysLogger.error("SqldbconfigDao:update()",e);
	     }
	     finally
	     {
	    	 conn.close();
	     }  
	     
	     return result;
	  }
	  
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   Sqldbconfig vo = new Sqldbconfig();
	      try
	      {
	          vo.setId(rs.getInt("id"));
	          vo.setIpaddress(rs.getString("ipaddress"));
	          vo.setDbname(rs.getString("dbname"));
	          vo.setLinkuse(rs.getString("linkuse"));
	          vo.setAlarmvalue(rs.getInt("alarmvalue"));
	          vo.setBak(rs.getString("bak"));
	          vo.setReportflag(rs.getInt("reportflag"));
	          vo.setSms(rs.getInt("sms"));
	          vo.setLogflag(rs.getInt("logflag"));
	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	          //SysLogger.info("EventListDao.loadFromRS()",e);
	          vo = null;
	      }
	      return vo;
	   } 
		/*
		 * 根据IP查询
		 * 
		 */
		public List getByIp(String ipaddress) {
			List list = new ArrayList();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select * from system_sqldbconf where ipaddress = '"+ipaddress+"' order by ipaddress");
				//Query query=session.createQuery("from Oraspaceconfig oraspaceconfig where oraspaceconfig.sms="+smsflag+" order by oraspaceconfig.ipaddress");
				while(rs.next())
		        	list.add(loadFromRS(rs)); 	
			}
			catch(Exception e){
				e.printStackTrace();
			}
		// TODO Auto-generated method stub
			return list;
	}
		/*
		 * 根据IP和是否要显示于日报表的标志位查询
		 * 
		 */
		public Hashtable getByAlarmflag(Integer smsflag) {
			List list = new ArrayList();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select * from system_sqldbconf where sms="+smsflag+" order by ipaddress");
				//Query query=session.createQuery("from Oraspaceconfig oraspaceconfig where oraspaceconfig.sms="+smsflag+" order by oraspaceconfig.ipaddress");
				while(rs.next())
		        	list.add(loadFromRS(rs)); 	
				if(list!=null && list.size()>0){
					for(int i=0;i<list.size();i++){
						Sqldbconfig sqldbconfig = (Sqldbconfig)list.get(i);	
						SysLogger.info(sqldbconfig.getIpaddress()+":"+sqldbconfig.getDbname()+":"+sqldbconfig.getLogflag()+"    value:"+sqldbconfig.getAlarmvalue());
						retValue.put(sqldbconfig.getIpaddress()+":"+sqldbconfig.getDbname()+":"+sqldbconfig.getLogflag(), sqldbconfig);				
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		// TODO Auto-generated method stub
			return retValue;
	}
		
		/*
		 * 
		 * 从内存和数据库表里获取每个IP的端口信息，存入端口配置表里
		 */
		public void fromLastToSqldbconfig()
		throws Exception {		
			List list=new ArrayList();
			List list1=new ArrayList();
			List shareList = new ArrayList();
			int dbflag = 0;
			int logflag = 1;
			Hashtable oraspacehash= new Hashtable();
			Session session=null;
			Sqldbconfig sqldbconfig = null;
			Vector configV = new Vector();
			try{
			
				//从sqldb配置表里获取列表
				rs = conn.executeQuery("select * from system_sqldbconf order by ipaddress");
				while(rs.next())
		        	list1.add(loadFromRS(rs)); 
			
			if (list1 != null && list1.size()>0){			
				for(int i=0;i<list1.size();i++){
					sqldbconfig = (Sqldbconfig)list1.get(i);	
					//IP:数据库名称:LOGFLAG				
					oraspacehash.put(sqldbconfig.getIpaddress()+":"+sqldbconfig.getDbname()+":"+sqldbconfig.getLogflag(),sqldbconfig);
				}
			}
					
			//从内存中得到所有SQLDB采集信息
			Hashtable sharedata = ShareData.getSqldbdata();		
			//从数据库得到监视SQLDB列表
			DBDao dbdao = new DBDao();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = typedao.findByDbtype("sqlserver");
			shareList = dbdao.getDbByTypeMonFlag(typevo.getId(), 1);
			if (shareList != null && shareList.size()>0){
				for(int i=0;i<shareList.size();i++){
					DBVo dbmonitorlist = (DBVo)shareList.get(i);					
					if(sharedata.get(dbmonitorlist.getIpAddress()) != null){
						Hashtable dbs = (Hashtable)sharedata.get(dbmonitorlist.getIpAddress());
						if (dbs == null )continue;					
						Hashtable spaces = new Hashtable();
						spaces.put("ip", dbmonitorlist.getIpAddress());
						spaces.put("dbs", dbs);
						list.add(spaces);
					}
				}
			}
			//判断采集到的SQLDB信息是否已经在SQLDB配置表里已经存在，若不存在则加入
			if (list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					Hashtable dbs = (Hashtable)list.get(i);
					if(dbs != null && dbs.size()>0){
						String ip = (String)dbs.get("ip");					
						Hashtable db_log = (Hashtable)dbs.get("dbs");		
						if(db_log != null && db_log.size()>0){
							//db_log容器里存放database和logfile
							Hashtable database = (Hashtable)db_log.get("database");
							Hashtable logfile = (Hashtable)db_log.get("logfile");
							Vector names = (Vector)db_log.get("names");						
							//处理DB配置
							if(names != null && names.size()>0){
								for(int k=0;k<names.size();k++){
									String dbname = (String)names.get(k);
									if(database.get(dbname) != null){
										Hashtable db = (Hashtable)database.get(dbname);
										if (!oraspacehash.containsKey(ip+":"+dbname+":"+dbflag)){
											//不存在该数据库,则加入
											sqldbconfig = new Sqldbconfig();
											sqldbconfig.setDbname(dbname);
											sqldbconfig.setBak("");
											sqldbconfig.setIpaddress(ip);
											sqldbconfig.setLinkuse("");	
											sqldbconfig.setAlarmvalue(90);
											sqldbconfig.setLogflag(0);
											sqldbconfig.setSms(new Integer(0));//0：不告警 1：告警，默认的情况是不发送短信
											sqldbconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
											//configV.add(sqldbconfig);
											conn = new  DBManager();
											save(sqldbconfig);
											oraspacehash.put(ip+":"+dbname+":"+dbflag,sqldbconfig);																							
										}									
									}
								}
							}

							
							//处理LOG配置
							if(names != null && names.size()>0){
								for(int k=0;k<names.size();k++){
									String dbname = (String)names.get(k);
									if(logfile.get(dbname) != null){
										Hashtable db = (Hashtable)logfile.get(dbname);
										if (!oraspacehash.containsKey(ip+":"+dbname+":"+logflag)){
											//不存在该数据库,则加入
											sqldbconfig = new Sqldbconfig();
											sqldbconfig.setDbname(dbname);
											sqldbconfig.setBak("");
											sqldbconfig.setIpaddress(ip);
											sqldbconfig.setLinkuse("");	
											sqldbconfig.setAlarmvalue(90);
											sqldbconfig.setLogflag(1);
											sqldbconfig.setSms(new Integer(0));//0：不告警 1：告警，默认的情况是不发送短信
											sqldbconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
											conn = new  DBManager();
											save(sqldbconfig);
											oraspacehash.put(ip+":"+dbname+":"+dbflag,sqldbconfig);																							
										}									
									}
								}
							}																
						}
						
					}
				}
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		// TODO Auto-generated method stub
		}
		
		//zhushouzhi--------------new getbyip(ip,flag)
		public List getByIp(String ipaddress,Integer smsflag) {
			List list = new ArrayList();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select * from system_sqldbconf where ipaddress = '"+ipaddress+"' and sms="+smsflag+" order by ipaddress");
				//Query query=session.createQuery("from Oraspaceconfig oraspaceconfig where oraspaceconfig.sms="+smsflag+" order by oraspaceconfig.ipaddress");
				while(rs.next())
		        	list.add(loadFromRS(rs)); 	
			}
			catch(Exception e){
				e.printStackTrace();
			}
		// TODO Auto-generated method stub
			return list;
	}
		//zhushouzhi----------------------end
		public void  deleteByIP(String ip){
			String sql="delete from  system_sqldbconf where ipaddress='"+ip+"'";
				try{
					conn.executeUpdate(sql);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}

}   