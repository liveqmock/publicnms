/**
 * <p>Description: nms_symantec_log记录</p>
 * <p>Company: 北京东华合创数码科技股份有限公司</p>
 * @author 王福民
 * @project 阿福网管
 * @date 2005-3-16
 */

package com.afunms.security.model;

public class SymantecLog
{
   private int id;
   private String ip;
   private String logFile;
   private int logRow;
   private String lasttime;
   private String info;

   public void setId(int newId)
   {
      id = newId;
   }

   public int getId()
   {
      return id;
   }

   public void setInfo(String newInfo)
   {
      info = newInfo;
   }

   public String getInfo()
   {
      return info;
   }

   public void setIp(String newIp)
   {
      ip = newIp;
   }

   public String getIp()
   {
      return ip;
   }

   public void setLogFile(String newLogFile)
   {
      logFile = newLogFile;
   }

   public String getLogFile()
   {
      return logFile;
   }

   public void setLogRow(int newLogRow)
   {
      logRow = newLogRow;
   }

   public int getLogRow()
   {
      return logRow;
   }

   public void setLasttime(String newLasttime)
   {
      lasttime = newLasttime;
   }

   public String getLasttime()
   {
      return lasttime;
   }
}
