package com.afunms.application.manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.Socketmonitor_realtimeDao;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Socketmonitor_realtime;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.InitCoordinate;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SocketService;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.inform.util.SystemSnap;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.SocketServiceLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.SocketDataCollector;
import com.afunms.polling.task.TaskXml;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;

public class PortServiceTypeManager extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();

	private String list() {
		PSTypeDao dao = new PSTypeDao();
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
				list = dao.getSocketByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list == null)
			list = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			PSTypeVo vo = (PSTypeVo) list.get(i);
			Node socketNode = PollingEngine.getInstance().getSocketByID(vo.getId());
			if (socketNode == null) {
				vo.setStatus(0);
			} else {
				vo.setStatus(SystemSnap.getNodeStatus(socketNode));
			}
		}
		request.setAttribute("list", list);
		return "/application/pstype/list.jsp";
	}

	private String changeMonflag() {
		boolean result = false;
		PSTypeVo pstyVo = new PSTypeVo();
		PSTypeDao pstypedao = null;
		try {
			String id = getParaValue("id");
			int monflag = getParaIntValue("value");
			pstypedao = new PSTypeDao();
			pstyVo = (PSTypeVo) pstypedao.findByID(id);
			pstyVo.setMonflag(monflag);
			result = pstypedao.update(pstyVo);

		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			pstypedao.close();
		}
		SocketServiceLoader loader = new SocketServiceLoader();
		loader.loading();
		if (result) {
			return list();
		} else {

			return "/application/ftp/savefail.jsp";
		}
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
		return "/application/pstype/add.jsp";
	}

	private String add() {
		PSTypeVo vo = new PSTypeVo();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort(getParaValue("port"));
		vo.setPortdesc(getParaValue("portdesc"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));

		vo.setBid(getParaValue("bid"));

		// 在轮询线程中增加被监视节点
		SocketServiceLoader loader = new SocketServiceLoader();
		try {
			loader.loadOne(vo);
			loader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}

		PSTypeDao dao = new PSTypeDao();
		try {
			dao.save(vo);
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("8"));
			/* snow add end */
			// 加入分时
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
			// add
			// 2009-12-30
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("8"));

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "socket", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "socket");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		PSTypeDao psdao = new PSTypeDao();
		List list = null;
		try {
			list = psdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error(e.getMessage());
		} finally {
			psdao.close();
		}
		if (list == null)
			list = new ArrayList();
		ShareData.setPslist(list);

		return "/pstype.do?action=list";
	}

	/**
	 * @author nielin add for sms
	 * @since 2009-12-30
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/pstype/edit.jsp";
		List timeShareConfigList = new ArrayList();
		PSTypeDao dao = new PSTypeDao();
		try {
			setTarget(jsp);
			jsp = readyEdit(dao);
			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("8"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
			/* snow end */
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("timeShareConfigList", timeShareConfigList);
		return jsp;
	}

	public String delete() {
		String id = getParaValue("radio");
		PSTypeDao dao = new PSTypeDao();
		try {
			dao.delete(id);
			/* snow add at 2010-5-21 */
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			tg.deleteTimeGratherConfig(id, tg.getObjectType("8"));
			/* snow add end */
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
			// add
			// 2009-12-30
			timeShareConfigUtil.deleteTimeShareConfig(id, timeShareConfigUtil.getObjectType("8"));
			PollingEngine.getInstance().deleteSocketByID(Integer.parseInt(id));

			// 删除该数据库的采集指标
			NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
			try {
				gatherdao.deleteByNodeIdAndTypeAndSubtype(id, "service", "socket");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				gatherdao.close();
			}
			// 删除该数据库的告警阀值
			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
			try {
				indidao.deleteByNodeId(id, "service", "socket");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				indidao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		PSTypeDao psdao = new PSTypeDao();
		List list = null;
		try {
			list = psdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error(e.getMessage());
		} finally {
			psdao.close();
		}
		if (list == null)
			list = new ArrayList();
		ShareData.setPslist(list);

		return "/pstype.do?action=list";
	}

	private String update() {
		PSTypeVo vo = new PSTypeVo();
		vo.setId(getParaIntValue("id"));
		vo.setPort(getParaValue("port"));
		vo.setPortdesc(getParaValue("portdesc"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));

		vo.setBid(getParaValue("bid"));

		PSTypeDao dao = new PSTypeDao();
		try {
			dao.update(vo);

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("8"));
			/* snow add end */
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
			// add
			// 2009-12-30
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("8"));

			if (PollingEngine.getInstance().getSocketByID(vo.getId()) != null) {
				com.afunms.polling.node.SocketService socketservice = (com.afunms.polling.node.SocketService) PollingEngine.getInstance().getSocketByID(vo.getId());
				socketservice.setIpaddress(vo.getIpaddress());
				socketservice.setPort(vo.getPort());
				socketservice.setPortdesc(vo.getPortdesc());
				socketservice.setMonflag(vo.getMonflag());
				socketservice.setFlag(vo.getFlag());
				socketservice.setTimeout(vo.getTimeout());
				socketservice.setSendemail(vo.getSendemail());
				socketservice.setSendmobiles(vo.getSendmobiles());
				socketservice.setSendphone(vo.getSendphone());
				socketservice.setAlias(vo.getPortdesc());
				socketservice.setIpAddress(vo.getIpaddress());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		PSTypeDao psdao = new PSTypeDao();
		List list = null;
		try {
			list = psdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error(e.getMessage());
		} finally {
			psdao.close();
		}
		if (list == null)
			list = new ArrayList();
		ShareData.setPslist(list);
		return "/pstype.do?action=list";
	}

	/*
	 * private String readyEdit(){ PSTypeDao pstypedao= null ; String targetJsp =
	 * null; targetJsp = readyEdit(pstypedao);
	 * setTarget("/application/email/edit.jsp"); return targetJsp; }
	 */
	private String detail() {
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Socketmonitor_realtimeDao realtimedao = new Socketmonitor_realtimeDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		PSTypeDao configdao = new PSTypeDao(); // 当前的对象
		Hashtable imgurlhash = new Hashtable();
		PSTypeVo initconf = null;
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
		String pingconavg = "0";
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
				urllist = configdao.getSocketByBID(rbids);
			} catch (Exception e) {

			} finally {
				configdao.close();
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			request.setAttribute("id", queryid);
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new PSTypeDao();
				try {
					initconf = (PSTypeVo) configdao.findByID(queryid + "");
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

			List urlList = realtimedao.getBySocketId(queryid);

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Socketmonitor_realtime) urlList.get(0)).getMon_time();
			}
			int interval = 0;
			try {
				List numList = new ArrayList();
				TaskXml taskxml = new TaskXml();
				numList = taskxml.ListXml();
				for (int i = 0; i < numList.size(); i++) {
					Task task = new Task();
					BeanUtils.copyProperties(task, numList.get(i));
					if (task.getTaskname().equals("sockettask")) {
						interval = task.getPolltime().intValue();
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
			}

			InitCoordinate initer = new InitCoordinate(new GregorianCalendar(), hour, 1);
			TimeSeries ss1 = new TimeSeries("", Minute.class);
			TimeSeries ss2 = new TimeSeries("", Minute.class);
			String id = request.getParameter("id");
			com.afunms.polling.node.SocketService tomcat = (com.afunms.polling.node.SocketService) PollingEngine.getInstance().getSocketByID(Integer.parseInt(id));
			String ip = tomcat.getIpAddress();

			try {
				String newip = doip(ip);
				String[] time = { "", "" };
				getTime(request, time);
				// String starttime = time[0];
				// String endtime = time[1];
				String time1 = request.getParameter("begindate");
				if (time1 == null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					time1 = sdf.format(new Date());
				}
				String starttime1 = time1 + " 00:00:00";
				String totime1 = time1 + " 23:59:59";

				// 是否连通
				Hashtable ConnectUtilizationhash = new Hashtable();
				// String conn[] = new String[2];
				configdao = new PSTypeDao();
				try {
					ConnectUtilizationhash = configdao.getCategory(initconf.getIpaddress(), "SOCKETPing", "ConnectUtilization", starttime, totime, initconf.getPort());
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					configdao.close();
				}

				if (ConnectUtilizationhash.get("avgpingcon") != null) {
					pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					pingconavg = pingconavg.replace("%", "");
				}

				// try{
				// Hashtable hash1 =
				// getCategory(ip,"SOCKETPing","ConnectUtilization",starttime1,totime1,initconf.getPort());
				// p_draw_line(hash1,"连通率",newip+"SOCKETPing"+initconf.getPort(),740,150);
				// }catch(Exception ex){
				// ex.printStackTrace();
				// }
				// imgurlhash
				imgurlhash.put("SPPing", "resource/image/jfreechart/" + newip + "SOCKETPing" + initconf.getPort() + ".png");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("socketlist", urllist);
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
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("imgurlhash", imgurlhash);
		return "/application/pstype/detail.jsp";
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
		PSTypeVo initconf = null;
		PSTypeDao configdao = new PSTypeDao();
		try {
			initconf = (PSTypeVo) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
			configdao.close();
		}

		try {
			if (initconf != null) {
				ip = initconf.getIpaddress();

				newip = SysUtil.doip(ip);
				reporthash.put("servicename", initconf.getPortdesc());
			}
			String runmodel = PollingEngine.getCollectwebflag();

			PSTypeDao historydao = new PSTypeDao();

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = configdao.getCategory(initconf.getIpaddress(), "SOCKETPing", "ConnectUtilization", starttime, totime, initconf.getPort());
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				configdao.close();
			}
			String curPing = "";
			String pingconavg = "";
			String minPing = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			if (ConnectUtilizationhash.get("pingmax") != null) {
				minPing = (String) ConnectUtilizationhash.get("pingmax");
				minPing = minPing.replace("%", "");
			}

			if (ConnectUtilizationhash.get("curping") != null) {
				curPing = (String) ConnectUtilizationhash.get("curping"); // 取当前连通率可直接从
				curPing = curPing.replace("%", ""); // nms_email_history表获取,没必要再从nms_email_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------

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
			request.setAttribute("type", "pstype");
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
		List<String> infoList = new ArrayList<String>();
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
		PSTypeVo initconf = null;
		PSTypeDao configdao = new PSTypeDao();
		try {
			initconf = (PSTypeVo) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
			configdao.close();
		}

		try {
			if (initconf != null) {

				String name = initconf.getPortdesc();
				String type = "类型: 端口服务监视";
				String port = initconf.getPort();
				ip = initconf.getIpaddress();
				infoList.add("名称: " + name);
				infoList.add(type);
				infoList.add("端口: " + port);
				infoList.add("IP地址: " + ip);
				newip = SysUtil.doip(ip);
				reporthash.put("servicename", initconf.getPortdesc());
			}
			String runmodel = PollingEngine.getCollectwebflag();

			PSTypeDao historydao = new PSTypeDao();

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = configdao.getCategory(initconf.getIpaddress(), "SOCKETPing", "ConnectUtilization", starttime, totime, initconf.getPort());
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				configdao.close();
			}
			String curPing = "";
			String pingconavg = "";
			String minPing = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			if (ConnectUtilizationhash.get("pingmax") != null) {
				minPing = (String) ConnectUtilizationhash.get("pingmax");
				minPing = minPing.replace("%", "");
			}

			if (ConnectUtilizationhash.get("curping") != null) {
				curPing = (String) ConnectUtilizationhash.get("curping"); // 取当前连通率可直接从
				curPing = curPing.replace("%", ""); // nms_email_history表获取,没必要再从nms_email_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------

			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", todate);
			reporthash.put("type", "pstype");
			reporthash.put("comInfo", infoList);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "pstype");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/showServiceCompositeReport.jsp";
	}

	private String showServiceEventReport() {
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

		String id = getParaValue("id");
		Hashtable reporthash = new Hashtable();
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
		PSTypeVo initconf = null;
		PSTypeDao configdao = new PSTypeDao();
		try {
			initconf = (PSTypeVo) configdao.findByID(id);
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
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				s.append("select * from system_eventlist where recordtime>= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')" + " " + "and recordtime<=" + "to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')" + " ");
			}
			s.append(" and nodeid=" + id);
			try {
				list = eventdao.getQuery(starttime, totime, "socket", status + "", level1 + "", user.getBusinessids(), initconf.getId());

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
				String servName = initconf.getPortdesc();
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
			String ipaddress = initconf.getIpaddress();
			request.setAttribute("ipaddress", ipaddress);
			reporthash.put("list", list);
		}

		request.setAttribute("id", id);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", orderList);
		request.setAttribute("type", "pstype");
		request.setAttribute("list", list);
		reporthash.put("starttime", starttime);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("eventlist", orderList);

		session.setAttribute("reporthash", reporthash);
		return "/capreport/service/showServiceEventReport.jsp";

	}

	private String sychronizeData() {

		int queryid = getParaIntValue("id");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
		// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的SOCKET所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "service", "socket");
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

		try {
			// HostCollectDataManager hostdataManager=new
			// HostCollectDataManager();
			SocketDataCollector socketcollector = new SocketDataCollector();
			// SysLogger.info("##############################");
			// SysLogger.info("### 开始采集ID为"+queryid+"的SOCKET数据 ");
			// SysLogger.info("##############################");
			NodeGatherIndicators nodeGatherIndicators = new NodeGatherIndicators();
			nodeGatherIndicators.setNodeid(queryid + "");
			socketcollector.collect_Data(nodeGatherIndicators);
			// socketcollector.collect_data(queryid+"", gatherHash);
		} catch (Exception exc) {

		}
		return "/pstype.do?action=detail&id=" + queryid;
		// return "/application/web/detail.jsp";
	}

	private String isOK() {

		int queryid = getParaIntValue("id");
		PSTypeDao configdao = new PSTypeDao();
		Calendar date = Calendar.getInstance();
		PSTypeVo ps = null;
		try {
			ps = (PSTypeVo) configdao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		boolean flag = false;
		SocketService socketsv = new SocketService();
		try {
			flag = socketsv.checkService(ps.getIpaddress(), Integer.parseInt(ps.getPort()), ps.getTimeout());
		} catch (Exception e) {

		}
		request.setAttribute("isOK", flag);
		request.setAttribute("name", ps.getPortdesc());
		request.setAttribute("str", ps.getPort());
		return "/tool/socketisok.jsp";
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
		if (action.equals("changeMonflag")) {
			return changeMonflag();
		}
		if (action.equals("ready_edit")) {

			return ready_edit();
		}
		if (action.equals("update"))
			return update();
		if (action.equals("detail"))
			return detail();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		if (action.equals("showPingReport"))
			return showPingReport();
		if (action.equals("showCompositeReport"))
			return showCompositeReport();
		if (action.equals("showServiceEventReport"))
			return showServiceEventReport();
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
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp));

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
		return "/application/pstype/alarm.jsp";
	}

	private String doip(String ip) {
		// String newip="";
		// for(int i=0;i<3;i++){
		// int p=ip.indexOf(".");
		// newip+=ip.substring(0,p);
		// ip=ip.substring(p+1);
		// }
		// newip+=ip;
		ip = SysUtil.doip(ip);
		// System.out.println("newip="+newip);
		return ip;
	}

	private void getTime(HttpServletRequest request, String[] time) {
		Calendar current = new GregorianCalendar();
		String key = getParaValue("beginhour");
		if (getParaValue("beginhour") == null) {
			Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			request.setAttribute("beginhour", new Integer(hour.intValue() - 1));
			request.setAttribute("endhour", hour);
			// mForm.setBeginhour(new Integer(hour.intValue()-1));
			// mForm.setEndhour(hour);
		}
		if (getParaValue("begindate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String begindate = "";
			begindate = timeFormatter.format(new java.util.Date());
			request.setAttribute("begindate", begindate);
			request.setAttribute("enddate", begindate);
			// mForm.setBegindate(begindate);
			// mForm.setEnddate(begindate);
		} else {
			String temp = getParaValue("begindate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("enddate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
		if (getParaValue("startdate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String startdate = "";
			startdate = timeFormatter.format(new java.util.Date());
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", startdate);
			// mForm.setStartdate(startdate);
			// mForm.setTodate(startdate);
		} else {
			String temp = getParaValue("startdate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("todate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}

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
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
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

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime, String port) throws Exception {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			// con=DataGate.getCon();
			if (!starttime.equals("") && !endtime.equals("")) {
				// con=DataGate.getCon();
				// String ip1 ="",ip2="",ip3="",ip4="";
				// String tempStr = "";
				// String allipstr = "";
				// if (ip.indexOf(".")>0){
				// ip1=ip.substring(0,ip.indexOf("."));
				// ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());
				// tempStr =
				// ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
				// }
				// ip2=tempStr.substring(0,tempStr.indexOf("."));
				// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
				// allipstr=ip1+ip2+ip3+ip4;
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (category.equals("SOCKETPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from spping" + port + allipstr + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from spping" + port + allipstr + " h where ");
					}
				}
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.subentity='");
				sb.append(subentity);
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= ");
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" and h.collecttime <= ");
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" order by h.collecttime");
				}
				sql = sb.toString();
				// SysLogger.info(sql);

				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				double pingcon = 0;
				double tomcat_jvm_con = 0;
				int downnum = 0;
				int i = 0;
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					if (category.equals("SOCKETPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					}
					if (subentity.equalsIgnoreCase("ConnectUtilization")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
						if (tempfloat > getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else if (category.equalsIgnoreCase("tomcat_jvm")) {
						tomcat_jvm_con = tomcat_jvm_con + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else {
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					}
					list1.add(v);
				}
				rs.close();
				// stmt.close();

				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector) list1.get(0);
						unit = (String) tempV.get(2);
					}
				}
				if (category.equals("SOCKETPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				}
				hash.put("size", size);
				hash.put("max", CEIString.round(tempfloat, 2) + unit);
				hash.put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	private String emitStr(String num) {
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
		}
		return num;
	}

	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum / 100.0).doubleValue();
		}
		return snum;
	}

	private void drawPiechart(String[] keys, String[] values, String chname, String enname) {
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for (int i = 0; i < keys.length; i++) {
			piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname, piedata, enname, 300, 120);
	}
}
