package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoServer;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DominoServerDao extends BaseDao implements DaoInterface{
   public DominoServerDao() {
	super("nms_dominoserver_realtime");
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		DominoServer vo=new DominoServer();
		try {
			
			vo.setName(rs.getString("NAME"));
			vo.setTitle(rs.getString("TITLE"));
			vo.setOs(rs.getString("OS"));
			vo.setArchitecture(rs.getString("ARCHITECTURE"));
			vo.setStarttime(rs.getString("STARTTIME"));
			vo.setCputype(rs.getString("CPUTYPE"));
			vo.setCpucount(rs.getString("CPUCOUNT"));
			vo.setPortnumber(rs.getString("PORTNUMBER"));
			vo.setCpupctutil(rs.getString("CPUPCTUTIL"));
			vo.setImapstatus(rs.getString("IMAPSTATUS"));
			vo.setLdapstatus(rs.getString("LDAPSTATUS"));
			vo.setPop3status(rs.getString("POP3STATUS"));
			vo.setSmtpstatus(rs.getString("SMTPSTATUS"));
			vo.setAvailabilityIndex(rs.getString("AVAILABILITYINDEX"));
			vo.setSessionsDropped(rs.getString("SESSIONSDROPPED"));
			vo.setTasks(rs.getString("TASKS"));
			vo.setTransPerMinute(rs.getString("TRANSPERMINUTE"));
			vo.setRequestsPer1Hour(rs.getString("REQUESTSPER1HOUR"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return vo;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
public BaseVo findByIp(String ip) {
	BaseVo vo = null;
    try
    {
       rs = conn.executeQuery("select * from nms_dominoserver_realtime where ipaddress='" + ip+"'" );
       if(rs.next())
          vo = loadFromRS(rs);
    }
    catch(Exception e)
    {
        SysLogger.error("DominoServerDao.findByIp()",e);
        vo = null;
    }
    finally
    {
     if(rs!=null)
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    if(conn!=null)
        conn.close();
    return vo;
}
}
