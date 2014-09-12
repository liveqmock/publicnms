package com.afunms.polling.ssh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;

import com.afunms.common.util.SysLogger;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.sun.activation.registries.MailcapParseException;

import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;

/**
 * 
 * @description SSHÔ¶³ÌµÇÂ¼¹¤¾ßÀà
 * @author wangxiangyong
 * @date Jun 7, 2012 10:58:47 AM
 */
public class SSHUtil {

	private  Session session;
	private ChannelShell channel;
	private  Expect4j expect = null;
	private static final long defaultTimeOut = 2000;
	private StringBuffer buffer = new StringBuffer();
	public static final int LOGIN_EXECUTION_FAILURE_OPCODE = 0;// µÇÂ¼Ê§°Ü
	public static final int LOGIN_EXECUTION_SUCCESS_OPCODE = 1;// µÇÂ¼³É¹¦
	public static final int COMMAND_EXECUTION_FAILURE_OPCODE = -1;// ÃüÁîÖ´ĞĞÊ§°Ü
	public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;// ÃüÁîÖ´ĞĞ³É¹¦
	public static final String BACKSLASH_R = "\r";
	public static final String BACKSLASH_N = "\n";
	public static final String COLON_CHAR = ":";
	public static String ENTER_CHARACTER = BACKSLASH_R;
	public static final int SSH_PORT = 22;

	// ÕıÔòÆ¥Åä£¬ÓÃÓÚ´¦Àí·şÎñÆ÷»òÍø¹ÜÉè±¸·µ»ØµÄ½á¹û
//	 public static String[] promptRegEx = new String[] { "~]#", "~#", "#",
//	 ":~#",":", "/$", ">", "]", "---- more ----", "--More--", "---- More ----"
//	 };
	public static String[] promptRegEx = new String[] { "#", ":", "$", ">", "]", "---- more ----", "--More--", "---- More ----" };
	public static String[] errorMsg = new String[] { "could not acquire the config lock " };// Õë¶Ô·µ»ØµÄ½á¹û×öÅĞ¶Ï

	// ssh·şÎñÆ÷»òÉè±¸Éè±¸ipµØÖ·
	private String ip;
	// ssh·şÎñÆ÷»òÉè±¸Éè±¸µÇÈë¶Ë¿Ú
	private int port;
	// ssh·şÎñÆ÷»òÉè±¸Éè±¸µÇÈëÓÃ»§Ãû
	private String user;
	// ssh·şÎñÆ÷»òÉè±¸Éè±¸µÇÈëÃÜÂë
	private String password;

