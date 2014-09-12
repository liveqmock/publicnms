package com.afunms.system.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Procs;
import com.afunms.system.vo.PortTypeVo;

public class PortTypeDao extends BaseDao implements DaoInterface
{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public PortTypeDao()
  {
	  super("nms_porttype");	  
  }
  
  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_porttype order by id");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("porttypeDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }
  
  //-------------loadByNodeAndEtype --------------
  public List loadByIp(String ip)
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_porttype where ipaddress='"+ip+"' order by id");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("KeyfileDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }
  
  public List loadByIpAndName(String ip,String name)
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_keyfile where ipaddress='"+ip+"' and filename = '"+name+"' order by id");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("KeyfileDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }

	public boolean save(BaseVo basevo)
	{
		PortTypeVo vo = (PortTypeVo)basevo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_porttype(typeid,chname,bak)values(");
		sql.append(vo.getTypeid());
		sql.append(",'");
		sql.append(vo.getChname());	
		sql.append("','");
		sql.append(vo.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a nodetobusiness----------------
  public boolean update(BaseVo basevo)
  {
	  PortTypeVo vo = (PortTypeVo)basevo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update nms_porttype set typeid=");
     sql.append(vo.getTypeid());
     sql.append(",chname='");
     sql.append(vo.getChname());
     sql.append("',bak='");
     sql.append(vo.getBak());
     sql.append("' where id=");
     sql.append(vo.getId());
     
     try
     {
    	 SysLogger.info(sql.toString());
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("porttypeDao:update()",e);
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
	            conn.addBatch("delete from nms_porttype where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("porttypeDao.delete()",e);	        
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
        rs = conn.executeQuery("select * from nms_porttype where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("porttypeDao.findByID()",e);
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
	   PortTypeVo vo = new PortTypeVo();
      try
      {
          vo.setId(rs.getInt("id"));
          vo.setTypeid(rs.getInt("typeid"));
          vo.setChname(rs.getString("chname"));    
          vo.setBak(rs.getString("bak"));
      }
      catch(Exception e)
      {
          SysLogger.error("porttypeDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }  
}

