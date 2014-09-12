/**
 * <p>Description:system initialize,loads system resources when server starting</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.initialize;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.cxf.frontend.ServerFactoryBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.alarm.dao.AlarmPortDao;
import com.afunms.alarm.dao.SendAlarmTimeDao;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.manage.PerformancePanelManager;
import com.afunms.application.model.WebConfig;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.inform.dao.AlarmDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.DBLoader;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.IfEntity;
import com.afunms.polling.om.VMWareConnectConfig;
import com.afunms.sysset.dao.ServiceDao;
import com.afunms.system.dao.TimeGratherConfigDao;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.EquipImageDao;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.dao.NodeEquipDao;
import com.afunms.topology.dao.VMWareConnectConfigDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.EquipImage;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.InterfaceNode;
import com.afunms.topology.model.NetSyslogNodeRule;
import com.afunms.topology.model.NodeEquip;
import com.afunms.util.ProjectProperties;
import com.database.config.SystemConfig;

public class SysInitialize
{
	private ResourceCenter res;
	private SAXBuilder builder;
	
    public SysInitialize()
    {    
        res = ResourceCenter.getInstance();	
        builder = new SAXBuilder();
    } 
    public void startUpService() {
//		String startSign = SystemConfig.getConfigInfomation("WebserviceConfig", "StartSign");
//		if (startSign != null && startSign.equals("true")) {
//			DeviceInfoImpl implementor = new DeviceInfoImpl();
//			String address = SystemConfig.getConfigInfomation("WebserviceConfig", "URL");
////			 String address = "http://192.168.1.127:8081/afunms/services/DeviceInfo";
//			// Endpoint.publish(address, implementor);
//			ServerFactoryBean svrFactory = new ServerFactoryBean();
//			svrFactory.setServiceClass(DeviceInfo.class);
//			svrFactory.setAddress(address);
//			svrFactory.setServiceBean(implementor);
//			svrFactory.create();
//			System.out.println("接口服务已经发布....");
//		}

	}
    public void init()
    {
    	loadSystemConfigXml();
    	loadManagerXml();    	
    	loadActionXml(); 
    	loadAjaxManagerXml();
    	loadMenuXml();
    	loadService();
//    	loadCfgBackup();
//    	startUpService();
		//---------------(2)加载接口数据-------------------
		HostInterfaceDao niDao = new HostInterfaceDao();
		List interfacelist = new ArrayList();
		ShareData.setAllinterfaces(new Hashtable());
		try{
			interfacelist = niDao.loadAll();
			for(int i=0;i<interfacelist.size();i++){
				InterfaceNode vo = (InterfaceNode)interfacelist.get(i);
				String speed="";
				if(vo.getSpeed()==null||vo.getSpeed().equalsIgnoreCase("null")){
					speed="0";
				}else {
					speed=vo.getSpeed();
				}
				IfEntity ifEntity = new IfEntity();
				   ifEntity.setId(vo.getId());
				   ifEntity.setAlias(vo.getAlias());
				   ifEntity.setIndex(vo.getEntity());
				   ifEntity.setDescr(vo.getDescr());
				   ifEntity.setIpAddress(vo.getIp_address());
				   ifEntity.setPhysAddress(vo.getPhys_address());
				   ifEntity.setPort(vo.getPort());
				   ifEntity.setSpeed(Long.parseLong(speed));
				   ifEntity.setOperStatus(vo.getOper_status());
				   ifEntity.setType(vo.getType());
				   ifEntity.setChassis(vo.getChassis());
				   ifEntity.setSlot(vo.getSlot());
				   ifEntity.setUport(vo.getUport());

				if(ShareData.getAllinterfaces() != null){
					if(ShareData.getAllinterfaces().containsKey(vo.getNode_id())){
						//已经存在主键，则先获取，然后在追加进去
						//SysLogger.info("add============="+vo.getNode_id()+"=====");
						((Hashtable)ShareData.getAllinterfaces().get(vo.getNode_id())).put(ifEntity.getIndex(),ifEntity);
					}else{
						Hashtable ifhash = new Hashtable();
						ifhash.put(ifEntity.getIndex(),ifEntity);
						//SysLogger.info("add============="+vo.getNode_id()+"=====");
						ShareData.getAllinterfaces().put(vo.getNode_id(), ifhash);
					}
				}else{
					Hashtable temphash = new Hashtable();
					Hashtable ifhash = new Hashtable();
					ifhash.put(ifEntity.getIndex(),ifEntity);
					temphash.put(vo.getNode_id(), ifhash);
					//SysLogger.info("add============="+vo.getNode_id()+"=====");
					ShareData.setAllinterfaces(temphash);
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			niDao.close();
		}
    	
    	PollingEngine.getInstance().doPolling();
    	//初始化性能面板中的数据
    	PerformancePanelManager.getInstance().init(); 
    	if(res.hasDiscovered())  //发现过了
    	{
    		
    		//loadTasks();
    		deleteAllAlarm(); 
    		/*
            //================轮询初始化===========================       	    	
        	if(res.hasDiscovered() && res.isStartPolling()) //如果在1或2区则轮询
        		PollingEngine.getInstance().doPolling();   
        	*/
    	}
    	//告警阀值初始化加载
    	AlarmIndicatorsUtil alarmIndicatorsUtil=new AlarmIndicatorsUtil();
    	alarmIndicatorsUtil.loadAlarmIndicatorsNode(); 

        //清除告警信息
    	CheckEventDao checkeventdao = new CheckEventDao();
		try{
			checkeventdao.empty();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			checkeventdao.close();
		}
		
		//清除告警已发送次数
		SendAlarmTimeDao sendAlarmDao = new SendAlarmTimeDao();
		try{
			sendAlarmDao.empty();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			sendAlarmDao.close();
		}
		
        ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
        Hashtable connectConfigHashtable = new Hashtable();
		List configList = new ArrayList();
		try{
			configList = connectTypeConfigDao.loadAll();
		}catch(Exception e){
			
		}finally{
			connectTypeConfigDao.close();
			connectTypeConfigDao = null;
		}
		if(configList != null && configList.size()>0){
			for(int i=0;i<configList.size();i++){
				ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)configList.get(i);
				connectConfigHashtable.put(connectTypeConfig.getNode_id(), connectTypeConfig);
			}
		}		
		ShareData.getConnectConfigHashtable().put("connectConfigHashtable", connectConfigHashtable);
		
		//刷新内存中采集指标
		NodeGatherIndicatorsUtil gatherutil = new NodeGatherIndicatorsUtil();
		gatherutil.refreshShareDataGather();
		
		IpaddressPanelDao paneldao = new IpaddressPanelDao();
		List list = null;
		try{
			list = paneldao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			paneldao.close();
		}
		PollingEngine.getInstance().setPanelList(list);
		
		PortConfigCenter.getInstance().setPortHastable();
		
		//装载分时间段采集的设备
