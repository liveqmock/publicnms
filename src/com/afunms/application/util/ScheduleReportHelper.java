package com.afunms.application.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.schedule.dao.PeriodDao;
import com.afunms.schedule.dao.ScheduleDao;
import com.afunms.schedule.model.Period;
import com.afunms.schedule.model.Schedule;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

/**
 * 
 */
public class ScheduleReportHelper {
	
	public List exportScheduleData(String type, String filePath, String startdate, String todate) {
		List resultlist = new ArrayList();
		List resultlist2 = new ArrayList();
		
		String[] scheduleTitle = { "日期", "星期", "白班", "夜班"};
		
		UserDao userDao = new UserDao();
		List<User> userList = userDao.loadAll();
		Map<Integer,User> userMap = new HashMap<Integer,User>();
		
		PeriodDao periodDao = new PeriodDao();
		List<Period> periodList = periodDao.loadAll();
		Map<String,Period> periodMap = new HashMap<String,Period>();
		
		for (User user : userList) {
			userMap.put(user.getId(), user);
		}
		
		ScheduleDao dao = new ScheduleDao();
		
		List<String> polist = dao.getPosition("select distinct position from nms_schedule");
		
		for (int i = 0; i < polist.size(); i++) {
			ArrayList<String[]> tableList = new ArrayList<String[]>();
			ScheduleDao sdao = new ScheduleDao();
//			tableList.add(scheduleTitle);
			StringBuffer where = new StringBuffer();
			where.append(" where 1=1 and position = '"+polist.get(i)+"'");
			
			if (startdate != null && !"".equals(startdate) && !"null".equals(startdate)){
				where.append(" and on_date>='" + startdate + " 00:00:00'");
			}
			if (todate != null && !"".equals(todate) && !"null".equals(todate)) {
				where.append(" and on_date<='" + todate + " 23:59:59'");
			}
			
			List<Schedule> list = null;
			try {
				list = sdao.findByCondition(where + " order by POSITION,on_date,period");
			} catch (Exception e) {
				e.printStackTrace();
			} 
			if (list != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String userName = "";
				String[] data = null;
				String strPosition = list.get(0).getPosition(); 
				for (int j = 0; j < list.size(); j++) {
					Schedule vo = (Schedule) list.get(j);
					if (vo == null)
						continue;
					
//					if (!strPosition.equals(vo.getPosition())) {
//						tableList.add(scheduleTitle);
//						strPosition = vo.getPosition();
//					}
					if (userMap.containsKey(vo.getWatcher())) {
						userName = userMap.get(vo.getWatcher()).getName();
					}
					if (j % 2 == 0) {
						data = new String[4];
						data[0] = sdf.format(vo.getOn_date());
						data[1] = parseWeek(vo.getOn_date());
						data[2] = userName;
					}else{
						data[3] = userName;
						tableList.add(data);
					}			
				}
			}
			resultlist.add(tableList);
			sdao.close();
		}
		
		for (int i = 0; i < resultlist.size(); i++) {
			List subTableList = new ArrayList();
			List regionTableList = new ArrayList();
			ArrayList<String[]> tableList = (ArrayList<String[]>)resultlist.get(i);
			String[] tds = tableList.get(0);
			subTableList.addAll(fill(tds[0], scheduleTitle));
			for (int j = 0; j < tableList.size(); j++) {
				tds = tableList.get(j);
				if ("星期一".equals(tds[1])) {
					subTableList.add(scheduleTitle);
				}
				subTableList.add(tds);
				if (subTableList.size() == 8 || j == tableList.size()-1) {
					regionTableList.add(subTableList);
					subTableList = new ArrayList();
				}
			}
			resultlist2.add(regionTableList);
		}
		dao.close();
		return resultlist2;
	}
	
	private ArrayList<String[]> fill(String strDate, String[] scheduleTitle) {
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(scheduleTitle);
		String[] strs = new String[]{"","","",""};
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(strDate);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int week = c.get(Calendar.DAY_OF_WEEK);
			switch(week){
			case 1://str = "星期日";
				for (int i = 0; i < 6; i++) {
					list.add(strs);
				}
				break;
			case 2://str = "星期一";
				break;
			case 3://str = "星期二";
				for (int i = 0; i < 1; i++) {
					list.add(strs);
				}
				break;
			case 4://str = "星期三";
				for (int i = 0; i < 2; i++) {
					list.add(strs);
				}
				break;
			case 5://str = "星期四";
				for (int i = 0; i < 3; i++) {
					list.add(strs);
				}
				break;
			case 6://str = "星期五";
				for (int i = 0; i < 4; i++) {
					list.add(strs);
				}
				break;
			case 7://str = "星期六";
				for (int i = 0; i < 5; i++) {
					list.add(strs);
				}
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static String parseWeek(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int week = c.get(Calendar.DAY_OF_WEEK);
		String str = "";
		switch(week){
		case 1:str = "星期日";break;
		case 2:str = "星期一";break;
		case 3:str = "星期二";break;
		case 4:str = "星期三";break;
		case 5:str = "星期四";break;
		case 6:str = "星期五";break;
		case 7:str = "星期六";break;
		}
		return str;
	}

}