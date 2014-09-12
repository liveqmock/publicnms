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
import com.afunms.topology.model.SystemPoolForAS400;
import com.afunms.topology.model.SystemValueForAS400;


public class SystemPoolForAS400Dao extends BaseDao implements DaoInterface
{
	public SystemPoolForAS400Dao(){
		super("nms_as400_system_pool");	   	  
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		SystemPoolForAS400 systemPoolForAS400 = new SystemPoolForAS400();
		try {
//			systemValueForAS400.setId(rs.getInt("id"));
			systemPoolForAS400.setNodeid(rs.getString("nodeid"));
			systemPoolForAS400.setIpaddress(rs.getString("ipaddress"));
			systemPoolForAS400.setSystemPool(rs.getString("system_pool"));
			systemPoolForAS400.setName(rs.getString("name"));
			systemPoolForAS400.setSize(rs.getString("sizes"));
			systemPoolForAS400.setReservedSize(rs.getString("reserved_size"));
			systemPoolForAS400.setMaximumActiveThreads(rs.getString("maximum_active_threads"));
			systemPoolForAS400.setCollectTime(rs.getString("collect_time"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return systemPoolForAS400;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		SystemPoolForAS400 systemPoolForAS400 = (SystemPoolForAS400)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_as400_system_pool(nodeid,ipaddress,system_pool,name,sizes,reserved_size,maximum_active_threads,collect_time) values('");
		sql.append(systemPoolForAS400.getNodeid());
		sql.append("','");
		sql.append(systemPoolForAS400.getIpaddress());
		sql.append("','");
		sql.append(systemPoolForAS400.getSystemPool());
		sql.append("','");
		sql.append(systemPoolForAS400.getName());
		sql.append("','");
		sql.append(systemPoolForAS400.getSize());
		sql.append("','");
		sql.append(systemPoolForAS400.getReservedSize());
		sql.append("','");
		sql.append(systemPoolForAS400.getMaximumActiveThreads());
		sql.append("','");
		sql.append(systemPoolForAS400.getCollectTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List<SystemPoolForAS400> systemPoolForAS400List) {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			if(systemPoolForAS400List != null){
				for(int i = 0 ; i < systemPoolForAS400List.size(); i++){
					SystemPoolForAS400 systemPoolForAS400 = (SystemPoolForAS400)systemPoolForAS400List.get(i);
					StringBuffer sql = new StringBuffer();
					sql.append("insert into nms_as400_system_pool(nodeid,ipaddress,system_pool,name,sizes,reserved_size,maximum_active_threads,collect_time) values('");
					sql.append(systemPoolForAS400.getNodeid());
					sql.append("','");
					sql.append(systemPoolForAS400.getIpaddress());
					sql.append("','");
					sql.append(systemPoolForAS400.getSystemPool());
					sql.append("','");
					sql.append(systemPoolForAS400.getName());
					sql.append("','");
					sql.append(systemPoolForAS400.getSize());
					sql.append("','");
					sql.append(systemPoolForAS400.getReservedSize());
					sql.append("','");
					sql.append(systemPoolForAS400.getMaximumActiveThreads());
					sql.append("','");
					sql.append(systemPoolForAS400.getCollectTime());
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
		String sql = "delete from nms_as400_system_pool where nodeid='" + nodeid + "'";
		return saveOrUpdate(sql);
	}
	
	public List findByNodeid(String nodeid) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_system_pool where nodeid='" + nodeid + "'";
		return findByCriteria(sql);
	}
	
   
}
