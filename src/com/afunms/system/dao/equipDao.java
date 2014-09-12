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

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.User;
import com.afunms.system.model.equip;

public class equipDao extends BaseDao implements DaoInterface
{
   public equipDao()
   {
	   super("topo_equip_pic");
   }

   public List listByPage(int curpage,int perpage)
   {
	   //不显示超级管理员
	   return listByPage(curpage,"",perpage);
   }
 
   
   /**
    * 按用户ID和密码找一条记录,用于登录
    */
   public User findByLogin(String id,String pwd)
   {
      User vo = null;
      try
      {
          rs = conn.executeQuery("select * from system_user where user_id='" + id + "' and password='" + pwd + "'");
          if(rs.next())
             vo = (User)loadFromRS(rs);
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
   
   public int save(equip vo)
   {	   
       int result = -1;
       String sql = null;
	   try
	   {
//	       sql = "select * from topo_equip_pic where cn_name ='" + vo.getCn_name() + "' and category = "+vo.getCategory();
//	       String sql1="select t.* from ("+sql+") t where cn_name ='" + vo.getCn_name() + "' and category = "+vo.getCategory();
//	       
//	       rs = conn.executeQuery(sql);
//	       if(rs.next())  
//	       {
	       StringBuffer sqlBf = new StringBuffer(100);
	       sqlBf.append("insert into topo_equip_pic (cn_name,en_name,category,topo_image,lost_image,alarm_image,path)");
	       sqlBf.append("values(");
	       sqlBf.append("'");
	       sqlBf.append(vo.getCn_name());
	       sqlBf.append("','");
	       sqlBf.append(vo.getEn_name());
	       sqlBf.append("',");
	       sqlBf.append(vo.getCategory());
	       sqlBf.append(",'");
	       sqlBf.append(vo.getTopo_image());
	       sqlBf.append("','");
	       sqlBf.append(vo.getLost_image());
	       sqlBf.append("','");
	       sqlBf.append(vo.getAlarm_image());
	       sqlBf.append("','");
	       sqlBf.append(vo.getPath());
	       sqlBf.append("')");
	       conn.executeUpdate(sqlBf.toString());
//	       result = 1;
//	       }else{
//	    	   return 0;
//	       }
	   }
	   catch (Exception e)
	   {
//	    	result = -1;
	        SysLogger.error("Error in UserDao.save()",e);
	   }
	   finally
	   {
	       conn.close();
	   }
	   return result;
   }
   public List findbyIDs(String IDs)
   {
	   List list = new ArrayList();
		  try{
			  rs = conn.executeQuery("select * from system_user where id in(" + IDs +")");
			  while(rs.next())
			  {
				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
   }
   public boolean update(BaseVo baseVo)
   {
	   equip vo = (equip)baseVo;

	   StringBuffer sql = new StringBuffer(200);
       sql.append("update topo_equip_pic set cn_name='");
       sql.append(vo.getCn_name());
       sql.append("',category=");
       sql.append(vo.getCategory());
       sql.append(",en_name='");
       sql.append(vo.getEn_name());
       sql.append("',topo_image='");
       sql.append(vo.getTopo_image());
       sql.append("',alarm_image='");
       sql.append(vo.getAlarm_image());
       sql.append("',path='");
       sql.append(vo.getPath());
       sql.append("',lost_image='");
       sql.append(vo.getLost_image());
	   sql.append("' where id=");
       sql.append(vo.getId());
       System.out.println(sql.toString());
       return saveOrUpdate(sql.toString());
   }

   public BaseVo loadFromRS(ResultSet rs)
   {
      equip vo = new equip();
      try
      {
         vo.setId(rs.getInt("id"));
         vo.setCategory(rs.getInt("category"));
         vo.setCn_name(rs.getString("cn_name"));
         vo.setEn_name(rs.getString("en_name"));
         vo.setTopo_image(rs.getString("topo_image"));
         vo.setLost_image(rs.getString("lost_image"));
         vo.setAlarm_image(rs.getString("alarm_image"));
         vo.setPath(rs.getString("path"));
         //SysLogger.info("bids===="+rs.getString("businessids"));
      }
      catch(Exception ex)
      {
          SysLogger.error("Error in equipDAO.loadFromRS()",ex);
          vo = null;
      }
      return vo;
   }   
   
   public boolean deleteById(String Id){
		String sql = "delete from topo_equip_pic where id='" + Id+"'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
   
   public List query(String sql)
   {
	   List list = new ArrayList();
		  try{
			  rs = conn.executeQuery(sql);
			  while(rs.next())
			  {
				  equip vo = new equip();
				  vo.setCn_name(rs.getString("cn_name"));
				  vo.setEn_name(rs.getString("en_name"));
				  vo.setCategory(rs.getInt("category"));
//				  BaseVo vo = loadFromRS(rs);
				  list.add(vo);
			  }
		  }catch(Exception e){e.printStackTrace();}
		  return list;
   }
}
