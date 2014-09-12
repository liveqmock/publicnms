package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.TreeNode;

public class TreeNodeDao extends BaseDao implements DaoInterface {
	
	public TreeNodeDao() {
		super("nms_manage_nodetype");
	}

	public void delete(String id) {
		try {
			conn.executeUpdate("delete from nms_manage_nodetype where id="+ id);
		} catch (Exception ex) {
			SysLogger.error("Error in TreeNodeDao.delete()", ex);
		} finally {
			conn.close();
		}
	}

	// 删除所有数据
	public void deleteAll() {
		try {
			conn.addBatch("delete from nms_manage_nodetype where 1=1");
			conn.executeBatch();
		} catch (Exception ex) {
			SysLogger.error("Error in TreeNodeDao.deleteAll()", ex);
		} finally {
			conn.close();
		}
	}

	public boolean save(BaseVo baseVo) {
		TreeNode vo = (TreeNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_manage_nodetype(id,name,text,father_id,table_name,category,node_tag,url)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getText());
		sql.append("',");
		sql.append(vo.getFatherId());
		sql.append(",'");
		sql.append(vo.getTableName());
		sql.append("','");
		sql.append(vo.getCategory());
		sql.append("','");
		sql.append(vo.getNodeTag());
		sql.append("','");
		sql.append(vo.getUrl());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		TreeNode vo = (TreeNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_manage_nodetype set name='");
		sql.append(vo.getName());
		sql.append("',text='");
		sql.append(vo.getText());
		sql.append("',father_id=");
		sql.append(vo.getFatherId());
		sql.append(",table_name='");
		sql.append(vo.getTableName());
		sql.append("',category='");
		sql.append(vo.getCategory());
		sql.append("',node_tag='");
		sql.append(vo.getNodeTag());
		sql.append("',url='");
		sql.append(vo.getUrl());
		sql.append("' where id=");
		sql.append(vo.getId());

		return saveOrUpdate(sql.toString());
	}

	/**
	 * 按father_id查找
	 */
	public List findByFatherId(String fatherId) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_manage_nodetype where father_id=" + fatherId);
			while(rs.next())
				  list.add(loadFromRS(rs));	
		} catch (Exception ex) {
			list = null;
			SysLogger.error("TreeNodeDao.findByFatherId()", ex);
		}
		return list;
	}
	
	/**
	 * 按node_tag查找
	 */
	public BaseVo findByNodeTag(String nodeTag) {
		BaseVo vo = null;
		try {
			SysLogger.info("select * from nms_manage_nodetype where node_tag='" + nodeTag + "'");
			rs = conn.executeQuery("select * from nms_manage_nodetype where node_tag='" + nodeTag + "'");
			if(rs.next())
		        vo = loadFromRS(rs);	
		} catch (Exception ex) {
			ex.printStackTrace();
			SysLogger.error("TreeNodeDao.findByNodeTag()", ex);
		} finally {
			try{
				rs.close();
			}catch(Exception e){
				
			}
			conn.close();
		}
		return vo;
	}
	
	/**
	 * 按name查找
	 */
	public BaseVo findByName(String name) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_manage_nodetype where name='" + name +"'");
			if (rs.next())
				vo = loadFromRS(rs);	
		} catch (Exception ex) {
			SysLogger.error("TreeNodeDao.findByName()", ex);
		} finally {
			conn.close();
		}
		return vo;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		TreeNode vo = new TreeNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setText(rs.getString("text"));
			vo.setFatherId(rs.getInt("father_id"));
			vo.setTableName(rs.getString("table_name"));
			vo.setCategory(rs.getString("category"));
			vo.setNodeTag(rs.getString("node_tag"));
			vo.setUrl(rs.getString("url"));
			vo.setDeceiveNum(rs.getString("deceive_num"));
			vo.setImgUrl(rs.getString("img_url"));
			vo.setIsHaveChild(rs.getString("is_have_child"));
		} catch (Exception e) {
			SysLogger.error("TreeNodeDao.loadFromRS()", e);
		}
		return vo;
	}
}
