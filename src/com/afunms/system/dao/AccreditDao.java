/**
 * <p>Description:operate table NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-08
 */

package com.afunms.system.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.*;

public class AccreditDao extends BaseDao
{
   public AccreditDao()
   {
	   super("system_role_menu");
   }

   /**
    * 得到每个一级菜单下总有几个子菜单
    */
   public int[] getMenuNumByFirst(boolean isAdmin)
   {
      int[] result = null;
      int len = 0;
      try
      {
         //得到一级菜单总数,然后初始化数组
    	 String sql = null; 
    	 if(isAdmin)  //is superadmin
            sql = "select count(*) from (select SUBSTRING(id,1,2) from system_menu group by SUBSTRING(id,1,2)) t";
    	 else   //2006.10.22改
    		sql = "select count(*) from (select SUBSTRING(menu_id,1,2) from system_role_menu where operate>0 and role_id=0 group by SUBSTRING(menu_id,1,2)) t"; 
         rs = conn.executeQuery(sql);
         if(rs.next())
            len = rs.getInt(1);

         if(len==0) return null;

         result = new int[len];
         if(isAdmin)  //is superadmin
            sql = "select SUBSTRING(id,1,2),count(id)-1 cc from system_menu group by SUBSTRING(id,1,2) order by id";
         else
         	sql = "select SUBSTRING(id,1,2),count(id)-1 cc from (select a.* from system_menu a,system_role_menu b " 
         		+ "where b.role_id=0 and b.operate>0 and a.id=b.menu_id) t group by SUBSTRING(id,1,2) order by id";
         
         rs = conn.executeQuery(sql);
         len = 0;
         while(rs.next())
         {
            result[len] = rs.getInt("cc");
            len++;
         }
      }
      catch(Exception e)
      {
          SysLogger.error("AccreditDao.getMenuNumByFirst",e);
          result = null;
      }
      return result;
   }

   /**
    * 按角色id得到角色菜单关系表
    */
   public List loadRoleMenu(int role_id)
   {
      List list = new ArrayList();
      String sql = null;
      try
      {
      	 if(role_id==0)  //is superadmin
            sql = "select b.id,b.title,c.operate from system_role a,system_menu b,system_role_menu c "
           	    + "where a.id=c.role_id and b.id=c.menu_id and a.id=0 order by b.id";        
         else      		
            sql = "select b.id,b.title,c.operate from system_role a,system_menu b,"
        	    + "system_role_menu c where a.id=c.role_id and b.id=c.menu_id and a.id="
			    + role_id + " and c.operate>0 order by b.id";  
         rs = conn.executeQuery(sql);           
         while(rs.next())
         {	
        	 Accredit vo = new Accredit();
        	 vo.setMenu(rs.getString("id"));
        	 vo.setOperate(rs.getInt("operate"));
          	 vo.setTitle(rs.getString("title"));           	             	 
             list.add(vo);
         }   
      }
      catch(Exception e)
      {
          SysLogger.error("AccreditDao.loadRoleMenu",e);
      }
      return list;
   }

   /**
    * 更新角色的授权
    * @role=角色ID
    * @oprvalue=操作权限
    */
   public boolean update(int role,String[] oprvalue)
   {
      boolean result = false;
      StringBuffer sqlBf = null;
      try
      {
         int loc = 0;
         for(int i=0;i<oprvalue.length;i++)
         {
            loc = oprvalue[i].indexOf(",");
            sqlBf = new StringBuffer(100);
            sqlBf.append("update system_role_menu set operate=");
            sqlBf.append(oprvalue[i].substring(loc+1));
            sqlBf.append(" where role_id=");
            sqlBf.append(role);
            sqlBf.append(" and menu_id='");
            sqlBf.append(oprvalue[i].substring(0,loc));
            sqlBf.append("'");
            conn.addBatch(sqlBf.toString());
         }
         UpdateOperate();
         conn.executeBatch();
         result = true;
      }
      catch(Exception e)
      {
          conn.rollback();
          result = false;
          SysLogger.error("AccreditDao.update()",e);
      }
      finally
      {
         conn.close();
      }
      return result;
   }

