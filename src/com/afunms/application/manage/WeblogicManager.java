/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
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
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.WeblogicLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Weblogic;
import com.afunms.polling.task.WeblogicDataCollector;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.temp.dao.WeblogicDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.DocumentException;

public class WeblogicManager extends BaseManager implements ManagerInterface {

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String list() {
		List ips = new ArrayList();
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
		WeblogicConfigDao configdao = new WeblogicConfigDao();
		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else
				list = configdao.getWeblogicByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}

		request.setAttribute("list", list);
		return "/application/weblogic/list.jsp";
	}

	public List getInfoByFlag(Integer flag) throws Exception {

		List list = new ArrayList();
		WeblogicConfigDao dao = new WeblogicConfigDao();
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
		return "/application/weblogic/add.jsp";
	}

	private String add() {
		WeblogicConfig vo = new WeblogicConfig();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setAlias(getParaValue("name"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setCommunity(getParaValue("community"));
		vo.setPortnum(getParaIntValue("portnum"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		// vo.setNetid(rs.getString("netid"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20

		vo.setNetid(getParaValue("bid"));

		WeblogicConfigDao configdao = new WeblogicConfigDao();
		try {
			configdao.save(vo);
			// nielin add for time-sharing at 2009-01-04
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();

			try {
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("12"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			try {
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("12"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "middleware", "weblogic", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "middleware", "weblogic");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 保存应用
			HostApplyManager.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}

		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
		// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的WEBLOGIC所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(vo.getId() + "", 1, "middleware", "weblogic");
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

		WeblogicDataCollector weblogicdata = new WeblogicDataCollector();
		try {
			weblogicdata.collect_data(vo.getId() + "", gatherHash);
		} catch (Exception e) {

		}

		String runmodel = PollingEngine.getCollectwebflag();
		Hashtable hash = null;

		WeblogicNormal normalvalue = null;
		List normaldatalist = new ArrayList();
		List serverdatalist = new ArrayList();
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			hash = (Hashtable) ShareData.getWeblogicdata().get(vo.getIpAddress());
		} else {
			List labelList = new ArrayList();
			labelList.add("normalValue");
			labelList.add("serverValue");
			WeblogicDao weblogicDao = new WeblogicDao();
			hash = weblogicDao.getWeblogicData(labelList, vo.getId() + "");
		}
		String serverName = "";
		String serverAddr = "";
		String serverPort = "";

		String domainName = "";
		String domainPort = "";
		String domainVersion = "";
		if (hash != null) {
			normaldatalist = (List) hash.get("normalValue");
			serverdatalist = (List) hash.get("serverValue");
			if (normaldatalist != null && normaldatalist.size() > 0) {
				for (int i = 0; i < normaldatalist.size(); i++) {
					normalvalue = (WeblogicNormal) normaldatalist.get(i);
					if ("weblogic".equals(normalvalue.getDomainName()))
						continue;
				}
			} else {
				normalvalue = new WeblogicNormal();
			}
			if (serverdatalist != null && serverdatalist.size() > 0) {
				WeblogicServer vs = (WeblogicServer) serverdatalist.get(0);
				serverName = vs.getServerRuntimeName();
				serverAddr = vs.getServerRuntimeListenAddress();
				serverPort = vs.getServerRuntimeListenPort();
				// serverRun = vo.getServerRuntimeState();
			}

		}

		vo.setServerName(serverName);
		vo.setServerAddr(serverAddr);
		vo.setServerPort(serverPort);
		if (normalvalue != null) {
			vo.setDomainName(normalvalue.getDomainName());
			vo.setDomainPort(normalvalue.getDomainAdministrationPort());
			vo.setDomainVersion(normalvalue.getDomainConfigurationVersion());
		}
		configdao = new WeblogicConfigDao();
		try {
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		// 在轮询线程中增加被监视节点
		WeblogicLoader loader = new WeblogicLoader();
		loader.loading();

		return "/weblogic.do?action=list";
	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		WeblogicConfig vo = new WeblogicConfig();
		List list = new ArrayList();

		if (ids != null && ids.length > 0) {
			WeblogicConfigDao configdao = new WeblogicConfigDao();
			try {
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil(); // snow
				// add
				// at
				// 2010-5-20
				// 删除数据库采集时间
				for (int i = 0; i < ids.length; i++) {
					Node node = PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(ids[i]));
					// 删除应用
					HostApplyDao hostApplyDao = null;
					try {
						hostApplyDao = new HostApplyDao();
						hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'weblogic' and nodeid = '" + ids[i] + "'");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (hostApplyDao != null) {
							hostApplyDao.close();
						}
					}
					// PollingEngine.getInstance().deleteWeblogicByID(Integer.parseInt(ids[i]));
					try {
						timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("12"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("12")); // snow
						// add
						// at
						// 2010-5-20
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "middleware", "weblogic");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(ids[i], "middleware", "weblogic");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						indidao.close();
					}

					// 更新业务视图
					String id = ids[i];
					NodeDependDao nodedependao = new NodeDependDao();
					List weblogiclist = nodedependao.findByNode("web" + id);
					if (weblogiclist != null && weblogiclist.size() > 0) {
						for (int j = 0; j < weblogiclist.size(); j++) {
							NodeDepend weblogicvo = (NodeDepend) weblogiclist.get(j);
							if (weblogicvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("web" + id, weblogicvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("web" + id, weblogicvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("web" + id, weblogicvo.getXmlfile());
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
								ManageXml manageXml = (ManageXml) subMapDao.findByXml(weblogicvo.getXmlfile());
								if (manageXml != null) {
									NodeDependDao nodeDepenDao = new NodeDependDao();
									try {
										List lists = nodeDepenDao.findByXml(weblogicvo.getXmlfile());
										ChartXml chartxml;
										chartxml = new ChartXml("NetworkMonitor", "/" + weblogicvo.getXmlfile().replace("jsp", "xml"));
										chartxml.addBussinessXML(manageXml.getTopoName(), lists);
										ChartXml chartxmlList;
										chartxmlList = new ChartXml("NetworkMonitor", "/" + weblogicvo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
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
				configdao.delete(ids);

				// 删除设备在临时表里中存储的数据
				String[] nmsTempDataTables = { "nms_weblogic_queue", "nms_weblogic_jdbc", "nms_weblogic_webapps", "nms_weblogic_heap", "nms_weblogic_server", "nms_weblogic_servlet", "nms_weblogic_normal" };
				CreateTableManager createTableManager = new CreateTableManager();
				createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
				// 重新加载weblogic节点
				WeblogicLoader loader = new WeblogicLoader();
				loader.loading();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
		}
		return "/weblogic.do?action=list";
	}

	/**
	 * @author nielin add for time-sharing at 2010-01-05
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/weblogic/edit.jsp";
		List timeShareConfigList = new ArrayList();
		WeblogicConfigDao dao = new WeblogicConfigDao();
		try {
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("12"));

			/* 获得设备的采集时间 snow add at 2010-05-20 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("12"));
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
		WeblogicConfig vo = new WeblogicConfig();

		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("name"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setCommunity(getParaValue("community"));
		vo.setPortnum(getParaIntValue("portnum"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));// snow add 2010-5-20

		vo.setNetid(getParaValue("bid"));
		try {
			WeblogicConfigDao configdao = new WeblogicConfigDao();
			try {
				configdao.update(vo);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
				// add
				// for
				// time-sharing
				// at
				// 2010-01-05
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("12"));

				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("12"));
				/* snow add end */

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			// 重新加载weblogic节点
			WeblogicLoader loader = new WeblogicLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// configdao.close();
		}
		return "/weblogic.do?action=list";
	}

	private String search() {
		// DBVo vo = new DBVo();
		// DBDao dao = new DBDao();
		// SybspaceconfigDao configdao = new SybspaceconfigDao();
		// List list = new ArrayList();
		// List conflist = new ArrayList();
		// List ips = new ArrayList();
		// String ipaddress ="";
		//
		// try{
		// ipaddress = getParaValue("ipaddress");
		// User operator =
		// (User)session.getAttribute(SessionConstant.CURRENT_USER);
		// String bids = operator.getBusinessids();
		// String bid[] = bids.split(",");
		// Vector rbids = new Vector();
		// if(bid != null && bid.length>0){
		// for(int i=0;i<bid.length;i++){
		// if(bid[i] != null && bid[i].trim().length()>0)
		// rbids.add(bid[i].trim());
		// }
		// }
		//
		// List oraList = new ArrayList();
		// DBTypeDao typedao = new DBTypeDao();
		// DBTypeVo typevo = (DBTypeVo)typedao.findByDbtype("sybase");
		// try{
		// oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// if(oraList != null && oraList.size()>0){
		// for(int i=0;i<oraList.size();i++){
		// DBVo dbmonitorlist = (DBVo)oraList.get(i);
		// ips.add(dbmonitorlist.getIpAddress());
		// }
		// }
		//			
		// configdao = new SybspaceconfigDao();
		// if (ipaddress != null && ipaddress.trim().length()>0){
		// configdao = new SybspaceconfigDao();
		// list =configdao.getByIp(ipaddress);
		// if (list == null || list.size() == 0){
		// list = configdao.loadAll();
		// }
		// }else{
		// configdao = new SybspaceconfigDao();
		// list = configdao.loadAll();
		// }
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// request.setAttribute("iplist",ips);
		// request.setAttribute("ipaddress",ipaddress);
		// configdao = new SybspaceconfigDao();
		// list = configdao.getByIp(ipaddress);
		// request.setAttribute("list",list);
		return "/application/db/sybaseconfigsearchlist.jsp";
	}

	private String addalert() {
		WeblogicConfig vo = new WeblogicConfig();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		WeblogicConfigDao configdao = null;
		try {
			configdao = new WeblogicConfigDao();

			vo = (WeblogicConfig) configdao.findByID(getParaValue("id"));
			vo.setMon_flag(1);
			configdao.update(vo);
			// User operator =
			// (User)session.getAttribute(SessionConstant.CURRENT_USER);
			// String bids = operator.getBusinessids();
			// String bid[] = bids.split(",");
			// Vector rbids = new Vector();
			// if(bid != null && bid.length>0){
			// for(int i=0;i<bid.length;i++){
			// if(bid[i] != null && bid[i].trim().length()>0)
			// rbids.add(bid[i].trim());
			// }
			// }
			// configdao = new WeblogicConfigDao();
			// try{
			// //list = configdao.getWeblogicByBID(rbids);
			// com.afunms.polling.node.Weblogic weblogic =
			// (com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByID(vo.getId());
			// if(weblogic != null)
			// weblogic.setMon_flag(1);
			// }catch(Exception e){
			// e.printStackTrace();
			// }finally{
			// configdao.close();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		WeblogicLoader loader = new WeblogicLoader();
		loader.loading();
		// return "/application/weblogic/list.jsp";
		return list();
	}

	private String cancelalert() {
		WeblogicConfig vo = new WeblogicConfig();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		WeblogicConfigDao configdao = null;
		try {
			configdao = new WeblogicConfigDao();
			vo = (WeblogicConfig) configdao.findByID(getParaValue("id"));
			vo.setMon_flag(0);

			configdao.update(vo);
			WeblogicLoader loader = new WeblogicLoader();
			loader.loading();
			// User operator =
			// (User)session.getAttribute(SessionConstant.CURRENT_USER);
			// String bids = operator.getBusinessids();
			// String bid[] = bids.split(",");
			// Vector rbids = new Vector();
			// if(bid != null && bid.length>0){
			// for(int i=0;i<bid.length;i++){
			// if(bid[i] != null && bid[i].trim().length()>0)
			// rbids.add(bid[i].trim());
			// }
			// }
			// configdao = new WeblogicConfigDao();
			// try{
			// com.afunms.polling.node.Weblogic weblogic =
			// (com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByID(vo.getId());
			// weblogic.setMon_flag(0);
			// }catch(Exception e){
			// e.printStackTrace();
			// }finally{
			// configdao.close();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		// request.setAttribute("list",list);
		// return "/application/weblogic/list.jsp";
		return list();
	}

	private String detail() {
		// java.text.SimpleDateFormat timeFormatter = new
		// java.text.SimpleDateFormat("yyyy-MM-dd");
		// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// WebConfigDao configdao = new WebConfigDao();
		// Urlmonitor_realtimeDao realtimedao = new Urlmonitor_realtimeDao();
		// Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
		// List urllist = new ArrayList(); //用做条件选择列表
		// WebConfig initconf = new WebConfig(); //当前的对象
		// String lasttime="";
		// String nexttime="";
		// String conn_name="";
		// String valid_name = "";
		// String fresh_name = "";
		// String wave_name = "";
		// String delay_name = "";
		// String connrate="0";
		// String validrate="0";
		// String freshrate="0";
		// Calendar now = Calendar.getInstance();
		// now.setTime(new Date());
		// Date nowdate = new Date();
		// nowdate.getHours();
		// String from_date1 = getParaValue("from_date1");
		// if (from_date1 == null){
		// from_date1 = timeFormatter.format(new Date());
		// request.setAttribute("from_date1", from_date1);
		// }
		// String to_date1 = getParaValue("to_date1");
		// if (to_date1 == null){
		// to_date1 = timeFormatter.format(new Date());
		// request.setAttribute("to_date1", to_date1);
		// }
		// String from_hour = getParaValue("from_hour");
		// if (from_hour == null){
		// from_hour = "00";
		// request.setAttribute("from_hour", from_hour);
		// }
		// String to_hour = getParaValue("to_hour");
		// if(to_hour == null){
		// to_hour = nowdate.getHours()+"";
		// request.setAttribute("to_hour", to_hour);
		// }
		// String starttime = from_date1+" "+from_hour+":00:00";
		// String totime = to_date1+" "+to_hour+":59:59";
		// int flag = 0;
		// try {
		// User operator =
		// (User)session.getAttribute(SessionConstant.CURRENT_USER);
		// String bids = operator.getBusinessids();
		// String bid[] = bids.split(",");
		// Vector rbids = new Vector();
		// if(bid != null && bid.length>0){
		// for(int i=0;i<bid.length;i++){
		// if(bid[i] != null && bid[i].trim().length()>0)
		// rbids.add(bid[i].trim());
		// }
		// }
		// configdao = new WebConfigDao();
		// try{
		// urllist = configdao.getWebByBID(rbids);
		// }catch(Exception e){
		//				
		// }
		//			
		// Integer queryid = getParaIntValue("id");//.getUrl_id();
		//
		// if(urllist.size()>0&&queryid==null){
		// Object obj = urllist.get(0);
		// }
		// if(queryid!=null){
		// //如果是链接过来则取用查询条件
		// configdao = new WebConfigDao();
		// initconf = (WebConfig)configdao.findByID(queryid+"");
		// }
		// configdao.close();
		// queryid = initconf.getId();
		// conn_name=queryid+"urlmonitor-conn";
		// valid_name =queryid+"urlmonitor-valid";
		// fresh_name =queryid+"urlmonitor-refresh";
		// wave_name = queryid+"urlmonitor-rec";
		// delay_name = queryid+"urlmonitor-delay";
		//        	
		// List urlList = realtimedao.getByUrlId(queryid);
		//             
		// Calendar last = null;
		// if(urlList != null && urlList.size()>0){
		// last = ((Urlmonitor_realtime)urlList.get(0)).getMon_time();
		// }
		// int interval = 0;
		// //TaskXmlUtil taskutil = new TaskXmlUtil("urltask");
		// try {
		// //Session session = this.beginTransaction();
		// List numList = new ArrayList();
		// TaskXml taskxml = new TaskXml();
		// numList = taskxml.ListXml();
		// for (int i = 0; i < numList.size(); i++) {
		// Task task = new Task();
		// BeanUtils.copyProperties(task, numList.get(i));
		// if (task.getTaskname().equals("urltask")){
		// interval = task.getPolltime().intValue();
		// //numThreads = task.getPolltime().intValue();
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// DateE de=new DateE();
		// if (last == null){
		// last = new GregorianCalendar();
		// flag=1;
		// }
		// lasttime = de.getDateDetail(last);
		// last.add(Calendar.MINUTE,interval);
		// nexttime = de.getDateDetail(last);
		//        	
		// int hour=1;
		// if(getParaValue("hour")!=null){
		// hour = Integer.parseInt(getParaValue("hour"));
		// }else{
		// request.setAttribute("hour", "1");
		// //urlconfForm.setHour("1");
		// }
		//        	
		// InitCoordinate initer=new InitCoordinate(new
		// GregorianCalendar(),hour,1);
		// //Minute[] minutes=initer.getMinutes();
		// TimeSeries ss1 = new TimeSeries("", Minute.class);
		// TimeSeries ss2 = new TimeSeries("", Minute.class);
		//        	
		// //ss.add()
		// TimeSeries[] s = new TimeSeries[1];
		// TimeSeries[] s_ = new TimeSeries[1];
		// //Vector wave_v = historyManager.getInfo(queryid,initer);
		// Vector wave_v = historydao.getByUrlid(queryid, starttime, totime,0);
		//        	
		// for(int i=0;i<wave_v.size();i++){
		// Hashtable ht = (Hashtable)wave_v.get(i);
		// double conn = Double.parseDouble(ht.get("conn").toString());
		// double fresh = Double.parseDouble(ht.get("refresh").toString());
		// double condelay = Double.parseDouble(ht.get("condelay").toString());
		// String time = ht.get("mon_time").toString();
		// ss1.addOrUpdate(new Minute(sdf1.parse(time)),conn);
		// ss2.addOrUpdate(new Minute(sdf1.parse(time)),condelay);
		// }
		// s[0] = ss1;
		// s_[0] = ss2;
		// ChartGraph cg = new ChartGraph();
		// cg.timewave(s,"时间","连通","",wave_name,600,120);
		// cg.timewave(s_,"时间","时延(ms)","",delay_name,600,120);
		// //p_draw_line(s,"","url"+queryid+"ConnectUtilization",740,120);
		//        	
		// //是否连通
		// String conn[] = new String[2];
		// if (flag == 0)
		// //conn =
		// historyManager.getAvailability(queryid,initer,"is_canconnected");
		// conn =
		// historydao.getAvailability(queryid,starttime,totime,"is_canconnected");
		// else{
		// //conn[0] = "0";
		// //conn[1] = "0";
		// //conn =
		// historyManager.getAvailability(queryid,initer,"is_canconnected");
		// conn =
		// historydao.getAvailability(queryid,starttime,totime,"is_canconnected");
		// }
		// String[] key1 = {"连通","未连通"};
		// drawPiechart(key1,conn,"",conn_name);
		// //drawchart(minutes,"连通",)
		// // Vector conn_v =
		// historyManager.getInfo(queryid,initer,"is_canconnected");
		// // for(int i=0;i<conn)
		// if (flag == 0)
		// connrate =
		// getF(String.valueOf(Float.parseFloat(conn[0])/(Float.parseFloat(conn[0])+Float.parseFloat(conn[1]))*100))+"%";
		// //是否有效
		// String avail[] = new String[2];
		// if (flag == 0)
		// avail = historydao.getAvailability(queryid,initer,"is_valid");
		// else{
		// avail[0] = "0";
		// avail[1] = "0";
		// }
		// String[] key2 = {"有效","无效"};
		// drawPiechart(key2,avail,"页面有效情况",valid_name);
		// if (flag == 0)
		// validrate =
		// getF(String.valueOf(Float.parseFloat(avail[0])/(Float.parseFloat(avail[0])+Float.parseFloat(avail[1]))*100))+"%";
		//
		// // 是否刷新
		// String refresh[] = new String[2];
		// if (flag == 0)
		// refresh = historydao.getAvailability(queryid,initer,"is_refresh");
		// else{
		// refresh[0] = "0";
		// refresh[1] = "0";
		// }
		//      
		// String[] key3 = {"刷新","未刷新"};
		// drawPiechart(key3,refresh,"页面刷新情况",fresh_name);
		// if (flag == 0)
		// freshrate =
		// getF(String.valueOf(Float.parseFloat(refresh[0])/(Float.parseFloat(refresh[0])+Float.parseFloat(refresh[1]))*100))+"%";
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// request.setAttribute("urllist",urllist);
		// request.setAttribute("initconf",initconf);
		// request.setAttribute("lasttime",lasttime);
		// request.setAttribute("nexttime",nexttime);
		// request.setAttribute("conn_name",conn_name);
		// request.setAttribute("valid_name",valid_name);
		// request.setAttribute("fresh_name",fresh_name);
		// request.setAttribute("wave_name",wave_name);
		// request.setAttribute("delay_name",delay_name);
		// request.setAttribute("connrate",connrate);
		// request.setAttribute("validrate",validrate);
		// request.setAttribute("freshrate",freshrate);
		//        
		// request.setAttribute("from_date1",from_date1);
		// request.setAttribute("to_date1",to_date1);
		//        
		// request.setAttribute("from_hour",from_hour);
		// request.setAttribute("to_hour",to_hour);
		return "/application/web/detail.jsp";
	}

	public String queuedetail() {
		String id = request.getParameter("id");
		// WeblogicSnmp weblogicsnmp=null;
		// WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		// WeblogicConfig weblogicconf=null;
		// try{
		// weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// weblogicconfigdao.close();
		// }
		// Hashtable hash = null;
		// weblogicsnmp = new
		// WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
		// hash=weblogicsnmp.collectData();
		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
		// weblogicconfigdao.close();
		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		return "/application/weblogic/weblogic_detail.jsp";

	}

	public String jdbcdetail() {
		String id = request.getParameter("id");
		// WeblogicSnmp weblogicsnmp=null;
		// WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		// WeblogicConfig weblogicconf=null;
		// try{
		// weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// weblogicconfigdao.close();
		// }
		// Hashtable hash = null;
		// weblogicsnmp = new
		// WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
		// hash=weblogicsnmp.collectData();
		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
		// weblogicconfigdao.close();
		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		return "/application/weblogic/weblogic_jdbcdetail.jsp";

	}

	public String webappdetail() {
		String id = request.getParameter("id");
		// WeblogicSnmp weblogicsnmp=null;
		// WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		// WeblogicConfig weblogicconf=null;
		// try{
		// weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// weblogicconfigdao.close();
		// }
		// Hashtable hash = null;
		// weblogicsnmp = new
		// WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
		// hash=weblogicsnmp.collectData();
		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
		// weblogicconfigdao.close();
		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		return "/application/weblogic/weblogic_webappdetail.jsp";

	}

	public String jvmheapdetail() {
		String id = request.getParameter("id");
		// WeblogicSnmp weblogicsnmp=null;
		// WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		// WeblogicConfig weblogicconf=null;
		// try{
		// weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// weblogicconfigdao.close();
		// }
		// Hashtable hash = null;
		// weblogicsnmp = new
		// WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
		// hash=weblogicsnmp.collectData();
		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
		// weblogicconfigdao.close();
		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		return "/application/weblogic/weblogic_jvmheapdetail.jsp";

	}

	public String serverdetail() {
		String id = request.getParameter("id");
		// WeblogicSnmp weblogicsnmp=null;
		// WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		// WeblogicConfig weblogicconf=null;
		// try{
		// weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// weblogicconfigdao.close();
		// }
		// Hashtable hash = null;
		// weblogicsnmp = new
		// WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
		// hash=weblogicsnmp.collectData();
		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
		// weblogicconfigdao.close();
		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		return "/application/weblogic/weblogic_serverdetail.jsp";

	}

	public String transdetail() {
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		return "/application/weblogic/weblogic_transdetail.jsp";
	}

	public String servletdetail() {
		String id = request.getParameter("id");
		// WeblogicSnmp weblogicsnmp=null;
		// WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		// WeblogicConfig weblogicconf=null;
		// try{
		// weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// weblogicconfigdao.close();
		// }
		// Hashtable hash = null;
		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		return "/application/weblogic/weblogic_servletdetail.jsp";

	}

	public String eventdetail() {
		String id = request.getParameter("id");
		WeblogicSnmp weblogicsnmp = null;
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		WeblogicConfig weblogicconf = null;
		List list = new ArrayList();
		String flag = getParaValue("flag");
		try {
			weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			weblogicconfigdao.close();
		}

		// Hashtable hash = null;
		// weblogicsnmp = new
		// WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
		// hash=weblogicsnmp.collectData();

		// hash =
		// (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
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
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				list = eventdao.getQuery(starttime1, totime1, status + "", level1 + "", user.getBusinessids(), weblogicconf.getId(), "weblogic");

				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// weblogicconfigdao.close();
		}

		request.setAttribute("id", id);
		// request.setAttribute("hash", hash);
		request.setAttribute("weblogicconf", weblogicconf);
		request.setAttribute("list", list);
		request.setAttribute("flag", flag);
		return "/application/weblogic/weblogic_eventdetail.jsp";

	}

	public double weblogicping(int id) {
		String strid = String.valueOf(id);
		WeblogicConfig vo = new WeblogicConfig();

		double avgpingcon = 0;
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg = "0";
		try {
			WeblogicConfigDao dao = new WeblogicConfigDao();
			try {
				vo = (WeblogicConfig) dao.findByID(strid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = getCategory(vo.getIpAddress(), "WeblogicPing", "ConnectUtilization", starttime1, totime1);
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
			// dao.close();
		}
		return avgpingcon;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
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
				if (category.equals("WeblogicPing")) {
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
					if (category.equals("WeblogicPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
				if (category.equals("WeblogicPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
				sb.append(" select max(DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s')) as collecttime from weblogicping" + allipstr + " h  ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS'))as collecttime from weblogicping" + allipstr + " h  ");
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
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return ready_add();
		if (action.equals("add"))
			return add();
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
		if (action.equals("detail"))// 队列详细信息
			return queuedetail();

		if (action.equals("jdbcdetail"))// JDBC连接池详细信息
			return jdbcdetail();

		if (action.equals("webappdetail"))// Web应用详细信息
			return webappdetail();

		if (action.equals("jvmheapdetail"))// JVM堆详细信息
			return jvmheapdetail();

		if (action.equals("serverdetail"))// weblogic 服务详细信息
			return serverdetail();

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
		if (action.equalsIgnoreCase("event")) {
			return downloadEventReport();
		}
		if (action.equalsIgnoreCase("eventReport")) {
			return event();
		}
		if (action.equalsIgnoreCase("configReport")) {
			return configReport();
		}
		if (action.equalsIgnoreCase("downloadReport")) {
			return downloadReport();
		}
		if (action.equalsIgnoreCase("perreport")) {
			return perReport();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String isOK() {

		int queryid = getParaIntValue("id");
		WeblogicConfig weblogicconf = new WeblogicConfig();
		WeblogicConfigDao dao = new WeblogicConfigDao();
		try {
			weblogicconf = (WeblogicConfig) dao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
		// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "middleware", "weblogic");
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

		// 实时数据
		boolean flag = true;

		WeblogicSnmp weblogicsnmp = null;
		Hashtable sendeddata = ShareData.getSendeddata();
		Hashtable hash = null;
		weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(), weblogicconf.getCommunity(), weblogicconf.getPortnum());
		hash = weblogicsnmp.collectData(gatherHash);
		if (hash != null) {
			if (hash.containsKey("normalValue")) {
				List normalValue = (List) hash.get("normalValue");
				if (normalValue == null || normalValue.size() == 0) {
					flag = false;
				}
			} else {
				flag = false;
			}
		} else {
			flag = false;
		}

		request.setAttribute("isOK", flag);
		request.setAttribute("name", weblogicconf.getAlias());
		request.setAttribute("str", weblogicconf.getIpAddress());
		return "/tool/weblogicisok.jsp";
	}

	private String sychronizeData() {

		int queryid = getParaIntValue("id");
		String dbpage = getParaValue("dbPage");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
		// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "middleware", "weblogic");
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
			WeblogicDataCollector weblogiccollector = new WeblogicDataCollector();
			weblogiccollector.collect_data(queryid + "", gatherHash);
		} catch (Exception exc) {

		}
		if ("detail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=detail&id=" + queryid;
		} else if ("jdbcdetail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=jdbcdetail&id=" + queryid;
		} else if ("webappdetail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=webappdetail&id=" + queryid;
		} else if ("jvmheapdetail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=jvmheapdetail&id=" + queryid;
		} else if ("serverdetail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=serverdetail&id=" + queryid;
		} else if ("servletdetail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=servletdetail&id=" + queryid;
		} else if ("eventdetail".equalsIgnoreCase(dbpage)) {
			return "/weblogic.do?action=eventdetail&id=" + queryid;
		} else
			return "/web.do?action=detail&id=" + queryid;
		// return "/application/web/detail.jsp";
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
		com.afunms.polling.node.Weblogic weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(queryid);
		try {
			// ip = getParaValue("ipaddress");
			ip = weblogic.getIpAddress();
			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			WeblogicConfigDao weblogic1 = new WeblogicConfigDao();

			Hashtable ConnectUtilizationhash = weblogic1.getPingDataById(ip, queryid, starttime, totime);
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
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "weblogicpingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", weblogic.getAlias());
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
		return "/application/weblogic/showPingReport.jsp";
	}

	private String doip(String ip) {
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}

	public String event() {
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
			com.afunms.polling.node.Weblogic weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(tmp));
			ip = weblogic.getIpAddress();
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
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao dao = new EventListDao();

				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "weblogic");

				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Hashtable hash1 = getCategory(ip, "WeblogicPing", "ConnectUtilization", starttime1, totime1);
				// Hashtable hash =
				// getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1);
				if (hash1 != null)
					request.setAttribute("pingcon", hash1);
				// if(hash!=null)
				// request.setAttribute("avgjvm", hash);
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
		return "/application/weblogic/eventReport.jsp";
	}

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
		// iis-----------------------------------------------------
		// weblogic-------------------------------------------------------------
		Node weblogicNode = PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(id));
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		s.append(" and nodeid=" + weblogicNode.getId());

		List infolist = eventdao.findByCriteria(s.toString());
		reporthash.put("eventlist", infolist);
		// if (infolist != null && infolist.size() > 0) {
		// // mainreport = mainreport+ " \r\n";
		// int pingvalue = 0;
		// for (int j = 0; j < infolist.size(); j++) {
		// EventList eventlist = (EventList) infolist.get(j);
		// if (eventlist.getContent() == null)
		// eventlist.setContent("");
		// String content = eventlist.getContent();
		// if (content.indexOf("WEBLOGIC服务停止") > 0) {
		// pingvalue = pingvalue + 1;
		// }
		// }
		// String equname = weblogicNode.getAlias();
		// String ip = weblogicNode.getIpAddress();
		// List ipeventList = new ArrayList();
		// ipeventList.add(ip);
		// ipeventList.add(equname);
		// ipeventList.add(pingvalue + "");
		// orderListweblogic.add(ipeventList);
		// }

		// System.out.println("--------session-----------------"+reporthash);
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/weblogicEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventDoc(fileName, starttime, totime, "weblogic");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/weblogicEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_TomcatEventExc(file, id, starttime, totime, "weblogic");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/weblogicEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventPdf(fileName, starttime, totime, "weblogic");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	private String configReport() {
		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");
		// WeblogicConfig weblogic = null;
		// WeblogicConfigDao webdao = new WeblogicConfigDao();
		// try {
		// weblogic = (WeblogicConfig) webdao.findByID(String.valueOf(queryid));
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// webdao.close();
		// }
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/weblogic/configReport.jsp";
	}

	private String downloadReport() {
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
		Hashtable allcpuhash = new Hashtable();
		String ip = "";
		String type = "";
		String typename = "WEBLOGIC";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		WeblogicManager weblogicManager = new WeblogicManager();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		String idd = request.getParameter("id");
		try {

			type = getParaValue("type");
			WeblogicConfigDao weblogicDao = null;
			Weblogic weblogic = null;
			int id = 0;
			try {
				weblogicDao = new WeblogicConfigDao();
				weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(idd));
			} catch (Exception e) {

			} finally {
				weblogicDao.close();
			}
			// if(node == null)
			ip = weblogic.getIpAddress();
			equipname = weblogic.getAlias() + "(" + weblogic.getIpAddress() + ")";
			equipnameDoc = weblogic.getAlias();

			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			Hashtable ConnectUtilizationhash = weblogicManager.getCategory(ip, "WeblogicPing", "ConnectUtilization", starttime, totime);
			String pingconavg = "";
			List list = (List) ConnectUtilizationhash.get("list");
			Vector vector = (Vector) list.get(list.size() - 1);
			String weblogicnow = "0.0";
			weblogicnow = (String) vector.get(0);
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去

			reporthash.put("ping", maxping);

			Hashtable hdata = (Hashtable) sharedata.get(ip);
			if (hdata == null)
				hdata = new Hashtable();
			// downum
			String downnum = (String) ConnectUtilizationhash.get("downnum");
			String grade = "优";
			if (!"0".equals(downnum)) {
				grade = "差";
			}
			// ---------------------------
			WeblogicConfigDao weblogicconfigdao = null;
			WeblogicConfig weblogicconf = null;
			int id1 = 0;
			try {
				weblogicconfigdao = new WeblogicConfigDao();
				id1 = weblogicconfigdao.getidByIp(ip);
				weblogicconfigdao = new WeblogicConfigDao();
				weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id1 + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				weblogicconfigdao.close();
			}

			// -------------------hash
			Hashtable hash = null;
			List normalValue = null;
			List queueValue = null;
			List jdbcValue = null;
			List webappValue = null;
			List heapValue = null;
			List serverValue = null;
			List jobValue = null;
			List servletValue = null;
			List logValue = null;
			List transValue = null;
			String runmodel = PollingEngine.getCollectwebflag();
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
			} else {
				List labList = new ArrayList();
				labList.add("normalValue");
				labList.add("queueValue");
				labList.add("jdbcValue");
				labList.add("webappValue");
				labList.add("heapValue");
				labList.add("serverValue");
				labList.add("jobValue");
				labList.add("servletValue");
				labList.add("logValue");
				labList.add("transValue");
				WeblogicDao weblogicdao = new WeblogicDao();
				hash = weblogicdao.getWeblogicData(labList, String.valueOf(id1));
			}
			if (hash != null) {
				normalValue = (List) hash.get("normalValue");
				queueValue = (List) hash.get("queueValue");
				jdbcValue = (List) hash.get("jdbcValue");
				webappValue = (List) hash.get("webappValue");
				heapValue = (List) hash.get("heapValue");
				serverValue = (List) hash.get("serverValue");
				jobValue = (List) hash.get("jobValue");
				servletValue = (List) hash.get("servletValue");
				logValue = (List) hash.get("logValue");
				transValue = (List) hash.get("transValue");
			}
			Hashtable weblogicnmphash = new Hashtable();
			if (normalValue != null) {
				weblogicnmphash.put("normalValue", normalValue);
			}
			if (queueValue != null) {
				weblogicnmphash.put("queueValue", queueValue);
			}
			if (jdbcValue != null) {
				weblogicnmphash.put("jdbcValue", jdbcValue);
			}
			if (webappValue != null) {
				weblogicnmphash.put("webappValue", webappValue);
			}
			if (heapValue != null) {
				weblogicnmphash.put("heapValue", heapValue);
			}
			if (serverValue != null) {
				weblogicnmphash.put("serverValue", serverValue);
			}
			if (servletValue != null) {
				weblogicnmphash.put("servletValue", servletValue);
			}
			if (logValue != null) {
				weblogicnmphash.put("logValue", logValue);
			}
			if (transValue != null) {
				weblogicnmphash.put("transValue", transValue);
			}
			// ------------------
			WeblogicNormal normalvalue = null;
			if (normalValue != null) {
				normalvalue = (WeblogicNormal) normalValue.get(0);
			}

			reporthash.put("weblogicnmphash", weblogicnmphash);
			reporthash.put("grade", grade);
			reporthash.put("weblogicnow", weblogicnow);
			reporthash.put("downnum", downnum);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("ip", ip);
			reporthash.put("weblogic", weblogic);
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			reporthash.put("weblogicconf", weblogicconf);
			reporthash.put("normalvalue", normalvalue);

			// Hashtable reporthash = (Hashtable)
			// session.getAttribute("reporthash");
			// 画图-----------------------------
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);

			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表

			String flag = request.getParameter("flag");
			if ("per".equalsIgnoreCase(flag)) {
				if ("0".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
					report1.createReport_weblogicAll("temp/weblogic_PerReportxls.xls");
					request.setAttribute("filename", report.getFileName());
				} else if ("1".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
					try {
						String file = "temp/weblogic_PerReportdoc.doc";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
						report1.createReport_weblogicDoc(fileName, "doc");// word综合报表
						request.setAttribute("filename", fileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("2".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
					try {
						String file = "temp/weblogic_PerReportPDF.pdf";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
						// report1.createReport_hostPDF(fileName);// pdf综合报表
						report1.createReport_weblogicDoc(fileName, "pdf");
						request.setAttribute("filename", fileName);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if ("all".equalsIgnoreCase(flag)) {
				// if ("0".equals(str)) {
				// // report.createReport_host("temp/hostnms_report.xls");//
				// excel综合报表
				// report.createReportxls_tomcat_all("temp/tomcat_AllReportxls.xls",id);
				// request.setAttribute("filename", report.getFileName());
				// } else if ("1".equals(str)) {
				// ExcelReport1 report1 = new ExcelReport1(new
				// IpResourceReport(),
				// reporthash);
				// try {
				// String file = "temp/tomcat_AllReportdoc.doc";//
				// 保存到项目文件夹下的指定文件夹
				// String fileName = ResourceCenter.getInstance().getSysPath()
				// + file;// 获取系统文件夹路径
				// report1.createReportDoc_tomcat_all(fileName,"doc",id);//
				// word综合报表
				// request.setAttribute("filename", fileName);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// } else if ("2".equals(str)) {
				// ExcelReport1 report1 = new ExcelReport1(new
				// IpResourceReport(),
				// reporthash);
				// try {
				// String file = "temp/tomcat_AllReportPDF.pdf";//
				// 保存到项目文件夹下的指定文件夹
				// String fileName = ResourceCenter.getInstance().getSysPath()
				// + file;// 获取系统文件夹路径
				// // report1.createReport_hostPDF(fileName);// pdf综合报表
				// report1.createReportDoc_tomcat_all(fileName,"pdf",id);
				// request.setAttribute("filename", fileName);
				// } catch (DocumentException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

			} else {

				if ("0".equals(str)) {
					// report.createReport_host("temp/hostnms_report.xls");//
					// excel综合报表
					report.createReportxls_weblogic_config("temp/weblogic_configReport.xls", idd);
					request.setAttribute("filename", report.getFileName());
				} else if ("1".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
					try {
						String file = "temp/weblogic_configReport.doc";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
						report1.createReportDoc_weblogic_config(fileName, "doc", idd);// word综合报表
						request.setAttribute("filename", fileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("2".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
					try {
						String file = "temp/weblogic_configReport.pdf";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
						// report1.createReport_hostPDF(fileName);// pdf综合报表
						report1.createReportDoc_weblogic_config(fileName, "pdf", idd);
						request.setAttribute("filename", fileName);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/download.jsp";

	}

	private String perReport() {
		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");
		// WeblogicConfig weblogic = null;
		// WeblogicConfigDao webdao = new WeblogicConfigDao();
		// try {
		// weblogic = (WeblogicConfig) webdao.findByID(String.valueOf(queryid));
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// webdao.close();
		// }
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/weblogic/perReport.jsp";
	}
}