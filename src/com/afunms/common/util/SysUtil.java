/**
 * <p>Description:utility class,includes some methods which are often used</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.text.ParseException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jdom.Element;

import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

public class SysUtil
{
   private SysUtil()
   {	   
   }

   /**
    * 转为中文
    */
   public static String getChinese(String ss)
   {
      String strpa = "";
      try
      {
         if (ss != null)
            strpa = new String(ss.getBytes("ISO-8859-1"), "GB2312");
         else
            strpa = ss;
      }
      catch (Exception e)
      {
         strpa = "";
      }
      return strpa;
   }

   /**
    * 得到当前日期
    */
   public static String getCurrentDate()
   {
       SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd");
       String currentDate = timeFormatter.format(new java.util.Date());
       return currentDate;
   }

   /**
    * 得到当前时间
    */
   public static String getCurrentTime()
   {
       SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String currentTime = timeFormatter.format(new java.util.Date());
       return currentTime;
   }

  /**
   * 得到当前年份
   */
   public static int getCurrentYear()
   {
       Calendar cal = new GregorianCalendar();
       return cal.get(Calendar.YEAR);
   }

  /**
   * 得到当前月份
   */
   public static int getCurrentMonth()
   {
       Calendar cal = new GregorianCalendar();
       return cal.get(Calendar.MONTH) + 1;
   }

   /**
    * 把null替换成""
    */
   public static String ifNull(String str)
   {
       if (str == null||str.equals("null"))
          return "";
       else
          return str;
   }

   /**
    * 把null替换成别的字符
    */
   public static String ifNull(String str, String replaceStr)
   {
       if (str == null || "".equals(str))
          return replaceStr;
       else
          return str;
   }

  /**
   * 把一个长整数转换成时间
   */
   public static String longToTime(long timeLong)
   {
       Date date = new Date(timeLong);
       SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       return timeFormatter.format(date);
   }

  /**
   * 把一个形如"yyyy-mm-dd hh24:mi:ss"时间型转换成一个长整数
   */
   public static long timeToLong(String time)
   {
      long timeLong = 0;
      try
      {
         SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         Date date = dateFormat.parse(time);
         timeLong = date.getTime() / 1000;
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }
      return timeLong;
   }

   /**
    * 计算两个时间点间的时间差
    */
   public static String diffTwoTime(String time1,String time2)
   {
      if(time1==null||time2==null)
         return "";

      StringBuffer timeStr = new StringBuffer(10);
      long diffTime = timeToLong(time2) - timeToLong(time1);
      long hh24 = diffTime/3600;
      long surplus = diffTime%3600;

      long mi = surplus/60;
      long ss = surplus%60;

      if(hh24<10) timeStr.append("0");
      timeStr.append(hh24);

      timeStr.append(":");
      if(mi<10) timeStr.append("0");
      timeStr.append(mi);

      timeStr.append(":");
      if(ss<10) timeStr.append("0");
      timeStr.append(ss);

      return timeStr.toString();
   }   
   
   /**
    * 得到现在的时间,用长形型表示
    */
   public static long getCurrentLongTime()
   {
	   return (long)(new java.util.Date()).getTime()/1000;	   
   }
      
   /**
    * 得到当前小时
    */
   public static int getCurrentHour()
   {
      Calendar cal = Calendar.getInstance();
      int curHour = cal.get(Calendar.HOUR_OF_DAY);
      return curHour;
   }
   
   public static String formatString(String s)
   {
       if(s == null || s.equals(""))
           return s;
       
       StringBuffer stringbuffer = new StringBuffer();
       for(int i = 0; i <= s.length() - 1; i++)
          if(s.charAt(i) == '\r')
             stringbuffer = stringbuffer.append("<br>");
          else
             stringbuffer = stringbuffer.append(s.substring(i, i + 1));

      return stringbuffer.toString();
   } 
   
   /**
    * 一个月有几天
    */
   public static int getDaysOfMonth(int year,int month)
   {
      if(year<1000||year>3000||month<1||month>12)
        return 0;

      int days = 0;
      if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
         days = 31;
      else if(month==4||month==6||month==9||month==11)
         days = 30;
      else //month==2
      {
         if(year%400==0||year%4==0&&year%100!=0)
           days = 29;
        else
           days = 28;
      }
      return days;
   }
   
   /**
    * 得到某天的日期
    * interval 距今天的天数
    */
   public static String getDate(int interval)
   {
	   Calendar cal = Calendar.getInstance();
  	   cal.add(Calendar.DATE,interval);
   	   String date = new java.sql.Date(cal.getTimeInMillis()).toString();
   	   return date;
   }
   
   public static double formatDouble(double a, double b)
   {
	   if(b==0) return 0;
	   
	   DecimalFormat df = new DecimalFormat("#.00");
	   return Double.parseDouble(df.format(a/b));
   }
   
   public static String getLongID()
   {
	   return String.valueOf((long)((new java.util.Date()).getTime() * Math.random()));
   }
   
   /**
    * 把一个形如"yyyy-MM-dd"日期型转换成一个长整数
    */
   public static long dateToLong(String date)
   {
      long timeLong = 0;
      try
      {
          SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
          Date dateTime = dateFormat.parse(date);
          timeLong = dateTime.getTime() / 1000;
      }
      catch (ParseException e)
      {
          timeLong = 0;
      }
      return timeLong;
   }  
   
   /**
    * 计算两个日期之间的天数 
    */
   public static int getDaysBetweenTwoDates(long beginDate,long endDate)
   {
       long result = 0;
	   if(beginDate>endDate)
		  result = (beginDate - endDate)/86400;
       else
          result = (endDate - beginDate)/86400;
	   return (int)result;
   }
   public static String doip(String ip){
//		  String newip="";
//		  for(int i=0;i<3;i++){
//			int p=ip.indexOf(".");
//			newip+=ip.substring(0,p);
//			ip=ip.substring(p+1);
//		  }
//		 newip+=ip;
		 //System.out.println("newip="+newip);
	   	 ip = ip.replaceAll("\\.", "_");
		 return ip;
	}
   public static List checkSize(String sizestr){
	   List rvalue = new ArrayList();
	   float size = Float.parseFloat(sizestr);
			String unit = "";
			float _size=0.0f;
			//if(_size >= size)
			_size=size*1.0f/1024;
			unit = "K";
			if(_size>=1024.0f){
				_size=_size/1024;
				unit = "M";
				if(_size>=1024.0f){
					_size=_size/1024;
					unit = "G";
				}
			}
			rvalue.add(0, Math.round(_size)+"");
			rvalue.add(1, unit);
	   return rvalue;
   }
   
   //吉林中行短信
   
   public static int checkTel(String str)
   {
     if (str.length() != 11)
       return -1;

     if ((str.startsWith("130")) || (str.startsWith("131")) || 
       (str.startsWith("132")) || (str.startsWith("133")))
       return 1;

     return 0;
   }
   
   public static String getStrByLength(String strParameter, int limitLength)
   {
     String return_str = strParameter;
     int temp_int = 0;
     int cut_int = 0;
     byte[] b = strParameter.getBytes();

     for (int i = 0; i < b.length; ++i) {
       if (b[i] >= 0) {
         ++temp_int;
       } else {
         temp_int += 2;
         ++i;
       }
       ++cut_int;

       if (temp_int >= limitLength) {
         if ((temp_int % 2 != 0) && (b[(temp_int - 1)] < 0))
           --cut_int;

         return_str = return_str.substring(0, cut_int);
         break;
       }
     }
     return return_str;
   }
   
   public static String getDay()
   {
     SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
     String str = s.format(Long.valueOf(System.currentTimeMillis()));
     return str;
   }

   public static String getSecond()
   {
     SimpleDateFormat s = new SimpleDateFormat("HHmmss");
     String str = s.format(Long.valueOf(System.currentTimeMillis()));
     return str;
   }

   public static String makeString(int length)
   {
     StringBuffer s = new StringBuffer();
     for (int i = 0; i < length; ++i)
       s.append("");

     return s.toString();
   }

   public static String CheckStr(String str, int length)
   {
     if (str.getBytes().length > length)
       return "告警信息过长";
     return str;
   }
   
