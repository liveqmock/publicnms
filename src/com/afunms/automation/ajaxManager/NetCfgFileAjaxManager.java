package com.afunms.automation.ajaxManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.afunms.automation.dao.NetCfgFileNodeDao;
import com.afunms.automation.model.NetCfgFileNode;
import com.afunms.automation.telnet.BaseTelnet;
import com.afunms.automation.telnet.CiscoTelnet;
import com.afunms.automation.util.ReaderFileLine;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.ssh.SSHUtil;

public class NetCfgFileAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	/**
	 * 
	 * @description ajax验证远程登录
	 * @author wangxiangyong
	 * @date Mar 12, 2013 9:25:11 AM
	 * @return void
	 */
	public void verifyLogin() {

		int connecttype = getParaIntValue("connecttype");
		String username = getParaValue("username");
		String pwd = getParaValue("pwd");
		String suuser = getParaValue("suuser");
		String supassword = getParaValue("supassword");
		String ipAddress = getParaValue("ipaddress");
		String type = getParaValue("type");
		int port = getParaIntValue("port");
System.out.println("========11111======"+port);
		this.verifyUser(connecttype, username, pwd, suuser, supassword, ipAddress, type, port);

	}

	public void verifyUser(int connecttype, String username, String pwd, String suuser, String supassword, String ipAddress, String type, int port) {
		String result = "验证失败！";
		if (connecttype == 1) {
			boolean flag = false;
			boolean suFlag = true;
			SSHUtil ssh = new SSHUtil(ipAddress, port, username, pwd);
			try {

				flag = ssh.testLogin();
				if (flag && supassword != null && !supassword.equalsIgnoreCase("null") && !supassword.equals("")) {
					String[] commStr = { suuser, supassword };
					String temp = ssh.executeCmds(commStr);
					if (type.equals("h3c")) {
						if (temp.indexOf("Password:") > -1)
							suFlag = false;
					} else {
						if (temp.indexOf("#") > -1)
							suFlag = true;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ssh.disconnect();
			}

			if (flag)
				result = "验证成功！";
			if (!suFlag)
				result = "超级管理员未验证成功！";
		} else if (connecttype == 0) {
			String[] commStr = { "" };
			if (type.equals("cisco")) {// cisco
				CiscoTelnet telnet = new CiscoTelnet(ipAddress, username, pwd, port, suuser, supassword);
				if (telnet.login()) {
					result = "验证成功！";
				} 
			} else {
				BaseTelnet tvpn = null;
				try {
					tvpn = new BaseTelnet(ipAddress, username, pwd, port, suuser, supassword);
					result = tvpn.login();
				} catch (Exception e) {
					SysLogger.error("NetworkDeviceAjaxManager.verifyLogin", e);
				} finally {
					if (tvpn != null)
						tvpn.disconnect();
				}

			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", result);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	/**
	 * 
	 * @description 文件内容比对
	 * @author wangxiangyong
	 * @date Aug 28, 2014 10:44:42 AM
	 * @return void
	 */
	public void compare() {
		String basePath=(String)session.getAttribute("baseCfgName");
		String comparePath=(String)session.getAttribute("compareCfgName");
		File file1=new File(basePath);
		File file2=new File(comparePath);
		Object[] results = null;
		try {
			results = ReaderFileLine.diffText(file2, file1);
		} catch (Exception e) {
			e.printStackTrace();
		}
			JSONObject obj = new JSONObject();
			JSONArray array = new JSONArray();
			{
				JSONObject basic = new JSONObject();
				basic.put("basic", results[1]);
				array.add(basic);
				
				JSONObject diff = new JSONObject();
				diff.put("diff", results[0]);
				array.add(diff);
			}
			obj.put("records",array);
		out.print(obj);
		out.flush();
	}
	/**
	 * 
	 * @description 加载配置文件内容
	 * @author wangxiangyong
	 * @date Aug 30, 2014 2:40:45 PM
	 * @return void
	 */
	public void loadFile() {
		String filePath = (String) getParaValue("filePath");
		String prePath = ResourceCenter.getInstance().getSysPath();
		FileReader fr = null;
		try {
			fr = new FileReader(prePath + filePath);
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
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", content.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}
	/**
	 * 
	 * @description 验证登陆
	 * @author wangxiangyong
	 * @date Apr 23, 2013 11:02:44 AM
	 * @return void
	 */
	public void verifyUpdate() {
		String id = getParaValue("id");
		NetCfgFileNodeDao telnetcfgdao = null;
		NetCfgFileNode hmo = null;
		try {
			telnetcfgdao = new NetCfgFileNodeDao();
			hmo = (NetCfgFileNode) telnetcfgdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			telnetcfgdao.close();
		}
		if(hmo!=null){
		int connecttype = hmo.getConnecttype();
		String username =hmo.getUser();
		String pwd = hmo.getPassword();
		String suuser =hmo.getSuuser();
		String supassword = hmo.getSupassword();
		String ipAddress = hmo.getIpaddress();
//		String promtp =hmo.getDefaultpromtp();
		String type =hmo.getDeviceRender();
		int port =hmo.getPort();

		this.verifyUser(connecttype, username, pwd, suuser, supassword, ipAddress, type, port);
		}
	}
	
	public void execute(String action) {
		if (action.equals("verifyLogin")) {
			verifyLogin();
		}
		if (action.equals("compare")) {
			compare();
		}
		if (action.equals("loadFile")) {
			loadFile();
		}
		if (action.equals("verifyUpdate")) {//verifyUpdate
			verifyUpdate();
		}
		
	}

}
