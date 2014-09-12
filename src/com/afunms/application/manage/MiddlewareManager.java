/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.ResinDao;
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
import com.afunms.application.model.Resin;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.ApacheLoader;
import com.afunms.polling.loader.CicsLoader;
import com.afunms.polling.loader.DNSLoader;
import com.afunms.polling.loader.DominoLoader;
import com.afunms.polling.loader.IISLoader;
import com.afunms.polling.loader.JBossLoader;
import com.afunms.polling.loader.MqLoader;
import com.afunms.polling.loader.TomcatLoader;
import com.afunms.polling.loader.TuxedoLoader;
import com.afunms.polling.loader.WasLoader;
import com.afunms.polling.loader.WeblogicLoader;
import com.afunms.polling.node.ApachConfig;
import com.afunms.polling.node.Cics;
import com.afunms.polling.node.Domino;
import com.afunms.polling.node.IIS;
import com.afunms.polling.node.MQ;
import com.afunms.polling.node.Was;
import com.afunms.polling.node.Weblogic;
import com.afunms.system.model.User;

public class MiddlewareManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		if ("list".equals(action)) {
			return list();
		} else if ("changeManage".equals(action)) {
			return changeManage();
		} else if ("middlewareRport".equals(action)) {
			return middlewareRport();
		}
		return null;
	}

	public String middlewareRport() {

		List tomcatList = getTomcatList();
		List iisList = getIISList();
		List weblogicList = getWeblogicList();
		List waslist = getWasList();
		request.setAttribute("tomcatList", tomcatList);
		request.setAttribute("iisList", iisList);
		request.setAttribute("weblogicList", weblogicList);
		request.setAttribute("waslist", waslist);
		return "/capreport/tomcat/midWareReport.jsp";
	}

	public String list() {

		List monitorMiddleWareDTOList = new ArrayList();

		String category = getParaValue("category");

		String flag = getParaValue("flag");

		request.setAttribute("flag", flag);

		MonitorMiddlewareDTO monitorMiddleWareDTO = null;

		if ("tomcat".equals(category)) {
			try {
				// Tomcat
				List tomcatList = getTomcatList();
				for (int i = 0; i < tomcatList.size(); i++) {
					Tomcat tomcat = (Tomcat) tomcatList.get(i);
					monitorMiddleWareDTO = getMonitorMiddlewareDTOByTomcat(tomcat);
					monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if ("mq".equals(category)) {
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

		} else if ("domino".equals(category)) {
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
		} else if ("was".equals(category)) {
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
		} else if ("weblogic".equals(category)) {
			try {
				// weblogic
				List weblogicList = getWeblogicList();
				for (int i = 0; i < weblogicList.size(); i++) {
					WeblogicConfig weblogicConfig = (WeblogicConfig) weblogicList.get(i);

					monitorMiddleWareDTO = getMonitorMiddlewareDTOByWeblogic(weblogicConfig);

					monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("jboss".equals(category)) {
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
		} else if ("apache".equals(category)) {
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
		} else if ("tuxedo".equals(category)) {
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

		} else if ("iis".equals(category)) {
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

		} else if ("cics".equals(category)) {
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
		} else if ("dns".equals(category)) {
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
		} else if ("resin".equals(category)) {
			try {
				// resin
				List resinList = getResinList();
				for (int i = 0; i < resinList.size(); i++) {
					Resin resin = (Resin) resinList.get(i);

					monitorMiddleWareDTO = getMonitorMiddlewareDTOByResin(resin);

					monitorMiddleWareDTOList.add(monitorMiddleWareDTO);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			monitorMiddleWareDTOList = getList();

			category = "middleWare";
		}

		request.setAttribute("category", category);

		request.setAttribute("list", monitorMiddleWareDTOList);
		return "/application/middleware/list.jsp";
	}

	public List getList() {

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
				WeblogicConfig weblogicConfig = (WeblogicConfig) weblogicList.get(i);

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
		try {
			List resinList = getResinList();
			for (int i = 0; i < resinList.size(); i++) {
				Resin resin = (Resin) resinList.get(i);

				monitorMiddleWareDTO = getMonitorMiddlewareDTOByResin(resin);

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
			tomcatlist = tomcatdao.findByCondition(getTomcatSql());
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
	 * 获取 Tomcat Sql 语句
	 * 
	 * @return
	 */
	public String getTomcatSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql("bid");

		return sql;
	}

	/**
	 * 获取 Resin Sql 语句
	 * 
	 * @return
	 */
	public String getResinSql() {

		String sql = " where 1=1";

		sql = sql + getMonFlagSql("monflag");

		sql = sql + getBidSql("bid");

		return sql;
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

		Node tomcatNode = PollingEngine.getInstance().getTomcatByID(tomcat.getId());

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tomcatNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
	 * 根据 tomcat 来组装 中间件
	 * 
	 * @param tomcat
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByResin(Resin resin) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());

		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = resin.getId();
		String alias = resin.getAlias();
		String ipAddress = resin.getIpAddress();
		String port = resin.getPort();
		String category = "resin";

		Node resinNode = PollingEngine.getInstance().getResinByID(resin.getId());

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(resinNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		if (resin.getMonflag() == 1) {
			monflag = "是";
		}

		MonitorMiddlewareDTO monitorMiddlewareDTO = new MonitorMiddlewareDTO();
		monitorMiddlewareDTO.setId(id);
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			mqlist = mqconfigdao.findByCondition(getMQSql());
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
	 * 获取 MQ Sql 语句
	 * 
	 * @return
	 */
	public String getMQSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql("netid");

		return sql;
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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mqNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			dominolist = dominoconfigdao.findByCondition(getDominoSql());
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
	 * 获取 Domino Sql 语句
	 * 
	 * @return
	 */
	public String getDominoSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql("netid");

		return sql;
	}

	/**
	 * 根据 dominoConfig 来组装 中间件
	 * 
	 * @param dominoConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByDomino(DominoConfig dominoConfig) {

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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dominoNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			waslist = wasconfigdao.findByCondition(getWasSql());
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
	 * 获取 Was Sql 语句
	 * 
	 * @return
	 */
	public String getWasSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql("netid");

		return sql;
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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(wasNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			weblogiclist = weblogicconfigdao.findByCondition(getWeblogicSql());
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
	 * 获取 Weblogic Sql 语句
	 * 
	 * @return
	 */
	public String getWeblogicSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql("netid");

		return sql;
	}

	/**
	 * 根据 weblogicConfig 来组装 中间件
	 * 
	 * @param weblogicConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByWeblogic(WeblogicConfig weblogicConfig) {

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
		WeblogicConfigDao dao = new WeblogicConfigDao();
		WeblogicConfig config = null;
		try {
			config = (WeblogicConfig) dao.findByID(id + "");
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		if (config != null)
			port = config.getPortnum() + "";
		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(weblogicNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		// int status = 0;
		// try {
		// status = weblogicNode.getStatus();
		// } catch (RuntimeException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			iislist = iisconfigdao.findByCondition(getIISSql());
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
	 * 获取 IIS Sql 语句
	 * 
	 * @return
	 */
	public String getIISSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql("netid");

		return sql;
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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(iisNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			cicslist = cicsconfigdao.findByCondition(getCICSSql());
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
	 * 获取 CICS Sql 语句
	 * 
	 * @return
	 */
	public String getCICSSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql("netid");

		return sql;
	}

	/**
	 * 根据 cicsConfig 来组装 中间件
	 * 
	 * @param cicsConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByCICS(CicsConfig cicsConfig) {

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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(cicsNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			dnslist = dnsconfigdao.findByCondition(getDNSSql());
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
	 * 获取 Resin 列表
	 * 
	 * @return
	 */
	public List getResinList() {
		// Resin
		ResinDao resindao = new ResinDao();
		List resinlist = null;
		try {
			resinlist = resindao.findByCondition(getResinSql());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resindao.close();
		}

		if (resinlist == null) {
			resinlist = new ArrayList();
		}

		return resinlist;
	}

	/**
	 * 获取 DNS Sql 语句
	 * 
	 * @return
	 */
	public String getDNSSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql("netid");

		return sql;
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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dnsNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			jbosslist = jbossConfigDao.findByCondition(getJBossSql());
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
	 * 获取 JBoss Sql 语句
	 * 
	 * @return
	 */
	public String getJBossSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql("netid");

		return sql;
	}

	/**
	 * 根据 JBoss 来组装 中间件
	 * 
	 * @param jBossConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByJBoss(JBossConfig jBossConfig) {

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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(jbossNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			apachelist = apacheConfigDao.findByCondition(getApacheSql());
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
	 * 获取 Apache Sql 语句
	 * 
	 * @return
	 */
	public String getApacheSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("flag");

		sql = sql + getBidSql("netid");

		return sql;
	}

	/**
	 * 根据 ApacheConfig 来组装 中间件
	 * 
	 * @param apacheConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByApache(ApacheConfig apacheConfig) {

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

		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/* ===========for status start================== */
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(apacheNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			// System.out.println(chexkname);
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}

		Hashtable eventListSummary = new Hashtable();

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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
		monitorMiddlewareDTO.setStatus(String.valueOf(alarmLevel));
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
			tuxedolist = tuxedoConfigDao.findByCondition(getTuxedoSql());
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
	 * 获取 Tuxedo Sql 语句
	 * 
	 * @return
	 */
	public String getTuxedoSql() {
		String sql = " where 1=1";

		sql = sql + getMonFlagSql("mon_flag");

		sql = sql + getBidSql("bid");

		return sql;
	}

	/**
	 * 根据 tuxedoConfig 来组装 中间件
	 * 
	 * @param tuxedoConfig
	 * @return
	 */
	public MonitorMiddlewareDTO getMonitorMiddlewareDTOByTuxedo(TuxedoConfig tuxedoConfig) {

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
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS' )and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}
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

	/**
	 * 获得业务权限的 SQL 语句
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 * @return
	 */
	public String getBidSql(String fieldName) {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		String sql = "";
		StringBuffer s = new StringBuffer();
		if (current_user.getRole() != 0) {
			int _flag = 0;
			if (current_user.getBusinessids() != null) {
				if (current_user.getBusinessids() != "-1") {
					String[] bids = current_user.getBusinessids().split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (_flag == 0) {
									s.append(" and ( " + fieldName + " like '%," + bids[i].trim() + ",%' ");
									_flag = 1;
								} else {
									// flag = 1;
									s.append(" or " + fieldName + " like '%," + bids[i].trim() + ",%' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
		}

		sql = s.toString();

		String treeBid = request.getParameter("treeBid");
		if (treeBid != null && treeBid.trim().length() > 0) {
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if (treeBids != null) {
				for (int i = 0; i < treeBids.length; i++) {
					if (treeBids[i].trim().length() > 0) {
						sql = sql + " and " + fieldName + " like '%," + treeBids[i].trim() + ",%'";
					}
				}
			}
		}
		// SysLogger.info("select * from topo_host_node where managed=1 "+sql);
		return sql;
	}

	public String getMonFlagSql(String fieldName) {
		String mon_flag = getParaValue("mon_flag");

		String sql = "";
		if (mon_flag != null && "1".equals(mon_flag)) {
			sql = " and " + fieldName + "='1'";
		}

		request.setAttribute("mon_flag", mon_flag);

		return sql;
	}

	public String changeManage() {

		String category = getParaValue("type");
		if ("tomcat".equals(category)) {
			changeTomcatManage();
		} else if ("mq".equals(category)) {
			changeMqManage();
		} else if ("domino".equals(category)) {
			changeDominoManage();
		} else if ("was".equals(category)) {
			changeWasManage();
		} else if ("weblogic".equals(category)) {
			changeWeblogicManage();
		} else if ("jboss".equals(category)) {
			changeJbossManage();
		} else if ("apache".equals(category)) {
			changeApacheManage();
		} else if ("tuxedo".equals(category)) {
			changeTuxedoManage();
		} else if ("iis".equals(category)) {
			changeIISManage();
		} else if ("cics".equals(category)) {
			changeCicsManage();
		} else if ("dns".equals(category)) {
			changeDnsManage();
		}

		return list();
	}

	public void changeTomcatManage() {
		TomcatDao tomcatDao = new TomcatDao();
		Tomcat vo = null;
		int monflag = getParaIntValue("value");
		try {
			vo = (Tomcat) tomcatDao.findByID(getParaValue("id"));
			vo.setMonflag(monflag);
			// MQ mq = (MQ)PollingEngine.getInstance().getMqByID(vo.getId());
			// mq.setMon_flag(monflag);

			// com.afunms.polling.node.Tomcat tomcat =
			// (com.afunms.polling.node.Tomcat)PollingEngine.getInstance().getTomcatByID(vo.getId());
			//		
			// tomcat.setMonflag(monflag);
			// if(monflag == 1){
			// tomcat.setManaged(true);
			// }else{
			// tomcat.setManaged(false);
			// }

			tomcatDao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatDao.close();
		}
		TomcatLoader loader = new TomcatLoader();
		loader.loading();
	}

	public void changeMqManage() {
		MQConfigDao configdao = new MQConfigDao();
		MQConfig vo = new MQConfig();
		try {
			int monflag = getParaIntValue("value");
			vo = (MQConfig) configdao.findByID(getParaValue("id"));
			vo.setMon_flag(monflag);
			// MQ mq = (MQ)PollingEngine.getInstance().getMqByID(vo.getId());
			// mq.setMon_flag(monflag);

			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		MqLoader loader = new MqLoader();
		loader.loading();
	}

	public void changeDominoManage() {
		DominoConfig vo = new DominoConfig();
		try {
			DominoConfigDao configdao = new DominoConfigDao();
			try {
				int monflag = getParaIntValue("value");
				vo = (DominoConfig) configdao.findByID(getParaValue("id"));
				vo.setMon_flag(monflag);
				Domino domino = (Domino) PollingEngine.getInstance().getDominoByID(vo.getId());
				domino.setMon_flag(monflag);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			configdao = new DominoConfigDao();
			try {
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		DominoLoader loader = new DominoLoader();
		loader.loading();
	}

	public void changeWasManage() {
		WasConfig vo = new WasConfig();
		try {
			WasConfigDao configdao = new WasConfigDao();
			try {
				vo = (WasConfig) configdao.findByID(getParaValue("id"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			int monflag = getParaIntValue("value");
			vo.setMon_flag(monflag);

			Was was = (Was) PollingEngine.getInstance().getWasByID(vo.getId());
			was.setMon_flag(monflag);
			if (monflag == 1) {
				was.setManaged(true);
			} else {
				was.setManaged(false);
			}
			configdao = new WasConfigDao();
			try {
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		WasLoader loader = new WasLoader();
		loader.loading();

	}

	public void changeWeblogicManage() {
		WeblogicConfig vo = new WeblogicConfig();
		WeblogicConfigDao configdao = null;
		try {
			configdao = new WeblogicConfigDao();
			vo = (WeblogicConfig) configdao.findByID(getParaValue("id"));
			int monflag = getParaIntValue("value");
			vo.setMon_flag(monflag);

			// Weblogic weblogic =
			// (Weblogic)PollingEngine.getInstance().getWeblogicByID(vo.getId());
			// weblogic.setMon_flag(monflag);
			// if(monflag == 1){
			// weblogic.setManaged(true);
			// }else{
			// weblogic.setManaged(false);
			// }
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		WeblogicLoader loader = new WeblogicLoader();
		loader.loading();
	}

	public void changeJbossManage() {
		JBossConfig vo = new JBossConfig();
		JBossConfigDao configdao = new JBossConfigDao();
		try {
			vo = (JBossConfig) configdao.findByID(getParaValue("id"));

			int monflag = getParaIntValue("value");
			vo.setFlag(monflag);

			// com.afunms.polling.node.JBossConfig jbossConfig =
			// (com.afunms.polling.node.JBossConfig)PollingEngine.getInstance().getJBossByID(vo.getId());
			// jbossConfig.setFlag(monflag);
			// if(monflag == 1){
			// jbossConfig.setManaged(true);
			// }else {
			// jbossConfig.setManaged(false);
			// }

			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		JBossLoader loader = new JBossLoader();
		loader.loading();
	}

	public void changeApacheManage() {
		ApacheConfig vo = new ApacheConfig();
		ApacheConfigDao configdao = new ApacheConfigDao();
		try {
			vo = (ApacheConfig) configdao.findByID(getParaValue("id"));
			int monflag = getParaIntValue("value");
			vo.setFlag(monflag);
			// ApachConfig apachConfig = (ApachConfig)
			// PollingEngine.getInstance().getApacheByID(vo.getId());
			// apachConfig.setFlag(monflag);
			// if(monflag == 1){
			// apachConfig.setManaged(true);
			// }else{
			// apachConfig.setManaged(false);
			// }

			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		ApacheLoader loader = new ApacheLoader();
		loader.loading();
	}

	public void changeTuxedoManage() {
		String id = getParaValue("id");
		String mon_flag = getParaValue("value");

		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			tuxedoConfigDao.updateMon_flagById(id, mon_flag);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			tuxedoConfigDao.close();
		}
		TuxedoLoader loader = new TuxedoLoader();
		loader.loading();

	}

	public void changeIISManage() {
		IISConfig vo = new IISConfig();
		IISConfigDao configdao = new IISConfigDao();
		try {
			vo = (IISConfig) configdao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		int monflag = getParaIntValue("value");
		vo.setMon_flag(monflag);

		IIS iis = (IIS) PollingEngine.getInstance().getIisByID(vo.getId());
		iis.setMon_flag(monflag);

		if (monflag == 1) {
			iis.setManaged(true);
		} else {
			iis.setManaged(false);
		}

		configdao = new IISConfigDao();
		try {
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		IISLoader loader = new IISLoader();
		loader.loading();
	}

	public void changeCicsManage() {
		CicsConfig vo = new CicsConfig();

		CicsConfigDao configdao = new CicsConfigDao();
		try {
			vo = (CicsConfig) configdao.findByID(getParaValue("id"));

			int monflag = getParaIntValue("value");
			vo.setFlag(monflag);
			// Cics cics =
			// (Cics)PollingEngine.getInstance().getCicsByID(vo.getId());
			// cics.setFlag(monflag);
			// if(monflag == 1 ){
			// cics.setManaged(true);
			// }else {
			// cics.setManaged(false);
			// }

			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}

		CicsLoader loader = new CicsLoader();
		loader.loading();
	}

	public void changeDnsManage() {
		DnsConfig vo = new DnsConfig();
		DnsConfigDao configdao = new DnsConfigDao();
		try {
			vo = (DnsConfig) configdao.findByID(getParaValue("id"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		int monflag = getParaIntValue("value");
		vo.setFlag(monflag);

		try {
			configdao = new DnsConfigDao();
			configdao.update(vo);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		DNSLoader loader = new DNSLoader();
		loader.loading();
	}

}