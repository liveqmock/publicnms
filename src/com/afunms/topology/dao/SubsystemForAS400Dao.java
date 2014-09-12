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
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemPoolForAS400;
import com.afunms.topology.model.SystemValueForAS400;


public class SubsystemForAS400Dao extends BaseDao implements DaoInterface
{
	public SubsystemForAS400Dao(){
		super("nms_as400_subsystem");	   	  
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		SubsystemForAS400 subsystemForAS400 = new SubsystemForAS400();
		try {
//			systemValueForAS400.setId(rs.getInt("id"));
			subsystemForAS400.setNodeid(rs.getString("nodeid"));
			subsystemForAS400.setIpaddress(rs.getString("ipaddress"));
			subsystemForAS400.setName(rs.getString("name"));
			subsystemForAS400.setCurrentActiveJobs(rs.getString("current_active_jobs"));
			subsystemForAS400.setExists(rs.getString("is_exists"));
			subsystemForAS400.setPath(rs.getString("path"));
			subsystemForAS400.setObjectDescription(rs.getString("object_description"));
			subsystemForAS400.setCollectTime(rs.getString("collectTime"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return subsystemForAS400;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_as400_subsystem(nodeid,ipaddress," +
				"name,current_active_jobs,is_exists,path,object_description,collect_time) values('");
		sql.append(subsystemForAS400.getNodeid());
		sql.append("','");
		sql.append(subsystemForAS400.getIpaddress());
		sql.append("','");
		sql.append(subsystemForAS400.getName());
		sql.append("','");
		sql.append(subsystemForAS400.getCurrentActiveJobs());
		sql.append("','");
		sql.append(subsystemForAS400.getExists());
		sql.append("','");
		sql.append(subsystemForAS400.getPath());
		sql.append("','");
		sql.append(subsystemForAS400.getObjectDescription());
		sql.append("','");
		sql.append(subsystemForAS400.getCollectTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List<SubsystemForAS400> subsystemForAS400List) {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			if(subsystemForAS400List != null){
				for(int i = 0 ; i < subsystemForAS400List.size(); i++){
					SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)subsystemForAS400List.get(i);
					StringBuffer sql = new StringBuffer();
					sql.append("insert into nms_as400_subsystem(nodeid,ipaddress," +
							"name,current_active_jobs,is_exists,path,object_description,collect_time) values('");
					sql.append(subsystemForAS400.getNodeid());
					sql.append("','");
					sql.append(subsystemForAS400.getIpaddress());
					sql.append("','");
					sql.append(subsystemForAS400.getName());
					sql.append("','");
					sql.append(subsystemForAS400.getCurrentActiveJobs());
					sql.append("','");
					sql.append(subsystemForAS400.getExists());
					sql.append("','");
					sql.append(subsystemForAS400.getPath());
					sql.append("','");
					sql.append(subsystemForAS400.getObjectDescription());
					sql.append("','");
					sql.append(subsystemForAS400.getCollectTime());
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
		String sql = "delete from nms_as400_subsystem where nodeid='" + nodeid + "'";
		return saveOrUpdate(sql);
	}
	
	public List findByNodeid(String nodeid) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_subsystem where nodeid='" + nodeid + "'";
		return findByCriteria(sql);
	}
	
   
}
