package com.afunms.automation.telnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;

import com.afunms.automation.dao.CmdCfgFileDao;
import com.afunms.automation.dao.NetCfgFileDao;
import com.afunms.automation.dao.NetCfgFileNodeDao;
import com.afunms.automation.model.CmdCfgFile;
import com.afunms.automation.model.NetCfgFile;
import com.afunms.automation.model.NetCfgFileNode;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.ssh.SSHUtil;

public class NetCfgFileTelnet {
public String bkpCfgFile(String id,String bkpType,String fileName,String fileDesc,Date bkpDate) {
	
	NetCfgFileNodeDao dao = new NetCfgFileNodeDao();
	NetCfgFileNode vo = (NetCfgFileNode) dao.findByID(id);
	dao.close();
	String result = "";
	String runBackFileResult = "";// ±¸·ÝÔËÐÐÊ±ÅäÖÃÎÄ¼þµÄ½á¹û×Ö·û´®
	String startupBackFileResult = "";// ±¸·ÝÆô¶¯Ê±ÅäÖÃÎÄ¼þµÄ½á¹û×Ö·û´®
	String jsp = null;
	if (vo.getConnecttype() == 1) {
		String[] cmds = new String[4];
		String[] cmds2 = new String[4];
		SSHUtil t = null;
		try {
			t = new SSHUtil(vo.getIpaddress(), vo.getPort(), vo.getUser(), vo.getPassword());
			if ("run".equals(bkpType)) {
				if (vo.getDeviceRender().equals("h3c") || vo.getDeviceRender().equals("haiwei")) {
					cmds[0] = vo.getSuuser();
					cmds[1] = vo.getSupassword();
					cmds[2] = "display current-configuration";
					cmds[3] = "\r";
				} else if (vo.getDeviceRender().equals("cisco") || vo.getDeviceRender().equals("redgiant")) {
					cmds[0] = vo.getSuuser();
					cmds[1] = vo.getSupassword();
					cmds[2] = "show run";
					cmds[3] = "\r";
				}
				result = t.executeCmds(cmds);
				if (result != null) {
					int beginIndex = result.indexOf(cmds[2]);
					int cmd2Len = beginIndex + cmds[2].length() + 2;
					if (beginIndex > -1 && (result.length() > cmd2Len))
						result = result.substring(cmd2Len);
				}
			} else if ("startup".equals(bkpType)) {
				if (vo.getDeviceRender().equals("h3c") || vo.getDeviceRender().equals("haiwei")) {
					cmds[0] = vo.getSuuser();
					cmds[1] = vo.getSupassword();
					cmds[2] = "display saved-configuration";
					cmds[3] = "\r";
				} else if (vo.getDeviceRender().equals("cisco") || vo.getDeviceRender().equals("redgiant")) {
					cmds[0] = vo.getSuuser();
					cmds[1] = vo.getSupassword();
					cmds[2] = "show startup";
					cmds[3] = "\r";
				}
				result = t.executeCmds(cmds);
				if (result != null) {
					int beginIndex = result.indexOf(cmds[2]);
					int cmd2Len = beginIndex + cmds[2].length() + 2;
					if (beginIndex > -1 && (result.length() > cmd2Len))
						result = result.substring(cmd2Len);
				}
			} else {
				if (vo.getDeviceRender().equals("h3c") || vo.getDeviceRender().equals("haiwei")) {
					cmds[0] = vo.getSuuser();
					cmds[1] = vo.getSupassword();
					cmds[2] = "display saved-configuration";
					cmds[3] = "\r";
					cmds2[0] = vo.getSuuser();
					cmds2[1] = vo.getSupassword();
					cmds2[2] = "display current-configuration";
					cmds2[3] = "\r";
				} else if (vo.getDeviceRender().equals("cisco") || vo.getDeviceRender().equals("redgiant")) {
					cmds[0] = vo.getSuuser();
					cmds[1] = vo.getSupassword();
					cmds[2] = "show startup";
					cmds[3] = "\r";
					cmds2[0] = vo.getSuuser();
					cmds2[1] = vo.getSupassword();
					cmds2[2] = "show run";
					cmds2[3] = "\r";
				}
				try {
					runBackFileResult = t.executeCmds(cmds);
					if (runBackFileResult != null) {
						int beginIndex = runBackFileResult.indexOf(cmds[2]);
						int cmd2Len = beginIndex + cmds[2].length() + 2;
						if (beginIndex > -1 && (runBackFileResult.length() > cmd2Len))
							runBackFileResult = runBackFileResult.substring(cmd2Len);

					}
				} catch (Exception e) {
					SysLogger.error("SSHUtil executeCmds·½·¨Ö´ÐÐÊ§°Ü£¡", e);
				} finally {
					if (t != null)
						t.disconnect();
				}

				t = new SSHUtil(vo.getIpaddress(), vo.getPort(), vo.getUser(), vo.getPassword());
				startupBackFileResult = t.executeCmds(cmds2);
				if (startupBackFileResult != null) {
					int beginIndex = startupBackFileResult.indexOf(cmds2[2]);
					int cmd2Len = beginIndex + cmds2[2].length() + 2;
					if (beginIndex > -1 && (startupBackFileResult.length() > cmd2Len))
						startupBackFileResult = startupBackFileResult.substring(cmd2Len);
				}
			}
		} catch (Exception e) {
			SysLogger.error("SSHUtil executeCmds·½·¨Ö´ÐÐÊ§°Ü£¡", e);
		} finally {
			if (t != null)
				t.disconnect();
		}

		jsp = "/automation/common/status.jsp";
	} else {
		if (vo.getDeviceRender().equals("h3c")||vo.getDeviceRender().equals("huawei"))// h3c
		{
			NetTelnetUtil tvpn = new NetTelnetUtil();
			tvpn.setSuUser(vo.getSuuser());// su
			tvpn.setSuPassword(vo.getSupassword());// suÃÜÂë
			tvpn.setUser(vo.getUser());// ÓÃ»§
			tvpn.setPassword(vo.getPassword());// ÃÜÂë
			tvpn.setIp(vo.getIpaddress());// ipµØÖ·
			tvpn.setPort(vo.getPort());
			if ("run".equals(bkpType) || "startup".equals(bkpType)) {
				result = tvpn.backupConfFile(bkpType);
			} else {// bkpType Îª allµÄÇé¿ö
				NetTelnetUtil secondTvpn = new NetTelnetUtil();
				secondTvpn.setSuUser(vo.getSuuser());// su
				secondTvpn.setSuPassword(vo.getSupassword());// suÃÜÂë
				secondTvpn.setUser(vo.getUser());// ÓÃ»§
				secondTvpn.setPassword(vo.getPassword());// ÃÜÂë
				secondTvpn.setIp(vo.getIpaddress());// ipµØÖ·
				secondTvpn.setPort(vo.getPort());
				runBackFileResult = tvpn.backupConfFile("run");

				startupBackFileResult = secondTvpn.backupConfFile("startup");
			}
			if (!result.equals("user or password error")) {
				jsp = "/automation/common/status.jsp";
			}
		} else if (vo.getDeviceRender().equals("cisco")) {// cisco

			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword());
			if (telnet.login()) {
				if ("run".equals(bkpType) || "startup".equals(bkpType)) {
					if (vo.getSupassword() == null) {
						jsp = "/automation/common/error.jsp";
					}
					result = telnet.getCfg(bkpType);
				} else {// bkpType Îª allµÄÇé¿ö
					CiscoTelnet secondTelnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword());
					runBackFileResult = telnet.getCfg("run");
					if (secondTelnet.login()) {
						if (vo.getSupassword() == null) {
							jsp = "/automation/common/error.jsp";
						}
						startupBackFileResult = secondTelnet.getCfg("startup");
					}
				}
				jsp = "/automation/common/status.jsp";
			}
		} else if (vo.getDeviceRender().equals("zte")) {// ÖÐÐËwxy add
			ZteTelnet telnet = new ZteTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword());
			String[] results = null;
			if (telnet.tologin()) {
				results = telnet.getCfg(bkpType);
				if (results != null && results.length == 2) {
					result = results[0];
					runBackFileResult = results[0];
					startupBackFileResult = results[1];
				}
				jsp = "/automation/common/status.jsp";
			}
		} else if (vo.getDeviceRender().equals("redgiant")) {// redgiant

			RedGiantTelnet telnet = new RedGiantTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword(), vo.getPort(), vo.getSuuser(), vo.getSupassword());
			String[] results = null;
			if (telnet.tologin()) {
				results = telnet.getCfg(bkpType);
				if (results != null && results.length == 2) {
					result = results[0];
					runBackFileResult = results[0];
					startupBackFileResult = results[1];
				}

				jsp = "/automation/common/status.jsp";
			}
		}
	}
	if (jsp != null) {
		result = result.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");
		result=result.replaceAll("Building configuration...\\r", "");
		if ("run".equals(bkpType) || "startup".equals(bkpType)) {
			backConfig(bkpType, fileName, fileDesc, bkpDate, vo, result);
		} else {// bkpType = all
			runBackFileResult = runBackFileResult.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");
			startupBackFileResult = startupBackFileResult.replaceAll("  ---- more ----", "").replaceAll("--More--", "").replaceAll("42d", "").replaceAll("                                          ", "").replaceAll("\\\\[", "");
			runBackFileResult=runBackFileResult.replaceAll("Building configuration...\\r", "");
			startupBackFileResult=startupBackFileResult.replaceAll("Building configuration...\\r", "");
			backConfig("run", fileName, fileDesc, bkpDate, vo, runBackFileResult);
			String startupFileName = "";
			if (fileName.lastIndexOf(".") != -1) {
				startupFileName = fileName.substring(0, fileName.lastIndexOf(".")).concat("(2)").concat(fileName.substring(fileName.lastIndexOf(".")));
			} else {
				startupFileName = startupFileName.concat("(2)");
			}
			backConfig("startup", startupFileName, fileDesc, bkpDate, vo, startupBackFileResult);
		}
	}

	return jsp;
	
}
/**
 * @param bkpType
 *            ±¸·ÝµÄÎÄ¼þÀàÐÍ
 * @param fileName
 *            ÎÄ¼þÃû
 * @param fileDesc
 *            ÃèÊö
 * @param bkpDate
 *            ±¸·ÝÈÕÆÚ
 * @param vo
 * @param result
 *            ÎÄ¼þÄÚÈÝ
 */
