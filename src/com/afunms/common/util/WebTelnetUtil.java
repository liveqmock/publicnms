package com.afunms.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class WebTelnetUtil {

    private static WebTelnetUtil instance; // 唯一实例

    public Hashtable<Integer, TelnetClient> sessionHashtable;

    public Hashtable<Integer, StringBuffer> serverOutputInfo;

    private Hashtable<String, String> terminalTypeHashtable;

    private String remoteIp = "127.0.0.1";;

    private int remotePort = 23;

    private String terminalType = "VT220";

    public WebTelnetUtil() {
        sessionHashtable = new Hashtable<Integer, TelnetClient>();
        serverOutputInfo = new Hashtable<Integer, StringBuffer>();

        terminalTypeHashtable = new Hashtable<String, String>();
        terminalTypeHashtable.put("WINDOWS", "VT220");
        terminalTypeHashtable.put("UNIX", "VT100");
        terminalTypeHashtable.put("LINUX", "VT100");
    }

    static synchronized public WebTelnetUtil getInstance() {
        if (instance == null) {
            System.out.println("初始化实例...");
            instance = new WebTelnetUtil();
        }
        return instance;
    }

    public int openSession(String remoteIp, int remotePort, String terminalTypeName) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.terminalType = terminalTypeHashtable.get(terminalTypeName);
        return init();
    }

    public int init() {

        final TelnetClient tc = new TelnetClient();

        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler(terminalType, false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

        try {

            // 注册处理参数
            tc.addOptionHandler(ttopt);
            tc.addOptionHandler(echoopt);
            tc.addOptionHandler(gaopt);

            // 连接服务端
            tc.connect(remoteIp, remotePort);
            tc.setConnectTimeout(3 * 60 * 1000);

            Thread reader = new Thread(new readUtil(tc));
            reader.start();

            sessionHashtable.put(tc.hashCode(), tc);

            return tc.hashCode();

        } catch (InvalidTelnetOptionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void sender(TelnetClient tc, String command) {
        OutputStream os = tc.getOutputStream();// 向服务端发送命令
        PrintStream ps = new PrintStream(os);
        ps.println(command);
        ps.flush();
    }

    class readUtil implements Runnable {

        private TelnetClient tc;

        public readUtil(TelnetClient tc) {
            this.tc = tc;
        }

        public void run() {

            InputStream is = tc.getInputStream();// 读取返回的信息
            try {
                byte[] buff = new byte[1024];
                int readCount = 0;
                do {
                    readCount = is.read(buff);
                    if (readCount > 0) {
                        int b = 0;
                        for (int i = 0; i < buff.length; i++) {
                            b = buff[i];
                            if (b == 1 || b == 3 || b == 8 || b == 24 || b == 27 || b == 50 || b == 52 || b == 68 || b == 91) {
                                buff[i] = 32;
                            }
                        }
                        if (serverOutputInfo.get(tc.hashCode()) != null) {
                            serverOutputInfo.put(tc.hashCode(), serverOutputInfo.get(tc.hashCode()).append(new String(buff, 0, readCount)).append("\n"));
                        } else {
                            serverOutputInfo.put(tc.hashCode(), new StringBuffer(new String(buff, 0, readCount)));
                        }
                    }
                } while (readCount >= 0);
            } catch (IOException e) {
                System.err.println("Exception while reading socket:" + e.getMessage());
                return;
            }

        }
    }

}
