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
import com.afunms.application.dao.DpConfigDao;
import com.afunms.application.dao.NasConfigDao;
import com.afunms.application.dao.TracertsDao;
import com.afunms.application.dao.TracertsDetailDao;
import com.afunms.application.dao.Urlmonitor_historyDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.model.NasConfig;
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
import com.afunms.polling.loader.NasLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Nas;
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

public class NasManager extends BaseManager implements ManagerInterface {
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
		NasConfigDao configdao = new NasConfigDao();
		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getNasByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		request.setAttribute("list", list);
		setTarget("/application/nas/nasconfiglist.jsp");
		return "/application/nas/nasconfiglist.jsp";
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
		return "/application/nas/add.jsp";
	}
	private String add() {
		NasConfig vo = new NasConfig();
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
		NasLoader loader = new NasLoader();
		//WebLoader loader = new WebLoader();
		try {
			loader.loadOne(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}
		
		NasConfigDao dao = new NasConfigDao();
		//WebConfigDao dao = new WebConfigDao();
		try {
			dao.save(vo);
			/**
			 * nielin add for time-sharing 2010-01-04
			 */
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("21"));

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("27"));
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "nas", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "nas");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (ShareData.getAllnas() != null) {
			ShareData.getAllnas().put(vo.getId(), vo);
		}
		NasConfigDao nasdao = new NasConfigDao();
		List list = new ArrayList();
		try {
			list = nasdao.loadAll();
		} catch (Exception e) {

		} finally {
			nasdao.close();
		}
		if (list == null)
			list = new ArrayList();
		ShareData.setNasconfiglist(list);
		nasdao.clearRubbish(list);
		return "/nas.do?action=list&jp=1&flag=" + _flag;
	}

	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		NasConfig vo = new NasConfig();
		List list = new ArrayList();
		String _flag = (String) request.getAttribute("flag");
		if (ids != null && ids.length > 0) {
			NasConfigDao configdao = new NasConfigDao();
			try {
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
																						// add
																						// 2009-12-30
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				for (int i = 0; i < ids.length; i++) {
					vo = (NasConfig)configdao.findByID(ids[i]);
					tg.deleteTimeGratherConfig(ids[i], timeShareConfigUtil.getObjectType("21"));
					timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("21")); // nielin
																												// add
																												// 2009-12-30
					PollingEngine.getInstance().deleteNasByID(Integer.parseInt(ids[i]));
					
					String id = ids[i];
					CreateTableManager ctable = new CreateTableManager();
					DBManager conn = new DBManager();
					ctable.deleteTable(conn, "ping", id, "ping");// Ping
					conn.close();
					EventListDao eventdao = new EventListDao();
					try {
						// 同时删除事件表里的相关数据
							eventdao.delete(vo.getId(), "nas");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventdao.close();
					}
					
					//删除存储数据表
					DBManager dbmanager = new DBManager();
					String sql = "delete from nms_hpnas where ipaddress = '" + vo.getIpAddress() + "'";
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
					List dplist = nodedependao.findByNode("nas" + id);
					if (dplist != null && dplist.size() > 0) {
						for (int j = 0; j < dplist.size(); j++) {
							NodeDepend wesvo = (NodeDepend) dplist.get(j);
							if (wesvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("nas" + id, wesvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("nas" + id, wesvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("nas" + id, wesvo.getXmlfile());
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
				dao.deleteByNodeIdsAndTypeAndSubtype(ids, "service", "nas");
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			// 删除该数据库的告警阀值
			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
			try {
				indidao.deleteByNodeIds(ids, "service", "nas");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				indidao.close();
			}
			
			NasConfigDao nasdao = new NasConfigDao();
			List nasblist = new ArrayList();
			try {
				nasblist = nasdao.loadAll();
			} catch (Exception e) {

			} finally {
				nasdao.close();
			}
			if (nasblist == null)
				nasblist = new ArrayList();
			ShareData.setNasconfiglist(nasblist);
			nasdao.clearRubbish(nasblist);
		}
		return "/nas.do?action=list&jp=1&flag=" + _flag;
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

		setTarget("/web.do?action=alarm");
		return list(dao, sql);
	}

	/**
	 * @author nielin add
	 * @since 2009-12-30
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/nas/edit.jsp";
		NasConfigDao dao = new NasConfigDao();
		try {
			setTarget(jsp);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("21"));
			request.setAttribute("timeShareConfigList", timeShareConfigList);
			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("27"));
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
		NasConfig vo = new NasConfig();

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
			NasConfigDao dao = new NasConfigDao();
			try {
				dao.update(vo);
				if (ShareData.getAllnas() != null) {
					ShareData.getAllnas().put(vo.getId(), vo);
				}
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
																						// add
																						// for
																						// time-sharing
																						// at
																						// 2010-01-04
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("21"));
				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("27"));
				/* snow add end */
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			Nas nas = (Nas) PollingEngine.getInstance().getNasByID(vo.getId());
			nas.setAlias(vo.getAlias());
			nas.setSendemail(vo.getSendemail());
			nas.setSendmobiles(vo.getSendmobiles());
			nas.setSendphone(vo.getSendphone());
			nas.setBid(vo.getNetid());
			nas.setMon_flag(vo.getMon_flag());
			nas.setIpAddress(vo.getIpAddress());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// dao.close();
		}
		return "/nas.do?action=list&jp=1&flag=" + _flag;
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
		//			
		// if(oraList != null && oraList.size()>0){
		// for(int i=0;i<oraList.size();i++){
		// DBVo dbmonitorlist = (DBVo)oraList.get(i);
		// ips.add(dbmonitorlist.getIpAddress());
		// }
		// }
		//			
		//			
		// configdao = new SybspaceconfigDao();
		// //configdao.fromLastToOraspaceconfig();
		//			
		// //ipaddress = (String)session.getAttribute("ipaddress");
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
		// //request.setAttribute("Oraspaceconfiglist", conflist);
		// request.setAttribute("iplist",ips);
		// request.setAttribute("ipaddress",ipaddress);
		// configdao = new SybspaceconfigDao();
		// list = configdao.getByIp(ipaddress);
		// request.setAttribute("list",list);
		return "/application/db/sybaseconfigsearchlist.jsp";
	}

	private String addalert() {
		NasConfig vo = new NasConfig();

		List list = new ArrayList();
		NasConfigDao configdao = null;
		try {
			 configdao = new NasConfigDao();
			vo = (NasConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(1);
			configdao.update(vo);
			
			NasLoader loader = new NasLoader();
			//WebLoader loader = new WebLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		return "/nas.do?action=list&jp=1";
	}

	private String cancelalert() {
		NasConfig vo = new NasConfig();
		//WebConfig vo = new WebConfig();
		
		NasConfigDao configdao = null;
		//WebConfigDao configdao =null;
		try {
			   configdao = new NasConfigDao();
				vo = (NasConfig) configdao.findByID(getParaValue("id"));
		     	vo.setFlag(0);
				configdao.update(vo);
			
//			Web web = (Web) PollingEngine.getInstance().getWebByID(vo.getId());
//			web.setFlag(0);
			NasLoader loader = new NasLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 configdao.close();
		}
		return "/nas.do?action=list&jp=1";
	}

	private String detail() {
		
/////////////
		NasConfig vo = new NasConfig();
		NasConfigDao dao = new NasConfigDao();
		try{
			String id1=getParaValue("id");
			request.setAttribute("id", id1);
			vo = (NasConfig)dao.findByID(id1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		Hashtable returnHash = new Hashtable();
		ResultSet rs = null;
		Hashtable hpnashash = new Hashtable();
		String sql = "select * from nms_hpnas where ipaddress = '"
				+ vo.getIpAddress() + "'";
		Hashtable retValue = new Hashtable();
		Vector v = new Vector();
		String collecttime = "";
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				hpnashash = new Hashtable();
				hpnashash.put("ipaddress", rs.getString("ipaddress"));
				hpnashash.put("server_name", rs.getString("server_name"));
				hpnashash.put("state", rs.getString("state"));
				hpnashash.put("cpu", rs.getString("cpu"));
				hpnashash.put("net_io", rs.getString("net_io"));
				hpnashash.put("disk_io", rs.getString("disk_io"));
				hpnashash.put("backup", rs.getString("backup"));
				hpnashash.put("ha", rs.getString("ha"));
				hpnashash.put("collecttime", rs.getString("mon_time"));
				collecttime = rs.getString("mon_time");
				v.add(hpnashash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
			sql = "";
		}
		returnHash.put("hpnas", v);
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
				list = eventdao.getQuery(starttime1,totime1,"nas","-1","3",user.getBusinessids(),vo.getId());
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
		return "/application/nas/nasdetail.jsp";
	}
	
	private String nasevent() {
		
	/////////////
		NasConfig vo = new NasConfig();
		NasConfigDao dao = new NasConfigDao();
		try{
			String id1=getParaValue("id");
			request.setAttribute("id", id1);
			vo = (NasConfig)dao.findByID(id1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		Hashtable returnHash = new Hashtable();
		ResultSet rs = null;
		Hashtable hpnashash = new Hashtable();
		String sql = "select * from nms_hpnas where ipaddress = '"
				+ vo.getIpAddress() + "'";
		Hashtable retValue = new Hashtable();
		Vector v = new Vector();
		String collecttime = "";
		DBManager dbmanager = new DBManager();
		try {
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				hpnashash = new Hashtable();
				hpnashash.put("ipaddress", rs.getString("ipaddress"));
				hpnashash.put("server_name", rs.getString("server_name"));
				hpnashash.put("state", rs.getString("state"));
				hpnashash.put("cpu", rs.getString("cpu"));
				hpnashash.put("net_io", rs.getString("net_io"));
				hpnashash.put("disk_io", rs.getString("disk_io"));
				hpnashash.put("backup", rs.getString("backup"));
				hpnashash.put("ha", rs.getString("ha"));
				hpnashash.put("collecttime", rs.getString("mon_time"));
				collecttime = rs.getString("mon_time");
				v.add(hpnashash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
			sql = "";
		}
		returnHash.put("hpnas", v);
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
				list = eventdao.getQuery(starttime1,totime1,"nas","99","99",user.getBusinessids(),vo.getId());
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
		return "/application/nas/nasevent.jsp";
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
		WebConfig initconf = null;
		WebConfigDao configdao = new WebConfigDao();
		try {
			initconf = (WebConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();

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
																			// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "webpingConnect", 740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", initconf.getAlias());
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
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/web/showPingReport.jsp";
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
		List<String> infoList = new ArrayList<String>();
		WebConfig initconf = null;
		WebConfigDao configdao = new WebConfigDao();
		try {
			initconf = (WebConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";

			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			// Webmonitor_realtimeDao realDao=new Webmonitor_realtimeDao();
			// List curList=realDao.getByFTPId(queryid);
			// Webmonitor_realtime ftpReal=new Webmonitor_realtime();
			// ftpReal=(Webmonitor_realtime) curList.get(0);
			// int ping=ftpReal.getIs_canconnected();
			// if (ping==1) {
			// curPing="100";
			// }else{
			// curPing="0";
			// }
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
																			// nms_web_history表获取,没必要再从nms_web_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.p_draw_line((List) ConnectUtilizationhash.get("pingList"), "连通率", newip + "pingConnect", 740, 150);
			pollMonitorManager.p_draw_line((List) ConnectUtilizationhash.get("delayList"), "时延", newip + "delayConnect", 740, 150);
			pollMonitorManager.p_draw_line((List) ConnectUtilizationhash.get("pageList"), "页面数据包大小", newip + "pageConnect", 740, 150);
			pollMonitorManager.p_draw_line((List) ConnectUtilizationhash.get("changeList"), "页面修改率", newip + "changeConnect", 740, 150);

			// 画图-----------------------------
			String name = "";
			String addr = "";
			if (initconf != null) {

				name = initconf.getAlias();
				ip = initconf.getIpAddress();
				addr = initconf.getStr();
				infoList.add("服务名称: " + name);
				infoList.add("      类型: 应用服务监视");
				infoList.add("      访问地址: " + addr);
				infoList.add("      IP地址: " + ip);
				reporthash.put("webconfig", initconf);
			}
			reporthash.put("servicename", name);
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			reporthash.put("comInfo", infoList);
			reporthash.put("type", "web");
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/ServiceCompositeReport.jsp";
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
		WebConfig initconf = null;
		WebConfigDao configdao = new WebConfigDao();
		try {
			initconf = (WebConfig) configdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		List infolist = null;
		List list = null;
		if (initconf != null) {

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
				s.append("select * from system_eventlist where recordtime>= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " " + "and recordtime<=" + "to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS') " + " ");
			}
			s.append(" and nodeid=" + id);
			try {
				list = eventdao.getQuery(starttime, totime, "url", status + "", level1 + "", user.getBusinessids(), initconf.getId());

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
				String servName = initconf.getAlias();

				List<String> ipeventList = new ArrayList<String>();
				ipeventList.add(ipaddress);
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
		request.setAttribute("list", list);

		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("eventlist", orderList);
		reporthash.put("list", list);
		session.setAttribute("reporthash", reporthash);
		return "/capreport/service/ServiceEventReport.jsp";

	}

	private String sychronizeData() {

		int queryid = getParaIntValue("id");// .getUrl_id();
		String page = getParaValue("page");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
																																				// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的URL所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1, "service", "url");
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
			WebDataCollector webcollector = new WebDataCollector();
			SysLogger.info("##############################");
			SysLogger.info("### 开始采集ID为" + queryid + "的URL数据 ");
			SysLogger.info("##############################");
			NodeGatherIndicators webIndicatorsNode = new NodeGatherIndicators();
			webIndicatorsNode.setNodeid(queryid + "");
			webcollector.collect_Data(webIndicatorsNode);
		} catch (Exception exc) {

		}
		return "/web.do?action=" + page + "&id=" + queryid;
		// return "/application/web/detail.jsp";
	}

	private String isOK() {

		int queryid = getParaIntValue("id");// .getUrl_id();
		List url_list = new ArrayList();
		WebConfigDao urldao = new WebConfigDao();
		Calendar date = Calendar.getInstance();
		WebConfig uc = null;
		try {
			uc = (WebConfig) urldao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			urldao.close();
		}
		boolean old = false;
		String str = "";
		Integer smssign = new Integer(0);
		Urlmonitor_realtime urold = null;

		UrlDataCollector udc = new UrlDataCollector();
		String contentStr = "";
		if (str != null && str.length() > 100) {
			contentStr = str.substring(0, 100);
		}

		Urlmonitor_realtime ur = null;
		try {
			ur = udc.getUrlmonitor_realtime(uc, old, contentStr);
		} catch (Exception e) {

		}
		// 实时数据
		boolean flag = true;
		if (ur.getIs_canconnected() == 0) {
			flag = false;
		} else if (uc.getMon_flag() == 2 && ur.getIs_refresh() == 0) {
			flag = false;
		}
		request.setAttribute("isOK", flag);
		request.setAttribute("name", uc.getAlias());
		request.setAttribute("str", uc.getStr());
		return "/tool/urlisok.jsp";
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
		if(action.equals("nasevent"))
			return nasevent();
		if (action.equals("sychronizeData"))
			return sychronizeData();
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("search"))
			return search();
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
		if (action.equals("showPingReport")) {
			return showPingReport();
		}
		if (action.equals("showCompositeReport")) {
			return showCompositeReport();
		}
		if (action.equals("showServiceEventReport")) {
			return showServiceEventReport();
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