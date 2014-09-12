package com.afunms.common.util;

public class Validator
{
  public static boolean checkIPWildcard(String ipwildcard)
  {
    if ((ipwildcard == null) || (ipwildcard.trim().equals(""))) {
      return false;
    }
    StringBuffer regex = new StringBuffer();
    regex.append("^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)\\.").append("((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)\\.").append("((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)\\.").append("((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)");

    return ipwildcard.matches(regex.toString());
  }

  public static boolean checkMACWildcard(String macwildcard)
  {
    if ((macwildcard == null) || (macwildcard.trim().equals(""))) {
      return false;
    }
    StringBuffer regex = new StringBuffer("^([0-9a-fA-F]{2})(([ ][0-9a-fA-F]{2}){5})$");
    return macwildcard.matches(regex.toString());
  }

  public static boolean checkPort(int port)
  {
    return (port >= 1) && (port <= 65535);
  }
}