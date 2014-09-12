package com.afunms.polling.telnet;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.CmdResult;
import com.afunms.initialize.ResourceCenter;

/**
 * 
 * 本来用来登录连接telnet 登录华为路由器 实现登录后输入命令返回，命令的信息 主要采集完成后一定要调用
 * disconnect（）方法来关闭telnet连接 实现获取配置文件，实现写配置文件
 * 
 * @author konglq
 * 
 */

public class Huawei3comvpn extends BaseVo {

	private final static int debug = 0;
	private TelnetClient telnet = new TelnetClient();

	private InputStream in; // 

	private PrintStream out;

	private String DEFAULT_FLAG = ">";
	private String DEFAULT_PROMPT = "#";

	private final String USER_PROMPT = ">";// 一般用户标识符
	private final String SU_PROMPT = "#";// 超级用户标识符
	private final String SYS_PROMPT = "]";// 全局视图用户标识符

	private String Loginuser = "Username:";// 用户
	private String Loginpassword = "Password:";// 用户的密码

	private String Loginsuuser = "Username:";// su用户
	private String Loginpsuassword = "Password:";// su用户的密码

	private int nummber = 30000;// 定义一个读取字符的总数

	/**
	 * telnet 端口
	 */
	private int port = 23;

	/**
	 * 用户名
	 */
	private String user = "";

	/**
	 * 密码
	 */
	private String password = "";

	/**
	 * IP 地址
	 */
	private String ip = "";

	/**
	 * 缺省端口
	 */
	private int DEFAULT_TELNET_PORT = 23;

	private String suuser = "";

	private String supassword = "";

	/**
	 * 
	 * 默认构造
	 */
	public Huawei3comvpn() {
	}

	/**
	 * 
	 * 构造
	 * 
	 * @param ip
	 *            地址
	 * @param user
	 *            默认用户
	 * @param password
	 *            默认用户密码
	 * @param suuser
	 *            su 用户
	 * @param supassword
	 *            用户密码
	 * @param defaule
	 *            默认的系统标识符号 $ 或是 #
	 */
	public Huawei3comvpn(String ip, String user, String password, int port, String suuser, String supassword, String defaule) {
		this.ip = ip;
		this.port = this.getDEFAULT_TELNET_PORT();
		this.user = user; // 默认用户
		this.password = password; // 默认用户密码
		this.supassword = supassword;// su用户
		this.suuser = suuser;// su用户的密码
		this.DEFAULT_PROMPT = defaule;// 系统默认标识符号
		this.DEFAULT_TELNET_PORT = port;// 端口号
	}

