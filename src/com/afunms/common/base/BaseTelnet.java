package com.afunms.common.base;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;

import com.afunms.common.util.SysLogger;

/**
 * 本来用来登录连接telnet 登录路由器、交换机 实现登录后输入命令返回，命令的信息 主要采集完成后一定要调用
 * disconnect（）方法来关闭telnet连接 实现获取配置文件，实现写配置文件。
 * 
 * @author wxy
 * @version Dec 10, 2011 7:35:28 PM
 */
public class BaseTelnet {
	private TelnetClient telnet = new TelnetClient();

	protected InputStream in; // 
	protected PrintStream out;
	protected String prompt = ">";
	protected final String USER_PROMPT = ">";// 一般用户标识符
	protected  String SU_PROMPT = "#";// 超级用户标识符
	protected  String SYS_PROMPT = "]";// 超级用户标识符
	protected final String USERSING = "Username:";// 用户名标识
	protected final String PWDSING = "Password:";// 密码标识
	protected String Loginuser = "Username:";// 用户
	protected String Loginpassword = "Password:";// 用户的密码
	private int nummber = 20000; // 定义一个读取字符的总数
	protected int port = 23; // telnet 端口
	protected String user = ""; // 用户名
	protected String password = ""; // 密码
	protected String suuser = ""; // 超级管理员
	protected String supassword = "";// 超级管理员密码、
	protected String ip = ""; // IP 地址
	protected int DEFAULT_TELNET_PORT = 23;// 缺省端口

	/**
	 * 默认构造方法
	 */
	public BaseTelnet() {

	}

	/**
	 * 带参数构造方法
	 */
	public BaseTelnet(String ip, String user, String password, int port, String suuser, String supassword,String defaule) {
		this.ip = ip;
		this.port = this.getDEFAULT_TELNET_PORT();
		this.user = user; // 默认用户
		this.password = password; // 默认用户密码
		this.supassword = supassword;// su用户
		this.suuser = suuser;// su用户的密码
		this.prompt = defaule;// 系统默认标识符号
		this.DEFAULT_TELNET_PORT = port;// 端口号
	}

	/**
	 * 连接路由器
	 * 
	 * @return boolean 连接成功返回true，否则返回false
	 */
	public boolean connect() {

		boolean isConnect = true;

		try {
			telnet.setDefaultTimeout(5000);// 设置从未连接到连接状态的超时时间
			telnet.connect(ip, port);
			in = telnet.getInputStream(); // 输入流
			out = new PrintStream(telnet.getOutputStream()); // 输出流
		} catch (Exception e) {
			isConnect = false;
			e.printStackTrace();
			return isConnect;
		}
		return isConnect;
	}

	public boolean userLogin() {
		boolean isLogin = false;
		try {
			if (null != this.password && this.password.trim().length() > 0) {
				// 连接不成功返回null
				// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度/////////////
				String[] patterns = { USER_PROMPT, SU_PROMPT, Loginuser, Loginpassword, "password:", "login:" };
				String aa = readUntil(patterns); // 先读取
				if (aa.endsWith("Password:") || aa.endsWith("password:")) {
					write(password);
					aa = readUntil(patterns);
				}
				if (null != this.user && this.user.trim().length() > 0) {
					if (aa.endsWith("Username:") || aa.endsWith("login:")) {
						write(user);
						aa = readUntil("Password:", "password:");
						write(password);
						aa = readUntil(patterns);
					}
				}
				if (aa.indexOf("% Login failed!") > 0 || aa.indexOf("user or password error") > 0) {
					isLogin = false;
				} else if (aa.equals(USER_PROMPT) || aa.equals(SU_PROMPT)) {
					isLogin = true;
				} else if (aa.endsWith(":")) {
					isLogin = false;
				} else if (aa.equals("in unit1 login")) {
					write("\r\n");
					isLogin = true;
				}
			}

		} catch (Exception e) {
			isLogin = false;
			this.disconnect();
			e.printStackTrace();
		}
		return isLogin;
	}

	/**
	 * 验证超级管理员登录
	 * 
	 * @return
	 */
	public boolean suLogin() {

		boolean result = false;
		String[] patterns = { USER_PROMPT, SU_PROMPT, "Password:", "password:" };
		if (null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			write(suuser);
			String pass = readUntil(patterns);

			if (pass.equalsIgnoreCase("Password:") || pass.equalsIgnoreCase("password:"))
				write(this.supassword);

			String aa = readUntil(patterns);// 在这里登录成功
			if (aa.equals(SU_PROMPT)) {
				result = true;
			}
		}
		return result;

	}

