package com.afunms.application.ajaxManager;

import net.sf.json.JSONObject;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ReflactUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.system.dao.SystemConfigDao;
import java.util.*;

public class SystemConfigAjaxManager extends AjaxBaseManager implements AjaxManagerInterface{

	public void execute(String action){
		if(action.equals("updateSystemconfigFlag")){
			Map<String,String> map = new HashMap<String,String>(); 
			String retMessage = "修改成功";
			//修改数据库中的系统运行模式  
			//修改数据库中的资源树显示模式
			String flagkey = getParaValue("flagkey");
			String flagvalue = getParaValue("flagvalue");
			SystemConfigDao systemConfigDao = new SystemConfigDao();
			try {
				boolean flag = systemConfigDao.updateSystemConfigByVariablenameAndValue(flagkey, flagvalue);
				if(!flag){
					retMessage = "修改失败";
				}
				//修改内存中的系统运行模式
				//修改内存中的资源树显示模式
				//通过反射 执行set方法
				ReflactUtil.invokeSet(PollingEngine.getInstance(), flagkey, flagvalue);
			} catch (Exception e) {
				e.printStackTrace();
				retMessage = "修改失败";
			} finally{
				systemConfigDao.close();
			}
			map.put("message", retMessage);
			JSONObject json = JSONObject.fromObject(map);
			out.print(json);
			out.flush();
		}
	}
}
