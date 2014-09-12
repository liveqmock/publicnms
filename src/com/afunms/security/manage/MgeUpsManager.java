/**
 * <p>Description:Gate Record Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 汉口银行
 * @date 2011-05-1
 */

package com.afunms.security.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.BusinessDao;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.CheckValueDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.PortConfigCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.AirLoader;
import com.afunms.polling.loader.UPSLoader;
import com.afunms.polling.node.AirNode;
import com.afunms.polling.node.UPSNode;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.security.model.MgeUps;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.KeyGenerator;

public class MgeUpsManager extends BaseManager implements ManagerInterface{
	
    public String execute(String action)
    {
      if(action.equals("list"))
    	 return list();
      if(action.equals("ready_add"))
      	 return "/ups/add.jsp";      
      if(action.equals("add"))  
     	 return add();
      if(action.equals("ready_edit"))
     	 return readyEdit();
      if(action.equals("update"))
      	 return update();
      if(action.equals("delete"))
       	 return delete();
      if("cancelmanage".equals(action))
		 return cancelmanage();
	  if("toDetail".equals(action))
		 return toDetail();
	  if("current".equals(action))
		 return toCurrent();
	  if("event".equals(action))
		 return event();
	  if("addmanage".equals(action))
		 return addmanage();
	  if("tosysinfo".equals(action))
		  return toSysinfo();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
    }
    
    public String toSysinfo()
    {
    	String id = getParaValue("id");
        MgeUpsDao mgeUpsDao = new MgeUpsDao();
        MgeUps vo = null;
        try {
			vo = (MgeUps) mgeUpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mgeUpsDao.close();
		}      
		request.setAttribute("vo", vo);  
		return "/ups/ups_sysinfo.jsp";
    }
    
    public String addmanage(){
		
		String id = getParaValue("id");
		
		MgeUpsDao tmpDao = new MgeUpsDao();
		try {
			tmpDao.update(id,"1");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tmpDao.close();
		}
		UPSNode upsnode = (UPSNode)PollingEngine.getInstance().getUpsByID(Integer.parseInt(id));
		if(upsnode!=null){
			upsnode.setManaged(true);
		}
		AirNode airnode = (AirNode)PollingEngine.getInstance().getAirByID(Integer.parseInt(id));
		if(airnode!=null){
			airnode.setManaged(true);
		}
		return list();
	}
	
	public String cancelmanage(){
		
		String id = getParaValue("id");
		
		MgeUpsDao tmpDao = new MgeUpsDao();
		try {
			tmpDao.update(id,"0");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tmpDao.close();  
		}
		UPSNode upsnode = (UPSNode)PollingEngine.getInstance().getUpsByID(Integer.parseInt(id));
		if(upsnode!=null){
			upsnode.setManaged(false);
		}
		AirNode airnode = (AirNode)PollingEngine.getInstance().getAirByID(Integer.parseInt(id));
		if(airnode!=null){
			airnode.setManaged(false);
		}
		return list();
	}
	
	public String toDetail(){
		
        String id = getParaValue("id");
        MgeUpsDao mgeUpsDao = new MgeUpsDao();
        MgeUps vo = null;
        try {
			vo = (MgeUps) mgeUpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mgeUpsDao.close();
		}      
		request.setAttribute("vo", vo);  
		return "/ups/ups_detail.jsp";
	}
	
	public String toCurrent(){
		
        String id = getParaValue("id");
        MgeUpsDao mgeUpsDao = new MgeUpsDao();
        MgeUps vo = null;
        try {
			vo = (MgeUps) mgeUpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mgeUpsDao.close();
		}      
		request.setAttribute("vo", vo);  
		return "/ups/ups_current_detail.jsp";
	}
	
    public String event(){
		
        String id = getParaValue("id");
        int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		List list = new ArrayList();
		try{
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
		} catch(Exception e){
			e.printStackTrace();
		}
		EventListDao dao = new EventListDao();
		try{
			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";
			User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER); 
			list = dao.getQuery(starttime2,totime2,status+"",level1+"",vo.getBusinessids(),Integer.parseInt(id));
		} catch(Exception ex){
			ex.printStackTrace();
		} finally {
			dao.close();
		}
        MgeUpsDao mgeUpsDao = new MgeUpsDao();
        MgeUps vo = null;
        try {
			vo = (MgeUps) mgeUpsDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mgeUpsDao.close();
		}      
		request.setAttribute("list",list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("vo", vo);  
		return "/ups/ups_event.jsp";
	}
	
    private String list()
    {
    	String type = getParaValue("category");
    	System.out.println("type    " + type);
    	String jsp = "/ups/list.jsp";
    	MgeUpsDao mgeUpsDao = new MgeUpsDao();
    	if(type!=null&&!type.equals("")){
    		List list = mgeUpsDao.loadByType(type);
    		request.setAttribute("list", list);  
    		return jsp;
    	} else {
    		List list = mgeUpsDao.loadAll();
    		request.setAttribute("list", list);  
    		return jsp;
    	}
    }

