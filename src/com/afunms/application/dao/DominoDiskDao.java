package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.DominoDisk;
import com.afunms.application.model.DominoMem;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DominoDiskDao extends BaseDao implements DaoInterface{
	   public DominoDiskDao() {
			super("nms_dominodisk_realtime");
		}
			@Override
			public BaseVo loadFromRS(ResultSet rs) {
				DominoDisk vo=new DominoDisk();
				try {
					vo.setDiskname(rs.getString("DISKNAME"));
					vo.setDisktype(rs.getString("DISKTYPE"));
					vo.setDisksize(rs.getString("DISKSIZE"));
					vo.setDiskfree(rs.getString("DISKFREE"));
					vo.setDiskusedpctutil(rs.getString("DISKUSEDPCTUTIL"));
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
		public List findByIp(String ip) {
			String condition=" where ipaddress='"+ip+"'";
			return findByCondition(condition);
			
		}
		}

