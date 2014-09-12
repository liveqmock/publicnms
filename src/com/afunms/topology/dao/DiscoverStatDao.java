/**
 * <p>Description:operate table NMS_DISCOVER_STAT</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;

import com.afunms.common.base.*;
import com.afunms.topology.model.DiscoverStat;

public class DiscoverStatDao extends BaseDao implements DaoInterface
{
   public DiscoverStatDao()
   {
	   super("topo_discover_stat");	   	  
   }
   
   public boolean update(BaseVo baseVo)
   {
	   return false;   
   }
      
   public boolean save(BaseVo baseVo)
   {
	   DiscoverStat vo = (DiscoverStat)baseVo;		
       StringBuffer sql = new StringBuffer(200);
       sql.append("insert into topo_discover_stat(id,start_time,end_time,elapse_time,host_total,subnet_total)values(");
       sql.append(getNextID());
       sql.append(",'");
       sql.append(vo.getStartTime());
       sql.append("','");
       sql.append(vo.getEndTime());
       sql.append("','");
       sql.append(vo.getElapseTime());
       sql.append("',");
       sql.append(vo.getHostTotal());
       sql.append(",");
       sql.append(vo.getSubnetTotal());
       sql.append(")");	        
       return saveOrUpdate(sql.toString());
   }
     
   public int getNodeID()
   {
	   int id = 0;
	   try
	   {
	      rs = conn.executeQuery("select max(id) from topo_node_id");
	      if(rs.next())
		     id = rs.getInt(1);
	   }
	   catch(Exception e)
	   {		   
	   }
	   return id;
   }

   public int getSubnetID()
   {
	   int id = 0;
	   try
	   {
	      rs = conn.executeQuery("select max(id) from topo_subnet");
	      if(rs.next())
		     id = rs.getInt(1) + 1;
	   }
	   catch(Exception e)
	   {		   
	   }
	   return id;	   
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
      return null;
   }	   
}
