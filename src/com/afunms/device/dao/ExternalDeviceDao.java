package com.afunms.device.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.device.model.Cabinet;
import com.afunms.device.model.ExternalDevice;

public class ExternalDeviceDao extends BaseDao implements DaoInterface {
	public  ExternalDeviceDao() {
		super("its_external_device");
	}
		@Override
		public BaseVo loadFromRS(ResultSet rs) {
			ExternalDevice vo=new ExternalDevice();
			try {
				vo.setId(rs.getInt("id"));
				vo.setBrand(rs.getString("brand"));
				
				vo.setDeviceId(rs.getString("deviceId"));
				vo.setDeviceType(rs.getString("deviceType"));
				vo.setInsureder(rs.getString("insureder"));
				vo.setIpaddress(rs.getString("ipaddress"));
				vo.setOsType(rs.getString("osType"));
				vo.setSpecification(rs.getString("specification"));
				vo.setSupplier(rs.getString("supplier"));
				vo.setCabinetId(rs.getInt("cabinetId"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return vo;
		}

		@Override
		public boolean save(BaseVo vo) {
			ExternalDevice device=(ExternalDevice) vo;
	     StringBuffer sql = new StringBuffer(100);
			
			sql.append("insert into its_external_device(id,brand,deviceId,deviceType,insureder,ipaddress,osType,specification,supplier,cabinetId)values(");
			sql.append(device.getId());
			sql.append(",'");
			sql.append(device.getBrand());
			sql.append("','");
			
			sql.append(device.getDeviceId());
			sql.append("','");
			sql.append(device.getDeviceType());
			sql.append("','");
			sql.append(device.getInsureder());
			sql.append("','");
			sql.append(device.getIpaddress());
			sql.append("','");
			sql.append(device.getOsType());
			sql.append("','");
			sql.append(device.getSpecification());
			sql.append("','");
			sql.append(device.getSupplier());
			sql.append("',");
			sql.append(device.getCabinetId());
			
			sql.append(")");
			System.out.println("=========="+sql.toString());
			return saveOrUpdate(sql.toString());
		}

		@Override
		public boolean update(BaseVo vo) {
			ExternalDevice device=(ExternalDevice) vo;
			StringBuffer sql = new StringBuffer(100);
			
			sql.append("update its_external_device set brand='");
			sql.append(device.getBrand());
			sql.append("',deviceId='");
			sql.append(device.getDeviceId());
			sql.append("',deviceType='");
			sql.append(device.getDeviceType());
			sql.append("',insureder='");
			sql.append(device.getInsureder());
			sql.append("',ipaddress='");
			sql.append(device.getIpaddress());
			
			sql.append("',osType='");
			sql.append(device.getOsType());
			sql.append("',specification='");
			sql.append(device.getSpecification());
			sql.append("',supplier='");
			sql.append(device.getSupplier());
			sql.append("',cabinetId=");
			sql.append(device.getCabinetId());
			sql.append(" where id=");
			sql.append(device.getId());
			System.out.println("==="+sql.toString());
			return saveOrUpdate(sql.toString());
		}
public int  getNextId() {
	int id=getNextID();
	return id;
}
public boolean deleteByCabinetId(String id){
	StringBuffer sql = new StringBuffer(100);
	
	sql.append("delete from its_external_device where cabinetId=");
	sql.append(id);
	return saveOrUpdate(sql.toString()); 
}
	}

