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
import com.afunms.topology.model.SystemValueForAS400;


public class SystemValueForAS400Dao extends BaseDao implements DaoInterface
{
	public SystemValueForAS400Dao(){
		super("nms_as400_system_value");	   	  
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		SystemValueForAS400 systemValueForAS400 = new SystemValueForAS400();
		try {
//			systemValueForAS400.setId(rs.getInt("id"));
			systemValueForAS400.setNodeid(rs.getString("nodeid"));
			systemValueForAS400.setIpaddress(rs.getString("ipaddress"));
			systemValueForAS400.setCategory(rs.getString("category"));
			systemValueForAS400.setValue(rs.getString("value"));
			systemValueForAS400.setUnit(rs.getString("unit"));
			systemValueForAS400.setDescription(rs.getString("description"));
			systemValueForAS400.setCollectTime(rs.getString("collect_time"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return systemValueForAS400;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		SystemValueForAS400 systemValueForAS400 = (SystemValueForAS400)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_as400_system_value(nodeid,ipaddress,category,value,unit,description,collect_time) values('");
		sql.append(systemValueForAS400.getNodeid());
		sql.append("','");
		sql.append(systemValueForAS400.getIpaddress());
		sql.append("','");
		sql.append(systemValueForAS400.getCategory());
		sql.append("','");
		sql.append(systemValueForAS400.getValue());
		sql.append("','");
		sql.append(systemValueForAS400.getUnit());
		sql.append("','");
		sql.append(systemValueForAS400.getDescription());
		sql.append("','");
		sql.append(systemValueForAS400.getCollectTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List<SystemValueForAS400> systemValueForAS400List) {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			if(systemValueForAS400List != null){
				for(int i = 0 ; i < systemValueForAS400List.size(); i++){
					SystemValueForAS400 systemValueForAS400 = systemValueForAS400List.get(i); 
					StringBuffer sql = new StringBuffer();
					sql.append("insert into nms_as400_system_value(nodeid,ipaddress,category,value,unit,description,collect_time) values('");
					sql.append(systemValueForAS400.getNodeid());
					sql.append("','");
					sql.append(systemValueForAS400.getIpaddress());
					sql.append("','");
					sql.append(systemValueForAS400.getCategory());
					sql.append("','");
					sql.append(systemValueForAS400.getValue());
					sql.append("','");
					sql.append(systemValueForAS400.getUnit());
					sql.append("','");
					sql.append(systemValueForAS400.getDescription());
					sql.append("','");
					sql.append(systemValueForAS400.getCollectTime());
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
		String sql = "delete from nms_as400_system_value where nodeid='" + nodeid + "'";
		return saveOrUpdate(sql);
	}
	
	public List findByNodeid(String nodeid) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_system_value where nodeid='" + nodeid + "'";
		return findByCriteria(sql);
	}
	
	public List findByNodeidAndPath(String nodeid , String subsystem) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_system_value where nodeid='" + nodeid + "' and subsystem='" + subsystem + "'";
		return findByCriteria(sql);
	}
   
   
}
