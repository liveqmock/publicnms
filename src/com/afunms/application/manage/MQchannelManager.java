/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.text.*;

import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.common.base.*;
import com.afunms.common.*;
import com.afunms.common.util.*;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.EventListDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.BaseTask;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.TaskXml;
import com.afunms.polling.loader.*;
import com.afunms.application.model.*;
import com.afunms.system.model.User;
import com.afunms.topology.util.*;
import com.afunms.application.dao.*;
import com.afunms.application.util.DBPool;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.polling.impl.*;
import com.afunms.polling.api.*;

import com.afunms.report.jfree.ChartCreator;


import org.jfree.data.general.DefaultPieDataset;

public class MQchannelManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
//		List ips = new ArrayList();
//		User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//		String bids = operator.getBusinessids();
//		String bid[] = bids.split(",");
//		Vector rbids = new Vector();
//		if(bid != null && bid.length>0){
//			for(int i=0;i<bid.length;i++){
//				if(bid[i] != null && bid[i].trim().length()>0)
//					rbids.add(bid[i].trim());
//			}
//		}
		String ipaddress=getParaValue("ipaddress");
		MQchannelConfigDao configdao = new MQchannelConfigDao();
		List list = null;
		String sqlWhere="";
		if(ipaddress!=null)sqlWhere=" where ipaddress='"+ipaddress+"'";
		try{
			list = configdao.findByCondition(sqlWhere);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/mq/mqchannellist.jsp";
	}
	private String channelList()
	{

		String ipaddress=getParaValue("ipaddress");
		MQchannelConfigDao configdao = new MQchannelConfigDao();
		List list = null;
		String sqlWhere="";
		if(ipaddress!=null)sqlWhere=" where ipaddress='"+ipaddress+"'";
		try{
			list = configdao.findByCondition(sqlWhere);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/mq/mqchannellist.jsp";
	}
	private String createmqconfig()
	{
		MQchannelConfigDao configdao = new MQchannelConfigDao();
		List list = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try {
			//createxmlfile();
			HttpSession session = request.getSession();
			configdao.fromLastToMqChannelconfig();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		return "/mqchannel.do?action=list&jp=1";
	}

	private String add()
    {    	   
		//MQchannelConfigDao dao=new MQchannelConfigDao();
		MQchannelConfig vo=new MQchannelConfig();
		
		String ipaddress=getParaValue("ipaddress");
		String chlname=getParaValue("chlname");
		int reportflag=getParaIntValue("reportflag");
		String connipaddress=getParaValue("connipaddress");
		String bak=getParaValue("bak");

		vo.setIpaddress(ipaddress);
		vo.setChlname(chlname);
		vo.setReportflag(reportflag);
		vo.setConnipaddress(connipaddress);
		vo.setBak(bak);
		MQchannelConfigDao configdao = new MQchannelConfigDao();
        try{
        	configdao.save(vo);	
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	configdao.close();
        }
        configdao = new MQchannelConfigDao();
		List list = null;
		try{
			list = configdao.loadAll();
		}catch(Exception e){
        	e.printStackTrace();
        }finally{
        	configdao.close();
        }
		request.setAttribute("list",list);
		return "/application/mq/mqchannellist.jsp";
    }    
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		//WasConfig vo = new WasConfig();
		List list = new ArrayList();
		
    	if(ids != null && ids.length > 0){	
    		MQchannelConfigDao configdao = new MQchannelConfigDao();
    		try{
    			configdao.delete(ids);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			configdao.close();
    		}
    	}
		try{
			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if(bid != null && bid.length>0){
				for(int i=0;i<bid.length;i++){
					if(bid[i] != null && bid[i].trim().length()>0)
						rbids.add(bid[i].trim());
				}
			}
			MQchannelConfigDao configdao = new MQchannelConfigDao();
			try{
				list = configdao.getMqChanelByBID(rbids);
			}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			configdao.close();
    		}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("list",list);
		return list();
	}
	
	private String update()
    {    	   
		
		MQchannelConfig vo=new MQchannelConfig();	
		List list = new ArrayList();
		
		int id=getParaIntValue("id");
		String ipaddress=getParaValue("ipaddress");
		String chlname=getParaValue("chlname");
		int reportflag=getParaIntValue("reportflag");
		String connipaddress=getParaValue("connipaddress");
		String bak=getParaValue("bak");

		
		vo.setId(id);
		vo.setIpaddress(ipaddress);
		vo.setChlname(chlname);
		vo.setReportflag(reportflag);
		vo.setConnipaddress(connipaddress);
		vo.setBak(bak);
        //WebConfigDao configdao = new WebConfigDao();
		MQchannelConfigDao configdao=new MQchannelConfigDao();
        try{
        	configdao.update(vo);	
        	configdao=new MQchannelConfigDao();
			list = configdao.loadAll();
			
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	configdao.close();
        }
		request.setAttribute("list",list);
             
		return "/application/mq/mqchannellist.jsp";
    }
	   
	private String search()
    {    	   
//		DBVo vo = new DBVo();
//		DBDao dao = new DBDao();
//		SybspaceconfigDao configdao = new SybspaceconfigDao();
//		List list = new ArrayList();
//		List conflist = new ArrayList();
//		List ips = new ArrayList();
//		String ipaddress ="";
//
//		try{
//			ipaddress = getParaValue("ipaddress");
//			User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//			String bids = operator.getBusinessids();
//			String bid[] = bids.split(",");
//			Vector rbids = new Vector();
//			if(bid != null && bid.length>0){
//				for(int i=0;i<bid.length;i++){
//					if(bid[i] != null && bid[i].trim().length()>0)
//						rbids.add(bid[i].trim());
//				}
//			}
//
//			List oraList = new ArrayList();
//			DBTypeDao typedao = new DBTypeDao();
//			DBTypeVo typevo = (DBTypeVo)typedao.findByDbtype("sybase");
//			try{
//				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//			if(oraList != null && oraList.size()>0){
//				for(int i=0;i<oraList.size();i++){
//					DBVo dbmonitorlist = (DBVo)oraList.get(i);
//					ips.add(dbmonitorlist.getIpAddress());
//				}
//			}
//			
//			
//			configdao = new SybspaceconfigDao();
//			//configdao.fromLastToOraspaceconfig();
//			
//			//ipaddress = (String)session.getAttribute("ipaddress");			
//			if (ipaddress != null && ipaddress.trim().length()>0){
//				configdao = new SybspaceconfigDao();
//				list =configdao.getByIp(ipaddress);
//				if (list == null || list.size() == 0){
//					list = configdao.loadAll();
//				}
//			}else{
//				configdao = new SybspaceconfigDao();
//				list = configdao.loadAll();		
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//request.setAttribute("Oraspaceconfiglist", conflist);
//		request.setAttribute("iplist",ips);
//		request.setAttribute("ipaddress",ipaddress);
//		configdao = new SybspaceconfigDao();
//		list = configdao.getByIp(ipaddress);
//		request.setAttribute("list",list);
		return "/application/db/sybaseconfigsearchlist.jsp";
    }
	
	private String addalert()
    {    
		MQchannelConfig vo = new MQchannelConfig();
		MQchannelConfigDao configdao = new MQchannelConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (MQchannelConfig)configdao.findByID(getParaValue("id"));
			vo.setReportflag(1);
			configdao = new MQchannelConfigDao();
			configdao.update(vo);	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		configdao = new MQchannelConfigDao();
		try{
			list = configdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/mq/mqchannellist.jsp";
    }
	
	private String cancelalert()
    {    
		MQchannelConfig vo = new MQchannelConfig();
		MQchannelConfigDao configdao = new MQchannelConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (MQchannelConfig)configdao.findByID(getParaValue("id"));
			vo.setReportflag(0);
			configdao = new MQchannelConfigDao();
			configdao.update(vo);	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		configdao = new MQchannelConfigDao();
		try{
			list = configdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/mq/mqchannellist.jsp";
    }
	
	private String detail()
    {    
/*		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MQchannelConfigDao configdao = new MQchannelConfigDao();
		Urlmonitor_realtimeDao realtimedao = new Urlmonitor_realtimeDao();
		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
		List urllist = new ArrayList(); // 用做条件选择列表
		MQchannelConfig initconf = new MQchannelConfig(); // 当前的对象
		String lasttime = "";
		String nexttime = "";
		String conn_name = "";
		String valid_name = "";
		String fresh_name = "";
		String wave_name = "";
		String delay_name = "";
		String connrate = "0";
		String validrate = "0";
		String freshrate = "0";
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Date nowdate = new Date();
		nowdate.getHours();
		String from_date1 = getParaValue("from_date1");
		if (from_date1 == null) {
			from_date1 = timeFormatter.format(new Date());
			request.setAttribute("from_date1", from_date1);
		}
		String to_date1 = getParaValue("to_date1");
		if (to_date1 == null) {
			to_date1 = timeFormatter.format(new Date());
			request.setAttribute("to_date1", to_date1);
		}
		String from_hour = getParaValue("from_hour");
		if (from_hour == null) {
			from_hour = "00";
			request.setAttribute("from_hour", from_hour);
		}
		String to_hour = getParaValue("to_hour");
		if (to_hour == null) {
			to_hour = nowdate.getHours() + "";
			request.setAttribute("to_hour", to_hour);
		}
		String starttime = from_date1 + " " + from_hour + ":00:00";
		String totime = to_date1 + " " + to_hour + ":59:59";
		int flag = 0;
		try {
			User operator = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			configdao = new MQchannelConfigDao();
			try {
				urllist = configdao.getMqChanelByBID(rbids);
			} catch (Exception e) {

			}

			Integer queryid = getParaIntValue("id");// .getUrl_id();

			if (urllist.size() > 0 && queryid == null) {
				Object obj = urllist.get(0);
			}
			if (queryid != null) {
				// 如果是链接过来则取用查询条件
				configdao = new MQchannelConfigDao();
				initconf = (MQchannelConfig) configdao.findByID(queryid + "");
			}
			queryid = initconf.getId();
			conn_name = queryid + "urlmonitor-conn";
			valid_name = queryid + "urlmonitor-valid";
			fresh_name = queryid + "urlmonitor-refresh";
			wave_name = queryid + "urlmonitor-rec";
			delay_name = queryid + "urlmonitor-delay";

			List urlList = realtimedao.getByUrlId(queryid);

			Calendar last = null;
			if (urlList != null && urlList.size() > 0) {
				last = ((Urlmonitor_realtime) urlList.get(0)).getMon_time();
			}
			int interval = 0;
			// TaskXmlUtil taskutil = new TaskXmlUtil("urltask");
			try {
				// Session session = this.beginTransaction();
				List numList = new ArrayList();
				TaskXml taskxml = new TaskXml();
				numList = taskxml.ListXml();
				for (int i = 0; i < numList.size(); i++) {
					Task task = new Task();
					BeanUtils.copyProperties(task, numList.get(i));
					if (task.getTaskname().equals("urltask")) {
						interval = task.getPolltime().intValue();
						// numThreads = task.getPolltime().intValue();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			DateE de = new DateE();
			if (last == null) {
				last = new GregorianCalendar();
				flag = 1;
			}
			lasttime = de.getDateDetail(last);
			last.add(Calendar.MINUTE, interval);
			nexttime = de.getDateDetail(last);

			int hour = 1;
			if (getParaValue("hour") != null) {
				hour = Integer.parseInt(getParaValue("hour"));
			} else {
				request.setAttribute("hour", "1");
				// urlconfForm.setHour("1");
			}

			InitCoordinate initer = new InitCoordinate(new GregorianCalendar(),
					hour, 1);
			// Minute[] minutes=initer.getMinutes();
			TimeSeries ss1 = new TimeSeries("", Minute.class);
			TimeSeries ss2 = new TimeSeries("", Minute.class);

			// ss.add()
			TimeSeries[] s = new TimeSeries[1];
			TimeSeries[] s_ = new TimeSeries[1];
			// Vector wave_v = historyManager.getInfo(queryid,initer);
			Vector wave_v = historydao
					.getByUrlid(queryid, starttime, totime, 0);

			for (int i = 0; i < wave_v.size(); i++) {
				Hashtable ht = (Hashtable) wave_v.get(i);
				double conn = Double.parseDouble(ht.get("conn").toString());
				double fresh = Double.parseDouble(ht.get("refresh").toString());
				double condelay = Double.parseDouble(ht.get("condelay")
						.toString());
				String time = ht.get("mon_time").toString();
				ss1.addOrUpdate(new Minute(sdf1.parse(time)), conn);
				ss2.addOrUpdate(new Minute(sdf1.parse(time)), condelay);
			}
			s[0] = ss1;
			s_[0] = ss2;
			ChartGraph cg = new ChartGraph();
			cg.timewave(s, "时间", "连通", "", wave_name, 600, 120);
			cg.timewave(s_, "时间", "时延(ms)", "", delay_name, 600, 120);
			// p_draw_line(s,"","url"+queryid+"ConnectUtilization",740,120);

			// 是否连通
			String conn[] = new String[2];
			if (flag == 0)
				// conn =
				// historyManager.getAvailability(queryid,initer,"is_canconnected");
				conn = historydao.getAvailability(queryid, starttime, totime,
						"is_canconnected");
			else {
				// conn[0] = "0";
				// conn[1] = "0";
				// conn =
				// historyManager.getAvailability(queryid,initer,"is_canconnected");
				conn = historydao.getAvailability(queryid, starttime, totime,
						"is_canconnected");
			}
			String[] key1 = { "连通", "未连通" };
			drawPiechart(key1, conn, "", conn_name);
			// drawchart(minutes,"连通",)
			// Vector conn_v =
			// historyManager.getInfo(queryid,initer,"is_canconnected");
			// for(int i=0;i<conn)
			if (flag == 0)
				connrate = getF(String.valueOf(Float.parseFloat(conn[0])
						/ (Float.parseFloat(conn[0]) + Float
								.parseFloat(conn[1])) * 100))
						+ "%";
			// 是否有效
			String avail[] = new String[2];
			if (flag == 0)
				avail = historydao.getAvailability(queryid, initer, "is_valid");
			else {
				avail[0] = "0";
				avail[1] = "0";
			}
			String[] key2 = { "有效", "无效" };
			drawPiechart(key2, avail, "页面有效情况", valid_name);
			if (flag == 0)
				validrate = getF(String.valueOf(Float.parseFloat(avail[0])
						/ (Float.parseFloat(avail[0]) + Float
								.parseFloat(avail[1])) * 100))
						+ "%";

			// 是否刷新
			String refresh[] = new String[2];
			if (flag == 0)
				refresh = historydao.getAvailability(queryid, initer,
						"is_refresh");
			else {
				refresh[0] = "0";
				refresh[1] = "0";
			}

			String[] key3 = { "刷新", "未刷新" };
			drawPiechart(key3, refresh, "页面刷新情况", fresh_name);
			if (flag == 0)
				freshrate = getF(String.valueOf(Float.parseFloat(refresh[0])
						/ (Float.parseFloat(refresh[0]) + Float
								.parseFloat(refresh[1])) * 100))
						+ "%";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("urllist", urllist);
		request.setAttribute("initconf", initconf);
		request.setAttribute("lasttime", lasttime);
		request.setAttribute("nexttime", nexttime);
		request.setAttribute("conn_name", conn_name);
		request.setAttribute("valid_name", valid_name);
		request.setAttribute("fresh_name", fresh_name);
		request.setAttribute("wave_name", wave_name);
		request.setAttribute("delay_name", delay_name);
		request.setAttribute("connrate", connrate);
		request.setAttribute("validrate", validrate);
		request.setAttribute("freshrate", freshrate);

		request.setAttribute("from_date1", from_date1);
		request.setAttribute("to_date1", to_date1);

		request.setAttribute("from_hour", from_hour);
		request.setAttribute("to_hour", to_hour);*/
		return "/application/web/detail.jsp";
    }
	
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        
        if(action.equals("channelList"))
            return channelList();  
        if(action.equals("ready_add"))
        	return "/application/mq/mqchanneladd.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new MQchannelConfigDao();
    	    setTarget("/application/mq/mqchanneledit.jsp");
            return readyEdit(dao);
        }
        if(action.equals("update"))
            return update();
        if(action.equals("addalert"))
            return addalert();
        if(action.equals("cancelalert"))
            return cancelalert();
        if(action.equals("detail"))
            return detail();
        if(action.equals("search"))
            return search();
        if(action.equals("create_mqconfig"))
        	return createmqconfig();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	private void drawPiechart(String[] keys,String[] values,String chname,String enname){
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for(int i=0;i<keys.length;i++){
		  piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname,piedata,enname,300,120);    	
	}
	
	private void drawchart(Minute[] minutes,String keys,String[][] values,String chname,String enname){
		try{
		//int size = keys.length;
		TimeSeries[] s2 = new TimeSeries[1];
		String[] keymemory = new String[1];
		String[] unitMemory = new String[1];
		//for(int i = 0 ; i < size ; i++){
			String key = keys;
		//	System.out.println("in drawchart -------------- i="+i+" key="+key+" ");
			TimeSeries ss2 = new TimeSeries(key,Minute.class);
			String[] hmemory = values[0];
			arrayTochart(ss2,hmemory,minutes);
			keymemory[0] = key;
			s2[0] = ss2;
	//		}
		ChartGraph cg = new ChartGraph();
		cg.timewave(s2,"x","y(MB)",chname,enname,300,150);    	
		}catch(Exception e){
		System.out.println("drawchart error:" + e);
	}
	}
	
	private void arrayTochart(TimeSeries s,String[] h,Minute[] minutes){
		try{
		for(int j=0;j<h.length;j++){
			//System.out.println("h[i]: " + h[j]);
			String value=h[j];
			Double v=new Double(0);
			if(value!=null)	 {
				v=new Double(value);
			}
			s.addOrUpdate(minutes[j],v);
			}
		}catch(Exception e){
			System.out.println("arraytochart error:" + e);
		}
	}
	public String getF(String s){
		if(s.length()>5)
			s=s.substring(0,5);
		return s;
	}
	
	private void p_draw_line(Hashtable hash,String title1,String title2,int w,int h){
		List list = (List)hash.get("list");
		try{
		if(list==null || list.size()==0){
			draw_blank(title1,title2,w,h);
		}
		else{
		String unit = (String)hash.get("unit");
		if (unit == null)unit="%";
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1,Minute.class);
		TimeSeries[] s = {ss};
		for(int j=0; j<list.size(); j++){
			//if (title1.equals("Cpu利用率")){
				Vector v = (Vector)list.get(j);
				//CPUcollectdata obj = (CPUcollectdata)list.get(j);
				Double	d=new Double((String)v.get(0));			
				String dt = (String)v.get(1);			
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time1 = sdf.parse(dt);				
				Calendar temp = Calendar.getInstance();
				temp.setTime(time1);
				Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
				ss.addOrUpdate(minute,d);
			//}
		}
		cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
		}
		hash = null;
		}
		catch(Exception e){e.printStackTrace();}
		}

	private void draw_blank(String title1,String title2,int w,int h){
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1,Minute.class);
		TimeSeries[] s = {ss};
		try{
			Calendar temp = Calendar.getInstance();
			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute,null);
			cg.timewave(s,"x(时间)","y",title1,title2,w,h);
		}
		catch(Exception e){e.printStackTrace();}
	}
	
}