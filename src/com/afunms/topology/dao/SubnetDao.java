/**
 * <p>Description:operate table NMS_SUBNET</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.Subnet;
import com.afunms.common.base.*;

public class SubnetDao extends BaseDao implements DaoInterface
{
   public SubnetDao()
   {
	   super("topo_subnet");	   	  
   }
    
   public List loadAll()
   {
	   return findByCriteria("select * from topo_subnet order by net_long");   
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Subnet vo = new Subnet();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setNetAddress(rs.getString("net_address"));
		   vo.setNetMask(rs.getString("net_mask"));
		   vo.setNetLong(rs.getLong("net_long"));
		   if(rs.getInt("managed")==1)
			  vo.setManaged(true);
		   else
			  vo.setManaged(false); 
	   }
       catch(Exception e)
       {
 	       SysLogger.error("SubnetDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
   public boolean delete(int netId,String[] nodeId)
   {	
	   boolean result = false;
	   try
	   {
           conn.executeUpdate("delete from topo_subnet where id=" + netId);
           
           HostNodeDao tnDao = new HostNodeDao();           
           result = tnDao.delete(nodeId);
	   }
       catch(Exception e)
       {
 	       SysLogger.error("SubnetDao.delete()",e); 
       }	
       finally
       {
    	   conn.close();
       }
       return result;
   }
   
   public boolean save(BaseVo vo)
   {
	   return false;
   }
   
   public boolean update(BaseVo baseVo)
   {	
	  Subnet vo = (Subnet)baseVo;
	  boolean result = false;
	  try
	  {
		  int managed = 0;
		  if(vo.isManaged()) managed = 1;
		  
		  conn.addBatch("update topo_subnet set managed=" + managed + " where id=" + vo.getId());
		  conn.addBatch("update topo_host_node set managed=" + managed + " where local_net=" + vo.getId());
		  conn.executeBatch();
		  result = true;
	  }
	  catch(Exception e)
	  {
		  result = false;
		  SysLogger.error("SubnetDao.update()",e); 
	  }
	  finally
	  {
		  conn.close();
	  }			
	  return result;			
   }
}