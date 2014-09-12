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
import com.afunms.config.model.TrapOIDConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;

public class TrapOIDConfigDao extends BaseDao implements DaoInterface
{
  public TrapOIDConfigDao()
  {
	  super("nms_trapoid");	  
  }
  
  //-------------load all top menus--------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_trapoid order by id");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("BusinessDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }
  
  public List loadByEnterpriseOID(String enterpriseoid)
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from nms_trapoid where enterpriseoid='"+enterpriseoid+"' order by orders");
         while(rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("TrapOIDConfigDao:loadByEnterpriseOID()",e);
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
		TrapOIDConfig vo = (TrapOIDConfig)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_trapoid(name,descr)values(");
		/*
		sql.append("'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDescr());		
		sql.append("')");
		*/
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {
	  TrapOIDConfig vo = (TrapOIDConfig)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     /*
     sql.append("update nms_trapoid set name='");
     sql.append(vo.getName());
     sql.append("',descr='");
     sql.append(vo.getDescr());
     sql.append("' where id=");
     sql.append(vo.getId());
     */
     try
     {
         conn.executeUpdate(sql.toString());
         result = true;
     }
     catch(Exception e)
     {
    	 result = false;
         SysLogger.error("BusinessDao:update()",e);
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
	            conn.addBatch("delete from nms_trapoid where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("BusinessDao.delete()",e);	        
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
        rs = conn.executeQuery("select * from nms_trapoid where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("BusinessDao.findByID()",e);
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
	   TrapOIDConfig vo = new TrapOIDConfig();
      try
      {
          vo.setId(rs.getString("id"));
          vo.setEnterpriseoid(rs.getString("enterpriseoid"));
          vo.setOrders(rs.getInt("orders"));
          vo.setOid(rs.getString("oid"));
          vo.setDesc(rs.getString("descr"));   
          vo.setValue1(rs.getString("value1"));
          vo.setValue2(rs.getString("value2"));
          vo.setTransvalue1(rs.getString("transvalue1"));
          vo.setTransvalue2(rs.getString("transvalue2"));
          vo.setTransflag(rs.getInt("transflag"));
          vo.setCompareflag(rs.getInt("compareflag"));
          vo.setTraptype(rs.getString("traptype"));
          //SysLogger.info("traptype==="+vo.getTraptype());
      }
      catch(Exception e)
      {
          SysLogger.error("BusinessDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }  
}
