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
import com.afunms.topology.model.DiskForAS400;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemPoolForAS400;
import com.afunms.topology.model.SystemValueForAS400;


public class DiskForAS400Dao extends BaseDao implements DaoInterface
{
	public DiskForAS400Dao(){
		super("nms_as400_disk");	   	  
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		DiskForAS400 diskForAS400 = new DiskForAS400();
		try {
//			systemValueForAS400.setId(rs.getInt("id"));
			diskForAS400.setNodeid(rs.getString("nodeid"));
			diskForAS400.setIpaddress(rs.getString("ipaddress"));
			diskForAS400.setUnit(rs.getString("unit"));
			diskForAS400.setType(rs.getString("type"));;
			diskForAS400.setSize(rs.getString("sizes"));
			diskForAS400.setUsed(rs.getString("used"));
			diskForAS400.setIoRqs(rs.getString("io_rqs"));
			diskForAS400.setRequestSize(rs.getString("request_size"));
			diskForAS400.setReadRqs(rs.getString("read_rqs"));
			diskForAS400.setWriteRqs(rs.getString("write_rqs"));
			diskForAS400.setRead(rs.getString("read"));
			diskForAS400.setWrite(rs.getString("write"));
			diskForAS400.setBusy(rs.getString("busy"));
			diskForAS400.setCollectTime(rs.getString("collect_time"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return diskForAS400;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		DiskForAS400 diskForAS400 = (DiskForAS400)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into `nms_as400_disk` (`nodeid`,`ipaddress`,`unit`,`type`,`sizes`,`used`,`io_rqs`,`request_size`,`read_rqs`,`write_rqs`,`read`,`write`,`busy`,`collect_time`) values('");
		sql.append(diskForAS400.getNodeid());
		sql.append("','");
		sql.append(diskForAS400.getIpaddress());
		sql.append("','");
		sql.append(diskForAS400.getUnit());
		sql.append("','");
		sql.append(diskForAS400.getType());
		sql.append("','");
		sql.append(diskForAS400.getSize());
		sql.append("','");
		sql.append(diskForAS400.getUsed());
		sql.append("','");
		sql.append(diskForAS400.getIoRqs());
		sql.append("','");
		sql.append(diskForAS400.getRequestSize());
		sql.append("','");
		sql.append(diskForAS400.getReadRqs());
		sql.append("','");
		sql.append(diskForAS400.getWriteRqs());
		sql.append("','");
		sql.append(diskForAS400.getRead());
		sql.append("','");
		sql.append(diskForAS400.getBusy());
		sql.append("','");
		sql.append(diskForAS400.getCollectTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean save(List<DiskForAS400> diskForAS400List) {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			if(diskForAS400List != null){
				for(int i = 0 ; i < diskForAS400List.size(); i++){
					DiskForAS400 diskForAS400 = (DiskForAS400)diskForAS400List.get(i);
					StringBuffer sql = new StringBuffer();
					sql.append("insert into nms_as400_disk(`nodeid`,`ipaddress`,`unit`,`type`,`sizes`,`used`,`io_rqs`,`request_size`,`read_rqs`,`write_rqs`,`read`,`write`,`busy`,`collect_time`) values('");
					sql.append(diskForAS400.getNodeid());
					sql.append("','");
					sql.append(diskForAS400.getIpaddress());
					sql.append("','");
					sql.append(diskForAS400.getUnit());
					sql.append("','");
					sql.append(diskForAS400.getType());
					sql.append("','");
					sql.append(diskForAS400.getSize());
					sql.append("','");
					sql.append(diskForAS400.getUsed());
					sql.append("','");
					sql.append(diskForAS400.getIoRqs());
					sql.append("','");
					sql.append(diskForAS400.getRequestSize());
					sql.append("','");
					sql.append(diskForAS400.getReadRqs());
					sql.append("','");
					sql.append(diskForAS400.getWriteRqs());
					sql.append("','");
					sql.append(diskForAS400.getRead());
					sql.append("','");
					sql.append(diskForAS400.getWrite());
					sql.append("','");
					sql.append(diskForAS400.getBusy());
					sql.append("','");
					sql.append(diskForAS400.getCollectTime());
					sql.append("')");
//					System.out.println(sql.toString());
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
		String sql = "delete from nms_as400_disk where nodeid='" + nodeid + "'";
		return saveOrUpdate(sql);
	}
	
	public List findByNodeid(String nodeid) {
		// TODO Auto-generated method stub
		String sql = "select * from nms_as400_disk where nodeid='" + nodeid + "'";
		return findByCriteria(sql);
	}
	
   
}
