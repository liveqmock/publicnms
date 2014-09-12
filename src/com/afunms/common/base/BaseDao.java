/**
 * <p>Description:father of all Dao classes</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;

public abstract class BaseDao
{
   protected String table;
   protected DBManager conn;
   protected ResultSet rs;
   protected JspPage jspPage;
      
   public BaseDao() {
	   conn = new DBManager();
   }
   
   public BaseDao(String table)
   {
       this.table = table; 
	   conn = new DBManager();
   }

   public BaseDao(String jndi,String table)
   {
       this.table = table; 
       try
       {
	       conn = new DBManager(jndi);
       }
       catch(Exception e)
       {
    	   conn = null;
    	   System.out.println("Error in BaseDao,Can not connect DB,jndi=" + jndi); 
       }
   }
   
   public void close()
   {
	   if(rs != null){
		   try{
			   rs.close();
		   }catch(Exception e){
		   }
	   }
	   if(conn != null){  
		   conn.close();
	   }
       rs = null;
       conn = null;
   }
   
   public JspPage getPage()
   {
       return jspPage;
   }  

   /**
    * 无条件分页
    */
   public List listByPage(int curpage,int perpage)
   {	
	   return listByPage(curpage,"",perpage);	   
   }
   
   /**
    * 有条件分页显示
    */
   public List listByPage(int curpage,String where,int perpage)
   {
	   List list = new ArrayList();	   
	   try 
	   {	
		   rs = conn.executeQuery("select count(*) from " + table + " " + where);
		   if(rs.next())
			   jspPage = new JspPage(perpage,curpage,rs.getInt(1));

		   rs = conn.executeQuery("select * from " + table + " " + where );
//		   SysLogger.info("--------------------->select * from " + table + " " + where );
		   int loop = 0;
		   while(rs.next())
		   {
			  loop++;
			  if(loop<jspPage.getMinNum()) continue;
			  list.add(loadFromRS(rs));
			  if(loop==jspPage.getMaxNum()) break;
		   }
	   } 
	   catch (Exception e) 
	   {
		   SysLogger.error("BaseDao.listByPage()",e);
		   list = null;
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;
   }

	public List listByPage(int curpage, List list, int perpage) {
	    List rsList = new ArrayList();
		jspPage = new JspPage(perpage, curpage, list.size());
		int loop = 0;
		// while(rs.next())
		// {
		// loop++;
		// if(loop<jspPage.getMinNum()) continue;
		// list.add(loadFromRS(rs));
		// if(loop==jspPage.getMaxNum()) break;
		// }
		for (Object object : list) {
			loop++;
			if (loop < jspPage.getMinNum())
				continue;
			rsList.add(list.get(loop-1));
			if (loop == jspPage.getMaxNum())
				break;
		}
		return rsList;
	}     
	
   /**
    * 删除一批记录
    */
   public boolean delete(String[] id)
   {
	   boolean result = false;
	   try
	   {
	       for(int i=0;i<id.length;i++)
	           conn.addBatch("delete from " + table + " where id=" + id[i]);
	       conn.executeBatch();
	       result = true;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("BaseDao.delete()",ex);
	       result = false;
	   }
	   return result;
   }
   
   /**
    * 得到下一个主键
    */
   protected synchronized int getNextID()
   {
	   int id = 0;
	   try
	   {
	       rs = conn.executeQuery("select max(id) from " + table);
	       if (rs.next())
	          id = rs.getInt(1) + 1;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("BaseDao.getNextID()",ex);
	       id = 0;
	   }finally{
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
	   }
	   return id;
   }

   /**
    * 得到下一个主键
    */
   protected synchronized int getNextID(String otherTable)
   {
	   int id = 0;
	   try
	   {
	       rs = conn.executeQuery("select max(id) from " + otherTable);
	       if (rs.next())
	          id = rs.getInt(1) + 1;
	   }
	   catch(Exception ex)
	   {
	       SysLogger.error("BaseDao.getNextID()",ex);
	       id = 0;
	   }finally{
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
	   }
	   return id;
   }
   
   /**
    * 按ID找一条记录
    */
   public BaseVo findByID(String id)
   {
	   BaseVo vo = null;
       try
	   {
//    	   SysLogger.info("findByID----------------->"+"select * from " + table + " where id=" + id);
		   rs = conn.executeQuery("select * from " + table + " where id=" + id); 
		   if(rs.next())
		       vo = loadFromRS(rs);
	   }    
	   catch(Exception ex)
	   {
		   //ex.printStackTrace();
		   SysLogger.error("BaseDao.findByID()",ex);
	   }finally{
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
	   }
       return vo;
   }

   /**
    * 载入所有记录
    */
   public List loadAll()
   {
	   List list = new ArrayList();
	   try 
	   {
//		   System.out.println("BaseDao.java--251行------>"+"select * from " + table + " order by id");
		   rs = conn.executeQuery("select * from " + table + " order by id");
		   if(rs == null)return null;
		   while(rs.next()){
			  list.add(loadFromRS(rs));				
		   }
	   }   
	   catch(Exception e) 
	   {
		   e.printStackTrace();
           list = null;
		   SysLogger.error("BaseDao.loadAll()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   
   public List loadAll(String where)
   {
	   List list = new ArrayList();
	   try 
	   {
//		   System.out.println("BaseDao.java--283行------>"+"select * from " + table +"  "+where+ "  order by id");
		   rs = conn.executeQuery("select * from " + table +"  "+where+ "  order by id");
		   if(rs == null)return null;
		   while(rs.next()){
			  list.add(loadFromRS(rs));				
		   }
	   }   
	   catch(Exception e) 
	   {
		   e.printStackTrace();
           list = null;
		   SysLogger.error("BaseDao.loadAll()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   
   /**
    * 按权限载入所有记录
    */
   public List loadByPerAll(String bid)
   {
        List list = new ArrayList();
	    StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (bid != null){
			if(bid !="-1"){
				String[] bids = bid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
	   try 
	   {
		   rs = conn.executeQuery("select * from " + table + " where 1=1 "+s+" order by id");
		   if(rs == null)return null;
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
		   e.printStackTrace();
           list = null;
		   SysLogger.error("BaseDao.loadByPerAll()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }

   /**
    * 载入所有记录(按IP地址排序)
    */
   public List loadOrderByIP()
   {
	   List list = new ArrayList();
	   try 
	   {
		   rs = conn.executeQuery("select * from " + table + " order by ip_long");
		   //SysLogger.info("select * from " + table + " order by ip_long");
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
           list = null;
		   SysLogger.error("BaseDao.loadAll()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   public List findByCriteria(String sql)
   {
	   List list = new ArrayList();
	   try 
	   {
		   rs = conn.executeQuery(sql);
		   if(rs == null)return null;
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
           list = null;
           e.printStackTrace();
		   SysLogger.error("BaseDao.findByCondition()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   public List findByCriteria(List sqllist)
   {
	   List list = new ArrayList();
	   try 
	   {
		   for(int i=0;i<sqllist.size();i++){
			   try {
				   String sql = (String) sqllist.get(i);
				   rs = conn.executeQuery(sql);
				   if(rs == null)continue;
				   while(rs.next()){
					   list.add(loadFromRS(rs));	
				   }
			   } catch (SQLException e) {
				   e.printStackTrace();
			   }
		   }
		   			
	   } 
	   catch(Exception e) 
	   {
           list = null;
           e.printStackTrace();
		   SysLogger.error("BaseDao.findByCriteria(List sqllist)",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   /**
    * 用于增加和修改
    */
   public boolean saveOrUpdate(String sql)
   {
      boolean result = false;	   
	  try
	  {
	      conn.executeUpdate(sql);
		  result = true;
	  }
	  catch(Exception e)
	  {		
		  e.printStackTrace();
		  result = false;
		  SysLogger.error("BaseDao.saveOrUpdate()",e); 
      }
	  finally
	  {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		  conn.close();
      }	
	  return result;
   }
   
   /**
    * 
    * 根据 where 来获取 列表
    * @author nielin
    * @date 2010-08-16
    * @param condition
    * @return
    */
   public List findByCondition(String condition){
	   List list = new ArrayList();
	   try 
	   {
		   rs = conn.executeQuery("select * from " + table + condition);
		 //  SysLogger.info("---BaseDao--469---------::select * from " + table + condition);
		   if(rs == null)return null;
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	   } 
	   catch(Exception e) 
	   {
           list = null;
           e.printStackTrace();
		   SysLogger.error("BaseDao.findByCondition()",e);
	   }
	   finally
	   {
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
		   conn.close();
	   }
	   return list;	
   }
   
   
   /**
    * 根据 where 获取到告警数
    * @author nielin
    * @date 2010-08-05
    * @param where
    * @return
    */
   public String getCountByWhere(String where){
   	try {
   		String sql = "select count(*) as cnt from " + table + where;
   		rs = conn.executeQuery(sql);
   		if(rs.next()){
			   return rs.getString("cnt");
   		}
   	} catch (SQLException e) {
   		// TODO Auto-generated catch block
   		e.printStackTrace();
   	}finally{
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
			   }
		   }
   	}
   	return "0";
   }
   /**
    * 把一条记录载入VO
    */
   public abstract BaseVo loadFromRS(ResultSet rs);
}
