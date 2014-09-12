package com.afunms.topology.dao;

import java.sql.ResultSet;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.HostNode;

public class CommonDao extends BaseDao implements DaoInterface {

	private String table;

	public CommonDao(String table) {
		super(table);
		this.table = table;
	}

	public boolean updateAliasById(String alias, String id) {
		String sql = "update " + table + " set alias='" + alias + "' where id="
				+ id;
		return saveOrUpdate(sql.toString());
	}

	/**
	 * É¾³ýÒ»Ìõ¼ÇÂ¼
	 */
	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from " + table + " where id=" + id);
			conn.addBatch("delete from topo_node_monitor where node_id=" + id);
    	    conn.addBatch("delete from topo_node_multi_data where node_id=" + id);
    	    conn.addBatch("delete from topo_node_single_data where node_id=" + id);
            conn.addBatch("delete from topo_interface where node_id=" + id);
            conn.addBatch("delete from topo_interface_data where node_id=" + id);
            conn.addBatch("delete from topo_network_link where start_id=" + id + " or end_id=" + id);
            conn.addBatch("delete from server_telnet_config where node_id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			SysLogger.error("CommonDao.delete()", ex);
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		HostNode vo = new HostNode();
		try {
			vo.setId(rs.getInt("id"));
			if(rs.getString("ip_address")!=null&&!"".equals(rs.getString("ip_address"))){
				vo.setIpAddress(rs.getString("ip_address"));
			}
			if(rs.getLong("ip_long")!=0){
				vo.setIpLong(rs.getLong("ip_long"));
			}
			if(rs.getString("sys_name")!=null&&!"".equals(rs.getString("sys_name"))){
				vo.setSysName(rs.getString("sys_name"));
			}
			if(rs.getString("alias")!=null&&!"".equals(rs.getString("alias"))){
				vo.setAlias(rs.getString("alias"));
			}
			if(rs.getString("net_mask")!=null&&!"".equals(rs.getString("net_mask"))){
				vo.setNetMask(rs.getString("net_mask"));
			}
			if(rs.getString("sys_descr")!=null&&!"".equals(rs.getString("sys_descr"))){
				vo.setSysDescr(rs.getString("sys_descr"));
			}
			if(rs.getString("sys_location")!=null&&!"".equals(rs.getString("sys_location"))){
				vo.setSysLocation(rs.getString("sys_location"));
			}
			if(rs.getString("sys_contact")!=null&&!"".equals(rs.getString("sys_contact"))){
				vo.setSysContact(rs.getString("sys_contact"));
			}
			if(rs.getString("sys_oid")!=null&&!"".equals(rs.getString("sys_oid"))){
				vo.setSysOid(rs.getString("sys_oid"));
			}
			if(rs.getString("category")!=null&&!"".equals(rs.getString("category"))){
				vo.setCategory(rs.getInt("category"));
			}

		} catch (Exception e) {
			SysLogger.error("CommonDao.loadFromRS()", e);
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

}
