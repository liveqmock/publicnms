/**
 * <p>Description:operate table nms_hint_node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-04
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.NodeDepend;

public class NodeDependDao extends BaseDao implements DaoInterface
{
   public NodeDependDao()
   {
	   super("nms_node_depend");	   	  
   }

   public List<NodeDepend> findByXml(String xml){
	   List list = new ArrayList();
	   String sql = "select * from nms_node_depend where xml='"+xml+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			   list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeDependDao.findByXml()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public List<NodeDepend> findByNode(String node){
	   List list = new ArrayList();
	   String sql = "select * from nms_node_depend where node_id='"+node+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			   list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeDependDao.findByNode()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public boolean isNodeExist(String id,String xml){
	   boolean result = false;
	   String sql = "select * from nms_node_depend where node_id='" + id + "' and xml='" + xml + "'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			   result=true;
	   }
	   catch(Exception e)
	   {
		   result=false;
		   SysLogger.error("NodeDependDao.isNodeExist()",e); 
	   }
	   return result;
   }
   
   public boolean save(BaseVo baseVo)
   {
	   NodeDepend vo = (NodeDepend)baseVo;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into nms_node_depend(node_id,xml,location,alias)values('");
	   sql.append(vo.getNodeId());
	   sql.append("','");
	   sql.append(vo.getXmlfile());
	   sql.append("','");
	   sql.append(vo.getLocation());
	   sql.append("','");
	   sql.append(vo.getAlias());
	   sql.append("')");
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean update(BaseVo baseVo)
   {   
	   NodeDepend vo = (NodeDepend)baseVo;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update nms_node_depend set ");
	   sql.append(" node_id = '"+vo.getNodeId()+"'");
	   sql.append(", xml = '"+vo.getXmlfile()+"'");
	   sql.append(", location= '"+vo.getLocation()+"'");
	   sql.append(", alias= '"+vo.getAlias()+"'");
	   sql.append(" where id = "+vo.getId());
	   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_node_depend where id=" + id);
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeDependDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public boolean deleteByIdXml(String id,String xml)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_node_depend where node_id='" + id + "' and xml='" +xml+ "'");
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeDependDao.deleteByIdXml()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public boolean deleteByXml(String xml)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_node_depend where xml='" +xml+ "'");
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("NodeDependDao.deleteByXml()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   NodeDepend vo = new NodeDepend();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setNodeId(rs.getString("node_id"));
		   vo.setXmlfile(rs.getString("xml"));
		   vo.setLocation(rs.getString("location"));
		   vo.setAlias(rs.getString("alias"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("NodeDependDao.loadFromRS()",e); 
       }	   
       return vo;
   }
   
   public boolean updateById(String id,String xy,String filename)
   {   
	   StringBuffer sql = new StringBuffer(100);
	   sql.append("update nms_node_depend set ");
	   sql.append("location= '"+xy+"'");
	   sql.append(" where node_id= '"+id+"' and xml='"+filename+"'");
	   return saveOrUpdate(sql.toString()); 
   }
}
