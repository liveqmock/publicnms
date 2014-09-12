package com.afunms.automation.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.automation.model.StrategyIp;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class StrategyIpDao extends BaseDao implements DaoInterface{
   public  StrategyIpDao() {
	super("nms_comp_strategy_device");
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		StrategyIp vo=new StrategyIp();
		try {
			vo.setId(rs.getInt("ID"));
			vo.setStrategyId(rs.getInt("STRATEGY_ID"));
			vo.setStrategyName(rs.getString("STRATEGY_NAME"));
			vo.setIp(rs.getString("IP"));
			vo.setDeviceType(rs.getString("DEVICE_TYPE"));
			vo.setAvailability(rs.getInt("AVAILABILITY"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		StrategyIp strategyIp=(StrategyIp)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_comp_strategy_device(STRATEGY_ID,IP,DEVICE_TYPE) values(");
		sql.append(strategyIp.getStrategyId());
		sql.append(",'");
		sql.append(strategyIp.getIp());
		sql.append("','");
		sql.append(strategyIp.getDeviceType());
		sql.append("'");
		return false;
	}
	public boolean saveBatch(String[] ips,int id) {
		StringBuffer sql=null;
		try {
			
		
			String sqlString="delete from nms_comp_strategy_device where STRATEGY_ID="+id;
			conn.addBatch(sqlString);
			sqlString="delete from nms_comp_check_results where STRATEGY_ID="+id;
			conn.addBatch(sqlString);
			if(ips!=null){
		for (int i = 0; i < ips.length; i++) {
			 sql=new StringBuffer();
			 sql.append("insert into nms_comp_strategy_device(STRATEGY_ID,IP,DEVICE_TYPE,AVAILABILITY) values(");
				sql.append(id);
				sql.append(",'");
				sql.append(ips[i]);
				sql.append("','");
				sql.append("");
				sql.append("',");
				sql.append(1);//默认所有设备的配置文件可用
				sql.append(")");
				conn.addBatch(sql.toString());
		    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			conn.executeBatch();
			conn.close();
		}
	
		
		return true;
	}

	public boolean update(BaseVo vo) {
		StringBuffer sql=new StringBuffer();
		StrategyIp strategyIp=(StrategyIp)vo;
		sql.append("update nms_comp_strategy_device set STRATEGY_NAME='");
		sql.append(strategyIp.getStrategyName());
		sql.append("',AVAILABILITY=");
		sql.append(strategyIp.getAvailability());
		sql.append("where IP='");
		sql.append(strategyIp.getIp());
		sql.append("' and STRATEGY_ID=");
		sql.append(strategyIp.getStrategyId());
		return saveOrUpdate(sql.toString());
	}
	
  public  List<String>  findIps(int id) {
	  List<String> ipList=new ArrayList<String>();
	  try {
	  rs=conn.executeQuery("select * from nms_comp_strategy_device where STRATEGY_ID="  + id);
		while (rs.next()) {
			ipList.add(rs.getString("IP"));
		}
	  } catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn!=null) {
				conn.close();
			}
		}
	return ipList;
}
 
}
