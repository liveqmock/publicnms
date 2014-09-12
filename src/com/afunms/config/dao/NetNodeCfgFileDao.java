/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.sql.ResultSet;


import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.NetNodeCfgFile;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;
import com.afunms.common.util.ShareData;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.polling.om.Interfacecollectdata;

public class NetNodeCfgFileDao extends BaseDao implements DaoInterface
{
	public NetNodeCfgFileDao()
  	{
	  	super("nms_netnodecfgfile");	  
  	}
  
  	//-------------load all --------------
  	public List loadAll()
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from nms_netnodecfgfile order by id desc");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("NetNodeCfgFileDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			conn.close();
  		}
  		return list;
  	}
  	
    public NetNodeCfgFile loadNetNodeCfgFile(int id)
    {
 	   
 	   List retList = new ArrayList();
 	   List portconfigList = findByCriteria("select * from nms_netnodecfgfile where id="+id); 
 	   if(portconfigList != null && portconfigList.size()>0){
 		  NetNodeCfgFile cfg = (NetNodeCfgFile)portconfigList.get(0);
 			   return cfg;
 		   
 	   }
 	   return null;
    }
    
    public boolean deleteByHostIp(String hostip)
    {	
 	   String sql = "delete from nms_netnodecfgfile where ipaddress='"+hostip+"'";
 	   return saveOrUpdate(sql);			
    }

  	public List loadByIpaddress(String ip)
  	{
  		List list = new ArrayList(5);
  		try
  		{
  			rs = conn.executeQuery("select * from nms_netnodecfgfile where ipaddress='"+ip+"' order by id");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("NetNodeCfgFileDao:loadAll()",e);
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
		NetNodeCfgFile vo = (NetNodeCfgFile)baseVo;
		StringBuffer sql = new StringBuffer(100);
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = theDate.format(vo.getRecordtime().getTime());
		sql.append("insert into nms_netnodecfgfile(ipaddress,name,recordtime)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getName());	
		sql.append("','");
		sql.append(dateString);
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a portconfig----------------
  public boolean update(BaseVo baseVo)
  {
	  NetNodeCfgFile vo = (NetNodeCfgFile)baseVo;
     boolean result = false;
     SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 String dateString = theDate.format(vo.getRecordtime().getTime());
     StringBuffer sql = new StringBuffer();
     sql.append("update nms_netnodecfgfile set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',name='");
		sql.append(vo.getName());	
		sql.append("',recordtime='");
		sql.append(dateString);
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
         SysLogger.error("NetNodeCfgFileDao:update()",e);
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
	            conn.addBatch("delete from nms_netnodecfgfile where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("NetNodeCfgFileDao.delete()",e);	        
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
        rs = conn.executeQuery("select * from nms_netnodecfgfile where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("NetNodeCfgFileDao.findByID()",e);
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
	   NetNodeCfgFile vo = new NetNodeCfgFile();
      try
      {
    	  	Calendar tempCal = Calendar.getInstance();							
  			Date cc = new Date();
  			cc.setTime(rs.getTimestamp("recordtime").getTime());
  			tempCal.setTime(cc);
          vo.setId(rs.getLong("id"));
          vo.setIpaddress(rs.getString("ipaddress"));  
          vo.setName(rs.getString("name"));
          vo.setRecordtime(tempCal);
      }
      catch(Exception e)
      {
          SysLogger.error("PortconfigDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }  
}
