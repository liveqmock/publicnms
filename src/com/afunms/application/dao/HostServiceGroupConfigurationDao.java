/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.SmsConfig;
import com.afunms.system.model.TimeShareConfig;
import com.afunms.system.model.User;

public class HostServiceGroupConfigurationDao extends BaseDao implements DaoInterface {
	public HostServiceGroupConfigurationDao() {
		super("nms_host_service_group_config");
	}

	public List getHostServiceGroupConfigurationByGroupId(String groupId) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_host_service_group_config where group_id='"
					+ groupId + "'";
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

	public boolean saveHostServiceGroupConfigurationList(String gourpId , List hostServiceGroupConfigurationList) {
		try {
			String sql = "";
			sql = "delete from nms_host_service_group_config where group_id='" + gourpId + "'";
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Iterator iterator = hostServiceGroupConfigurationList.iterator();
			while (iterator.hasNext()) {
				HostServiceGroupConfiguration HostServiceGroupConfiguration = (HostServiceGroupConfiguration) iterator
						.next();
				sql = "insert into nms_host_service_group_config(group_id,name,status) values ('" 
						+ gourpId
						+ "','"
						+ HostServiceGroupConfiguration.getName()
						+ "','"
						+ HostServiceGroupConfiguration.getStatus() + "')";
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

	public boolean deleteByGroupId(String groupId) {
		try {
			String sql = "";
			sql = "delete from nms_host_service_group_config where group_id='" + groupId + "'";
			//System.out.println(sql);
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
		HostServiceGroupConfiguration HostServiceGroupConfiguration = new HostServiceGroupConfiguration();
		try {

			HostServiceGroupConfiguration.setId(rs.getInt("id"));
			HostServiceGroupConfiguration.setName(rs.getString("name"));
			HostServiceGroupConfiguration.setGroupId(rs.getString("group_id"));
			HostServiceGroupConfiguration.setStatus(rs.getString("status"));
		} catch (Exception ex) {
			SysLogger.error("Error in TimeShareConfigDAO.loadFromRS()", ex);
			ex.printStackTrace();
			HostServiceGroupConfiguration = null;
		}
		return HostServiceGroupConfiguration;
	}
}
