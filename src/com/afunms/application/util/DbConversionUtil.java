package com.afunms.application.util;

import com.afunms.common.util.SystemConstant;

/**
 * @description 数据库转换工具类
 * @author wangxiangyong
 * @date Apr 6, 2012 2:32:11 PM
 */
public class DbConversionUtil {
	/**
	 * 单个时间参数之间的转换
	 * @param time
	 * @return
	 */
public static String coversionTimeSql(String time){
	String formatTime="";
	if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
		formatTime="'"+time+"'";
	}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
		formatTime="to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')";
	}
	return formatTime;
}
}
