/**
 * @author zhangys
 * @project afunms
 * @date 2011-07
 */
package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.manage.NetSyslogManager;
import com.afunms.event.model.NetSyslogAllEvent;
import com.afunms.event.model.NetSyslogViewer;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
@SuppressWarnings("unchecked")
public class NetSyslogViewerDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public NetSyslogViewerDao() {
		super("nms_netsyslog");
	}
	
	/**
	 * @return获得所有host的日志统计记录
	 */
	public List<NetSyslogViewer> loadAll(String starttime, String totime, String strclass){
		List list = new ArrayList();
		List hostList = PollingEngine.getInstance().getNodeList();

		if (null != hostList && hostList.size() > 0) {
			for (int i = 0; i < hostList.size(); i++) {
				list.add(loadNetSyslogViewer((Host)hostList.get(i),null,null,null));
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<NetSyslogViewer> loadNetSyslogViewersByIP(String ipaddress, String starttime, String totime){
		List list = new ArrayList();
		List processnameList = new ArrayList();
		if (null != ipaddress && !"".equals(ipaddress)) {
			String table = NetSyslogManager.getTableName(ipaddress);
			processnameList = loadProcessname(table);
			if (processnameList.size() > 1) {
				for (int i = 0; i < processnameList.size(); i++) {
					list.add(loadNetSyslogAllEvent(((String)processnameList.get(i)).replace("'", "''"), table, starttime, totime));
				}
			}
		}
		if (conn != null) {
			conn.close();
			conn = null;
		}
		return list;
	}

	private List loadProcessname(String table){
		List list = new ArrayList();
		String sql = "select distinct processname from " + table;
		rs = conn.executeQuery(sql);
		try {
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;		
	}
	
	/**
	 * @param processname
	 * @return获得一个进程的各种日志的统计记录
	 */
	private NetSyslogAllEvent loadNetSyslogAllEvent(String processname, String table, String starttime, String totime){
		NetSyslogAllEvent event = new NetSyslogAllEvent();
		event.setProcessname(processname);
		event.setErrors(querySyslogCountByProcess(table, processname, "error", starttime, totime));
		event.setWarnings(querySyslogCountByProcess(table, processname, "warning", starttime, totime));
		event.setFailures(querySyslogCountByProcess(table, processname, "fail", starttime, totime));
		event.setOthers(querySyslogCountByProcess(table, processname, "others", starttime, totime));
		event.setAll(querySyslogCountByProcess(table, processname, null, starttime, totime));
		return event;
	}	

	private int querySyslogCountByProcess(String table, String processname, String priorityname, String starttime, String totime) {
		String sql = "";
		if (null == priorityname) {//全部
			sql = "select count(*) from " + table + " where processname = '" + processname + "'";
		}else if(null != priorityname && "others".equals(priorityname)){
			sql = "select count(*) from " + table + " where trim(priorityname) != 'error' and trim(priorityname) != 'warning' and processname = '" + processname + "'";
		}else{
			sql = "select count(*) from " + table + " where trim(priorityname)= '" + priorityname.trim() + "' and processname = '" + processname + "'";
		}
		sql += " and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
		try{
			//System.out.println("NetSyslogViewerDao.java 108 ==>" + sql);
			rs = conn.executeQuery(sql);
			while(rs.next()){
				return rs.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	private List getNodeByCategory(List nodeList, String strclass){
		int category = -1;
		if (null != strclass && !"".equals(strclass) && !"null".equalsIgnoreCase(strclass)) {
			category = Integer.parseInt(strclass);
		}
		if (category == -1) {
			return nodeList;
		} 
		List hostList = new ArrayList();
		for (int i = 0; i < nodeList.size(); i++) {
			Host host = (Host)nodeList.get(i);
			if(category == 1 && 4 == host.getCategory()){
				hostList.add(host);
			}else if(category == 2 && 4 != host.getCategory()){
				hostList.add(host);
			}
		}
		return hostList;
	}
	
	private List getNodeByIpaddress(List nodeList, String ipaddress){
		List hostList = new ArrayList();
		if (null != ipaddress && !"".equals(ipaddress)) {
			for (int i = 0; i < nodeList.size(); i++) {
				Host host = (Host)nodeList.get(i);
				if (host.getIpAddress().indexOf(ipaddress)!= -1 ) {
					hostList.add(host);
				}
			}
		}else{
			return nodeList;
		}
		return hostList;
	}
	
	/**
	 * 
	 * @param perpage
	 * @param currentpage
	 * @return获得所有host的日志统计记录
	 */
	public List loadNetSyslogViewers(int perpage, int currentpage, String starttime, String totime, String strclass, String ipaddress) {

		List list = new ArrayList();
		List allNodeList = PollingEngine.getInstance().getNodeList();
		List hostList = getNodeByIpaddress(getNodeByCategory(allNodeList, strclass), ipaddress);
		
		List pageList = new ArrayList();
		int index = (currentpage-1)* perpage;
		if((index + perpage) < hostList.size()){
			for (int i = index; i < index + perpage; i++) {
				pageList.add(hostList.get(i));
			}
		}else{
			for (int i = index; i < hostList.size(); i++) {
				pageList.add(hostList.get(i));
			}
		}
		if (null != pageList && pageList.size() > 0) {
			for (int i = 0; i < pageList.size(); i++) {
				list.add(loadNetSyslogViewer((Host)pageList.get(i), starttime, totime, strclass)); 
			}
		}
		jspPage = new JspPage(perpage,currentpage,allNodeList.size());
		return list;
	}

	/**
	 * @param host
	 * @return获得一个设备的各种日志的统计记录
	 */
	private NetSyslogViewer loadNetSyslogViewer(Host host, String starttime, String totime, String strclass){
		NetSyslogViewer viewer = new NetSyslogViewer();
		String ipAddress = host.getIpAddress();
		viewer.setIpaddress(ipAddress);
		viewer.setHostName(host.getAlias());
		viewer.setCategory(host.getCategory());
		String status = "0";
		if(host.isManaged()) {
			status = "1";
		}
		viewer.setStatus(status);
		viewer.setErrors(querySyslogCountByIP(ipAddress, "error", starttime, totime, strclass));
		viewer.setWarnings(querySyslogCountByIP(ipAddress, "warning", starttime, totime, strclass));
		viewer.setFailures(querySyslogCountByIP(ipAddress, "fail", starttime, totime, strclass));
		viewer.setOthers(querySyslogCountByIP(ipAddress, "others", starttime, totime, strclass));
		viewer.setAll(querySyslogCountByIP(ipAddress, null, starttime, totime, strclass));
		return viewer;
	}

	/**
	 * 获取某个host的syslog统计信息
	 * @param ipaddress：设备ip地址
	 * @param priorityname：分类信息：错误、警告、失败、其他、全部
	 * @return 统计数字
	 */
	private int querySyslogCountByIP(String ipaddress, String priorityname, String starttime, String totime, String strclass){
		String sql = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			if (null == priorityname) {//全部
				if (NetSyslogManager.isNetworkDev(ipaddress)) {
					sql = "select count(*) from nms_netsyslog where ipaddress = '" + ipaddress.trim() + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
				}else
					sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + " where ipaddress = '" + ipaddress.trim() + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
			}else if(null != priorityname && "others".equals(priorityname)){
				if (NetSyslogManager.isNetworkDev(ipaddress)) {
					sql = "select count(*) from nms_netsyslog where trim(priorityname) != 'error' and trim(priorityname) != 'warning' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
				}else
					sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + " where trim(priorityname) != 'error' and trim(priorityname) != 'warning' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
					
			}else{
				if (NetSyslogManager.isNetworkDev(ipaddress)) {
					sql = "select count(*) from nms_netsyslog where trim(priorityname)= '" + priorityname.trim() + "' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
				}else
					sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + " where trim(priorityname)= '" + priorityname.trim() + "' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= '" + starttime + "' and recordtime <= '" + totime + "'";
			}
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			if (null == priorityname) {//全部
				if (NetSyslogManager.isNetworkDev(ipaddress)) {
					sql = "select count(*) from nms_netsyslog where ipaddress = '" + ipaddress.trim() + "' and recordtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and recordtime <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				}else
					sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + " where ipaddress = '" + ipaddress.trim() + "' and recordtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and recordtime <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
			}else if(null != priorityname && "others".equals(priorityname)){
				if (NetSyslogManager.isNetworkDev(ipaddress)) {
					sql = "select count(*) from nms_netsyslog where trim(priorityname) != 'error' and trim(priorityname) != 'warning' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and recordtime <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				}else
					sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + " where trim(priorityname) != 'error' and trim(priorityname) != 'warning' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and recordtime <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
					
			}else{
				if (NetSyslogManager.isNetworkDev(ipaddress)) {
					sql = "select count(*) from nms_netsyslog where trim(priorityname)= '" + priorityname.trim() + "' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and recordtime <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
				}else
					sql = "select count(*) from " + SysUtil.doip("log" + ipaddress) + " where trim(priorityname)= '" + priorityname.trim() + "' and ipaddress = '" + ipaddress.trim() + "' and recordtime >= to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS') and recordtime <= to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')";
			}
		}
		

		
//		if(!"-1".equals(strclass) && strclass != null && !"".equals(strclass) && !"null".equals(strclass)){
//			if("1".equals(strclass)){
//				sql += " and category = 4 ";
//			}else if("2".equals(strclass)){
//				sql += " and category <> 4 ";
//			}
//		}
		try{
//			System.out.println(sql);
			rs = conn.executeQuery(sql);
			while(rs.next()){
				return rs.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
