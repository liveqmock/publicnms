package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.sf.hibernate.Session;


import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MySqlSpaceConfig;
import com.afunms.application.model.Sqldbconfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;

public class MySqlSpaceConfigDao extends BaseDao implements DaoInterface{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public MySqlSpaceConfigDao() {
		super("system_mysqlspaceconf");
	}
	
	public boolean save(BaseVo baseVo)
	{
		MySqlSpaceConfig vo = (MySqlSpaceConfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_mysqlspaceconf(ipaddress,dbname,linkuse,sms,bak,reportflag,alarmvalue,logflag)values(");
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
		  MySqlSpaceConfig vo = (MySqlSpaceConfig)baseVo;
			boolean result = false;
			
			StringBuffer sql = new StringBuffer();
			sql.append("update system_mysqlspaceconf set ipaddress='");
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
	         SysLogger.error("MySqlSpaceConfigDao:update()",e);
	     }
	     finally
	     {
	    	 conn.close();
	     }  
	     
	     return result;
	  }
	  
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   MySqlSpaceConfig vo = new MySqlSpaceConfig();
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
				rs = conn.executeQuery("select * from system_mysqlspaceconf where ipaddress = '"+ipaddress+"' order by ipaddress");
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
		public List getList() {
			List list = new ArrayList();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select * from system_mysqlspaceconf ");
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
				rs = conn.executeQuery("select * from system_mysqlspaceconf where sms="+smsflag+" order by ipaddress");
				//Query query=session.createQuery("from Oraspaceconfig oraspaceconfig where oraspaceconfig.sms="+smsflag+" order by oraspaceconfig.ipaddress");
				while(rs.next())
		        	list.add(loadFromRS(rs)); 	
				if(list!=null && list.size()>0){
					for(int i=0;i<list.size();i++){
						MySqlSpaceConfig MySqlSpaceConfig = (MySqlSpaceConfig)list.get(i);	
						SysLogger.info(MySqlSpaceConfig.getIpaddress()+":"+MySqlSpaceConfig.getDbname()+":"+MySqlSpaceConfig.getLogflag()+"    value:"+MySqlSpaceConfig.getAlarmvalue());
						retValue.put(MySqlSpaceConfig.getIpaddress()+":"+MySqlSpaceConfig.getDbname()+":"+MySqlSpaceConfig.getLogflag(), MySqlSpaceConfig);				
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
		public void fromLastToMySqlSpaceConfig()
		throws Exception {		
			List list=new ArrayList();
			List list1=new ArrayList();
			List shareList = new ArrayList();
			int dbflag = 0;
			int logflag = 1;
			Hashtable oraspacehash= new Hashtable();
			Session session=null;
			MySqlSpaceConfig MySqlSpaceConfig = null;
			Vector configV = new Vector();
			try{
			
				//从sqldb配置表里获取列表
				rs = conn.executeQuery("select * from system_mysqlspaceconf order by ipaddress");
				while(rs.next())
		        	list1.add(loadFromRS(rs)); 
			
			if (list1 != null && list1.size()>0){			
				for(int i=0;i<list1.size();i++){
					MySqlSpaceConfig = (MySqlSpaceConfig)list1.get(i);	
					//IP:数据库名称:LOGFLAG				
					oraspacehash.put(MySqlSpaceConfig.getIpaddress()+":"+MySqlSpaceConfig.getDbname()+":"+MySqlSpaceConfig.getLogflag(),MySqlSpaceConfig);
				}
			}
					
			//从内存中得到所有SQLDB采集信息
			Hashtable sharedata = ShareData.getSqldbdata();		
			//从数据库得到监视SQLDB列表
			DBDao dbdao = new DBDao();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = typedao.findByDbtype("mysql");
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
											MySqlSpaceConfig = new MySqlSpaceConfig();
											MySqlSpaceConfig.setDbname(dbname);
											MySqlSpaceConfig.setBak("");
											MySqlSpaceConfig.setIpaddress(ip);
											MySqlSpaceConfig.setLinkuse("");	
											MySqlSpaceConfig.setAlarmvalue(90);
											MySqlSpaceConfig.setLogflag(0);
											MySqlSpaceConfig.setSms(new Integer(0));//0：不告警 1：告警，默认的情况是不发送短信
											MySqlSpaceConfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
											//configV.add(MySqlSpaceConfig);
											conn = new  DBManager();
											save(MySqlSpaceConfig);
											oraspacehash.put(ip+":"+dbname+":"+dbflag,MySqlSpaceConfig);																							
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
											MySqlSpaceConfig = new MySqlSpaceConfig();
											MySqlSpaceConfig.setDbname(dbname);
											MySqlSpaceConfig.setBak("");
											MySqlSpaceConfig.setIpaddress(ip);
											MySqlSpaceConfig.setLinkuse("");	
											MySqlSpaceConfig.setAlarmvalue(90);
											MySqlSpaceConfig.setLogflag(1);
											MySqlSpaceConfig.setSms(new Integer(0));//0：不告警 1：告警，默认的情况是不发送短信
											MySqlSpaceConfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
											conn = new  DBManager();
											save(MySqlSpaceConfig);
											oraspacehash.put(ip+":"+dbname+":"+dbflag,MySqlSpaceConfig);																							
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
		
		public void  deleteByIP(String ip){
			String sql="delete from system_mysqlspaceconf where ipaddress='"+ip+"'";
				try{
					conn.executeUpdate(sql);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}

}