	/**
	 * 
	 * 连接路由器
	 * 
	 * @return boolean 连接成功返回true，否则返回false
	 */
	private boolean connect() {

		boolean isConnect = true;

		try {
			telnet.setConnectTimeout(5000);// 设置从未连接到连接状态的超时时间
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

	/***************************************************************************
	 * 读取信息
	 * 
	 * @param pattern
	 *            结束符号
	 * @return 如果读到对应的字符返回字符读到的所有结果，如果没有读到则返回null
	 */
	private String readUntil(String pattern) {
		StringBuffer sb = new StringBuffer();
		try {

			// System.out.println("======读取");
			char lastChar = pattern.charAt(pattern.length() - 1);
			// System.out.println("======读取---"+lastChar);
			char ch = (char) in.read();
			int n = 0;
			// System.out.println("======ch---"+ch);
			boolean flag = true;
			while (flag) {
				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
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
			return null;
		}
		return sb.toString();
	}

	/*
	 * 提供两个字符串，作为流结束的判断 命令执行后，可能返回成功，也可能返回失败。提供两个字符串后，能快速识别命令是否执行结束，加快程序执行效率
	 */
	private String readUntil1(String pattern1, String pattern2) {
		StringBuffer sb = new StringBuffer();
		try {

			char ch = (char) in.read();
			int n = 0;
			boolean flag = true;

			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				if (sb.toString().endsWith("in unit1 login")) // 如果以pattern1结尾，说明命令执行成功
				{
					return "in unit1 login";
				} else if (sb.toString().endsWith(pattern1)) // 规定，如果以pattern1结尾，说明命令执行成功
				{
					return pattern1;
				} else if (sb.toString().endsWith(pattern2)) // 如果以pattern1结尾，说明命令执行成功
				{
					return pattern2;
				} else if (sb.toString().indexOf("% Login failed!") > 0) {
					return "user or password error";
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
	 * @description 提供三个结束符，作为流结束的判断
	 *              命令执行后，可能返回成功，也可能返回失败。提供两个字符串后，能快速识别命令是否执行结束，加快程序执行效率
	 * @author wangxiangyong
	 * @date Feb 23, 2013 11:04:49 AM
	 * @return String
	 * @param pattern1
	 * @param pattern2
	 * @param pattern3
	 * @return
	 */
	private String readUntil(String pattern1, String pattern2, String pattern3) {
		StringBuffer sb = new StringBuffer();
		try {

			char ch = (char) in.read();
			int n = 0;
			boolean flag = true;

			while (flag) {

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}

				if (sb.toString().endsWith(pattern1)) // 规定，如果以pattern1结尾，说明命令执行成功
				{
					return pattern1;
				} else if (sb.toString().endsWith(pattern2)) // 如果以pattern2结尾，说明命令执行成功
				{
					return pattern2;
				} else if (sb.toString().endsWith(pattern3)) // 如果以pattern3结尾，说明命令执行成功
				{
					return pattern3;
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
	 * @description 提供结束字符数组，作为流结束的判断
	 *              命令执行后，可能返回成功，也可能返回失败。提供两个字符串后，能快速识别命令是否执行结束，加快程序执行效率
	 * @author wangxiangyong
	 * @date Feb 23, 2013 10:49:15 AM
	 * @return String
	 * @param patterns
	 * @return
	 */
	private String readUntil(String[] patterns) {
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

	private boolean isLoginSuccess(String pattern) {
		StringBuffer sb = new StringBuffer();
		try {

			// System.out.println("======读取");
			char lastChar = pattern.charAt(pattern.length() - 1);

			char ch = (char) in.read();
			int n = 0;
			while (true) {
				// System.out.print(ch);// ---需要注释掉

				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}

				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						// System.out.println(sb.toString());
						return true;
					}
				}
				if (sb.toString().contains("Login failed!")) {
					// System.out.println(sb.toString());
					return false;
				}

				ch = (char) in.read();
				n++;
				if (n > this.nummber) {// 如果读取的字符个数大于2万个字符就认没有正确
					// sb.delete(0, sb.length());
					// sb.append("login error");
					break;
				}
				// System.out.println(n);

			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/***************************************************************************
	 * 
	 * 读取命令返回结果
	 * 
	 * @param pattern
	 * @return
	 */
	private String readcommand(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			byte[] tempchars = new byte[30];

			int charread = 0;

			// 读入多个字符到字符数组中，charread为一次读取字符数
			byte[] b1 = new byte[1];
			int n = 0;
			int nn = 0;
			StringBuffer ret = new StringBuffer();
			String current;

			while (n >= 0) {
				// System.out.println("--读取命令信息");
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

					if (ret.toString().endsWith("---- More ----")) {
						out.write(32);
						out.flush();
					}

					if (ret.toString().endsWith(pattern)) {

						return ret.toString();
					}
					if (ret.toString().endsWith(pattern)) {

						break;
					}

				} // if
				// nn++;
				// if(nn>this.nummber)
				// {
				// return null;
				// }
			}

			return ret.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @description TODO
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

					if (ret.toString().toLowerCase().indexOf("---- more ----") > 0||ret.toString().toLowerCase().indexOf("--more--") > 0) {
						out.write(32);
						out.flush();
					}

					if (ret.toString().indexOf(pattern1) > 0) {

						return ret.toString();
					}
					if (ret.toString().indexOf(pattern2) > 0) {
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

	/***************************************************************************
	 * 
	 * 读取命令返回结果
	 * 
	 * @param pattern
	 * @return
	 */
	private void readcommand(String pattern1, String pattern2, StringBuffer ret, String each) {
		try {

			// 读入多个字符到字符数组中，charread为一次读取字符数
			byte[] b1 = new byte[1];
			int n = 0;
			int nn = 0;

			String current;

			while (n >= 0) {
				// System.out.println("--读取命令信息");
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

					if (ret.toString().endsWith("---- More ----")||ret.toString().endsWith("--More--")) {
						out.write(32);
						out.flush();
					}

					if (ret.toString().indexOf(pattern1) > 0) {

						break;
					}
					if (ret.toString().indexOf(pattern2) > 0) {
						break;
					}

				} // if

			}

			// System.out.println(ret.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 发送指令
	 * 
	 * @param value
	 */
	private void write(String value) {
		try {
			out.println(value);

			out.flush();
			// System.out.println(value);// ---需要注释掉
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 发送命令并返回结果
	 * 
	 * @param command
	 *            命令行
	 * @return 命令的结果
	 */
	private String sendCommand(String command) {
		String content = null;
		try {
			write("\r\n");
			// content = readcommand(DEFAULT_PROMPT);
			// System.out.println(content);
			write(command);
			content = readcommand(DEFAULT_PROMPT);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void disconnect() {
		try {
			if (null != in) {
				in.close();
			}

			if (null != out) {
				out.close();
			}
			if (null != telnet && telnet.isConnected()) {
				telnet.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 统一登录方法，登录分为2部分 一个是普通用户登录，二是su用户登录
	 * 
	 * @return true 或是 false true表示成功，false表示失败
	 */
	private boolean Loginnet() {
		boolean connetflg = false;
		boolean loginflg = false;

		if (null != this.user && this.user.trim().length() > 0 && null != this.password && this.password.trim().length() > 0) {
			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) {// 成功

				// String aa = readUntil(this.Loginsuuser); // 先读取
				// System.out.println("读取登录字符是否成功"+aa);
				// write(user);
				String aa = readUntil(this.Loginpassword);
				// System.out.println("====2"+aa);
				write(password);
				aa = readUntil1(DEFAULT_PROMPT, this.Loginpassword);//
				if (aa.equals("user or password error"))
					loginflg = false;
				else if (aa.equals(DEFAULT_PROMPT))
					loginflg = true;
				else if (aa.equals("Password:"))
					loginflg = false;
				// System.out.println(aa);

				// System.out.println("第一此登录成功");
			}

		} else if (loginflg == false) {// 用户密码不正确返回
			// System.out.println("=======1==");
			this.disconnect(); // 关闭连接
			return false;
		}
		// System.out.println("");
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {

			// System.out.println("=====开始su 用户登录");
			// readUntil(this.Loginsuuser); //先读取
			// System.out.println("读取登录字符是否成功"+aa);
			write(this.suuser);
			String suresult = readUntil(this.Loginpsuassword);
			write(this.supassword);
			suresult = readUntil(DEFAULT_PROMPT);// 在这里登录成功

			loginflg = true;

		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			return false;
		}

		return loginflg;

	}

	/**
	 * 
	 * 针对h3c/haiwei设备备份设备的配置
	 * 
	 * @return 字符串格式的配置
	 */
	public String backupConfFile(String bkpType) {

		String result = "";
		// result = this.Getcommantvalue("disp cu");// 获取命令的结果
		if (bkpType.equals("run"))
			result = this.getCommantValue("0", "disp cu");// 获取命令的结果
		// 0:h3c/haiwei设备配置文件备份
		else
			result = this.getCommantValue("0", "disp saved-configuration");// 获取命令的结果
		// 0:h3c/haiwei设备配置文件备份
		// 对结果进行格式化
		if (null != result && !result.equals("user or password error")) {
			String[] st = result.split("\r\n");
			StringBuffer buff = new StringBuffer();
			for (int i = 1; i < st.length - 1; i++) {

				if (!st[i].startsWith("  ---- More ----")) {
					buff.append(st[i]).append("\r\n");
				}
			}
			result = buff.toString();

		}

		return result;
	}

	/**
	 * 
	 * 备份扫描命令设备的配置
	 * 
	 * @return 字符串格式的配置
	 */
	public String BackupConfFile(String[] content) {

		String result = "";
		// result = this.Getcommantvalue("disp cu");// 获取命令的结果

		result = this.Getcommantvalue1(content);// 获取命令的结果

		// 对结果进行格式化
		// if (null != result && !result.equals("user or password error")) {
		// String[] st = result.split("\r\n");
		// StringBuffer buff = new StringBuffer();
		// for (int i = 1; i < st.length-1; i++) {
		//				
		// if(!st[i].startsWith(" ---- More ----"))
		// {
		// buff.append(st[i]).append("\r\n");
		// }
		// }
		// result = buff.toString();
		//			
		// }

		return result;
	}

	/**
	 * 把指定文件名的文件上传到指定的IP设备上，并且使该配置文件在设备中生效
	 * 
	 * @param remoteFile
	 *            上传后的文件名，如/cfg1.cfg
	 * @param localFile
	 *            本地文件名，如C:\\Documents and Settings\\GZM\\cfg1.cfg
	 * @param ftpIp
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPassword
	 * @return
	 */
	public boolean setupNewConfFile(String remoteFile, String localFile, String ftpIp, int ftpPort, String ftpUser, String ftpPassword) {
		boolean flg = Loginnet();

		String aa = null;
		if (flg) {
			write("system-view");
			System.out.println(readUntil("]"));

			write("ftp server enable");
			System.out.println(readUntil("]"));
			write("quit");
			System.out.println(readUntil(">"));

			FTPComply ft = new FTPComply();
			// flg = ft.uploadFile("/cfg1.cfg", "C:\\Documents and
			// Settings\\GZM\\cfg1.cfg","10.10.152.254",21,"admin","admin",12000);
			flg = ft.uploadFile(remoteFile, localFile, ftpIp, ftpPort, ftpUser, ftpPassword, 12000);
		}
		if (flg) {
			flg = false;
			write("startup saved-configuration " + remoteFile.substring(1));
			aa = readUntil("[Y/N]:");
			System.out.println(aa);
			if (!aa.equals("user or password error")) {
				write("y");
				aa = readUntil(">");
				System.out.println(aa);
				if (!aa.equals("user or password error")) {
					write("reboot");
					aa = readUntil("[Y/N]?");
					System.out.println(aa);
					if (!aa.equals("user or password error")) {
						write("n");
						aa = readUntil("[Y/N]?");
						System.out.println(aa);
						if (!aa.equals("user or password error")) {
							flg = true;
							write("y");
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
							}
							System.out.println("wake up");
						}
					}
				}
			}
		}
		this.disconnect();
		return flg;
	}

	public boolean Writeconffile(String conf) {
		boolean flg = Loginnet();

		if (flg) {
			// 进入到配置模式
			write("system-view");
			write(conf);// 写配置文件
			if (this.suuser != null && this.supassword != null && this.supassword.length() > 0) {

				write("quit");
			}
			write("quit");
			write("save");
			readUntil("N");
			write("y");
			readUntil(">");// 等待保存结束

		}

		return flg;
	}

	/**
	 * 
	 * 获取参数获取命令行的信息
	 * 
	 * @param command
	 *            //命令行
	 * @return 返回命令的显示内容，如果连接不上或异常返回null
	 */
	public String Getcommantvalue(String command) {
		String result = "";// 命令结果
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		// String Liginstr="";
		if (null != this.user && this.user.trim().length() > 0 && null != this.password && this.password.trim().length() > 0) {

			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) {// 成功

				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度
				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// String aa = readUntil(this.Loginsuuser); // 先读取
				String aa = readUntil("Password:");
				// System.out.println("读取登录字符是否成功"+aa);
				write(user);
				aa = readUntil(this.Loginpassword);
				// System.out.println("====2"+aa);
				write(password);
				aa = readUntil(DEFAULT_PROMPT);//
				if (aa.equals("user or password error"))
					loginflg = false;
				else {
					loginflg = true;
					// System.out.println("第一此登录成功");
				}
			}
		} else if (loginflg == false) {// 用户密码不正确返回
			// System.out.println("=======1==");
			this.disconnect(); // 关闭连接
			return "user or password error";
		}
		// System.out.println("");
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			// System.out.println("=====开始su 用户登录");
			// readUntil(this.Loginsuuser); //先读取
			// System.out.println("读取登录字符是否成功"+aa);
			write(this.suuser);
			readUntil(this.Loginpsuassword);
			write(this.supassword);
			readUntil(DEFAULT_PROMPT);// 在这里登录成功

			loginflg = true;

		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			return "user or password error";
		}

		// 如果登录成则发送命令
		if (loginflg) {
			result = this.sendCommand(command);
			// System.out.println("采集结果=======" + result);
		}

		this.disconnect(); // 关闭连接

		return result;
	}

	/**
	 * 
	 * @description 获取命令返回结果，此方法主要针对读取配置文件的
	 * @author wangxiangyong
	 * @date Feb 23, 2013 10:09:06 AM
	 * @return String
	 * @param type
	 *            0:h3c/huawei 设备配置文件 1：通用的
	 * @param command
	 * @return
	 */

	public String getCommantValue(String type, String command) {
		String result = "";// 命令结果

		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		try {

			connetflg = this.connect();// 当telnet用户名、密码为空时就直接创建连接
			if (null != this.password && this.password.trim().length() > 0) {
				// 连接不成功返回null
				if (connetflg) {// 成功
					// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度/////////////
					String[] patterns = { USER_PROMPT, SYS_PROMPT, this.Loginuser, Loginpassword, "Password:" };
					String[] pass_patterns = { "in unit1 login", USER_PROMPT, SYS_PROMPT, Loginpassword, "% Login failed!" };
					String aa = readUntil(patterns); // 先读取
					if (aa.equalsIgnoreCase("Password:")) {
						write(password);
						aa = readUntil(pass_patterns);
					}
					if (null != this.user && this.user.trim().length() > 0) {
						if (aa.equalsIgnoreCase("Username:")) {
							write(user);
							aa = readUntil("Password:");
							write(password);
							aa = readUntil(pass_patterns);
						}
					}

					if (aa.equals("% Login failed!") || aa.equals("user or password error")) {
						loginflg = false;
					} else if (aa.equals(USER_PROMPT) || aa.equals(SYS_PROMPT)) {
						loginflg = true;
					} else if (aa.equals("Password:")) {
						loginflg = false;
					} else if (aa.equals("in unit1 login")) {
						write("\r\n");
						loginflg = true;
					}

				}
			} else if (loginflg == false) {// 用户密码不正确返回
				// this.disconnect(); // 关闭连接
				return "user or password error";
			}
			// 如果登录成功，再判断su的用户密码的可用性
			if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
				write(suuser);
				String pass = readUntil(USER_PROMPT, SYS_PROMPT, "Password:");

				if (pass.equalsIgnoreCase("Password:"))
					write(this.supassword);

				readUntil1(USER_PROMPT, SYS_PROMPT);// 在这里登录成功
				loginflg = true;

			} else if (loginflg == false) {
				// this.disconnect(); // 关闭连接
				return "user or password error";
			}

			// 如果登录成则发送命令
			if (loginflg) {
				write(command);
				String pattern1 = "";
				String pattern2 = "";
				if (type != null && type.equals("0")) {
					pattern1 = "return\r\n";
					pattern2 = USER_PROMPT;
				} else {
					pattern1 = USER_PROMPT;
					pattern2 = SYS_PROMPT;
				}
				result = readcommand(pattern1, pattern2);
				if (result != null){
					
					result = result.substring(result.indexOf(command));
				}
			}
		} catch (Exception e) {
			SysLogger.error("telnet 命令执行出现问题com.afunms.polling.telnet.Huawei3comvpn 方法：getCommantValue(String command)", e);
		} finally {
			this.disconnect(); // 关闭连接
		}

		return result;
	}

	/**
	 * 
	 * @description 无返回结果的命令
	 * @author wangxiangyong
	 * @date Apr 22, 2013 1:33:44 PM
	 * @return void
	 * @param type 1:在超级管理员下执行
	 * @param command
	 */
	public void getCommantValue(String type, String[] command) {

		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		try {

			connetflg = this.connect();// 当telnet用户名、密码为空时就直接创建连接
			if (null != this.password && this.password.trim().length() > 0) {
				// 连接不成功返回null
				if (connetflg) {// 成功
					// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度/////////////
					String[] patterns = { USER_PROMPT, SYS_PROMPT, this.Loginuser, Loginpassword, "Password:" };
					String[] pass_patterns = { "in unit1 login", USER_PROMPT, SYS_PROMPT, Loginpassword, "% Login failed!" };
					String aa = readUntil(patterns); // 先读取
					if (aa.equalsIgnoreCase("Password:")) {
						write(password);
						aa = readUntil(pass_patterns);
					}
					if (null != this.user && this.user.trim().length() > 0) {
						if (aa.equalsIgnoreCase("Username:")) {
							write(user);
							aa = readUntil("Password:");
							write(password);
							aa = readUntil(pass_patterns);
						}
					}

					if (aa.equals("% Login failed!") || aa.equals("user or password error")) {
						loginflg = false;
					} else if (aa.equals(USER_PROMPT) || aa.equals(SYS_PROMPT)) {
						loginflg = true;
					} else if (aa.equals("Password:")) {
						loginflg = false;
					} else if (aa.equals("in unit1 login")) {
						write("\r\n");
						loginflg = true;
					}

				}
			}
			// 如果登录成功，再判断su的用户密码的可用性
			if (type!=null&&type.equals("1")&&loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
				write(suuser);
				String pass = readUntil(USER_PROMPT, SYS_PROMPT, "Password:");

				if (pass.equalsIgnoreCase("Password:"))
					write(this.supassword);

				readUntil1(USER_PROMPT, SYS_PROMPT);// 在这里登录成功
				loginflg = true;

			}

			// 如果登录成则发送命令
			if (loginflg) {
				StringBuffer result = new StringBuffer();
				if (command != null && command.length > 0) {
					for (int i = 0; i < command.length; i++) {
						String each = (String) command[i];
						write(each);

						readcommand(USER_PROMPT, SYS_PROMPT, result, each);
					}
				}
				String pattern1 = USER_PROMPT;
				String pattern2 = SYS_PROMPT;

				readcommand(pattern1, pattern2);

			}
		} catch (Exception e) {
			SysLogger.error("telnet 命令执行出现问题com.afunms.polling.telnet.Huawei3comvpn 方法：getCommantValue(String command)", e);
		} finally {
			this.disconnect(); // 关闭连接
		}

	}

	/**
	 * 命令集合
	 * 
	 * @param command
	 * @return
	 */
	public String Getcommantvalue1(String[] command) {
		StringBuffer result = null;// 命令结果
		StringBuffer realResult = new StringBuffer();// 命令结果
		String tempRes = "";
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		// String Liginstr="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date());
		if (null != this.user && this.user.trim().length() > 0 && null != this.password && this.password.trim().length() > 0) {

			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) {// 成功

				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度
				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				String aa = readUntil(this.Loginsuuser); // 先读取
				write(user);

				aa = readUntil("Password:");
				write(password);
				aa = readUntil1(DEFAULT_PROMPT, "Password:");
				if (aa.equals("user or password error"))
					loginflg = false;
				else if (aa.equals(DEFAULT_PROMPT))
					loginflg = true;
				else if (aa.equals("Password:"))
					loginflg = false;
			}
		} else if (loginflg == false) {// 用户密码不正确返回
			// System.out.println("=======1==");
			this.disconnect(); // 关闭连接
			return "user or password error";
		}
		// System.out.println("");
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {

			// System.out.println("=====开始su 用户登录");
			// readUntil(this.Loginsuuser); //先读取
			// System.out.println("读取登录字符是否成功"+aa);
			readUntil1(">", "unit1 login");
			write(this.suuser);
			String suresult = readUntil1(this.Loginpsuassword, DEFAULT_PROMPT);
			if (suresult.equalsIgnoreCase(this.Loginpsuassword)) {
				write(this.supassword);
				suresult = readUntil(DEFAULT_PROMPT);// 在这里登录成功
			} else if (suresult.equalsIgnoreCase(DEFAULT_PROMPT)) {
				// suresult=readUntil(DEFAULT_PROMPT);// 已经为超级管理员
				loginflg = true;
			}

		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			return "user or password error";
		}

		// 如果登录成则发送命令
		if (loginflg) {
			// String aa = this.readUntil(">");
			// out.println("\r\n");
			// out.flush();
			// String aa = readUntil1(">","unit1 login");
			if (command != null && command.length > 0) {
				for (int i = 0; i < command.length; i++) {
					result = new StringBuffer();
					String each = (String) command[i];
					write(each);

					readcommand(USER_PROMPT, SYS_PROMPT, result, each);
					String resultTemp = result.toString();
					if (null != resultTemp && !resultTemp.equals("user or password error")) {
						String[] st = resultTemp.split("\r\n");
						StringBuffer buff = new StringBuffer();
						buff.append("\r\n-----------------Date(" + time + ")-----------------\r\n");
						buff.append("-----------------begin(" + each + ")-----------------\r\n");
						for (int j = 1; j < st.length - 1; j++) {

							if (!st[j].startsWith("  ---- More ----")) {
								buff.append(st[j]).append("\r\n");
							}
						}
						buff.append("-----------------end(" + each + ")-----------------\r\n");
						realResult.append(buff.toString());

					}
				}
			}
			// result = this.sendCommand(command);
			// System.out.println("采集结果=======" + result);
		}

		this.disconnect(); // 关闭连接
		tempRes = realResult.toString() + "";
		return tempRes;
	}

	/**
	 * 
	 * @param command
	 * @param list
	 * @param ip
	 * @return
	 */
	public void getCommantValue(String[] command, List<CmdResult> list, String ip) {
		StringBuffer result = null;// 命令结果
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		// String Liginstr="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date());
		if ( null != this.password && this.password.trim().length() > 0) {

			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) {
				// 连接不成功返回null
				if (connetflg) {// 成功
					// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度/////////////
					String[] patterns = { USER_PROMPT, SYS_PROMPT, this.Loginuser, Loginpassword, "Password:" };
					String[] pass_patterns = { "in unit1 login", USER_PROMPT, SYS_PROMPT, Loginpassword, "% Login failed!" };
					String aa = readUntil(patterns); // 先读取
					if (aa.equalsIgnoreCase("Password:")) {
						write(password);
						aa = readUntil(pass_patterns);
					}
					if (null != this.user && this.user.trim().length() > 0) {
						if (aa.equalsIgnoreCase("Username:")||aa.equalsIgnoreCase("login:")) {
							write(user);
							aa = readUntil("Password:");
							write(password);
							aa = readUntil(pass_patterns);
						}
					}

					if (aa.equals("% Login failed!") || aa.equals("user or password error")) {
						loginflg = false;
					} else if (aa.equals(USER_PROMPT) || aa.equals(SYS_PROMPT)) {
						loginflg = true;
					} else if (aa.equals("Password:")) {
						loginflg = false;
					} else if (aa.equals("in unit1 login")) {
						write("\r\n");
						loginflg = true;
					}

				}
			}
		} else if (loginflg == false) {// 用户密码不正确返回
			this.disconnect(); // 关闭连接
			CmdResult cmdResult = new CmdResult();
			cmdResult.setIp(ip);
			cmdResult.setCommand("---------");
			cmdResult.setResult("登录失败!");
			list.add(cmdResult);
		}
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			String[] patterns = { USER_PROMPT, SYS_PROMPT };
			write(this.suuser);
			readUntil(this.Loginpsuassword);
			write(this.supassword);

			readUntil(patterns);// 在这里登录成功

			loginflg = true;

		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			CmdResult cmdResult = new CmdResult();
			cmdResult.setIp(ip);
			cmdResult.setCommand("---------");
			cmdResult.setResult("登录失败!");
			list.add(cmdResult);
		}
		// 如果登录成则发送命令

		if (loginflg) {
//			String aa = readUntil1(">", "unit1 login");
			if (command != null && command.length > 0) {

				for (int i = 0; i < command.length; i++) {
					boolean isSucess = true;
					result = new StringBuffer();
					CmdResult cmdResult = new CmdResult();
					String each = (String) command[i];
					write(each);
					String[] patterns = { USER_PROMPT, SYS_PROMPT,SU_PROMPT};

					readUntil(patterns);// 在这里登录成功
//					readcommand(USER_PROMPT, SYS_PROMPT,SU_PROMPT);
//					String resultTemp = result.toString();
					isSucess = true;
//					if (null != resultTemp && !resultTemp.equals("user or password error")) {
//						String[] st = resultTemp.split("\r\n");
//						for (int j = 0; j < st.length; j++) {
//
//							if (!st[j].startsWith("---- More ----")) {
//								if (st[j].indexOf("% Unrecognized command found at '^' position.") > -1 || st[j].indexOf("% Ambiguous command found at '^' position.") > -1 || st[j].indexOf("% Incomplete command found at '^' position.") > -1 || st[j].indexOf("Error:") > -1) {
//									cmdResult.setIp(ip);
//									cmdResult.setCommand(each);
//									cmdResult.setResult("执行失败!");
//									isSucess = false;
//									break;
//								}
//							}
//						}
//
//					}
					if (isSucess) {

						cmdResult.setIp(ip);
						cmdResult.setCommand(each);
						cmdResult.setResult("执行完成!");
					}
					list.add(cmdResult);
				}
			}
		}

		this.disconnect(); // 关闭连接

	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Feb 25, 2013 12:05:42 PM
	 * @return boolean
	 * @return
	 */
	public boolean login() {
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		connetflg = this.connect();// 当telnet用户名、密码为空时就直接创建连接
		if (null != this.password && this.password.trim().length() > 0) {
			// 连接不成功返回null
			if (connetflg) {// 成功
				// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度/////////////
				String[] patterns = { USER_PROMPT, SYS_PROMPT, this.Loginuser, Loginpassword, "Password:" };
				String[] pass_patterns = { "in unit1 login", USER_PROMPT, SYS_PROMPT, Loginpassword, "% Login failed!" };
				String aa = readUntil(patterns); // 先读取
				if (aa.equalsIgnoreCase("Password:")) {
					write(password);
					aa = readUntil(pass_patterns);
				}
				if (null != this.user && this.user.trim().length() > 0) {
					if (aa.equalsIgnoreCase("Username:")||aa.equalsIgnoreCase("login:")) {
						write(user);
						aa = readUntil("Password:");
						write(password);
						aa = readUntil(pass_patterns);
					}
				}

				if (aa.equals("% Login failed!") || aa.equals("user or password error")) {
					loginflg = false;
				} else if (aa.equals(USER_PROMPT) || aa.equals(SYS_PROMPT)) {
					loginflg = true;
				} else if (aa.equals("Password:")) {
					loginflg = false;
				} else if (aa.equals("in unit1 login")) {
					write("\r\n");
					loginflg = true;
				}

			}
		}
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			String[] patterns = { USER_PROMPT, SYS_PROMPT, SU_PROMPT, Loginpassword };
			
			write(suuser);
			String pass = readUntil(patterns);


			if (pass.equalsIgnoreCase("Password:"))
				write(this.supassword);
             System.out.println(pass+"==="+suuser+"-----"+supassword);
			String sign = readUntil(patterns);// 在这里登录成功
			if (sign.equals(SU_PROMPT) || sign.equals(SYS_PROMPT)) {
				loginflg = true;
			} 
		}
		return loginflg;
	}

	/**
	 * 
	 * @description 手工运行命令集合
	 * @author wangxiangyong
	 * @date Feb 25, 2013 3:16:44 PM
	 * @return String
	 * @param command
	 * @return
	 */
	public String getCommantValue(String[] command) {
		StringBuffer result = null;// 命令结果
		StringBuffer realResult = new StringBuffer();// 命令结果
		// 先判断用户，密码是否为null或是空的字符
		boolean loginflg = this.login();
		// 如果登录成则发送命令
		if (loginflg) {
			if (command != null && command.length > 0) {
				for (int i = 0; i < command.length; i++) {
					result = new StringBuffer();
					String each = (String) command[i];
					write(each);
					String pattern1 = USER_PROMPT;
					String pattern2 = SYS_PROMPT;
					if (each.startsWith("dis")) {
						pattern1 = "return\r\n";
						pattern2 = "return\r\n";
					}
//					readcommand(pattern1, pattern2, result, each);
					String resultTemp = readcommand(pattern1, pattern2);

					if (null != resultTemp && !resultTemp.equals("user or password error")) {
						resultTemp = resultTemp.substring(resultTemp.indexOf(each));
						String[] st = resultTemp.split("\r\n");
						StringBuffer buff = new StringBuffer();
						for (int j = 0; j < st.length; j++) {
							if (j == st.length - 1) {
								buff.append(st[j].trim());
							} else if (!st[j].startsWith("  ---- More ----")) {
								buff.append(st[j]).append("\r\n");
							}
						}
						realResult.append(buff.toString());

					}
				}
			}
		}

		this.disconnect(); // 关闭连接

		return realResult.toString();
	}

	/**
	 * 手工运行命令集合,返回结果数组
	 * 
	 * @param command
	 *            命令集合
	 * @return
	 */
	public String[] getCommantValues(String[] command) {
		String[] results = new String[command.length]; // 结果集
		StringBuffer result = null;// 命令结果
		StringBuffer realResult = new StringBuffer();// 命令结果
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		if (null != this.user && this.user.trim().length() > 0 && null != this.password && this.password.trim().length() > 0) {

			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) {// 成功

				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度
				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				String aa = readUntil(this.Loginsuuser); // 先读取
				write(user);
				aa = readUntil("Password:");
				write(password);
				aa = readUntil1(USER_PROMPT, "Password:");
				if (aa.equals("user or password error"))
					loginflg = false;
				else if (aa.equals(USER_PROMPT))
					loginflg = true;
				else if (aa.equals("Password:"))
					loginflg = false;
			}
		} else if (loginflg == false) {
			// 用户密码不正确返回
			this.disconnect(); // 关闭连接
			results = new String[] { "user or password error" };
			return results;
		}
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			write(this.suuser);
			readUntil(this.Loginpsuassword);
			write(this.supassword);

			readUntil(USER_PROMPT);// 在这里登录成功

			loginflg = true;

		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			results = new String[] { "user or password error" };
			return results;
		}

		// 如果登录成则发送命令
		if (loginflg) {
			// String aa = readUntil1(">","unit1 login");
			if (command != null && command.length > 0) {
				for (int i = 0; i < command.length; i++) {
					realResult = new StringBuffer();
					result = new StringBuffer();
					String each = (String) command[i];
					// SysLogger.info("------------------------each:"+each);
					write(each);

					readcommand(USER_PROMPT, SU_PROMPT, result, each);
					String resultTemp = result.toString();
					if (null != resultTemp && !resultTemp.equals("user or password error")) {
						String[] st = resultTemp.split("\r\n");
						StringBuffer buff = new StringBuffer();
						for (int j = 0; j < st.length; j++) {
							if (j == st.length - 1) {
								buff.append(st[j].trim());
							} else if (!st[j].startsWith("  ---- More ----")) {
								buff.append(st[j]).append("\r\n");
							}
						}
						realResult.append(buff.toString());

					}
					// 添加到结果集合里
					// SysLogger.info(realResult.toString());
					results[i] = realResult.toString();

				}
			}
		}

		this.disconnect(); // 关闭连接

		return results;
	}

	// 测试密码是否正确
	public String isPasswordCorrect() {
		String result = "";// 命令结果
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;
		// String Liginstr="";
		if (null != this.user && this.user.trim().length() > 0 && null != this.password && this.password.trim().length() > 0) {
			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) {// 成功

				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// /////////////////设置不同的账户类型，会有不同的字符提示，这个地方会影响到代码执行速度
				// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// String aa = readUntil(this.Loginsuuser); // 先读取
				String aa = readUntil("Password:");
				write(password);
				aa = readUntil1(USER_PROMPT, "Password:");
				if (aa.equals("user or password error"))
					loginflg = false;
				else if (aa.equals(USER_PROMPT))
					loginflg = true;
				else if (aa.equals("Password:"))
					loginflg = false;
			}
		} else if (loginflg == false) {// 用户密码不正确返回
			// System.out.println("=======1==");
			this.disconnect(); // 关闭连接
			return "user or password error";
		}
		// System.out.println("");
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			// System.out.println("=====开始su 用户登录");
			// readUntil(this.Loginsuuser); //先读取
			// System.out.println("读取登录字符是否成功"+aa);
			write(this.suuser);
			readUntil(this.Loginpsuassword);
			write(this.supassword);
			readUntil1(USER_PROMPT, "");// 在这里登录成功//bug
			loginflg = true;
		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			return "su user or password error";
		}
		this.disconnect(); // 关闭连接
		return result;
	}

	/**
	 * 
	 * 用来修改普通用户和su 用户的密码
	 * 
	 * @param modifyuser
	 *            修改的用户
	 * @param threeA
	 *            是否有3a认证
	 * @param newpassword
	 *            需要修改的密码
	 * @param encrypt
	 *            加密方式 0代表加密，1代表不加密
	 * @param newpassword
	 *            新密码
	 * @return
	 */
	public boolean modifypassowd(String modifyuser, String newpassword, int encrypt, String threeA, String ostype) {
		// 先判断用户，密码是否为null或是空的字符
		boolean connetflg = false;// 连接标识符号
		boolean loginflg = false;// 是否登录成功
		boolean threeaflg = false;// 是否有3a的认证方式
		// System.out.println("---modifyuser-----"+modifyuser+"--------------newpassword-------"+newpassword+"----------encrypt------------"+encrypt+"----threeA---"+threeA+"-----ostype----"+ostype);
		if (null != this.password && this.password.trim().length() > 0) {
			connetflg = this.connect();// telnet连接
			// 连接不成功返回null
			if (connetflg) // 成功
			{
				String aa = readUntil1(this.Loginsuuser, this.Loginpassword); // 先读取
				
				if (null != this.user && this.user.trim().length() > 0 &&aa.equalsIgnoreCase("Username:")) {
					write(user);
					aa = readUntil(this.Loginpassword);
					write(password);
				} else if (aa.equalsIgnoreCase("Password:")) {
					write(password);
				}
				aa = readUntil1(USER_PROMPT, "failed!");//
				if (aa.equals("user or password error"))
					loginflg = false;
				else if (aa.equals(USER_PROMPT))
					loginflg = true;
				else if (aa.equals("Password:"))
					loginflg = false;

			}

		} else if (loginflg == false) // 用户密码不正确返回
		{
			this.disconnect(); // 关闭连接
			return false;
		}
		// System.out.println("");
		// 如果登录成功，再判断su的用户密码的可用性
		if (loginflg && null != suuser && suuser.trim().length() > 0 && null != supassword && supassword.trim().length() > 0) {
			//readUntil(this.Loginsuuser); // 先读取

			write(this.suuser);
			readUntil(this.Loginpsuassword);
			write(this.supassword);
			readUntil1(USER_PROMPT, SU_PROMPT);// 在这里登录成功//bug
			loginflg = true;
		} else if (loginflg == false) {
			this.disconnect(); // 关闭连接
			return false;
		}
		// 如果登录成则发送命令
		String SYS_PROMPT="]";
		if (loginflg) {
			String aa = "";
//			write("disp version");
//			aa = readUntil1(USER_PROMPT,SU_PROMPT);
			write("system-view");
			aa = readUntil(SYS_PROMPT);
			if (null != threeA && threeA.trim().length() > 0) {
				write(threeA);
				threeaflg = true;
			}

			// encrypt 加密方式 0代表加密，1代表不加密
			if (encrypt == 0) {
				// write("local-user " + modifyuser + " password cipher " +
				// newpassword);
				write("local-user " + modifyuser);
				aa = readUntil1(USER_PROMPT,SYS_PROMPT);
				write("password cipher " + newpassword);
				aa=readUntil1(USER_PROMPT,SYS_PROMPT);
				if (aa.contains("Unrecognized command found")) {
					this.disconnect(); // 关闭连接
					return false;
				}
				// System.out.println(aa);
			}

			if (encrypt == 1) {
				write("local-user " + modifyuser);
				aa=readUntil1(USER_PROMPT,SYS_PROMPT);
				write("password simple " + newpassword);
				aa=readUntil1(USER_PROMPT,SYS_PROMPT);
				if (aa.contains("Unrecognized command found")) {
					this.disconnect(); // 关闭连接
					return false;
				}
				// System.out.println(aa);
			}

			if (threeaflg) {// 如果有3a认证则需要对一个退出
				write("quit");
			}
			write("quit");
			write("save");
			aa=readUntil1(USER_PROMPT,"N");
			// System.out.println(aa);
			write("y");
//			aa = readUntil1(USER_PROMPT,"key):");
//			write("\r\n");
//			aa = readUntil1(USER_PROMPT,"fully.");
			this.disconnect(); // 关闭连接
			return true;
		}

		this.disconnect(); // 关闭连接

		return true;

	}

	private String getVersion(String content) {
		String method = "";
		Hashtable hash = (Hashtable) ResourceCenter.getInstance().getCfgHash().get("h3c");
		// System.out.println("----------------1517------hash---------"+hash);
		Enumeration enu = hash.keys();
		while (enu.hasMoreElements()) {
			String s = (String) enu.nextElement();
			// System.out.println("----------------1521---------------"+s);
			// System.out.println("----------------content.contains(s)---------------"+content.contains(s));
			if (content.contains(s)) {
				method = (String) hash.get(s);
				// System.out.println("-------------1527--------------------------"+method);
				break;
			}
		}
		return method;
	}

	/**
	 * 
	 * @author GZM
	 */
	public boolean Modifypassowd(String modifyuser, String newpassword) {
		String result = "";// 命令结果
		boolean isSuccess = false;
		String aa = null;
		isSuccess = this.connect();// telnet连接

		// 连接不成功返回null
		if (isSuccess) // 成功
		{
			isSuccess = false;
			aa = readUntil("Username:"); // 先读取
			if (!isContainInvalidateWords(aa)) {
				write(user);
				aa = readUntil("Password:");
				if (!isContainInvalidateWords(aa)) {
					write(password);
					boolean b = isLoginSuccess(">");
					if (b) {
						write("system-view");
						aa = readUntil("]");
						if (!isContainInvalidateWords(aa)) {
							write("local-user " + modifyuser);
							aa = readUntil("]");
							if (!isContainInvalidateWords(aa)) {
								write("password simple " + newpassword);
								aa = readUntil("]");
								if (!isContainInvalidateWords(aa)) {
									write("service-type telnet level 3");
									aa = readUntil("]");
									if (!isContainInvalidateWords(aa)) {
										write("quit");
										aa = readUntil("]");
										if (!isContainInvalidateWords(aa)) {
											write("user-interface vty 0 4");
											aa = readUntil("]");
											if (!isContainInvalidateWords(aa)) {
												write("authentication-mode scheme");
												aa = readUntil("]");
												if (!isContainInvalidateWords(aa)) {
													write("quit");
													aa = readUntil("]");
													if (!isContainInvalidateWords(aa)) {
														write("quit");
														isSuccess = true;
														/*
														 * aa = readUntil(">");
														 * if(!isContainInvalidateWords(aa)) {
														 * write("save"); aa =
														 * readUntil(">");
														 * if(!isContainInvalidateWords(aa)) {
														 * out.write(89);//y
														 * out.write(13);//回车键
														 * aa = readUntil(":");
														 * out.write(13);//回车键
														 * //aa =
														 * readUntil(">");
														 * isSuccess = true; } }
														 */
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		this.disconnect(); // 关闭连接
		return isSuccess;
	}

	private boolean isContainInvalidateWords(String content) {
		boolean isContained = false;
		if (content.contains("failed") || content.contains("Unknown")) {
			isContained = true;
		}
		return isContained;
	}

	public int getDEFAULT_TELNET_PORT() {
		return DEFAULT_TELNET_PORT;
	}

	public void setDEFAULT_TELNET_PORT(int default_telnet_port) {
		DEFAULT_TELNET_PORT = default_telnet_port;
	}

	public String getSuuser() {
		return suuser;
	}

	public void setSuuser(String suuser) {
		this.suuser = suuser;
	}

	public String getSupassword() {
		return supassword;
	}

	public void setSupassword(String supassword) {
		this.supassword = supassword;
	}

	public TelnetClient getTelnet() {
		return telnet;
	}

	public void setTelnet(TelnetClient telnet) {
		this.telnet = telnet;
	}

	public String getDEFAULT_PROMPT() {
		return DEFAULT_PROMPT;
	}

	public void setDEFAULT_PROMPT(String default_prompt) {
		DEFAULT_PROMPT = default_prompt;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static void main(String[] args) {
		Huawei3comvpn tvpn = new Huawei3comvpn();
		tvpn.setSuuser("");// su
		tvpn.setSupassword("");// su密码
		tvpn.setUser("1");// 用户
		tvpn.setPassword("1");// 密码
		tvpn.setIp("10.10.151.176");// ip地址
		tvpn.setDEFAULT_PROMPT(">");// 结束标记符号
		tvpn.setPort(23);
		if (tvpn.Modifypassowd("1", "1")) {
			System.out.println("success");
		}
	}

	public String t(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			while (true) {
				System.out.print(ch + "@" + (int) ch);
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				if (sb.toString().endsWith("--More--")) {
					write((char) 32 + "");
				}
				if (ch == 8) {
					sb = sb.deleteCharAt(sb.length() - 1);
					sb = sb.deleteCharAt(sb.length() - 1);
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