//	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,int recover){
//	 	//建立短信		 	
//	 	//从内存里获得当前这个IP的PING的值
//	 	Calendar date=Calendar.getInstance();
//	 	Hashtable sendeddata = ShareData.getSendeddata();
//	 	Hashtable createeventdata = ShareData.getCreateEventdata();
//	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	 	
//	 	Host host = null;
//		try{
//			host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		if(host == null)return;
//	 	try{
// 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress)){
// 				//若不在，则建立短信，并且添加到发送列表里
//	 			Smscontent smscontent = new Smscontent();
//	 			String time = sdf.format(date.getTime());
//	 			smscontent.setLevel(flag+"");
//	 			smscontent.setObjid(objid);
//	 			smscontent.setMessage(content);
//	 			smscontent.setRecordtime(time);
//	 			smscontent.setSubtype(subtype);
//	 			smscontent.setSubentity(subentity);
//	 			smscontent.setIp(ipaddress);
//	 			//发送短信
//	 			SmscontentDao smsmanager=new SmscontentDao();
//	 			smsmanager.sendURLSmscontent(smscontent);	
//				sendeddata.put(subtype+":"+subentity+":"+ipaddress,date);	
////				if(recover == 1){
////					if(subentity.equalsIgnoreCase("ping"))
////						createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
////				}
//				
// 			}else{
// 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
// 				Calendar formerdate =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress);		 				
//	 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//	 			Date last = null;
//	 			Date current = null;
//	 			Calendar sendcalen = formerdate;
//	 			Date cc = sendcalen.getTime();
//	 			String tempsenddate = formatter.format(cc);
//	 			
//	 			Calendar currentcalen = date;
//	 			cc = currentcalen.getTime();
//	 			last = formatter.parse(tempsenddate);
//	 			String currentsenddate = formatter.format(cc);
//	 			current = formatter.parse(currentsenddate);
//	 			
//	 			long subvalue = current.getTime()-last.getTime();	
//	 			if(checkday == 1){
//	 				//检查是否设置了当天发送限制,1为检查,0为不检查
//	 				if (subvalue/(1000*60*60*24)>=1){
//		 				//超过一天，则再发信息
//			 			Smscontent smscontent = new Smscontent();
//			 			String time = sdf.format(date.getTime());
//			 			smscontent.setLevel(flag+"");
//			 			smscontent.setObjid(objid);
//			 			smscontent.setMessage(content);
//			 			smscontent.setRecordtime(time);
//			 			smscontent.setSubtype(subtype);
//			 			smscontent.setSubentity(subentity);
//			 			smscontent.setIp(ipaddress);//发送短信
//			 			SmscontentDao smsmanager=new SmscontentDao();
//			 			smsmanager.sendURLSmscontent(smscontent);
//						//修改已经发送的短信记录	
//						sendeddata.put(subtype+":"+subentity+":"+ipaddress,date);
////						if(recover == 1){
////							if(subentity.equalsIgnoreCase("ping"))
////								createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
////						}
//			 		}
//	 			}else{
//	 				//createEvent("poll",host.getAlias(),host.getBid(),content,flag,subtype,subentity,ipaddress,objid);
//	 				if(recover == 1){
//						if(subentity.equalsIgnoreCase("ping"))
//							createeventdata.put(subtype+":"+subentity+":"+ipaddress, date);
//					}
//	 				//createEvent("poll",sysLocation,getBid(),nm.getAlarmInfo()+"当前值:"+CEIString.round(new Double(memorydata.getThevalue()).doubleValue(),2)+" 阀值:"+nm.getLimenvalue0(),flag,"host","memory");
//	 			}
// 			}	 			 			 			 			 	
//	 	}catch(Exception e){
//	 		e.printStackTrace();
//	 	}
//	 }
	
	/**
	 * 根据告警次数，得到业务系统运行情况
	 * @param levelone
	 * @param leveltwo
	 * @param levelthree
	 * @return
	 */
	public static synchronized String getRunAppraise(int levelone, int leveltwo, int levelthree){
		if((levelone+leveltwo+levelthree) > 0  && (levelone+leveltwo+levelthree) < 3){
			return "良";
		}
		if((levelone+leveltwo+levelthree) > 3){
			return "差";
		}
		if((levelone+leveltwo+levelthree) == 0){
			return "优";
		}
		return "优";
	}
}
