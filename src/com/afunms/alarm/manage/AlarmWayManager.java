/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.alarm.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.dao.AlarmWayDetailDao;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.util.AlarmWayUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.topology.util.KeyGenerator;


public class AlarmWayManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		
		if("list".equals(action)){
			return list();
		}else if("add".equals(action)){
			return add();
		}else if("edit".equals(action)){
			return edit();
		}else if("save".equals(action)){
			return save();
		}else if("update".equals(action)){
			return update();
		}else if("delete".equals(action)){
			return delete();
		}else if("chooselist".equals(action)){
			return chooselist();
		}
		return null;
	}
	
	public String list(){
		
		String jsp = "/alarm/way/list.jsp";
		
		try {
			List list = getList();
			
			request.setAttribute("list", list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsp;
	}
	
	public String chooselist(){
		String jsp = "/alarm/way/chooselist.jsp";
		
		String alarmWayIdEvent = getParaValue("alarmWayIdEvent");
		
		String alarmWayNameEvent = getParaValue("alarmWayNameEvent");
		
		try {
			List list = getList();
			
			request.setAttribute("list", list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("alarmWayIdEvent", alarmWayIdEvent);
		
		request.setAttribute("alarmWayNameEvent", alarmWayNameEvent);
		
		return jsp;
	}
	
	public List getList(){
		String sqlQuery = getSQLQueryForList();
		
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			list(alarmWayDao, sqlQuery);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmWayDao.close();
		}
		List list = (List)request.getAttribute("list");
		return list;
	}
	
	public String getSQLQueryForList(){
		return "";
	}
	
	public String add(){
		String jsp = "/alarm/way/add.jsp";
		return jsp;
	}
	
	public String edit(){
		String jsp = "/alarm/way/edit.jsp";
		String id = getParaValue("id");
		AlarmWay alarmWay = null;
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			alarmWay = (AlarmWay)alarmWayDao.findByID(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmWayDao.close();
		}
		request.setAttribute("alarmWay", alarmWay);
		
		List alarmWayDetailList = null;
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		try {
			alarmWayDetailList = alarmWayDetailDao.findByAlarmWayId(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmWayDetailDao.close();
		}
		request.setAttribute("alarmWayDetailList", alarmWayDetailList);
		
		return jsp;
	}
	
	public String update(){
		boolean result = false;
		AlarmWay alarmWay = createAlarmWay();
		int id = getParaIntValue("id");
		alarmWay.setId(id);
		
		try {
			AlarmWayUtil alarmWayUtil = new AlarmWayUtil();
			result = alarmWayUtil.updateAlarmWay(alarmWay);
			if(result){
				List alarmWayDetailList = createAlarmWayDetailList();
				alarmWayUtil.saveAlarmWayDetail(alarmWay , alarmWayDetailList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list();
	}
	
	public String save(){
		boolean result = false;
		AlarmWay alarmWay = createAlarmWay();
		
		alarmWay.setId(KeyGenerator.getInstance().getNextKey());
		AlarmWayUtil alarmWayUtil = new AlarmWayUtil();
		try {
			result = alarmWayUtil.saveAlarmWay(alarmWay);
			if(result){
				List alarmWayDetailList = createAlarmWayDetailList();
				alarmWayUtil.saveAlarmWayDetail(alarmWay , alarmWayDetailList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list();
	}
	
	public String delete(){
		
		boolean result = false;
		
		String ids[] = getParaArrayValue("checkbox");

		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			result = alarmWayDao.delete(ids);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmWayDao.close();
		}
		
		if(result){
			AlarmWayUtil alarmWayUtil = new AlarmWayUtil();
			alarmWayUtil.deleteAlarmWayDetail(ids);
		}
		
		return list();
	}
	
	
	public AlarmWay createAlarmWay(){
		String name = getParaValue("name");
		String description = getParaValue("description");
		String isDefault = getParaValue("isDefault");
		String[] isPageAlarm_arr = getParaArrayValue("isPageAlarm");
		String[] isSoundAlarm_arr = getParaArrayValue("isSoundAlarm");
		String[] isPhoneAlarm_arr = getParaArrayValue("isPhoneAlarm");
		String[] isSMSAlarm_arr = getParaArrayValue("isSMSAlarm");
		String[] isMailAlarm_arr = getParaArrayValue("isMailAlarm");
		String[] isDesktopAlarm_arr = getParaArrayValue("isDesktopAlarm");
		String isPageAlarm = "0";
		if(isPageAlarm_arr!=null && isPageAlarm_arr.length > 0 && "1".equals(isPageAlarm_arr[0])){
			isPageAlarm = "1";
		}
		
		String isSoundAlarm = "0";
		if(isSoundAlarm_arr!=null && isSoundAlarm_arr.length > 0 && "1".equals(isSoundAlarm_arr[0])){
			isSoundAlarm = "1";
		}
		
		String isPhoneAlarm = "0";
		if(isPhoneAlarm_arr!=null && isPhoneAlarm_arr.length > 0 && "1".equals(isPhoneAlarm_arr[0])){
			isPhoneAlarm = "1";
		}
		
		String isMailAlarm = "0";
		if(isMailAlarm_arr!=null && isMailAlarm_arr.length > 0 && "1".equals(isMailAlarm_arr[0])){
			isMailAlarm = "1";
		}
		
		String isSMSAlarm = "0";
		if(isSMSAlarm_arr!=null && isSMSAlarm_arr.length > 0 && "1".equals(isSMSAlarm_arr[0])){
			isSMSAlarm = "1";
		}
		
		String isDesktopAlarm = "0";
		if(isDesktopAlarm_arr!=null && isDesktopAlarm_arr.length > 0 && "1".equals(isDesktopAlarm_arr[0])){
			isDesktopAlarm = "1";
		}
		AlarmWay alarmWay = new AlarmWay();
		alarmWay.setName(name);
		alarmWay.setDescription(description);
		alarmWay.setIsDefault(isDefault);
		alarmWay.setIsPageAlarm(isPageAlarm);
		alarmWay.setIsSoundAlarm(isSoundAlarm);
		alarmWay.setIsPhoneAlarm(isPhoneAlarm);
		alarmWay.setIsSMSAlarm(isSMSAlarm);
		alarmWay.setIsMailAlarm(isMailAlarm);
		alarmWay.setIsDesktopAlarm(isDesktopAlarm);
		
		return alarmWay;
	}
	
	
	public List createAlarmWayDetailList(){
		List alarmWayDetailList = new ArrayList(); 
		int num = 0;
		if(request.getParameter("rowNum")!=null && request.getParameter("rowNum").trim().length() > 0 ){
			num = Integer.parseInt(request.getParameter("rowNum"));
		}
		
		System.out.println(num);
		 
		for (int i = 1; i <= num; i++) {
   			String partName = "";
   			if (i < 10) {
   				partName = "0" + String.valueOf(i);
   			} else {
   				partName = String.valueOf(i);
   			}
   			
			String alarmCategory = request.getParameter("category-" + partName);
			String dateType = request.getParameter("dateType-" + partName);
			String sendTimes = request.getParameter("sendTimes-" + partName);
			String startDate = request.getParameter("startDate-" + partName);
			String endDate = request.getParameter("endDate-" + partName);
			String startTime = request.getParameter("startTime-" + partName);
			String endTime = request.getParameter("endTime-" + partName);
			String userIds = request.getParameter("userIds-" + partName);
			AlarmWayDetail alarmWayDetail = new AlarmWayDetail();
			alarmWayDetail.setAlarmCategory(alarmCategory);
			alarmWayDetail.setDateType(dateType);
			alarmWayDetail.setSendTimes(sendTimes);
			alarmWayDetail.setStartDate(startDate);
			alarmWayDetail.setEndDate(endDate);
			alarmWayDetail.setStartTime(startTime);
			alarmWayDetail.setEndTime(endTime);
			alarmWayDetail.setUserIds(userIds);
			alarmWayDetailList.add(alarmWayDetail);
   		}
		return alarmWayDetailList;
		
	}
	
	
	
}