package com.afunms.polling.telnet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.afunms.common.util.SysLogger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHWrapper extends Wrapper {
	private static int debug = 1;

	private String identity = UUID.randomUUID().toString();

	JSch jsch = new JSch();

	Session session = null;

	/** Connect the socket and open the connection. */
	public void connect(String host, int port, String username, String password) throws IOException {
		if (debug > 0)
			System.err.println("Wrapper: connect(" + host + "," + port + ")");

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;

		if (isClosed()) {
			try {
				session = jsch.getSession(username, host, port);
				session.setPassword(password);
				session.setUserInfo(defaultUserInfo);
				session.connect();
				System.out.println("connected");
			} catch (JSchException e) {
				e.printStackTrace();
				throw new IOException("connect or authenticate failed");
			}

		}

	}

	private boolean isClosed() {
		if (session != null) {
			return !session.isConnected();
		}
		return true;
	}

	public String send(String command) throws IOException {
		if (isClosed()) {
			connect(this.host, this.port, this.username, this.password);
		}
		// System.out.println("begin to send cmd = " + command);
		Channel channel = null;
		try {
			channel = session.openChannel("shell");
		} catch (JSchException e) {
			e.printStackTrace();
		}

		if (channel == null) {
			System.out.println("can not open channel shell");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		String result = "";
		String request = new String("echo " + identity + "\n" + command + "\n" + "\nexit\n");
		ChannelShell shell = (ChannelShell) channel;
		shell.setPtyType("vt320", 512, 100, 1024, 768);
		try {
			InputStream input = new ByteArrayInputStream(request.getBytes());
			channel.setInputStream(input);

			input = new ByteArrayInputStream(request.getBytes());
			channel.setInputStream(input);

			InputStream in = channel.getInputStream();

			// channel.setOutputStream(out);
			// channel.setExtOutputStream(System.err);
			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				// System.out.println("waiting for input ...");
				// avai = in.available();
				// System.out.println("available = "+avai );
				while ((in.available()) > 0) {
					// System.out.println("begin to read");
					int i = in.read(tmp, 0, 1024);
					// System.out.println("i" + " = "+ i);
					if (i < 0)
						break;
					sb.append(new String(tmp, 0, i));
					// System.out.println("sb = " + sb.toString());
					// avai = 0;
				}
				if (channel.isClosed()) {
					// System.out.println("exit-status: " +
					// channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(200);
				} catch (Exception ee) {
				}
			}

			String executeResult = sb.toString();
			executeResult = executeResult.replaceAll("\r\n", "\n");
			if (executeResult.length() > 0) {
				// if(command.indexOf("entstat")>=0)
				// SysLogger.info(executeResult);

				String[] results = executeResult.split("\n");

				sb.setLength(0);

				boolean needAppend = false;

				// if(command.indexOf("entstat")>=0){
				// for(int i = 0 ; i < results.length - 1 ; i++)
				// {
				// String line = results[i];
				// SysLogger.info(line);
				// }
				// }

				for (int i = 0; i < results.length - 1; i++) {
					String line = results[i];
					// if(command.indexOf("entstat")>=0)
					// SysLogger.info(line);
					if (needAppend) {
						// if(command.indexOf("entstat")>=0)
						// SysLogger.info("&&&&&&&&&& "+i+" ====
						// "+(results.length - 2));
						if (line.contains(" exit") && i >= results.length - 2) {
							// SysLogger.info(line);
							needAppend = false;
							break;
						}
						// if(command.indexOf("entstat")>=0)
						// SysLogger.info(line);
						sb.append(line);
						sb.append("\n");
					} else {
						// if(command.indexOf("entstat")>=0)
						// SysLogger.info(line);
						if (line.equals(identity) || line.equals("$" + identity) || line.equals("#" + identity)) {
							// if(command.indexOf("entstat")>=0)
							// SysLogger.info(results[i+1]);
							if (results[i + 1].indexOf("Hardware Address:") >= 0 || results[i + 1].indexOf("load average:") >= 0 || results[i + 1].indexOf("$hdisk") >= 0 || results[i + 1].indexOf("BEIST") >= 0 || command.equalsIgnoreCase("lsuser ALL")
									|| results[i + 1].indexOf("$AIX") >= 0 || command.equalsIgnoreCase("cat /etc/group")) {

								needAppend = true;
							} else {
								i++;
								needAppend = true;
							}

						}
					}
				}

				if (sb.length() > 1) {
					sb.setLength(sb.length() - 1);
				}

				result = sb.toString();

				// SysLogger.info("cmd = " + command + " , result = " + result);
				return result;
			}

		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			channel.disconnect();
		}

		// System.out.println("cmd = " + command + " result = " + result);
		// log("cmd = " + command + " , result = " + result);
		return "";

	}

	public void disconnect() throws IOException {
		if (debug > 0)
			System.out.println("Wrapper: disconnect()");
		if (session != null) {
			session.disconnect();
		}
	}

	public void connect(String host, int port) throws IOException {
		throw new IOException("this ssh wrapper is not support this method  , please use connect(String host, int port, String username, String password) ");
	}

	public void login(String user, String pwd, String loginPrompt, String passwordPrompt, String shellPrompt) throws IOException {
		throw new IOException("this ssh wrapper is not support this method  , please use connect(String host, int port, String username, String password) ");
	}

	public static void main(String[] args) {

//		 String ip = "172.25.25.5";
//		 String username = "itims";
//		 String password = "itims";

//		String ip = "172.24.24.235";
//		String username = "itims";
//		String password = "itimsdev";

		// String ip = "10.0.9.23";
		// String username = "nms";
		// String password = "nms123";
		String ip = "10.10.117.176";
		String username = "root";
		String password = "root";
		SSHWrapper ssh = new SSHWrapper();
		try {
			ssh.connect(ip, 22, username, password);

			String result;

			long startTime = System.currentTimeMillis();

			// log(ip , ip + ".log");

			result = ssh.send("\ns");

			System.out.println("================" + result);
			System.out.println("================");
			// solaris
			// log(ip , "cmdbegin:version");
			// result = ssh.send("uname -snrvmapi");
			// log(ip , result);
			// log(ip , "cmdbegin:swapinfo");
			// result = ssh.send("swap -s");
			// log(ip , result);
			// log(ip , "cmdbegin:process");
			// result = ssh.send("ps -eo pid,user,pcpu,pmem,time,rss,comm");
			// log(ip , result);
			// log(ip , "cmdbegin:cpu");
			// result = ssh.send("sar -u 1 3");
			// log(ip , result);
			// log(ip , "cmdbegin:allmemory");
			// result = ssh.send("prtconf | head -2");
			// log(ip , result);
			// log(ip , "cmdbegin:vmstat");
			// result = ssh.send("vmstat");
			// log(ip , result);
			// log(ip , "cmdbegin:cpuconfig");
			// result = ssh.send("psrinfo -vp");
			// log(ip , result);
			// log(ip , "cmdbegin:disk");
			// result = ssh.send("df -k");
			// log(ip , result);
			// log(ip , "cmdbegin:diskperf");
			// result = ssh.send("sar -d 1 2");
			// log(ip , result);
			// log(ip , "cmdbegin:netconf");
			// result = ssh.send("dladm show-dev");
			// log(ip , result);
			// log(ip , "cmdbegin:netperf");
			// result = ssh.send("netstat -ian");
			// log(ip , result);
			// log(ip , "cmdbegin:netaddr");
			// result = ssh.send("netstat -pn");
			// log(ip , result);
			// log(ip , "cmdbegin:service");
			// result = ssh.send("svcs");
			// log(ip , result);
			// log(ip , "cmdbegin:uname");
			// result = ssh.send("uname -sn");
			// log(ip , result);
			// log(ip , "cmdbegin:usergroup");
			// result = ssh.send("cat /etc/group");
			// log(ip , result);
			// log(ip , "cmdbegin:user");
			// result = ssh.send("cat /etc/passwd");
			// log(ip , result);
			// log(ip , "cmdbegin:date");
			// result = ssh.send("date");
			// log(ip , result);
			// log(ip , "cmdbegin:uptime");
			// result = ssh.send("uptime");
			// log(ip , result);
			// log(ip , "cmdbegin:end");

			// linux
			// log(ip , "cmdbegin:version");
			// result = ssh.send("cat /proc/version");
			// System.out.println("##############£º"+result);
			// //log(ip , result);
			// //log(ip , "cmdbegin:cpuconfig");
			// result = ssh.send("cat /proc/cpuinfo | egrep \"model
			// name|processor|cpu MHz|cache size\"");
			// //log(ip , result);
			// log(ip , "cmdbegin:disk");
			// result = ssh.send("df -k");
			// log(ip , result);
			// log(ip , "cmdbegin:diskperf");
			// result = ssh.send("sar -d 1 3");
			// log(ip , result);
			// log(ip , "cmdbegin:cpu");
			// result = ssh.send("sar -u 1 3");
			// log(ip , result);
			// log(ip , "cmdbegin:memory");
			// result = ssh.send("free");
			// log(ip , result);
			// log(ip , "cmdbegin:process");
			// result = ssh.send("ps -aux");
			// log(ip , result);
			// log(ip , "cmdbegin:mac");
			// result = ssh.send("/sbin/ip addr");
			// log(ip , result);
			// log(ip , "cmdbegin:interface");
			// result = ssh.send("sar -n DEV 1 3");
			// log(ip , result);
			// log(ip , "cmdbegin:uname");
			// result = ssh.send("uname -sn");
			// log(ip , result);
			// log(ip , "cmdbegin:usergroup");
			// result = ssh.send("cat /etc/group");
			// log(ip , result);
			// log(ip , "cmdbegin:user");
			// result = ssh.send("cat /etc/passwd");
			// log(ip , result);
			// log(ip , "cmdbegin:date");
			// result = ssh.send("date");
			// log(ip , result);
			// log(ip , "cmdbegin:uptime");
			// result = ssh.send("uptime");
			// log(ip , result);
			// log(ip , "cmdbegin:end");

			// ibm - aix
			// String cmd = "oslevel";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:version");
			// log(ip , result);
			//			
			//			
			// cmd = "vmstat";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:vmstat");
			// log(ip , result);
			//			
			//			
			// cmd = "lsps -s";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:lsps");
			// log(ip , result);
			//			
			//			
			// cmd = "swap -s";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:swap");
			// log(ip , result);
			//			
			//			
			// cmd = "ps gv | head -n 1; ps gv | egrep -v \"RSS\" | sort +6b -7
			// -n -r";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:process");
			// log(ip , result);
			//			
			//			
			// cmd = "sar -u 1 3";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:cpu");
			// log(ip , result);
			//			
			//			
			// cmd = "prtconf";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:allconfig");
			// log(ip , result);
			//			
			//			
			// cmd = "df -m";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:disk");
			// log(ip , result);
			//			
			//			
			// cmd = "sar -d 1 2";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:diskperf");
			// log(ip , result);
			//			
			//			
			// cmd = "netstat -ian";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:netperf");
			// log(ip , result);
			//			
			//			
			// cmd = "entstat -d en0 |egrep 'Bytes|Hardware Address|Link
			// Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v
			// 'XOFF|XON';entstat -d en2 |egrep 'Bytes|Hardware Address|Link
			// Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v
			// 'XOFF|XON';entstat -d en3 |egrep 'Bytes|Hardware Address|Link
			// Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v
			// 'XOFF|XON'";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:netallperf");
			// log(ip , result);
			//			
			// cmd = "uname -sn";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:uname");
			// log(ip , result);
			//			
			//			
			// cmd = "lssrc -a";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:service");
			// log(ip , result);
			//			
			//			
			// cmd = "cat /etc/group";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:usergroup");
			// log(ip , result);
			//			
			//			
			// cmd = "lsuser ALL";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:user");
			// log(ip , result);
			//			
			//			
			// cmd = "date";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:date");
			// log(ip , result);
			//			
			//			
			// cmd = "uptime";
			// result = ssh.send(cmd);
			// log(ip , "cmdbegin:uptime");
			// log(ip , result);
			// log(ip , "cmdbegin:end");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ssh.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static final UserInfo defaultUserInfo = new UserInfo() {
		public String getPassphrase() {
			return null;
		}

		public String getPassword() {
			return null;
		}

		public boolean promptPassword(String arg0) {
			return false;
		}

		public boolean promptPassphrase(String arg0) {
			return false;
		}

		public boolean promptYesNo(String arg0) {
			return true;
		}

		public void showMessage(String arg0) {
		}
	};

}
