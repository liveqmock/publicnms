package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoLdap;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DominoLdapDao extends BaseDao implements DaoInterface{
	   public DominoLdapDao() {
			super("nms_dominoldap_realtime");
		}
			@Override
			public BaseVo loadFromRS(ResultSet rs) {
				DominoLdap vo=new DominoLdap();
				try {
					vo.setLdapRunning(rs.getString("LDAPRUNNING"));
					vo.setLdapInboundQue(rs.getString("LDAPINBOUNDQUE"));
					vo.setLdapInboundActive(rs.getString("LDAPINBOUNDACTIVE"));
					vo.setLdapInboundActiveSSL(rs.getString("LDAPINBOUNDACTIVESSL"));
					vo.setLdapInboundBytesReseived(rs.getString("LDAPINBOUNDBYTESRESEIVED"));
					vo.setLdapInboundBytesSent(rs.getString("LDAPINBOUNDBYTESSENT"));
					vo.setLdapInboundPeak(rs.getString("LDAPINBOUNDPEAK"));
					vo.setLdapInboundPeakSSL(rs.getString("LDAPINBOUNDPEAKSSL"));
					vo.setLdapInboundTotal(rs.getString("LDAPINBOUNDTOTAL"));
					vo.setLdapInboundTotalSSL(rs.getString("LDAPINBOUNDTOTALSSL"));
					vo.setLdapBadHandShake(rs.getString("LDAPBADHANDSHAKE"));
					vo.setLdapThreadsBusy(rs.getString("LDAPTHREADSBUSY"));
					vo.setLdapThreadsIdle(rs.getString("LDAPTHREADSLDLE"));
					vo.setLdapThreadsInPool(rs.getString("LDAPTHREADSINPOOL"));
					vo.setLdapTHreadsPeak(rs.getString("LDAPTHREADSPEAK"));
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
		       rs = conn.executeQuery("select * from nms_dominoldap_realtime where ipaddress='" + ip+"'" );
		       if(rs.next())
		          vo = loadFromRS(rs);
		    }
		    catch(Exception e)
		    {
		        SysLogger.error("DominoLdapDao.findByIp()",e);
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
