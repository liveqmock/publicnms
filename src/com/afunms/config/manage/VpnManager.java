package com.afunms.config.manage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.VpnCfgFileDao;
import com.afunms.config.dao.VpnDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.Vpn;
import com.afunms.config.model.VpnCfgCmdFile;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.slaaudit.dao.SlaAuditDao;
import com.afunms.slaaudit.model.SlaAudit;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;

public class VpnManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		} else if (action.equals("ready_add")) {
			return ready_add();
		} else if (action.equals("multi_vpn_netip")) {
			return multi_vpn_netip();
		} else if (action.equals("add")) {
			return add();
		} else if (action.equals("delete")) {
			return delete();
		} else if (action.equals("choosePort")) {
			return choosePort();
		} else if (action.equals("ready_edit")) {
			return ready_edit();
		} else if (action.equals("edit")) {
			return edit();
		} else if (action.equals("showmap")) {
			return showmap();
		} else if (action.equals("saveCmdCfg")) {
			return saveCmdCfg();
		} else if (action.equals("showScript")) {
			return showScript();
		} else if (action.equals("vpnAudit")) {
			return vpnAudit();
		} else if (action.equals("auditList")) {
			return auditList();
		} else if (action.equals("read")) {
			return read();
		} else if (action.equals("loadFileFromMenu")) {
			return loadFileFromMenu();
		} else if (action.equals("saveVpnFile")) {
			return saveVpnFile();
		} else if (action.equals("saveVpnCmdCfg")) {
			return saveVpnCmdCfg();
		}
		return null;
	}

	private String list() {
		VpnDao dao = new VpnDao();
		setTarget("/config/vpn/vpnList.jsp");
		return list(dao, "");
	}

	private String ready_add() {
		// Host host1 = (Host)PollingEngine.getInstance().getNodeByID(id1);
		// Host host2 = (Host)PollingEngine.getInstance().getNodeByID(id2);
		//        
		// List<IfEntity> startHostIfentityList =
		// getSortListByHash(host1.getInterfaceHash());
		// List<IfEntity> endHostIfentityList =
		// getSortListByHash(host2.getInterfaceHash());
		return "/config/vpn/add.jsp";
	}

	private String multi_vpn_netip() {
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		String id = getParaValue("id");
		List list = null;
		try {
			list = haweitelnetconfDao.getAllTelnetConfig();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			haweitelnetconfDao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("id", id);
		return "/config/vpn/multi_vpn_netip.jsp";
	}

	private String add() {
		int sourceId = getParaIntValue("sourceId");
		int desId = getParaIntValue("desId");
		String start_index = getParaValue("start_index");
		String end_index = getParaValue("end_index");
		Host sourceHost = (Host) PollingEngine.getInstance().getNodeByID(sourceId);
		Host desHost = (Host) PollingEngine.getInstance().getNodeByID(desId);
		String sourceIp = "";
		String desIp = "";
		if (sourceHost != null)
			sourceIp = sourceHost.getIpAddress();
		if (desHost != null)
			desIp = desHost.getIpAddress();
		VpnDao dao = new VpnDao();
		if (sourceHost != null && desHost != null) {
			IfEntity if1 = sourceHost.getIfEntityByIndex(start_index);
			IfEntity if2 = desHost.getIfEntityByIndex(end_index);
			Vpn vpn = new Vpn();
			vpn.setSourceId(sourceId);
			vpn.setSourceIp(sourceIp);
			vpn.setSourcePortIndex(start_index);
			vpn.setSourcePortName(if1.getDescr());
			vpn.setDesId(desId);
			vpn.setDesIp(desIp);
			vpn.setDesPortIndex(end_index);
			vpn.setDesPortName(if2.getDescr());
			try {
				dao.save(vpn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setTarget("/config/vpn/vpnList.jsp");
		dao = new VpnDao();
		return list(dao, "");
	}

	private String delete() {
		String[] ids = getParaArrayValue("checkbox");
		VpnDao dao = new VpnDao();
		if (ids != null && ids.length > 0) {
			try {
				dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setTarget("/config/vpn/vpnList.jsp");
		return list(dao, "");
	}

	private String choosePort() {
		int sourceId = getParaIntValue("sourceId");
		int desId = getParaIntValue("desId");

		Host host1 = (Host) PollingEngine.getInstance().getNodeByID(sourceId);
		Host host2 = (Host) PollingEngine.getInstance().getNodeByID(desId);
		List<IfEntity> startHostIfentityList = null;
		List<IfEntity> endHostIfentityList = null;
		if (host1 != null)
			startHostIfentityList = getSortListByHash(host1.getInterfaceHash());
		if (host2 != null)
			endHostIfentityList = getSortListByHash(host2.getInterfaceHash());
		request.setAttribute("startHostIfentityList", startHostIfentityList);
		request.setAttribute("endHostIfentityList", endHostIfentityList);
		request.setAttribute("sourceId", sourceId);
		request.setAttribute("desId", desId);
		return "/config/vpn/choosePort.jsp";
	}

	private String ready_edit() {
		String id = getParaValue("id");
		VpnDao dao = new VpnDao();
		Vpn vpn = null;
		try {
			vpn = (Vpn) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		List<IfEntity> startHostIfentityList = null;
		List<IfEntity> endHostIfentityList = null;
		Host host1 = null;
		Host host2 = null;
		if (vpn != null) {

			host1 = (Host) PollingEngine.getInstance().getNodeByID(vpn.getSourceId());
			host2 = (Host) PollingEngine.getInstance().getNodeByID(vpn.getDesId());
		}
		if (host1 != null)
			startHostIfentityList = getSortListByHash(host1.getInterfaceHash());
		if (host2 != null)
			endHostIfentityList = getSortListByHash(host2.getInterfaceHash());
		request.setAttribute("startHostIfentityList", startHostIfentityList);
		request.setAttribute("endHostIfentityList", endHostIfentityList);
		request.setAttribute("vpn", vpn);
		return "/config/vpn/ready_edit.jsp";
	}

	private String edit() {
		int id = getParaIntValue("id");
		int sourceId = getParaIntValue("sourceId");
		int desId = getParaIntValue("desId");
		String start_index = getParaValue("start_index");
		String end_index = getParaValue("end_index");
		Host sourceHost = (Host) PollingEngine.getInstance().getNodeByID(sourceId);
		Host desHost = (Host) PollingEngine.getInstance().getNodeByID(desId);
		String sourceIp = "";
		String desIp = "";
		if (sourceHost != null)
			sourceIp = sourceHost.getIpAddress();
		if (desHost != null)
			desIp = desHost.getIpAddress();
		IfEntity if1 = sourceHost.getIfEntityByIndex(start_index);
		IfEntity if2 = desHost.getIfEntityByIndex(end_index);
		VpnDao dao = new VpnDao();
		Vpn vpn = new Vpn();
		vpn.setId(id);
		vpn.setSourceId(sourceId);
		vpn.setSourceIp(sourceIp);
		vpn.setSourcePortIndex(start_index);
		vpn.setSourcePortName(if1.getDescr());
		vpn.setDesId(desId);
		vpn.setDesIp(desIp);
		vpn.setDesPortIndex(end_index);
		vpn.setDesPortName(if2.getDescr());
		try {
			dao.update(vpn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTarget("/config/vpn/vpnList.jsp");
		dao = new VpnDao();
		return list(dao, "");
	}

	private String showmap() {
		return "/config/vpn/showmap.jsp";
	}

	public synchronized static List<IfEntity> getSortListByHash(Hashtable<String, IfEntity> orignalHash) {
		if (orignalHash == null) {
			return null;
		}
		List<IfEntity> retList = new ArrayList<IfEntity>();
		Iterator<String> iterator = orignalHash.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			retList.add(orignalHash.get(key));
		}
		Collections.sort(retList);
		return retList;
	}

	private String saveCmdCfg() {
		String name = "";
		String id = getParaValue("id");
		String type = getParaValue("type");
		String commands = getParaValue("commands");
		String fileDesc = getParaValue("fileDesc");
		String devicetype = getParaValue("devicetype");
		name = type;
		String result = commands.replaceAll(";;", "\r\n");
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
		String filePath = "slascript/" + devicetype + "/" + name + ".log";
		File f = new File(prefix + filePath);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			fw.write(result);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date date = new Date();
		// User user = (User)
		// session.getAttribute(SessionConstant.CURRENT_USER);
		// SlaCfgCmdFileDao dao = null;
		// try {
		// dao = new SlaCfgCmdFileDao();
		// CiscoSlaCfgCmdFile vo = new CiscoSlaCfgCmdFile();
		// vo.setName(name);
		// vo.setSlatype(type);
		// vo.setFilename(filePath);
		// vo.setCreateBy(user.getName());
		// vo.setCreateTime(sdf.format(date));
		// vo.setFileDesc(fileDesc);
		// vo.setDevicetype(devicetype);
		// dao.save(vo);
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// dao.close();
		// }

		return "/config/ciscosla/status.jsp";
	}

	public String showScript() {
		String id = getParaValue("id");
		String type = getParaValue("type");
		String devicetype = getParaValue("devicetype");
		FileReader fr = null;
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
		String filePath = prefix + "slascript/" + devicetype + "/" + type + ".log";
		File f = new File(filePath);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			fr = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String lineStr = "";
		StringBuffer content = new StringBuffer();
		try {
			while (null != (lineStr = br.readLine())) {
				content.append(lineStr + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		request.setAttribute("id", id);
		request.setAttribute("type", type);
		request.setAttribute("devicetype", devicetype);

		request.setAttribute("commands", content.toString());
		return "/config/vpn/exeScript.jsp";
	}

	public String vpnAudit() {
		return null;
	}

	public String auditList() {
		DaoInterface dao = new SlaAuditDao();
		UserDao userDao = new UserDao();
		HaweitelnetconfDao telnetdao = new HaweitelnetconfDao();
		// SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		List listOne = new ArrayList();
		Hashtable userHash = new Hashtable();
		Hashtable telnetHash = new Hashtable();
		try {
			listOne = userDao.loadAll();
			if (listOne != null && listOne.size() > 0) {
				for (int i = 0; i < listOne.size(); i++) {
					User user = (User) listOne.get(i);
					userHash.put(user.getId(), user);
				}
			}
			List telnetlist = telnetdao.loadAll();
			if (telnetlist != null && telnetlist.size() > 0) {
				for (int i = 0; i < telnetlist.size(); i++) {
					Huaweitelnetconf telnetconfig = (Huaweitelnetconf) telnetlist.get(i);
					telnetHash.put(telnetconfig.getId(), telnetconfig);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}
		request.setAttribute("listOne", listOne);
		request.setAttribute("userHash", userHash);
		request.setAttribute("telnetHash", telnetHash);
		HostNodeDao hostNodeDao = new HostNodeDao();
		List listThree = new ArrayList();
		try {
			listOne = hostNodeDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		request.setAttribute("listThree", listThree);
		String userid = getParaValue("userid");// 用户类型
		String type = getParaValue("type");// 类型
		String dotime = getParaValue("dotime");// 操作时间
		String startdate = getParaValue("startdate");// 开始日期
		String todate = getParaValue("todate");// 
		String id = getParaValue("id");
		// jsp页面修改
		setTarget("/config/vpn/auditList.jsp");

		StringBuffer where = new StringBuffer();

		where.append(" where slatype='vpn' ");
		if (userid == null) {
			userid = "-1";
		}
		if (!userid.equals("-1")) {
			where.append(" and userid='" + userid + "'");
		}
		if (id == null) {
			id = "-1";
		}
		if (!id.equals("-1")) {
			where.append(" and telnetconfigid=" + id);
		}

		if (startdate != null && todate != null && !"".equals(startdate) && !"".equals(todate) && !"null".equals(startdate) && !"null".equals(todate)) {
			where.append(" and dotime>'" + startdate + " 00:00:00' and dotime<'" + todate + " 23:59:59'");
		} else {
			String currentDateString = "";// 初始页面开始日期为空
			String perWeekDateString = "";// 初始页面结束日期为空
			todate = currentDateString;
			startdate = perWeekDateString;
		}
		request.setAttribute("userid", userid);
		request.setAttribute("type", type);
		request.setAttribute("id", id);
		request.setAttribute("dotime", dotime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return list(dao, where + " order by dotime desc");

	}

	// 查看信息
	private String read() {

		// System.out.print("$$$$$$$welcome to here read the website !");
		String targetJsp = "/config/vpn/read.jsp";
		SlaAudit vo = null;
		SlaAuditDao dao = new SlaAuditDao();
		try {
			vo = (SlaAudit) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		User user = null;
		UserDao userdao = new UserDao();
		try {
			user = (User) userdao.findByID(vo.getUserid() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userdao.close();
		}
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		Huaweitelnetconf telnetconfig = null;
		try {
			telnetconfig = (Huaweitelnetconf) haweitelnetconfDao.findByID(vo.getTelnetconfigid() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			haweitelnetconfDao.close();
		}
		request.setAttribute("username", user.getName());
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		if (telnetconfig != null) {
			request.setAttribute("ipaddress", telnetconfig.getIpaddress());
		}
		// request.setAttribute("map", getTranslate());
		return targetJsp;
	}

	// 加载命令文件
	private String loadFileFromMenu() {
		String deviceType = request.getParameter("deviceType");
		String where = "";
		if (deviceType != null) {
			where = " where devicetype='" + deviceType + "'";
		}
		VpnCfgFileDao dao = new VpnCfgFileDao();
		List list = dao.findByCondition(where);
		request.setAttribute("list", list);
		return "/config/vpn/loadFileListMenu.jsp";
	}

	private String saveVpnFile() {
		String commands = getParaValue("commands");
		String deviceType = getParaValue("deviceType");
		// SysLogger.info("prefix===="+ResourceCenter.getInstance().getSysPath());

		request.setAttribute("commands", commands);
		request.setAttribute("deviceType", deviceType);
		return "/config/vpn/saveFile.jsp";
	}

	// 保存命令
	private String saveVpnCmdCfg() {
		String name = getParaValue("name");
		String vpnType = getParaValue("vpnType");
		// String fileName = getParaValue("fileName");
		String commands = getParaValue("commands");
		String fileDesc = getParaValue("fileDesc");
		String deviceType = getParaValue("deviceType");

		String result = commands.replaceAll(";", "\r\n");
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
		String filePath ="slascript/" + deviceType + "/" + name+"_"+vpnType + ".log";

		File f = new File(prefix+filePath);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			fw.write(result);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		VpnCfgFileDao dao = null;
		try {
			dao = new VpnCfgFileDao();
			VpnCfgCmdFile vo = new VpnCfgCmdFile();
			vo.setName(name);
			vo.setVpnType(vpnType);
			vo.setFilename(filePath);
			vo.setCreateBy(user.getName());
			vo.setCreateTime(sdf.format(date));
			vo.setFileDesc(fileDesc);
			vo.setDeviceType(deviceType);
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return "/config/ciscosla/status.jsp";
	}
}
