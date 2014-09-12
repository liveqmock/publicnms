package com.afunms.application.manage;

import java.io.IOException;
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
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.Apachemonitor_historyDao;
import com.afunms.application.dao.Apachemonitor_realtimeDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.Apachemonitor_realtime;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Tomcat;
import com.afunms.application.tomcatmonitor.ServerStream;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.InitCoordinate;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.ApacheLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.ApacheDataCollector;
import com.afunms.polling.task.TaskXml;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.DocumentException;

public class ApacheManager extends BaseManager implements ManagerInterface {

	/**
	 * 查询apache信息
	 * 
	 * @return
	 */
	private String list() {
		List ips = new ArrayList();
		ApacheConfig vo = null;
		ApacheConfigDao configdao = null;
		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		if (bids == null) {
			bids = "";
		}
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0) {
					rbids.add(bid[i].trim());
				}
			}
		}
		List list = null;
		try {
			configdao = new ApacheConfigDao();
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getApacheByBID(rbids);
			}
			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		setTarget("/application/apache/list.jsp");
		return "/application/apache/list.jsp";
		// return list(configdao);
	}

	/**
	 * 添加一条apache信息
	 * 
	 * @return
	 */
	private String add() {
		ApacheConfigDao dao = null;
		ApacheConfig vo = new ApacheConfig();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setAlias(getParaValue("alias"));
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort(getParaIntValue("port"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendemail(getParaValue("sendphone"));
		vo.setNetid(getParaValue("bid"));
		try {
			dao = new ApacheConfigDao();
			dao.save(vo);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		CreateTableManager ctable = new CreateTableManager();
		DBManager conn = null;
		
		try {
			conn = new DBManager();
			String allipstr = SysUtil.doip(vo.getIpaddress());
			ctable.createTable(conn,"apaping",allipstr,"apaping");//Ping
			ctable.createTable(conn,"apapingh",allipstr,"apapingh");//Ping
			ctable.createTable(conn,"apapingd",allipstr,"apapingd");//Ping
			
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(vo.getId() + "", AlarmConstant.TYPE_MIDDLEWARE, "apache");
			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(vo.getId() + "", AlarmConstant.TYPE_MIDDLEWARE, "apache", "4", 4);

			dao = new ApacheConfigDao();
			List list = dao.loadAll();
			if (list == null)
				list = new ArrayList();
			ShareData.setApachlist(list);
			ApacheLoader apacheloader = new ApacheLoader();
			apacheloader.clearRubbish(list);
			apacheloader.loading();

		} catch (Exception e) {
			SysLogger.error("ApacheManager add() error", e);
			e.printStackTrace();
		} finally {
			dao.close();
			if(conn!=null){
				conn.close();
			}
		}

		return "/apache.do?action=list&flag=0";
		// return list();
	}

	/**
	 * 删除一条apache信息记录
	 * 
	 * @return
	 */
	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		List list = new ArrayList();
		ApacheConfigDao configdao = new ApacheConfigDao();
		if (ids != null && ids.length > 0) {
			configdao.delete(ids);
			for (int i = 0; i < ids.length; i++) {
				// 更新业务视图
				String id = ids[i];
				NodeDependDao nodedependao = new NodeDependDao();
				List weslist = nodedependao.findByNode("apa" + id);
				if (weslist != null && weslist.size() > 0) {
					for (int j = 0; j < weslist.size(); j++) {
						NodeDepend wesvo = (NodeDepend) weslist.get(j);
						if (wesvo != null) {
							LineDao lineDao = new LineDao();
							lineDao.deleteByidXml("apa" + id, wesvo.getXmlfile());
							NodeDependDao nodeDependDao = new NodeDependDao();
							if (nodeDependDao.isNodeExist("apa" + id, wesvo.getXmlfile())) {
								nodeDependDao.deleteByIdXml("apa" + id, wesvo.getXmlfile());
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
				NodeGatherIndicatorsDao gatherdao = null;
				try {
					gatherdao = new NodeGatherIndicatorsDao();
					gatherdao.deleteByNodeIdAndTypeAndSubtype(id + "", "middleware", "apache");
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					gatherdao.close();
				}
				// 删除网络设备指标采集表里的对应的数据
				AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
				try {
					indicatdao.deleteByNodeId(id + "", "middleware", "apache");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					indicatdao.close();
				}
				Node config = PollingEngine.getInstance().getApacheByID(Integer.parseInt(id));
				String allipstr = SysUtil.doip(config.getIpAddress());
				CreateTableManager ctable = new CreateTableManager();
				DBManager conn = null;
				try {
					conn = new DBManager();
					ctable.deleteTable(conn, "apaping", allipstr, "apaping");// Ping
					ctable.deleteTable(conn, "apapingh", allipstr, "apapingh");// Ping
					ctable.deleteTable(conn, "apapingd", allipstr, "apapingd");// Ping
				} catch (Exception e) {
					SysLogger.error("ApacheManger delete() error",e);
				}finally{
					if(conn!=null){
						conn.close();
					}
						
				}
				
			}
			// 删除Apache Http Server在临时表里中存储的数据
			String[] nmsTempDataTables = { "nms_apache_temp" };
			CreateTableManager createTableManager = new CreateTableManager();
			createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
			String[] nmsHistoryDataTables = { "nms_apache_history", "nms_apache_realtime" };
			createTableManager.clearTablesData(nmsHistoryDataTables, "apache_id", ids);
		}
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
			configdao = new ApacheConfigDao();
			list = configdao.getApacheByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			configdao = new ApacheConfigDao();
			List _list = configdao.loadAll();
			if (_list == null)
				_list = new ArrayList();
			ShareData.setApachlist(_list);
			ApacheLoader apacheloader = new ApacheLoader();
			apacheloader.clearRubbish(_list);
		} catch (Exception e) {

		} finally {
			configdao.close();
		}

		return list();
	}

	/**
	 * 修改apache信息
	 * 
	 * @return
	 */
	private String update() {
		ApacheConfig vo = new ApacheConfig();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setUsername(getParaValue("username"));
		vo.setPassword(getParaValue("password"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort(getParaIntValue("port"));
		vo.setFlag(getParaIntValue("flag1"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendemail(getParaValue("sendphone"));
		vo.setNetid(getParaValue("bid"));
		// vo.setNetid(getParaValue("netid"));
		// String allbid = ",";
		// String[] businessids = getParaArrayValue("checkbox");
		// if(businessids != null && businessids.length>0){
		// for(int i=0;i<businessids.length;i++){
		//        		
		// String bid = businessids[i];
		// allbid= allbid+bid+",";
		// }
		// }
		// vo.setNetid(allbid);
		ApacheConfigDao configdao = null;
		try {
			configdao = new ApacheConfigDao();
			configdao.update(vo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (configdao != null) {
				configdao.close();
			}
		}
		try {
			configdao = new ApacheConfigDao();
			List list = configdao.loadAll();
			if (list == null)
				list = new ArrayList();
			ShareData.setApachlist(list);
			ApacheLoader apacheloader = new ApacheLoader();
			apacheloader.clearRubbish(list);
		} catch (Exception e) {

		} finally {
			configdao.close();
		}
		return list();
	}

	/**
	 * 添加apache监视信息
	 * 
	 * @return
	 */
	private String addalert() {
		ApacheConfig vo = new ApacheConfig();
		ApacheConfigDao configdao = null;

		List list = new ArrayList();

		try {
			configdao = new ApacheConfigDao();
			vo = (ApacheConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(1);
//			configdao = new ApacheConfigDao();
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
		
			// configdao = new ApacheConfigDao();
			// list = configdao.getApacheByBID(rbids);
//			configdao = new ApacheConfigDao();
//			 list = configdao.loadAll();
//			if (list == null)
//				list = new ArrayList();
//			ShareData.setApachlist(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (configdao != null) {
				configdao.close();
			}
		}
		ApacheLoader loader=new ApacheLoader();
		loader.loading();
		// request.setAttribute("list",list);
		return list();
	}

	/**
	 * 取消Apache监视信息
	 * 
	 * @return
	 */
	private String cancelalert() {
		ApacheConfig vo = new ApacheConfig();
		ApacheConfigDao configdao = null;
		DBVo dbvo = new DBVo();
		DBDao dao = null;
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		try {
			configdao = new ApacheConfigDao();
			dao = new DBDao();
			vo = (ApacheConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(0);
//			configdao = new ApacheConfigDao();
			configdao.update(vo);
//			configdao = new ApacheConfigDao();
//			 list = configdao.loadAll();
//			if (list == null)
//				list = new ArrayList();
//			ShareData.setApachlist(list);
			// configdao = new ApacheConfigDao();
			// list = configdao.getApacheByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (configdao != null) {
				configdao.close();
			}
		}
		ApacheLoader loader=new ApacheLoader();
		loader.loading();
		// request.setAttribute("list",list);
		return list();
	}

	public Hashtable collectapachedata(ApacheConfig apacheConfig) {
		HttpClientJBoss apache = new HttpClientJBoss();
		Hashtable<String, String> apache_ht = new Hashtable<String, String>();
		String src = apache.getGetResponseWithHttpClient("http://" + apacheConfig.getIpaddress() + ":" + apacheConfig.getPort() + "/server-status", "GBK");
		// System.out.println(src);
		int status = 0;
		if (src.contains("Version")) {
			status = 0;
			String version = src.substring(src.indexOf("Version"));
			String apa_version = version.substring(version.indexOf("Version") + 9, version.indexOf("/dt") - 1);
			// 版本号
			String built = src.substring(src.indexOf("Built"));
			String apa_built = built.substring(built.indexOf("Built") + 7, built.indexOf("/dt") - 1);
			// 编译安装时间
			String current = src.substring(src.indexOf("Current"));
			String apa_current = current.substring(current.indexOf("Current") + 14, current.indexOf("/dt") - 1);
			// 目前系统时间
			String restart = src.substring(src.indexOf("Restart"));
			String apa_restart = restart.substring(restart.indexOf("Restart") + 14, restart.indexOf("/dt") - 1);
			// 重新启动时间
			String parent = src.substring(src.indexOf("Parent"));
			String apa_parent = parent.substring(parent.indexOf("Parent") + 26, parent.indexOf("/dt") - 1);
			// 父程序的世代编号
			String uptime = src.substring(src.indexOf("uptime"));
			String apa_uptime = uptime.substring(uptime.indexOf("uptime") + 9, uptime.indexOf("/dt") - 1);
			// 服务器运行时间
			String accesses = src.substring(src.indexOf("accesses"));
			String apa_accesses = accesses.substring(accesses.indexOf("accesses") + 10, accesses.indexOf("-"));
			// 接受的联机数量
			String traffic = src.substring(src.indexOf("Traffic"));
			String apa_traffic = traffic.substring(traffic.indexOf("Traffic") + 9, traffic.indexOf("/dt") - 1);
			// 传输的数据量
			String swss = traffic.substring(traffic.indexOf("<dt>"));
			String apa_swss1 = swss.substring(swss.indexOf("<dt>") + 4, swss.indexOf("</dt>"));
			String swss1 = swss.substring(swss.indexOf("</dt>") + 5);
			String apa_swss2 = swss1.substring(swss1.indexOf("<dt>") + 4, swss1.indexOf("</dt>"));
			// apaswss1和apaswss2为Apache process目前的状态

			String pre = src.substring(src.indexOf("pre") + 4, src.indexOf("/pre") - 1);

			String vhost = src.substring(src.indexOf("VHost"));
			String vhost1 = vhost.substring(vhost.indexOf("</tr>") + 6);
			String _td = vhost1.substring(vhost1.indexOf("<tr>"), vhost1.indexOf("</table>"));
			String td1 = _td.replaceAll("<tr", "<tr bgcolor=\"#FFFFFF\"   onmouseout=\"this.style.background='#FFFFFF'\"  onmouseover=\"this.style.background='#AACCFF'\" height=\"28\"");
			String td = td1.replaceAll("<td", "<td align=center class=\"application-detail-data-body-list\"");
			/**
			 * * 注释： _：等待连结中 S：启动中 R：正在读取要求 W：正在送出回应 K：处于保持联机的状态 D：正在查找DNS
			 * C：正在关闭连结 L：正在写入记录文件 G：进入正常结束程序中 I：处理闲置 .：尚无此程序
			 */
			apa_built = apa_built.replaceAll("\n", "");// 过滤掉换行符
			pre = pre.replaceAll("\\n", "");
			if (pre.indexOf("_") != -1) {
				pre = pre.replaceAll("_++", "等待连结中-->");
			}
			if (pre.indexOf(".") != -1) {
				pre = pre.replaceAll("\\.++", "尚无此程序-->");
			}
			if (pre.indexOf("S") != -1) {
				pre = pre.replaceAll("S", "启动中-->");
			}
			if (pre.indexOf("R") != -1) {
				pre = pre.replaceAll("R", "正在读取要求-->");
			}
			if (pre.indexOf("W") != -1) {
				pre = pre.replaceAll("W", "正在送出回应-->");
			}
			if (pre.indexOf("K") != -1) {
				pre = pre.replaceAll("K", "处于保持联机的状态-->");
			}
			if (pre.indexOf("D") != -1) {
				pre = pre.replaceAll("D", "正在查找DNS-->");
			}
			if (pre.indexOf("C") != -1) {
				pre = pre.replaceAll("C", "正在关闭连结-->");
			}
			if (pre.indexOf("L") != -1) {
				pre = pre.replaceAll("L", "正在写入记录文件-->");
			}
			if (pre.indexOf("G") != -1) {
				pre = pre.replaceAll("G", "进入正常结束程序中-->");
			}
			if (pre.indexOf("I") != -1) {
				pre = pre.replaceAll("I", "处理闲置-->");
			}
			String req_sec = apa_swss1.substring(apa_swss1.indexOf(">") + 1, apa_swss1.indexOf("-"));
			// request/sec的数值
			String b_sec = apa_swss1.substring(apa_swss1.indexOf("-") + 1, apa_swss1.indexOf("d") + 1);
			// B/second的数值
			String b_sec1 = apa_swss1.substring(apa_swss1.indexOf("second"));
			String b_req = b_sec1.substring(b_sec1.indexOf("-") + 1, b_sec1.indexOf("t") + 1);
			// B/request的数值
			String process = apa_swss2.substring(apa_swss2.indexOf(">") + 1, apa_swss2.indexOf("req"));
			// process
			String workers = apa_swss2.substring(apa_swss2.indexOf(",") + 2, apa_swss2.indexOf("idle"));
			// workers

			apache_ht.put("version", apa_version);
			apache_ht.put("built", apa_built);
			apache_ht.put("current", apa_current);
			apache_ht.put("restart", apa_restart);
			apache_ht.put("parent", apa_parent);
			apache_ht.put("uptime", apa_uptime);
			apache_ht.put("accesses", apa_accesses);
			apache_ht.put("traffic", apa_traffic);
			apache_ht.put("swss", apa_swss1);
			apache_ht.put("swss1", apa_swss2);
			apache_ht.put("pre", pre);
			apache_ht.put("td", td);
			apache_ht.put("req_sec", req_sec);
			apache_ht.put("b_sec", b_sec);
			apache_ht.put("b_req", b_req);
			apache_ht.put("process", process);
			apache_ht.put("workers", workers);
			apache_ht.put("status", String.valueOf(status));
		} else {
			status = 1;
			apache_ht.put("status", String.valueOf(status));
		}

		return apache_ht;
	}

	/**
	 * 
	 * apache服务流程
	 * 
	 * @return
	 */

	private String apacheServer() {
		Integer id = getParaIntValue("id");
		ApacheConfig vo = null;
		ApacheConfigDao dao = null;
		List arr = null;
		try {
			dao = new ApacheConfigDao();
			arr = dao.getApacheById(id);
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		if (arr != null && arr.size() > 0) {
			vo = (ApacheConfig) arr.get(0);
		}
		request.setAttribute("vo", vo);
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ApacheConfigDao configdao = new ApacheConfigDao();
		Apachemonitor_realtimeDao realtimedao = new Apachemonitor_realtimeDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		ApacheConfig initconf = new ApacheConfig(); // 当前的对象
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String wave_name = "";
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
				urllist = configdao.getApacheByBID(rbids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();

			
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new ApacheConfigDao();
				try {
					initconf = (ApacheConfig) configdao.findByID(queryid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
			queryid = initconf.getId();
			conn_name = queryid + "urlmonitor-conn";

			List urlList = null;
			try {
				urlList = realtimedao.getByApacheId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Apachemonitor_realtime) urlList.get(0)).getMon_time();
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

			// 是否连通
//			String conn[] = new String[2];
//			configdao = new ApacheConfigDao();
//			conn = configdao.getAvailability(vo.getIpaddress(), starttime, totime, "thevalue");
//			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(configdao!=null)
			configdao.close();
			realtimedao.close();
		}
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("wave_name", wave_name);
		;
		request.setAttribute("connrate", connrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		return "/application/apache/apacheserver.jsp";
	}

	/**
	 * 性能信息
	 * 
	 * @return
	 */
	private String apacheStatus() {
		Integer id = getParaIntValue("id");
		ApacheConfig vo = null;
		ApacheConfigDao dao = new ApacheConfigDao();
		List arr = dao.getApacheById(id);
		for (int i = 0; i < arr.size(); i++) {
			vo = (ApacheConfig) arr.get(i);
		}
		request.setAttribute("vo", vo);
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ApacheConfigDao configdao = new ApacheConfigDao();
		Apachemonitor_realtimeDao realtimedao = new Apachemonitor_realtimeDao();
//		Apachemonitor_historyDao historydao = new Apachemonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		ApacheConfig initconf = new ApacheConfig(); // 当前的对象
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
				urllist = configdao.getApacheByBID(rbids);
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
				configdao = new ApacheConfigDao();
				try {
					initconf = (ApacheConfig) configdao.findByID(queryid + "");
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
				urlList = realtimedao.getByApacheId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Apachemonitor_realtime) urlList.get(0)).getMon_time();
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

//			InitCoordinate initer = new InitCoordinate(new GregorianCalendar(), hour, 1);
			// Minute[] minutes=initer.getMinutes();
//			TimeSeries ss1 = new TimeSeries("", Minute.class);
//			TimeSeries ss2 = new TimeSeries("", Minute.class);
//
//			TimeSeries[] s = new TimeSeries[1];
//			TimeSeries[] s_ = new TimeSeries[1];
//			Vector wave_v = null;
//			try {
//				wave_v = historydao.getByApacheid(queryid, starttime, totime, 0);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} 
//			if (wave_v == null)
//				wave_v = new Vector();
//			for (int i = 0; i < wave_v.size(); i++) {
//				Hashtable ht = (Hashtable) wave_v.get(i);
//				double conn = Double.parseDouble(ht.get("conn").toString());
//				String time = ht.get("mon_time").toString();
//				ss1.addOrUpdate(new Minute(sdf1.parse(time)), conn);
//			}
//			s[0] = ss1;
//			s_[0] = ss2;
//			ChartGraph cg = new ChartGraph();
//			cg.timewave(s, "时间", "连通", "", wave_name, 600, 120);
			// cg.timewave(s_,"时间","时延(ms)","",delay_name,600,120);
			// p_draw_line(s,"","url"+queryid+"ConnectUtilization",740,120);

			// 是否连通
//			String conn[] = new String[2];
//			if (flag == 0)
//				conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
//			else {
//				conn = historydao.getAvailability(queryid, starttime, totime, "is_canconnected");
//			}
//			String[] key1 = { "连通", "未连通" };
//			drawPiechart(key1, conn, "", conn_name);
//			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			historydao.close();
			configdao.close();
			realtimedao.close();
		}
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("wave_name", wave_name);
		;
		request.setAttribute("connrate", connrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		return "/application/apache/apachestatus.jsp";
	}

	/**
	 * 进程状态
	 * 
	 * @return
	 */
	private String apacheProcess() {
		Integer id = getParaIntValue("id");
		ApacheConfig vo = null;
		ApacheConfigDao dao = new ApacheConfigDao();
		List arr = dao.getApacheById(id);
		for (int i = 0; i < arr.size(); i++) {
			vo = (ApacheConfig) arr.get(i);
		}
		request.setAttribute("vo", vo);
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ApacheConfigDao configdao = new ApacheConfigDao();
		Apachemonitor_realtimeDao realtimedao = new Apachemonitor_realtimeDao();
//		Apachemonitor_historyDao historydao = new Apachemonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		ApacheConfig initconf = new ApacheConfig(); // 当前的对象
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
				urllist = configdao.getApacheByBID(rbids);
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
				configdao = new ApacheConfigDao();
				try {
					initconf = (ApacheConfig) configdao.findByID(queryid + "");
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
				urlList = realtimedao.getByApacheId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Apachemonitor_realtime) urlList.get(0)).getMon_time();
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

//			InitCoordinate initer = new InitCoordinate(new GregorianCalendar(), hour, 1);
//			TimeSeries ss1 = new TimeSeries("", Minute.class);
//			TimeSeries ss2 = new TimeSeries("", Minute.class);
//
//			TimeSeries[] s = new TimeSeries[1];
//			TimeSeries[] s_ = new TimeSeries[1];
//			Vector wave_v = null;
//			try {
//				wave_v = historydao.getByApacheid(queryid, starttime, totime, 0);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				// historydao.close();
//			}
//			if (wave_v == null)
//				wave_v = new Vector();
//			for (int i = 0; i < wave_v.size(); i++) {
//				Hashtable ht = (Hashtable) wave_v.get(i);
//				double conn = Double.parseDouble(ht.get("conn").toString());
//				String time = ht.get("mon_time").toString();
//				ss1.addOrUpdate(new Minute(sdf1.parse(time)), conn);
//			}
//			s[0] = ss1;
//			s_[0] = ss2;
//			ChartGraph cg = new ChartGraph();
//			cg.timewave(s, "时间", "连通", "", wave_name, 600, 120);

			// 是否连通
//			String conn[] = new String[2];
//			conn = configdao.getAvailability(vo.getIpaddress(), starttime, totime, "thevalue");
//			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			historydao.close();
			configdao.close();
			realtimedao.close();
		}
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("wave_name", wave_name);
		;
		request.setAttribute("connrate", connrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		return "/application/apache/apacheprocess.jsp";
	}

	/**
	 * 连通图
	 * 
	 * @return
	 */
	private String apachePing() {
		Integer id = getParaIntValue("id");
		ApacheConfig vo = null;
		ApacheConfigDao dao = new ApacheConfigDao();
		List arr = dao.getApacheById(id);
		for (int i = 0; i < arr.size(); i++) {
			vo = (ApacheConfig) arr.get(i);
		}
		request.setAttribute("vo", vo);
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ApacheConfigDao configdao = new ApacheConfigDao();
		Apachemonitor_realtimeDao realtimedao = new Apachemonitor_realtimeDao();
		Apachemonitor_historyDao historydao = new Apachemonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		ApacheConfig initconf = new ApacheConfig(); // 当前的对象
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
				urllist = configdao.getApacheByBID(rbids);
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
				configdao = new ApacheConfigDao();
				try {
					initconf = (ApacheConfig) configdao.findByID(queryid + "");
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
				urlList = realtimedao.getByApacheId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Apachemonitor_realtime) urlList.get(0)).getMon_time();
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
				wave_v = historydao.getByApacheid(queryid, starttime, totime, 0);
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
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("wave_name", wave_name);
		;
		request.setAttribute("connrate", connrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		return "/application/apache/apacheping.jsp";
	}

	private String system() {

		Integer id = getParaIntValue("id");
		ApacheConfig vo = null;
		String connrate = "0";
		ApacheConfigDao dao = new ApacheConfigDao();
		List arr = dao.getApacheById(id);
		for (int i = 0; i < arr.size(); i++) {
			vo = (ApacheConfig) arr.get(i);
		}
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List urllist = new ArrayList(); // 用做条件选择列表
		ApacheConfig initconf = new ApacheConfig(); // 当前的对象
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
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
		String starttime = from_date1 + " " + from_hour + ":00:00";
		String totime = to_date1 + " " + to_hour + ":59:59";
		// 是否连通
		Integer queryid = getParaIntValue("id");// .getUrl_id();

		if (urllist.size() > 0 && queryid == null) {
			Object obj = urllist.get(0);
		}
		ApacheConfigDao configdao = null;
		try {
			configdao = new ApacheConfigDao();
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				initconf = (ApacheConfig) configdao.findByID(queryid + "");
			}
			queryid = initconf.getId();
			String conn[] = new String[2];
			conn = configdao.getAvailability(vo.getIpaddress(), starttime, totime, "thevalue");
			// String[] key1 = { "连通", "未连通" };
			// drawPiechart(key1, conn, "", conn_name);
			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		request.setAttribute("vo", vo);
		request.setAttribute("connrate", connrate);
		return "/application/apache/apache_system.jsp";

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
		ApacheConfig apache = null;
		ApacheConfigDao apacheDao = new ApacheConfigDao();
		try {
			apache = (ApacheConfig) apacheDao.findByID(String.valueOf(queryid));
			ip = getParaValue("ipaddress");
			newip = SysUtil.doip(ip);
			Hashtable ConnectUtilizationhash = apacheDao.getPingDataById(ip, queryid, starttime, totime);
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
			reporthash.put("servicename", apache.getAlias());
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
		}finally {
			apacheDao.close();
		}
		return "/application/apache/showPingReport.jsp";
	}

	private String eventReport() {

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";

		tmp = request.getParameter("id");
		Node config = PollingEngine.getInstance().getApacheByID(Integer.parseInt(tmp));
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

			list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "apache");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dao.close();
		}

		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/apache/eventReport.jsp";
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
		Node apacheNode = PollingEngine.getInstance().getApacheByID(Integer.parseInt(id));
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "' ");
		s.append(" and nodeid=" + apacheNode.getId());

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
				report1.createReport_midEventDoc(fileName, starttime1, totime1, "apache");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/apacheEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_TomcatEventExc(file, id, starttime1, totime1, "apache");
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
				report1.createReport_midEventPdf(fileName, starttime1, totime1, "apache");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

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
		ApacheConfig apache = null;
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		ApacheConfigDao apacheDao = new ApacheConfigDao();
		try {
			apache = (ApacheConfig) apacheDao.findByID(String.valueOf(queryid));
			ip = getParaValue("ipaddress");
			newip = SysUtil.doip(ip);
			Hashtable ConnectUtilizationhash = apacheDao.getPingDataById(ip, queryid, starttime, totime);
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

				list = dao.getQuery(starttime, totime, status + "", level1 + "", vo.getBusinessids(), queryid, "apache");

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("list", list);
			// 画图-----------------------------
			reporthash.put("servicename", apache.getAlias());
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
		return "/application/apache/allReport.jsp";
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
		Hashtable reporthash = new Hashtable();
		Node apacheNode = PollingEngine.getInstance().getApacheByID(Integer.parseInt(id));
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		s.append(" and nodeid=" + apacheNode.getId());

		List infolist = eventdao.findByCriteria(s.toString());
		reporthash.put("eventlist", infolist);
		// ///////////////////////////////////////////////////
		ip = apacheNode.getIpAddress();
		ApacheConfigDao apacheDao = new ApacheConfigDao();
		Hashtable ConnectUtilizationhash = apacheDao.getPingDataById(ip, Integer.parseInt(id), starttime, totime);
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
		reporthash.put("servicename", apacheNode.getAlias());
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
			String file = "temp/apacheEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midDoc(fileName, starttime, totime, "apache");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/apache_Report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_ApacheExc(file, id, starttime, totime, "apache");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/apache_Report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {

				report1.createReport_ApachePdf(fileName, id + "", starttime, totime, "apache");

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
	 * @description 告警信息
	 * @author wangxiangyong
	 * @date Mar 28, 2013 8:51:58 AM
	 * @return String
	 * @return
	 */
	private String alarm() {
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
			Node apacheNode = PollingEngine.getInstance().getApacheByID(Integer.parseInt(tmp));
			ip = apacheNode.getIpAddress();
			String newip = SysUtil.doip(ip);
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;

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

			EventListDao dao = new EventListDao();
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "apache");
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ApacheConfig vo = null;
		ApacheConfigDao dao = new ApacheConfigDao();
		List arr = dao.getApacheById(Integer.parseInt(tmp));
		for (int i = 0; i < arr.size(); i++) {
			vo = (ApacheConfig) arr.get(i);
		}
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ApacheConfigDao configdao = new ApacheConfigDao();
		Apachemonitor_realtimeDao realtimedao = new Apachemonitor_realtimeDao();
//		Apachemonitor_historyDao historydao = new Apachemonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		ApacheConfig initconf = new ApacheConfig(); // 当前的对象
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
				urllist = configdao.getApacheByBID(rbids);
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
				configdao = new ApacheConfigDao();
				try {
					initconf = (ApacheConfig) configdao.findByID(queryid + "");
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
				urlList = realtimedao.getByApacheId(queryid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				realtimedao.close();
			}

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Apachemonitor_realtime) urlList.get(0)).getMon_time();
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

			
			// 是否连通
//			String conn[] = new String[2];
//			conn = configdao.getAvailability(vo.getIpaddress(), starttime, totime, "thevalue");
//			connrate = getF(String.valueOf(Float.parseFloat(conn[0]) / (Float.parseFloat(conn[0]) + Float.parseFloat(conn[1])) * 100)) + "%";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			historydao.close();
			if(configdao!=null)
			configdao.close();
			realtimedao.close();
		}
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("wave_name", wave_name);
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);
		request.setAttribute("connrate", connrate);
		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);
		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);
		request.setAttribute("vo", vo);
		request.setAttribute("vector", vector);
		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/apache/apacheAlarm.jsp";

	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return "/application/apache/add.jsp";
		if (action.equals("add"))
			return add();
		if (action.equals("delete"))
			return delete();
		if (action.equals("ready_edit")) {
			DaoInterface dao = new ApacheConfigDao();
			setTarget("/application/apache/edit.jsp");
			return readyEdit(dao);
		}
		if (action.equals("update"))
			return update();
		if (action.equals("addalert"))
			return addalert();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("server"))
			return apacheServer();
		if (action.equals("status"))
			return apacheStatus();
		if (action.equals("process"))
			return apacheProcess();
		if (action.equals("ping"))
			return apachePing();
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("showPingReport"))
			return showPingReport();
		if (action.equals("eventReport"))
			return eventReport();
		if (action.equals("downloadEventReport"))
			return downloadEventReport();
		if (action.equals("allReport"))
			return allReport();
		if (action.equals("downloadAllReport"))
			return downloadAllReport();
		if (action.equals("alarm"))
			return alarm();
		if (action.equals("system"))
			return system();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Mar 29, 2013 9:48:30 AM
	 * @return String  
	 * @return
	 */
	 private String sychronizeData(){
		 String id = request.getParameter("id");
			ApacheDataCollector appache=new ApacheDataCollector();
			appache.collectData(id);
			
			String url="/apache.do?action=system&id="+id;
		return url;
		 
	 }
	private void drawPiechart(String[] keys, String[] values, String chname, String enname) {
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for (int i = 0; i < keys.length; i++) {
			piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname, piedata, enname, 200, 120);
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

	public List<ApacheConfig> getApacheConfigListByMonflag(Integer flag) {
		ApacheConfigDao apacheConfigDao = null;
		List<ApacheConfig> apacheConfigList = null;
		try {
			apacheConfigDao = new ApacheConfigDao();
			apacheConfigList = (List<ApacheConfig>) apacheConfigDao.getApacheConfigListByMonFlag(flag);
		} catch (Exception e) {

		} finally {
			apacheConfigDao.close();
		}
		return apacheConfigList;
	}

	private String isOK() {
		ApacheConfigDao apacheConfigDao = null;
		String ipaddress = "";
		int port = 0;
		String reason = "Apache当前状态不可用";
		String name = "";
		boolean isSucess = false;
		ApacheConfig apacheConfig = null;
		List<ApacheConfig> apacheConfigList = null;
		int id = getParaIntValue("id");
		try {
			apacheConfigDao = new ApacheConfigDao();
			apacheConfigList = apacheConfigDao.getApacheById(id);
			if (apacheConfigList != null && apacheConfigList.size() > 0)
				apacheConfig = apacheConfigList.get(0);
			if (apacheConfig != null) {
				ipaddress = apacheConfig.getIpaddress();
				port = apacheConfig.getPort();
				HttpClientJBoss apache = new HttpClientJBoss();
				String response = apache.getGetResponseWithHttpClient("http://" + ipaddress + ":" + port, "GBK");
				if (response.toLowerCase().contains("it works")) {
					reason = "Apache当前状态可用";
					isSucess = true;
				}
				name = apacheConfig.getAlias();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		request.setAttribute("isOK", isSucess);
		request.setAttribute("name", name);
		request.setAttribute("str", ipaddress);
		return "/tool/tomcatisok.jsp";
	}
}
