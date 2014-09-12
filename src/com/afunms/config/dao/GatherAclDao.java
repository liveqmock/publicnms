package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.GatherAcl;

public class GatherAclDao extends BaseDao implements DaoInterface{
   public  GatherAclDao() {
	super("sys_gather_aclList");
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		GatherAcl acl=new GatherAcl();
		try {
			acl.setId(rs.getInt("id"));
			acl.setIpaddress(rs.getString("ipaddress"));
			acl.setCommand(rs.getString("command"));
			acl.setIsMonitor(rs.getInt("isMonitor"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return acl;
	}

	public boolean save(BaseVo vo) {
		GatherAcl acl=(GatherAcl)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into sys_gather_aclList(ipaddress,command,isMonitor) values('");
		sql.append(acl.getIpaddress());
		sql.append("','");
		sql.append(acl.getCommand());
		sql.append("',");
		sql.append(acl.getIsMonitor());
		
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
   public boolean deleteByIp(String ip) {
	String sql="delete from sys_gather_aclList where ipaddress='"+ip+"'";
	return saveOrUpdate(sql);
}
}
