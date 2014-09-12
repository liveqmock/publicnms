package com.afunms.automation.task;

import com.afunms.automation.model.NetCfgFileNode;
import com.afunms.automation.telnet.CiscoTelnet;
import com.afunms.automation.telnet.NetTelnetUtil;

public class BatchDeployTask implements Runnable {
	NetCfgFileNode vo = null;
	String remoteFileName = null;
	StringBuffer result = null;
	String localFullPathFileName = null;

	public BatchDeployTask(StringBuffer result, NetCfgFileNode vo, String remoteFileName, String localFullPathFileName) {
		this.vo = vo;
		this.remoteFileName = remoteFileName;
		this.result = result;
		this.localFullPathFileName = localFullPathFileName;
	}

	public void run() {
		if (vo.getDeviceRender().equals("h3c"))// h3c的sysoid
		{
			NetTelnetUtil tvpn = new NetTelnetUtil();
			tvpn.setSuUser(vo.getSuuser());// su
			tvpn.setSuPassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
//			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
//			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());
			boolean b = tvpn.setupNewConfFile("/" + remoteFileName, localFullPathFileName, vo.getIpaddress(), 21, vo.getUser(), vo.getPassword());
			if (!b) {
				synchronized (result) {
					result.append("," + vo.getIpaddress());
				}
			}
		} else if (vo.getDeviceRender().equals("cisco"))// cisco的sysoid
		{
			CiscoTelnet telnet = new CiscoTelnet("172.25.25.240", "1", "1", 23);
			// telnet.writeCfgFile(content.toString());
		}
	}
}

