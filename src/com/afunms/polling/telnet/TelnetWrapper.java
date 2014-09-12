
package com.afunms.polling.telnet;

import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Hashtable;



public class TelnetWrapper extends Wrapper
{
	
	private String enterLine = "\n";
	
	
	protected TelnetProtocolHandler handler;

	/** debugging level */
	private final static int debug = 0;

	public TelnetWrapper()
	{
		handler = new TelnetProtocolHandler()
		{
			/** get the current terminal type */
			public String getTerminalType()
			{
				return "vt320";
			}

			/** get the current window size */
			public Dimension getWindowSize()
			{
				return new Dimension(80, 25);
			}

			/** notify about local echo */
			public void setLocalEcho(boolean echo)
			{
				/* EMPTY */
			}

			/** write data to our back end */
			public void write(byte[] b) throws IOException
			{
				out.write(b);
			}

			/** sent on IAC EOR (prompt terminator for remote access systems). */
			public void notifyEndOfRecord()
			{
			}
		};
	}

	public TelnetProtocolHandler getHandler()
	{
		return handler;
	}

	public void connect(String host, int port) throws IOException
	{
		super.connect(host, port);
		handler.reset();
	}
	
	
	public void setSystemEnterLine(String enterLine)
	{
		if(enterLine != null && (enterLine.contains("\r") || enterLine.contains("\n")))
			this.enterLine = enterLine;
	}
	
	
	
	

	/**
	 * Send a command to the remote host. A newline is appended and if a prompt
	 * is set it will return the resulting data until the prompt is encountered.
	 * 
	 * @param cmd
	 *            the command
	 * @return output of the command or null if no prompt is set
	 */
	public String send(String cmd) throws IOException
	{
		if(isClosed())
		{
			connect(host, port);
			relogin();
		}
		
	
		byte arr[];
		arr = (cmd + enterLine).getBytes("iso-8859-1");
		handler.transpose(arr);
		if (getPrompt() != null)
		{
			String result = waitfor(getPrompt());
//			System.out.println("==================result===============");
//			System.out.println(result);
//			System.out.println("==================result===============");
			
			if(result != null)
			{
				result = changeCharset(result);
				result = result.replaceAll("\r\n", "\n");
			    
			    
				String[] lines = result.split("\n");
				//System.out.println("=================lines==="+lines.length);
				StringBuilder sb = new StringBuilder();
				for(int i = 1 ; i < lines.length - 1 ; i++)
				{
					sb.append(lines[i]);
					if(i == lines.length - 2)
					{
						break;
					}
					sb.append("\n");
				}
				String res = sb.toString();
				log("cmd = " + cmd + " , result = "  + res);
				return res;
			}
		}
		
		log("cmd = " + cmd + " , result = null");
		return null;
	}
	
	
	
	private boolean isClosed()
	{
		if(socket != null)
		{
			//System.out.println("-------111----------");
			return socket.isClosed(); 
		}
		
		return true;
	}
	
	
	
	
	

	/**
	 * Read data from the socket and use telnet negotiation before returning the
	 * data read.
	 * 
	 * @param b
	 *            the input buffer to read in
	 * @return the amount of bytes read
	 */
	public int read(byte[] b) throws IOException
	{
		/* process all already read bytes */
		int n;

		do
		{
			n = handler.negotiate(b);
			if (n > 0)
				return n;
		} while (n == 0);

		while (n <= 0)
		{
			do
			{
				n = handler.negotiate(b);
				if (n > 0)
					return n;
			} while (n == 0);
			n = in.read(b);
			if (n < 0)
				return n;
			handler.inputfeed(b, n);
			n = handler.negotiate(b);
		}
		return n;
	}
	
	

	
	
	

	
	public static void main(String[] args)
	{
		
		TelnetWrapper telnet = new TelnetWrapper();
		try
		{
//			telnet.connect("172.25.25.5", 23);
//			telnet.login("itims", "itims", "login:", "assword:", "$");
			
			telnet.connect("10.10.152.213", 23);
			telnet.login("root", "123456", ":", ":", "#");
			
			String cmd = "uname";
			String result = telnet.send(cmd);
			output(cmd + "="+result);
			
			cmd = "oslevel";
            result = telnet.send(cmd);
            output("cmdbegin:version");
			output(result);
			
			
			cmd = "vmstat";
            result = telnet.send(cmd);
            output("cmdbegin:vmstat");
			output(result);
			
			
			cmd = "lsps -s";
            result = telnet.send(cmd);
            output("cmdbegin:lsps");
			output(result);
			
			
			cmd = "swap -s";
            result = telnet.send(cmd);
            output("cmdbegin:swap");
			output(result);
			
			
			cmd = "ps gv | head -n 1; ps gv | egrep -v \"RSS\" | sort +6b -7 -n -r";
            result = telnet.send(cmd);
            output("cmdbegin:process");
			output(result);
			
			
			cmd = "sar -u 1 3";
            result = telnet.send(cmd);
            output("cmdbegin:cpu");
			output(result);
			
			
			cmd = "prtconf";
            result = telnet.send(cmd);
            output("cmdbegin:allconfig");
			output(result);
			
			
			cmd = "df -m";
            result = telnet.send(cmd);
            output("cmdbegin:disk");
			output(result);
			
			
			cmd = "sar -d 1 2";
            result = telnet.send(cmd);
            output("cmdbegin:diskperf");
			output(result);
			
			
			cmd = "netstat -ian";
            result = telnet.send(cmd);
            output("cmdbegin:netperf");
			output(result);
			
			
			cmd = "entstat -d en0 |egrep 'Bytes|Hardware Address|Link Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v 'XOFF|XON';entstat -d en2 |egrep 'Bytes|Hardware Address|Link Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v 'XOFF|XON';entstat -d en3 |egrep 'Bytes|Hardware Address|Link Status|Media Speed Running|Packets|Bytes|Interrupts' |egrep -v 'XOFF|XON'";
            result = telnet.send(cmd);
            output("cmdbegin:netallperf");
			output(result);
			
			cmd = "uname -sn";
            result = telnet.send(cmd);
            output("cmdbegin:uname");
			output(result);
			
			
			cmd = "lssrc -a";
            result = telnet.send(cmd);
            output("cmdbegin:service");
			output(result);
			
			
			cmd = "cat /etc/group";
            result = telnet.send(cmd);
            output("cmdbegin:usergroup");
			output(result);
			
			
			cmd = "lsuser ALL";
            result = telnet.send(cmd);
            output("cmdbegin:user");
			output(result);
			
			
			cmd = "date";
            result = telnet.send(cmd);
            output("cmdbegin:date");
			output(result);
			
			
			cmd = "uptime";
            result = telnet.send(cmd);
            output("cmdbegin:uptime");
			output(result);
			output("cmdbegin:end");
			telnet.disconnect();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public static void output(String s)
	{
		System.out.println(s);
	}
	
	
	
	
	

}
