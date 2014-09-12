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
import java.util.Vector;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.*;
import com.afunms.system.model.TFtpServer;

public class TFtpServerDao extends BaseDao implements DaoInterface
{
   public TFtpServerDao()
   {
	   super("system_tftpserver");
   }

   public List listByPage(int curpage,int perpage)
   {
	   //不显示超级管理员
	   return listByPage(curpage,"where role_id>0",perpage);
   }
   
   public List getByFlage(int flag){
	   List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from system_tftpserver where usedflag = "+flag);
	   return findByCriteria(sql.toString());
   }
   
   public TFtpServer loadByFlage(int flag){
	   TFtpServer tftpserver= null;
	   List rlist = new ArrayList();
	   StringBuffer sql = new StringBuffer();
	   sql.append("select * from system_tftpserver where usedflag = "+flag);
	   rlist = findByCriteria(sql.toString());
	   if(rlist != null && rlist.size()>0){
		   tftpserver = (TFtpServer)rlist.get(0);
	   }
	   return tftpserver;
   }
   
   public boolean save(BaseVo baseVo)
   {
	   return false;	   
   }
   
   public int save(TFtpServer vo)
   {	   
       int result = -1;
       String sql = null;
	   try
	   {

	       StringBuffer sqlBf = new StringBuffer(100);
	       sqlBf.append("insert into system_tftpserver(ip,usedflag)");
	       sqlBf.append("values('");
	       sqlBf.append(vo.getIp());
	       sqlBf.append("',");
	       sqlBf.append(vo.getUsedflag());
	       sqlBf.append(")");
	       conn.executeUpdate(sqlBf.toString());
	       result = 1;
	   }
	   catch (Exception e)
	   {
	    	result = -1;
	        SysLogger.error("Error in TFtpServerDao.save()",e);
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
	   TFtpServer vo = (TFtpServer)baseVo;

	   StringBuffer sql = new StringBuffer(200);
       sql.append("update system_tftpserver set ip='");
       sql.append(vo.getIp());
       sql.append("',usedflag=");
       sql.append(vo.getUsedflag());
	   sql.append(" where id=");
       sql.append(vo.getId());
       return saveOrUpdate(sql.toString());
   }

   public BaseVo loadFromRS(ResultSet rs)
   {
	   TFtpServer vo = new TFtpServer();
      try
      {
         vo.setId(rs.getInt("id"));
         vo.setIp(rs.getString("ip"));
         vo.setUsedflag(rs.getInt("usedflag"));
      }
      catch(Exception ex)
      {
          SysLogger.error("Error in TFtpServerDao.loadFromRS()",ex);
          vo = null;
          ex.printStackTrace();
      }
      return vo;
   }   
}
