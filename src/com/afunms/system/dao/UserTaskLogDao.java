/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.*;
import com.afunms.system.model.User;
import com.afunms.system.model.UserTaskLog;

public class UserTaskLogDao extends BaseDao implements DaoInterface
{
   public UserTaskLogDao()
   {
	   super("nms_user_tasklog");
   }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		UserTaskLog userTaskLog = new UserTaskLog();
		userTaskLog = createUserTaskLog(rs);
		return userTaskLog;
	}
	
	public List<UserTaskLog> findByUserId(String userId){
		String sql = "select * from nms_user_tasklog where userId = " + userId;
		List<UserTaskLog> userTaskLogList = new ArrayList<UserTaskLog>();
		try{
			rs = conn.executeQuery(sql);
			while(rs.next()){
				UserTaskLog userTaskLog = new UserTaskLog();
				userTaskLog = (UserTaskLog)createUserTaskLog(rs);
				if(userTaskLog!=null){
					userTaskLogList.add(userTaskLog);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}  
		return userTaskLogList;
	}
	
	private UserTaskLog createUserTaskLog(ResultSet rs) {
		// TODO Auto-generated method stub
		UserTaskLog userTaskLog = new UserTaskLog();
		if(rs != null){
			try {
				userTaskLog.setId(rs.getInt("id"));
				userTaskLog.setUserId(rs.getInt("userid"));
				userTaskLog.setContent(rs.getString("content"));
				userTaskLog.setTime(rs.getString("time"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return userTaskLog;
	}

	public boolean save(BaseVo vo) {
		UserTaskLog userTaskLog = (UserTaskLog)vo;
		
		StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_user_tasklog(userid,content,time)values(");
		   sql.append("'");
		   sql.append(userTaskLog.getUserId());
		   sql.append("','");
		   sql.append(userTaskLog.getContent());
		   sql.append("','");
		   sql.append(userTaskLog.getTime());
		   sql.append("'");
		   sql.append(")");
		   return saveOrUpdate(sql.toString());
	}
	
	public boolean deleteById(String id){
		String sql = "delete from nms_user_tasklog where id = " + id;
		return saveOrUpdate(sql);
	}
	
	public boolean update(BaseVo vo) {
		UserTaskLog userTaskLog = (UserTaskLog)vo;
		StringBuffer sql = new StringBuffer();
	     sql.append("update nms_user_tasklog set userid='");
	     sql.append(userTaskLog.getUserId());
	     sql.append("',content='");
	     sql.append(userTaskLog.getContent());
	     sql.append("',time='");
	     sql.append(userTaskLog.getTime());
	     sql.append("' where id='");
	     sql.append(userTaskLog.getId());
	     sql.append("'");
		 return saveOrUpdate(sql.toString());
	}
	


  
}
