/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.loader.DNSLoader;
import com.afunms.polling.loader.DominoLoader;
import com.afunms.polling.node.Domino;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;

public class DominoManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		List ips = new ArrayList();
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
		
		DominoConfigDao configdao = new DominoConfigDao();	
		List list = new ArrayList();
		try{
			list = configdao.loadAll();
			//list = configdao.getDominoByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/domino/list.jsp";	
	}
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_add(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/domino/add.jsp";
	}

	private String add()
    {    	  	
		DominoConfigDao dao=new DominoConfigDao();
		DominoConfig vo=new DominoConfig();
		
		String name=getParaValue("name");
		String ipaddress=getParaValue("ipaddress");
		String community=getParaValue("community");
		int mon_flag=getParaIntValue("mon_flag");
		int netid=getParaIntValue("netid");
		String sendmobiles=getParaValue("sendmobiles");
		String sendemail=getParaValue("sendemail");
		
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setName(name);
		vo.setIpaddress(ipaddress);
		vo.setCommunity(community);
		vo.setMon_flag(mon_flag);
		//vo.setNetid(netid);
		vo.setSendmobiles(sendmobiles);
		vo.setSendemail(sendemail);
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20

        vo.setNetid(getParaValue("bid"));
        try{
        	dao.save(vo);
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();  // nielin add 2009-12-31
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("10"));
        
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("10"));
        	/* snow add end*/
        	//保存应用
    		HostApplyManager.save(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
 
        //在轮询线程中增加被监视节点
        DominoLoader loader = new DominoLoader();
        try{
        	loader.loadOne(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	loader.close();
        }
     	try{
      		dao=new DominoConfigDao();
			List list = dao.loadAll();
			if (list == null)list = new ArrayList();
			ShareData.setDominolist(list);
			DominoLoader dominoloader = new DominoLoader();
			dominoloader.clearRubbish(list);
		}catch(Exception e){
				
		}finally{
			dao.close();
		}
        return "/domino.do?action=list";
    }    
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		MQConfig vo = new MQConfig();
		List list = new ArrayList();
		DominoConfigDao configdao = new DominoConfigDao();
		try{
			// nielin modify 2010-01-04 start
			if(ids != null && ids.length > 0){		
	    		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); //
	    		TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil(); // snow add at 2010-5-20 删除数据库采集时间
	    		for(int i=0;i<ids.length;i++){
	    			timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("10"));
	    			timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("10")); // snow add at 2010-5-20 
	    			
	    			Node node = PollingEngine.getInstance().getDominoByID(Integer.parseInt(ids[i]));
	    			//删除应用
					HostApplyDao hostApplyDao = null;
					try{
						hostApplyDao = new HostApplyDao();
						hostApplyDao.delete(" where ipaddress = '"+node.getIpAddress()+"' and subtype = 'domino' and nodeid = '"+ids[i]+"'");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(hostApplyDao != null){
							hostApplyDao.close();
						}
					}
	    			PollingEngine.getInstance().deleteDominoByID(Integer.parseInt(ids[i]));
	    		}
	    		configdao.delete(ids);
	    	}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
			// nielin modify 2010-01-04 end
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
			configdao = new DominoConfigDao();
			try{
				list = configdao.getDominoByBID(rbids);
			}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			configdao.close();
    		}
		}catch(Exception e){
			e.printStackTrace();
		}
     	try{
     		configdao=new DominoConfigDao();
			List _list = configdao.loadAll();
			if (_list == null)_list = new ArrayList();
			ShareData.setDominolist(_list);
			DominoLoader dominoloader = new DominoLoader();
			dominoloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
		
		//request.setAttribute("list",list);
		return "/domino.do?action=list";
	}
	
	/**
	 * @author nielin add for time-sharing 
	 * @since 2010-01-04
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/domino/edit.jsp";
		List timeShareConfigList = new ArrayList();
		DominoConfigDao dao = new DominoConfigDao();
		try{
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();  //nielin add 2009-12-31
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("10"));
		    /* 获得设备的采集时间 snow add at 2010-05-18 */
			//提供供应商信息
			SupperDao supperdao = new SupperDao();
	    	List<Supper> allSupper = supperdao.loadAll();
	    	request.setAttribute("allSupper", allSupper);
	    	//提供已设置的采集时间信息
	    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
	    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("10"));
	    	for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
	    		timeGratherConfig.setHourAndMin();
			}
	    	request.setAttribute("timeGratherConfigList", timeGratherConfigList);  
	    	/* snow end */
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
	    request.setAttribute("timeShareConfigList", timeShareConfigList);
	    return jsp;
	}
	
	private String update()
    {    	   
		DominoConfig vo = new DominoConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		int id=getParaIntValue("id");
		String name=getParaValue("name");
		String ipaddress=getParaValue("ipaddress");
		String community=getParaValue("community");
		int mon_flag=getParaIntValue("mon_flag");
		int netid=getParaIntValue("netid");
		String sendmobiles=getParaValue("sendmobiles");
		String sendemail=getParaValue("sendemail");
		
		vo.setId(id);
		vo.setName(name);
		vo.setIpaddress(ipaddress);
		vo.setCommunity(community);
		vo.setMon_flag(mon_flag);
		//vo.setNetid(netid);
		vo.setSendmobiles(sendmobiles);
		vo.setSendemail(sendemail);
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20
        vo.setNetid(getParaValue("bid"));
        //WebConfigDao configdao = new WebConfigDao();
        DominoConfigDao configdao = new DominoConfigDao();
        try{
        	configdao.update(vo);	
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();   // nielin add 2009-12-31
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("10"));
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("10"));
        	/* snow add end*/
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
			configdao.close();
		}
  
        if(PollingEngine.getInstance().getMqByID(vo.getId())!=null)
        {        
           com.afunms.polling.node.Domino domino = (com.afunms.polling.node.Domino)PollingEngine.getInstance().getDominoByID(vo.getId());
           domino.setId(vo.getId());
       		domino.setAlias(vo.getName());
       		domino.setName(vo.getName());
       		domino.setIpAddress(vo.getIpaddress());
       		domino.setCommunity(vo.getCommunity());
       		domino.setSendemail(vo.getSendemail());
       		domino.setSendmobiles(vo.getSendmobiles());
       		domino.setSendphone(vo.getSendphone());
       		domino.setBid(vo.getNetid());
       		domino.setMon_flag(vo.getMon_flag());
        }
       	try{
     		configdao=new DominoConfigDao();
			List _list = configdao.loadAll();
			if (_list == null)_list = new ArrayList();
			ShareData.setDominolist(_list);
			DominoLoader dominoloader = new DominoLoader();
			dominoloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
        return "/domino.do?action=list";
    }
	
	private String search()
    {    	   
		DBVo vo = new DBVo();
		
//		//SybspaceconfigDao configdao = new SybspaceconfigDao();
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
//			DBTypeVo typevo = null;
//			try{
//				typevo = (DBTypeVo)typedao.findByDbtype("sybase");
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally{
//				typedao.close();
//			}
//			DBDao dao = new DBDao();
//			try{
//				oraList = dao.getDbByTypeAndBID(typevo.getId(),rbids);
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally{
//				dao.close();
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
		DominoConfig vo = new DominoConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			DominoConfigDao configdao = new DominoConfigDao();
			try{
				vo = (DominoConfig)configdao.findByID(getParaValue("id"));
				vo.setMon_flag(1);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			configdao = new DominoConfigDao();
			try{
				configdao.update(vo);	
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
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
			configdao = new DominoConfigDao();
			try{
				list = configdao.getDominoByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//request.setAttribute("list",list);
		return "/domino.do?action=list";
    }
	
	private String cancelalert()
    {    
		DominoConfig vo = new DominoConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			DominoConfigDao configdao = new DominoConfigDao();
			try{
				vo = (DominoConfig)configdao.findByID(getParaValue("id"));
				vo.setMon_flag(0);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
			
			configdao = new DominoConfigDao();
			try{
				configdao.update(vo);	
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
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
			configdao = new DominoConfigDao();
			try{
				list = configdao.getDominoByBID(rbids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				configdao.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//request.setAttribute("list",list);
		return "/domino.do?action=list";
    }
	
	private String detail()
    {    
//		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		WebConfigDao configdao = new WebConfigDao();
//		Urlmonitor_realtimeDao realtimedao = new Urlmonitor_realtimeDao();
//		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
//		List urllist = new ArrayList();      //用做条件选择列表
//		WebConfig initconf = new WebConfig();    //当前的对象
//        String lasttime="";
//        String nexttime="";
//        String conn_name="";
//        String valid_name = "";
//        String fresh_name = "";
//        String wave_name = "";
//        String delay_name = "";
//        String connrate="0";
//        String validrate="0";
//        String freshrate="0";
//        Calendar now = Calendar.getInstance();
//        now.setTime(new Date());
//        Date nowdate = new Date();
//        nowdate.getHours();
//        String from_date1 = getParaValue("from_date1");
//        if (from_date1 == null){
//        	from_date1 = timeFormatter.format(new Date());
//        	request.setAttribute("from_date1", from_date1);
//        }
//        String to_date1 = getParaValue("to_date1");
//        if (to_date1 == null){
//        	to_date1 = timeFormatter.format(new Date());
//        	request.setAttribute("to_date1", to_date1);
//        }
//        String from_hour = getParaValue("from_hour");
//        if (from_hour == null){
//        	from_hour = "00";
//        	request.setAttribute("from_hour", from_hour);
//        }
//        String to_hour = getParaValue("to_hour");
//        if(to_hour == null){
//        	to_hour = nowdate.getHours()+"";            	            
//        	request.setAttribute("to_hour", to_hour);
//        }
//        String starttime = from_date1+" "+from_hour+":00:00";
//        String totime = to_date1+" "+to_hour+":59:59";            
//        int flag = 0;
//        try {
//        	User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
//			String bids = operator.getBusinessids();
//			String bid[] = bids.split(",");
//			Vector rbids = new Vector();
//			if(bid != null && bid.length>0){
//				for(int i=0;i<bid.length;i++){
//					if(bid[i] != null && bid[i].trim().length()>0)
//						rbids.add(bid[i].trim());
//				}
//			}
//			configdao = new WebConfigDao();
//			try{
//				urllist = configdao.getWebByBID(rbids);
//			}catch(Exception e){
//				
//			}
//			
//        	Integer queryid = getParaIntValue("id");//.getUrl_id();
//
//        	if(urllist.size()>0&&queryid==null){
//        		Object obj = urllist.get(0);           		
//        	}
//        	if(queryid!=null){					
//        		//如果是链接过来则取用查询条件
//        		configdao = new WebConfigDao();
//        		initconf = (WebConfig)configdao.findByID(queryid+"");
//        	}
//        	queryid = initconf.getId();
//        	 conn_name=queryid+"urlmonitor-conn";
//             valid_name =queryid+"urlmonitor-valid";
//             fresh_name =queryid+"urlmonitor-refresh";
//             wave_name = queryid+"urlmonitor-rec";
//             delay_name = queryid+"urlmonitor-delay";
//        	
//             List urlList = realtimedao.getByUrlId(queryid);
//             
//        	Calendar last = null;
//        	if(urlList != null && urlList.size()>0){
//        		last = ((Urlmonitor_realtime)urlList.get(0)).getMon_time();
//        	}
//        	int interval = 0;
//        	//TaskXmlUtil taskutil = new TaskXmlUtil("urltask");
//        	try {
//    			//Session session = this.beginTransaction();
//    			List numList = new ArrayList();
//    			TaskXml taskxml = new TaskXml();
//    			numList = taskxml.ListXml();
//    			for (int i = 0; i < numList.size(); i++) {
//    				Task task = new Task();
//    				BeanUtils.copyProperties(task, numList.get(i));
//    				if (task.getTaskname().equals("urltask")){
//    					interval = task.getPolltime().intValue();
//    					//numThreads = task.getPolltime().intValue();
//    				}
//    			}
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//        	DateE de=new DateE();
//        	if (last == null){
//        		last = new GregorianCalendar();
//        		flag=1;
//        	}
//        	lasttime = de.getDateDetail(last);
//        	last.add(Calendar.MINUTE,interval);
//        	nexttime = de.getDateDetail(last);
//        	
//        	int hour=1;
//        	if(getParaValue("hour")!=null){
//        		 hour = Integer.parseInt(getParaValue("hour"));
//        	}else{
//        		request.setAttribute("hour", "1");
//        		//urlconfForm.setHour("1");
//        	}
//        	
//        	InitCoordinate initer=new InitCoordinate(new GregorianCalendar(),hour,1);
//        	//Minute[] minutes=initer.getMinutes();
//        	TimeSeries ss1 = new TimeSeries("", Minute.class); 
//        	TimeSeries ss2 = new TimeSeries("", Minute.class); 
//        	
//        	//ss.add()
//        	TimeSeries[] s = new TimeSeries[1];
//        	TimeSeries[] s_ = new TimeSeries[1];
//        	//Vector wave_v = historyManager.getInfo(queryid,initer);
//        	Vector wave_v = historydao.getByUrlid(queryid, starttime, totime,0);
//        	
//        	for(int i=0;i<wave_v.size();i++){
//        		Hashtable ht = (Hashtable)wave_v.get(i);
//        		double conn = Double.parseDouble(ht.get("conn").toString());
//        		double fresh = Double.parseDouble(ht.get("refresh").toString());
//        		double condelay = Double.parseDouble(ht.get("condelay").toString());
//        		String time = ht.get("mon_time").toString();
//          		ss1.addOrUpdate(new Minute(sdf1.parse(time)),conn);
//          		ss2.addOrUpdate(new Minute(sdf1.parse(time)),condelay);
//        	}
//        	s[0] = ss1;
//        	s_[0] = ss2;
//        	ChartGraph cg = new ChartGraph();
//        	cg.timewave(s,"时间","连通","",wave_name,600,120);
//        	cg.timewave(s_,"时间","时延(ms)","",delay_name,600,120);
//        	//p_draw_line(s,"","url"+queryid+"ConnectUtilization",740,120);
//        	
//        	//是否连通
//        	String conn[] = new String[2];
//        	if (flag == 0)
//        		//conn = historyManager.getAvailability(queryid,initer,"is_canconnected");
//        		conn = historydao.getAvailability(queryid,starttime,totime,"is_canconnected");
//        	else{
//        		//conn[0] = "0";
//        		//conn[1] = "0";
//        		//conn = historyManager.getAvailability(queryid,initer,"is_canconnected");
//        		conn = historydao.getAvailability(queryid,starttime,totime,"is_canconnected");
//        	}
//        	String[] key1 = {"连通","未连通"};
//        	drawPiechart(key1,conn,"",conn_name);
//        	//drawchart(minutes,"连通",)
////        	Vector conn_v = historyManager.getInfo(queryid,initer,"is_canconnected");
////        	for(int i=0;i<conn)
//        	if (flag == 0)
//        	connrate = getF(String.valueOf(Float.parseFloat(conn[0])/(Float.parseFloat(conn[0])+Float.parseFloat(conn[1]))*100))+"%";
//        	//是否有效
//        	String avail[] = new String[2];
//        	if (flag == 0)
//        		avail = historydao.getAvailability(queryid,initer,"is_valid");
//        	else{
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
//        	if (flag == 0)
//        	refresh = historydao.getAvailability(queryid,initer,"is_refresh");
//        	else{
//        		refresh[0] = "0";
//        		refresh[1] = "0";
//        	}
//      
//        	String[] key3 = {"刷新","未刷新"};
//        	drawPiechart(key3,refresh,"页面刷新情况",fresh_name);
//        	if (flag == 0)
//        	freshrate = getF(String.valueOf(Float.parseFloat(refresh[0])/(Float.parseFloat(refresh[0])+Float.parseFloat(refresh[1]))*100))+"%";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        request.setAttribute("urllist",urllist);
//        request.setAttribute("initconf",initconf);
//        request.setAttribute("lasttime",lasttime);
//        request.setAttribute("nexttime",nexttime);
//        request.setAttribute("conn_name",conn_name);
//        request.setAttribute("valid_name",valid_name);
//        request.setAttribute("fresh_name",fresh_name);
//        request.setAttribute("wave_name",wave_name);
//        request.setAttribute("delay_name",delay_name);
//        request.setAttribute("connrate",connrate);
//        request.setAttribute("validrate",validrate);
//        request.setAttribute("freshrate",freshrate);
//        
//        request.setAttribute("from_date1",from_date1);
//        request.setAttribute("to_date1",to_date1);
//        
//        request.setAttribute("from_hour",from_hour);
//        request.setAttribute("to_hour",to_hour);
		return "/application/web/detail.jsp";
    }
	private String domcpu(){
		String id=getParaValue("id");
		
		String flag=getParaValue("flag");
		Hashtable cpuhash = new Hashtable();
		Hashtable sermemhash = new Hashtable();
		Hashtable platmemhash = new Hashtable();
		I_HostCollectData hostmanager = new HostCollectDataManager();
		 Domino domino = (Domino)PollingEngine.getInstance().getDominoByID(Integer.parseInt(id));
		
		try{
			cpuhash = hostmanager.getCategory(domino.getIpAddress(),"dominoCpu","Utilization",getStartTime(),getToTime());
			sermemhash=hostmanager.getCategory(domino.getIpAddress(), "domservmem", "Utilization", getStartTime(), getToTime());
			platmemhash=hostmanager.getCategory(domino.getIpAddress(), "domplatmem", "Utilization", getStartTime(), getToTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		Hashtable maxhash=null;
		//cpu最大值
		maxhash = new Hashtable();
		String cpumax="";
		if(cpuhash.get("max")!=null){
				cpumax = (String)cpuhash.get("max");
		}
		maxhash.put("cpu",cpumax);
		
		//cpu平均值
		String cpuavg="";
		if(cpuhash.get("avgcpucon")!=null){
				cpuavg = (String)cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg",cpuavg);
		Hashtable maxSermemhash= new Hashtable();
	    //server memory 最大值
		String sermemmax="";
		if(sermemhash.get("max")!=null){
			sermemmax = (String)sermemhash.get("max");
	     }
		maxSermemhash.put("serMemMax",sermemmax);
		  //server memory 平均值
		String sermemavg="";
		if(sermemhash.get("avgmemory")!=null){
			sermemavg = (String)sermemhash.get("avgmemory");
	     }
		maxSermemhash.put("serMemAvg",sermemavg);
		
		Hashtable maxPlatmemhash= new Hashtable();
		  //plat memory 最大值
		String platmemmax="";
		if(platmemhash.get("max")!=null){
			platmemmax = (String)platmemhash.get("max");
	     }
	
		maxPlatmemhash.put("platMemMax",platmemmax);
		  //plat memory 平均值
		String platmemavg="";
		if(platmemhash.get("avgmemory")!=null){
			platmemavg = (String)platmemhash.get("avgmemory");
	     }
		maxPlatmemhash.put("platMemAvg",platmemavg);
		
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		request.setAttribute("cpuMaxAvg", maxhash);
		request.setAttribute("serMemMaxAvg", maxSermemhash);
		request.setAttribute("platMemMaxAvg", maxPlatmemhash);
		return "/detail/dom_cpu.jsp";
	}
	private String dommail(){
		
        String id=getParaValue("id");
		String flag=getParaValue("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/detail/dom_mail.jsp";
	}
	private String domldap(){
		
		String id=getParaValue("id");
		String flag=getParaValue("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/detail/dom_ldap.jsp";
	}
	private String domhttp(){
		
		String id=getParaValue("id");
		String flag=getParaValue("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/detail/dom_http.jsp";
	}
	private String domcache(){
		
		String id=getParaValue("id");
		String flag=getParaValue("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/detail/dom_cache.jsp";
	}
	private String domserver(){
		
		String id=getParaValue("id");
		String flag=getParaValue("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/detail/dom_server.jsp";
	}
	private String domdb(){
		
		String id=getParaValue("id");
		String flag=getParaValue("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/detail/dom_db.jsp";
	}
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return ready_add();
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        	return ready_edit();
        if(action.equals("update"))
            return update();
        if(action.equals("addalert"))
            return addalert();
        if(action.equals("cancelalert"))
            return cancelalert();
        if(action.equals("detail"))
            return domcpu();
        if(action.equals("search"))
            return search();
        if(action.equals("domcpu"))
        	return domcpu();
        if(action.equals("dommail"))
        	return dommail();
        if(action.equals("domldap"))
        	return domldap();
        if(action.equals("domhttp"))
        	return domhttp();
        if(action.equals("domserver"))
        	return domserver();
        if(action.equals("domcache"))
        	return domcache();
        if(action.equals("domdb"))
        	return domdb();
        
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	public String getStartTime(){
		String startTime = request.getParameter("startTime");
		if(startTime == null){
			startTime = getStartDate() + " 00:00:00";
		}
		return startTime;
	}
	
	public String getToTime(){
		String toTime = request.getParameter("toTime");
		if(toTime == null){
			toTime = getToDate() + " 23:59:59";
		}
		return toTime;
	}
	public String getStartDate(){
		String startdate = request.getParameter("startdate");
		if(startdate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startdate = sdf.format(new Date());
		}
		return startdate;
	}
	
	public String getToDate(){
		String toDate = request.getParameter("todate");
		if(toDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate = sdf.format(new Date());
		}
		return toDate;
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