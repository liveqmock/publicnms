package com.afunms.common.util;

/*
 * 此类提供测试telnet是否连接成功
 * 
 * 若能成功连接 返回 0 , 否则返回 -1
 * 
 * 此类包含三个方法： 
 * 		connect(String ip)   
 *    		根据主机 ip 和 默认的端口 23 以及 默认的超时时间 3000 
 *    		测试是否能连接成功
 *    
 *    	connect(String ip , int timeout)   
 *    		根据主机 ip 和 默认的端口 23 以及 超时时间 timeout 
 *    		测试是否能连接成功
 * 		
 * 		connect(String ip , int port , int timeout)   
 *    		根据主机 ip 和 端口 port 以及 超时时间 timeout 
 *    		测试是否能连接成功
 */

import java.io.IOException;
import java.util.ArrayList;

import cn.org.xone.telnet.TelnetWrapper;

/**
 * 
 * This class is used to test whether it can successfully telnet to connect
 * 
 * @author nielin
 * @create on 2010-02-26
 *
 */
public class TelnetUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TelnetUtil telnetTest = new TelnetUtil();
		
		System.out.println(telnetTest.connect("10.10.1.1"));
	}
	
	/**
	 * 
	 * According to the host ip, the default port 23, the default timeout 3000 
	 * Test whether a successful connection
	 * 
	 * @param ip
	 *			the host ip
	 * @return
	 * 			If successful to return 0, failure to return -1
	 */
	public int connect(String ip){
		return connect(ip , 23 , 3000);
	}
	
	/**
	 * 
	 * According to the host ip, the default port 23, the connect timeout 
	 * Test whether a successful connection
	 * 
	 * @param ip
	 * 			the host ip
	 * @param timeout
	 * 			the connect timeout 
	 * @return
	 * 			If successful to return 0, failure to return -1
	 */
	public int connect(String ip , int timeout){
		return connect(ip , 23 , timeout);
	}
	
	/**
	 * 
	 * According to the host ip, the host port, the connect timeout 
	 * Test whether a successful connection
	 * 
	 * @param ip
	 * 			the host ip
	 * @param port
	 * 			the host port
	 * @param timeout
	 * 			the connect timeout 
	 * @return
	 * 			If successful to return 0, failure to return -1
	 */
	public int connect(String ip , int port , int timeout){
		TelnetWrapper telnet = new TelnetWrapper();
		try{
			telnet.connect(ip, port , timeout);
		}catch(Exception e){
			return -1;
		}finally{
			try {
				telnet.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

}
