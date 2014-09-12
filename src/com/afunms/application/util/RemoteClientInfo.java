package com.afunms.application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class RemoteClientInfo
{
	private Socket client; 
	private BufferedReader in; 
	private PrintWriter out; 
	public RemoteClientInfo(Socket s)
	{
		try{
			client = s; 
			in = new BufferedReader(new InputStreamReader(client.getInputStream(), "GB2312")); 
			out = new PrintWriter(client.getOutputStream(), true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void executeCmd(String cmd)
	{
		out.println(cmd);
        out.flush();
	}
	public void closeConnection()
	{
		try{
			this.client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private PrintWriter getWriter(Socket socket)throws IOException{
	    OutputStream socketOut = socket.getOutputStream();
	    return new PrintWriter(socketOut,true);
	  }
	  private BufferedReader getReader(Socket socket)throws IOException{
	    InputStream socketIn = socket.getInputStream();
	    return new BufferedReader(new InputStreamReader(socketIn));
	  }
}
