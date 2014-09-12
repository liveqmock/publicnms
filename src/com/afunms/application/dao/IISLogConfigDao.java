/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.IISLogConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;

public class IISLogConfigDao extends BaseDao implements DaoInterface {

	public IISLogConfigDao() {
		super("nms_iislogconfig");
	}
	public boolean delete(String []ids){
		String sql = "";
		for(int i=0;i<ids.length;i++){
			sql = "delete from nms_iislogconfig where id ="+ids[i];
	
		}
		return saveOrUpdate(sql);
	}
	public BaseVo findByID(String id)
	  {
	     BaseVo vo = null;
	     try
	     {
	        rs = conn.executeQuery("select * from nms_iislogconfig where id=" + id );
	        if(rs.next())
	           vo = loadFromRS(rs);
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("IISLogConfigDao.findByID()",e);
	         try {
					conn.rollback();
					// DataGate.freeCon(con);
				} catch (Exception ex) {

				}
	         vo = null;
	     }
	     return vo;
	  }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		IISLogConfig vo=new IISLogConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setHistory_row(rs.getInt("history_row"));
			vo.setFlag(rs.getInt("flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSupperid(rs.getInt("supperid"));// snow add supperid at 2010-5-20
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
	return vo;
	}

	public boolean save(BaseVo vo) {
		IISLogConfig vo1=(IISLogConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_iislogconfig(id,name,ipaddress,history_row,flag,netid,supperid) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("',");
		sql.append(vo1.getHistory_row());
		sql.append(",");
		sql.append(vo1.getFlag());
		sql.append(",'");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("')");
		//生成表
		String ip = vo1.getIpaddress();
//		String ip1 ="",ip2="",ip3="",ip4="";
//		String[] ipdot = ip.split(".");	
//		String tempStr = "";
//		String allipstr = "";
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
		String allipstr = SysUtil.doip(ip);
	 	String _sql = "";
	 	//if (tablename.indexOf("hour")>=0){
	 		//创建SYSLOG表格
	 	if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	 		_sql="create table iislog"+allipstr
		 	+"(ID bigint(20) not null auto_increment,configid bigint(20),ssitename VARCHAR(20),sip VARCHAR(15),"
		 	+"csmethod VARCHAR(10),csuristem VARCHAR(50),csuriquery VARCHAR(100),sport VARCHAR(5),"
		 	+"csusername VARCHAR(100),cip VARCHAR(15),csagent VARCHAR(1000),"
		 	+"recordtime timestamp,scstatus bigint(10),scsubstatus bigint(10),scwin32status bigint(10),PRIMARY KEY  (ID)) ENGINE=InnoDB DEFAULT CHARSET=gb2312";	 		
	 	}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	 		_sql="create table iislog"+allipstr
		 	+"(ID number(20) not null,configid number(20),ssitename VARCHAR(20),sip VARCHAR(15),"
		 	+"csmethod VARCHAR(10),csuristem VARCHAR(50),csuriquery VARCHAR(100),sport VARCHAR(5),"
		 	+"csusername VARCHAR(100),cip VARCHAR(15),csagent VARCHAR(1000),"
		 	+"recordtime date default sysdate-1,scstatus number(10),scsubstatus number(10),scwin32status number(10),PRIMARY KEY  (ID))";	 		
	 	}
	 	//}
		 	System.out.println(_sql);		 	
		 	try{
		 		conn.executeUpdate(_sql);
		 		if(SystemConstant.DBType.equals("oracle")){
					CreateTableManager.createSeqOrcl(conn, "iislog", allipstr);
					CreateTableManager.createTrigerOrcl(conn, "iislog", allipstr,"iislog");
		 		}
		 	}catch(Exception e){
		 		e.printStackTrace(); 		 		
		 	}		
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		IISLogConfig vo1=(IISLogConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_iislogconfig set name='");
		sql.append(vo1.getName());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',history_row=");
		sql.append(vo1.getHistory_row());;
		sql.append(",flag=");
		sql.append(vo1.getFlag());
		sql.append(",netid='");
		sql.append(vo1.getNetid());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("' where id="+vo1.getId());
		return saveOrUpdate(sql.toString());
	}
	
	   public List getIISLogByBID(Vector bids){
		   List rlist = new ArrayList();
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
		   sql.append("select * from nms_iislogconfig "+wstr);
		   System.out.println(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   public List getAllIISLog(){
		   List rlist = new ArrayList();
		   IISLogConfig vo=null;
		   DBManager dao = new DBManager();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_iislogconfig");
		   ResultSet rs=dao.executeQuery(sql.toString());
		   try {
			while(rs.next())
			   {
				   vo = new IISLogConfig();
				   vo.setId(rs.getInt("id"));
				   vo.setName(rs.getString("name"));
				   vo.setIpaddress(rs.getString("ipaddress"));
				   vo.setHistory_row(rs.getInt("history_row"));
				   vo.setFlag(rs.getInt("flag"));
				   vo.setNetid(rs.getString("netid"));
				   rlist.add(vo);
			   }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			dao.close();
		}
		   return rlist;
	   }
	   
	   public List getIISLogByFlag(int flag){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_iislogconfig where flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	   
	   public List getIISLogByIP(String ip){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_iislogconfig where sip = '"+ip+"'");
		   return findByCriteria(sql.toString());
	   }
	   
	  
	   
	  
}