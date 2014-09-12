/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

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
import com.afunms.system.model.SmsConfig;
import com.afunms.system.model.TimeShareConfig;
import com.afunms.system.model.User;

public class TimeShareConfigDao extends BaseDao implements DaoInterface {
	public TimeShareConfigDao() {
		super("nms_timeshareconfig");
	}

	/**
	 * Use the objectId <code>String</code>, objectType <code>String</code>
	 * to get the list of time-sharing from the Database
	 * 利用objectId <code>String</code> ,objectType <code>String</code> 来获取
	 * 分时列表
	 * @param objectId <code>String</code>
	 * @param objectType <code>String</code>
	 * @return
	 */
	public List getTimeShareConfigByObject(String objectId, String objectType) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_timeshareconfig where objectId='"
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

	public List getTimeShareConfigByObject(String timeShareType,
			String objectId, String objectType) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_timeshareconfig where timeShareType='"
					+ timeShareType + "' and objectId='" + objectId
					+ "' and objectType='" + objectType + "'";
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
	
	/**
	 * Save time-sharing a list of equipment required objectId, objectType, a list of time-sharing
	 * into the database
	 * 保存分时列表
	 * @param objectId <code>String</code>
	 * @param objectType <code>String</code>
	 * @param timeShareConfigList <code>List</code>
	 * @return
	 */
	public boolean saveTimeShareConfigList(String objectId, String objectType,
			List timeShareConfigList) {
		try {
			String sql = "";
			sql = "delete from nms_timeshareconfig where objectId='" + objectId
					+ "' and objectType='" + objectType + "'";
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Iterator iterator = timeShareConfigList.iterator();
			while (iterator.hasNext()) {
				TimeShareConfig timeShareConfig = (TimeShareConfig) iterator
						.next();
				sql = "insert into nms_timeshareconfig(objectid,objecttype,timeShareType,begintime,endtime,userids) values ('"
						+ timeShareConfig.getObjectId()
						+ "','"
						+ timeShareConfig.getObjectType()
						+ "','"
						+ timeShareConfig.getTimeShareType()
						+ "','"
						+ timeShareConfig.getBeginTime()
						+ "','"
						+ timeShareConfig.getEndTime()
						+ "','"
						+ timeShareConfig.getUserIds() + "')";
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

	public boolean saveSmsConfigList(List smsConfigList) {
		try {

			Iterator iterator = smsConfigList.iterator();
			String sql = "";
			while (iterator.hasNext()) {
				SmsConfig smsConfig = (SmsConfig) iterator.next();
				sql = "insert into nms_smsconfig (objectid,objecttype,begintime,endtime,userids) values ('"
						+ smsConfig.getObjectId()
						+ "','"
						+ smsConfig.getObjectType()
						+ "','"
						+ smsConfig.getBeginTime()
						+ "','"
						+ smsConfig.getEndTime()
						+ "','"
						+ smsConfig.getUserIds() + "')";
				try {
					conn.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
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
				}
			}
		}
		return true;
	}
	
	/**
	 * Remove the device list of time-sharing, through the device id and device type
	 * from the database
	 * @param objectId<code>String</code>
	 * @param objectType<code>String</code>
	 * @return
	 */
	public boolean deleteTimeShareConfigListByObject(String objectId,
			String objectType) {
		try {
			String sql = "";
			sql = "delete from nms_timeshareconfig where objectId='" + objectId
					+ "' and objectType='" + objectType + "'";
			System.out.println(sql);
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
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
				}
			}
		}
		return true;
	}

	public boolean save(BaseVo baseVo) {
		return false;
	}

	public int save(User vo) {
		int result = -1;
		String sql = null;
		
		return result;
	}

	public boolean update(BaseVo baseVo) {
		return false;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		TimeShareConfig timeShareConfig = new TimeShareConfig();
		try {

			timeShareConfig.setId(rs.getInt("id"));
			timeShareConfig.setObjectId(rs.getString("objectid"));
			timeShareConfig.setObjectType(rs.getString("objecttype"));
			timeShareConfig.setTimeShareType(rs.getString("timesharetype"));
			timeShareConfig.setBeginTime(rs.getString("begintime"));
			timeShareConfig.setEndTime(rs.getString("endtime"));
			timeShareConfig.setUserIds(rs.getString("userids"));
		} catch (Exception ex) {
			SysLogger.error("Error in TimeShareConfigDAO.loadFromRS()", ex);
			ex.printStackTrace();
			timeShareConfig = null;
		}
		return timeShareConfig;
	}
}
