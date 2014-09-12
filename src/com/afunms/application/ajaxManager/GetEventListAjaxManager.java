package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.system.model.User;
import com.afunms.system.vo.EventVo;

public class GetEventListAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("ajaxUpdate_eventflow")) {
			ajaxUpdate_eventflow();
		}
		if(action.equals("ajaxGetEventList"))
		{
			ajaxGetEventList(); 
		}
	}
	
	

	private void ajaxGetEventList() {
		try {
			EventListDao eventdao = new EventListDao();
			String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String timeFormat = "HH:mm:ss";
			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
			
			List<EventList> eventList = new ArrayList<EventList>();
			try {
				User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
				String businessid="";
				if(current_user!=null)
				 businessid=current_user.getBusinessids();
				eventList = eventdao.getQueryForEventList(startTime, endTime, businessid); //获取事件列表
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(eventdao != null){
					eventdao.close();
				}
			}
			
			if (eventList == null || eventList.size() == 0) {
				return ;
			}
			
			List<EventVo> eventListForMap = new ArrayList<EventVo>();
			for (int i = 0; i < eventList.size(); i++) {
				EventList event = (EventList) eventList.get(i);
				EventVo Vo = new EventVo();
				Vo.setNodeid(event.getNodeid());
				Vo.setContent(event.getContent());
				Vo.setLevel1(event.getLevel1()+"");
				Vo.setEventlocation(event.getEventlocation());
				Date d2 = event.getRecordtime().getTime();
				String time = timeFormatter.format(d2);
				Vo.setRecordtime(time);
				eventListForMap.add(Vo);
			}
			Map<String, List<EventVo>> map = new HashMap<String, List<EventVo>>();
			map.put("EventList", eventListForMap);
			JSONObject json = JSONObject.fromObject(map);
			out.print(json);
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	
	public void ajaxUpdate_eventflow() {
		ArrayList<EventVo> flexDataList = new ArrayList<EventVo>();
		List rpceventlist = new ArrayList();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		EventListDao eventdao = new EventListDao();
		String timeFormat = "HH:mm:ss";
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
		try {

			this.request.setCharacterEncoding("utf-8");
			int page = Integer.parseInt(request.getParameter("page"));
			int rp = Integer.parseInt(request.getParameter("rp"));
			int total = 50;

			StringBuilder json = new StringBuilder();
			json.append("{\n");
			json.append("page:" + page + ",\n");
			json.append("total:" + total + ",\n");
			json.append("rows:[");
			boolean rc = false;
			rpceventlist = eventdao.getQuery_flex(startTime, endTime, "99", "99", "-1", 99);
			if (rpceventlist != null && rpceventlist.size() > 0) {
				for (int i = 0; i < rpceventlist.size(); i++) {
					EventVo Vo = new EventVo();
					EventList event = (EventList) rpceventlist.get(i);
					Vo.setContent(event.getContent());
					Vo.setEventlocation(event.getEventlocation());
					Date d2 = event.getRecordtime().getTime();
					String time = timeFormatter.format(d2);
					Vo.setRecordtime(time);
					String level = String.valueOf(event.getLevel1());
					if ("0".equals(level)) {
						level = "提示信息";
					}
					if ("1".equals(level)) {
						level = "普通告警";
					}
					if ("2".equals(level)) {
						level = "严重告警";
					}
					if ("3".equals(level)) {
						level = "紧急告警";
					}
					Vo.setLevel1(level);
					Vo.setNodeid(event.getNodeid());
					//Vo.setLasttime(event.getLasttime().split(" ")[1]);
					flexDataList.add(Vo);
					if (rc)
						json.append(",");

					json.append("\n{");
					json.append("id:'");
					json.append(i);
					json.append("',");

					json.append("cell:['");
					json.append(level);
					json.append("'");

					json.append(",'");
					json.append(Vo.getEventlocation());
					json.append("'");

					json.append(",'");
					json.append(Vo.getContent());
					json.append("'");

					json.append(",'");
					json.append(Vo.getRecordtime());
//					json.append("'");
//
//					json.append(",'");
//					json.append(Vo.getLasttime());
					json.append("']");

					json.append("}");
					rc = true;

				}
			}
			json.append("]\n");
			json.append("}");
			System.out.println(json);
			this.response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventdao.close();
		}
	}
}
