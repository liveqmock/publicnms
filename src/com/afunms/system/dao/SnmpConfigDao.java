/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.BaseDao;
import com.afunms.system.model.Department;
import com.afunms.system.model.SnmpConfig;
import com.afunms.common.base.BaseVo;

public class SnmpConfigDao extends BaseDao
{
  public SnmpConfigDao()
  {
	  super("system_snmpconfig");	  
  }
  
  //-------------load all --------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from system_snmpconfig ");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("SnmpConfigDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }

  //---------------update a menu----------------
  public boolean update(SnmpConfig snmpconfig)
  {
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update system_snmpconfig set name='");
     sql.append(snmpconfig.getName());
     sql.append("',snmpversion=");
     sql.append(snmpconfig.getSnmpversion());
     sql.append(",readcommunity='");
     sql.append(snmpconfig.getReadcommunity());
     sql.append("',writecommunity='");
     sql.append(snmpconfig.getWritecommunity());
     sql.append("',timeout=");
     sql.append(snmpconfig.getTimeout());
     sql.append(",trytime='");
     sql.append(snmpconfig.getTrytime());
     sql.append("' where id=");
     sql.append(snmpconfig.getId());
     sql.append("");
     
     try
     {
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("SnmpConfigDao:update()",e);
     }
     finally
     {
    	 conn.close();
     }     
     return result;
  }
  
  //---------------update a menu----------------
  public boolean delete(String id)
  {
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("delete from system_snmpconfig where id="+id);
     try
     {
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("SnmpConfigDao:delete()",e);
     }
     finally
     {
    	 conn.close();
     }     
     return result;
  }
  
	public boolean save(BaseVo baseVo)
	{
		SnmpConfig vo = (SnmpConfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_snmpconfig(name,snmpversion,readcommunity,writecommunity,timeout,trytime)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("',");
		sql.append(vo.getSnmpversion());		
		sql.append(",'");
		sql.append(vo.getReadcommunity());
		sql.append("','");
		sql.append(vo.getWritecommunity());
		sql.append("',");
		sql.append(vo.getTimeout());
		sql.append(",");
		sql.append(vo.getTrytime());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}



  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from system_snmpconfig where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("SnmpConfigDao.findByID()",e);
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
	   SnmpConfig vo = new SnmpConfig();
      try
      {
          vo.setId(rs.getString("id"));
          vo.setName(rs.getString("name"));
          vo.setSnmpversion(rs.getInt("snmpversion"));
          vo.setReadcommunity(rs.getString("readcommunity"));
          vo.setWritecommunity(rs.getString("writecommunity"));
          vo.setTimeout(rs.getInt("timeout"));    
          vo.setTrytime(rs.getInt("trytime"));
      }
      catch(Exception e)
      {
          SysLogger.error("SnmpConfigDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }  
}
