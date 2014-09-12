/**
 * <p>Description:loading host node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-28
 */

package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.dao.AlarmPortDao;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.TelnetDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.TelnetConfig;

public class HostLoader extends NodeLoader
{  	    	    	
    //private HostInterfaceDao niDao; 
    private List TelnetConfigs;
    
    public HostLoader()
    {    	
    	//niDao = new HostInterfaceDao();
    }
    
	public void loading()
    {    	  
    	 	
    	List nodeList = new ArrayList();
    	List retList = new ArrayList();
    	
    	HostNodeDao nodeDao = new HostNodeDao();  
    	
    	try{
    		nodeList = nodeDao.loadOrderByIP();  
    		//clearRubbish(nodeList); 
    		refreshNodeToMem(nodeList);
    		nodeDao = new HostNodeDao();  
    		String category = "8";
    		String condition = " where managed = 1 " ;
    		if(category == null) {
    			category = "";
    		}
    		category = "," + category + ",";
    		String[] categorys = category.split(",");
    		if(categorys!=null && categorys.length > 0 ){
    			int i = 0 ; 
    			for (String category_per : categorys) {
    				if(category_per != null && category_per.trim().length() > 0){
    					if(i == 0) { 
    						condition = condition + " and (";
    					} else {
    						condition = condition + " or";
    					}
    					condition = condition + " category ='" + category_per + "'";
    					i++;
    					if(i == categorys.length -1){
    						condition = condition + ") ";
    					}
    				}
    			}
    		}
    		//System.out.println(condition);
    		retList = nodeDao.findByCondition(condition);
    		if(retList == null)retList = new ArrayList();
    		ShareData.setSafelist(retList);
    		
    		List atmlist = new ArrayList();
    		category = "9";
    		condition = " where managed =1 ";
    		if(category == null) {
    			category = "";
    		}
    		category = "," + category + ",";
    		categorys = category.split(",");
    		if(categorys!=null && categorys.length > 0 ){
    			int i = 0 ; 
    			for (String category_per : categorys) {
    				if(category_per != null && category_per.trim().length() > 0){
    					if(i == 0) { 
    						condition = condition + " and (";
    					} else {
    						condition = condition + " or";
    					}
    					condition = condition + " category ='" + category_per + "'";
    					i++;
    					if(i == categorys.length -1){
    						condition = condition + ") ";
    					}
    				}
    			}
    		}
    		nodeDao = new HostNodeDao();  
    		atmlist = nodeDao.findByCondition(condition);
    		if(atmlist == null)atmlist = new ArrayList();
    		ShareData.setAtmlist(atmlist);
    		
    		List cmtslist = new ArrayList();
    		category = "13";
    		condition = " where managed =1 ";
    		if(category == null) {
    			category = "";
    		}
    		category = "," + category + ",";
    		categorys = category.split(",");
    		if(categorys!=null && categorys.length > 0 ){
    			int i = 0 ; 
    			for (String category_per : categorys) {
    				if(category_per != null && category_per.trim().length() > 0){
    					if(i == 0) { 
    						condition = condition + " and (";
    					} else {
    						condition = condition + " or";
    					}
    					condition = condition + " category ='" + category_per + "'";
    					i++;
    					if(i == categorys.length -1){
    						condition = condition + ") ";
    					}
    				}
    			}
    		}
    		nodeDao = new HostNodeDao();  
    		cmtslist = nodeDao.findByCondition(condition);
    		if(cmtslist == null)cmtslist = new ArrayList();
    		ShareData.setCmtslist(cmtslist);
    		
    		//装载存储
    		List storagelist = new ArrayList();
    		category = "14";
    		condition = " where managed =1 ";
    		if(category == null) {
    			category = "";
    		}
    		category = "," + category + ",";
    		categorys = category.split(",");
    		if(categorys!=null && categorys.length > 0 ){
    			int i = 0 ; 
    			for (String category_per : categorys) {
    				if(category_per != null && category_per.trim().length() > 0){
    					if(i == 0) { 
    						condition = condition + " and (";
    					} else {
    						condition = condition + " or";
    					}
    					condition = condition + " category ='" + category_per + "'";
    					i++;
    					if(i == categorys.length -1){
    						condition = condition + ") ";
    					}
    				}
    			}
    		}
    		nodeDao = new HostNodeDao();  
    		storagelist = nodeDao.findByCondition(condition);
    		if(storagelist == null)storagelist = new ArrayList();
    		ShareData.setStoragelist(storagelist);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		nodeDao.close();
    	}
    	
    	for(int i=0;i<nodeList.size();i++)
    	{    		
    		HostNode node = (HostNode)nodeList.get(i);
    		
    		if(node.getCategory()==5) continue; //暂时不对打印机进行监控
    		
    		loadOne(node);    		
    	}//all hosts are loaded	
    	//niDao.close();
    	close();    
    	
    	loadLinks();
    	
    	loadCheckEvent();
    	
    	loadPort();
    } 
	/**
	 * 清空内存中垃圾
	 * @param baseVoList
	 * @author makewen
	 * @date   Apr 20, 2011
	 */
	public void clearRubbish(List baseVoList){
		
		List nodeList=PollingEngine.getInstance().getNodeList();
		for(int index=0;index<nodeList.size();index++){
			if(nodeList.get(index) instanceof Host){ 
				Host node=(Host)nodeList.get(index);
    			if(baseVoList==null){ 
					nodeList.remove(node); 
    			}else{
    				boolean flag=false;
					for(int j=0;j<baseVoList.size();j++)
					{ 
						HostNode hostNode=(HostNode)baseVoList.get(j);
						if(node.getId()==hostNode.getId()){
							flag=true;
						} 
					} 
					if(!flag){
						nodeList.remove(node);
					}
    			}
			}
		}
	}
	
