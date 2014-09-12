package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.beanutils.BeanUtils;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.EmailHistoryDao;
import com.afunms.application.dao.EmailRealTimeDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.EmailRealtime;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.DateE;
import com.afunms.common.util.InitCoordinate;
import com.afunms.common.util.ReceiveMail;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.SendMail;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Business;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.MailLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Mail;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.MailDataCollector;
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

public class MailMonitorManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return ready_add();
		if (action.equals("add"))
			return add();
		if (action.equals("delete"))
			return delete();
		if (action.equals("ready_edit")) {
			return readyEdit();
		}
		if (action.equals("update"))
			return update();
		if (action.equals("changeMonflag")) {
			return changeMonflag();
		}
		if (action.equals("detail"))
			return detail();
		if (action.equals("search"))
			return search();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("alarm")) {
			return alarm();
		}
		if (action.equals("showPingReport"))
			return showPingReport();
		if (action.equals("showCompositeReport"))
			return showCompositeReport();
		if (action.equals("showServiceEventReport"))
			return showServiceEventReport();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String alarm() {
		detail();
		Vector vector = new Vector();
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
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "mail");
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
		return "/application/email/alarm.jsp";
	}

	private String list() {
		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		List<EmailMonitorConfig> userEmailMonitorConfigList = new ArrayList<EmailMonitorConfig>();
		EmailConfigDao emailConfigDao = new EmailConfigDao();
		try {
			if (operator.getRole() == 0) {
				userEmailMonitorConfigList = emailConfigDao.loadAll();
			} else {
				userEmailMonitorConfigList = emailConfigDao.getByBIDAndFlag(operator.getBusinessids(), -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emailConfigDao.close();
		}
		request.setAttribute("userEmailMonitorConfigList", userEmailMonitorConfigList);
		return "/application/email/emailconfiglist.jsp";

	}

	public List<EmailMonitorConfig> getFTPConfigListByMonflag(Integer flag) {
		EmailConfigDao emailConfigDao = new EmailConfigDao();
		List<EmailMonitorConfig> emailConfigList = null;
		try {
			emailConfigList = (List<EmailMonitorConfig>) emailConfigDao.getEmailConfigListByMonFlag(flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emailConfigDao.close();
		}
		return emailConfigList;
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
		return "/application/email/add.jsp";
	}

	private String add() {
		EmailMonitorConfig vo = new EmailMonitorConfig();
		vo = createEmailMonitorConfig();
		EmailConfigDao dao = new EmailConfigDao();
		try {
			dao.save(vo);
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("3"));
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("3"));
			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "mail", "1");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "mail");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		// 在轮询线程中增加被监视节点
		MailLoader loader = new MailLoader();
		try {
			loader.loadOne(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}
		try {
			dao = new EmailConfigDao();
			List list = dao.loadAll();
			ShareData.setEmaillist(list);
			loader.clearRubbish(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// 保存应用
		HostApplyManager.save(vo);
		return list();
	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		EmailConfigDao emailConfigDao = null;
		boolean result = false;
		try {
			emailConfigDao = new EmailConfigDao();
			if (ids != null && ids.length > 0) {
				result = emailConfigDao.delete(ids);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				for (int i = 0; i < ids.length; i++) {
					Node node = PollingEngine.getInstance().getMailByID(Integer.parseInt(ids[i]));
					// 删除应用
					HostApplyDao hostApplyDao = null;
					try {
						hostApplyDao = new HostApplyDao();
						hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'email' and nodeid = '" + ids[i] + "'");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (hostApplyDao != null) {
							hostApplyDao.close();
						}
					}
					timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("3"));
					tg.deleteTimeGratherConfig(ids[i], timeShareConfigUtil.getObjectType("3"));

					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "service", "mail");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(ids[i], "service", "mail");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						indidao.close();
					}

					PollingEngine.getInstance().deleteMailByID(Integer.parseInt(ids[i]));

					// 更新业务视图
					String id = ids[i];
					NodeDependDao nodedependao = new NodeDependDao();
					List weslist = nodedependao.findByNode("mai" + id);
					if (weslist != null && weslist.size() > 0) {
						for (int j = 0; j < weslist.size(); j++) {
							NodeDepend wesvo = (NodeDepend) weslist.get(j);
							if (wesvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("mai" + id, wesvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("mai" + id, wesvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("mai" + id, wesvo.getXmlfile());
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

					// Emailmonitor_historyDao historyDao = new
					// Emailmonitor_historyDao();
					// Emailmonitor_realtimeDao realtimeDao = new
					// Emailmonitor_realtimeDao();
					// try {
					// historyDao.deleteByEmail(ids[i]);
					// realtimeDao.deleteByEmail(ids[i]);
					// } catch (Exception e) {
					// // TODO: handle exception
					// e.printStackTrace();
					// }finally{
					// historyDao.close();
					// realtimeDao.close();
					// }

				}
			}
		} catch (Exception ex) {
			SysLogger.error("MailMonitorManager.delete()", ex);
			result = false;
		} finally {
			if (emailConfigDao != null)
				emailConfigDao.close();
		}
		try {
			emailConfigDao = new EmailConfigDao();
			List _list = emailConfigDao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setEmaillist(_list);
			MailLoader loader = new MailLoader();
			loader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			emailConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/ftp/savefail.jsp";
		}

	}

	private String readyEdit() {
		EmailConfigDao emailConfigdao = null;
		BusinessDao businessdao = null;
		String targetJsp = null;
		boolean result = false;
		try {
			emailConfigdao = new EmailConfigDao();
			setTarget("/application/email/edit.jsp");
			targetJsp = readyEdit(emailConfigdao);
			businessdao = new BusinessDao();
			// 载入所有业务
			List<Business> allBusiness = businessdao.loadAll();
			request.setAttribute("allBusiness", allBusiness);
			// nielin add for time-sharing at 2010-01-03
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("3"));
			request.setAttribute("timeShareConfigList", timeShareConfigList);
			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("3"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
			/* snow end */

			result = true;
		} catch (Exception ex) {
			SysLogger.error("MailMonitorManager.readyEdit()", ex);
			result = false;
		} finally {
			emailConfigdao.close();
			if (businessdao != null) {
				businessdao.close();
			}
		}
		return targetJsp;
	}

	private String update() {
		EmailMonitorConfig vo = new EmailMonitorConfig();
		vo = createEmailMonitorConfig();
		EmailConfigDao configdao = new EmailConfigDao();
		try {
			configdao.update(vo);
			// nielin add for time-sharing at 2010-01-04
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("3"));
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("3"));
			/* snow add end */
			Mail mail = (Mail) PollingEngine.getInstance().getMailByID(vo.getId());
			mail.setId(vo.getId());
			mail.setName(vo.getName());
			mail.setAddress(vo.getAddress());
			mail.setUsername(vo.getUsername());
			mail.setPassword(vo.getPassword());
			mail.setRecivemail(vo.getRecivemail());
			mail.setFlag(vo.getFlag());
			mail.setMonflag(vo.getMonflag());
			mail.setTimeout(vo.getTimeout());
			mail.setSendemail(vo.getSendemail());
			mail.setSendmobiles(vo.getSendmobiles());
			mail.setSendphone(vo.getSendphone());
			mail.setBid(vo.getBid());
			mail.setMonflag(vo.getMonflag());
			mail.setIpAddress(vo.getIpaddress());
			mail.setAlias(vo.getName());
			mail.setSupperid(vo.getSupperid());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();

		}
		try {
			configdao = new EmailConfigDao();
			List _list = configdao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setEmaillist(_list);
			MailLoader loader = new MailLoader();
			loader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			configdao.close();
		}
		return list();
	}

	private EmailMonitorConfig createEmailMonitorConfig() {
		EmailMonitorConfig vo = new EmailMonitorConfig();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setName(getParaValue("name"));
		vo.setAddress(getParaValue("address"));
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setRecivemail(getParaValue("recivemail"));
		vo.setTimeout(getParaIntValue("timeout"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setSupperid(getParaIntValue("supperid"));
		vo.setBid(getParaValue("bid"));
		vo.setReceiveAddress(getParaValue("receiveAddress"));
		return vo;
	}

	private String changeMonflag() {
		boolean result = false;
		EmailMonitorConfig emailMonitorConfig = new EmailMonitorConfig();
		EmailConfigDao emailConfigDao = null;
		try {
			String id = getParaValue("id");
			int monflag = getParaIntValue("value");
			emailConfigDao = new EmailConfigDao();
			emailMonitorConfig = (EmailMonitorConfig) emailConfigDao.findByID(id);
			emailMonitorConfig.setMonflag(monflag);
			result = emailConfigDao.update(emailMonitorConfig);
			Mail mail = (Mail) PollingEngine.getInstance().getMailByID(Integer.parseInt(id));
			mail.setFlag(monflag);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			emailConfigDao.close();
		}
		MailLoader loader = new MailLoader();
		loader.loading();
		if (result) {
			return list();
		} else {
			return "/application/ftp/savefail.jsp";
		}
	}

	private String search() {
		return "/application/db/sybaseconfigsearchlist.jsp";
	}

	private String detail() {
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List urllist = new ArrayList(); // 用做条件选择列表
		EmailMonitorConfig initconf = new EmailMonitorConfig(); // 当前的对象
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
			EmailConfigDao configdao = new EmailConfigDao();
			try {
				urllist = configdao.getByBID(rbids);
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
				configdao = new EmailConfigDao();
				try {
					initconf = (EmailMonitorConfig) configdao.findByID(queryid + "");
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
			EmailRealTimeDao realtimedao = new EmailRealTimeDao();
			try {
				urlList = realtimedao.getByEmailId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((EmailRealtime) urlList.get(0)).getMon_time();
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

			TimeSeries[] s = new TimeSeries[1];
			TimeSeries[] s_ = new TimeSeries[1];
			Vector wave_v = null;
			EmailHistoryDao historydao = new EmailHistoryDao();
			try {
				wave_v = historydao.getByMailid(queryid, starttime, totime, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				historydao.close();
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
			// 是否连通
			String conn[] = new String[2];
			historydao = new EmailHistoryDao();
			try {
				if (flag == 0)
					conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
				else {
					conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
				}
			} catch (Exception e) {
			} finally {
				historydao.close();
			}

			String[] key1 = { "连通", "未连通" };
			drawPiechart(key1, conn, "", conn_name);
			// drawchart(minutes,"连通",)
			// Vector conn_v =
			// historyManager.getInfo(queryid,initer,"is_canconnected");
			// for(int i=0;i<conn)
			if (flag == 0)
				connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// historydao.close();
			// configdao.close();
			// realtimedao.close();
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
		return "/application/email/detail.jsp";
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
		EmailMonitorConfig initconf = null;
		EmailConfigDao configdao = new EmailConfigDao();
		try {
			initconf = (EmailMonitorConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = initconf.getIpaddress();

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			EmailHistoryDao historydao = new EmailHistoryDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(queryid, starttime, totime);
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
				// nms_email_history表获取,没必要再从nms_email_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "mailpingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", initconf.getName());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			reporthash.put("type", "mail");
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "mail");
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
		EmailMonitorConfig initconf = null;
		EmailConfigDao configdao = new EmailConfigDao();
		try {
			initconf = (EmailMonitorConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			String name = "";
			if (initconf != null) {

				name = initconf.getName();
				String type = "    类型: 邮件服务监视";
				String sendMail = initconf.getAddress();
				String toMail = initconf.getRecivemail();
				ip = initconf.getIpaddress();
				infoList.add("    名称: " + name);
				infoList.add(type);
				infoList.add("    邮件地址: " + sendMail);
				infoList.add("    接受邮箱: " + toMail);
				newip = SysUtil.doip(ip);
			}
			reporthash.put("servicename", name);

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			EmailHistoryDao historydao = new EmailHistoryDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(queryid, starttime, totime);
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
				// nms_email_history表获取,没必要再从nms_email_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("comInfo", infoList);
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			reporthash.put("type", "mail");
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "mail");
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
		EmailMonitorConfig initconf = null;
		EmailConfigDao configdao = new EmailConfigDao();
		try {
			initconf = (EmailMonitorConfig) configdao.findByID(id);
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
				list = eventdao.getQuery(starttime, totime, "mail", status + "", level1 + "", user.getBusinessids(), initconf.getId());

				infolist = eventdao.findByCriteria(s.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventdao.close();
			}
			if (infolist != null && infolist.size() > 0) {
				int levezero = 0;
				int levelone = 0;
				int levletwo = 0;
				int levelthree = 0;

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					if (eventlist.getLevel1() == 0) {
						levezero = levezero + 1;
					} else if (eventlist.getLevel1() == 1) {
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
				ipeventList.add(levezero + "");
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
		request.setAttribute("type", "mail");
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
		try {
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "service", "mail");
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
			MailDataCollector mailcollector = new MailDataCollector();
			NodeGatherIndicators mailIndicatorsNode = new NodeGatherIndicators();
			mailIndicatorsNode.setNodeid(queryid + "");
			mailcollector.collect_Data(mailIndicatorsNode);
		} catch (Exception exc) {

		}
		return "/mail.do?action=detail&id=" + queryid;
	}

	private String isOK() {
		int queryid = getParaIntValue("id");
		EmailConfigDao configdao = new EmailConfigDao();
		EmailMonitorConfig mailconfig = null;
		try {
			mailconfig = (EmailMonitorConfig) configdao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		String reason = "服务有效";

		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "service", "mail");
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

		ReceiveMail receieveMail = new ReceiveMail();
		SendMail sendmail = new SendMail();

		boolean sendFlag = true;
		boolean receieveFlag = true;
		try {
			if (gatherHash.containsKey("send")) {
				try {
					if (configSendMail(sendmail, mailconfig)) {
						sendFlag = sendmail.sendmail();
					}
				} catch (Exception ex) {
					sendFlag = false;
					ex.printStackTrace();
				}
			}

			if (gatherHash.containsKey("receieve")) {
				try {
					receieveFlag = receieveMail.GetReceieveMail(mailconfig.getAddress(), mailconfig.getUsername(), mailconfig.getPassword());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (gatherHash.containsKey("receieve") && gatherHash.containsKey("send")) {
				if (sendFlag && receieveFlag) {
					reason = "服务有效";
				} else {
					if (sendFlag == true && receieveFlag == false) {
						reason = "邮件发送服务无效,邮件接收服务正常";
					} else if (sendFlag == false && receieveFlag == true) {
						reason = "邮件发送服务正常,邮件接收服务无效";
					} else {
						reason = "邮件服务无效";
					}
				}
			} else if (gatherHash.containsKey("receieve")) {
				// 只测试接收邮件服务
				if (receieveFlag) {
					reason = "邮件接收服务正常";
				} else {
					reason = "邮件接收服务无效";
				}
			} else if (gatherHash.containsKey("send")) {
				// 只测试发送邮件服务
				if (sendFlag) {
					reason = "邮件发送服务正常";
				} else {
					reason = "邮件发送服务无效";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("isOK", reason);
		request.setAttribute("name", mailconfig.getName());
		request.setAttribute("str", mailconfig.getAddress());
		return "/tool/mailisok.jsp";
	}

	private void drawPiechart(String[] keys, String[] values, String chname, String enname) {
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for (int i = 0; i < keys.length; i++) {
			if (values[i] != null && values[i].trim().length() > 0) {
				piedata.setValue(keys[i], new Double(values[i]).doubleValue());
			} else {
				// piedata.setValue(keys[i], new
				// Double(values[i]).doubleValue());
			}
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

	public boolean configSendMail(SendMail sendmail, EmailMonitorConfig mailConfig) throws AddressException {
		Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
		sendmail.setMailaddress(configAddress(mailConfig));
		System.out.println(mailConfig.getAddress());
		sendmail.setSendmail(mailConfig.getUsername());
		sendmail.setSendpasswd(mailConfig.getPassword());
		sendmail.setToAddr(mailConfig.getRecivemail());
		sendmail.setBody("邮件服务测试");
		sendmail.setSubject("邮件服务设置");
		sendmail.setFromAddr(mailConfig.getUsername() + "@" + configAddress(mailConfig));
		sendmail.setCcAddress(ccAddress);
		return true;
	}

	public String configAddress(EmailMonitorConfig mailCnfig) {
		String fromAddress = "";
		if (mailCnfig.getAddress().indexOf("mail") == -1 || mailCnfig.getAddress().indexOf("smtp") == -1) {
			// 为IP地址
			fromAddress = mailCnfig.getAddress();
		} else {
			// 为接受服务器(pop) 或者 为发送服务器(smtp)
			fromAddress = mailCnfig.getAddress().substring(5, mailCnfig.getAddress().length());
		}
		return fromAddress;
	}

}