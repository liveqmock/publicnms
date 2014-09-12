package com.afunms.application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class CreateServerThread extends Thread
{
	private Socket client; 
	private BufferedReader in; 
	private PrintWriter out; 
	public CreateServerThread(Socket s)
	{
		try{
			client = s; 
			in = new BufferedReader(new InputStreamReader(client.getInputStream(), "GB2312")); 
			out = new PrintWriter(client.getOutputStream(), true);
			start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void run() 
	{
		try{
			String cmd;
			BufferedReader localReader=new BufferedReader(new InputStreamReader(System.in));
			while((cmd=localReader.readLine())!=null)
			{
		        out.println(cmd);
		        out.flush();
		        if(cmd.equals("shutdown"))
		          break;
		     }
			System.out.println("关闭了客户端");
			this.client.close();
		}catch (IOException e){} 
	}
	private String createMessage(String line)
	{
		return "";
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