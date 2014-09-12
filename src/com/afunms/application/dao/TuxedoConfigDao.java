/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.NetworkUtil;


public class TuxedoConfigDao extends BaseDao implements DaoInterface {

	public TuxedoConfigDao() {
		super("nms_tuxedoconfig");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		
		TuxedoConfig tuxedoConfig = new TuxedoConfig();
		try {
			tuxedoConfig.setId(rs.getInt("id"));
			tuxedoConfig.setName(rs.getString("name"));
			tuxedoConfig.setIpAddress(rs.getString("ipaddress"));
			tuxedoConfig.setCommunity(rs.getString("community"));
			tuxedoConfig.setPort(rs.getString("port"));
			tuxedoConfig.setStatus(rs.getString("status"));
			tuxedoConfig.setMon_flag(rs.getString("mon_flag"));
			tuxedoConfig.setBid(rs.getString("bid"));
			tuxedoConfig.setSendemail(rs.getString("sendemail"));
			tuxedoConfig.setSendmobiles(rs.getString("sendmobiles"));
			tuxedoConfig.setSendphone(rs.getString("sendphone"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tuxedoConfig;
	}
	
	public List findByMonFlag(String mon_flag){
		String sql = "select * from nms_tuxedoconfig where mon_flag='" + mon_flag + "'";
		return findByCriteria(sql);
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		TuxedoConfig tuxedoConfig = (TuxedoConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_tuxedoconfig(id,name,ipaddress,community,port,status,mon_flag,bid,sendphone,sendemail,sendmobiles) values('");
		sql.append(tuxedoConfig.getId());
		sql.append("','");
		sql.append(tuxedoConfig.getName());
		sql.append("','");
		sql.append(tuxedoConfig.getIpAddress());
		sql.append("','");
		sql.append(tuxedoConfig.getCommunity());
		sql.append("','");
		sql.append(tuxedoConfig.getPort());
		sql.append("','");
		sql.append(tuxedoConfig.getStatus());
		sql.append("','");
		sql.append(tuxedoConfig.getMon_flag());
		sql.append("','");
		sql.append(tuxedoConfig.getBid());
		sql.append("','");
		sql.append(tuxedoConfig.getSendphone());
		sql.append("','");
		sql.append(tuxedoConfig.getSendemail());
		sql.append("','");
		sql.append(tuxedoConfig.getSendmobiles());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		
		TuxedoConfig tuxedoConfig = (TuxedoConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_tuxedoconfig set name='");
		sql.append(tuxedoConfig.getName());
		sql.append("',ipaddress='");
		sql.append(tuxedoConfig.getIpAddress());
		sql.append("',community=");
		sql.append(tuxedoConfig.getCommunity());
		sql.append(",port='");
		sql.append(tuxedoConfig.getPort());
		sql.append("',status='");
		sql.append(tuxedoConfig.getStatus());
		sql.append("',mon_flag='");
		sql.append(tuxedoConfig.getMon_flag());
		sql.append("',bid='");
		sql.append(tuxedoConfig.getBid());
		sql.append("',sendphone='");
	   	sql.append(tuxedoConfig.getSendphone());
	   	sql.append("',sendemail='");
	   	sql.append(tuxedoConfig.getSendemail());
	   	sql.append("',sendmobiles='");
	   	sql.append(tuxedoConfig.getSendmobiles());
	   	sql.append("' where id=");
	   	sql.append(tuxedoConfig.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean updateMon_flagById(String id , String mon_flag){
		String sql = "update nms_tuxedoconfig set mon_flag='" + mon_flag + "' where id='" + id + "'";
		return saveOrUpdate(sql);
	}
	

}