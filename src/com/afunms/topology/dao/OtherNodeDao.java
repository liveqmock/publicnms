/**
 * <p>Description:operate table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-04
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.OtherNode;

public class OtherNodeDao extends BaseDao implements DaoInterface
{
   public OtherNodeDao()
   {
	   super("topo_other_node");	   	  
   }
   
   public boolean save(BaseVo baseVo)
   {
	   OtherNode vo = (OtherNode)baseVo;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into topo_other_node(name,ipAddress,alais,category,sendmobiles,sendemail,sendphone,bid,managed)values('");
	   sql.append(vo.getName());
	   sql.append("','");
	   sql.append(vo.getIpAddress());
	   sql.append("','");
	   sql.append(vo.getAlais());
	   sql.append("',");
	   sql.append(vo.getCategory());
	   sql.append(",'");
	   sql.append(vo.getSendmobiles());
	   sql.append("','");
	   sql.append(vo.getSendemail());
	   sql.append("','");
	   sql.append(vo.getSendphone());
	   sql.append("','");
	   sql.append(vo.getBid());
	   sql.append("',");
	   sql.append(vo.getManaged());
	   sql.append(")");
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean update(BaseVo baseVo)
   {   
	   OtherNode vo = (OtherNode)baseVo;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update topo_other_node set ");
	   sql.append(" name = '"+vo.getName()+"'");
	   sql.append(", ipAddress = '"+vo.getIpAddress()+"'");
	   sql.append(", alais= '"+vo.getAlais()+"'");
	   sql.append(", sendmobiles= '"+vo.getSendmobiles()+"'");
	   sql.append(", sendemail= '"+vo.getSendemail()+"'");
	   sql.append(", sendphone= '"+vo.getSendphone()+"'");
	   sql.append(", bid= '"+vo.getBid()+"'");
	   sql.append(", managed= "+vo.getManaged());
	   sql.append(" where id = "+vo.getId());
	   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from topo_other_node where id=" + id);
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("OtherNodeDao.delete(String id)",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   public BaseVo findByType_Name(String type,String name)
   {
	   BaseVo vo = null;
       try
	   {
		   rs = conn.executeQuery("select * from topo_other_node where category=" + type + " and name='" + name + "'"); 
		   if(rs.next())
		       vo = loadFromRS(rs);
	   }    
	   catch(Exception ex)
	   {
		   ex.printStackTrace();
		   SysLogger.error("BaseDao.findByType_Name(String type,String name)",ex);
	   }  
	   finally
	   {
		   conn.close();   
	   }
       return vo;
   }
   public BaseVo loadFromRS(ResultSet rs)
   {
	   OtherNode vo = new OtherNode();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setAlais(rs.getString("alais"));
		   vo.setBid(rs.getString("bid"));
		   vo.setCategory(rs.getInt("category"));
		   vo.setIpAddress(rs.getString("ipAddress"));
		   vo.setName(rs.getString("name"));
		   vo.setSendemail(rs.getString("sendemail"));
		   vo.setSendmobiles(rs.getString("sendmobiles"));
		   vo.setSendphone(rs.getString("sendphone"));
		   vo.setManaged(rs.getInt("managed"));
		   vo.setCollecttype(rs.getString("collecttype"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("OtherNodeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}
