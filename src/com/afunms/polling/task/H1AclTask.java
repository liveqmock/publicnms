package com.afunms.polling.task;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.afunms.capreport.dao.BaseDaoImp;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.impl.ProcessTelnetCfgData;
import com.afunms.polling.snmp.LoadTelnetAclFile;
import com.afunms.polling.snmp.LoadTelnetCfgFile;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;

public class H1AclTask extends MonitorTask{

	private static Logger log = Logger.getLogger(H1AclTask.class);

	@Override
	public void run() {
		log.info("******************开始采集ACL信息****************");

		collectCfgInfo();
	}
	private void collectCfgInfo() {
		BaseDaoImp cd = null;
		String sql = "SELECT * FROM sys_gather_acllist";
		ArrayList<Map<String, String>> ssconfAL = null;
		try {
			cd = new BaseDaoImp();
			ssconfAL = cd.executeQuery(sql);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			if(cd != null){
				cd.close();
			}
		}
		Map<String, String> ssidAL = null;
		if (ssconfAL != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
				for (int i = 0; i < ssconfAL.size(); i++) {
					ssidAL = ssconfAL.get(i);
//					String id = ssidAL.get("id");
					String ipaddress = ssidAL.get("ipaddress");
					String command = ssidAL.get("command");
					String isMonitor = ssidAL.get("isMonitor");
					if (ipaddress != null&&!ipaddress.equals("") && isMonitor.equals("1")) {
						HaweitelnetconfDao dao = null;
						try {
							dao = new HaweitelnetconfDao();
							Huaweitelnetconf vo = (Huaweitelnetconf) dao.loadByIp(ipaddress);
							if(vo == null){
								continue;
							}
//							String b_time = sdf.format(new Date());
							String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
							String fileName = prefix + "script/" + vo.getIpaddress() + "acl.log";
							bkpCfg(ipaddress, fileName, command);
						} catch (Exception e) {
							e.printStackTrace();
						} finally{
							if(dao != null){
								dao.close();
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private void bkpCfg(String ip, String fileName, String command) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		try {
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String[] commStr={command};
		String result = "";
		Hashtable<String, List<?>> alldata=null;
		LoadTelnetAclFile file=new LoadTelnetAclFile();
		if(vo!=null){
		if (vo.getDeviceRender().equals("cisco")) {
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo
					.getUser(), vo.getPassword(),vo.getPort());
			if (telnet.login()) {
				result = telnet.getFileCfg(vo.getSupassword(), commStr);
				 exeLog(fileName, result);
				 alldata=file.dealCfgData(result,fileName,ip,"cisco");
			}
		}else if (vo.getDeviceRender().equals("h3c")) {
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
				result = tvpn.BackupConfFile(commStr);
				 exeLog(fileName, result);
				alldata=file.dealCfgData(result,fileName,ip,"h3c");
		}
		
		ProcessTelnetCfgData processData=new ProcessTelnetCfgData();
		boolean issucess=processData.processTelnetAclData(alldata, ip);
      
		}
	}

	private void exeLog(String fileName, String result) {
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

}
