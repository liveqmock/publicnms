package com.afunms.config.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Vpn;

/**
 * vpnÍØÆËÍ¼Dao
 * @description TODO
 * @author wangxiangyong
 * @date Feb 8, 2012 4:39:15 PM
 */
public class VpnDao extends BaseDao implements DaoInterface {
	public VpnDao() {
		super("topo_vpn_link");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Vpn vpn = new Vpn();
		try {
			vpn.setId(rs.getInt("id"));
			vpn.setSourceIp(rs.getString("source_ip"));
			vpn.setSourceId(rs.getInt("source_id"));
			vpn.setSourcePortName(rs.getString("sourceport_name"));
			vpn.setSourcePortIndex(rs.getString("sourceport_index"));
			vpn.setDesIp(rs.getString("des_ip"));
			vpn.setDesId(rs.getInt("des_id"));
			vpn.setDesPortName(rs.getString("desport_name"));
			vpn.setDesPortIndex(rs.getString("desport_index"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vpn;
	}

	public boolean save(BaseVo vo) {
		Vpn vpn = (Vpn) vo;
		StringBuffer sqlBuffer = new StringBuffer();
		try {
			sqlBuffer.append("insert into topo_vpn_link(id,source_ip,source_id,sourceport_name,sourceport_index,des_ip,des_id,desport_name,desport_index) values(");
			sqlBuffer.append(getNextID());
			sqlBuffer.append(",'");
			sqlBuffer.append(vpn.getSourceIp());
			sqlBuffer.append("',");
			sqlBuffer.append(vpn.getSourceId());
			sqlBuffer.append(",'");
			sqlBuffer.append(vpn.getSourcePortName());
			sqlBuffer.append("','");
			sqlBuffer.append(vpn.getSourcePortIndex());
			sqlBuffer.append("','");
			sqlBuffer.append(vpn.getDesIp());
			sqlBuffer.append("',");
			sqlBuffer.append(vpn.getDesId());
			sqlBuffer.append(",'");
			sqlBuffer.append(vpn.getDesPortName());
			sqlBuffer.append("','");
			sqlBuffer.append(vpn.getDesPortIndex());
			sqlBuffer.append("')");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return saveOrUpdate(sqlBuffer.toString());
	}

	public boolean update(BaseVo vo) {
		Vpn vpn = (Vpn) vo;
		StringBuffer sqlBuffer = new StringBuffer();
		try {
			sqlBuffer.append("update topo_vpn_link ");
			sqlBuffer.append("set source_ip='");
			sqlBuffer.append(vpn.getSourceIp());
			sqlBuffer.append("',source_id=");
			sqlBuffer.append(vpn.getSourceId());
			sqlBuffer.append(",sourceport_name='");
			sqlBuffer.append(vpn.getSourcePortName());
			sqlBuffer.append("',sourceport_index='");
			sqlBuffer.append(vpn.getSourcePortIndex());
			sqlBuffer.append("',des_ip='");
			sqlBuffer.append(vpn.getDesIp());
			sqlBuffer.append("',des_id=");
			sqlBuffer.append(vpn.getDesId());
			sqlBuffer.append(",desport_name='");
			sqlBuffer.append(vpn.getDesPortName());
			sqlBuffer.append("',desport_index='");
			sqlBuffer.append(vpn.getDesPortIndex());
			sqlBuffer.append("' where id=");
			sqlBuffer.append(vpn.getId());

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(sqlBuffer.toString());
		return saveOrUpdate(sqlBuffer.toString());
	}
	 public boolean delete(String[] id)
	   {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<id.length;i++){
		         conn.addBatch("delete from topo_vpn_link where id=" + id[i]);
		       }
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("BaseDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
}