	/**
	 * 刷新内存
	 * @param baseVoList
	 * @author hukelei
	 * @date   6 20, 2012
	 */
	public void refreshNodeToMem(List baseVoList){
		
		List nodeList=PollingEngine.getInstance().getNodeList();
		for(int index=0;index<baseVoList.size();index++){
			if(baseVoList.get(index) instanceof Host){ 
				Host node=(Host)baseVoList.get(index);
				if(nodeList != null){
					boolean flag=false;
					for(int j=0;j<nodeList.size();j++)
					{ 
						HostNode hostNode=(HostNode)nodeList.get(j);
						if(node.getId()==hostNode.getId()){
							flag=true;
							break;
						} 
					}
					if(!flag){
						baseVoList.remove(node);
					}
				}
			}
		}
	}
    
    public void loadOne(BaseVo baseVo)
    {
		//--------------(1)加载基础数据-------------------    	
    	HostNode hostNode = (HostNode)baseVo;
		Host host = new Host();
		host.setId(hostNode.getId());
	    host.setAssetid(hostNode.getAssetid());
	    host.setLocation(hostNode.getLocation());
		host.setSysName(hostNode.getSysName());  
		host.setCategory(hostNode.getCategory());
		host.setCommunity(hostNode.getCommunity());
		host.setWritecommunity(hostNode.getWriteCommunity());
		host.setSnmpversion(hostNode.getSnmpversion());
		host.setTransfer(hostNode.getTransfer());
		host.setIpAddress(hostNode.getIpAddress());
		host.setLocalNet(hostNode.getLocalNet());
		host.setNetMask(hostNode.getNetMask());
		host.setAlias(hostNode.getAlias());
		host.setSysDescr(hostNode.getSysDescr());
		host.setSysOid(hostNode.getSysOid());
		host.setType(hostNode.getType());
		host.setManaged(hostNode.isManaged());
		host.setDiscoverstatus(hostNode.getDiscovertatus());
		host.setOstype(hostNode.getOstype());
		host.setCollecttype(hostNode.getCollecttype());
		host.setSysLocation(hostNode.getSysLocation());
		host.setSendemail(hostNode.getSendemail());
		host.setSendmobiles(hostNode.getSendmobiles());
		host.setSendphone(hostNode.getSendphone());
		host.setBid(hostNode.getBid());
		host.setEndpoint(hostNode.getEndpoint());
		host.setMac(hostNode.getBridgeAddress());
		host.setStatus(0);
		host.setSupperid(hostNode.getSupperid());
		host.setLayer(hostNode.getLayer());
		//SysLogger.info(host.getIpAddress()+"==========="+host.isManaged());
		
		TelnetConfig tc = findTelnetConfig(host.getId());
		if(tc!=null)
		{			
			host.setUser(tc.getUser());
		    host.setPassword(tc.getPassword());
		    host.setPrompt(tc.getPrompt());
		}
		
//		//---------------(2)加载接口数据-------------------
//		HostInterfaceDao niDao = new HostInterfaceDao();
//		try{
//			host.setInterfaceHash(niDao.loadInterfaces(host.getId()));
//		}catch(Exception e){
//			
//		}finally{
//			niDao.close();
//		}
		if(ShareData.getAllinterfaces() != null){
			if(ShareData.getAllinterfaces().containsKey(host.getId())){
				//SysLogger.info("load======"+host.getId()+"======size:"+((Hashtable)ShareData.getAllinterfaces().get(host.getId())).size());
				host.setInterfaceHash((Hashtable)ShareData.getAllinterfaces().get(host.getId()));
			}else{
				//---------------(2)加载接口数据-------------------
				HostInterfaceDao niDao = new HostInterfaceDao();
				try{
					host.setInterfaceHash(niDao.loadInterfaces(host.getId()));
				}catch(Exception e){
					
				}finally{
					niDao.close();
				}
			}
		}else{
			//---------------(2)加载接口数据-------------------
			HostInterfaceDao niDao = new HostInterfaceDao();
			try{
				host.setInterfaceHash(niDao.loadInterfaces(host.getId()));
			}catch(Exception e){
				
			}finally{
				niDao.close();
			}
		}
		
				
        //---------------(3)加载被监视对象-------------------
//		List moidList = new ArrayList(5);
//		List nmList = new ArrayList();
//		NodeMonitorDao nodeMonitorDao = getNmDao();
//		try{
//			nmList = nodeMonitorDao.loadByNodeID(host.getId());
//		}catch(Exception e){
//			
//		}finally{
//			nodeMonitorDao.close();
//		}
		
		
		//List nmList = getNmDao().loadByNodeID(host.getId());
//		for(int j=0;j<nmList.size();j++)
//		{
//			NodeMonitor nm = (NodeMonitor)nmList.get(j); 			     			
			//MonitoredItem item = MonitorFactory.createItem(nm.getMoid());
            //item.loadSelf(nm);                
            //moidList.add(item);
//		} //all monitor items are loaded
		//host.setMoidList(moidList);	
//		host.setMoidList(nmList);
		//SysLogger.info("add============"+host.getId()+"==="+host.getIpAddress());
//		PollingEngine.getInstance().addNode(host);	

		Node node=PollingEngine.getInstance().getNodeByID(host.getId());
		if(node!=null){ 
			PollingEngine.getInstance().getNodeList().remove(node);
		} 
		PollingEngine.getInstance().addNode(host); 
    }
    
