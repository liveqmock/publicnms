package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.BusinessSystem;
import com.afunms.config.model.Knowledgebase;

public class BusinessSystemDao extends BaseDao implements DaoInterface {

	public BusinessSystemDao() {
		super("system_businesssystem");
	}


	public BaseVo loadFromRS(ResultSet rs) {
		BusinessSystem vo = new BusinessSystem();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setDescr(rs.getString("descr"));
			vo.setContactname(rs.getString("contactname"));
			vo.setContactphone(rs.getString("contactphone"));
			vo.setContactemail(rs.getString("contactemail"));
		} catch (Exception e) {
			SysLogger.error("BusinessSystemDao.loadFromRS()", e);
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		BusinessSystem vo = (BusinessSystem) baseVo;
		StringBuffer sql = new StringBuffer();
		sql
				.append("insert into system_businesssystem(id,name,descr,contactname,contactphone,contactemail)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDescr());
		sql.append("','");
		sql.append(vo.getContactname());
		sql.append("','");
		sql.append(vo.getContactphone());
		sql.append("','");
		sql.append(vo.getContactemail());
		sql.append("');");
		return saveOrUpdate(sql.toString());
	}


	public boolean update(BaseVo baseVo) {
		BusinessSystem vo = (BusinessSystem) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_businesssystem set name='");
		sql.append(vo.getName());
		sql.append("',descr='");
		sql.append(vo.getDescr());
		sql.append("',contactname='");
		sql.append(vo.getContactname());
		sql.append("',contactphone='");
		sql.append(vo.getContactphone());
		sql.append("',contactemail='");
		sql.append(vo.getContactemail());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from system_businesssystem where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("BusinessSystemDao.delete()", e);
		} finally {
			conn.close();
		}
		return result;
	}

	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn
					.executeQuery("select * from system_businesssystem order by id");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			SysLogger.error("BusinessSystemDao:loadAll()", e);
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public String getByKeySql(String key) {
		String sql = "where name like '%" + key + "%' or descr like '%" + key
				+ "%' or contactname like '%" + key
				+ "%' or contactemail like '%" + key + "%';";
		return sql;
	}

	public Vector<List> getConditionList() {
		Vector<List> vector = new Vector<List>();
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){		
			String sqlName = "select distinct name from system_businesssystem;";
			String sqlContactName = "select distinct contactname from system_businesssystem;";		
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){		
			String sqlName = "select distinct name from system_businesssystem";
			String sqlContactName = "select distinct contactname from system_businesssystem";			
		}
		String sqlName = "select distinct name from system_businesssystem";
		String sqlContactName = "select distinct contactname from system_businesssystem";
		List nameList = new ArrayList();
		List contactNameList = new ArrayList();
		try {
			rs = conn.executeQuery(sqlName);
			while (rs.next()) {
				nameList.add(rs.getString("name"));
			}
			rs = conn.executeQuery(sqlContactName);
			while (rs.next()) {
				contactNameList.add(rs.getString("contactname"));
			}
			vector.add(nameList);
			vector.add(contactNameList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}		
		return vector;
	}

}
