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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;
import com.afunms.common.util.ShareData;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.polling.om.Interfacecollectdata;

public class IpaddressPanelDao extends BaseDao implements DaoInterface
{
	public IpaddressPanelDao()
  	{
	  	super("system_ipaddresspanel");	  
  	}
  
  	//-------------load all --------------
  	public List loadAll()
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from system_ipaddresspanel order by id");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			e.printStackTrace();
  			SysLogger.error("IpaddressPanelDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
  	
  	public boolean deleteByHostIp(String hostip)
	  {	
		   String sql = "delete from nms_portscan_config where ipaddress='"+hostip+"'";
		   return saveOrUpdate(sql);			
	  }
  	
    public IpaddressPanel loadIpaddressPanel(int id)
    {
 	   
 	   List retList = new ArrayList();
 	   List modelList = findByCriteria("select * from system_ipaddresspanel where id="+id); 
 	   if(modelList != null && modelList.size()>0){
 		  IpaddressPanel model = (IpaddressPanel)modelList.get(0);
 			   return model;
 		   
 	   }
 	   return null;
    }
    
    public IpaddressPanel loadIpaddressPanel(String ipaddress)
    {
 	   
 	   List retList = new ArrayList();
 	   List modelList = findByCriteria("select * from system_ipaddresspanel where ipaddress='"+ipaddress+"'"); 
 	   if(modelList != null && modelList.size()>0){
 		  IpaddressPanel model = (IpaddressPanel)modelList.get(0);
 			   return model;
 		   
 	   }
 	   return null;
    }
  	
  	public void empty()
  	{
  		try
  		{
  			conn.executeUpdate("delete from system_ipaddresspanel ");
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
	/**
	 * @author nielin 
	 * modify at 2010-01-14
	 */
	public boolean save(BaseVo baseVo)
	{
		IpaddressPanel vo = (IpaddressPanel)baseVo;
		String sqldelete = "";
		sqldelete = "delete from system_ipaddresspanel where ipaddress='" + vo.getIpaddress() + "'";
		try {
			conn.executeUpdate(sqldelete);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_ipaddresspanel(ipaddress,status,imageType)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getStatus());	
		sql.append("','");
		sql.append(vo.getImageType());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	/**
	 * Save a list of IpaddressPanel into database
	 * @author nielin
	 * add at 2010-01-14
	 * @param list
	 * @return
	 */
	public boolean save(List list){
		try{
			for(int i = 0 ; i< list.size(); i++ ){
				IpaddressPanel vo = (IpaddressPanel)list.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into system_ipaddresspanel(ipaddress,status,imagetype) values (");
				sql.append("'");
				sql.append(vo.getIpaddress());
				sql.append("','");
				sql.append(vo.getStatus());	
				sql.append("','");
				sql.append(vo.getImageType());	
				sql.append("')");
				conn.executeUpdate(sql.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			conn.close();
		}
		return true;
	}
	
  //---------------update a portconfig----------------
  public boolean update(BaseVo baseVo)
  {
	  IpaddressPanel vo = (IpaddressPanel)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update system_ipaddresspanel set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',status='");
		sql.append(vo.getStatus());	
		sql.append("',imagetype='");      // nielin  add 2010-01-14
		sql.append(vo.getImageType());	
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
	            conn.addBatch("delete from system_ipaddresspanel where id=" + id[i]);
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
        rs = conn.executeQuery("select * from system_ipaddresspanel where id=" + id );
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
  
  public BaseVo findByIpaddress(String ipaddress)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from system_ipaddresspanel where ipaddress='" + ipaddress +"'");
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("PanelModelDao.findByIpaddress()",e);
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
	   IpaddressPanel vo = new IpaddressPanel();
      try
      {
          vo.setId(rs.getInt("id"));
          vo.setIpaddress(rs.getString("ipaddress"));
          vo.setStatus(rs.getString("status")); 
          vo.setImageType(rs.getString("imageType"));  // nielin add at 2010-01-14
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
          vo = null;
      }
      return vo;
   }  
}
