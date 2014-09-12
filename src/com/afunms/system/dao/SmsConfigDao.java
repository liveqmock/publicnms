/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.SmsConfig;
import com.afunms.system.model.User;

public class SmsConfigDao extends BaseDao implements DaoInterface
{
   public SmsConfigDao()
   {
	   super("nms_smsconfig");
   }
   
   public List getSmsConfigByObject(String objectId, String objectType) {
       List list = new ArrayList();
       try {
    	   String sql = "select * from nms_smsconfig where objectId='" + objectId + "' and objectType='" + objectType + "'";
    	   rs = conn.executeQuery(sql);
           while(rs.next())
              list.add(loadFromRS(rs));
       } catch (Exception e) {
       		e.printStackTrace();
       } finally {
           if (rs != null) {
               try {
                   rs.close();
               } catch (SQLException ex) {
                   ex.printStackTrace();
               }
           }
           if (conn != null) {
               try {
                   conn.close();
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
           }
       }
       return list;
   } 
   
   public boolean saveSmsConfigList(String objectId,String objectType,ArrayList smsConfigList) {
       try {
           String sql = "";
           sql = "delete from nms_smsconfig where objectId='" + objectId + "' and objectType='" + objectType + "'";
           System.out.println(sql); 
           try{
        	   conn.executeUpdate(sql);
           }catch(Exception e){
        	   e.printStackTrace();
           }
           Iterator iterator = smsConfigList.iterator();
           
           while (iterator.hasNext()) {
        	   System.out.println(smsConfigList.size()+"---------------------------");            	
               SmsConfig smsConfig = (SmsConfig) iterator.next();
               sql = "insert into nms_smsconfig (objectid,objecttype,begintime,endtime,userids) values ('" + smsConfig.getObjectId() + "','" + smsConfig.getObjectType() + "','" + smsConfig.getBeginTime() + "','" + smsConfig.getEndTime() + "','" + smsConfig.getUserIds() + "')";
               System.out.println("---"+sql);
               try{
            	   conn.executeUpdate(sql);
               }catch(Exception e){
            	   e.printStackTrace();
               }
           }
       } catch (Exception e) {
       		e.printStackTrace();
       		return false;
       } finally {
           if (conn != null) {
               try {
                   conn.close();
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
           }
       }
       return true;
   }

   public boolean save(BaseVo baseVo)
   {
	   return false;	   
   }
   
   public int save(User vo)
   {	   
       int result = -1;
       String sql = null;
//	   try
//	   {
//	       sql = "select * from system_user where user_id='" + vo.getUserid() + "'";
//	       rs = conn.executeQuery(sql);
//	       if(rs.next())  //用户已经存在
//	          return 0;
//
//	       StringBuffer sqlBf = new StringBuffer(100);
//	       sqlBf.append("insert into system_user(id,name,user_id,password,sex,dept_id,position_id,role_id,phone,email,mobile,businessids)");
//	       sqlBf.append("values(");
//	       sqlBf.append(getNextID());
//	       sqlBf.append(",'");
//	       sqlBf.append(vo.getName());
//	       sqlBf.append("','");
//	       sqlBf.append(vo.getUserid());
//	       sqlBf.append("','");
//	       sqlBf.append(vo.getPassword());
//	       sqlBf.append("',");
//	       sqlBf.append(vo.getSex());
//	       sqlBf.append(",");
//	       sqlBf.append(vo.getDept());
//	       sqlBf.append(",");
//	       sqlBf.append(vo.getPosition());
//	       sqlBf.append(",");	       
//	       sqlBf.append(vo.getRole());
//	       sqlBf.append(",'");
//	       sqlBf.append(vo.getPhone());
//	       sqlBf.append("','");
//	       sqlBf.append(vo.getEmail());
//	       sqlBf.append("','");
//	       sqlBf.append(vo.getMobile());
//	       sqlBf.append("','");
//	       sqlBf.append(vo.getBusinessids());
//	       sqlBf.append("')");
//	       conn.executeUpdate(sqlBf.toString());
//	       result = 1;
//	   }
//	   catch (Exception e)
//	   {
//	    	result = -1;
//	        SysLogger.error("Error in UserDao.save()",e);
//	   }
//	   finally
//	   {
//	       conn.close();
//	   }
	   return result;
   }

   public boolean update(BaseVo baseVo)
   {
	   User vo = (User)baseVo;

	   StringBuffer sql = new StringBuffer(200);
//       sql.append("update system_user set name='");
//       sql.append(vo.getName());
//       sql.append("',sex=");
//       sql.append(vo.getSex());
//       sql.append(",dept_id=");
//       sql.append(vo.getDept());
//       sql.append(",position_id=");
//       sql.append(vo.getPosition());
//       sql.append(",role_id=");
//       sql.append(vo.getRole());
//       sql.append(",phone='");
//       sql.append(vo.getPhone());
//       sql.append("',mobile='");
//       sql.append(vo.getMobile());
//       sql.append("',email='");
//       sql.append(vo.getEmail());
//       sql.append("',businessids='");
//       sql.append(vo.getBusinessids());          
//       if(vo.getPassword()!=null) //密码要修改
//       {
//           sql.append("',password='");
//           sql.append(vo.getPassword());
//       }
//	   sql.append("' where id=");
//       sql.append(vo.getId());
       return saveOrUpdate(sql.toString());
   }

   public BaseVo loadFromRS(ResultSet rs)
   {
	   SmsConfig smsConfig = new SmsConfig();
      try
      {
    	  
    	  
    	  
          smsConfig.setId(rs.getInt("id"));
          smsConfig.setObjectId(rs.getString("objectid"));
          smsConfig.setObjectType(rs.getString("objecttype"));
          smsConfig.setBeginTime(rs.getString("begintime"));
          smsConfig.setEndTime(rs.getString("endtime"));
          smsConfig.setUserIds(rs.getString("userids"));
      }
      catch(Exception ex)
      {
          SysLogger.error("Error in SmsConfigDAO.loadFromRS()",ex);
          ex.printStackTrace();
          smsConfig = null;
      }
      return smsConfig;
   }   
}
