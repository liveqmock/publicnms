package com.afunms.application.dao;

import java.sql.ResultSet;

import com.afunms.application.model.UpAndDownLog;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

//create table machine_up_and_down_log(id int,ip_address varchar(30),action int,oper_time datetime,oper_user varchar(20))
public class UpAndDownLogDao extends BaseDao implements DaoInterface 
{

	public UpAndDownLogDao()
	{
		super("nms_machineoperatelog");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		UpAndDownLog log = new UpAndDownLog();
		try{
			log.setId(rs.getInt("id"));
			log.setIp_address(rs.getString("ip_address"));
			log.setAction(rs.getInt("action"));
			log.setOper_time(rs.getTimestamp("oper_time"));
			log.setOper_user(rs.getString("oper_user"));
			log.setOperid(rs.getString("oper_id"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return log;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		UpAndDownLog log = (UpAndDownLog)vo;
		StringBuffer sql = new StringBuffer("insert into nms_machineoperatelog(id,ip_address,action,oper_time,oper_user,oper_id) values(");
		sql.append(this.getNextID());
		sql.append(",'");
		sql.append(log.getIp_address());
		sql.append("',");
		sql.append(log.getAction());
		sql.append(",'");
		sql.append(log.getOper_time());
		sql.append("','");
		sql.append(log.getOper_user());
		sql.append("','");
		sql.append(log.getOperid());
		sql.append("')");
		return this.saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