   private String update()
   {
	   MgeUps vo = new MgeUps();
       vo.setId(getParaIntValue("id"));
       vo.setAlias(getParaValue("alias"));
       vo.setLocation(getParaValue("location"));
       String ismanaged = getParaValue("ismanaged");
       String community = getParaValue("community");
	   String bid = getParaValue("bid");
	   if(bid.lastIndexOf(",")<bid.length()-1){
			bid = bid+",";
		}
       vo.setIsmanaged(ismanaged);
       vo.setBid(bid);
       vo.setCommunity(community);
       DaoInterface dao = new MgeUpsDao();
       boolean result = false;
       result = dao.update(vo);	
       if(result){
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
	        try {
				boolean result2 = timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("23"));
			} catch (Exception e) {
				e.printStackTrace();
			}
       }
       if("ups".equalsIgnoreCase(vo.getType())){
    	   if(PollingEngine.getInstance().getUpsByID(vo.getId())!=null)
           {        
        	   UPSNode node = (UPSNode)PollingEngine.getInstance().getUpsByID(vo.getId());
               node.setAlias(vo.getAlias());
               node.setLocation(vo.getLocation());
               node.setBid(vo.getBid());
               if("1".equals(vo.getIsmanaged())){
                   node.setManaged(true);
    	       }else {
    	       	   node.setManaged(false);
    	       }
               node.setCommunity(vo.getCommunity());
           } else {
        	   PollingEngine.getInstance().deleteAirByID(vo.getId());
        	   UPSLoader loader = new UPSLoader();
               loader.loadOne(vo);
               loader.close();
           }
       }else if("air".equalsIgnoreCase(vo.getType())){
    	   if(PollingEngine.getInstance().getAirByID(vo.getId())!=null)
           {        
    		   AirNode node = (AirNode)PollingEngine.getInstance().getAirByID(vo.getId());
               node.setAlias(vo.getAlias());
               node.setLocation(vo.getLocation());
               node.setBid(vo.getBid());
               if("1".equals(vo.getIsmanaged())){
                   node.setManaged(true);
    	       }else {
    	       	   node.setManaged(false);
    	       }
               node.setCommunity(vo.getCommunity());
           } else {
        	   PollingEngine.getInstance().deleteUpsByID(vo.getId());
        	   AirLoader loader = new AirLoader();
               loader.loadOne(vo);
               loader.close();
           }
       }
       
       return "/ups.do?action=list";
   }
   
   private String add()
   {
	    String ipAddress = getParaValue("ipadress");
	    String alias = getParaValue("alias");
	    String community = getParaValue("community");
	    String location = getParaValue("location");
	    String type = getParaValue("type");
	    String subtype = getParaValue("subtype");
	    String ismanaged = getParaValue("ismanaged");
	    String bid = getParaValue("bid");
	    String collecttype = getParaValue("collecttype");
	    MgeUpsDao tmpDao = new MgeUpsDao();
    	List tmpList = tmpDao.findByIP(ipAddress);
    	if(tmpList.size()>0)
	    {	  
	        setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
	        return null;      
	    }   	    	    	
    	if(NetworkUtil.ping(ipAddress)==0) 
	    {	  
	        setErrorCode(ErrorMessage.PING_FAILURE);
	        return null;      
	    }   	    
    	String sysOid = SnmpUtil.getInstance().getSysOid(ipAddress,community);
    	if(sysOid==null)
    	{
	        setErrorCode(ErrorMessage.SNMP_FAILURE);
	        return null;      
    	}
//    	if(!sysOid.equals("1.3.6.1.4.1.705.1.2"))
//    	{
//	        setErrorCode(ErrorMessage.IS_NOT_MGE_UPS);
//	        return null;          		
//    	}
    	MgeUps vo = new MgeUps();
    	vo.setId(KeyGenerator.getInstance().getNextKey());
	    vo.setAlias(alias); 
        vo.setIpAddress(ipAddress);
        vo.setCommunity(community);
        vo.setLocation(location);
        vo.setSysOid(sysOid);
        vo.setType(type);
        vo.setSubtype(subtype);
        if(bid.lastIndexOf(",")<bid.length()-1){
			bid = bid+",";
		}
        vo.setIsmanaged(ismanaged);
        vo.setBid(bid);
        vo.setSysName(SnmpUtil.getInstance().getSysName(ipAddress, community));
        vo.setSysDescr(SnmpUtil.getInstance().getSysDescr(ipAddress, community));
        vo.setCollecttype(collecttype);
        
        DaoInterface dao = new MgeUpsDao();
        boolean result = false;
        result = dao.save(vo);
        //将采集指标导入到nms_gather_indicators_node表
        try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(vo.getId() + "", AlarmConstant.TYPE_UPS, vo.getSubtype());
			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", AlarmConstant.TYPE_UPS, vo.getSubtype(), "1");
			//PortConfigCenter.getInstance().setPortHastable();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(result){
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
	        try {
				boolean result2 = timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("23"));
			} catch (Exception e) {
				e.printStackTrace();
			}
