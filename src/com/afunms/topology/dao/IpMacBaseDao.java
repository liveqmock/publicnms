/**
 * <p>Description:与nodedao都是操作表nms_topo_node,但nodedao主要用于发现</p>
 * <p>Description:而toponodedao主要用于页面操作</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.IpMacBase;

public class IpMacBaseDao extends BaseDao implements DaoInterface
{
   public IpMacBaseDao()
   {
	   super("nms_ipmacbase");	   	  
   }
   
   public List findByCondition(String key,String value)
   {	  
	   return findByCriteria("select * from nms_ipmacbase where " + key + "='" + value + "'");   
   }
     
   
   public List loadIpMac()
   {
	   return findByCriteria("select * from nms_ipmacbase order by ip_address"); 
   }
   
   
   public IpMacBase loadIpMacBase(int id)
   {
	   
	   List retList = new ArrayList();
	   List ipmacbaseList = findByCriteria("select * from nms_ipmacbase where id="+id); 
	   if(ipmacbaseList != null && ipmacbaseList.size()>0){
		   IpMacBase ipmac = (IpMacBase)ipmacbaseList.get(0);
			   return ipmac;
		   
	   }
	   return null;
   }
   
   public List loadIpMacBaseByIP(String relateipaddr)
   {
	   List ipmacbaseList = findByCriteria("select * from nms_ipmacbase where relateipaddr='"+relateipaddr+"'"); 
	   return ipmacbaseList;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
   
   public Hashtable loadMacBaseByIP(String relateipaddr)
   {
	   Hashtable rvalue = new Hashtable();
	   List ipmacbaseList = findByCriteria("select * from nms_ipmacbase where relateipaddr='"+relateipaddr+"'"); 
	   if(ipmacbaseList !=null && ipmacbaseList.size()>0){
		   for(int i=0;i<ipmacbaseList.size();i++){
			   IpMacBase vo = (IpMacBase)ipmacbaseList.get(i);
			   rvalue.put(vo.getMac(), vo.getIfband()+"");
		   }
	   }
	   return rvalue;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
   
   public Hashtable loadMacBaseByRIPAndIpAndMac()
   {
	   Hashtable rvalue = new Hashtable();
	   List ipmacbaseList = findByCriteria("select * from nms_ipmacbase order by relateipaddr"); 
	   if(ipmacbaseList !=null && ipmacbaseList.size()>0){
		   for(int i=0;i<ipmacbaseList.size();i++){
			   IpMacBase vo = (IpMacBase)ipmacbaseList.get(i);
			   rvalue.put(vo.getRelateipaddr()+":"+vo.getIpaddress()+":"+vo.getMac(), vo);
		   }
	   }
	   return rvalue;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
   
   public List loadIpMacBaseByRIPMAC(String relateipaddr,String mac)
   {
	   List ipmacbaseList = findByCriteria("select * from nms_ipmacbase where relateipaddr='"+relateipaddr+"' and mac='"+mac+"'"); 
	   return ipmacbaseList;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
   
   public boolean deleteall()
   {	
	   String sql = "delete from nms_ipmacbase";
	   return saveOrUpdate(sql);			
   }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   IpMacBase vo = new IpMacBase();
       try
       {
      		Calendar tempCal = Calendar.getInstance();							
    		Date cc = new Date();
    		cc.setTime(rs.getTimestamp("collecttime").getTime());
    		tempCal.setTime(cc);
		   vo.setId(rs.getLong("id"));
		   vo.setRelateipaddr(rs.getString("relateipaddr"));
		   vo.setBak(rs.getString("bak"));
		   vo.setCollecttime(tempCal);
		   vo.setIfband(rs.getInt("ifband"));
		   vo.setIfindex(rs.getString("ifindex"));
		   vo.setIfsms(rs.getString("ifsms"));
		   vo.setIpaddress(rs.getString("ipaddress"));
		   vo.setMac(rs.getString("mac"));
		   vo.setIftel(rs.getString("iftel"));
		   vo.setIfemail(rs.getString("ifemail"));
		   vo.setEmployee_id(rs.getInt("employee_id"));
       }
       catch(Exception e)
       {
 	       SysLogger.error("HostNodeDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
   
   public boolean save(BaseVo baseVo)
   {
	   IpMacBase vo = (IpMacBase)baseVo;
	   Calendar tempCal = (Calendar)vo.getCollecttime();
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
	   StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_ipmacbase(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms,iftel,ifemail,employee_id,bak)values(");
		sql.append("'");
		sql.append(vo.getRelateipaddr());
		sql.append("','");
		sql.append(vo.getIfindex());
		sql.append("','");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getMac());
		if(SystemConstant.DBType.equals("mysql")){
			sql.append("','");
			sql.append(time);
			sql.append("','");
		}else if(SystemConstant.DBType.equals("oracle")){
			sql.append("',to_date('");
			sql.append(time);
			sql.append("','yyyy-mm-dd hh24:mi:ss'),'");
		}
		sql.append(vo.getIfband());
		sql.append("','");
		sql.append(vo.getIfsms());
		sql.append("','");
		sql.append(vo.getIftel());
		sql.append("','");
		sql.append(vo.getIfemail());
		sql.append("',");
		sql.append(vo.getEmployee_id());
		sql.append(",'");
		sql.append(vo.getBak());
		sql.append("')");
	//	System.out.println("IpMacBaseDao.java _______ save(BaseVo baseVo)---->>> "+sql.toString());
		return saveOrUpdate(sql.toString());
   }
   
   public boolean update(BaseVo baseVo)
   {	
	   IpMacBase vo = (IpMacBase)baseVo;
	   String sql = "update nms_ipmacbase set ifband="+vo.getIfband()+",ifemail='" + vo.getIfemail() +"',iftel='"+vo.getIftel()+ "',ifsms='"+vo.getIfsms()+"',employee_id="+vo.getEmployee_id()+" where id=" + vo.getId();
	   return saveOrUpdate(sql);			
   }
   
   public boolean deleteByHostIp(String hostip)
   {	
	   String sql = "delete from nms_ipmacbase where relateipaddr='"+hostip+"'";
	   return saveOrUpdate(sql);			
   }

   
   public List<String> getIfIps()
   {
	   List<String> allIps = new ArrayList<String>();
	   try
	   {
		   rs = conn.executeQuery("select a.ip_address from topo_interface a,topo_host_node b where a.node_id=b.id and b.category<4 and a.ip_address<>'' order by a.id");
		   while(rs.next())
		   {
		       String ips = rs.getString("ip_address");
		       allIps.add(ips);
		   }
	   }
	   catch(Exception e)
	   {		   
	   }
	   return allIps;
   }
   public String loadOneColFromRS(ResultSet rs){
	   return "";
   }
}
