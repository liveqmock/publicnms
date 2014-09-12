package com.afunms.polling.telnet;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.indicators.model.NodeGatherIndicators;

//import org.apache.commons.io.output.FileWriterWithEncoding;

//import org.apache.commons.io.output.FileWriterWithEncoding;

public class Wrapper {

	/** debugging level */
	private final static int debug = 0;

	private final static boolean output = false;

	protected ScriptHandler scriptHandler = new ScriptHandler();

	private Thread reader;

	protected InputStream in;

	protected OutputStream out;

	protected Socket socket;

	protected String host;

	protected int port = 23;

	protected Vector script = new Vector();

	protected String username;

	protected String password;

	protected String loginPrompt;

	protected String passwordPrompt;

	private List monitorItemList;

	/** Connect the socket and open the connection. */
	public void connect(String host, int port) throws IOException {
		if (debug > 0)
			System.err.println("Wrapper: connect(" + host + "," + port + ")");
		try {
			this.host = host;
			this.port = port;
			socket = new java.net.Socket(host, port);
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (Exception e) {
			System.err.println("Wrapper: " + host + ":" + port + "连接超时");
			disconnect();
			throw ((IOException) e);
		}
	}

	/** Disconnect the socket and close the connection. */
	public void disconnect() throws IOException {
		if (debug > 0)
			System.err.println("Wrapper: disconnect()");
		if (socket != null)
			socket.close();
	}

	/**
	 * Login into remote host. This is a convenience method and only works if
	 * the prompts are "login:" and "Password:".
	 * 
	 * @param user
	 *            the user name
	 * @param pwd
	 *            the password
	 */
	public void login(String user, String pwd, String loginPrompt, String passwordPrompt, String shellPrompt) throws IOException {
		this.username = user;
		this.password = pwd;
		this.loginPrompt = loginPrompt;
		this.passwordPrompt = passwordPrompt;

		waitfor(loginPrompt); // throw output away
		send(user);
		waitfor(passwordPrompt); // throw output away

		this.setPrompt(shellPrompt);
		send(pwd);
	}

	protected void relogin() throws IOException {
		login(username, password, loginPrompt, passwordPrompt, prompt);
	}

	/**
	 * Set the prompt for the send() method.
	 */
	private String prompt = null;

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getPrompt() {
		return prompt;
	}

	/**
	 * Send a command to the remote host. A newline is appended and if a prompt
	 * is set it will return the resulting data until the prompt is encountered.
	 * 
	 * @param cmd
	 *            the command
	 * @return output of the command or null if no prompt is set
	 */
	public String send(String cmd) throws IOException {
		System.out.println("========your cmd is " + cmd);

		return null;
	}

	/**
	 * Wait for a string to come from the remote host and return all that
	 * characters that are received until that happens (including the string
	 * being waited for).
	 * 
	 * @param match
	 *            the string to look for
	 * @return skipped characters
	 */

	public String waitfor(String[] searchElements) throws IOException {
		ScriptHandler[] handlers = new ScriptHandler[searchElements.length];
		for (int i = 0; i < searchElements.length; i++) {
			// initialize the handlers
			handlers[i] = new ScriptHandler();
			handlers[i].setup(searchElements[i]);
		}

		byte[] b1 = new byte[1];
		int n = 0;
		StringBuffer ret = new StringBuffer();
		String current;

		while (n >= 0) {

			n = read(b1);

			if (n > 0) {
				current = new String(b1, 0, n, "iso-8859-1");

				String tempCurrent = "";
				char[] a = current.toCharArray();
				for (int i = 0; i < a.length; i++) {
					char flag = a[i];
					if (flag == 0 || flag == 13 || flag == 10 || (flag >= 32)) {
						tempCurrent += a[i];
					} else {
						tempCurrent += a[i];
					}
				}
				if (debug > 0) {
					System.err.print(current);
				}
				ret.append(tempCurrent);

				for (int i = 0; i < handlers.length; i++) {
					if (handlers[i].match(ret.toString().getBytes("iso-8859-1"), ret.toString().length())) {

						// System.out.println("=====输入结果=="+ret.toString());
						return ret.toString();
					}
				} // for
				// try
				// {
				// Thread.sleep(100);
				// } catch (InterruptedException e)
				// {
				// }
			} // if
		} // while

		return null; // should never happen
	}

	public String waitfor(String match) throws IOException {
		String[] matches = new String[1];
		matches[0] = match;
		return waitfor(matches);
	}

	/**
	 * Read data from the socket and use telnet negotiation before returning the
	 * data read.
	 * 
	 * @param b
	 *            the input buffer to read in
	 * @return the amount of bytes read
	 */
	public int read(byte[] b) throws IOException {
		return -1;
	};

	/**
	 * Write data to the socket.
	 * 
	 * @param b
	 *            the buffer to be written
	 */
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public String getTerminalType() {
		return "dumb";
	}

	public Dimension getWindowSize() {
		return new Dimension(80, 24);
	}

	public void setLocalEcho(boolean echo) {
		if (debug > 0)
			System.err.println("local echo " + (echo ? "on" : "off"));
	}

	public String getHost() {
		return host;
	}

	// /**
	// * 返回telnet linux的所有信息
	// * @return Hashtable
	// */
	// public Hashtable getTelnetMonitorDetail()
	// {
	// String operateSystem = null;
	// try
	// {
	// operateSystem = send("uname");
	// } catch (IOException e)
	// {
	// //e.printStackTrace();
	// }
	//		
	// System.out.println("operateSystem =========&&&&&&&&&&&&&&&&&&&&&&&& " +
	// operateSystem);
	//		
	// if(operateSystem != null && operateSystem.equalsIgnoreCase("Linux"))
	// {
	// //return LinuxInfoParser.getTelnetMonitorDetail(this);
	// //return AIXInfoParser.getTelnetMonitorDetail(this);
	// }
	// else if(operateSystem != null && operateSystem.equalsIgnoreCase("SunOS"))
	// {
	// return SolarisInfoParser.getTelnetMonitorDetail(this);
	// }
	// else if(operateSystem != null && operateSystem.equalsIgnoreCase("AIX"))
	// {
	// return AIXInfoParser.getTelnetMonitorDetail(this);
	// }
	//		
	// return null;
	// }

	public static String changeCharset(String str) {
		if (str != null && str.length() > 0) {
			try {
				byte[] bs = str.getBytes("iso-8859-1");
				String result = new String(bs, "gbk");
				// System.out.println("result = " + result);
				return result;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return str;
			}
		}
		return str;
	}

	public static String changeCharset(String str, String oldCharset, String newCharset) {
		if (str != null && str.length() > 0) {
			try {
				byte[] bs = str.getBytes(oldCharset);
				return new String(bs, newCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return str;
			}
		}
		return str;
	}

	public void log(String info) {

		if (output) {
			Writer out = null;
			File file = new File(host + ".log");

			if (file.exists() && file.length() >= 20 * 1024 * 1024) {
				try {
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				// out = new FileWriterWithEncoding(file ,
				// Charset.forName("GBK"), true);// 追加写入

				// out = new BufferedWriter(new OutputStreamWriter( new
				// FileOutputStream(file), "UTF8"));
				out.write(info);
				out.write("\n");
				out.flush();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				file = null;
			}
		}
	}

	/**
	 * 返回telnet linux的所有信息
	 * 
	 * @return Hashtable
	 */
	public Hashtable getTelnetMonitorDetail() {
		String operateSystem = null;
		try {
			operateSystem = send("uname").trim();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		System.out.println("=================operateSystem=" + operateSystem);
		if (operateSystem != null && operateSystem.indexOf("Linux")>-1) {

			System.out.println("------------------Linux--------------");
			return LinuxInfoParser.getTelnetMonitorDetail(this);
		} else if (operateSystem != null && operateSystem.indexOf("SunOS")>-1) {
			System.out.println("------------------SunOS--------------");
			return SolarisInfoParser.getTelnetMonitorDetail(this);
		} else if (operateSystem != null && operateSystem.indexOf("AIX")>-1) {
			System.out.println("------------------AIX--------------");
			return AIXInfoParser.getTelnetMonitorDetail(this);
		}

		return null;
	}

	/**
	 * 返回telnet linux的所有信息
	 * 
	 * @return Hashtable
	 */
	public Hashtable getTelnetMonitorDetail(List monitorItemList) {
		String operateSystem = null;
		try {
			operateSystem = send("uname").trim();
		} catch (IOException e) {
			// e.printStackTrace();
		}

		System.out.println("=================operateSystem=" + operateSystem);
		if (operateSystem != null && operateSystem.equalsIgnoreCase("Linux")) {

			System.out.println("------------------Linux--------------");
			return LinuxInfoParser.getTelnetMonitorDetail(this);

		} else if (operateSystem != null && operateSystem.equalsIgnoreCase("SunOS")) {
			return SolarisInfoParser.getTelnetMonitorDetail(this);
		} else if (operateSystem != null && operateSystem.equalsIgnoreCase("AIX")) {
			return AIXInfoParser.getTelnetMonitorDetail(this);
		}

		return null;
	}

	public static void main(String[] arg) {

		Wrapper telnet = new Wrapper();

		telnet.password = "123456";
		telnet.username = "root";
		telnet.passwordPrompt = ":";
		telnet.loginPrompt = ":";
		telnet.prompt = "#";
		telnet.host = "10.10.152.213";

		try {

			telnet.connect(telnet.host, telnet.port);
			telnet.login(telnet.username, telnet.password, telnet.loginPrompt, telnet.passwordPrompt, telnet.prompt);

			System.out.println(telnet.send("cat /proc/version"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List getMonitorItemList() {
		return monitorItemList;
	}

	public void setMonitorItemList(List monitorItemList) {
		this.monitorItemList = monitorItemList;
	}

}
