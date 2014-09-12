/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.MQchannelConfigDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.detail.service.mqInfo.MQInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.MailLoader;
import com.afunms.polling.loader.MqLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.MQ;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.abstraction.ExcelReport3;
import com.afunms.report.base.ImplementorReport1;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class MQManager extends BaseManager implements ManagerInterface
{
	
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	DateE datemanager = new DateE();
	
	
	
	
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
		List list = null;
		MQConfigDao configdao = new MQConfigDao();
		try{
			if(operator.getRole()==0){
				list = configdao.loadAll();
			}else
				list = configdao.getMQByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		if(list == null)list = new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			MQConfig vo = (MQConfig)list.get(i);
			Node mqNode = PollingEngine.getInstance().getMqByID(vo.getId());
			if(mqNode==null)
			   vo.setStatus(0);
			else
			   vo.setStatus(mqNode.getStatus());	
		}
		request.setAttribute("list",list);	
		return "/application/mq/list.jsp";
	}
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_add(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/mq/add.jsp";
	}

	private String add()
    {    	   
		MQConfig vo = new MQConfig();
		
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setName(getParaValue("name"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setManagername(getParaValue("managername"));
		vo.setPortnum(getParaIntValue("portnum"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));

        
        vo.setNetid(getParaValue("bid"));
        
        MQConfigDao dao = new MQConfigDao();
        try{
        	dao.save(vo);
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin add for time-sharing 2010-01-04
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("9"));
            /* 增加采集时间设置 snow add at 2010-5-20 */
            TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
            timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("9"));
      		/* snow add end*/
        
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
        
        
        AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(vo.getId() + "", AlarmConstant.TYPE_MIDDLEWARE, "mq");
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(vo.getId() + "", AlarmConstant.TYPE_MIDDLEWARE, "mq", "4", 4);
		
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
//    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> mqHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的DB2指标  <dbid:Hashtable<name:NodeGatherIndicators>>

    	try{
    		//获取被启用的mq所有被监视指标
    		monitorItemList = indicatorsdao.getByNodeId(vo.getId()+"",1,"middleware","mq");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		indicatorsdao.close();
    	}
    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
    	Hashtable gatherHash = new Hashtable();
    	for(int i=0;i<monitorItemList.size();i++){
    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
    		gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    	}
        
    	
        //保存应用
		HostApplyManager.save(vo);
        
        //在轮询线程中增加被监视节点
        MqLoader loader = new MqLoader();
        try{
        	loader.loadOne(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	loader.close();
        }
        try{
    		dao = new MQConfigDao();
			List _list = dao.loadAll();
			if(_list == null)_list = new ArrayList();
			ShareData.setMqlist(_list);
			MqLoader _loader = new MqLoader();
			_loader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			dao.close();
		}
        return "/mq.do?action=list&jp=1";
    }    
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		MQConfig vo = new MQConfig();
		List list = new ArrayList();
		MQConfigDao configdao = new MQConfigDao();
		try{
			if(ids != null && ids.length > 0){	
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil(); // snow add at 2010-5-20 删除数据库采集时间
	    		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();   // nielin add for time-sharing at 2010-01-04
	    		for(int i=0;i<ids.length;i++){
	    			timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("9")); // snow add at 2010-5-20 
	    			timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("9"));
	    			Node node = PollingEngine.getInstance().getMqByID(Integer.parseInt(ids[i]));
	    			//删除应用
					HostApplyDao hostApplyDao = null;
					try{
						hostApplyDao = new HostApplyDao();
						hostApplyDao.delete(" where ipaddress = '"+node.getIpAddress()+"' and subtype = 'mq' and nodeid = '"+ids[i]+"'");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(hostApplyDao != null){
							hostApplyDao.close();
						}
					}
	    			PollingEngine.getInstance().deleteMqByID(Integer.parseInt(ids[i]));
	    			
	    			//删除该数据库的采集指标
	    			NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
	    			try {
	    				gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "middleware", "mq");
	    			} catch (RuntimeException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}finally{
	    				gatherdao.close();
	    			}
	    			//删除该数据库的告警阀值
	    			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
	    			try {
	    				indidao.deleteByNodeId(ids[i], "middleware", "mq");
	    			} catch (RuntimeException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}finally{
	    				indidao.close();
	    			}
	    			
	    		}
	    		configdao.delete(ids);
	    		
	    		//删除MQ临时表信息
	    		//根据nodeid和数据库id删除存储采集数据表的数据
				DBDao dbDao = new DBDao();
				String[] tableNames = {"nms_mq_temp"};
				try {
					dbDao.clearTablesDataByNodeIds(tableNames, ids);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}finally{
					dbDao.close();
				}
	    	}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
	  	try{
	  		configdao = new MQConfigDao();
			List _list = configdao.loadAll();
			if(_list == null)_list = new ArrayList();
			ShareData.setMqlist(_list);
			MqLoader loader = new MqLoader();
			loader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
		if(ids != null && ids.length > 0){	
    		MQchannelConfigDao channeldao = new MQchannelConfigDao();
    		try{
    			channeldao.delete(ids);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			channeldao.close();
    		}
    	}
		return "/mq.do?action=list";
		
	}
	
	/**
	 * @author nielin add for sms
	 * @since 2009-12-31
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/mq/edit.jsp";
		List  timeShareConfigList = new ArrayList();
		MQConfigDao dao = new MQConfigDao();
		try{
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("9"));
		    /* 获得设备的采集时间 snow add at 2010-05-18 */
			//提供供应商信息
			SupperDao supperdao = new SupperDao();
	    	List<Supper> allSupper = supperdao.loadAll();
	    	request.setAttribute("allSupper", allSupper);
	    	//提供已设置的采集时间信息
	    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
	    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("9"));
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
		MQConfig vo = new MQConfig();
		
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";		
		
    	vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setManagername(getParaValue("managername"));
		vo.setPortnum(getParaIntValue("portnum"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMon_flag(getParaIntValue("mon_flag"));
		//vo.setNetid(rs.getString("netid"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));

        
        vo.setNetid(getParaValue("bid"));
        MQConfigDao configdao = new MQConfigDao();
        try{
        	configdao.update(vo);	
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("9"));
            /* 增加采集时间设置 snow add at 2010-5-20 */
            TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
            timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("9"));
      		/* snow add end*/
        
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	configdao.close();
        }
      	try{
      		configdao = new MQConfigDao();
			List _list = configdao.loadAll();
			if(_list == null)_list = new ArrayList();
			ShareData.setMqlist(_list);
			MqLoader loader = new MqLoader();
			loader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
        if(PollingEngine.getInstance().getMqByID(vo.getId())!=null)
        {        
           com.afunms.polling.node.MQ mq = (com.afunms.polling.node.MQ)PollingEngine.getInstance().getMqByID(vo.getId());
           mq.setName(vo.getName());
       		mq.setIpaddress(vo.getIpaddress());
       		mq.setManagername(vo.getManagername());
       		mq.setPortnum(vo.getPortnum());
       		mq.setSendemail(vo.getSendemail());
       		mq.setSendmobiles(vo.getSendmobiles());
       		mq.setSendphone(vo.getSendphone());
       		mq.setBid(vo.getNetid());
       		mq.setMon_flag(vo.getMon_flag());
        }
        try{
      		configdao = new MQConfigDao();
			List _list = configdao.loadAll();
			if(_list == null)_list = new ArrayList();
			ShareData.setMqlist(_list);
			MqLoader loader = new MqLoader();
			loader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
        return "/mq.do?action=list";
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
	/**
	 * 
	 * @description 添加监视
	 * @author wangxiangyong update
	 * @date Mar 31, 2013 11:10:44 AM
	 * @return String  
	 * @return
	 */
	private String addalert()
    {    
		MQConfig vo = new MQConfig();
		MQConfigDao configdao = new MQConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (MQConfig)configdao.findByID(getParaValue("id"));
			vo.setMon_flag(1);
			configdao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		//加载到内存
		MqLoader loader = new MqLoader();
		loader.loading();
		
		return "/mq.do?action=list";
    }
	/**
	 * 
	 * @description 取消监视
	 * @author wangxiangyong update
	 * @date Mar 31, 2013 11:10:44 AM
	 * @return String  
	 * @return
	 */
	private String cancelalert()
    {    
		MQConfig vo = new MQConfig();
		MQConfigDao configdao = new MQConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (MQConfig)configdao.findByID(getParaValue("id"));
			vo.setMon_flag(0);
			configdao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		//加载到内存
		MqLoader loader = new MqLoader();
		loader.loading();
		return "/mq.do?action=list";
    }
	
	private String detail(){
//      List remotelist = new ArrayList();
//      List locallist = new ArrayList();
		Hashtable rValue = new Hashtable();
      Vector mqValue = new Vector();//ping信息
      
      MQConfig vo = new MQConfig();
      String runmodel = PollingEngine.getCollectwebflag(); 
		MQConfigDao configdao = new MQConfigDao();
		
      try{
      	vo = (MQConfig)configdao.findByID(getParaValue("id"));
      	String ip = vo.getIpaddress();
      	if("0".equals(runmodel)){
             	//采集与访问是集成模式	
	        	Hashtable allMqValues = ShareData.getMqdata();
	        	if (allMqValues != null && allMqValues.size()>0){
					rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
	        	}
      	}else{
      	 	//采集与访问是分离模式
      		MQInfoService mqInfoService = new MQInfoService();
      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
      	}
      	double mqAvgPing = getMqAvgPing(vo);
      	request.setAttribute("mqAvgPing", mqAvgPing);
          request.setAttribute("vo", vo);
      }catch(Exception e){
      	e.printStackTrace();
      }finally{
      	configdao.close();
      }
//		remotelist = (List)rValue.get("remote");
//		locallist = (List)rValue.get("local");
		if(mqValue == null)mqValue = new Vector();
//		if(remotelist == null)remotelist = new ArrayList();
//		if(locallist == null)locallist = new ArrayList();
      request.setAttribute("mqValue", (Vector)rValue.get("mqValue"));
      request.setAttribute("collecttime", (String)rValue.get("collecttime"));
      request.setAttribute("basicInfoHashtable", (Hashtable)rValue.get("basicInfoHashtable"));//基本信息
      request.setAttribute("chstatusList", (List)rValue.get("chstatusList"));//通道信息
      request.setAttribute("localQueueList", (List)rValue.get("localQueueList"));//本地队列信息
      request.setAttribute("remoteQueueList", (List)rValue.get("remoteQueueList"));//远程队列信息
//      request.setAttribute("remote", remotelist);
//      request.setAttribute("local", locallist);
      String type = request.getParameter("type");
      
//      System.out.println("-------------------------->"+type);
      
      String path = "";
      if(type != null){
        if(type.equalsIgnoreCase("system")){
     	    path = "/application/mq/sysdetail.jsp";
        }else if(type.equalsIgnoreCase("channel")){
    	    path = "/application/mq/channel.jsp";
        }else if(type.equalsIgnoreCase("monitor")){
    	    path = "/application/mq/monitor.jsp";
        }else if(type.equalsIgnoreCase("remoteQueue")){
    	    path = "/application/mq/remoteQueue.jsp";
        }else if(type.equalsIgnoreCase("localQueue")){
    	    path = "/application/mq/localQueue.jsp";
        }
      }else{
    	  path = "/application/mq/sysdetail.jsp";
      }
		return path;
  }
	
	/**
	 * 得到mq的平均利用率
	 * @param vo
	 * @return
	 */
	private double getMqAvgPing(MQConfig vo) {
		double avgpingcon=0;
		if(vo == null){
			return avgpingcon;
		}
		String pingconavg ="0";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		
		Hashtable ConnectUtilizationhash = new Hashtable();
		I_HostCollectData hostmanager=new HostCollectDataManager();
		try{
			ConnectUtilizationhash = hostmanager.getCategory(vo.getIpaddress(),"MqPing","ConnectUtilization",starttime1,totime1);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if (ConnectUtilizationhash.get("avgpingcon")!=null)
			pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
		if(pingconavg != null){
			pingconavg = pingconavg.replace("%", "");
		}
		  avgpingcon = new Double(pingconavg+"").doubleValue();
		return avgpingcon;
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
            return detail();
        if(action.equals("search"))
            return search();
        if(action.equals("alarm"))
        	return alarm();
        if(action.equalsIgnoreCase("downloadEventReport"))
        	return downloadEventReport();
        if(action.equalsIgnoreCase("eventReport")) 
        	return eventReport();
        if(action.equalsIgnoreCase("allReport"))
        	return allReport();
        if(action.equalsIgnoreCase("downloadAllReport"))
        	return downloadAllReport();
        if(action.equalsIgnoreCase("showPingReport"))
        	return showPingReport();
      
        
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
	
	 public String alarm(){
			String id=request.getParameter("id");
			MQConfigDao jbosscconfigdao=new MQConfigDao();
			MQConfig jbossconf=null;
	    	List list = new ArrayList();
	    	String flag = getParaValue("flag");
	    	try{
	    		jbossconf = (MQConfig) jbosscconfigdao.findByID(id);
	    	}catch(Exception e){
				e.printStackTrace();
			}finally{
				jbosscconfigdao.close();
			}
	    	try{
	    	
	    		int	status = getParaIntValue("status");
		    	int	level1 = getParaIntValue("level1");
		    	if(status == -1)status=99;
		    	if(level1 == -1)level1=99;
		    	request.setAttribute("status", status);
		    	request.setAttribute("level1", level1);
		    	
		    	String	b_time = getParaValue("startdate");
		    	String	t_time = getParaValue("todate");
		    	
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				if (b_time == null){
					b_time = sdf1.format(new Date());
				}
				if (t_time == null){
					t_time = sdf1.format(new Date());
				}
				String starttime1 = b_time + " 00:00:00";
				String totime1 = t_time + " 23:59:59";
				request.setAttribute("startdate", b_time);
				request.setAttribute("todate", t_time);
				try{
					User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
					EventListDao eventdao = new EventListDao();
					list = eventdao.getQuery(starttime1,totime1,"mq",status+"",level1+"",
							user.getBusinessids(),jbossconf.getId());				
				}catch(Exception ex){
					ex.printStackTrace();
				}
		        
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				//weblogicconfigdao.close();
			}
			
			com.afunms.polling.node.MQ jboss = (com.afunms.polling.node.MQ)PollingEngine.getInstance().getMqByID(Integer.parseInt(id)); 
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			/*===========for status start==================*/
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(jboss);
			if(nodeDTO!=null){
				String chexkname = id+":"+nodeDTO.getType()+":"+nodeDTO.getSubtype()+":";
				//System.out.println(chexkname);
				if(checkEventHashtable!=null){
					for(Iterator it = checkEventHashtable.keySet().iterator();it.hasNext();){ 
				        String key = (String)it.next(); 
				        if(key.startsWith(chexkname)){
				        	if(alarmLevel < (Integer) checkEventHashtable.get(key)){
				        		alarmLevel = (Integer) checkEventHashtable.get(key); 
				        	}
				        }
					}
				}
			}
			request.setAttribute("alarmLevel",alarmLevel);
	    	
	    	request.setAttribute("id", id);
	    	request.setAttribute("mqConfig", jbossconf);
	    	request.setAttribute("list", list);
	    	request.setAttribute("flag", flag);
	    	return "/application/mq/alarm.jsp";
			
		}
	 
	 
	 public String eventReport(){
		 	Vector vector = new Vector();
			
			String ip="";
			String tmp ="";
			List list = new ArrayList();
			int status =99;
			int level1 = 99;
			String b_time ="";
			String t_time = "";
			
			try {
				
		    	tmp = request.getParameter("id");
		    	com.afunms.polling.node.MQ jboss = (com.afunms.polling.node.MQ)PollingEngine.getInstance().getMqByID(Integer.parseInt(tmp));
				ip=jboss.getIpAddress();
				String newip=doip(ip);
		    	status = getParaIntValue("status");
		    	level1 = getParaIntValue("level1");
		    	if(status == -1)status=99;
		    	if(level1 == -1)level1=99;
		    	request.setAttribute("status", status);
		    	request.setAttribute("level1", level1);
		    	
		    	b_time = getParaValue("startdate");
				t_time = getParaValue("todate");
		    	
				if (b_time == null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					b_time = sdf.format(new Date());
				}
				if (t_time == null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					t_time = sdf.format(new Date());
				}
				String starttime1 = b_time + " 00:00:00";
				String totime1 = t_time + " 23:59:59";
		    	
			
				try{
					User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
					//SysLogger.info("user businessid===="+vo.getBusinessids());
					EventListDao dao = new EventListDao();
					
					list = dao.getQuery(starttime1,totime1,status+"",level1+"",
							vo.getBusinessids(),Integer.parseInt(tmp),"mq");
					
					//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
//				try {
//					Hashtable hash1 = getCategory(ip,"IISPing","ConnectUtilization",starttime1,totime1);						
//					Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1);						
//					if(hash1!=null)
//					request.setAttribute("pingcon", hash1);
//					if(hash!=null)
//						request.setAttribute("avgjvm", hash);	
//				} catch(Exception ex) {
//					ex.printStackTrace();
//				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			request.setAttribute("vector",vector);
			request.setAttribute("id", Integer.parseInt(tmp));
			request.setAttribute("list", list);
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			return "/application/mq/eventReport.jsp";
		}
	 
	 
	//event 报表
	 private String downloadEventReport() {
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

			String id = request.getParameter("id");

		    Hashtable reporthash = new Hashtable();
			// 按排序标志取各端口最新记录的列表
			String orderflag = "ipaddress";
			if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
				orderflag = getParaValue("orderflag");
			}
					Node iisNode = PollingEngine.getInstance().getJBossByID(Integer.parseInt(id));
					String ip = iisNode.getIpAddress();
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();
					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
							+ totime + "' ");
					s.append(" and nodeid=" + iisNode.getId());

					List infolist = eventdao.findByCriteria(s.toString());
					reporthash.put("eventlist", infolist);
					
			// 画图-----------------------------
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String file = "temp/MQEventReport.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				try {
					report1.createReport_midEventDoc(fileName,starttime,totime,"MQ("+ip+")");
				} catch (IOException e) {
					e.printStackTrace();
				}// word事件报表分析表
				request.setAttribute("filename", fileName);
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String file = "temp/MQEventReport.xls";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				try {
					report1.createReport_TomcatEventExc(file,id,starttime,totime,"MQ("+ip+")");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// xls事件报表分析表
				request.setAttribute("filename", fileName);
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String file = "temp/MQEventReport.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				try {
					report1.createReport_midEventPdf(fileName,starttime,totime,"MQ("+ip+")");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// pdf事件报表分析表
				request.setAttribute("filename", fileName);
			}
			return "/capreport/service/download.jsp";
		}
	 
	 private String doip(String ip){
		 ip =  SysUtil.doip(ip);
		 return ip;
	}
	 
	 
	 
	 private String allReport(){
		  Hashtable rValue = new Hashtable();
	      Vector mqValue = new Vector();//ping信息
	      
	      MQConfig vo = new MQConfig();
	      String runmodel = PollingEngine.getCollectwebflag(); 
			MQConfigDao configdao = new MQConfigDao();
			
	      try{
	      	vo = (MQConfig)configdao.findByID(getParaValue("id"));
	      	String ip = vo.getIpaddress();
	      	if("0".equals(runmodel)){
	             	//采集与访问是集成模式	
		        	Hashtable allMqValues = ShareData.getMqdata();
		        	if (allMqValues != null && allMqValues.size()>0){
						rValue = (Hashtable)allMqValues.get(ip+":"+vo.getManagername());
		        	}
	      	}else{
	      	 	//采集与访问是分离模式
	      		MQInfoService mqInfoService = new MQInfoService();
	      		rValue = mqInfoService.getMQDataHashtable(vo.getId()+"");
	      	}
	      	double mqAvgPing = getMqAvgPing(vo);
	      	request.setAttribute("mqAvgPing", mqAvgPing);
	          request.setAttribute("vo", vo);
	      }catch(Exception e){
	      	e.printStackTrace();
	      }finally{
	      	configdao.close();
	      }
		  if(mqValue == null)mqValue = new Vector();
	      request.setAttribute("mqValue", (Vector)rValue.get("mqValue"));
	      request.setAttribute("collecttime", (String)rValue.get("collecttime"));
	      request.setAttribute("basicInfoHashtable", (Hashtable)rValue.get("basicInfoHashtable"));//基本信息
	      request.setAttribute("chstatusList", (List)rValue.get("chstatusList"));//通道信息
	      request.setAttribute("localQueueList", (List)rValue.get("localQueueList"));//本地队列信息
	      request.setAttribute("remoteQueueList", (List)rValue.get("remoteQueueList"));//远程队列信息
	      String type = request.getParameter("type");
	      
//	      System.out.println("-------------------------->"+type);
	      
	      String path = "";
	      if(type != null){
	        if(type.equalsIgnoreCase("monitor")){
	    	    path = "/application/mq/monitorReport.jsp";
	        }else if(type.equalsIgnoreCase("queue")){
	    	    path = "/application/mq/queueReport.jsp";
	        }else if(type.equalsIgnoreCase("all")){
	    	    path = "/application/mq/allReport.jsp";
	        }
	      }else{
	    	  path = "/application/mq/sysdetail.jsp";
	      }
			return path;
	  }
	 
	 
	 private String downloadAllReport() {
		    Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
			// 画图-----------------------------
		    ExcelReport3 report = new ExcelReport3(new IpResourceReport(),
					reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			String id = request.getParameter("id");
			String flag = request.getParameter("flag");
			String type = request.getParameter("type");
		if("all".equalsIgnoreCase(type)){
				if ("0".equals(str)) {
					// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
					report.createReportxls_mq_per("temp/MQ_AllReport.xls",id);
					request.setAttribute("filename", report.getFileName());
				} else if ("1".equals(str)) {
					ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(),
							reporthash);
					try {
						String file = "temp/MQ_AllReport.doc";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath()
								+ file;// 获取系统文件夹路径
						report1.createReportDoc_mq_per(fileName,"doc",id);// word综合报表
						request.setAttribute("filename", fileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("2".equals(str)) {
					ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(),
							reporthash);
					try {
						String file = "temp/MQ_AllReport.pdf";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath()
								+ file;// 获取系统文件夹路径
						// report1.createReport_hostPDF(fileName);// pdf综合报表
						report1.createReportDoc_mq_per(fileName,"pdf",id);
						request.setAttribute("filename", fileName);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}else if("monitor".equalsIgnoreCase(type)){
			if ("0".equals(str)) {
				// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
				report.createReportxls_mq_monitor("temp/MQ_monitorReport.xls",id);
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(),
						reporthash);
				try {
					String file = "temp/MQ_monitorReport.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath()
							+ file;// 获取系统文件夹路径
					report1.createReportDoc_mq_monitor(fileName,"doc",id);// word综合报表
					request.setAttribute("filename", fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(),
						reporthash);
				try {
					String file = "temp/MQ_monitorReport.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath()
							+ file;// 获取系统文件夹路径
					// report1.createReport_hostPDF(fileName);// pdf综合报表
					report1.createReportDoc_mq_monitor(fileName,"pdf",id);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}else if("queue".equalsIgnoreCase(type)){
		if ("0".equals(str)) {
			// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			report.createReportxls_mq_queue("temp/MQ_queueReport.xls",id);
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/MQ_queueReport.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径
				report1.createReportDoc_mq_queue(fileName,"doc",id);// word综合报表
				request.setAttribute("filename", fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/MQ_queueReport.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径
				// report1.createReport_hostPDF(fileName);// pdf综合报表
				report1.createReportDoc_mq_queue(fileName,"pdf",id);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
			return "/capreport/service/download.jsp";

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
			com.afunms.polling.node.MQ jboss = (com.afunms.polling.node.MQ)PollingEngine.getInstance().getMqByID(queryid);
			try {
//				ip = getParaValue("ipaddress");
	            ip = jboss.getIpAddress();
				newip = SysUtil.doip(ip);

				MQConfigDao mq = new MQConfigDao();

				Hashtable ConnectUtilizationhash = mq.getPingDataById(newip,
						queryid, starttime, totime);
				String curPing = "";
				String pingconavg = "";
				if (ConnectUtilizationhash.get("avgPing") != null)
					pingconavg = (String) ConnectUtilizationhash.get("avgPing");
				String minPing = "";

				if (ConnectUtilizationhash.get("minPing") != null) {
					minPing = (String) ConnectUtilizationhash.get("minPing");
				}
				// Ftpmonitor_realtimeDao realDao=new Ftpmonitor_realtimeDao();
				// List curList=realDao.getByFTPId(queryid);
				// Ftpmonitor_realtime ftpReal=new Ftpmonitor_realtime();
				// ftpReal=(Ftpmonitor_realtime) curList.get(0);
				// int ping=ftpReal.getIs_canconnected();
				// if (ping==1) {
				// curPing="100";
				// }else{
				// curPing="0";
				// }
				if (ConnectUtilizationhash.get("curPing") != null) {
					curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
																				// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
				}

				// 画图----------------------
				String timeType = "minute";
				PollMonitorManager pollMonitorManager = new PollMonitorManager();
				pollMonitorManager.chooseDrawLineType(timeType,
						ConnectUtilizationhash, "连通率", newip + "mqpingConnect",
						740, 150);

				// 画图-----------------------------
				reporthash.put("servicename",jboss.getAlias() );
				reporthash.put("Ping", curPing);
				reporthash.put("ip", ip);
				reporthash.put("ping", ConnectUtilizationhash);
				reporthash.put("starttime", startdate);
				reporthash.put("totime", totime);
				request.setAttribute("id", String.valueOf(queryid));
				request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
				request.setAttribute("Ping", curPing);
				request.setAttribute("avgpingcon", pingconavg);
				request.setAttribute("newip", newip);
				request.setAttribute("ipaddress", ip);
				request.setAttribute("startdate", startdate);
				request.setAttribute("todate", todate);
				request.setAttribute("type", "tftp");
				session.setAttribute("reporthash", reporthash);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "/application/mq/showPingReport.jsp";
		}
	 
	 
	 

	 
	
}