package com.afunms.polling.task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class FtpUtil {

    private FTPClient ftpClient = null;

    private int port = 21;

    private String remoteRootPath = "/";

    private String server = null;

    private String user = null;

    private String password = null;

    public FtpUtil(String server, String user, String password) {
        this.server = server;
        this.user = user;
        this.password = password;
    }

    public void init() {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(this.server, this.port);
            ftpClient.setDataTimeout(60000);
            ftpClient.setConnectTimeout(60000);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 测试下载
    public boolean download(String localAbsolutePath, String fileName) {
        this.init();
        OutputStream os = null;
        int replyCode = ftpClient.getReplyCode();// 准备就绪,可以连接
        try {
            if (replyCode == 220) {
                ftpClient.login(this.user, this.password);
                replyCode = ftpClient.getReplyCode();
                if (replyCode == 230) {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.changeWorkingDirectory(this.remoteRootPath);
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    os = new FileOutputStream(localAbsolutePath + "/" + fileName);
                    return ftpClient.retrieveFile(fileName, os);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.disConnect();
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 测试上传
    public boolean upload(String localAbsolutePath, String fileName) {
        this.init();
        InputStream is = null;
        int replyCode = ftpClient.getReplyCode();// 准备就绪,可以连接
        try {
            if (replyCode == 220) {
                ftpClient.login(this.user, this.password);
                replyCode = ftpClient.getReplyCode();
                if (replyCode == 230) {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.changeWorkingDirectory(this.remoteRootPath);
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    is = new FileInputStream(localAbsolutePath + "/" + fileName);
                    return  ftpClient.storeFile(fileName, is);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            this.disConnect();
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void disConnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
