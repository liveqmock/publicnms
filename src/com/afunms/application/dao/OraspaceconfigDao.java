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
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.event.model.AlarmInfo;
import com.afunms.polling.om.*;
import com.afunms.polling.task.*;

public class OraspaceconfigDao extends BaseDao implements DaoInterface{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public OraspaceconfigDao() {
		super("system_oraspaceconf");
	}
	
	public boolean save(BaseVo baseVo)
	{
		Oraspaceconfig vo = (Oraspaceconfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_oraspaceconf(ipaddress,spacename,linkuse,sms,bak,reportflag,alarmvalue)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getSpacename());	
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
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}
	
	  public boolean update(BaseVo baseVo)
	  {
		  Oraspaceconfig vo = (Oraspaceconfig)baseVo;
			boolean result = false;
			
			StringBuffer sql = new StringBuffer();
			sql.append("update system_oraspaceconf set ipaddress='");
			sql.append(vo.getIpaddress());
			sql.append("',spacename='");
			sql.append(vo.getSpacename());	
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
			sql.append(" where id=");
			sql.append(vo.getId());
	     
	     try
	     {
	         conn.executeUpdate(sql.toString());
	         result = true;
	     }
	     catch(Exception e)
	     {
	    	 result = false;
	         SysLogger.error("OraspaceconfigDao:update()",e);
	     }
	     finally
	     {
	    	 conn.close();
	     }  
	     
	     return result;
	  }
	  
