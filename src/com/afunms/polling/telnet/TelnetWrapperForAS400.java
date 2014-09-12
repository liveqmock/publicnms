package com.afunms.polling.telnet;

import java.io.IOException;

import com.afunms.common.util.SysLogger;
public class TelnetWrapperForAS400 extends TelnetWrapper
{
	/**
	 * Login into remote host. This is a convenience method and only works if
	 * the prompts are "login:" and "Password:".
	 * 
	 * @param user
	 *            the user name
	 * @param pwd
	 *            the password
	 */
	public void login(String user, String pwd, String loginPrompt,
			String passwordPrompt , String shellPrompt) throws IOException
	{
		this.username = user;
		this.password = pwd;
		this.loginPrompt = loginPrompt;
		this.passwordPrompt = passwordPrompt;
		
		System.out.println("loginPrompt===="+loginPrompt);
		waitfor(loginPrompt); // throw output away
		
//		setSystemEnterLine("\t");
		System.out.println(user + "\t" + pwd);
		send(user + "\t" + pwd);
		
		this.setPrompt(shellPrompt);

	}
	
	public String writes(byte[] arr) throws IOException
	{
//		byte arr[];
//		System.out.println(cmdStr);
//		arr = (cmdStr + "\n").getBytes("iso-8859-1");
		
		handler.transpose(arr);
		if (getPrompt() != null)
		{
			String result = waitfor(getPrompt());
			if(result != null)
			{
//				result = changeCharset(result);
//				result = result.replaceAll("\r\n", "\n");
//				String[] lines = result.split("\n");
//				StringBuilder sb = new StringBuilder();
//				for(int i = 1 ; i < lines.length - 1 ; i++)
//				{
//					sb.append(lines[i]);
//					if(i == lines.length - 2)
//					{
//						break;
//					}
//					sb.append("\n");
//				}
//				String res = sb.toString();
//				log("cmd = " + cmd + " , result = "  + res);
				return result;
			}
		}
		   
//		log("cmd = " + cmdStr + " , result = null");
		return null;
	}
	
	public String write(String cmdStr) throws IOException
	{
		byte arr[];
		System.out.println(cmdStr);
		arr = (cmdStr + "\n").getBytes("iso-8859-1");
		
		handler.transpose(arr);
		if (getPrompt() != null)
		{
			String result = waitfor(getPrompt());
			if(result != null)
			{
//				result = changeCharset(result);
//				result = result.replaceAll("\r\n", "\n");
//				String[] lines = result.split("\n");
//				StringBuilder sb = new StringBuilder();
//				for(int i = 1 ; i < lines.length - 1 ; i++)
//				{
//					sb.append(lines[i]);
//					if(i == lines.length - 2)
//					{
//						break;
//					}
//					sb.append("\n");
//				}
//				String res = sb.toString();
//				log("cmd = " + cmd + " , result = "  + res);
				return result;
			}
		}
		
		log("cmd = " + cmdStr + " , result = null");
		return null;
	}
	
	
	public String write(char cmdStr) throws IOException
	{
		byte arr[];
		System.out.println(cmdStr);
//		arr = (cmdStr + "\n").getBytes("iso-8859-1");
		arr = String.valueOf(cmdStr).getBytes("iso-8859-1");
		handler.transpose(arr);
		if (getPrompt() != null)
		{
			String result = waitfor(getPrompt());
			if(result != null)
			{
//				result = changeCharset(result);
//				result = result.replaceAll("\r\n", "\n");
//				String[] lines = result.split("\n");
//				StringBuilder sb = new StringBuilder();
//				for(int i = 1 ; i < lines.length - 1 ; i++)
//				{
//					sb.append(lines[i]);
//					if(i == lines.length - 2)
//					{
//						break;
//					}
//					sb.append("\n");
//				}
//				String res = sb.toString();
//				log("cmd = " + cmd + " , result = "  + res);
				return result;
			}
		}
		
		log("cmd = " + cmdStr + " , result = null");
		return null;
	}
	
    public byte[] intToByteArray(int value){
        byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
		    int offset = (b.length - 1 - i) * 8;
		    b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
    } 
    public String write(int cmdStr,String shellPrompt) throws IOException
	{
		byte arr1[];
		arr1 = intToByteArray(cmdStr);
		int len = arr1.length;
		byte arr[] = new byte[len];
		for(int i=0;i<arr1.length;i++){
			arr[i]=arr1[i];
		}  
		handler.transpose(arr);  
		
		String result = waitfor(shellPrompt);
		if(result != null)
		{
			return result;
		}
		
		log("cmd = " + cmdStr + " , result = null");
		return null;
	}
	public String write(int cmdStr1,int cmdStr2,String shellPrompt) throws IOException
	{
		byte arr1[];
		byte arr2[];
		byte arr3[];
//		SysLogger.info(cmdStr1+","+cmdStr2);
		arr1 = intToByteArray(cmdStr1);
		arr2 = intToByteArray(cmdStr2);
		arr3 = ("\n").getBytes("iso-8859-1");
		int len = arr1.length+arr2.length+arr3.length;
		byte arr[] = new byte[len];
		for(int i=0;i<arr1.length;i++){
			arr[i]=arr1[i];
		}  
		for(int i=0;i<arr2.length;i++){
			arr[arr1.length+i]=arr2[i];
		}
		for(int i=0;i<arr3.length;i++){
			arr[arr1.length+arr2.length+i]=arr3[i];
		}
		handler.transpose(arr);  
		
		String result = waitfor("Password");
		if(result != null)
		{
			return result;
		}
		
		log("cmd = " + cmdStr1 + "," + cmdStr2 + " , result = null");
		return null;
	}
	public String write(String str1,int cmdStr1,int cmdStr2,String str2,String shellPrompt) throws IOException
	{
		byte arr1[];
		byte arr2[];
		byte arr3[];
		byte arr4[];
		byte arr5[];
		SysLogger.info(str1 + "," + cmdStr1 + "," + cmdStr2 + "," + str2);
		arr1 = str1.getBytes("iso-8859-1");
		arr2 = intToByteArray(cmdStr1);
		arr3 = intToByteArray(cmdStr2);
		arr4 = str2.getBytes("iso-8859-1");
		arr5 = ("\n").getBytes("iso-8859-1");
		int len = arr1.length+arr2.length+arr3.length+arr4.length+arr5.length;
		byte arr[] = new byte[len];
		for(int i=0;i<arr1.length;i++){
			arr[i]=arr1[i];
		}  
		for(int i=0;i<arr2.length;i++){
			arr[arr1.length+i]=arr2[i];
		}
		for(int i=0;i<arr3.length;i++){
			arr[arr1.length+arr2.length+i]=arr3[i];
		}
		for(int i=0;i<arr4.length;i++){
			arr[arr1.length+arr2.length+arr3.length+i]=arr4[i];
		}
		for(int i=0;i<arr5.length;i++){
			arr[arr1.length+arr2.length+arr3.length+arr4.length+i]=arr5[i];
		}
		handler.transpose(arr);  
		
		String result = waitfor(shellPrompt);
		if(result != null)
		{
			return result;
		}
		
		log("cmd = " + str1 + "," + cmdStr1 + "," + cmdStr2 + "," + str2 + " , result = null");
		return null;
	}
	
}
