/*
 * Created on 2005-4-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.afunms.alertalarm.CreateAlarmXml;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.AlarmInfoDao;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AlarmUpdateTask extends TimerTask
{

	//取过去10秒钟内发生的告警信息
	public void run()
	{
		Connection con = null;			
		PreparedStatement stmt = null;
		ResultSet rs = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar sendcalen = Calendar.getInstance();
 		Date cc = sendcalen.getTime();
 		String curdate = formatter.format(cc);			 			
		
		
		String dateStr = CommonUtil.getCurrentDate();  //取当前日期
		String endTime = CommonUtil.getCurrentTime();  //取当前时间
		String beginTime = CommonUtil.getLaterTenSecondTime();  //取过去10秒的时间
		
		String fromTime = curdate+" "+beginTime;
		String toTime = curdate+" "+endTime;
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		String queryStr = "";
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			queryStr = "select ipaddress,level1,content,type from nms_alarminfo where level1 = 2 and recordtime >='"+fromTime+"' and recordtime<='"+toTime+"' ";
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			queryStr = "select ipaddress,level1,content,type from nms_alarminfo where level1 = 2 and recordtime >=to_date('"+fromTime+"','YYYY-MM-DD HH24:MI:SS')  and recordtime<=to_date('"+toTime+"','YYYY-MM-DD HH24:MI:SS')";
		}
		
		List list = new ArrayList();
		try{
			list = alarmdao.findByCriteria(queryStr);
		}catch(Exception ex){
		}finally{
			try{
				alarmdao.close();
			}catch(Exception exp){
				exp.printStackTrace();
			}
		}

		
		//String queryStr = "select Ipaddress,AlarmData,AlarmTime,SeverityoftheAlarm,SourceOftheAlarm,AlarmDetailInfor from dbo.alarminfor where AlarmData = '"+dateStr.trim()+"' and AlarmTime BETWEEN  '"+beginTime.trim()+"' AND '"+endTime.trim()+"'";
		//System.out.println("声音告警："+queryStr);
		//String[][] tmpResults = ResObjsContainer.persistService.executeQuery(queryStr, connection); //最新的时间段的报警信息的查询,暂定为取严重级别的告警
        if(list !=null && list.size()>0){        	
        	CreateAlarmXml cax = new CreateAlarmXml();
        	cax.CreateAlarmXml(list);  //生成告警信息的XML文件
        }
	}
}