   /**
    * 更新角色的授权(超级管理员用的)
    */
   public boolean adminUpdate(String[] oprvalue)
   {
   	  boolean result = false;
      try
      {
         int loc = 0;
         int operate = 0;
         for(int i=0;i<oprvalue.length;i++)
         {
            loc = oprvalue[i].indexOf(",");
            StringBuffer sqlBf = new StringBuffer(100);
            operate = Integer.parseInt(oprvalue[i].substring(loc+1));            
            if(operate==0) //禁用
            {
               sqlBf.append("update system_role_menu set operate=");
               sqlBf.append(operate);
               sqlBf.append(" where menu_id='");
               sqlBf.append(oprvalue[i].substring(0, loc));
               sqlBf.append("'");
            }
            else //开放
            {
                //开放时只能把原来禁用的菜单改为"开放",而对原来就是"开放"的菜单保持其operate值
               sqlBf.append("update system_role_menu set operate=");
               sqlBf.append(operate);
               sqlBf.append(" where menu_id='");
               sqlBf.append(oprvalue[i].substring(0, loc));
               sqlBf.append("' and operate=0");
            }            
            conn.addBatch(sqlBf.toString());
         }//enb_for
         conn.executeBatch();
         UpdateOperate();         
         result = true;
      }
      catch(Exception e)
      {
          conn.rollback();
          result = false;
          SysLogger.error("AccreditDao.adminUpdate()",e);         
      }
      finally
      {
         conn.close();
      }
      return result;
   }

   private void UpdateOperate()
   {
      Menu menu = null;
      MenuDao menuDao = new MenuDao();
      List fmList = menuDao.loadTopMenu();
      Role role = null;
      RoleDao roleDao = new RoleDao();
      List roleList = roleDao.loadAll(true); //不包括superadmin
      StringBuffer sqlBf = null;
      StringBuffer sqlBf2 = null;
      ResultSet rs = null;
      int maxOperate = 0;
      for(int i=0;i<fmList.size();i++)
      {
        menu = (Menu)fmList.get(i);
        for(int j=0;j<roleList.size();j++)
        {
           role = (Role)roleList.get(j);
           sqlBf = new StringBuffer(100);
           sqlBf2 = new StringBuffer(100);
           
           sqlBf2.append("select max(operate) from system_role_menu where role_id=");
           sqlBf2.append(role.getId());
           sqlBf2.append(" and substr(menu_id,3,4)<>'00' and substr(menu_id,1,2)='");
           sqlBf2.append(menu.getId().substring(0,2));
           sqlBf2.append("'");
           //mysql不能使用嵌套更新，故多这一步
           rs = conn.executeQuery(sqlBf2.toString());           
           maxOperate = 0;
           try
		   {
             if(rs.next())
           	    maxOperate = rs.getInt(1);
		   }
           catch(SQLException sqle)
		   {
           	  maxOperate = 0;	
		   }finally{
			   if(rs != null){
		    		try{
		    			rs.close();
		    		}catch(Exception e){
		    			
		    		}
		    	}
		   }
           sqlBf.append("update system_role_menu set operate=");
           sqlBf.append(maxOperate);
           sqlBf.append(" where role_id=");
           sqlBf.append(role.getId());
           sqlBf.append(" and menu_id='");
           sqlBf.append(menu.getId());
           sqlBf.append("'");
           conn.addBatch(sqlBf.toString());
        }
      }
      conn.executeBatch();
   }

   /**
    * 按角色id和菜单id得到操作权限
    */
   public int getOperate(int role,String menu)
   {
      int opr = -1;
      try
      {
      	 rs = conn.executeQuery("select * from system_role_menu where role_id=" + role + " and menu_id='" + menu + "'");
         if(rs.next())
           opr = rs.getInt("operate");
      }
      catch(Exception e)
      {
          SysLogger.error("AccreditDao.getOperate",e);
          opr = -1;
      }
      finally
      {
         conn.close();
      }
      return opr;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
       return null;
   }	   
}

