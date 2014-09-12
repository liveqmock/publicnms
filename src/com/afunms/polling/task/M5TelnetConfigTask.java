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

import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.dao.BaseDaoImp;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessTelnetCfgData;
import com.afunms.polling.node.Host;
import com.afunms.polling.snmp.LoadTelnetCfgFile;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.database.config.SystemConfig;

public class M5TelnetConfigTask extends MonitorTask {
	private static Logger log = Logger.getLogger(M5TelnetConfigTask.class);

	@Override
	public void run() {
		
		collectCfgInfo();
	}
	private void collectCfgInfo() {
		
		String sql = "SELECT * FROM sys_gather_telnetconfig  WHERE status = 1";
		ArrayList<Map<String, String>> ssconfAL=null;
		//在配置文件中设置是否启动定时巡检 wxy add
		String flag= SystemConfig.getConfigInfomation("Agentconfig","InspectionServer");
		if(flag!=null&&flag.equals("enable")){
			BaseDaoImp cd = new BaseDaoImp();
			ssconfAL = cd.executeQuery(sql);
			
		}
		
		Map<String, String> ssidAL = null;
		if (ssconfAL != null) {
			log.info(flag+"-------------------------------(定时巡检)定时器执行"
					+ "-------------------------------");
			try {
				for (int i = 0; i < ssconfAL.size(); i++) {
					ssidAL = ssconfAL.get(i);
					String id = ssidAL.get("id");
					String telnetIps = ssidAL.get("telnetIps");
					String commands = ssidAL.get("commands");
					String status = ssidAL.get("status");

					// 发送频率，0:全部发送;1:每天;2:每周;3:每月;4每季度;5每年

					if (!telnetIps.equals("") && telnetIps != null) {
						String[] ips = telnetIps.split(",");
						HaweitelnetconfDao dao = new HaweitelnetconfDao();
						for (String ip : ips) {
							if (ip != null && !ip.equals("") && !ip.equals(",")
									&& "1".equals(status)) {
								Huaweitelnetconf vo = (Huaweitelnetconf) dao
										.loadByIp(ip);
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyyMMdd-HH-mm");
								String b_time = sdf.format(new Date());
								String prefix = ResourceCenter.getInstance()
										.getSysPath().replace("\\", "/");
								if(vo==null)continue;
								String fileName = prefix + "script/"
										+ vo.getIpaddress() + ".log";
//								List checkList=null;
//                                if (docollcetHash!=null) {
//                                	Host node = (Host)PollingEngine.getInstance().getNodeByIP(ip);
//                                	checkList=(List)docollcetHash.get(node.getId());
//								}
								bkpCfg(ip, fileName, commands);

							}
						}
						dao.close();
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private void bkpCfg(String ip, String fileName, String commands) {
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
		String[] commStr = new String[commands.split(";").length];
		commStr = commands.split(";");
		String result = "";
		Hashtable<String, List> alldata=null;
		LoadTelnetCfgFile file=new LoadTelnetCfgFile();
		if(vo!=null){
		if (vo.getDeviceRender().equals("cisco")) {
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo
					.getUser(), vo.getPassword(),vo.getPort());
			if (telnet.login()) {
				result = telnet.getFileCfg(vo.getSupassword(), commStr);
				 exeLog(fileName, result);
				 alldata=file.dealCfgData(result,fileName,commStr,ip,"cisco");
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
				alldata=file.dealCfgData(result,fileName,commStr,ip,"h3c");
		}
		
		ProcessTelnetCfgData processData=new ProcessTelnetCfgData();
		boolean issucess=processData.processTelnetCfgData(alldata, ip);
      
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
