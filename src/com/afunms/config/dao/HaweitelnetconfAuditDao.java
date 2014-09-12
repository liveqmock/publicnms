package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.HaweitelnetconfAudit;

public class HaweitelnetconfAuditDao extends BaseDao implements DaoInterface {

    public HaweitelnetconfAuditDao() {
        super("nms_passwordaudit");
    }

    public BaseVo loadFromRS(ResultSet rs) {
        HaweitelnetconfAudit vo = new HaweitelnetconfAudit();
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

        HaweitelnetconfAudit vo = (HaweitelnetconfAudit) baseVo;
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
        HaweitelnetconfAudit vo = (HaweitelnetconfAudit) basevo;
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
