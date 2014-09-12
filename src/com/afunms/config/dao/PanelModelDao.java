/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.sql.ResultSet;


import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.PanelModel;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;
import com.afunms.common.util.ShareData;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.polling.om.Interfacecollectdata;

public class PanelModelDao extends BaseDao implements DaoInterface
{
	public PanelModelDao()
  	{
	  	super("system_panelmodel");	  
  	}
  
  	//-------------load all --------------
  	public List loadAll()
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_panelmodel order by oid");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PanelModelDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
  	
    public PanelModel loadPanelModel(int id)
    {
 	   
 	   List retList = new ArrayList();
 	   List modelList = findByCriteria("select * from system_panelmodel where id="+id); 
 	   if(modelList != null && modelList.size()>0){
 		  PanelModel model = (PanelModel)modelList.get(0);
 			   return model;
 		   
 	   }
 	   return null;
    }
    
    
    /**
     * get the list of panelmodel by oid
     * @author nielin 
     * modify 2010-01-08
     * @param oid <code>String</code>
     * @return
     */
    public List loadPanelModel(String oid)
    {
 	   
 	   List retList = new ArrayList();
 	   List modelList = findByCriteria("select * from system_panelmodel where oid='"+oid+"'"); 
 	   return modelList;
    }
    
    /**
     * get the panelMode by oid and imageType
     * @author nielin
     * add 200-01-13
     * @param oid  <code>String</code>
     * @param imageType  <code>String</code>
     * @return
     */
    public PanelModel loadPanelModel(String oid , String imageType)
    {
 	   List retList = new ArrayList();
 	   List modelList = findByCriteria("select * from system_panelmodel where oid='"+oid+"' and imageType=" + imageType); 
 	   if(modelList != null && modelList.size()>0){
 		  PanelModel model = (PanelModel)modelList.get(0);
 			   return model;
 		   
 	   }
 	   return null;
    }
    /*
    public PanelModel loadPanelModel(String oid)
    {
 	   
 	   List retList = new ArrayList();
 	   List modelList = findByCriteria("select * from system_panelmodel where oid='"+oid+"'"); 
 	   if(modelList != null && modelList.size()>0){
 		  PanelModel model = (PanelModel)modelList.get(0);
 			   return model;
 		   
 	   }
 	   return null;
    }
    */
   
  	
  	public void empty()
  	{
  		try
  		{
  			conn.executeUpdate("delete from system_panelmodel ");
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PanelModelDao:empty()",e);
  		}
  		finally
  		{
  			conn.close();
  		}
  	}
	
	public boolean save(BaseVo baseVo)
	{
		PanelModel vo = (PanelModel)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_panelmodel(oid,imagetype,width,height)values(");
		sql.append("'");
		sql.append(vo.getOid());
		sql.append("','");
		sql.append(vo.getImageType());   // nielin add 2010-01-07
		sql.append("','");
		sql.append(vo.getWidth());	
		sql.append("','");
		sql.append(vo.getHeight());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a portconfig----------------
  public boolean update(BaseVo baseVo)
  {
	  PanelModel vo = (PanelModel)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update system_panelmodel set oid='");
		sql.append(vo.getOid());
		sql.append("',imagetype='");    // nielin add 2010-01-14
		sql.append(vo.getImageType());	
		sql.append("',width='");
		sql.append(vo.getWidth());	
		sql.append("',height='");
		sql.append(vo.getHeight());
     sql.append("' where id=");
     sql.append(vo.getId());
     try
     {
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("PanelModelDao:update()",e);
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
	            conn.addBatch("delete from system_panelmodel where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("PanelModelDao.delete()",e);	        
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
        rs = conn.executeQuery("select * from system_panelmodel where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("PanelModelDao.findByID()",e);
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
	   PanelModel vo = new PanelModel();
      try
      {
          vo.setId(rs.getInt("id"));
          vo.setOid(rs.getString("oid"));
          vo.setImageType(rs.getString("imageType"));   // nielin add
          vo.setWidth(rs.getString("width"));  
          vo.setHeight(rs.getString("height"));
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
          vo = null;
      }
      return vo;
   } 
   
}
