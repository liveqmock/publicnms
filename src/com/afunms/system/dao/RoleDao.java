/**
 * <p>Description:operate table NMS_ROLE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.system.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

import com.afunms.common.util.SysLogger;
import com.afunms.system.model.Role;
import com.afunms.common.base.*;

public class RoleDao extends BaseDao implements DaoInterface
{
   public RoleDao()
   {
	   super("system_role");
   }

   /**
    * 增加一个角色,则在role_menu表中增加n条记录(n=菜单的总数)
    */
   public boolean save(BaseVo baseVo)
   {
       Role vo = (Role)baseVo;
       boolean result = false;
       try
       {         
    	  int role_id = getNextID();
    	  
    	  String sql = null;
          //在表system_role添加一条记录
          sql = "insert into system_role(id,role)values(" + role_id + ",'" + vo.getRole() + "')";
          conn.addBatch(sql);
          
          int id = 1;
          sql = "select max(id) from system_role_menu";
          rs = conn.executeQuery(sql);
          if (rs.next())
             id = rs.getInt(1) + 1;

          rs = conn.executeQuery("select a.*,b.operate from system_menu a,system_role_menu b where b.role_id=0 and a.id=b.menu_id");
          while(rs.next())	
          {
        	  StringBuffer sb = new StringBuffer(100);
        	  sb.append("insert into system_role_menu(id,role_id,menu_id,operate)values(");
        	  sb.append(id);
        	  sb.append(",");
        	  sb.append(role_id);
        	  sb.append(",'");
        	  sb.append(rs.getString("id"));
        	  sb.append("',");
        	  sb.append(rs.getInt("operate"));
        	  sb.append(")");
        	  id++;
              conn.addBatch(sb.toString());
          }         
          //添加 角色对应的功能模块
          rs = conn.executeQuery("select * from nms_home_module");
          while(rs.next())	
          {
              StringBuffer sb = new StringBuffer(100);
              sb.append("insert into nms_home_module_role(enName, chName, role_id, dept_id, visible, note,type)values('");
              sb.append(rs.getString("enName"));
              sb.append("','");
              sb.append(rs.getString("chName"));
              sb.append("','");
              sb.append(role_id);//角色id
              sb.append("','");
              sb.append("0");//部门默认为空
              sb.append("','");
              sb.append(1);//默认都为可见
              sb.append("','");
              java.util.Date currentTime = new java.util.Date(); 
              SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
              String dateString = formatter.format(currentTime);  
              sb.append(dateString);
              sb.append("','");
              sb.append(rs.getInt("type"));
              sb.append("')"); 
              conn.addBatch(sb.toString()); 
          }
          //添加 角色对应的功能模块（结束）
          
          
          conn.executeBatch();
          result = true;
     }
     catch(Exception e)
     {
          
    	 result = false;
    	 conn.rollback();
    	 SysLogger.error("RoleDao.save()",e);                  
     }
     finally
     {
    	 conn.close();    	 
     }
     return result;
   }

   /**
    * 分页显示,覆盖掉父类同名方法
    */
   public List listByPage(int curpage,int perpage)
   {
	   return listByPage(curpage,"where id>0",perpage);
   }

   /**
    * 列出所有角色
    */
   public List loadAll(boolean includeAdmin)
   {
      List list = new ArrayList();
      try
      {
         String sql = null;
         if(includeAdmin)
            sql = "select * from system_role order by id";
         else
            sql = "select * from system_role where id<>0 order by id";
         rs = conn.executeQuery(sql);
         while(rs.next())
            list.add(loadFromRS(rs));
      }
      catch(Exception e)
      {
         SysLogger.error("RoleDao.loadAll()",e);
         list = null;
      }
      finally
      {
    	  conn.close();    	 
      }
      return list;
   }

   public boolean update(BaseVo baseVo)
   {
	   Role vo = (Role)baseVo;
	   boolean result = false;
       try
       {
           conn.executeUpdate("update system_role set role='" + vo.getRole() + "' where id=" + vo.getId());
           result = true;
       }
       catch(Exception e)
       {
           SysLogger.error("RoleDao.update()",e);
           result = false;
       }
       finally
       {
           conn.close();
       }
       return result;
   }

   /**
    * 删除一个角色,它将删除三个表中的记录
    * 1.删除role表中的一条记录;
    * 2.删除user表中与该角色有关的所有用户
    * 3.删除role_menu表中与该角色有关的所有关系
    */
   public boolean delete(String id)
   {
	  boolean result = false;
      try
      {
          conn.addBatch("delete from system_role_menu where role_id=" + id); //删除角色菜单关系表中相关记录
          conn.addBatch("delete from system_user where role_id=" + id);      //删除用户表中该角色下的用户         
          conn.addBatch("delete from system_role where id=" + id);      
          conn.addBatch("delete from nms_home_module_role where role_id=" + id);// 删除 角色模块数据
          conn.addBatch("delete from nms_home_module_user where role_id=" + id);// 删除 用户首页模块数据
          conn.executeBatch();
          result = true;
      }
      catch(Exception ex)
      {
          conn.rollback();
          SysLogger.error("Error in RoleDao.delete()",ex);
          result = false;
      }
      finally
      {
         conn.close();
      }
      return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
       Role vo = new Role();
       try
       {
           vo.setId(rs.getInt("id"));
           vo.setRole(rs.getString("role"));
       }
       catch(Exception e)
       {
    	   SysLogger.error("RoleDao.loadFromRS()",e); 
       }	   
       return vo;
   }   
}
