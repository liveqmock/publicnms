package com.afunms.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.afunms.system.dao.UserAuditDao;
import com.afunms.system.model.User;
import com.afunms.system.model.UserAudit;

public class UserAuditUtil {
	/**
	 * 保存一条用户审计信息 参数为：用户user 时间Date 动作String
	 * @param user
	 * @param time
	 * @param action
	 * @return
	 */
	public boolean saveUserAudit(User user, Date time , String action){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		return saveUserAudit(user,simpleDateFormat.format(time),action);
	}
	/**
	 * 保存一条用户审计信息 参数: 用户user , 时间格式("yyyy-MM-ss HH-mm-ss") String类型 , 
	 * 动作String action
	 * @param user
	 * @param time
	 * @param action
	 * @return
	 */
	public boolean saveUserAudit(User user, String time , String action){
		UserAuditDao userAuditDao = null ;
		boolean result = false;
		try{
			UserAudit userAudit = new UserAudit();
			userAudit.setUserId(user.getId());
			userAudit.setTime(time);
			userAudit.setAction(action);
			userAuditDao = new UserAuditDao();
			result = userAuditDao.save(userAudit);	
		}catch(Exception e){
			e.printStackTrace();
			result = false;
		}finally{
			userAuditDao.close();
		}
		return result;
	}
    /**
     * 保存一条用户审计信息 参数 用户user , 动作String action . 使用的时间为默认的系统时间 
     * @param user
     * @param action
     * @return
     */
	public boolean saveUserAudit(User user , String action){
		
		return saveUserAudit(user,new Date(),action);
	}
}
