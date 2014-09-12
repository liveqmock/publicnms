package com.afunms.polling.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.dao.BaseDaoImp;
import com.afunms.common.base.BaseTelnet;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.Hua3VPNFileConfigDao;
import com.afunms.config.manage.HaweitelnetconfManager;
import com.afunms.config.model.Hua3VPNFileConfig;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.ssh.SSHUtil;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;
import com.afunms.polling.telnet.RedGiantTelnet;
import com.afunms.polling.telnet.ZteTelnet;
import com.database.config.SystemConfig;

/**
 * ∂® ±±∏∑›≈‰÷√Œƒº˛
 * 
 * @author HONGLI
 */
public class M30BackupTelnetConfigTask extends MonitorTask {
	private static Logger log = Logger.getLogger(M30BackupTelnetConfigTask.class);

	private final String hms = " 00:00:00";

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		subscribe();
		// System.gc();
	}

	/**
	 * ∂® ±±∏∑›≈‰÷√Œƒº˛
	 */
	private void subscribe() {
	
		DateTime dt = new DateTime();
		String time = dt.getMyDateTime(DateTime.Datetime_Format_14);
		
		String sql = "SELECT * FROM sys_timingbackup_telnetconfig s WHERE status = '1' and s.BACKUP_DATE > 10000";
		
		//‘⁄≈‰÷√Œƒº˛÷–…Ë÷√ «∑Ò∆Ù∂Ø∂®÷∆∑¢ÀÕ±®±Ì wxy add
		String flag= SystemConfig.getConfigInfomation("Agentconfig","Configserver");
		ArrayList<Map<String, String>> ssconfAL=null;
		if(flag!=null&&flag.equals("enable")){
			BaseDaoImp cd = new BaseDaoImp();
			ssconfAL = cd.executeQuery(sql);
		}
		Map<String, String> ssidAL = null;
		if (ssconfAL != null) {
			log.info("-------------------------------(∂® ±±∏∑›)∂® ±∆˜÷¥–– ±º‰£∫" + dt.getMyDateTime(DateTime.Datetime_Format_2) + "-------------------------------");
			try {
				for (int i = 0; i < ssconfAL.size(); i++) {
					ssidAL = ssconfAL.get(i);
					String status = ssidAL.get("status");
					String telnetconfigips = ssidAL.get("telnetconfigips");
					String backup_sendfrequency = ssidAL.get("BACKUP_SENDFREQUENCY");
					String backup_time_month = ssidAL.get("BACKUP_TIME_MONTH");
					String backup_time_week = ssidAL.get("BACKUP_TIME_WEEK");
					String backup_time_day = ssidAL.get("BACKUP_TIME_DAY");
					String backup_time_hou = ssidAL.get("BACKUP_TIME_HOU");
					String exportType = ssidAL.get("ATTACHMENTFORMAT");
					String bkpType = ssidAL.get("bkpType");
					String content = ssidAL.get("content");
					String id = ssidAL.get("id");
					boolean istrue = false;
					// ∑¢ÀÕ∆µ¬ £¨0:»´≤ø∑¢ÀÕ;1:√øÃÏ;2:√ø÷‹;3:√ø‘¬;4√øºæ∂»;5√øƒÍ
					if ("0".equals(backup_sendfrequency)) {
						istrue = true;
					} else if ("1".equals(backup_sendfrequency)) {
						if (backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("2".equals(backup_sendfrequency)) {
						if (backup_time_week.contains("/" + (dt.getDay() - 1) + "/") && backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("3".equals(backup_sendfrequency)) {
						if (backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
								&& backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("4".equals(backup_sendfrequency)) {
						if (backup_time_month.contains("/" + (dt.getMonth() < 10 ? "0" + dt.getMonth() : dt.getMonth()) + "/")
								&& backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
								&& backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("5".equals(backup_sendfrequency)) {
						if (backup_time_month.contains("/" + (dt.getMonth() < 10 ? "0" + dt.getMonth() : dt.getMonth()) + "/")
								&& backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
								&& backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					}
					if (istrue) {
						log.info("∂® ±±∏∑›≈‰÷√Œƒº˛ø™ º--telnetconfigips=" + telnetconfigips);
						// String filePath =
						// ResourceCenter.getInstance().getSysPath() + "temp" +
						// File.separator
						// + "backup" + "_" + System.currentTimeMillis() + "." +
						// exportType;
						// if (doSubscribe(ssidAL, filePath)) {
						// sendMail(ssidAL, filePath);
						// } else {
						// log.error("∂©‘ƒ" + subscribe_id + " ß∞‹£°");
						// }
						if (!telnetconfigips.equals("") && telnetconfigips != null) {
							String[] ips = telnetconfigips.split(",");
							HaweitelnetconfDao dao = new HaweitelnetconfDao();
							for (String ip : ips) {
								if (ip != null && !ip.equals("") && !ip.equals(",") && "1".equals(status)) {
									Huaweitelnetconf vo = (Huaweitelnetconf) dao.loadByIp(ip);
									if (vo != null) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
										String b_time = sdf.format(new Date());
										String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
										String fileName = prefix + "cfg/" + vo.getIpaddress() + "_" + b_time + "cfg.cfg";
										String descri = vo.getIpaddress() + "_" + b_time;

										if (bkpType.equals("0")) {
											fileName = prefix + "cfg/" + vo.getIpaddress() + "(" + i + ")_" + b_time + "log.log";
											bkpLog(ip, fileName, content, descri, id);
										} else {
											bkpCfg_1(ip, fileName, descri, bkpType);
										}
									}
								}
							}
							dao.close();
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	/**
	 * ±∏∑›≈‰÷√Œƒº˛
	 * 
	 * @param id
	 * @param fileName
	 * @param fileDesc
	 * @param bkpType
	 */
	private void bkpCfg_1(String ip, String fileName, String fileDesc, String bkpType) {
		// String id = getParaValue("id");//Huaweitelnetconf µƒ÷˜º¸ID
		// String fileName = this.getParaValue("fileName");
		// String fileDesc = this.getParaValue("fileDesc");
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
		Huaweitelnetconf vo = null;
		;
		try {
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String result = "";
		String runBackFileResult = "";// ±∏∑›‘À–– ±≈‰÷√Œƒº˛µƒΩ·π˚◊÷∑˚¥Æ
		String startupBackFileResult = "";// ±∏∑›∆Ù∂Ø ±≈‰÷√Œƒº˛µƒΩ·π˚◊÷∑˚¥Æ
		String jsp = null;
		if(vo.getConnecttype()==1){
			String[] cmds=new String[2];
			String[] cmds2=new String[2];
			SSHUtil t = new SSHUtil(vo.getIpaddress(),vo.getPort(),vo.getUser(), vo.getPassword());
            if ("run".equals(bkpType)) {
            	if(vo.getDeviceRender().equals("h3c")||vo.getDeviceRender().equals("haiwei")){
        			cmds[0]="display current-configuration";
        			cmds[1]="\r";
    			}else if (vo.getDeviceRender().equals("cisco")||vo.getDeviceRender().equals("redgiant")) {
    				cmds[0]="show run";
        			cmds[1]="\r";
				}
            	 result = t.executeCmds(cmds);
			}else if("startup".equals(bkpType)){
				if(vo.getDeviceRender().equals("h3c")||vo.getDeviceRender().equals("haiwei")){
        			cmds[0]="display saved-configuration";
        			cmds[1]="\r";
    			}else if (vo.getDeviceRender().equals("cisco")||vo.getDeviceRender().equals("redgiant")) {
    				cmds[0]="show startup";
        			cmds[1]="\r";
				}
				 result = t.executeCmds(cmds);
				 if (result != null) {
						int beginIndex = result.indexOf(cmds[2]);
						int cmd2Len = beginIndex + cmds[2].length() + 2;
						if (beginIndex > -1 && (result.length() > cmd2Len))
							result = result.substring(cmd2Len);
					}
			}else {
				if(vo.getDeviceRender().equals("h3c")||vo.getDeviceRender().equals("haiwei")){
        			cmds[0]="display saved-configuration";
        			cmds[1]="\r";
        			cmds2[0]="display current-configuration";
        			cmds2[1]="\r";
    			}else if (vo.getDeviceRender().equals("cisco")||vo.getDeviceRender().equals("redgiant")) {
    				cmds[0]="show startup";
        			cmds[1]="\r";
        			cmds2[0]="show run";
        			cmds2[1]="\r";
				}
				runBackFileResult = t.executeCmds(cmds);
				t.disconnect();
				t = new SSHUtil(vo.getIpaddress(),vo.getPort(),vo.getUser(), vo.getPassword());
				startupBackFileResult=t.executeCmds(cmds2);
				if (runBackFileResult != null) {
					int beginIndex = runBackFileResult.indexOf(cmds[0]);
					int cmd2Len = beginIndex + cmds[0].length() + 2;
					if (beginIndex > -1 && (runBackFileResult.length() > cmd2Len))
						runBackFileResult = runBackFileResult.substring(cmd2Len);

				}
				
				if (startupBackFileResult != null) {
					int beginIndex = startupBackFileResult.indexOf(cmds2[0]);
					int cmd2Len = beginIndex + cmds2[0].length() + 2;
					if (beginIndex > -1 && (startupBackFileResult.length() > cmd2Len))
						startupBackFileResult = startupBackFileResult.substring(cmd2Len);
				}
			}
           
			t.disconnect();
			
		}else{
		if (vo.getDeviceRender().equals("h3c")||vo.getDeviceRender().equals("huawei")) {// h3c
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su√‹¬Î
			tvpn.setUser(vo.getUser());// ”√ªß
			tvpn.setPassword(vo.getPassword());// √‹¬Î
			tvpn.setIp(vo.getIpaddress());// ipµÿ÷∑
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// Ω· ¯±Íº«∑˚∫≈
			tvpn.setPort(vo.getPort());
			if ("run".equals(bkpType) || "startup".equals(bkpType)) {
				result = tvpn.backupConfFile(bkpType);
			
			} else {// bkpType Œ™ allµƒ«Èøˆ
				Huawei3comvpn secondTvpn = new Huawei3comvpn();
				secondTvpn.setSuuser(vo.getSuuser());// su
				secondTvpn.setSupassword(vo.getSupassword());// su√‹¬Î
				secondTvpn.setUser(vo.getUser());// ”√ªß
				secondTvpn.setPassword(vo.getPassword());// √‹¬Î
				secondTvpn.setIp(vo.getIpaddress());// ipµÿ÷∑
				secondTvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// Ω· ¯±Íº«∑˚∫≈
				secondTvpn.setPort(vo.getPort());
				runBackFileResult = tvpn.backupConfFile("run");
				startupBackFileResult = secondTvpn.backupConfFile("startup");
			}
		} else if (vo.getDeviceRender().equals("cisco")) {// cisco
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword());
			if (telnet.login()) {
				if ("run".equals(bkpType) || "startup".equals(bkpType)) {
					result = telnet.getCfg( bkpType);
				} else {// bkpType Œ™ allµƒ«Èøˆ
					CiscoTelnet secondTelnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword());
					runBackFileResult = telnet.getCfg( "run");
					if (secondTelnet.login()) {
						startupBackFileResult = secondTelnet.getCfg("startup");
					}
				}
			}
		}else if (vo.getDeviceRender().equals("zte")) {// ÷––Àwxy add
			ZteTelnet telnet = new ZteTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword(),vo.getDefaultpromtp());
			String[] results=null;
			if (telnet.tologin()) {
				 results = telnet.getCfg(bkpType);
				if(results!=null&&results.length==2){
					result=results[0];//µ±∆‰÷–“ª÷÷≈‰÷√ ±
					runBackFileResult=results[0];//µ±≈‰÷√Œ™all
					startupBackFileResult=results[1];//µ±∆‰÷–“ª÷÷≈‰÷√Œ™all
				}	
			}
			
		}else if (vo.getDeviceRender().equals("redgiant")) {// »ÒΩ› wxy add
			RedGiantTelnet telnet = new RedGiantTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword(),vo.getDefaultpromtp());
			String[] results=null;
			if (telnet.tologin()) {
				 results = telnet.getCfg(bkpType);
				if(results!=null&&results.length==2){
					result=results[0];
					runBackFileResult=results[0];
					startupBackFileResult=results[1];
				}	
			}
			
		}
		}
		if ("run".equals(bkpType) || "startup".equals(bkpType)) {
			result = result.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");

			HaweitelnetconfManager.getInstance().backVpnConfig(bkpType, fileName, fileDesc, bkpDate, vo, result);
		} else {// bkpType = all
			runBackFileResult = runBackFileResult.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");
			startupBackFileResult = startupBackFileResult.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");

			HaweitelnetconfManager.getInstance().backVpnConfig("run", fileName, fileDesc, bkpDate, vo, runBackFileResult);
			String startupFileName = fileName.substring(0, fileName.lastIndexOf(".")).concat("(2)").concat(fileName.substring(fileName.lastIndexOf(".")));
			HaweitelnetconfManager.getInstance().backVpnConfig("startup", startupFileName, fileDesc, bkpDate, vo, startupBackFileResult);
		}
	}

	/**
	 * 
	 * @description ±∏∑›÷¥––√¸¡ÓΩ·π˚»’÷æ
	 * @author wangxiangyong
	 * @date Feb 25, 2013 11:16:24 AM
	 * @return void  
	 * @param ip
	 * @param fileName
	 * @param content
	 * @param fileDesc
	 * @param id
	 */
	private void bkpLog(String ip, String fileName, String content, String fileDesc, String id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String bkptime = "";
		Date bkpDate = new Date();
		String reg = "_(.*)log.log";
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
		Huaweitelnetconf vo = null;
		try {
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String result = "";
		String[] commStr = new String[content.split("\r\n").length];
		commStr = content.split("\r\n");
		if(vo.getConnecttype()==1){
			SSHUtil t =null;
			try {
				 t = new SSHUtil(vo.getIpaddress(),vo.getPort(),vo.getUser(), vo.getPassword());
				 result = t.executeCmds(commStr);
			} catch (Exception e) {
				SysLogger.error("M30BackupTelnetConfig.bkpLog()",e);
			}finally{
				if(t!=null){
					t.disconnect();
				}
			}
		}else{
		if (vo.getDeviceRender().equals("h3c")||vo.getDeviceRender().equals("huawei")) {// h3c
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su√‹¬Î
			tvpn.setUser(vo.getUser());// ”√ªß
			tvpn.setPassword(vo.getPassword());// √‹¬Î
			tvpn.setIp(vo.getIpaddress());// ipµÿ÷∑
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// Ω· ¯±Íº«∑˚∫≈
			tvpn.setPort(vo.getPort());
			result = tvpn.getCommantValue(commStr);

		} else if (vo.getDeviceRender().equals("cisco")) {// cisco
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword());
			if (telnet.login()) {
				result = telnet.getCommantValue(commStr);

			}
		} else if (vo.getDeviceRender().equals("redgiant")) {// »ÒΩ› wxy add
			BaseTelnet telnet = new RedGiantTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword(),vo.getDefaultpromtp());
			if (telnet.tologin()) {
				result = telnet.getCommantValue(commStr);

			}
		}else if (vo.getDeviceRender().equals("zte")) {// ÷––Àwxy add
			BaseTelnet telnet = new ZteTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(),vo.getPort(),vo.getSuuser(),vo.getSupassword(),vo.getDefaultpromtp());
			if (telnet.tologin()) {
				result = telnet.getCommantValue(commStr);

			}
			
		}
		}
		if(result!=null)
		result = result.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");

		vo.setId(Integer.parseInt(id));// ‘› ±¥Ê∑≈
		HaweitelnetconfManager.getInstance().backVpnConfig("0", fileName, content, bkpDate, vo, result);

	}

	/**
	 * »’∆⁄∏Ò Ω◊™ªª
	 * 
	 * @param startTime
	 * @return
	 */
	public String startTime(String startTime) {
		return startTime + hms;
	}

	/**
	 * »’∆⁄∏Ò Ω◊™ªª
	 * 
	 * @param toTime
	 * @return
	 */
	public String toTime(String toTime) {
		String Millisecond = String.valueOf(DateTime.getMillisecond(toTime, DateTime.Datetime_Format_5) - 1000);
		return DateTime.getDateFromMillisecond(Millisecond, DateTime.Datetime_Format_2);
	}
}
