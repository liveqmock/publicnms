/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;

public class NodeToBusinessDao extends BaseDao implements DaoInterface
{
  public NodeToBusinessDao()
  {
	  super("system_nodetobusiness");	  
  }
  
  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from system_nodetobusiness order by id");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("NodeToBusinessDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }
  
  //-------------loadByNodeAndEtype --------------
  public List loadByNodeAndEtype(int nodeid,String eletype)
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from system_nodetobusiness where nodeid="+nodeid+" and elementtype='"+eletype+"' order by id");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("NodeToBusinessDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }

	public boolean save(BaseVo baseVo)
	{
		NodeToBusiness vo = (NodeToBusiness)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_nodetobusiness(elementtype,nodeid,businessid)values(");
		sql.append("'");
		sql.append(vo.getElementtype());
		sql.append("',");
		sql.append(vo.getNodeid());
		sql.append(",");
		sql.append(vo.getBusinessid());		
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a nodetobusiness----------------
  public boolean update(BaseVo baseVo)
  {
	  NodeToBusiness vo = (NodeToBusiness)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update system_nodetobusiness set elementtype=");
     sql.append(vo.getElementtype());
     sql.append(",nodeid=");
     sql.append(vo.getNodeid());
     sql.append(",businessid=");
     sql.append(vo.getBusinessid());
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
         SysLogger.error("NodeToBusinessDao:update()",e);
     }
     finally
     {
    	 conn.close();
     }     
     return result;
  }
  
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from system_nodetobusiness where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("NodeToBusinessDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
	
	public boolean deleteallbyNE(int nodeid,String eletype)
	{
		boolean result = false;
	    try
	    {	    
	        conn.addBatch("delete from system_nodetobusiness where nodeid=" + nodeid+" and elementtype='"+eletype+"'");	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("NodeToBusinessDao.deleteall()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
	
	public boolean deleteall()
	{
		boolean result = false;
	    try
	    {	    
	        conn.addBatch("delete from system_nodetobusiness");	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("NodeToBusinessDao.deleteall()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from system_nodetobusiness where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("NodeToBusinessDao.findByID()",e);
         vo = null;
     }
     finally
     {
        conn.close();
     }
     return vo;
  }
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NodeToBusiness vo = new NodeToBusiness();
      try
      {
          vo.setId(rs.getInt("id"));
          vo.setElementtype(rs.getString("elementtype"));
          vo.setNodeid(rs.getInt("nodeid"));
          vo.setBusinessid(rs.getInt("businessid"));                     
      }
      catch(Exception e)
      {
          SysLogger.error("NodeToBusinessDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }  
}
