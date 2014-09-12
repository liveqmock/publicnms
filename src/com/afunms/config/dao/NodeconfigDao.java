/**
 * <p>Description: Nodeconfig</p>
 * <p>Company:dhcc.com</p>
 * @date 2009-12-14
 */

package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Nodeconfig;


public class NodeconfigDao extends BaseDao implements DaoInterface {
	
	
	public NodeconfigDao() {
		super("nms_nodeconfig");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		Nodeconfig nodeconfig = new Nodeconfig();
		try {
			if(rs!=null){
				nodeconfig.setId(rs.getInt("id"));
				nodeconfig.setNodeid(rs.getInt("nodeid"));
				nodeconfig.setHostname(rs.getString("hostname"));
				nodeconfig.setSysname(rs.getString("sysname"));
				nodeconfig.setSerialNumber(rs.getString("serialNumber"));
				nodeconfig.setCSDVersion(rs.getString("cSDVersion"));
				nodeconfig.setNumberOfProcessors(rs.getString("numberOfProcessors"));
				nodeconfig.setMac(rs.getString("mac"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeconfig;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		Nodeconfig nodeconfig = (Nodeconfig)vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_nodeconfig(nodeid,hostname,sysname,serialNumber,cSDVersion," +
				"numberOfProcessors,mac)values(");
		sql.append("'");
		sql.append(nodeconfig.getNodeid());
		sql.append("','");
		sql.append(nodeconfig.getHostname());	
		sql.append("','");
		sql.append(nodeconfig.getSysname());
		sql.append("','");
		sql.append(nodeconfig.getSerialNumber());
		sql.append("','");
		sql.append(nodeconfig.getCSDVersion());
		sql.append("','");
		sql.append(nodeconfig.getNumberOfProcessors());
		sql.append("','");
		sql.append(nodeconfig.getMac());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		Nodeconfig nodeconfig = (Nodeconfig)vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("update nms_nodeconfig set nodeid='");
		sql.append(nodeconfig.getNodeid());
		sql.append("',hostname='");
		sql.append(nodeconfig.getHostname());
		sql.append("',sysname='");
		sql.append(nodeconfig.getSysname());
		sql.append("',serialNumber='");
		sql.append(nodeconfig.getSerialNumber());
		sql.append("',cSDVersion='");
		sql.append(nodeconfig.getCSDVersion());
		sql.append("',numberOfProcessors='");
		sql.append(nodeconfig.getNumberOfProcessors());
		sql.append("',mac='");
		sql.append(nodeconfig.getMac());
		sql.append("'where id=");
		sql.append(nodeconfig.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean delete(String id){
		boolean result = false;
		try{
			conn.addBatch("delete from nms_nodeconfig where id=" + id);
			conn.executeBatch();
			result = true;
	   }
	   catch(Exception ex){
		    SysLogger.error("NodeconfigDao.delete()",ex);
	        result = false;
	   }finally{
		    conn.close();
	   }
	   return result;
	}	
	
	/**
	 * 根据nodeid得到nodeconfig
	 * @param nodeid
	 * @return
	 */
	public Nodeconfig getByNodeID(String nodeid){
		Nodeconfig nodeconfig = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_nodeconfig where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("'");
		try {
			rs = conn.executeQuery(sqlBuffer.toString());
			if(rs.next()){
				nodeconfig = (Nodeconfig)loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs != null){
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return nodeconfig;
	}
}
