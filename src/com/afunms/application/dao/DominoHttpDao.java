package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoHttp;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DominoHttpDao extends BaseDao implements DaoInterface{
	   public DominoHttpDao() {
			super("nms_dominohttp_realtime");
		}
			@Override
			public BaseVo loadFromRS(ResultSet rs) {
				DominoHttp vo=new DominoHttp();
				try {
					vo.setHttpAccept(rs.getString("HTTPACCEPT"));
					vo.setHttpRefused(rs.getString("HTTPREFUSED"));
					vo.setHttpCurrentCon(rs.getString("HTTPCURRENTCON"));
					vo.setHttpMaxCon(rs.getString("HTTPMAXCON"));	
					vo.setHttpPeakCon(rs.getString("HTTPPEAKCON"));
					vo.setHttpWorkerRequest(rs.getString("HTTPWORKERREQUEST"));
					vo.setHttpWorkerRequestTime(rs.getString("HTTPWORKERREQUESTTIME"));
					vo.setHttpWorkerBytesRead(rs.getString("HTTPWORKERBYTESREAD"));
					vo.setHttpWorkerBytesWritten(rs.getString("HTTPWORKERBYTESWRITTEN"));
					vo.setHttpWorkerRequestProcess(rs.getString("HTTPWORKERREQUESTPROCESS"));
					vo.setHttpWorkerTotalRequest(rs.getString("HTTPWORKERTOTALREQUEST"));
					vo.setHttpErrorUrl(rs.getString("HTTPERRORURL"));
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
		       rs = conn.executeQuery("select * from nms_dominohttp_realtime where ipaddress='" + ip+"'" );
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