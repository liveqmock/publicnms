package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoMem;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DominoMemDao extends BaseDao implements DaoInterface{
	   public DominoMemDao() {
			super("nms_dominomem_realtime");
		}
			@Override
			public BaseVo loadFromRS(ResultSet rs) {
				DominoMem vo=new DominoMem();
				try {
					vo.setMemAllocate(rs.getString("MEMALLOCATE"));
					vo.setMemAllocateProcess(rs.getString("MEMALLOCATEPROCESS"));
					vo.setMemAllocateShare(rs.getString("MEMALLOCATESHARE"));
					vo.setMemPhysical(rs.getString("MEMPHYSICAL"));
					vo.setMemFree(rs.getString("MEMFREE"));
					vo.setPlatformMemPhyPctUtil(rs.getString("PLATFORMMEMPHYPCTUTIL"));
					vo.setPlatformMemPhysical(rs.getString("PLATFORMMEMPHYSICAL"));
					vo.setMempctutil(rs.getString("MEMPCTUTIL"));
					
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
		       rs = conn.executeQuery("select * from nms_dominomem_realtime where ipaddress='" + ip+"'" );
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