//		Hashtable timergatherhash = new Hashtable();
//		TimeGratherConfigDao timergatherdao = new TimeGratherConfigDao();
//		List timerlsit = new ArrayList();
//		try{
//			timerlsit = timergatherdao.loadAll();
//			if(timerlsit != null && timerlsit.size()>0){
//				for(int i=0;i<timerlsit.size();i++){
//					TimeGratherConfig timerconfig = (TimeGratherConfig)timerlsit.get(i);
//					
//					if(ShareData.getTimegatherhash() != null){
//						if(ShareData.getTimegatherhash().containsKey(timerconfig.getObjectId()+":"+timerconfig.getObjectType())){
//							//已经存在主键，则先获取，然后在追加进去
//							SysLogger.info("add============="+timerconfig.getObjectId()+":"+timerconfig.getObjectType());
//							((List)ShareData.getTimegatherhash().get(timerconfig.getObjectId()+":"+timerconfig.getObjectType())).add(timerconfig);
//						}else{
//							List timerlist = new ArrayList();
//							timerlist.add(timerconfig);
//							SysLogger.info("add============="+timerconfig.getObjectId()+":"+timerconfig.getObjectType());
//							ShareData.getTimegatherhash().put(timerconfig.getObjectId()+":"+timerconfig.getObjectType(), timerlist);
//						}
//					}else{
//						List timerlist = new ArrayList();
//						timerlist.add(timerconfig);
//						Hashtable _timegatherhash = new Hashtable();
//						SysLogger.info("add============="+timerconfig.getObjectId()+":"+timerconfig.getObjectType());
//						_timegatherhash.put(timerconfig.getObjectId()+":"+timerconfig.getObjectType(), timerlist);
//						ShareData.setTimegatherhash(_timegatherhash);
//					}
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			timergatherdao.close();
//		}
		//ShareData.setTimegatherhash(timergatherhash);
		
		
		//装栽内存中IP与别名IP对照表
