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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.ResinDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.model.Resin;
import com.afunms.application.model.Tomcat;
import com.afunms.application.resinmonitor.ResinServerStream;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.detail.service.resinInfo.ResinInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.ResinLoader;
import com.afunms.polling.loader.TomcatLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.task.ResinDataCollector;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.DocumentException;

public class ResinManager extends BaseManager implements ManagerInterface {

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

		List list = null;
		ResinDao dao = new ResinDao();
		try {
			if (operator.getRole() == 0) {
				list = dao.loadAll();
			} else
				list = dao.getResinByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list == null)
			list = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Resin vo = (Resin) list.get(i);
			Node resinNode = PollingEngine.getInstance().getResinByID(vo.getId());
			if (resinNode == null)
				vo.setStatus(0);
			else
				vo.setStatus(resinNode.getStatus());
		}
		request.setAttribute("list", list);
		return "/application/resin/list.jsp";
	}

	/**
	 * 增加前将供应商查找到
	 * 
	 * @return
	 */
	private String ready_add() {
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);
		return "/application/resin/add.jsp";
	}

	private String add() {
		Resin vo = new Resin();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20
		vo.setVersion(getParaValue("version"));
		vo.setJvmversion("");
		vo.setJvmvender("");
		vo.setOs("");
		vo.setOsversion("");
		vo.setBid(getParaValue("bid"));

		// 在数据库里增加被监控指标
		DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
		try {
			dcDao.addMonitor(vo.getId(), vo.getIpAddress(), "resin");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dcDao.close();
		}

		ResinDao dao = new ResinDao();
		try {
			dao.save(vo);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("19"));
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("19"));
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "middleware", "resin", "1");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "middleware", "resin");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			// 保存应用
			HostApplyManager.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// 在轮询线程中增加被监视节点
		ResinLoader loader = new ResinLoader();
		loader.loading();

		return "/resin.do?action=list";
	}

	public String delete() {
		String id = getParaValue("radio");
		ResinDao dao = new ResinDao();
		try {
			Node node = PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
			// 删除应用
			HostApplyDao hostApplyDao = null;
			try {
				hostApplyDao = new HostApplyDao();
				hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'resin' and nodeid = '" + id + "'");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (hostApplyDao != null) {
					hostApplyDao.close();
				}
			}

			PollingEngine.getInstance().deleteResinByID(Integer.parseInt(id));
			dao.delete(id);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.deleteTimeShareConfig(id, timeShareConfigUtil.getObjectType("19"));
			/* snow add at 2010-5-20 */
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			tg.deleteTimeGratherConfig(id, tg.getObjectType("19"));
			/* snow add end */

			// 删除该数据库的采集指标
			NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
			try {
				gatherdao.deleteByNodeIdAndTypeAndSubtype(id, "middleware", "resin");
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				gatherdao.close();
			}
			// 删除该数据库的告警阀值
			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
			try {
				indidao.deleteByNodeId(id, "middleware", "resin");
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				indidao.close();
			}

			// String[] nmsTempDataTables = { "nms_resin_temp",
			// "system_eventlist" };
			// String[] ids = new String[] { id };
			// CreateTableManager createTableManager = new CreateTableManager();
			// createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		// 更新业务视图
		NodeDependDao nodedependao = new NodeDependDao();
		List list = nodedependao.findByNode("res" + id);
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				NodeDepend vo = (NodeDepend) list.get(j);
				if (vo != null) {
					LineDao lineDao = new LineDao();
					lineDao.deleteByidXml("res" + id, vo.getXmlfile());
					NodeDependDao nodeDependDao = new NodeDependDao();
					if (nodeDependDao.isNodeExist("res" + id, vo.getXmlfile())) {
						nodeDependDao.deleteByIdXml("res" + id, vo.getXmlfile());
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
					ManageXml manageXml = (ManageXml) subMapDao.findByXml(vo.getXmlfile());
					if (manageXml != null) {
						NodeDependDao nodeDepenDao = new NodeDependDao();
						try {
							List lists = nodeDepenDao.findByXml(vo.getXmlfile());
							ChartXml chartxml;
							chartxml = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml"));
							chartxml.addBussinessXML(manageXml.getTopoName(), lists);
							ChartXml chartxmlList;
							chartxmlList = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
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

		ResinLoader loader = new ResinLoader();
		loader.loading();

		return "/resin.do?action=list";
	}

	/**
	 * @author nielin add for time-sharing at 2010-01-05
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/resin/edit.jsp";
		List timeShareConfigList = new ArrayList();
		ResinDao dao = new ResinDao();
		try {
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("13"));
			/* 获得设备的采集时间 snow add at 2010-05-20 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("13"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
			/* snow end */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("timeShareConfigList", timeShareConfigList);
		return jsp;
	}

	private String update() {
		Resin vo = new Resin();
		vo.setId(getParaIntValue("id"));
		ResinDao _dao = new ResinDao();
		Resin pvo = null;
		try {
			pvo = (Resin) _dao.findByID(vo.getId() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_dao.close();
		}

		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20
		vo.setVersion(pvo.getVersion());
		vo.setJvmversion(pvo.getJvmversion());
		vo.setJvmvender(pvo.getJvmvender());
		vo.setOs(pvo.getOs());
		vo.setOsversion(pvo.getOsversion());

		vo.setBid(getParaValue("bid"));

		if (PollingEngine.getInstance().getResinByID(vo.getId()) != null) {
			com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(vo.getId());
			resin.setUser(vo.getUser());
			resin.setPassword(vo.getPassword());
			resin.setPort(vo.getPort());
			resin.setIpAddress(vo.getIpAddress());
			resin.setAlias(vo.getAlias());
			resin.setSendemail(vo.getSendemail());
			resin.setSendmobiles(vo.getSendmobiles());
			resin.setSendphone(vo.getSendphone());
			resin.setBid(vo.getBid());
			resin.setMonflag(vo.getMonflag());
			resin.setVersion(vo.getVersion());
			resin.setJvmversion(vo.getJvmversion());
			resin.setJvmvender(vo.getJvmvender());
			resin.setOs(vo.getOs());
			resin.setOsversion(vo.getOsversion());
			// SysLogger.info(resin.getId()+"===="+resin.getPort()+"====resin");
		}
		try {
			_dao = new ResinDao();
			List list = _dao.loadAll();
			if (list == null)
				list = new ArrayList();
			ShareData.setResinlist(list);
			ResinLoader loader = new ResinLoader();
			loader.clearRubbish(list);
		} catch (Exception e) {

		} finally {
			_dao.close();
		}
		ResinDao dao = new ResinDao();
		try {
			if (dao.update(vo)) {
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
				// add
				// for
				// time-sharing
				// at
				// 2010-01-05
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("13"));
				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("13"));
				/* snow add end */
				return list();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			dao.close();
		}

	}

	private String syncconfig() {
		ResinDao _dao = new ResinDao();
		Resin pvo = null;
		try {
			pvo = (Resin) _dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_dao.close();
		}
		String runmodel = PollingEngine.getCollectwebflag();
		Hashtable hash_data = null;
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable resinvalues = ShareData.getResindata();
			if (resinvalues != null && resinvalues.size() > 0) {
				hash_data = (Hashtable) resinvalues.get(pvo.getIpAddress());
			}
		} else {
			// 采集与访问分离模式
			ResinInfoService resinInfoService = new ResinInfoService();
			hash_data = resinInfoService.getResinDataHashtable(pvo.getId() + "");
		}
		if (hash_data != null && hash_data.size() > 0) {
			String server = hash_data.get("server").toString();
			// String jvm=hash_data.get("jvm").toString();
			if (server != null) {
				String[] temserver = server.split(",");
				String resin_version = temserver[0];
				String jvm_version = temserver[1];
				String jvm_vender = temserver[2];
				String os_name = temserver[3];
				String os_version = temserver[4];
				pvo.setVersion(resin_version);
				pvo.setJvmversion(jvm_version);
				pvo.setJvmvender(jvm_vender);
				pvo.setOs(os_name);
				pvo.setOsversion(os_version);

				if (PollingEngine.getInstance().getResinByID(pvo.getId()) != null) {
					com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(pvo.getId());
					resin.setVersion(pvo.getVersion());
					resin.setJvmversion(pvo.getJvmversion());
					resin.setJvmvender(pvo.getJvmvender());
					resin.setOs(pvo.getOs());
					resin.setOsversion(pvo.getOsversion());
				}

				ResinDao dao = new ResinDao();
				try {
					if (dao.update(pvo)) {
						return "/resin.do?action=resin_jvm&id=" + pvo.getId();
					} else {
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					dao.close();
				}
			}
		}
		// }
		setTarget("/application/resin/resin_detail2.jsp");
		return "/resin.do?action=resin_jvm&id=" + pvo.getId();

	}

	// public Hashtable detail(com.afunms.polling.node.Resin node) {
	// Hashtable data_ht = new Hashtable();
	// try {
	// ServerStream serverstream = new ServerStream();
	// Hashtable returnVal = new Hashtable();
	// String ip = "";
	// try {
	// com.afunms.polling.node.Resin tc = new com.afunms.polling.node.Resin();
	// BeanUtils.copyProperties(tc, node);
	// ip = tc.getIpAddress();
	// StringBuffer tmp = new StringBuffer();
	// tmp.append(tc.getIpAddress());
	// tmp.append(",");
	// tmp.append(tc.getPort());
	// tmp.append(",");
	// tmp.append(tc.getUser());
	// tmp.append(" , ");
	// tmp.append(tc.getPassword());
	// returnVal.put(String.valueOf(0), tmp.toString());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// String liststr = serverstream.validServer(returnVal);
	// SysLogger.info(liststr);
	// if ("".equals(liststr))
	// return null;
	// String[] pos_s = liststr.split(",");
	// for (int list_i = 0; list_i < pos_s.length - 1; list_i++) {
	// String tmps = returnVal.get(pos_s[list_i]).toString();
	// String[] serverinfo = tmps.split(",");
	// serverstream.foundData(serverinfo[0], serverinfo[1], serverinfo[2],
	// serverinfo[3]);
	// serverstream.foundApp(serverinfo[0], serverinfo[1], serverinfo[2],
	// serverinfo[3]);
	// data_ht = serverstream.data_ht;
	// data_ht.put("application", serverstream.app_info);
	// Hashtable sendeddata = ShareData.getSendeddata();
	//
	// try {
	//
	// if (data_ht == null) {
	// Pingcollectdata hostdata = null;
	// hostdata = new Pingcollectdata();
	// hostdata.setIpaddress(ip);
	// Calendar date = Calendar.getInstance();
	// hostdata.setCollecttime(date);
	// hostdata.setCategory("ResinPing");
	// hostdata.setEntity("Utilization");
	// hostdata.setSubentity("ConnectUtilization");
	// hostdata.setRestype("dynamic");
	// hostdata.setUnit("%");
	// hostdata.setThevalue("0");
	// ResinDao ResinDao = new ResinDao();
	// try {
	// ResinDao.createHostData(hostdata);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// ResinDao.close();
	// }
	// } else {
	// Pingcollectdata hostdata = null;
	// hostdata = new Pingcollectdata();
	// hostdata.setIpaddress(ip);
	// Calendar date = Calendar.getInstance();
	// hostdata.setCollecttime(date);
	// hostdata.setCategory("ResinPing");
	// hostdata.setEntity("Utilization");
	// hostdata.setSubentity("ConnectUtilization");
	// hostdata.setRestype("dynamic");
	// hostdata.setUnit("%");
	// hostdata.setThevalue("100");
	// ResinDao ResinDao = new ResinDao();
	// try {
	// ResinDao.createHostData(hostdata);
	// if (sendeddata.containsKey("resin" + ":" + ip))
	// sendeddata.remove("resin" + ":" + ip);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// ResinDao.close();
	// }
	// }
	// if (data_ht != null) {
	// String jvm = data_ht.get("jvm").toString();
	// int jvm_memoryuiltillize = 0;
	// String jvm_utilization = null;
	// String[] temjvm = jvm.split(",");
	// double freememory = Double.parseDouble(temjvm[0].trim());
	// double totalmemory = (double) Double.parseDouble(temjvm[1].trim());
	// double maxmemory = (double) Double.parseDouble(temjvm[2].trim());
	//
	// jvm_memoryuiltillize = (int) Math.rint((totalmemory - freememory) * 100 /
	// totalmemory);
	// jvm_utilization = String.valueOf(jvm_memoryuiltillize);
	//
	// Pingcollectdata hostdata = null;
	// hostdata = new Pingcollectdata();
	// hostdata.setIpaddress(ip);
	// Calendar date = Calendar.getInstance();
	// hostdata.setCollecttime(date);
	// hostdata.setCategory("resin_jvm");
	// hostdata.setEntity("Utilization");
	// hostdata.setSubentity("jvm_utilization");
	// hostdata.setRestype("dynamic");
	// hostdata.setUnit("%");
	// hostdata.setThevalue(jvm_utilization);
	// ResinDao ResinDao = new ResinDao();
	// try {
	// ResinDao.createHostData(hostdata);
	// if (sendeddata.containsKey("resin" + ":" + ip))
	// sendeddata.remove("resin" + ":" + ip);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// ResinDao.close();
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// // ResinDao.close();
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// }
	// return data_ht;
	// }

	public double resinping(int id) {
		String strid = String.valueOf(id);
		Resin vo = new Resin();
		ResinDao dao = new ResinDao();
		double avgpingcon = 0;
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg = "0";
		try {
			vo = (Resin) dao.findByID(strid);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = getCategory(vo.getIpAddress(), "ResinPing", "ConnectUtilization", starttime1, totime1, "");
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
		} finally {
			dao.close();
		}
		return avgpingcon;
	}

	public Hashtable getCollecttime(String ip) throws Exception {

		String collecttime = "";
		String nexttime = "";
		Hashtable pollingtime_ht = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			// String ip1 ="",ip2="",ip3="",ip4="";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".")>0){
			// ip1=ip.substring(0,ip.indexOf("."));
			// ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());
			// tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
			// }
			// ip2=tempStr.substring(0,tempStr.indexOf("."));
			// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
			// allipstr=ip1+ip2+ip3+ip4;
			String allipstr = SysUtil.doip(ip);

			String sql = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s')) as collecttime from resinping" + allipstr + " h  ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h.collecttime) as collecttime from resinping" + allipstr + " h  ");
			}
			sql = sb.toString();

			rs = dbmanager.executeQuery(sql);
			if (rs.next()) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				collecttime = rs.getString("collecttime");
				if (collecttime != null && collecttime.trim().length() > 0) {
					Date date = format.parse(collecttime);
					int mins = date.getMinutes() + 5;
					date.setMinutes(mins);
					nexttime = format.format(date);
				} else {
					collecttime = "";
					nexttime = "";
				}
				pollingtime_ht.put("lasttime", collecttime);
				pollingtime_ht.put("nexttime", nexttime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {

				}
			}
			dbmanager.close();
		}

		return pollingtime_ht;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime, String time) throws Exception {
		Hashtable hash = new Hashtable();
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			// con=DataGate.getCon();
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("ResinPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from resinping" + time + allipstr + " h where ");
					}
					if (category.contains("resin_")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from resin_mem" + allipstr + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("ResinPing")) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from resinping" + time + allipstr + " h where ");
					}
					if (category.contains("resin_")) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from resin_mem" + allipstr + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= ");
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" and h.collecttime <= ");
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" order by h.collecttime");
				}

				sql = sb.toString();
				// SysLogger.info("=="+sql);
				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "%";
				// String max = "";
				double tempfloat = 0;
				double pingcon = 0;
				double resin_mem_con = 0;
				int downnum = 0;
				int i = 0;
				String thevalue = "0";
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					if (category.equals("ResinPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
					} else if (category.contains("resin_")) {
						resin_mem_con = resin_mem_con + getfloat(thevalue);
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
				if (category.equals("ResinPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + unit);
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				}
				if (category.contains("resin_")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avg_resin_mem", CEIString.round(resin_mem_con / list1.size(), 2) + unit);

					} else {
						hash.put("avg_resin_mem", "0.0%");
					}
				}
				hash.put("curcon", thevalue + unit);
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

	public String tcpport() {
		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("flag", flag);

		com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
		String ip = resin.getIpAddress();
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		List tcpportList = null;
		if (ShareData.getResindata().get(ip) != null) {
			Hashtable ht_app = (Hashtable) ShareData.getResindata().get(ip);
			tcpportList = (ArrayList) ht_app.get("resin_tcpports");
			if (tcpportList == null)
				tcpportList = new ArrayList();
		}
		request.setAttribute("id", id);
		request.setAttribute("tcpportList", tcpportList);
		return "/application/resin/tcpport.jsp";

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
		Resin resin = null;
		ResinDao resdao = new ResinDao();
		try {
			resin = (Resin) resdao.findByID(String.valueOf(queryid));

			ip = resin.getIpAddress();

			newip = SysUtil.doip(ip);

			Hashtable ConnectUtilizationhash = resdao.getPingDataById(newip, queryid, starttime, totime);
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
				// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "pingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", resin.getAlias());
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
		} finally {
			resdao.close();
		}
		return "/application/resin/showPingReport.jsp";
	}

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
		Resin resin = null;
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		ResinDao resinDao = new ResinDao();
		try {
			resin = (Resin) resinDao.findByID(String.valueOf(queryid));
			ip = resin.getIpAddress();
			newip = SysUtil.doip(ip);
			Hashtable ConnectUtilizationhash = resinDao.getPingDataById(newip, queryid, starttime, totime);
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
////////////////////////////////
			if(resin.getVersion().equals("3")){
				resinDao=new ResinDao();
			Hashtable memUtilizationhash = resinDao.getMemDataById(newip, queryid,"mem_utilization", starttime, totime);
			String curmemcon = "0";
			String avgmemcon = "0";
			String maxmemcon = "0";
			if (memUtilizationhash.get("avgmemcon") != null)
				avgmemcon = (String) memUtilizationhash.get("avgmemcon");
			
			if (memUtilizationhash.get("maxmemcon") != null) {
				maxmemcon = (String) memUtilizationhash.get("maxmemcon");
			}
			if (memUtilizationhash.get("curmemcon") != null) {
				curmemcon = (String) memUtilizationhash.get("curmemcon"); 
			}
			
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType("minute", memUtilizationhash, "内存利用率", newip + "memUtil", 740, 150);
			request.setAttribute("maxmemcon", maxmemcon);
			request.setAttribute("curmemcon", curmemcon);
			request.setAttribute("avgmemcon", avgmemcon);
			reporthash.put("maxmemcon",maxmemcon);
			reporthash.put("curmemcon",curmemcon);
			reporthash.put("avgmemcon",avgmemcon);
			reporthash.put("version",resin.getVersion());
			}else if(resin.getVersion().equals("4")){
				reporthash.put("version",resin.getVersion());	
				String avgpingStr = "0";
				String avgpingcon ="0";
				String avgmem = "";
				String avgmemcon ="0";
				String curcon="0"; 
				String max="0"; 
				PollMonitorManager pollMonitorManager = new PollMonitorManager();
				resinDao=new ResinDao();
				Hashtable heapHash = resinDao.getMemDataById(newip, queryid,"heap_utilization", starttime, totime);
				if (heapHash != null) {
					if (heapHash.get("avgmemcon") != null)
						avgmemcon = (String) heapHash.get("avgmemcon");
					
					if (heapHash.get("maxmemcon") != null) {
						max = (String) heapHash.get("maxmemcon");
					}
					if (heapHash.get("curmemcon") != null) {
						curcon = (String) heapHash.get("curmemcon"); 
					}
//					request.setAttribute("avgheap", heapHash);
//					request.setAttribute("avgheapcon", avgmemcon);
//					request.setAttribute("maxheapcon", max);
//					request.setAttribute("curheapcon", curcon);
					reporthash.put("avgheap",heapHash);
					reporthash.put("avgheapcon",avgmemcon);
					reporthash.put("maxheapcon",max);
					reporthash.put("curheapcon",curcon);
//					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					
					pollMonitorManager.chooseDrawLineType("minute", heapHash, "Heap利用率", newip + "heapUtil", 740, 150);
				}
				resinDao=new ResinDao();
				Hashtable swapHash = resinDao.getMemDataById(newip, queryid, "swap_utilization", starttime, totime);
				if (swapHash != null) {
					if (swapHash.get("avgmemcon") != null)
						avgmemcon = (String) swapHash.get("avgmemcon");
					
					if (swapHash.get("maxmemcon") != null) {
						max = (String) swapHash.get("maxmemcon");
					}
					if (swapHash.get("curmemcon") != null) {
						curcon = (String) swapHash.get("curmemcon"); 
					}
//					request.setAttribute("avgswap", swapHash);
//					request.setAttribute("avgswapcon", avgmem);
//					request.setAttribute("maxswapcon", max);
//					request.setAttribute("curswapcon", curcon);
					reporthash.put("avgswap",swapHash);
					reporthash.put("avgswapcon",avgmemcon);
					reporthash.put("maxswapcon",max);
					reporthash.put("curswapcon",curcon);
//					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					pollMonitorManager.chooseDrawLineType("minute", swapHash, "交换内存率", newip + "swapUtil", 740, 150);
				}
				resinDao=new ResinDao();
				Hashtable physicalHash = resinDao.getMemDataById(newip, queryid, "physical_utilization", starttime, totime);
				if (physicalHash != null) {
					if (physicalHash.get("avgmemcon") != null)
						avgmemcon = (String) physicalHash.get("avgmemcon");
					
					if (physicalHash.get("maxmemcon") != null) {
						max = (String) physicalHash.get("maxmemcon");
					}
					if (physicalHash.get("curmemcon") != null) {
						curcon = (String) physicalHash.get("curmemcon"); 
					}
//					request.setAttribute("avgphy", physicalHash);
//					request.setAttribute("avgphycon", avgmemcon);
//					request.setAttribute("maxsphycon", max);
//					request.setAttribute("curphycon", curcon);
					reporthash.put("avgphy",physicalHash);
					reporthash.put("avgphycon",avgmemcon);
					reporthash.put("maxphycon",max);
					reporthash.put("curphycon",curcon);
					pollMonitorManager.chooseDrawLineType("minute", physicalHash, "物理内存率", newip + "phyUtil", 740, 150);
//					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
//					pollMonitorManager.chooseDrawLineType("minute", physicalHash, "物理内存率", newip + "memUtil", 740, 150);
				}

			
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

				list = dao.getQuery(starttime, totime, status + "", level1 + "", vo.getBusinessids(), queryid, "resin");

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("list", list);
			// 画图-----------------------------
			reporthash.put("servicename", resin.getAlias());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			reporthash.put("eventlist", list);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "tftp");
			
			session.setAttribute("resinreporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/application/resin/resinAllReport.jsp";
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
		Hashtable reporthash = (Hashtable) session.getAttribute("resinreporthash");
	
		// ///////////////////////////////////////////////////
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/resinEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_resinDoc(fileName, starttime, totime, "resin");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/resin_Report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_ResinExc(file, id, starttime, totime, "resin");
			} catch (IOException e) {
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/resin_Report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {

				report1.createReport_ResinPdf(fileName, id + "", starttime, totime, "resin");

			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	public String app() {
		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("flag", flag);

		com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
		String ip = resin.getIpAddress();
		Hashtable imgurlhash = new Hashtable();
		try {
			String newip = doip(ip);
			String[] time = { "", "" };
			getTime(request, time);
			String starttime = time[0];
			String endtime = time[1];
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			try {
				Hashtable hash1 = getCategory(ip, "ResinPing", "ConnectUtilization", starttime1, totime1, "");
				p_draw_line(hash1, "连通率", newip + "ResinPing", 740, 150);
				Hashtable hash = getCategory(ip, "resin_jvm", "mem_utilization", starttime1, totime1, "");
				p_draw_line(hash, "内存利用率", newip + "resin_mem", 740, 150);
				if (hash1 != null)
					request.setAttribute("pingcon", hash1);
				if (hash != null)
					request.setAttribute("avgjvm", hash);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// imgurlhash
			imgurlhash.put("resin_mem", "resource/image/jfreechart/" + newip + "resin_mem" + ".png");
			imgurlhash.put("ResinPing", "resource/image/jfreechart/" + newip + "ResinPing" + ".png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String app_table = "";
		if (ShareData.getResindata().get(ip) != null) {
			Hashtable ht_app = (Hashtable) ShareData.getResindata().get(ip);
			app_table = (String) ht_app.get("application");
			if (app_table == null)
				app_table = "";
		}
		request.setAttribute("imgurlhash", imgurlhash);
		request.setAttribute("id", id);
		request.setAttribute("application", app_table);
		return "/application/resin/app.jsp";

	}

	public String connPool() {
		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("flag", flag);

		String ip = "";
		ResinDao dao = null;
		Resin vo = null;
		try {
			dao = new ResinDao();
			vo = (Resin) dao.findByID(id);
			ip = vo.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		List connPollList = null;
		if (ShareData.getResindata().get(ip) != null) {
			Hashtable ht_app = (Hashtable) ShareData.getResindata().get(ip);
			if (vo.getVersion().equals("3")) {
				connPollList = (ArrayList) ht_app.get("resin_connectionpools");
			} else if (vo.getVersion().equals("4")) {
				connPollList = (ArrayList) ht_app.get("resin_databasepools");
			}
			if (connPollList == null)
				connPollList = new ArrayList();
		}
		request.setAttribute("id", id);
		request.setAttribute("connPollList", connPollList);
		return "/application/resin/resin_connPool.jsp";
	}



	private String doip(String ip) {
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}

	private void getTime(HttpServletRequest request, String[] time) {
		Calendar current = new GregorianCalendar();
		String key = getParaValue("beginhour");
		if (getParaValue("beginhour") == null) {
			Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			request.setAttribute("beginhour", new Integer(hour.intValue() - 1));
			request.setAttribute("endhour", hour);
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

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);

				String sql = "";
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("ResinPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from resinping" + allipstr + " h where ");
					}
					if (category.contains("resin_")) {
						sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from resin_mem" + allipstr + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("ResinPing")) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from resinping" + allipstr + " h where ");
					}
					if (category.contains("resin_")) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from resin_mem" + allipstr + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= ");
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" and h.collecttime <= ");
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" order by h.collecttime");
				}
				sql = sb.toString();

				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				double tempfloat1 = 0;
				double pingcon = 0;

				double resin_jvm_con = 0;
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
					if (category.equals("ResinPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					}
					if (category.contains("resin_") && subentity.contains("utilization")) {
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
					} else if (category.contains("resin_")) {
						resin_jvm_con = resin_jvm_con + getfloat(thevalue);
						if (i == 1)
							tempfloat1 = getfloat(thevalue);

						if (tempfloat1 > getfloat(thevalue))
							tempfloat1 = getfloat(thevalue);

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
				if (category.equals("ResinPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
				// zhushouzhi-------------------jvm
				if (category.contains("resin_")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avg_resin_mem", CEIString.round(resin_jvm_con / list1.size(), 2) + unit);
						hash.put("max_resin_mem", CEIString.round(tempfloat1, 2) + unit + "");
					} else {
						hash.put("avg_resin_mem", "0.0%");
						hash.put("max_resin_mem", "0.0%");
					}
				}
				// zhushouzhi------------------------jvm
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

	/**
	 * 
	 * @description 数据同步
	 * @author wangxiangyong
	 * @date Apr 28, 2013 11:34:26 AM
	 * @return String
	 * @return
	 */
	private String sychronizeData() {

		int queryid = getParaIntValue("id");

		try {
			ResinDataCollector resincollector = new ResinDataCollector();
			resincollector.collectResinData(queryid + "");
		} catch (Exception exc) {
			SysLogger.error("Resin数据同步", exc);
		}
		return "/resin.do?action=system&id=" + queryid;
	}

	/**
	 * 
	 * @description 检测resin可用性
	 * @author wangxiangyong
	 * @date Apr 28, 2013 11:40:08 AM
	 * @return String
	 * @return
	 */
	private String isOK() {

		int queryid = getParaIntValue("id");

		ResinDao dao = new ResinDao();
		Resin node = new Resin();

		boolean isOK = true;
		try {
			ResinServerStream serverstream = new ResinServerStream();
			Hashtable returnVal = new Hashtable();
			String ipaddress = "";

			try {
				node = (Resin) dao.findByID(queryid + "");
			} catch (Exception e) {
			} finally {
				dao.close();
			}
			try {
				com.afunms.polling.node.Resin tc = new com.afunms.polling.node.Resin();
				BeanUtils.copyProperties(tc, node);
				ipaddress = tc.getIpAddress();
				com.afunms.polling.node.Resin tnode = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByIP(ipaddress);

				StringBuffer tmp = new StringBuffer();
				tmp.append(tc.getIpAddress());
				tmp.append(",");
				tmp.append(tc.getPort());
				tmp.append(",");
				tmp.append(tc.getUser());
				tmp.append(" , ");
				tmp.append(tc.getPassword());
				returnVal.put(String.valueOf(0), tmp.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			String liststr = serverstream.validResinServer(returnVal);
			if ("".equals(liststr)) {
				isOK = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("isOK", isOK);
		request.setAttribute("name", node.getAlias());
		request.setAttribute("str", node.getIpAddress());
		return "/tool/tomcatisok.jsp";
	}

	private String alarm() {
		// resin_jvm();
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
			com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(tmp));
			ip = resin.getIpAddress();
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

				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "resin");

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Hashtable hash1 = getCategory(ip, "ResinPing", "ConnectUtilization", starttime1, totime1, "");
				Hashtable hash = getCategory(ip, "resin_jvm", "jvm_utilization", starttime1, totime1, "");
				if (hash1 != null)
					request.setAttribute("pingcon", hash1);
				if (hash != null)
					request.setAttribute("avgjvm", hash);
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
		return "/application/resin/resin_alarm.jsp";

	}

	public double resinPing(int id) {
		String strid = String.valueOf(id);
		Resin vo = new Resin();
		ResinDao dao = new ResinDao();
		double avgpingcon = 0;
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg = "0";
		try {
			vo = (Resin) dao.findByID(strid);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = getCategory(vo.getIpAddress(), "ResinPing", "ConnectUtilization", starttime1, totime1, "");
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
		} finally {
			dao.close();
		}
		return avgpingcon;
	}

	/**
	 * 
	 * @description resin 系统信息
	 * @author wangxiangyong
	 * @date Apr 25, 2013 10:20:53 AM
	 * @return String
	 * @return
	 */
	public String system() {

		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("flag", flag);

		com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
		String ip = resin.getIpAddress();
		// Hashtable imgurlhash = new Hashtable();
		try {
			String newip = doip(ip);
			String[] time = { "", "" };
			getTime(request, time);
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			this.getBaseInfo(id, starttime1, totime1);// 基本信息
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("id", id);
		return "/application/resin/system.jsp";
	}

	/**
	 * 
	 * @description 事件报表
	 * @author wangxiangyong
	 * @date Apr 28, 2013 11:39:12 AM
	 * @return String
	 * @return
	 */
	private String eventReport() {

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";

		tmp = request.getParameter("id");
		Node config = PollingEngine.getInstance().getResinByID(Integer.parseInt(tmp));
		ip = config.getIpAddress();
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

			list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "resin");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dao.close();
		}

		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/resin/resin_eventReport.jsp";
	}

	/**
	 * 
	 * @description 下载事件报表
	 * @author wangxiangyong
	 * @date Apr 28, 2013 11:39:46 AM
	 * @return String
	 * @return
	 */
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
		Node resinNode = PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "' ");
		s.append(" and nodeid=" + resinNode.getId());

		List infolist = eventdao.findByCriteria(s.toString());
		reporthash.put("eventlist", infolist);

		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/resinEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventDoc(fileName, starttime1, totime1, "resin");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/resinEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_TomcatEventExc(file, id, starttime1, totime1, "resin");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/resinEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventPdf(fileName, starttime1, totime1, "resin");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	/**
	 * 
	 * @description 基本信息
	 * @author wangxiangyong
	 * @date Apr 25, 2013 5:23:34 PM
	 * @return void
	 * @param resin
	 */
	private void getBaseInfo(String id, String starttime, String totime) {
		Hashtable data_ht = new Hashtable();
		double resinping = 0;
		String lasttime;
		String nexttime;

		Hashtable pollingtime_ht = new Hashtable();
		String runmodel = PollingEngine.getCollectwebflag();
		com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
		ResinDao dao = null;
		Resin vo = null;
		try {
			dao = new ResinDao();
			vo = (Resin) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String ip = vo.getIpAddress();
		String monStr = "未监视";
		if (resin.isManaged())
			monStr = "已监视";
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable resinvalues = ShareData.getResindata();
			if (resinvalues != null && resinvalues.containsKey(resin.getIpAddress())) {
				data_ht = (Hashtable) resinvalues.get(resin.getIpAddress());
			}
		} else {
			// 采集与访问分离模式
		}
		ResinManager tm = new ResinManager();
		resinping = (double) tm.resinPing(resin.getId());
		try {
			pollingtime_ht = tm.getCollecttime(resin.getIpAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (pollingtime_ht != null) {
			lasttime = (String) pollingtime_ht.get("lasttime");
			nexttime = (String) pollingtime_ht.get("nexttime");
		} else {
			lasttime = null;
			nexttime = null;
		}
		String avgpingStr = "0";
		double avgpingcon = 0d;
		String avgmem = "";
		double avgmemcon = 0d;
		try {
			Hashtable hash1 = getCategory(ip, "ResinPing", "ConnectUtilization", starttime, totime, "");

			if (hash1 != null) {
				avgpingStr = (String) hash1.get("avgpingcon");
				request.setAttribute("pingcon", hash1);
				request.setAttribute("avgpingcon", avgpingStr);
				avgpingcon = Double.parseDouble(avgpingStr.replace("%", ""));

			}
			CreateMetersPic cmp = new CreateMetersPic();
			if (vo.getVersion().equals("3")) {
				Hashtable hash = getCategory(ip, "resin_mem", "mem_utilization", starttime, totime, "");
				if (hash != null) {
					avgmem = (String) hash.get("avg_resin_mem");
					if (avgmem == null || avgmem.equals("null"))
						avgmem = "0";
					request.setAttribute("avgmem", hash);
					request.setAttribute("avgmemcon", avgmem);

					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(resin.getId() + "", avgmemcon, path, "内存利用率", "resin_mem");
				}
			} else if (vo.getVersion().equals("4")) {
				Hashtable heapHash = getCategory(ip, "resin_heap", "heap_utilization", starttime, totime, "");
				if (heapHash != null) {
					avgmem = (String) heapHash.get("avg_resin_mem");
					request.setAttribute("avgheap", heapHash);
					request.setAttribute("avgheapcon", avgmem);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(resin.getId() + "", avgmemcon, path, "Heap利用率", "resin_heap");
				}
				Hashtable swapHash = getCategory(ip, "resin_swap", "swap_utilization", starttime, totime, "");
				if (swapHash != null) {
					avgmem = (String) swapHash.get("avg_resin_mem");
					request.setAttribute("avgswap", swapHash);
					request.setAttribute("avgswapcon", avgmem);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(resin.getId() + "", avgmemcon, path, "交换内存率", "resin_swap");
				}
				Hashtable physicalHash = getCategory(ip, "resin_physical", "physical_utilization", starttime, totime, "");
				if (physicalHash != null) {
					avgmem = (String) physicalHash.get("avg_resin_mem");
					request.setAttribute("avgphy", physicalHash);
					request.setAttribute("avgphycon", avgmem);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(resin.getId() + "", avgmemcon, path, "物理内存率", "resin_mem");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		StringBuffer dataStr = new StringBuffer();
		dataStr.append("连通;").append(Math.round(avgpingcon)).append(";false;7CFC00\\n");
		dataStr.append("未连通;").append(100 - Math.round(avgpingcon)).append(";false;FF0000\\n");
		String avgdata = dataStr.toString();
		String version = "";
		String hostname = "";
		String state = "";
		String uptime = "";
		String serverId = "";
		String configPath = "";
		String homePath = "";
		String os = "";
		String username = "";
		if (data_ht.containsKey("resin_server") && data_ht.get("resin_server") != null) {
			Hashtable serverHash = (Hashtable) data_ht.get("resin_server");
			if (serverHash != null) {

				if (vo.getVersion().equals("3")) {
					serverId = (String) serverHash.get("Server id:");
					version = (String) serverHash.get("Version:");
					hostname = (String) serverHash.get("Local host:");
					state = (String) serverHash.get("State:");
					uptime = (String) serverHash.get("Uptime:");
					configPath = (String) serverHash.get("Config file:");
					homePath = (String) serverHash.get("Resin home:");
				} else if (vo.getVersion().equals("4")) {
					serverId = (String) serverHash.get("Server id:");
					username = (String) serverHash.get("User:");
					version = (String) serverHash.get("Resin:");
					hostname = (String) serverHash.get("Machine:");
					state = (String) serverHash.get("State:");
					os = (String) serverHash.get("OS:");
					uptime = (String) serverHash.get("Uptime:");

				}
				vo.setOs(os);
			}

		}

		request.setAttribute("resin", vo);
		request.setAttribute("version", version);
		// request.setAttribute("lastAlarm", lastAlarm);

		request.setAttribute("monStr", monStr);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("state", state);
		request.setAttribute("uptime", uptime);
		request.setAttribute("avgdata", avgdata);
		request.setAttribute("hostname", hostname);

		request.setAttribute("serverId", serverId);
		request.setAttribute("configPath", configPath);
		request.setAttribute("homePath", homePath);
		request.setAttribute("username", username);

	}

	public String detail() {

		String id = request.getParameter("id");
		String runmodel = PollingEngine.getCollectwebflag();
		Resin node = new Resin();
		ResinDao _dao = new ResinDao();
		try {
			node = (Resin) _dao.findByID(id);
		} catch (Exception e) {
		} finally {
			_dao.close();
		}
		String flag = request.getParameter("flag");
		request.setAttribute("flag", flag);

		com.afunms.polling.node.Resin resin = (com.afunms.polling.node.Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(id));
		String ip = node.getIpAddress();

		// resin性能数据
		Hashtable data_ht = null;
		Hashtable serverHash = null;
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable resinvalues = ShareData.getResindata();
			if (resinvalues != null && resinvalues.containsKey(node.getIpAddress())) {
				data_ht = (Hashtable) resinvalues.get(node.getIpAddress());
			}
			if (data_ht != null && data_ht.containsKey("resin_server")) {
				serverHash = (Hashtable) data_ht.get("resin_server");
			} else {
				serverHash = new Hashtable();
			}
		} else {
			// 采集与访问分离模式
		}
		// /////////////////////////////////////
		String avgpingStr = "0";
		double avgpingcon = 0d;
		String avgmem = "";
		double avgmemcon = 0d;
		try {
			String newip = doip(ip);
			String[] time = { "", "" };
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}
			String starttime = time1 + " 00:00:00";
			String endtime = time1 + " 23:59:59";
			Hashtable hash1 = getCategory(ip, "ResinPing", "ConnectUtilization", starttime, endtime, "");
			String curcon = "0";
			if (hash1 != null) {
				avgpingStr = (String) hash1.get("avgpingcon");
				String pingmax = (String) hash1.get("pingmax");
				curcon = (String) hash1.get("curcon");
				request.setAttribute("pingcon", hash1);
				request.setAttribute("avgpingcon", avgpingStr);
				request.setAttribute("maxpingcon", pingmax);
				request.setAttribute("curpingcon", curcon);

				avgpingcon = Double.parseDouble(avgpingStr.replace("%", ""));

			}
			CreateMetersPic cmp = new CreateMetersPic();
			String max = "0";
			if (node.getVersion().equals("3")) {
				Hashtable hash = getCategory(ip, "resin_mem", "mem_utilization", starttime, endtime, "");
				if (hash != null) {
					avgmem = (String) hash.get("avg_resin_mem");
					if (avgmem == null || avgmem.equals("null"))
						avgmem = "0";
					curcon = (String) hash.get("curcon");
					if (curcon == null || curcon.equals("null"))
						curcon = "0";
					max = (String) hash.get("max");
					if (max == null || max.equals("null"))
						max = "0";
					request.setAttribute("avgmem", hash);
					request.setAttribute("avgmemcon", avgmem);
					request.setAttribute("curmemcon", curcon);
					request.setAttribute("maxmemcon", max);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(node.getId() + "", avgmemcon, path, "内存利用率", "resin_mem");
				}
			} else if (node.getVersion().equals("4")) {
				Hashtable heapHash = getCategory(ip, "resin_heap", "heap_utilization", starttime, endtime, "");
				if (heapHash != null) {
					avgmem = (String) heapHash.get("avg_resin_mem");
					if (avgmem == null || avgmem.equals("null"))
						avgmem = "0";
					curcon = (String) heapHash.get("curcon");
					if (curcon == null || curcon.equals("null"))
						curcon = "0";
					max = (String) heapHash.get("max");
					if (max == null || max.equals("null"))
						max = "0";
					request.setAttribute("avgheap", heapHash);
					request.setAttribute("avgheapcon", avgmem);
					request.setAttribute("maxheapcon", max);
					request.setAttribute("curheapcon", curcon);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(node.getId() + "", avgmemcon, path, "Heap利用率", "resin_heap");
				}
				Hashtable swapHash = getCategory(ip, "resin_swap", "swap_utilization", starttime, endtime, "");
				if (swapHash != null) {
					avgmem = (String) swapHash.get("avg_resin_mem");
					if (avgmem == null || avgmem.equals("null"))
						avgmem = "0";
					curcon = (String) swapHash.get("curcon");
					if (curcon == null || curcon.equals("null"))
						curcon = "0";
					max = (String) swapHash.get("max");
					if (max == null || max.equals("null"))
						max = "0";
					request.setAttribute("avgswap", swapHash);
					request.setAttribute("avgswapcon", avgmem);
					request.setAttribute("maxswapcon", max);
					request.setAttribute("curswapcon", curcon);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(node.getId() + "", avgmemcon, path, "交换内存率", "resin_swap");
				}

				Hashtable physicalHash = getCategory(ip, "resin_physical", "physical_utilization", starttime, endtime, "");
				if (physicalHash != null) {
					avgmem = (String) physicalHash.get("avg_resin_mem");
					if (avgmem == null || avgmem.equals("null"))
						avgmem = "0";
					curcon = (String) physicalHash.get("curcon");
					if (curcon == null || curcon.equals("null"))
						curcon = "0";
					max = (String) physicalHash.get("max");
					if (max == null || max.equals("null"))
						max = "0";
					request.setAttribute("avgphy", physicalHash);
					request.setAttribute("avgphycon", avgmem);
					request.setAttribute("maxsphycon", max);
					request.setAttribute("curphycon", curcon);
					avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
					String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
					cmp.createPic(node.getId() + "", avgmemcon, path, "物理内存率", "resin_mem");
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// //////////////////////////////////////////////////////////////
		request.setAttribute("resin", resin);
		request.setAttribute("serverHash", serverHash);
		request.setAttribute("id", id);
		return "/application/resin/resin_detail.jsp";
	}

	public String event() {
		ResinDao dao = new ResinDao();

		return null;
	}
	/**
	 * @description 取消监视
	 * @author wangxiangyong add
	 * @date Mar 31, 2013 10:51:16 AM
	 * @return String
	 * @return
	 */
	private String cancelalert() {
		Resin vo = new Resin();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		try {
			ResinDao configdao = new ResinDao();
			try {
				vo = (Resin) configdao.findByID(getParaValue("id"));
				vo.setMonflag(0);
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (configdao != null)
					configdao.close();
			}
			// 在轮询线程中增加被监视节点，放入内存中
			ResinLoader loader = new ResinLoader();
			loader.loading();

			// TomcatLoader loader = null;
			// try {
			// loader = new TomcatLoader();
			// loader.loadOne(vo);
			//				
			// configdao = new TomcatDao();
			// list = configdao.loadAll();
			// if (list == null)
			// list = new ArrayList();
			// ShareData.setTomcatlist(list);
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// if (loader != null)
			// loader.close();
			// if (configdao != null)
			// configdao.close();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list();
	}
	/**
	 * 
	 * @description 添加监视
	 * @author wangxiangyong add
	 * @date Mar 31, 2013 10:51:16 AM
	 * @return String
	 * @return
	 */
	private String addalert() {
		Resin vo = new Resin();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		try {
			ResinDao configdao = new ResinDao();
			try {
				vo = (Resin) configdao.findByID(getParaValue("id"));
				vo.setMonflag(1);
				
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (configdao != null)
					configdao.close();
			}
			// 在轮询线程中增加被监视节点，放入内存中
			ResinLoader loader = new ResinLoader();
			loader.loading();
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list();
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
		if (action.equals("syncconfig"))
			return syncconfig();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("alarm")) {
			return alarm();
		}
		if (action.equals("app"))
			return app();
		if (action.equals("system"))
			return system();
		if (action.equals("detail"))
			return detail();
		if (action.equals("tcpport"))
			return tcpport();
		if (action.equals("connPool"))
			return connPool();
		if (action.equals("showPingReport"))
			return showPingReport();
		if (action.equals("allReport"))
			return allReport();
		if (action.equals("downloadAllReport"))
			return downloadAllReport();
		if (action.equals("eventReport"))
			return eventReport();
		if (action.equals("downloadEventReport"))
			return downloadEventReport();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("addalert"))
			return addalert();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}