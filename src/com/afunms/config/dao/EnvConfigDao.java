package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.EnvConfig;
/**
 * @description µÁ‘¥∏ÊæØ≈‰÷√Dao
 * @author wangxiangyong
 * @date Dec 28, 2011
 */
public class EnvConfigDao   extends BaseDao implements DaoInterface{
	  public EnvConfigDao(){
		  super("system_envconfig");
	  }
		@Override
		public BaseVo loadFromRS(ResultSet rs) {
			EnvConfig envConfig=new EnvConfig();
			try {
				envConfig.setId(rs.getInt("id"));
				envConfig.setIpaddress(rs.getString("ipaddress"));
				envConfig.setName(rs.getString("name"));
				envConfig.setAlarmvalue(rs.getInt("alarmvalue"));
				envConfig.setAlarmlevel(rs.getString("alarmlevel"));
				envConfig.setAlarmtimes(rs.getInt("alarmtimes"));
				envConfig.setEntity(rs.getString("entity"));
				envConfig.setEnabled(rs.getInt("enabled"));
				envConfig.setBak(rs.getString("bak"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return envConfig;
		}

		public boolean save(BaseVo vo) {
			EnvConfig config=(EnvConfig)vo;
			StringBuffer sql = new StringBuffer(100);
			sql.append("insert into system_envconfig(ipaddress,name,alarmvalue,alarmlevel,alarmtimes,entity,enbaled,bak)values(");
			sql.append("'");
			sql.append(config.getIpaddress());	
			sql.append("','");
			sql.append(config.getName());
			sql.append("',");
			sql.append(config.getAlarmvalue());
			sql.append(",'");
			sql.append(config.getAlarmlevel());
			sql.append(",");
			sql.append(config.getAlarmtimes());
			sql.append(",'");
			sql.append(config.getEntity());
			sql.append("',");
			sql.append(config.getEnabled());
			sql.append(",'");
			sql.append(config.getBak());
			sql.append("')");
			return saveOrUpdate(sql.toString());
		}

		public boolean update(BaseVo vo) {
			boolean result = false;
			EnvConfig config=(EnvConfig)vo;
		     StringBuffer sql = new StringBuffer();
		        sql.append("update system_envconfig set ipaddress='");
				sql.append(config.getIpaddress());
				sql.append("',name='");
				sql.append(config.getName());	
				sql.append("',alarmvalue=");
				sql.append(config.getAlarmvalue());
				sql.append(",alarmlevel='");
				sql.append(config.getAlarmlevel());
				sql.append(",alarmtimes=");
				sql.append(config.getAlarmtimes());
				sql.append(",entity='");
				sql.append(config.getEntity());
				sql.append("',enbaled=");
				sql.append(config.getEnabled());
				sql.append(",bak='");
				sql.append(config.getBak());
		        sql.append("' where id=");
		        sql.append(config.getId());
		        return saveOrUpdate(sql.toString());
		}
		public List loadByIpaddress(String ip)
	  	{
	  		List list = new ArrayList();
	  		try
	  		{
	  			rs = conn.executeQuery("select * from system_envconfig where ipaddress='"+ip+"'");
	  			while(rs.next())
	  				list.add(loadFromRS(rs)); 
	  		}
	  		catch(Exception e)
	  		{
	  			SysLogger.error("EnvConfigDao:loadAll()",e);
	  			list = null;
	  		}
	  		finally
	  		{
	  			conn.close();
	  		}
	  		return list;
	  	}
		public boolean updateValue(int id,int value,int times) {
			 StringBuffer sql = new StringBuffer();
		        sql.append("update system_envconfig set alarmvalue=");
		        sql.append(value);
		        sql.append(",alarmtimes=");
		        sql.append(times);
		        sql.append(" where id=");
		        sql.append(id);
		        return saveOrUpdate(sql.toString());
		}
		public boolean updateEnabled(int id,int enable) {
			 StringBuffer sql = new StringBuffer();
		        sql.append("update system_envconfig set enabled=");
		        sql.append(enable);
		        sql.append(" where id=");
		        sql.append(id);
		        return saveOrUpdate(sql.toString());
		}
		public boolean updateAlarmlevelByID(String id,String alarmlevel) {
			StringBuffer sql = new StringBuffer();
			sql.append("update system_envconfig set alarmlevel='");
			sql.append(alarmlevel);
			sql.append("' where id=");
			sql.append(id);
			return saveOrUpdate(sql.toString());
		}
		
		}
