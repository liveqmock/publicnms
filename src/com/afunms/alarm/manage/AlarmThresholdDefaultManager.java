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

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.alarm.dao.AlarmThresholdDao;
import com.afunms.alarm.dao.AlarmThresholdDefaultDao;
import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmThreshold;
import com.afunms.alarm.model.AlarmThresholdDefault;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;


public class AlarmThresholdDefaultManager extends BaseManager implements ManagerInterface{

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
		}
		return null;
	}

	private String delete() {
		// TODO Auto-generated method stub
		
		String ids[] = getParaArrayValue("checkbox");
		
		AlarmThresholdDefaultDao alarmThresholdDefaultDao = new AlarmThresholdDefaultDao();
		try {
			alarmThresholdDefaultDao.delete(ids);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmThresholdDefaultDao.close();
		}
		
		return list();
	}

	private String update() {
		// TODO Auto-generated method stub
		return null;
	}

	private String save() {
		// TODO Auto-generated method stub
		
		String indicatorsId = "";
		
		String type = getParaValue("type");
		
		String subtype = getParaValue("subtype");
		
		String indicators = getParaValue("indicators");
		
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		AlarmIndicators alarmIndicators = null;
		try {
//			alarmIndicators = alarmIndicatorsDao.getByTypeAndSubTypeAndName(type, subtype, indicators);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			alarmIndicatorsDao.close();
		}
		
		
		List list2 = new ArrayList();
		
		if(alarmIndicators!=null){
			indicatorsId = String.valueOf(alarmIndicators.getId());
			List list = null;
			AlarmThresholdDao alarmThresholdDao = new AlarmThresholdDao();
			try {
				list = alarmThresholdDao.getAlarmThresholdByIndicatorsId(indicatorsId);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			alarmThresholdDao.close();
			
			if(list !=null && list.size() > 0){
				for(int i = 0; i < list.size(); i++){
					System.out.println("====================" + list.size());
					
					AlarmThreshold alarmThreshold = (AlarmThreshold)list.get(i);
					String datatype = alarmThreshold.getDatatype();
					String level = alarmThreshold.getLevel();
					String alarmTimes = alarmThreshold.getAlarmTimes();
					String thresholdValue = alarmThreshold.getValue();
					String thresholdUnit = alarmThreshold.getUnit();
					String isAlarm = alarmThreshold.getIsAlarm();
					String isSendSMS = alarmThreshold.getIsSendSMS();
//					String description = alarmIndicators.getDescription();
					String bak = alarmThreshold.getBak();
					
					AlarmThresholdDefault alarmThresholdDefault = new AlarmThresholdDefault();
					alarmThresholdDefault.setIndicators(alarmIndicators.getName());
					alarmThresholdDefault.setType(type);
					alarmThresholdDefault.setSubtype(subtype);
					alarmThresholdDefault.setDatatype(datatype);
					alarmThresholdDefault.setLevel(level);
					alarmThresholdDefault.setAlarmTimes(alarmTimes);
					alarmThresholdDefault.setThresholdValue(thresholdValue);
					alarmThresholdDefault.setThresholdUnit(thresholdUnit);
					alarmThresholdDefault.setIsAlarm(isAlarm);
					alarmThresholdDefault.setIsSendSMS(isSendSMS);
//					alarmThresholdDefault.setDescription(description);
					alarmThresholdDefault.setBak(bak);
					
					list2.add(alarmThresholdDefault);
					
				}
			}
			
		}
		
		AlarmThresholdDefaultDao alarmThresholdDefaultDao = new AlarmThresholdDefaultDao();
		try {
			alarmThresholdDefaultDao.saveAlarmThresholdDefaultList(list2);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmThresholdDefaultDao.close();
		}
		
		
		return list();
	}

	private String add() {
		// TODO Auto-generated method stub
		
		
		return "/alarm/threshold/add.jsp";
	}

	private String edit() {
		// TODO Auto-generated method stub
		return null;
	}

	private String list() {
		// TODO Auto-generated method stub
		List list = getList();
		
		request.setAttribute("list", list);
		return "/alarm/threshold/defaultlist.jsp";
	}
	
	public List getList(){
		String where = getSQL();
		
		List list = new ArrayList();
		
		AlarmThresholdDefaultDao alarmThresholdDefaultDao = new AlarmThresholdDefaultDao();
		list(alarmThresholdDefaultDao, where);
		
		list = (List)request.getAttribute("list");
		return list;
	}
	
	
	public String getSQL(){
		
		String type = getParaValue("type");
		
		String typeSQL = getTypeSQL(type);
		
		String subtype = getParaValue("subtype");
		
		String subtypeSQL = getSubtypeSQL(subtype);
		
		String indicators = getParaValue("indicators");
		
		String indicatorsSQL = getIndicatorsSQL(indicators);
		
		String where = " where ";
		boolean isSearch = false;
		 
		if(typeSQL != null &&  typeSQL.trim().length() > 0){
			where += typeSQL;
			isSearch = true;
		}
		
		if(subtypeSQL != null &&  subtypeSQL.trim().length() > 0){
			if(isSearch){
				where += " and";
			}
			where += subtypeSQL;
			isSearch = true;
		}
		
		if(indicatorsSQL != null &&  indicatorsSQL.trim().length() > 0){
			if(isSearch){
				where += " and";
			}
			where += indicatorsSQL;
			isSearch = true;
		}
		if(!isSearch){
			where = "";
		}
		
		
		
		return where;
	}
	
	public String getTypeSQL(String type){
		String sql = "";
		
		if(type != null && type.trim().length()>0 && !"-1".equals(type)){
			sql = " type='" + type +  "'";
		}
		
		return sql;
	}
	
	public String getSubtypeSQL(String subtype){
		String sql = "";
		
		if(subtype != null && subtype.trim().length()>0 && !"-1".equals(subtype)){
			sql = " subtype='" + subtype +  "'";
		}
		
		return sql;
	}
	
	public String getIndicatorsSQL(String indicators){
		String sql = "";
		
		if(indicators != null && indicators.trim().length()>0 && !"-1".equals(indicators)){
			sql = " indicators='" + indicators +  "'";
		}
		
		return sql;
	}
	
	
	
}