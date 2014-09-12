package com.afunms.device.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.device.model.Cabinet;

public class CabinetDao extends BaseDao implements DaoInterface {
public  CabinetDao() {
	super("its_device_cabinet");
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Cabinet vo=new Cabinet();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCity(rs.getString("city"));
			vo.setRoadInfo(rs.getString("roadInfo"));
			vo.setAffiliation(rs.getString("affiliation"));
			vo.setBelong(rs.getString("belong"));
			vo.setLatitude(rs.getString("latitude"));
			vo.setLongitude(rs.getString("longitude"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setRackNumber(rs.getString("rackNumber"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	@Override
	public boolean save(BaseVo vo) {
		Cabinet cabinet=(Cabinet) vo;
     StringBuffer sql = new StringBuffer(100);
		
		sql.append("insert into its_device_cabinet(id,city,roadInfo,affiliation,belong,latitude,longitude,ipaddress,rackNumber)values(");
		sql.append(cabinet.getId());
		sql.append(",'");
		sql.append(cabinet.getCity());
		sql.append("','");
		sql.append(cabinet.getRoadInfo());
		sql.append("','");
		sql.append(cabinet.getAffiliation());
		sql.append("','");
		sql.append(cabinet.getBelong());
		sql.append("','");
		sql.append(cabinet.getLatitude());
		sql.append("','");
		sql.append(cabinet.getLongitude());
		sql.append("','");
		sql.append(cabinet.getIpaddress());
		sql.append("','");
		sql.append(cabinet.getRackNumber());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	@Override
	public boolean update(BaseVo vo) {
		Cabinet cabinet=(Cabinet) vo;
		StringBuffer sql = new StringBuffer(100);
		
		sql.append("update its_device_cabinet set city='");
		sql.append(cabinet.getCity());
		sql.append("',roadInfo='");
		sql.append(cabinet.getRoadInfo());
		sql.append("',affiliation='");
		sql.append(cabinet.getAffiliation());
		sql.append("',belong='");
		sql.append(cabinet.getBelong());
		sql.append("',latitude='");
		sql.append(cabinet.getLatitude());
		sql.append("',longitude='");
		sql.append(cabinet.getLongitude());
		sql.append("',ipaddress='");
		sql.append(cabinet.getIpaddress());
		sql.append("',rackNumber='");
		sql.append(cabinet.getRackNumber());
		sql.append("' where id=");
		sql.append(cabinet.getId());
		System.out.println("==="+sql.toString());
		return saveOrUpdate(sql.toString());
	}
	public int  getNextId() {
		int id=getNextID();
		return id;
	}
}
