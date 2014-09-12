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
import com.afunms.schedule.model.Period;
import com.afunms.system.model.User;

public class PeriodDao extends BaseDao implements DaoInterface {// 

	public PeriodDao() {
		super("nms_period");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Period vo = new Period();
		try {
			vo.setId(rs.getString("id"));
			vo.setName(rs.getString("name"));
			vo.setStart_time(rs.getTime("start_time"));
			vo.setEnd_time(rs.getTime("end_time"));
			vo.setCreated_by(rs.getString("created_by"));
			vo.setCreated_on(rs.getTimestamp("created_on"));
			vo.setUpdated_by(rs.getString("updated_by"));
			vo.setUpdated_on(rs.getTimestamp("updated_on"));
		} catch (Exception ex) {
			SysLogger.error("Error in PeriodDAO.loadFromRS()", ex);
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public int save(Period vo) {
		int result = -1;
		String sql = null;
		try {
			sql = "select * from nms_period where name='" + vo.getName() + "'";
			rs = conn.executeQuery(sql);
			if (rs.next()) // ÒÑ´æÔÚ
				return 0;
			StringBuffer sqlBf = new StringBuffer(100);
			sqlBf.append("insert into nms_period(id,name,START_TIME,END_TIME,CREATED_BY,CREATED_ON)");
			sqlBf.append("values('");
			sqlBf.append(UUID.randomUUID());
			sqlBf.append("','");
			sqlBf.append(vo.getName());
			sqlBf.append("','");
			sqlBf.append(vo.getStart_time());
			sqlBf.append("','");
			sqlBf.append(vo.getEnd_time());
			sqlBf.append("','");
			sqlBf.append(vo.getCreated_by());
			sqlBf.append("','");
			sqlBf.append(new Timestamp(new Date().getTime()));
			sqlBf.append("')");
			conn.executeUpdate(sqlBf.toString());
			result = 1;
		} catch (Exception e) {
			result = -1;
			SysLogger.error("Error in PeriodDao.save()", e);
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo vo) {
		   Period period = (Period)vo;
		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update nms_period set name='");
	       sql.append(period.getName());
	       sql.append("',start_time='");
	       sql.append(period.getStart_time());
	       sql.append("',end_time='");
	       sql.append(period.getEnd_time());
	       sql.append("',updated_by='");
	       sql.append(period.getUpdated_by());
	       sql.append("',updated_on='");
	       sql.append(period.getUpdated_on());
		   sql.append("' where id='");
	       sql.append(period.getId());
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
		           conn.addBatch("delete from nms_period where id='" + id[i] + "'");
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("PeriodDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
	   
	   public Period findByID(String id)
	   {
		   Period vo = null;
	       try
		   {
//	    	   SysLogger.info("findByID----------------->"+"select * from " + table + " where id=" + id);
			   rs = conn.executeQuery("select * from nms_period where id='" + id + "'"); 
			   if(rs.next())
			       vo = (Period)loadFromRS(rs);
		   }    
		   catch(Exception ex)
		   {
			   //ex.printStackTrace();
			   SysLogger.error("PeriodDao.findByID()",ex);
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