public synchronized void backConfig(String bkpType, String fileName, String fileDesc, Date bkpDate, NetCfgFileNode vo, String result) {
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

	NetCfgFile h3vpn = new NetCfgFile();
	h3vpn.setFileName(fileName);
	h3vpn.setDescri(fileDesc);
	h3vpn.setIpaddress(vo.getIpaddress());
	h3vpn.setFileSize(fileSize);
	h3vpn.setBackupTime(new Timestamp(bkpDate.getTime()));

	if (bkpType.equals("0")) {// ±¸·ÝµÄÊÇÃüÁîÎÄ¼þ
		CmdCfgFile cmdCfgFile = new CmdCfgFile();
		cmdCfgFile.setTimingId(vo.getId());
		cmdCfgFile.setFileName(fileName);
		cmdCfgFile.setContent(fileDesc);
		cmdCfgFile.setIpaddress(vo.getIpaddress());
		cmdCfgFile.setFileSize(fileSize);
		cmdCfgFile.setBackupTime(new Timestamp(bkpDate.getTime()));
		cmdCfgFile.setBkpType(vo.getDeviceRender());
		CmdCfgFileDao dao = new CmdCfgFileDao();
		dao.save(cmdCfgFile);
		dao.close();
	} else {
		h3vpn.setBkpType(bkpType);
		NetCfgFileDao h3Dao = new NetCfgFileDao();
		h3Dao.save(h3vpn);
		h3Dao.close();
	}

}
}
