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
import com.afunms.topology.model.HintNode;

public class HintNodeDao extends BaseDao implements DaoInterface
{
   public HintNodeDao()
   {
	   super("nms_hint_node");	   	  
   }

   public BaseVo findById(String id,String xml){
	   HintNode vo = null;
	   String sql = "select * from nms_hint_node where node_id='" + id + "' and xml_file='" + xml + "'";
	   try
	   {
//		   SysLogger.info(sql);
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			   vo=(HintNode) loadFromRS(rs);
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HintNodeDao.findById()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return vo;
   }

   public boolean deleteByXml(String id,String xml)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_node where node_id='" + id + "' and xml_file='" + xml + "'");
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HintNodeDao.deleteByXml()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public void deleteByXml(String xml)
   {
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_node where xml_file='"+xml+"'");
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HintNodeDao.deleteByXml()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
   }
   
   public boolean nodeExist(String nodeid)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from nms_hint_node where node_id='" + nodeid + "'";		   
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return true;          		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HintNodeDao.nodeExist()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return false;	      
   }
   
   public boolean save(BaseVo baseVo)
   {
	   HintNode vo = (HintNode)baseVo;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into nms_hint_node(node_id,xml_file,node_type,image,name,alias)values('");
	   sql.append(vo.getNodeId());
	   sql.append("','");
	   sql.append(vo.getXmlfile());
	   sql.append("','");
	   sql.append(vo.getType());
	   sql.append("','");
	   sql.append(vo.getImage());
	   sql.append("','");
	   sql.append(vo.getName());
	   sql.append("','");
	   sql.append(vo.getAlias());
	   sql.append("')");
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean update(BaseVo baseVo)
   {   
	   HintNode vo = (HintNode)baseVo;
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update nms_hint_node set ");
	   sql.append(" node_id = '"+vo.getNodeId()+"'");
	   sql.append(", xml_file = '"+vo.getXmlfile()+"'");
	   sql.append(", node_type= '"+vo.getType()+"'");
	   sql.append(", image= '"+vo.getImage()+"'");
	   sql.append(", name= '"+vo.getName()+"'");
	   sql.append(", alias= '"+vo.getAlias()+"'");
	   sql.append(" where id = "+vo.getId());
	   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_node where id=" + id);
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("HintNodeDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   HintNode vo = new HintNode();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setNodeId(rs.getString("node_id"));
		   vo.setXmlfile(rs.getString("xml_file"));
		   vo.setType(rs.getString("node_type"));
		   vo.setImage(rs.getString("image"));
		   vo.setName(rs.getString("name"));
		   vo.setAlias(rs.getString("alias"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("HintNodeDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}
