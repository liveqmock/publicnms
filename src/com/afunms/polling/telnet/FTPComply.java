package com.afunms.polling.telnet;


/**
 * 该程序用来测试FTP服务器的运行情况
 * connectServer();测试服务器的连接情况
 * loadFile();测试服务器下载文件情况
 * uploadFile();测试服务器上传文件情况
 */
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import org.apache.log4j.Logger;

public class FTPComply {
    
    private  Logger logger= Logger.getLogger(FTPComply.class);
    public  String conmes=""; //连接FTP服务器异常信息
    public  String uploadmes=""; //FTP上传任务执行结果异常信息
    public  String downloadmes="";	//FTP下载任务执行结果异常信息
    private boolean binaryTransfer = false;
    /**
     * 
     * 连接到服务器
     * @return true 连接服务器成功，false 连接服务器失败
     */
    public boolean connectServer(String ipaddress,int port,String userName,String password,int timeOut) 
    {
    	FTPClient ftpClient = null;
    	boolean flag = true; 
        if (ftpClient == null) { 
        	int reply; 
        	try { 
        		ftpClient = new FTPClient(); 
        		ftpClient.setControlEncoding("UTF-8"); 
        		ftpClient.setDefaultPort(port); 
        		ftpClient.connect(ipaddress); 

        		reply = ftpClient.getReplyCode(); 
        		
        		ftpClient.setDataTimeout(timeOut); 
        		if (!FTPReply.isPositiveCompletion(reply)) { 
        			ftpClient.disconnect(); 
        			//System.err.println("FTP server refused connection."); 
        			flag = false; 
                }
        	} catch (SocketException e) { 
        		flag = false; 
        		e.printStackTrace(); 
        		logger.error("登录ftp服务器 " + ipaddress + " 失败,连接超时！"); 
        	} catch (IOException e) { 
        		flag = false; 
        		e.printStackTrace(); 
        		logger.error("登录ftp服务器 " + ipaddress + " 失败，FTP服务器无法打开！"); 
        	} 
        } 
        return flag; 
    } 
    
    
    /** 
     * 下载文件 
     * 
     * @param remoteFileName --服务器上的文件名 
     * @param localFileName--本地文件名 
     * @return true 下载成功，false 下载失败 
     */
    public boolean loadFile(String remoteFileName,String localFileName,String ipaddress,int port,String userName,String password,int timeOut) 
    { 
    	FTPClient ftpClient = new FTPClient();
    	boolean flag = true; 
        // 下载文件 
        BufferedOutputStream buffOut = null; 
        try { 
        	ftpClient.setDefaultPort(port); 
        	ftpClient.connect(ipaddress); 
            ftpClient.login(userName, password); 

        	ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 

        	buffOut = new BufferedOutputStream(new FileOutputStream(localFileName)); 
        	flag = ftpClient.retrieveFile(remoteFileName, buffOut); 
        	if (flag == true) { 
        		logger.info("下载文件成功！"); 
        	} else { 
        		logger.info("下载文件失败！"); 
        	}
        	buffOut.close();
        } catch (Exception e) { 
                e.printStackTrace(); 
                flag=false;
                logger.debug("本地文件下载失败！", e); 
        } finally { 
        	try { 
        		if (buffOut != null) 
        			buffOut.close(); 
        		ftpClient.disconnect();
        	} catch (Exception e) { 
        		e.printStackTrace(); 
        	} 
        } 
        
        return flag; 
    } 
    /**  
     * 上传一个本地文件到远程指定文件  
     *   
     * @param remoteAbsoluteFile  
     *            远程文件名(包括完整路径)  
     * @param localAbsoluteFile  
     *            本地文件名(包括完整路径)  
     * @return 成功时，返回true，失败返回false  
     */  
    public boolean uploadFile(String remoteAbsoluteFile,String localAbsoluteFile,String ipaddress,int port,String userName,String password,int timeOut) 
    {
    	FTPClient ftpClient = new FTPClient(); 
    	boolean flag = true; 
    	InputStream input = null;   
    	try {
        	ftpClient.setDefaultPort(port); 
        	ftpClient.connect(ipaddress); 
            ftpClient.login(userName, password); 

            
            //设置文件传输类型   
            if (binaryTransfer) {   
            	ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);   
            } else {   
            	ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);   
            }   
            // 处理传输 
            input = new FileInputStream(localAbsoluteFile);   
            flag=ftpClient.storeFile(remoteAbsoluteFile, input);
            if (flag == true) { 
        		logger.info("上传文件成功！"); 
        	} else { 
        		logger.info("上传文件失败！");
        	} 
            input.close();   
            logger.debug("delete " + localAbsoluteFile);   
        } catch (Exception e) { 
            e.printStackTrace(); 
            flag=false;
            logger.debug("本地文件下载失败！", e); 
        } finally { 
        	try { 
        		if (input != null) 
        			input.close(); 
        		ftpClient.disconnect();
        	} catch (Exception e) { 
        		e.printStackTrace(); 
        	} 
        } 
        
        return flag;   
    }   

    public static void main(String[] args)
    {
    	FTPComply ft = new FTPComply();
    	long last = System.currentTimeMillis();
    	ft.connectServer("10.10.152.254",21,"admin","admin",12000);
    	String connect_time = (System.currentTimeMillis()-last)+"";   //连接服务器响应时间
    	System.out.println("连接时间="+connect_time);
    	long download_last = System.currentTimeMillis();
    	//ft.loadFile("/demo.txt","D:\\demo2.txt","192.168.1.103",21,"nms","webnms",12000);
    	//String download_time = (System.currentTimeMillis()-download_last)+""; //下载时间
    	//System.out.println("下载时间="+download_time);
    	long upload_last=System.currentTimeMillis();
    	ft.uploadFile("/cfg1.cfg", "C:\\Documents and Settings\\GZM\\cfg1.cfg","10.10.152.254",21,"admin","admin",12000);
        //ft.uploadFile(new File("D:\\logs.tar"), new File("D:\\BC\\logs.tar"), "logs.tar","192.168.1.103",21,"nms","webnms",12000);
        String upload_time = (System.currentTimeMillis()-upload_last)+"";   //上传时间
        System.out.println("上传时间="+upload_time);
    	
    }
}
