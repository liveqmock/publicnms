/**
 * <p>Description:通过http服务器收集各地市symantec数据</p>
 * <p>Company: 北京东华合创数码科技股份有限公司</p>
 * @author 王福民
 * @project 阿福网管
 * @date 2005-03-15
 */

package com.afunms.security.util;

import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.net.*;

import com.afunms.security.dao.SymantecDao;
import com.afunms.common.util.DBManager;
import com.afunms.security.model.SymantecLog;
import com.afunms.common.util.SysUtil;

public class HttpSymantecLog
{
  private String beginDate;     //日期
  private String beginTime;     //发生时间
  private String machine;       //机器名
  private String machine_ip;    //机器ip
  private String virus;         //病毒
  private String virus_file;    //被感染文件
  private String deal_way;      //处理方式
  private int historyRow;       //日志文件开始的行数
  private String logFile;       //日志文件  
  private SymantecLog slvo;     //日志记录
  private SymantecDao dao;      
  
  public HttpSymantecLog()
  {
  }

  public void init(int logID)
  {
     beginDate = SysUtil.getCurrentDate();
     //把当前日期转换成一个日志文件名
     logFile = beginDate.substring(5,7) + beginDate.substring(8,10) + beginDate.substring(0,4) + ".log";
     try
     {
        dao = new SymantecDao();
        slvo = dao.findLogByID(logID); //只有一行记录
        if(logFile.equals(slvo.getLogFile()))
           historyRow = slvo.getLogRow();
        else
           historyRow = 1;
     }
     catch(Exception mse)
     {
        historyRow = 1;         //上一次读到第几行
     }
  }
  
  //连接各地市的http服务器
  private URLConnection connectHttpServer()
  {
     String info = null;
     URLConnection urlConn = null;
     try
     {
        URL url = new URL("http://" + slvo.getIp() + ":5166/" + logFile);
        System.out.println("连接" + slvo.getIp() + ",Symantec服务器");
        urlConn = url.openConnection();
        urlConn.connect();
     }
     catch(NoRouteToHostException nre)
     {
        info = "ping不通:" + slvo.getIp();
     }
     catch(FileNotFoundException fe)
     {
        info = "找不到文件:" + slvo.getLogFile();
     }
     catch(ConnectException ce)
     {
        info = "http服务器没启动:" + slvo.getIp();
     }
     catch(Exception e)
     {
        info = "未知错误:" + e.getClass();
     }

     if(info!=null)
     {
        slvo.setInfo(info);
        urlConn = null;
     }
     else  //正常连接,没有错误
        slvo.setInfo("正常");

     return urlConn;
  }

  //导入日志
  public synchronized void beginTransaction()
  {  	 
     URLConnection uc = connectHttpServer();
     if(uc==null) return;

     DataInputStream dis = null;
     String oneRow = null;
     int realNew = 0,row=0,loop=0; //实际插入的记录行数
     DBManager db = null;
     try
     {    
        dis = new DataInputStream(new BufferedInputStream(uc.getInputStream()));
        db = new DBManager();
        while((oneRow = dis.readLine())!=null)
        {
          row++;
          if(row<historyRow) continue; //从上次读过的行开始
          
          if(dealOneRow(oneRow))
          {
             StringBuffer sqlBf = new StringBuffer(100);
             sqlBf.append("insert into nms_symantec(begintime,machine,machine_ip,virus,virus_file,deal_way)values(");
             sqlBf.append("'");
             sqlBf.append(beginTime);
             sqlBf.append("','");
             sqlBf.append(machine);
             sqlBf.append("','");
             sqlBf.append(machine_ip);
             sqlBf.append("','");
             sqlBf.append(virus.replace('\'','-'));
             sqlBf.append("','");
             sqlBf.append(virus_file.replace('\\','/'));
             sqlBf.append("','");
             sqlBf.append(deal_way);
             sqlBf.append("')");
            
             try
             {
                db.addBatch(sqlBf.toString());
                realNew++;
                loop++;
                if(loop>200)  //每200条提交一次
                {
                   db.executeBatch();
                   loop = 0;
                }
             }
             catch(Exception e)
             {
             }
          }//end_if_ok
        }//end_while
        db.executeBatch();
        //用oracle procedure处理两个复杂的过程
        System.out.println("共导入" + realNew + "条Symantec日志");
    }
    catch (Exception e)
    {
       System.out.println("导入日志出错:" + e.getMessage());
    }
    finally
    {
       db.close();
       slvo.setLogFile(logFile);
       slvo.setLogRow(row);
       dao.finish(slvo);
    }
  }

