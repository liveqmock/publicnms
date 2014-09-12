package com.afunms.schedule.manage;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.application.util.ReportExport;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.initialize.ResourceCenter;
import com.afunms.schedule.dao.DistrictDao;
import com.afunms.schedule.dao.PeriodDao;
import com.afunms.schedule.dao.ScheduleDao;
import com.afunms.schedule.model.Period;
import com.afunms.schedule.model.Position;
import com.afunms.schedule.model.Schedule;
import com.afunms.schedule.util.Scheduling2;
import com.afunms.schedule.util.Scheduling3;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class ScheduleManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		/*if (action.equals("list"))
			return list();*/
		if (action.equals("savePeriod")) {
			return savePeriod();
		}
		if (action.equals("deletePeriod")) {
			return deletePeriod();
		}
		if (action.equals("editPeriod")) {
			return editPeriod();
		}
		if (action.equals("updatePeriod")) {
			return updatePeriod();
		}
		if (action.equals("listPeriod")) {
			DaoInterface dao = new PeriodDao();
		    setTarget("/schedule/period/list.jsp");
		    return list(dao);
		}
		if (action.equals("savePosition")) {
			return savePosition();
		}
		if (action.equals("deletePosition")) {
			return deletePosition();
		}
		if (action.equals("editPosition")) {
			return editPosition();
		}
		if (action.equals("updatePosition")) {
			return updatePosition();
		}
		if (action.equals("listPosition")) {
			DaoInterface dao = new DistrictDao();
		    setTarget("/schedule/position/list.jsp");
		    return list(dao);
		}
		if (action.equals("list")) {
		    return list();
		}
		if (action.equals("ready_schedule")) {
			return readySchedule();
		}
		if (action.equals("saveSchedule")) {
			return saveSchedule();
		}
		if (action.equals("saveSchedule2")) {
			return saveSchedule2();
		}
		if (action.equals("deleteSchedule")) {
			return deleteSchedule();
		}
		if (action.equals("ready_takeover")) {
			return readyTakeover();
		}
		if (action.equals("saveTakeover")) {
			return saveTakeover();
		}
		if (action.equals("ready_handover")) {
			return readyHandover();
		}
		if (action.equals("saveHandover")) {
			return saveHandover();
		}
		if (action.equals("downloadReport")) {
			return downloadReport();
		}
		if (action.equals("calendarView")) {
			return calendarView();
		}
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String calendarView() {
		UserDao userDao = new UserDao();
		List<User> userList = userDao.loadAll();
		Map<Integer,User> userMap = new HashMap<Integer,User>();
		
		PeriodDao periodDao = new PeriodDao();
		List<Period> periodList = periodDao.loadAll();
		Map<String,Period> periodMap = new HashMap<String,Period>();
		
		DistrictDao positionDao = new DistrictDao();
		List<Position> positionList = positionDao.loadAll();
		Map<String,Position> positionMap = new HashMap<String,Position>();
		
		for (User user : userList) {
			userMap.put(user.getId(), user);
		}
		
		for (Period period : periodList) {
			periodMap.put(period.getId(), period);
		}
		
		for (Position position : positionList) {
			positionMap.put(position.getId(), position);
		}
		
		ScheduleDao dao = null;
		boolean result = false;
		List<Schedule> scheduleList = null;
		try{
			String positionid = getParaValue("positionid");
			dao = new ScheduleDao();
			if (null == positionid || "".equals(positionid)) {
				scheduleList = dao.loadAll();
			}else{
				scheduleList = dao.loadAll("where position = '" + positionid + "'");
			}
			result = true;
		}catch(Exception e){
			result = false ;
			e.printStackTrace();
		}finally{
			dao.close();
		}
		request.setAttribute("scheduleList", scheduleList);
		request.setAttribute("userMap", userMap);
		request.setAttribute("periodMap", periodMap);
		request.setAttribute("positionMap", positionMap);
		if(result){
			return "/schedule/scheduling/calendarView.jsp";
		}else{
			return "/schedule/scheduling/saveFail.jsp";
		}
	}

	private String downloadReport() {
		String type=getParaValue("type");
		String exportType=getParaValue("exportType");
		int cabinetid=getParaIntValue("cabinetid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time=sdf.format(new Date());
		//String name = getParaValue("fileName");
		String filePath = ResourceCenter.getInstance().getSysPath() + "/schedule/scheduling/scheduleReport("+time+")." +exportType;
		String startTime=getParaValue("startdate");
		String toTime=getParaValue("todate");
		ReportExport export=new ReportExport();
		export.exportScheduleReport(type, filePath, startTime, toTime,exportType);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}

	private String readyHandover() {
		ScheduleDao dao = new ScheduleDao();
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		Schedule schedule = dao.getHandover(userid);
		if(null != schedule)
			schedule.setName(user.getName());
		
		PeriodDao periodDao = new PeriodDao();
		List<Period> periodList = periodDao.loadAll();
		Map<String,Period> periodMap = new HashMap<String,Period>();
		for (Period period : periodList) {
			periodMap.put(period.getId(), period);
		}
		
		request.setAttribute("schedule", schedule);
		request.setAttribute("periodMap", periodMap);
		return "/schedule/scheduling/handover.jsp";
	}

	private String saveHandover() {
		ScheduleDao dao = new ScheduleDao();
		Schedule schedule = new Schedule();
		String id = request.getParameter("id");
		String log = request.getParameter("log");
		
		String target = null;
		try {
			schedule.setId(id);
			schedule.setLog(log);
			schedule.setStatus("3");
			schedule.setUpdated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
			schedule.setUpdated_on(new Timestamp(new Date().getTime()));
			if (dao.update(schedule))
				target = "/schedule.do?action=ready_handover";
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return target;
	}

	private String saveTakeover() {
		String id = request.getParameter("scheduleid");
		ScheduleDao dao = new ScheduleDao();
		dao.update(id, 2);
		return readyTakeover();
	}

	private String readyTakeover() {
		ScheduleDao dao = new ScheduleDao();
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		int userid = user.getId();
		Schedule schedule = dao.getTakeover(userid);
		
		PeriodDao periodDao = new PeriodDao();
		List<Period> periodList = periodDao.loadAll();
		Map<String,Period> periodMap = new HashMap<String,Period>();
		for (Period period : periodList) {
			periodMap.put(period.getId(), period);
		}
		
		request.setAttribute("schedule", schedule);
		request.setAttribute("periodMap", periodMap);
		return "/schedule/scheduling/takeover.jsp";
	}

	private String list() {
		ScheduleDao dao = new ScheduleDao();
		setTarget("/schedule/scheduling/list.jsp");

		int userid = getParaIntValue("userid");//值班人
		String periodid = getParaValue("periodid");// 班次
		String positionid = getParaValue("positionid");// 地点
		String startdate = getParaValue("startdate");//开始日期
		String enddate = getParaValue("enddate");//结束日期
		
		UserDao userDao = new UserDao();
		List<User> userList = userDao.loadAll();
		Map<Integer,User> userMap = new HashMap<Integer,User>();
		
		PeriodDao periodDao = new PeriodDao();
		List<Period> periodList = periodDao.loadAll();
		Map<String,Period> periodMap = new HashMap<String,Period>();
		
		DistrictDao positionDao = new DistrictDao();
		List<Position> positionList = positionDao.loadAll();
		Map<String,Position> positionMap = new HashMap<String,Position>();
		
		for (User user : userList) {
			userMap.put(user.getId(), user);
		}
		
		for (Period period : periodList) {
			periodMap.put(period.getId(), period);
		}
		
		for (Position position : positionList) {
			positionMap.put(position.getId(), position);
		}
		
		StringBuffer where = new StringBuffer();
		where.append(" where 1=1");
		
		if(userid!=-1){
			where.append(" and watcher=" + userid);
		}
		if(periodid != null && !"".equals(periodid) && !"null".equals(periodid)){
			where.append(" and period='" + periodid + "'");
		}
		if(positionid != null && !"".equals(positionid) && !"null".equals(positionid)){
			where.append(" and position='" + positionid + "'");
		}
		if (startdate != null && !"".equals(startdate) && !"null".equals(startdate)){
			where.append(" and on_date>='" + startdate + " 00:00:00'");
		}
		if (enddate != null && !"".equals(enddate) && !"null".equals(enddate)) {
			where.append(" and on_date<='" + enddate + " 23:59:59'");
		}
		
		periodid = periodid == null?"":periodid;
		startdate = startdate == null?"":startdate;
		enddate = enddate == null?"":enddate;
		
		request.setAttribute("userid", userid);
		request.setAttribute("userMap", userMap);
		request.setAttribute("periodMap", periodMap);
		request.setAttribute("positionMap", positionMap);
		request.setAttribute("periodid", periodid);
		request.setAttribute("positionid", positionid);
		request.setAttribute("startdate", startdate);
		request.setAttribute("enddate", enddate);
		return list(dao, where + " order by on_date,position,period");
	}

	private String readySchedule() {
		PeriodDao periodDao = new PeriodDao();
		DistrictDao positionDao = new DistrictDao();
		List<Period> periodList = new ArrayList<Period>();
		List<Position> positionList = new ArrayList<Position>();
		try {
			periodList = periodDao.loadAll();
			positionList = positionDao.loadAll();
		} catch (Exception e) {

		} finally {
			periodDao.close();
			positionDao.close();
		}
		request.setAttribute("periodList", periodList);
		request.setAttribute("positionList", positionList);
		return "/schedule/scheduling/scheduling.jsp";
	}
	
/*	private String saveSchedule() {
		String userids = getParaValue("userids");
		String periodids = getParaValue("periodids");
		String positionids = getParaValue("positionids");
		String startdate = getParaValue("startdate");
		int loops = getParaIntValue("loops");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = sdf.parse(startdate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Scheduling scheduling = new Scheduling(periodids,positionids);
		int days = userids.split(",").length * 2 * 7 * 2/4;
		List<Worker> list = null;
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(d);

		for (int loop = 0; loop < loops; loop++) {
			list = scheduling.doSchedule(userids, days);
			for (int i = 0; i < list.size(); i++) {
				Worker worker = list.get(i);
				if (i !=0 && i%4==0) {
					rightNow.add(Calendar.DAY_OF_WEEK, 1);
				}
				String date = sdf.format(rightNow.getTime());
				System.out.println((i+1) + " 日期:" + date + ":" + worker.getName()+":"+worker.getPlace()+":"+worker.getWorkTime());
				Schedule schedule = new Schedule();
				schedule.setOn_date(new Timestamp(rightNow.getTime().getTime()));
				schedule.setWatcher(Integer.parseInt(worker.getName()));
				schedule.setPeriod(worker.getWorkTime());
				schedule.setPosition(worker.getPlace());
				schedule.setCreated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
				schedule.setCreated_on(new Timestamp(new Date().getTime()));
				ScheduleDao dao = new ScheduleDao();
				dao.save(schedule);
			}
		}
		
		System.out.println("saveSchedule");
		return list();
	}*/
	
	private String saveSchedule() {
		String userids = getParaValue("userids");
		String periodids = getParaValue("periodids");
		String positionids = getParaValue("positionids");
		String startdate = getParaValue("startdate");
		int loops = getParaIntValue("loops");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		Date d = null;
		Date now = new Date();

		try {
			d = sdf.parse(startdate);
			now = sdf.parse(sdf.format(now));
			if (d.before(now)) {
				d = now;
			}

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Scheduling2 scheduling = new Scheduling2(periodids,positionids);
		List<String> list = null;
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		for (int loop = 0; loop < loops; loop++) {
			list = scheduling.doSchedule(userids, c);
			for (int i = 0; i < list.size(); i++) {
				String workerStr = list.get(i);
				String[] keys = workerStr.split("_");
				if (i !=0 && i%4==0) {
					c.add(Calendar.DAY_OF_WEEK, 1);
				}
				String date = sdf.format(c.getTime());
				System.out.println((i+1) + " 日期:" + date + ":" + keys[0]+":"+keys[2]+":"+keys[1]);
				Schedule schedule = new Schedule();
				schedule.setOn_date(new Timestamp(c.getTime().getTime()));
				schedule.setWatcher(Integer.parseInt(keys[0]));
				schedule.setPeriod(keys[1]);
				schedule.setPosition(keys[2]);
				schedule.setCreated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
				schedule.setCreated_on(new Timestamp(new Date().getTime()));
				schedule.setStatus("1");
				ScheduleDao dao = new ScheduleDao();
				dao.save(schedule);
			}
		}
		
		System.out.println("saveSchedule");
		return list();
	}	
	
	private String saveSchedule2() {
		String userids = getParaValue("userids");
		String periodids = getParaValue("periodids");
		String positionids = getParaValue("positionids");
		String startdate = getParaValue("startdate");
		int loops = getParaIntValue("loops");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		Date d = null;
		Date now = new Date();
		
		try {
			d = sdf.parse(startdate);
			now = sdf.parse(sdf.format(now));
			if (d.before(now)) {
				d = now;
			}
			
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Scheduling3 scheduling = new Scheduling3(periodids,positionids);
		List<String> list = null;
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		List<Schedule> scheduleList = new ArrayList<Schedule>();
		for (int loop = 0; loop < loops; loop++) {
			list = scheduling.doSchedule(userids, c);
			for (int i = 0; i < list.size(); i++) {
				String workerStr = list.get(i);
				String[] keys = workerStr.split("_");
				if (i !=0 && i%2==0) {
					c.add(Calendar.DAY_OF_WEEK, 1);
				}
//				String date = sdf.format(c.getTime());
//				System.out.println((i+1) + " 日期:" + date + ":" + keys[0]+":"+keys[2]+":"+keys[1]);
				Schedule schedule = new Schedule();
				schedule.setOn_date(new Timestamp(c.getTime().getTime()));
				schedule.setWatcher(Integer.parseInt(keys[0]));
				schedule.setPeriod(keys[1]);
				schedule.setPosition(keys[2]);
				schedule.setCreated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
				schedule.setCreated_on(new Timestamp(new Date().getTime()));
				schedule.setStatus("1");
				scheduleList.add(schedule);
			}
		}
		if (null != scheduleList && scheduleList.size() > 0) {
			ScheduleDao dao = new ScheduleDao();
			dao.save(scheduleList);
		}
		
		return list();
	}	
	
	private String deleteSchedule() {
		ScheduleDao dao = new ScheduleDao();
		setTarget("/schedule/scheduling/list.jsp");

		int userid = getParaIntValue("userid");//值班人
		String periodid = getParaValue("periodid");// 班次
		String positionid = getParaValue("positionid");// 地点
		String startdate = getParaValue("startdate");//开始日期
		String enddate = getParaValue("enddate");//结束日期
		
		UserDao userDao = new UserDao();
		List<User> userList = userDao.loadAll();
		Map<Integer,User> userMap = new HashMap<Integer,User>();
		
		PeriodDao periodDao = new PeriodDao();
		List<Period> periodList = periodDao.loadAll();
		Map<String,Period> periodMap = new HashMap<String,Period>();
		
		DistrictDao positionDao = new DistrictDao();
		List<Position> positionList = positionDao.loadAll();
		Map<String,Position> positionMap = new HashMap<String,Position>();
		
		for (User user : userList) {
			userMap.put(user.getId(), user);
		}
		
		for (Period period : periodList) {
			periodMap.put(period.getId(), period);
		}
		
		for (Position position : positionList) {
			positionMap.put(position.getId(), position);
		}
		
		StringBuffer where = new StringBuffer();
		where.append(" where 1=1 and status = 1 ");
		
		if(userid!=-1){
			where.append(" and watcher=" + userid);
		}
		if(periodid != null && !"".equals(periodid) && !"null".equals(periodid)){
			where.append(" and period='" + periodid + "'");
		}
		if(positionid != null && !"".equals(positionid) && !"null".equals(positionid)){
			where.append(" and position='" + positionid + "'");
		}
		if (startdate != null && !"".equals(startdate) && !"null".equals(startdate)){
			where.append(" and on_date>='" + startdate + " 00:00:00'");
		}
		if (enddate != null && !"".equals(enddate) && !"null".equals(enddate)) {
			where.append(" and on_date<='" + enddate + " 23:59:59'");
		}
		
		periodid = periodid == null?"":periodid;
		startdate = startdate == null?"":startdate;
		enddate = enddate == null?"":enddate;
		
		request.setAttribute("userid", userid);
		request.setAttribute("userMap", userMap);
		request.setAttribute("periodMap", periodMap);
		request.setAttribute("positionMap", positionMap);
		request.setAttribute("periodid", periodid);
		request.setAttribute("positionid", positionid);
		request.setAttribute("startdate", startdate);
		request.setAttribute("enddate", enddate);
		dao.delete(where.toString());
		return list();
	}
	    
	private String savePeriod() {
		String name = request.getParameter("name");
		String startTime = request.getParameter("start_time");
		String endTime = request.getParameter("end_time");
		Period period = new Period();
		period.setName(name);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:ss");
		String target = null;
		try {
			Date d = sdf.parse(startTime);
			Timestamp t = new Timestamp(d.getTime());
			Time st = new Time(t.getTime());
			d = sdf.parse(endTime);
			t = new Timestamp(d.getTime());
			Time et = new Time(t.getTime());
			period.setStart_time(st);
			period.setEnd_time(et);
			period.setCreated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
			PeriodDao dao = new PeriodDao();
			int result = dao.save(period);

			if (result == 0) {
			    target = null;
			    setErrorCode(ErrorMessage.SCHEDULE_PERIOD_EXIST);
			} else if (result == 1)
			    target = "/schedule.do?action=listPeriod";
			else
			    target = null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return target;
	}
		
	private String deletePeriod() {
		PeriodDao dao = new PeriodDao();
		String[] ids = getParaArrayValue("checkbox");
    	if(ids != null && ids.length > 0){
    		dao.delete(ids);
    	}
    	return "/schedule.do?action=listPeriod";
	}

	private String editPeriod() {
		String targetJsp = "/schedule/period/edit.jsp";
		BaseVo vo = null;
		PeriodDao dao = new PeriodDao();
		try {
		    vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    dao.close();
		}
		if (vo != null) {
		    request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String updatePeriod() {
		String startTime = getParaValue("start_time");
		String endTime = getParaValue("end_time");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:ss");
		PeriodDao dao = new PeriodDao();
		Period period = new Period();
		period.setId(getParaValue("id"));
		period.setName(getParaValue("name"));
		String target = null;
		try {
			Date d = sdf.parse(startTime);
			Timestamp t = new Timestamp(d.getTime());
			Time st = new Time(t.getTime());
			d = sdf.parse(endTime);
			period.setStart_time(st);
			t = new Timestamp(d.getTime());
			Time et = new Time(t.getTime());
			period.setEnd_time(et);
			period.setUpdated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
			period.setUpdated_on(new Timestamp(new Date().getTime()));
			if (dao.update(period))
				target = "/schedule.do?action=listPeriod";
		}catch(Exception e){
			e.printStackTrace();
		}
		return target;
	}
	
	private String savePosition() {
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		Position position = new Position();
		position.setName(name);
		String target = null;
		position.setDescription(description);
		position.setCreated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
		position.setCreated_on(new Timestamp(new Date().getTime()));
		DistrictDao dao = new DistrictDao();
		int result = dao.save(position);
		
		if (result == 0) {
			target = null;
			setErrorCode(ErrorMessage.SCHEDULE_POSITION_EXIST);
		} else if (result == 1)
			target = "/schedule.do?action=listPosition";
		else
			target = null;
		return target;
	}
	
	private String deletePosition() {
		DistrictDao dao = new DistrictDao();
		String[] ids = getParaArrayValue("checkbox");
		if(ids != null && ids.length > 0){
			dao.delete(ids);
		}
		return "/schedule.do?action=listPosition";
	}
	
	private String editPosition() {
		String targetJsp = "/schedule/position/edit.jsp";
		BaseVo vo = null;
		DistrictDao dao = new DistrictDao();
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}
	
	private String updatePosition() {
		String startTime = getParaValue("start_time");
		String endTime = getParaValue("end_time");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:ss");
		DistrictDao dao = new DistrictDao();
		Position position = new Position();
		position.setId(getParaValue("id"));
		position.setName(getParaValue("name"));
		position.setDescription(getParaValue("description"));
		String target = null;
		position.setUpdated_by(((User)session.getAttribute(SessionConstant.CURRENT_USER)).getName());
		position.setUpdated_on(new Timestamp(new Date().getTime()));
		if (dao.update(position))
			target = "/schedule.do?action=listPosition";
		return target;
	}

}
