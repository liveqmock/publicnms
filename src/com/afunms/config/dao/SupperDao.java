package com.afunms.config.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Supper;

public class SupperDao extends BaseDao implements DaoInterface {

	public SupperDao() {
		super("nms_supper_info");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Supper vo = new Supper();
		try {
			vo.setSu_id(rs.getString("id"));
			vo.setSu_name(rs.getString("su_name"));
			vo.setSu_class(rs.getString("su_class"));
			vo.setSu_person(rs.getString("su_person"));
			vo.setSu_area(rs.getString("su_area"));
			vo.setSu_email(rs.getString("su_email"));
			vo.setSu_phone(rs.getString("su_phone"));
			vo.setSu_address(rs.getString("su_address"));
			vo.setSu_desc(rs.getString("su_desc"));
			vo.setSu_dept(rs.getString("su_dept"));
			vo.setSu_url(rs.getString("su_url"));
		} catch (Exception e) {
			SysLogger.error("HostNodeDao.loadFromRS()", e);
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public int save(Supper vo) {
		int result = -1;
		String sql = null;
		try {
			/*
			 * sql = "select * from system_user where user_id='" +
			 * vo.getUserid() + "'"; rs = conn.executeQuery(sql); if (rs.next()) //
			 * 用户已经存在 return 0;
			 */
			StringBuffer sqlBf = new StringBuffer(100);
			sqlBf
					.append("insert into nms_supper_info(id,su_name,su_class,su_area,su_desc,su_person,su_email,su_phone,su_address,su_dept,su_url)");
			sqlBf.append("values(");
			sqlBf.append(getNextID());
			sqlBf.append(",'");
			sqlBf.append(vo.getSu_name());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_class());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_area());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_desc());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_person());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_email());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_phone());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_address());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_dept());
			sqlBf.append("','");
			sqlBf.append(vo.getSu_url());
			sqlBf.append("')");
			conn.executeUpdate(sqlBf.toString());
			result = 1;
		} catch (Exception e) {
			result = -1;
			SysLogger.error("Error in UserDao.save()", e);
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo baseVo) {
		Supper vo = (Supper) baseVo;
		
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_supper_info set su_name='");
		sql.append(vo.getSu_name());
		sql.append("',su_class='");
		sql.append(vo.getSu_class());
		sql.append("',su_area='");
		sql.append(vo.getSu_area());
		sql.append("',su_desc='");
		sql.append(vo.getSu_desc());
		sql.append("',su_person='");
		sql.append(vo.getSu_person());
		sql.append("',su_email='");
		sql.append(vo.getSu_email());
		sql.append("',su_phone='");
		sql.append(vo.getSu_phone());
		sql.append("',su_address='");
		sql.append(vo.getSu_address());
		sql.append("',su_dept='");
		sql.append(vo.getSu_dept());
		sql.append("',su_url='");
		sql.append(vo.getSu_url());
		sql.append("' where id=");
		sql.append(vo.getSu_id());
		return saveOrUpdate(sql.toString());
	}

}
