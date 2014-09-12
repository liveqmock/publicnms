/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.JobForAS400Group;
import com.afunms.application.model.ProcessGroup;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;


public class JobForAS400GroupDao extends BaseDao implements DaoInterface {

	public JobForAS400GroupDao() {
		super("nms_as400_job_group");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		JobForAS400Group jobForAS400Group = new JobForAS400Group();
		try {
			jobForAS400Group.setId(rs.getInt("id"));
			jobForAS400Group.setIpaddress(rs.getString("ipaddress"));
			jobForAS400Group.setName(rs.getString("name"));
			jobForAS400Group.setNodeid(rs.getString("nodeid"));
			jobForAS400Group.setMon_flag(rs.getString("mon_flag"));
			jobForAS400Group.setAlarm_level(rs.getString("alarm_level"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobForAS400Group;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		JobForAS400Group jobForAS400Group = (JobForAS400Group)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_as400_job_group(id,ipaddress,name,nodeid,mon_flag,alarm_level)values('");
		sql.append(jobForAS400Group.getId());
		sql.append("','");
		sql.append(jobForAS400Group.getIpaddress());	
		sql.append("','");
		sql.append(jobForAS400Group.getName());
		sql.append("','");
		sql.append(jobForAS400Group.getNodeid());
		sql.append("','");
		sql.append(jobForAS400Group.getMon_flag());
		sql.append("','");
		sql.append(jobForAS400Group.getAlarm_level());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public List findByIp(String ipaddress){
		String sql = " where ipaddress='" + ipaddress + "'";
		return findByCondition(sql);
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		JobForAS400Group jobForAS400Group = (JobForAS400Group)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_as400_job_group set ipaddress='");
		sql.append(jobForAS400Group.getIpaddress());	
		sql.append("',name='");
		sql.append(jobForAS400Group.getName());
		sql.append("',nodeid='");
		sql.append(jobForAS400Group.getNodeid());
		sql.append("',mon_flag='");
		sql.append(jobForAS400Group.getMon_flag());
		sql.append("',alarm_level='");
		sql.append(jobForAS400Group.getAlarm_level());
		sql.append("' where id=");
		sql.append(jobForAS400Group.getId());
//		System.out.println(sql.toString()+"=======================================");
		return saveOrUpdate(sql.toString());
	}


}