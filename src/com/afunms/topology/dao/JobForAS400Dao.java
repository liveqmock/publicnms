/**
 * <p>Description:与nodedao都是操作表nms_topo_node,但nodedao主要用于发现</p>
 * <p>Description:而toponodedao主要用于页面操作</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.topology.model.JobForAS400;


public class JobForAS400Dao extends BaseDao implements DaoInterface
{
	public JobForAS400Dao(){
		super("nms_as400_job");	   	  
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		JobForAS400 jobForAS400 = new JobForAS400();
		try {
//			jobForAS400.setId(rs.getInt("id"));
			jobForAS400.setName(rs.getString("name"));
			jobForAS400.setSubsystem(rs.getString("subsystem"));
			jobForAS400.setStatus(rs.getString("status"));
			jobForAS400.setType(rs.getString("type"));
			jobForAS400.setSubtype(rs.getString("subtype"));
			jobForAS400.setCPUUsedTime(rs.getString("cpu_used_time"));
			jobForAS400.setActiveStatus(rs.getString("active_status"));
			jobForAS400.setUser(rs.getString("user"));
			jobForAS400.setNodeid(rs.getString("nodeid"));
			jobForAS400.setIpaddress(rs.getString("ipaddress"));
			jobForAS400.setCollectTime(rs.getString("collect_time"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobForAS400;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		JobForAS400 jobForAS400 = (JobForAS400)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_as400_job(nodeid,ipaddress,name,subsystem,status," +
				"active_status,type,subtype,cpu_used_time,user,collect_time) values('");
		sql.append(jobForAS400.getNodeid());
		sql.append("','");
		sql.append(jobForAS400.getIpaddress());
		sql.append("','");
		sql.append(jobForAS400.getName());
		sql.append("','");
		sql.append(jobForAS400.getSubsystem());
		sql.append("','");
		sql.append(jobForAS400.getStatus());
		sql.append("','");
		sql.append(jobForAS400.getActiveStatus());
		sql.append("','");
		sql.append(jobForAS400.getType());
		sql.append("','");
		sql.append(jobForAS400.getSubtype());
		sql.append("','");
		sql.append(jobForAS400.getCPUUsedTime());
		sql.append("','");
		sql.append(jobForAS400.getUser());
		sql.append("','");
		sql.append(jobForAS400.getCollectTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List<JobForAS400> jobList) {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			if(jobList != null){
				for(int i = 0 ; i < jobList.size(); i++){
					JobForAS400 jobForAS400 = jobList.get(i); 
					StringBuffer sql = new StringBuffer();
					sql.append("insert into nms_as400_job(nodeid,ipaddress,name,subsystem,status," +
							"active_status,type,subtype,cpu_used_time,user,collect_time) values('");
					sql.append(jobForAS400.getNodeid());
					sql.append("','");
					sql.append(jobForAS400.getIpaddress());
					sql.append("','");
					sql.append(jobForAS400.getName());
					sql.append("','");
					sql.append(jobForAS400.getSubsystem());
					sql.append("','");
					sql.append(jobForAS400.getStatus());
					sql.append("','");
					sql.append(jobForAS400.getActiveStatus());
					sql.append("','");
					sql.append(jobForAS400.getType());
					sql.append("','");
					sql.append(jobForAS400.getSubtype());
					sql.append("','");
					sql.append(jobForAS400.getCPUUsedTime());
					sql.append("','");
					sql.append(jobForAS400.getUser());
					sql.append("','");
					sql.append(jobForAS400.getCollectTime());
					sql.append("')");
					conn.addBatch(sql.toString());
				}
				conn.executeBatch();
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} finally {
			if(conn != null){
				conn.close();
			}
		}
		return result;
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean deleteByNodeid(String nodeid) {
		// TODO Auto-generated method stub
		String sql = "delete from nms_as400_job where nodeid='" + nodeid + "'";
		return saveOrUpdate(sql);
	}
	
	public List findByNodeid(String nodeid) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_job where nodeid='" + nodeid + "'";
		return findByCriteria(sql);
	}
	
	public List findByNodeidAndPath(String nodeid , String subsystem) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_job where nodeid='" + nodeid + "' and subsystem='" + subsystem + "'";
		return findByCriteria(sql);
	}
	
	public List getJobForAS400ListInfo(String nodeid, String jobType, String jobSubtype, String jobActivestatus, String jobSubsystem) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_as400_job where nodeid='" + nodeid + "'");
		if(jobType != null && jobType.trim().length() > 0 && !"-1".equals(jobType) && !"null".equals(jobType)){
			sql.append(" and jobtype='" + jobType + "'");
		}
		
		if(jobSubtype != null && jobSubtype.trim().length() > 0 && !"-1".equals(jobSubtype) && !"null".equals(jobSubtype)){
			sql.append(" and jobsubtype='" + jobSubtype + "'");
		}
		
		if(jobActivestatus != null && jobActivestatus.trim().length() > 0 && !"-1".equals(jobActivestatus) && !"null".equals(jobActivestatus)){
			sql.append(" and active_status='" + jobActivestatus + "'");
		}
		
		if(jobSubsystem != null && jobSubsystem.trim().length() > 0 && !"-1".equals(jobSubsystem) && !"null".equals(jobSubsystem)){
			sql.append(" and subsystem='" + jobSubsystem + "'");
		}
		System.out.println(sql.toString());
		return findByCriteria(sql.toString());
	}
   
   
}