	   public BaseVo loadFromRS(ResultSet rs)
	   {
		   Oraspaceconfig vo = new Oraspaceconfig();
	      try
	      {
	          vo.setId(rs.getInt("id"));
	          vo.setIpaddress(rs.getString("ipaddress"));
	          vo.setSpacename(rs.getString("spacename"));
	          vo.setLinkuse(rs.getString("linkuse"));
	          vo.setAlarmvalue(rs.getInt("alarmvalue"));
	          vo.setBak(rs.getString("bak"));
	          vo.setReportflag(rs.getInt("reportflag"));
	          vo.setSms(rs.getInt("sms"));
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
		 * 根据IP和是否要显示于日报表的标志位查询
		 * 
		 */
		public Hashtable getByAlarmflag(Integer smsflag) {
			List list = new ArrayList();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select * from system_oraspaceconf where sms="+smsflag+" order by ipaddress");
				//Query query=session.createQuery("from Oraspaceconfig oraspaceconfig where oraspaceconfig.sms="+smsflag+" order by oraspaceconfig.ipaddress");
				while(rs.next())
		        	list.add(loadFromRS(rs)); 	
				if(list!=null && list.size()>0){
					for(int i=0;i<list.size();i++){
						Oraspaceconfig oraspaceconfig = (Oraspaceconfig)list.get(i);	
						String ipaddr=oraspaceconfig.getIpaddress();
						String []iparry=ipaddr.split(":");
						IpTranslation tranfer=new IpTranslation();
						String []tip=tranfer.getIpFromHex(iparry[0]);
						retValue.put(tip[0]+"."+tip[1]+"."+tip[2]+"."+tip[3]+":"+iparry[1]+":"+oraspaceconfig.getSpacename(), oraspaceconfig);	
						//SysLogger.info(tip[0]+"."+tip[1]+"."+tip[2]+"."+tip[3]+":"+iparry[1]+":"+oraspaceconfig.getSpacename()+"===="+oraspaceconfig.getSpacename());
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
		 * 根据IP查询
		 * 
		 */
		public List getByIp(String ipaddress,Integer smsflag) {
			List list = new ArrayList();
			Session session=null;
			if(conn==null)
				conn=new DBManager();
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select * from system_oraspaceconf where ipaddress = '"+ipaddress+"' and sms="+smsflag+" order by ipaddress");
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
		 * 根据IP查询
		 * 
		 */
		public List getByIp(String ipaddress) {
			List list = new ArrayList();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				
				rs = conn.executeQuery("select * from system_oraspaceconf where ipaddress = '"+ipaddress+"' order by ipaddress");
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
		 * 查询唯一IP列表
		 * auth@hukelei
		 * date 2010-01-21
		 * 
		 */
		public Hashtable getDistinctIp() {
			Hashtable returnHash = new Hashtable();
			Session session=null;
			Hashtable retValue = new Hashtable();
			try{
				rs = conn.executeQuery("select distinct(ipaddress) from system_oraspaceconf");
				while(rs.next())
					returnHash.put(rs.getString("ipaddress"),rs.getString("ipaddress")); 	
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return returnHash;
	}
		
		/*
		 * 
		 * 从内存和数据库表里获取每个IP的端口信息，存入端口配置表里
		 */
		public void fromLastToOraspaceconfig()
		throws Exception {		
			List list=new ArrayList();
			List list1=new ArrayList();
			List shareList = new ArrayList();
			Hashtable oraspacehash= new Hashtable();
			Session session=null;
			Vector configV = new Vector();
			IpTranslation tranfer=new IpTranslation();
			try{
				//从ORASPACCE配置表里获取列表
				rs = conn.executeQuery("select * from system_oraspaceconf order by ipaddress");
				while(rs.next())
		        	list1.add(loadFromRS(rs)); 	
				if(list1!=null && list1.size()>0){
					for(int i=0;i<list1.size();i++){
						Oraspaceconfig oraspaceconfig = (Oraspaceconfig)list1.get(i);	
						//IP:表空间名称	
						
						oraspacehash.put(oraspaceconfig.getIpaddress()+":"+oraspaceconfig.getSpacename(),oraspaceconfig);					
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			try{

			//从内存中得到所有ORASPACCE采集信息
			Hashtable sharedata = ShareData.getOraspacedata();
			
			//从数据库得到监视ORASPACCE列表
			DBDao dbdao = new DBDao();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = typedao.findByDbtype("oracle");
			shareList = dbdao.getDbByTypeMonFlag(typevo.getId(), 1);
			if (shareList != null && shareList.size()>0){
				for(int i=0;i<shareList.size();i++){
//					OraclePartsDao oracleDao=null;
					try{
						DBVo dbmonitorlist = (DBVo)shareList.get(i);
//						 oracleDao=new OraclePartsDao();
//						List<OracleEntity> oracles=oracleDao.findOracleParts(dbmonitorlist.getId());
//						for(OracleEntity ora:oracles){
//							
//						}
						if(sharedata.get(dbmonitorlist.getIpAddress()+":"+dbmonitorlist.getId()) != null){
							Vector tableinfo_v = (Vector)sharedata.get(dbmonitorlist.getIpAddress()+":"+dbmonitorlist.getId());
							if (tableinfo_v == null )continue;
							Hashtable spaces = new Hashtable();
							spaces.put("ip", dbmonitorlist.getIpAddress()+":"+dbmonitorlist.getId());
							spaces.put("tableinfo_v", tableinfo_v);
							list.add(spaces);
						}
					}catch(Exception e){
						e.printStackTrace();
					}finally{
//						if(oracleDao!=null)
//						  oracleDao.close();
					}
					
					
				}
			}
			//判断采集到的ORASPACCE信息是否已经在ORASPACCE配置表里已经存在，若不存在则加入
			if (list != null && list.size()>0){
				for(int i=0;i<list.size();i++){
					Hashtable spaces = (Hashtable)list.get(i);
					if(spaces != null && spaces.size()>0){
						String ip = (String)spaces.get("ip");					
						Vector tableinfo_v = (Vector)spaces.get("tableinfo_v");		
						if(tableinfo_v != null && tableinfo_v.size()>0){
							Oraspaceconfig oraspaceconfig = null;
							for(int k=0;k<tableinfo_v.size();k++){
								Hashtable return_value = (Hashtable)tableinfo_v.get(k);
								String spacename = (String)return_value.get("tablespace");
								String []iparr=ip.split(":");
								String tip=tranfer.formIpToHex(iparr[0]);
								if (!oraspacehash.containsKey(tip+":"+iparr[1]+":"+spacename)){					
									oraspaceconfig = new Oraspaceconfig();
									oraspaceconfig.setSpacename(spacename);
									oraspaceconfig.setBak("");
									oraspaceconfig.setIpaddress(tip+":"+iparr[1]);
									oraspaceconfig.setLinkuse("");	
									oraspaceconfig.setAlarmvalue(90);
									oraspaceconfig.setSms(new Integer(0));//0：不告警 1：告警，默认的情况是不发送短信
									oraspaceconfig.setReportflag(new Integer(0));// 0：不存在于报表 1：存在于报表，默认的情况是不存在于报表
									conn = new  DBManager();			
									save(oraspaceconfig);
									//configV.add(oraspaceconfig);
									oraspacehash.put(tip+":"+iparr[1]+":"+spacename,oraspaceconfig);					
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
		String sql="delete from system_oraspaceconf where ipaddress='"+ip+"'";
			try{
				conn.executeUpdate(sql);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
}   