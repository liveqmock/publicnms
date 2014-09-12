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
import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmThreshold;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.KnowledgebaseDao;


public class AlarmIndicatorsManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		
		if("list".equals(action)){
			KnowledgebaseDao dao=new KnowledgebaseDao();
			String alarmfindselect=dao.selectcontent();
			session.setAttribute("alarmfindselect", alarmfindselect);
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
		}else if("viewAlarmThreshold".equals(action)){
			return viewAlarmThreshold();
		}else if("saveAlarmThreshold".equals(action)){
			return saveAlarmThreshold();
		}else if("setDefaultValue".equals(action)){
			return setDefaultValue();
		}
		else if("find".equals(action)){
			return find();
		}
		return null;
	}
	
	public String list(){
		
		String jsp = "/alarm/indicators/list.jsp";
		
		try {
			List list = getList();
			
			request.setAttribute("list", list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return jsp;
	}
	
	public List getList(){
		//String sqlQuery = getSQLQueryForList();
		
		//添加了order by排序 HONGLI
		String sqlQuery = getSQLQueryForList()+" order by type,subtype";
		
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			list(alarmIndicatorsDao, sqlQuery);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsDao.close();
		}
		List list = (List)request.getAttribute("list");
		return list;
	}
	
	public String getSQLQueryForList(){
		
		return "";
	}
	
	public String add(){
		String jsp = "/alarm/indicators/add.jsp";
		return jsp;
	}
	
	public String edit(){
		String jsp = "/alarm/indicators/edit.jsp";
		String id = getParaValue("id");
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		AlarmIndicators alarmIndicators = null;
		try {
			alarmIndicators = (AlarmIndicators)alarmIndicatorsDao.findByID(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsDao.close();
		}
		request.setAttribute("alarmIndicators", alarmIndicators);
		return jsp;
	}
	
	public String update(){
		
		AlarmIndicators alarmIndicators = createAlarmIndicators();
		int id = getParaIntValue("id");
		alarmIndicators.setId(id);
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			alarmIndicatorsDao.update(alarmIndicators);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsDao.close();
		}
		
		AlarmIndicatorsDao dao=new AlarmIndicatorsDao();
		List uplist=dao.updatelist(id);
		String jsp=list();
		request.setAttribute("list", uplist);
		AlarmIndicatorsDao typedao=new AlarmIndicatorsDao();
		String type=typedao.type(id);
		AlarmIndicatorsDao subtypedao=new AlarmIndicatorsDao();
		String subtype=subtypedao.subtype(id);
		request.setAttribute("con1", type);
		request.setAttribute("con2", subtype);
		KnowledgebaseDao knowdao=new KnowledgebaseDao();
		String alarmfindselect=knowdao.selectcontent();
		session.setAttribute("alarmfindselect", alarmfindselect);
		JspPage jp = (JspPage)request.getAttribute("page");
		jp.setTotalPage(1);
		jp.setCurrentPage(1);
		jp.setMinNum(1);
		request.setAttribute("page", jp);
		return jsp;
	}
	
	public String save(){
		
		AlarmIndicators alarmIndicators = createAlarmIndicators();
		
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		
		//判断数据库中该条告警指标是否已经存在     HONGLI
		AlarmIndicatorsDao alarmao = new AlarmIndicatorsDao();
		AlarmIndicators alarm = alarmao.getAlarmIndicatorsByNameAndTypeAndSubType(alarmIndicators.getName(), alarmIndicators.getType(), alarmIndicators.getSubtype());
		if(alarm != null){//已存在，返回不保存
			SysLogger.info("#######HONG  AlarmIndicatorsManager--告警指标已存在，返回不保存");
			return list();
		}
		try {
			alarmIndicatorsDao.save(alarmIndicators);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsDao.close();
		}
		
		return list();
	}
	
	public String delete(){
		
		String ids[] = getParaArrayValue("checkbox");
		
		AlarmIndicators alarmIndicators = createAlarmIndicators();
		
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			alarmIndicatorsDao.delete(ids);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsDao.close();
		}
		
		return list();
	}
	
	
	public AlarmIndicators createAlarmIndicators(){
		String name = getParaValue("name");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String datatype = getParaValue("datatype");
		String moid = getParaValue("moid");
		int threshold = getParaIntValue("threshold");
		String threshold_unit = getParaValue("threshold_unit");
		int compare = getParaIntValue("compare");
		int compare_type = getParaIntValue("compare_type");
		String alarm_times = getParaValue("alarm_times");
		String alarm_info = getParaValue("alarm_info");
		String alarm_level = getParaValue("alarm_level");
		String enabled = getParaValue("enabled");
//		String poll_interval = getParaValue("poll_interval");
//		String interval_unit = getParaValue("interval_unit");
		String subentity = getParaValue("subentity");
		String limenvalue0 = getParaValue("limenvalue0");
		String limenvalue1 = getParaValue("limenvalue1");
		String limenvalue2 = getParaValue("limenvalue2");
		String time0 = getParaValue("time0");
		String time1 = getParaValue("time1");
		String time2 = getParaValue("time2");
		String sms0 = getParaValue("sms0");
		String sms1 = getParaValue("sms1");
		String sms2 = getParaValue("sms2");
		String category = getParaValue("category");
		String descr = getParaValue("descr");
		String unit = getParaValue("unit");
		
		threshold = 1;
		//compare = 1;
		compare_type = 1;
		
		AlarmIndicators alarmIndicators = new AlarmIndicators();
		alarmIndicators.setName(name);
		alarmIndicators.setType(type);
		alarmIndicators.setSubtype(subtype);
		alarmIndicators.setDatatype(datatype);
		alarmIndicators.setMoid(moid);
		alarmIndicators.setThreshlod(threshold);
		alarmIndicators.setThreshlod_unit(threshold_unit);
		alarmIndicators.setCompare(compare);
		alarmIndicators.setCompare_type(compare_type);
		alarmIndicators.setAlarm_times(alarm_times);
		alarmIndicators.setAlarm_info(alarm_info);
		alarmIndicators.setAlarm_level(alarm_level);
		alarmIndicators.setEnabled(enabled);
//		String[] interstr = poll_interval.split("-");
//		alarmIndicators.setPoll_interval(interstr[0]);
//		alarmIndicators.setInterval_unit(interstr[1]);
		alarmIndicators.setSubentity(subentity);
		alarmIndicators.setLimenvalue0(limenvalue0);
		alarmIndicators.setLimenvalue1(limenvalue1);
		alarmIndicators.setLimenvalue2(limenvalue2);
		alarmIndicators.setTime0(time0);
		alarmIndicators.setTime1(time1);
		alarmIndicators.setTime2(time2);
		alarmIndicators.setSms0(sms0);
		alarmIndicators.setSms1(sms1);
		alarmIndicators.setSms2(sms2);
		alarmIndicators.setCategory(category);
		alarmIndicators.setDescr(descr);
		alarmIndicators.setUnit(unit);
		return alarmIndicators;
	}
	
	public String viewAlarmThreshold(){
		
		String jsp = "/alarm/indicators/viewalarmthreshold.jsp";
		
		String indicatorsId = getParaValue("id");
		
		AlarmIndicators alarmIndicators = getAlarmIndicatorsById(indicatorsId);
		
		List alarmThresholdList = getAlarmThresholdListByIndicatorsId(indicatorsId);
		
		request.setAttribute("alarmIndicators", alarmIndicators);
		
		request.setAttribute("list", alarmThresholdList);
		
		request.setAttribute("indicatorsId", indicatorsId);
		return jsp;
	}
	
	public String saveAlarmThreshold(){
		String indicatorsId = getParaValue("indicatorsId");
//		System.out.println(indicatorsId);
		
		List alarmThresholdList = getAlarmThresholdList();
		
		AlarmThresholdDao alarmThresholdDao = new AlarmThresholdDao();
		try {
			alarmThresholdDao.saveAlarmThresholdList(indicatorsId, alarmThresholdList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmThresholdDao.close();
		}
		
		
		
		return list();
	}
	
	public List getAlarmThresholdListByIndicatorsId(String indicatorsId){
		AlarmThresholdDao alarmThresholdDao = new AlarmThresholdDao();
		
		List list = null;
		try {
			list = alarmThresholdDao.getAlarmThresholdByIndicatorsId(indicatorsId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmThresholdDao.close();
		}
		return list;
	}
	
	public AlarmIndicators getAlarmIndicatorsById(String id){
		AlarmIndicators alarmIndicators = null;
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			alarmIndicators = (AlarmIndicators)alarmIndicatorsDao.findByID(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsDao.close();
		}
		
		return alarmIndicators;
		
	}
	
	public List getAlarmThresholdList(){
		List alarmIndicatorsList = new ArrayList(); 
		int num = 0;
		if(request.getParameter("rowNum")!=null && request.getParameter("rowNum").trim().length() > 0 ){
			num = Integer.parseInt(request.getParameter("rowNum"));
		}
		
		String indicatorsId = request.getParameter("indicatorsId");
		if("".equals(indicatorsId) || null == indicatorsId){
			return alarmIndicatorsList;
		}
		
		System.out.println(num);
		 
		for (int i = 1; i <= num; i++) {
   			String partName = "";
   			if (i < 10) {
   				partName = "0" + String.valueOf(i);
   			} else {
   				partName = String.valueOf(i);
   			}
   			
			String datatype = request.getParameter("datatype" + partName);
			String level = request.getParameter("level" + partName);
			String alarmTimes = request.getParameter("alarmTimes" + partName);
			String value = request.getParameter("value" + partName);
			String unit = request.getParameter("unit" + partName);
			String isAlarm = request.getParameter("isAlarm" + partName);
			String type = request.getParameter("type" + partName);
			String isSendSMS = request.getParameter("isSendSMS" + partName);
			if(datatype == null || datatype.trim().length() == 0){
				continue;
			}
			
			if(level == null || level.trim().length() == 0){
				continue;
			}
			
			if(value == null || value.trim().length() == 0){
				continue;
			}
			
			AlarmThreshold alarmThreshold = new AlarmThreshold();
			alarmThreshold.setDatatype(datatype);
			alarmThreshold.setLevel(level);
			alarmThreshold.setAlarmTimes(alarmTimes);
			alarmThreshold.setValue(value);
			alarmThreshold.setUnit(unit);
			alarmThreshold.setIsAlarm(isAlarm);
			alarmThreshold.setType(type);
			alarmThreshold.setIndicatorsId(indicatorsId);
			alarmIndicatorsList.add(alarmThreshold);
   		}
		return alarmIndicatorsList;
		
	}
	
	public String find(){
		
		AlarmIndicatorsDao dao=new AlarmIndicatorsDao();
		String con1=getParaValue("categorycon");
		request.setAttribute("con1",con1 );
		String con2=getParaValue("entitycon");
		request.setAttribute("con2",con2 );
		List alarmfindlist=dao.alarmfind(con1, con2);
		setTarget("/alarm/indicators/list.jsp");
		KnowledgebaseDao dao2=new KnowledgebaseDao();
		String page=list(dao2);
		request.setAttribute("list", alarmfindlist);
		JspPage jp = (JspPage)request.getAttribute("page");
		jp.setTotalPage(1);
		jp.setCurrentPage(1);
		jp.setMinNum(1);
		request.setAttribute("page", jp);
		return page;
	}
	public String setDefaultValue(){
		
		return null;
	}
	
	
	
	
}