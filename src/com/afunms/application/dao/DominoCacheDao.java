package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoCache;
import com.afunms.application.model.DominoMail;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DominoCacheDao extends BaseDao implements DaoInterface{
	   public DominoCacheDao() {
			super("nms_dominocache_realtime");
		}
			@Override
			public BaseVo loadFromRS(ResultSet rs) {
				DominoCache vo=new DominoCache();
				try {
					vo.setCacheCommandCount(rs.getString("CACHECOMMANDCOUNT"));
					vo.setCacheCommandDisRate(rs.getString("CACHECOMMANDDISRATE"));
					vo.setCacheCommandHitRate(rs.getString("CACHECOMMANDHITRATE"));
					vo.setCacheCommandSize(rs.getString("CACHECOMMANDSIZE"));
					vo.setCacheDbHitRate(rs.getString("CACHEDBHITRATE"));
					vo.setCacheSessionCount(rs.getString("CACHESESSIONCOUNT"));
					vo.setCacheSessionDisRate(rs.getString("CACHESESSIONDISRATE"));
					vo.setCacheSessionHitRate(rs.getString("CACHESESSIONHITRATE"));
					vo.setCacheSessionSize(rs.getString("CACHESESSIONSIZE"));
					vo.setCacheUserCount(rs.getString("CACHEUSERCOUNT"));
					vo.setCacheUserDisRate(rs.getString("CACHEUSERDISRATE"));
					vo.setCacheUserHitRate(rs.getString("CACHEUSERHITRATE"));
					vo.setCacheUserSize(rs.getString("CACHEUSRSIZE"));
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
		       rs = conn.executeQuery("select * from nms_dominocache_realtime where ipaddress='" + ip+"'" );
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