	public   SSHUtil(String ip, int port, String user, String password) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
		expect = getExpect();
	}

	/**
	 * »ñÈ¡·şÎñÆ÷·µ»ØµÄĞÅÏ¢
	 * 
	 * @return ·şÎñ¶ËµÄÖ´ĞĞ½á¹û
	 */
	public  String getResponse() {
		return buffer.toString();
	}

	/**
	 * ÑéÖ¤µÇÂ¼ÊÇ·ñ³É¹¦
	 * 
	 * @return
	 */
	public boolean testLogin() {
		if (expect == null) {
			return false;
		} else {
			return true;
		}
	}

	// »ñµÃExpect4j¶ÔÏó£¬¸Ã¶ÔÓÃ¿ÉÒÔÍùSSH·¢ËÍÃüÁîÇëÇó
	private Expect4j getExpect() {
		try {
			if (isClosed()) {
			System.out.println(String.format("Start logging to %s@%s:%s", user, ip, port));
			JSch jsch = new JSch();
			session = jsch.getSession(user, ip, port);
			session.setPassword(password);
			Hashtable<String, String> config = new Hashtable<String, String>();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			localUserInfo ui = new localUserInfo();
			session.setUserInfo(ui);
			session.connect();
			channel = (ChannelShell) session.openChannel("shell");
			Expect4j expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
			channel.connect();
			System.out.println(String.format("Logging to %s@%s:%s successfully!", user, ip, port));
			return expect;
			}
		} catch (Exception ex) {
			SysLogger.error("Connect to " + ip + ":" + port + "failed,please check your username and password!");
			ex.printStackTrace();
		}
		return null;
	}

	public String executeCmds(String[] commands) {
		if (isClosed()) {
			expect = getExpect();
		}
		// Èç¹ûexpect·µ»ØÎª0£¬ËµÃ÷µÇÈëÃ»ÓĞ³É¹¦
		if (expect == null) {
			return "0";
		}

		Closure closure = new Closure() {
			public void run(ExpectState expectState) throws Exception {
				buffer.append(expectState.getBuffer());// buffer is string

				if (expectState.getBuffer().toString().toLowerCase().indexOf("press any key to continue")>-1||expectState.getBuffer().toString().toLowerCase().indexOf("---- more ----") > -1 || expectState.getBuffer().toString().toLowerCase().indexOf("--more--") > -1) {
					expect.send(" ");
				}
				expectState.exp_continue();
				// if
				// (expectState.getBuffer().toString().toLowerCase().indexOf("----
				// more ----") > -1 ||
				// expectState.getBuffer().toString().toLowerCase().indexOf("--more--")
				// > -1) {
				// expect.send(" ");
				// }
			}
		};
		List<Match> lstPattern = new ArrayList<Match>();
		String[] regEx = promptRegEx;
		if (regEx != null && regEx.length > 0) {
			synchronized (regEx) {
				for (String regexElement : regEx) {
					try {

						RegExpMatch mat = new RegExpMatch(regexElement, closure);
						lstPattern.add(mat);

					} catch (MalformedPatternException e) {
						e.printStackTrace();
						return COMMAND_EXECUTION_FAILURE_OPCODE + "";
					} catch (Exception e) {
						e.printStackTrace();
						return COMMAND_EXECUTION_FAILURE_OPCODE + "";
					}

				}
				lstPattern.add(new EofMatch(new Closure() { // should cause
							public void run(ExpectState state) {
							}
						}));
				lstPattern.add(new TimeoutMatch(defaultTimeOut, new Closure() {
					public void run(ExpectState state) {

					}
				}));
			}
		}

		try {
			boolean isSuccess = true;
			for (String strCmd : commands) {
				isSuccess = isSuccess(lstPattern, strCmd);
			}

			// ·ÀÖ¹×îºóÒ»¸öÃüÁîÖ´ĞĞ²»ÁË
			isSuccess = !checkResult(expect.expect(lstPattern));

			// ÕÒ²»µ½´íÎóĞÅÏ¢±êÊ¾³É¹¦
			String response = buffer.toString();

			for (String msg : errorMsg) {
				if (response.indexOf(msg) > -1) {
					return msg;
				}
			}

			return response;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "-1";
		}finally{
			  if(channel!=null)
				channel.disconnect();
			
		}
	}

	// ¼ì²éÖ´ĞĞÊÇ·ñ³É¹¦
	private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
		try {
			boolean isFailed = checkResult(expect.expect(objPattern));

			if (!isFailed) {

				expect.send(strCommandPattern);
				expect.send("\r");

				return true;
			}
			
			return false;
		} catch (MailcapParseException ex) {
			ex.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	// ¼ì²éÖ´ĞĞ·µ»ØµÄ×´Ì¬
	private boolean checkResult(int intRetVal) {
		if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
			return true;
		}
		return false;
	}

	/**
	 * ¹Ø±ÕSSHÔ¶³ÌÁ¬½Ó
	 */
	public void disconnect() {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}
	private boolean isClosed() {
		if (session != null) {
			return !session.isConnected();
		}
		return true;
	}
	// µÇÈëSSHÊ±µÄ¿ØÖÆĞÅÏ¢
	// ÉèÖÃ²»ÌáÊ¾ÊäÈëÃÜÂë¡¢²»ÏÔÊ¾µÇÈëĞÅÏ¢µÈ
	public static class localUserInfo implements UserInfo {
		String passwd;

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {

		}
	}

	public static void main(String[] args) throws IOException {
//		 SSHUtil t=new SSHUtil("172.24.24.235", 22, "itims", "itimsdev");
		SSHUtil t = new SSHUtil("10.10.117.176", 22, "root", "root");
		// t.isSuccess(objPattern, strCommandPattern)
		// String[] aStrings = { "system-view", "undo vlan 5", "quit", "quit" };
		String[] aStrings = { "", "display current-configuration", };
		try {
			String af = t.executeCmds(aStrings);

			System.out.println(af.toString());
			// System.out.println(af.replaceAll(" ---- more ----",
			// "").replaceAll("42d", "").replaceAll(" ",
			// "").replaceAll("\\\\[", ""));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			t.disconnect();
		}
	}

}
