/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Errptconfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class ErrptconfigDao extends BaseDao implements DaoInterface
{
	public ErrptconfigDao()
  	{
	  	super("nms_errpt_config");	  
  	}
  
  	//-------------load all --------------
  	public List loadAll()
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from nms_errpt_config order by id");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("ErrptconfigDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
  	
    public Errptconfig loadErrptconfig(int id)
    {
 	   
 	   List retList = new ArrayList();
 	   List errptconfigList = findByCriteria("select * from nms_errpt_config where id="+id); 
 	   if(errptconfigList != null && errptconfigList.size()>0){
 		  Errptconfig errptconfig = (Errptconfig)errptconfigList.get(0);
 			   return errptconfig;	   
 	   }
 	   return null;
    }
    
    public Errptconfig loadErrptconfigByNodeid(int id)
    {
 	   
 	   List retList = new ArrayList();
 	   List errptconfigList = findByCriteria("select * from nms_errpt_config where nodeid="+id); 
 	   if(errptconfigList != null && errptconfigList.size()>0){
 		  Errptconfig errptconfig = (Errptconfig)errptconfigList.get(0);
 			   return errptconfig;	   
 	   }
 	   return null;
    }
  	
  	public void empty()
  	{
  		try
  		{
  			conn.executeUpdate("delete from nms_errpt_config ");
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("ErrptconfigDao:empty()",e);
  		}
  		finally
  		{
  			conn.close();
  		}
  	}
	
	
	
		
	
	public boolean save(BaseVo baseVo)
	{
		Errptconfig vo = (Errptconfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_errpt_config(nodeid,errpttype,errptclass,alarmwayid)values(");
		sql.append(vo.getNodeid());
		sql.append(",'");
		sql.append(vo.getErrpttype());	
		sql.append("','");
		sql.append(vo.getErrptclass());
		sql.append("','");
		sql.append(vo.getAlarmwayid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a portconfig----------------
  public boolean update(BaseVo baseVo)
  {
	  Errptconfig vo = (Errptconfig)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update nms_errpt_config set nodeid='");
		sql.append(vo.getNodeid());
		sql.append("',errpttype='");
		sql.append(vo.getErrpttype());	
		sql.append("',errptclass='");
		sql.append(vo.getErrptclass());	
		sql.append("',alarmwayid='");
		sql.append(vo.getAlarmwayid());
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
         SysLogger.error("ErrptconfigDao:update()",e);
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
	            conn.addBatch("delete from nms_diskconfig where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("ErrptconfigDao.delete()",e);	        
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
        rs = conn.executeQuery("select * from nms_diskconfig where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("DiskconfigDao.findByID()",e);
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
	   Errptconfig vo = new Errptconfig();
      try
      {
          vo.setId(rs.getInt("id"));
          vo.setNodeid(rs.getInt("nodeid"));
          vo.setErrptclass(rs.getString("errptclass"));  
          vo.setErrpttype(rs.getString("errpttype"));
          vo.setAlarmwayid(rs.getString("alarmwayid"));
      }
      catch(Exception e)
      {
          //SysLogger.error("PortconfigDao.loadFromRS()",e);
    	  e.printStackTrace();
          vo = null;
      }
      return vo;
   }  
}
