package com.afunms.application.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.Dnsmonitor_historyDao;
import com.afunms.application.dao.Dnsmonitor_realtimeDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.Dnsmonitor_realtime;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.InitCoordinate;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.ApacheLoader;
import com.afunms.polling.loader.DNSLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.ApacheDataCollector;
import com.afunms.polling.task.DnsDataCollector;
import com.afunms.polling.task.TaskXml;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.DocumentException;

public class DnsManager extends BaseManager implements ManagerInterface {
	/**
	 * 查询dns信息
	 * 
	 * @return
	 */
	private String list() {
		int status = 0;
		List ips = new ArrayList();
		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		if (bids == null)
			bids = "";
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}
		List list = null;
		DnsConfigDao configdao = new DnsConfigDao();
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getDnsByBID(rbids);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			configdao.close();
		}

		request.setAttribute("list", list);
		// System.out.println(status);
		request.setAttribute("status", status);
		setTarget("/application/dns/dnsconfiglist.jsp");
		return "/application/dns/dnsconfiglist.jsp";
		// return list(configdao);
	}

	/**
	 * snow 增加前将供应商查找到
	 * 
	 * @return
	 */
	private String ready_add() {
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);
		return "/application/dns/add.jsp";
	}

	/**
	 * 添加一条dns信息
	 * 
	 * @return
	 */
	private String add() {
		DnsConfig vo = new DnsConfig();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setHostip(getParaValue("hostip"));
		vo.setHostinter(getParaIntValue("hostinter"));
		vo.setDns(getParaValue("dns"));
		vo.setDnsip(getParaValue("dnsip"));
		vo.setFlag(getParaIntValue("_flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendemail(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20

		vo.setNetid(getParaValue("bid"));
		DnsConfigDao dao = new DnsConfigDao();
		try {
			dao.save(vo);
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("17"));
			/* snow add end */
			// 保存应用
			HostApplyManager.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// //////////////////////
		CreateTableManager ctable = new CreateTableManager();
		DBManager conn = null;

		try {
			conn = new DBManager();
			String allipstr = SysUtil.doip(vo.getHostip());
			ctable.createTable(conn, "dnsping", allipstr, "dnsping");// Ping
			ctable.createTable(conn, "dnspingh", allipstr, "dnspingh");// Ping
			ctable.createTable(conn, "dnspingd", allipstr, "dnspingd");// Ping

			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(vo.getId() + "", AlarmConstant.TYPE_MIDDLEWARE, "dns");
			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(vo.getId() + "", AlarmConstant.TYPE_MIDDLEWARE, "dns", "4", 4);

		} catch (Exception e) {
			SysLogger.error("DnsManager add() error", e);
			e.printStackTrace();
		} finally {
			dao.close();
			if (conn != null) {
				conn.close();
			}
		}
		// /////////////////////////////////////
		try {
			dao = new DnsConfigDao();
			List list = dao.loadAll();
			if (list == null)
				list = new ArrayList();
			ShareData.setDnslist(list);
			DNSLoader dnsloader = new DNSLoader();
			dnsloader.clearRubbish(list);
		} catch (Exception e) {
			SysLogger.error("DnsManager add() error", e);
		} finally {
			dao.close();
		}
		// 在轮询线程中增加被监视节点
		DNSLoader loader = new DNSLoader();
		try {
			loader.loadOne(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}

		return "/dns.do?action=list&jp=1";
	}

	/**
	 * 删除一条dns信息记录
	 * 
	 * @return
	 */
	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		DnsConfig vo = new DnsConfig();
		List list = new ArrayList();
		DnsConfigDao configdao = null;
		if (ids != null && ids.length > 0) {
			configdao = new DnsConfigDao();
			try {
				configdao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			/* snow add 2010-5-20 */
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			for (String str : ids) {
				Node node = PollingEngine.getInstance().getDnsByID(Integer.parseInt(str));
				// 删除应用
				HostApplyDao hostApplyDao = null;
				try {
					hostApplyDao = new HostApplyDao();
					hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'dns' and nodeid = '" + str + "'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (hostApplyDao != null) {
						hostApplyDao.close();
					}
				}
				tg.deleteTimeGratherConfig(str, tg.getObjectType("17"));
			}
			/* snow end */

		}
		// 删除DNS在临时表里中存储的数据
		String[] nmsTempDataTables = { "nms_dns_temp" };
		CreateTableManager createTableManager = new CreateTableManager();
		createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);

		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new DnsConfigDao();
			try {
				list = configdao.getDnsByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			configdao = new DnsConfigDao();
			List _list = configdao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setDnslist(_list);
			DNSLoader dnsloader = new DNSLoader();
			dnsloader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			configdao.close();
		}
		// /////////
		String allipstr = SysUtil.doip(vo.getHostip());
		CreateTableManager ctable = new CreateTableManager();
		DBManager conn = null;
		try {
			conn = new DBManager();
			ctable.deleteTable(conn, "dnsping", allipstr, "dnsping");// Ping
			ctable.deleteTable(conn, "dnspingh", allipstr, "dnspingh");// Ping
			ctable.deleteTable(conn, "dnspingd", allipstr, "dnspingd");// Ping
		} catch (Exception e) {
			SysLogger.error("DnsManger delete() error", e);
		} finally {
			if (conn != null) {
				conn.close();
			}

		}
		request.setAttribute("list", list);
		return list();
	}

	/**
	 * snow 修改cics前获得其数据库中的关联数据，采集时间
	 * 
	 * @return url
	 */
	private String ready_edit() {
		DaoInterface dao = new DnsConfigDao();
		setTarget("/application/dns/edit.jsp");
		String jsp = "";
		try {
			jsp = readyEdit(dao);
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("17"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsp;
	}

	/**
	 * 修改DNS信息
	 * 
	 * @return
	 */
	private String update() {
		DnsConfig vo = new DnsConfig();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		vo.setId(getParaIntValue("id"));
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setHostip(getParaValue("hostip"));
		vo.setHostinter(getParaIntValue("hostinter"));
		vo.setDns(getParaValue("dns"));
		vo.setDnsip(getParaValue("dnsip"));
		vo.setFlag(getParaIntValue("_flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendemail(getParaValue("sendphone"));
		vo.setNetid(getParaValue("netid"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20

		vo.setNetid(getParaValue("bid"));
		DnsConfigDao configdao = new DnsConfigDao();
		try {
			try {
				configdao.update(vo);
				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("17"));
				/* snow add end */
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			if (bids == null)
				bids = "";
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new DnsConfigDao();
			try {
				list = configdao.getDnsByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("list", list);
		try {
			configdao = new DnsConfigDao();
			List _list = configdao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setDnslist(_list);
			DNSLoader dnsloader = new DNSLoader();
			dnsloader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			configdao.close();
		}
		return list();
	}

	/**
	 * 添加DNS监视信息
	 * 
	 * @return
	 */
	private String addalert() {
		DnsConfig vo = new DnsConfig();
		DnsConfigDao configdao = new DnsConfigDao();

		List list = new ArrayList();

		try {
			vo = (DnsConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(1);

			configdao.update(vo);
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			if (bids == null)
				bids = "";
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new DnsConfigDao();

			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getDnsByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		DNSLoader loader = new DNSLoader();
		loader.loading();
		request.setAttribute("list", list);
		return "/application/dns/dnsconfiglist.jsp";
	}

	/**
	 * 取消DNS监视信息
	 * 
	 * @return
	 */
	private String cancelalert() {
		DnsConfig vo = new DnsConfig();
		DnsConfigDao configdao = new DnsConfigDao();
		DBVo dbvo = new DBVo();
		DBDao dao = new DBDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		try {
			vo = (DnsConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(0);
			// configdao = new DnsConfigDao();
			configdao.update(vo);
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			if (bids == null)
				bids = "";
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new DnsConfigDao();

			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getDnsByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		DNSLoader loader = new DNSLoader();
		loader.loading();
		request.setAttribute("list", list);
		return "/application/dns/dnsconfiglist.jsp";
	}

	/**
	 * 
	 * 监控信息
	 * 
	 * @return
	 */

	private String detail() {
		Integer id = getParaIntValue("id");
		System.out.println(id);
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
		}
		request.setAttribute("id", id);
		request.setAttribute("dnsip", dnsip);
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_realtimeDao realtimedao = new Dnsmonitor_realtimeDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
		String connrate = "0";
		String validrate = "0";
		String freshrate = "0";
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		nowdate.getHours();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}
		String from_hour = getParaValue("from_hour");
		if (from_hour == null) {
			from_hour = "00";
			request.setAttribute("from_hour", from_hour);
		}
		String to_hour = getParaValue("to_hour");
		if (to_hour == null) {
			to_hour = nowdate.getHours() + "";
			request.setAttribute("to_hour", to_hour);
		}
		String starttime = from_date1 + " " + from_hour + ":00:00";
		String totime = to_date1 + " " + to_hour + ":59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			// configdao = new WebConfigDao();
			try {
				urllist = configdao.getDnsByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();

			if (urllist.size() > 0 && queryid == null) {
				Object obj = urllist.get(0);
			}
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			conn_name = queryid + "urlmonitor-conn";
			valid_name = queryid + "urlmonitor-valid";
			fresh_name = queryid + "urlmonitor-refresh";
			wave_name = queryid + "urlmonitor-rec";
			delay_name = queryid + "urlmonitor-delay";

			List urlList = null;
			try {
				urlList = realtimedao.getByDNSId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Dnsmonitor_realtime) urlList.get(0)).getMon_time();
			}
			int interval = 0;
			// TaskXmlUtil taskutil = new TaskXmlUtil("urltask");
			try {
				// Session session = this.beginTransaction();
				List numList = new ArrayList();
				TaskXml taskxml = new TaskXml();
				numList = taskxml.ListXml();
				for (int i = 0; i < numList.size(); i++) {
					Task task = new Task();
					BeanUtils.copyProperties(task, numList.get(i));
					if (task.getTaskname().equals("urltask")) {
						interval = task.getPolltime().intValue();
						// numThreads = task.getPolltime().intValue();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			DateE de = new DateE();
			if (last == null) {
				last = new GregorianCalendar();
				flag = 1;
			}
			lasttime = de.getDateDetail(last);
			last.add(Calendar.MINUTE, interval);
			nexttime = de.getDateDetail(last);

			int hour = 1;
			if (getParaValue("hour") != null) {
				hour = Integer.parseInt(getParaValue("hour"));
			} else {
				request.setAttribute("hour", "1");
				// urlconfForm.setHour("1");
			}

			InitCoordinate initer = new InitCoordinate(new GregorianCalendar(), hour, 1);
			// Minute[] minutes=initer.getMinutes();
			TimeSeries ss1 = new TimeSeries("", Minute.class);
			TimeSeries ss2 = new TimeSeries("", Minute.class);

			// ss.add()
			TimeSeries[] s = new TimeSeries[1];
			TimeSeries[] s_ = new TimeSeries[1];
			// Vector wave_v = historyManager.getInfo(queryid,initer);
			Vector wave_v = null;
			try {
				wave_v = historydao.getByDnsid(queryid, starttime, totime, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// historydao.close();
			}
			if (wave_v == null)
				wave_v = new Vector();
			for (int i = 0; i < wave_v.size(); i++) {
				Hashtable ht = (Hashtable) wave_v.get(i);
				double conn = Double.parseDouble(ht.get("conn").toString());
				String time = ht.get("mon_time").toString();
				ss1.addOrUpdate(new Minute(sdf1.parse(time)), conn);
			}
			s[0] = ss1;
			s_[0] = ss2;
			ChartGraph cg = new ChartGraph();
			// cg.timewave(s,"时间","连通","",wave_name,600,120);
			// cg.timewave(s_,"时间","时延(ms)","",delay_name,600,120);
			// p_draw_line(s,"","url"+queryid+"ConnectUtilization",740,120);

			// 是否连通
			String conn[] = new String[2];
			if (flag == 0)
				conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
			else {
				conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
			}
			String[] key1 = { "连通", "未连通" };
			drawPiechart(key1, conn, "", conn_name);
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
			realtimedao.close();
		}
		collectDNSData(dc);
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("valid_name", valid_name);
		request.setAttribute("fresh_name", fresh_name);
		request.setAttribute("wave_name", wave_name);
		request.setAttribute("delay_name", delay_name);
		request.setAttribute("connrate", connrate);
		request.setAttribute("validrate", validrate);
		request.setAttribute("freshrate", freshrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		return "/application/dns/detail.jsp";
	}

	public void collectDNSData(DnsConfig dc) {
		Hashtable dnsDataHash = new Hashtable();
		List array = new ArrayList();
		String str = "";
		String defaultStr = "";
		String hostip = "";
		int zhuangtai = 0;
		String dnsip = "";
		String dns = "";
		String aaa = "";
		String primary = "";
		String responsible = "";
		String serial = "";
		String refresh = "";
		String retry = "";
		String expire = "";
		String dfault = "";
		String time = "";
		List mx = new ArrayList();
		List ns = new ArrayList();
		List cache = new ArrayList();
		// 得到服务器名 以及ip地址
		Process process = null;
		BufferedReader bf = null;
		try {
			process = Runtime.getRuntime().exec("cmd /c nslookup " + dc.getHostip() + "");
			bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((str = bf.readLine()) != null) {
				array.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process != null) {
				process.destroy();
			}
		}
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).toString().contains("Name:")) {
				defaultStr = array.get(i).toString().substring(6);
				hostip = dc.getHostip();
				zhuangtai = 1;
			}
		}

		// 得到域名所对应的地址
		List array1 = new ArrayList();
		List arr1 = new ArrayList();
		String str1 = null;
		long lasting = System.currentTimeMillis();
		Process process1 = null;
		BufferedReader bf1 = null;
		try {
			process1 = Runtime.getRuntime().exec("cmd /c nslookup " + dc.getDns() + "");
			bf1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
			while ((str1 = bf1.readLine()) != null) {
				array1.add(str1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf1 != null) {
				try {
					bf1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process1 != null) {
				process1.destroy();
			}
		}
		for (int j = 0; j < array1.size(); j++) {
			if (array1.get(j).toString().contains("Addresses:")) {
				dnsip = array1.get(j).toString().substring(10);
				dns = dc.getDns();
			}
			if (array1.get(j).toString().contains("Address:")) {
				arr1.add(array1.get(j));
				if (arr1.size() == 2) {
					dnsip = array1.get(j).toString().substring(8);
					dns = dc.getDns();
				}
			}
		}
		time = "响应时间：" + (System.currentTimeMillis() - lasting) + "ms";

		// A记录
		List array2 = new ArrayList();
		List arr2 = new ArrayList();
		Process process2 = null;
		BufferedReader bf2 = null;
		String str2 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(4);
		} else {
			dns = dc.getDns();
		}
		try {
			process2 = Runtime.getRuntime().exec("cmd /c nslookup -qt=a " + dns + " " + dc.getHostip() + "");
			bf2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
			while ((str2 = bf2.readLine()) != null) {
				array2.add(str2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf2 != null) {
				try {
					bf2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process2 != null) {
				process2.destroy();
			}
		}
		for (int j = 0; j < array2.size(); j++) {
			if (array2.get(j).toString().contains("Addresses:")) {
				aaa = array2.get(j).toString().substring(10);
			}
			if (array2.get(j).toString().contains("Address:")) {
				arr2.add(array2.get(j));
				if (arr2.size() == 2) {
					aaa = array2.get(j).toString().substring(8);
				}
			}
		}

		// HINFO硬件配置信息
		List array3 = new ArrayList();
		String str3 = null;
		Process process3 = null;
		BufferedReader bf3 = null;
		try {
			process3 = Runtime.getRuntime().exec("cmd /c nslookup -qt=hinfo " + dns + " " + dc.getHostip() + "");
			bf3 = new BufferedReader(new InputStreamReader(process3.getInputStream()));
			while ((str3 = bf3.readLine()) != null) {
				array3.add(str3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf3 != null) {
				try {
					bf3.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process3 != null) {
				process3.destroy();
			}
		}
		for (int j = 0; j < array3.size(); j++) {
			if (array3.get(j).toString().contains("primary")) {
				primary = "主要名字服务器:" + array3.get(j).toString().substring(22);
			}
			if (array3.get(j).toString().contains("responsible")) {
				responsible = "邮件地址:" + array3.get(j).toString().substring(24);
			}
			if (array3.get(j).toString().contains("serial")) {
				serial = "文件版本:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("refresh")) {
				refresh = "重刷新时间:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("retry")) {
				retry = "重试时间:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("expire")) {
				expire = "有效时间:" + array3.get(j).toString().substring(10);
			}
			if (array3.get(j).toString().contains("default")) {
				dfault = "TTL设置:" + array3.get(j).toString().substring(14);
			}
		}
		// MX记录
		List array4 = new ArrayList();
		String str4 = null;
		BufferedReader bf4 = null;
		Process process4 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(4);
		} else {
			dns = dc.getDns();
		}
		try {
			process4 = Runtime.getRuntime().exec("cmd /c nslookup -qt=mx " + dns + " " + dc.getHostip() + "");
			bf4 = new BufferedReader(new InputStreamReader(process4.getInputStream()));
			while ((str4 = bf4.readLine()) != null) {
				array4.add(str4);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf4 != null) {
				try {
					bf4.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process4 != null) {
				process4.destroy();
			}
		}
		List array5 = new ArrayList();
		for (int j = 0; j < array4.size(); j++) {
			if (array4.get(j).toString().contains(dns)) {
				array5.add(array4.get(j).toString());
				mx = array5;
			}
		}

		// NS记录
		List array6 = new ArrayList();
		String str6 = null;
		Process process6 = null;
		BufferedReader bf6 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(6);
		} else {
			dns = dc.getDns();
		}
		try {
			process6 = Runtime.getRuntime().exec("cmd /c nslookup -qt=ns " + dns + " " + dc.getHostip() + "");
			bf6 = new BufferedReader(new InputStreamReader(process6.getInputStream()));
			while ((str6 = bf6.readLine()) != null) {
				array6.add(str6);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf6 != null) {
				try {
					bf6.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process6 != null) {
				process6.destroy();
			}
		}
		List array7 = new ArrayList();
		for (int j = 0; j < array6.size(); j++) {
			if (array6.get(j).toString().contains(dns)) {
				array7.add(array6.get(j).toString());
				ns = array7;
			}
		}
		// 缓存记录
		// NS记录
		List array8 = new ArrayList();
		String str8 = null;
		if (dc.getDns().contains("www")) {
			dns = dc.getDns().substring(8);
		} else {
			dns = dc.getDns();
		}
		Process process8 = null;
		BufferedReader bf8 = null;
		try {
			process8 = Runtime.getRuntime().exec("cmd /c nslookup -d3 " + dc.getHostip() + "");
			bf8 = new BufferedReader(new InputStreamReader(process8.getInputStream()));
			while ((str8 = bf8.readLine()) != null) {
				array8.add(str8);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf8 != null) {
				try {
					bf8.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (process8 != null) {
				process8.destroy();
			}
		}
		for (int k = 0; k < array8.size(); k++) {
			if (array8.get(k).toString().contains("opcode")) {
			}
		}
		cache = array8;
		dnsDataHash.put("time", time);
		dnsDataHash.put("default", defaultStr);
		dnsDataHash.put("hostip", hostip);
		dnsDataHash.put("zhuangtai", zhuangtai);
		dnsDataHash.put("dnsip", dnsip);
		dnsDataHash.put("dns", dns);
		dnsDataHash.put("aaa", aaa);
		dnsDataHash.put("primary", primary);
		dnsDataHash.put("responsible", responsible);
		dnsDataHash.put("serial", serial);
		dnsDataHash.put("refresh", refresh);
		dnsDataHash.put("retry", retry);
		dnsDataHash.put("expire", expire);
		dnsDataHash.put("dfault", dfault);
		dnsDataHash.put("mx", mx);
		dnsDataHash.put("ns", ns);
		dnsDataHash.put("cache", cache);
		// 存入内存
		ShareData.getAllDnsData().put(dc.getId(), dnsDataHash);
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return ready_add();
		if (action.equals("add"))
			return add();
		if (action.equals("delete"))
			return delete();
		if (action.equals("ready_edit"))
			return ready_edit();
		if (action.equals("update"))
			return update();
		if (action.equals("addalert"))
			return addalert();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("detail"))
			return detail();
		if (action.equals("system"))
			return system();
		if (action.equals("addr"))
			return addr();
		if (action.equals("perform"))
			return perform();
		if (action.equals("mail"))
			return mail();
		if (action.equals("name"))
			return name();
		if (action.equals("cache"))
			return cache();
		if (action.equals("alarm"))
			return alarm();
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		if (action.equals("showPingReport"))
			return showPingReport();
		if (action.equals("eventReport"))
			return eventReport();
		if (action.equals("allReport"))
			return allReport();

		if (action.equals("downloadEventReport"))
			return downloadEventReport();
		if (action.equals("downloadAllReport"))
			return downloadAllReport();

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private void drawPiechart(String[] keys, String[] values, String chname, String enname) {
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for (int i = 0; i < keys.length; i++) {
			piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname, piedata, enname, 300, 120);
	}

	private void drawchart(Minute[] minutes, String keys, String[][] values, String chname, String enname) {
		try {
			// int size = keys.length;
			TimeSeries[] s2 = new TimeSeries[1];
			String[] keymemory = new String[1];
			String[] unitMemory = new String[1];
			// for(int i = 0 ; i < size ; i++){
			String key = keys;
			// System.out.println("in drawchart -------------- i="+i+"
			// key="+key+" ");
			TimeSeries ss2 = new TimeSeries(key, Minute.class);
			String[] hmemory = values[0];
			arrayTochart(ss2, hmemory, minutes);
			keymemory[0] = key;
			s2[0] = ss2;
			// }
			ChartGraph cg = new ChartGraph();
			cg.timewave(s2, "x", "y(MB)", chname, enname, 300, 150);
		} catch (Exception e) {
			System.out.println("drawchart error:" + e);
		}
	}

	private void arrayTochart(TimeSeries s, String[] h, Minute[] minutes) {
		try {
			for (int j = 0; j < h.length; j++) {
				// System.out.println("h[i]: " + h[j]);
				String value = h[j];
				Double v = new Double(0);
				if (value != null) {
					v = new Double(value);
				}
				s.addOrUpdate(minutes[j], v);
			}
		} catch (Exception e) {
			System.out.println("arraytochart error:" + e);
		}
	}

	public String getF(String s) {
		if (s.length() > 5)
			s = s.substring(0, 5);
		return s;
	}

	private void p_draw_line(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Minute.class);
				TimeSeries[] s = { ss };
				for (int j = 0; j < list.size(); j++) {
					// if (title1.equals("Cpu利用率")){
					Vector v = (Vector) list.get(j);
					// CPUcollectdata obj = (CPUcollectdata)list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
					// }
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<DnsConfig> getDnsConfigListByMonflag(Integer flag) {
		DnsConfigDao dnsConfigDao = null;
		List<DnsConfig> dnsConfigList = null;
		try {
			dnsConfigDao = new DnsConfigDao();
			dnsConfigList = (List<DnsConfig>) dnsConfigDao.getDNSConfigListByMonFlag(flag);
		} catch (Exception e) {

		} finally {
			dnsConfigDao.close();
		}
		return dnsConfigList;
	}

	/**
	 * 
	 * @description DNS 系统信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String system() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}
		request.setAttribute("id", id);
		request.setAttribute("dnsip", dnsip);
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_realtimeDao realtimedao = new Dnsmonitor_realtimeDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
		String connrate = "0";
		String validrate = "0";
		String freshrate = "0";
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}

		String starttime = from_date1 + " 00:00:00";
		String totime = to_date1 + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			try {
				urllist = configdao.getDnsByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();

			if (urllist.size() > 0 && queryid == null) {
				Object obj = urllist.get(0);
			}
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			conn_name = queryid + "urlmonitor-conn";
			valid_name = queryid + "urlmonitor-valid";
			fresh_name = queryid + "urlmonitor-refresh";
			wave_name = queryid + "urlmonitor-rec";
			delay_name = queryid + "urlmonitor-delay";

			List urlList = null;
			try {
				urlList = realtimedao.getByDNSId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Dnsmonitor_realtime) urlList.get(0)).getMon_time();
			}
			int interval = 0;
			try {
				// Session session = this.beginTransaction();
				List numList = new ArrayList();
				TaskXml taskxml = new TaskXml();
				numList = taskxml.ListXml();
				for (int i = 0; i < numList.size(); i++) {
					Task task = new Task();
					BeanUtils.copyProperties(task, numList.get(i));
					if (task.getTaskname().equals("urltask")) {
						interval = task.getPolltime().intValue();
						// numThreads = task.getPolltime().intValue();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			DateE de = new DateE();
			if (last == null) {
				last = new GregorianCalendar();
				flag = 1;
			}
			lasttime = de.getDateDetail(last);
			last.add(Calendar.MINUTE, interval);
			nexttime = de.getDateDetail(last);

			TimeSeries ss1 = new TimeSeries("", Minute.class);
			TimeSeries ss2 = new TimeSeries("", Minute.class);

			TimeSeries[] s = new TimeSeries[1];
			TimeSeries[] s_ = new TimeSeries[1];
			// Vector wave_v = historyManager.getInfo(queryid,initer);
			Vector wave_v = null;
			try {
				wave_v = historydao.getByDnsid(queryid, starttime, totime, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// historydao.close();
			}
			if (wave_v == null)
				wave_v = new Vector();
			for (int i = 0; i < wave_v.size(); i++) {
				Hashtable ht = (Hashtable) wave_v.get(i);
				double conn = Double.parseDouble(ht.get("conn").toString());
				String time = ht.get("mon_time").toString();
				ss1.addOrUpdate(new Minute(sdf1.parse(time)), conn);
			}
			s[0] = ss1;
			s_[0] = ss2;

			// 是否连通
			String conn[] = new String[2];

			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");

			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])))) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
			realtimedao.close();
		}
		// collectDNSData(dc);
		request.setAttribute("vo", dc);
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("valid_name", valid_name);
		request.setAttribute("fresh_name", fresh_name);
		request.setAttribute("wave_name", wave_name);
		request.setAttribute("delay_name", delay_name);
		request.setAttribute("connrate", connrate);
		request.setAttribute("validrate", validrate);
		request.setAttribute("freshrate", freshrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);

		return "/application/dns/dns_system.jsp";

	}

	/**
	 * 
	 * @description DNS 地址记录信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String addr() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}

		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象

		String conn_name = "";

		String connrate = "0";

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}

		String starttime = from_date1 + " 00:00:00";
		String totime = to_date1 + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			// 是否连通
			String conn[] = new String[2];
			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
		}
		// collectDNSData(dc);
		Hashtable hash = (Hashtable) ShareData.getAllDnsData().get(dc.getId());
		String addr = "";

		if (hash != null && hash.containsKey("addr")) {
			addr = (String) hash.get("addr");
		}
		request.setAttribute("id", id);
		request.setAttribute("addr", addr);
		request.setAttribute("vo", dc);
		request.setAttribute("connrate", connrate);
		return "/application/dns/dns_addr.jsp";

	}

	/**
	 * 
	 * @description DNS 地址记录信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String perform() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}

		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象

		String conn_name = "";

		String connrate = "0";

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}

		String starttime = from_date1 + " 00:00:00";
		String totime = to_date1 + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			// 是否连通
			String conn[] = new String[2];
			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
		}
		// collectDNSData(dc);
		// Hashtable hash=null;
		// if(hash!=null){
		// hash=(Hashtable) ShareData.getAllDnsData().get(dc.getId());
		// }else{
		// hash=new Hashtable();
		// }
		//	
		// request.setAttribute("hash", hash);
		request.setAttribute("id", id);
		request.setAttribute("vo", dc);
		request.setAttribute("connrate", connrate);
		return "/application/dns/dns_perform.jsp";

	}

	/**
	 * 
	 * @description DNS mail记录信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String mail() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}

		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象

		String conn_name = "";

		String connrate = "0";

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}

		String starttime = from_date1 + " 00:00:00";
		String totime = to_date1 + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			// 是否连通
			String conn[] = new String[2];
			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
		}
		request.setAttribute("id", id);
		request.setAttribute("vo", dc);
		request.setAttribute("connrate", connrate);
		return "/application/dns/dns_mail.jsp";

	}

	/**
	 * 
	 * @description DNS 名字服务信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String name() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}

		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象

		String conn_name = "";

		String connrate = "0";

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}

		String starttime = from_date1 + " 00:00:00";
		String totime = to_date1 + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			// 是否连通
			String conn[] = new String[2];
			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
		}
		request.setAttribute("id", id);
		request.setAttribute("vo", dc);
		request.setAttribute("connrate", connrate);
		return "/application/dns/dns_name.jsp";

	}

	/**
	 * 
	 * @description DNS 缓存信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String cache() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}

		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象

		String conn_name = "";

		String connrate = "0";

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}

		String starttime = from_date1 + " 00:00:00";
		String totime = to_date1 + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			// 是否连通
			String conn[] = new String[2];
			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
		}
		request.setAttribute("id", id);
		request.setAttribute("vo", dc);
		request.setAttribute("connrate", connrate);
		return "/application/dns/dns_cache.jsp";

	}

	/**
	 * 
	 * @description DNS 告警信息
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String alarm() {

		Integer id = getParaIntValue("id");
		DnsConfig dc = null;
		DnsConfigDao dao = new DnsConfigDao();
		List arr = dao.getDnsById(id);
		for (int i = 0; i < arr.size(); i++) {
			dc = (DnsConfig) arr.get(i);
		}
		String dnsip = "";
		String hostip = "";
		if (dc != null) {
			dnsip = dc.getDnsip();
			hostip = dc.getHostip();
		}

		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DnsConfigDao configdao = new DnsConfigDao();
		Dnsmonitor_historyDao historydao = new Dnsmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		DnsConfig initconf = new DnsConfig(); // 当前的对象

		String conn_name = "";

		String connrate = "0";

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = timeFormatter.format(new Date());
			request.setAttribute("startdate", startdate);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = timeFormatter.format(new Date());
			request.setAttribute("todate", todate);
		}

		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		int flag = 0;
		try {
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DnsConfigDao();
				try {
					initconf = (DnsConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			// 是否连通
			String conn[] = new String[2];
			conn = historydao.getAvailability(hostip, starttime, totime, "thevalue");
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
			configdao.close();
		}
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		EventListDao eventdao = new EventListDao();
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			list = eventdao.getQuery(starttime, totime, status + "", level1 + "", vo.getBusinessids(), id, "dns");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			eventdao.close();
		}
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("id", id);
		request.setAttribute("vo", dc);
		request.setAttribute("list", list);
		request.setAttribute("connrate", connrate);

		return "/application/dns/dns_alarm.jsp";

	}

	/**
	 * 
	 * @description DNS 可用性检测
	 * @author wangxiangyong
	 * @date Apr 2, 2013 2:46:29 PM
	 * @return String
	 * @return
	 */
	private String isOK() {
		DnsConfigDao dnsConfigDao = null;
		String ipaddress = "";
		int port = 0;
		String reason = "Apache当前状态不可用";
		String name = "";
		boolean isSucess = false;
		DnsConfig dnsConfig = null;
		List<DnsConfig> dnsConfigList = null;
		int id = getParaIntValue("id");

		int status = 0;
		try {
			Hashtable dnsDataHash = (Hashtable) ShareData.getAllDnsData().get(id);
			;
			dnsConfigDao = new DnsConfigDao();
			dnsConfigList = dnsConfigDao.getDnsById(id);
			if (dnsConfigList != null && dnsConfigList.size() > 0)
				dnsConfig = dnsConfigList.get(0);
			if (dnsConfig != null) {
				ipaddress = dnsConfig.getHostip();
				name = dnsConfig.getDns();
				DnsDataCollector dns = new DnsDataCollector();
				status = dns.collectDnsStatus(dnsDataHash, dnsConfig.getHostip());
				if (status == 100)
					isSucess = true;
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		request.setAttribute("isOK", isSucess);
		request.setAttribute("name", name);
		request.setAttribute("str", ipaddress);
		return "/tool/tomcatisok.jsp";

	}

	/**
	 * 
	 * @description 数据同步
	 * @author wangxiangyong
	 * @date Apr 3, 2013 3:28:08 PM
	 * @return String
	 * @return
	 */
	private String sychronizeData() {
		String id = getParaValue("id");
		DnsDataCollector dns = new DnsDataCollector();
		dns.collectDnsData(id);
		String url = "/dns.do?action=system&id=" + id;
		return url;

	}

	/**
	 * 
	 * @description 可用性报表
	 * @author wangxiangyong
	 * @date Apr 3, 2013 4:19:20 PM
	 * @return String
	 * @return
	 */
	private String showPingReport() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = getParaValue("startdate");
		Hashtable reporthash = new Hashtable();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");
		DnsConfig dns = null;
		DnsConfigDao dnsDao = new DnsConfigDao();
		try {
			dns = (DnsConfig) dnsDao.findByID(String.valueOf(queryid));
			ip = getParaValue("ipaddress");
			newip = SysUtil.doip(ip);
			Hashtable ConnectUtilizationhash = dnsDao.getPingDataById(ip, queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";
			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", dns.getDns());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "tftp");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dnsDao != null)
				dnsDao.close();
		}
		return "/application/dns/showPingReport.jsp";
	}

	/**
	 * 
	 * @description 事件报表
	 * @author wangxiangyong
	 * @date Apr 3, 2013 4:46:28 PM
	 * @return String
	 * @return
	 */
	private String eventReport() {

		String ip = "";
		String id = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";

		id = request.getParameter("id");
		DnsConfigDao dnsDao = new DnsConfigDao();
		DnsConfig dns = null;

		try {
			dns = (DnsConfig) dnsDao.findByID(String.valueOf(id));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dnsDao.close();
		}
		if (dns != null)
			ip = dns.getHostip();
		String newip = SysUtil.doip(ip);
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		EventListDao dao = null;
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			dao = new EventListDao();

			list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(id), "dns");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dao.close();
		}

		request.setAttribute("id", Integer.parseInt(id));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/dns/eventReport.jsp";
	}

	private String downloadEventReport() {

		String id = request.getParameter("id");
		String b_time = getParaValue("startdate");
		String t_time = getParaValue("todate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";

		Hashtable reporthash = new Hashtable();
		DnsConfigDao dnsDao = new DnsConfigDao();
		DnsConfig dns = null;

		try {
			dns = (DnsConfig) dnsDao.findByID(String.valueOf(id));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dnsDao.close();
		}
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "' ");
		s.append(" and nodeid=" + dns.getId());

		List infolist = eventdao.findByCriteria(s.toString());
		reporthash.put("eventlist", infolist);

		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/apacheEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventDoc(fileName, starttime1, totime1, "dns");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/apacheEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_TomcatEventExc(file, id, starttime1, totime1, "dns");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/apacheEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventPdf(fileName, starttime1, totime1, "dns");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	private String downloadAllReport() {

		String id = request.getParameter("id");
		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");

		if (todate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			todate = sdf.format(new Date());
		}
		if (startdate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startdate = sdf.format(new Date());
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		Hashtable ConnectUtilizationhash = null;
		Hashtable reporthash = new Hashtable();
		DnsConfigDao dnsDao = new DnsConfigDao();
		DnsConfig dns = null;
		EventListDao eventdao = null;
		try {
			dns = (DnsConfig) dnsDao.findByID(String.valueOf(id));
			eventdao = new EventListDao();
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
			s.append(" and nodeid=" + dns.getId());

			List infolist = eventdao.findByCriteria(s.toString());
			reporthash.put("eventlist", infolist);
			// ///////////////////////////////////////////////////
			ip = dns.getHostip();
			ConnectUtilizationhash = dnsDao.getPingDataById(ip, Integer.parseInt(id), starttime, totime);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dnsDao.close();
			eventdao.close();
		}
		String curPing = "";
		String pingconavg = "";
		if (ConnectUtilizationhash.get("avgPing") != null)
			pingconavg = (String) ConnectUtilizationhash.get("avgPing");
		String minPing = "";
		if (ConnectUtilizationhash.get("minPing") != null) {
			minPing = (String) ConnectUtilizationhash.get("minPing");
		}
		if (ConnectUtilizationhash.get("curPing") != null) {
			curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
		}
		String newip = SysUtil.doip(ip);
		// 画图----------------------
		String timeType = "minute";
		PollMonitorManager pollMonitorManager = new PollMonitorManager();
		pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

		// 画图-----------------------------
		reporthash.put("servicename", dns.getDns());
		reporthash.put("Ping", curPing);
		reporthash.put("ip", ip);
		reporthash.put("ping", ConnectUtilizationhash);
		reporthash.put("starttime", startdate);
		reporthash.put("totime", startdate);
		request.setAttribute("id", String.valueOf(id));
		request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
		request.setAttribute("Ping", curPing);
		request.setAttribute("avgpingcon", pingconavg);
		// //////////////////////////////////////////////////
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dnsEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midDoc(fileName, starttime, totime, "dns");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dns_Report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_ApacheExc(file, id, starttime, totime, "dns");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dns_Report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {

				report1.createReport_ApachePdf(fileName, id + "", starttime, totime, "dns");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	/**
	 * 
	 * @description 综合报表
	 * @author wangxiangyong
	 * @date Apr 3, 2013 5:19:08 PM
	 * @return String
	 * @return
	 */
	private String allReport() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = getParaValue("startdate");
		Hashtable reporthash = new Hashtable();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String newip = "";
		String ip = "";
		int status = 99;
		int level1 = 99;
		Integer queryid = getParaIntValue("id");
		DnsConfig dns = null;
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		DnsConfigDao dnsDao = new DnsConfigDao();
		try {
			dns = (DnsConfig) dnsDao.findByID(String.valueOf(queryid));
			ip = getParaValue("ipaddress");
			newip = SysUtil.doip(ip);
			Hashtable ConnectUtilizationhash = dnsDao.getPingDataById(ip, queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";
			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);
			EventListDao dao = null;
			List list = null;
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				dao = new EventListDao();

				list = dao.getQuery(starttime, totime, status + "", level1 + "", vo.getBusinessids(), queryid, "dns");

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("list", list);
			// 画图-----------------------------
			reporthash.put("servicename", dns.getDns());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "tftp");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/dns/allReport.jsp";
	}
}
