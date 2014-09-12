package com.afunms.config.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.EnvConfigDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.dao.PowerConfigDao;
import com.afunms.config.model.EnvConfig;
import com.afunms.config.model.Portconfig;
import com.afunms.config.model.PowerConfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.temp.dao.CommonTempDao;
import com.afunms.temp.dao.EnvironmentTempDao;
import com.afunms.temp.model.NodeTemp;

/**
 * @description 动力环境告警配置
 * @author wangxiangyong
 * @date Dec 28, 2011
 */
public class EnvConfigManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// 电源列表
		if (action.equals("nodepowerlist")) {
			return nodepowerlist();
		}
		// 风扇列表
		if (action.equals("nodefanlist")) {
			return nodefanlist();
		}
		if (action.equals("fromNodeLasttoconfig")) {
			return fromNodeLasttoconfig();
		}
		if (action.equals("fromNodeFanConfig")) {
			return fromNodeFanConfig();
		}
		if (action.equals("readyEdit")) {
			return readyEdit();
		}
		if (action.equals("updateValue")) {
			return updateValue();
		}
		return null;
	}

	private String nodepowerlist() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		EnvConfigDao dao = new EnvConfigDao();
		request.setAttribute("id", id);
		List list = new ArrayList();
		try {
			list = dao.findByCondition(" where ipaddress='" + ipaddress + "' and entity='Power'");
		} catch (Exception e) {

		} finally {
			dao.close();
		}

		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "/config/envconfig/nodepowerlist.jsp";
	}

	private String nodefanlist() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		EnvConfigDao dao = new EnvConfigDao();
		request.setAttribute("id", id);
		List list = new ArrayList();
		try {
			list = dao.findByCondition(" where ipaddress='" + ipaddress + "' and entity='Fan'");
		} catch (Exception e) {

		} finally {
			dao.close();
		}

		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "/config/envconfig/nodefanlist.jsp";
	}

	private String fromNodeLasttoconfig() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");

		EnvironmentTempDao tempDao = new EnvironmentTempDao();
		List<NodeTemp> tempList = null;
		Vector<Interfacecollectdata> tempVec = null;
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if ("1".equals(runmodel)) {//分离模式
			tempList = tempDao.findByCondition(" where ip='" + ipaddress + "' and entity='Power'");
		} else {//集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipaddress);
			if(ipAllData!=null&&ipAllData.containsKey("power")){
			tempVec = (Vector<Interfacecollectdata>) ipAllData.get("power");
			}
		}
		List list = new ArrayList();
		StringBuffer sqlBuffer = null;
		EnvConfigDao dao = null;
		DBManager db = null;

		if ("1".equals(runmodel)) {
			if (tempList != null && tempList.size() > 0) {
				try {

					db = new DBManager();
					dao = new EnvConfigDao();
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("delete from system_envconfig where entity='Power' and ipaddress='");
					sqlBuffer.append(ipaddress);
					sqlBuffer.append("'");
					db.addBatch(sqlBuffer.toString());
					for (int i = 0; i < tempList.size(); i++) {
						NodeTemp nodeTemp = tempList.get(i);
						sqlBuffer = new StringBuffer();
						sqlBuffer.append("insert into system_envconfig(ipaddress,name,alarmvalue,alarmlevel,alarmtimes,entity,enabled,bak) values(");
						sqlBuffer.append("'");
						sqlBuffer.append(nodeTemp.getIp());
						sqlBuffer.append("','");
						sqlBuffer.append(nodeTemp.getSubentity());
						sqlBuffer.append("',");
						sqlBuffer.append(1);
						sqlBuffer.append(",'");
						sqlBuffer.append(1);
						sqlBuffer.append("',");
						sqlBuffer.append(3);
						sqlBuffer.append(",'");
						sqlBuffer.append(nodeTemp.getEntity());
						sqlBuffer.append("',");
						sqlBuffer.append(0);
						sqlBuffer.append(",'");
						sqlBuffer.append("电源模块告警配置");
						sqlBuffer.append("')");
						db.addBatch(sqlBuffer.toString());
					}
					db.executeBatch();
					list = dao.findByCondition(" where entity='Power' and  ipaddress='" + ipaddress + "'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.close();
					dao.close();
				}
			}
		} else {
			if (tempVec != null && tempVec.size()> 0) {

				try {

					db = new DBManager();
					dao = new EnvConfigDao();
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("delete from system_envconfig where entity='Power' and ipaddress='");
					sqlBuffer.append(ipaddress);
					sqlBuffer.append("'");
					db.addBatch(sqlBuffer.toString());
					for (int i = 0; i < tempVec.size(); i++) {
						Interfacecollectdata data = tempVec.get(i);
						sqlBuffer = new StringBuffer();
						sqlBuffer.append("insert into system_envconfig(ipaddress,name,alarmvalue,alarmlevel,alarmtimes,entity,enabled,bak) values(");
						sqlBuffer.append("'");
						sqlBuffer.append(data.getIpaddress());
						sqlBuffer.append("','");
						sqlBuffer.append(data.getEntity());
						sqlBuffer.append("',");
						sqlBuffer.append(1);
						sqlBuffer.append(",'");
						sqlBuffer.append(1);
						sqlBuffer.append("',");
						sqlBuffer.append(3);
						sqlBuffer.append(",'");
						sqlBuffer.append(data.getCategory());
						sqlBuffer.append("',");
						sqlBuffer.append(0);
						sqlBuffer.append(",'");
						sqlBuffer.append("电源模块告警配置");
						sqlBuffer.append("')");
						db.addBatch(sqlBuffer.toString());
					}
					db.executeBatch();
					list = dao.findByCondition(" where entity='Power' and  ipaddress='" + ipaddress + "'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.close();
					dao.close();
				}

			}
		}

		request.setAttribute("id", id);
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "/config/envconfig/nodepowerlist.jsp";
	}

	private String fromNodeFanConfig() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		String allipstr = SysUtil.doip(ipaddress);
		String tablename = "fan" + allipstr;
		CommonTempDao tempDao = new CommonTempDao(tablename);
		List<NodeTemp> tempList =null;
		Vector<Interfacecollectdata> tempVec = null;
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if ("1".equals(runmodel)) {
			tempList = tempDao.findByCondition(" where ipaddress='" + ipaddress + "' and category='Fan' group by entity");
		} else {
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipaddress);
			if(ipAllData!=null&&ipAllData.containsKey("fan")){
			 tempVec = (Vector<Interfacecollectdata>) ipAllData.get("fan");
			}
		}
		List list = new ArrayList();
		DBManager db=null;
		EnvConfigDao dao =null;
		if ("1".equals(runmodel)) {//分离模式
		if (tempList != null&&tempList.size()>0) {
			StringBuffer sqlBuffer = null;
			 db = new DBManager();
			 dao = new EnvConfigDao();
			try {
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("delete from system_envconfig where entity='Fan' and ipaddress='");
				sqlBuffer.append(ipaddress);
				sqlBuffer.append("'");
				db.addBatch(sqlBuffer.toString());
				for (int i = 0; i < tempList.size(); i++) {
					NodeTemp nodeTemp = tempList.get(i);
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into system_envconfig(ipaddress,name,alarmvalue,alarmlevel,alarmtimes,entity,enabled,bak) values(");
					sqlBuffer.append("'");
					sqlBuffer.append(nodeTemp.getIp());
					sqlBuffer.append("','");
					sqlBuffer.append(nodeTemp.getEntity());
					sqlBuffer.append("',");
					sqlBuffer.append(1);
					sqlBuffer.append(",'");
					sqlBuffer.append(1);
					sqlBuffer.append("',");
					sqlBuffer.append(3);
					sqlBuffer.append(",'");
					sqlBuffer.append(nodeTemp.getType());
					sqlBuffer.append("',");
					sqlBuffer.append(0);
					sqlBuffer.append(",'");
					sqlBuffer.append("风扇模块告警配置");
					sqlBuffer.append("')");
					db.addBatch(sqlBuffer.toString());
				}
				db.executeBatch();
				list = dao.findByCondition(" where entity='Fan' and ipaddress='" + ipaddress + "'");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
				dao.close();
			}
		}
		}else {//集成模式
			if (tempVec != null&&tempVec.size()>0) {
				StringBuffer sqlBuffer = null;
				 db = new DBManager();
				 dao = new EnvConfigDao();
				try {
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("delete from system_envconfig where entity='Fan' and ipaddress='");
					sqlBuffer.append(ipaddress);
					sqlBuffer.append("'");
					db.addBatch(sqlBuffer.toString());
					for (int i = 0; i < tempVec.size(); i++) {
						Interfacecollectdata nodeTemp = tempVec.get(i);
						sqlBuffer = new StringBuffer();
						sqlBuffer.append("insert into system_envconfig(ipaddress,name,alarmvalue,alarmlevel,alarmtimes,entity,enabled,bak) values(");
						sqlBuffer.append("'");
						sqlBuffer.append(nodeTemp.getIpaddress());
						sqlBuffer.append("','");
						sqlBuffer.append(nodeTemp.getEntity());
						sqlBuffer.append("',");
						sqlBuffer.append(1);
						sqlBuffer.append(",'");
						sqlBuffer.append(1);
						sqlBuffer.append("',");
						sqlBuffer.append(3);
						sqlBuffer.append(",'");
						sqlBuffer.append(nodeTemp.getCategory());
						sqlBuffer.append("',");
						sqlBuffer.append(0);
						sqlBuffer.append(",'");
						sqlBuffer.append("风扇模块告警配置");
						sqlBuffer.append("')");
						db.addBatch(sqlBuffer.toString());
					}
					db.executeBatch();
					list = dao.findByCondition(" where entity='Fan' and ipaddress='" + ipaddress + "'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.close();
					dao.close();
				}
			}
		}
		request.setAttribute("id", id);
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "/config/envconfig/nodefanlist.jsp";
	}

	private String readyEdit() {
		EnvConfigDao dao = new EnvConfigDao();
		EnvConfig vo = new EnvConfig();
		String entity = getParaValue("entity");
		try {
			vo = (EnvConfig) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		String url = "";
		if (entity != null) {
			if (entity.equals("power")) {
				url = "/config/envconfig/edit.jsp";
			} else if (entity.equals("fan")) {
				url = "/config/envconfig/fanEdit.jsp";
			}
		}
		return url;
	}

	private String updateValue() {
		int id = getParaIntValue("id");
		int alarmValue = getParaIntValue("alarmvalue");
		int alarmTimes = getParaIntValue("alarmtimes");
		EnvConfigDao dao = new EnvConfigDao();
		boolean flag = false;
		try {
			flag = dao.updateValue(id, alarmValue, alarmTimes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return null;
	}

}
