/**
 * <p>Description: nms_as400_job_subsystem</p>
 * <p>Company:dhcc.com</p>
 * @author Соѕь
 * @project afunms
 * @date 2011-6-21
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.JobForAS400SubSystem;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;


public class JobForAS400SubSystemDao extends BaseDao implements DaoInterface {

	public JobForAS400SubSystemDao() {
		super("nms_as400_job_subsystem");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		JobForAS400SubSystem jobForAS400SubSystem = new JobForAS400SubSystem();
		try {
			jobForAS400SubSystem.setId(rs.getInt("id"));
			jobForAS400SubSystem.setIpaddress(rs.getString("ipaddress"));
			jobForAS400SubSystem.setName(rs.getString("name"));
			jobForAS400SubSystem.setNodeid(rs.getString("nodeid"));
			jobForAS400SubSystem.setMon_flag(rs.getString("mon_flag"));
			jobForAS400SubSystem.setAlarm_level(rs.getString("alarm_level"));
			jobForAS400SubSystem.setActive_status(rs.getString("active_status"));
			jobForAS400SubSystem.setActive_status_type(rs.getString("active_status_type"));
			jobForAS400SubSystem.setNum(rs.getString("num"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobForAS400SubSystem;
	}

	public boolean save(BaseVo vo) {
		
		JobForAS400SubSystem jobForAS400SubSystem = (JobForAS400SubSystem)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_as400_job_subsystem(id,ipaddress,name,nodeid,mon_flag,alarm_level,num,active_status,active_status_type)values('");
		sql.append(jobForAS400SubSystem.getId());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getIpaddress());	
		sql.append("','");
		sql.append(jobForAS400SubSystem.getName());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getNodeid());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getMon_flag());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getAlarm_level());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getNum());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getActive_status());
		sql.append("','");
		sql.append(jobForAS400SubSystem.getActive_status_type());
		sql.append("')");
		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	public List findByIp(String ipaddress){
		String sql = " where ipaddress='" + ipaddress + "'";
		return findByCondition(sql);
	}

	public boolean update(BaseVo vo) {
		JobForAS400SubSystem jobForAS400SubSystem = (JobForAS400SubSystem)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_as400_job_subsystem set ipaddress='");
		sql.append(jobForAS400SubSystem.getIpaddress());	
		sql.append("',name='");
		sql.append(jobForAS400SubSystem.getName());
		sql.append("',nodeid='");
		sql.append(jobForAS400SubSystem.getNodeid());
		sql.append("',mon_flag='");
		sql.append(jobForAS400SubSystem.getMon_flag());
		sql.append("',alarm_level='");
		sql.append(jobForAS400SubSystem.getAlarm_level());
		sql.append("',num='");
		sql.append(jobForAS400SubSystem.getNum());
		sql.append("',active_status='");
		sql.append(jobForAS400SubSystem.getActive_status());
		sql.append("',active_status_type='");
		sql.append(jobForAS400SubSystem.getActive_status_type());
		sql.append("' where id=");
		sql.append(jobForAS400SubSystem.getId());
//		System.out.println(sql.toString()+"=======================================");
		return saveOrUpdate(sql.toString());
	}


}