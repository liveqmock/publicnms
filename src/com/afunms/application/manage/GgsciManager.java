/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

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
import com.afunms.application.dao.GgsciConfigDao;
import com.afunms.application.dao.TracertsDao;
import com.afunms.application.dao.TracertsDetailDao;
import com.afunms.application.dao.Urlmonitor_historyDao;
import com.afunms.application.model.GgsciConfig;
import com.afunms.application.model.Tracerts;
import com.afunms.application.model.Urlmonitor_realtime;
import com.afunms.application.model.WebConfig;
import com.afunms.application.util.UrlDataCollector;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
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
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.DpLoader;
import com.afunms.polling.loader.GgsciLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Ggsci;
import com.afunms.polling.task.WebDataCollector;
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

public class GgsciManager extends BaseManager implements ManagerInterface {
	private String list() {
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
		GgsciConfigDao configdao = new GgsciConfigDao();
		//WebConfigDao configdao = new WebConfigDao();
		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getGgsciByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		request.setAttribute("list", list);
		setTarget("/application/ggsci/ggsciconfiglist.jsp");
		return "/application/ggsci/ggsciconfiglist.jsp";
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
		return "/application/ggsci/add.jsp";
	}
	private String add() {
		GgsciConfig vo = new GgsciConfig();
		//WebConfig vo = new WebConfig();

		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setAlias(getParaValue("alias"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setSupperid(getParaIntValue("supperid"));
		String _flag = (String) request.getAttribute("flag");
		// SysLogger.info("word=========================="+words);

		vo.setNetid(getParaValue("bid"));
		// vo.setManaged(getParaIntValue("managed"));
		// 在数据库里增加被监控指标
		// DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
		// dcDao.addDBMonitor(vo.getId(),vo.getIpAddress(),"mysql");

		// 在轮询线程中增加被监视节点
		GgsciLoader loader = new GgsciLoader();
		//WebLoader loader = new WebLoader();
		try {
			loader.loadOne(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}
		
		GgsciConfigDao dao = new GgsciConfigDao();
		//WebConfigDao dao = new WebConfigDao();
		try {
			dao.save(vo);
			/**
			 * nielin add for time-sharing 2010-01-04
			 */
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("22"));

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("28"));
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "ggsci", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "ggsci");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (ShareData.getAllggsci() != null) {
			ShareData.getAllggsci().put(vo.getId(), vo);
		}
		GgsciConfigDao ggscidao = new GgsciConfigDao();
		List list = new ArrayList();
		try {
			list = ggscidao.loadAll();
		} catch (Exception e) {

		} finally {
			ggscidao.close();
		}
		if (list == null)
			list = new ArrayList();
		ShareData.setGgsciconfiglist(list);
		ggscidao.clearRubbish(list);
		return "/ggsci.do?action=list&jp=1&flag=" + _flag;
	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		GgsciConfig vo = new GgsciConfig();
		List list = new ArrayList();
		String _flag = (String) request.getAttribute("flag");
		if (ids != null && ids.length > 0) {
			GgsciConfigDao configdao = new GgsciConfigDao();
			try {
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
																						// add
																						// 2009-12-30
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				for (int i = 0; i < ids.length; i++) {
					vo = (GgsciConfig)configdao.findByID(ids[i]);
					tg.deleteTimeGratherConfig(ids[i], timeShareConfigUtil.getObjectType("22"));
					timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("22")); // nielin
																												// add
																												// 2009-12-30
					PollingEngine.getInstance().deleteGgsciByID(Integer.parseInt(ids[i]));
					
					String id = ids[i];
					CreateTableManager ctable = new CreateTableManager();
					DBManager conn = new DBManager();
					ctable.deleteTable(conn, "ping", id, "ping");// Ping
					conn.close();
					EventListDao eventdao = new EventListDao();
					try {
						// 同时删除事件表里的相关数据
							eventdao.delete(vo.getId(), "ggsci");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventdao.close();
					}
					
					DBManager dbmanager = new DBManager();
					String sql = "delete from nms_ggsci where ipaddress = '" + vo.getIpAddress() + "'";
					try {
						dbmanager.addBatch(sql);
						dbmanager.executeBatch();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dbmanager.close();
					}

					// 更新业务视图
					NodeDependDao nodedependao = new NodeDependDao();
					List ggscilist = nodedependao.findByNode("ggsci" + id);
					if (ggscilist != null && ggscilist.size() > 0) {
						for (int j = 0; j < ggscilist.size(); j++) {
							NodeDepend wesvo = (NodeDepend) ggscilist.get(j);
							if (wesvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("ggsci" + id, wesvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("ggsci" + id, wesvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("ggsci" + id, wesvo.getXmlfile());
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
				configdao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			// 删除相关数据表信息
//			Urlmonitor_historyDao urlmonitor_historyDao = new Urlmonitor_historyDao();
//			Urlmonitor_realtimeDao urlmonitor_realtimeDao = new Urlmonitor_realtimeDao();
//			for (int i = 0; i < ids.length; i++) {
//				urlmonitor_historyDao.deleteByUrl(ids[i]);
//				urlmonitor_realtimeDao.deleteByUrl(ids[i]);
//			}
//			urlmonitor_historyDao.close();
//			urlmonitor_realtimeDao.close();

			// 删除该数据库的采集指标
			NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
			try {
				dao.deleteByNodeIdsAndTypeAndSubtype(ids, "service", "ggsci");
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			// 删除该数据库的告警阀值
			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
			try {
				indidao.deleteByNodeIds(ids, "service", "ggsci");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				indidao.close();
			}
			
			GgsciConfigDao ggscidao = new GgsciConfigDao();
			List ggsciblist = new ArrayList();
			try {
				ggsciblist = ggscidao.loadAll();
			} catch (Exception e) {

			} finally {
				ggscidao.close();
			}
			if (ggsciblist == null)
				ggsciblist = new ArrayList();
			ShareData.setDpconfiglist(ggsciblist);
			ggscidao.clearRubbish(ggsciblist);
		}
		return "/ggsci.do?action=list&jp=1&flag=" + _flag;
	}

	private String alarmdelete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行删除
			EventListDao edao = new EventListDao();
			edao.delete(ids);
			edao.close();

		}

		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventListDao dao = new EventListDao();
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
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				s.append("where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				s.append("where recordtime>= " + "to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') " + " " + "and recordtime<=" + "to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS') " + "");
			}
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (businessid != null) {
				if (businessid != "-1") {
					String[] bids = businessid.split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (flag == 0) {
									s.append(" and ( businessid = '," + bids[i].trim() + ",' ");
									flag = 1;
								} else {
									// flag = 1;
									s.append(" or businessid = '," + bids[i].trim() + ",' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
			sql = s.toString();
			sql = sql + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);

		setTarget("/ggsci.do?action=alarm");
		return list(dao, sql);
	}

	/**
	 * @author nielin add
	 * @since 2009-12-30
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/ggsci/edit.jsp";
		GgsciConfigDao dao = new GgsciConfigDao();
		try {
			setTarget(jsp);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("22"));
			request.setAttribute("timeShareConfigList", timeShareConfigList);
			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("28"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
			/* snow end */
			jsp = readyEdit(dao);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return jsp;
	}

	private String update() {
		GgsciConfig vo = new GgsciConfig();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";

		String _flag = request.getParameter("flag");

		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setFlag(getParaIntValue("_flag"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setSupperid(getParaIntValue("supperid"));
		vo.setNetid(getParaValue("bid"));
		try {
			GgsciConfigDao dao = new GgsciConfigDao();
			try {
				dao.update(vo);
				if (ShareData.getAllggsci() != null) {
					ShareData.getAllggsci().put(vo.getId(), vo);
				}
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
																						// add
																						// for
																						// time-sharing
																						// at
																						// 2010-01-04
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("22"));
				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("28"));
				/* snow add end */
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			Ggsci dp = (Ggsci) PollingEngine.getInstance().getGgsciByID(vo.getId());
			dp.setAlias(vo.getAlias());
			dp.setSendemail(vo.getSendemail());
			dp.setSendmobiles(vo.getSendmobiles());
			dp.setSendphone(vo.getSendphone());
			dp.setBid(vo.getNetid());
			dp.setMon_flag(vo.getMon_flag());
			dp.setIpAddress(vo.getIpAddress());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// dao.close();
		}
		return "/ggsci.do?action=list&jp=1&flag=" + _flag;
	}


	private String addalert() {
		GgsciConfig vo = new GgsciConfig();

		List list = new ArrayList();
		GgsciConfigDao configdao = null;
		try {
			 configdao = new GgsciConfigDao();
			vo = (GgsciConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(1);
			configdao.update(vo);
			
			GgsciLoader loader = new GgsciLoader();
			//WebLoader loader = new WebLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		return "/ggsci.do?action=list&jp=1";
	}

	private String cancelalert() {
		GgsciConfig vo = new GgsciConfig();
		//WebConfig vo = new WebConfig();
		
		GgsciConfigDao configdao = null;
		//WebConfigDao configdao =null;
		try {
			   configdao = new GgsciConfigDao();
				vo = (GgsciConfig) configdao.findByID(getParaValue("id"));
		     	vo.setFlag(0);
				configdao.update(vo);
			
//			Web web = (Web) PollingEngine.getInstance().getWebByID(vo.getId());
//			web.setFlag(0);
			GgsciLoader loader = new GgsciLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 configdao.close();
		}
		return "/ggsci.do?action=list&jp=1";
	}

	private String detail() {
		
/////////////
		GgsciConfig vo = new GgsciConfig();
		GgsciConfigDao dao = new GgsciConfigDao();
		try{
			String id1=getParaValue("id");
			request.setAttribute("id", id1);
			vo = (GgsciConfig)dao.findByID(id1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		Hashtable returnHash = new Hashtable();
		ResultSet rs = null;
		Hashtable ggscihash = new Hashtable();
		String sql = "select * from nms_ggsci where ipaddress = '"
				+ vo.getIpAddress() + "'";
		Vector v = new Vector();
		String collecttime = "";
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				ggscihash = new Hashtable();
				ggscihash.put("ipaddress", rs.getString("ipaddress"));
				ggscihash.put("programName", rs.getString("programName"));
				ggscihash.put("status1", rs.getString("status1"));
				ggscihash.put("group1", rs.getString("group1"));
				ggscihash.put("lagAtChkpt", rs.getString("lagAtChkpt"));
				ggscihash.put("timeSinceChkpt", rs.getString("timeSinceChkpt"));
				ggscihash.put("collecttime", rs.getString("collecttime"));
				collecttime = rs.getString("collecttime");
				v.add(ggscihash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
			sql = "";
		}
		returnHash.put("ggsci", v);
		returnHash.put("collecttime", collecttime);
		
		request.setAttribute("vo", vo);
		request.setAttribute("returnHash", returnHash);
		return "/application/ggsci/ggscidetail.jsp";
	}
	
	private String ggscievent() {
		
	/////////////
		GgsciConfig vo = new GgsciConfig();
		GgsciConfigDao dao = new GgsciConfigDao();
		try{
			String id1=getParaValue("id");
			request.setAttribute("id", id1);
			vo = (GgsciConfig)dao.findByID(id1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		Hashtable returnHash = new Hashtable();
		ResultSet rs = null;
		Hashtable ggscihash = new Hashtable();
		String sql = "select * from nms_ggsci where ipaddress = '"
				+ vo.getIpAddress() + "'";
		Vector v = new Vector();
		String collecttime = "";
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				ggscihash = new Hashtable();
				ggscihash.put("ipaddress", rs.getString("ipaddress"));
				ggscihash.put("programName", rs.getString("programName"));
				ggscihash.put("status1", rs.getString("status1"));
				ggscihash.put("group1", rs.getString("group1"));
				ggscihash.put("lagAtChkpt", rs.getString("lagAtChkpt"));
				ggscihash.put("timeSinceChkpt", rs.getString("timeSinceChkpt"));
				ggscihash.put("collecttime", rs.getString("collecttime"));
				collecttime = rs.getString("collecttime");
				v.add(ggscihash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
			sql = "";
		}
		returnHash.put("ggsci", v);
		returnHash.put("collecttime", collecttime);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
		String newip=SysUtil.doip(vo.getIpAddress());						
	
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		String strStartDay = getParaValue("startdate");
		String strToDay = getParaValue("todate");
		if(strStartDay!=null && !"".equals(strStartDay)){
		starttime1 = strStartDay+" 00:00:00";
		}
		if(strToDay!=null && !"".equals(strToDay)){
		totime1 = strToDay+" 23:59:59";
		}
		List list = new ArrayList();
		try{
			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
			//SysLogger.info("user businessid===="+vo.getBusinessids());
			EventListDao eventdao = new EventListDao();
			try{
				//list = eventdao.getQuery(starttime1,totime1,"db",status+"",level1+"",user.getBusinessids(),vo.getId());
				list = eventdao.getQuery(starttime1,totime1,"ggsci","-1","3",user.getBusinessids(),vo.getId());
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				eventdao.close();
			}
			
			//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		String b_time ="";
		String t_time = "";
		
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
    	
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (b_time == null){
			b_time = sdf1.format(new Date());
		}
		if (t_time == null){
			t_time = sdf1.format(new Date());
		}
		
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("vo", vo);
		request.setAttribute("returnHash", returnHash);
		return "/application/ggsci/ggscievent.jsp";
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
		if (action.equals("alarmdelete"))
			return alarmdelete();
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
		if(action.equals("ggscievent"))
			return ggscievent();
		if (action.equals("liantong")) {
			return liantong();
		}
		if (action.equals("page")) {
			return page();
		}
		if (action.equals("alarm")) {
			return alarm();
		}
		if (action.equals("tracert")) {
			return tracert();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String liantong() {

		String flag = getParaValue("flag");
		return detail();
	}

	private String page() {
		detail();
		return "/application/web/page.jsp";
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
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "url");

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
		return "/application/web/alarm.jsp";
	}

	private String tracert() {
		String find = getParaValue("find");
		String tmp = request.getParameter("id");

		detail();
		Hashtable tracertHash = new Hashtable();
		String b_time = "";
		String t_time = "";
		List listDetails = new ArrayList();

		List timelist = new ArrayList();
		try {
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
				if (ShareData.getAlltracertsdata() != null && !"find".equals(find)) {
					tracertHash = ShareData.getAlltracertsdata();
				} else {
					String where = "";
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						where = "dotime >'" + starttime1 + "' and dotime <'" + totime1 + "'  and configid = " + tmp + " order by id desc";
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						where = "dotime >" + "to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS')" + " and dotime <" + "to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS')" + " and configid = " + tmp + " order by id desc";
					}
					TracertsDao tradao = new TracertsDao();
					List listTracerts = tradao.getAllRsByDoTime(where);
					tradao.close();
					if (listTracerts != null && listTracerts.size() > 0) {
						for (int i = 0; i < listTracerts.size(); i++) {
							Tracerts tracert = (Tracerts) listTracerts.get(i);
							TracertsDetailDao detaildao = new TracertsDetailDao();
							listDetails = detaildao.getListByTracertId(tracert.getId());
							timelist.add(tracert.getNodetype() + ":" + tracert.getId());
							detaildao.close();
							Hashtable hash = new Hashtable();
							hash.put("details", listDetails);
							hash.put("tracert", tracert);
							tracertHash.put(tracert.getNodetype() + ":" + tracert.getId(), hash);
						}
						request.setAttribute("timelist", timelist);
						request.setAttribute("select", "select");
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("listDetails", listDetails);
		request.setAttribute("tracertsHash", tracertHash);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/web/tracert.jsp";
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

}