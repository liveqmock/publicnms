package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.AclBase;

public class AclBaseDao extends BaseDao implements DaoInterface{
  public  AclBaseDao() {
	super("sys_gather_aclbase");
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		AclBase base=new AclBase();
		try {
			base.setId(rs.getInt("id"));
			base.setIpaddres(rs.getString("ipaddress"));
			base.setName(rs.getString("name"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return base;
	}

	public boolean save(BaseVo vo) {
		AclBase base=(AclBase)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into sys_gather_aclbase(id,ipaddress,name) values('");
		sql.append(base.getIpaddress());
		sql.append("','");
		sql.append(base.getName());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public synchronized int getNextID() {
		// TODO Auto-generated method stub
		return super.getNextID();
	}
public Vector<String> getNames(String ip) {
	Vector<String> vector=new Vector<String>();
	String sql="select * from sys_gather_aclbase where ipaddress='"+ip+"'";
	rs=conn.executeQuery(sql);
	try {
		while (rs.next()) {
			String name=rs.getString("name");
			vector.add(name);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}finally{
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	return vector;
}
public HashMap<String, Integer> getKeyVal(String ip){
	HashMap<String, Integer> keyVal=new HashMap<String, Integer>();
	String sql="select * from sys_gather_aclbase where ipaddress='"+ip+"'";
	rs=conn.executeQuery(sql);
	try {
		while (rs.next()) {
			int id=rs.getInt("id");
			String name=rs.getString("name");
			keyVal.put(name, id);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return keyVal;
}
public HashMap<Integer, String> getDataByIp(String ip){
	HashMap<Integer, String> keyVal=new HashMap<Integer, String>();
	String sql="select * from sys_gather_aclbase where ipaddress='"+ip+"'";
	rs=conn.executeQuery(sql);
	try {
		while (rs.next()) {
			int id=rs.getInt("id");
			String name=rs.getString("name");
			keyVal.put(id, name);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return keyVal;
}
}
