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

import com.afunms.application.model.JobForAS400GroupDetail;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.SmsConfig;
import com.afunms.system.model.TimeShareConfig;
import com.afunms.system.model.User;

public class JobForAS400GroupDetailDao extends BaseDao implements DaoInterface {
	public JobForAS400GroupDetailDao() {
		super("nms_as400_job_group_detail");
	}

	public List getJobForAS400GroupDetailByGroupId(String groupId) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_as400_job_group_detail where group_id='"
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

	public boolean saveJobForAS400GroupDetailList(String gourpId , List list) {
		try {
			String sql = "";
			sql = "delete from nms_as400_job_group_detail where group_id='" + gourpId + "'";
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				JobForAS400GroupDetail jobForAS400GroupDetail = (JobForAS400GroupDetail) iterator
						.next();
				sql = "insert into nms_as400_job_group_detail(group_id,name,num,status,active_status,active_status_type) values ('"
						+ gourpId
						+ "','"
						+ jobForAS400GroupDetail.getName()
						+ "','"
						+ jobForAS400GroupDetail.getNum()
						+ "','"
						+ jobForAS400GroupDetail.getStatus()
						+ "','"
						+ jobForAS400GroupDetail.getActiveStatus() 
						+ "','"
						+ jobForAS400GroupDetail.getActiveStatusType()
						+ "')";
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
			sql = "delete from nms_as400_job_group_detail where group_id='" + groupId + "'";
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
		JobForAS400GroupDetail jobForAS400GroupDetail = new JobForAS400GroupDetail();
		try {

			jobForAS400GroupDetail.setId(rs.getInt("id"));
			jobForAS400GroupDetail.setName(rs.getString("name"));
			jobForAS400GroupDetail.setNum(rs.getString("num"));
			jobForAS400GroupDetail.setGroupId(rs.getString("group_id"));
			jobForAS400GroupDetail.setStatus(rs.getString("status"));
			jobForAS400GroupDetail.setActiveStatus(rs.getString("active_status"));
			jobForAS400GroupDetail.setActiveStatusType(rs.getString("active_status_type"));
		} catch (Exception ex) {
			SysLogger.error("Error in TimeShareConfigDAO.loadFromRS()", ex);
			ex.printStackTrace();
			jobForAS400GroupDetail = null;
		}
		return jobForAS400GroupDetail;
	}
}
