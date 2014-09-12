package com.afunms.automation.dao;


import java.sql.ResultSet;

import com.afunms.automation.model.NetCfgFileAudit;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class NetCfgFileAuditDao extends BaseDao implements DaoInterface {

    public NetCfgFileAuditDao() {
        super("nms_passwordaudit");
    }

    public BaseVo loadFromRS(ResultSet rs) {
    	NetCfgFileAudit vo = new NetCfgFileAudit();
        try {
            vo.setId(rs.getInt("id"));
            vo.setIp(rs.getString("ip"));
            vo.setUserid(rs.getInt("userid"));
            vo.setUsername(rs.getString("username"));
            vo.setOldpassword(rs.getString("oldpassword"));
            vo.setNewpassword(rs.getString("newpassword"));
            vo.setDotime(rs.getTimestamp("dotime"));
            vo.setBak(rs.getString("bak"));
        } catch (Exception e) {
            SysLogger.error("HaweitelnetconfAuditDao.loadFromRS()", e);
            vo = null;
        }
        return vo;
    }

    public boolean update(BaseVo baseVo) {

    	NetCfgFileAudit vo = (NetCfgFileAudit) baseVo;
        StringBuffer sql = new StringBuffer();
        sql.append("update nms_passwordaudit set username ='");
        sql.append(vo.getUsername());
        sql.append("',oldpassword='");
        sql.append(vo.getOldpassword());
        sql.append("',newpassword='");
        sql.append(vo.getNewpassword());

        sql.append("' where ip = '" + vo.getIp());
        sql.append("'");
        return saveOrUpdate(sql.toString());
    }

    public boolean save(BaseVo basevo) {
    	NetCfgFileAudit vo = (NetCfgFileAudit) basevo;
        StringBuffer sql = new StringBuffer();
        sql.append("insert into nms_passwordaudit(id,ip,userid,username,oldpassword,newpassword,bak) values(");
        sql.append(vo.getId());
        sql.append(",'");
        sql.append(vo.getIp());
        sql.append("','");
        sql.append(vo.getUserid());
        sql.append("','");

        sql.append(vo.getUsername() == null ? "" : vo.getUsername());
        sql.append("','");
        sql.append(vo.getOldpassword() == null ? "" : vo.getOldpassword());
        sql.append("','");
        sql.append(vo.getNewpassword() == null ? "" : vo.getNewpassword());
        sql.append("','");
        sql.append(vo.getBak() == null ? "" : vo.getBak());
        sql.append("')");
        return saveOrUpdate(sql.toString());
    }


}
