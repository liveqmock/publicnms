/**
 * <p>Description:operate table nms_custom_view_ip and nms_custom_view_xml</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 聂成海
 * @date 2006-10-21
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.Relation;

public class RelationDao extends BaseDao implements DaoInterface {
	public RelationDao() {
		super("node_submap_relation");
	}

	public String delete(String mapId) {
		String nodeStr = null;
		try {
			rs = conn
					.executeQuery("select * from node_submap_relation where map_id='"
							+ mapId + "'");
			if (rs.next())
				nodeStr = rs.getString("node_id") + ","
						+ rs.getString("xml_name");
			conn
					.executeUpdate("delete from node_submap_relation where map_id='"
							+ mapId + "'");
		} catch (Exception ex) {
			SysLogger.error("Error in RelationDao.delete()", ex);
		} finally {
			conn.close();
		}
		return nodeStr;
	}

	// 删除所有数据
	public void deleteAll() {
		try {
			conn.addBatch("delete from node_submap_relation where 1=1");
			conn.executeBatch();
		} catch (Exception ex) {
			SysLogger.error("Error in RelationDao.deleteAll()", ex);
		} finally {
			conn.close();
		}
	}

	/**
	 * 按nodeId和nodeId所在的xml文件删除一条记录
	 */
	public String deleteByNode(String nodeId, String fileName) {
		try {
			conn
					.executeUpdate("delete from node_submap_relation where xml_name='"
							+ fileName + "' and node_id='" + nodeId + "'");
		} catch (Exception ex) {
			SysLogger.error("Error in RelationDao.deleteByNode()", ex);
		} finally {
			conn.close();
		}
		return null;
	}

	public boolean save(BaseVo baseVo) {
		Relation vo = (Relation) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql
				.append("insert into node_submap_relation(id,xml_name,node_id,category,map_id)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getXmlName());
		sql.append("','");
		sql.append(vo.getNodeId());
		sql.append("','");
		sql.append(vo.getCategory());
		sql.append("','");
		sql.append(vo.getMapId());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Relation vo = (Relation) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update node_submap_relation set xml_name='");
		sql.append(vo.getXmlName());
		sql.append("',node_id='");
		sql.append(vo.getNodeId());
		sql.append("',category='");
		sql.append(vo.getCategory());
		sql.append("',map_id='");
		sql.append(vo.getMapId());
		sql.append("' where id=");
		sql.append(vo.getId());

		return saveOrUpdate(sql.toString());
	}

	/**
	 * 按nodeId和nodeId所在的xml文件找一条记录
	 */
	public BaseVo findByNodeId(String nodeId, String xmlName) {
		BaseVo vo = null;
		try {
			rs = conn
					.executeQuery("select * from node_submap_relation where xml_name='"
							+ xmlName + "' and node_id='" + nodeId + "'");
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception ex) {
			SysLogger.error("RelationDao.findByNodeId()", ex);
		}
		return vo;
	}

	/**
	 * 按map_id找一条记录
	 */
	public List<Relation> findByMapId(String mapId) {
		List<Relation> list = new ArrayList();
		Relation vo = null;
		try {
			rs = conn
					.executeQuery("select * from node_submap_relation where map_id='"
							+ mapId + "'");
			while(rs.next()){
				vo = (Relation) loadFromRS(rs);
				list.add(vo);
			}
		} catch (Exception ex) {
			SysLogger.error("RelationDao.findByMapId()", ex);
		} finally {
	    	conn.close();
	    } 
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Relation vo = new Relation();
		try {
			vo.setId(rs.getInt("id"));
			vo.setXmlName(rs.getString("xml_name"));
			vo.setNodeId(rs.getString("node_id"));
			vo.setCategory(rs.getString("category"));
			vo.setMapId(rs.getString("map_id"));
		} catch (Exception e) {
			SysLogger.error("RelationDao.loadFromRS()", e);
		}
		return vo;
	}
}