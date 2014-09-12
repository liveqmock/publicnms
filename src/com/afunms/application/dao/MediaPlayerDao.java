package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.MediaPlayer;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class MediaPlayerDao extends BaseDao implements DaoInterface {

	public MediaPlayerDao(){
		super("nms_mediaplayer");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		MediaPlayer mp = new MediaPlayer();
		try{
			mp.setId(rs.getInt("id"));
			mp.setName(rs.getString("name"));
			mp.setDesc(rs.getString("descr"));
			mp.setFileName(rs.getString("fileName"));
			mp.setBsid(rs.getInt("bsid"));
			mp.setOperid(rs.getInt("operid"));
			mp.setDotime(rs.getString("dotime"));
		}catch(SQLException e){
			SysLogger.error("Error in MediaPlayerDAO.loadFromRS()", e);
			mp = null;
		}
		return mp;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		MediaPlayer mp = (MediaPlayer)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_mediaplayer(id,name,fileName,descr,bsid,operid,dotime)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(mp.getName());
		sql.append("','");
		sql.append(mp.getFileName());
		sql.append("','");
		sql.append(mp.getDesc());
		sql.append("',");
		sql.append(mp.getBsid());
		sql.append(",");
		sql.append(mp.getOperid());
		sql.append(",'");
		sql.append(mp.getDotime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		MediaPlayer mp = (MediaPlayer)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_mediaplayer set name='");
		sql.append(mp.getName());
		sql.append("',descr='");
		sql.append(mp.getDesc());
		sql.append("',fileName='");
		sql.append(mp.getFileName());
		sql.append("',bsid=");
		sql.append(mp.getBsid());
		sql.append(",operid=");
		sql.append(mp.getOperid());
		sql.append(",dotime='");
		sql.append(mp.getDotime());
		sql.append("' where id=");
		sql.append(mp.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean deleteById(String id) {
		boolean result = false;

		try {
			conn.addBatch("delete from nms_mediaplayer where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("MediaPlayerDAO.delete()", e);
		} finally {
			conn.close();
		}
		return result;
	}
}
