package com.afunms.application.ajaxManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.w3c.dom.Document;


import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.dao.SubscribeResourcesDao;
import com.afunms.capreport.model.SubscribeResources;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.base.BaseTelnet;
import com.afunms.common.util.Arith;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.CreatePiePicture;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ExcelImportUtil;
import com.afunms.common.util.GeneratorKey;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.EnvConfigDao;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.Hua3VPNFileConfigDao;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.dao.SlaCfgCmdFileDao;
import com.afunms.config.model.CiscoSlaCfgCmdFile;
import com.afunms.config.model.Hua3VPNFileConfig;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.Portconfig;
import com.afunms.detail.net.service.NetService;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.ssh.SSHUtil;
import com.afunms.polling.task.CheckLinkTask;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.H3CTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;
import com.afunms.polling.telnet.RedGiantTelnet;
import com.afunms.polling.telnet.ZteTelnet;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.manage.LinkManager;
import com.afunms.topology.manage.LinkPerformanceManager;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.LinkPerformanceDTO;
import com.afunms.topology.model.NodeMonitor;

public class NetworkDeviceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	// 更改端口配置是否监视
	private void editnodepower() {
		String flagStr = "";
		int id = getParaIntValue("id");
		int enabled = getParaIntValue("enabled");
		if (enabled != -1) {
			flagStr = enabled + "";
		}

		EnvConfigDao dao = new EnvConfigDao();
		try {
			dao.updateEnabled(id, enabled);
		} catch (Exception e) {
			e.printStackTrace();
			flagStr = "3";
		} finally {
			dao.close();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("flagStr", flagStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}

	// 更改端口配置是否监视
	private void editnodeport() {
		String flagStr = "";
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		if (sms != -1) {
			flagStr = sms + "";
		}
		if (reportflag != -1) {
			flagStr = reportflag + "";
		}
		PortconfigDao dao = null;

		dao = new PortconfigDao();
		try {
			vo = dao.loadPortconfig(id);
		} catch (Exception e) {
			e.printStackTrace();
			flagStr = "3";
		} finally {
			dao.close();
		}

		if (sms > -1)
			vo.setSms(sms);

		if (reportflag > -1)
			vo.setReportflag(reportflag);

		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
			flagStr = "3";
		} finally {
			dao.close();
		}

		if (sms == 0) {
			Node node = PollingEngine.getInstance().getNodeByIP(vo.getIpaddress());
			NodeDTO nodeDTO = null;
			NodeUtil nodeUtil = new NodeUtil();
			nodeDTO = nodeUtil.conversionToNodeDTO(node);
			boolean checkEvent;
			CheckEventDao checkeventdao = null;
			EventListDao eventlistDao=null;
			try {
				if (node != null) {
					// String name = node.getId() + ":" + nodeDTO.getType() +
					// ":interface";
					checkeventdao = new CheckEventDao();
					eventlistDao=new EventListDao();
					if (vo.getPortindex() != null) {
						eventlistDao.deleteByIdAndSubentity(node.getId(), "interface:"+vo.getName());
						checkEvent = checkeventdao.deleteCheckEvent(node.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype(), "interface", vo.getName() + "");
						Hashtable checkEventHash = ShareData.getCheckEventHash();
						String name = node.getId() + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":" + "interface"+":"+vo.getName();
						if (checkEventHash.containsKey(name)) {
							checkEventHash.remove(name);
						}
					} else {
						checkEvent = checkeventdao.deleteCheckEvent(node.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype(), "interface");
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if (checkeventdao != null)
					checkeventdao.close();
				if(eventlistDao!=null)
					eventlistDao.close();
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("flagStr", flagStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		out.close();
	}

	private void savePerforIndex() {

		String ids = request.getParameter("ids");

		String type = "";
		type = request.getParameter("type");
		String startTime = request.getParameter("startdate");
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (startTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startTime = sdf.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + " 00:00:00";
		}

		String toTime = request.getParameter("todate");
		if (toTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toTime = sdf.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + " 23:59:59";
		}

		String[] idValue = null;
		String ip = "";
		String category = "";
		String entity = "";
		String subentity = "";
		String dataStr = "保存成功！";
		// ////////////////////////////////////////////////////
		DBManager dbManager = new DBManager();
		String reportname = this.getParaValue("report_name");
		String transmitfrequency = request.getParameter("transmitfrequency");
		String bidtext = this.getParaValue("bidtext");
		String reporttype = this.getParaValue("reporttype");
		String exporttype = request.getParameter("exporttype");
		String username = this.getParaValue("recievers_name");
		String tile = this.getParaValue("tile");
		String desc = this.getParaValue("desc");
		try {
			reportname = new String(reportname.getBytes("iso8859-1"), "UTF-8");
			bidtext = new String(bidtext.getBytes("iso8859-1"), "UTF-8");
			reporttype = new String(reporttype.getBytes("iso8859-1"), "UTF-8");
			username = new String(username.getBytes("iso8859-1"), "UTF-8");
			tile = new String(tile.getBytes("iso8859-1"), "UTF-8");
			desc = new String(desc.getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sendtimemonth = null;
		String sendtimeweek = null;
		String sendtimeday = null;
		String sendtimehou = null;
		if ("1".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
		} else if ("2".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeweek = request.getParameter("sendtimeweek");
		} else if ("3".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeday = request.getParameter("sendtimeday");
		} else if ("4".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeday = request.getParameter("sendtimeday");
			sendtimemonth = request.getParameter("sendtimemonth");
		} else if ("5".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeday = request.getParameter("sendtimeday");
			sendtimemonth = request.getParameter("sendtimemonth");
		}
		String recieversId = this.getParaValue("recievers_id");
		String bid = this.getParaValue("bid");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdFormat.format(cc);
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_userReport(name,type,ids,collecttime,begintime,endtime) values('");
		sql.append(reportname);
		sql.append("','");

		sql.append(type);
		sql.append("','");
		sql.append(ids);
		sql.append("','");
		sql.append(time);

		sql.append("','");
		sql.append(startTime);
		sql.append("','");
		sql.append(toTime);

		sql.append("')");
		try {
			dbManager.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			dataStr = "保存失败！！！";
		} finally {
			dbManager.close();
		}
		int key = GeneratorKey.getInstance().getNextKey();
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try {
			userList = userDao.findbyIDs(recieversId.substring(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < userList.size(); i++) {
			User vo = (User) userList.get(i);
			buf.append(vo.getEmail());
			buf.append(",");
		}
		DateTime dt = new DateTime();
		SubscribeResources sr = new SubscribeResources();
		sr.setSubscribe_id(key);
		sr.setBidtext(bidtext);
		sr.setBID(bid);
		sr.setUsername(username);
		sr.setEmail(buf.toString());
		sr.setEmailtitle(tile);
		sr.setEmailcontent(desc);
		sr.setAttachmentformat(exporttype);
		sr.setReport_type(reporttype);
		sr.setReport_senddate(Integer.parseInt(dt.getMyDateTime(DateTime.Datetime_Format_14)));
		sr.setReport_sendfrequency(Integer.parseInt(transmitfrequency));
		sr.setReport_time_month(arrayToString(sendtimemonth));
		sr.setReport_time_week(arrayToString(sendtimeweek));
		sr.setReport_time_day(arrayToString(sendtimeday));
		sr.setReport_time_hou(arrayToString(sendtimehou));
		SubscribeResourcesDao srd = new SubscribeResourcesDao();
		boolean b = srd.save(sr);
		if (!b) {
			dataStr = "保存失败！！！";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", dataStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * 保存端口对比模板 by zhangys
	 */
	private void saveDkdbTemplate() {
		String startTime = request.getParameter("startdate");
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (startTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startTime = sdf.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + " 00:00:00";
		}

		String toTime = request.getParameter("todate");
		if (toTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toTime = sdf.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + " 23:59:59";
		}
		/* 保存端口对比报表名称等基本信息 */
		String dataStr = "保存成功！";
		DBManager dbManager = new DBManager();
		String reportname = this.getParaValue("report_name");
		String transmitfrequency = request.getParameter("transmitfrequency");
		String bidtext = this.getParaValue("bidtext");
		String reporttype = this.getParaValue("reporttype");
		String exporttype = request.getParameter("exporttype");
		String username = this.getParaValue("recievers_name");
		String tile = this.getParaValue("tile");
		String desc = this.getParaValue("desc");
		try {
			reportname = new String(reportname.getBytes("iso8859-1"), "UTF-8");
			bidtext = new String(bidtext.getBytes("iso8859-1"), "UTF-8");
			reporttype = new String(reporttype.getBytes("iso8859-1"), "UTF-8");
			username = new String(username.getBytes("iso8859-1"), "UTF-8");
			tile = new String(tile.getBytes("iso8859-1"), "UTF-8");
			desc = new String(desc.getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sendtimemonth = null;
		String sendtimeweek = null;
		String sendtimeday = null;
		String sendtimehou = null;
		if ("1".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
		} else if ("2".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeweek = request.getParameter("sendtimeweek");
		} else if ("3".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeday = request.getParameter("sendtimeday");
		} else if ("4".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeday = request.getParameter("sendtimeday");
			sendtimemonth = request.getParameter("sendtimemonth");
		} else if ("5".equals(transmitfrequency)) {
			sendtimehou = request.getParameter("sendtimehou");
			sendtimeday = request.getParameter("sendtimeday");
			sendtimemonth = request.getParameter("sendtimemonth");
		}
		String recieversId = this.getParaValue("recievers_id");
		String bid = this.getParaValue("bid");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdFormat.format(cc);
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_dkdbareport(name,collecttime,begintime,endtime) values('");
		sql.append(reportname);
		sql.append("','");
		sql.append(time);
		sql.append("','");
		sql.append(startTime);
		sql.append("','");
		sql.append(toTime);
		sql.append("')");

		try {
			dbManager.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			dataStr = "保存失败！！！";
		}

		/* 保存端口对比报表中配置的端口号列表 */
		int key = GeneratorKey.getInstance().getNextKey("nms_dkdbareport");
		String ids = request.getParameter("ids");
		try {
			if (null != ids && !"".equals(ids)) {
				String[] interfs = ids.split(",");
				for (String interf : interfs) {
					sql = new StringBuffer();
					sql.append("insert into nms_dkdbports(SUBSCRIBE_ID, ids) values(");
					sql.append(key);
					sql.append(",'");
					sql.append(interf);
					sql.append("')");
					dbManager.executeUpdate(sql.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbManager.close();
		}

		/* 保存端口对比报表中配置的报表生成日期、格式等信息 */
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try {
			userList = userDao.findbyIDs(recieversId.substring(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < userList.size(); i++) {
			User vo = (User) userList.get(i);
			buf.append(vo.getEmail());
			buf.append(",");
		}

		DateTime dt = new DateTime();
		SubscribeResources sr = new SubscribeResources();
		sr.setSubscribe_id(key);
		sr.setBidtext(bidtext);
		sr.setBID(bid);
		sr.setUsername(username);
		sr.setEmail(buf.toString());
		sr.setEmailtitle(tile);
		sr.setEmailcontent(desc);
		sr.setAttachmentformat(exporttype);
		sr.setReport_type(reporttype);
		sr.setReport_senddate(Integer.parseInt(dt.getMyDateTime(DateTime.Datetime_Format_14)));
		sr.setReport_sendfrequency(Integer.parseInt(transmitfrequency));
		sr.setReport_time_month(arrayToString(sendtimemonth));
		sr.setReport_time_week(arrayToString(sendtimeweek));
		sr.setReport_time_day(arrayToString(sendtimeday));
		sr.setReport_time_hou(arrayToString(sendtimehou));
		SubscribeResourcesDao srd = new SubscribeResourcesDao();
		boolean b = srd.save(sr, "sys_dkdb_ssresources");
		if (!b) {
			dataStr = "保存失败！！！";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", dataStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		out.close();
	}

	public String arrayToString(String array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			array = array.replace(",", "/");
			sb.append("/").append(array).append("/");
		}
		return sb.toString();
	}

	// 加载模板列表（网络设备、服务器）
	private void loadPeforIndex() {
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		StringBuffer html = new StringBuffer();

		String id = this.getParaValue("id");
		String type = this.getParaValue("type");

		String sql = "select n.id,n.name,n.ids,s.EMAIL,s.REPORT_SENDDATE from nms_userreport n,sys_subscribe_resources s where n.id=s.SUBSCRIBE_ID and n.type='" + type + "'";
		List<SubscribeResources> list = new ArrayList<SubscribeResources>();
		try {
			if (id != null && !id.equals("")) {

				String sql1 = "delete from nms_userreport where id=" + id;
				String sql2 = "delete from sys_subscribe_resources where SUBSCRIBE_ID=" + id;

				try {
					dbManager.executeUpdate(sql1);
					dbManager.executeUpdate(sql2);

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
			rs = dbManager.executeQuery(sql);
			while (rs.next()) {
				SubscribeResources sr = new SubscribeResources();
				sr.setSubscribe_id(rs.getInt("id"));
				sr.setUsername(rs.getString("name"));// 为方便暂用字段充当模板名称
				sr.setEmail(rs.getString("EMAIL"));
				sr.setReport_senddate(rs.getInt("REPORT_SENDDATE"));
				sr.setEmailcontent(rs.getString("ids"));// 为方便暂用字段充当性能指标
				list.add(sr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dbManager.close();

		}
		if (list != null && list.size() > 0) {

			html
					.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>序号</td><td align='center' class='body-data-title' height=21>模板名称</td><td align='center' class='body-data-title' height=21>接收邮箱</td><td align='center' class='body-data-title' height=21>发送时间</td><td align='center' class='body-data-title' height=21>详情</td>");
			for (int i = 0; i < list.size(); i++) {
				SubscribeResources sr = new SubscribeResources();
				sr = list.get(i);
				html.append("<tr><td  align='center' height=19>");
				html.append(i + 1);
				html.append("</td><td align='center' height=19>");
				html.append(sr.getUsername());
				html.append("</td><td align='center' height=19>");
				html.append(sr.getEmail());
				html.append("</td><td align='center' height=19>");
				html.append(sr.getReport_senddate());
				html.append("</td><td align='center' height=19>");

				String path = request.getContextPath();
				String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
				html.append("<img src='" + basePath + "resource/image/vcf.gif' border='0' onClick='createWin(" + sr.getSubscribe_id() + ")' title='查看模板详细信息'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/viewreport.gif' border='0' onClick='preview(" + sr.getSubscribe_id()
						+ ")' title='预览模板报表'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/delete.gif' border='0' onClick='deleteItem(" + sr.getSubscribe_id() + ")' title='删除模板'/></td></tr>");
			}
			html.append("</table>");

		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", html.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		out.close();
	}

	/**
	 * 加载端口对比模板列表 by zhangys
	 */
	private void loadDkdbTemplet() {
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		StringBuffer html = new StringBuffer();
		String id = this.getParaValue("id");
		String sql = "select n.id,n.name,s.EMAIL,s.REPORT_SENDDATE from nms_dkdbareport n,sys_dkdb_ssresources s where n.id=s.SUBSCRIBE_ID";
		List<SubscribeResources> list = new ArrayList<SubscribeResources>();

		try {
			rs = dbManager.executeQuery(sql);
			while (rs.next()) {
				SubscribeResources sr = new SubscribeResources();
				sr.setSubscribe_id(rs.getInt("id"));
				sr.setUsername(rs.getString("name"));// 为方便暂用字段充当模板名称
				sr.setEmail(rs.getString("EMAIL"));
				sr.setReport_senddate(rs.getInt("REPORT_SENDDATE"));
				// sr.setEmailcontent(rs.getString("ids"));// 为方便暂用字段充当性能指标
				list.add(sr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dbManager.close();

		}
		if (list != null && list.size() > 0) {
			html
					.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>序号</td><td align='center' class='body-data-title' height=21>模板名称</td><td align='center' class='body-data-title' height=21>接收邮箱</td><td align='center' class='body-data-title' height=21>发送时间</td><td align='center' class='body-data-title' height=21>详情</td></tr>");
			for (int i = 0; i < list.size(); i++) {
				SubscribeResources sr = new SubscribeResources();
				sr = list.get(i);
				html.append("<tr><td  align='center' height=19>");
				html.append(i + 1);
				html.append("</td><td align='center' height=19>");
				html.append(sr.getUsername());
				html.append("</td><td align='center' height=19>");
				html.append(sr.getEmail());
				html.append("</td><td align='center' height=19>");
				html.append(sr.getReport_senddate());
				html.append("</td><td align='center' height=19>");

				String path = request.getContextPath();
				String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
				html.append("<img src='" + basePath + "resource/image/vcf.gif' border='0' onClick='createWin(" + sr.getSubscribe_id() + ")' title='查看模板详细信息'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/viewreport.gif' border='0' onClick='preview(" + sr.getSubscribe_id()
						+ ")' title='预览模板报表'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/delete.gif' border='0' onClick='deleteItem(" + sr.getSubscribe_id() + ")' title='删除模板'/></td></tr>");
			}
			html.append("</table>");
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", html.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	private void deletePerforIndex() {

		DBManager dbManager = new DBManager();
		String id = this.getParaValue("id");
		String flagStr = "删除成功！";
		String sql1 = "delete from nms_userreport where id=" + id;
		String sql2 = "delete from sys_subscribe_resources where SUBSCRIBE_ID=" + id;
		List<SubscribeResources> list = new ArrayList<SubscribeResources>();
		try {
			dbManager.executeUpdate(sql1);
			dbManager.executeUpdate(sql2);

		} catch (Exception e) {
			e.printStackTrace();
			flagStr = "删除失败！";
		} finally {
			dbManager.close();

		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("flagStr", flagStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}

	/**
	 * 删除端口对比模板 by zhangys
	 */
	private void deleteDkdbTemplet() {
		DBManager dbManager = new DBManager();
		String id = this.getParaValue("id");
		String sql1 = "delete from nms_dkdbareport where id=" + id;
		String sql2 = "delete from nms_dkdbports where SUBSCRIBE_ID=" + id;
		String sql3 = "delete from sys_dkdb_ssresources where SUBSCRIBE_ID=" + id;
		try {
			dbManager.executeUpdate(sql2);
			dbManager.executeUpdate(sql3);
			dbManager.executeUpdate(sql1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbManager.close();

		}
		this.loadDkdbTemplet();
	}

	private void showContent() {
		String command = this.getParaValue("command");
		String content = this.getParaValue("cmdContent");
		String beginStr = "*****************begin(" + command.trim() + ")*****************\r\n";
		String endStr = "*****************end(" + command.trim() + ")*****************";
		int begin = content.indexOf(beginStr);
		int end = content.indexOf(endStr);
		content = content.substring(begin + beginStr.length(), end);
		Map<String, String> map = new HashMap<String, String>();

		map.put("content", content);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	private void showFileContent() {
		String contain = getParaValue("contain");
		String command = getParaValue("command");
		String content = getParaValue("cmdContent");
		StringBuffer sb = new StringBuffer();
		String[] contains = new String[contain.split(",").length];
		contains = contain.split(",");
		String[] commands = new String[command.split(",").length];
		commands = command.split(",");
		if (contains != null && commands != null && contains.length == commands.length) {
			for (int i = 0; i < commands.length; i++) {
				if (contains[i].equals("1")) {
					String beginStr = "*****************begin(" + commands[i] + ")*****************\r\n";
					String endStr = "*****************end(" + commands[i] + ")*****************\r\n";
					int begin = content.indexOf(beginStr);
					int end = content.indexOf(endStr);
					sb.append(content.substring(begin, end + endStr.length()));
				}

			}

		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", sb.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	private void setBaseLine() {
		String id = getParaValue("id");
		
		String result = "";
		Hua3VPNFileConfigDao dao = new Hua3VPNFileConfigDao();
		List list = dao.findByCondition(" where baseline=1 ");

		Hua3VPNFileConfig config = null;
		if (list != null && list.size() > 0) {
			config = (Hua3VPNFileConfig) list.get(0);
		}

		if (config == null) {
			dao = new Hua3VPNFileConfigDao();
			dao.updateBaseLine(id, 1);
			result = "修改成功";
		} else {
			result = "0";
		}
		dao.close();

		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	private void exeCmd() {
		String ip = getParaValue("ip");
		String commands = getParaValue("commands");
		String result = "";
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		;
		try {
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String[] commStr = new String[commands.split("\n").length];
		commStr = commands.split("\n");
		if (vo.getDeviceRender().equals("h3c")) {// h3c
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
			result = tvpn.getCommantValue(commStr);

		} else if (vo.getDeviceRender().equals("cisco")) {// cisco
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword());
			if (telnet.login()) {
				result = telnet.getCommantValue(commStr);

			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", result);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void loadFile() {
		String filePath = (String) getParaValue("filePath");
		String prePath = ResourceCenter.getInstance().getSysPath();
		FileReader fr = null;
		try {
			fr = new FileReader(prePath + filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String lineStr = "";
		StringBuffer content = new StringBuffer();
		try {
			while (null != (lineStr = br.readLine())) {
				content.append(lineStr + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", content.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}

	public void loadFileFromMenu() {
		String id = (String) getParaValue("id");
		SlaCfgCmdFileDao dao = new SlaCfgCmdFileDao();
		CiscoSlaCfgCmdFile slacfg = null;
		FileReader fr = null;
		String prePath = ResourceCenter.getInstance().getSysPath();
		try {
			slacfg = (CiscoSlaCfgCmdFile) dao.findByID(id);
			String fileName = slacfg.getFilename().split("afunms/")[1];
			fr = new FileReader(prePath + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String lineStr = "";
		StringBuffer content = new StringBuffer();
		try {
			while (null != (lineStr = br.readLine())) {
				content.append(lineStr + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", content.toString());
		map.put("slatype", slacfg.getSlatype());
		map.put("cmdid", slacfg.getId() + "");
		map.put("deviceType", slacfg.getDevicetype());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}

	public void linkRoadList() {
		String linkids = request.getParameter("linkids");
		// 取出所有的链路数据
		List linkList = null;
		LinkDao linkDao = null;
		try {
			linkDao = new LinkDao();
			linkList = linkDao.loadAll();
			// linkList = (List)request.getAttribute("list");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			linkDao.close();
		}

		List linkPerformanceList = new ArrayList();
		DecimalFormat df = new DecimalFormat("#.##");
		String runmodel = PollingEngine.getCollectwebflag();
		LinkPerformanceManager linkPerformanceManager = new LinkPerformanceManager();
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			for (int i = 0; i < linkList.size(); i++) {
				Link link = (Link) linkList.get(i);
				if (link.getLinktype() != -1) {
					LinkPerformanceDTO linkPerformanceDTO = linkPerformanceManager.getLinkPerformanceDTO(link);
					if(linkPerformanceDTO==null)continue;
					linkPerformanceList.add(linkPerformanceDTO);
				}
			}
		} else {
			// 采集与访问是分离模式
			// 根据链路的ID得到链路的起始端口、流速等信息
			// 取端口流速
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			Vector end_vector = new Vector();
			Vector start_vector = new Vector();
			String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "ifOutBroadcastPkts", "ifInBroadcastPkts", "ifOutMulticastPkts", "ifInMulticastPkts", "OutBandwidthUtilHdx", "InBandwidthUtilHdx", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdxPerc" };
			Hashtable interfaceHash = CheckLinkTask.getLinknodeInterfaceData(linkList);// 先采集所有，避免在for循环中多次采集
			for (int k = 0; k < linkList.size(); k++) {
				Link link = (Link) linkList.get(k);
				if (link.getLinktype() != -1) {
					int startId = link.getStartId();
					int endId = link.getEndId();
					String startIndex = link.getStartIndex();
					String endIndex = link.getEndIndex();
					String start_inutilhdx = "0";
					String start_oututilhdx = "0";
					String start_inutilhdxperc = "0";
					String start_oututilhdxperc = "0";
					String end_inutilhdx = "0";
					String end_oututilhdx = "0";
					String end_inutilhdxperc = "0";
					String end_oututilhdxperc = "0";
					String starOper = "";
					String endOper = "";
					String pingValue = "0";
					String allSpeedRate = "0";
					com.afunms.polling.base.Node startnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByID(startId);
					com.afunms.polling.base.Node endnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByID(endId);
					if (startnode == null || endnode == null) {
						continue;
					}
					try {
						// start_vector =
						// hostlastmanager.getInterface(startnode.getIpAddress(),netInterfaceItem,"index","","");
						// end_vector =
						// hostlastmanager.getInterface(endnode.getIpAddress(),netInterfaceItem,"index","","");
						if (interfaceHash != null && interfaceHash.containsKey(startnode.getIpAddress())) {
							start_vector = (Vector) interfaceHash.get(startnode.getIpAddress());
						}
						if (interfaceHash != null && interfaceHash.containsKey(endnode.getIpAddress())) {
							end_vector = (Vector) interfaceHash.get(endnode.getIpAddress());
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (startnode != null) {
						try {
							for (int i = 0; i < start_vector.size(); i++) {
								String[] strs = (String[]) start_vector.get(i);
								String index = strs[0];
								if (index.equalsIgnoreCase(startIndex)) {
									starOper = strs[3].trim();
									start_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									start_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									start_oututilhdxperc = strs[10].replaceAll("%", "");
									start_inutilhdxperc = strs[11].replaceAll("%", "");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (endnode != null) {
						try {
							for (int i = 0; i < end_vector.size(); i++) {
								String[] strs = (String[]) end_vector.get(i);
								String index = strs[0];
								if (index.equalsIgnoreCase(endIndex)) {
									endOper = strs[3].trim();
									;
									end_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									end_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									end_oututilhdxperc = strs[10].replaceAll("%", "");
									end_inutilhdxperc = strs[11].replaceAll("%", "");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					int downspeed = (Integer.parseInt(start_oututilhdx) + Integer.parseInt(end_inutilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
					int upspeed = (Integer.parseInt(start_inutilhdx) + Integer.parseInt(end_oututilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;

					double upperc = 0;
					try {
						if (start_oututilhdxperc != null && start_oututilhdxperc.trim().length() > 0 && end_inutilhdxperc != null && end_inutilhdxperc.trim().length() > 0)
							upperc = Arith.div((Double.parseDouble(start_oututilhdxperc) + Double.parseDouble(end_inutilhdxperc)), 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					double downperc = 0;
					try {
						if (start_inutilhdxperc != null && start_inutilhdxperc.trim().length() > 0 && end_oututilhdxperc != null && end_oututilhdxperc.trim().length() > 0)
							downperc = Arith.div((Double.parseDouble(start_inutilhdxperc) + Double.parseDouble(end_oututilhdxperc)), 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					int linkflag = 100;
					if ("".equals(starOper.trim()) || "".equals(endOper.trim()) || "down".equalsIgnoreCase(starOper) || "down".equalsIgnoreCase(endOper)) {
						linkflag = 0;
					}

					pingValue = String.valueOf(linkflag);
					LinkPerformanceDTO linkPerformanceDTO = new LinkPerformanceDTO();
					String name = link.getLinkName();
					int id = link.getId();
					allSpeedRate = String.valueOf(df.format(downperc + upperc));
					// 组装链路端口流速等信息
					linkPerformanceDTO.setId(id);
					linkPerformanceDTO.setName(name);
					linkPerformanceDTO.setStartNode(startnode.getIpAddress());
					linkPerformanceDTO.setEndNode(endnode.getIpAddress());
					linkPerformanceDTO.setStratIndex(startIndex);
					linkPerformanceDTO.setEndIndex(endIndex);
					linkPerformanceDTO.setUplinkSpeed(upspeed + "");
					linkPerformanceDTO.setDownlinkSpeed(downspeed + "");
					linkPerformanceDTO.setPingValue(pingValue);
					linkPerformanceDTO.setAllSpeedRate(allSpeedRate);
					linkPerformanceList.add(linkPerformanceDTO);
				}
			}
		}

		String field = getParaValue("field");

		String sorttype = getParaValue("sorttype");
		if (field != null) {
			if (sorttype == null || sorttype.trim().length() == 0) {
				sorttype = "asc";
			} else if ("asc".equals(sorttype)) {
				sorttype = "desc";
			} else if ("desc".equals(sorttype)) {
				sorttype = "asc";
			}
			linkPerformanceList = linkPerformanceManager.linkPerformanceListSort(linkPerformanceList, field, sorttype);
			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		}

		//
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("linkPerformanceList", linkPerformanceList);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	/**
	 * 生成链路带宽利用率曲线图
	 */
	public void createLinkBandwidthPecPic() {
		// 根据日期查询所有该日期范围内的链路
		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");
		String linkids = getParaValue("linkids");
		SysLogger.info("生成链路带宽利用率曲线图^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^linkids==" + linkids);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat detailSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		todate = todate + " 23:59:59";
		startdate = startdate + " 00:00:00";
		if (startdate == null || todate == null) {
			Calendar calendar = Calendar.getInstance();
			String currentDateString = sdf.format(calendar.getTime());// 当前日期
			// calendar.add(Calendar.DATE, -2); //日期减 如果不够减会将月变动
			String perWeekDateString = sdf.format(calendar.getTime());// 一周之前的日期
			todate = currentDateString + " 23:59:59";
			startdate = perWeekDateString + " 00:00:00";
		}
		LinkDao linkdao = new LinkDao();
		List<Link> linkList = null;
		try {
			linkList = linkdao.loadListByIds(linkids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (linkdao != null) {
				linkdao.close();
			}
		}
		// link的ID为key Link为value的hashtable
		Hashtable<String, Link> linkHash = new Hashtable();
		for (int i = 0; i < linkList.size(); i++) {
			linkHash.put(linkList.get(i).getId() + "", linkList.get(i));
		}

		Hashtable<String, List<Systemcollectdata>> linksHashtable = null;
		if (linkList != null) {
			linkdao = new LinkDao();
			try {
				linksHashtable = linkdao.getLinkBandwidthDataByLinkId(linkList, startdate, todate);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (linkdao != null) {
					linkdao.close();
				}
			}
		}
		// 得到日期的List集合
		List<Calendar> dateList = new ArrayList<Calendar>();
		// 得到带宽利用率的集合
		Hashtable<String, List<Systemcollectdata>> linksBandwidthHashtable = new Hashtable<String, List<Systemcollectdata>>();
		int index = 0;
		if (linksHashtable != null && !linksHashtable.isEmpty()) {
			Iterator<String> iterator = linksHashtable.keySet().iterator();
			while (iterator.hasNext()) {
				index++;
				String linkid = iterator.next();
				List<Systemcollectdata> linkidData = linksHashtable.get(linkid);
				List<Systemcollectdata> linkBandwidthIdData = new ArrayList<Systemcollectdata>();// 该link的带宽利用率的集合

				for (int i = 0; i < linkidData.size(); i++) {
					Systemcollectdata systemcollectdata = linkidData.get(i);
					linkBandwidthIdData.add(systemcollectdata);
					if (index == 1) {// 得到日期的list集合，只需要一条链路的信息即可
						dateList.add(systemcollectdata.getCollecttime());
					}
				}
				linksBandwidthHashtable.put(linkid, linkBandwidthIdData);
			}
		}
		StringBuffer linkBandwidthdivData = new StringBuffer();// 链路流速amchart图表xml数据
		linkBandwidthdivData.append("<chart>");
		linkBandwidthdivData.append("<series>");
		for (int i = 0; i < dateList.size(); i++) {
			linkBandwidthdivData.append("<value xid='");
			linkBandwidthdivData.append(i);
			linkBandwidthdivData.append("'>");
			linkBandwidthdivData.append(detailSdf.format(dateList.get(i).getTime()));
			linkBandwidthdivData.append("</value>");
		}
		linkBandwidthdivData.append("</series>");
		linkBandwidthdivData.append("<graphs>");
		// 带宽利用率
		if (linksBandwidthHashtable != null && !linksBandwidthHashtable.isEmpty()) {
			index = 0;
			Iterator<String> linkIdIterator = linksBandwidthHashtable.keySet().iterator();
			while (linkIdIterator.hasNext()) {
				index++;
				String linkid = linkIdIterator.next();
				List<Systemcollectdata> linkidData = linksBandwidthHashtable.get(linkid);
				linkBandwidthdivData.append("<graph gid='");
				linkBandwidthdivData.append(index);
				linkBandwidthdivData.append("' title='");
				linkBandwidthdivData.append(linkHash.get(linkid).getLinkName());
				linkBandwidthdivData.append("'> ");
				// 遍历该link的上行流速集合集合
				for (int j = 0; j < linkidData.size(); j++) {
					linkBandwidthdivData.append("<value xid='");
					linkBandwidthdivData.append(j);
					linkBandwidthdivData.append("'>");
					linkBandwidthdivData.append(linkidData.get(j).getThevalue());
					linkBandwidthdivData.append("</value>");
				}
				linkBandwidthdivData.append("</graph>");
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("linkBandwidthdivData", linkBandwidthdivData.toString());
		SysLogger.info(linkBandwidthdivData.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void createLinkRoadPic() {
		// 根据日期查询所有该日期范围内的链路
		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");
		String linkids = getParaValue("linkids");
		SysLogger.info("//////////////linkids==" + linkids);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat detailSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		todate = todate + " 23:59:59";
		startdate = startdate + " 00:00:00";
		if (startdate == null || todate == null) {
			Calendar calendar = Calendar.getInstance();
			String currentDateString = sdf.format(calendar.getTime());// 当前日期
			// calendar.add(Calendar.DATE, -2); //日期减 如果不够减会将月变动
			String perWeekDateString = sdf.format(calendar.getTime());// 一周之前的日期
			todate = currentDateString + " 23:59:59";
			startdate = perWeekDateString + " 00:00:00";
		}
		LinkDao linkdao = new LinkDao();
		List<Link> linkList = null;
		try {
			linkList = linkdao.loadListByIds(linkids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (linkdao != null) {
				linkdao.close();
			}
		}
		// link的ID为key Link为value的hashtable
		Hashtable<String, Link> linkHash = new Hashtable();
		for (int i = 0; i < linkList.size(); i++) {
			linkHash.put(linkList.get(i).getId() + "", linkList.get(i));
		}

		Hashtable<String, List<Systemcollectdata>> linksHashtable = null;
		if (linkList != null) {
			linkdao = new LinkDao();
			try {
				linksHashtable = linkdao.getLinkDataByLinkId(linkList, startdate, todate);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (linkdao != null) {
					linkdao.close();
				}
			}
		}
		// 得到日期的List集合
		List<Calendar> dateList = new ArrayList<Calendar>();
		// 得到上行流速的集合
		Hashtable<String, List<Systemcollectdata>> upLinksHashtable = new Hashtable<String, List<Systemcollectdata>>();
		// 得到下行流速的集合
		Hashtable<String, List<Systemcollectdata>> downLinksHashtable = new Hashtable<String, List<Systemcollectdata>>();
		int index = 0;
		if (linksHashtable != null && !linksHashtable.isEmpty()) {
			Iterator<String> iterator = linksHashtable.keySet().iterator();
			while (iterator.hasNext()) {
				index++;
				String linkid = iterator.next();
				List<Systemcollectdata> linkidData = linksHashtable.get(linkid);
				List<Systemcollectdata> uplinkidData = new ArrayList<Systemcollectdata>();// 该link的上行流速集合
				List<Systemcollectdata> downlinkidData = new ArrayList<Systemcollectdata>();// 该link的下行流速集合

				// 遍历出该ID的link的上行流速
				for (int i = 0; i < linkidData.size(); i++) {
					Systemcollectdata systemcollectdata = linkidData.get(i);
					if (systemcollectdata.getEntity().equalsIgnoreCase("up")) {
						uplinkidData.add(systemcollectdata);
						if (index == 1) {// 得到日期的list集合，只需要一条链路的信息即可
							dateList.add(systemcollectdata.getCollecttime());
						}
					}
					if (systemcollectdata.getEntity().equalsIgnoreCase("down")) {
						downlinkidData.add(systemcollectdata);
					}
				}
				upLinksHashtable.put(linkid, uplinkidData);
				downLinksHashtable.put(linkid, downlinkidData);
			}
		}
		StringBuffer linkUtilhdxdivData = new StringBuffer();// 链路流速amchart图表xml数据
		linkUtilhdxdivData.append("<chart>");
		linkUtilhdxdivData.append("<series>");
		for (int i = 0; i < dateList.size(); i++) {
			linkUtilhdxdivData.append("<value xid='");
			linkUtilhdxdivData.append(i);
			linkUtilhdxdivData.append("'>");
			linkUtilhdxdivData.append(detailSdf.format(dateList.get(i).getTime()));
			linkUtilhdxdivData.append("</value>");
		}
		linkUtilhdxdivData.append("</series>");
		linkUtilhdxdivData.append("<graphs>");
		// 上行流速
		if (upLinksHashtable != null && !upLinksHashtable.isEmpty()) {
			index = 0;
			Iterator<String> linkIdIterator = upLinksHashtable.keySet().iterator();
			while (linkIdIterator.hasNext()) {
				index++;
				String linkid = linkIdIterator.next();
				List<Systemcollectdata> linkidData = upLinksHashtable.get(linkid);
				linkUtilhdxdivData.append("<graph gid='");
				linkUtilhdxdivData.append(index);
				linkUtilhdxdivData.append("' title='");
				linkUtilhdxdivData.append(linkHash.get(linkid).getLinkName());
				linkUtilhdxdivData.append("上行流速");
				linkUtilhdxdivData.append("'> ");
				// 遍历该link的上行流速集合集合
				for (int j = 0; j < linkidData.size(); j++) {
					linkUtilhdxdivData.append("<value xid='");
					linkUtilhdxdivData.append(j);
					linkUtilhdxdivData.append("'>");
					linkUtilhdxdivData.append(linkidData.get(j).getThevalue());
					linkUtilhdxdivData.append("</value>");
				}
				linkUtilhdxdivData.append("</graph>");
			}
		}
		// 下行流速
		if (downLinksHashtable != null && !downLinksHashtable.isEmpty()) {
			index = 0;
			Iterator<String> linkIdIterator = downLinksHashtable.keySet().iterator();
			while (linkIdIterator.hasNext()) {
				index++;
				String linkid = linkIdIterator.next();
				List<Systemcollectdata> linkidData = downLinksHashtable.get(linkid);
				linkUtilhdxdivData.append("<graph gid='");
				linkUtilhdxdivData.append(index);
				linkUtilhdxdivData.append("' title='");
				linkUtilhdxdivData.append(linkHash.get(linkid).getLinkName());
				linkUtilhdxdivData.append("下行流速");
				SysLogger.info(linkHash.get(linkid).getLinkName() + "下行流速");
				linkUtilhdxdivData.append("'> ");
				// 遍历该link的上行流速集合集合
				for (int j = 0; j < linkidData.size(); j++) {
					linkUtilhdxdivData.append("<value xid='");
					linkUtilhdxdivData.append(j);
					linkUtilhdxdivData.append("'>");
					linkUtilhdxdivData.append(linkidData.get(j).getThevalue());
					linkUtilhdxdivData.append("</value>");
				}
				linkUtilhdxdivData.append("</graph>");
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("linkUtilhdxdivData", linkUtilhdxdivData.toString());
		SysLogger.info(linkUtilhdxdivData.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	// 写入ｘｍｌ文件
	public static void callWriteXmlFile(Document doc, Writer w, String encoding) {
		try {
			Source source = new DOMSource(doc);
			Result result = new StreamResult(w);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @description 验证登陆
	 * @author wangxiangyong
	 * @date Apr 23, 2013 11:02:44 AM
	 * @return void
	 */
	public void verifyUpdate() {
		String id = getParaValue("id");
		HaweitelnetconfDao telnetcfgdao = null;
		Huaweitelnetconf hmo = null;
		try {
			telnetcfgdao = new HaweitelnetconfDao();
			hmo = (Huaweitelnetconf) telnetcfgdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			telnetcfgdao.close();
		}
		if(hmo!=null){
		int connecttype = hmo.getConnecttype();
		String username =hmo.getUser();
		String pwd = hmo.getPassword();
		String suuser =hmo.getSuuser();
		String supassword = hmo.getSupassword();
		String ipAddress = hmo.getIpaddress();
		String promtp =hmo.getDefaultpromtp();
		String type =hmo.getDeviceRender();
		int port =hmo.getPort();

		this.verifyUser(connecttype, username, pwd, suuser, supassword, ipAddress, promtp, type, port);
		}
	}

	/**
	 * 
	 * @description ajax验证远程登录
	 * @author wangxiangyong
	 * @date Mar 12, 2013 9:25:11 AM
	 * @return void
	 */
	public void verifyLogin() {

		int connecttype = getParaIntValue("connecttype");
		String username = getParaValue("username");
		String pwd = getParaValue("pwd");
		String suuser = getParaValue("suuser");
		String supassword = getParaValue("supassword");
		String ipAddress = getParaValue("ipaddress");
		String promtp = getParaValue("promtp");
		String type = getParaValue("type");
		int port = getParaIntValue("port");

		this.verifyUser(connecttype, username, pwd, suuser, supassword, ipAddress, promtp, type, port);

	}

	public void verifyUser(int connecttype, String username, String pwd, String suuser, String supassword, String ipAddress, String promtp, String type, int port) {
		String result = "验证失败！";
		if (connecttype == 1) {
			boolean flag = false;
			boolean suFlag = true;
			SSHUtil ssh = new SSHUtil(ipAddress, port, username, pwd);
			try {

				flag = ssh.testLogin();
				if (flag && supassword != null && !supassword.equalsIgnoreCase("null") && !supassword.equals("")) {
					String[] commStr = { suuser, supassword };
					String temp = ssh.executeCmds(commStr);
					if (type.equals("h3c")) {
						if (temp.indexOf("Password:") > -1)
							suFlag = false;
					} else {
						if (temp.indexOf("#") > -1)
							suFlag = true;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ssh.disconnect();
			}

			if (flag)
				result = "验证成功！";
			if (!suFlag)
				result = "超级管理员未验证成功！";
		} else if (connecttype == 0) {
			String[] commStr = { "" };
			if (type.equals("cisco")) {// cisco
				CiscoTelnet telnet = new CiscoTelnet(ipAddress, username, pwd, port, suuser, supassword);
				if (telnet.login()) {
					result = "验证成功！";
				} 
			} else {
				BaseTelnet tvpn = null;
				try {
					tvpn = new BaseTelnet(ipAddress, username, pwd, port, suuser, supassword, promtp);
					result = tvpn.login();
				} catch (Exception e) {
					SysLogger.error("NetworkDeviceAjaxManager.verifyLogin", e);
				} finally {
					if (tvpn != null)
						tvpn.disconnect();
				}

			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", result);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	/**
	 * 得到链路列表
	 */
	public void getLinkList() {
		int nodeid = getParaIntValue("nodeid");
		int startId = getParaIntValue("startId");
		int endId = getParaIntValue("endId");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(nodeid);
		Host hostStart = (Host) PollingEngine.getInstance().getNodeByID(startId);
		Host hostEnd = (Host) PollingEngine.getInstance().getNodeByID(endId);
		// 得到设备的类别
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeStartdto = nodeUtil.creatNodeDTOByNode(hostStart);
		NodeDTO nodeEnddto = nodeUtil.creatNodeDTOByNode(hostEnd);

		Hashtable interfaceHash = host.getInterfaceHash();
		// Iterator interfaceIterator = interfaceHash.keySet().iterator();
		List<IfEntity> ifEntityList = LinkManager.getSortListByHash(interfaceHash);
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject json = JSONObject.fromObject(map);
		JSONArray jsonArray = JSONArray.fromObject(ifEntityList);
		json.put("ifEntityMap", jsonArray);
		json.put("startTypeAndId", nodeStartdto.getType() + hostStart.getId());
		json.put("endTypeAndId", nodeEnddto.getType() + hostEnd.getId());
		json.put("startIp", hostStart.getIpAddress());
		json.put("endIp", hostEnd.getIpAddress());
		out.print(json);
		out.flush();
	}

	/**
	 * 得到设备的详细信息
	 */
	public void getDeviceDetailInfo() {
		String deviceTypeAndId = getParaValue("id");
		if (deviceTypeAndId == null) {
			return;
		}
		// net10
		// 设备类型
		String type = deviceTypeAndId.substring(0, 2);
		String nodeType = null;
		// 设备号
		String nodeid = deviceTypeAndId.substring(3);
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
		int category = host.getCategory();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
		if (nodedto.getType().equals("net")) {
			nodeType = "网络设备";
		} else if (nodedto.getType().equals("host")) {
			nodeType = "服务器";
		} else if (nodedto.getType().equals("firewall")) {
			nodeType = "防火墙";
		}
		// 取出当前连通率和平均响应时间
		// ping和响应时间begin
		String avgresponse = "0";
		String maxresponse = "0";
		String responsevalue = "0";
		String pingconavg = "0";
		String maxpingvalue = "0";
		String pingvalue = "0";
		Hashtable connectUtilizationhash = new Hashtable();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		String starttime = time + " 00:00:00";
		String totime = time + " 23:59:59";
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			connectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime, totime);
			if (connectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) connectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			connectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ResponseTime", starttime, totime);
			if (connectUtilizationhash.get("avgpingcon") != null) {
				avgresponse = (String) connectUtilizationhash.get("avgpingcon");
				avgresponse = avgresponse.replace("毫秒", "").replaceAll("%", "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Vector memoryVector = new Vector();
		// cpu利用率
		String cpuValue = "0";
		// 内存利用率
		String memoryValue = "0";
		double memeryValueDouble = 0;
		double cpuValueDouble = 0;
		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipAllData = (Hashtable) sharedata.get(host.getIpAddress());
			Hashtable allpingdata = ShareData.getPingdata();
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
					if (cpu != null && cpu.getThevalue() != null) {
						cpuValueDouble = Double.valueOf(cpu.getThevalue());
						cpuValue = numberFormat.format(cpuValueDouble);
					}
				}
				memoryVector = (Vector) ipAllData.get("memory");
				// int allmemoryvalue = 0;
				// if (memoryVector != null && memoryVector.size() > 0) {
				// for (int si = 0; si < memoryVector.size(); si++) {
				// Memorycollectdata memorydata = (Memorycollectdata)
				// memoryVector.elementAt(si);
				// if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
				// // 利用率
				// if (category == 4 &&
				// memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory"))
				// {//服务器的情况
				// memeryValueDouble = Double.valueOf(memorydata.getThevalue());
				// }
				// if(category == 1 || category == 2 || category == 3){//网络设备的情况
				// allmemoryvalue = allmemoryvalue +
				// Integer.parseInt(memorydata.getThevalue());
				// if(si == memoryVector.size()-1){
				// memeryValueDouble = allmemoryvalue/memoryVector.size();
				// }
				// }
				// }
				// }
				// memoryValue = numberFormat.format(memeryValueDouble);
				// //#####HONGLI ADD 防火墙 的内存
				// if(category == 8){
				// //内存信息
				// memoryVector = (Vector) ipAllData.get("memory");
				// if (memoryVector != null && memoryVector.size() > 0) {
				// for (int i = 0; i < memoryVector.size(); i++) {
				// Memorycollectdata memorycollectdata = (Memorycollectdata)
				// memoryVector.get(i);
				// allmemoryvalue = allmemoryvalue +
				// Integer.parseInt(memorycollectdata.getThevalue());
				// }
				// memoryValue = (allmemoryvalue/memoryVector.size())+"";
				// }
				// }
				// //#### END
			}
			// }
		} else {
			// cpuValueDouble = new Double(new NetService(host.getId()+"",
			// nodedto.getType(), nodedto.getSubtype()).getCurrCpuAvgInfo());
			// cpuValue = Double.valueOf(cpuValueDouble).intValue()+"";
			CpuInfoService cpuInfoService = new CpuInfoService(nodedto.getId() + "", nodedto.getType(), nodedto.getSubtype());
			Vector cpuV = cpuInfoService.getCpuInfo();
			if (cpuV != null && cpuV.size() > 0) {
				CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuValueDouble = new Double(cpu.getThevalue());
				}
			}
			cpuValue = Double.valueOf(cpuValueDouble).intValue() + "";
			List memoryList = null;
			// 获取内存信息
			memoryList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrMemoryInfo();
			if (memoryList != null && memoryList.size() > 0) {
				for (int i = 0; i < memoryList.size(); i++) {
					Memorycollectdata memorycollectdata = new Memorycollectdata();
					NodeTemp nodetemp = (NodeTemp) memoryList.get(i);
					memorycollectdata.setEntity(nodetemp.getSubentity());
					memorycollectdata.setSubentity(nodetemp.getSindex());
					memorycollectdata.setThevalue(nodetemp.getThevalue());
					memoryVector.add(memorycollectdata);
				}
			}
		}

		// 根据设备的类型的不同，存储集合的结构也不一样
		if (nodedto.getType().equals("net")) {
			double allmemoryvalue = 0;
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int i = 0; i < memoryVector.size(); i++) {
					Memorycollectdata memorycollectdata = (Memorycollectdata) memoryVector.get(i);
					allmemoryvalue = allmemoryvalue + Integer.parseInt(memorycollectdata.getThevalue());
				}
				memeryValueDouble = Math.round(allmemoryvalue / memoryVector.size());
				memoryValue = numberFormat.format(memeryValueDouble);
			}
		}else if (nodedto.getType().equals("firewall")) {
				double allmemoryvalue = 0;
				if (memoryVector != null && memoryVector.size() > 0) {
					for (int i = 0; i < memoryVector.size(); i++) {
						Memorycollectdata memorycollectdata = (Memorycollectdata) memoryVector.get(i);
						allmemoryvalue = allmemoryvalue + Integer.parseInt(memorycollectdata.getThevalue());
					}
					memeryValueDouble = Math.round(allmemoryvalue / memoryVector.size());
					memoryValue = numberFormat.format(memeryValueDouble);
				}
		} else if (nodedto.getType().equals("host")) {
			// 得到内存利用率
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int i = 0; i < memoryVector.size(); i++) {
					Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(i);
					if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
						memoryValue = Math.round(Float.parseFloat(memorydata.getThevalue())) + "";
					}
				}
			}
		}

		// 设定cpu和内存的告警颜色
		String cpuValueColor = "";
		String memoryValueColor = "";
		NodeMonitorDao nodeMonitorDao = new NodeMonitorDao();
		List nodeMonitorList = null;
		try {
			nodeMonitorList = nodeMonitorDao.loadByNodeID(host.getId());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			nodeMonitorDao.close();
		}
		double memoryvalueDouble = Double.parseDouble(memoryValue);
		int cpuvalue = new Double(cpuValueDouble).intValue();
		if (nodeMonitorList != null) {
			for (int j = 0; j < nodeMonitorList.size(); j++) {
				NodeMonitor nodeMonitor = (NodeMonitor) nodeMonitorList.get(j);
				if ("cpu".equals(nodeMonitor.getCategory())) {
					if (cpuvalue > nodeMonitor.getLimenvalue2()) {
						cpuValueColor = "red";
					} else if (cpuvalue > nodeMonitor.getLimenvalue1()) {
						cpuValueColor = "orange";
					} else if (cpuvalue > nodeMonitor.getLimenvalue0()) {
						cpuValueColor = "yellow";
					} else {
						cpuValueColor = "green";
					}
				}
				if ("memory".equals(nodeMonitor.getCategory())) {
					if (memoryvalueDouble > nodeMonitor.getLimenvalue2()) {
						memoryValueColor = "red";
					} else if (memoryvalueDouble > nodeMonitor.getLimenvalue1()) {
						memoryValueColor = "orange";
					} else if (memoryvalueDouble > nodeMonitor.getLimenvalue0()) {
						memoryValueColor = "yellow";
					} else {
						memoryValueColor = "green";
					}
				}
			}
		}
		if (pingconavg == null) {
			pingconavg = "0";
		}
		// 使用jfreechart生成一张连通率的饼状图
		// String pingChartName = host.getIpAddress()+"_ping";
		// ChartGraph chartGraph = new ChartGraph();
		// DefaultPieDataset dpd = new DefaultPieDataset();
		// dpd.setValue("可用率", Double.parseDouble(pingconavg));
		// dpd.setValue("不可用率", 100 - Double.parseDouble(pingconavg));
		// chartGraph.pie("", dpd, pingChartName, 60, 60);
		// pie("", dpd, pingChartName, 130, 90);

		String result = "true";
		Map<String, Object> map = new HashMap<String, Object>();
		// #####测试的数据###################
		// map.put("pingChartName", pingChartName);
		map.put("deviceName", host.getAlias());
		map.put("result", result);
		map.put("nodeType", nodeType);
		map.put("ipaddress", host.getIpAddress());
		map.put("pingconavg", pingconavg);
		map.put("avgresponse", avgresponse);
		map.put("cpuUnusedvalue", (100 - cpuvalue) + "");
		map.put("cpuvalue", cpuvalue + "");
		map.put("memoryUnusedvalue", (100 - memoryvalueDouble) + "");
		map.put("memoryvalue", memoryValue + "");
		map.put("cpuValueColor", cpuValueColor + "");
		map.put("memoryValueColor", memoryValueColor + "");
		// #####测试的数据###################
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	// public String pie(String title, DefaultPieDataset piedata, String
	// report_info, int w, int h) {
	// JFreeChart chart = ChartFactory.createPieChart(title, piedata, false,
	// true, true);
	// // 设定图片标题
	// chart.setTitle(new TextTitle(title, new Font("隶书", Font.ITALIC, 15)));
	// // 设定背景
	// chart.setBackgroundPaint(Color.white);
	// PiePlot pie = (PiePlot) chart.getPlot();
	// pie.setSectionPaint(1,Color.ORANGE);
	// pie.setSectionPaint(0,Color.GREEN);
	//	
	// pie.setLabelGenerator(new StandardPieItemLabelGenerator("{0} = {2}",
	// NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
	// pie.setBackgroundPaint(Color.white);
	// pie.setBackgroundAlpha(0.6f);
	// pie.setForegroundAlpha(0.90f);
	// String
	// path=ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/";
	// String fileName = path+ report_info + ".png";
	// try {
	// ChartUtilities.saveChartAsPNG(new File(fileName), chart, w,
	// h);//宽1000，高600
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return fileName;
	// }
	public void importExcel() {
		String flag = "";
		String path = getParaValue("files");
		try {
			path = new String(path.getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		ExcelImportUtil util = new ExcelImportUtil();
		flag = util.loadAllData(path);
		Map<String, String> map = new HashMap<String, String>();
		map.put("isSuccess", flag);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("importExcel")) {
			importExcel();
		}
		if (action.equals("editnodepower")) {
			editnodepower();
		}
		// 动力环境模块配置
		if (action.equals("modifyPowerAlarmlevel")) {
			modifyPowerAlarmlevel();
		}
		// 端口配置
		if (action.equals("modifyAlarmlevel")) {
			modifyAlarmlevel();
		}

		if (action.equals("editnodeport")) {
			editnodeport();
		}
		if (action.equals("savePerforIndex")) {
			savePerforIndex();
		}
		if (action.equals("loadPeforIndex")) {
			loadPeforIndex();
		}

		if (action.equals("deletePerforIndex")) {
			deletePerforIndex();
		}
		if (action.equals("deletedkdbtemplet")) {
			deleteDkdbTemplet();
		}
		if (action.equals("showContent")) {
			showContent();
		}
		if (action.equals("showFileContent")) {
			showFileContent();
		}
		if (action.equals("setBaseLine")) {
			setBaseLine();
		}
		if (action.equals("exeCmd")) {
			exeCmd();
		}
		if (action.equals("loadFile")) {
			loadFile();
		}
		if (action.equals("loadFileFromMenu")) {
			loadFileFromMenu();
		}

		if (action.equals("createLinkRoadPic")) {
			createLinkRoadPic();
		}
		if (action.equals("linkRoadList")) {
			linkRoadList();
		}
		if (action.equals("createLinkBandwidthPecPic")) {
			createLinkBandwidthPecPic();
		}
		if (action.equals("verifyLogin")) {
			verifyLogin();
		}
		if (action.equals("getLinkList")) {
			getLinkList();
		}
		if (action.equals("getDeviceDetailInfo")) {
			getDeviceDetailInfo();
		}
		if (action.equals("savedkdbtemplate")) {
			saveDkdbTemplate();
		}
		if (action.equals("loaddkdbtemplet")) {
			loadDkdbTemplet();
		}
		if (action.equals("verifyUpdate")) {
			verifyUpdate();
		}
		if (action.equals("getAliasById")) {
			getAliasById();
		}
		if (action.equals("netAjaxUpdate")) {
			SysLogger.info("###########开始刷新数据 ######################");
			String runmodel = PollingEngine.getCollectwebflag();
			String pingconavg = "";
			double cpuvalue = 0;
			String tmp = request.getParameter("tmp");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			if (host == null) {
				return;
			}

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}
			int percent1 = Double.valueOf(pingconavg).intValue();
			int percent2 = 100 - percent1;
			Map<String, Integer> map = new HashMap<String, Integer>();

			map.put("percent1", percent1);
			map.put("percent2", percent2);

			// }
			Vector cpuV = null;
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
				if (ipAllData != null) {
					cpuV = (Vector) ipAllData.get("cpu");
				}
			} else {
				// 采集与访问是分离模式
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
				CpuInfoService cpuInfoService = new CpuInfoService(nodedto.getId() + "", nodedto.getType(), nodedto.getSubtype());
				cpuV = cpuInfoService.getCpuInfo();
			}
			if (cpuV != null && cpuV.size() > 0) {
				CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			int cpuper = Double.valueOf(cpuvalue).intValue();
			String picip = CommonUtil.doip(host.getIpAddress());
			CreatePiePicture _cpp = new CreatePiePicture();
			_cpp.createAvgPingPic(picip, Double.valueOf(pingconavg));

			// 生成CPU仪表盘
			CreateMetersPic cmp = new CreateMetersPic();
			cmp.createCpuPic(picip, cpuper);

			map.put("cpuper", cpuper);
			JSONObject json = JSONObject.fromObject(map);
			out.print(json);
			out.flush();
			// System.out.println(json);
			out.close();
		} else if (action.equals("modifyIpAlias")) {
			String ipalias = getParaValue("ipalias");
			String ipaddress = getParaValue("ipaddress");
			// 更新数据库
			IpAliasDao ipAliasDao = new IpAliasDao();
			try {
				ipAliasDao.updateIpAlias(ipaddress, ipalias);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ipAliasDao.close();
			}
		} else if (action.equals("updatemac")) {
			String mac = getParaValue("mac");
			String id = getParaValue("id");
			int tmp = Integer.parseInt(id);

			HostNodeDao dao = new HostNodeDao();
			try {
				dao.UpdateAixMac(tmp, mac);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		
		
	}
	
public void readLine() {
	int flag=getParaIntValue("flag");
	String path="";
	if(flag==1){
		path=getParaValue("test1");
		path="D:\\temp\\a.txt";
	}else if(flag==2){
		path=getParaValue("test2");
		path="D:\\temp\\b.txt";
	}
	StringBuilder sb = new StringBuilder();
	File file1=new File(path);
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file1), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String line;
		try {
			while((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("<br>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(path+"==================="+sb.toString()+"::");
	JSONObject obj = new JSONObject();
	obj.put("item", sb.toString());
	out.print(obj);
	out.flush();
}
	public void modifyAlarmlevel() {
		String id = getParaValue("id");
		String alarmvalue = getParaValue("alarmvalue");
		// 更新数据库
		PortconfigDao portconfigDao = new PortconfigDao();
		try {
			portconfigDao.updatePortConfigOfAlarmlevelByID(id, alarmvalue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portconfigDao.close();
		}
	}

	public void modifyPowerAlarmlevel() {
		String id = getParaValue("id");
		String alarmlevel = getParaValue("alarmlevel");
		// 更新数据库
		EnvConfigDao configDao = new EnvConfigDao();
		try {
			configDao.updateAlarmlevelByID(id, alarmlevel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configDao.close();
		}
	}
	
	//获取起止设备的别名  --by hipo
	private void getAliasById() {
		String startId = getParaValue("startId");
		String endId = getParaValue("endId");
		HostNodeDao dao1 = new HostNodeDao();
		HostNodeDao dao2 = new HostNodeDao();
		String returnString = "";
		try {
			HostNode startNode = dao1.loadHost(Integer.parseInt(startId));
			HostNode endNode = dao2.loadHost(Integer.parseInt(endId));
			returnString = startNode.getAlias() + "," + endNode.getAlias();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			dao1.close();
			dao2.close();
		}
		out.print(returnString);
	}

}
