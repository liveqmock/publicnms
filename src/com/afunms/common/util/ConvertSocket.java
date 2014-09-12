/**
 * <p>Description:snmp tool</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class ConvertSocket
{
  private static final String GB_2312 = "gb2312";
  private static Properties p = new Properties();
  private static String strPropFileName = "";
  private static Socket connection;

  public static String OrganizationMessage(String str)
  {
    connect();
    try
    {
      sendMsg2Server(str);
      System.out.println("发送报文成功：" + str);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    closeConnection();
    return null;
  }

  public static Socket connect()
  {
    try
    {
    	 //java.util.Properties p = new java.util.Properties();
         String filePath=CommonAppUtil.getAppName()+"/task/OracleProjectProertipes.txt";
         FileInputStream fin = new FileInputStream(new File(filePath));
         p.load(fin);
         //System.out.println(filePath);
         //String webpath = p.getProperty("DJFilePath");
         
      //strPropFileName = Util.getZZAlarmConfigFileName();

      //p.load(new FileInputStream(new File(strPropFileName)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String ip = p.getProperty("ip");
    String port = p.getProperty("port");
    System.out.println(ip+"==="+port);
    try
    {
      connection = new Socket(ip, Integer.valueOf(port).intValue());
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return connection;
  }

  public static void closeConnection()
  {
    try
    {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void sendMsg2Server(String sendMsg)
    throws IOException
  {
    try
    {
      write(sendMsg);
    } catch (IOException e) {
      throw e;
    }
  }

  private static void write(String str)
    throws IOException
  {
	  OutputStream output = connection.getOutputStream();
	  output.write(str.getBytes());
	  connection.getOutputStream().flush();
	  connection.getOutputStream().close();
  }

  static String read()
    throws IOException
  {
    String str = "111";
    if (connection.isClosed())
      connection = connect();
    InputStream is = connection.getInputStream();
    InputStreamReader isr = new InputStreamReader(is, "gb2312");
    isr.read();

    System.out.println(str);
    return str;
  }
}