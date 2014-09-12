package com.afunms.application.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.TuxedoConfigDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.MonitorMiddlewareDTO;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostLastCollectDataManager;

public class AssetHelper {
	public List getMidwareList() {

		List monitorMiddleWareDTOList = new ArrayList();
		MonitorMiddlewareDTO monitorMiddleWareDTO = null;

		try {
			// Tomcat
			List tomcatList = getTomcatList();
			for (int i = 0; i < tomcatList.size(); i++) {
				Tomcat tomcat = (Tomcat) tomcatList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByTomcat(tomcat);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// MQ
			List mqList = getMQList();
			for (int i = 0; i < mqList.size(); i++) {
				MQConfig mqConfig = (MQConfig) mqList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByMQ(mqConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// Domino
			List dominoList = getDominoList();
			for (int i = 0; i < dominoList.size(); i++) {
				DominoConfig dominoConfig = (DominoConfig) dominoList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByDomino(dominoConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// WAS
			List waslist = getWasList();
			for (int i = 0; i < waslist.size(); i++) {
				WasConfig wasConfig = (WasConfig) waslist.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByWas(wasConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// weblogic
			List weblogicList = getWeblogicList();
			for (int i = 0; i < weblogicList.size(); i++) {
				WeblogicConfig weblogicConfig = (WeblogicConfig) weblogicList
						.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByWeblogic(weblogicConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// JBoss
			List jbossList = getJBossList();
			for (int i = 0; i < jbossList.size(); i++) {
				JBossConfig jBossConfig = (JBossConfig) jbossList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByJBoss(jBossConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// apache
			List apacheList = getApacheList();
			for (int i = 0; i < apacheList.size(); i++) {
				ApacheConfig apacheConfig = (ApacheConfig) apacheList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByApache(apacheConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// tuxedo
			List tuxedoList = getTuxedoList();
			for (int i = 0; i < tuxedoList.size(); i++) {
				TuxedoConfig tuxedoConfig = (TuxedoConfig) tuxedoList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByTuxedo(tuxedoConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// IIS
			List iisList = getIISList();
			for (int i = 0; i < iisList.size(); i++) {
				IISConfig iISConfig = (IISConfig) iisList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByIIS(iISConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// cics
			List cicsList = getCICSList();
			for (int i = 0; i < cicsList.size(); i++) {
				CicsConfig cicsConfig = (CicsConfig) cicsList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByCICS(cicsConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// dns
			List dnsList = getDNSList();
			for (int i = 0; i < dnsList.size(); i++) {
				DnsConfig dnsConfig = (DnsConfig) dnsList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByDNS(dnsConfig);

				monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return monitorMiddleWareDTOList;

	}

	/**
	 * 获取 Tomcat 列表
	 * 
	 * @return
	 */
	public List getTomcatList() {
		// Tomcat

		List tomcatlist = null;

		TomcatDao tomcatdao = new TomcatDao();
		try {
			tomcatlist = tomcatdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatdao.close();
		}

		if (tomcatlist == null) {
			tomcatlist = new ArrayList();
		}

		return tomcatlist;
	}

	/**
	 * 根据 tomcat 来组装 中间件
	 * 
	 * @param tomcat
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByTomcat(Tomcat tomcat) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = tomcat.getId();
		String alias = tomcat.getAlias();
		String ipAddress = tomcat.getIpAddress();
		String port = tomcat.getPort();
		String category = "tomcat";

		Node tomcatNode = PollingEngine.getInstance().getTomcatByID(
				tomcat.getId());

		int status = 0;
		try {
			status = tomcatNode.getStatus();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (tomcat.getMonflag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 MQ 列表
	 * 
	 * @return
	 */
	public List getMQList() {
		// mq
		MQConfigDao mqconfigdao = new MQConfigDao();
		List mqlist = null;
		try {
			mqlist = mqconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqconfigdao.close();
		}

		if (mqlist == null) {
			mqlist = new ArrayList();
		}

		return mqlist;
	}

	/**
	 * 根据 mqConfig 来组装 中间件
	 * 
	 * @param mqConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByMQ(MQConfig mqConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = mqConfig.getId();
		String alias = mqConfig.getName();
		String ipAddress = mqConfig.getIpaddress();
		String port = String.valueOf(mqConfig.getPortnum());
		String category = "mq";

		Node mqNode = PollingEngine.getInstance().getMqByID(id);

		int status = 0;
		try {
			status = mqNode.getStatus();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (mqConfig.getMon_flag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 Domino 列表
	 * 
	 * @return
	 */
	public List getDominoList() {
		// domimo
		DominoConfigDao dominoconfigdao = new DominoConfigDao();
		List dominolist = new ArrayList();
		try {
			dominolist = dominoconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dominoconfigdao.close();
		}

		if (dominolist == null) {
			dominolist = new ArrayList();
		}

		return dominolist;
	}

	/**
	 * 根据 dominoConfig 来组装 中间件
	 * 
	 * @param dominoConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByDomino(
			DominoConfig dominoConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = dominoConfig.getId();
		String alias = dominoConfig.getName();
		String ipAddress = dominoConfig.getIpaddress();
		String port = "";
		String category = "domino";

		Node dominoNode = PollingEngine.getInstance().getDominoByID(id);

		int status = 0;
		try {
			status = dominoNode.getStatus();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (dominoConfig.getMon_flag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;

	}

	/**
	 * 获取 Was 列表
	 * 
	 * @return
	 */
	public List getWasList() {
		// was
		WasConfigDao wasconfigdao = new WasConfigDao();
		List waslist = null;
		try {
			waslist = wasconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wasconfigdao.close();
		}

		if (waslist == null) {
			waslist = new ArrayList();
		}

		return waslist;
	}

	/**
	 * 根据 wasConfig 来组装 中间件
	 * 
	 * @param wasConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByWas(WasConfig wasConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = wasConfig.getId();
		String alias = wasConfig.getName();
		String ipAddress = wasConfig.getIpaddress();
		String port = "";
		String category = "was";

		Node wasNode = PollingEngine.getInstance().getWasByID(id);

		int status = 0;
		try {
			status = wasNode.getStatus();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (wasConfig.getMon_flag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 Weblogic 列表
	 * 
	 * @return
	 */
	public List getWeblogicList() {
		// weblogic
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		List weblogiclist = null;
		try {
			weblogiclist = weblogicconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			weblogicconfigdao.close();
		}
		if (weblogiclist == null) {
			weblogiclist = new ArrayList();
		}
		return weblogiclist;
	}

	/**
	 * 根据 weblogicConfig 来组装 中间件
	 * 
	 * @param weblogicConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByWeblogic(
			WeblogicConfig weblogicConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = weblogicConfig.getId();
		String alias = weblogicConfig.getAlias();
		String ipAddress = weblogicConfig.getIpAddress();
		String port = "";
		String category = "weblogic";

		Node weblogicNode = PollingEngine.getInstance().getWeblogicByID(id);

		int status = 0;
		try {
			status = weblogicNode.getStatus();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (weblogicConfig.getMon_flag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 IIS 列表
	 * 
	 * @return
	 */
	public List getIISList() {
		// IIS
		IISConfigDao iisconfigdao = new IISConfigDao();
		List iislist = new ArrayList();
		try {
			iislist = iisconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			iisconfigdao.close();
		}
		if (iislist == null) {
			iislist = new ArrayList();
		}

		return iislist;
	}

	/**
	 * 根据 IISConfig 来组装 中间件
	 * 
	 * @param iISConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByIIS(IISConfig iISConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = iISConfig.getId();
		String alias = iISConfig.getName();
		String ipAddress = iISConfig.getIpaddress();
		String port = "";
		String category = "iis";

		Node iisNode = PollingEngine.getInstance().getIisByID(id);

		int status = 0;
		try {
			status = iisNode.getStatus();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (iISConfig.getMon_flag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 CICS 列表
	 * 
	 * @return
	 */
	public List getCICSList() {
		// CICS
		List cicslist = new ArrayList();
		CicsConfigDao cicsconfigdao = new CicsConfigDao();
		try {
			cicslist = cicsconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cicsconfigdao.close();
		}

		if (cicslist == null) {
			cicslist = new ArrayList();
		}

		return cicslist;
	}

	/**
	 * 根据 cicsConfig 来组装 中间件
	 * 
	 * @param cicsConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByCICS(
			CicsConfig cicsConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = cicsConfig.getId();
		String alias = cicsConfig.getAlias();
		String ipAddress = cicsConfig.getIpaddress();
		String port = "";
		String category = "cics";

		Node cicsNode = PollingEngine.getInstance().getCicsByID(id);

		int status = 0;
		try {
			status = cicsNode.getStatus();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (cicsConfig.getFlag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 DNS 列表
	 * 
	 * @return
	 */
	public List getDNSList() {
		// DNS
		DnsConfigDao dnsconfigdao = new DnsConfigDao();
		List dnslist = null;
		try {
			dnslist = dnsconfigdao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dnsconfigdao.close();
		}

		if (dnslist == null) {
			dnslist = new ArrayList();
		}

		return dnslist;
	}

	/**
	 * 根据 DNS 来组装 中间件
	 * 
	 * @param dnsConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByDNS(DnsConfig dnsConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = dnsConfig.getId();
		String alias = dnsConfig.getDns();
		String ipAddress = dnsConfig.getDnsip();
		String port = "";
		String category = "dns";

		Node dnsNode = PollingEngine.getInstance().getDnsByID(id);

		int status = 0;
		try {
			// status = dnsNode.getStatus();
			status = dnsConfig.getFlag();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (dnsConfig.getFlag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 JBoss 列表
	 * 
	 * @return
	 */
	public List getJBossList() {
		// JBoss
		JBossConfigDao jbossConfigDao = new JBossConfigDao();
		List jbosslist = null;
		try {
			jbosslist = jbossConfigDao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jbossConfigDao.close();
		}

		if (jbosslist == null) {
			jbosslist = new ArrayList();
		}

		return jbosslist;
	}

	/**
	 * 根据 JBoss 来组装 中间件
	 * 
	 * @param jBossConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByJBoss(
			JBossConfig jBossConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = jBossConfig.getId();
		String alias = jBossConfig.getAlias();
		String ipAddress = jBossConfig.getIpaddress();
		String port = "";
		String category = "jboss";

		Node jbossNode = PollingEngine.getInstance().getJBossByID(id);

		int status = 0;
		try {
			status = jbossNode.getStatus();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (jBossConfig.getFlag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 Apache 列表
	 * 
	 * @return
	 */
	public List getApacheList() {
		// Apache
		ApacheConfigDao apacheConfigDao = new ApacheConfigDao();
		List apachelist = null;
		try {
			apachelist = apacheConfigDao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			apacheConfigDao.close();
		}

		if (apachelist == null) {
			apachelist = new ArrayList();
		}

		return apachelist;
	}

	/**
	 * 根据 ApacheConfig 来组装 中间件
	 * 
	 * @param apacheConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByApache(
			ApacheConfig apacheConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = apacheConfig.getId();
		String alias = apacheConfig.getAlias();
		String ipAddress = apacheConfig.getIpaddress();
		String port = "";
		String category = "apache";

		Node apacheNode = PollingEngine.getInstance().getApacheByID(id);

		int status = 0;
		try {
			status = apacheNode.getStatus();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if (apacheConfig.getFlag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	/**
	 * 获取 Tuxedo 列表
	 * 
	 * @return
	 */
	public List getTuxedoList() {
		// Tuxedo
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		List tuxedolist = null;
		try {
			tuxedolist = tuxedoConfigDao.findByCondition("");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tuxedoConfigDao.close();
		}

		if (tuxedolist == null) {
			tuxedolist = new ArrayList();
		}

		return tuxedolist;
	}

	/**
	 * 根据 tuxedoConfig 来组装 中间件
	 * 
	 * @param tuxedoConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByTuxedo(
			TuxedoConfig tuxedoConfig) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = tuxedoConfig.getId();
		String alias = tuxedoConfig.getName();
		String ipAddress = tuxedoConfig.getIpAddress();
		String port = "";
		String category = "tuxedo";
		// Node jbossNode = PollingEngine.getInstance().gett;

		String status = tuxedoConfig.getStatus();

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='1' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='2' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
			seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id
					+ "'" + " and level1='3' and recordtime>='" + starttime
					+ "' and recordtime<='" + totime + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		String monflag = "否";
		if ("1".equals(tuxedoConfig.getMon_flag())) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(status));
		monitorMiddlewareDTO.setAlias(alias);
		monitorMiddlewareDTO.setIpAddress(ipAddress);
		monitorMiddlewareDTO.setPort(port);
		monitorMiddlewareDTO.setPingValue("");
		monitorMiddlewareDTO.setCategory(category);
		monitorMiddlewareDTO.setEventListSummary(eventListSummary);
		monitorMiddlewareDTO.setMonflag(monflag);
		return monitorMiddlewareDTO;
	}

	public List<StatisNumer> getAssetList(String ip, String type) {
		List<StatisNumer> list = new ArrayList<StatisNumer>();
		String runmodel = PollingEngine.getCollectwebflag();

		Hashtable diskhash = new Hashtable();
		Hashtable memhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		if ("0".equals(runmodel)) {
			try {
				if (type.equals("Memory")) {
					memhash = hostlastmanager.getMemory_share(ip, type, null,
							null);
				}
				if (type.equals("Disk")) {
					diskhash = hostlastmanager.getDisk_share(ip, type, null,
							null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (type.equals("Memory")) {
					memhash = hostlastmanager.getMemory(ip, type, null, null);
				}
				if (type.equals("Disk")) {
					diskhash = hostlastmanager.getDisk(ip, type, null, null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (type.equals("Disk")) {
			if (diskhash != null && diskhash.size() > 0) {
				// 写磁盘

				for (int j = 0; j < diskhash.size(); j++) {
					Hashtable diskhash1 = (Hashtable) (diskhash
							.get(new Integer(j)));
					String name = (String) diskhash1.get("name");
					String value = (String) diskhash1.get("AllSize");
					StatisNumer voNumer = new StatisNumer();
					voNumer.setName(name);
					voNumer.setCurrent(value);
					list.add(voNumer);
				}

			}
		} else if (type.equals("Memory")) {
			if (memhash != null && memhash.size() > 0) {
				for (int k = 0; k < memhash.size(); k++) {
					Hashtable mhash = (Hashtable) (memhash.get(new Integer(k)));
					String name = (String) mhash.get("name");
					if (mhash.get("Capability") != null) {
						String value = (String) mhash.get("Capability");
						if ("PhysicalMemory".equals(name)) {
							name = "物理内存总量";
						} else {
							name = "虚拟内存总量";
						}
						StatisNumer voNumer = new StatisNumer();
						voNumer.setName(name);
						voNumer.setCurrent(value);
						list.add(voNumer);
					}
				}
			}
		}
		return list;
	}
}
