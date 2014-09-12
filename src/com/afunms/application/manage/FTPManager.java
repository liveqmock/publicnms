package com.afunms.application.manage;

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

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.FtpHistoryDao;
import com.afunms.application.dao.FtpRealTimeDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.FtpRealTime;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.DateE;
import com.afunms.common.util.InitCoordinate;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Business;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.FtpLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.FTPDataCollector;
import com.afunms.polling.task.FtpUtil;
import com.afunms.polling.task.TaskXml;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;

public class FTPManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		} else if (action.equals("ready_add")) {
			return ready_add();
		} else if (action.equals("add")) {
			return add();
		} else if (action.equals("delete")) {
			return delete();
		} else if (action.equals("ready_edit")) {
			return readyEdit();
		} else if (action.equals("update")) {
			return update();
		} else if (action.equals("detail")) {
			return detail();
		} else if (action.equals("sychronizeData")) {
			return sychronizeData();
		} else if (action.equals("changeMonflag")) {
			return changeMonflag();
		} else if (action.equals("allservice")) {
			return allServiceList();
		} else if (action.equals("midalllist")) {
			return midalllist();

		} else if (action.equals("showPingReport")) {
			return showPingReport();

		} else if (action.equals("showCompositeReport")) {
			return showCompositeReport();

		} else if (action.equals("showServiceEventReport")) {
			return showServiceEventReport();

		}
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("alarm")) {
			return alarm();
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String alarm() {
		detail();
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {

			tmp = request.getParameter("id");
			request.setAttribute("id", Integer.parseInt(tmp));
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

			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "ftp");
				// list = dao.getQuery(starttime1,totime1,status+"",level1+"",
				// vo.getBusinessids(),Integer.parseInt(tmp));

				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/ftp/alarm.jsp";
	}

	/**
	 * 生成FTP饼图 guangfei
	 * 
	 * @return
	 */
	private String detail() {
		// SysLogger.info("######################################################");
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FTPConfigDao configdao = new FTPConfigDao();
		FtpRealTimeDao realtimedao = new FtpRealTimeDao();
		FtpHistoryDao historydao = new FtpHistoryDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		FTPConfig initconf = new FTPConfig(); // 当前的对象
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
			try {
				urllist = configdao.getFtpByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			request.setAttribute("id", queryid);
			if (urllist.size() > 0 && queryid == null) {
				Object obj = urllist.get(0);
			}
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new FTPConfigDao();
				try {
					initconf = (FTPConfig) configdao.findByID(queryid + "");
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
				urlList = realtimedao.getByFTPId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((FtpRealTime) urlList.get(0)).getMon_time();
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
				wave_v = historydao.getByFTPid(queryid, starttime, totime, 0);
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
			cg.timewave(s, "时间", "连通", "", wave_name, 600, 120);
			// cg.timewave(s_,"时间","时延(ms)","",delay_name,600,120);
			// p_draw_line(s,"","url"+queryid+"ConnectUtilization",740,120);

			// 是否连通
			String conn[] = new String[2];
			if (flag == 0)
				conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
			else {
				conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
			}
			// System.out.println(conn[0] + "!!!" + conn[1]);
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
		return "/application/ftp/detail.jsp";
	}

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
		FTPConfig initconf = null;
		FTPConfigDao configdao = new FTPConfigDao();
		try {
			initconf = (FTPConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			FtpHistoryDao historydao = new FtpHistoryDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			// Ftpmonitor_realtimeDao realDao=new Ftpmonitor_realtimeDao();
			// List curList=realDao.getByFTPId(queryid);
			// Ftpmonitor_realtime ftpReal=new Ftpmonitor_realtime();
			// ftpReal=(Ftpmonitor_realtime) curList.get(0);
			// int ping=ftpReal.getIs_canconnected();
			// if (ping==1) {
			// curPing="100";
			// }else{
			// curPing="0";
			// }
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
				// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", initconf.getName());
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
			request.setAttribute("type", "FTP");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/showPingReport.jsp";
	}

	private String showCompositeReport() {
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
		FTPConfig initconf = null;
		List<String> infoList = new ArrayList<String>();
		FTPConfigDao configdao = new FTPConfigDao();
		try {
			initconf = (FTPConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			FtpHistoryDao historydao = new FtpHistoryDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			// Ftpmonitor_realtimeDao realDao=new Ftpmonitor_realtimeDao();
			// List curList=realDao.getByFTPId(queryid);
			// Ftpmonitor_realtime ftpReal=new Ftpmonitor_realtime();
			// ftpReal=(Ftpmonitor_realtime) curList.get(0);
			// int ping=ftpReal.getIs_canconnected();
			// if (ping==1) {
			// curPing="100";
			// }else{
			// curPing="0";
			// }
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
				// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------
			if (initconf != null) {
				String name = initconf.getName();
				String type = "       类型: 端口服务监视";
				ip = initconf.getIpaddress();
				String file = initconf.getFilename();
				infoList.add("名称: " + name);
				infoList.add(type);
				infoList.add("      测试文件: " + file);
				infoList.add("      IP地址: " + ip);

			}
			reporthash.put("servicename", initconf.getName());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			reporthash.put("type", "FTP");
			reporthash.put("comInfo", infoList);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "FTP");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/showServiceCompositeReport.jsp";
	}

	private String showServiceEventReport() {
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		request.setAttribute("ipaddress", ipaddress);
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		List orderList = new ArrayList();
		FTPConfig initconf = null;
		FTPConfigDao configdao = new FTPConfigDao();
		try {
			initconf = (FTPConfig) configdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		List infolist = null;
		List list = null;
		if (initconf != null) {

			// 事件列表

			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			EventListDao eventdao = new EventListDao();
			StringBuffer s = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
				s.append(" and nodeid=" + id);
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				s.append("select * from system_eventlist where recordtime>= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')" + " " + "and recordtime<=" + "to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')" + " ");
				s.append(" and nodeid=" + id);
			}

			try {
				list = eventdao.getQuery(starttime, totime, "ftp", status + "", level1 + "", user.getBusinessids(), initconf.getId());

				infolist = eventdao.findByCriteria(s.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventdao.close();
			}
			if (infolist != null && infolist.size() > 0) {
				int levelone = 0;
				int levletwo = 0;
				int levelthree = 0;

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");

					if (eventlist.getLevel1() == 1) {
						levelone = levelone + 1;
					} else if (eventlist.getLevel1() == 2) {
						levletwo = levletwo + 1;
					} else if (eventlist.getLevel1() == 3) {
						levelthree = levelthree + 1;
					}

				}
				String servName = initconf.getName();
				String ip = initconf.getIpaddress();
				List<String> ipeventList = new ArrayList<String>();
				ipeventList.add(ip);
				ipeventList.add(servName);
				ipeventList.add((levelone + levletwo + levelthree) + "");
				ipeventList.add(levelone + "");
				ipeventList.add(levletwo + "");
				ipeventList.add(levelthree + "");

				orderList.add(ipeventList);

			}
		}
		Hashtable reporthash = new Hashtable();

		request.setAttribute("id", id);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", orderList);
		request.setAttribute("type", "FTP");
		request.setAttribute("list", list);
		reporthash.put("starttime", starttime);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("eventlist", orderList);
		reporthash.put("list", list);
		session.setAttribute("reporthash", reporthash);
		return "/capreport/service/showServiceEventReport.jsp";

	}

	private String sychronizeData() {
		int ftpId = getParaIntValue("id");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// 获取被启用的SOCKET所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(ftpId + "", 1, "service", "ftp");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null)
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			try {
				FTPDataCollector ftpcollector = new FTPDataCollector();
				ftpcollector.collect_Data(nodeGatherIndicators);
			} catch (Exception exc) {

			}
		}

		return "/FTP.do?action=detail&id=" + ftpId;
	}

	private String isOK() {
		int ftpID = getParaIntValue("id");
		FTPConfigDao configdao = new FTPConfigDao();
		FTPConfig ftpConfig = null;
		try {
			ftpConfig = (FTPConfig) configdao.findByID(ftpID + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		String reason = "服务有效";
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// 获取被启用的FTP所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(ftpID + "", 1, "service", "ftp");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null)
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable gatherHash = new Hashtable();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}

		FtpUtil ftpUtil = new FtpUtil(ftpConfig.getIpaddress(), ftpConfig.getUsername(), ftpConfig.getPassword());

		boolean downloadflag = true;
		boolean uploadsuccess = true;
		try {

			uploadsuccess = ftpUtil.upload(ResourceCenter.getInstance().getSysPath() + "/ftpupload/", ftpConfig.getFilename());
			downloadflag = ftpUtil.download(ResourceCenter.getInstance().getSysPath() + "/ftpdownload/", ftpConfig.getFilename());

			if (downloadflag && uploadsuccess) {
				reason = "服务有效";
			} else {
				if (downloadflag == true && uploadsuccess == false) {
					reason = "上载服务无效,下载服务正常";
				} else if (downloadflag == false && uploadsuccess == true) {
					reason = "上载服务正常,下载服务无效";
				} else {
					reason = "FTP服务无效";
				}
			}

		} catch (Exception e) {

		}
		request.setAttribute("isOK", reason);
		request.setAttribute("name", ftpConfig.getName());
		request.setAttribute("str", ftpConfig.getIpaddress());
		return "/tool/ftpisok.jsp";
	}

	private void drawPiechart(String[] keys, String[] values, String chname, String enname) {
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for (int i = 0; i < keys.length; i++) {
			piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname, piedata, enname, 300, 120);
	}

	public String getF(String s) {
		if (s.length() > 5)
			s = s.substring(0, 5);
		return s;
	}

	private String allServiceList() {

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
		// ftp list
		FTPConfigDao ftpdao = new FTPConfigDao();
		List ftplist = null;
		try {
			if (operator.getRole() == 0) {
				ftplist = ftpdao.loadAll();
			} else
				ftplist = ftpdao.getFtpByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ftpdao.close();
		}
		request.setAttribute("ftplist", ftplist);

		// mail list
		EmailConfigDao emailConfigDao = new EmailConfigDao();
		List<EmailMonitorConfig> userEmailMonitorConfigList = new ArrayList<EmailMonitorConfig>();
		try {
			userEmailMonitorConfigList = (List<EmailMonitorConfig>) emailConfigDao.getByBIDAndFlag(operator.getBusinessids(), 1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emailConfigDao.close();
		}

		request.setAttribute("emaillist", userEmailMonitorConfigList);

		// process list
		ProcsDao pdao = new ProcsDao();
		List prolist = null;
		try {
			prolist = pdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pdao.close();
		}
		request.setAttribute("prolist", prolist);

		// web list
		WebConfigDao configdao = new WebConfigDao();
		List weblist = null;
		try {
			weblist = configdao.getWebByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		request.setAttribute("weblist", weblist);

		// port list
		PSTypeDao portdao = new PSTypeDao();
		List portlist = null;
		try {
			if (operator.getRole() == 0) {
				portlist = portdao.loadAll();
			} else
				portlist = portdao.getSocketByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portdao.close();
		}
		if (portlist == null)
			portlist = new ArrayList();
		for (int i = 0; i < portlist.size(); i++) {
			PSTypeVo vo = (PSTypeVo) portlist.get(i);
			Node socketNode = PollingEngine.getInstance().getSocketByID(vo.getId());
			if (socketNode == null)
				vo.setStatus(0);
			else
				vo.setStatus(socketNode.getStatus());
		}
		request.setAttribute("portlist", portlist);

		return "/application/ftp/servicelist.jsp";
	}

	private String list() {
		FTPConfigDao dao = new FTPConfigDao();
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

		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = dao.loadAll();
			} else
				list = dao.getFtpByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("userFTPConfigList", list);
		return "/application/ftp/list.jsp";
	}

	public List<FTPConfig> getAllFTPConfigList() {
		FTPConfigDao ftpConfigDao = null;
		List<FTPConfig> allFTPConfigList = null;
		try {
			ftpConfigDao = new FTPConfigDao();
			allFTPConfigList = (List<FTPConfig>) ftpConfigDao.loadAll();
		} catch (Exception e) {

		} finally {
			ftpConfigDao.close();
		}
		return allFTPConfigList;
	}

	/**
	 * 根据用户的所属业务来提取FTPConfig列表
	 * 
	 * @param allFTPConfigList
	 * @return
	 */
	public List<FTPConfig> getFTPConfigListByUser(List<FTPConfig> ftpConfigList) {

		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String businessids = operator.getBusinessids();
		List<FTPConfig> userFTPConfigList = new ArrayList<FTPConfig>();
		List<String> userBusinessidList = getBusinessidList(businessids);

		// 如果用户所属的业务id列表 包含 FTPConfig所属的业务 则显示
		if (userBusinessidList != null && userBusinessidList.size() > 0) {
			for (int i = 0; i < ftpConfigList.size(); i++) {
				String FTPConfigbids = ftpConfigList.get(i).getBid();
				List<String> ftpConfigBusinessidList = getBusinessidList(FTPConfigbids);
				for (int j = 0; j < userBusinessidList.size(); j++) {
					if (ftpConfigBusinessidList.contains(userBusinessidList.get(j))) {
						userFTPConfigList.add(ftpConfigList.get(i));
						break;
					}
				}

			}
		}
		return userFTPConfigList;
	}

	public List<FTPConfig> getFTPConfigListByMonflag(Integer flag) {
		FTPConfigDao ftpConfigDao = null;
		List<FTPConfig> ftpConfigList = null;
		try {
			ftpConfigDao = new FTPConfigDao();
			ftpConfigList = (List<FTPConfig>) ftpConfigDao.getFTPConfigListByMonFlag(flag);
		} catch (Exception e) {

		} finally {
			ftpConfigDao.close();
		}
		return ftpConfigList;
	}

	/**
	 * 将业务id的字符串 拆分成一个业务id的列表
	 * 
	 * @param businessids
	 * @return
	 */
	private List<String> getBusinessidList(String businessids) {
		String bid[] = businessids.split(",");
		List<String> businessidList = new ArrayList<String>();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				// 去掉空白字符串
				if (bid[i] != null && bid[i].trim().length() > 0)
					businessidList.add(bid[i].trim());
			}
		}
		return businessidList;
	}

	/**
	 * snow 增加前将供应商查找到
	 * 
	 * @return
	 * @date 2010-5-21
	 */
	private String ready_add() {
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);
		return "/application/ftp/add.jsp";
	}

	/**
	 * 添加 FTPConfig
	 * 
	 * @return
	 */
	private String add() {
		boolean result = false;
		FTPConfig ftpConfig = new FTPConfig();
		FTPConfigDao ftpConfigDao = null;
		try {
			ftpConfigDao = new FTPConfigDao();
			// 创建 ftpConfig
			ftpConfig = createFTPConfig();
			ftpConfig.setId(KeyGenerator.getInstance().getNextKey());
			result = ftpConfigDao.save(ftpConfig);
			// nielin add for time-sharing at 2010-01-04
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			result = timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(ftpConfig.getId()), timeShareConfigUtil.getObjectType("2"));
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(ftpConfig.getId()), timeGratherConfigUtil.getObjectType("2"));
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(ftpConfig.getId() + "", "service", "ftp", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(ftpConfig.getId()), "service", "ftp");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 在轮询线程中增加被监视节点
			FtpLoader loader = new FtpLoader();
			try {
				loader.loadOne(ftpConfig);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loader.close();
			}

			// 保存应用
			HostApplyManager.save(ftpConfig);
		} catch (Exception e) {
			result = false;
		} finally {
			ftpConfigDao.close();
		}
		try {
			ftpConfigDao = new FTPConfigDao();
			List _list = ftpConfigDao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setFtplist(_list);
			FtpLoader ftploader = new FtpLoader();
			ftploader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			ftpConfigDao.close();
		}
		// 成功则跳回 FTPConfig 列表页面 失败则显示数据错误
		if (result) {
			return list();
		} else {
			return "/application/ftp/savefail.jsp";
		}
	}

	/**
	 * 删除 FTPConfig
	 * 
	 * @return
	 */
	private String delete() {
		String[] ids = getParaArrayValue("checkbox");
		FTPConfigDao ftpConfigDao = null;
		boolean result = false;
		try {
			ftpConfigDao = new FTPConfigDao();
			if (ids != null && ids.length > 0) {
				result = ftpConfigDao.delete(ids);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				for (int i = 0; i < ids.length; i++) {

					Node node = PollingEngine.getInstance().getFtpByID(Integer.parseInt(ids[i]));
					// 删除应用
					HostApplyDao hostApplyDao = null;
					try {
						hostApplyDao = new HostApplyDao();
						hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'ftp' and nodeid = '" + ids[i] + "'");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (hostApplyDao != null) {
							hostApplyDao.close();
						}
					}

					PollingEngine.getInstance().deleteFtpByID(Integer.parseInt(ids[i]));
					timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("2"));
					tg.deleteTimeGratherConfig(ids[i], timeShareConfigUtil.getObjectType("2"));

					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "service", "ftp");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(ids[i], "service", "ftp");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						indidao.close();
					}

					// 更新业务视图
					String id = ids[i];
					NodeDependDao nodedependao = new NodeDependDao();
					List weslist = nodedependao.findByNode("ftp" + id);
					if (weslist != null && weslist.size() > 0) {
						for (int j = 0; j < weslist.size(); j++) {
							NodeDepend wesvo = (NodeDepend) weslist.get(j);
							if (wesvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("ftp" + id, wesvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("ftp" + id, wesvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("ftp" + id, wesvo.getXmlfile());
								} else {
									nodeDependDao.close();
								}

								// yangjun
								User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
								ManageXmlDao mXmlDao = new ManageXmlDao();
								List xmlList = new ArrayList();
								try {
									xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									mXmlDao.close();
								}
								try {
									ChartXml chartxml;
									chartxml = new ChartXml("tree");
									chartxml.addViewTree(xmlList);
								} catch (Exception e) {
									e.printStackTrace();
								}

								ManageXmlDao subMapDao = new ManageXmlDao();
								ManageXml manageXml = (ManageXml) subMapDao.findByXml(wesvo.getXmlfile());
								if (manageXml != null) {
									NodeDependDao nodeDepenDao = new NodeDependDao();
									try {
										List lists = nodeDepenDao.findByXml(wesvo.getXmlfile());
										ChartXml chartxml;
										chartxml = new ChartXml("NetworkMonitor", "/" + wesvo.getXmlfile().replace("jsp", "xml"));
										chartxml.addBussinessXML(manageXml.getTopoName(), lists);
										ChartXml chartxmlList;
										chartxmlList = new ChartXml("NetworkMonitor", "/" + wesvo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
										chartxmlList.addListXML(manageXml.getTopoName(), lists);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										nodeDepenDao.close();
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			SysLogger.error("FTPManager.delete()", ex);
			result = false;
		} finally {
			ftpConfigDao.close();
		}
		try {
			ftpConfigDao = new FTPConfigDao();
			List _list = ftpConfigDao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setFtplist(_list);
			FtpLoader ftploader = new FtpLoader();
			ftploader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			ftpConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/ftp/savefail.jsp";
		}
	}

	/**
	 * 发送更新 FTPConfig 的页面
	 * 
	 * @return
	 */
	private String readyEdit() {
		FTPConfigDao ftpConfigdao = null;
		BusinessDao businessdao = null;
		String targetJsp = null;
		boolean result = false;
		try {
			ftpConfigdao = new FTPConfigDao();
			setTarget("/application/ftp/edit.jsp");
			targetJsp = readyEdit(ftpConfigdao);
			FTPConfig ftpConfig = (FTPConfig) request.getAttribute("vo");
			businessdao = new BusinessDao();
			// 载入所有业务
			List<Business> allBusiness = businessdao.loadAll();
			request.setAttribute("allBusiness", allBusiness);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(String.valueOf(ftpConfig.getId()), timeShareConfigUtil.getObjectType("2"));
			request.setAttribute("timeShareConfigList", timeShareConfigList);

			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(String.valueOf(ftpConfig.getId()), tg.getObjectType("2"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
			/* snow end */

			result = true;
		} catch (Exception ex) {
			SysLogger.error("FTPManager.readyEdit()", ex);
			result = false;
		} finally {
			ftpConfigdao.close();
			if (businessdao != null) {
				businessdao.close();
			}
		}
		return targetJsp;
	}

	/**
	 * 更新 FTPConfig
	 * 
	 * @return
	 */
	private String update() {
		FTPConfig vo = new FTPConfig();
		boolean result = false;
		FTPConfigDao ftpConfigDao = null;
		try {
			ftpConfigDao = new FTPConfigDao();

			// 创建 FTPConfig
			vo = createFTPConfig();
			int id = getParaIntValue("id");
			vo.setId(id);
			result = ftpConfigDao.update(vo);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("2"));

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("2"));
			/* snow add end */

			Ftp ftp = (Ftp) PollingEngine.getInstance().getFtpByID(id);
			ftp.setName(vo.getName());
			ftp.setFilename(vo.getFilename());
			ftp.setUsername(vo.getUsername());
			ftp.setPassword(vo.getPassword());
			ftp.setMonflag(vo.getMonflag());
			ftp.setTimeout(vo.getTimeout());
			ftp.setAlias(vo.getName());
			ftp.setSendemail(vo.getSendemail());
			ftp.setSendmobiles(vo.getSendmobiles());
			ftp.setSendphone(vo.getSendphone());
			ftp.setBid(vo.getBid());
			ftp.setMonflag(vo.getMonflag());
			ftp.setIpAddress(vo.getIpaddress());
			ftp.setAlias(vo.getName());
			ftp.setSupperid(vo.getSupperid());// snow add at 2010-5-21

		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		} finally {
			ftpConfigDao.close();
		}
		try {
			ftpConfigDao = new FTPConfigDao();
			List _list = ftpConfigDao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setFtplist(_list);
			FtpLoader ftploader = new FtpLoader();
			ftploader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			ftpConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/ftp/savefail.jsp";
		}

	}

	/**
	 * 根据页面的参数来创建 FTPConfig
	 * 
	 * @return
	 */
	private FTPConfig createFTPConfig() {

		FTPConfig ftpConfig = new FTPConfig();

		String name = getParaValue("name");
		String username = getParaValue("username");
		String password = getParaValue("password");
		int timeout = getParaIntValue("timeout");
		int monflag = getParaIntValue("monflag");
		String ipaddress = getParaValue("ipaddress");
		String filename = getParaValue("filename");
		String sendmobiles = getParaValue("sendmobiles");
		String sendemail = getParaValue("sendemail");
		String sendphone = getParaValue("sendphone");
		String bid = getParaValue("bid");

		ftpConfig.setId(getParaIntValue("id"));
		ftpConfig.setName(name);
		ftpConfig.setUsername(username);
		ftpConfig.setPassword(password);
		ftpConfig.setTimeout(timeout);
		ftpConfig.setMonflag(monflag);
		ftpConfig.setIpaddress(ipaddress);
		ftpConfig.setFilename(filename);
		ftpConfig.setSendmobiles(sendmobiles);
		ftpConfig.setSendemail(sendemail);
		ftpConfig.setSendphone(sendphone);
		ftpConfig.setSupperid(getParaIntValue("supperid"));// snow add at
		// 2010-5-21
		ftpConfig.setBid(bid);

		return ftpConfig;
	}

	/**
	 * 修改 FTPConfig 的监视信息之后返回FTPConfig列表页面
	 * 
	 * @return
	 */
	private String changeMonflag() {
		boolean result = false;
		FTPConfig ftpConfig = new FTPConfig();
		FTPConfigDao ftpConfigDao = null;
		try {
			String id = getParaValue("id");
			int monflag = getParaIntValue("value");
			ftpConfigDao = new FTPConfigDao();
			ftpConfig = (FTPConfig) ftpConfigDao.findByID(id);
			ftpConfig.setMonflag(monflag);
			result = ftpConfigDao.update(ftpConfig);
			Ftp ftp = (Ftp) PollingEngine.getInstance().getFtpByID(Integer.parseInt(id));
			ftp.setMonflag(monflag);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			ftpConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/ftp/savefail.jsp";
		}
	}

	private String midalllist() {

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

		// Tomcat
		TomcatDao tomcatdao = new TomcatDao();
		List tomcatlist = null;
		try {
			if (operator.getRole() == 0) {
				tomcatlist = tomcatdao.loadAll();
			} else
				tomcatlist = tomcatdao.getTomcatByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatdao.close();
		}
		if (tomcatlist == null)
			tomcatlist = new ArrayList();
		for (int i = 0; i < tomcatlist.size(); i++) {
			Tomcat tomcatvo = (Tomcat) tomcatlist.get(i);
			Node tomcatNode = PollingEngine.getInstance().getTomcatByID(tomcatvo.getId());
			if (tomcatNode == null)
				tomcatvo.setStatus(0);
			else
				tomcatvo.setStatus(tomcatNode.getStatus());
		}

		// mq
		MQConfigDao mqconfigdao = new MQConfigDao();
		List mqlist = null;
		try {
			if (operator.getRole() == 0) {
				mqlist = mqconfigdao.loadAll();
			} else
				mqlist = mqconfigdao.getMQByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqconfigdao.close();
		}
		if (mqlist == null)
			mqlist = new ArrayList();
		for (int i = 0; i < mqlist.size(); i++) {
			MQConfig mqvo = (MQConfig) mqlist.get(i);
			Node mqNode = PollingEngine.getInstance().getMqByID(mqvo.getId());
			if (mqNode == null)
				mqvo.setStatus(0);
			else
				mqvo.setStatus(mqNode.getStatus());
		}
		// domimo
		DominoConfigDao dominoconfigdao = new DominoConfigDao();
		List dominolist = new ArrayList();
		try {
			dominolist = dominoconfigdao.getDominoByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dominoconfigdao.close();
		}

		// was
		WasConfigDao wasconfigdao = new WasConfigDao();
		List waslist = null;
		try {
			waslist = wasconfigdao.getWasByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wasconfigdao.close();
		}

		// weblogic
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		List weblogiclist = null;
		try {
			if (operator.getRole() == 0) {
				weblogiclist = weblogicconfigdao.loadAll();
			} else
				weblogiclist = weblogicconfigdao.getWeblogicByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			weblogicconfigdao.close();
		}
		// IIS
		IISConfigDao iisconfigdao = new IISConfigDao();
		List iislist = new ArrayList();
		try {
			iislist = iisconfigdao.getIISByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			iisconfigdao.close();
		}

		// CICS
		CicsConfigDao cicsconfigdao = new CicsConfigDao();
		List cicslist = new ArrayList();
		try {
			cicslist = cicsconfigdao.getCicsByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cicsconfigdao.close();
		}
		// DNS
		DnsConfigDao dnsconfigdao = new DnsConfigDao();
		List dnslist = null;
		try {
			dnslist = dnsconfigdao.getDnsByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dnsconfigdao.close();
		}

		request.setAttribute("dnslist", dnslist);
		request.setAttribute("cicslist", cicslist);
		request.setAttribute("iislist", iislist);
		request.setAttribute("weblogiclist", weblogiclist);
		request.setAttribute("waslist", waslist);
		request.setAttribute("tomcatlist", tomcatlist);
		request.setAttribute("mqlist", mqlist);
		request.setAttribute("dominolist", dominolist);
		return "/application/ftp/midalllist.jsp";
	}

}