//		ShareData.setAllipalias(new Hashtable());
//		ShareData.setAllipaliasVSip(new Hashtable());
//		IpAliasDao ipaliasdao = new IpAliasDao();
//		try{
//			ipaliasdao.RefreshIpAlias();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			ipaliasdao.close();
//		}
//		List allist = new ArrayList();
//		try{
//			//delte后,conn已经关闭
//			allist = ipaliasdao.loadAll();
//		}catch(Exception e){
//				e.printStackTrace();
//		}finally{
//			ipaliasdao.close();
//		}
//		if(allist != null && allist.size()>0){
//			for(int i=0;i<allist.size();i++){
//				IpAlias vo = (IpAlias)allist.get(i);
//				if(ShareData.getAllipalias() != null){
//					if(ShareData.getAllipalias().containsKey(vo.getIpaddress())){
//						//已经存在主键，则先获取，然后在追加进去
//						((List)ShareData.getAllipalias().get(vo.getIpaddress())).add(vo.getAliasip());
//					}else{
//						List aliaslist = new ArrayList();
//						aliaslist.add(vo.getAliasip());
//						ShareData.getAllipalias().put(vo.getIpaddress(), aliaslist);
//					}
//				}
//			}
//		}
		

		
		//将端口配置装载到内存中
		ShareData.setAllportconfigs(new Hashtable());
		ShareData.setAllportconfigsbyIP(new Hashtable());
		PortconfigDao portconfigdao = new PortconfigDao();
		List portconfglist = new ArrayList();
		try{
			portconfglist = portconfigdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			portconfigdao.close();
		}
		if(portconfglist != null && portconfglist.size()>0){
			for(int i=0;i<portconfglist.size();i++){
				Portconfig portconfig = (Portconfig)portconfglist.get(i);
				if(ShareData.getAllportconfigs() != null){
//					if("10.10.117.176".equalsIgnoreCase(portconfig.getIpaddress()))
					ShareData.getAllportconfigs().put(portconfig.getIpaddress()+":"+portconfig.getPortindex(), portconfig);
				}else{
					Hashtable hash = new Hashtable();
//					if("10.10.117.176".equalsIgnoreCase(portconfig.getIpaddress()))
					hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(), portconfig);
					ShareData.setAllportconfigs(hash);
				}
				if(ShareData.getAllportconfigsbyIP() != null){
					if(ShareData.getAllportconfigsbyIP().containsKey(portconfig.getIpaddress())){
						//已经存在主键，则先获取，然后在追加进去
						((List)ShareData.getAllportconfigsbyIP().get(portconfig.getIpaddress())).add(portconfig);
					}else{
						List aliaslist = new ArrayList();
						aliaslist.add(portconfig);
						ShareData.getAllportconfigsbyIP().put(portconfig.getIpaddress(), aliaslist);
					}
				}
			}
		}
		
		//将端口流速告警阀值装载到内存中
		ShareData.setAlarmportConfigHash(new Hashtable());
		AlarmPortDao alarmPortDao = new AlarmPortDao();
		List alarmPortList = new ArrayList();
		try{
			alarmPortList = alarmPortDao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			alarmPortDao.close();
		}
		if (alarmPortList != null && alarmPortList.size() > 0) {
			Hashtable hash = new Hashtable();
			//List tempList = new ArrayList();
			for (int i = 0; i < alarmPortList.size(); i++) {
				AlarmPort alarmPort = (AlarmPort) alarmPortList.get(i);
				String ipaddress = alarmPort.getIpaddress();
				if (!hash.containsKey(ipaddress)) {
					List tempList = new ArrayList();
					tempList.add(alarmPort);
					hash.put(ipaddress, tempList);
				} else {
					((ArrayList)hash.get(ipaddress)).add(alarmPort);
				}
			}
			ShareData.setAlarmportConfigHash(hash);
		}
		
		//将WEB监视列表到内存中
		ShareData.setAllurls(new Hashtable());
