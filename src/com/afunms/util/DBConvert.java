package com.afunms.util;

import com.afunms.common.util.SystemConstant;

public class DBConvert {

	/**
	 * 如果是mysql 则不变  如果是oracle则转换
	 * @author GANYI
	 * @param YYYY-MM-SS MM:HH:SS样式的String字符串 
	 * @return 
	 * @创建时间 2012-4-17 14:50
	 * 
	 */
	public static String mysqlAndOracleConvert(String time) {
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			return "'"+time+"'";
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			return "to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')";
		}
		return time;
	}
}