  //把字符转成时间
  private String stringToTime(String hexStr)
  {
    int c1,c2;
    int year=0,month=0,day=0,hour=0,minute=0,second=0;
    String result;

    for(int i=0;i<12;i+=2)
    {
       c1 = hexToDec(hexStr.substring(i,i+1));
       c2 = hexToDec(hexStr.substring(i+1,i+2));
       if(i==0)
           year = c1*16 + c2 + 1970;
       else if(i==2)
           month = c1*16 + c2 + 1;
       else if(i==4)
           day = c1*16 + c2;
       else if(i==6)
           hour = c1*16 + c2;
       else if(i==8)
           minute = c1*16 + c2;
       else if(i==10)
           second = c1*16 + c2;
    }
    String tmpM = null;
    String tmpD = null;    
    if(month>9)
        tmpM = "" + month;
     else
        tmpM = "0" + month;

     if(day>9)
        tmpD = "" + day;
     else
        tmpD = "0" + day;
     
     result = year + "-" + tmpM + "-" + tmpD + " " + hour + ":" + minute + ":" + second;
    
    return result;
  }

  private int hexToDec(String h)
  {
     if(h.equals("A"))
        return 10;
     else if(h.equals("B"))
        return 11;
     else if(h.equals("C"))
        return 12;
     else if(h.equals("D"))
        return 13;
     else if(h.equals("E"))
        return 14;
     else if(h.equals("F"))
        return 15;
     else
        return Integer.parseInt(h);
  }

  //给出病毒处理方式
  private String getDealWay(String dw)
  {
    if(dw.equals("1"))
       return "隔离被感染文件";
    else if(dw.equals("2"))
       return "重命名被感染文件";
    else if(dw.equals("3"))
       return "删除被感染文件";
    else if(dw.equals("4"))
       return "仅记录,不操作";
    else if(dw.equals("5"))
       return "清除病毒";
    else if(dw.equals("6"))
       return "清除病毒";
    else
       return "未知";
  }

  private String getMachineIP(String mip)
  {
     if(mip==null||"".equals(mip)||mip.indexOf("(IP)")==-1)
        return "";

     return mip.substring(5);
  }

  //处理一行
  private boolean dealOneRow(String one_Row)
  {
    String oneRow = SysUtil.getChinese(one_Row); //转换成中文
    boolean ok = true;
    try
    {
      StringTokenizer st = new StringTokenizer(oneRow, ",");
      int i = 0;
      while(st.hasMoreTokens())
      {
         if(i==0)
            beginTime = stringToTime(st.nextToken()); //时间
         else if(i==4)
            machine = st.nextToken();        //机器名
         else if(i==6)
            virus = st.nextToken();          //病毒名
         else if(i==7)
         {	
            String vf = st.nextToken().replace('\'','-');    
         	virus_file = vf.replace('\\','/');     //被感染文件         	
         }   
         else if(i==10)
            deal_way = getDealWay(st.nextToken());    //处理方式
         else if(i==30)
            machine_ip = getMachineIP(st.nextToken());  //机器ip
         else
            st.nextToken();
         i++;
      }//end_while
      try
      {
         machine_ip = machine_ip.trim();
      }
      catch(Exception e)
      {
         machine_ip = null;
      }
      if(machine_ip==null||"".equals(machine_ip)||"未知".equals(deal_way))
          ok = false;  //如果没有ip地址或者处理方式为"未知",则不插入
      if("255.255.255.255".equals(machine_ip))
          ok = false;
    }
    catch(Exception e)
    {
       ok = false;
    }
    return ok;
  }
}