//		WebConfigDao urldao = new WebConfigDao();
//		List urllist = new ArrayList();
//		try{
//			urllist = urldao.loadAll();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			urldao.close();
//		}
//		if(urllist != null && urllist.size()>0){
//			for(int i=0;i<urllist.size();i++){
//				WebConfig webconfig = (WebConfig)urllist.get(i);
//				if(ShareData.getAllurls() != null){
//					ShareData.getAllurls().put(webconfig.getId(), webconfig);
//				}else{
//					Hashtable hash = new Hashtable();
//					hash.put(webconfig.getId(), webconfig);
//					ShareData.setAllurls(hash);
//				}
//			}
//		}
		
		//将Bussiness业务列表装到内存中
		Hashtable bushash = new Hashtable();
		BusinessDao bussdao = new BusinessDao();
		List buslist = new ArrayList();
		try{
			buslist = bussdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			bussdao.close();
		}
		if(buslist != null && buslist.size()>0){
			ShareData.setAllbussness(buslist);
		}
		
		
		NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
		Hashtable nodeequiphash = new Hashtable();
		try{
			List nodeequiplist = nodeEquipDao.loadAll();
			if(nodeequiplist != null && nodeequiplist.size()>0){
				for(int i=0;i<nodeequiplist.size();i++){
					NodeEquip nodeequip = (NodeEquip)nodeequiplist.get(i);
					nodeequiphash.put(nodeequip.getNodeId()+":"+nodeequip.getXmlName(), nodeequip);
				}
			}
		}catch(Exception e){
			
		}finally{
			nodeEquipDao.close();
		}
		ShareData.setAllnodeequps(nodeequiphash);
		
		HashMap EquipMap = new HashMap();
		EquipImageDao equipImageDao = new EquipImageDao();
		List imglist = new ArrayList();
		try{
			imglist = equipImageDao.loadAll();
			for(int i = 0;i<imglist.size();i++){
				EquipImage equipImage = (EquipImage)imglist.get(i);
				EquipMap.put(equipImage.getId(), equipImage);
			}
		}catch(Exception e){
			
		}finally{
			equipImageDao.close();
		}
		ShareData.setAllequpimgs(EquipMap);
		
		
		
		
//		HintNodeDao hintNodeDao = new HintNodeDao();
//		Hashtable hinthash = new Hashtable();
//		try{
//			List hintlist = hintNodeDao.loadAll();
//			if(hintlist != null && hintlist.size()>0){
//				for(int i=0;i<hintlist.size();i++){
//					HintNode hintnode = (HintNode)hintlist.get(i);
//					hinthash.put(hintnode.getNodeId()+":"+hintnode.getXmlfile(), hintnode);
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			hintNodeDao.close();
//		}
//		NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
//		Hashtable nodeequiphash = new Hashtable();
//		try{
//			List nodeequiplist = nodeEquipDao.loadAll();
//			if(nodeequiplist != null && nodeequiplist.size()>0){
//				for(int i=0;i<nodeequiplist.size();i++){
//					NodeEquip nodeequip = (NodeEquip)nodeequiplist.get(i);
//					nodeequiphash.put(nodeequip.getNodeId()+":"+nodeequip.getXmlName(), nodeequip);
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			nodeEquipDao.close();
//		}
		
		//初始化数据库采集指标
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	try{
    		//获取被启用的ORACLE所有被监视指标
    		monitorItemList = indicatorsdao.getByInterval("5", "m",1,"db","oracle");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		indicatorsdao.close();
    	}
    	ShareData.getOracleGIHash().put("5:m",monitorItemList);
    	
//    	VMWareConnectConfigDao vmwareConnectDao = new VMWareConnectConfigDao();
//    	List vmwareConnectList = new ArrayList();
//    	Hashtable vmwareConnecthash = new Hashtable();
//    	try{
//    		vmwareConnectList = vmwareConnectDao.loadAll();
//    		if(vmwareConnectList != null && vmwareConnectList.size()>0){
//    			for(int i=0;i<vmwareConnectList.size();i++){
//    				VMWareConnectConfig vmwareConfig = (VMWareConnectConfig)vmwareConnectList.get(i);
//    				ShareData.setVmwareConfig(vmwareConfig.getNodeid()+"", vmwareConfig);
//    				//vmwareConnecthash.put(vmwareConfig.getNodeid(), vmwareConfig);
//    			}
//    		}
//    	}catch(Exception e){
//    		
//    	}finally{
//    		vmwareConnectDao.close();
//    	}
    	
//    	//初始化ORACLE的SID列表
//    	OraclePartsDao siddao = null;
//    	List oraclesidlist = new ArrayList();
//		try {
//			siddao = new OraclePartsDao();
//			oraclesidlist = siddao.findByCondition(" where managed=1 ");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (siddao != null)
//				siddao.close();
//		}
//		ShareData.setOracleSIDList(oraclesidlist);
		
