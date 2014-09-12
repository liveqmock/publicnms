/**
 * <p>Description:operate table nms_custom_view_ip and nms_custom_view_xml</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project Äô³Éº£
 * @date 2006-10-21
 */

package com.afunms.application.dao;

import java.sql.ResultSet;

import com.afunms.common.base.*;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.application.model.IPNode;

public class IPNodeDao extends BaseDao implements DaoInterface
{
    public IPNodeDao()
    {
	    super("app_ip_node");	   	  
    }
	     		
	public boolean save(BaseVo baseVo) 
	{
		IPNode vo = (IPNode)baseVo;		
	    StringBuffer sql = new StringBuffer(200);
	    sql.append("insert into app_ip_node(id,ip_address,ip_long,alias)values(");
	    sql.append(vo.getId());
	    sql.append(",'");
	    sql.append(vo.getIpAddress());
		sql.append("',");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(",'");	   	    
	    sql.append(vo.getAlias());
	    sql.append("')");	    
	    return saveOrUpdate(sql.toString());
	}
	
	public boolean update(BaseVo baseVo) 
	{
		IPNode vo = (IPNode)baseVo;		
	    StringBuffer sql = new StringBuffer(200);
	    sql.append("update app_ip_node set alias='");
	    sql.append(vo.getAlias());
	    sql.append("',ip_address='");
	    sql.append(vo.getIpAddress());
	    sql.append("',ip_long=");
	    sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
	    sql.append(" where id=");
	    sql.append(vo.getId());	    
	    return saveOrUpdate(sql.toString());
	}
	
	public boolean delete(String id)
	{
		boolean result = false;
		try
		{
		    conn.addBatch("delete from topo_node_single_data where node_id=" + id);
		    conn.addBatch("delete from app_ip_node where id=" + id);
		    conn.executeBatch();
		    result = true;
		}
		catch(Exception e)
		{			
		}
		return result;
	}
	
	public BaseVo loadFromRS(ResultSet rs)
	{
		IPNode vo = new IPNode();
		try
		{
			vo.setId(rs.getInt("id"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setAlias(rs.getString("alias"));
		}
		catch(Exception e)
		{
			SysLogger.error("IPNodeDao.loadFromRS()",e);
		}
	    return vo;
	}	
}   