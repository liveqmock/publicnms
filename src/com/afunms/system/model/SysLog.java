/**
 * <p>Description:mapping table NMS_SYS_LOG</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class SysLog extends BaseVo
{
   private int id;
   private String event;
   private String logtime;
   private String ip;
   private String user;
  
   public void setId(int id)
   {
      this.id = id;
   }

   public int getId()
   {
      return id;
   }

   public void setEvent(String event)
   {
      this.event = event;
   }

   public String getEvent()
   {
      return event;
   }

   public void setLogTime(String logtime)
   {
      this.logtime = logtime;
   }

   public String getLogTime()
   {
      return logtime;
   }

   public void setIp(String ip)
   {
      this.ip = ip;
   }

   public String getIp()
   {
      return ip;
   } 

   public void setUser(String user)
   {
      this.user = user;
   }

   public String getUser()
   {
      return user;
   }
}
