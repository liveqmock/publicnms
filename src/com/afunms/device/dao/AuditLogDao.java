package com.afunms.device.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.device.model.AuditLog;

public class AuditLogDao  extends BaseDao implements DaoInterface {
	public AuditLogDao() {
        super("its_audit_log");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		AuditLog vo=new AuditLog();
		try {
            vo.setId(rs.getInt("id"));
            vo.setIp(rs.getString("ip"));
            vo.setUsername(rs.getString("username"));
            vo.setOperateType(rs.getString("operateType"));
            vo.setDotime(rs.getTimestamp("dotime"));
          // vo.setType(rs.getString("type"));
        } catch (Exception e) {
            SysLogger.error("AuditLogDao.loadFromRS()", e);
            vo = null;
        }
        return vo;
	}

	@Override
	public boolean save(BaseVo basevo) {
		AuditLog vo = (AuditLog) basevo;
	        StringBuffer sql = new StringBuffer();
	        sql.append("insert into its_audit_log(ip,username,operateType) values('");
	        sql.append(vo.getIp());
	        sql.append("','");
	        sql.append(vo.getUsername());
	        sql.append("','");
	        sql.append(vo.getOperateType());
//	        sql.append("','");
//	        sql.append(vo.getDotime());
//	        sql.append("','");
//	        sql.append(vo.getType());
	        sql.append("')");
	        System.out.println("----------:"+sql.toString());
	        return saveOrUpdate(sql.toString());
	}

	@Override
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
