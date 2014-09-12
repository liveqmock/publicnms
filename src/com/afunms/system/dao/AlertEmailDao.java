/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.*;
import com.afunms.system.model.AlertEmail;

public class AlertEmailDao extends BaseDao implements DaoInterface
{
   public AlertEmailDao()
   {
	   super("system_alertemail");
   }

   public List listByPage(int curpage,int perpage)
   {
	   //不显示超级管理员
	   return listByPage(curpage,"where role_id>0",perpage);
   }
   
   public List getByFlage(int flag)
   {
	   List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from system_alertemail where usedflag = "+flag);
	   return findByCriteria(sql.toString());
   }
   
   public boolean save(BaseVo baseVo)
   {
	   return false;	   
   }
   
   public int save(AlertEmail vo)
   {	   
       int result = -1;
       String sql = null;
	   try
	   {

	       StringBuffer sqlBf = new StringBuffer(100);
	       sqlBf.append("insert into system_alertemail(username,password,smtp,usedflag,mail_address)");
	       sqlBf.append("values('");
	       sqlBf.append(vo.getUsername());
	       sqlBf.append("','");
	       sqlBf.append(vo.getPassword());
	       sqlBf.append("','");
	       sqlBf.append(vo.getSmtp());
	       sqlBf.append("',");
	       sqlBf.append(vo.getUsedflag());
	       sqlBf.append(",'");
	       sqlBf.append(vo.getMailAddress());
	       sqlBf.append("')");
	       conn.executeUpdate(sqlBf.toString());
	       result = 1;
	   }
	   catch (Exception e)
	   {
	    	result = -1;
	        SysLogger.error("Error in UserDao.save()",e);
	        e.printStackTrace();
	   }
	   finally
	   {
	       conn.close();
	   }
	   return result;
   }

   public boolean update(BaseVo baseVo)
   {
	   AlertEmail vo = (AlertEmail)baseVo;

	   StringBuffer sql = new StringBuffer(200);
       sql.append("update system_alertemail set username='");
       sql.append(vo.getUsername());
       sql.append("',password='");
       sql.append(vo.getPassword());
       sql.append("',smtp='");
       sql.append(vo.getSmtp());
       sql.append("',usedflag='");
       sql.append(vo.getUsedflag());
       sql.append("',mail_address='");
       sql.append(vo.getMailAddress());
       sql.append("'");
	   sql.append(" where id='");
       sql.append(vo.getId());
       sql.append("'");
       return saveOrUpdate(sql.toString());
   }

   public BaseVo loadFromRS(ResultSet rs)
   {
	   AlertEmail vo = new AlertEmail();
      try
      {
         vo.setId(rs.getInt("id"));
         vo.setUsername(rs.getString("username"));
         vo.setPassword(rs.getString("password"));
         vo.setSmtp(rs.getString("smtp"));
         vo.setUsedflag(rs.getInt("usedflag"));
         vo.setMailAddress(rs.getString("mail_address"));
      }
      catch(Exception ex)
      {
          SysLogger.error("Error in UserDAO.loadFromRS()",ex);
          vo = null;
          ex.printStackTrace();
      }
      return vo;
   }   
}
