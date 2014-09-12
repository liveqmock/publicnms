package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.FtpTransConfig;

public class FtpTransConfigDao extends BaseDao implements DaoInterface {

	public FtpTransConfigDao() {
		super("nms_ftptransconfig");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		FtpTransConfig vo=new FtpTransConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setIp(rs.getString("ip"));
			vo.setUsername(rs.getString("username"));
			vo.setPassword(rs.getString("password"));
			vo.setFlag(rs.getInt("flag"));
		} catch (Exception e) {
			SysLogger.error("Error in TFtpServerDao.loadFromRS()",e);
	        vo = null;
	        e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		FtpTransConfig vo = (FtpTransConfig)baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_ftptransconfig(ip,username,password,flag)values(");
		sql.append("'");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getUsername());
		sql.append("','");
		sql.append(vo.getPassword());
		sql.append("','");
		sql.append(vo.getFlag());
		sql.append("');");
		
		String yj=sql.toString();
		return saveOrUpdate(yj);
	}

	public boolean update(BaseVo baseVo) {
		FtpTransConfig vo = (FtpTransConfig) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_ftptransconfig set ip='");
		sql.append(vo.getIp());
		sql.append("',username='");
		sql.append(vo.getUsername());
		sql.append("',password='");
		sql.append(vo.getPassword());
		sql.append("',flag='");
		sql.append(vo.getFlag());
		sql.append("' where id=");
		sql.append(vo.getId());
		
		String yj=sql.toString();
		return saveOrUpdate(yj);
	}
	
	/**
	 * µÃµ½FtpTransConfig
	 * @return
	 */
	public FtpTransConfig getFtpTransConfig(){
		FtpTransConfig ftpTransConfig = null;
		String sqlString = "select * from nms_ftptransconfig";
		try {
			rs = conn.executeQuery(sqlString);
			if(rs.next()){
				ftpTransConfig = (FtpTransConfig)loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return ftpTransConfig;
	}
	public FtpTransConfig getFtpTransMonitorConfig(){
		FtpTransConfig ftpTransConfig = null;
		String sqlString = "select * from nms_ftptransconfig where flag=1";
		try {
			rs = conn.executeQuery(sqlString);
			if(rs.next()){
				ftpTransConfig = (FtpTransConfig)loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return ftpTransConfig;
	}
}
