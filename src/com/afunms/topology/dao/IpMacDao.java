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
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.discovery.Host;
import com.afunms.polling.node.*;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.base.*;

public class IpMacDao extends BaseDao implements DaoInterface
{
   public IpMacDao()
   {
	   super("ipmac");	   	  
   }
   
   public List findByCondition(String key,String value)
   {	  
	   return findByCriteria("select * from ipmac where " + key + "='" + value + "'");   
   }
     
   public boolean deleteByHostIp(String hostip)
   {	
 	   String sql = "delete from ipmac where relateipaddr='"+hostip+"'";
 	   return saveOrUpdate(sql);			
   }
   
   public List loadIpMac()
   {
	   return findByCriteria("select * from ipmac order by ip_address"); 
   }
   
   
   public IpMac loadIpMac(int id)
   {
	   
	   List retList = new ArrayList();
	   List ipmacList = findByCriteria("select * from ipmac where id="+id); 
	   if(ipmacList != null && ipmacList.size()>0){
		   IpMac ipmac = (IpMac)ipmacList.get(0);
			   return ipmac;
		   
	   }
	   return null;
   }
   
   public List loadIpMacByIP(String relateipaddr)
   {
	   List ipmacList = findByCriteria("select * from ipmac where relateipaddr='"+relateipaddr+"'"); 
	   return ipmacList;
	   //return findByCriteria("select * from topo_host_node where managed=1 and((category>0 and category<4) or category=7) order by ip_long"); 
   }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   IpMac vo = new IpMac();
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
		   vo.setIfband(rs.getString("ifband"));
		   vo.setIfindex(rs.getString("ifindex"));
		   vo.setIfsms(rs.getString("ifsms"));
		   vo.setIpaddress(rs.getString("ipaddress"));
		   vo.setMac(rs.getString("mac"));
       }
       catch(Exception e)
       {
 	       SysLogger.error("HostNodeDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
   
   public boolean save(BaseVo vo)
   {
	   return false;
   }
   
   public boolean update(BaseVo baseVo)
   {	
	   IpMac vo = (IpMac)baseVo;
	   String sql = "update ipmac set ifband='" + vo.getIfband() + "',ifsms='"+vo.getIfsms()+"' where id=" + vo.getId();
	   return saveOrUpdate(sql);			
   }
   
   public boolean deleteall()
   {	
	   String sql = "delete from ipmac";
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