//		IpAccountingBaseDao ipaccbasedao = new IpAccountingBaseDao();
//		List ipacclist = new ArrayList();
//		if(ShareData.getAllipaccountipbases() == null){
//			ShareData.setAllipaccountipbases(new Hashtable());
//		}
//		try{
//			ipacclist = ipaccbasedao.loadAll();
//			if(ipacclist != null && ipacclist.size()>0){
//				for(int i=0;i<ipacclist.size();i++){
//					IpAccountingBase vo = (IpAccountingBase)ipacclist.get(i);
//					ShareData.getAllipaccountipbases().put(vo.getSrcip()+":"+vo.getDestip()+":"+vo.getNodeid(), vo.getId()+"");
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			ipaccbasedao.close();
//		}
//		
//		NetSyslogNodeRuleDao nodeRuleDao = new NetSyslogNodeRuleDao();
//		Hashtable nodeRuleHash = new Hashtable();
//		try{
//			List nodeRuleList = nodeRuleDao.loadAll();
//			if(nodeRuleList != null && nodeRuleList.size()>0){
//				for(int i=0;i<nodeRuleList.size();i++){
//					NetSyslogNodeRule noderule = (NetSyslogNodeRule)nodeRuleList.get(i);
//					nodeRuleHash.put(noderule.getNodeid(), noderule.getFacility());
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			nodeRuleDao.close();
//		}
//		ShareData.setSyslogruleNode(nodeRuleHash);
		
		
//		HostNodeDao hostNodeDao = new HostNodeDao();
//		Hashtable nodehash = new Hashtable();
//		try{
//			List hostlist = hostNodeDao.loadIsMonitored(1);
//			if(hostlist != null && hostlist.size()>0){
//				for(int i=0;i<hostlist.size();i++){
//					HostNode node = (HostNode)hostlist.get(i);
//					if(nodehash.containsKey(node.getCategory()+"")){
//						//SysLogger.info("add category======"+node.getCategory());
//						((List)nodehash.get(node.getCategory()+"")).add(node);
//					}else{
//						List nodelist = new ArrayList();
//						//SysLogger.info("add category======"+node.getCategory());
//						nodelist.add(node);
//						nodehash.put(node.getCategory()+"", nodelist);
//					}
//				}
//			}
//		}catch(Exception e){
//			
//		}finally{
//			hostNodeDao.close();
//		}
//		ShareData.setNodehash(nodehash);
		
		//初始化所有数据库
//		DBDao dbdao = new DBDao();
//		List dbmonitorlist = new ArrayList(); 
//		try{
//			dbmonitorlist = dbdao.loadAll();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			dbdao.close();
//		}
//		ShareData.setDBList(dbmonitorlist);
		
		//刷新内存中的数据库列表
