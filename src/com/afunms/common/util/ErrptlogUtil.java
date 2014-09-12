package com.afunms.common.util;

/**
 * 
 * @author HONGLI  Feb 28, 2011
 *
 */
public class ErrptlogUtil {
	
	/**
	 * @param type  错误级别码
	 * @return      错误级别中文名
	 */
	public static String getTypename(String type){
		String typename = "";
		if("pend".equalsIgnoreCase(type)){
			typename = "设备或功能组件可能丢";
		}else if("perf".equalsIgnoreCase(type)){
			typename = "性能严重下降";
		}else if("perm".equalsIgnoreCase(type)){
			typename = "硬件设备或软件模块损坏";
		}else if("temp".equalsIgnoreCase(type)){
			typename = "临时性错误，经过重试后已经恢复正常";
		}else if("info".equalsIgnoreCase(type)){
			typename = "一般消息，不是错误";
		}else if("unkn".equalsIgnoreCase(type)){
			typename = "不能确定错误的严重性";
		}else {
			typename = "设备或功能组件可能丢";
		}
		return typename;
	}
	
	/**
	 * @param errptclass  错误种类代码
	 * @return			  错误种类中文名
	 */
	public static String getClassname(String errptclass){
		String errptclassname = "";
		if("h".equalsIgnoreCase(errptclass)){
			errptclassname = "硬件或介质故障";
		}else if("s".equalsIgnoreCase(errptclass)){
			errptclassname = "软件故障";
		}else if("o".equalsIgnoreCase(errptclass)){
			errptclassname = "人为错误";
		}else if("u".equalsIgnoreCase(errptclass)){
			errptclassname = "不能确定";
		}else{
			errptclassname = "硬件或介质故障";
		}
		return errptclassname;
	}
	
}
