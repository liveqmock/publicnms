package com.afunms.application.manage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.NtpConfigDao;
import com.afunms.application.model.NtpConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.NTPLoader;
import com.afunms.polling.node.Ntp;
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

public class NTPManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		if (action.equals("list"))

			return list();
		if (action.equals("ready_add"))
			return ready_add();
		if (action.equals("add"))
			return add();
		if (action.equals("ready_edit"))
			return ready_edit();
		if (action.equals("update"))
			return update();
		if (action.equals("delete"))
			return delete();
		if (action.equals("addalert"))
			return addalert();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("ntpevent"))
			return ntpevent();
		return null;
	}
	
	private String ready_add() {
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);
		return "/application/ntp/add.jsp";
	}
	
	private String add() {
		NtpConfig vo = new NtpConfig();

		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setAlias(getParaValue("alias"));
		vo.setFlag(getParaIntValue("flag"));
		vo.setPort(getParaValue("port"));
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
		NTPLoader loader = new NTPLoader();
		//WebLoader loader = new WebLoader();
		try {
			loader.loadOne(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader.close();
		}
		
		NtpConfigDao dao = new NtpConfigDao();
		//WebConfigDao dao = new WebConfigDao();
		try {
			dao.save(vo);
			/**
			 * nielin add for time-sharing 2010-01-04
			 */
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("23"));

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("29"));
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "service", "ntp", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "service", "ntp");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (ShareData.getAllntp() != null) {
			ShareData.getAllntp().put(vo.getId(), vo);
		}
		NtpConfigDao ntpdao = new NtpConfigDao();
		List list = new ArrayList();
		try {
			list = ntpdao.loadAll();
		} catch (Exception e) {

		} finally {
			ntpdao.close();
		}
		if (list == null)
			list = new ArrayList();
		ShareData.setNtpconfiglist(list);
		ntpdao.clearRubbish(list);
		return "/ntp.do?action=list&jp=1&flag=" + _flag;
	}

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
		NtpConfigDao configdao = new NtpConfigDao();
		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else {
				list = configdao.getNtpByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		DBManager dbmanger = new DBManager();
		String sql = "select * from nms_ntp";
		ResultSet rs = dbmanger.executeQuery(sql);
		Hashtable tmp = new Hashtable();
		Hashtable ntpHash = new Hashtable();
		try {
			while(rs.next()){
				tmp = new Hashtable();
				String ipaddress = rs.getString("ipaddress");
				String datetime = rs.getString("datetime");
				String collecttime = rs.getString("collecttime");
				tmp.put("ipaddress", ipaddress);
				tmp.put("datetime", datetime);
				tmp.put("collecttime", collecttime);
				ntpHash.put(ipaddress, tmp);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("list", list);
		request.setAttribute("ntpHash", ntpHash);
		setTarget("/application/ntp/ntpconfiglist.jsp");
		return "/application/ntp/ntpconfiglist.jsp";
	}
	
	private String update() {
		NtpConfig vo = new NtpConfig();

		String _flag = (String) request.getParameter("flag");
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setFlag(getParaIntValue("_flag"));
		vo.setPort(getParaValue("port"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setIpAddress(getParaValue("ipaddress"));
		vo.setSupperid(getParaIntValue("supperid"));

		vo.setNetid(getParaValue("bid"));
		try {
			NtpConfigDao dao = new NtpConfigDao();
			try {
				dao.update(vo);
				if (ShareData.getAllntp() != null) {
					ShareData.getAllntp().put(vo.getId(), vo);
				}
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin
																						// add
																						// for
																						// time-sharing
																						// at
																						// 2010-01-04
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("23"));
				/* 增加采集时间设置 snow add at 2010-5-20 */
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("29"));
				/* snow add end */
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			Ntp ntp = (Ntp) PollingEngine.getInstance().getNtpByID(vo.getId());
			ntp.setAlias(vo.getAlias());
			ntp.setPort(vo.getPort());
			ntp.setSendemail(vo.getSendemail());
			ntp.setSendmobiles(vo.getSendmobiles());
			ntp.setSendphone(vo.getSendphone());
			ntp.setBid(vo.getNetid());
			ntp.setMon_flag(vo.getMon_flag());
			ntp.setIpAddress(vo.getIpAddress());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// dao.close();
		}
		return "/ntp.do?action=list&jp=1&flag=" + _flag;
	}
	
	public String delete() {
		String[] ids = getParaArrayValue("checkbox");
		NtpConfig vo = new NtpConfig();
		List list = new ArrayList();
		String _flag = (String) request.getAttribute("flag");
		if (ids != null && ids.length > 0) {
			NtpConfigDao configdao = new NtpConfigDao();
			try {
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin

				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				for (int i = 0; i < ids.length; i++) {
					vo = (NtpConfig)configdao.findByID(ids[i]);
					tg.deleteTimeGratherConfig(ids[i], timeShareConfigUtil.getObjectType("23"));
					timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("23")); // nielin
																												// add
																												// 2009-12-30
					PollingEngine.getInstance().deleteNtpByID(Integer.parseInt(ids[i]));
					
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
					String sql = "delete from nms_ntp where ipaddress = '" + vo.getIpAddress() + "'";
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
					List dplist = nodedependao.findByNode("ntp" + id);
					if (dplist != null && dplist.size() > 0) {
						for (int j = 0; j < dplist.size(); j++) {
							NodeDepend wesvo = (NodeDepend) dplist.get(j);
							if (wesvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("ntp" + id, wesvo.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("ntp" + id, wesvo.getXmlfile())) {
									nodeDependDao.deleteByIdXml("ntp" + id, wesvo.getXmlfile());
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


			// 删除该数据库的采集指标
			NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
			try {
				dao.deleteByNodeIdsAndTypeAndSubtype(ids, "service", "ntp");
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			// 删除该数据库的告警阀值
			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
			try {
				indidao.deleteByNodeIds(ids, "service", "ntp");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				indidao.close();
			}

			NtpConfigDao ntpdao = new NtpConfigDao();
			List ntpblist = new ArrayList();
			try {
				ntpblist = ntpdao.loadAll();
			} catch (Exception e) {

			} finally {
				ntpdao.close();
			}
			if (ntpblist == null)
				ntpblist = new ArrayList();
			ShareData.setNtpconfiglist(ntpblist);
			ntpdao.clearRubbish(ntpblist);
		}
		return "/ntp.do?action=list&jp=1&flag=" + _flag;
	}
	
	private String addalert() {
		NtpConfig vo = new NtpConfig();

		List list = new ArrayList();
		NtpConfigDao configdao = null;
		try {
			 configdao = new NtpConfigDao();
			vo = (NtpConfig) configdao.findByID(getParaValue("id"));
			vo.setFlag(1);
			configdao.update(vo);
			
			NTPLoader loader = new NTPLoader();
			//WebLoader loader = new WebLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		return "/ntp.do?action=list&jp=1";
	}
	
	private String cancelalert() {
		NtpConfig vo = new NtpConfig();
		//WebConfig vo = new WebConfig();
		
		NtpConfigDao configdao = null;
		//WebConfigDao configdao =null;
		try {
			   configdao = new NtpConfigDao();
				vo = (NtpConfig) configdao.findByID(getParaValue("id"));
		     	vo.setFlag(0);
				configdao.update(vo);
			
//			Web web = (Web) PollingEngine.getInstance().getWebByID(vo.getId());
//			web.setFlag(0);
			NTPLoader loader = new NTPLoader();
			loader.loading();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 configdao.close();
		}
		return "/ntp.do?action=list&jp=1";
	}
	
	private String ready_edit() {
		String jsp = "/application/ntp/edit.jsp";
		NtpConfigDao dao = new NtpConfigDao();
		try {
			setTarget(jsp);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("23"));
			request.setAttribute("timeShareConfigList", timeShareConfigList);
			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("29"));
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
	
	public String ntpevent(){

		
		/////////////
		NtpConfig vo = new NtpConfig();
		NtpConfigDao dao = new NtpConfigDao();
		try{
			String id1=getParaValue("id");
			request.setAttribute("id", id1);
			vo = (NtpConfig)dao.findByID(id1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		DBManager dbmanger = new DBManager();
		String sql = "select * from nms_ntp where ipaddress ='" + vo.getIpAddress() + "'";
		ResultSet rs = dbmanger.executeQuery(sql);
		Hashtable tmp = new Hashtable();
		Hashtable ntpHash = new Hashtable();
		try {
			while(rs.next()){
				tmp = new Hashtable();
				String ipaddress = rs.getString("ipaddress");
				String datetime = rs.getString("datetime");
				String collecttime = rs.getString("collecttime");
				tmp.put("ipaddress", ipaddress);
				tmp.put("datetime", datetime);
				tmp.put("collecttime", collecttime);
				ntpHash.put(ipaddress, tmp);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("ntpHash", ntpHash);
			
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
				EventListDao eventdao = new EventListDao();
				try{
					//list = eventdao.getQuery(starttime1,totime1,"db",status+"",level1+"",user.getBusinessids(),vo.getId());
					list = eventdao.getQuery(starttime1,totime1,"ntp","99","99",user.getBusinessids(),vo.getId());
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
			return "/application/ntp/ntpevent.jsp";
		
	}
}
