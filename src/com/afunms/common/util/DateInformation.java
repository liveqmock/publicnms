/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.text.DateFormat;
import java.util.*;
import java.text.*;
import java.io.*;

/**
 * detail:日期类
 * 创建日期：(2003-3-27 15:30:41)
 * @author:zhoulf
 */

public class DateInformation {
	Calendar  calendar = null;

	public DateInformation() {
	calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
	Date trialTime = new Date();
	calendar.setTime(trialTime);
	}
	public int getAMPM() {
	return calendar.get(Calendar.AM_PM);
	}
	public String getDate() {
	 StringBuffer temp = new StringBuffer("");
	 temp.append(getYear());
	 if (getMonthInt()>9)
	   {temp.append("-");}
	   else
	     {temp.append("-0");}
	  temp.append(getMonthInt());

	 if (getDayOfMonth()>9)
	   {temp.append("-");}
	   else
	     {temp.append("-0");}
	  temp.append(getDayOfMonth());
	return temp.toString();

	}
	public String getDay() {
	int x = getDayOfWeek();
	String[] days = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday",
				      "Thursday", "Friday", "Saturday"};

	if (x > 7)
	    return "Unknown to Man";

	return days[x - 1];

	}
	public int getDayOfMonth() {
	return calendar.get(Calendar.DAY_OF_MONTH);
	}
	public int getDayOfWeek() {
	return calendar.get(Calendar.DAY_OF_WEEK);
	}
	public int getDayOfYear() {
	return calendar.get(Calendar.DAY_OF_YEAR);
	}
	public int getDSTOffset() {
	return calendar.get(Calendar.DST_OFFSET)/(60*60*1000);
	}
	public int getEra() {
	return calendar.get(Calendar.ERA);
	}
/**
 * 得到本月第一天。。
 * 创建日期：(2003-12-2 13:47:15)
 * @return java.lang.String
 */
public String getFirstDayOfMonth() {
	SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-01");
     String dateString = theDate.format(calendar.getTime());
	return dateString;
}
	public int getHour() {
	return calendar.get(Calendar.HOUR_OF_DAY);
	}
/**
 * 根据传入的偏移量算出与当前日期偏差的日期
 * 创建日期：(2003-8-8 15:25:34)
 * @return java.lang.String
 */
public String getIntervalDate(int Interval) {
	Calendar cal = new GregorianCalendar();
     cal.add(GregorianCalendar.DATE,Interval);
     SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
     String dateString = theDate.format(cal.getTime());

return dateString;
}
/**
 * 计算当前时间所在月有多少天。
 * 创建日期：(2003-12-2 15:53:08)
 * @return int
 * @param param java.lang.String
 * @param param1 java.lang.String
 */
public int getIntervalInOneMonth() {
	DateInformation dinf=new DateInformation();

	Calendar cal1 = new GregorianCalendar();
    cal1.add(GregorianCalendar.DATE,(-(dinf.getDayOfMonth())+1));

    Calendar cal = new GregorianCalendar();
    cal.add(GregorianCalendar.MONTH,1);
    cal.add(GregorianCalendar.DATE,-(dinf.getDayOfMonth()));
	return cal.getTime().getDate()-cal1.getTime().getDate()+1;
}
/**
 * 得到本月最后一天。
 * 创建日期：(2003-12-2 13:50:12)
 * @return java.lang.String
 * @param Interval int
 */
