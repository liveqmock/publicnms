package com.afunms.automation.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.automation.model.PasswdTimingConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class PasswdTimingConfigDao extends BaseDao implements DaoInterface {
    
    public PasswdTimingConfigDao(){
        super("sys_pwdbackup_telnetconfig");
    }
    
    public boolean save(BaseVo vo) {
        PasswdTimingConfig passwdTimingBackupTelnetConfig = (PasswdTimingConfig)vo;
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("insert into sys_pwdbackup_telnetconfig(telnetconfigips,warntype,BACKUP_FILENAME,BACKUP_TYPE,");
        sqlBuffer.append("BACKUP_DATE,BACKUP_SENDFREQUENCY,BACKUP_TIME_MONTH,BACKUP_TIME_WEEK,BACKUP_TIME_DAY,");
        sqlBuffer.append("BACKUP_TIME_HOU,BACKUP_DAY_STOP,BACKUP_WEEK_STOP,BACKUP_MONTH_STOP,BACKUP_SEASON_STOP,");
        sqlBuffer.append("BACKUP_YEAR_STOP,status) values ('");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getTelnetconfigips());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getWarntype());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_filename());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_type());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_date());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_sendfrequency());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_month());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_week());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_day());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_hou());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_day_stop());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_week_stop());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_month_stop());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_season_stop());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_year_stop());
        sqlBuffer.append("','");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getStatus());
        sqlBuffer.append("')");
        System.out.println(sqlBuffer.toString());
        return saveOrUpdate(sqlBuffer.toString());
    }

    public boolean update(BaseVo vo) {
        PasswdTimingConfig passwdTimingBackupTelnetConfig = (PasswdTimingConfig)vo;
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("update sys_pwdbackup_telnetconfig set telnetconfigips = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getTelnetconfigips());
        sqlBuffer.append("',warntype = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getWarntype());
        sqlBuffer.append("',BACKUP_FILENAME = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_filename());
        sqlBuffer.append("',BACKUP_TYPE = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_type());
        sqlBuffer.append("',BACKUP_SENDFREQUENCY = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_sendfrequency());
        sqlBuffer.append("',BACKUP_TIME_MONTH = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_month());
        sqlBuffer.append("',BACKUP_TIME_WEEK = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_week());
        sqlBuffer.append("',BACKUP_TIME_DAY = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_day());
        sqlBuffer.append("',BACKUP_TIME_HOU = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_time_hou());
        sqlBuffer.append("',BACKUP_DAY_STOP = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_day_stop());
        sqlBuffer.append("',BACKUP_WEEK_STOP = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_week_stop());
        sqlBuffer.append("',BACKUP_MONTH_STOP = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_month_stop());
        sqlBuffer.append("',BACKUP_SEASON_STOP = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_season_stop());
        sqlBuffer.append("',BACKUP_YEAR_STOP = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getBackup_year_stop());
        sqlBuffer.append("',status = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getStatus());
        sqlBuffer.append("' where id = '");
        sqlBuffer.append(passwdTimingBackupTelnetConfig.getId());
        sqlBuffer.append("'");
        SysLogger.info(sqlBuffer.toString());
        return saveOrUpdate(sqlBuffer.toString());
    }

    public BaseVo loadFromRS(ResultSet rs) {
        PasswdTimingConfig passwdTimingBackupTelnetConfig = new PasswdTimingConfig();
        try {
            passwdTimingBackupTelnetConfig.setBackup_date(Integer.parseInt(rs.getString("BACKUP_DATE")));
            passwdTimingBackupTelnetConfig.setBackup_day_stop(rs.getString("BACKUP_DAY_STOP"));
            passwdTimingBackupTelnetConfig.setBackup_month_stop(rs.getString("BACKUP_MONTH_STOP"));
            passwdTimingBackupTelnetConfig.setBackup_season_stop(rs.getString("BACKUP_SEASON_STOP"));
            passwdTimingBackupTelnetConfig.setBackup_sendfrequency(rs.getString("BACKUP_SENDFREQUENCY"));
            passwdTimingBackupTelnetConfig.setBackup_time_day(rs.getString("BACKUP_TIME_DAY"));
            passwdTimingBackupTelnetConfig.setBackup_time_hou(rs.getString("BACKUP_TIME_HOU"));
            passwdTimingBackupTelnetConfig.setBackup_time_month(rs.getString("BACKUP_TIME_MONTH"));
            passwdTimingBackupTelnetConfig.setBackup_time_week(rs.getString("BACKUP_TIME_WEEK"));
            passwdTimingBackupTelnetConfig.setBackup_type(rs.getString("BACKUP_TYPE"));
            passwdTimingBackupTelnetConfig.setBackup_week_stop(rs.getString("BACKUP_WEEK_STOP"));
            passwdTimingBackupTelnetConfig.setBackup_year_stop(rs.getString("BACKUP_YEAR_STOP"));
            passwdTimingBackupTelnetConfig.setId(Integer.parseInt(rs.getString("id")));
            passwdTimingBackupTelnetConfig.setStatus(rs.getString("status"));
            passwdTimingBackupTelnetConfig.setTelnetconfigids(rs.getString("telnetconfigips"));
            passwdTimingBackupTelnetConfig.setBackup_filename(rs.getString("backup_filename"));
            passwdTimingBackupTelnetConfig.setWarntype(rs.getString("warntype"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwdTimingBackupTelnetConfig;
    }

    public List<PasswdTimingConfig> getAlList() {
        String sqlString = "select * from sys_pwdbackup_telnetconfig";
        return findByCriteria(sqlString);
    }
    
    public boolean delete(String id) {
        boolean result = false;
        try {
            conn.addBatch("delete from sys_pwdbackup_telnetconfig where id=" + id);
            conn.executeBatch();
            result = true;
        } catch (Exception e) {
            SysLogger.error("PasswdTimingBackupTelnetConfigDao.delete()", e);
        } 
        return result;
    }

    public boolean updateStatus(String status,String id){
        StringBuffer sql = new StringBuffer();
        sql.append("update sys_pwdbackup_telnetconfig set status = '");
        sql.append(status);
        sql.append("' where id = '");
        sql.append(id);
        sql.append("'");
        boolean result = false;
        try {
            conn.executeUpdate(sql.toString()); 
            result = true;
        } catch (Exception e) {
            SysLogger.error("PasswdTimingBackupTelnetConfigDao.updateStatus(String status,String id)", e);
        } 
        return result;
    }
}
