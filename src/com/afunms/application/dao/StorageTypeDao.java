/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.application.model.StorageTypeVo;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class StorageTypeDao extends BaseDao implements DaoInterface
{
   public StorageTypeDao()
   {
	   super("nms_storagetype");
   }

   public boolean update(BaseVo baseVo)
   {
	   StorageTypeVo vo = (StorageTypeVo)baseVo;	
	   StringBuffer sql = new StringBuffer();
	   sql.append("update nms_storagetype set producer=");
	   sql.append(vo.getProducer());
	   sql.append(",model='");
	   sql.append(vo.getModel());
	   sql.append("',descr='");
	   sql.append(vo.getDescr());
	   sql.append("' where id=");
	   sql.append(vo.getId());
	   
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean save(BaseVo baseVo)
   {
	   StorageTypeVo vo = (StorageTypeVo)baseVo;	   
	   StringBuffer sql = new StringBuffer();
	   sql.append("insert into nms_storagetype(id,producer,model,descr) values(");
	   sql.append(vo.getId());
	   sql.append(",");
	   sql.append(vo.getProducer());
	   sql.append(",'");
	   sql.append(vo.getModel());
	   sql.append("','");
	   sql.append(vo.getDescr());
	   sql.append("')");
	   
	   return saveOrUpdate(sql.toString());
   }
   
   public StorageTypeVo findByProducerid(int producerid)
   {
	   StorageTypeVo vo = null;
      try
      {
          rs = conn.executeQuery("select * from nms_storagetype where producer='" + producerid + "'");
          if(rs.next())
             vo = (StorageTypeVo)loadFromRS(rs);
      }
      catch(Exception e)
      {
          SysLogger.error("StorageTypeDao.findByProducerid",e);
          vo = null;
      }
      finally
      {
          //conn.close();
      }
      return vo;
   }
   public List getAllModel()
   {
	   List list = new ArrayList();
	   StorageTypeVo vo = null;
      try
      {
          rs = conn.executeQuery("select * from nms_storagetype ");
          if(rs.next()){
        	  vo = (StorageTypeVo)loadFromRS(rs);
        	  list.add(vo);
          } 
      }
      catch(Exception e)
      {
          SysLogger.error("StorageTypeDao.getAllModel",e);
      }
      finally
      {
      }
      return list;
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.addBatch("delete from nms_storagetype where id=" + id);
		   conn.executeBatch();
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("StorageTypeDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   StorageTypeVo vo = new StorageTypeVo();
       try
       {
		   vo.setId(rs.getInt("id"));
		   vo.setProducer(rs.getInt("producer"));
		   vo.setModel(rs.getString("model"));
		   vo.setDescr(rs.getString("descr"));		   
       }
       catch(Exception e)
       {
   	       SysLogger.error("StorageTypeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
 //zhushouzhi-----------------------------
   public int findstoragecountbyip(String ip)
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