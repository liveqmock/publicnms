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

import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.system.model.SmsConfig;
import com.afunms.system.model.TimeShareConfig;
import com.afunms.system.model.User;

public class ProcessGroupConfigurationDao extends BaseDao implements DaoInterface {
	public ProcessGroupConfigurationDao() {
		super("nms_process_group_config");
	}

	public List getProcessGroupConfigurationByGroupId(String groupId) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_process_group_config where group_id='"
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

	public boolean saveProcessGroupConfigurationList(String gourpId , List processGroupConfigurationList) {
		try {
			String sql = "";
			sql = "delete from nms_process_group_config where group_id='" + gourpId + "'";
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Iterator iterator = processGroupConfigurationList.iterator();
			while (iterator.hasNext()) {
				ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration) iterator
						.next();
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into nms_process_group_config(group_id,name,times,status) values ('"
						+ gourpId
						+ "','"
						+ processGroupConfiguration.getName()
						+ "','"
						+ processGroupConfiguration.getTimes() 
						+ "','"
						+ processGroupConfiguration.getStatus()
						+ "')";
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					sql = "insert into nms_process_group_config(group_id,name,times,status) values ('"
						+ gourpId
						+ "','"
						+ processGroupConfiguration.getName()
						+ "',"
						+ "to_date('"+processGroupConfiguration.getTimes()+"','YYYY-MM-DD HH24:MI:SS')" 
						+ ",'"
						+ processGroupConfiguration.getStatus()
						+ "')";
				}
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
			sql = "delete from nms_process_group_config where group_id='" + groupId + "'";
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
		ProcessGroupConfiguration processGroupConfiguration = new ProcessGroupConfiguration();
		try {

			processGroupConfiguration.setId(rs.getInt("id"));
			processGroupConfiguration.setName(rs.getString("name"));
			processGroupConfiguration.setGroupId(rs.getString("group_id"));
			processGroupConfiguration.setTimes(rs.getString("times"));
			processGroupConfiguration.setStatus(rs.getString("status"));
		} catch (Exception ex) {
			SysLogger.error("Error in TimeShareConfigDAO.loadFromRS()", ex);
			ex.printStackTrace();
			processGroupConfiguration = null;
		}
		return processGroupConfiguration;
	}
}
