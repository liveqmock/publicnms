package com.afunms.config.manage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.capreport.common.DateTime;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.GeneratorKey;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfAuditDao;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.Hua3VPNFileConfigDao;
import com.afunms.config.dao.PasswdTimingBackupTelnetConfigDao;
import com.afunms.config.dao.SlaCfgCmdFileDao;
import com.afunms.config.dao.TimingBackupConditionDao;
import com.afunms.config.dao.TimingBackupTelnetConfigDao;
import com.afunms.config.dao.VPNFileConfigDao;
import com.afunms.config.model.CfgCmdFile;
import com.afunms.config.model.CiscoSlaCfgCmdFile;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.ConfiguringDevice;
import com.afunms.config.model.HaweitelnetconfAudit;
import com.afunms.config.model.Hua3VPNFileConfig;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.PasswdTimingBackupTelnetConfig;
import com.afunms.config.model.TimingBackupCondition;
import com.afunms.config.model.TimingBackupTelnetConfig;
import com.afunms.config.model.VPNFileConfig;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class CiscoSLACMDconfManager extends BaseManager implements
		ManagerInterface {
	private static HaweitelnetconfManager haweitelnetconfManager;

	public static synchronized HaweitelnetconfManager getInstance() {
		if (haweitelnetconfManager == null) {
			haweitelnetconfManager = new HaweitelnetconfManager();
		}
		return haweitelnetconfManager;
	}

	static StringBuffer result = new StringBuffer();

	public String execute(String action) {
		//System.out.println("action====================" + action);

		// TODO Auto-generated method stub
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("loadfilelist")) {
			return loadFileList();
		}
		if (action.equals("loadFileFromMenu")) {
			return loadFileFromMenu();
		}
		if (action.equals("passwdList")) {
			return passwdList();
		}

		if (action.equals("configlist")) {
			return configlist();
		}

		if (action.equals("ready_add"))// list.jsp页面 添加
		{
			return readyAdd();
		}
		if (action.equals("add")) // list.jsp页面->添加->添加按钮
		{
			return add();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("ready_edit")) {// list.jsp页面 右键菜单 编辑

			return ready_edit();
		}
		if (action.equals("findip")) {
			return findip();
		}
		if (action.equals("netip")) {
			return netip();
		}
		if (action.equals("ipmenu")) {
			return ipmenu();
		}
		if (action.equals("add_pg")) {
			return add_pg();
		}
		if (action.equals("ready_edit_gp")) {
			DaoInterface dao = new HaweitelnetconfDao();
			setTarget("/config/vpntelnet/edit_pg.jsp");
			return readyEdit(dao);
		}
		if (action.equals("bkpCfg"))// 右键菜单->备份->备份按钮
		{
			// return bkpCfg();
			return bkpCfg_1();
		}
		if (action.equals("detailPage_readybkpCfg")) {
			return detailPageReadyBackupConfig();
		}
		if (action.equals("readySetupConfig"))// list.jsp页面 右键菜单 管理备份
		{
			return readySetupConfig();
		}
		if (action.equals("readyBackupConfig"))// list.jsp页面 右键菜单 备份
		{
			return readyBackupConfig();
		}
		if (action.equals("setupConfig")) {
			return setupConfig_1();
		}
		if (action.equals("download")) {
			return download();
		}
		if (action.equals("deviceList")) {
			return deviceList();
		}
		if (action.equals("readyTelnetModify"))// list.jsp页面 右键菜单 修改密码
		{
			return readyTelnetModify();
		}
		if (action.equals("updatepassword")) {
			return updatepassword();
		}
		if (action.equals("readyuploadCfgFile"))// list.jsp页面 右键菜单 上传备份
		{
			return readyuploadCfgFile();
		}
		if (action.equals("uploadFile"))// list.jsp页面->右键菜单->上传备份->上传按钮
		{
			return uploadFile();
		}
		if (action.equals("synchronizeData")) {
			return synchronizeData();
		}
		if (action.equals("ready_addForBatch")) {
			return ready_addForBatch();
		}
		if (action.equals("multi_netip")) {
			return multi_netip();
		}
		if (action.equals("batchAdd")) {
			return batchAdd();
		}
		if (action.equals("ready_multi_modify"))// list.jsp页面 批量修改密码
		{
			return ready_multi_modify1();
		}
		if (action.equals("ready_multi_audit"))// list.jsp页面 审计信息
		{
			return ready_multi_audit();
		}
		if (action.equals("queryByCondition"))// list.jsp页面 审计信息
		{
			return queryByCondition();
		}
		if (action.equals("multi_modify"))// list.jsp页面->批量修改密码->修改按钮
		{
			return modifyTelnetPasswordForBatch();
		}
		if (action.equals("ready_backupForBatch"))// list.jsp页面 批量备份
		{
			return ready_backupForBatch();
		}
		if (action.equals("bkpCfg_forBatch"))// list.jsp页面->批量备份->备份按钮
		{
			return bkpCfg_forBatch();
		}
		if (action.equals("ready_deployCfgForBatch"))// list.jsp页面->批量应用
		{
			return ready_deployCfgForBatch();
		}
		if (action.equals("deployCfgForBatch"))// list.jsp页面->批量应用->批量应用按钮
		{
			return deployCfgForBatch();
		}
		if (action.equals("ready_timingBackup")) {// 定时备份页面
			return ready_timingBackup();
		}
		if (action.equals("timingBackup")) {// 定时备份动作
			return timingBackup();
		}
		if (action.equals("multi_telnet_netip")) {// 远程登录的设备列表
			return multi_telnet_netip();
		}
		if (action.equals("deleteTimingBackupTelnetConfig")) {// 删除定时备份的任务
			return deleteTimingBackupTelnetConfig();
		}
		if (action.equals("addTimingBackupTelnetConfig")) {
			return addTimingBackupTelnetConfig();// 增加定时备份配置文件的任务
		}
		if (action.equals("ready_editTimingBackupTelnetConfig")) {
			return ready_editTimingBackupTelnetConfig();// 编辑定时备份配置文件的任务
		}
		if (action.equals("modifyTimingBackup")) {
			return modifyTimingBackup();
		}
		if (action.equals("addBackup")) {// 添加定时备份
			return addBackup();
		}
		if (action.equals("addPasswdBackup")) {// 添加定时备份
			return addPasswdBackup();
		}
		if (action.equals("disBackup")) {// 取消定时备份
			return disBackup();
		}
		if (action.equals("disPasswdBackup")) {// 取消定时备份
			return disPasswdBackup();
		}

		// //////////////////////////////////////////
		if (action.equals("ready_fileBackup")) { // 定时扫描命令配置
			return ready_fileBackup();
		}
		if (action.equals("addFileBackupTelnetConfig")) { // 添加定时命令配置
			return addFileBackupTelnetConfig();
		}
		if (action.equals("deleteFileBackupTelnetConfig")) { // 删除定时命令配置
			return deleteFileBackupTelnetConfig();
		}
		if (action.equals("fileBackup")) {// 定时备份动作
			return fileBackup();
		}
		if (action.equals("ready_editFileBackupTelnetConfig")) {// 定时命令日志
			return ready_editFileBackupTelnetConfig();
		}
		if (action.equals("modifyFileBackup")) {// 保存修改定时备份动作
			return modifyFileBackup();
		}
		if (action.equals("fileList")) {// 定时备份文件列表
			return fileList();
		}
		if (action.equals("configFileList")) {// 详细定时备份文件列表
			return configFileList();
		}
		if (action.equals("showFileContent")) {// 定时备份文件列表
			return showFileContent();
		}
		if (action.equals("disFileBackup")) {// 取消定时扫描备份
			return disFileBackup();
		}
		if (action.equals("addFileBackup")) {// 添加定时备份
			return addFileBackup();
		}
		if (action.equals("deleteFile")) {// 删除备份
			return deleteFile();
		}
		if (action.equals("deleteFileByIp")) {// 根据IP删除备份
			return deleteFileByIp();
		}
		if (action.equals("editBaseLine")) {// 删除备份
			return editBaseLine();
		}
		if (action.equals("compareFile")) {// 删除备份
			return compareFile();
		}
		if (action.equals("showAllFile")) {// 取消定时备份
			return showAllFile();
		}
		if (action.equals("deleteLogFile")) {// 取消定时备份
			return deleteLogFile();
		}
		if (action.equals("showcmd")) {// 显示命令配置页面
			return deviceCfg();
		}

		if (action.equals("exeCmd")) {// 手工运行脚本日志
			return exeCmd();
		}
		if (action.equals("downloadLog")) {// 下载手工运行脚本日志
			return downloadLog();
		}
		if (action.equals("loadFile")) {// 加载SLA配置命令文件
			return loadFile();
		}
		if (action.equals("saveFile")) {// 将配置命令存入日志文件中界面
			return saveFile();
		}
		if (action.equals("saveCmdCfg")) {// 将配置命令存入日志文件中
			return saveCmdCfg();
		}

		// ///////////////////

		if (action.equals("serverip")) {
			return serverip();
		}
		if (action.equals("ready_addPasswd")) {
			return ready_addPasswd();
		}
		if (action.equals("passwdTimingBackup")) {
			return modifyPasswdTimingBackup();
		}
		if (action.equals("passwdEditTimingBackup")) {
			return modifyEditPasswdTimingBackup();
		}
		if (action.equals("multi_warn_netip")) {
			return multi_warn_netip();
		}
		if (action.equals("deletePasswdTimingBackupTelnetConfig")) {
			return deletePasswdTimingBackupTelnetConfig();
		}
		if (action.equals("ready_editPasswdTimingBackupTelnetConfig")) {
			return ready_editPasswdTimingBackupTelnetConfig();
		}
		if (action.equals("chooselist")) {
			return chooselist();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	private String ready_edit(){
		String jsp = "/config/ciscosla/edit.jsp";
		SlaCfgCmdFileDao dao = new SlaCfgCmdFileDao();
		CiscoSlaCfgCmdFile vo = null;
		StringBuffer content=new StringBuffer();
	       try{
	    	   vo = (CiscoSlaCfgCmdFile)dao.findByID(getParaValue("id"));   
	   	       String prePath=ResourceCenter.getInstance().getSysPath();
	    	   //String filePath=prePath+vo.getFilename().split("afunms/")[1];
	   	       String filePath=prePath+vo.getFilename();
	   	       //SysLogger.info("filePath======="+filePath);
	   		
	   		FileReader fr=null;
	   		try {
	   			fr = new FileReader(filePath);
	   		} catch (FileNotFoundException e) {
	   			e.printStackTrace();
	   		}
	   		BufferedReader br = new BufferedReader(fr);
	   		String lineStr="";
	   		
	   		try {
	   			while (null != (lineStr = br.readLine())) {
	   				content.append(lineStr + "\r\n");
	   			}
	   		} catch (IOException e) {
	   			e.printStackTrace();
	   		}
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dao.close();
	       }
	       if(vo!=null)
	       {	   
	          request.setAttribute("vo",vo);
	          request.setAttribute("content",content.toString());
	          
	       }
	    return jsp;
	}
	
        //加载命令文件
	private String loadFile() {
		SlaCfgCmdFileDao dao=new SlaCfgCmdFileDao();
         List list=dao.loadAll();
         request.setAttribute("list", list);
		return "/config/ciscosla/loadFile.jsp";
	}
	
    //命令文件
	private String loadFileList() {
		SlaCfgCmdFileDao dao=new SlaCfgCmdFileDao();
         List list=dao.loadAll();
         request.setAttribute("list", list);
		return "/config/ciscosla/loadFileList.jsp";
	}
	
    //加载命令文件
	private String loadFileFromMenu() {
		String devicetype=request.getParameter("devicetype");
		String where ="";
		if(devicetype!=null&&!devicetype.equals("all")){
			where=" where devicetype='"+devicetype+"'";
		}
		SlaCfgCmdFileDao dao=new SlaCfgCmdFileDao();
         List list=dao.findByCondition(where);
         request.setAttribute("list", list);
		return "/config/ciscosla/loadFileListMenu.jsp";
	}
	
	private String saveFile() {
		String commands = getParaValue("commands");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());
		//SysLogger.info("prefix===="+ResourceCenter.getInstance().getSysPath());
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		String fileName = b_time + ".log";
		String filePath = prefix + "script\\\\" + fileName;
		request.setAttribute("fileName", fileName);
		request.setAttribute("commands", commands);
		return "/config/ciscosla/saveFile.jsp";
	}

	// 保存命令
	private String saveCmdCfg() {
		String name=getParaValue("name");
		String slatype=getParaValue("slatype");
		//String fileName = getParaValue("fileName");
		String commands = getParaValue("commands");
		String fileDesc = getParaValue("fileDesc");
		String devicetype=getParaValue("devicetype");
		
		String result = commands.replaceAll(";;", "\r\n");
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
		"/");
		String filePath = prefix + "slascript/"+devicetype+"/" + name+".log";
		
		File f = new File(filePath);
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
		SlaCfgCmdFileDao dao = null;
		try {
			dao = new SlaCfgCmdFileDao();
			CiscoSlaCfgCmdFile vo = new CiscoSlaCfgCmdFile();
			vo.setName(name);
			vo.setSlatype(slatype);
			vo.setFilename(filePath);
			vo.setCreateBy(user.getName());
			vo.setCreateTime(sdf.format(date));
			vo.setFileDesc(fileDesc);
			vo.setDevicetype(devicetype);
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return "/config/ciscosla/status.jsp";
	}

	private String ready_addPasswd() {
		return "/config/vpntelnet/addPasswdTimingBackup.jsp";
	}

	private String modifyPasswdTimingBackup() {
		return null;
	}

	// 修改
	private String modifyEditPasswdTimingBackup() {
		return null;
	}

	private String addBackup() {
		String id = getParaValue("id");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		try {
			timingBackupTelnetConfigDao.updateStatus("1", id);
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/timingBackup.jsp";
	}

	private String addFileBackup() {
		String id = getParaValue("id");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		try {
			timingBackupTelnetConfigDao.updateStatus("1", id);
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getFileList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/fileBackup.jsp";
	}

	private String addPasswdBackup() {
		String id = getParaValue("id");
		List<PasswdTimingBackupTelnetConfig> passwdTimingBackupTelnetConfigList = null;
		PasswdTimingBackupTelnetConfigDao PasswdTimingBackupTelnetConfigDao = new PasswdTimingBackupTelnetConfigDao();
		try {
			PasswdTimingBackupTelnetConfigDao.updateStatus("是", id);
			passwdTimingBackupTelnetConfigList = PasswdTimingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PasswdTimingBackupTelnetConfigDao.close();
		}
		request.setAttribute("list", passwdTimingBackupTelnetConfigList);
		return "/config/vpntelnet/device_list.jsp";
	}

	private String disBackup() {
		String id = getParaValue("id");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		try {
			timingBackupTelnetConfigDao.updateStatus("0", id);
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/timingBackup.jsp";
	}

	// 取消定时命令
	private String disFileBackup() {
		String id = getParaValue("id");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		try {
			timingBackupTelnetConfigDao.updateStatus("0", id);
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getFileList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/fileBackup.jsp";
	}

	private String disPasswdBackup() {
		String id = getParaValue("id");
		List<PasswdTimingBackupTelnetConfig> passwdTimingBackupTelnetConfigList = null;
		PasswdTimingBackupTelnetConfigDao PasswdTimingBackupTelnetConfigDao = new PasswdTimingBackupTelnetConfigDao();
		try {
			PasswdTimingBackupTelnetConfigDao.updateStatus("否", id);
			passwdTimingBackupTelnetConfigList = PasswdTimingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PasswdTimingBackupTelnetConfigDao.close();
		}
		request.setAttribute("list", passwdTimingBackupTelnetConfigList);
		return "/config/vpntelnet/device_list.jsp";
	}

	/**
	 * 修改定时备份任务
	 * 
	 * @return
	 */
	private String modifyTimingBackup() {
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		TimingBackupTelnetConfig timingBackupTelnetConfig = new TimingBackupTelnetConfig();
		String ipaddress = getParaValue("ipaddress");
		String[] sendtimemonth = request.getParameterValues("sendtimemonth");
		String[] sendtimeweek = request.getParameterValues("sendtimeweek");
		String[] sendtimeday = request.getParameterValues("sendtimeday");
		String[] sendtimehou = request.getParameterValues("sendtimehou");
		String transmitfrequency = request.getParameter("transmitfrequency");
		String bkpType = request.getParameter("bkpType");
		String status = getParaValue("status");
		String content = getParaValue("content");
		String id = getParaValue("id");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		try {
			timingBackupTelnetConfig.setId(Integer.parseInt(id));
			timingBackupTelnetConfig.setTelnetconfigids(ipaddress);
			timingBackupTelnetConfig.setBackup_sendfrequency(Integer
					.parseInt(transmitfrequency));
			timingBackupTelnetConfig
					.setBackup_time_month(arrayToString(sendtimemonth));
			timingBackupTelnetConfig
					.setBackup_time_week(arrayToString(sendtimeweek));
			timingBackupTelnetConfig
					.setBackup_time_day(arrayToString(sendtimeday));
			timingBackupTelnetConfig
					.setBackup_time_hou(arrayToString(sendtimehou));
			timingBackupTelnetConfig.setBkpType(bkpType);
			// timingBackupTelnetConfig.setBackup_date(Integer.parseInt(dt.getMyDateTime(DateTime.Datetime_Format_14)));
			timingBackupTelnetConfig.setStatus(status);
			timingBackupTelnetConfigDao.update(timingBackupTelnetConfig);
			timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();

			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getAlList();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/timingBackup.jsp";
	}

	/**
	 * 修改定时备份任务
	 * 
	 * @return
	 */
	private String modifyFileBackup() {
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		TimingBackupTelnetConfig fileBackupTelnetConfig = new TimingBackupTelnetConfig();
		String ipaddress = getParaValue("ipaddress");
		String[] sendtimemonth = request.getParameterValues("sendtimemonth");
		String[] sendtimeweek = request.getParameterValues("sendtimeweek");
		String[] sendtimeday = request.getParameterValues("sendtimeday");
		String[] sendtimehou = request.getParameterValues("sendtimehou");
		String transmitfrequency = request.getParameter("transmitfrequency");
		String bkpType = request.getParameter("bkpType");
		String status = getParaValue("status");
		String content = getParaValue("content");
		String id = getParaValue("id");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		try {
			fileBackupTelnetConfig.setId(Integer.parseInt(id));
			fileBackupTelnetConfig.setTelnetconfigids(ipaddress);
			fileBackupTelnetConfig.setBackup_sendfrequency(Integer
					.parseInt(transmitfrequency));
			fileBackupTelnetConfig
					.setBackup_time_month(arrayToString(sendtimemonth));
			fileBackupTelnetConfig
					.setBackup_time_week(arrayToString(sendtimeweek));
			fileBackupTelnetConfig
					.setBackup_time_day(arrayToString(sendtimeday));
			fileBackupTelnetConfig
					.setBackup_time_hou(arrayToString(sendtimehou));
			fileBackupTelnetConfig.setBkpType(bkpType);
			// timingBackupTelnetConfig.setBackup_date(Integer.parseInt(dt.getMyDateTime(DateTime.Datetime_Format_14)));
			fileBackupTelnetConfig.setStatus(status);
			fileBackupTelnetConfig.setContent(content);
			timingBackupTelnetConfigDao.update(fileBackupTelnetConfig);
			timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();

			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getFileList();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		saveCondition(Integer.parseInt(id));
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/fileBackup.jsp";
	}

	/**
	 * 编辑定时备份配置文件的任务
	 * 
	 * @return
	 */
	private String ready_editTimingBackupTelnetConfig() {
		String id = getParaValue("id");
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		TimingBackupTelnetConfig timingBackupTelnetConfig = null;
		try {
			timingBackupTelnetConfig = (TimingBackupTelnetConfig) timingBackupTelnetConfigDao
					.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("id", id);
		request.setAttribute("timingBackupTelnetConfig",
				timingBackupTelnetConfig);
		return "/config/vpntelnet/editTimingBackup.jsp";
	}

	/**
	 * 编辑定时命令日志文件的任务
	 * 
	 * @return
	 */
	private String ready_editFileBackupTelnetConfig() {
		String id = getParaValue("id");

		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		TimingBackupTelnetConfig timingBackupTelnetConfig = null;
		try {
			timingBackupTelnetConfig = (TimingBackupTelnetConfig) timingBackupTelnetConfigDao
					.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}

		TimingBackupConditionDao conditionDao = new TimingBackupConditionDao();
		List<TimingBackupCondition> conditionList = conditionDao
				.findByCondition(" where timingId=" + id);
		conditionDao.close();
		request.setAttribute("conditionList", conditionList);
		request.setAttribute("id", id);
		request.setAttribute("timingBackupTelnetConfig",
				timingBackupTelnetConfig);
		return "/config/vpntelnet/editFileBackup.jsp";
	}

	private String ready_editPasswdTimingBackupTelnetConfig() {
		String id = getParaValue("id");
		PasswdTimingBackupTelnetConfigDao passwdTimingBackupTelnetConfigDao = new PasswdTimingBackupTelnetConfigDao();
		PasswdTimingBackupTelnetConfig passwdTimingBackupTelnetConfig = null;
		try {
			passwdTimingBackupTelnetConfig = (PasswdTimingBackupTelnetConfig) passwdTimingBackupTelnetConfigDao
					.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			passwdTimingBackupTelnetConfigDao.close();
		}
		request.setAttribute("id", id);
		request.setAttribute("list", passwdTimingBackupTelnetConfig);
		return "/config/vpntelnet/editPasswdTimingBackup.jsp";
	}

	public String chooselist() {
		String jsp = "/config/vpntelnet/chooselist.jsp";
		String alarmWayIdEvent = getParaValue("alarmWayIdEvent");
		String alarmWayNameEvent = getParaValue("alarmWayNameEvent");
		try {
			List list = getList();
			request.setAttribute("list", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("alarmWayIdEvent", alarmWayIdEvent);
		request.setAttribute("alarmWayNameEvent", alarmWayNameEvent);
		return jsp;
	}

	/**
	 * 增加定时备份配置文件的任务
	 * 
	 * @return
	 */
	private String addTimingBackupTelnetConfig() {
		return "/config/vpntelnet/addTimingBackup.jsp";
	}

	/**
	 * 增加定时备份配置文件的任务
	 * 
	 * @return
	 */
	private String addFileBackupTelnetConfig() {
		return "/config/vpntelnet/addFileBackup.jsp";
	}

	/**
	 * 转向定时备份页面
	 * 
	 * @return
	 */
	private String ready_timingBackup() {
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		try {
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/timingBackup.jsp";
	}

	/**
	 * 转向定时扫描日志列表
	 * 
	 * @return
	 */
	private String ready_fileBackup() {
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		try {
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getFileList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/fileBackup.jsp";
	}

	/**
	 * 定时备份
	 * 
	 * @return
	 */
	private String timingBackup() {
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		TimingBackupTelnetConfig timingBackupTelnetConfig = new TimingBackupTelnetConfig();
		String ipaddress = getParaValue("ipaddress");
		String[] sendtimemonth = request.getParameterValues("sendtimemonth");
		String[] sendtimeweek = request.getParameterValues("sendtimeweek");
		String[] sendtimeday = request.getParameterValues("sendtimeday");
		String[] sendtimehou = request.getParameterValues("sendtimehou");
		String transmitfrequency = request.getParameter("transmitfrequency");
		String status = getParaValue("status");
		String bkpType = getParaValue("bkpType");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		SysLogger.info("ipaddress===" + ipaddress);
		DateTime dt = new DateTime();
		try {
			timingBackupTelnetConfig.setTelnetconfigids(ipaddress);
			timingBackupTelnetConfig.setBackup_sendfrequency(Integer
					.parseInt(transmitfrequency));
			timingBackupTelnetConfig
					.setBackup_time_month(arrayToString(sendtimemonth));
			timingBackupTelnetConfig
					.setBackup_time_week(arrayToString(sendtimeweek));
			timingBackupTelnetConfig
					.setBackup_time_day(arrayToString(sendtimeday));
			timingBackupTelnetConfig
					.setBackup_time_hou(arrayToString(sendtimehou));
			timingBackupTelnetConfig.setBackup_date(Integer.parseInt(dt
					.getMyDateTime(DateTime.Datetime_Format_14)));
			timingBackupTelnetConfig.setStatus(status);
			timingBackupTelnetConfig.setBkpType(bkpType);
			timingBackupTelnetConfigDao.save(timingBackupTelnetConfig);
			timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/timingBackup.jsp";
	}

	/**
	 * 定时命令扫描日志备份
	 * 
	 * @return
	 */
	private String fileBackup() {
		TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
		TimingBackupTelnetConfig fileBackupTelnetConfig = new TimingBackupTelnetConfig();
		String ipaddress = getParaValue("ipaddress");
		String[] sendtimemonth = request.getParameterValues("sendtimemonth");
		String[] sendtimeweek = request.getParameterValues("sendtimeweek");
		String[] sendtimeday = request.getParameterValues("sendtimeday");
		String[] sendtimehou = request.getParameterValues("sendtimehou");
		String transmitfrequency = request.getParameter("transmitfrequency");
		String status = getParaValue("status");
		String bkpType = getParaValue("bkpType");
		String content = getParaValue("content");
		List<TimingBackupTelnetConfig> timingBackupTelnetConfigList = null;
		DateTime dt = new DateTime();
		try {
			fileBackupTelnetConfig.setTelnetconfigids(ipaddress);
			fileBackupTelnetConfig.setBackup_sendfrequency(Integer
					.parseInt(transmitfrequency));
			fileBackupTelnetConfig
					.setBackup_time_month(arrayToString(sendtimemonth));
			fileBackupTelnetConfig
					.setBackup_time_week(arrayToString(sendtimeweek));
			fileBackupTelnetConfig
					.setBackup_time_day(arrayToString(sendtimeday));
			fileBackupTelnetConfig
					.setBackup_time_hou(arrayToString(sendtimehou));
			fileBackupTelnetConfig.setBackup_date(Integer.parseInt(dt
					.getMyDateTime(DateTime.Datetime_Format_14)));
			fileBackupTelnetConfig.setStatus(status);
			fileBackupTelnetConfig.setBkpType(bkpType);
			fileBackupTelnetConfig.setContent(content);

			timingBackupTelnetConfigDao.save(fileBackupTelnetConfig);
			timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
			timingBackupTelnetConfigList = timingBackupTelnetConfigDao
					.getFileList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timingBackupTelnetConfigDao.close();
		}
		saveCondition(0);// 保存条件
		request.setAttribute("timingBackupTelnetConfigList",
				timingBackupTelnetConfigList);
		return "/config/vpntelnet/fileBackup.jsp";
	}

	private void saveCondition(int id) {
		String[] selVals = null;
		String[] textVals = null;
		String selVal = getParaValue("selVal");
		String textVal = getParaValue("textVal");
		TimingBackupConditionDao dao = null;
		try {

			dao = new TimingBackupConditionDao();
			TimingBackupCondition vo = null;
			dao.addBatch(vo, id);
			if (selVal != null && textVal != null) {
				selVals = new String[selVal.split(",").length];
				textVals = new String[textVal.split(",").length];
				selVals = selVal.split(",");
				textVals = textVal.split(",");
				if (selVals.length == textVals.length) {
					int key = 0;
					if (id == 0) {
						key = GeneratorKey.getInstance().getTimingKey();
					} else {
						key = id;
					}

					for (int i = 0; i < selVals.length; i++) {

						vo = new TimingBackupCondition();
						vo.setTimingId(key);
						vo.setIsContain(Integer.parseInt(selVals[i]));
						vo.setContent(textVals[i]);
						dao.addBatch(vo, -2);
					}

				}
			}
			dao.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	}

	/**
	 * 删除定时备份配置文件的任务
	 * 
	 * @return
	 */
	private String deleteTimingBackupTelnetConfig() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null) {
			if (ids != null && ids.length != 0) {
				TimingBackupTelnetConfigDao timingBackupTelnetConfigDao = new TimingBackupTelnetConfigDao();
				try {
					for (String id : ids) {
						if (id != null && !"".equals(id)) {
							timingBackupTelnetConfigDao.deleteById(id);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					timingBackupTelnetConfigDao.close();
				}
			}
		}
		return ready_timingBackup();
	}

	/**
	 * 删除定时命令日志文件的任务
	 * 
	 * @return
	 */
	private String deleteFileBackupTelnetConfig() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null) {
			if (ids != null && ids.length != 0) {
				TimingBackupTelnetConfigDao dao = new TimingBackupTelnetConfigDao();
				try {
					dao.delete(ids);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}

			}
		}
		return ready_fileBackup();
	}

	private String deletePasswdTimingBackupTelnetConfig() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null) {
			if (ids != null && ids.length != 0) {
				PasswdTimingBackupTelnetConfigDao passwdTimingBackupTelnetConfigDao = new PasswdTimingBackupTelnetConfigDao();
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

	public static String splitDate(String item, String[] itemCh, String type) {
		String[] idValue = null;
		String value = "";
		idValue = new String[item.split("/").length];
		idValue = item.split("/");

		for (int i = 0; i < idValue.length; i++) {
			if (!idValue[i].equals("")) {
				if (type.equals("week")) {
					value += itemCh[Integer.parseInt(idValue[i])];
				} else if (type.equals("day")) {
					value += (idValue[i] + "日 ");
				} else if (type.equals("hour")) {
					value += (idValue[i] + "时 ");
				} else {
					value += itemCh[Integer.parseInt(idValue[i]) - 1];
				}
			}
		}

		return value;
	}

	public String arrayToString(String[] array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (String value : array) {
				sb.append("/");
				sb.append(value);
			}
			sb.append("/");
		}
		return sb.toString();
	}

	// 定时执行页面中，点击选择设备，执行该方法
	private String multi_telnet_netip() {
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
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
		return "/config/vpntelnet/multi_telnet_netip.jsp";
	}

	// 定时执行页面中，点击选择告警方式，执行该方法
	private String multi_warn_netip() {
		String jsp = "/config/vpntelnet/chooselist.jsp";

		String alarmWayIdEvent = getParaValue("alarmWayIdEvent");

		String alarmWayNameEvent = getParaValue("alarmWayNameEvent");

		try {
			List list = getList();

			request.setAttribute("list", list);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		request.setAttribute("alarmWayIdEvent", alarmWayIdEvent);

		request.setAttribute("alarmWayNameEvent", alarmWayNameEvent);

		return jsp;
	}

	public List getList() {
		String sqlQuery = "";

		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			list(alarmWayDao, sqlQuery);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		List list = (List) request.getAttribute("list");
		return list;
	}

	// public String getSQLQueryForList(){
	// return "";
	// }

	public String serverip() {
		HostNodeDao dao = new HostNodeDao();
		List list = dao.loadServer();
		int listsize = list.size();
		request.setAttribute("iplist", list);
		HostNodeDao listdao = new HostNodeDao();
		setTarget("/config/vpntelnet/serverip.jsp");
		String page = list(listdao);
		JspPage jp = (JspPage) request.getAttribute("page");
		jp.setTotalRecord(listsize);
		request.setAttribute("page", jp);
		return page;
	}

	private String deployCfgForBatch() {
		String fileName = null;
		String serverFilePath = null;
		FileItem fileIntem = null;
		String ipAddresses = null;

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 10);
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		factory.setRepository(new File(prefix + "cfg\\batch"));// 设置服务器端保存路径
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1000000);
		String jsp = "/config/vpntelnet/status.jsp";
		try {
			List fileItems = upload.parseRequest(this.request);
			Iterator iter = fileItems.iterator(); // 依次处理每个控件
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();// 忽略其他是文件域的所有表单信息
				// System.out.println(item.getName());
				if (item.getName() != null) {
					fileIntem = item;
					System.out.println(fileIntem.getName());
					fileName = fileIntem.getName();
				}
				if (item.isFormField()) {
					if (item.getFieldName().equals("ipaddress"))// 文件名
					{
						ipAddresses = item.getString();
					}

				}
			}
			serverFilePath = prefix + "cfg\\\\batch\\\\" + fileName;
			fileIntem.write(new File(serverFilePath));

		} catch (Exception e) {
			e.printStackTrace();
			jsp = null;
			setErrorCode(0);// 未知错误
		}

		String[] split = ipAddresses.substring(1).split(",");
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 30, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30),
				new ThreadPoolExecutor.CallerRunsPolicy());
		String s = "";
		for (int i = 0; i < split.length; i++) {
			s = s + "," + split[i];
		}
		String s2 = s.substring(1);
		String sql = "select * from topo_node_telnetconfig where ip_address in('"
				+ s2.replace(",", "','") + "')";
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		List list = dao.findByCriteria(sql);
		dao.close();

		result.delete(0, result.length());
		for (int i = 0; i < split.length; i++) {
			threadPool.execute(new BatchDeployTask(result,
					(Huaweitelnetconf) list.get(i), fileName, serverFilePath));
		}
		threadPool.shutdown();
		try {
			boolean loop = true;
			do { // 等待所有任务完成
				loop = !threadPool.awaitTermination(2, TimeUnit.SECONDS);
			} while (loop);
		} catch (Exception e) {
		}

		System.out.println(result.toString());
		request.setAttribute("result", result.toString());
		return "/config/vpntelnet/multi_modify_status.jsp";
	}

	private String ready_deployCfgForBatch() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		String fileName = prefix + "cfg\\\\" + "_" + b_time + "batch.cfg";
		request.setAttribute("fileName", fileName);
		return "/config/vpntelnet/deployCfgForBatch.jsp";
	}

	private String bkpCfg_forBatch() {
		String bkpType = this.getParaValue("bkptype");
		result.delete(0, result.length());
		String fileName = this.getParaValue("fileName");
		String fileDesc = this.getParaValue("fileDesc");
		String ipAddresses = request.getParameter("ipaddress");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String bkptime = "";
		Date bkpDate = new Date();
		String reg = "_(.*)cfg.cfg";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(fileName);
		if (m.find()) {
			bkptime = m.group(1);
		}
		try {
			bkpDate = sdf.parse(bkptime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] split = ipAddresses.substring(1).split(",");
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 30, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30),
				new ThreadPoolExecutor.CallerRunsPolicy());
		String s = "";
		for (int i = 0; i < split.length; i++) {
			s = s + "," + split[i];
		}
		String s2 = s.substring(1);
		String sql = "select * from topo_node_telnetconfig where ip_address in('"
				+ s2.replace(",", "','") + "')";
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		List list = dao.findByCriteria(sql);
		dao.close();
		result.delete(0, result.length());
		System.out.println("filename=====>" + fileName);
		// String prefixFileName = fileName.substring(2);
		for (int i = 0; i < split.length; i++) {
			threadPool.execute(new BatchBackupTask(result,
					(Huaweitelnetconf) list.get(i), fileName, fileDesc,
					bkpDate, bkpType));
		}
		threadPool.shutdown();
		try {
			boolean loop = true;
			do { // 等待所有任务完成
				loop = !threadPool.awaitTermination(2, TimeUnit.SECONDS);
			} while (loop);
		} catch (Exception e) {
		}

		System.out.println(result.toString());
		request.setAttribute("result", result.toString());
		return "/config/vpntelnet/multi_modify_status.jsp";
	}

	private String ready_backupForBatch() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"/");
		String fileName = prefix + "cfg/" + "IP" + "_" + b_time + "cfg.cfg";
		System.out.println(fileName + "======back");
		request.setAttribute("fileName", fileName);
		return "/config/vpntelnet/batchBackup.jsp";
	}

	private String ready_multi_modify1() {
		return "/config/vpntelnet/multi_telnetmodifypassword.jsp";
	}

	private String ready_multi_modify() {
		String[] ids = this.getParaArrayValue("checkbox");
		if (ids != null) {
			String s = "";
			for (int i = 0; i < ids.length; i++) {
				s = s + "," + ids[i];
			}
			String s2 = s.substring(1);
			String sql = "select * from topo_node_telnetconfig where id in("
					+ s2 + ")";
			HaweitelnetconfDao dao = new HaweitelnetconfDao();
			List list = dao.findByCriteria(sql);
			dao.close();
			if (list.size() == 1)// 如果只勾选中一个，那么就进入单个网元的修改操作，下面这段代码和单个网元的修改操作相同
			{
				HaweitelnetconfDao telnetcfgdao = new HaweitelnetconfDao();
				// Huaweitelnetconf telnetCfg =
				// (Huaweitelnetconf)telnetcfgdao.findByID(id);
				Huaweitelnetconf telnetCfg = (Huaweitelnetconf) telnetcfgdao
						.findByID(s2);
				request.setAttribute("vpntelnetconf", telnetCfg);
				telnetcfgdao.close();
				if (telnetCfg.getDeviceRender().equals("h3c")) {
					request.setAttribute("deviceType", "h3c");
					return "/config/vpntelnet/telnetmodifypassword.jsp";
				} else // 思科
				{
					request.setAttribute("deviceType", "cisco");
					return "/config/vpntelnet/cisco_telnet_modify.jsp";
				}
			} else// 否则进入批量修改
			{
				Huaweitelnetconf vo = (Huaweitelnetconf) list.get(0);
				int num = 1;
				request.setAttribute("vpntelnetconf", vo);
				request.setAttribute("ipAddresses", s2);
				// HaweitelnetconfDao telnetcfgdao=new HaweitelnetconfDao();
				return "/config/vpntelnet/multi_telnetmodifypassword.jsp";
				/*
				 * for(int i = 1;i <list.size();i++) { Huaweitelnetconf t1 =
				 * (Huaweitelnetconf)list.get(i);
				 * //判断所选设备的初始密码是否相同，只有在相同的情况下才能执行批量操作
				 * if(vo.getUser().equals(t1.getUser()) &&
				 * vo.getPassword().equals(t1.getPassword()) &&
				 * vo.getSuuser().equals(t1.getSuuser()) &&
				 * vo.getSupassword().equals(t1.getSupassword()) &&
				 * vo.getDeviceRender().equals(t1.getDeviceRender())) { num++;
				 * continue; } else { break; } } if(num == list.size())//相等
				 * 说明满足条件，可以进行批量处理 { request.setAttribute("vpntelnetconf", vo);
				 * request.setAttribute("ipAddresses", s2);
				 * if(vo.getDeviceRender().equals("h3c")) {
				 * request.setAttribute("deviceType", "h3c"); return
				 * "/config/vpntelnet/multi_telnetmodifypassword.jsp"; }
				 * else//if(vo.getDeviceRender().equals("cisco"))//无法测试，未操作 {
				 * return ""; } } else { return
				 * "/config/vpntelnet/multi_modify_error.jsp"; }
				 */
			}
		} else
			return list();

	}

	// 密码修改信息
	private String ready_multi_audit() {
		HaweitelnetconfAuditDao haweitelnetconfAuditDao = new HaweitelnetconfAuditDao();

		this.list(haweitelnetconfAuditDao);
		List<HaweitelnetconfAudit> auditlist = (List<HaweitelnetconfAudit>) request
				.getAttribute("list");
		request.setAttribute("list", auditlist);
		return "/config/vpntelnet/passwdAudit.jsp";
	}

	// 审计查询
	private String queryByCondition() {
		// String key = getParaValue("key");
		// String value = getParaValue("value");
		// User current_user = (User)
		// session.getAttribute(SessionConstant.CURRENT_USER);
		//
		// StringBuffer s = new StringBuffer();
		// int _flag = 0;
		// if (current_user.getBusinessids() != null) {
		// if (current_user.getBusinessids() != "-1") {
		// String[] bids = current_user.getBusinessids().split(",");
		// if (bids.length > 0) {
		// for (int i = 0; i < bids.length; i++) {
		// if (bids[i].trim().length() > 0) {
		// if (_flag == 0) {
		// s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
		// _flag = 1;
		// } else {
		// // flag = 1;
		// s.append(" or bid like '%," + bids[i].trim() + ",%' ");
		// }
		// }
		// }
		// s.append(") and " + key + " like '%" + value + "%'");
		// }
		// }
		// }
		// request.setAttribute("actionlist", "list");
		// setTarget("/config/vpntelnet/passwdAudit.jsp");
		// HaweitelnetconfAuditDao haDao = new HaweitelnetconfAuditDao();
		// if (current_user.getRole() == 0) {
		// return list(haDao, "where 1=1 and " + key + " like '%" + value +
		// "%'");
		// } else {
		// return list(haDao, "where 1=1 " + s);
		// }

		String key = getParaValue("key");
		String value = getParaValue("value");
		HaweitelnetconfAuditDao haweitelnetconfAuditDao = new HaweitelnetconfAuditDao();

		request.setAttribute("key", key);
		request.setAttribute("value", value);
		setTarget("/config/vpntelnet/passwdAuditFind.jsp");
		return list(haweitelnetconfAuditDao, " where " + key + " = '" + value
				+ "'");
	}

	// 批量修改设备密码操作
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
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 30, 10,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30),
				new ThreadPoolExecutor.CallerRunsPolicy());
		String s = "";
		for (int i = 0; i < split.length; i++) {
			s = s + "," + split[i];
		}
		String s2 = s.substring(1);
		String sql = "select * from topo_node_telnetconfig where ip_address in('"
				+ s2.replace(",", "','") + "')";
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		List list = dao.findByCriteria(sql);
		dao.close();
		result.delete(0, result.length());
		for (int i = 0; i < list.size(); i++) {
			threadPool.execute(new BatchModifyTask(result,
					(Huaweitelnetconf) list.get(i), modifyuser, newpassword,
					threeA, encrypt));
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
		return "/config/vpntelnet/multi_modify_status.jsp";
	}

	// 执行批量添加操作
	private String batchAdd() {
		int isSynchronized = 1;
		String ipAddress = getParaValue("ipaddress");
		String threeA = this.getParaValue("threeA");
		String device_render = "unknow";
		int encrypt = this.getParaIntValue("encrypt");
		String ipAddressTmp = ipAddress.substring(1);
		String[] split = ipAddressTmp.split(",");
		String tmp = ipAddress.substring(1);
		HaweitelnetconfDao hdao = new HaweitelnetconfDao();
		boolean b = hdao.isExistsIp(tmp);
		hdao.close();
		if (b) {
			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
			return null;
		} else {
			HaweitelnetconfDao dao = new HaweitelnetconfDao();
			try {
				for (int k = 0; k < split.length; k++) {
					String temp1 = split[k];
					System.out.println(temp1);
					HostNodeDao hostNodeDao = new HostNodeDao();
					HostNode hostNode = null;
					BaseVo baseVo = null;
					try {
						baseVo = hostNodeDao.findByIpaddress(temp1);
						hostNode = (HostNode) baseVo;
					} catch (Exception e) {
						hostNode = null;
						isSynchronized = 0;// 说明数据不同步
					}
					hostNodeDao.close();
					if (hostNode != null) {
						HaweitelnetconfDao tmpDao = new HaweitelnetconfDao();
						tmpDao.delete(hostNode.getId() + "");
						tmpDao.close();
					}

					Huaweitelnetconf vo = new Huaweitelnetconf();

					// 设置ID
					if (hostNode != null) {
						vo.setId(hostNode.getId());
						String sysOid = hostNode.getSysOid();
						if (sysOid.startsWith("1.3.6.1.4.1.25506")
								|| sysOid.startsWith("1.3.6.1.4.1.2011"))// 华三
						{
							// vo.setDeviceRender("h3c");
							device_render = "h3c";
						} else if (sysOid.equals("1.3.6.1.4.1.9.1.209"))// 思科
						{
							// vo.setDeviceRender("cisco");
							device_render = "cisco";
						} else {
							// vo.setDeviceRender("unknow");
							device_render = "unknow";
						}
					} else {
						HaweitelnetconfDao dao1 = new HaweitelnetconfDao();
						int minId = dao1.getMinId();
						dao1.close();
						if (minId > 0)
							vo.setId(-1);
						else
							vo.setId(minId - 1);
					}
					//

					vo.setUser(getParaValue("user"));
					vo.setPassword(getParaValue("password"));
					vo.setSuuser(getParaValue("suuser"));
					vo.setSupassword(getParaValue("supassword"));
					vo.setIpaddress(temp1);
					vo.setPort(getParaIntValue("port"));
					vo.setDefaultpromtp(getParaValue("defaultpromtp"));
					vo.setEnablevpn(getParaIntValue("enablevpn"));
					vo.setIsSynchronized(isSynchronized);
					vo.setDeviceRender(device_render);
					vo.setThreeA(threeA);
					vo.setEncrypt(encrypt);
					dao.addBatch(vo);
				}
				dao.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			return "/vpntelnetconf.do?action=list";
		}
	}

	// 批量添加设备页面中，点击选择设备，执行该方法
	private String multi_netip() {
		HostNodeDao dao = new HostNodeDao();
		List list = dao.loadNetwork(-1);
		dao.close();
		// int listsize=list.size();
		request.setAttribute("list", list);
		/*
		 * HostNodeDao listdao=new HostNodeDao();
		 * setTarget("/config/vpntelnet/multi_netip.jsp"); String
		 * page=list(listdao); JspPage jp =
		 * (JspPage)request.getAttribute("page"); jp.setTotalRecord(listsize);
		 * request.setAttribute("page", jp);
		 */
		return "/config/vpntelnet/multi_netip.jsp";
	}

	// 跳转进批量添加页面
	private String ready_addForBatch() {
		return "/config/vpntelnet/batch_add.jsp";
	}

	// 同步数据
	private String synchronizeData() {
		HaweitelnetconfDao hdao = new HaweitelnetconfDao();
		// 把表topo_node_telnetconfig 中 is_synchronized=0 并且
		// ip_address=topo_host_node.ip_address 的数据项的id设置为表topo_host_node 中的 id
		String sql = "update topo_node_telnetconfig  set topo_node_telnetconfig.id=(select topo_host_node.id from topo_host_node where topo_node_telnetconfig.ip_address=topo_host_node.ip_address),topo_node_telnetconfig.is_synchronized=1,topo_node_telnetconfig.device_render=(select topo_host_node.sys_oid from topo_host_node where topo_node_telnetconfig.ip_address=topo_host_node.ip_address) where topo_node_telnetconfig.is_synchronized=0 and topo_node_telnetconfig.ip_address in(select topo_host_node.ip_address from topo_host_node)";
		System.out.println(sql);
		hdao.executeUpdate(sql);
		hdao.close();
		return list();
	}

	// 上传文件
	private String uploadFile() {
		String id = null;// Huaweitelnetconf 的主键ID
		String fileName = null;
		String fileDesc = null;
		String serverFilePath = null;
		FileItem fileIntem = null;

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 10);
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		factory.setRepository(new File(prefix + "cfg\\"));
		ServletFileUpload upload = new ServletFileUpload(factory);
		// upload.setHeaderEncoding("utf-8");
		upload.setSizeMax(1000000);
		String jsp = "/config/vpntelnet/status.jsp";
		try {
			List fileItems = upload.parseRequest(this.request);
			Iterator iter = fileItems.iterator(); // 依次处理每个控件
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();// 忽略其他是文件域的所有表单信息
				// System.out.println(item.getName());
				if (item.getName() != null) {
					fileIntem = item;
				}
				if (item.isFormField()) {
					if (item.getFieldName().equals("filena"))// 文件名
					{
						fileName = item.getString();
					}
					if (item.getFieldName().equals("fileName"))// 全路径
					{
						serverFilePath = item.getString();
					}
					if (item.getFieldName().equals("fileDesc")) {
						fileDesc = new String(item.getString().getBytes(
								"iso-8859-1"), "gbk");
						// fileDesc = item.getString();
					}
					if (item.getFieldName().equals("id")) {
						id = item.getString();
					}
					// System.out.println(item.getFieldName()+"="+item.getString());
					// //获得表单数据
				}
			}
			fileIntem.write(new File(serverFilePath));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
			String bkptime = "";
			Date bkpDate = new Date();
			String reg = "_(.*)cfg.cfg";
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(fileName);
			if (m.find()) {
				bkptime = m.group(1);
			}
			bkpDate = sdf.parse(bkptime);
			HaweitelnetconfDao dao = new HaweitelnetconfDao();
			Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(id);
			dao.close();
			HostNodeDao hostDao = new HostNodeDao();
			HostNode hostNode = (HostNode) hostDao.findByIpaddress(vo
					.getIpaddress());
			hostDao.close();

			File f = new File(serverFilePath);
			int fileSize = 0;
			FileInputStream fis = new FileInputStream(f);
			fileSize = fis.available();
			if (fileSize != 0) {
				fileSize = fileSize / 1000;
				if (fileSize == 0)
					fileSize = 1;
			}

			Hua3VPNFileConfig h3vpn = new Hua3VPNFileConfig();
			h3vpn.setFileName(serverFilePath);
			h3vpn.setDescri(fileDesc);
			h3vpn.setIpaddress(vo.getIpaddress());
			h3vpn.setFileSize(fileSize);
			h3vpn.setBackupTime(new Timestamp(bkpDate.getTime()));
			Hua3VPNFileConfigDao h3Dao = new Hua3VPNFileConfigDao();
			h3Dao.save(h3vpn);
			h3Dao.close();
			request.setAttribute("id", id);
		} catch (Exception e) {
			e.printStackTrace();
			jsp = null;
			setErrorCode(0);// 未知错误
		}
		return jsp;
	}

	// 跳转进上传文件页面
	private String readyuploadCfgFile() {
		String id = this.getParaValue("id");
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(id);
		dao.close();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		String fileName = prefix + "cfg\\\\" + vo.getIpaddress() + "_" + b_time
				+ "cfg.cfg";
		request.setAttribute("id", id);
		request.setAttribute("ipaddress", vo.getIpaddress());
		request.setAttribute("fileName", fileName);
		return "/config/vpntelnet/uploadfile.jsp";
	}

	private String updatepassword() {

		String deviceType = this.getParaValue("deviceType");
		SysLogger.info("deviceType=====" + deviceType);

		if (deviceType.equals("h3c"))// h3c
		{
			String id = this.getParaValue("id");
			String modifyuser = this.getParaValue("modifyuser");
			String newpassword = this.getParaValue("newpassword");
			int encrypt = this.getParaIntValue("encrypt");
			String threeA = this.getParaValue("threeA");

			HaweitelnetconfDao telnetcfgdao = new HaweitelnetconfDao();
			Huaweitelnetconf hmo = (Huaweitelnetconf) telnetcfgdao.findByID(id);
			telnetcfgdao.close();

			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(hmo.getSuuser());// su
			tvpn.setSupassword(hmo.getSupassword());// su密码
			tvpn.setUser(hmo.getUser());// 用户
			tvpn.setPassword(hmo.getPassword());// 密码
			tvpn.setIp(hmo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(hmo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(hmo.getPort());
			// boolean b = tvpn.Modifypassowd(modifyuser, newpassword);
			boolean b = tvpn.modifypassowd(modifyuser, newpassword, encrypt,
					threeA, hmo.getOstype());
			if (b) {
				if (modifyuser.equals("su")) {
					hmo.setSupassword(newpassword);
				} else {
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
				}
				HaweitelnetconfDao hdao = new HaweitelnetconfDao();
				hdao.update(hmo);
				hdao.close();
				return "/config/vpntelnet/status.jsp";
			} else {
				setErrorCode(0);
				return null;
			}

		}
		// return "";
		else if (deviceType.equals("cisco"))// cisco
		{
			String id = this.getParaValue("id");
			String modifyuser = this.getParaValue("modifyuser");
			String newpassword = this.getParaValue("newpassword");
			HaweitelnetconfDao telnetcfgdao = new HaweitelnetconfDao();

			HaweitelnetconfAudit hmoa = new HaweitelnetconfAudit();
			HaweitelnetconfAuditDao dao = new HaweitelnetconfAuditDao();
			User user = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
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
			Huaweitelnetconf hmo = null;
			try {
				hmo = (Huaweitelnetconf) telnetcfgdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				telnetcfgdao.close();
				dao.close();
			}

			CiscoTelnet ciscoTelnet = new CiscoTelnet(hmo.getIpaddress(), hmo
					.getUser(), hmo.getPassword(),hmo.getPort());
			if (ciscoTelnet.login()) {
				if (ciscoTelnet.modifyPasswd(hmo.getSupassword(), modifyuser,
						newpassword)) {
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
					HaweitelnetconfDao tdao = new HaweitelnetconfDao();
					try {
						tdao.update(hmo);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						tdao.close();
					}
					return "/config/vpntelnet/status.jsp";
				} else {
					setErrorCode(-1);
					// return null;
					SysLogger.info("===================3");
					return "/config/vpntelnet/error.jsp";
				}
			} else {
				setErrorCode(-1);
				SysLogger.info("===================1");
				return "/config/vpntelnet/error.jsp";
			}
		} else {
			SysLogger.info("===================2");
			setErrorCode(-1);
			return "/config/vpntelnet/error.jsp";
		}
	}

	// 右键菜单中的 修改密码 操作
	private String readyTelnetModify() {
		String id = this.getParaValue("id");
		HaweitelnetconfDao telnetcfgdao = new HaweitelnetconfDao();
		Huaweitelnetconf telnetCfg = (Huaweitelnetconf) telnetcfgdao
				.findByID(id);
		request.setAttribute("vpntelnetconf", telnetCfg);
		telnetcfgdao.close();
		if (telnetCfg.getDeviceRender().equals("h3c"))// 华三
		{
			request.setAttribute("deviceType", "h3c");
			return "/config/vpntelnet/telnetmodifypassword.jsp?deviceType=h3c";
		} else // if(sysOid.equals("1.3.6.1.4.1.9.1.209"))//思科
		{
			request.setAttribute("deviceType", "cisco");
			return "/config/vpntelnet/cisco_telnet_modify.jsp?deviceType=cisco";
		}
	}

	// private String deviceList1() {
	// List list = new ArrayList();
	// Hua3VPNFileConfigDao vpnFileDao = new Hua3VPNFileConfigDao();
	// // List vpnDevicelist = vpnFileDao.getDeviceWithLastModify();
	// this.list(vpnFileDao);
	// List vpnDevicelist = (List) request.getAttribute("list");
	// vpnFileDao.close();
	// HostNodeDao hostNodeDao = new HostNodeDao();
	// HaweitelnetconfDao haweiDao = new HaweitelnetconfDao();
	// for (int i = 0; i < vpnDevicelist.size(); i++) {
	// ConfiguringDevice cfgingDevice = new ConfiguringDevice();
	// Hua3VPNFileConfig vpnFileCfg = (Hua3VPNFileConfig) vpnDevicelist.get(i);
	// String ipaddress = vpnFileCfg.getIpaddress();
	// HostNode host = (HostNode) hostNodeDao.findByIpaddress(ipaddress);
	// Huaweitelnetconf telnetConf = (Huaweitelnetconf)
	// haweiDao.loadByIp(ipaddress);
	// String alias = host.getAlias();
	// cfgingDevice.setId(vpnFileCfg.getId());
	// cfgingDevice.setCategory(host.getCategory());
	// cfgingDevice.setAlias(alias);
	// cfgingDevice.setIpaddress(ipaddress);
	// cfgingDevice.setLastUpdateTime(vpnFileCfg.getBackupTime());
	// int temp = telnetConf.getEnablevpn();
	// cfgingDevice.setEnablevpn(temp);
	// cfgingDevice.setPrompt(telnetConf.getDefaultpromtp());
	// list.add(cfgingDevice);
	// }
	// hostNodeDao.close();
	// haweiDao.close();
	//
	// request.setAttribute("list", list);
	// // return "/config/vpntelnet/devicelist.jsp";
	// return "/config/vpntelnet/device_list.jsp";
	// }

	private String deviceList() {
		PasswdTimingBackupTelnetConfigDao passwdTimingBackupTelnetConfigDao = new PasswdTimingBackupTelnetConfigDao();
		List<PasswdTimingBackupTelnetConfig> passwdTimingBackupTelnetConfigList = null;
		try {
			passwdTimingBackupTelnetConfigList = passwdTimingBackupTelnetConfigDao
					.getAlList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			passwdTimingBackupTelnetConfigDao.close();
		}

		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String id = getParaValue("id");
		AlarmIndicatorsNode alarmIndicatorsNode = null;
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNode = (AlarmIndicatorsNode) alarmIndicatorsNodeDao
					.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Hashtable alarmWayHashtable = new Hashtable();

		if (alarmIndicatorsNode != null) {
			nodeid = alarmIndicatorsNode.getNodeid();
			type = alarmIndicatorsNode.getType();
			subtype = alarmIndicatorsNode.getSubtype();
			AlarmWayDao alarmWayDao = new AlarmWayDao();
			try {
				AlarmWay alarmWay0 = (AlarmWay) alarmWayDao
						.findByID(alarmIndicatorsNode.getWay0());
				if (alarmWay0 != null) {
					alarmWayHashtable.put("way0", alarmWay0);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				alarmWayDao.close();
			}
		}
		request.setAttribute("alarmWayHashtable", alarmWayHashtable);
		request.setAttribute("alarmIndicatorsNode", alarmIndicatorsNode);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("list", passwdTimingBackupTelnetConfigList);
		return "/config/vpntelnet/device_list.jsp";
	}

	private String download() {
		String id = this.getParaValue("id");
		Hua3VPNFileConfigDao h3Dao = new Hua3VPNFileConfigDao();
		Hua3VPNFileConfig h3 = (Hua3VPNFileConfig) h3Dao.findByID(id);
		h3Dao.close();
		String filename = h3.getFileName();
		request.setAttribute("filename", filename);
		return "/capreport/net/download.jsp";
	}

	private String delete() {
		String[] id = getParaArrayValue("checkbox");
		if (id != null) {
			SlaCfgCmdFileDao dao = new SlaCfgCmdFileDao();
			try{
				dao.delete(id);
			}catch(Exception e){
				
			}finally{
				dao.close();
			}
			//DaoInterface dao = new HaweitelnetconfDao();
			//setTarget("/sla.do?action=list&jp=1");
		}
		String target = "/slacmd.do?action=loadfilelist&jp=1";
		return target;

	}

	// 删除定时备份文件
	private String deleteFile() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null) {

			Hua3VPNFileConfigDao vpnDao = new Hua3VPNFileConfigDao();
			if (ids != null && ids.length != 0) {

				List vpnConfigList = vpnDao.loadByIds(ids);
				for (int j = 0; j < vpnConfigList.size(); j++) {
					Hua3VPNFileConfig tmp = (Hua3VPNFileConfig) vpnConfigList
							.get(j);
					if (tmp != null) {
						File f = new File(tmp.getFileName());
						if (f.exists())
							f.delete();
					}
				}

			}
			vpnDao.delete(ids);
			vpnDao.close();

		}
		return showAllFile();

	}

	// 删除定时命令日志文件
	private String deleteLogFile() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null) {

			VPNFileConfigDao vpnDao = new VPNFileConfigDao();
			if (ids != null && ids.length != 0) {

				List vpnConfigList = vpnDao.loadByIds(ids);
				for (int j = 0; j < vpnConfigList.size(); j++) {
					VPNFileConfig tmp = (VPNFileConfig) vpnConfigList.get(j);
					if (tmp != null) {
						File f = new File(tmp.getFileName());
						if (f.exists())
							f.delete();
					}
				}

			}
			vpnDao.delete(ids);
			vpnDao.close();

		}
		return configFileList();

	}

	// 删除扫描命令
	private String deleteFileByIp() {
		String[] ip = getParaArrayValue("checkbox");
		if (ip != null) {

			// HaweitelnetconfDao telnetcfgdao = new HaweitelnetconfDao();
			// List telnetcfgList = telnetcfgdao.loadByIps(ip);
			// telnetcfgdao.close();
			VPNFileConfigDao vpnDao = new VPNFileConfigDao();
			if (ip != null && ip.length != 0) {
				List vpnConfigList = vpnDao.loadByIps(ip);
				String[] vpncol = new String[vpnConfigList.size()];
				for (int j = 0; j < vpnConfigList.size(); j++) {
					VPNFileConfig tmp = (VPNFileConfig) vpnConfigList.get(j);
					vpncol[j] = new Integer(tmp.getId()).toString();
					File f = new File(tmp.getFileName());
					f.delete();
				}
				vpnDao.deleteFileByIps(ip);

			}
			vpnDao.close();
			// DaoInterface dao = new VPNFileConfigDao();
			// setTarget("/vpntelnetconf.do?action=configFilelist&jp=1");
			// return "/vpntelnetconf.do?action=filelist&jp=1";
		}

		return fileList();

	}

	// 修改基线
	private String editBaseLine() {
		String id = getParaValue("id");
		Hua3VPNFileConfigDao vpnDao = new Hua3VPNFileConfigDao();
		vpnDao.updateBaseLine(id, 2);
		vpnDao.updateBaseLine(id, 1);
		vpnDao.close();

		return showAllFile();
	}

	// 配置文件比对
	private String compareFile() {
		String filename = getParaValue("filename");
		String baseFileName = "";
		Hua3VPNFileConfigDao dao = new Hua3VPNFileConfigDao();
		List list = (List) dao.findByCondition(" where baseline=1");
		if (list != null) {
			Hua3VPNFileConfig config = (Hua3VPNFileConfig) list.get(0);
			baseFileName = config.getFileName();
		}
		FileReader fr = null;
		BufferedReader br = null;
		String lineStr = "";
		StringBuffer content = new StringBuffer();
		StringBuffer baseContent = new StringBuffer();
		int i = 1, j = 1;
		File baseFile = new File(baseFileName);
		File cmpFile = new File(filename);
		String baseCfgName = "", cmpCfgName = "";

		if (baseFile.exists() && cmpFile.exists()) {
			try {
				fr = new FileReader(baseFileName);
				br = new BufferedReader(fr);
				try {
					while (null != (lineStr = br.readLine())) {
						baseContent.append(i++).append(".  ").append(
								lineStr + "\r\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			lineStr = "";
			try {
				fr = new FileReader(filename);
				br = new BufferedReader(fr);
				try {
					while (null != (lineStr = br.readLine())) {
						content.append(j++).append(".  ").append(
								lineStr + "\r\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			String[] baseCfgNames = new String[baseFileName.split("/").length];
			baseCfgNames = baseFileName.split("/");
			if (baseCfgNames.length > 1) {
				baseCfgName = baseCfgNames[baseCfgNames.length - 1];
			} else {
				baseCfgNames = new String[baseFileName.split("\\\\").length];
				baseCfgNames = baseFileName.split("\\\\");
				if (baseCfgNames.length > 0)
					baseCfgName = baseCfgNames[baseCfgNames.length - 1];
			}
			String[] cmpCfgNames = new String[filename.split("/").length];
			cmpCfgNames = filename.split("/");
			if (cmpCfgNames.length > 1) {
				cmpCfgName = cmpCfgNames[cmpCfgNames.length - 1];
			} else {
				cmpCfgNames = new String[filename.split("\\\\").length];
				cmpCfgNames = filename.split("\\\\");
				if (cmpCfgNames.length > 0)
					cmpCfgName = cmpCfgNames[cmpCfgNames.length - 1];

			}
		}
		request.setAttribute("baseCfgName", baseCfgName);
		request.setAttribute("cmpCfgName", cmpCfgName);
		request.setAttribute("content", content.toString());
		request.setAttribute("baseContent", baseContent.toString());
		return "/config/vpntelnet/compareFile.jsp";
	}

	private String deviceCfg() {
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		List list = null;
		//SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			list = haweitelnetconfDao.getAllTelnetConfig();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			haweitelnetconfDao.close();
		}
		request.setAttribute("list", list);
		return "/config/ciscosla/exeScript.jsp";
	}

	// private String exeScriptLog() {
	// HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
	// List list = null;
	// SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	// try {
	// list = haweitelnetconfDao.getAllTelnetConfig();
	// } catch (RuntimeException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// haweitelnetconfDao.close();
	// }
	// request.setAttribute("list", list);
	// return "/config/vpntelnet/exeScriptLog.jsp";
	//
	//	
	// }
	private String exeCmd() {
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		List deviceList = null;
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			deviceList = haweitelnetconfDao.getAllTelnetConfig();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			haweitelnetconfDao.close();
		}
		request.setAttribute("list", deviceList);
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		// String ip=getParaValue("netip");
		String[] ips = getParaArrayValue("checkbox");

		String commands = getParaValue("commands");
		String cmdid = getParaValue("cmdid");
		String slatype = getParaValue("slatype");
		String isReturn = "0";
		//ICMP
		String icmp_destip = getParaValue("icmp_destip");
		String icmp_datapacket = getParaValue("icmp_datapacket");
		String icmp_tos = getParaValue("icmp_tos");
		//icmppath
		String icmppath_destip = getParaValue("icmppath_destip");
		String icmppath_rate = getParaValue("icmppath_rate");
		String icmppath_history = getParaValue("icmppath_history");
		String icmppath_buckets = getParaValue("icmppath_buckets");
		String icmppath_life = getParaValue("icmppath_life");
		//UDP
		String udp_destip = getParaValue("udp_destip");
		String udp_destport = getParaValue("udp_destport");
		//Jitter
		String jitter_destip = getParaValue("jitter_destip");
		String jitter_destport = getParaValue("jitter_destport");
		String jitter_numpacket = getParaValue("jitter_numpacket");
		String jitter_interval = getParaValue("jitter_interval");
		//tcpconnectwithresponder
		String tcpconnectwithresponder_destip = getParaValue("tcpconnectwithresponder_destip");
		String tcpconnectwithresponder_destport = getParaValue("tcpconnectwithresponder_destport");
		String tcpconnectwithresponder_tos = getParaValue("tcpconnectwithresponder_tos");
		//tcpconnectnoresponder
		String tcpconnectnoresponder_destip = getParaValue("tcpconnectnoresponder_destip");
		String tcpconnectnoresponder_destport = getParaValue("tcpconnectnoresponder_destport");
		//HTTP
		String http_urlconnect = getParaValue("http_urlconnect");
		//DNS
		String dns_destip = getParaValue("dns_destip");
		String dns_dnsserver = getParaValue("dns_dnsserver");
		
		Hashtable slaParamHash = new Hashtable();
		slaParamHash.put("icmp_destip", icmp_destip);
		slaParamHash.put("icmp_datapacket", icmp_datapacket);
		slaParamHash.put("icmp_tos", icmp_tos);
		slaParamHash.put("icmppath_destip", icmppath_destip);
		slaParamHash.put("icmppath_rate", icmppath_rate);
		slaParamHash.put("icmppath_history", icmppath_history);
		slaParamHash.put("icmppath_buckets", icmppath_buckets);
		slaParamHash.put("icmppath_life", icmppath_life);
		slaParamHash.put("udp_destip", udp_destip);
		slaParamHash.put("udp_destport", udp_destport);
		slaParamHash.put("jitter_destip", jitter_destip);
		slaParamHash.put("jitter_destport", jitter_destport);
		slaParamHash.put("jitter_numpacket", jitter_numpacket);
		slaParamHash.put("jitter_interval", jitter_interval);
		slaParamHash.put("tcpconnectwithresponder_destip", tcpconnectwithresponder_destip);
		slaParamHash.put("tcpconnectwithresponder_destport", tcpconnectwithresponder_destport);
		slaParamHash.put("tcpconnectwithresponder_tos", tcpconnectwithresponder_tos);
		slaParamHash.put("tcpconnectnoresponder_destip", tcpconnectnoresponder_destip);
		slaParamHash.put("tcpconnectnoresponder_destport", tcpconnectnoresponder_destport);
		slaParamHash.put("http_urlconnect", http_urlconnect);
		slaParamHash.put("dns_destip", dns_destip);
		slaParamHash.put("dns_dnsserver", dns_dnsserver);
		
		if (isReturn == null)
			isReturn = "0";
		String result = "";
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		List<Huaweitelnetconf> list = new ArrayList<Huaweitelnetconf>();
		StringBuffer sBuffer = new StringBuffer();
		List<CmdResult> resultList = new ArrayList<CmdResult>();
		CiscoSlaCfgCmdFile slaconfig = null;
		SlaCfgCmdFileDao slaconfigdao = new SlaCfgCmdFileDao();
		try{
			slaconfig = (CiscoSlaCfgCmdFile)slaconfigdao.findByID(cmdid);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			slaconfigdao.close();
		}
		if (ips != null && ips.length > 0) {

			try {
				list = (List) dao.loadByIps(ips);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			String[] commStr = new String[commands.split("\r\n").length];
			commStr = commands.split("\r\n");

			for (int i = 0; i < list.size(); i++) {
				vo = list.get(i);

				if (vo.getDeviceRender().equals("h3c")) {// h3c
//					Huawei3comvpn tvpn = new Huawei3comvpn();
//					tvpn.setSuuser(vo.getSuuser());// su
//					tvpn.setSupassword(vo.getSupassword());// su密码
//					tvpn.setUser(vo.getUser());// 用户
//					tvpn.setPassword(vo.getPassword());// 密码
//					tvpn.setIp(vo.getIpaddress());// ip地址
//					tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
//					tvpn.setPort(vo.getPort());
//					if (isReturn.equals("0")) {
//						tvpn.getCommantValue(commStr, resultList, ips[i]);
//					} else if (isReturn.equals("1")) {
//						result = tvpn.BackupConfFile(commStr);
//					}
				} else if (vo.getDeviceRender().equals("cisco")) {// cisco
					CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo
							.getUser(), vo.getPassword(),vo.getPort());
					if (telnet.login()) {
						if (isReturn.equals("0")) {
							telnet.getSlaCommantValue(vo.getSupassword(), commStr,
									resultList, ips[i],user,slaconfig,slaParamHash);
						} else if (isReturn.equals("1")) {
							result = telnet.getFileCfg(vo.getSupassword(), commStr);
						}	
					} else {
						CmdResult cmdResult = new CmdResult();
						cmdResult.setIp(ips[i]);
						cmdResult.setCommand("------");
						cmdResult.setResult("登录失败!");
						resultList.add(cmdResult);
					}

				}
				sBuffer.append(result + "\r\n");
			}
		}

		request.setAttribute("commands", commands);
		request.setAttribute("isReturn", isReturn);
		request.setAttribute("ips", ips);
		request.setAttribute("content", sBuffer.toString());
		request.setAttribute("resultList", resultList);
		return "/config/ciscosla/exeScriptLog.jsp";
	}

	public String downloadLog() {
		String result = getParaValue("content");
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"/");
		String filename = prefix + "cfg\\\\cfgLog.log";
		this.backVpnConfig(filename, result);
		request.setAttribute("filename", filename);
		return "/capreport/net/download.jsp";

	}

	/*
	 * private String bkpCfg() { String id = getParaValue("id"); String
	 * ipaddress=getParaValue("ipaddress"); String page = getParaValue("page");
	 * HaweitelnetconfDao Hdao=new HaweitelnetconfDao(); String
	 * idrs=Hdao.findbyip(ipaddress); Hdao.close(); HaweitelnetconfDao dao = new
	 * HaweitelnetconfDao(); Huaweitelnetconf vo =
	 * (Huaweitelnetconf)dao.findByID(idrs); dao.close(); Huawei3comvpn tvpn =
	 * new Huawei3comvpn(); tvpn.setSuuser(vo.getSuuser());// su
	 * tvpn.setSupassword(vo.getSupassword());// su密码
	 * tvpn.setUser(vo.getUser());// 用户 tvpn.setPassword(vo.getPassword());// 密码
	 * tvpn.setIp(vo.getIpaddress());// ip地址
	 * tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号 tvpn.setPort(23);
	 * //String result = tvpn.Getcommantvalue("disp cu");
	 * //tvpn.Backupconffile(); String result = "adsfasdf\r\nadsfasdf\r\n";
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm"); String
	 * b_time = sdf.format(new Date()); String prefix =
	 * ResourceCenter.getInstance().getSysPath().replace("\\", "\\\\"); String
	 * fileName = prefix + "cfg\\\\"+vo.getIpaddress()+"_"+b_time+"cfg.cfg";
	 * File f = new File(fileName); //f.mkdir(); try{ FileWriter fw = new
	 * FileWriter(f); fw.write(result); fw.flush(); fw.close(); //PrintWriter pw =
	 * new PrintWriter(new BufferedWriter(new FileWriter(f)));
	 * //pw.print(result); //pw.close(); }catch(Exception
	 * e){e.printStackTrace();} request.setAttribute("id", id);
	 * PollMonitorManager pm = new PollMonitorManager(); pm.setRequest(request);
	 * String jsp = ""; if(page.equals("liusu")) { jsp =
	 * "/topology/network/networkview.jsp?flag=0"; return jsp; } else
	 * if(page.equals("netcpu")) { return pm.execute("netcpu"); } else
	 * if(page.equals("netarp")) { return pm.execute("netarp"); } else
	 * if(page.equals("netfdb")) { return pm.execute("netfdb"); }
	 * if(page.equals("netroute")) { return pm.execute("netroute"); } else
	 * if(page.equals("netiplist")) { return pm.execute("netiplist"); } else
	 * if(page.equals("netenv")) { return pm.execute("netenv"); } else
	 * if(page.equals("netevent")) { return pm.execute("netevent"); } else {
	 * return pm.execute("netevent"); } }
	 */
	private String bkpCfg_1() {
		String bkpType = this.getParaValue("bkptype");
		String id = getParaValue("id");// Huaweitelnetconf 的主键ID
		String fileName = this.getParaValue("fileName");
		String fileDesc = this.getParaValue("fileDesc");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String bkptime = "";
		Date bkpDate = new Date();
		String reg = "_(.*)cfg.cfg";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(fileName);
		if (m.find()) {
			bkptime = m.group(1);
		}
		try {
			bkpDate = sdf.parse(bkptime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(id);
		dao.close();

		String result = "";
		String runBackFileResult = "";// 备份运行时配置文件的结果字符串
		String startupBackFileResult = "";// 备份启动时配置文件的结果字符串
		String jsp = null;
		if (vo.getDeviceRender().equals("h3c"))// h3c
		{
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
			if ("run".equals(bkpType) || "startup".equals(bkpType)) {
				result = tvpn.backupConfFile(bkpType);
			} else {// bkpType 为 all的情况
				Huawei3comvpn secondTvpn = new Huawei3comvpn();
				secondTvpn.setSuuser(vo.getSuuser());// su
				secondTvpn.setSupassword(vo.getSupassword());// su密码
				secondTvpn.setUser(vo.getUser());// 用户
				secondTvpn.setPassword(vo.getPassword());// 密码
				secondTvpn.setIp(vo.getIpaddress());// ip地址
				secondTvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
				secondTvpn.setPort(vo.getPort());
				runBackFileResult = tvpn.backupConfFile("run");
				startupBackFileResult = secondTvpn.backupConfFile("startup");
			}
			if (!result.equals("user or password error")) {
				jsp = "/config/vpntelnet/status.jsp";
			}
			// System.out.println("############################################123");
			// System.out.println(result);
		} else if (vo.getDeviceRender().equals("cisco"))// cisco
		{

			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword());

			if (telnet.login()) {
				if ("run".equals(bkpType) || "startup".equals(bkpType)) {
					result = telnet.getCfg( bkpType);
				} else {// bkpType 为 all的情况
					CiscoTelnet secondTelnet = new CiscoTelnet(vo
							.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort());
					runBackFileResult = telnet.getCfg( "run");
					if (secondTelnet.login()) {
						startupBackFileResult = secondTelnet.getCfg("startup");
					}
				}
				jsp = "/config/vpntelnet/status.jsp";
			}
		}
		if (jsp != null) {
			if ("run".equals(bkpType) || "startup".equals(bkpType)) {
				backVpnConfig(bkpType, fileName, fileDesc, bkpDate, vo, result);
			} else {// bkpType = all
				backVpnConfig("run", fileName, fileDesc, bkpDate, vo,
						runBackFileResult);
				String startupFileName = "";
				if (fileName.lastIndexOf(".") != -1) {
					startupFileName = fileName.substring(0,
							fileName.lastIndexOf(".")).concat("(2)").concat(
							fileName.substring(fileName.lastIndexOf(".")));
				} else {
					startupFileName = startupFileName.concat("(2)");
				}
				backVpnConfig("startup", startupFileName, fileDesc, bkpDate,
						vo, startupBackFileResult);
			}
		}

		request.setAttribute("id", id);
		if (jsp == null) {
			this.setErrorCode(1003);// 用户名或密码错误
		}
		return jsp;
	}

	/**
	 * @param bkpType
	 *            备份的文件类型
	 * @param fileName
	 *            文件名
	 * @param fileDesc
	 *            描述
	 * @param bkpDate
	 *            备份日期
	 * @param vo
	 * @param result
	 *            文件内容
	 */
	public synchronized void backVpnConfig(String bkpType, String fileName,
			String fileDesc, Date bkpDate, Huaweitelnetconf vo, String result) {
		File f = new File(fileName);
		int fileSize = 0;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			fw.write(result);
			fw.flush();
			fw.close();
			FileInputStream fis = new FileInputStream(f);
			fileSize = fis.available();
			if (fileSize != 0) {
				fileSize = fileSize / 1000;
				if (fileSize == 0)
					fileSize = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hua3VPNFileConfig h3vpn = new Hua3VPNFileConfig();
		h3vpn.setFileName(fileName);
		h3vpn.setDescri(fileDesc);
		h3vpn.setIpaddress(vo.getIpaddress());
		h3vpn.setFileSize(fileSize);
		h3vpn.setBackupTime(new Timestamp(bkpDate.getTime()));

		if (bkpType.equals("0")) {// 备份的是命令文件
			VPNFileConfig vpn = new VPNFileConfig();
			vpn.setTimingId(vo.getId());
			vpn.setFileName(fileName);
			vpn.setContent(fileDesc);
			vpn.setIpaddress(vo.getIpaddress());
			vpn.setFileSize(fileSize);
			vpn.setBackupTime(new Timestamp(bkpDate.getTime()));
			vpn.setBkpType(vo.getDeviceRender());
			VPNFileConfigDao dao = new VPNFileConfigDao();
			dao.save(vpn);
			dao.close();
		} else {
			h3vpn.setBkpType(bkpType);
			Hua3VPNFileConfigDao h3Dao = new Hua3VPNFileConfigDao();
			h3Dao.save(h3vpn);
			h3Dao.close();
		}

	}

	public synchronized void backVpnConfig(String fileName, String result) {
		File f = new File(fileName);
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

	}

	private String readySetupConfig() {
		String id = this.getParaValue("id");
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(id);
		dao.close();
		String ipaddress = this.getParaValue("ipaddress");
		Hua3VPNFileConfigDao h3Dao = new Hua3VPNFileConfigDao();
		List list = h3Dao.loadByIp(ipaddress);
		h3Dao.close();
		request.setAttribute("list", list);
		request.setAttribute("id", id);
		request.setAttribute("ipaddress", ipaddress);
		return "/config/vpntelnet/setupConfig_cisco.jsp";
	}

	private String readyBackupConfig() {
		String id = this.getParaValue("id");
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(id);
		dao.close();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		String fileName = prefix + "cfg\\\\" + vo.getIpaddress() + "_" + b_time
				+ "cfg.cfg";
		request.setAttribute("id", id);
		request.setAttribute("ipaddress", vo.getIpaddress());
		request.setAttribute("fileName", fileName);
		return "/config/vpntelnet/backup_cisco.jsp";
	}

	private String detailPageReadyBackupConfig() {
		String ipaddress = this.getParaValue("ipaddress");
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.loadByIp(ipaddress);
		String id = new Integer(vo.getId()).toString();
		dao.close();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String b_time = sdf.format(new Date());
		String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
				"\\\\");
		String fileName = prefix + "cfg\\\\" + vo.getIpaddress() + "_" + b_time
				+ "cfg.cfg";
		request.setAttribute("id", id);
		request.setAttribute("ipaddress", vo.getIpaddress());
		request.setAttribute("fileName", fileName);
		return "/config/vpntelnet/backup.jsp";
	}

	private String setupConfig() {
		// String id = getParaValue("id");
		String ipaddress = getParaValue("ipaddress");
		String page = getParaValue("page");
		HaweitelnetconfDao Hdao = new HaweitelnetconfDao();
		String idrs = Hdao.findbyip(ipaddress);
		Hdao.close();
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(idrs);
		dao.close();
		Huawei3comvpn tvpn = new Huawei3comvpn();
		tvpn.setSuuser(vo.getSuuser());// su
		tvpn.setSupassword(vo.getSupassword());// su密码
		tvpn.setUser(vo.getUser());// 用户
		tvpn.setPassword(vo.getPassword());// 密码
		tvpn.setIp(vo.getIpaddress());// ip地址
		tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
		tvpn.setPort(23);
		// String result = tvpn.Getcommantvalue("disp cu");

		String filePath = this.getParaValue("filePath");
		File f = new File(filePath);
		StringBuffer content = new StringBuffer();
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			while ((s = br.readLine()) != null) {
				content.append(s);
			}
			System.out.println("content=" + content.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		tvpn.Writeconffile(content.toString());

		PollMonitorManager pm = new PollMonitorManager();
		pm.setRequest(request);
		String jsp = "";
		if (page.equals("liusu")) {
			jsp = "/topology/network/networkview.jsp?flag=0";
			return jsp;
		} else if (page.equals("netcpu")) {
			return pm.execute("netcpu");
		} else if (page.equals("netarp")) {
			return pm.execute("netarp");
		} else if (page.equals("netfdb")) {
			return pm.execute("netfdb");
		}
		if (page.equals("netroute")) {
			return pm.execute("netroute");
		} else if (page.equals("netiplist")) {
			return pm.execute("netiplist");
		} else if (page.equals("netenv")) {
			return pm.execute("netenv");
		} else {
			return pm.execute("netevent");
		}
	}

	private String setupConfig_1() {
		String id = getParaValue("id");
		String radio = this.getParaValue("radio");

		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = (Huaweitelnetconf) dao.findByID(id);
		dao.close();

		Hua3VPNFileConfigDao h3Dao = new Hua3VPNFileConfigDao();
		Hua3VPNFileConfig vpncfg = (Hua3VPNFileConfig) h3Dao.findByID(radio);
		h3Dao.close();

		String filePath = vpncfg.getFileName();
		String remoteFileName = filePath
				.substring(filePath.lastIndexOf("\\") + 1);
		/*
		 * File f = new File(filePath); StringBuffer content = new
		 * StringBuffer(); try{ FileReader fr = new FileReader(f);
		 * BufferedReader br = new BufferedReader(fr); String s = null;
		 * while((s=br.readLine())!=null) { content.append(s+"\r\n"); }
		 * 
		 * System.out.println("content="+content.toString()); br.close();
		 * fr.close(); }catch(Exception e){e.printStackTrace();}
		 */

		String jsp = null;
		if (vo.getDeviceRender().equals("h3c"))// h3c的sysoid
		{
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
			// tvpn.Backupconffile();
			// boolean b = tvpn.Writeconffile(content.toString());
			boolean b = tvpn.setupNewConfFile("/" + remoteFileName, filePath,
					vo.getIpaddress(), 21, vo.getUser(), vo.getPassword());
			if (b) {
				jsp = "/config/vpntelnet/status.jsp";
			} else {
				this.setErrorCode(1003);// 用户名或密码错误
			}
		} else if (vo.getDeviceRender().equals("cisco"))// cisco的sysoid
		{
			CiscoTelnet telnet = new CiscoTelnet("172.25.25.240", "1", "1",23);
			// telnet.writeCfgFile(content.toString());
		}

		// tvpn.Writeconffile(content.toString());
		return jsp;
	}

	private String update() {
		CiscoSlaCfgCmdFile vo = new CiscoSlaCfgCmdFile();
		SlaCfgCmdFileDao dao = new SlaCfgCmdFileDao();
		String id = getParaValue("id");
		try{
			vo = (CiscoSlaCfgCmdFile)dao.findByID(id);
			vo.setName(getParaValue("name"));
			vo.setFileDesc(getParaValue("fileDesc"));
			vo.setSlatype(getParaValue("slatype"));
			vo.setDevicetype(getParaValue("devicetype"));
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			vo.setCreateBy(user.getName());
			String content = getParaValue("commands");
			String filename = vo.getName();
			String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
			"/");
			//String fileName = b_time + ".log";
			String filePath = prefix + "slascript/"+vo.getDevicetype()+"/" + filename+".log";
			vo.setFilename(filePath);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			vo.setCreateTime(sdf.format(date));
			dao.update(vo);
			File f = new File(filePath);
			String result = content.replaceAll(";;", "\r\n");
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
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		String target = "/slacmd.do?action=loadfilelist&jp=1";
		return target;
	}

	/***************************************************************************
	 * 
	 * 修改telnet 的用户的密码，如果修改成功则telnet 连接的用户密码更新到数据库表但不对enable字段进行修改
	 * 
	 * @return
	 */
	public String modifypassword() {
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf hmo = new Huaweitelnetconf();
		Huawei3comvpn tvpn = new Huawei3comvpn();

		hmo.setId(Integer.parseInt(getParaValue("id")));
		hmo.setUser(getParaValue("user"));
		hmo.setIpaddress(getParaValue("ipaddress"));
		hmo.setSupassword(getParaValue("supassword"));
		hmo.setSuuser(getParaValue("suuser"));
		hmo.setPort(Integer.parseInt(getParaValue("port")));
		hmo.setDefaultpromtp(getParaValue("defaultpromtp"));
		hmo.setPassword(getParaValue("password"));
		hmo.setDefaultpromtp(getParaValue("defaultpromtp"));

		String modifyuser = getParaValue("modifyuser");
		String newpassword = getParaValue("newpassword");
		int encrypt = Integer.parseInt(getParaValue("encrypt"));
		String threeA = getParaValue("threeA");

		try {
			tvpn.setDEFAULT_TELNET_PORT(hmo.getPort());// 端口
			tvpn.setSuuser(hmo.getSuuser());// su
			tvpn.setSupassword(hmo.getSupassword());// su密码
			tvpn.setUser(hmo.getUser());// 用户
			tvpn.setPassword(hmo.getPassword());// 密码
			tvpn.setIp(hmo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(hmo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(hmo.getPort());
			boolean mflg = tvpn.modifypassowd(modifyuser, newpassword, encrypt,
					threeA, hmo.getOstype());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/vpntelnetconf.do?action=findpassword&id=" + hmo.getId()
				+ "&ipaddress=" + hmo.getIpaddress();
	}

	public String list() {
		List configingDeviceList = new ArrayList();
		Hua3VPNFileConfigDao vpnFileDao = new Hua3VPNFileConfigDao();
		List vpnDevicelist = vpnFileDao.getDeviceWithLastModify();
		vpnFileDao.close();

		HaweitelnetconfDao telnetConfDao = new HaweitelnetconfDao();
		this.list(telnetConfDao);
		List telnetConfList = (List) request.getAttribute("list");
		int vpnDevicelistSize = 0;
		int tmp = 0;
		Hua3VPNFileConfig tmp2 = null;
		HostNodeDao hostDao = new HostNodeDao();
		HostNode hostNode = null;
		for (int i = 0; i < telnetConfList.size(); i++) {
			ConfiguringDevice cfgingDevice = new ConfiguringDevice();
			Huaweitelnetconf telnetConf = (Huaweitelnetconf) telnetConfList
					.get(i);
			cfgingDevice.setId(telnetConf.getId());
			cfgingDevice.setIpaddress(telnetConf.getIpaddress());
			cfgingDevice.setPrompt(telnetConf.getDefaultpromtp());
			cfgingDevice.setEnablevpn(telnetConf.getEnablevpn());
			cfgingDevice.setIsSynchronized(telnetConf.getIsSynchronized());
			cfgingDevice.setDeviceRender(telnetConf.getDeviceRender());
			vpnDevicelistSize = vpnDevicelist.size();
			tmp = 0;

			while (vpnDevicelistSize > tmp)// 有备份文件的设备就装填上最后一次的备份时间，无备份文件的设备该字段填null
			{
				tmp2 = (Hua3VPNFileConfig) vpnDevicelist.get(tmp);
				if (tmp2.getIpaddress().equals(telnetConf.getIpaddress())) {
					cfgingDevice.setLastUpdateTime(tmp2.getBackupTime());
					break;
				}
				tmp++;
				if (vpnDevicelistSize == tmp) {
					cfgingDevice.setLastUpdateTime(null);
				}
			}
			configingDeviceList.add(cfgingDevice);
		}
		hostDao.close();
		request.setAttribute("list", configingDeviceList);
		return "/config/vpntelnet/list.jsp";
	}

	public String configlist() {
		List configingDeviceList = new ArrayList();
		Hua3VPNFileConfigDao vpnFileDao = new Hua3VPNFileConfigDao();
		List vpnDevicelist = vpnFileDao.getDeviceWithLastModify();
		vpnFileDao.close();

		HaweitelnetconfDao telnetConfDao = new HaweitelnetconfDao();
		this.list(telnetConfDao);
		List telnetConfList = (List) request.getAttribute("list");
		int vpnDevicelistSize = 0;
		int tmp = 0;
		Hua3VPNFileConfig tmp2 = null;
		HostNodeDao hostDao = new HostNodeDao();
		HostNode hostNode = null;
		for (int i = 0; i < telnetConfList.size(); i++) {
			ConfiguringDevice cfgingDevice = new ConfiguringDevice();
			Huaweitelnetconf telnetConf = (Huaweitelnetconf) telnetConfList
					.get(i);
			cfgingDevice.setId(telnetConf.getId());
			cfgingDevice.setIpaddress(telnetConf.getIpaddress());
			cfgingDevice.setPrompt(telnetConf.getDefaultpromtp());
			cfgingDevice.setEnablevpn(telnetConf.getEnablevpn());
			cfgingDevice.setIsSynchronized(telnetConf.getIsSynchronized());
			cfgingDevice.setDeviceRender(telnetConf.getDeviceRender());
			vpnDevicelistSize = vpnDevicelist.size();
			tmp = 0;

			while (vpnDevicelistSize > tmp)// 有备份文件的设备就装填上最后一次的备份时间，无备份文件的设备该字段填null
			{
				tmp2 = (Hua3VPNFileConfig) vpnDevicelist.get(tmp);
				if (tmp2.getIpaddress().equals(telnetConf.getIpaddress())) {
					cfgingDevice.setLastUpdateTime(tmp2.getBackupTime());
					break;
				}
				tmp++;
				if (vpnDevicelistSize == tmp) {
					cfgingDevice.setLastUpdateTime(null);
				}
			}
			configingDeviceList.add(cfgingDevice);
		}
		hostDao.close();
		request.setAttribute("list", configingDeviceList);
		return "/config/vpntelnet/configlist.jsp";
	}

	/**
	 * 根据IP来显示所有配置配置文件
	 * 
	 * @return
	 */
	public String showAllFile() {
		String ip = getParaValue("ip");
		String type = getParaValue("type");// 设备类型
		List configingDeviceList = new ArrayList();
		Hua3VPNFileConfigDao vpnFileDao = new Hua3VPNFileConfigDao();
		List vpnDevicelist = vpnFileDao.loadByIp(ip);
		vpnFileDao.close();

		// List telnetConfList = (List) request.getAttribute("list");
		request.setAttribute("ip", ip);
		request.setAttribute("type", type);
		request.setAttribute("list", vpnDevicelist);
		return "/config/vpntelnet/allConfiglist.jsp";
	}

	/**
	 * 定时命令扫描配置文件汇总列表
	 * 
	 * @return
	 */
	public String fileList() {
		List configingDeviceList = new ArrayList();
		VPNFileConfigDao vpnFileDao = new VPNFileConfigDao();
		List vpnDevicelist = vpnFileDao.getAllcfgList();
		vpnFileDao.close();
		// request.setAttribute("page", 1);
		request.setAttribute("list", vpnDevicelist);
		return "/config/vpntelnet/fileList.jsp";
	}

	/**
	 * 定时命令扫描配置文件列表
	 * 
	 * @return
	 */
	public String configFileList() {
		String ip = getParaValue("ip");
		List configingDeviceList = new ArrayList();
		VPNFileConfigDao vpnFileDao = new VPNFileConfigDao();
		List ipList = vpnFileDao.loadAllIps();
		List vpnDevicelist = vpnFileDao.getcfgListByIp(ip);

		// request.setAttribute("page", 1);

		vpnFileDao.close();
		request.setAttribute("ip", ip);
		request.setAttribute("ipList", ipList);
		request.setAttribute("list", vpnDevicelist);
		return "/config/vpntelnet/configFileList.jsp";
	}

	public String showFileContent() {
		// String filePath=getParaValue("filepath");
		// String ip=getParaValue("ip");
		// String type=getParaValue("type");
		// String command=getParaValue("command");
		String id = getParaValue("id");
		VPNFileConfigDao dao = new VPNFileConfigDao();
		VPNFileConfig config = (VPNFileConfig) dao.findByID(id);
		FileReader fr = null;
		BufferedReader br = null;
		// boolean bFlag = false;
		// boolean realFlag = true;
		String lineStr = "";
		// int isViolation = 0;
		StringBuffer sql = null;
		StringBuffer content = new StringBuffer();
		List<String> list = new ArrayList<String>();
		StringBuffer contentStr = new StringBuffer();
		TimingBackupConditionDao conditionDao = new TimingBackupConditionDao();
		List<TimingBackupCondition> conditionList = conditionDao
				.findByCondition(" where timingId=" + config.getTimingId());
		File file = new File(config.getFileName());
		if (file.exists()) {
			try {
				fr = new FileReader(config.getFileName());
				br = new BufferedReader(fr);
				try {
					while (null != (lineStr = br.readLine())) {
						content.append(lineStr + "\r\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			if (conditionList != null && conditionList.size() > 0) {
				boolean flag = true;
				for (int i = 0; i < conditionList.size(); i++) {
					TimingBackupCondition condition = conditionList.get(i);
					String conStr = condition.getContent();
					if (conStr == null || conStr.trim().equals("")) {
						if (i == (conditionList.size() - 1)) {
							contentStr.append("无");
							continue;
						} else {
							continue;
						}
					}
					try {
						fr = new FileReader(config.getFileName());
						br = new BufferedReader(fr);
						try {
							if (condition.getIsContain() == 1) {
								// contentStr.append("包含: ");
								flag = false;
								while (null != (lineStr = br.readLine())) {

									if (lineStr.indexOf(conStr) > -1) {

										contentStr.append("包 含:").append(
												lineStr).append("\r\n");
										flag = true;
									}
								}
							} else {
								// contentStr.append("不包含:");
								flag = true;
								while (null != (lineStr = br.readLine())) {
									if (lineStr.indexOf(conStr) > -1) {
										flag = false;
										break;
									}
								}
								if (flag) {
									contentStr.append("不包含:").append(conStr)
											.append("\r\n");
								}
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					// if(flag)
					// list.add(contentStr.toString());
				}

			} else {
				contentStr.append("无");
			}
		}
		request.setAttribute("content", content.toString());
		request.setAttribute("config", config);
		// request.setAttribute("list", list);
		request.setAttribute("conStr", contentStr.toString());
		return "/config/vpntelnet/showFileContent.jsp";
	}

	public String passwdList() {
		List configingDeviceList = new ArrayList();
		Hua3VPNFileConfigDao vpnFileDao = new Hua3VPNFileConfigDao();
		List vpnDevicelist = vpnFileDao.getDeviceWithLastModify();
		vpnFileDao.close();

		HaweitelnetconfDao telnetConfDao = new HaweitelnetconfDao();
		this.list(telnetConfDao);
		List telnetConfList = (List) request.getAttribute("list");
		int vpnDevicelistSize = 0;
		int tmp = 0;
		Hua3VPNFileConfig tmp2 = null;
		HostNodeDao hostDao = new HostNodeDao();
		HostNode hostNode = null;
		for (int i = 0; i < telnetConfList.size(); i++) {
			ConfiguringDevice cfgingDevice = new ConfiguringDevice();
			Huaweitelnetconf telnetConf = (Huaweitelnetconf) telnetConfList
					.get(i);
			cfgingDevice.setId(telnetConf.getId());
			cfgingDevice.setIpaddress(telnetConf.getIpaddress());
			cfgingDevice.setPrompt(telnetConf.getDefaultpromtp());
			cfgingDevice.setEnablevpn(telnetConf.getEnablevpn());
			cfgingDevice.setIsSynchronized(telnetConf.getIsSynchronized());
			cfgingDevice.setDeviceRender(telnetConf.getDeviceRender());
			vpnDevicelistSize = vpnDevicelist.size();
			tmp = 0;

			while (vpnDevicelistSize > tmp)// 有备份文件的设备就装填上最后一次的备份时间，无备份文件的设备该字段填null
			{
				tmp2 = (Hua3VPNFileConfig) vpnDevicelist.get(tmp);
				if (tmp2.getIpaddress().equals(telnetConf.getIpaddress())) {
					cfgingDevice.setLastUpdateTime(tmp2.getBackupTime());
					break;
				}
				tmp++;
				if (vpnDevicelistSize == tmp) {
					cfgingDevice.setLastUpdateTime(null);
				}
			}
			configingDeviceList.add(cfgingDevice);
		}
		hostDao.close();
		request.setAttribute("list", configingDeviceList);
		return "/config/vpntelnet/passwdList.jsp";
	}

	public String readyAdd() {
		return "/config/ciscosla/add.jsp";
	}

	public String add() {
		CiscoSlaCfgCmdFile vo = new CiscoSlaCfgCmdFile();
		SlaCfgCmdFileDao dao = new SlaCfgCmdFileDao();
		//String id = getParaValue("id");
		try{
			//vo = (CiscoSlaCfgCmdFile)dao.findByID(id);
			//dao.
			vo.setName(getParaValue("name"));
			vo.setFileDesc(getParaValue("fileDesc"));
			vo.setSlatype(getParaValue("slatype"));
			vo.setDevicetype(getParaValue("devicetype"));
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			vo.setCreateBy(user.getName());
			String content = getParaValue("commands");
			String filename = vo.getName();
			String prefix = ResourceCenter.getInstance().getSysPath().replace("\\",
			"/");
			String path = "slascript/"+vo.getDevicetype()+"/" + filename+".log";
			String filePath = prefix + path;
			vo.setFilename(path);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			vo.setCreateTime(sdf.format(date));
			dao.save(vo);
			File f = new File(filePath);
			String result = content.replaceAll(";;", "\r\n");
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
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		String target = "/slacmd.do?action=loadfilelist&jp=1";
		return target;
		
	}

	public String add_pg() {
		Huaweitelnetconf vo = new Huaweitelnetconf();
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		String ipaddress = getParaValue("ipaddress");
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setSuuser(getParaValue("suuser"));
		vo.setSupassword(getParaValue("supassword"));
		vo.setIpaddress(ipaddress);
		vo.setPort(getParaIntValue("port"));
		vo.setDefaultpromtp(getParaValue("defaultpromtp"));
		vo.setEnablevpn(getParaIntValue("enablevpn"));

		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return "/config/vpntelnet/success.jsp";
	}

	public String findip() {
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		String ipfindaddress = getParaValue("ipfindaddress");
		List findlist = dao.find(ipfindaddress);
		HaweitelnetconfDao listdao = new HaweitelnetconfDao();
		setTarget("/config/vpntelnet/list.jsp");
		String page = list(listdao);
		request.setAttribute("list", findlist);
		JspPage jp = (JspPage) request.getAttribute("page");
		jp.setTotalPage(1);
		jp.setCurrentPage(1);
		jp.setMinNum(1);
		request.setAttribute("page", jp);
		System.out.println("===page=======" + page);

		return page;
	}

	public String netip() {
		HostNodeDao dao = new HostNodeDao();
		List list = dao.loadNetwork(1);
		int listsize = list.size();
		request.setAttribute("iplist", list);
		HostNodeDao listdao = new HostNodeDao();
		setTarget("/config/vpntelnet/netip.jsp");
		String page = list(listdao);
		JspPage jp = (JspPage) request.getAttribute("page");
		jp.setTotalRecord(listsize);
		request.setAttribute("page", jp);
		return page;
	}

	public String ipmenu() {
		String ipaddress = getParaValue("ipaddress");
		int id = getParaIntValue("id");
		HaweitelnetconfDao Hdao = new HaweitelnetconfDao();
		String idrs = Hdao.findbyip(ipaddress);
		Hdao.close();
		if (idrs.equals("")) {
			request.setAttribute("ipadd", ipaddress);
			return "/config/vpntelnet/add_pg.jsp";
		} else {
			return "/vpntelnetconf.do?action=ready_edit_gp&id=" + idrs;
		}
	}
}