//			在轮询线程中增加被监视节点
	        if("ups".equalsIgnoreCase(type)){
	        	UPSLoader loader = new UPSLoader();
	            loader.loadOne(vo);
	            loader.close();
	        }else{
	        	AirLoader loader = new AirLoader();
	            loader.loadOne(vo);
	            loader.close();
	        }
	        MgeUpsDao upsdao = new MgeUpsDao();			
			try {
				List upslist = upsdao.loadMonitorUps();				
				ShareData.setUpslist(upslist);
			} catch (Exception e) {
               e.printStackTrace();
			} finally {
				upsdao.close();
			}
			
		}
	    return "/ups.do?action=list";
   }	
   
   private String readyEdit()
   {	   
	    DaoInterface dao = new MgeUpsDao();
	    setTarget("/ups/edit.jsp");
	    List allbuss = null; // 业务权限
		BusinessDao businessDao = new BusinessDao();
		try {
			allbuss = businessDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			businessDao.close();
		}
		
		request.setAttribute("allbuss", allbuss);
		
		//提供已设置的采集时间信息
    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("23"));
    	for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
    		timeGratherConfig.setHourAndMin();
		}
    	request.setAttribute("timeGratherConfigList", timeGratherConfigList);  
        return readyEdit(dao);
   }

   private String delete()
   {	   
	    String[] ids = getParaArrayValue("checkbox");
	    boolean result = false;
	    if(ids!=null&&ids.length>0){
			try {
				DaoInterface dao = new MgeUpsDao();
				result = dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(result){
					TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
					for(int i = 0 ; i < ids.length ; i ++){
						int nodeId = Integer.parseInt(ids[i]);
						UPSNode node = (UPSNode)PollingEngine.getInstance().getUpsByID(nodeId);
				        PollingEngine.getInstance().deleteUpsByID(nodeId);
						timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("23"));
						String ip = node.getIpAddress();
//				 	    String ip1 ="",ip2="",ip3="",ip4="";
//						String tempStr = "";
				 	    String allipstr = "";
//						if (ip.indexOf(".")>0){
//							ip1=ip.substring(0,ip.indexOf("."));
//							ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//							tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
						allipstr = SysUtil.doip(ip);
						CreateTableManager ctable = new CreateTableManager();
						DBManager conn = new DBManager();
						try {
							ctable.deleteTable(conn,"ping",allipstr,"ping");//Ping
//							ctable.dropSeq(conn, "ping", allipstr);//删除序列
							ctable.deleteTable(conn,"pinghour",allipstr,"pinghour");//Ping
//							ctable.dropSeq(conn, "pinghour", allipstr);//删除序列
							ctable.deleteTable(conn,"pingday",allipstr,"pingday");//Ping
//							ctable.dropSeq(conn, "pingday", allipstr);//删除序列
							if("ups".equalsIgnoreCase(node.getType())){
								ctable.deleteTable(conn,"input",allipstr,"input");//input
//								ctable.dropSeq(conn, "input", allipstr);
								
								ctable.deleteTable(conn,"inputhour",allipstr,"inputhour");//input
//								ctable.dropSeq(conn, "inputhour", allipstr);
								
								ctable.deleteTable(conn,"inputday",allipstr,"inputday");//input
//								ctable.dropSeq(conn, "inputday", allipstr);
								
								ctable.deleteTable(conn,"output",allipstr,"output");//output
//								ctable.dropSeq(conn, "output", allipstr);
								
								ctable.deleteTable(conn,"outputhour",allipstr,"outputhour");//output
//								ctable.dropSeq(conn, "outputhour", allipstr);
								
								ctable.deleteTable(conn,"outputday",allipstr,"outputday");//output
//								ctable.dropSeq(conn, "outputday", allipstr);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							conn.close();
						}
//						删除该设备的采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(nodeId+"", "ups","");
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							gatherdao.close();
						}
						
						//删除告警指标
			  			AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try{
							indicatdao.deleteByNodeId(nodeId+"", "ups");
						}catch(Exception e){
			  				e.printStackTrace();
			  			}finally{
			  				indicatdao.close();
			  			}
			  			
						EventListDao eventdao = new EventListDao();
						try{
							//同时删除事件表里的相关数据
							eventdao.delete(nodeId, "ups");
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							eventdao.close();
						}
						//
						CheckEventDao checkEventDao = new CheckEventDao();
						try {
							checkEventDao.deleteByNodeType(nodeId+"", "ups");
						} catch (Exception e2) {
							e2.printStackTrace();
						} finally {
							checkEventDao.close();
						}
						CheckValueDao checkValueDao = new CheckValueDao();
						try {
							//checkValueDao.deleteByNodeType(nodeId+"", "ups");
						} catch (Exception e2) {
							e2.printStackTrace();
						} finally {
							checkValueDao.close();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
        return "/ups.do?action=list";
   }
   
}