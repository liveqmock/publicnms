package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.TimeGratherConfig;

public class TimeGratherConfigDao extends BaseDao implements DaoInterface {

	public TimeGratherConfigDao() {
		super("nms_timegratherconfig");
	}

	public List findTimeGratherConfigByObject(String objectId, String objectType) {
		List list = new ArrayList();
		try {
			String sql = "select id,objectid,objecttype,begintime,endtime from nms_timegratherconfig where objectId='"
					+ objectId + "' and objectType='" + objectType + "'";
			rs = conn.executeQuery(sql);
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return list;

	}

	public boolean saveTimeGratherConfigList(String objectId,
			String objectType, List timeGratherConfigList) {
		try {
			String sql = "delete from nms_timegratherconfig where objectId='"
					+ objectId + "' and objectType='" + objectType + "'";
			SysLogger.info(sql);
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			Iterator iterator = timeGratherConfigList.iterator();
			while (iterator.hasNext()) {
				TimeGratherConfig timeGratherConfig = (TimeGratherConfig) iterator
						.next();
				sql = "insert into nms_timegratherconfig(objectid,objecttype,begintime,endtime) values ('"
						+ timeGratherConfig.getObjectId()
						+ "','"
						+ timeGratherConfig.getObjectType()
						+ "','"
						+ timeGratherConfig.getBeginTime()
						+ "','"
						+ timeGratherConfig.getEndTime() + "')";
				SysLogger.info(sql);
				try {
					conn.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		return true;

	}

	public boolean deleteTimeGratherConfigByObject(String objectId,
			String objectType) {
		try {
			String sql = "delete from nms_timegratherconfig where objectId='"
					+ objectId + "' and objectType='" + objectType + "'";
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return true;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		TimeGratherConfig timeGratherConfig = new TimeGratherConfig();
		try {
			timeGratherConfig.setId(rs.getInt("id"));
			timeGratherConfig.setObjectId(rs.getString("objectid"));
			timeGratherConfig.setObjectType(rs.getString("objecttype"));
			timeGratherConfig.setBeginTime(rs.getString("begintime"));
			timeGratherConfig.setEndTime(rs.getString("endtime"));
		} catch (Exception e) {
			SysLogger.error("Error in TimeGratherConfigDao.loadFromRS()", e);
			e.printStackTrace();
			timeGratherConfig = null;
		}
		return timeGratherConfig;
	}

}
