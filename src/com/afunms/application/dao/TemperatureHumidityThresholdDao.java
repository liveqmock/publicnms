

/*
 *	此类为 TemperatureHumidityThresholdConfig 类的dao类 对表 system_temperature_humidity_threshold 进行操作
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.TemperatureHumidityThresholdConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;



public class TemperatureHumidityThresholdDao extends BaseDao implements DaoInterface {
	
	
	public TemperatureHumidityThresholdDao() {
		super("system_temper_humi_thresh");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = new TemperatureHumidityThresholdConfig();
		try {
			temperatureHumidityThresholdConfig.setId(rs.getInt("id"));
			temperatureHumidityThresholdConfig.setNode_id(rs.getString("node_id"));
			temperatureHumidityThresholdConfig.setMinTemperature(rs.getString("min_temperature"));
			temperatureHumidityThresholdConfig.setMaxTemperature(rs.getString("max_temperature"));
			temperatureHumidityThresholdConfig.setMinHumidity(rs.getString("min_humidity"));
			temperatureHumidityThresholdConfig.setMaxHumidity(rs.getString("max_humidity"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
		return temperatureHumidityThresholdConfig;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = (TemperatureHumidityThresholdConfig)vo;	   
		StringBuffer sql = new StringBuffer();
		sql.append("insert into system_temper_humi_thresh(id,node_id,min_temperature,max_temperature," +
				"min_humidity,max_humidity)values(");
		sql.append(temperatureHumidityThresholdConfig.getId());
		sql.append(",'");
		sql.append(temperatureHumidityThresholdConfig.getNode_id());
		sql.append("','");
		sql.append(temperatureHumidityThresholdConfig.getMinTemperature());   
		sql.append("','");
		sql.append(temperatureHumidityThresholdConfig.getMaxTemperature());   
		sql.append("','");
		sql.append(temperatureHumidityThresholdConfig.getMinHumidity());   
		sql.append("','");
		sql.append(temperatureHumidityThresholdConfig.getMaxHumidity());   
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		
		if(vo == null){
			return false;
		}
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = (TemperatureHumidityThresholdConfig)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_temper_humi_thresh set node_id='");
		sql.append(temperatureHumidityThresholdConfig.getNode_id());
		sql.append("',min_temperature='");
		sql.append(temperatureHumidityThresholdConfig.getMinTemperature());
		sql.append("',max_temperature=");
		sql.append(temperatureHumidityThresholdConfig.getMaxTemperature());
		sql.append(",min_humidity='");
		sql.append(temperatureHumidityThresholdConfig.getMinHumidity());
	   	sql.append("',max_humidity='");
	   	sql.append(temperatureHumidityThresholdConfig.getMaxHumidity());
	   	sql.append("' where id=");
	   	sql.append(temperatureHumidityThresholdConfig.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public TemperatureHumidityThresholdConfig findByNodeId(String nodeId){
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = null;
		String sql = "select * from system_temper_humi_thresh where node_id='" 
			         + nodeId +"'";
		List list = findByCriteria(sql);
		if(list != null && list.size() > 0){
			 temperatureHumidityThresholdConfig 
				= (TemperatureHumidityThresholdConfig)list.get(0);
		}
		return temperatureHumidityThresholdConfig;
	}
	
	public boolean deleteByNodeId(String nodeId){
		String sql = "delete from system_temper_humi_thresh where node_id='" + nodeId+"'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