//		new DBLoader().refreshDBConfiglist();
//		
//		ManageXmlDao subMapDao = new ManageXmlDao();
//		List subfileList = null;
//		try{
//			subfileList = subMapDao.loadAll();
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			subMapDao.close();
//		}
//		PollingEngine.getInstance().setXmlList(subfileList);
//		res.setDbtype(ProjectProperties.getDBType());
//		
//		HostLoader hostloader = new HostLoader();
//		try{
//			hostloader.loadLinks();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		NodeGatherIndicatorsDao gatherDao = new NodeGatherIndicatorsDao();
//        Hashtable gatherHashtable = new Hashtable();
//		try{
//			gatherHashtable = gatherDao.getAllGather();
//		}catch(Exception e){
//			
//		}finally{
//			gatherDao.close();
//			gatherDao = null;
//		}
//		if(gatherHashtable == null)gatherHashtable = new Hashtable();
//		ShareData.setGatherHash(gatherHashtable);
    } 
		public void loadCfgBackup()
    {
    	Hashtable cfgHas = new Hashtable();
    	Hashtable h3cHas = new Hashtable();
    	Hashtable ciscoHas = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/cfg-backup.xml"));          
            List ciscoList = doc.getRootElement().getChildren("cisco");
            Iterator it = ciscoList.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	ciscoHas.put(element.getChild("name").getText(), element.getChild("class").getText()); 
            }       
            List list = doc.getRootElement().getChildren("h3c");
            it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	String type = element.getChild("type").getText();
            	String method = element.getChild("method").getText();
            	h3cHas.put(type,method); 
            }
            cfgHas.put("h3c", h3cHas);
            cfgHas.put("cisco", ciscoHas);
            res.setCfgHash(cfgHas);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadManagerXml()",e);
		}
    }
    /**
     * 系统路径
     */
    public void setSysPath(String path)
    {
    	res.setSysPath(path);
    }

    /**
     * 加载系统配置信息
     */
    private void loadSystemConfigXml()
    {
        SAXBuilder builder = new SAXBuilder();
        try
		{        	
           Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/system-config.xml"));  
           
           res.setAppServer(doc.getRootElement().getChildText("app-server"));   
           res.setSnmpversion(doc.getRootElement().getChildText("snmpversion"));
           res.setJndi(doc.getRootElement().getChildText("jndi"));
           
           String temp1 = doc.getRootElement().getChildText("log-info");
           res.setLogInfo(Boolean.parseBoolean(temp1));
          
           String temp2 = doc.getRootElement().getChildText("log-error");
           res.setLogError(Boolean.parseBoolean(temp2));
           
           String temp3 = doc.getRootElement().getChildText("poll_per_thread_nodes");
           res.setPerThreadNodes(Integer.parseInt(temp3));

           String temp4 = doc.getRootElement().getChildText("poll_thread_interval");
           res.setPollingThreadInterval(Integer.parseInt(temp4) * 60 * 1000);                      

           String temp5 = doc.getRootElement().getChildText("max_threads");
           res.setMaxThreads(Integer.parseInt(temp5));
           
           String temp6 = doc.getRootElement().getChildText("start_polling");
           res.setStartPolling(Boolean.parseBoolean(temp6));
                      
           String temp8 = doc.getRootElement().getChildText("has_discoverd");
           res.setHasDiscovered(Boolean.parseBoolean(temp8));           
		}
        catch(Exception e)
		{
      	    SysLogger.error("SysInitializtion.loadSystemConfigXml()",e);
		}
    }
    
    private void loadAjaxManagerXml()
    {        
        Hashtable ajaxManagerMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/ajax.xml"));          
            List list = doc.getRootElement().getChildren("manager");
            Iterator it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	String name = element.getChild("name").getText();
            	String theclass = element.getChild("class").getText();
            	ajaxManagerMap.put(name, Class.forName(theclass).newInstance()); 
            }    
            res.setAjaxManagerMap(ajaxManagerMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadManagerXml()",e);
		}
    }
    
    /**
     * 加载Manager信息
     */    
    private void loadManagerXml()
    {        
        Hashtable managerMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/manager.xml"));          
            List list = doc.getRootElement().getChildren("manager");
            Iterator it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
//            	SysLogger.info("=========="+element.getChild("name").getText());
            	managerMap.put(element.getChild("name").getText(),Class.forName(element.getChild("class").getText()).newInstance()); 
            }    
            res.setManagerMap(managerMap);
		}
        catch(Exception e)
		{
//        	e.printStackTrace();
        	SysLogger.error("SysInitializtion.loadManagerXml()",e);
		}
    }

    /**
     * 加载Action信息
     */
    private void loadActionXml()
    {
        Hashtable actionMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/action.xml"));          
            List list = doc.getRootElement().getChildren("action");
            Iterator it = list.iterator();
                        
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	actionMap.put(element.getAttributeValue("tag"),
            		new Integer(element.getAttributeValue("operate"))); 
            }    
            res.setActionMap(actionMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadActionXml()",e);
		}
    } 
    
    /**
     * 加载服务信息
     */    
    private void loadService()
    {
 	    ServiceDao dao = new ServiceDao();
 	    try{
 	    	res.setServiceList(dao.loadService(1));
 	    }catch(Exception e){
 	    	
 	    }finally{
 	    	dao.close();
 	    }
    }
    
    private void deleteAllAlarm()
    {
    	AlarmDao dao = new AlarmDao();
    	try{
    		dao.deleteAll();
    	}catch(Exception e){
    		
    	}finally{
    		dao.close();
    	}
    } 
    
    private void loadMenuXml()
    {        
        Hashtable menuMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/menu.xml"));          
            List list = doc.getRootElement().getChildren("menu");
            Iterator it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	menuMap.put(element.getChild("filename").getText(), 
            			element.getChild("menuId").getText()); 
            }
            res.setMenuMap(menuMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadMenuXml()",e);
		}
    }
}
