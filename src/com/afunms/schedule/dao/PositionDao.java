package com.afunms.schedule.dao;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.schedule.model.Position;

public class PositionDao extends BaseDao implements DaoInterface {// 

	public PositionDao() {
		super("nms_position");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Position vo = new Position();
		try {
			vo.setId(rs.getString("id"));
			vo.setName(rs.getString("name"));
			vo.setDescription(rs.getString("description"));
			vo.setCreated_by(rs.getString("created_by"));
			vo.setCreated_on(rs.getTimestamp("created_on"));
			vo.setUpdated_by(rs.getString("updated_by"));
			vo.setUpdated_on(rs.getTimestamp("updated_on"));
		} catch (Exception ex) {
			SysLogger.error("Error in PositionDAO.loadFromRS()", ex);
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public int save(Position vo) {
		int result = -1;
		String sql = null;
		try {
			sql = "select * from nms_Position where name='" + vo.getName() + "'";
			rs = conn.executeQuery(sql);
			if (rs.next()) // ÒÑ´æÔÚ
				return 0;
			StringBuffer sqlBf = new StringBuffer(100);
			sqlBf.append("insert into nms_Position(id,name,Description,CREATED_BY,CREATED_ON)");
			sqlBf.append("values('");
			sqlBf.append(UUID.randomUUID());
			sqlBf.append("','");
			sqlBf.append(vo.getName());
			sqlBf.append("','");
			sqlBf.append(vo.getDescription());
			sqlBf.append("','");
			sqlBf.append(vo.getCreated_by());
			sqlBf.append("','");
			sqlBf.append(vo.getCreated_on());
			sqlBf.append("')");
			conn.executeUpdate(sqlBf.toString());
			result = 1;
		} catch (Exception e) {
			result = -1;
			SysLogger.error("Error in PositionDao.save()", e);
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo vo) {
		   Position Position = (Position)vo;
		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update nms_Position set name='");
	       sql.append(Position.getName());
	       sql.append("',description='");
	       sql.append(Position.getDescription());
	       sql.append("',updated_by='");
	       sql.append(Position.getUpdated_by());
	       sql.append("',updated_on='");
	       sql.append(Position.getUpdated_on());
		   sql.append("' where id='");
	       sql.append(Position.getId());
	       sql.append("'");
	       return saveOrUpdate(sql.toString());
	}

	@Override
	public List listByPage(int curpage, int perpage) {
		return super.listByPage(curpage, perpage);
	}

	   public boolean delete(String[] id)
	   {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<id.length;i++)
		           conn.addBatch("delete from nms_Position where id='" + id[i] + "'");
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("PositionDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
	   
	   public Position findByID(String id)
	   {
		   Position vo = null;
	       try
		   {
			   rs = conn.executeQuery("select * from nms_Position where id='" + id + "'"); 
			   if(rs.next())
			       vo = (Position)loadFromRS(rs);
		   }    
		   catch(Exception ex)
		   {
			   SysLogger.error("PositionDao.findByID()",ex);
		   }finally{
			   if(rs != null){
				   try{
					   rs.close();
				   }catch(Exception e){
				   }
			   }
		   }
	       return vo;
	   }
}
