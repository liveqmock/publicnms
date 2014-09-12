package com.afunms.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{
  static final String[] HEX_SPLIT_STRING = { ":", " " };

  static final String[] CHANGE2CHINESE_OID = { "1.3.6.1.2.1.25.6.3.1.2", "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.1", "1.3.6.1.2.1.1.4", "1.3.6.1.2.1.25.2.3.1.3" };

  public static boolean isHex(String value)
  {
    if ((value == null) || (value.length() == 0)) {
      return false;
    }

    for (String splitStr : HEX_SPLIT_STRING) {
      if (value.indexOf(splitStr) >= 0) {
        value = value + splitStr;
        String rex = "([0-9a-fA-F][0-9a-fA-F][" + splitStr + "])+";
        Pattern p = Pattern.compile(rex);
        if (p.matcher(value).matches()) {
          return true;
        }
      }
    }
    return false;
  }

  public static String hexToChinese(String value, String oid)
  {
    if (value == null) {
      return null;
    }

    if (isChineseOid(oid)) {
      return hexToChinese(value);
    }
    if (isHex(value)) {
      value = value.replaceAll(":", " ");
    }
    return value;
  }

  public static String hexToChinese(String value)
  {
    if (value == null) {
      return null;
    }

    String hexString = value.trim();
    if (!isHex(hexString)) {
      return hexString;
    }

    String[] splitStrs = HEX_SPLIT_STRING;
    String[] splitResult = null;
    for (String splitStr : splitStrs) {
      if (hexString.indexOf(splitStr) > 0) {
        splitResult = hexString.split(splitStr);
        break;
      }
    }

    String result = null;
    if (splitResult != null) {
      byte[] bs = new byte[splitResult.length];
      int i = 0;
      for (String st : splitResult) {
        bs[(i++)] = Integer.valueOf(st, 16).byteValue();
      }
      result = new String(bs);
    }
    return result;
  }

  private static boolean isChineseOid(String oid)
  {
    if (oid == null) {
      return false;
    }

    for (String tmpOid : CHANGE2CHINESE_OID) {
      if (oid.startsWith(tmpOid)) {
        return true;
      }
    }
    return false;
  }

  public static String transeSnmp4jTime(String timeStr)
    throws Exception
  {
    if (timeStr == null) {
      return "";
    }

    String[] tmp = timeStr.split(":");
    StringBuffer sb = new StringBuffer();
    sb.append(tmp[0]).append(" hours, ");
    sb.append(tmp[1]).append(" minutes, ");
    sb.append(tmp[2].substring(0, tmp[2].indexOf("."))).append(" seconds.");
    return sb.toString();
  }

  public static String parseAsciiTo10(String value)
  {
    String[] nn = null;
    if (value.indexOf(".") >= 0) {
      nn = value.split("\\.");
    }

    if (nn != null) {
      byte[] bs = new byte[nn.length];
      for (int i = 0; i < nn.length; ++i) {
        bs[i] = Byte.parseByte(nn[i]);
      }
      String[] tmp = new String(bs).split(" ");
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < tmp.length; ++i) {
        sb.append(Integer.parseInt(tmp[i], 16));
        if (i < tmp.length - 1) {
          sb.append(".");
        }
      }
      return sb.toString();
    }
    return null;
  }

  public static String parse16To10(String value)
  {
    if (value == null) {
      return null;
    }
    String[] tmp = null;
    if (value.indexOf(" ") >= 0) {
      tmp = value.split(" ");
    } else {
      tmp = new String[1];
      tmp[0] = value;
    }

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < tmp.length; ++i) {
      sb.append(Integer.parseInt(tmp[i], 16));
      if (i < tmp.length - 1) {
        sb.append(".");
      }
    }
    return sb.toString();
  }
}