	/**
	 * 验证普通用户的登录
	 * 
	 * @return
	 */
	public boolean siglelogin() {
		boolean flag = false;
		flag = this.connect();
		if (flag) {
			flag = this.userLogin();
		}
		return flag;
	}

	/**
	 * 验证两种用户的登录
	 * 
	 * @return
	 */
	public boolean tologin() {
		boolean flag = false;
		flag = this.connect();
		if (flag) {
			flag = this.userLogin();
			if (flag) {
				flag = this.suLogin();
			}
		}
		return flag;
	}

	/**
	 * 验证两种用户的登录
	 * 
	 * @return
	 */
	public String login() {
		String msg = "";
		boolean flag = false;
		// flag = this.connect();
		// if (flag) {// true:连接成功
		// flag = this.userLogin();
		// if (flag) {
		// flag = this.suLogin();
		// if (flag) {
		// msg = "用户/管理员登录成功";
		// } else {
		// msg = "用户登录成功";
		// }
		// } else {
		// msg = "用户登录失败";
		// }
		// } else {
		// msg = "连接失败";
		// }

		boolean isLogin = false;
		try {

			boolean connetflg = this.connect();
			String[] patterns = { USER_PROMPT, SU_PROMPT, Loginuser, Loginpassword, "Password:", "password:", "login:" };
			if (connetflg) {// 成功
				if (null != this.password && this.password.trim().length() > 0) {
					// 连接不成功返回null
					// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度/////////////

					String aa = readUntil(patterns); // 先读取
					if (aa.endsWith("Password:") || aa.endsWith("password:")) {
						write(password);
						aa = readUntil(patterns);
					}
					if (null != this.user && this.user.trim().length() > 0) {
						if (aa.endsWith("Username:") || aa.endsWith("login:")) {
							write(user);
							aa = readUntil("Password:", "password:");
							write(password);
							aa = readUntil(patterns);
						}
					}
					if (aa.indexOf("% Login failed!") > 0 || aa.indexOf("user or password error") > 0) {
						isLogin = false;
						msg = "用户登录失败";
					} else if (aa.endsWith(USER_PROMPT) || aa.endsWith(SU_PROMPT)) {
						isLogin = true;
						msg = "用户登录成功";
					} else if (aa.endsWith(":")) {
						isLogin = false;
						msg = "用户登录失败";
					} else if (aa.endsWith("in unit1 login")) {
						write("\r\n");
						isLogin = true;
						msg = "用户登录成功";
					}
				}
				// 如果登录成功，再判断su的用户密码的可用性
				String aa = "";
				if (isLogin && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
					write(suuser);
					String pass = readUntil(patterns);

					if (pass.equalsIgnoreCase("Password:") || pass.equalsIgnoreCase("password:"))
						write(this.supassword);

					aa = readUntil(patterns);// 在这里登录成功
					if (aa.equals(SU_PROMPT)) {
						msg = "用户/管理员登录成功";
					} else if (aa.equals(USER_PROMPT)) {
						msg = "超级用户登录失败";
					}
				}

			} else {
				msg = "连接失败";
			}

		} catch (Exception e) {
			isLogin = false;
			this.disconnect();
			e.printStackTrace();
		}

		return msg;

	}