public String getLastDayOfMonth() {
	Calendar cal = new GregorianCalendar();
     DateInformation dinf=new DateInformation();
    cal.add(GregorianCalendar.MONTH,1);
    cal.add(GregorianCalendar.DATE,-(dinf.getDayOfMonth()));
     SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
     String dateString = theDate.format(cal.getTime());
return dateString;
}
	public int getMinute() {
	return calendar.get(Calendar.MINUTE);
	}
	public String getMonth() {
	int m = getMonthInt();
	String[] months = new String [] { "January", "February", "March",
					"April", "May", "June",
					"July", "August", "September",
					"October", "November", "December" };
	if (m > 12)
	    return "Unknown to Man";

	return months[m - 1];

	}
	public int getMonthInt() {
	return 1 + calendar.get(Calendar.MONTH);
	}
	public int getSecond() {
	return calendar.get(Calendar.SECOND);
	}
	public String getTime() {
		StringBuffer temp = new StringBuffer("");
	 temp.append(getHour() );
	 if (getMinute() >9)
	   {temp.append(":");}
	   else
	     {temp.append(":0");}
	  temp.append(getMinute() );

	 if (getSecond()>9)
	   {temp.append(":");}
	   else
	     {temp.append(":0");}
	  temp.append(getSecond());
	return temp.toString();
}
	public int getWeekOfMonth() {
	return calendar.get(Calendar.WEEK_OF_MONTH);
	}
	public int getWeekOfYear() {
	return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	public int getYear() {
	return calendar.get(Calendar.YEAR);
	}
	public static void main(String args[]) {
	DateInformation db = new DateInformation();
	/*p("dateofweek: " + db.getDayOfWeek());
	p("date: " + db.getDayOfMonth());
	p("year: " + db.getYear());
	p("month: " + db.getMonth());

	p("date: " + db.getDate());
	p("Day: " + db.getDay());
	p("DayOfYear: " + db.getDayOfYear());
	p("WeekOfYear: " + db.getWeekOfYear());
	p("era: " + db.getEra());
	p("ampm: " + db.getAMPM());
	p("DST: " + db.getDSTOffset());
	p(db.getIntervalDate(- db.getDayOfWeek()));
	p(db.getIntervalDate(- db.getDayOfWeek()+7));

	p(db.getFirstDayOfMonth());
	p(Integer.toString(db.getDayOfMonth()));
	p(Integer.toString(db.getIntervalInOneMonth()));
	p(Integer.toString(db.getYear()));
	p(db.getCurrentTime());*/
	p("time: " + db.getTime());
	p(db.getLastDayOfMonth());
	p(db.getIntervalTime(-3));
	p("currenttime by intdddd "+db.getCurrentTime(1113408000));
	p("currenttime by int "+db.getCurrentTime(1073984400));
	p("currenttime by int "+db.getCurrentTime(1074049200));
	p(""+new Date(104,1,12,23,59,59));
	p(""+new Date(104,1,12,0,0,0).getTime()/1000);
	p(""+new Date(104,1,12,23,59,59).getTime()/1000);
	p(Integer.toString(db.getIntervalInOneMonth(2003,12)));
	p(db.getFirstDayOfWeek("2004-2-29"));
	p(db.getEndDayOfWeek("2004-01-01"));
	db.getAllDayOfWeek("2004-02-18");
	}
	private static void p(String x) {
	System.out.println(x);
	}

/**
 * 取得当前时间。
 * 创建日期：(2003-12-30 14:58:43)
 * @return java.lang.String
 */
public String getCurrentTime() {
	Calendar cal1 = new GregorianCalendar();
	System.out.println(cal1.getTime());
	SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     String dateString = theDate.format(cal1.getTime());
	return dateString;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-1-8 19:02:56)
 * @return java.lang.String
 * @param timeaddbyseconds int
 */
public String getCurrentTime(int timeaddbyseconds) {
	long timeLong=(long)timeaddbyseconds;
	Date d=new Date(timeLong*1000);
	String timeFormat = "yyyy-MM-dd HH:mm:ss";
    java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(
        timeFormat);
	return timeFormatter.format(d);
}

/**
 * 计算与当前时间的偏移时间。
 * 创建日期：(2003-12-30 14:45:13)
 * @return java.lang.String
 * @param starttime int
 */
public String getIntervalTime(int starttime) {
	DateInformation db = new DateInformation();
	Calendar cal1 = new GregorianCalendar();
	cal1.add(GregorianCalendar.HOUR,starttime);
	SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
     String dateString = theDate.format(cal1.getTime());
	return dateString;
}

/**
 * 根据传入的日期计算出本周的所有天的日期
 * 创建日期：(2004-2-10 10:10:34)
 * @return java.lang.String[]  本周的所有天的日期
 * @param param java.lang.String 传入的日期
 */
public String[] getAllDayOfWeek(String date) {
	String weeks[]=new String[7];
	int year=0,month=0,day=0;
		year=Integer.parseInt(date.substring(0,date.indexOf("-")));
		month=Integer.parseInt(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")));
		day=Integer.parseInt(date.substring(date.lastIndexOf("-")+1,date.length()));
		DateInformation dinf=new DateInformation();
		Calendar cal = new GregorianCalendar();
    	cal.setTime(new Date(year-1900,month-1,day));
    	for(int i=0;i<7;i++){
    	cal.add(Calendar.DATE,(-cal.get(Calendar.DAY_OF_WEEK)+i+1));
    	weeks[i]=(cal.getTime().getYear()+1900)+"-"+(cal.getTime().getMonth()+1)+"-"+cal.getTime().getDate();
    	}

    	return weeks;
}

/**
 * 根据传入的日期计算出本周的最后一天的日期
 * 创建日期：(2003-12-2 13:47:15)
 * @return java.lang.String 本周的最后一天的日期
 */
public String getEndDayOfWeek(String date) {
		int year=0,month=0,day=0;
		year=Integer.parseInt(date.substring(0,date.indexOf("-")));
		month=Integer.parseInt(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")));
		day=Integer.parseInt(date.substring(date.lastIndexOf("-")+1,date.length()));
		DateInformation dinf=new DateInformation();
		Calendar cal = new GregorianCalendar();
    	cal.setTime(new Date(year-1900,month-1,day));
    	cal.add(Calendar.DATE,(-cal.get(Calendar.DAY_OF_WEEK)+7));
		return (cal.getTime().getYear()+1900)+"-"+(cal.getTime().getMonth()+1)+"-"+cal.getTime().getDate();
	}

/**
 * 根据传入的日期计算出本周的第一天的日期
 * 创建日期：(2003-12-2 13:47:15)
 * @return java.lang.String 本周的第一天的日期
 */
public String getFirstDayOfWeek(String date) {
		int year=0,month=0,day=0;
		year=Integer.parseInt(date.substring(0,date.indexOf("-")));
		month=Integer.parseInt(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")));
		day=Integer.parseInt(date.substring(date.lastIndexOf("-")+1,date.length()));
		DateInformation dinf=new DateInformation();
		Calendar cal = new GregorianCalendar();
    	cal.setTime(new Date(year-1900,month-1,day));
    	cal.add(Calendar.DATE,(-cal.get(Calendar.DAY_OF_WEEK)+1));
		return (cal.getTime().getYear()+1900)+"-"+(cal.getTime().getMonth()+1)+"-"+cal.getTime().getDate();
	}

/**
 * 根据传入的年月算出某年的某月共有多少天。
 * 创建日期：(2003-12-2 15:53:08)
 * @return int
 * @param param java.lang.String
 * @param param1 java.lang.String
 */
public int getIntervalInOneMonth(int year,int month) {
	DateInformation dinf=new DateInformation();

	/*Calendar cal1 = new GregorianCalendar();
	cal1.setTime(new Date(year-1900,month-1,0));
    cal1.add(cal1.DATE,1);
	System.out.println(cal1.getTime());*/
    Calendar cal = new GregorianCalendar();
    cal.setTime(new Date(year-1900,month-1,1));
    //System.out.println(cal.getTime());
    cal.add(Calendar.MONTH,1);
    cal.add(Calendar.DATE,-1);
    //System.out.println(cal.getTime());
	return cal.getTime().getDate();
}

public String getLastDayOfMonth(String year,String month){
	String returnVal="";
	//String year = date.substring(0,4);
	//String month = date.substring(date.indexOf("-")+1,date.lastIndexOf("-"));
	int year_i = Integer.valueOf(year).intValue();
	int month_i = Integer.valueOf(month).intValue();

	int i = getIntervalInOneMonth(year_i,month_i);
	returnVal = year+"-"+month+"-"+i;
	 // returnVal = date.substring(0,date.lastIndexOf("-")+1)+String.valueOf(i);
	//System.out.println("year:"+year+"  month:"+month);
	return returnVal;
}

public String getFirstDayOfMonth(String year,String month) {
   //  String dateString = date.substring(0,date.lastIndexOf("-")+1)+"01";

	String dateString = year+"-"+month+"-"+"01";
	return dateString;
}
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
}
