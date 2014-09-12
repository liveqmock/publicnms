/**
 * <p>Description:operate table NMS_TOPO_LINK</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-12
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.discovery.RepairLink;

public class RepairLinkDao extends BaseDao implements DaoInterface
{
   public RepairLinkDao()
   {
	   super("topo_repair_link");	   	  
   }

   public List<RepairLink> loadNetLinks()
   {
	   return loadByTpye();
   }

   public List<RepairLink> loadServerLinks()
   {
	   return loadByTpye();
   }
   
   public List<RepairLink> loadAll()
   {
	   return loadByTpye();
   }

   private List<RepairLink> loadByTpye()
   {
	   List list = new ArrayList();
	   
	   String sql = "select * from  topo_repair_link order by id";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
		     list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("RepairLinkDao.loadAll()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public int linkExist(String startIp,String startIndex,String endIp,String endIndex)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from topo_repair_link where start_ip='" + startIp + "' and end_ip='" + endIp 
		       + "' and start_index='" + startIndex + "' and end_index='" + endIndex + "'";
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return 1;        		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("RepairLinkDao.linkExist()",e); 
	   }
	   return 0;	      
   }
   
   public boolean deleteByHostIp(String hostip)
   {	
 	   String sql = "delete from topo_repair_link where start_ip='"+hostip+"' or end_ip='"+hostip+"'";
 	   return saveOrUpdate(sql);			
   }
   
   public RepairLink loadLink(String startIp,String startIndex,String endIp,String endIndex)
   {	   
	   String sql = null;
	   List list = new ArrayList();
	   try
	   {
		   sql = "select * from topo_repair_link where start_ip='" + startIp + "' and end_ip='" + endIp 
		       + "' and start_index='" + startIndex + "' and end_index='" + endIndex + "'";
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			     list.add(loadFromRS(rs));       		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("RepairLinkDao.loadLink()",e); 
	   }
	   if(list != null && list.size()>0){
		   return (RepairLink)list.get(0);
	   }else{
		   return null;
	   }      
   }
   
   public RepairLink loadRepairLink(String startIp,String new_startIndex,String endIp,String new_endIndex)
   {	   
	   String sql = null;
	   List list = new ArrayList();
	   try
	   {
		   sql = "select * from topo_repair_link where start_ip='" + startIp + "' and end_ip='" + endIp 
		       + "' and new_start_index='" + new_startIndex + "' and new_end_index='" + new_endIndex + "'";
		   //SysLogger.info(sql);
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			     list.add(loadFromRS(rs));       		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("RepairLinkDao.linkExist()",e); 
	   }
	   if(list != null && list.size()>0){
		   SysLogger.info("运行 已经被修改过,返回修改过的连接");
		   return (RepairLink)list.get(0);
	   }else{
		   return null;
	   }	      
   }

   public int linkExist(String startIp,String endIp)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from topo_repair_link where start_ip='" + startIp + "' and end_ip='" + endIp+"'";		   
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return 1;          		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("RepairLinkDao.linkExist()",e); 
	   }
	   return 0;	      
   }
   
   public boolean save(BaseVo baseVo)
   {
	   return false;
   }
   
   public RepairLink save(RepairLink vo)
   {
	   
	   int id = getNextID();	   
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_repair_link(id,start_index,start_ip,");
	   sql.append("end_index,end_ip,new_start_index,new_end_index)values(");
	   sql.append(id);;
	   sql.append(",'");
	   sql.append(vo.getStartIndex());
	   sql.append("','");
	   sql.append(vo.getStartIp());
	   sql.append("','");
	   sql.append(vo.getEndIndex());
	   sql.append("','");
	   sql.append(vo.getEndIp());
	   sql.append("','");
	   sql.append(vo.getNewStartIndex());
	   sql.append("','");	   
	   sql.append(vo.getNewEndIndex());
	   sql.append("')");
	   System.out.println(sql.toString());
	   if(saveOrUpdate(sql.toString()))
	   {
		   vo.setId(id);
		   //vo.setAssistant(assistant);
	   }		  
	   return vo; 
   }
   
   public boolean update(RepairLink vo)
   {   
	   //int id = getNextID();	   
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update topo_repair_link set ");
	   sql.append(" start_index='"+vo.getStartIndex()+"'");
	   sql.append(", start_ip='"+vo.getStartIp()+"'");
	   sql.append(", end_index='"+vo.getEndIndex()+"'");
	   sql.append(", end_ip='"+vo.getEndIp()+"'");
	   sql.append(", new_start_index='"+vo.getNewStartIndex()+"'");
	   sql.append(", new_end_index='"+vo.getNewEndIndex()+"'");
	   sql.append(" where id = "+vo.getId());
	   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean updatestartlinkip(String oldip,String newip)
   {   
	   //int id = getNextID();	   
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update topo_repair_link set ");
	   sql.append("start_ip='"+newip+"'");
	   sql.append(" where start_ip = '"+oldip+"'");   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean updateendlinkip(String oldip,String newip)
   {   
	   //int id = getNextID();	   
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update topo_repair_link set ");
	   sql.append("end_ip='"+newip+"'");
	   sql.append(" where start_ip = '"+oldip+"'");   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from topo_repair_link where id=" + id);
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("RepairLinkDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   RepairLink vo = new RepairLink();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setStartIndex(rs.getString("start_index"));
		   vo.setEndIndex(rs.getString("end_index"));
		   vo.setStartIp(rs.getString("start_ip"));
		   vo.setEndIp(rs.getString("end_ip"));
		   vo.setNewStartIndex(rs.getString("new_start_index"));
		   vo.setNewEndIndex(rs.getString("new_end_index"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("RepairLinkDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
   public boolean update(BaseVo baseVo)
   {
	   return false;   
   }
}