    public void close()
    {
    	super.close();
    	//niDao.close();    	
    }
    
    private TelnetConfig findTelnetConfig(int nodeId)
    {    	
    	if(TelnetConfigs==null)
    	{
    		TelnetDao td = new TelnetDao();
    		TelnetConfigs = td.loadAll();
    	}
    	
    	TelnetConfig result = null;
    	for(int i=0;i<TelnetConfigs.size();i++)
    	{
    		TelnetConfig tc = (TelnetConfig)TelnetConfigs.get(i);    		
    		if(nodeId==tc.getNodeID())
    		{
    			result = tc;
    			break;
    		}
    	}
    	return result;
    }
//  载入设备端口信息
    private void loadPort(){
    	List portconfiglist = new ArrayList();
		PortconfigDao configdao = new PortconfigDao(); 			
		Portconfig portconfig = null;
		Hashtable portconfigHash = new Hashtable();
		try {
			portconfiglist = configdao.getAllBySms();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		if(portconfiglist != null && portconfiglist.size()>0){
			for(int i=0;i<portconfiglist.size();i++){
				portconfig = (Portconfig)portconfiglist.get(i);
				if(portconfigHash.containsKey(portconfig.getIpaddress())){
					List portlist = (List)portconfigHash.get(portconfig.getIpaddress());
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}else{
					List portlist = new ArrayList();
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}
			}
		} 
		ShareData.setPortConfigHash(portconfigHash);
		
		List alarmportlist = new ArrayList();
		AlarmPortDao dao = new AlarmPortDao(); 			
		AlarmPort alarmportconfig = null;
		Hashtable alarmportconfigHash = new Hashtable();
		try {
			alarmportlist = dao.getAllByEnabledAndIp();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if(alarmportlist != null && alarmportlist.size()>0){
			for(int i=0;i<alarmportlist.size();i++){
				alarmportconfig = (AlarmPort)alarmportlist.get(i);
				if(alarmportconfigHash.containsKey(alarmportconfig.getIpaddress())){
					List portlist = (List)alarmportconfigHash.get(alarmportconfig.getIpaddress());
					portlist.add(alarmportconfig);
					alarmportconfigHash.put(alarmportconfig.getIpaddress(), portlist);
				}else{
					List portlist = new ArrayList();
					portlist.add(alarmportconfig);
					alarmportconfigHash.put(alarmportconfig.getIpaddress(), portlist);
				}
			}
		} 
		ShareData.setAlarmportConfigHash(alarmportconfigHash);
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
	}
    //载入存在告警的设备信息
    private void loadCheckEvent(){
    	Hashtable checkEventHashtable = new Hashtable();
		CheckEventDao checkeventdao = new CheckEventDao();
		List eventlist = new ArrayList();
		try{
			eventlist = checkeventdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			checkeventdao.close();
		}
		if(eventlist != null && eventlist.size()>0){
			CheckEvent vo = null;
			for(int i=0;i<eventlist.size();i++){
				try {
					vo = (CheckEvent)eventlist.get(i);
					String name = vo.getNodeid() + ":" + vo.getType() + ":" + vo.getSubtype() + ":" + vo.getIndicatorsName();
					if (vo.getSindex() != null && vo.getSindex().trim().length() > 0) {
						name = name + ":" + vo.getSindex();
					}
//					System.out.println("name==========="+name);
					checkEventHashtable.put(name, vo.getAlarmlevel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		ShareData.setCheckEventHash(checkEventHashtable);
    }
    /**
     * 载入链路信息
     */
    public void loadLinks()
    {
    	LinkDao dao = new LinkDao();
    	List list=null;
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dao.close();
		}
    	List linkList = new ArrayList();
    	
    	for(int i=0;i<list.size();i++)
    	{
    		Link vo = (Link)list.get(i);
    		LinkRoad link = new LinkRoad();
    		link.setId(vo.getId());
    		link.setStartId(vo.getStartId());
    		link.setLinkName(vo.getLinkName());//yangjun add
    		link.setStartIp(vo.getStartIp());
    		link.setStartIndex(vo.getStartIndex());
    		link.setStartDescr(vo.getStartDescr());
    		
    		link.setEndId(vo.getEndId());
    		link.setEndIp(vo.getEndIp());
    		link.setEndIndex(vo.getEndIndex());
    		link.setEndDescr(vo.getEndDescr());
    		link.setAssistant(vo.getAssistant());
    		link.setType(vo.getType());
    		link.setMaxSpeed(vo.getMaxSpeed());//yangjun add
    		link.setMaxPer(vo.getMaxPer());//yangjun add
    		link.setShowinterf(vo.getShowinterf());
    		linkList.add(link);
    	}
    	PollingEngine.getInstance().setLinkList(null);
    	PollingEngine.getInstance().setLinkList(linkList);
    	//System.out.println(PollingEngine.getInstance().getLinkList().size()+"----");

    }
}