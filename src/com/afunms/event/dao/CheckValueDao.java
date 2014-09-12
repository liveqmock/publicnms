/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.CheckValue;

public class CheckValueDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CheckValueDao() {
		super("nms_checkvalue");
	}

	// -------------load all --------------
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_checkvalue order by name");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			SysLogger.error("CheckValueDao:loadAll()", e);
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadByWhere(String where) {
		return findByCondition(where);
	}

	public boolean save(BaseVo baseVo) {
		CheckValue vo = (CheckValue) baseVo;
		// 先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkvalue(nodeid,indicators_name,sindex,type,subtype,alarmlevel,content,thevalue,topo_show)values(");
		sql.append("'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIndicatorsName());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',");
		sql.append(vo.getAlarmlevel());
		sql.append(",'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getTopoShow());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean savecheckvalue(BaseVo baseVo) {
		CheckValue vo = (CheckValue) baseVo;
		// 先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkvalue(nodeid,indicators_name,sindex,type,subtype,alarmlevel,content,thevalue,topo_show)values(");
		sql.append("'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIndicatorsName());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',");
		sql.append(vo.getAlarmlevel());
		sql.append(",'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getTopoShow());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean deleteByName(String indicatorsName) {
		boolean flag = true;
		String sql = "delete from nms_checkvalue where indicators_name='"
				+ indicatorsName + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public boolean empty() {
		String sql = "delete from nms_checkvalue";
		return saveOrUpdate(sql);
	}

	// ---------------update a business----------------
	public boolean update(BaseVo baseVo) {
		return true;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		CheckValue vo = new CheckValue();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setContent(rs.getString("content"));
			vo.setIndicatorsName(rs.getString("indicators_name"));
			vo.setSindex(rs.getString("sindex"));
			vo.setAlarmlevel(rs.getInt("alarmlevel"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setTopoShow(rs.getString("topo_show"));
		} catch (Exception e) {
			SysLogger.error("EventListDao.loadFromRS()", e);
			vo = null;
		}
		return vo;
	}

	public List<CheckValue> findCheckValue(String nodeId, String type,
			String subtype, String name, String sindex) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			if(sindex!=null&&!"".equals(sindex)&&!"null".equals(sindex)){
				rs = conn.executeQuery("select * from nms_checkvalue where nodeid='"
						+ nodeId + "' and type='" + type
						+ "' and subtype='" + subtype
						+ "' and indicators_name='" + name
						+ "' and sindex='" + sindex + "'");
			} else {
				rs = conn.executeQuery("select * from nms_checkvalue where nodeid='"
						+ nodeId + "' and type='" + type
						+ "' and subtype='" + subtype
						+ "' and indicators_name='" + name
						+ "'");
			}
			
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("CheckValueDao.findCheckValue(String id,String type,String subtype,String name,String subname)",e);
		}
		return list;
	}
    
	public boolean deleteByNodeType(String nodeid,String type)
	{
		boolean flag = true;
		String sql = "delete from nms_checkvalue where nodeid='"+nodeid+"' and type ='"+type+"'";
		try{
			conn.executeUpdate(sql);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
	public List<CheckValue> findCheckValue(String nodeId, String type,
			String subtype, String name) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			rs = conn.executeQuery("select * from nms_checkvalue where nodeid='"
							+ nodeId + "' and type='" + type
							+ "' and subtype='" + subtype
							+ "' and indicators_name='" + name + "'");
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("CheckValueDao.findCheckValue(String id,String type,String subtype,String name,String subname)",e);
		}
		return list;
	}
	
	public List<CheckValue> findCheckValue(String nodeId, String type) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			rs = conn.executeQuery("select * from nms_checkvalue where nodeid='"
							+ nodeId + "' and type='" + type + "'");
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("CheckValueDao.findCheckValue(String nodeId, String type)",e);
		}
		return list;
	}

	public List<CheckValue> findCheckValue(String nodeId) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			rs = conn.executeQuery("select * from nms_checkvalue where nodeid='"
							+ nodeId + "'");
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("CheckValueDao.findCheckValue(String nodeId)", e);
		}
		return list;
	}

	public boolean deleteCheckValue(String nodeId, String type, String subtype,
			String name) {
		return saveOrUpdate("delete from nms_checkvalue where nodeid='"
				+ nodeId + "' and type='" + type + "' and subtype='" + subtype
				+ "' and indicators_name='" + name + "'");
	}

	public boolean deleteCheckValue(String nodeId, String type, String subtype,
			String name, String sindex) {
		return saveOrUpdate("delete from nms_checkvalue where nodeid='"
				+ nodeId + "' and type='" + type + "' and subtype='" + subtype
				+ "' and indicators_name='" + name + "' and sindex='" + sindex
				+ "'");
	}
}
