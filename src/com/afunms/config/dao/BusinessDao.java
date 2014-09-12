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
import com.afunms.config.model.Business;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;
import com.afunms.system.model.User;

public class BusinessDao extends BaseDao implements DaoInterface
{
  public BusinessDao()
  {
	  super("system_business");	  
  }
  
  //-------------load all top menus--------------
  public List loadAll()
  {
     List list = new ArrayList(5);
     try
     {
         rs = conn.executeQuery("select * from system_business order by id");
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
  public Business loadBidbyID(String id)
  {
     Business vo = null;
     try
     {
    	 if(id!=null||id!=""){
         rs = conn.executeQuery("select * from system_business where id ="+id);
    	 }
         while(rs.next())
        	  vo = (Business)loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("BusinessDao:loadAll()",e);
         vo = null;
     }
     finally
     {
         conn.close();
     }
     return vo;
  }
	public boolean save(BaseVo baseVo)
	{
		Business vo = (Business)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_business(name,descr,pid)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDescr());	
		sql.append("','");
		sql.append(vo.getPid());	
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {
	  Business vo = (Business)baseVo;
     boolean result = false;
     StringBuffer sql = new StringBuffer();
     sql.append("update system_business set name='");
     sql.append(vo.getName());
     sql.append("',descr='");
     sql.append(vo.getDescr());
     sql.append("',pid='");
     sql.append(vo.getPid());
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
	            conn.addBatch("delete from system_business where id=" + id[i]);
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
	
	
	public boolean deleteVoAndChildVoById(String id)
	{
		boolean result = false;
		try {
			String sql = "delete from system_business where id='" + id +"' or pid='" + id + "'";
			System.out.println(sql);
			conn.executeUpdate(sql);
			
			result = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}finally{
			conn.close();
		}
	    return result;
	}
  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from system_business where id=" + id );
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
  public List findByIDs(String IDs)
  {
	  List list = new ArrayList();
	  try{
		  rs = conn.executeQuery("select * from system_business where id in(" + IDs +")");
		  if(rs!=null){
			  while(rs.next()){
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return list;
  }
  /**
   * 根据id递归查询所有节点，包括父节点
   * @author 
   * @param IDs
   * @return
   */
  public List queryRecursionIDs(String IDs){
	  List list = new ArrayList();
	  try{
		 // System.out.println("=========sqll========sselect distinct * from system_business " +"connect by id = prior pid start with id in (" + IDs +") order by id ");
		 /* rs = conn.executeQuery("select distinct * from system_business " +
		  		"connect by id = prior pid start with id in (" + IDs +") order by id ");*/
		  
		  rs = conn.executeQuery("select distinct * from system_business order by id ");
		  if(rs!=null){
			  while(rs.next()){
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return list;
  }
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   Business vo = new Business();
      try
      {
          vo.setId(rs.getString("id"));
          vo.setName(rs.getString("name"));
          vo.setDescr(rs.getString("descr")); 
          vo.setPid(rs.getString("pid"));      
      }
      catch(Exception e)
      {
          SysLogger.error("BusinessDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   }
   public BaseVo findBySuperID(String id)
   {
      return super.findByID(id);
   }
   /**
    * 
    * @description 暂且支持四层节点
    * @author wangxiangyong
    * @date Feb 18, 2013 6:02:47 PM
    * @return List  
    * @param bids
    * @return
    */
   public List loadRoleBusiness(String bids)
   {
      List list = new ArrayList();
      try
      {
     	 StringBuffer sql=new StringBuffer();
     	 if(bids!=null){
     		 bids=bids.replaceAll(",,", ",");
     		 if(bids.length()>1){
     		 bids=bids.substring(1,bids.length()-1);
     		 sql.append("select * from system_business ");
     		 sql.append("where pid in(0) or id in(");
     		 sql.append(bids);
     		 sql.append(") or id in(select pid from system_business where id in(");
     		 sql.append(bids).append("))");
     		 sql.append(" or id in(select a.pid from system_business a where a.id in(select b.pid from system_business b where b.id in(");
     		 sql.append(bids).append(")))");
     		 sql.append(" order by id");
     		
     		 rs = conn.executeQuery(sql.toString());
              while(rs.next())
             	list.add(loadFromRS(rs));
     	 }
     	 }
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
}
