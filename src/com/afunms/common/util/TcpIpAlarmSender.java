/**
 * <p>Description:snmp tool</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.common.util;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TcpIpAlarmSender
{
	
  public void sendAlarm(String message,String mobile)
    throws Exception
  {
    ConvertSocket.OrganizationMessage(converSendMsg(message,mobile));
  }

  private static String converSendMsg(String message,String mobile)
  {
    StringBuffer str = new StringBuffer();
    String phoneNum = mobile;
    StringBuffer strMsg = new StringBuffer();
    strMsg.append("9000").append("|").append("0").append("|")
      .append("").append("|").append(phoneNum).append("|")
      .append(SysUtil.checkTel(phoneNum)).append("|")
      .append(SysUtil.getStrByLength(message, 256)).append("|")
      .append(SysUtil.getDay()).append("|")
      .append(SysUtil.getSecond()).append("|")
      .append(SysUtil.makeString(20)).append("|");
    int length = strMsg.length();
    String s = null;
    if (length < 1000) {
      s = "0" + String.valueOf(length);
    }
    else if (length < 100) {
      s = "00" + String.valueOf(length);
    }
    else
      s = String.valueOf(length);

    System.out.println("发送报文长度为：" + s);
    str.append(s).append(strMsg.toString());
    return str.toString();
  }
}