	/**
	 * 
	 * @description 修改设备密码
	 * @author wangxiangyong
	 * @date Apr 24, 2013 1:14:59 PM
	 * @return boolean
	 * @param devType
	 *            设备类型
	 * @param newUser
	 * @param newPasswd
	 *            新密码
	 * @return
	 */
	public boolean modifyDevPasswd(String devType, String newUser, String newPasswd, String encrypt) {
		boolean isSuccess = false;
		try {
			String cmd = "";
			if (devType.equals("zte")) {

				String temp = sendCommand("conf t");
				cmd = "set login-password " + newPasswd;
				sendCommand(cmd);
				temp = sendCommand(cmd);
				if (!isContainInvalidateWords(temp)) {
					isSuccess = true;
				}
			} else if (devType.equals("redgiant")) {
				String temp = sendCommand("conf t");
				cmd = "username " + newUser + " password " + newPasswd;
				temp = sendCommand(cmd);
				if (!isContainInvalidateWords(temp)) {
					isSuccess = true;
				}
			} else if (devType.equals("huawei")) {
				String temp = sendHCommand("system-view");
				cmd = "user-interface console 0";
				temp = sendHCommand(cmd);
				if (!isContainInvalidateWords(temp)) {
					temp = sendHCommand("user-interface vty 0 4");
					if (encrypt != null && encrypt == "0") {
						encrypt = "cipher";
					} else {
						encrypt = "simple";
					}
					;
					cmd = "set authentication password " + encrypt + " " + newPasswd;
					temp = sendHCommand(cmd);

					if (!isContainInvalidateWords(temp)) {
						isSuccess = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return isSuccess;
	}

	private boolean isContainInvalidateWords(String content) {
		boolean isContained = false;
		if (content.contains("invalid") || content.contains("Unknown") || content.contains("% Ambiguous command")) {
			isContained = true;
		}
		return isContained;
	}

	/**
	 * 发送命令
	 * 
	 * @param command
	 * @return
	 */
	public String sendCommand(String command) {
		try {
			write(command);
			return readcommand(USER_PROMPT, SU_PROMPT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 发送命令
	 * 
	 * @param command
	 * @return
	 */
	public String sendHCommand(String command) {
		try {
			write(command);
			return readcommand(USER_PROMPT, SYS_PROMPT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * 读取信息 @param pattern 结束符号 @return 如果读到对应的字符返回字符读到的所有结果，如果没有读到则返回null
	 */
	public String readUntil(String pattern) {

		StringBuffer sb = new StringBuffer();
		try {

			char lastChar = pattern.charAt(pattern.length() - 1);

			char ch = (char) in.read();
			int n = 0;

			boolean flag = true;
			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return pattern;
					}
				}
				ch = (char) in.read();
				if (sb.toString().endsWith(" --More-- ")) {
					out.write(32);
					out.flush();
				}
				n++;
				if (n > this.nummber) {// 如果读取的字符个数大于2万个字符就认没有正确
					flag = false;
					return "time out";
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		return sb.toString() + "";
	}

	/*
	 * 提供两个字符串，作为流结束的判断 命令执行后，可能返回成功，也可能返回失败。提供两个字符串后，能快速识别命令是否执行结束，加快程序执行效率
	 */
	public String readUntil(String pattern1, String pattern2) {
		StringBuffer sb = new StringBuffer();
		try {
			char ch = (char) in.read();
			boolean flag = true;
			int n = 0;
			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				if (sb.toString().endsWith(pattern1)) // 规定，如果以pattern1结尾，说明命令执行成功
				{
					return pattern1;
				}
				// }
				if (sb.toString().endsWith(pattern2)) // 如果以pattern1结尾，说明命令执行成功
				{
					return pattern2;
				}
				// }
				if (sb.toString().endsWith("in unit1 login")) // 如果以pattern1结尾，说明命令执行成功
				{
					return "in unit1 login";
				}
				if (sb.toString().endsWith(" --More-- ")) {
					out.write(32);
					out.flush();
				}
				ch = (char) in.read();

				n++;
				if (n > this.nummber) {// 如果读取的字符个数大于2万个字符就认没有正确
					flag = false;
					return "time out";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return sb.toString();
	}

	/*
	 * 提供三个字符串，作为流结束的判断 命令执行后，可能返回成功，也可能返回失败。提供两个字符串后，能快速识别命令是否执行结束，加快程序执行效率
	 */
	public String readUntil(String pattern1, String pattern2, String pattern3) {
		StringBuffer sb = new StringBuffer();
		try {
			char ch = (char) in.read();
			boolean flag = true;
			int n = 0;
			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}

				if (sb.toString().endsWith(pattern1)) // 规定，如果以pattern1结尾，说明命令执行成功
				{
					return pattern1;
				} else if (sb.toString().endsWith(pattern2)) // 如果以pattern3结尾，说明命令执行成功
				{
					return pattern2;
				} else if (sb.toString().endsWith(pattern3)) // 如果以pattern3结尾，说明命令执行成功
				{
					return pattern3;
				}

				if (sb.toString().endsWith("in unit1 login")) // 如果以pattern1结尾，说明命令执行成功
				{
					return "in unit1 login";
				}
				if (sb.toString().endsWith(" --More-- ")) {
					out.write(32);
					out.flush();
				}
				ch = (char) in.read();

				n++;
				if (n > this.nummber) {// 如果读取的字符个数大于2万个字符就认没有正确
					flag = false;
					return "time out";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return sb.toString();
	}

	/*
	 * 提供多个字符串，作为流结束的判断 命令执行后，可能返回成功，也可能返回失败。提供两个字符串后，能快速识别命令是否执行结束，加快程序执行效率
	 */
	public String readUntil(String pattern, String[] patterns) {

		StringBuffer sb = new StringBuffer();
		int length = 0;
		try {

			char lastChar = pattern.charAt(pattern.length() - 1);

			char ch = (char) in.read();
			int n = 0;

			boolean flag = true;
			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				for (int i = 0; i < patterns.length; i++) {
					length = sb.toString().trim().length();
					if (length >= patterns[i].length() && sb.toString().indexOf((patterns[i])) > -1) {
						flag = false;
						return patterns[i];
					}
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return pattern;
					}
				}

				ch = (char) in.read();
				if (sb.toString().endsWith(" --More-- ")) {
					out.write(32);
					out.flush();
				}
				n++;
				if (n > this.nummber) {// 如果读取的字符个数大于2万个字符就认没有正确
					flag = false;
					return "time out";
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		return sb.toString() + "";

	}

	/*
	 * 提供多个字符串，作为流结束的判断 命令执行后，可能返回成功，也可能返回失败，能快速识别命令是否执行结束，加快程序执行效率
	 */
	public String readUntil(String[] patterns) {
		StringBuffer sb = new StringBuffer();
		try {

			char ch = (char) in.read();
			int n = 0;
			boolean flag = true;

			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				for (int i = 0; i < patterns.length; i++) {
					if (sb.toString().endsWith(patterns[i])) // 规定，如果以pattern1结尾，说明命令执行成功
					{
						return patterns[i];
					}
				}

				ch = (char) in.read();
				n++;
				if (n > this.nummber) {// 如果读取的字符个数大于2万个字符就认没有正确
					sb.delete(0, sb.length());
					sb.append("user or password error");
					flag = false;
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "user or password error";
		}
		return sb.toString();
	}

	/**
	 * 
	 * @description 根据命令获取返回的信息
	 * @author wangxiangyong
	 * @date Feb 23, 2013 1:40:30 PM
	 * @return String
	 * @param pattern
	 * @return
	 */
	private String readcommand(String pattern1, String pattern2) {
		try {
			// 读入多个字符到字符数组中，charread为一次读取字符数
			byte[] b1 = new byte[254];
			int n = 0;
			StringBuffer ret = new StringBuffer();
			String current;
			while (n >= 0) {
				n = in.read(b1);
				if (n > 0) {
					current = new String(b1, 0, n, "iso-8859-1");
					String tempCurrent = "";
					char[] a = current.toCharArray();

					for (int i = 0; i < a.length; i++) {
						char flag = a[i];
						if (flag == 0 || flag == 13 || flag == 10 || (flag >= 32)) {
							ret.append(a[i]);
						}

					}
					ret.append(tempCurrent);
					if (ret.toString().indexOf(" --More-- ") > 0 || ret.toString().toLowerCase().indexOf("---- more ----") > 0) {
						out.write(32);
						out.flush();
					}
					if (ret.toString().endsWith(pattern1)) {

						return ret.toString();
					}
					if (ret.toString().endsWith(pattern2)) {
						return ret.toString();
					}

				}
			}

			return ret.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @description 返回命令集合的结果
	 * @author wangxiangyong
	 * @date Feb 25, 2013 11:11:50 AM
	 * @return String
	 * @param command
	 * @return
	 */
	public String getCommantValue(String[] command) {

		// setPrompt("Password:");
		// sendCommand("en");
		// setPrompt("#");
		// sendCommand(enPasswd);
		String result = "";
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < command.length; i++) {
				write(command[i]);
				result = readcommand(USER_PROMPT, SU_PROMPT);
				if (null != result && !result.equals("user or password error")) {
					String[] st = result.split("\r\n");
					StringBuffer buff = new StringBuffer();
					for (int j = 0; j < st.length; j++) {
						if (j == st.length - 1) {
							buff.append(st[j]);
						} else if (!st[j].toString().toLowerCase().contains("--more--") || !st[j].toString().toLowerCase().contains("---- more ----")) {
							buff.append(st[j]).append("\r\n");
						}
					}

					result = buff.toString();

				}
				sb.append(result);
			}

		} catch (Exception e) {
			SysLogger.error("BaseTelnet.getCommantValue( String[] command) error", e);
			e.printStackTrace();
		} finally {
			disconnect();
		}

		return result;
	}

	/**
	 * 发送指令
	 * 
	 * @param value
	 */
	public void write(String value) {
		try {
			out.println(value);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public void disconnect() {
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (telnet != null && telnet.isConnected()) {
				telnet.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public int getDEFAULT_TELNET_PORT() {
		return DEFAULT_TELNET_PORT;
	}

	public void setDEFAULT_TELNET_PORT(int default_telnet_port) {
		DEFAULT_TELNET_PORT = default_telnet_port;
	}
}
