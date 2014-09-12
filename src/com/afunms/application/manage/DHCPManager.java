package com.afunms.application.manage;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.DHCPLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.dhcp.WindowsDhcpScopeSnmp;
import com.afunms.report.abstraction.ExcelReport1;
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
import com.lowagie.text.DocumentException;

public class DHCPManager extends BaseManager implements ManagerInterface {

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	DateE datemanager = new DateE();

	private String list() {
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
		DHCPConfigDao configdao = new DHCPConfigDao();
		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else
				list = configdao.getDHCPByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		request.setAttribute("list", list);
		return "/application/dhcp/list.jsp";
	}

	public List getInfoByFlag(Integer flag) throws Exception {
		List list = new ArrayList();
		DHCPConfigDao dao = new DHCPConfigDao();
		try {
			list = dao.getByFlag(flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list;
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
		return "/application/dhcp/add.jsp";
	}

	private String add() {
		DHCPConfig vo = new DHCPConfig();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setAlias(getParaValue("name"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setCommunity(getParaValue("community"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		vo.setSupperid(getParaIntValue("supperid"));
		vo.setDhcptype(getParaValue("dhcptype"));
		vo.setNetid(getParaValue("bid"));
		DHCPConfigDao configdao = new DHCPConfigDao();
		try {
			configdao.save(vo);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			try {
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("12"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			try {
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("12"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("windows".equalsIgnoreCase(vo.getDhcptype())) {
				// WINDOWS DHCP
				// 初始化采集指标
				try {
					NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
					nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "windhcp", "1");
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

				// 初始化指标阀值
				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "windhcp");
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else if ("cisco".equalsIgnoreCase(vo.getDhcptype())) {
				// CISCO DHCP
				// 初始化采集指标
				try {
					NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
					nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "ciscodhcp", "1");
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				// 初始化指标阀值
				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "ciscodhcp");
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 保存应用
			HostApplyManager.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		// 在轮询线程中增加被监视节点
		DHCPLoader loader = new DHCPLoader();
		try {
			loader.loadOne(vo);
			loader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			monitorItemList = indicatorsdao.getByNodeId(vo.getId() + "", 1, "service", "dhcp");
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
		return "/dhcp.do?action=list&jp=1";
	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			DHCPConfigDao configdao = new DHCPConfigDao();
			List dhcpconfiglist = configdao.getidByIDS(ids);
			Hashtable dhcpconfigHash = new Hashtable();
			if (dhcpconfiglist != null && dhcpconfiglist.size() > 0) {
				for (int k = 0; k < dhcpconfiglist.size(); k++) {
					DHCPConfig dhcpconfig = (DHCPConfig) dhcpconfiglist.get(k);
					dhcpconfigHash.put(dhcpconfig.getId(), dhcpconfig);
				}
			}
			try {
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil(); // snow
				DHCPConfig vo = new DHCPConfig();
				for (int i = 0; i < ids.length; i++) {
					Node node = PollingEngine.getInstance().getDHCPByID(Integer.parseInt(ids[i]));
					vo = null;
					// 删除应用
					HostApplyDao hostApplyDao = null;
					try {
						hostApplyDao = new HostApplyDao();
						if (dhcpconfigHash.containsKey(node.getId())) {
							vo = (DHCPConfig) dhcpconfigHash.get(node.getId());
							if ("windows".equalsIgnoreCase(vo.getDhcptype())) {
								hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'windhcp' and nodeid = '" + ids[i] + "'");
							} else if ("cisco".equalsIgnoreCase(vo.getDhcptype())) {
								hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'ciscodhcp' and nodeid = '" + ids[i] + "'");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (hostApplyDao != null) {
							hostApplyDao.close();
						}
					}
					PollingEngine.getInstance().deleteDHCPByID(Integer.parseInt(ids[i]));
					try {
						timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("12"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("12")); // snow
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						if ("windows".equalsIgnoreCase(vo.getDhcptype())) {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "service", "windhcp");
						} else if ("cisco".equalsIgnoreCase(vo.getDhcptype())) {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "service", "ciscodhcp");
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(ids[i], "service", "dhcp");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
					// 更新业务视图
					String id = ids[i];
					NodeDependDao nodedependao = new NodeDependDao();
					List dhcplist = nodedependao.findByNode("dhcp" + id);
					if (dhcplist != null && dhcplist.size() > 0) {
						for (int j = 0; j < dhcplist.size(); j++) {
							NodeDepend dhcpvo = (NodeDepend) dhcplist.get(j);
							if (dhcpvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("dhcp" + id, dhcpvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("dhcp" + id, dhcpvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("dhcp" + id, dhcpvo.getXmlfile());
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
								ManageXml manageXml = (ManageXml) subMapDao.findByXml(dhcpvo.getXmlfile());
								if (manageXml != null) {
									NodeDependDao nodeDepenDao = new NodeDependDao();
									try {
										List lists = nodeDepenDao.findByXml(dhcpvo.getXmlfile());
										ChartXml chartxml;
										chartxml = new ChartXml("NetworkMonitor", "/" + dhcpvo.getXmlfile().replace("jsp", "xml"));
										chartxml.addBussinessXML(manageXml.getTopoName(), lists);
										ChartXml chartxmlList;
										chartxmlList = new ChartXml("NetworkMonitor", "/" + dhcpvo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
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
				configdao = new DHCPConfigDao();
				configdao.delete(ids);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
		}
		return list();
	}

	/**
	 * @author nielin add for time-sharing at 2010-01-05
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/dhcp/edit.jsp";
		List timeShareConfigList = new ArrayList();
		DHCPConfigDao dao = new DHCPConfigDao();
		try {
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("12"));
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("12"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("timeShareConfigList", timeShareConfigList);
		return jsp;
	}

	private String update() {
		DHCPConfig vo = new DHCPConfig();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("name"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setCommunity(getParaValue("community"));
		vo.setDhcptype(getParaValue("dhcptype"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20
		vo.setNetid(getParaValue("bid"));
		try {
			DHCPConfigDao configdao = new DHCPConfigDao();
			try {
				configdao.update(vo);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("12"));
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("12"));

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			if (PollingEngine.getInstance().getDHCPByID(vo.getId()) != null) {
				com.afunms.polling.node.DHCP dhcp = (com.afunms.polling.node.DHCP) PollingEngine.getInstance().getDHCPByID(vo.getId());
				dhcp.setAlias(vo.getAlias());
				dhcp.setIpAddress(vo.getIpAddress());
				dhcp.setCommunity(vo.getCommunity());
				dhcp.setBid(vo.getNetid());
				dhcp.setMon_flag(vo.getMon_flag());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/dhcp.do?action=list";
	}

	private String search() {
		return "/application/db/sybaseconfigsearchlist.jsp";
	}

	private String addalert() {
		DHCPConfig vo = new DHCPConfig();
		DHCPConfigDao configdao = null;
		try {
			configdao = new DHCPConfigDao();
			vo = (DHCPConfig) configdao.findByID(getParaValue("id"));
			vo.setMon_flag(1);
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		DHCPLoader loader = new DHCPLoader();
		loader.loading();
		return list();
	}

	private String cancelalert() {
		DHCPConfig vo = new DHCPConfig();
		DHCPConfigDao configdao = null;
		try {
			configdao = new DHCPConfigDao();
			vo = (DHCPConfig) configdao.findByID(getParaValue("id"));
			vo.setMon_flag(0);
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		DHCPLoader loader = new DHCPLoader();
		loader.loading();
		return list();
	}

	/**
	 * ping
	 * 
	 * @return
	 */
	private String detail() {
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		DHCPConfigDao configdao = new DHCPConfigDao();
		List dhcplist = new ArrayList(); // 用做条件选择列表
		DHCPConfig initconf = new DHCPConfig(); // 当前的对象
		String lasttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
		String connrate = "0";
		String responsetime = "0";
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
				dhcplist = configdao.getDHCPByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			Integer queryid = getParaIntValue("id");// .getUrl_id();
			request.setAttribute("id", queryid);
			if (dhcplist.size() > 0 && queryid == null) {
				Object obj = dhcplist.get(0);
			}
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DHCPConfigDao();
				try {
					initconf = (DHCPConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			Calendar last = null;
			if (ShareData.getDhcpdata() != null && ShareData.getDhcpdata().containsKey(initconf.getIpAddress())) {
				Hashtable dhcpHash = (Hashtable) ShareData.getDhcpdata().get(initconf.getIpAddress());
				if (dhcpHash != null && dhcpHash.containsKey("dhcpping")) {
					Vector dhcpV = (Vector) dhcpHash.get("dhcpping");
					Pingcollectdata pingdata = (Pingcollectdata) dhcpV.get(0);
					last = pingdata.getCollecttime();
					connrate = pingdata.getThevalue() + "%";
					pingdata = (Pingcollectdata) dhcpV.get(1);
					last = pingdata.getCollecttime();
					responsetime = pingdata.getThevalue();
				}
			}
			DateE de = new DateE();
			if (last == null) {
				last = new GregorianCalendar();
				flag = 1;
			}
			lasttime = de.getDateDetail(last);

			int hour = 1;
			if (getParaValue("hour") != null) {
				hour = Integer.parseInt(getParaValue("hour"));
			} else {
				request.setAttribute("hour", "1");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}

		String allipstr = SysUtil.doip(initconf.getIpAddress());
		String tablename = "dhcpping" + allipstr;
		request.setAttribute("allipstr", allipstr);
		request.setAttribute("tablename", tablename);
		request.setAttribute("urllist", dhcplist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("valid_name", valid_name);
		request.setAttribute("fresh_name", fresh_name);
		request.setAttribute("wave_name", wave_name);
		request.setAttribute("delay_name", delay_name);
		request.setAttribute("connrate", connrate);
		request.setAttribute("responsetime", responsetime);
		request.setAttribute("validrate", validrate);
		request.setAttribute("freshrate", freshrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		return "/application/dhcp/detail.jsp";
	}

	/**
	 * ping
	 * 
	 * @return
	 */
	private String scope() {
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		DHCPConfigDao configdao = new DHCPConfigDao();
		List dhcplist = new ArrayList(); // 用做条件选择列表
		DHCPConfig initconf = new DHCPConfig(); // 当前的对象
		List dhcpscopeList = new ArrayList();
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
		String connrate = "0";
		String responsetime = "0";
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
				dhcplist = configdao.getDHCPByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			Integer queryid = getParaIntValue("id");// .getUrl_id();
			request.setAttribute("id", queryid);
			if (dhcplist.size() > 0 && queryid == null) {
				Object obj = dhcplist.get(0);
			}
			if (queryid != null) {
				configdao = new DHCPConfigDao();
				try {
					initconf = (DHCPConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			Calendar last = null;
			if (ShareData.getDhcpdata() != null && ShareData.getDhcpdata().containsKey(initconf.getIpAddress())) {
				Hashtable dhcpHash = (Hashtable) ShareData.getDhcpdata().get(initconf.getIpAddress());
				if (dhcpHash != null && dhcpHash.containsKey("dhcpping")) {
					Vector dhcpV = (Vector) dhcpHash.get("dhcpping");
					Pingcollectdata pingdata = (Pingcollectdata) dhcpV.get(0);
					last = pingdata.getCollecttime();
					connrate = pingdata.getThevalue() + "%";
					pingdata = (Pingcollectdata) dhcpV.get(1);
					last = pingdata.getCollecttime();
					responsetime = pingdata.getThevalue();
				}
				if (dhcpHash != null && dhcpHash.containsKey("dhcpscopeValue")) {
					dhcpscopeList = (List) dhcpHash.get("dhcpscopeValue");
				}
			}
			DateE de = new DateE();
			if (last == null) {
				last = new GregorianCalendar();
				flag = 1;
			}
			lasttime = de.getDateDetail(last);

			int hour = 1;
			if (getParaValue("hour") != null) {
				hour = Integer.parseInt(getParaValue("hour"));
			} else {
				request.setAttribute("hour", "1");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		String allipstr = SysUtil.doip(initconf.getIpAddress());
		String tablename = "dhcpping" + allipstr;
		request.setAttribute("allipstr", allipstr);
		request.setAttribute("tablename", tablename);
		request.setAttribute("urllist", dhcplist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("valid_name", valid_name);
		request.setAttribute("fresh_name", fresh_name);
		request.setAttribute("wave_name", wave_name);
		request.setAttribute("delay_name", delay_name);
		request.setAttribute("connrate", connrate);
		request.setAttribute("responsetime", responsetime);
		request.setAttribute("validrate", validrate);
		request.setAttribute("freshrate", freshrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		request.setAttribute("dhcpscopeList", dhcpscopeList);
		return "/application/dhcp/scope.jsp";
	}

	/**
	 * ping
	 * 
	 * @return
	 */
	private String alarm() {
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		DHCPConfigDao configdao = new DHCPConfigDao();
		List dhcplist = new ArrayList(); // 用做条件选择列表
		DHCPConfig initconf = new DHCPConfig(); // 当前的对象
		List eventList = new ArrayList();
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
		String connrate = "0";
		String responsetime = "0";
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
				dhcplist = configdao.getDHCPByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();
			request.setAttribute("id", queryid);
			if (dhcplist.size() > 0 && queryid == null) {
				Object obj = dhcplist.get(0);
			}
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new DHCPConfigDao();
				try {
					initconf = (DHCPConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			Calendar last = null;
			if (ShareData.getDhcpdata() != null && ShareData.getDhcpdata().containsKey(initconf.getIpAddress())) {
				Hashtable dhcpHash = (Hashtable) ShareData.getDhcpdata().get(initconf.getIpAddress());
				if (dhcpHash != null && dhcpHash.containsKey("dhcpping")) {
					Vector dhcpV = (Vector) dhcpHash.get("dhcpping");
					Pingcollectdata pingdata = (Pingcollectdata) dhcpV.get(0);
					last = pingdata.getCollecttime();
					connrate = pingdata.getThevalue() + "%";
					pingdata = (Pingcollectdata) dhcpV.get(1);
					last = pingdata.getCollecttime();
					responsetime = pingdata.getThevalue();
				}
			}
			DateE de = new DateE();
			if (last == null) {
				last = new GregorianCalendar();
				flag = 1;
			}
			lasttime = de.getDateDetail(last);
			int hour = 1;
			if (getParaValue("hour") != null) {
				hour = Integer.parseInt(getParaValue("hour"));
			} else {
				request.setAttribute("hour", "1");
			}

			try {

				int status = getParaIntValue("status");
				int level1 = getParaIntValue("level1");
				if (status == -1)
					status = 99;
				if (level1 == -1)
					level1 = 99;
				request.setAttribute("status", status);
				request.setAttribute("level1", level1);

				String b_time = getParaValue("startdate");
				String t_time = getParaValue("todate");

				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				if (b_time == null) {
					b_time = sdf1.format(new Date());
				}
				if (t_time == null) {
					t_time = sdf1.format(new Date());
				}
				String starttime1 = b_time + " 00:00:00";
				String totime1 = t_time + " 23:59:59";
				request.setAttribute("startdate", b_time);
				request.setAttribute("todate", t_time);
				try {
					User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
					EventListDao eventdao = new EventListDao();
					try {
						eventList = eventdao.getQuery(starttime1, totime1, "dhcp", status + "", level1 + "", user.getBusinessids(), initconf.getId());
					} catch (Exception e) {

					} finally {
						eventdao.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		String allipstr = SysUtil.doip(initconf.getIpAddress());
		String tablename = "dhcpping" + allipstr;
		request.setAttribute("allipstr", allipstr);
		request.setAttribute("tablename", tablename);
		request.setAttribute("responsetime", responsetime);
		request.setAttribute("urllist", dhcplist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
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
		request.setAttribute("eventList", eventList);
		return "/application/dhcp/event.jsp";
	}

	public String transdetail() {
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		return "/application/weblogic/weblogic_transdetail.jsp";
	}

	public String servletdetail() {
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		return "/application/weblogic/weblogic_servletdetail.jsp";

	}

	public String eventdetail() {
		String id = request.getParameter("id");
		DHCPConfigDao dhcpconfigdao = new DHCPConfigDao();
		DHCPConfig dhcpconf = null;
		List list = new ArrayList();
		String flag = getParaValue("flag");
		try {
			dhcpconf = (DHCPConfig) dhcpconfigdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dhcpconfigdao.close();
		}
		try {
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);
			String b_time = getParaValue("startdate");
			String t_time = getParaValue("todate");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			if (b_time == null) {
				b_time = sdf1.format(new Date());
			}
			if (t_time == null) {
				t_time = sdf1.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				list = eventdao.getQuery(starttime1, totime1, "dhcp", status + "", level1 + "", user.getBusinessids(), dhcpconf.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", id);
		request.setAttribute("dhcpconf", dhcpconf);
		request.setAttribute("list", list);
		request.setAttribute("flag", flag);
		return "/application/dhcp/dhcp_eventdetail.jsp";
	}

	public double weblogicping(int id) {
		String strid = String.valueOf(id);
		DHCPConfig vo = new DHCPConfig();
		double avgpingcon = 0;
		String pingconavg = "0";
		try {
			DHCPConfigDao dao = new DHCPConfigDao();
			try {
				vo = (DHCPConfig) dao.findByID(strid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = getCategory(vo.getIpAddress(), "DHCPPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return avgpingcon;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (category.equals("DHCPPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from weblogicping" + allipstr + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,collecttime,h.unit from weblogicping" + allipstr + " h where ");
					}

				}
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.subentity='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= ");
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("'");
					sb.append(starttime);
					sb.append("'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')");
				}

				sb.append(" and h.collecttime <= ");
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("'");
					sb.append(endtime);
					sb.append("'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
				}

				sb.append(" order by h.collecttime");
				sql = sb.toString();
				SysLogger.info(sql);

				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				double pingcon = 0;
				double cpucon = 0;
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
					if (category.equals("DHCPPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
				if (category.equals("DHCPPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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

	public Hashtable getCollecttime(String ip) throws Exception {

		String collecttime = "";
		String nexttime = null;
		Hashtable pollingtime_ht = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s')) as collecttime from dhcpping" + allipstr + " h  ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS'))as collecttime from dhcpping" + allipstr + " h  ");
			}
			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			if (rs.next()) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				collecttime = rs.getString("collecttime");
				if (collecttime == null) {
					return null;
				}
				Date date = format.parse(collecttime);
				int mins = date.getMinutes() + 5;
				date.setMinutes(mins);
				nexttime = format.format(date);
				pollingtime_ht.put("lasttime", collecttime);
				pollingtime_ht.put("nexttime", nexttime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return pollingtime_ht;
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

	public String execute(String action) {
		if (action.equalsIgnoreCase("perReport")) {
			return perReport();
		}
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return ready_add();
		if (action.equals("add"))
			return add();
		if (action.equals("detail"))
			return detail();
		if (action.equals("delete"))
			return delete();
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("update"))
			return update();
		if (action.equals("addalert"))
			return addalert();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("scope"))// appaddress信息
			return scope();
		if (action.equals("alarm"))// 事件详细信息
			return alarm();

		if (action.equals("servletdetail"))// weblogic Servlet详细信息
			return servletdetail();

		if (action.equals("eventdetail"))// Weblogic 事件详细信息
			return eventdetail();
		if (action.equals("transdetail"))// Weblogic 事件详细信息
			return transdetail();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("search"))
			return search();
		if (action.equalsIgnoreCase("showPingReport")) {
			return showPingReport();
		}
		if (action.equalsIgnoreCase("eventReport")) {
			return eventReport();
		}
		if (action.equalsIgnoreCase("downloadEventReport")) {
			return downloadEventReport();
		}
		if (action.equalsIgnoreCase("downloadReport")) {
			return downloadReport();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String isOK() {

		int queryid = getParaIntValue("id");
		DHCPConfig dhcpconf = new DHCPConfig();
		DHCPConfigDao dao = new DHCPConfigDao();
		try {
			dhcpconf = (DHCPConfig) dao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String flag1 = "";
		if ("windows".equalsIgnoreCase(dhcpconf.getDhcptype())) {
			flag1 = "windhcp";
		} else {
			flag1 = "ciscodhcp";
		}
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();

		try {
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "service", flag1);
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
			if (nodeGatherIndicators.getName().equalsIgnoreCase("domain"))
				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}

		boolean flag = true;

		request.setAttribute("isOK", flag);
		request.setAttribute("name", dhcpconf.getAlias());
		request.setAttribute("str", dhcpconf.getIpAddress());
		return "/tool/weblogicisok.jsp";
	}

	private String sychronizeData() {
		int queryid = getParaIntValue("id");
		DHCPConfig dhcpconf = new DHCPConfig();
		DHCPConfigDao dao = new DHCPConfigDao();
		try {
			dhcpconf = (DHCPConfig) dao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String flag = "";
		if ("windows".equalsIgnoreCase(dhcpconf.getDhcptype())) {
			flag = "windhcp";
		} else {
			flag = "ciscodhcp";
		}
		String dbpage = getParaValue("dbPage");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "service", flag);
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

			NodeGatherIndicators vo = new NodeGatherIndicators();
			vo.setNodeid(queryid + "");
			WindowsDhcpScopeSnmp dhcpcollector = new WindowsDhcpScopeSnmp();
			dhcpcollector.collect_Data(vo);
		} catch (Exception exc) {

		}
		if ("detail".equalsIgnoreCase(dbpage)) {
			return "/dhcp.do?action=detail&id=" + queryid;
		} else if ("scope".equalsIgnoreCase(dbpage)) {
			return "/dhcp.do?action=scope&id=" + queryid;
		} else if ("alarm".equalsIgnoreCase(dbpage)) {
			return "/dhcp.do?action=alrm&id=" + queryid;
		} else
			return "/dhcp.do?action=detail&id=" + queryid;
	}

	public String getF(String s) {
		if (s.length() > 5)
			s = s.substring(0, 5);
		return s;
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
		com.afunms.polling.node.DHCP dhcp = (com.afunms.polling.node.DHCP) PollingEngine.getInstance().getDHCPByID(queryid);
		try {
			// ip = getParaValue("ipaddress");
			ip = dhcp.getIpAddress();
			newip = SysUtil.doip(ip);

			DHCPConfigDao dhcpdao = new DHCPConfigDao();

			Hashtable ConnectUtilizationhash = dhcpdao.getPingDataById(newip, queryid, starttime, totime);
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
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "dhcppingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", dhcp.getAlias());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", totime);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "DHCP");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/dhcp/showPingReport.jsp";
	}

	private String doip(String ip) {
		ip = SysUtil.doip(ip);
		return ip;
	}

	public String eventReport() {
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
			com.afunms.polling.node.DHCP dhcp = (com.afunms.polling.node.DHCP) PollingEngine.getInstance().getDHCPByID(Integer.parseInt(tmp));
			ip = dhcp.getIpAddress();
			String newip = doip(ip);
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

				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "dhcp");

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ip", ip);
		request.setAttribute("vector", vector);
		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/dhcp/eventReport.jsp";
	}

	// event 报表
	private String downloadEventReport() {
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

		String id = request.getParameter("id");

		Hashtable reporthash = new Hashtable();
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}
		Node iisNode = PollingEngine.getInstance().getDHCPByID(Integer.parseInt(id));
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		s.append(" and nodeid=" + iisNode.getId());

		List infolist = eventdao.findByCriteria(s.toString());
		reporthash.put("eventlist", infolist);

		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/DHCPEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventDoc(fileName, starttime, totime, "DHCP");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/DHCPEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_TomcatEventExc(file, id, starttime, totime, "DHCP");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/DHCPEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventPdf(fileName, starttime, totime, "DHCP");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";
	}

	public String perReport() {

		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("flag", flag);

		DHCPConfig initconf = null;
		DHCPConfigDao dao = new DHCPConfigDao();
		try {
			initconf = (DHCPConfig) dao.findByID(id);
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		List dhcpscopeList = new ArrayList();
		if (ShareData.getDhcpdata() != null && ShareData.getDhcpdata().containsKey(initconf.getIpAddress())) {
			Hashtable dhcpHash = (Hashtable) ShareData.getDhcpdata().get(initconf.getIpAddress());
			if (dhcpHash != null && dhcpHash.containsKey("dhcpscopeValue")) {
				dhcpscopeList = (List) dhcpHash.get("dhcpscopeValue");
			}
		}

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
		com.afunms.polling.node.DHCP dhcp = (com.afunms.polling.node.DHCP) PollingEngine.getInstance().getDHCPByID(queryid);
		try {
			ip = dhcp.getIpAddress();
			newip = SysUtil.doip(ip);

			DHCPConfigDao dhcpdao = new DHCPConfigDao();

			Hashtable ConnectUtilizationhash = dhcpdao.getPingDataById(newip, queryid, starttime, totime);
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
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "dhcppingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", dhcp.getAlias());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", totime);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "DHCP");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("id", id);
		request.setAttribute("dhcpscopeList", dhcpscopeList);
		request.setAttribute("ip", initconf.getIpAddress());

		return "/application/dhcp/perReport.jsp";
	}

	private String downloadReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		String id = request.getParameter("id");
		String flag = request.getParameter("flag");

		if ("per".equalsIgnoreCase(flag)) {
			if ("0".equals(str)) {
				// excel综合报表
				report.createReportxls_dhcp_per("temp/dhcp_PerReport.xls", id);
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dhcp_PerReport.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_dhcp_per(fileName, "doc", id);// word综合报表
					request.setAttribute("filename", fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dhcp_PerReport.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_dhcp_per(fileName, "pdf", id);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "/capreport/service/download.jsp";
	}
}