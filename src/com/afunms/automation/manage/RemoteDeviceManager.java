package com.afunms.automation.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.automation.dao.NetCfgFileAuditDao;
import com.afunms.automation.dao.NetCfgFileNodeDao;
import com.afunms.automation.dao.PasswdTimingConfigDao;
import com.afunms.automation.model.ConfiguringDevice;
import com.afunms.automation.model.NetCfgFileAudit;
import com.afunms.automation.model.NetCfgFileNode;
import com.afunms.automation.model.PasswdTimingConfig;
import com.afunms.automation.task.BatchModifyTask;
import com.afunms.automation.telnet.BaseTelnet;
import com.afunms.automation.telnet.CiscoTelnet;
import com.afunms.automation.telnet.NetTelnetUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;

public class RemoteDeviceManager extends BaseManager implements ManagerInterface {
	static StringBuffer result = new StringBuffer();
	/**
	 * 
	 * @description 远程设备密码修改列表
	 * @author wangxiangyong
	 * @date Aug 30, 2014 4:34:44 PM
	 * @return String  
	 * @return
	 */
	public String passwdList() {
		List configingDeviceList = new ArrayList();
		NetCfgFileNodeDao netCfgFileNodeDao = new NetCfgFileNodeDao();
		this.list(netCfgFileNodeDao);
		List telnetConfList = (List) request.getAttribute("list");
		for (int i = 0; i < telnetConfList.size(); i++) {
			ConfiguringDevice cfgingDevice = new ConfiguringDevice();
			NetCfgFileNode telnetConf = (NetCfgFileNode) telnetConfList.get(i);
			cfgingDevice.setId(telnetConf.getId());
			cfgingDevice.setAlias(telnetConf.getAlias());
			cfgingDevice.setIpaddress(telnetConf.getIpaddress());
			cfgingDevice.setDeviceRender(telnetConf.getDeviceRender());
			configingDeviceList.add(cfgingDevice);
		}
		request.setAttribute("configlist", configingDeviceList);
		return "/automation/remote/passwdList.jsp";
	}
	/**
	 * 
	 * @description 右键菜单中的 修改密码 操作
	 * @author wangxiangyong
	 * @date Aug 30, 2014 10:09:52 PM
	 * @return String  
	 * @return
	 */
	private String readyTelnetModify() {
		String id = this.getParaValue("id");
		NetCfgFileNodeDao telnetcfgdao = new NetCfgFileNodeDao();
		NetCfgFileNode telnetCfg = (NetCfgFileNode) telnetcfgdao.findByID(id);
		request.setAttribute("vpntelnetconf", telnetCfg);
		telnetcfgdao.close();
		String deviceType = telnetCfg.getDeviceRender();
		String jsp = "";
		if (deviceType.equals("h3c") || deviceType.equals("huawei"))// 华三/华为
		{
			jsp = "/automation/remote/telnetmodifypassword.jsp?deviceType=" + deviceType;
		} else { // if(sysOid.equals("1.3.6.1.4.1.9.1.209"))//思科
			jsp = "/automation/remote/cisco_telnet_modify.jsp?deviceType=" + deviceType;
		}
		request.setAttribute("deviceType", deviceType);
		return jsp;
	}
	/**
	 * 
	 * @description 修改密码
	 * @author wangxiangyong
	 * @date Aug 30, 2014 10:10:05 PM
	 * @return String  
	 * @return
	 */
	private String updatepassword() {

		String deviceType = this.getParaValue("deviceType");

		if (deviceType.equals("h3c"))// h3c
		{
			String id = this.getParaValue("id");
			String modifyuser = this.getParaValue("modifyuser");
			String newpassword = this.getParaValue("newpassword");
			int encrypt = this.getParaIntValue("encrypt");
			String threeA = this.getParaValue("threeA");

			NetCfgFileNodeDao telnetcfgdao = new NetCfgFileNodeDao();
			NetCfgFileNode hmo = (NetCfgFileNode) telnetcfgdao.findByID(id);
			telnetcfgdao.close();

			NetTelnetUtil tvpn = new NetTelnetUtil();
			tvpn.setSuUser(hmo.getSuuser());// su
			tvpn.setSuPassword(hmo.getSupassword());// su密码
			tvpn.setUser(hmo.getUser());// 用户
			tvpn.setPassword(hmo.getPassword());// 密码
			tvpn.setIp(hmo.getIpaddress());// ip地址
//			tvpn.setDEFAULT_PROMPT(hmo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(hmo.getPort());
			boolean b = tvpn.modifypassowd(modifyuser, newpassword, encrypt, threeA, hmo.getOstype());

			if (b) {
				if (modifyuser.equals("su")) {
					hmo.setSupassword(newpassword);
				} else {
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
				}
				NetCfgFileNodeDao hdao = new NetCfgFileNodeDao();
				hdao.update(hmo);
				hdao.close();
				return "/automation/common/status.jsp";
			} else {
				setErrorCode(0);
				return null;
			}

		} else if (deviceType.equals("cisco") ) {

			String id = this.getParaValue("id");
			String modifyuser = this.getParaValue("modifyuser");
			String newpassword = this.getParaValue("newpassword");
			NetCfgFileNodeDao telnetcfgdao = new NetCfgFileNodeDao();

			NetCfgFileAudit hmoa = new NetCfgFileAudit();
			NetCfgFileNodeDao dao = new NetCfgFileNodeDao();
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			int userid = user.getId();
			String username = user.getName();
			String oldPassword = this.getParaValue("password");
			String newPassword = this.getParaValue("newpassword");
			hmoa.setIp(getParaValue("ipaddress"));
			hmoa.setUserid(userid);
			hmoa.setUsername(username);
			hmoa.setOldpassword(oldPassword);
			hmoa.setNewpassword(newPassword);
			dao.save(hmoa);
			NetCfgFileNode hmo = null;
			try {
				hmo = (NetCfgFileNode) telnetcfgdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				telnetcfgdao.close();
				dao.close();
			}

			CiscoTelnet ciscoTelnet = new CiscoTelnet(hmo.getIpaddress(), hmo.getUser(), hmo.getPassword(), hmo.getPort());
			if (ciscoTelnet.login()) {
				if (ciscoTelnet.modifyPasswd(hmo.getSupassword(), modifyuser, newpassword)) {
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
					NetCfgFileNodeDao tdao = new NetCfgFileNodeDao();
					try {
						tdao.update(hmo);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						tdao.close();
					}
					return "/automation/common/status.jsp";
				} else {
					setErrorCode(-1);

					return "/automation/common/error.jsp";
				}
			} else {
				setErrorCode(-1);
				return "/automation/common/error.jsp";
			}
		} else if (deviceType.equals("zte")|| deviceType.equals("redgiant")||deviceType.equals("huawei")) {
			String id = this.getParaValue("id");
			String modifyuser = this.getParaValue("modifyuser");
			String newpassword = this.getParaValue("newpassword");
			String encrypt = this.getParaValue("encrypt");
			String threeA = this.getParaValue("threeA");
			NetCfgFileNodeDao telnetcfgdao = new NetCfgFileNodeDao();

			NetCfgFileAudit hmoa = new NetCfgFileAudit();
			NetCfgFileAuditDao dao = new NetCfgFileAuditDao();
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			int userid = user.getId();
			String username = user.getName();
			String oldPassword = this.getParaValue("password");
			String newPassword = this.getParaValue("newpassword");
			hmoa.setIp(getParaValue("ipaddress"));
			hmoa.setUserid(userid);
			hmoa.setUsername(username);
			hmoa.setOldpassword(oldPassword);
			hmoa.setNewpassword(newPassword);
			dao.save(hmoa);
			NetCfgFileNode vo = null;
			try {
				vo = (NetCfgFileNode) telnetcfgdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				telnetcfgdao.close();
				dao.close();
			}

			BaseTelnet telnet = new BaseTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword());
			String sign=telnet.login();
			if (sign.indexOf("成功")>-1) {
				if (telnet.modifyDevPasswd(deviceType, modifyuser, newpassword,encrypt)) {
					
					vo.setUser(modifyuser);
					vo.setPassword(newpassword);
					NetCfgFileNodeDao tdao = new NetCfgFileNodeDao();
					try {
						tdao.update(vo);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						tdao.close();
					}
					return "/automation/common/status.jsp";
				} else {
					setErrorCode(-1);
					return "/automation/common/error.jsp";
				}
			} else {
				setErrorCode(-1);
				return "/automation/common/error.jsp";
			}
		} else {
			setErrorCode(-1);
			return "/automation/common/error.jsp";
		}
	}
	/**
	 * 
	 * @description 批量修改密码界面
	 * @author wangxiangyong
	 * @date Aug 30, 2014 11:31:35 PM
	 * @return String  
	 * @return
	 */
	private String ready_multi_modify() {
		return "/automation/remote/multi_telnetmodifypassword.jsp";
	}
	/**
	 * 
	 * @description  批量修改设备密码操作
	 * @author wangxiangyong
	 * @date Aug 30, 2014 11:34:24 PM
	 * @return String  
	 * @return
	 */
	private String modifyTelnetPasswordForBatch() {
		String ipAddresses = request.getParameter("ipaddress");
		String modifyuser = this.getParaValue("modifyuser");
		String threeA = this.getParaValue("threeA");
		int encrypt = this.getParaIntValue("encrypt");
		String newpassword = this.getParaValue("newpassword");
		String[] split = ipAddresses.substring(1).split(",");

		/**
		 * 线程池
		 * 
		 * @param 线程池维护线程的最少数量
		 * @param 线程池维护线程的最大数量
		 * @param 线程池维护线程所允许的空闲时间
		 * @param 线程池维护线程所允许的空闲时间的单位
		 * @param 线程池所使用的缓冲队列
		 * @param 线程池对拒绝任务的处理策略
		 *            ,此处的策略是 重试添加当前的任务，他会自动重复调用execute()方法
		 * @author GZM
		 */
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 30, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30), new ThreadPoolExecutor.CallerRunsPolicy());
		String s = "";
		for (int i = 0; i < split.length; i++) {
			s = s + "," + split[i];
		}
		String s2 = s.substring(1);
		String sql = "select * from topo_node_telnetconfig where ip_address in('" + s2.replace(",", "','") + "')";
		NetCfgFileNodeDao dao = new NetCfgFileNodeDao();
		List list = dao.findByCriteria(sql);
		dao.close();
		result.delete(0, result.length());
		for (int i = 0; i < list.size(); i++) {
			threadPool.execute(new BatchModifyTask(result, (NetCfgFileNode) list.get(i), modifyuser, newpassword, threeA, encrypt));
		}
		threadPool.shutdown();
		try {
			boolean loop = true;
			do { // 等待所有任务完成
				loop = !threadPool.awaitTermination(2, TimeUnit.SECONDS);
			} while (loop);
		} catch (Exception e) {
		}

		/*
		 * while(threadPool.getActiveCount() == 0)//返回主动执行任务的近似线程数,为0
		 * 说明所有任务已经执行完 { try{ Thread.sleep(1000); }catch(Exception
		 * e){e.printStackTrace();} }
		 */
		System.out.println(result.toString());
		request.setAttribute("result", result.toString());
		return "/automation/cfgfile/multi_modify_status.jsp";
	}
	/**
	 * 
	 * @description 密码定时变更列表
	 * 
	 * @author wangxiangyong
	 * @date Aug 30, 2014 11:46:28 PM
	 * @return String  
	 * @return
	 */
	private String deviceList() {
		PasswdTimingConfigDao passwdTimingBackupTelnetConfigDao = new PasswdTimingConfigDao();
		List<PasswdTimingConfig> passwdTimingBackupTelnetConfigList = null;
		try {
			passwdTimingBackupTelnetConfigList = passwdTimingBackupTelnetConfigDao.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			passwdTimingBackupTelnetConfigDao.close();
		}

		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String id = getParaValue("id");
		Hashtable<String, String> alarmWayHashtable = new Hashtable<String, String>();

		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			List list = alarmWayDao.loadAll();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					AlarmWay way = (AlarmWay) list.get(i);
					if (way != null) {
						alarmWayHashtable.put(way.getId() + "", way.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		// }
		request.setAttribute("alarmWayHashtable", alarmWayHashtable);
		// request.setAttribute("alarmIndicatorsNode", alarmIndicatorsNode);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("list", passwdTimingBackupTelnetConfigList);
		return "/automation/remote/device_list.jsp";
	}
	/**
	 * 
	 * @description 密码操作审计
	 * @author wangxiangyong
	 * @date Aug 30, 2014 11:58:43 PM
	 * @return String  
	 * @return
	 */
	private String ready_multi_audit() {
		NetCfgFileAuditDao haweitelnetconfAuditDao = new NetCfgFileAuditDao();

		this.list(haweitelnetconfAuditDao);
		List<NetCfgFileAudit> auditlist = (List<NetCfgFileAudit>) request.getAttribute("list");
		request.setAttribute("list", auditlist);
		return "/automation/remote/passwdAudit.jsp";
	}
	/**
	 * 
	 * @description 审计查询
	 * @author wangxiangyong
	 * @date Aug 31, 2014 10:48:08 PM
	 * @return String  
	 * @return
	 */
	private String queryByCondition() {
		

		String key = getParaValue("key");
		String value = getParaValue("value");
		NetCfgFileAuditDao haweitelnetconfAuditDao = new NetCfgFileAuditDao();

		request.setAttribute("key", key);
		request.setAttribute("value", value);
		
		return list(haweitelnetconfAuditDao, " where " + key + " = '" + value + "'");
	}
	/**
	 * 
	 * @description 启动定时修改
	 * @author wangxiangyong
	 * @date Aug 31, 2014 11:04:56 PM
	 * @return String  
	 * @return
	 */
	private String addPasswdBackup() {
		String id = getParaValue("id");
		List<PasswdTimingConfig> passwdTimingBackupTelnetConfigList = null;
		PasswdTimingConfigDao passwdTimingConfigDao = new PasswdTimingConfigDao();
		try {
			passwdTimingConfigDao.updateStatus("是", id);
			passwdTimingBackupTelnetConfigList = passwdTimingConfigDao.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			passwdTimingConfigDao.close();
		}
		request.setAttribute("list", passwdTimingBackupTelnetConfigList);
		return "/automation/remote/device_list.jsp";
	}
	/**
	 * 
	 * @description 取消启动定时修改
	 * @author wangxiangyong
	 * @date Aug 31, 2014 11:07:26 PM
	 * @return String  
	 * @return
	 */
	private String disPasswdBackup() {
		String id = getParaValue("id");
		List<PasswdTimingConfig> passwdTimingBackupTelnetConfigList = null;
		PasswdTimingConfigDao PasswdTimingBackupTelnetConfigDao = new PasswdTimingConfigDao();
		try {
			PasswdTimingBackupTelnetConfigDao.updateStatus("否", id);
			passwdTimingBackupTelnetConfigList = PasswdTimingBackupTelnetConfigDao.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PasswdTimingBackupTelnetConfigDao.close();
		}
		request.setAttribute("list", passwdTimingBackupTelnetConfigList);
		return "/automation/remote/device_list.jsp";
	}
	/**
	 * 
	 * @description 密码定时变更添加页面
	 * @author wangxiangyong
	 * @date Sep 1, 2014 8:13:55 AM
	 * @return String  
	 * @return
	 */
	private String ready_addPasswd() {
		return "/automation/remote/addPasswdTimingCfg.jsp";
	}
	private String deletePasswdTimingCfg() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null) {
			if (ids != null && ids.length != 0) {
				PasswdTimingConfigDao passwdTimingBackupTelnetConfigDao = new PasswdTimingConfigDao();
				try {
					for (String id : ids) {
						if (id != null && !"".equals(id)) {
							passwdTimingBackupTelnetConfigDao.delete(id);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					passwdTimingBackupTelnetConfigDao.close();
				}
			}
		}
		return deviceList();
	}
	/**
	 * 
	 * @description 定时执行页面中，点击选择设备，执行该方法
	 * @author wangxiangyong
	 * @date Sep 1, 2014 10:52:00 AM
	 * @return String  
	 * @return
	 */
	private String multi_telnet_netip() {
		NetCfgFileNodeDao netCfgFileNodeDao = new NetCfgFileNodeDao();
		String deviceType=getParaValue("deviceType");
		StringBuffer whereSql=new StringBuffer();
		whereSql.append("select * from topo_node_telnetconfig ");
		if(deviceType!=null&&!deviceType.equals("null")){
			whereSql.append("where device_render='"+deviceType+"'");
		}
		List list = null;
		try {
			list = netCfgFileNodeDao.findByCriteria(whereSql.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			netCfgFileNodeDao.close();
		}
		request.setAttribute("list", list);
		return "/automation/remote/multi_telnet_netip.jsp";
	}
	/**
	 * 
	 * @description 密码定时变更列表编辑
	 * @author wangxiangyong
	 * @date Sep 1, 2014 11:06:37 AM
	 * @return String  
	 * @return
	 */
	private String ready_editPasswdTimingCfg() {
		
		String id = getParaValue("id");
		PasswdTimingConfigDao passwdTimingBackupTelnetConfigDao = new PasswdTimingConfigDao();
		PasswdTimingConfig passwdTimingBackupTelnetConfig = null;
		try {
			passwdTimingBackupTelnetConfig = (PasswdTimingConfig) passwdTimingBackupTelnetConfigDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			passwdTimingBackupTelnetConfigDao.close();
		}
		request.setAttribute("id", id);
		request.setAttribute("list", passwdTimingBackupTelnetConfig);
		return "/automation/remote/editPasswdTimingCfg.jsp";
	}
	/**
	 * 
	 * @description 远程设备密码修改列表
	 * @author wangxiangyong
	 * @date Aug 30, 2014 4:34:44 PM
	 * @return String  
	 * @return
	 */
	public String queryNodesListById() {
		String id=getParaValue("id");
		List configingDeviceList = new ArrayList();
		List telnetConfList=null;
		NetCfgFileNodeDao telnetConfDao=null;
		 try {
				telnetConfDao = new NetCfgFileNodeDao();
			   telnetConfList = telnetConfDao.loadById(id);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				telnetConfDao.close();
			}
		for (int i = 0; i < telnetConfList.size(); i++) {
			ConfiguringDevice cfgingDevice = new ConfiguringDevice();
			NetCfgFileNode telnetConf = (NetCfgFileNode) telnetConfList.get(i);
			cfgingDevice.setId(telnetConf.getId());
			cfgingDevice.setIpaddress(telnetConf.getIpaddress());
			cfgingDevice.setAlias(telnetConf.getAlias());
			cfgingDevice.setDeviceRender(telnetConf.getDeviceRender());
			
			configingDeviceList.add(cfgingDevice);
		}
		request.setAttribute("configlist", configingDeviceList);
		return "/automation/remote/passwdList.jsp";
	}
	public String execute(String action) {
		if(action.equals("passwdList")){
			return passwdList();
		}
		if(action.equals("readyTelnetModify")){
			return readyTelnetModify();
		}
		if(action.equals("updatepassword")){
			return updatepassword();
		}
		if(action.equals("ready_multi_modify")){//批量修改密码界面
			return ready_multi_modify();
		}
		if (action.equals("multi_modify"))// list.jsp页面->批量修改密码->修改按钮
		{
			return modifyTelnetPasswordForBatch();
		}
		if (action.equals("deviceList"))//密码定时变更列表
		{
			return deviceList();
		}
		if (action.equals("ready_multi_audit"))//密码操作审计
		{
			return ready_multi_audit();
		}
		if (action.equals("queryByCondition"))//审计查询
		{
			return queryByCondition();
		}
		if (action.equals("addPasswdBackup"))//审计查询
		{
			return addPasswdBackup();
		}
		if (action.equals("disPasswdBackup"))//审计查询
		{
			return disPasswdBackup();
		}
		if (action.equals("ready_addPasswd"))//密码定时变更添加
		{
			return ready_addPasswd();
		}
		if (action.equals("deletePasswdTimingCfg"))//密码定时变更删除
		{
			return deletePasswdTimingCfg();
		}
		if (action.equals("multi_telnet_netip"))//定时执行页面中，点击选择设备，执行该方法
		{
			return multi_telnet_netip();
		}
		if (action.equals("ready_editPasswdTimingCfg"))//密码定时变更编辑
		{
			return ready_editPasswdTimingCfg();
		}
		if (action.equals("queryNodesListById"))//密码定时变更编辑
		{
			return queryNodesListById();
		}
		
		return null;
	}

}
