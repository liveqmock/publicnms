package com.afunms.detail.service.syslogInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.Syslog;


public class SyslogInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public SyslogInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}




	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}




	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}




	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}




	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}




	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}




	/**
	 * 获取Syslog信息
	 * 该方法为对外接口方法
	 * @param startdate		开始日期
	 * @param todate		结束日期
	 * @param priorityname	日志等级
	 * @return
	 */
	public List<Syslog> getSyslogInfo(String ipaddress, String startdate, String todate, String priorityname){
		return getSyslogSQLCondition(ipaddress, startdate, todate, priorityname);
	}
	 
	/**
	 * 获取Syslog信息
	 * 该方法为私有方法
	 * @param startdate		开始日期
	 * @param todate		结束日期
	 * @param priorityname	日志等级
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Syslog> getSyslogSQLCondition(String ipaddress, String startdate, String todate, String priorityname){
		String startTime = "";		// 开始时间
		String toTime = "";			// 结束时间
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(startdate == null || "".equals(startdate.trim())){
			startdate = simpleDateFormat.format(date);
		}
		startTime = startdate + " 00:00:00";
		if(todate == null || "".equals(todate.trim())){
			todate = simpleDateFormat.format(date);
		}
		toTime = todate + " 23:59:59";
		List<Syslog> list = null;
		SyslogDao syslogDao = new SyslogDao();
		try {
			list = syslogDao.getQuery(ipaddress, startTime, toTime, priorityname);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			syslogDao.close();
		}
		return list;
	}
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
