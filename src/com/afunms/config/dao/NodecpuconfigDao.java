/**
 * <p>Description: Nodecpuconfig</p>
 * <p>Company:dhcc.com</p>
 * @date 2009-12-14
 */


package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Nodecpuconfig;


public class NodecpuconfigDao extends BaseDao implements DaoInterface {
	
	
	public NodecpuconfigDao() {
		super("nms_nodecpuconfig");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
		try {
			if(rs!=null){
				nodecpuconfig.setId(rs.getInt("id"));
				nodecpuconfig.setNodeid(rs.getInt("nodeid"));
				nodecpuconfig.setDataWidth(rs.getString("dataWidth"));
				nodecpuconfig.setProcessorId(rs.getString("processorId"));
				nodecpuconfig.setName(rs.getString("name"));
				nodecpuconfig.setL2CacheSize(rs.getString("l2CacheSize"));
				nodecpuconfig.setL2CacheSpeed(rs.getString("l2CacheSpeed"));
				nodecpuconfig.setProcessorSpeed(rs.getString("processorSpeed"));
				nodecpuconfig.setProcessorType(rs.getString("processorType"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodecpuconfig;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		Nodecpuconfig nodecpuconfig = (Nodecpuconfig)vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_nodecpuconfig(nodeid,dataWidth,processorId,name,l2CacheSize," +
				"l2CacheSpeed,processorSpeed,processorType)values(");
		sql.append("'");
		sql.append(nodecpuconfig.getNodeid());
		sql.append("','");
		sql.append(nodecpuconfig.getDataWidth());	
		sql.append("','");
		sql.append(nodecpuconfig.getProcessorId());
		sql.append("','");
		sql.append(nodecpuconfig.getName());
		sql.append("','");
		sql.append(nodecpuconfig.getL2CacheSize());
		sql.append("','");
		sql.append(nodecpuconfig.getL2CacheSpeed());
		sql.append("','");
		sql.append(nodecpuconfig.getProcessorSpeed());
		sql.append("','");
		sql.append(nodecpuconfig.getProcessorType());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		Nodecpuconfig nodecpuconfig = (Nodecpuconfig)vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("update nms_nodecpuconfig set nodeid='");
		sql.append(nodecpuconfig.getNodeid());
		sql.append("',dataWidth='");
		sql.append(nodecpuconfig.getDataWidth());
		sql.append("',processorId='");
		sql.append(nodecpuconfig.getProcessorId());
		sql.append("',name='");
		sql.append(nodecpuconfig.getName());
		sql.append("',l2CacheSize='");
		sql.append(nodecpuconfig.getL2CacheSize());
		sql.append("',l2CacheSpeed='");
		sql.append(nodecpuconfig.getL2CacheSpeed());
		sql.append("'where id=");
		sql.append(nodecpuconfig.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean delete(String id){
		boolean result = false;
		try{
			conn.addBatch("delete from nms_nodecpuconfig where id=" + id);
			conn.executeBatch();
			result = true;
	   }
	   catch(Exception ex){
		    SysLogger.error("NodecpuconfigDao.delete()",ex);
	        result = false;
	   }finally{
		    conn.close();
	   }
	   return result;
	}	
	
	/**
	 * 取出Cpu配置信息
	 * @param nodeid
	 * @return
	 */
	public List getNodecpuconfig(String nodeid){
		List retList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_nodecpuconfig where nodeid = '");
		sql.append(nodeid);
		sql.append("'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				BaseVo baseVo = loadFromRS(rs);
				retList.add(baseVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dbManager.close();
		}
		return retList;
	}
}
