/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.*;
import com.afunms.config.model.Employee;

public class EmployeeDao extends BaseDao implements DaoInterface
{
   public EmployeeDao()
   {
	   super("nms_employee");
   }
   
   public List loadAll()
   {
	   return findByCriteria("select * from nms_employee order by dept_id,name"); 
   }
   
   /**
    * 按用户ID和密码找一条记录,用于登录
    */
   public Employee findByLogin(String id,String pwd)
   {
	   Employee vo = null;
      try
      {
          rs = conn.executeQuery("select * from nms_employee where user_id='" + id + "' and password='" + pwd + "'");
          if(rs.next())
             vo = (Employee)loadFromRS(rs);
      }
      catch(Exception e)
      {
          SysLogger.error("UserDao.findByLogin",e);
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
	   return false;	   
   }
   
   public int save(Employee vo)
   {	   
       int result = -1;
       String sql = null;
	   try
	   {
	       sql = "select * from nms_employee where dept_id=" + vo.getDept() + " and name='"+vo.getName()+"'";
	       rs = conn.executeQuery(sql);
	       if(rs.next())  //用户已经存在
	          return 0;

	       StringBuffer sqlBf = new StringBuffer(100);
	       sqlBf.append("insert into nms_employee(name,sex,dept_id,position_id,phone,email,mobile,businessids)");
	       sqlBf.append("values('");
	       sqlBf.append(vo.getName());
	       sqlBf.append("',");
	       sqlBf.append(vo.getSex());
	       sqlBf.append(",");
	       sqlBf.append(vo.getDept());
	       sqlBf.append(",");
	       sqlBf.append(vo.getPosition());
	       sqlBf.append(",'");	       
	       sqlBf.append(vo.getPhone());
	       sqlBf.append("','");
	       sqlBf.append(vo.getEmail());
	       sqlBf.append("','");
	       sqlBf.append(vo.getMobile());
	       sqlBf.append("','");
	       sqlBf.append(vo.getBusinessids());
	       sqlBf.append("')");
	       conn.executeUpdate(sqlBf.toString());
	       result = 1;
	   }
	   catch (Exception e)
	   {
	    	result = -1;
	        SysLogger.error("Error in UserDao.save()",e);
	   }
	   finally
	   {
	       conn.close();
	   }
	   return result;
   }

   public boolean update(BaseVo baseVo)
   {
	   Employee vo = (Employee)baseVo;

	   StringBuffer sql = new StringBuffer(200);
       sql.append("update nms_employee set name='");
       sql.append(vo.getName());
       sql.append("',sex=");
       sql.append(vo.getSex());
       sql.append(",dept_id=");
       sql.append(vo.getDept());
       sql.append(",position_id=");
       sql.append(vo.getPosition());
       sql.append(",phone='");
       sql.append(vo.getPhone());
       sql.append("',mobile='");
       sql.append(vo.getMobile());
       sql.append("',email='");
       sql.append(vo.getEmail());
       sql.append("',businessids='");
       sql.append(vo.getBusinessids());          
	   sql.append("' where id=");
       sql.append(vo.getId());
       return saveOrUpdate(sql.toString());
   }

   public BaseVo loadFromRS(ResultSet rs)
   {
	   Employee vo = new Employee();
      try
      {
         vo.setId(rs.getInt("id"));
         vo.setName(rs.getString("name"));
         vo.setSex(rs.getInt("sex"));
         vo.setDept(rs.getInt("dept_id"));
         vo.setPosition(rs.getInt("position_id"));
         vo.setEmail(rs.getString("email"));
         vo.setPhone(rs.getString("phone"));
         vo.setMobile(rs.getString("mobile"));
         vo.setBusinessids(rs.getString("businessids"));
      }
      catch(Exception ex)
      {
          SysLogger.error("Error in UserDAO.loadFromRS()",ex);
          vo = null;
      }
      return vo;
   }   
}
