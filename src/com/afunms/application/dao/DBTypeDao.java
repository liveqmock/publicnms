/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;

import com.afunms.application.model.DBTypeVo;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.system.model.User;

public class DBTypeDao extends BaseDao implements DaoInterface
{
   public DBTypeDao()
   {
	   super("app_dbtype");
   }

   public boolean update(BaseVo baseVo)
   {
	   DBTypeVo vo = (DBTypeVo)baseVo;	
	   StringBuffer sql = new StringBuffer();
	   sql.append("update app_dbtype set dbtype='");
	   sql.append(vo.getDbtype());
	   sql.append("',dbdesc='");
	   sql.append(vo.getDbdesc());	   
	   sql.append("' where id=");
	   sql.append(vo.getId());
	   
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean save(BaseVo baseVo)
   {
	   DBTypeVo vo = (DBTypeVo)baseVo;	   
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into app_dbtype(id,dbtype,dbdesc)values(");
	   sql.append(vo.getId());
	   sql.append(",'");
	   sql.append(vo.getDbtype());
	   sql.append("','");
	   sql.append(vo.getDbdesc());   
	   sql.append("')");
	   
	   return saveOrUpdate(sql.toString());
   }
   
   public DBTypeVo findByDbtype(String dbtype)
   {
	   DBTypeVo vo = null;
      try
      {
//    	  System.out.println("--------------==============="+dbtype);
          rs = conn.executeQuery("select * from app_dbtype where dbtype='" + dbtype + "'");
          SysLogger.info("select * from app_dbtype where dbtype='" + dbtype + "'");
          if(rs.next())
             vo = (DBTypeVo)loadFromRS(rs);
      }
      catch(Exception e)
      {
          SysLogger.error("DBTypeDao.findByDbtype",e);
          vo = null;
      }
      finally
      {
          //conn.close();
      }
      return vo;
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from app_dbtype where id=" + id);
		   conn.executeBatch();
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("DBTypeDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   DBTypeVo vo = new DBTypeVo();
       try
       {
		   vo.setId(rs.getInt("id"));
		   vo.setDbtype(rs.getString("dbtype"));
		   vo.setDbdesc(rs.getString("dbdesc"));		   
       }
       catch(Exception e)
       {
   	       SysLogger.error("DBTypeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
 //zhushouzhi-----------------------------
   public int finddbcountbyip(String ip)
   {
	   int count = 0;
	   try
	     {
	        rs = conn.executeQuery("select count(content) from system_eventlist where content like '%"+ip+"%' and content like '%"+"¿Õ¼ä"+"%'");
	        if(rs.next())
	        {
	        	count = rs.getInt(1);
	        }
	     
	     }
	   catch(Exception e)
	     {
	         SysLogger.error("EventListDao.finddbcountByIp()",e);
	        
	     }
	     finally
	     {
	        conn.close();
	     }
	     return count;
   }

}   