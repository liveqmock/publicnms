/**
 * <p>Description:operate table nms_custom_view_ip and nms_custom_view_xml</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 聂成海
 * @date 2006-10-21
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.NodeEquip;

public class NodeEquipDao extends BaseDao implements DaoInterface
{
    public NodeEquipDao() {
    	super("topo_node_equip");
	}
	
    public boolean save(BaseVo baseVo) 
    {
    	NodeEquip vo = (NodeEquip)baseVo;		
	    StringBuffer sql = new StringBuffer(200);
	    sql.append("insert into topo_node_equip(id,xml_name,node_id,equip_id)values(");
	    sql.append(getNextID());
	    sql.append(",'");
	    sql.append(vo.getXmlName());
	    sql.append("','");
	    sql.append(vo.getNodeId());
	    sql.append("',");
	    sql.append(vo.getEquipId());
	    sql.append(")");
	   
	    return saveOrUpdate(sql.toString());
    }
	
	public boolean update(BaseVo baseVo) 
	{
		NodeEquip vo = (NodeEquip)baseVo;		
	    StringBuffer sql = new StringBuffer(200);
	    sql.append("update topo_node_equip set xml_name='");
	    sql.append(vo.getXmlName());
	    sql.append("',node_id='");
	    sql.append(vo.getNodeId());
	    sql.append("',equip_id=");
	    sql.append(vo.getEquipId());
	    sql.append(" where id=");
	    sql.append(vo.getId());
	    
	    return saveOrUpdate(sql.toString());
	}
	
    //删除所有数据
	public void deleteAll()
	{
	    try {
	        conn.addBatch("delete from topo_node_equip where 1=1");
		    conn.executeBatch();
	    } catch(Exception ex)
	    {
	        SysLogger.error("Error in NodeEquipDao.deleteAll()",ex);
	    } finally {
	        conn.close();
	    }
	}
	
	/**
	    * 根据id找一条记录
	    */
    public BaseVo findById(int id)
    {
	    BaseVo vo = null;
        try {
		    rs = conn.executeQuery("select * from topo_node_equip where id=" + id); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch (Exception ex) {
		    SysLogger.error("NodeEquipDao.findById(int id)",ex);
	    } finally {
			conn.close();
		} 
        return vo;
    }
			
    public BaseVo findByNodeAndXml(String nodeId,String fileName)
    {
	    BaseVo vo = null;
        try {
		    rs = conn.executeQuery("select * from topo_node_equip where node_id='" + nodeId + "' and xml_name='" + fileName + "'"); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch (Exception ex) {
		    SysLogger.error("NodeEquipDao.findByNodeAndXml(String nodeId,String fileName)",ex);
	    } 
        return vo;
    }
    
    public BaseVo findByNode(String nodeId)
    {
	    BaseVo vo = null;
        try {
		    rs = conn.executeQuery("select * from topo_node_equip where node_id='" + nodeId + "'"); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch (Exception ex) {
		    SysLogger.error("NodeEquipDao.findByNode(String nodeId)",ex);
	    } 
        return vo;
    }
    
    public BaseVo findByXml(String fileName)
    {
	    BaseVo vo = null;
        try {
		    rs = conn.executeQuery("select * from topo_node_equip where xml_name='" + fileName  + "'"); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch (Exception ex) {
		    SysLogger.error("NodeEquipDao.findByXml(String fileName)",ex);
	    } 
        return vo;
    }
    
    /**
	    * 按xml文件删除一条记录
	    */
	public String deleteByXml(String fileName)
	{
	    try {
	    	conn.executeUpdate("delete from topo_node_equip where xml_name='" + fileName + "'");
	    } catch(Exception ex) {
	        SysLogger.error("Error in RelationDao.deleteByXml(String fileName)",ex);
	    } finally {
	        conn.close();
	    }
	    return null;
	}
	public String deleteByNode(String nodeId)
	{
	    try {
	    	conn.executeUpdate("delete from topo_node_equip where node_id='" + nodeId +"'");
	    } catch(Exception ex) {
	        SysLogger.error("Error in RelationDao.deleteByNode(String nodeId)",ex);
	    } finally {
	        conn.close();
	    }
	    return null;
	}
	public BaseVo loadFromRS(ResultSet rs)
	{
		NodeEquip vo = new NodeEquip();
		try {
			vo.setId(rs.getInt("id"));
			vo.setXmlName(rs.getString("xml_name"));
			vo.setNodeId(rs.getString("node_id"));
			vo.setEquipId(rs.getInt("equip_id"));
		} catch(Exception e) {
			SysLogger.error("NodeEquipDao.loadFromRS()",e);
		} 
	    return vo;
	}	
}   