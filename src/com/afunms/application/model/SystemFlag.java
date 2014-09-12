package com.afunms.application.model;
/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 21, 2011 6:20:35 PM
 * 类说明 首次启动标识
 */
public class SystemFlag {
	private static SystemFlag test = new SystemFlag();
	
	/**
	 *首次启动标识 
	 */
	private boolean isFirstStart = true;


	public boolean isFirstStart() {
		return isFirstStart;
	}


	public void setFirstStart(boolean isFirstStart) {
		this.isFirstStart = isFirstStart;
	}


	public synchronized static SystemFlag getInstance(){
		return test;
	}
}
