package com.afunms.polling.telnet;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseTelnet;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.SlaNodePropDao;
import com.afunms.config.model.CiscoSlaCfgCmdFile;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.SlaNodeProp;
import com.afunms.slaaudit.dao.SlaAuditDao;
import com.afunms.slaaudit.model.SlaAudit;
import com.afunms.system.model.User;

/**
 * 本来用来登录连接telnet 登录H3C路由器 实现登录后输入命令返回，命令的信息 主要采集完成后一定要调用
 * disconnect（）方法来关闭telnet连接 实现获取配置文件，实现写配置文件
 * 
 * @author wxy
 * @version Dec 9, 2011 3:31:44 PM
 */
public class H3CTelnet extends BaseTelnet {

	/**
	 * 默认构造函数
	 */
	public H3CTelnet() {
		super();
	}

	/**
	 * @param ip
	 * @param user
	 * @param password
	 * @param port
	 * @param suuser
	 * @param supassword
	 * @param defaule
	 */
	public H3CTelnet(String ip, String user, String password, int port, String suuser, String supassword, String defaule) {
		super(ip, user, password, port, suuser, supassword, defaule);
	}

	public void getSlaCommantValue(List<CmdResult> list, String ip, User operator, CiscoSlaCfgCmdFile slaconfig, Hashtable slaParamHash) {
		boolean isSuccess = tologin();
		CmdResult cmdResult = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String[] command = null;
		String admin="";
		String tag="";
		if (isSuccess) {
			try {
				// for (int i = 0; i < command.length; i++) {

				String content = "";
				String result = "";
				HaweitelnetconfDao dao = new HaweitelnetconfDao();
				Huaweitelnetconf vo = null;
				try {
					vo = (Huaweitelnetconf) dao.loadByIp(ip);

					if ("icmp".equalsIgnoreCase(slaconfig.getSlatype())) {
						 admin = (String) slaParamHash.get("h3c_icmp_admin");
						 tag = (String) slaParamHash.get("h3c_icmp_tag");
						String ipaddress = (String) slaParamHash.get("h3c_icmp_destip");
						command = new String[8];
						command[0] = "sys";
						command[1] = "nqa entry " + admin + " " + tag;
						command[2] = "type icmp-echo";
						command[3] = "destination ip " + ipaddress;
						command[4] = "quit";
						command[5] = "nqa schedule " + admin + " " + tag + " start-time now lifetime forever";
						command[6] = "save";
						command[7] = "quit";
					} else if ("http".equalsIgnoreCase(slaconfig.getSlatype())) {
						 admin = (String) slaParamHash.get("h3c_http_admin");
						 tag = (String) slaParamHash.get("h3c_http_tag");
						String ipaddress = (String) slaParamHash.get("h3c_http_destip");
						String url = (String) slaParamHash.get("h3c_http_url");
						command = new String[11];
						command[0] = "sys";
						command[1] = "nqa entry " + admin + " " + tag;
						command[2] = "type http";
						command[3] = "destination ip " + ipaddress;
						command[4] = "operation get";
						command[5] = "url " + url;
						command[6] = "http-version v1.0";
						command[7] = "quit";
						command[8] = "nqa schedule " + admin + " " + tag + " start-time now lifetime forever";
						command[9] = "save";
						command[10] = "quit";
					} else if ("udp".equalsIgnoreCase(slaconfig.getSlatype())) {
						 admin = (String) slaParamHash.get("h3c_admin");
						 tag = (String) slaParamHash.get("h3c_tag");
						String ipaddress = (String) slaParamHash.get("h3c_destip");
						String port = (String) slaParamHash.get("h3c_destport");
						command = new String[9];
						command[0] = "sys";
						command[1] = "nqa entry " + admin + " " + tag;
						command[2] = "type udp-echo";
						command[3] = "destination ip " + ipaddress;
						command[4] = "destination port " + port;
						command[5] = "quit";
						command[6] = "nqa schedule " + admin + " " + tag + " start-time now lifetime forever";
						command[7] = "save";
						command[8] = "quit";
					} else if ("tcpconnect-noresponder".equalsIgnoreCase(slaconfig.getSlatype())) {
						 admin = (String) slaParamHash.get("h3c_admin");
						 tag = (String) slaParamHash.get("h3c_tag");
						String ipaddress = (String) slaParamHash.get("h3c_destip");
						String port = (String) slaParamHash.get("h3c_destport");
						command = new String[9];
						command[0] = "sys";
						command[1] = "nqa entry " + admin + " " + tag;
						command[2] = "type tcp";
						command[3] = "destination ip " + ipaddress;
						command[4] = "destination port " + port;
						command[5] = "quit";
						command[6] = "nqa schedule " + admin + " " + tag + " start-time now lifetime forever";
						command[7] = "save";
						command[8] = "quit";
					} else if ("jitter".equalsIgnoreCase(slaconfig.getSlatype())) {
						 admin = (String) slaParamHash.get("h3c_admin");
						 tag = (String) slaParamHash.get("h3c_tag");
						String ipaddress = (String) slaParamHash.get("h3c_destip");
						String port = (String) slaParamHash.get("h3c_destport");
						command = new String[9];
						command[0] = "sys";
						command[1] = "nqa entry " + admin + " " + tag;
						command[2] = "type udp-jitter";
						command[3] = "destination ip " + ipaddress;
						command[4] = "destination port " + port;
						command[5] = "quit";
						command[6] = "nqa schedule " + admin + " " + tag + " start-time now lifetime forever";
						command[7] = "save";
						command[8] = "quit";
					}
					write("sys");
					setPrompt("]");
					result = readUntil(prompt);
					if (result.equals(prompt)) {
						String[] errors = { "% Unrecognized command found at '^' position.", "Info: The NQA entry can't be modified for schedule has been configured."};
						String error = prompt;
						for (int i = 0; i < command.length; i++) {
							cmdResult = new CmdResult();
							cmdResult.setIp(ip);
							cmdResult.setCommand(command[i]);
							cmdResult.setTime(sdf.format(date));
							if (i == 0) {
								cmdResult.setResult("执行成功!");
							} else if (i != 0 && error.equals(prompt)) {
								write(command[i]);
								if(command[i].equals("save")){
									write("Y");
									setPrompt(":");
								}else {
									setPrompt("]");
								}
//								if(!command[i].equals("quit")){
//									setPrompt(">");
//								}else {
//									setPrompt("]");
//								}
								error = readUntil(prompt, errors);
								if (error.equals(prompt)) {
									cmdResult.setResult("执行成功!");
								} else {
									cmdResult.setResult("执行失败!");
								}
							} else {
								cmdResult.setResult("执行失败!");
								isSuccess = false;
							}

							list.add(cmdResult);
						}
					} else {
						cmdResult = new CmdResult();
						cmdResult.setIp(ip);
						cmdResult.setCommand("-------");
						cmdResult.setResult("操作失败!");
						cmdResult.setTime(sdf.format(date));
						isSuccess = false;
						list.add(cmdResult);
					}
					// write(command[1]);
					// setPrompt("]");
					// String[] errors={"% Unrecognized command found at '^'
					// position."};
					// for (int i = 1; i < command.length; i++) {
					// write(command[i]);
					// result=readUntil(prompt, errors);
					// System.out.println(command[i]+"------"+result);
					// }

					for (int i = 0; i < command.length; i++) {
						String temp = command[i];
						content = content + temp + "\r\n";
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			cmdResult = new CmdResult();
			cmdResult.setIp(ip);
			cmdResult.setCommand("-------");
			cmdResult.setResult("登录失败!");
			cmdResult.setTime(sdf.format(date));
			list.add(cmdResult);
		}
		Huaweitelnetconf vo = null;
		HaweitelnetconfDao dao = null;
		try {
			dao = new HaweitelnetconfDao();
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		SlaAudit slaaudit = new SlaAudit();
		slaaudit.setUserid(operator.getId());
		slaaudit.setSlatype(slaconfig.getSlatype());
		slaaudit.setTelnetconfigid(vo.getId());
		slaaudit.setOperation("add");
		slaaudit.setDotime(sdf.format(date));
		if (isSuccess) {
			slaaudit.setDostatus(1);
			SlaNodePropDao nodepropdao = new SlaNodePropDao();
			// 将执行成功的设备存入SLA节点属性表
			SlaNodeProp slanodeprop = new SlaNodeProp();
			slanodeprop.setTelnetconfigid(vo.getId());
			slanodeprop.setCreatetime(sdf.format(date));
			slanodeprop.setBak(vo.getDeviceRender());//暂存设备类型);
			slanodeprop.setOperatorid(operator.getId());
			slanodeprop.setEntrynumber(0);
			slanodeprop.setSlatype(slaconfig.getSlatype());
			slanodeprop.setAdminsign(admin);
			slanodeprop.setOperatesign(tag);
			try {
				nodepropdao.save(slanodeprop);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodepropdao.close();
			}

		} else {
			slaaudit.setDostatus(0);
		}
		String content = "";
		for (int i = 0; i < command.length; i++) {
			String temp = command[i];
			content = content + temp + "\r\n";
		}
		slaaudit.setCmdcontent(content);
		//
		SlaAuditDao slaauditdao = new SlaAuditDao();
		try {
			slaauditdao.save(slaaudit);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			slaauditdao.close();
		}
		disconnect();
	}
	public void cancelNqaCommantValue(List<CmdResult> list, String ip, User operator,SlaNodeProp slanodeprop)
	{

		boolean isSuccess = tologin();
		CmdResult cmdResult = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String[] command = null;
		String admin="";
		String tag="";
		if (isSuccess) {
			try {

				String content = "";
				String result = "";
				HaweitelnetconfDao dao = new HaweitelnetconfDao();
				Huaweitelnetconf vo = null;
				try {
					vo = (Huaweitelnetconf) dao.loadByIp(ip);

						 admin = slanodeprop.getAdminsign();
						 tag = slanodeprop.getOperatesign();
						command = new String[3];
						command[0] = "sys";
						command[1] = "undo nqa entry " + admin + " " + tag;
						command[2] = "quit";
					write("sys");
					setPrompt("]");
					result = readUntil(prompt);
					if (result.equals(prompt)) {
						String[] errors = { "% Unrecognized command found at '^' position.", "Error: The specified NQA entry does not exist." };
						String error = prompt;
						for (int i = 0; i < command.length; i++) {
							cmdResult = new CmdResult();
							cmdResult.setIp(ip);
							cmdResult.setCommand(command[i]);
							cmdResult.setTime(sdf.format(date));
							if (i == 0) {
								cmdResult.setResult("执行成功!");
							} else if (i != 0 && error.equals(prompt)) {
								
								write(command[i]);
								
								if(i==1)
								error = readUntil(prompt, errors);
								if (error.equals(prompt)) {
									cmdResult.setResult("执行成功!");
								} else {
									cmdResult.setResult("执行失败!");
								}
							} else {
								cmdResult.setResult("执行失败!");
								isSuccess = false;
							}

							list.add(cmdResult);
						}
					} else {
						cmdResult = new CmdResult();
						cmdResult.setIp(ip);
						cmdResult.setCommand("-------");
						cmdResult.setResult("操作失败!");
						cmdResult.setTime(sdf.format(date));
						isSuccess = false;
						list.add(cmdResult);
					}

					for (int i = 0; i < command.length; i++) {
						String temp = command[i];
						content = content + temp + "\r\n";
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			cmdResult = new CmdResult();
			cmdResult.setIp(ip);
			cmdResult.setCommand("-------");
			cmdResult.setResult("登录失败!");
			cmdResult.setTime(sdf.format(date));
			list.add(cmdResult);
		}
		Huaweitelnetconf vo = null;
		HaweitelnetconfDao dao = null;
		try {
			dao = new HaweitelnetconfDao();
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		SlaAudit slaaudit = new SlaAudit();
		slaaudit.setUserid(operator.getId());
		slaaudit.setSlatype(slanodeprop.getSlatype());
		slaaudit.setTelnetconfigid(vo.getId());
		slaaudit.setOperation("delete");
		slaaudit.setDotime(sdf.format(date));
		if (isSuccess) {
			slaaudit.setDostatus(1);
			SlaNodePropDao nodepropdao = new SlaNodePropDao();
			// 将执行成功设备的SLA节点属性表删除
			try {
				String[] ids = new String[1];
				ids[0] = slanodeprop.getId()+"";
				nodepropdao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodepropdao.close();
			}

		} else {
			slaaudit.setDostatus(0);
		}
		String content = "";
		for (int i = 0; i < command.length; i++) {
			String temp = command[i];
			content = content + temp + "\r\n";
		}
		slaaudit.setCmdcontent(content);
		//
		SlaAuditDao slaauditdao = new SlaAuditDao();
		try {
			slaauditdao.save(slaaudit);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			slaauditdao.close();
		}
		disconnect();
		
	}
	public String readSLAUntil(String pattern, String[] command) {
		String retStr = "1";
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			boolean flag = true;

			char ch = (char) in.read();
			while (flag) {
				sb.append(ch);

				if (sb.toString().endsWith(" --More-- ")) {
					out.write(32);
					out.flush();
				}
				if (command.length > 1) {
					for (int i = 1; i < command.length; i++) {
						write(command[i]);
					}

				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			retStr = "0";
			e.printStackTrace();
		}
		return retStr;
	}
}
