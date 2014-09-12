package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SysUtil;
import com.afunms.config.model.GatherTelnetConfig;

public class GatherTelnetConfigDao extends BaseDao implements DaoInterface{
    public  GatherTelnetConfigDao() {
		super("sys_gather_telnetconfig");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		GatherTelnetConfig vo=new GatherTelnetConfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setTelnetIps(rs.getString("telnetIps"));
			vo.setCommands(rs.getString("commands"));
			vo.setCreate_time(rs.getString("create_time"));
			vo.setStatus(rs.getInt("status"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		GatherTelnetConfig config=(GatherTelnetConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into sys_gather_telnetconfig(telnetIps,commands,create_time,status) values('");
		sql.append(config.getTelnetIps());
		sql.append("','");
		sql.append(config.getCommands());
		sql.append("','");
		sql.append(config.getCreate_time());
		sql.append("',");
		sql.append(config.getStatus());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		GatherTelnetConfig config=(GatherTelnetConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update sys_gather_telnetconfig set telnetIps='");
		sql.append(config.getTelnetIps());
		sql.append("',commands='");
		sql.append(config.getCommands());
		sql.append("',create_time='");
		sql.append(config.getCreate_time());
		sql.append("',status=");
		sql.append(config.getStatus());
		sql.append(" where id=").append(config.getId());
		return saveOrUpdate(sql.toString());
	}
	public boolean updateStatus(int id,int status) {
		StringBuffer sql=new StringBuffer();
		sql.append("update sys_gather_telnetconfig set status=");
		sql.append(status);
		sql.append(" where id=").append(id);
		return saveOrUpdate(sql.toString());
	}
public void createTable(String ips){
	CreateTableManager ctable = new CreateTableManager();
	String[] ip=ips.split(",");
	for (int i = 0; i < ip.length; i++) {
		if(ip[i]==null||ip[i].trim().length()<7)continue;
		String allipstr = SysUtil.doip(ip[i]);
		ctable.createTelnetTable(conn,"baseinfo",allipstr);
		ctable.createTelnetTable(conn,"interfacepolicy",allipstr);
		ctable.createTelnetTable(conn,"queueinfo",allipstr);
	}
	
}
public void dropTable(String ips){
	CreateTableManager ctable = new CreateTableManager();

	String[] ip=ips.split(",");
	for (int i = 0; i < ip.length; i++) {
		if(ip[i]==null||ip[i].trim().length()<7)continue;
		String allipstr = SysUtil.doip(ip[i]);
		ctable.dropRootTable(conn,"baseinfo",allipstr);
		ctable.dropRootTable(conn,"interfacepolicy",allipstr);
		ctable.dropRootTable(conn,"queueinfo",allipstr);
	}
	
}
public void exeBatch() {
	conn.executeBatch();
}
}
