/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2009-10-29
 */

package com.afunms.application.manage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.FtpHistoryDao;
import com.afunms.application.dao.FtpRealTimeDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.Urlmonitor_historyDao;
import com.afunms.application.dao.Urlmonitor_realtimeDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.dao.WebLoginConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.FtpRealTime;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.Urlmonitor_realtime;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.webloginConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.InitCoordinate;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Business;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.DominoLoader;
import com.afunms.polling.loader.FtpLoader;
import com.afunms.polling.loader.WebLoader;
import com.afunms.polling.loader.WebLoginLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.WebLogin;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.FTPDataCollector;
import com.afunms.polling.task.FtpUtil;
import com.afunms.polling.task.TaskXml;
import com.afunms.polling.task.WebLoginDataCollector;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;

public class WebLoginManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		} else if (action.equals("ready_add")) {
			return ready_add();
		} else if (action.equals("add")) {
			return add();
		} else if (action.equals("delete")) {
			return delete();
		} else if (action.equals("ready_edit")) {
			return readyEdit();
		} else if (action.equals("update")) {
			return update();
		} else if (action.equals("detail")) {
			return detail();
		} else if (action.equals("_detail")) {
			return _detail();
		} else if (action.equals("sychronizeData")) {
			return sychronizeData();
		} else if (action.equals("changeMonflag")) {
			return changeMonflag();
		} else if (action.equals("allservice")) {
			return allServiceList();
		} else if (action.equals("midalllist")) {
			return midalllist();

		} else if (action.equals("showPingReport")) {
			return showPingReport();

		} else if (action.equals("showCompositeReport")) {
			return showCompositeReport();

		} else if (action.equals("showServiceEventReport")) {
			return showServiceEventReport();

		}
		if (action.equals("isOK"))
			return isOK();
		if (action.equals("alarm")) {
			return alarm();
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String alarm() {
		detail();
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {

			tmp = request.getParameter("id");
			request.setAttribute("id", Integer.parseInt(tmp));
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				 //SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao dao = new EventListDao();
				try{
					list = dao.getQuery(starttime1, totime1, status + "",
							level1 + "", vo.getBusinessids(), Integer
							.parseInt(tmp), "weblogin");
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dao.close();
				}
								
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/webLoginFile/alarm.jsp";
	}

	

	private String detail()
    {    
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List urllist = new ArrayList();      //用做条件选择列表
		webloginConfig initconf = new webloginConfig();    //当前的对象
        String lasttime="";
        String nexttime="";
        String conn_name="";
        String valid_name = "";
        String fresh_name = "";
        String wave_name = "";
        String delay_name = "";
        String connrate="0";
        String validrate="0";
        String freshrate="0";
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        Date nowdate = new Date();
        nowdate.getHours();
        String from_date1 = getParaValue("from_date1");
        if (from_date1 == null){
        	from_date1 = timeFormatter.format(new Date());
        	request.setAttribute("from_date1", from_date1);
        }
        String to_date1 = getParaValue("to_date1");
        if (to_date1 == null){
        	to_date1 = timeFormatter.format(new Date());
        	request.setAttribute("to_date1", to_date1);
        }
        String from_hour = getParaValue("from_hour");
        if (from_hour == null){
        	from_hour = "00";
        	request.setAttribute("from_hour", from_hour);
        }
        String to_hour = getParaValue("to_hour");
        if(to_hour == null){
        	to_hour = nowdate.getHours()+"";            	            
        	request.setAttribute("to_hour", to_hour);
        }
        String starttime = from_date1+" "+from_hour+":00:00";
        String totime = to_date1+" "+to_hour+":59:59";            
        int flag = 0;
        try {
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
			WebLoginConfigDao configdao = new WebLoginConfigDao();
			try{
				urllist = configdao.getWebLoginByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
        	Integer queryid = getParaIntValue("id");//.getUrl_id();
        	String id=getParaValue("id");
        	System.out.println(";;;;"+id);
        	request.setAttribute("id", queryid);
        	if(urllist.size()>0&&queryid==null){
        		Object obj = urllist.get(0);           		
        	}
        	if(queryid!=null){					
        		//如果是链接过来则取用查询条件
        		configdao = new WebLoginConfigDao();
        		try{
        			initconf = (webloginConfig)configdao.findByID(queryid+"");
        		}catch(Exception e){
    				e.printStackTrace();
    			}finally{
    				configdao.close();
    			}
        	}
        	if(initconf != null)
        		queryid = initconf.getId();
        	 conn_name=queryid+"urlmonitor-conn";
             valid_name =queryid+"urlmonitor-valid";
             fresh_name =queryid+"urlmonitor-refresh";
             wave_name = queryid+"urlmonitor-rec";
             delay_name = queryid+"urlmonitor-delay";
             List urlList = new ArrayList();
        	Calendar last = null;
        	int interval = 0;
        	try {
    			List numList = new ArrayList();
    			NodeGatherIndicatorsUtil util=new NodeGatherIndicatorsUtil();
    			List list=util.getGatherIndicatorsForNode(id,"service","weblogin","1");
    			if(list!=null){
    				for (int i = 0; i < list.size(); i++) {
    					NodeGatherIndicators indicators=(NodeGatherIndicators) list.get(i);
    					if(indicators!=null){
    						if("ping".equals(indicators.getName())){
    							interval=Integer.parseInt(indicators.getPoll_interval());
    							break;
    						}
    					}
					}
    				
    				
    			}
    		
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	DateE de=new DateE();
        	if (last == null){
        		last = new GregorianCalendar();
        		flag=1;
        	}
        	lasttime = de.getDateDetail(last);
        	last.add(Calendar.MINUTE,interval);
        	nexttime = de.getDateDetail(last);
        	int hour=1;
        	if(getParaValue("hour")!=null){
        		 hour = Integer.parseInt(getParaValue("hour"));
        	}else{
        		request.setAttribute("hour", "1");
        	}
        	
        	InitCoordinate initer=new InitCoordinate(new GregorianCalendar(),hour,1);
        	TimeSeries ss1 = new TimeSeries("", Minute.class); 
        	TimeSeries ss2 = new TimeSeries("", Minute.class); 
        	
        	TimeSeries[] s = new TimeSeries[1];
        	TimeSeries[] s_ = new TimeSeries[1];
        	configdao = new WebLoginConfigDao();
        	Vector wave_v = new Vector();
        	try{
        	 }catch(Exception e){
  				e.printStackTrace();
  			}finally{
  				configdao.close();
  			}
        	
        	double curping=0;
        	double tempping=0;
        	double minping=0;
        	double tempdelay=0;
        	double curdelay=0;
        	double maxdelay=0;
        	double avgdelay=0;
        	double sumdelay=0;
        	request.setAttribute("curping", curping*100);
        	request.setAttribute("minping", minping*100);
        	request.setAttribute("curdelay", curdelay);
        	request.setAttribute("maxdelay", maxdelay);
        	request.setAttribute("avgdelay", avgdelay);
        	s[0] = ss1;
        	s_[0] = ss2;
        	//是否连通
        	String conn[] = new String[2];
        	if (flag == 0){
        		//conn = historyManager.getAvailability(queryid,initer,"is_canconnected");
        		configdao = new WebLoginConfigDao();
        		try{
//        			conn = configdao.getAvailability(queryid,starttime,totime,"is_connected");
        		 }catch(Exception e){
       				e.printStackTrace();
       			}finally{
       				configdao.close();
       			}
        	}else{
        		configdao = new WebLoginConfigDao();
        		try{
//        			conn = configdao.getAvailability(queryid,starttime,totime,"is_connected");
        		 }catch(Exception e){
       				e.printStackTrace();
       			}finally{
       				configdao.close();
       			}
        	}
        	String[] key1 = {"连通","未连通"};
//        	drawPiechart(key1,conn,"",conn_name);
        	//drawchart(minutes,"连通",)
//        	Vector conn_v = historyManager.getInfo(queryid,initer,"is_canconnected");
//        	for(int i=0;i<conn)
//        	if (flag == 0)
//        	connrate = getF(String.valueOf(Float.parseFloat(conn[0])/(Float.parseFloat(conn[0])+Float.parseFloat(conn[1]))*100))+"%";
        	//是否有效
//        	String avail[] = new String[2];
//        	if (flag == 0){
//        		configdao = new WebLoginConfigDao();
//        		try{
//        			avail = historydao.getAvailability(queryid,initer,"is_valid");
//        		}catch(Exception e){
//       				e.printStackTrace();
//       			}finally{
//       				historydao.close();
//       			}
//        	}else{
//        		avail[0] = "0";
//        		avail[1] = "0";
//        	}
//        	String[] key2 = {"有效","无效"};
//        	drawPiechart(key2,avail,"页面有效情况",valid_name);
//        	if (flag == 0)
//        	validrate = getF(String.valueOf(Float.parseFloat(avail[0])/(Float.parseFloat(avail[0])+Float.parseFloat(avail[1]))*100))+"%";
//
//        	//            	是否刷新
//        	String refresh[] = new String[2];
//        	if (flag == 0){
//        		historydao = new Urlmonitor_historyDao();
//        		try{
//        			refresh = historydao.getAvailability(queryid,initer,"is_refresh");
//        		}catch(Exception e){
//       				e.printStackTrace();
//       			}finally{
//       				historydao.close();
//       			}
//        	}else{
//        		refresh[0] = "0";
//        		refresh[1] = "0";
//        	}
//      
//        	String[] key3 = {"刷新","未刷新"};
//        	drawPiechart(key3,refresh,"页面刷新情况",fresh_name);
//        	if (flag == 0)
//        	freshrate = getF(String.valueOf(Float.parseFloat(refresh[0])/(Float.parseFloat(refresh[0])+Float.parseFloat(refresh[1]))*100))+"%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("urllist",urllist);
        request.setAttribute("initconf",initconf);
        request.setAttribute("lasttime",lasttime);
        request.setAttribute("nexttime",nexttime);
        request.setAttribute("conn_name",conn_name);
        request.setAttribute("valid_name",valid_name);
        request.setAttribute("fresh_name",fresh_name);
        request.setAttribute("wave_name",wave_name);
        request.setAttribute("delay_name",delay_name);
        request.setAttribute("connrate",connrate);
        request.setAttribute("validrate",validrate);
        request.setAttribute("freshrate",freshrate);
        
        request.setAttribute("from_date1",from_date1);
        request.setAttribute("to_date1",to_date1);
        
        request.setAttribute("from_hour",from_hour);
        request.setAttribute("to_hour",to_hour);
		return "/application/webLoginFile/detail.jsp";
    }
	
	
	
	private String _detail()
    {    
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List urllist = new ArrayList();      //用做条件选择列表
		webloginConfig initconf = new webloginConfig();    //当前的对象
        String lasttime="";
        String nexttime="";
        String conn_name="";
        String valid_name = "";
        String fresh_name = "";
        String wave_name = "";
        String delay_name = "";
        String connrate="0";
        String validrate="0";
        String freshrate="0";
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        Date nowdate = new Date();
        nowdate.getHours();
        String from_date1 = getParaValue("from_date1");
        if (from_date1 == null){
        	from_date1 = timeFormatter.format(new Date());
        	request.setAttribute("from_date1", from_date1);
        }
        String to_date1 = getParaValue("to_date1");
        if (to_date1 == null){
        	to_date1 = timeFormatter.format(new Date());
        	request.setAttribute("to_date1", to_date1);
        }
        String from_hour = getParaValue("from_hour");
        if (from_hour == null){
        	from_hour = "00";
        	request.setAttribute("from_hour", from_hour);
        }
        String to_hour = getParaValue("to_hour");
        if(to_hour == null){
        	to_hour = nowdate.getHours()+"";            	            
        	request.setAttribute("to_hour", to_hour);
        }
        String starttime = from_date1+" "+from_hour+":00:00";
        String totime = to_date1+" "+to_hour+":59:59";            
        int flag = 0;
        try {
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
			WebLoginConfigDao configdao = new WebLoginConfigDao();
			try{
				urllist = configdao.getWebLoginByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
        	Integer queryid = getParaIntValue("id");//.getUrl_id();
        	String id=getParaValue("id");
        	System.out.println(";;;;"+id);
        	request.setAttribute("id", queryid);
        	if(urllist.size()>0&&queryid==null){
        		Object obj = urllist.get(0);           		
        	}
        	if(queryid!=null){					
        		//如果是链接过来则取用查询条件
        		configdao = new WebLoginConfigDao();
        		try{
        			initconf = (webloginConfig)configdao.findByID(queryid+"");
        		}catch(Exception e){
    				e.printStackTrace();
    			}finally{
    				configdao.close();
    			}
        	}
        	if(initconf != null)
        		queryid = initconf.getId();
        	 conn_name=queryid+"urlmonitor-conn";
             valid_name =queryid+"urlmonitor-valid";
             fresh_name =queryid+"urlmonitor-refresh";
             wave_name = queryid+"urlmonitor-rec";
             delay_name = queryid+"urlmonitor-delay";
             List urlList = new ArrayList();
        	Calendar last = null;
        	int interval = 0;
        	try {
    			List numList = new ArrayList();
    			NodeGatherIndicatorsUtil util=new NodeGatherIndicatorsUtil();
    			List list=util.getGatherIndicatorsForNode(id,"service","weblogin","1");
    			if(list!=null){
    				for (int i = 0; i < list.size(); i++) {
    					NodeGatherIndicators indicators=(NodeGatherIndicators) list.get(i);
    					if(indicators!=null){
    						if("weblogin".equals(indicators.getName())){
    							interval=Integer.parseInt(indicators.getPoll_interval());
    							break;
    						}
    					}
					}
    				
    				
    			}
    		
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	DateE de=new DateE();
        	if (last == null){
        		last = new GregorianCalendar();
        		flag=1;
        	}
        	lasttime = de.getDateDetail(last);
        	last.add(Calendar.MINUTE,interval);
        	nexttime = de.getDateDetail(last);
        	int hour=1;
        	if(getParaValue("hour")!=null){
        		 hour = Integer.parseInt(getParaValue("hour"));
        	}else{
        		request.setAttribute("hour", "1");
        	}
        	
        	InitCoordinate initer=new InitCoordinate(new GregorianCalendar(),hour,1);
        	TimeSeries ss1 = new TimeSeries("", Minute.class); 
        	TimeSeries ss2 = new TimeSeries("", Minute.class); 
        	
        	TimeSeries[] s = new TimeSeries[1];
        	TimeSeries[] s_ = new TimeSeries[1];
        	configdao = new WebLoginConfigDao();
        	Vector wave_v = new Vector();
        	try{
        	 }catch(Exception e){
  				e.printStackTrace();
  			}finally{
  				configdao.close();
  			}
        	
        	double curping=0;
        	double tempping=0;
        	double minping=0;
        	double tempdelay=0;
        	double curdelay=0;
        	double maxdelay=0;
        	double avgdelay=0;
        	double sumdelay=0;
        	request.setAttribute("curping", curping*100);
        	request.setAttribute("minping", minping*100);
        	request.setAttribute("curdelay", curdelay);
        	request.setAttribute("maxdelay", maxdelay);
        	request.setAttribute("avgdelay", avgdelay);
        	s[0] = ss1;
        	s_[0] = ss2;
        	//是否连通
        	String conn[] = new String[2];
        	if (flag == 0){
        		//conn = historyManager.getAvailability(queryid,initer,"is_canconnected");
        		configdao = new WebLoginConfigDao();
        		try{
//        			conn = configdao.getAvailability(queryid,starttime,totime,"is_connected");
        		 }catch(Exception e){
       				e.printStackTrace();
       			}finally{
       				configdao.close();
       			}
        	}else{
        		configdao = new WebLoginConfigDao();
        		try{
//        			conn = configdao.getAvailability(queryid,starttime,totime,"is_connected");
        		 }catch(Exception e){
       				e.printStackTrace();
       			}finally{
       				configdao.close();
       			}
        	}
        	String[] key1 = {"连通","未连通"};
//        	drawPiechart(key1,conn,"",conn_name);
        	//drawchart(minutes,"连通",)
//        	Vector conn_v = historyManager.getInfo(queryid,initer,"is_canconnected");
//        	for(int i=0;i<conn)
//        	if (flag == 0)
//        	connrate = getF(String.valueOf(Float.parseFloat(conn[0])/(Float.parseFloat(conn[0])+Float.parseFloat(conn[1]))*100))+"%";
        	//是否有效
//        	String avail[] = new String[2];
//        	if (flag == 0){
//        		configdao = new WebLoginConfigDao();
//        		try{
//        			avail = historydao.getAvailability(queryid,initer,"is_valid");
//        		}catch(Exception e){
//       				e.printStackTrace();
//       			}finally{
//       				historydao.close();
//       			}
//        	}else{
//        		avail[0] = "0";
//        		avail[1] = "0";
//        	}
//        	String[] key2 = {"有效","无效"};
//        	drawPiechart(key2,avail,"页面有效情况",valid_name);
//        	if (flag == 0)
//        	validrate = getF(String.valueOf(Float.parseFloat(avail[0])/(Float.parseFloat(avail[0])+Float.parseFloat(avail[1]))*100))+"%";
//
//        	//            	是否刷新
//        	String refresh[] = new String[2];
//        	if (flag == 0){
//        		historydao = new Urlmonitor_historyDao();
//        		try{
//        			refresh = historydao.getAvailability(queryid,initer,"is_refresh");
//        		}catch(Exception e){
//       				e.printStackTrace();
//       			}finally{
//       				historydao.close();
//       			}
//        	}else{
//        		refresh[0] = "0";
//        		refresh[1] = "0";
//        	}
//      
//        	String[] key3 = {"刷新","未刷新"};
//        	drawPiechart(key3,refresh,"页面刷新情况",fresh_name);
//        	if (flag == 0)
//        	freshrate = getF(String.valueOf(Float.parseFloat(refresh[0])/(Float.parseFloat(refresh[0])+Float.parseFloat(refresh[1]))*100))+"%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("urllist",urllist);
       // System.out.println("=======================initconf======================="+initconf);
        request.setAttribute("initconf",initconf);
        request.setAttribute("lasttime",lasttime);
        request.setAttribute("nexttime",nexttime);
        request.setAttribute("conn_name",conn_name);
        request.setAttribute("valid_name",valid_name);
        request.setAttribute("fresh_name",fresh_name);
        request.setAttribute("wave_name",wave_name);
        request.setAttribute("delay_name",delay_name);
        request.setAttribute("connrate",connrate);
        request.setAttribute("validrate",validrate);
        request.setAttribute("freshrate",freshrate);
        
        request.setAttribute("from_date1",from_date1);
        request.setAttribute("to_date1",to_date1);
        
        request.setAttribute("from_hour",from_hour);
        request.setAttribute("to_hour",to_hour);
		return "/application/webLoginFile/_detail.jsp";
    }
	
	
	
	
	
	
	private String showPingReport() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = getParaValue("startdate");
		Hashtable reporthash = new Hashtable();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");
		FTPConfig initconf = null;
		FTPConfigDao configdao = new FTPConfigDao();
		try {
			initconf = (FTPConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			FtpHistoryDao historydao = new FtpHistoryDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(
					queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
																			// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType,
					ConnectUtilizationhash, "连通率", newip + "pingConnect",
					740, 150);

			// 画图-----------------------------
			reporthash.put("servicename", initconf.getName());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "FTP");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/showPingReport.jsp";
	}

	private String showCompositeReport() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = getParaValue("startdate");
		Hashtable reporthash = new Hashtable();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");
		FTPConfig initconf = null;
		List<String> infoList=new ArrayList<String>();
		FTPConfigDao configdao = new FTPConfigDao();
		try {
			initconf = (FTPConfig) configdao.findByID(String.valueOf(queryid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		try {
			ip = getParaValue("ipaddress");

			newip = SysUtil.doip(ip);
			String runmodel = PollingEngine.getCollectwebflag();

			FtpHistoryDao historydao = new FtpHistoryDao();

			Hashtable ConnectUtilizationhash = historydao.getPingDataById(
					queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
																			// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType,
					ConnectUtilizationhash, "连通率", newip + "pingConnect",
					740, 150);

			// 画图-----------------------------
			if (initconf!=null) {
				 String name=initconf.getName();
		            String type="       类型: 端口服务监视";
		            ip = initconf.getIpaddress();
		            String file=initconf.getFilename();
		            infoList.add("名称: "+name);
		            infoList.add(type);
		            infoList.add("      测试文件: "+file);
		            infoList.add("      IP地址: "+ip);
				
			}
			reporthash.put("servicename", initconf.getName());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", startdate);
			reporthash.put("type", "FTP");
			reporthash.put("comInfo", infoList);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "FTP");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/showServiceCompositeReport.jsp";
	}

	private String showServiceEventReport() {
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		request.setAttribute("ipaddress", ipaddress);
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		List orderList = new ArrayList();
		FTPConfig initconf = null;
		FTPConfigDao configdao = new FTPConfigDao();
		try {
			initconf = (FTPConfig) configdao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		List infolist = null;
		List list=null;
		if (initconf != null) {

			// 事件列表
			
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			User user = (User) session
					.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			EventListDao eventdao = new EventListDao();
			StringBuffer s = new StringBuffer();
			if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				s.append("select * from system_eventlist where recordtime>= '"+starttime+"' " +
						"and recordtime<='"+totime+"' ");
				s.append(" and nodeid="+id);
			}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				s.append("select * from system_eventlist where recordtime>= "+"to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')"+" " +
						"and recordtime<="+"to_date('"+totime+"','YYYY-MM-DD HH24:MI:SS')"+" ");
				s.append(" and nodeid="+id);
			}
			
			try {
				list = eventdao.getQuery(starttime, totime, "ftp", status
						+ "", level1 + "", user.getBusinessids(), initconf
						.getId());
				
				
				 infolist = eventdao.findByCriteria(s.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventdao.close();
			}
			if (infolist != null && infolist.size() > 0) {
				int levelone = 0;
				int levletwo = 0;
				int levelthree = 0;

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");

					if (eventlist.getLevel1() == 1) {
						levelone = levelone + 1;
					} else if (eventlist.getLevel1() == 2) {
						levletwo = levletwo + 1;
					} else if (eventlist.getLevel1() == 3) {
						levelthree = levelthree + 1;
					}

				}
				String servName = initconf.getName();
				String ip = initconf.getIpaddress();
				List<String> ipeventList = new ArrayList<String>();
				ipeventList.add(ip);
				ipeventList.add(servName);
				ipeventList.add((levelone + levletwo + levelthree) + "");
				ipeventList.add(levelone + "");
				ipeventList.add(levletwo + "");
				ipeventList.add(levelthree + "");

				orderList.add(ipeventList);

			}
		}
		Hashtable reporthash = new Hashtable();

		request.setAttribute("id", id);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", orderList);
		request.setAttribute("type", "FTP");
		request.setAttribute("list", list);
		reporthash.put("starttime", starttime);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("eventlist", orderList);
		reporthash.put("list", list);
		session.setAttribute("reporthash", reporthash);
		return "/capreport/service/showServiceEventReport.jsp";

	}

	private String sychronizeData() {

		int queryid = getParaIntValue("id");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
																																				// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的SOCKET所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1,
					"service", "weblogin");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null)
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable gatherHash = new Hashtable();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			try {
				WebLoginDataCollector collector = new WebLoginDataCollector();
				collector.collect_Data(nodeGatherIndicators);
			} catch (Exception exc) {

			}
		}

		return "/weblogin.do?action=detail&id=" + queryid;
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	private static HttpClient httpClient = null;
	private static long condelay = 0;
	
	
	private String isOK() {

		int queryid = getParaIntValue("id");
		WebLoginConfigDao configdao = new WebLoginConfigDao();
		Calendar date = Calendar.getInstance();
		webloginConfig Config = null;
		try {
			Config = (webloginConfig) configdao.findByID(queryid + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		String reason = "网站可用";

		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable<String, Hashtable<String, NodeGatherIndicators>> urlHash = new Hashtable<String, Hashtable<String, NodeGatherIndicators>>();// 存放需要监视的DB2指标
																																				// <dbid:Hashtable<name:NodeGatherIndicators>>

		try {
			// 获取被启用的FTP所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(queryid + "", 1,
					"service", "weblogin");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null)
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		Hashtable gatherHash = new Hashtable();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList
					.get(i);
			gatherHash
					.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}

		
		//登陆 Url 
		long starttime = 0;
		long endtime = 0;
		String doo=".do?";
	
		//需登陆后访问的 Url      
		String dataUrl = Config.getUrl();
		
//		int index= dataUrl.lastIndexOf(".");//获取url字符串中“.”的最后一个索引           
//		String a= dataUrl.substring(index+1);//返回a="html"
		if(dataUrl.contains(doo)){
	//		System.out.println("======进去了==========do.?=========可用性================");
			httpClient = new HttpClient();  
			httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,  
	        "gbk");//设置编码格式
			//模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式     
			PostMethod postMethod = null;
			try {       
				httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);  
				try{
				starttime = System.currentTimeMillis();
				postMethod = new PostMethod(dataUrl); 
				endtime = System.currentTimeMillis();
				NameValuePair[] data = null;
				      data = new NameValuePair[]{        
						new NameValuePair(Config.getName_flag(), Config.getUser_name()),   
						new NameValuePair(Config.getPassword_flag(), Config.getUser_password()),
						};   
				postMethod.setRequestBody(data);  
				}catch(Exception e){
					endtime = System.currentTimeMillis();
					e.printStackTrace();
				}
				condelay = endtime - starttime;
				httpClient.executeMethod(postMethod);   
				try{
				  if(Config.getOutflag() == 1){
					postMethod = new PostMethod(Config.getOuturl()); 
					httpClient.executeMethod(postMethod); 
					String result1 = postMethod.getResponseBodyAsString(); 
//					System.out.println("页面返回退出结果："+result1);
				  }
				}catch(Exception e){
					e.printStackTrace();
				}
//				System.out.println("页面返回结果："+result);     
				} catch (Exception e) {
					reason = "网站不可用";
//					e.printStackTrace(); 
				} 
		
			request.setAttribute("isOK", reason);
			request.setAttribute("name", Config.getAlias());
			request.setAttribute("str", Config.getUrl());
			return "/tool/ftpisok.jsp";


		}else{
			boolean flag=false;
	//		System.out.println("Config.getUser_name()     " + Config.getUser_name());
			if(!Config.getUser_name().equals("")&&Config.getUser_name()!=null)
			  {flag=true;}
			if(flag==true){
//				System.out.println("======进去了==========协同======可用性===================");
				String Url=dataUrl+"?"+Config.getName_flag()+"="+Config.getUser_name()+"&"+Config.getPassword_flag()+"="+Config.getUser_password();
				httpClient = new HttpClient();  
				httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,  
		        "gbk");//设置编码格式
				//模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式     
//			    PostMethod postMethod = null;
				GetMethod getMethod = null;
				//设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了     
				
				try {       
					//设置 HttpClient 接收 Cookie,用与浏览器一样的策略     
					httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);  
//					httpClient.executeMethod(postMethod);   

					//获得登陆后的 Cookie       
//					Cookie[] cookies=httpClient.getState().getCookies(); 
//					String tmpcookies= "";       
//					for(Cookie c:cookies){    
//						tmpcookies += c.toString()+";";   
//						}           
//					//进行登陆后的操作   
					try{
					starttime = System.currentTimeMillis();
					getMethod = new GetMethod(Url); 
					endtime = System.currentTimeMillis();
					
					
					}catch(Exception e){
						endtime = System.currentTimeMillis();
						e.printStackTrace();
					}
					//响应时间
					condelay = endtime - starttime;
//					//每次访问需授权的网址时需带上前面的 cookie 作为通行证       
//					getMethod.setRequestHeader("cookie",tmpcookies);   
//					//你还可以通过 PostMethod/GetMethod 设置更多的请求后数据   
//					//例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外          
//					postMethod.setRequestHeader("Referer", "http://unmi.cc");     
//					postMethod.setRequestHeader("User-Agent","Unmi Spot");
					
					httpClient.executeMethod(getMethod);   
					
					try{
					  if(Config.getOutflag() == 1){
						getMethod = new GetMethod(Config.getOuturl()); 
						httpClient.executeMethod(getMethod); 
						String result1 = getMethod.getResponseBodyAsString();  
//						System.out.println("页面返回退出结果："+result1);
					  }
					}catch(Exception e){
						e.printStackTrace();
					}
//					System.out.println("页面返回结果："+result);     
					} catch (Exception e) {
						reason = "网站不可用";
//						e.printStackTrace(); 
						}     
					request.setAttribute("isOK", reason);
					request.setAttribute("name", Config.getAlias());
					request.setAttribute("str", Config.getUrl());
					return "/tool/ftpisok.jsp";
			}else{
//				System.out.println("======进去了==========Config.getUser_name()==空=========可用性================");
				String Url=dataUrl;
				httpClient = new HttpClient();  
				try { 
				httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,  
		        "gbk");//设置编码格式
				//模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式     
//			    PostMethod postMethod = null;
				GetMethod getMethod = null;
				//设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了     
				
					try{
					starttime = System.currentTimeMillis();
					getMethod = new GetMethod(Url); 
					endtime = System.currentTimeMillis();
					
					
					}catch(Exception e){
						endtime = System.currentTimeMillis();
						e.printStackTrace();
					}
					//响应时间
					condelay = endtime - starttime;

					
						httpClient.executeMethod(getMethod);
						String result1 = getMethod.getResponseBodyAsString(); 
//						System.out.println("页面返回退出结果："+result1);
				
				} catch (Exception e) {
					reason = "网站不可用";
//					e.printStackTrace(); 
				}
				request.setAttribute("isOK", reason);
				request.setAttribute("name", Config.getAlias());
				request.setAttribute("str", Config.getUrl());
				return "/tool/ftpisok.jsp";
			}
		}
	}
	
	
	private void drawPiechart(String[] keys, String[] values, String chname,
			String enname) {
		ChartGraph cg = new ChartGraph();
		DefaultPieDataset piedata = new DefaultPieDataset();
		for (int i = 0; i < keys.length; i++) {
			piedata.setValue(keys[i], new Double(values[i]).doubleValue());
		}
		cg.pie(chname, piedata, enname, 300, 120);
	}

	public String getF(String s) {
		if (s.length() > 5)
			s = s.substring(0, 5);
		return s;
	}

	private String allServiceList() {

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
		// ftp list
//		FTPConfigDao ftpdao = new FTPConfigDao();
//		List ftplist = null;
//		try {
//			if (operator.getRole() == 0) {
//				ftplist = ftpdao.loadAll();
//			} else
//				ftplist = ftpdao.getFtpByBID(rbids);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			ftpdao.close();
//		}
//		request.setAttribute("ftplist", ftplist);
//
//		// mail list
//		EmailConfigDao emailConfigDao = new EmailConfigDao();
//		List<EmailMonitorConfig> userEmailMonitorConfigList = new ArrayList<EmailMonitorConfig>();
//		try {
//			userEmailMonitorConfigList = (List<EmailMonitorConfig>) emailConfigDao
//					.getByBIDAndFlag(operator.getBusinessids(), 1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			emailConfigDao.close();
//		}
//
//		request.setAttribute("emaillist", userEmailMonitorConfigList);
//
//		// process list
//		ProcsDao pdao = new ProcsDao();
//		List prolist = null;
//		try {
//			prolist = pdao.loadAll();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			pdao.close();
//		}
//		request.setAttribute("prolist", prolist);
//
//		// web list
//		WebConfigDao configdao = new WebConfigDao();
//		List weblist = null;
//		try {
//			weblist = configdao.getWebByBID(rbids);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			configdao.close();
//		}
//		request.setAttribute("weblist", weblist);
		
		// weblogin list
		WebLoginConfigDao webconfigdao = new WebLoginConfigDao();
		List webloginlist = null;
		try {
			webloginlist = webconfigdao.getWebLoginByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webconfigdao.close();
		}
		request.setAttribute("webloginlist", webloginlist);

//		// port list
//		PSTypeDao portdao = new PSTypeDao();
//		List portlist = null;
//		try {
//			if (operator.getRole() == 0) {
//				portlist = portdao.loadAll();
//			} else
//				portlist = portdao.getSocketByBID(rbids);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			portdao.close();
//		}
//		if (portlist == null)
//			portlist = new ArrayList();
//		for (int i = 0; i < portlist.size(); i++) {
//			PSTypeVo vo = (PSTypeVo) portlist.get(i);
//			Node socketNode = PollingEngine.getInstance().getSocketByID(
//					vo.getId());
//			if (socketNode == null)
//				vo.setStatus(0);
//			else
//				vo.setStatus(socketNode.getStatus());
//		}
//		request.setAttribute("portlist", portlist);

		return "/application/webLoginFile/servicelist.jsp";
	}

	private String list() {
		
		WebLoginConfigDao dao = new WebLoginConfigDao();
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

		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = dao.loadAll();
			} else
				list = dao.getWebLoginByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("userWebLoginConfigList", list);
		return "/application/webLoginFile/list.jsp";
	}

	public List<FTPConfig> getAllFTPConfigList() {
		FTPConfigDao ftpConfigDao = null;
		List<FTPConfig> allFTPConfigList = null;
		try {
			ftpConfigDao = new FTPConfigDao();
			allFTPConfigList = (List<FTPConfig>) ftpConfigDao.loadAll();
		} catch (Exception e) {

		} finally {
			ftpConfigDao.close();
		}
		return allFTPConfigList;
	}

	/**
	 * 根据用户的所属业务来提取FTPConfig列表
	 * 
	 * @param allFTPConfigList
	 * @return
	 */
	public List<FTPConfig> getFTPConfigListByUser(List<FTPConfig> ftpConfigList) {

		User operator = (User) session
				.getAttribute(SessionConstant.CURRENT_USER);
		String businessids = operator.getBusinessids();
		List<FTPConfig> userFTPConfigList = new ArrayList<FTPConfig>();
		List<String> userBusinessidList = getBusinessidList(businessids);

		// 如果用户所属的业务id列表 包含 FTPConfig所属的业务 则显示
		if (userBusinessidList != null && userBusinessidList.size() > 0) {
			for (int i = 0; i < ftpConfigList.size(); i++) {
				String FTPConfigbids = ftpConfigList.get(i).getBid();
				List<String> ftpConfigBusinessidList = getBusinessidList(FTPConfigbids);
				for (int j = 0; j < userBusinessidList.size(); j++) {
					if (ftpConfigBusinessidList.contains(userBusinessidList
							.get(j))) {
						userFTPConfigList.add(ftpConfigList.get(i));
						break;
					}
				}

			}
		}
		return userFTPConfigList;
	}

	public List<webloginConfig> getWebLoginConfigListByMonflag(Integer flag) {
		WebLoginConfigDao loginConfigDao = null;
		List<webloginConfig> loginConfigList = null;
		try {
			loginConfigDao = new WebLoginConfigDao();
			loginConfigList = (List<webloginConfig>) loginConfigDao
					.getWebLoginConfigListByMonFlag(flag);
		} catch (Exception e) {

		} finally {
			loginConfigDao.close();
		}
		return loginConfigList;
	}

	/**
	 * 将业务id的字符串 拆分成一个业务id的列表
	 * 
	 * @param businessids
	 * @return
	 */
	private List<String> getBusinessidList(String businessids) {
		String bid[] = businessids.split(",");
		List<String> businessidList = new ArrayList<String>();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				// 去掉空白字符串
				if (bid[i] != null && bid[i].trim().length() > 0)
					businessidList.add(bid[i].trim());
			}
		}
		return businessidList;
	}

	/**
	 * snow 增加前将供应商查找到
	 * 
	 * @return
	 * @date 2010-5-21
	 */
	private String ready_add() {
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);
		return "/application/webLoginFile/add.jsp";
	}

	/**
	 * 添加 FTPConfig
	 * 
	 * @return
	 */
	private String add() {
		boolean result = false;
		webloginConfig loginConfig = new webloginConfig();
		WebLoginConfigDao loginConfigDao = null;
		try {
			loginConfigDao = new WebLoginConfigDao();
			// 创建 ftpConfig
			loginConfig = createWebLoginConfig();
			
//			
			loginConfig.setId(KeyGenerator.getInstance().getNextKey());
			result = loginConfigDao.save(loginConfig);
			
            this.createTablePing(loginConfig.getId());
            this.createTableResponse(loginConfig.getId());		
			
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			result = timeShareConfigUtil.saveTimeShareConfigList(request,
					String.valueOf(loginConfig.getId()), timeShareConfigUtil
							.getObjectType("20"));
			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String
					.valueOf(loginConfig.getId()), timeGratherConfigUtil
					.getObjectType("26"));
			/* snow add end */

			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(loginConfig
						.getId()
						+ "", "service", "weblogin", "1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String
						.valueOf(loginConfig.getId()), "service", "weblogin");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 在轮询线程中增加被监视节点
			WebLoginLoader loader = new WebLoginLoader();
			try {
				loader.loadOne(loginConfig);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loader.close();
			}
			
			//保存应用
//			HostApplyManager.save(loginConfig);
		} catch (Exception e) {
			result = false;
		} finally {
			loginConfigDao.close();
		}
	  	try{
	  		loginConfigDao = new WebLoginConfigDao();
			List _list = loginConfigDao.loadAll();
			if (_list == null)_list = new ArrayList();
			ShareData.setWebloginlist(_list);
			WebLoginLoader webloader = new WebLoginLoader();
			webloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			loginConfigDao.close();
		}
		// 成功则跳回 FTPConfig 列表页面 失败则显示数据错误
		if (result) {
			return list();
		} else {
			return "/application/webLoginFile/savefail.jsp";
		}
	}

	/**
	 * 删除 FTPConfig
	 * 
	 * @return
	 */
	private String delete() {
		String[] ids = getParaArrayValue("checkbox");
		WebLoginConfigDao webConfigDao = null;
		boolean result = false;
		try {
			webConfigDao = new WebLoginConfigDao();
			if (ids != null && ids.length > 0) {
				result = webConfigDao.delete(ids);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				for (int i = 0; i < ids.length; i++) {
					this.dropTablePing(Integer.parseInt(ids[i]));
					this.dropTableResponse(Integer.parseInt(ids[i]));
					Node node = PollingEngine.getInstance().getWebLoginByID(Integer.parseInt(ids[i]));
					//删除应用
//					HostApplyDao hostApplyDao = null;
//					try{
//						hostApplyDao = new HostApplyDao();
//						hostApplyDao.delete(" where ipaddress = '"+node.getIpAddress()+"' and subtype = 'ftp' and nodeid = '"+ids[i]+"'");
//					} catch (Exception e) {
//						e.printStackTrace();
//					} finally {
//						if(hostApplyDao != null){
//							hostApplyDao.close();
//						}
//					}
					
					PollingEngine.getInstance().deleteFtpByID(
							Integer.parseInt(ids[i]));
					timeShareConfigUtil.deleteTimeShareConfig(ids[i],
							timeShareConfigUtil.getObjectType("20"));
					tg.deleteTimeGratherConfig(ids[i], timeShareConfigUtil
							.getObjectType("26"));

					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i],
								"service", "weblogin");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(ids[i], "service", "weblogin");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						indidao.close();
					}

					// 更新业务视图
					String id = ids[i];
					NodeDependDao nodedependao = new NodeDependDao();
					List weslist = nodedependao.findByNode("weblogin" + id);
					if (weslist != null && weslist.size() > 0) {
						for (int j = 0; j < weslist.size(); j++) {
							NodeDepend wesvo = (NodeDepend) weslist.get(j);
							if (wesvo != null) {
								LineDao lineDao = new LineDao();
								lineDao.deleteByidXml("weblogin" + id, wesvo
										.getXmlfile());
								NodeDependDao nodeDependDao = new NodeDependDao();
								if (nodeDependDao.isNodeExist("weblogin" + id, wesvo
										.getXmlfile())) {
									nodeDependDao.deleteByIdXml("weblogin" + id,
											wesvo.getXmlfile());
								} else {
									nodeDependDao.close();
								}

								// yangjun
								User user = (User) session
										.getAttribute(SessionConstant.CURRENT_USER);
								ManageXmlDao mXmlDao = new ManageXmlDao();
								List xmlList = new ArrayList();
								try {
									xmlList = mXmlDao.loadByPerAll(user
											.getBusinessids());
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									mXmlDao.close();
								}
								try {
									ChartXml chartxml;
									chartxml = new ChartXml("tree");
									chartxml.addViewTree(xmlList);
								} catch (Exception e) {
									e.printStackTrace();
								}

								ManageXmlDao subMapDao = new ManageXmlDao();
								ManageXml manageXml = (ManageXml) subMapDao
										.findByXml(wesvo.getXmlfile());
								if (manageXml != null) {
									NodeDependDao nodeDepenDao = new NodeDependDao();
									try {
										List lists = nodeDepenDao
												.findByXml(wesvo.getXmlfile());
										ChartXml chartxml;
										chartxml = new ChartXml(
												"NetworkMonitor", "/"
														+ wesvo.getXmlfile()
																.replace("jsp",
																		"xml"));
										chartxml.addBussinessXML(manageXml
												.getTopoName(), lists);
										ChartXml chartxmlList;
										chartxmlList = new ChartXml(
												"NetworkMonitor",
												"/"
														+ wesvo
																.getXmlfile()
																.replace("jsp",
																		"xml")
																.replace(
																		"businessmap",
																		"list"));
										chartxmlList.addListXML(manageXml
												.getTopoName(), lists);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										nodeDepenDao.close();
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			SysLogger.error("WebLoginManager.delete()", ex);
			result = false;
		} finally {
			webConfigDao.close();
		}
	  	try{
	  		webConfigDao = new WebLoginConfigDao();
			List _list = webConfigDao.loadAll();
			if (_list == null)_list = new ArrayList();
			ShareData.setWebloginlist(_list);
			WebLoginLoader webloader = new WebLoginLoader();
			webloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			webConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/webLoginFile/savefail.jsp";
		}
	}

	/**
	 * 
	 * @return
	 */
	private String readyEdit() {
		WebLoginConfigDao webConfigdao = null;
		BusinessDao businessdao = null;
		String targetJsp = null;
		boolean result = false;
		try {
			webConfigdao = new WebLoginConfigDao();
			
			setTarget("/application/webLoginFile/edit.jsp");
			targetJsp = readyEdit(webConfigdao);
			webloginConfig webConfig = (webloginConfig) request.getAttribute("vo");
			
			businessdao = new BusinessDao();
			// 载入所有业务
			List<Business> allBusiness = businessdao.loadAll();
			request.setAttribute("allBusiness", allBusiness);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			List timeShareConfigList = timeShareConfigUtil
					.getTimeShareConfigList(String.valueOf(webConfig.getId()),
							timeShareConfigUtil.getObjectType("20"));
			request.setAttribute("timeShareConfigList", timeShareConfigList);

			/* 获得设备的采集时间 snow add at 2010-05-21 */
			// 提供供应商信息
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
			// 提供已设置的采集时间信息
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg
					.getTimeGratherConfig(String.valueOf(webConfig.getId()), tg
							.getObjectType("26"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request
					.setAttribute("timeGratherConfigList",
							timeGratherConfigList);
			/* snow end */

			result = true;
		} catch (Exception ex) {
			SysLogger.error("webloginManager.readyEdit()", ex);
			result = false;
		} finally {
			webConfigdao.close();
			if (businessdao != null) {
				businessdao.close();
			}
		}
		return targetJsp;
	}

	/**
	 * 更新 
	 * 
	 * @return
	 */
	private String update() {
		webloginConfig vo = new webloginConfig();
		boolean result = false;
		WebLoginConfigDao ConfigDao = null;
		try {
			ConfigDao = new WebLoginConfigDao();

			vo = createWebLoginConfig();
			int id = getParaIntValue("id");
			vo.setId(id);
			result = ConfigDao.update(vo);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String
					.valueOf(vo.getId()), timeShareConfigUtil
					.getObjectType("20"));

			/* 增加采集时间设置 snow add at 2010-5-20 */
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String
					.valueOf(vo.getId()), timeGratherConfigUtil
					.getObjectType("26"));
			/* snow add end */

//			System.out.println("=======================>"+vo.getAlias());
			
			WebLogin web = (WebLogin) PollingEngine.getInstance().getWebLoginByID(id);
			web.setAlias(vo.getAlias());
			web.setUrl(vo.getUrl());
			web.setFlag(Integer.parseInt(vo.getFlag()));
			web.setName_flag(vo.getName_flag());
			web.setPassword_flag(vo.getPassword_flag());
			web.setCode_flag(vo.getCode_flag());
			web.setUser_name(vo.getUser_name());
			web.setUser_password(vo.getUser_password());
			web.setUser_code(vo.getUser_code());
			web.setKeyword(vo.getKeyword());
			web.setTimeout(vo.getTimeout());
			web.setBid(vo.getBid());
			web.setSupperid(vo.getSupperid());
			web.setCategory(88);
			web.setStatus(0);
			web.setType("WEB虚拟登陆");
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		} finally {
			ConfigDao.close();
		}
	  	try{
	  		ConfigDao = new WebLoginConfigDao();
			List _list = ConfigDao.loadAll();
			if (_list == null)_list = new ArrayList();
			ShareData.setWebloginlist(_list);
			WebLoginLoader webloader = new WebLoginLoader();
			webloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			ConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/webLoginFile/savefail.jsp";
		}

	}

	/**
	 * 根据页面的参数来创建 
	 * 
	 * @return
	 */
	private webloginConfig createWebLoginConfig() {

		webloginConfig loginConfig = new webloginConfig();

//		loginConfig.setId(KeyGenerator.getInstance().getNextKey());
		loginConfig.setAlias(getParaValue("alias"));
		loginConfig.setUrl(getParaValue("url"));
		loginConfig.setOutflag(getParaIntValue("outflag"));
		loginConfig.setOuturl(getParaValue("outurl"));
		loginConfig.setTimeout(getParaValue("timeout"));
		loginConfig.setFlag(getParaIntValue("flag")+"");
		loginConfig.setName_flag(getParaValue("name_flag"));
		loginConfig.setUser_name(getParaValue("user_name"));
		loginConfig.setPassword_flag(getParaValue("password_flag"));
		loginConfig.setUser_password(getParaValue("user_password"));
		loginConfig.setCode_flag(getParaValue("code_flag"));
		loginConfig.setUser_code(getParaValue("user_code"));
		loginConfig.setSupperid(getParaIntValue("supperid"));
		String wordNum = request.getParameter("wordNum");
//		String _flag = (String)request.getAttribute("flag");
//		loginConfig.setFlag(_flag);
		
		
		int num = 0;
		try{
			num = Integer.parseInt(wordNum);
		}catch(Exception e){
			//e.printStackTrace();
		}
		String words = "";
		for (int i = 0; i <= num; i++) {
   			String partName = String.valueOf(i);
   			words = words + request.getParameter("words" + partName);
   			if(i!=num){
   				words = words + ",";
   			}
		}
		//SysLogger.info("word=========================="+words);
		if(words == null || "null".equals(words))words = "";
		loginConfig.setKeyword(words);
		
		String errorwordNum = request.getParameter("errorwordNum");
//		String _flag = (String)request.getAttribute("flag");
//		loginConfig.setFlag(_flag);
		
		
		int errornum = 0;
		try{
			errornum = Integer.parseInt(errorwordNum);
		}catch(Exception e){
			//e.printStackTrace();
		}
		String errorwords = "";
		for (int i = 0; i <= num; i++) {
   			String partName = String.valueOf(i);
   			errorwords = errorwords + request.getParameter("errorwords" + partName);
   			if(i!=num){
   				errorwords = errorwords + ",";
   			}
		}
		//SysLogger.info("errorword=========================="+errorwords);
		if(errorwords == null || "null".equals(errorwords))errorwords = "";
		loginConfig.setUser_code(errorwords);
		

		String bid = getParaValue("bid");
		loginConfig.setBid(bid);
		return loginConfig;
	}

	/**
	 * 修改 FTPConfig 的监视信息之后返回FTPConfig列表页面
	 * 
	 * @return
	 */
	private String changeMonflag() {
		boolean result = false;
		webloginConfig loginConfig = new webloginConfig();
		WebLoginConfigDao loginConfigDao = null;
		try {
			String id = getParaValue("id");
			int monflag = getParaIntValue("value");
			loginConfigDao = new WebLoginConfigDao();
			loginConfig = (webloginConfig) loginConfigDao.findByID(id);
			loginConfig.setFlag(monflag+"");
			result = loginConfigDao.update(loginConfig);
			WebLogin web = (WebLogin) PollingEngine.getInstance().getWebLoginByID(
					Integer.parseInt(id));
			web.setFlag(monflag);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			loginConfigDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/application/webLogin/savefail.jsp";
		}
	}

	private String midalllist() {

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

		// Tomcat
		TomcatDao tomcatdao = new TomcatDao();
		List tomcatlist = null;
		try {
			if (operator.getRole() == 0) {
				tomcatlist = tomcatdao.loadAll();
			} else
				tomcatlist = tomcatdao.getTomcatByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatdao.close();
		}
		if (tomcatlist == null)
			tomcatlist = new ArrayList();
		for (int i = 0; i < tomcatlist.size(); i++) {
			Tomcat tomcatvo = (Tomcat) tomcatlist.get(i);
			Node tomcatNode = PollingEngine.getInstance().getTomcatByID(
					tomcatvo.getId());
			if (tomcatNode == null)
				tomcatvo.setStatus(0);
			else
				tomcatvo.setStatus(tomcatNode.getStatus());
		}

		// mq
		MQConfigDao mqconfigdao = new MQConfigDao();
		List mqlist = null;
		try {
			if (operator.getRole() == 0) {
				mqlist = mqconfigdao.loadAll();
			} else
				mqlist = mqconfigdao.getMQByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqconfigdao.close();
		}
		if (mqlist == null)
			mqlist = new ArrayList();
		for (int i = 0; i < mqlist.size(); i++) {
			MQConfig mqvo = (MQConfig) mqlist.get(i);
			Node mqNode = PollingEngine.getInstance().getMqByID(mqvo.getId());
			if (mqNode == null)
				mqvo.setStatus(0);
			else
				mqvo.setStatus(mqNode.getStatus());
		}
		// domimo
		DominoConfigDao dominoconfigdao = new DominoConfigDao();
		List dominolist = new ArrayList();
		try {
			dominolist = dominoconfigdao.getDominoByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dominoconfigdao.close();
		}

		// was
		WasConfigDao wasconfigdao = new WasConfigDao();
		List waslist = null;
		try {
			waslist = wasconfigdao.getWasByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wasconfigdao.close();
		}

		// weblogic
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		List weblogiclist = null;
		try {
			if (operator.getRole() == 0) {
				weblogiclist = weblogicconfigdao.loadAll();
			} else
				weblogiclist = weblogicconfigdao.getWeblogicByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			weblogicconfigdao.close();
		}
		// IIS
		IISConfigDao iisconfigdao = new IISConfigDao();
		List iislist = new ArrayList();
		try {
			iislist = iisconfigdao.getIISByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			iisconfigdao.close();
		}

		// CICS
		CicsConfigDao cicsconfigdao = new CicsConfigDao();
		List cicslist = new ArrayList();
		try {
			cicslist = cicsconfigdao.getCicsByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cicsconfigdao.close();
		}
		// DNS
		DnsConfigDao dnsconfigdao = new DnsConfigDao();
		List dnslist = null;
		try {
			dnslist = dnsconfigdao.getDnsByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dnsconfigdao.close();
		}

		request.setAttribute("dnslist", dnslist);
		request.setAttribute("cicslist", cicslist);
		request.setAttribute("iislist", iislist);
		request.setAttribute("weblogiclist", weblogiclist);
		request.setAttribute("waslist", waslist);
		request.setAttribute("tomcatlist", tomcatlist);
		request.setAttribute("mqlist", mqlist);
		request.setAttribute("dominolist", dominolist);
		return "/application/ftp/midalllist.jsp";
	}
	
	
	private void createTablePing(int id){
		String sql = "create table weblogin_ping"+id+" (" +
				" id bigint(11) not null auto_increment," +
				" thevalue int(5) default null," +
				" collecttime timestamp ," +
				" unit varchar(50) default '%' ," +
				" primary key(id))";
		
		System.out.println(sql);
		this.AddSql(sql);
	}
	
	private void createTableResponse(int id){
		String sql = "create table weblogin_response"+id+" (" +
				" id bigint(11) not null auto_increment," +
				" thevalue int(5) default null," +
				" collecttime timestamp ," +
				" unit varchar(50) default 'ms' ," +
				" primary key(id))";
		
		System.out.println(sql);
		this.AddSql(sql);
	}
	
	private void dropTablePing(int id){
		String sql = "drop table weblogin_ping"+id;
		this.AddSql(sql);
	}
	private void dropTableResponse(int id){
		String sql = "drop table weblogin_response"+id;
		this.AddSql(sql);
	}
	
	public static void AddSql(String sql){
		DBManager pollmg = new DBManager();// 数据库管理对象
		 try{
			 pollmg.executeUpdate(sql);
		 }catch(Exception e){
			 
		 }finally{
			 pollmg.close(); 
		 }		 
		 pollmg=null;
	}
}