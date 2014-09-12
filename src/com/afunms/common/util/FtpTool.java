package com.afunms.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;

/**
 * <p>FTP上传  下载工具类</p>
 * @author HONGLI  Feb 28, 2011
 */
public class FtpTool {
	private String ip;          
	private int port;   
	private String user;   
	private String pwd;   
	private String remotePath;  //FTP服务器上的文件夹 
	private String localPath;   
	private FtpClient ftpClient;   
  
    public FtpTool() {
		super();
	}

	/**  
     * 连接ftp服务器  
     * @param ip  
     * @param port  
     * @param user  
     * @param pwd  
     * @return  
     * @throws Exception  
     */  
    public boolean connectServer(String ip, int port, String user, String pwd)   
        throws Exception {   
        boolean isSuccess = false;   
        try {   
            ftpClient = new FtpClient();   
            ftpClient.openServer(ip, port);   
            ftpClient.login(user, pwd);   
            isSuccess = true;   
        } catch (Exception ex) {   
            throw new Exception("Connect ftp server error:" + ex.getMessage());   
        }   
        return isSuccess;   
    }   
  
    /**  
     * 下载文件  
     * @param remotePath  远程FTP文件夹
     * @param localPath   本地文件夹
     * @param filename    要下载的文件名 
     * @throws Exception  
     */  
    public void downloadFile(String remotePath,String localPath, String filename) throws Exception {   
        try {   
            if (connectServer(getIp(), getPort(), getUser(), getPwd())) {   
                if (remotePath.length() != 0)   
                    ftpClient.cd(remotePath);   
                ftpClient.binary();   
                TelnetInputStream is = ftpClient.get(filename);   
                File file_out = new File(localPath + File.separator + filename);   
                FileOutputStream os = new FileOutputStream(file_out);   
                byte[] bytes = new byte[1024];   
                int c;   
                while ((c = is.read(bytes)) != -1) {   
                    os.write(bytes, 0, c);   
                }   
                is.close();   
                os.close();   
                ftpClient.closeServer();   
            }   
        } catch (Exception ex) {   
            throw new Exception("ftp download file error:" + ex.getMessage());   
        }   
    }   
  
    /**  
     * 上传文件  
     * @param remotePath  远程FTP文件夹
     * @param localPath   本地文件夹
     * @param filename    要上传的文件名
     * @throws Exception  
     */  
    public void uploadFile(String remotePath,String localPath, String filename) throws Exception {   
        try {   
            if (connectServer(getIp(), getPort(), getUser(), getPwd())) {   
                if (remotePath.length() != 0)   
                    ftpClient.cd(remotePath);   
                ftpClient.binary();   
                TelnetOutputStream os = ftpClient.put(filename);   
                File file_in = new File(localPath + File.separator + filename);   
                FileInputStream is = new FileInputStream(file_in);   
                byte[] bytes = new byte[1024];   
                int c;   
                while ((c = is.read(bytes)) != -1) {   
                    os.write(bytes, 0, c);   
                }   
                is.close();   
                os.close();   
                ftpClient.closeServer();   
            }   
        } catch (Exception ex) {   
            throw new Exception("ftp upload file error:" + ex.getMessage());   
        }   
    }   
  
    /**  
     * @return  
     */  
    public String getIp() {   
        return ip;   
    }   
  
    /**  
     * @return  
     */  
    public int getPort() {   
        return port;   
    }   
  
    /**  
     * @return  
     */  
    public String getPwd() {   
        return pwd;   
    }   
  
    /**  
     * @return  
     */  
    public String getUser() {   
        return user;   
    }   
  
    /**  
     * @param string  
     */  
    public void setIp(String string) {   
        ip = string;   
    }   
  
    /**  
     * @param i  
     */  
    public void setPort(int i) {   
        port = i;   
    }   
  
    /**  
     * @param string  
     */  
    public void setPwd(String string) {   
        pwd = string;   
    }   
  
    /**  
     * @param string  
     */  
    public void setUser(String string) {   
        user = string;   
    }   
  
    /**  
     * @return  
     */  
    public FtpClient getFtpClient() {   
        return ftpClient;   
    }   
  
    /**  
     * @param client  
     */  
    public void setFtpClient(FtpClient client) {   
        ftpClient = client;   
    }   
  
    /**  
     * @return  
     */  
    public String getRemotePath() {   
        return remotePath;   
    }   
  
    /**  
     * @param string  
     */  
    public void setRemotePath(String string) {   
        remotePath = string;   
    }   
  
    /**  
     * @return  
     */  
    public String getLocalPath() {   
        return localPath;   
    }   
  
    /**  
     * @param string  
     */  
    public void setLocalPath(String string) {   
        localPath = string;   
    }   
    
    public static void main(String[] args) {
    	FtpTool ftpTool = new FtpTool();
    	 ftpTool = new FtpTool();   
         ftpTool.setIp("127.0.0.1");   
         ftpTool.setPort(21);   
         ftpTool.setUser("hongli");   
         ftpTool.setPwd("hongli");   
         ftpTool.setRemotePath("/");   
    	 try {
			ftpTool.uploadFile(ftpTool.getRemotePath(),"D:/Tomcat5.0/webapps/afunms/ftpupload/","aix服务器_cpu.xml");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
