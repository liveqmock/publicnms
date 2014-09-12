package com.afunms.automation.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.automation.model.TimingBackupCfgFile;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

/**
 * 
 * @descrition 定时备份DAO
 * @author wangxiangyong
 * @date Aug 29, 2014 8:07:37 AM
 */
public class TimingBackupCfgFileDao extends BaseDao implements DaoInterface {
	public TimingBackupCfgFileDao(){
		super("sys_timingbackup_telnetconfig");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		TimingBackupCfgFile timingBackupCfgFile = new TimingBackupCfgFile();
		try {
			timingBackupCfgFile.setBackup_date(Integer.parseInt(rs.getString("BACKUP_DATE")));
			timingBackupCfgFile.setBackup_day_stop(rs.getString("BACKUP_DAY_STOP"));
			timingBackupCfgFile.setBackup_month_stop(rs.getString("BACKUP_MONTH_STOP"));
			timingBackupCfgFile.setBackup_season_stop(rs.getString("BACKUP_SEASON_STOP"));
			timingBackupCfgFile.setBackup_sendfrequency(Integer.parseInt(rs.getString("BACKUP_SENDFREQUENCY")));
			timingBackupCfgFile.setBackup_time_day(rs.getString("BACKUP_TIME_DAY"));
			timingBackupCfgFile.setBackup_time_hou(rs.getString("BACKUP_TIME_HOU"));
			timingBackupCfgFile.setBackup_time_month(rs.getString("BACKUP_TIME_MONTH"));
			timingBackupCfgFile.setBackup_time_week(rs.getString("BACKUP_TIME_WEEK"));
			timingBackupCfgFile.setBackup_type(rs.getString("BACKUP_TYPE"));
			timingBackupCfgFile.setBackup_week_stop(rs.getString("BACKUP_WEEK_STOP"));
			timingBackupCfgFile.setBackup_year_stop(rs.getString("BACKUP_YEAR_STOP"));
			timingBackupCfgFile.setId(Integer.parseInt(rs.getString("id")));
			timingBackupCfgFile.setStatus(rs.getString("status"));
			timingBackupCfgFile.setTelnetconfigids(rs.getString("telnetconfigips"));
			timingBackupCfgFile.setBkpType(rs.getString("bkpType"));
			timingBackupCfgFile.setContent(rs.getString("content"));
			timingBackupCfgFile.setCheckupdateflag(rs.getString("checkupdateflag"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return timingBackupCfgFile;
	}
	public boolean save(BaseVo vo) {
		TimingBackupCfgFile timingBackupCfgFile = (TimingBackupCfgFile)vo;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("insert into sys_timingbackup_telnetconfig(telnetconfigips,BACKUP_TYPE,");
		sqlBuffer.append("BACKUP_DATE,BACKUP_SENDFREQUENCY,BACKUP_TIME_MONTH,BACKUP_TIME_WEEK,BACKUP_TIME_DAY,");
		sqlBuffer.append("BACKUP_TIME_HOU,BACKUP_DAY_STOP,BACKUP_WEEK_STOP,BACKUP_MONTH_STOP,BACKUP_SEASON_STOP,");
		sqlBuffer.append("BACKUP_YEAR_STOP,status,bkpType,content,checkupdateflag) values ('");
		sqlBuffer.append(timingBackupCfgFile.getTelnetconfigips());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_type());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_date());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_sendfrequency());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_month());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_week());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_day());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_hou());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_day_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_week_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_month_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_season_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBackup_year_stop());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getStatus());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getBkpType());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getContent());
		sqlBuffer.append("','");
		sqlBuffer.append(timingBackupCfgFile.getCheckupdateflag());
		sqlBuffer.append("')");
		System.out.println(sqlBuffer.toString());
		return saveOrUpdate(sqlBuffer.toString());
	}
	
	public boolean update(BaseVo vo) {
		TimingBackupCfgFile timingBackupCfgFile = (TimingBackupCfgFile)vo;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update sys_timingbackup_telnetconfig set telnetconfigips = '");
		sqlBuffer.append(timingBackupCfgFile.getTelnetconfigips());
		sqlBuffer.append("',BACKUP_TYPE = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_type());
//		sqlBuffer.append("',BACKUP_DATE = '");
//		sqlBuffer.append(timingBackupCfgFile.getBackup_date());
		sqlBuffer.append("',BACKUP_SENDFREQUENCY = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_sendfrequency());
		sqlBuffer.append("',BACKUP_TIME_MONTH = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_month());
		sqlBuffer.append("',BACKUP_TIME_WEEK = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_week());
		sqlBuffer.append("',BACKUP_TIME_DAY = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_day());
		sqlBuffer.append("',BACKUP_TIME_HOU = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_time_hou());
		sqlBuffer.append("',BACKUP_DAY_STOP = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_day_stop());
		sqlBuffer.append("',BACKUP_WEEK_STOP = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_week_stop());
		sqlBuffer.append("',BACKUP_MONTH_STOP = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_month_stop());
		sqlBuffer.append("',BACKUP_SEASON_STOP = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_season_stop());
		sqlBuffer.append("',BACKUP_YEAR_STOP = '");
		sqlBuffer.append(timingBackupCfgFile.getBackup_year_stop());
		sqlBuffer.append("',status = '");
		sqlBuffer.append(timingBackupCfgFile.getStatus());
		sqlBuffer.append("',bkpType = '");
		sqlBuffer.append(timingBackupCfgFile.getBkpType());
		sqlBuffer.append("',content = '");
		sqlBuffer.append(timingBackupCfgFile.getContent());
		sqlBuffer.append("',checkupdateflag = '");
		sqlBuffer.append(timingBackupCfgFile.getCheckupdateflag());
		sqlBuffer.append("' where id = '");
		sqlBuffer.append(timingBackupCfgFile.getId());
		sqlBuffer.append("'");
		SysLogger.info(sqlBuffer.toString());
		return saveOrUpdate(sqlBuffer.toString());
	}
	
	/**
	 * 根据id删除这条记录
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteById(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from sys_timingbackup_telnetconfig where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("TimingBackupCfgFileDao.delete()", e);
		} 
		return result;
	}
	
	/**
	 * 得到所有的备份配置文件的记录
	 * 
	 * @return
	 */
	public List getAlList(){
		String sqlString = "select * from sys_timingbackup_telnetconfig where bkpType!='0'";
		return findByCriteria(sqlString);
	}
	
	public List getFileList(){
		String sqlString = "select * from sys_timingbackup_telnetconfig where bkpType='0'";
		return findByCriteria(sqlString);
	}
	public List getAllList(){
		String sqlString = "select * from sys_timingbackup_telnetconfig";
		return findByCriteria(sqlString);
	}
	/**
	 * 更新定时备份的状态
	 * @param status
	 * @param id
	 * @return
	 */
	public boolean updateStatus(String status,String id){
		StringBuffer sql = new StringBuffer();
		sql.append("update sys_timingbackup_telnetconfig set status = '");
		sql.append(status);
		sql.append("' where id = '");
		sql.append(id);
		sql.append("'");
		boolean result = false;
		try {
			conn.executeUpdate(sql.toString()); 
			result = true;
		} catch (Exception e) {
			SysLogger.error("TimingBackupCfgFileDao.updateStatus(String status,String id)", e);
		} 
		return result;
	}
}
