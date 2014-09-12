package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.WebConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;


public class DnsConfigDao extends BaseDao implements DaoInterface {
public DnsConfigDao() {
		
		super("nms_dnsconfig");
		
	}
	
	public boolean delete(String []ids){
		return super.delete(ids);
	}
   /**
    * 
    * 取出的数值放到vo里
    */
	public BaseVo loadFromRS(ResultSet rs) {
		DnsConfig vo=new DnsConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setUsername(rs.getString("username"));
			vo.setPassword(rs.getString("password"));
			vo.setHostip(rs.getString("hostip"));
			vo.setHostinter(rs.getInt("hostinter"));
			vo.setDns(rs.getString("dns"));
			vo.setDnsip(rs.getString("dnsip"));
			vo.setFlag(rs.getInt("flag"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setSupperid(rs.getInt("supperid"));// snow add supperid at 2010-5-20
		} catch (SQLException e) {
			
			SysLogger.error("DnsConfigDao.loadFromRS()",e);
		}
		
		return vo;
	}
    /**
     * DNS
     * 添加一条记录
     */
	public boolean save(BaseVo vo) {
		DnsConfig vo1=(DnsConfig)vo;
		StringBuffer sql=new StringBuffer();
		// snow add id at 2010-5-20
		sql.append("insert into nms_dnsconfig(id,username,password,hostip,hostinter,dns,dnsip,flag,sendmobiles,sendemail,sendphone,netid,supperid) values('");
		sql.append(vo1.getId());
		sql.append("','");
		sql.append(vo1.getUsername());
		sql.append("','");
		sql.append(vo1.getPassword());
		sql.append("','");
		sql.append(vo1.getHostip());
		sql.append("','");
		sql.append(vo1.getHostinter());
		sql.append("','");
		sql.append(vo1. getDns());
		sql.append("','");
		sql.append(vo1.getDnsip());
		sql.append("','");
		sql.append(vo1.getFlag());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("')");
	    
		return saveOrUpdate(sql.toString());
		
	}  
	  /**
	   * DNS
	   * 根据id 查处所有
	   * @param bids
	   * @return
	   */
	   public List getDnsByBID(Vector bids){
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
		   sql.append("select * from nms_dnsconfig "+wstr);
		 
		   return findByCriteria(sql.toString());
	   }
	   /**
	    * DNS
	    * 根据flag查询一条记录
	    * @param flag
	    * @return
	    */
	   public List getDnsByFlag(int flag){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
	
		   sql.append("select * from nms_dnsconfig where flag = "+flag);
		  
		   return findByCriteria(sql.toString());
	   }
	   /**
	    * DNS
	    * 根据id 查询某条记录
	    * @param id
	    * @return
	    */
	   public List getDnsById(int id){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
	
		   sql.append("select * from nms_dnsconfig where id = "+id);
		   
		   return findByCriteria(sql.toString());
	   }
	/**
	 * DNS
	 * 修改一条记录
	 */
	   public boolean update(BaseVo vo) {
			DnsConfig vo1=(DnsConfig)vo;
			StringBuffer sql=new StringBuffer();
		
			sql.append("update nms_dnsconfig set username ='");
			sql.append(vo1.getUsername());
			sql.append("',password='");
			sql.append(vo1.getPassword());
			sql.append("',hostip='");
			sql.append(vo1.getHostip());
			sql.append("',hostinter='");
			sql.append(vo1.getHostinter());
			sql.append("',dns='");
			sql.append(vo1.getDns());
			sql.append("',dnsip='");
			sql.append(vo1.getDnsip());
			sql.append("',flag='");
			sql.append(vo1.getFlag());
			sql.append("',sendmobiles='");
			sql.append(vo1.getSendmobiles());
			sql.append("',sendemail='");
			sql.append(vo1.getSendemail());
			sql.append("',sendphone='");
			sql.append(vo1.getSendphone());
			sql.append("',netid='");
			sql.append(vo1.getNetid());
			sql.append("',supperid='");
			sql.append(vo1.getSupperid());
			sql.append("' where id="+vo1.getId());
		
			return saveOrUpdate(sql.toString());
			
		}
	
	public List<DnsConfig> getDNSConfigListByMonFlag(Integer flag){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_dnsconfig where flag= ");
		sql.append(flag);
		return findByCriteria(sql.toString());
	}
	
	/**
	 * 从数据库获取采集的dns的数据信息
	 * @param nodeid
	 * @return
	 * @throws Exception
	 */
	public Hashtable getDnsDataHashtable(String nodeid) throws Exception{
		Hashtable retHash = new Hashtable();
		try {
			//SysLogger.info();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select * from nms_dns_temp where nodeid = '");
			sqlBuffer.append(nodeid);
			sqlBuffer.append("'");
			 rs = conn.executeQuery(sqlBuffer.toString());
			 List mx = new ArrayList();
			 List ns = new ArrayList();
			 List cache = new ArrayList();
			while (rs.next()) {
				String entity = rs.getString("entity");
				String value = rs.getString("value");
				if("mx".equalsIgnoreCase(entity)){//key为mx的情况
					mx.add(value);
				}else if("ns".equalsIgnoreCase(entity)){//key为ns的情况
					ns.add(value);
				}else if ("cache".equalsIgnoreCase(entity)) {//key为cache的情况
					cache.add(value);
				}else{//普通键值对的情形
					retHash.put(entity, value);
				}
			}
			retHash.put("mx", mx);
			retHash.put("ns", ns);
			retHash.put("cache", cache);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs != null){
				rs.close();
			}
		}
		return retHash;
	}
	  public Hashtable getPingDataById(String ip,Integer id,String starttime,String endtime) {
	 	   Hashtable hash = new Hashtable();
	 	   if (!starttime.equals("") && !endtime.equals("")) {
	 		   List list1 = new ArrayList();
	 		   String sql = "";
	 		  String allipstr = SysUtil.doip(ip);
	 		   if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
	 			   sql = "select a.thevalue,a.collecttime from  dnsping"+allipstr+" a where " +
	 			   	"(a.collecttime >= '"+starttime +"' and  a.collecttime <= '"+endtime+"') order by id";;
	 		   }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
	 			   sql = "selectselect a.thevalue,a.collecttime from  dnsping"+" a where " +
	 			   	" (a.collecttime >= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" and  a.collecttime <= "+"to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')"+") order by id";;
	 		   }
//	 		   SysLogger.info(sql);
	 		   int i = 0;
	 		   double curPing=0;
	 		   double avgPing = 0;
	 		  double  minPing=0;
	 		   rs = conn.executeQuery(sql);
	 		   try {
	 			   while (rs.next()) {
	 				   i = i + 1;
	 				   Vector v = new Vector();
	 				   String thevalue = rs.getString("thevalue");
	 				   String collecttime = rs.getString("collecttime");
	 				  // String reason = rs.getString("reason");
	 				   thevalue=String.valueOf(Integer.parseInt(thevalue) );
	 				   v.add(0, Float.parseFloat(thevalue));
	 				   v.add(1, collecttime);
	 				   v.add(2, "%");
	 				   avgPing = avgPing + Float.parseFloat(thevalue);
	 				   curPing=Float.parseFloat(thevalue);
	 				   if (curPing<minPing)
	 					 minPing=curPing;
	 				    list1.add(v);
	 			   }
	 			   
	 		   } catch (SQLException e) {
	 			   e.printStackTrace();
	 		   } finally {
	 			   try {
	 				   if (rs!=null)
	 				   rs.close();
	 				   if (conn!=null)
	 					conn.close();
	 				
	 			   } catch (SQLException e) {
	 				   e.printStackTrace();
	 			   }
	 		   }
	 		   hash.put("list", list1);
	 		   if (list1 != null && list1.size() > 0) {
	 			   hash.put("avgPing", CEIString.round(avgPing/ list1.size(), 2)+"");
	 		   } else {
	 			   hash.put("avgPing", "0");
	 		   }
	 		   hash.put("minPing", minPing+"");
	 		   hash.put("curPing", curPing+"");
	 	   }
	 	   return hash;
	    }
	 //处理Ping得到的数据，放到历史表里
		public synchronized boolean createHostData(Pingcollectdata pingdata) {
			if (pingdata == null )
				return false;	
			try{			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				Vector v = new Vector();
				//for (int i = 0; i < hostdatavec.size(); i++) {
					String ip = pingdata.getIpaddress();				
					if (pingdata.getRestype().equals("dynamic")) {						
						String allipstr = SysUtil.doip(ip);
						Calendar tempCal = (Calendar)pingdata.getCollecttime();							
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "";
						String type=pingdata.getCategory();
//						if("ApachePing".equals(type)){
							tablename = "dnsping"+allipstr;
//						}
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
			} 
//			finally {
//				conn.close();
//				
//			}
			return true;
		}
}
