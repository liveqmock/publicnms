package com.bpm.system.utils;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * 
 * Description: 字符串处理
 * StringUtil.java Create on 2012-10-17 下午2:26:17 
 * @author hexinlin
 * Copyright (c) 2012 DHCC Company,Inc. All Rights Reserved.
 */
public class StringUtil {
	
	/**
	 * 
	 * Description:转换字符串编码
	 * Date:2012-10-17
	 * @author hexinlin
	 * @return String
	 */
	public static String decodeStr(String str){
		byte[] b;
		String temp = "";
		try {
			b = str.getBytes("iso-8859-1");
			temp = new String(b,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 
	 * Description:判断字符串是否为空
	 * Date:2012-10-17
	 * @author hexinlin
	 * @return boolean
	 */
	public static boolean isBlank(String str) {
		return null==str||"".equals(str.trim())||"null".equals(str.trim());
	}
	
	/**
	 * 
	 * Description:
	 * Date:2012-10-17
	 * @author hexinlin
	 * @return boolean
	 */
	public static boolean isNotBlank(String str) {
		return null!=str&&!"null".equals(str.trim())&&!"".equals(str.trim());
	}
	
	/**
	 * 截取字符串的指定长度，过长的以...表示
	 */
	public static String interceptStr(String str,int n) {
		if(isBlank(str)||n<=0||n>str.length()) {
			return str;
		}else{
			return str.substring(0,n)+"...";
		}
	}
	
	/**
	 * 如果字符串为空的话，就用指定的字符串来代替
	 */
	public static String ifNull(String srcStr,String replaceStr) {
		if(srcStr==null) {
			return replaceStr;
		} else {
			return srcStr;
		}
	}
	
	/**
	 * 
	 * Description:判断字段是否有空
	 * Date:2012-10-17
	 * @author hexinlin
	 * @return boolean
	 */
	public static boolean exitBlank(String ...strs) {
		for(String str :strs) {
			if(isBlank(str)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * Description:根据时间获取唯一ID
	 * Date:2012-11-9
	 * @author hexinlin
	 * @return long
	 */
	public synchronized static long getTimeId() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}
	/**
	 * 把空字符串转换为"";
	 * @param str
	 * @return
	 */
	public static String toBlank(String str){
		if(isBlank(str)) {
			return "";
		}
		return str;
	}
}
