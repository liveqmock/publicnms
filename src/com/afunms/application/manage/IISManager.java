/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.IISVo;
import com.afunms.application.model.MQConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
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
import com.afunms.polling.loader.FtpLoader;
import com.afunms.polling.loader.IISLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.snmp.IISSnmp;
import com.afunms.polling.task.IISDataCollector;
import com.afunms.report.abstraction.ExcelReport1;
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
import com.lowagie.text.DocumentException;

public class IISManager extends BaseManager implements ManagerInterface
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
		
		IISConfigDao configdao = new IISConfigDao();	
		List list = new ArrayList();
		try{
			//if(operator.getRole() == 0){
				list = configdao.loadAll();
			//}else{
			//	list = configdao.getIISByBID(rbids);
			//}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		request.setAttribute("list",list);
		return "/application/iis/list.jsp";	
	}
	
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_add(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/iis/add.jsp";
	}
	

	private String add()
    {    	  	
		
		IISConfig vo=new IISConfig();
		
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
        IISConfigDao dao=new IISConfigDao();
        
        /*
         * 判断是否有相同信息
         * */
        List ipExistList = new IISConfigDao().getAllByIp(vo);
        if(ipExistList!=null&&ipExistList.size()>0){
        	IISConfig iisc = (IISConfig)ipExistList.get(0);
        	//判断IP是否重复
    		if(vo.getIpaddress().equals(iisc.getIpaddress())){
    			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
        		return null;
    		}
    		//判断NAME是否重复
    		if(vo.getName().equals(iisc.getName())){
    			setErrorCode(ErrorMessage.SERVICE_EXIST);
        		return null;
    		}
        }
        
        try{
        	dao.save(vo);
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();  // nielin add for time-sharing at 2010-01-05
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("14"));
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("14"));
        	/* snow add end*/
			
            //初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId()+"", "middleware", "iis","1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "middleware", "iis");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//保存应用
        	HostApplyManager.save(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
       
       
        //在轮询线程中增加被监视节点
        IISLoader loader = new IISLoader();
        try{
        	loader.loadOne(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	loader.close();
        }
		 try{
			 dao=new IISConfigDao();
        	 List list = dao.loadAll();
     		if(list == null)list = new ArrayList();
     		ShareData.setIislist(list);
     		IISLoader iisloader = new IISLoader();
     		iisloader.clearRubbish(list);
		}catch(Exception e){
				
		}finally{
			dao.close();
		}
        return "/iis.do?action=list";
    }    
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		MQConfig vo = new MQConfig();
		List list = new ArrayList();
		
    	if(ids != null && ids.length > 0){
    		// nielin modify 2010-01-05
    		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();  // nielin add for time-sharing at 2010-01-05
    		TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil(); // snow add at 2010-5-20 删除数据库采集时间
    		List l = new ArrayList();
    		for(int i=0;i<ids.length;i++){
    			Node node = PollingEngine.getInstance().getIisByID(Integer.parseInt(ids[i]));
    			//删除应用
				HostApplyDao hostApplyDao = null;
				try{
					hostApplyDao = new HostApplyDao();
					hostApplyDao.delete(" where ipaddress = '"+node.getIpAddress()+"' and subtype = 'iis' and nodeid = '"+ids[i]+"'");
					 CreateTableManager createTableManager = new CreateTableManager();
				     String[] tablename = {"nms_iislogconfig"};
				     l.add(node.getIpAddress());
				     String[] ipaddress = new String[l.size()];
				     ipaddress[i] = (String) l.get(i);
				     createTableManager.clearTablesData(tablename,"ipaddress",ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(hostApplyDao != null){
						hostApplyDao.close();
					}
				}
    			PollingEngine.getInstance().deleteIisByID(Integer.parseInt(ids[i]));
    			timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("14"));
    			timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("14")); // snow add at 2010-5-20
    			
    			//删除该数据库的采集指标
    			NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
    			try {
    				gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "middleware", "iis");
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}finally{
    				gatherdao.close();
    			}
    			//删除该数据库的告警阀值
    			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
    			try {
    				indidao.deleteByNodeId(ids[i], "middleware", "iis");
    			} catch (RuntimeException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}finally{
    				indidao.close();
    			}
    			
    			//更新业务视图
    			String id = ids[i];
    			NodeDependDao nodedependao = new NodeDependDao();
    			List weslist = nodedependao.findByNode("iis"+id);
    			if(weslist!=null&&weslist.size()>0){
    				for(int j = 0; j < weslist.size(); j++){
    					NodeDepend wesvo = (NodeDepend)weslist.get(j);
    					if(wesvo!=null){
    						LineDao lineDao = new LineDao();
    		    			lineDao.deleteByidXml("iis"+id, wesvo.getXmlfile());
    		    			NodeDependDao nodeDependDao = new NodeDependDao();
    		    			if(nodeDependDao.isNodeExist("iis"+id, wesvo.getXmlfile())){
    		            		nodeDependDao.deleteByIdXml("iis"+id, wesvo.getXmlfile());
    		            	} else {
    		            		nodeDependDao.close();
    		            	}
    		    			
    		    			//yangjun
    		    			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
    		    			ManageXmlDao mXmlDao =new ManageXmlDao();
    		    			List xmlList = new ArrayList();
    		    			try{
    		    				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
    		    			}catch(Exception e){
    		    				e.printStackTrace();
    		    			}finally{
    		    				mXmlDao.close();
    		    			}
    		    			try{
    		    				ChartXml chartxml;
    		    			    chartxml = new ChartXml("tree");
    		    			    chartxml.addViewTree(xmlList);
    		    		    }catch(Exception e){
    		    			    e.printStackTrace();   	
    		    		    }
    		                
    		                ManageXmlDao subMapDao = new ManageXmlDao();
    		    			ManageXml manageXml = (ManageXml) subMapDao.findByXml(wesvo.getXmlfile());
    		    			if(manageXml!=null){
    		    				NodeDependDao nodeDepenDao = new NodeDependDao();
    		    				try{
    		    				    List lists = nodeDepenDao.findByXml(wesvo.getXmlfile());
    		    				    ChartXml chartxml;
    		    					chartxml = new ChartXml("NetworkMonitor","/"+wesvo.getXmlfile().replace("jsp", "xml"));
    		    					chartxml.addBussinessXML(manageXml.getTopoName(),lists);
    		    					ChartXml chartxmlList;
    		    					chartxmlList = new ChartXml("NetworkMonitor","/"+wesvo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
    		    					chartxmlList.addListXML(manageXml.getTopoName(),lists);
    		    				}catch(Exception e){
    		    				    e.printStackTrace();   	
    		    				}finally{
    		    					nodeDepenDao.close();
    		                    }
    		    			}
    					}
    				}
    			}
    		}
			//删除IIS在临时表里中存储的数据
	        String[] nmsTempDataTables = {"nms_iis_temp","system_eventlist"};
	        CreateTableManager createTableManager = new CreateTableManager();
	        createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
//	        com.afunms.common.base.DaoInterface iis = new IISConfigDao();
//	        IISConfig is_vo = (IISConfig) iis.findByID(ids[0]);
//	        String[] ipaddress = {is_vo.getIpaddress()};
//	        String[] tablename = {"nms_iislogconfig"};
//	        createTableManager.clearTablesData(tablename,"ipaddress",ipaddress);
    		IISConfigDao configdao = new IISConfigDao();
    		try{
    			configdao.delete(ids);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			configdao.close();
    		}
    	      try{
    	    	  configdao=new IISConfigDao();
    	        	 List _list = configdao.loadAll();
    	     		if(_list == null)_list = new ArrayList();
    	     		ShareData.setIislist(_list);
    	     		IISLoader iisloader = new IISLoader();
    	     		iisloader.clearRubbish(_list);
    			}catch(Exception e){
    					
    			}finally{
    				configdao.close();
    			}
    	}
		return "/iis.do?action=list";
	}
	
	/**
	 * @author nielin add for time-sharing at 2010-01-05
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/iis/edit.jsp";
		List timeShareConfigList = new ArrayList();
		IISConfigDao dao = new IISConfigDao();
		try{
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); 
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("14"));
			
			/* 获得设备的采集时间 snow add at 2010-05-20 */
			//提供供应商信息
			SupperDao supperdao = new SupperDao();
	    	List<Supper> allSupper = supperdao.loadAll();
	    	request.setAttribute("allSupper", allSupper);
	    	//提供已设置的采集时间信息
	    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
	    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("14"));
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
		IISConfig vo = new IISConfig();
		
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
        IISConfigDao configdao = new IISConfigDao();
        
        /*
         * 判断是否有相同信息
         * */
        List ipExistList = new IISConfigDao().getAllByIp(vo);
        IISConfig iisc;
        if(ipExistList!=null&&ipExistList.size()>1){
        	System.out.println("com.afunms.application.manage.IISManager 有没有进来459行");
        	for(int i=0;i<ipExistList.size();i++){
        		iisc = (IISConfig)ipExistList.get(i);
        		if(vo.getId()!=iisc.getId()){
        			System.out.println("com.afunms.application.manage.IISManager 有没有进来463行");
        			//判断IP是否重复
            		if(vo.getIpaddress().equals(iisc.getIpaddress())){
            			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
                		return null;
            		}
            		//判断NAME是否重复
            		if(vo.getName().equals(iisc.getName())){
            			setErrorCode(ErrorMessage.SERVICE_EXIST);
                		return null;
            		}
            	}
        	}
        }else if(ipExistList!=null&&ipExistList.size()>0){
        	iisc = (IISConfig)ipExistList.get(0);
        	if(vo.getId()!=iisc.getId()){
        		//判断IP是否重复
        		if(vo.getIpaddress().equals(iisc.getIpaddress())){
        			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
            		return null;
        		}
        		//判断NAME是否重复
        		if(vo.getName().equals(iisc.getName())){
        			setErrorCode(ErrorMessage.SERVICE_EXIST);
            		return null;
        		}
        	}
        }
        
        try{
        	configdao.update(vo);	
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin add for time-sharing at 2010-01-05
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("14"));
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("14"));
        	/* snow add end*/
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	configdao.close();
        }
	      
        if(PollingEngine.getInstance().getIisByID(vo.getId())!=null)
        {        
           com.afunms.polling.node.IIS iis = (com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByID(vo.getId());
           iis.setId(vo.getId());
           iis.setAlias(vo.getName());
           iis.setName(vo.getName());
           iis.setIpAddress(vo.getIpaddress());
           iis.setCommunity(vo.getCommunity());
           iis.setSendemail(vo.getSendemail());
           iis.setSendmobiles(vo.getSendmobiles());
           iis.setSendphone(vo.getSendphone());
           iis.setBid(vo.getNetid());
           iis.setMon_flag(vo.getMon_flag());
        }
        try{
    	    configdao=new IISConfigDao();
        	 List _list = configdao.loadAll();
     		if(_list == null)_list = new ArrayList();
     		ShareData.setIislist(_list);
     		IISLoader iisloader = new IISLoader();
     		iisloader.clearRubbish(_list);
		}catch(Exception e){
				
		}finally{
			configdao.close();
		}
        return "/iis.do?action=list";
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
		IISConfig vo = new IISConfig();
		IISConfigDao configdao = new IISConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (IISConfig)configdao.findByID(getParaValue("id"));
			vo.setMon_flag(1);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		configdao = new IISConfigDao();
		try{
			configdao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		IISLoader loader=new IISLoader();
		loader.loading();
		return "/iis.do?action=list";
    }
	
	private String cancelalert()
    {    
		IISConfig vo = new IISConfig();
		IISConfigDao configdao = new IISConfigDao();
		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress ="";
		try{
			vo = (IISConfig)configdao.findByID(getParaValue("id"));
			vo.setMon_flag(0);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		configdao = new IISConfigDao();
		try{
			configdao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			configdao.close();
		}
		IISLoader loader=new IISLoader();
		loader.loading();
		return "/iis.do?action=list";
    }
	
	public String detail(){
		 
		   String id= request.getParameter("id");
		   String flag = request.getParameter("flag");
		   request.setAttribute("flag", flag);
		   com.afunms.polling.node.IIS iis = (com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByID(Integer.parseInt(id)); 
		   String ip=iis.getIpAddress();
		   	Hashtable imgurlhash=new Hashtable();
			try {
				String newip=doip(ip);
				java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		        if(to_hour == null|| "0".equals(to_hour) ){
		        	to_hour = nowdate.getHours()+"";
		        	if(to_hour.length()<2)
		        		to_hour = "0"+to_hour;
		        	request.setAttribute("to_hour", to_hour);
		        }
		        String starttime = from_date1+" "+from_hour+":00:00";
		        String totime = to_date1+" "+to_hour+":59:59";   
				request.setAttribute("starttime1", from_date1);
				request.setAttribute("totime1", to_date1);										
				try{
					Hashtable hash1 = getCategory(ip,"IISPing","ConnectUtilization",starttime,totime);						
					p_draw_line(hash1,"连通率",newip+"IISPing",740,150);
					//Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1);						
					//p_draw_line(hash,"JVM内存利用率",newip+"tomcat_jvm",740,150);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//imgurlhash
				//imgurlhash.put("tomcat_jvm","resource\\image\\jfreechart\\"+newip+"tomca_jvm"+".png");
				imgurlhash.put("IISPing","resource/image/jfreechart/"+newip+"IISPing"+".png");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			/*===========for status start==================*/
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(iis);
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
			
			request.setAttribute("imgurlhash",imgurlhash);
			
		   return  "/application/iis/iis_detail.jsp";
	   }
	
	public String eventdetail(){
		String id=request.getParameter("id");
		IISConfigDao iiscconfigdao=new IISConfigDao();
		IISConfig iisconf=null;
    	List list = new ArrayList();
    	String flag = getParaValue("flag");
    	try{
    		iisconf = (IISConfig) iiscconfigdao.findByID(id);
    	}catch(Exception e){
			e.printStackTrace();
		}finally{
			iiscconfigdao.close();
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
				list = eventdao.getQuery(starttime1,totime1,"iis",status+"",level1+"",
						user.getBusinessids(),iisconf.getId());				
			}catch(Exception ex){
				ex.printStackTrace();
			}
	        
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//weblogicconfigdao.close();
		}
		
		com.afunms.polling.node.IIS iis = (com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByID(Integer.parseInt(id)); 
		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/*===========for status start==================*/
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(iis);
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
    	request.setAttribute("iisconf", iisconf);
    	request.setAttribute("list", list);
    	request.setAttribute("flag", flag);
    	return "/application/iis/iis_alarm.jsp";
		
	}
	
	private String isOK()
    {    
		
		int queryid = getParaIntValue("id");
		IISConfig iisconf = new IISConfig();
		IISConfigDao dao = new IISConfigDao();
		try{
			iisconf = (IISConfig)dao.findByID(queryid+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> urlHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的IIS指标  <dbid:Hashtable<name:NodeGatherIndicators>>

    	try{
    		//获取被启用的所有被监视指标
    		monitorItemList = indicatorsdao.getByNodeId(queryid+"", 1,"middleware","iis");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		indicatorsdao.close();
    	}
    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
    	Hashtable gatherHash = new Hashtable();
    	for(int i=0;i<monitorItemList.size();i++){
    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
    		if(nodeGatherIndicators.getName().equalsIgnoreCase("domain"))
    			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    	}
		
		// 实时数据
		boolean flag = false;
		List list = null;
		IISSnmp iissnmp=new IISSnmp();
		try{
			list = iissnmp.collect_Data(iisconf);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(list != null && list.size()>0){
			IISVo iisvo = new IISVo();
			iisvo =(IISVo) list.get(0);
			if(iisvo.getCurrentAnonymousUsers()!=null ){
				flag = true;
			}
		}
		String reason = "WEB服务有效";
		if(!flag)reason = "WEB服务无效";

		request.setAttribute("isOK", reason);
		request.setAttribute("name", iisconf.getName());
		request.setAttribute("str", iisconf.getIpaddress());
        return "/tool/webisok.jsp";
    }
	
	private String sychronizeData()
    {    
		
		int queryid = getParaIntValue("id");
		String dbpage = getParaValue("dbPage");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> urlHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的DB2指标  <dbid:Hashtable<name:NodeGatherIndicators>>

    	try{
    		//获取被启用的所有被监视指标
    		monitorItemList = indicatorsdao.getByNodeId(queryid+"", 1,"middleware","iis");
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
    	
		IISConfig iisconf = new IISConfig();
		IISConfigDao dao = new IISConfigDao();
		try{
			iisconf = (IISConfig)dao.findByID(queryid+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
        try {         
        	NodeGatherIndicators nodeGatherIndicators = new NodeGatherIndicators();
        	nodeGatherIndicators.setNodeid(queryid+"");
        	IISDataCollector iiscollector = new IISDataCollector();
        	iiscollector.collect_Data(nodeGatherIndicators);
        }catch(Exception exc){
        	
        }
        if("detail".equalsIgnoreCase(dbpage)){
        	return "/iis.do?action=detail&id="+queryid;
        }else if("alarm".equalsIgnoreCase(dbpage)){
            return "/iis.do?action=alarm&id="+queryid;
        }else
        	return "/iis.do?action=detail&id="+queryid;
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
        if(action.equals("alarm"))
            return eventdetail();
        if(action.equals("search"))
            return search();
        if(action.equals("sychronizeData"))
            return sychronizeData();
        if(action.equals("isOK"))
            return isOK();
        if(action.equalsIgnoreCase("showPingReport")){
        	return showPingReport();
        }if(action.equalsIgnoreCase("eventReport")){
        	return event();
        }
        if(action.equalsIgnoreCase("event"))
        	{
        		return downloadEventReport();
        	}
        if(action.equalsIgnoreCase("perreport")){
        	return perReport();
        }if(action.equalsIgnoreCase("downloadReport"))
        	return downloadReport();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
public Hashtable getCollecttime(String ip) throws Exception{
    	
    	String collecttime = null;
    	String nexttime = null;
    	Hashtable pollingtime_ht= new Hashtable();
    	DBManager dbmanager = new DBManager();
    	ResultSet rs = null;
//    	String ip1 ="",ip2="",ip3="",ip4="";	
//		String tempStr = "";
//		String allipstr = "";
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
    	String allipstr = SysUtil.doip(ip);
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sb.append(" select max(DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s')) as collecttime from iisping"+allipstr+" h  ");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sb.append(" select max(to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS')) as collecttime from iisping"+allipstr+" h  ");
		}
		
		sql = sb.toString();
		SysLogger.info(sql);
		try{
			rs = dbmanager.executeQuery(sql);
			if(rs.next()){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				collecttime =rs.getString("collecttime");
				
				if(collecttime == null){
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(new Date().getTime());
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					collecttime = dateFormat.format(c.getTime()).toString();
				}
				Date date=format.parse(collecttime);
				int mins=date.getMinutes()+5;
				date.setMinutes(mins);
				nexttime=format.format(date);
				pollingtime_ht.put("lasttime", collecttime);
				pollingtime_ht.put("nexttime", nexttime);
			}
		}catch(Exception e){
			
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					
				}
			}
			dbmanager.close();
		}
	
		
		return  pollingtime_ht;
    }
	
    public double iisping(int id)
    {    	   
    	String strid = String.valueOf(id);
		IISConfig vo = new IISConfig();
		
		double avgpingcon=0;
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		try{
			IISConfigDao dao = new IISConfigDao();
			try{
				vo = (IISConfig)dao.findByID(strid);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpaddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = getCategory(vo.getIpaddress(),"IISPing","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			  avgpingcon = new Double(pingconavg+"").doubleValue();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//dao.close();
		}
		return avgpingcon;
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
	 private void getTime(HttpServletRequest request,String[] time){		
		  Calendar current = new GregorianCalendar();
		  String key = getParaValue("beginhour");
		  if(getParaValue("beginhour") == null){
			  Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			  request.setAttribute("beginhour", new Integer(hour.intValue()-1));
			  request.setAttribute("endhour", hour);
			  //mForm.setBeginhour(new Integer(hour.intValue()-1));
			  //mForm.setEndhour(hour);
		  }
		  if(getParaValue("begindate") == null){
			  current.set(Calendar.MINUTE,59);
			  current.set(Calendar.SECOND,59);
			  time[1] = datemanager.getDateDetail(current);
			  current.add(Calendar.HOUR_OF_DAY,-1);
			  current.set(Calendar.MINUTE,0);
			  current.set(Calendar.SECOND,0);
			  time[0] = datemanager.getDateDetail(current);

			  java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			  String begindate = "";
			  begindate = timeFormatter.format(new java.util.Date());
			  request.setAttribute("begindate", begindate);
			  request.setAttribute("enddate", begindate);
			  //mForm.setBegindate(begindate);
			  //mForm.setEnddate(begindate);
		 }
		 else{
			  String temp = getParaValue("begindate");
			  time[0] = temp+" "+getParaValue("beginhour")+":00:00";
			  temp = getParaValue("enddate");
			  time[1] = temp+" "+getParaValue("endhour")+":59:59";
		 }
		  if(getParaValue("startdate") == null){
			  current.set(Calendar.MINUTE,59);
			  current.set(Calendar.SECOND,59);
			  time[1] = datemanager.getDateDetail(current);
			  current.add(Calendar.HOUR_OF_DAY,-1);
			  current.set(Calendar.MINUTE,0);
			  current.set(Calendar.SECOND,0);
			  time[0] = datemanager.getDateDetail(current);

			  java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			  String startdate = "";
			  startdate = timeFormatter.format(new java.util.Date());
			  request.setAttribute("startdate", startdate);
			  request.setAttribute("todate", startdate);
			  //mForm.setStartdate(startdate);
			  //mForm.setTodate(startdate);
		 }
		 else{
			  String temp = getParaValue("startdate");
			  time[0] = temp+" "+getParaValue("beginhour")+":00:00";
			  temp = getParaValue("todate");
			  time[1] = temp+" "+getParaValue("endhour")+":59:59";
		 }
		  
	}
	 
	 private String doip(String ip){
//		  String newip="";
//		  for(int i=0;i<3;i++){
//			int p=ip.indexOf(".");
//			newip+=ip.substring(0,p);
//			ip=ip.substring(p+1);
//		  }
//		 newip+=ip;
		 ip =  SysUtil.doip(ip);
		 //System.out.println("newip="+newip);
		 return ip;
	}
	    public Hashtable getCategory(
				String ip,
				String category,
				String subentity,
				String starttime,
				String endtime)
				throws Exception {
				Hashtable hash = new Hashtable();
			 	//Connection con = null;
			 	//PreparedStatement stmt = null;
			 	DBManager dbmanager = new DBManager();
			 	ResultSet rs = null;
				try{
					//con=DataGate.getCon();
					if (!starttime.equals("") && !endtime.equals("")) {
						//con=DataGate.getCon();
//						String ip1 ="",ip2="",ip3="",ip4="";	
//						String tempStr = "";
//						String allipstr = "";
//						if (ip.indexOf(".")>0){
//							ip1=ip.substring(0,ip.indexOf("."));
//							ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//							tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
						String allipstr = SysUtil.doip(ip);
//						
						String sql = "";
						StringBuffer sb = new StringBuffer();
						 if (category.equals("IISPing")){
							 if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
								 sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from iisping"+allipstr+" h where ");
							 }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
								 sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from iisping"+allipstr+" h where ");
							 }
							
						 }
						sb.append(" h.category='");
						sb.append(category);
						sb.append("' and h.subentity='");
						sb.append(subentity);
						sb.append("' and h.collecttime >= ");
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							sb.append("'");
							sb.append(starttime);
							sb.append("'");
						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							sb.append("to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')");
						}			
						sb.append(" and h.collecttime <= ");
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							sb.append("'");
							sb.append(endtime);
							sb.append("'");
						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							sb.append("to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')");
						}						
						sb.append(" order by h.collecttime");
						sql = sb.toString();
						SysLogger.info(sql);
						
						rs = dbmanager.executeQuery(sql);
						List list1 =new ArrayList();
						String unit = "";
						String max = "";
						double tempfloat=0;
						double pingcon = 0;
						double tomcat_jvm_con = 0;
						int downnum = 0;
						int i=0;
				        while (rs.next()) {
				        	i=i+1;
				        	Vector v =new Vector();		        	
				            String thevalue=rs.getString("thevalue");
				            String collecttime = rs.getString("collecttime");		            
				            v.add(0,emitStr(thevalue));
				            v.add(1,collecttime);
				            v.add(2,rs.getString("unit"));
				            if (category.equals("IISPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
				            	pingcon=pingcon+getfloat(thevalue);
				            	if(thevalue.equals("0")){
				            		downnum = downnum + 1;
				            	}	
				            }
//				            if (category.equals("tomcat_jvm")&&subentity.equalsIgnoreCase("ConnectUtilization")){
//				            	pingcon=pingcon+getfloat(thevalue);
//				            	if(thevalue.equals("0")){
//				            		downnum = downnum + 1;
//				            	}	
//				            }
				            if (subentity.equalsIgnoreCase("ConnectUtilization")) {
				            	if (i==1)tempfloat = getfloat(thevalue);
				            	if (tempfloat > getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }else{
				            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }	
				            list1.add(v);	
				    }	
				        rs.close();
				        //stmt.close();
				        
						Integer size = new Integer(0);
						hash.put("list", list1);
						if (list1.size() != 0) {
							size = new Integer(list1.size());
							if (list1.get(0) != null) {
								Vector tempV = (Vector)list1.get(0);
								unit = (String)tempV.get(2);
							}
						}
						if (category.equals("IISPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
							if (list1 !=null && list1.size()>0){
								hash.put("avgpingcon", CEIString.round(pingcon/list1.size(),2)+unit);						
								hash.put("pingmax", tempfloat+"");
								hash.put("downnum", downnum+"");
							}else{ 
								hash.put("avgpingcon", "0.0%");	
								hash.put("pingmax", "0.0%");
								hash.put("downnum", "0");
							}
						}
						hash.put("size", size);			
						hash.put("max", CEIString.round(tempfloat,2) + unit);
						hash.put("unit", unit);
				        }
					} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (rs != null)
					rs.close();
					dbmanager.close();
				}
				
				return hash;
			}
	    
		private double getfloat(String num) {
			double snum = 0.0;
			if (num != null) {
				if (num.indexOf(".")>=0){				
					if (num.substring(num.indexOf(".")+1).length()>7){
						String tempStr = num.substring(num.indexOf(".")+1);
						num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
					}
				}
				int inum = (int) (Float.parseFloat(num) * 100);
				snum = new Double(inum/100.0).doubleValue();
			}
			return snum;
		}
		private String emitStr(String num) {
			if (num != null) {
				if (num.indexOf(".")>=0){				
					if (num.substring(num.indexOf(".")+1).length()>7){
						String tempStr = num.substring(num.indexOf(".")+1);
						num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
					}
				}
			}
			return num;
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
				com.afunms.polling.node.IIS iis = (com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByID(queryid);
				try {
//					ip = getParaValue("ipaddress");
	                ip = iis.getIpAddress();
					newip = SysUtil.doip(ip);
					String runmodel = PollingEngine.getCollectwebflag();

					IISConfigDao iisdao = new IISConfigDao();

					Hashtable ConnectUtilizationhash = iisdao.getPingDataById(ip,
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
							ConnectUtilizationhash, "连通率", newip + "iispingConnect",
							740, 150);

					// 画图-----------------------------
					reporthash.put("servicename",iis.getAlias() );
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
				return "/application/iis/showPingReport.jsp";
			}
		 
		 
		 public String event(){
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
			    	com.afunms.polling.node.IIS iis = (com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByID(Integer.parseInt(tmp));
					ip=iis.getIpAddress();
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
								vo.getBusinessids(),Integer.parseInt(tmp),"iis");
						
						//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					try {
						Hashtable hash1 = getCategory(ip,"IISPing","ConnectUtilization",starttime1,totime1);						
//						Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1);						
						if(hash1!=null)
						request.setAttribute("pingcon", hash1);
//						if(hash!=null)
//							request.setAttribute("avgjvm", hash);	
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				request.setAttribute("vector",vector);
				request.setAttribute("id", Integer.parseInt(tmp));
				request.setAttribute("list", list);
				request.setAttribute("startdate", b_time);
				request.setAttribute("todate", t_time);
				return "/application/iis/eventReport.jsp";
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
				// iis-----------------------------------------------------
						Node iisNode = PollingEngine.getInstance().getIisByID(Integer.parseInt(id));
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
					String file = "temp/iisEventReport.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					try {
						report1.createReport_midEventDoc(fileName,starttime,totime,"IIS");
					} catch (IOException e) {
						e.printStackTrace();
					}// word事件报表分析表
					request.setAttribute("filename", fileName);
				} else if ("1".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
							reporthash);
					String file = "temp/iisEventReport.xls";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					try {
						report1.createReport_TomcatEventExc(file,id,starttime,totime,"IIS");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// xls事件报表分析表
					request.setAttribute("filename", fileName);
				} else if ("2".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
							reporthash);
					String file = "temp/iisEventReport.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					try {
						report1.createReport_midEventPdf(fileName,starttime,totime,"IIS");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// pdf事件报表分析表
					request.setAttribute("filename", fileName);
				}
				return "/capreport/service/download.jsp";
			}
		 
		 
		 
		 private String downloadReport() {
			    Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
				// 画图-----------------------------
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
				String id = request.getParameter("id");
				String flag = request.getParameter("flag");
				
				
				if("per".equalsIgnoreCase(flag)){
					if ("0".equals(str)) {
						// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
						report.createReportxls_iis_per("temp/iis_PerReport.xls",id);
						request.setAttribute("filename", report.getFileName());
					} else if ("1".equals(str)) {
						ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
								reporthash);
						try {
							String file = "temp/iis_PerReport.doc";// 保存到项目文件夹下的指定文件夹
							String fileName = ResourceCenter.getInstance().getSysPath()
									+ file;// 获取系统文件夹路径
							report1.createReportDoc_iis_per(fileName,"doc",id);// word综合报表
							request.setAttribute("filename", fileName);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if ("2".equals(str)) {
						ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
								reporthash);
						try {
							String file = "temp/iis_PerReport.pdf";// 保存到项目文件夹下的指定文件夹
							String fileName = ResourceCenter.getInstance().getSysPath()
									+ file;// 获取系统文件夹路径
							// report1.createReport_hostPDF(fileName);// pdf综合报表
							report1.createReportDoc_iis_per(fileName,"pdf",id);
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
		 
		 public String perReport(){
			 
			   String id= request.getParameter("id");
			   String flag = request.getParameter("flag");
			   request.setAttribute("flag", flag);
			   
			   
			   com.afunms.polling.node.IIS iis = (com.afunms.polling.node.IIS)PollingEngine.getInstance().getIisByID(Integer.parseInt(id)); 
			   String ip=iis.getIpAddress();
			   	Hashtable imgurlhash=new Hashtable();
				try {
					String newip=doip(ip);
					java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
			        if(to_hour == null|| "0".equals(to_hour) ){
			        	to_hour = nowdate.getHours()+"";
			        	if(to_hour.length()<2)
			        		to_hour = "0"+to_hour;
			        	request.setAttribute("to_hour", to_hour);
			        }
			        String starttime = from_date1+" "+from_hour+":00:00";
			        String totime = to_date1+" "+to_hour+":59:59";   
					request.setAttribute("starttime1", from_date1);
					request.setAttribute("totime1", to_date1);										
					try{
						Hashtable hash1 = getCategory(ip,"IISPing","ConnectUtilization",starttime,totime);						
						p_draw_line(hash1,"连通率",newip+"IISPing",740,150);
						//Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1);						
						//p_draw_line(hash,"JVM内存利用率",newip+"tomcat_jvm",740,150);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					//imgurlhash
					//imgurlhash.put("tomcat_jvm","resource\\image\\jfreechart\\"+newip+"tomca_jvm"+".png");
					imgurlhash.put("IISPing","resource/image/jfreechart/"+newip+"IISPing"+".png");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				int alarmLevel = 0;
				Hashtable checkEventHashtable = ShareData.getCheckEventHash();
				NodeUtil nodeUtil = new NodeUtil();
				/*===========for status start==================*/
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(iis);
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
				
				request.setAttribute("imgurlhash",imgurlhash);
				
			   return  "/application/iis/perReport.jsp";
		   }
		 
}