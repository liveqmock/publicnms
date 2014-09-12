package com.afunms.application.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.application.model.IISLog_history;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class IISLog_historyDao extends BaseDao implements DaoInterface {

	public IISLog_historyDao() {
		super("");
		// TODO Auto-generated constructor stub
	}

	@Override
	

	
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		IISLog_history vo = new IISLog_history();
	      try
	      {
	          vo.setId(rs.getInt("id"));
	          vo.setConfigid(rs.getInt("configid"));
	          vo.setSsitename(rs.getString("ssitename"));
	          vo.setSip(rs.getString("sip")); 
	          vo.setCsmethod(rs.getString("csmethod"));
	          vo.setCsuristem(rs.getString("csuristem"));
	          vo.setCsuriquery(rs.getString("csuriquery"));
	          vo.setSport(rs.getString("sport"));
	          vo.setCsusername(rs.getString("csusername"));
	          vo.setCip(rs.getString("cip"));
	          vo.setCsagent(rs.getString("csagent"));
	          Calendar cal = Calendar.getInstance();
	          Date newdate = new Date();
	          newdate.setTime(rs.getTimestamp("recordtime").getTime());
	          cal.setTime(newdate);
	          vo.setRecordtime(cal);
	          vo.setScstatus(rs.getInt("scstatus"));
	          vo.setScsubstatus(rs.getInt("scsubstatus"));
	          vo.setScwin32status(rs.getInt("scwin32status"));

	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	          SysLogger.error("IISLog_historyDao.loadFromRS()",e);
	          vo = null;
	      }
	      return vo;
	}

	  public List findByCriteria(String sql)
	   {
		   List list = new ArrayList();
		   try 
		   {
			   rs = conn.executeQuery(sql);
			   if(rs == null)return null;
			   while(rs.next())
				  list.add(loadFromRS(rs));				
		   } 
		   catch(Exception e) 
		   {
	           list = null;
	           e.printStackTrace();
			   SysLogger.error("BaseDao.findByCondition()",e);
		   }
		   
		   return list;	
	   }
	   
	 /* public List getIISLogIP(String allipstr){
		   List rlist = new ArrayList();
		   DBManager dao = new DBManager();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select distinct cip from iislog"+allipstr);
		   System.out.println(sql.toString());
		   ResultSet rs=dao.executeQuery(sql.toString());
		   try {
			while(rs.next())
			   {
				   rlist.add(rs.getString("cip"));
			   }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
		   return rlist;
	   }*/
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
