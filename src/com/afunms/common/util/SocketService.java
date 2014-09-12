package com.afunms.common.util;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import com.afunms.discovery.DiscoverResource;
import com.afunms.sysset.model.Service;


public class SocketService {
	/**
	*构造函数,初始化一个对象
	*/
	public SocketService(){}
    
    /**
     * 测试运行的服务 
     */
     public static boolean checkService(String ipAddress,int port,int timeout) 
     {	   
         boolean result = false;
            
         Socket socket = new Socket();          
  	     try{
                InetAddress addr = InetAddress.getByName(ipAddress);
                SocketAddress sockaddr = new InetSocketAddress(addr,port);                                  
                socket.connect(sockaddr, timeout);
      	      	result = true; 
  	      }catch(SocketTimeoutException ste){	 		  
  		  }catch(IOException ioe){	 			      
  		  }finally{
     	    	  try
     	    	  {
     	    	     socket.close();
     	    	  }
     	    	  catch(IOException ioe){}
     	  }	       	       
     	  return result;
     }

}